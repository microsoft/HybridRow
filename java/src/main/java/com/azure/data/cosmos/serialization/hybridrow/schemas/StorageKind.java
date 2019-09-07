// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the storage placement for primitive properties.
 */
public enum StorageKind {
    /**
     * The property does not define a column
     * <p>
     * This is indicative of an error in the the column specification.
     */
    NONE(-1),

    /**
     * The property defines a sparse column
     * <p>
     * Columns marked as sparse consume no space in the row when not present.  When present they appear in an unordered
     * linked list at the end of the row. Access time for sparse columns is proportional to the number of sparse columns
     * in the row.
     */
    SPARSE(0),

    /**
     * The property is a fixed-length, space-reserved column
     * <p>
     * The column will consume 1 null-bit, and its byte-width regardless of whether the value is present in the row.
     */
    FIXED(1),

    /**
     * The property is a variable-length column.
     * <p>
     * The column will consume 1 null-bit regardless of whether the value is present. When the value is present it will
     * also consume a variable number of bytes to encode the length preceding the actual value.
     * <p>
     * When a <em>long</em> value is marked variable then a null-bit is reserved and the value is optionally encoded as
     * variable if small enough to fit, otherwise the null-bit is set and the value is encoded as sparse.
     */
    VARIABLE(2);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, StorageKind> mappings;
    private int value;

    StorageKind(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public int value() {
        return this.value;
    }

    public static StorageKind forValue(int value) {
        return mappings().get(value);
    }

    private static java.util.HashMap<Integer, StorageKind> mappings() {
        if (mappings == null) {
            synchronized (StorageKind.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<>();
                }
            }
        }
        return mappings;
    }
}