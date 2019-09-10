// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

// TODO: DANOBLE: Fixup JSON-serialized naming for agreement with the dotnet code

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Describes the logical type of a property.
 */
public enum TypeKind {
    /**
     * Reserved.
     */
    INVALID(0),

    /**
     * The literal null.
     * <p>
     * When used as a fixed column, only a presence bit is allocated.  When used as a sparse column, a sparse value with
     * 0-length payload is written.
     */
    NULL(1),

    /**
     * A boolean property.
     * <p>
     * Boolean properties are allocated a single bit when schematized within a row.
     */
    BOOLEAN(2),

    /**
     * 8-bit signed integer.
     */
    INT_8(3),

    /**
     * 16-bit signed integer.
     */
    INT_16(4),

    /**
     * 32-bit signed integer.
     */
    INT_32(5),

    /**
     * 64-bit signed integer.
     */
    INT_64(6),

    /**
     * 8-bit unsigned integer.
     */
    UINT_8(7),

    /**
     * 16-bit unsigned integer.
     */
    UINT_16(8),

    /**
     * 32-bit unsigned integer.
     */
    UINT_32(9),

    /**
     * 64-bit unsigned integer.
     */
    UINT_64(10),

    /**
     * Variable length encoded signed integer.
     */
    VAR_INT(11),

    /**
     * Variable length encoded unsigned integer.
     */
    VAR_UINT(12),

    /**
     * 32-bit IEEE 754 floating point value.
     */
    FLOAT_32(13),

    /**
     * 64-bit IEEE 754 floating point value.
     */
    FLOAT_64(14),

    /**
     * 128-bit IEEE 754-2008 floating point value.
     */
    FLOAT_128(15),

    /**
     * 128-bit floating point value.
     *
     * @see java.math.BigDecimal
     */
    DECIMAL(16),

    /**
     * 64-bit date/time value in 100ns increments from C# epoch.
     *
     * @see java.time.OffsetDateTime
     */
    DATE_TIME(17),

    /**
     * 64-bit date/time value in milliseconds increments from Unix epoch.
     *
     * @see com.azure.data.cosmos.serialization.hybridrow.UnixDateTime
     */
    UNIX_DATE_TIME(18),

    /**
     * 128-bit globally unique identifier (in little-endian byte order).
     */
    GUID(19),

    /**
     * 12-byte MongoDB Object Identifier (in little-endian byte order).
     */
    MONGODB_OBJECT_ID(20),

    /**
     * Zero to MAX_ROW_SIZE bytes encoded as UTF-8 code points.
     */
    UTF_8(21),

    /**
     * Zero to MAX_ROW_SIZE untyped bytes.
     */
    BINARY(22),

    /**
     * An object property.
     */
    OBJECT(23),

    /**
     * An array property, either typed or untyped.
     */
    ARRAY(24),

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
    TUPLE(27),

    /**
     * A tagged property.
     * <p>
     * Tagged properties pair one or more typed values with an API-specific uint8 type code.
     */
    TAGGED(28),

    /**
     * A row with schema.
     * <p>
     * May define either a top-level table schema or a UDT (nested row).
     */
    SCHEMA(29),

    /**
     * An untyped sparse field.
     * <p>
     * May only be used to define the type within a nested scope.
     */
    ANY(30);

    public static final int BYTES = Integer.BYTES;
    private static Int2ObjectMap<TypeKind> mappings;
    private int value;

    TypeKind(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public int value() {
        return this.value;
    }

    public static TypeKind from(int value) {
        return mappings().get(value);
    }

    private static Int2ObjectMap<TypeKind> mappings() {
        if (mappings == null) {
            synchronized (TypeKind.class) {
                if (mappings == null) {
                    mappings = new Int2ObjectOpenHashMap<>();
                }
            }
        }
        return mappings;
    }
}
