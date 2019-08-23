//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

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

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, Result> mappings;
    private int intValue;

    Result(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static Result forValue(int value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Integer, Result> getMappings() {
        if (mappings == null) {
            synchronized (Result.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, Result>();
                }
            }
        }
        return mappings;
    }
}