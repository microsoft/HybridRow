// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * Represents an IEEE 754-2008 128-bit decimal floating point number.
 * <p>
 * The {@link Float128} represents an IEEE 754-2008 floating point number as a pair of {@code long} values:
 * {@link #high()} and {@link #low()}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Decimal128_floating-point_format">decimal128 floating-point format</a>
 * @see <a href="https://ieeexplore.ieee.org/document/4610935">754-2008: IEEE Standard for Floating-Point Arithmetic</a>
 * @see <a href="http://speleotrove.com/decimal/decbits.html">Decimal Arithmetic Encodings Version 1.01 – 7 Apr 2009</a>
 */
public final class Float128 {

    /**
     * The size (in bytes) of a {@link Float128}.
     */
    public static final int BYTES = 2 * Long.BYTES;
    public static final Float128 ZERO = new Float128(0L, 0L);

    private final long high;
    private final long low;

    /**
     * Initializes a new instance of the {@link Float128} struct.
     *
     * @param high the high-order 64 bits.
     * @param low  the low-order 64 bits.
     */
    public Float128(long high, long low) {
        this.high = high;
        this.low = low;
    }

    /**
     * The high-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID encoding scheme.
     *
     * @return the high-order 64 bits of the IEEE 754-2008 128-bit floating point number represented by this object.
     */
    public long high() {
        return this.high;
    }

    /**
     * The low-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID encoding scheme.
     *
     * @return the low-order 64 bits of the IEEE 754-2008 128-bit floating point number represented by this object.
     */
    public long low() {
        return this.low;
    }
}