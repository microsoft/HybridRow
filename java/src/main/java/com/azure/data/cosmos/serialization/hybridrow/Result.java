// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum Result {
    Success(0),
    Failure(1),
    NotFound(2),
    Exists(3),
    TooBig(4),

    /**
     * The type of an existing field does not match the expected type for this operation.
     */
    TypeMismatch(5),

    /**
     * An attempt to write in a read-only scope.
     */
    InsufficientPermissions(6),

    /**
     * An attempt to write a field that did not match its (optional) type constraints.
     */
    TypeConstraint(7),

    /**
     * The byte sequence could not be parsed as a valid row.
     */
    InvalidRow(8),

    /**
     * The byte sequence was too short for the requested action.
     */
    InsufficientBuffer(9),

    /**
     * The operation was cancelled.
     */
    Canceled(10);

    public static final int SIZE = Integer.SIZE;

    private static Int2ObjectMap<Result> mappings;
    private final int value;

    Result(int value) {
        this.value = value;
        mappings().put(value, this);
    }

    public static Result from(int value) {
        return mappings().get(value);
    }

    public int value() {
        return this.value;
    }

    private static Int2ObjectMap<Result> mappings() {
        if (mappings == null) {
            synchronized (Result.class) {
                if (mappings == null) {
                    mappings = new Int2ObjectOpenHashMap<>();
                }
            }
        }
        return mappings;
    }
}
