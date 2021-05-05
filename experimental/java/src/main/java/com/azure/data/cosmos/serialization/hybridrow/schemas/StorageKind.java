// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Describes the storage placement for primitive properties.
 */
public enum StorageKind {
    /**
     * The property does not define a column
     * <p>
     * This is indicative of an error in the the column specification.
     */
    NONE(-1, "none"),

    /**
     * The property defines a sparse column
     * <p>
     * Columns marked as sparse consume no space in the row when not present.  When present they appear in an unordered
     * linked list at the end of the row. Access time for sparse columns is proportional to the number of sparse columns
     * in the row.
     */
    SPARSE(0, "sparse"),

    /**
     * The property is a fixed-length, space-reserved column
     * <p>
     * The column will consume 1 null-bit, and its byte-width regardless of whether the value is present in the row.
     */
    FIXED(1, "fixed"),

    /**
     * The property is a variable-length column.
     * <p>
     * The column will consume 1 null-bit regardless of whether the value is present. When the value is present it will
     * also consume a variable number of bytes to encode the length preceding the actual value.
     * <p>
     * When a <em>long</em> value is marked variable then a null-bit is reserved and the value is optionally encoded as
     * variable if small enough to fit, otherwise the null-bit is set and the value is encoded as sparse.
     */
    VARIABLE(2, "variable");

    public static final int BYTES = Integer.BYTES;

    private static final Supplier<Int2ReferenceMap<StorageKind>> mappings = Suppliers.memoize(() -> {
        StorageKind[] storageKinds = StorageKind.class.getEnumConstants();
        int[] values = new int[storageKinds.length];
        Arrays.setAll(values, index -> storageKinds[index].value);
        return new Int2ReferenceArrayMap<>(values, storageKinds);
    });

    private final String friendlyName;
    private final int value;

    StorageKind(int value, String friendlyName) {
        this.friendlyName = friendlyName;
        this.value = value;
    }

    public String friendlyName() {
        return this.friendlyName;
    }

    public static StorageKind from(int value) {
        return mappings.get().get(value);
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }

    public int value() {
        return this.value;
    }
}
