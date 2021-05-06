// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * A wall clock time expressed in milliseconds since the Unix Epoch.
 * <p>
 * A {@link UnixDateTime} is a fixed length value-type providing millisecond
 * granularity as a signed offset from the Unix Epoch (midnight, January 1, 1970 UTC).
 */
public final class UnixDateTime {
    /**
     * Unix epoch.
     * <p>
     * {@link UnixDateTime} values are signed values centered on this value.
     */
    public static final UnixDateTime EPOCH = new UnixDateTime();

    /**
     * Size in bytes of a {@link UnixDateTime}.
     */
    public static final int BYTES = Long.SIZE;

    private long milliseconds;

    private UnixDateTime() {
    }

    /**
     * Initializes a new instance of the {@link UnixDateTime} class.
     *
     * @param milliseconds The number of milliseconds since {@link #EPOCH}.
     */
    public UnixDateTime(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * {@code> true} if this value is the same as another value.
     *
     * @param other value to compare.
     * @return {code true} if this value is the same as the {code other}, {@code false} otherwise.
     */
    public boolean equals(UnixDateTime other) {
        if (other == null) {
            return false;
        }
        return this.milliseconds() == other.milliseconds();
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return other instanceof UnixDateTime && this.equals((UnixDateTime)other);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.milliseconds).hashCode();
    }

    /**
     * The number of milliseconds since {@link #EPOCH}.
     * <p>
     * This value may be negative.
     *
     * @return the number of milliseconds since {@link #EPOCH}.
     */
    public long milliseconds() {
        return this.milliseconds;
    }
}