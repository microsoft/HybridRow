// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Describes the sort order direction.
 */
public enum SortDirection {
    /**
     * Sorts from the lowest to the highest value.
     */
    ASCENDING(0),

    /**
     * Sorts from the highests to the lowest value.
     */
    DESCENDING(1);

    public static final int BYTEST = Integer.BYTES;

    private static Int2ObjectMap<SortDirection> mappings;
    private int value;

    SortDirection(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public int value() {
        return this.value;
    }

    public static SortDirection from(int value) {
        return mappings().get(value);
    }

    private static Int2ObjectMap<SortDirection> mappings() {
        if (mappings == null) {
            synchronized (SortDirection.class) {
                if (mappings == null) {
                    mappings = new Int2ObjectOpenHashMap<>();
                }
            }
        }
        return mappings;
    }
}