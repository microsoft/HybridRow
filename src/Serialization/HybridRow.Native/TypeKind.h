// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  using namespace std::literals;

  /// <summary>Describes the logical type of a property.</summary>
  enum class TypeKind : unsigned char
  {
    /// <summary>Reserved.</summary>
    Invalid = 0,

    /// <summary>The literal null.</summary>
    /// <remarks>
    /// When used as a fixed column, only a presence bit is allocated.  When used as a sparse
    /// column, a sparse value with 0-length payload is written.
    /// </remarks>
    Null,

    /// <summary>
    /// A boolean property.  Boolean properties are allocated a single bit when schematized within
    /// a row.
    /// </summary>
    Boolean,

    /// <summary>8-bit signed integer.</summary>
    Int8,

    /// <summary>
    /// 16-bit signed integer.
    /// </summary>
    Int16,

    /// <summary>32-bit signed integer.</summary>
    Int32,

    /// <summary>64-bit signed integer.</summary>
    Int64,

    /// <summary>8-bit unsigned integer.</summary>
    UInt8,

    /// <summary>16-bit unsigned integer.</summary>
    UInt16,

    /// <summary>32-bit unsigned integer.</summary>
    UInt32,

    /// <summary>64-bit unsigned integer.</summary>
    UInt64,

    /// <summary>Variable length encoded signed integer.</summary>
    VarInt,

    /// <summary>Variable length encoded unsigned integer.</summary>
    VarUInt,

    /// <summary>32-bit IEEE 754 floating point value.</summary>
    Float32,

    /// <summary>64-bit IEEE 754 floating point value.</summary>
    Float64,

    /// <summary>128-bit IEEE 754-2008 floating point value.</summary>
    Float128,

    /// <summary>128-bit floating point value.  See <see cref="Decimal" /></summary>
    Decimal,

    /// <summary>
    /// 64-bit date/time value in 100ns increments from C# epoch.  See <see cref="DateTime" />
    /// </summary>
    DateTime,

    /// <summary>
    /// 64-bit date/time value in milliseconds increments from Unix epoch.  See <see cref="UnixDateTime" />
    /// </summary>
    UnixDateTime,

    /// <summary>128-bit globally unique identifier (in little-endian byte order).</summary>
    Guid,

    /// <summary>12-byte MongoDB Object Identifier (in little-endian byte order).</summary>
    MongoDbObjectId,

    /// <summary>Zero to MAX_ROW_SIZE bytes encoded as UTF-8 code points.</summary>
    Utf8,

    /// <summary>Zero to MAX_ROW_SIZE untyped bytes.</summary>
    Binary,

    /// <summary>An object property.</summary>
    Object,

    /// <summary>An array property, either typed or untyped.</summary>
    Array,

    /// <summary>A set property, either typed or untyped.</summary>
    Set,

    /// <summary>A map property, either typed or untyped.</summary>
    Map,

    /// <summary>A tuple property.  Tuples are typed, finite, ordered, sets.</summary>
    Tuple,

    /// <summary>A tagged property.  Tagged properties pair one or more typed values with an API-specific uint8 type code.</summary>
    Tagged,

    /// <summary>A row with schema.</summary>
    /// <remarks>May define either a top-level table schema or a UDT (nested row).</remarks>
    Schema,

    /// <summary>An untyped sparse field.</summary>
    /// <remarks>May only be used to define the type within a nested scope (e.g. <see cref="Object"/> or <see cref="Array"/>.</remarks>
    Any,

    /// <summary>
    /// An enum type defined in the same namespace.
    /// </summary>
    Enum,
  };

  constexpr std::string_view ToStringView(const TypeKind value) noexcept
  {
    switch (value)
    {
    case TypeKind::Invalid: return "Invalid"sv;
    case TypeKind::Null: return "Null"sv;
    case TypeKind::Boolean: return "Boolean"sv;
    case TypeKind::Int8: return "Int8"sv;
    case TypeKind::Int16: return "Int16"sv;
    case TypeKind::Int32: return "Int32"sv;
    case TypeKind::Int64: return "Int64"sv;
    case TypeKind::UInt8: return "UInt8"sv;
    case TypeKind::UInt16: return "UInt16"sv;
    case TypeKind::UInt32: return "UInt32"sv;
    case TypeKind::UInt64: return "UInt64"sv;
    case TypeKind::VarInt: return "VarInt"sv;
    case TypeKind::VarUInt: return "VarUInt"sv;
    case TypeKind::Float32: return "Float32"sv;
    case TypeKind::Float64: return "Float64"sv;
    case TypeKind::Float128: return "Float128"sv;
    case TypeKind::Decimal: return "Decimal"sv;
    case TypeKind::DateTime: return "DateTime"sv;
    case TypeKind::UnixDateTime: return "UnixDateTime"sv;
    case TypeKind::Guid: return "Guid"sv;
    case TypeKind::MongoDbObjectId: return "MongoDbObjectId"sv;
    case TypeKind::Utf8: return "Utf8"sv;
    case TypeKind::Binary: return "Binary"sv;
    case TypeKind::Object: return "Object"sv;
    case TypeKind::Array: return "Array"sv;
    case TypeKind::Set: return "Set"sv;
    case TypeKind::Map: return "Map"sv;
    case TypeKind::Tuple: return "Tuple"sv;
    case TypeKind::Tagged: return "Tagged"sv;
    case TypeKind::Schema: return "Schema"sv;
    case TypeKind::Any: return "Any"sv;
    case TypeKind::Enum: return "Enum"sv;
    default: cdb_core::Contract::Fail("Unknown TypeKind");
    }
  }
}
