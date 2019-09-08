// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the logical type of a property.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(StringEnumConverter), true)] public enum TypeKind
public enum TypeKind {
    /**
     * Reserved.
     */
    Invalid(0),

    /**
     * The literal null.
     * <p>
     * When used as a fixed column, only a presence bit is allocated.  When used as a sparse
     * column, a sparse value with 0-length payload is written.
     */
    Null(1),

    /**
     * A boolean property.  Boolean properties are allocated a single bit when schematized within
     * a row.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "bool")] Boolean,
    Boolean(2),

    /**
     * 8-bit signed integer.
     */
    Int8(3),

    /**
     * 16-bit signed integer.
     */
    Int16(4),

    /**
     * 32-bit signed integer.
     */
    Int32(5),

    /**
     * 64-bit signed integer.
     */
    Int64(6),

    /**
     * 8-bit unsigned integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "uint8")] UInt8,
    UInt8(7),

    /**
     * 16-bit unsigned integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "uint16")] UInt16,
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [EnumMember(Value = "uint16")] UInt16,
    UInt16(8),

    /**
     * 32-bit unsigned integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "uint32")] UInt32,
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [EnumMember(Value = "uint32")] UInt32,
    UInt32(9),

    /**
     * 64-bit unsigned integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "uint64")] UInt64,
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [EnumMember(Value = "uint64")] UInt64,
    UInt64(10),

    /**
     * Variable length encoded signed integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "varint")] VarInt,
    VarInt(11),

    /**
     * Variable length encoded unsigned integer.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "varuint")] VarUInt,
    VarUInt(12),

    /**
     * 32-bit IEEE 754 floating point value.
     */
    Float32(13),

    /**
     * 64-bit IEEE 754 floating point value.
     */
    Float64(14),

    /**
     * 128-bit IEEE 754-2008 floating point value.
     */
    Float128(15),

    /**
     * 128-bit floating point value.  See {@link decimal}
     */
    Decimal(16),

    /**
     * 64-bit date/time value in 100ns increments from C# epoch.  See {@link System.DateTime}
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "datetime")] DateTime,
    DateTime(17),

    /**
     * 64-bit date/time value in milliseconds increments from Unix epoch.  See {@link UnixDateTime}
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "unixdatetime")] UnixDateTime,
    UnixDateTime(18),

    /**
     * 128-bit globally unique identifier (in little-endian byte order).
     */
    Guid(19),

    /**
     * 12-byte MongoDB Object Identifier (in little-endian byte order).
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "mongodbobjectid")] MongoDbObjectId,
    MongoDbObjectId(20),

    /**
     * Zero to MAX_ROW_SIZE bytes encoded as UTF-8 code points.
     */
    Utf8(21),

    /**
     * Zero to MAX_ROW_SIZE untyped bytes.
     */
    Binary(22),

    /**
     * An object property.
     */
    Object(23),

    /**
     * An array property, either typed or untyped.
     */
    Array(24),

    /**
     * A set property, either typed or untyped.
     */
    SET(25),

    /**
     * A map property, either typed or untyped.
     */
    MAP(26),

    /**
     * A tuple property.  Tuples are typed, finite, ordered, sets.
     */
    Tuple(27),

    /**
     * A tagged property.  Tagged properties pair one or more typed values with an API-specific uint8 type code.
     */
    TAGGED(28),

    /**
     * A row with schema.
     * May define either a top-level table schema or a UDT (nested row).
     */
    Schema(29),

    /**
     * An untyped sparse field.
     * May only be used to define the type within a nested scope (e.g. {@link Object} or {@link Array}.
     */
    Any(30);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, TypeKind> mappings;
    private int intValue;

    TypeKind(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static TypeKind forValue(int value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Integer, TypeKind> getMappings() {
        if (mappings == null) {
            synchronized (TypeKind.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, TypeKind>();
                }
            }
        }
        return mappings;
    }
}