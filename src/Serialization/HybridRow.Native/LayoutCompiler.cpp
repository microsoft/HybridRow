// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Schema.h"
#include "Namespace.h"
#include "LayoutCode.h"
#include "LayoutType.h"
#include "LayoutBuilder.h"
#include "LayoutCompiler.h"
#include "ArrayPropertyType.h"
#include "LayoutCodeTraits.h"
#include "MapPropertyType.h"
#include "ObjectPropertyType.h"
#include "PrimitivePropertyType.h"
#include "SetPropertyType.h"
#include "TaggedPropertyType.h"
#include "TuplePropertyType.h"
#include "UdtPropertyType.h"

// ReSharper disable CppClangTidyCppcoreguidelinesProTypeStaticCastDowncast
namespace cdb_hr
{
  class LayoutBuilder;

  // Forward declaration.
  static void AddBase(LayoutBuilder& builder, const Namespace& ns, const Schema& s);
  static void AddProperties(LayoutBuilder& builder, SchemaLanguageVersion v, const Namespace& ns, LayoutCode scope,
                            const std::vector<std::unique_ptr<Property>>& properties);
  static std::tuple<const LayoutType*, TypeArgumentList> LogicalToPhysicalType(
    SchemaLanguageVersion v, const Namespace& ns, const PropertyType& logicalType);
  static const LayoutType* PrimitiveToPhysicalType(TypeKind type);
  constexpr static std::string_view BasePropertyName = "__base"sv;

  std::unique_ptr<Layout> LayoutCompiler::Compile(const Namespace& ns, const Schema& schema) noexcept(false)
  {
    cdb_core::Contract::Requires(schema.GetType() == TypeKind::Schema);
    cdb_core::Contract::Requires(!schema.GetName().empty());
    cdb_core::Contract::Requires(std::find_if(ns.GetSchemas().begin(), ns.GetSchemas().end(),
      [&schema](const std::unique_ptr<Schema>& p) { return p.get() == &schema; }) != ns.GetSchemas().end());

    SchemaLanguageVersion v = schema.GetEffectiveSdlVersion(ns);
    LayoutBuilder builder{schema.GetName(), schema.GetSchemaId()};
    if (!schema.GetBaseName().empty())
    {
      AddBase(builder, ns, schema);
    }
    AddProperties(builder, v, ns, LayoutCode::Schema, schema.GetProperties());

    return builder.Build();
  }

  void AddBase(LayoutBuilder& builder, const Namespace& ns, const Schema& s)
  {
    const auto& schemas = ns.GetSchemas();
    decltype(schemas.begin()) iter;
    if (s.GetBaseSchemaId() == SchemaId::Invalid())
    {
      iter = std::find_if(schemas.begin(), schemas.end(),
        [&s](const std::unique_ptr<Schema>& q) { return q->GetName() == s.GetBaseName(); });
    }
    else
    {
      iter = std::find_if(schemas.begin(), schemas.end(),
        [&s](const std::unique_ptr<Schema>& q) { return q->GetSchemaId() == s.GetBaseSchemaId(); });
    }

    if (iter == schemas.end())
    {
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Cannot resolve schema reference: '%s:%d'",
        s.GetBaseName().data(), s.GetBaseSchemaId().Id()));
    }

    const Schema& bs = **iter;
    if (bs.GetName() != s.GetBaseName())
    {
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Ambiguous schema reference: '%s:%d'",
        s.GetBaseName().data(), s.GetBaseSchemaId().Id()));
    }

    builder.AddTypedScope(BasePropertyName, &LayoutLiteral::UDT, bs.GetSchemaId());
  }

  void AddProperties(
    LayoutBuilder& builder,
    SchemaLanguageVersion v,
    const Namespace& ns,
    LayoutCode scope,
    const std::vector<std::unique_ptr<Property>>& properties)
  {
    for (const auto& ptr : properties)
    {
      const Property& p = *ptr;
      if (!p.GetPropertyType().has_value())
      {
        throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Property missing type"));
      }
      const PropertyType& pt = p.GetPropertyType().value();
      auto [type, typeArgs] = LogicalToPhysicalType(v, ns, pt);
      switch (LayoutCodeTraits::ClearImmutableBit(type->GetLayoutCode()))
      {
      case LayoutCode::ObjectScope:
      {
        if (!pt.GetNullable())
        {
          throw LayoutCompiler::LayoutCompilationException("Non-nullable sparse column are not supported.");
        }

        cdb_core::Contract::Invariant(pt.GetKind() == PropertyKind::Object);
        const ObjectPropertyType& op = static_cast<const ObjectPropertyType&>(pt);
        builder.AddObjectScope(p.GetPath(), type);
        AddProperties(builder, v, ns, type->GetLayoutCode(), op.GetProperties());
        builder.EndObjectScope();
        break;
      }

      case LayoutCode::ArrayScope:
      case LayoutCode::TypedArrayScope:
      case LayoutCode::SetScope:
      case LayoutCode::TypedSetScope:
      case LayoutCode::MapScope:
      case LayoutCode::TypedMapScope:
      case LayoutCode::TupleScope:
      case LayoutCode::TypedTupleScope:
      case LayoutCode::TaggedScope:
      case LayoutCode::Tagged2Scope:
      case LayoutCode::Schema:
      {
        if (!pt.GetNullable())
        {
          throw LayoutCompiler::LayoutCompilationException("Non-nullable sparse column are not supported.");
        }

        builder.AddTypedScope(p.GetPath(), type, typeArgs);
        break;
      }

      case LayoutCode::NullableScope:
      {
        throw LayoutCompiler::LayoutCompilationException("Nullables cannot be explicitly declared as columns.");
      }

      default:
      {
        if (pt.GetKind() == PropertyKind::Primitive)
        {
          const PrimitivePropertyType& pp = static_cast<const PrimitivePropertyType&>(pt);
          if ((pp.GetType() == TypeKind::Enum) && (v < SchemaLanguageVersion::V2))
          {
            throw LayoutCompiler::LayoutCompilationException("Enums require SDL v2 or higher.");
          }

          switch (pp.GetStorage())
          {
          case StorageKind::Fixed:
            if (LayoutCodeTraits::ClearImmutableBit(scope) != LayoutCode::Schema)
            {
              throw LayoutCompiler::LayoutCompilationException("Cannot have fixed storage within a sparse scope.");
            }

            if (type->IsNull() && !pp.GetNullable())
            {
              throw LayoutCompiler::LayoutCompilationException("Non-nullable null columns are not supported.");
            }

            builder.AddFixedColumn(p.GetPath(), type, pp.GetNullable(), pp.GetLength());
            break;
          case StorageKind::Variable:
            if (pp.GetType() == TypeKind::Enum)
            {
              throw LayoutCompiler::LayoutCompilationException("Enums cannot have storage specification: Variable");
            }

            if (LayoutCodeTraits::ClearImmutableBit(scope) != LayoutCode::Schema)
            {
              throw LayoutCompiler::LayoutCompilationException("Cannot have variable storage within a sparse scope.");
            }

            if (!pp.GetNullable())
            {
              throw LayoutCompiler::LayoutCompilationException("Non-nullable variable columns are not supported.");
            }

            builder.AddVariableColumn(p.GetPath(), type, pp.GetLength());
            break;
          case StorageKind::Sparse:
            if (!pp.GetNullable())
            {
              throw LayoutCompiler::LayoutCompilationException("Non-nullable sparse columns are not supported.");
            }

            builder.AddSparseColumn(p.GetPath(), type);
            break;
          default:
            throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown storage specification: %u",
              pp.GetStorage()));
          }
        }
        else
        {
          throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown property type: %s",
            type->GetName().data()));
        }

        break;
      }
      }
    }
  }

  static std::tuple<const LayoutType*, TypeArgumentList> LogicalToPhysicalType(
    SchemaLanguageVersion v, const Namespace& ns, const PropertyType& logicalType)
  {
    bool immutable = (logicalType.GetKind() == PropertyKind::Primitive)
                       ? false
                       : static_cast<const ScopePropertyType&>(logicalType).GetImmutable();

    switch (logicalType.GetType())
    {
    case TypeKind::Null:
    case TypeKind::Boolean:
    case TypeKind::Int8:
    case TypeKind::Int16:
    case TypeKind::Int32:
    case TypeKind::Int64:
    case TypeKind::UInt8:
    case TypeKind::UInt16:
    case TypeKind::UInt32:
    case TypeKind::UInt64:
    case TypeKind::Float32:
    case TypeKind::Float64:
    case TypeKind::Float128:
    case TypeKind::Decimal:
    case TypeKind::DateTime:
    case TypeKind::UnixDateTime:
    case TypeKind::Guid:
    case TypeKind::MongoDbObjectId:
    case TypeKind::Utf8:
    case TypeKind::Binary:
    case TypeKind::VarInt:
    case TypeKind::VarUInt:
      return {PrimitiveToPhysicalType(logicalType.GetType()), {}};

    case TypeKind::Object:
      return {immutable ? &LayoutLiteral::ImmutableObject : &LayoutLiteral::Object, {}};
    case TypeKind::Array:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Array);
      const ArrayPropertyType& ap = static_cast<const ArrayPropertyType&>(logicalType);
      if (ap.GetItems().has_value())
      {
        const PropertyType& pt = *ap.GetItems();
        if (pt.GetType() != TypeKind::Any)
        {
          auto [itemType, itemTypeArgs] = LogicalToPhysicalType(v, ns, pt);
          if (pt.GetNullable())
          {
            itemTypeArgs = {{itemType, itemTypeArgs}};
            itemType = itemType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
          }

          TypeArgumentList typeArgs{{itemType, itemTypeArgs}};
          return {immutable ? &LayoutLiteral::ImmutableTypedArray : &LayoutLiteral::TypedArray, typeArgs};
        }
      }

      return {immutable ? &LayoutLiteral::ImmutableArray : &LayoutLiteral::Array, {}};
    }
    case TypeKind::Set:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Set);
      const SetPropertyType& sp = static_cast<const SetPropertyType&>(logicalType);
      if (sp.GetItems().has_value())
      {
        const PropertyType& pt = *sp.GetItems();
        if (pt.GetType() != TypeKind::Any)
        {
          auto [itemType, itemTypeArgs] = LogicalToPhysicalType(v, ns, pt);
          if (pt.GetNullable())
          {
            itemTypeArgs = {{itemType, itemTypeArgs}};
            itemType = itemType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
          }

          TypeArgumentList typeArgs{{itemType, itemTypeArgs}};
          return {immutable ? &LayoutLiteral::ImmutableTypedSet : &LayoutLiteral::TypedSet, typeArgs};
        }
      }

      // TODO(283638): implement sparse set.
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown property type: %u",
        logicalType.GetType()));
    }
    case TypeKind::Map:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Map);
      const MapPropertyType& mp = static_cast<const MapPropertyType&>(logicalType);
      if (mp.GetKeys().has_value() && mp.GetValues().has_value())
      {
        const PropertyType& kpt = *mp.GetKeys();
        const PropertyType& vpt = *mp.GetValues();
        if ((kpt.GetType() != TypeKind::Any) && (vpt.GetType() != TypeKind::Any))
        {
          auto [keyType, keyTypeArgs] = LogicalToPhysicalType(v, ns, kpt);
          if (kpt.GetNullable())
          {
            keyTypeArgs = {{keyType, keyTypeArgs}};
            keyType = keyType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
          }

          auto [valueType, valueTypeArgs] = LogicalToPhysicalType(v, ns, vpt);
          if (vpt.GetNullable())
          {
            valueTypeArgs = {{valueType, valueTypeArgs}};
            valueType = valueType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
          }

          TypeArgumentList typeArgs{{{keyType, keyTypeArgs}, {valueType, valueTypeArgs}}};
          return {immutable ? &LayoutLiteral::ImmutableTypedMap : &LayoutLiteral::TypedMap, typeArgs};
        }
      }

      // TODO(283638): implement sparse map.
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown property type: %u",
        logicalType.GetType()));
    }
    case TypeKind::Tuple:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Tuple);
      const TuplePropertyType& tp = static_cast<const TuplePropertyType&>(logicalType);
      tla::vector<TypeArgument> args{};
      args.reserve(tp.GetItems().size());
      for (const auto& item : tp.GetItems())
      {
        auto [itemType, itemTypeArgs] = LogicalToPhysicalType(v, ns, *item);
        if (item->GetNullable())
        {
          itemTypeArgs = {{itemType, itemTypeArgs}};
          itemType = itemType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
        }

        args.emplace_back(itemType, itemTypeArgs);
      }

      TypeArgumentList typeArgs{args};
      return {immutable ? &LayoutLiteral::ImmutableTypedTuple : &LayoutLiteral::TypedTuple, typeArgs};
    }
    case TypeKind::Tagged:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Tagged);
      const TaggedPropertyType& tg = static_cast<const TaggedPropertyType&>(logicalType);
      if ((tg.GetItems().size() < TaggedPropertyType::MinTaggedArguments) ||
        (tg.GetItems().size() > TaggedPropertyType::MaxTaggedArguments))
      {
        throw LayoutCompiler::LayoutCompilationException(
          cdb_core::make_string("Invalid number of arguments in Tagged: %u <= %u <= %u",
            TaggedPropertyType::MinTaggedArguments,
            tg.GetItems().size(),
            TaggedPropertyType::MaxTaggedArguments));
      }

      tla::vector<TypeArgument> tgArgs{};
      tgArgs.reserve(tg.GetItems().size() + 1);
      tgArgs.emplace_back(&LayoutLiteral::UInt8);
      for (const auto& item : tg.GetItems())
      {
        auto [itemType, itemTypeArgs] = LogicalToPhysicalType(v, ns, *item);
        if (item->GetNullable())
        {
          itemTypeArgs = {{itemType, itemTypeArgs}};
          itemType = itemType->IsImmutable() ? &LayoutLiteral::ImmutableNullable : &LayoutLiteral::Nullable;
        }

        tgArgs.emplace_back(itemType, itemTypeArgs);
      }

      TypeArgumentList typeArgs{tgArgs};
      switch (tg.GetItems().size())
      {
      case 1:
        return {immutable ? &LayoutLiteral::ImmutableTagged : &LayoutLiteral::Tagged, typeArgs};
      case 2:
        return {immutable ? &LayoutLiteral::ImmutableTagged2 : &LayoutLiteral::Tagged2, typeArgs};
      default:
        throw LayoutCompiler::LayoutCompilationException("Unexpected tagged arity");
      }
    }
    case TypeKind::Schema:
    {
      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Udt);
      const UdtPropertyType& up = static_cast<const UdtPropertyType&>(logicalType);
      const auto& schemas = ns.GetSchemas();
      decltype(schemas.begin()) iter;
      if (up.GetSchemaId() == SchemaId::Invalid())
      {
        iter = std::find_if(schemas.begin(), schemas.end(),
          [&up](const std::unique_ptr<Schema>& s) { return s->GetName() == up.GetName(); });
      }
      else
      {
        iter = std::find_if(schemas.begin(), schemas.end(),
          [&up](const std::unique_ptr<Schema>& s) { return s->GetSchemaId() == up.GetSchemaId(); });
      }
      if (iter == schemas.end())
      {
        throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string(
          "Cannot resolve schema reference: '%s:%d'",
          up.GetName().data(), up.GetSchemaId().Id()));
      }

      const Schema& udtSchema = **iter;
      if (udtSchema.GetName() != up.GetName())
      {
        throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Ambiguous schema reference: '%s:%d'",
          up.GetName().data(), up.GetSchemaId().Id()));
      }

      TypeArgumentList typeArgs{udtSchema.GetSchemaId()};
      return {immutable ? &LayoutLiteral::ImmutableUDT : &LayoutLiteral::UDT, typeArgs};
    }
    case TypeKind::Enum:
    {
      if (v < SchemaLanguageVersion::V2)
      {
        throw LayoutCompiler::LayoutCompilationException("Enums require SDL v2 or higher.");
      }

      cdb_core::Contract::Invariant(logicalType.GetKind() == PropertyKind::Primitive);
      const PrimitivePropertyType& ep = static_cast<const PrimitivePropertyType&>(logicalType);
      const auto& enums = ns.GetEnums();
      decltype(enums.begin()) iter = std::find_if(enums.begin(), enums.end(),
        [&ep](const std::unique_ptr<EnumSchema>& es) { return es->GetName() == ep.GetEnum(); });
      if (iter == enums.end())
      {
        throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string(
          "Cannot resolve enum schema reference: '%s'",
          ep.GetEnum().data()));
      }

      const EnumSchema& enumSchema = **iter;
      return {PrimitiveToPhysicalType(enumSchema.GetType()), {}};
    }
    default:
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown property type: %u",
        logicalType.GetType()));
    }
  }

  static const LayoutType* PrimitiveToPhysicalType(TypeKind type)
  {
    switch (type)
    {
    case TypeKind::Null:
      return &LayoutLiteral::Null;
    case TypeKind::Boolean:
      return &LayoutLiteral::Boolean;
    case TypeKind::Int8:
      return &LayoutLiteral::Int8;
    case TypeKind::Int16:
      return &LayoutLiteral::Int16;
    case TypeKind::Int32:
      return &LayoutLiteral::Int32;
    case TypeKind::Int64:
      return &LayoutLiteral::Int64;
    case TypeKind::UInt8:
      return &LayoutLiteral::UInt8;
    case TypeKind::UInt16:
      return &LayoutLiteral::UInt16;
    case TypeKind::UInt32:
      return &LayoutLiteral::UInt32;
    case TypeKind::UInt64:
      return &LayoutLiteral::UInt64;
    case TypeKind::Float32:
      return &LayoutLiteral::Float32;
    case TypeKind::Float64:
      return &LayoutLiteral::Float64;
    case TypeKind::Float128:
      return &LayoutLiteral::Float128;
    case TypeKind::Decimal:
      return &LayoutLiteral::Decimal;
    case TypeKind::DateTime:
      return &LayoutLiteral::DateTime;
    case TypeKind::UnixDateTime:
      return &LayoutLiteral::UnixDateTime;
    case TypeKind::Guid:
      return &LayoutLiteral::Guid;
    case TypeKind::MongoDbObjectId:
      return &LayoutLiteral::MongoDbObjectId;
    case TypeKind::Utf8:
      return &LayoutLiteral::Utf8;
    case TypeKind::Binary:
      return &LayoutLiteral::Binary;
    case TypeKind::VarInt:
      return &LayoutLiteral::VarInt;
    case TypeKind::VarUInt:
      return &LayoutLiteral::VarUInt;
    default:
      throw LayoutCompiler::LayoutCompilationException(cdb_core::make_string("Unknown property type: %u", type));
    }
  }
}
