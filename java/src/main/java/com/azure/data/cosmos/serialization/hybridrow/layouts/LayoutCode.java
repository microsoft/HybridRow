// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1028 // Enum Storage should be Int32


import java.util.HashMap;
import java.util.Map;

/**
 * Type coded used in the binary encoding to indicate the formatting of succeeding bytes.
 */
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public enum LayoutCode : byte
public enum LayoutCode {

    INVALID((byte)0),

    NULL((byte)1),
    BOOLEAN_FALSE((byte)2),
    BOOLEAN((byte)3),

    INT_8((byte)5),
    INT_16((byte)6),
    INT_32((byte)7),
    INT_64((byte)8),
    UINT_8((byte)9),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt16 = 10,
    UINT_16((byte)10),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt32 = 11,
    UINT_32((byte)11),
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: UInt64 = 12,
    UINT_64((byte)12),
    VAR_INT((byte)13),
    VAR_UINT((byte)14),

    FLOAT_32((byte)15),
    FLOAT_64((byte)16),
    DECIMAL((byte)17),

    DATE_TIME((byte)18),
    GUID((byte)19),

    UTF_8((byte)20),
    BINARY((byte)21),

    FLOAT_128((byte)22),
    UNIX_DATE_TIME((byte)23),
    MONGODB_OBJECT_ID((byte)24),

    OBJECT_SCOPE((byte)30),
    IMMUTABLE_OBJECT_SCOPE((byte)31),
    ARRAY_SCOPE((byte)32),
    IMMUTABLE_ARRAY_SCOPE((byte)33),
    TYPED_ARRAY_SCOPE((byte)34),
    IMMUTABLE_TYPED_ARRAY_SCOPE((byte)35),
    TUPLE_SCOPE((byte)36),
    IMMUTABLE_TUPLE_SCOPE((byte)37),
    TYPED_TUPLE_SCOPE((byte)38),
    IMMUTABLE_TYPED_TUPLE_SCOPE((byte)39),
    MAP_SCOPE((byte)40),
    IMMUTABLE_MAP_SCOPE((byte)41),
    TYPED_MAP_SCOPE((byte)42),
    IMMUTABLE_TYPED_MAP_SCOPE((byte)43),
    SET_SCOPE((byte)44),
    IMMUTABLE_SET_SCOPE((byte)45),
    TYPED_SET_SCOPE((byte)46),
    IMMUTABLE_TYPED_SET_SCOPE((byte)47),
    NULLABLE_SCOPE((byte)48),
    IMMUTABLE_NULLABLE_SCOPE((byte)49),
    TAGGED_SCOPE((byte)50),
    IMMUTABLE_TAGGED_SCOPE((byte)51),
    TAGGED2_SCOPE((byte)52),
    IMMUTABLE_TAGGED2_SCOPE((byte)53),

    /**
     * Nested row.
     */
    SCHEMA((byte)68),
    IMMUTABLE_SCHEMA((byte)69),

    END_SCOPE((byte)70);

    public static final int SIZE = Byte.SIZE;

    private static Map<Byte, LayoutCode> mappings;
    private byte value;

    LayoutCode(byte value) {
        this.value = value;
        mappings().put(value, this);
    }

    public byte value() {
        return value;
    }

    public static LayoutCode forValue(byte value) {
        return mappings().get(value);
    }

    private static Map<Byte, LayoutCode> mappings() {
        if (mappings == null) {
            synchronized (LayoutCode.class) {
                if (mappings == null) {
                    mappings = new HashMap<Byte, LayoutCode>();
                }
            }
        }
        return mappings;
    }
}