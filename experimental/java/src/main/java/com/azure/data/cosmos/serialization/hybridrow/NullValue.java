// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * The literal null value.
 * <p>
 * May be stored hybrid row to indicate the literal null value. Typically this value should not be used and the
 * corresponding column should be absent from the row.
 */
public final class NullValue {
    /**
     * The default null literal.
     * This is the same value as default({@link NullValue}).
     */
    public static final NullValue DEFAULT = new NullValue();

    /**
     * Returns true if this is the same value as {@code other}.
     *
     * @param other The value to compare against.
     * @return True if the two values are the same.
     */
    public boolean equals(NullValue other) {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        return other instanceof NullValue && this.equals((NullValue)other);
    }

    @Override
    public int hashCode() {
        return 42;
    }
}