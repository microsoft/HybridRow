// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum Result {

    SUCCESS(0),
    FAILURE(1),
    NOT_FOUND(2),
    EXISTS(3),
    TOO_BIG(4),

    /**
     * The type of an existing field does not match the expected type for this operation.
     */
    TYPE_MISMATCH(5),

    /**
     * An attempt to write in a read-only scope.
     */
    INSUFFICIENT_PERMISSIONS(6),

    /**
     * An attempt to write a field that did not match its (optional) type constraints.
     */
    TYPE_CONSTRAINT(7),

    /**
     * The byte sequence could not be parsed as a valid row.
     */
    INVALID_ROW(8),

    /**
     * The byte sequence was too short for the requested action.
     */
    INSUFFICIENT_BUFFER(9),

    /**
     * The operation was cancelled.
     */
    CANCELED(10);

    public static final int BYTES = Integer.BYTES;

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
