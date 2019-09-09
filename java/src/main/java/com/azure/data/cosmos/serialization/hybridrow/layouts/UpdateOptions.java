// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Describes the desired behavior when writing a {@link LayoutType}.
 */
public enum UpdateOptions {

    NONE(0),

    /**
     * Overwrite an existing value.
     * <p>
     * An existing value is assumed to exist at the offset provided.  The existing value is
     * replaced inline.  The remainder of the row is resized to accomodate either an increase or decrease
     * in required space.
     */
    UPDATE(1),

    /**
     * Insert a new value
     * <p>
     * An existing value is assumed NOT to exist at the offset provided.  The new value is inserted immediately at the
     * offset. The remainder of the row is resized to accommodate either an increase or decrease in required space.
     */
    INSERT(2),

    /**
     * Update an existing value or insert a new value, if no value exists
     * <p>
     * If a value exists, then this operation becomes {@link #UPDATE}, otherwise it becomes {@link #INSERT}.
     */
    UPSERT(3),

    /**
     * Insert a new value moving existing values to the right.
     * <p>
     * Within an array scope, inserts a new value immediately at the index moving all subsequent
     * items to the right. In any other scope behaves the same as {@link #UPSERT}.
     */
    INSERT_AT(4);

    public static final int BYTES = Integer.BYTES;

    private static Int2ObjectMap<UpdateOptions> mappings;
    private int value;

    UpdateOptions(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public static UpdateOptions from(int value) {
        return mappings().get(value);
    }

    public int value() {
        return this.value;
    }

    private static Int2ObjectMap<UpdateOptions> mappings() {
        if (mappings == null) {
            synchronized (UpdateOptions.class) {
                if (mappings == null) {
                    mappings = new Int2ObjectOpenHashMap<>();
                }
            }
        }
        return mappings;
    }
}