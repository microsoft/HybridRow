//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

import java.util.HashMap;

/**
 * Describes the desired behavior when mutating a hybrid row.
 */
public enum RowOptions {
    None(0),

    /**
     * Overwrite an existing value.
     * <p>
     * An existing value is assumed to exist at the offset provided.  The existing value is
     * replaced inline.  The remainder of the row is resized to accomodate either an increase or decrease
     * in required space.
     */
    Update(1),

    /**
     * Insert a new value.
     * <p>
     * An existing value is assumed NOT to exist at the offset provided.  The new value is
     * inserted immediately at the offset.  The remainder of the row is resized to accomodate either an
     * increase or decrease in required space.
     */
    Insert(2),

    /**
     * Update an existing value or insert a new value, if no value exists.
     * <p>
     * If a value exists, then this operation becomes {@link Update}, otherwise it
     * becomes {@link Insert}.
     */
    Upsert(3),

    /**
     * Insert a new value moving existing values to the right.
     * <p>
     * Within an array scope, inserts a new value immediately at the index moving all subsequent
     * items to the right. In any other scope behaves the same as {@link Upsert}.
     */
    InsertAt(4),

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    Delete(5);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static HashMap<Integer, RowOptions> mappings;
    private int value;

    RowOptions(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public int getValue() {
        return this.value;
    }

    public static RowOptions from(int value) {
        return mappings().get(value);
    }

    private static HashMap<Integer, RowOptions> mappings() {
        if (mappings == null) {
            synchronized (RowOptions.class) {
                if (mappings == null) {
                    mappings = new HashMap<>();
                }
            }
        }
        return mappings;
    }
}