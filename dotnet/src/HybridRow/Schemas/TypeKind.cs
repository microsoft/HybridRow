// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Runtime.Serialization;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Describes the logical type of a property.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    public enum TypeKind
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
        [EnumMember(Value = "bool")]
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
        [EnumMember(Value = "uint8")]
        UInt8,

        /// <summary>16-bit unsigned integer.</summary>
        [EnumMember(Value = "uint16")]
        UInt16,

        /// <summary>32-bit unsigned integer.</summary>
        [EnumMember(Value = "uint32")]
        UInt32,

        /// <summary>64-bit unsigned integer.</summary>
        [EnumMember(Value = "uint64")]
        UInt64,

        /// <summary>Variable length encoded signed integer.</summary>
        [EnumMember(Value = "varint")]
        VarInt,

        /// <summary>Variable length encoded unsigned integer.</summary>
        [EnumMember(Value = "varuint")]
        VarUInt,

        /// <summary>32-bit IEEE 754 floating point value.</summary>
        Float32,

        /// <summary>64-bit IEEE 754 floating point value.</summary>
        Float64,

        /// <summary>128-bit IEEE 754-2008 floating point value.</summary>
        Float128,

        /// <summary>128-bit floating point value.  See <see cref="decimal" /></summary>
        Decimal,

        /// <summary>
        /// 64-bit date/time value in 100ns increments from C# epoch.  See <see cref="System.DateTime" />
        /// </summary>
        [EnumMember(Value = "datetime")]
        DateTime,

        /// <summary>
        /// 64-bit date/time value in milliseconds increments from Unix epoch.  See <see cref="UnixDateTime" />
        /// </summary>
        [EnumMember(Value = "unixdatetime")]
        UnixDateTime,

        /// <summary>128-bit globally unique identifier (in little-endian byte order).</summary>
        Guid,

        /// <summary>12-byte MongoDB Object Identifier (in little-endian byte order).</summary>
        [EnumMember(Value = "mongodbobjectid")]
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
    }
}
