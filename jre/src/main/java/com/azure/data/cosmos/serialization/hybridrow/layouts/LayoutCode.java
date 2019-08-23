//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1028 // Enum Storage should be Int32


/**
 * Type coded used in the binary encoding to indicate the formatting of succeeding bytes.
 */
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public enum LayoutCode : byte
public enum LayoutCode {
    Invalid((byte)0),

    Null((byte)1),
    BooleanFalse((byte)2),
    Boolean((byte)3),

    Int8((byte)5),
    Int16((byte)6),
    Int32((byte)7),
    Int64((byte)8),
    UInt8((byte)9),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt16 = 10,
    UInt16((byte)10),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt32 = 11,
    UInt32((byte)11),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt64 = 12,
    UInt64((byte)12),
    VarInt((byte)13),
    VarUInt((byte)14),

    Float32((byte)15),
    Float64((byte)16),
    Decimal((byte)17),

    DateTime((byte)18),
    Guid((byte)19),

    Utf8((byte)20),
    Binary((byte)21),

    Float128((byte)22),
    UnixDateTime((byte)23),
    MongoDbObjectId((byte)24),

    ObjectScope((byte)30),
    ImmutableObjectScope((byte)31),
    ArrayScope((byte)32),
    ImmutableArrayScope((byte)33),
    TypedArrayScope((byte)34),
    ImmutableTypedArrayScope((byte)35),
    TupleScope((byte)36),
    ImmutableTupleScope((byte)37),
    TypedTupleScope((byte)38),
    ImmutableTypedTupleScope((byte)39),
    MapScope((byte)40),
    ImmutableMapScope((byte)41),
    TypedMapScope((byte)42),
    ImmutableTypedMapScope((byte)43),
    SetScope((byte)44),
    ImmutableSetScope((byte)45),
    TypedSetScope((byte)46),
    ImmutableTypedSetScope((byte)47),
    NullableScope((byte)48),
    ImmutableNullableScope((byte)49),
    TaggedScope((byte)50),
    ImmutableTaggedScope((byte)51),
    Tagged2Scope((byte)52),
    ImmutableTagged2Scope((byte)53),

    /**
     * Nested row.
     */
    Schema((byte)68),
    ImmutableSchema((byte)69),

    EndScope((byte)70);

    public static final int SIZE = java.lang.Byte.SIZE;
    private static java.util.HashMap<Byte, LayoutCode> mappings;
    private byte byteValue;

    LayoutCode(byte value) {
        byteValue = value;
        getMappings().put(value, this);
    }

    public byte getValue() {
        return byteValue;
    }

    public static LayoutCode forValue(byte value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Byte, LayoutCode> getMappings() {
        if (mappings == null) {
            synchronized (LayoutCode.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Byte, LayoutCode>();
                }
            }
        }
        return mappings;
    }
}