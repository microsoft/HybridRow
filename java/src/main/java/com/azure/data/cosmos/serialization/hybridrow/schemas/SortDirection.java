// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the sort order direction.
 */
public enum SortDirection {
    /**
     * Sorts from the lowest to the highest value.
     */
    Ascending(0),

    /**
     * Sorts from the highests to the lowest value.
     */
    Descending(1);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, SortDirection> mappings;
    private int value;

    SortDirection(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public int value() {
        return this.value;
    }

    public static SortDirection forValue(int value) {
        return mappings().get(value);
    }

    private static java.util.HashMap<Integer, SortDirection> mappings() {
        if (mappings == null) {
            synchronized (SortDirection.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<>();
                }
            }
        }
        return mappings;
    }
}