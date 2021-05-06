// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "LayoutCodeTraits.h"
#include "LayoutType.h"
#include "RowCursor.h"

namespace cdb_hr
{
  namespace Internal
  {
    template<typename T, LayoutCode code>
    struct LayoutTypeCheck
    {
      constexpr static const T& TypeAs(const LayoutType& t) noexcept
      {
        LayoutCode tc = t.GetLayoutCode();
        if (code == LayoutCode::Boolean)
        {
          tc = LayoutCodeTraits::Canonicalize(tc);
        }
        else if ((code >= LayoutCode::ObjectScope) && (code < LayoutCode::EndScope))
        {
          tc = LayoutCodeTraits::ClearImmutableBit(tc);
        }
        cdb_core::Contract::Requires(tc == code);
        return static_cast<const T&>(t);  // NOLINT(cppcoreguidelines-pro-type-static-cast-downcast)
      }
    };

    template<typename T>
    struct LayoutScopeTypeCheck {};

    template<>
    struct LayoutScopeTypeCheck<LayoutIndexedScope>
    {
      constexpr static const LayoutIndexedScope& TypeAs(const LayoutType& t) noexcept
      {
        LayoutCode tc = t.GetLayoutCode();
        cdb_core::Contract::Requires((tc >= LayoutCode::ArrayScope) && (tc <= LayoutCode::ImmutableTagged2Scope));
        return static_cast<const LayoutIndexedScope&>(t);  // NOLINT(cppcoreguidelines-pro-type-static-cast-downcast)
      }
    };

    template<>
    struct LayoutScopeTypeCheck<LayoutUniqueScope>
    {
      constexpr static const LayoutUniqueScope& TypeAs(const LayoutType& t) noexcept
      {
        LayoutCode tc = t.GetLayoutCode();
        cdb_core::Contract::Requires((tc >= LayoutCode::MapScope) && (tc <= LayoutCode::ImmutableTypedSetScope));
        return static_cast<const LayoutUniqueScope&>(t);  // NOLINT(cppcoreguidelines-pro-type-static-cast-downcast)
      }
    };

    template<typename T> struct LayoutTypeAs final {};
    template<> struct LayoutTypeAs<LayoutInt8> final : LayoutTypeCheck<LayoutInt8, LayoutCode::Int8>{};
    template<> struct LayoutTypeAs<LayoutInt16> final : LayoutTypeCheck<LayoutInt16, LayoutCode::Int16>{};
    template<> struct LayoutTypeAs<LayoutInt32> final : LayoutTypeCheck<LayoutInt32, LayoutCode::Int32>{};
    template<> struct LayoutTypeAs<LayoutInt64> final : LayoutTypeCheck<LayoutInt64, LayoutCode::Int64>{};
    template<> struct LayoutTypeAs<LayoutUInt8> final : LayoutTypeCheck<LayoutUInt8, LayoutCode::UInt8>{};
    template<> struct LayoutTypeAs<LayoutUInt16> final : LayoutTypeCheck<LayoutUInt16, LayoutCode::UInt16>{};
    template<> struct LayoutTypeAs<LayoutUInt32> final : LayoutTypeCheck<LayoutUInt32, LayoutCode::UInt32>{};
    template<> struct LayoutTypeAs<LayoutUInt64> final : LayoutTypeCheck<LayoutUInt64, LayoutCode::UInt64>{};
    template<> struct LayoutTypeAs<LayoutVarInt> final : LayoutTypeCheck<LayoutVarInt, LayoutCode::VarInt>{};
    template<> struct LayoutTypeAs<LayoutVarUInt> final : LayoutTypeCheck<LayoutVarUInt, LayoutCode::VarUInt>{};
    template<> struct LayoutTypeAs<LayoutFloat32> final : LayoutTypeCheck<LayoutFloat32, LayoutCode::Float32>{};
    template<> struct LayoutTypeAs<LayoutFloat64> final : LayoutTypeCheck<LayoutFloat64, LayoutCode::Float64>{};
    template<> struct LayoutTypeAs<LayoutFloat128> final : LayoutTypeCheck<LayoutFloat128, LayoutCode::Float128>{};
    template<> struct LayoutTypeAs<LayoutDecimal> final : LayoutTypeCheck<LayoutDecimal, LayoutCode::Decimal>{};
    template<> struct LayoutTypeAs<LayoutDateTime> final : LayoutTypeCheck<LayoutDateTime, LayoutCode::DateTime>{};
    template<> struct LayoutTypeAs<LayoutUnixDateTime> final : LayoutTypeCheck<LayoutUnixDateTime, LayoutCode::UnixDateTime>{};
    template<> struct LayoutTypeAs<LayoutGuid> final : LayoutTypeCheck<LayoutGuid, LayoutCode::Guid>{};
    template<> struct LayoutTypeAs<LayoutMongoDbObjectId> final : LayoutTypeCheck<LayoutMongoDbObjectId, LayoutCode::MongoDbObjectId>{};
    template<> struct LayoutTypeAs<LayoutNull> final : LayoutTypeCheck<LayoutNull, LayoutCode::Null>{};
    template<> struct LayoutTypeAs<LayoutBoolean> final : LayoutTypeCheck<LayoutBoolean, LayoutCode::Boolean>{};
    template<> struct LayoutTypeAs<LayoutUtf8> final : LayoutTypeCheck<LayoutUtf8, LayoutCode::Utf8>{};
    template<> struct LayoutTypeAs<LayoutBinary> final : LayoutTypeCheck<LayoutBinary, LayoutCode::Binary>{};
    template<> struct LayoutTypeAs<LayoutObject> final : LayoutTypeCheck<LayoutObject, LayoutCode::ObjectScope>{};
    template<> struct LayoutTypeAs<LayoutArray> final : LayoutTypeCheck<LayoutArray, LayoutCode::ArrayScope>{};
    template<> struct LayoutTypeAs<LayoutTypedArray> final : LayoutTypeCheck<LayoutTypedArray, LayoutCode::TypedArrayScope>{};
    template<> struct LayoutTypeAs<LayoutTypedSet> final : LayoutTypeCheck<LayoutTypedSet, LayoutCode::TypedSetScope>{};
    template<> struct LayoutTypeAs<LayoutTypedMap> final : LayoutTypeCheck<LayoutTypedMap, LayoutCode::TypedMapScope>{};
    template<> struct LayoutTypeAs<LayoutTuple> final : LayoutTypeCheck<LayoutTuple, LayoutCode::TupleScope>{};
    template<> struct LayoutTypeAs<LayoutTypedTuple> final : LayoutTypeCheck<LayoutTypedTuple, LayoutCode::TypedTupleScope>{};
    template<> struct LayoutTypeAs<LayoutTagged> final : LayoutTypeCheck<LayoutTagged, LayoutCode::TaggedScope>{};
    template<> struct LayoutTypeAs<LayoutTagged2> final : LayoutTypeCheck<LayoutTagged2, LayoutCode::Tagged2Scope>{};
    template<> struct LayoutTypeAs<LayoutNullable> final : LayoutTypeCheck<LayoutNullable, LayoutCode::NullableScope>{};
    template<> struct LayoutTypeAs<LayoutUDT> final : LayoutTypeCheck<LayoutUDT, LayoutCode::Schema>{};
    template<> struct LayoutTypeAs<LayoutEndScope> final : LayoutTypeCheck<LayoutEndScope, LayoutCode::EndScope>{};
    template<> struct LayoutTypeAs<LayoutIndexedScope> final : LayoutScopeTypeCheck<LayoutIndexedScope>{};
    template<> struct LayoutTypeAs<LayoutUniqueScope> final : LayoutScopeTypeCheck<LayoutUniqueScope>{};
  }

  template<typename T, typename> [[nodiscard]] const T& LayoutType::TypeAs() const
  {
    return Internal::LayoutTypeAs<T>::TypeAs(*this);
  }

  template<typename TContext>
  Result LayoutScope::WriteScope(RowBuffer& b, RowCursor& scope,
                                 const TypeArgumentList& typeArgs, TContext& context,
                                 WriterFunc<TContext> func, UpdateOptions options) const noexcept
  {
    auto [r, childScope] = WriteScope(b, scope, typeArgs, options);
    if (r != Result::Success)
    {
      return r;
    }

    r = func != nullptr ? func(b, childScope, context) : Result::Success;
    if (r != Result::Success)
    {
      DeleteScope(b, scope);
      return r;
    }

    scope.Skip(b, childScope);
    return Result::Success;
  }

  template<typename T, typename> const T& TypeArgument::TypeAs() const
  {
    return m_type->TypeAs<T>();
  }

  template<typename T, typename> const T& LayoutColumn::TypeAs() const
  {
    return m_typeArg.GetType()->TypeAs<T>();
  }

  template<typename TContext>
  Result LayoutUniqueScope::WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                       TContext& context, WriterFunc<TContext> func,
                                       UpdateOptions options) const noexcept
  {
    Result r;
    RowCursor uniqueScope;
    std::tie(r, uniqueScope) = LayoutIndexedScope::WriteScope(b, scope, typeArgs, options);
    if (r != Result::Success)
    {
      return r;
    }

    RowCursor childScope = uniqueScope.Clone();
    childScope.m_deferUniqueIndex = true;
    r = func != nullptr ? func(b, childScope, context) : Result::Success;
    if (r != Result::Success)
    {
      DeleteScope(b, scope);
      return r;
    }

    uniqueScope.m_count = childScope.m_count;
    r = b.TypedCollectionUniqueIndexRebuild(uniqueScope);
    if (r != Result::Success)
    {
      DeleteScope(b, scope);
      return r;
    }

    scope.Skip(b, childScope);
    return Result::Success;
  }
}
