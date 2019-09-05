// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * An IEEE 128-bit floating point value.
 * <p>
 * A binary integer decimal representation of a 128-bit decimal value, supporting 34 decimal digits of
 * significand and an exponent range of -6143 to +6144.
 * <list type="table">
 * <listheader>
 * <term>Source</term> <description>Link</description>
 * </listheader> <item>
 * <term>Wikipedia:</term>
 * <description>https: //en.wikipedia.org/wiki/Decimal128_floating-point_format</description>
 * </item> <item>
 * <term>The spec:</term> <description>https: //ieeexplore.ieee.org/document/4610935</description>
 * </item> <item>
 * <term>Decimal Encodings:</term> <description>http: //speleotrove.com/decimal/decbits.html</description>
 * </item>
 * </list>
 */

public final class Float128 {
    /**
     * The size (in bytes) of a {@link Float128}.
     */
    public static final int BYTES = 2 * Long.BYTES;

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
     * The high-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
     * encoding scheme.
     */
    public long high() {
        return this.high;
    }

    /**
     * The low-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
     * encoding scheme.
     */
    public long low() {
        return this.low;
    }
}