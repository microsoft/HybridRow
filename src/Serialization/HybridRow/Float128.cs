// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Diagnostics;
    using System.Runtime.InteropServices;

    /// <summary>An IEEE 128-bit floating point value.</summary>
    /// <remarks>
    /// A binary integer decimal representation of a 128-bit decimal value, supporting 34 decimal digits of
    /// significand and an exponent range of -6143 to +6144. <list type="table">
    /// <listheader>
    /// <term>Source</term> <description>Link</description>
    /// </listheader> <item>
    /// <term>Wikipedia:</term>
    /// <description>https://en.wikipedia.org/wiki/Decimal128_floating-point_format</description>
    /// </item> <item>
    /// <term>The spec:</term> <description>https://ieeexplore.ieee.org/document/4610935</description>
    /// </item> <item>
    /// <term>Decimal Encodings:</term>
    /// <description>http://speleotrove.com/decimal/decbits.html</description>
    /// </item>
    /// </list>
    /// </remarks>
    [DebuggerDisplay("{" + nameof(Float128.Low) + "," + nameof(Float128.High) + "}")]
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public readonly struct Float128 : IEquatable<Float128>
    {
        /// <summary>The size (in bytes) of a <see cref="Float128" />.</summary>
        public const int Size = sizeof(long) + sizeof(long);

        /// <summary>
        /// The low-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
        /// encoding scheme.
        /// </summary>
        public readonly long Low;

        /// <summary>
        /// The high-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
        /// encoding scheme.
        /// </summary>
        public readonly long High;

        /// <summary>Initializes a new instance of the <see cref="Float128" /> struct.</summary>
        /// <param name="high">the high-order 64 bits.</param>
        /// <param name="low">the low-order 64 bits.</param>
        public Float128(long high, long low)
        {
            this.High = high;
            this.Low = low;
        }

        /// <summary>Operator == overload.</summary>
        public static bool operator ==(Float128 left, Float128 right)
        {
            return left.Equals(right);
        }

        /// <summary>Operator == overload.</summary>
        public static bool operator !=(Float128 left, Float128 right)
        {
            return !left.Equals(right);
        }

        /// <summary>Returns true if this is the same value as <see cref="other" />.</summary>
        /// <param name="other">The value to compare against.</param>
        /// <returns>True if the two values are the same.</returns>
        public bool Equals(Float128 other)
        {
            return this.Low == other.Low && this.High == other.High;
        }

        /// <summary><see cref="object.Equals(object)" /> overload.</summary>
        public override bool Equals(object obj)
        {
            return obj is Float128 other && this.Equals(other);
        }

        /// <summary><see cref="object.GetHashCode" /> overload.</summary>
        public override int GetHashCode()
        {
            return HashCode.Combine(this.Low, this.High);
        }
    }
}
