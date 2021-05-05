// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Describes the logical type of a property.
 */
public enum TypeKind {
    /**
     * Reserved.
     */
    @JsonEnumDefaultValue
    INVALID(0, "invalid"),

    /**
     * The literal null.
     * <p>
     * When used as a fixed column, only a presence bit is allocated.  When used as a sparse column, a sparse value with
     * 0-length payload is written.
     */
    NULL(1, "null"),

    /**
     * A boolean property.
     * <p>
     * Boolean properties are allocated a single bit when schematized within a row.
     */
    BOOLEAN(2, "bool"),

    /**
     * 8-bit signed integer.
     */
    INT_8(3, "int8"),

    /**
     * 16-bit signed integer.
     */
    INT_16(4, "int16"),

    /**
     * 32-bit signed integer.
     */
    INT_32(5, "int32"),

    /**
     * 64-bit signed integer.
     */
    INT_64(6, "int64"),

    /**
     * 8-bit unsigned integer.
     */
    UINT_8(7, "uint8"),

    /**
     * 16-bit unsigned integer.
     */
    UINT_16(8, "uint16"),

    /**
     * 32-bit unsigned integer.
     */
    UINT_32(9, "uint32"),

    /**
     * 64-bit unsigned integer.
     */
    UINT_64(10, "uint64"),

    /**
     * Variable length encoded signed integer.
     */
    VAR_INT(11, "varint"),

    /**
     * Variable length encoded unsigned integer.
     */
    VAR_UINT(12, "varuint"),

    /**
     * 32-bit IEEE 754 floating point value.
     */
    FLOAT_32(13, "float32"),

    /**
     * 64-bit IEEE 754 floating point value.
     */
    FLOAT_64(14, "float64"),

    /**
     * 128-bit IEEE 754-2008 floating point value.
     */
    FLOAT_128(15, "float128"),

    /**
     * 128-bit floating point value.
     *
     * @see java.math.BigDecimal
     */
    DECIMAL(16, "decimal"),

    /**
     * 64-bit date/time value in 100ns increments from C# epoch.
     *
     * @see java.time.OffsetDateTime
     */
    DATE_TIME(17, "datetime"),

    /**
     * 64-bit date/time value in milliseconds increments from Unix epoch.
     *
     * @see com.azure.data.cosmos.serialization.hybridrow.UnixDateTime
     */
    UNIX_DATE_TIME(18, "unixdatetime"),

    /**
     * 128-bit globally unique identifier (in little-endian byte order).
     */
    GUID(19, "guid"),

    /**
     * 12-byte MongoDB Object Identifier (in little-endian byte order).
     */
    MONGODB_OBJECT_ID(20, "mongodb.objectid"),

    /**
     * Zero to MAX_ROW_SIZE bytes encoded as UTF-8 code points.
     */
    UTF_8(21, "utf8"),

    /**
     * Zero to MAX_ROW_SIZE untyped bytes.
     */
    BINARY(22, "binary"),

    /**
     * An object property.
     */
    OBJECT(23, "object"),

    /**
     * An array property, either typed or untyped.
     */
    ARRAY(24, "array"),

    /**
     * A set property, either typed or untyped.
     */
    SET(25, "set"),

    /**
     * A map property, either typed or untyped.
     */
    MAP(26, "map"),

    /**
     * A tuple property.  Tuples are typed, finite, ordered, sets.
     */
    TUPLE(27, "tuple"),

    /**
     * A tagged property.
     * <p>
     * Tagged properties pair one or more typed values with an API-specific uint8 type code.
     */
    TAGGED(28, "tagged"),

    /**
     * A row with schema.
     * <p>
     * May define either a top-level table schema or a UDT (nested row).
     */
    SCHEMA(29, "schema"),

    /**
     * An untyped sparse field.
     * <p>
     * May only be used to define the type of a field within a nested scope.
     */
    ANY(30, "any");

    public static final int BYTES = Integer.BYTES;

    private static Supplier<Int2ReferenceMap<TypeKind>> mappings = Suppliers.memoize(() -> {
        TypeKind[] constants = TypeKind.class.getEnumConstants();
        int[] values = new int[constants.length];
        Arrays.setAll(values, index -> constants[index].value);
        return new Int2ReferenceOpenHashMap<>(values, constants);
    });

    private final String friendlyName;
    private final int value;

    TypeKind(final int value, final String friendlyName) {
        this.friendlyName = friendlyName;
        this.value = value;
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     * @see #toString()
     */
    public String friendlyName() {
        return this.friendlyName;
    }

    public static TypeKind from(int value) {
        return mappings.get().get(value);
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     * @see #friendlyName()
     */
    @Override
    public String toString() {
        return this.friendlyName;
    }

    public int value() {
        return this.value;
    }
}
