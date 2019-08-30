//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


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
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{" + nameof(Float128.Low) + "," + nameof(Float128.High) + "}")][StructLayout
// (LayoutKind.Sequential, Pack = 1)] public readonly struct Float128
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [DebuggerDisplay("{" + nameof(Float128.Low) + "," + nameof(Float128.High) + "}")][StructLayout
// (LayoutKind.Sequential, Pack = 1)] public readonly struct Float128
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class Float128 {
    /**
     * The size (in bytes) of a {@link Float128}.
     */
    public static final int Size = (Long.SIZE / Byte.SIZE) + (Long.SIZE / Byte.SIZE);

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