// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;

import java.util.Arrays;
import java.util.function.Supplier;

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

    private static final Supplier<Int2ReferenceMap<SortDirection>> mappings = Suppliers.memoize(() -> {
        SortDirection[] constants = SortDirection.class.getEnumConstants();
        int[] values = new int[constants.length];
        Arrays.setAll(values, index -> constants[index].value);
        return new Int2ReferenceArrayMap<>(values, constants);
    });

    private final int value;

    SortDirection(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static SortDirection from(int value) {
        return mappings.get().get(value);
    }
}