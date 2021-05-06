// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;

    public readonly struct LayoutBit : IEquatable<LayoutBit>
    {
        /// <summary>The empty bit.</summary>
        public static readonly LayoutBit Invalid = new LayoutBit(-1);

        /// <summary>The 0-based offset into the layout bitmask.</summary>
        private readonly int index;

        /// <summary>Initializes a new instance of the <see cref="LayoutBit" /> struct.</summary>
        /// <param name="index">The 0-based offset into the layout bitmask.</param>
        internal LayoutBit(int index)
        {
            Contract.Requires(index >= -1);
            this.index = index;
        }

        /// <summary>The 0-based offset into the layout bitmask.</summary>
        public bool IsInvalid
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.index == -1;
        }

        /// <summary>The 0-based offset into the layout bitmask.</summary>
        public int Index
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.index;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool operator ==(LayoutBit left, LayoutBit right)
        {
            return left.Equals(right);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool operator !=(LayoutBit left, LayoutBit right)
        {
            return !(left == right);
        }

        /// <summary>
        /// Returns the 0-based byte offset from the beginning of the row or scope that contains the
        /// bit from the bitmask.
        /// </summary>
        /// <remarks>Also see <see cref="GetBit" /> to identify.</remarks>
        /// <param name="offset">The byte offset from the beginning of the row where the scope begins.</param>
        /// <returns>The byte offset containing this bit.</returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public int GetOffset(int offset)
        {
            return offset + (this.index / LayoutType.BitsPerByte);
        }

        /// <summary>Returns the 0-based bit from the beginning of the byte that contains this bit.</summary>
        /// <remarks>Also see <see cref="GetOffset" /> to identify relevant byte.</remarks>
        /// <returns>The bit of the byte within the bitmask.</returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public int GetBit()
        {
            return this.index % LayoutType.BitsPerByte;
        }

        public override bool Equals(object other)
        {
            return other is LayoutBit layoutBit && this.Equals(layoutBit);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public bool Equals(LayoutBit other)
        {
            return this.index == other.index;
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(this.index);
        }

        /// <summary>Compute the division rounding up to the next whole number.</summary>
        /// <param name="numerator">The numerator to divide.</param>
        /// <param name="divisor">The divisor to divide by.</param>
        /// <returns>The ceiling(numerator/divisor).</returns>
        internal static int DivCeiling(int numerator, int divisor)
        {
            return (numerator + (divisor - 1)) / divisor;
        }

        /// <summary>Allocates layout bits from a bitmask.</summary>
        internal class Allocator
        {
            /// <summary>The next bit to allocate.</summary>
            private int next;

            /// <summary>Initializes a new instance of the <see cref="Allocator" /> class.</summary>
            public Allocator()
            {
                this.next = 0;
            }

            /// <summary>The number of bytes needed to hold all bits so far allocated.</summary>
            public int NumBytes => LayoutBit.DivCeiling(this.next, LayoutType.BitsPerByte);

            /// <summary>Allocates a new bit from the bitmask.</summary>
            /// <returns>The allocated bit.</returns>
            public LayoutBit Allocate()
            {
                return new LayoutBit(this.next++);
            }
        }
    }
}
