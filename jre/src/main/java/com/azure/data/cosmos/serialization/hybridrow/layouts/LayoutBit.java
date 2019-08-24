//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import static com.google.common.base.Preconditions.checkArgument;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public readonly struct LayoutBit : IEquatable<LayoutBit>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class LayoutBit implements IEquatable<LayoutBit> {
    /**
     * The empty bit.
     */
    public static final LayoutBit Invalid = new LayoutBit(-1);

    /**
     * The 0-based offset into the layout bitmask.
     */
    private int index;

    /**
     * Initializes a new instance of the {@link LayoutBit} struct.
     *
     * @param index The 0-based offset into the layout bitmask.
     */
    public LayoutBit() {
    }

    public LayoutBit(int index) {
        checkArgument(index >= -1);
        this.index = index;
    }

    /**
     * Compute the division rounding up to the next whole number.
     *
     * @param numerator The numerator to divide.
     * @param divisor   The divisor to divide by.
     * @return The ceiling(numerator/divisor).
     */
    public static int DivCeiling(int numerator, int divisor) {
        return (numerator + (divisor - 1)) / divisor;
    }

    /**
     * Returns the 0-based bit from the beginning of the byte that contains this bit.
     * Also see {@link GetOffset} to identify relevant byte.
     *
     * @return The bit of the byte within the bitmask.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public int GetBit()
    public int GetBit() {
        return this.index % LayoutType.BitsPerByte;
    }

    /**
     * Returns the 0-based byte offset from the beginning of the row or scope that contains the
     * bit from the bitmask.
     * <p>
     * Also see {@link GetBit} to identify.
     *
     * @param offset The byte offset from the beginning of the row where the scope begins.
     * @return The byte offset containing this bit.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public int GetOffset(int offset)
    public int GetOffset(int offset) {
        return offset + (this.index / LayoutType.BitsPerByte);
    }

    public LayoutBit clone() {
        LayoutBit varCopy = new LayoutBit();

        varCopy.index = this.index;

        return varCopy;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LayoutBit && this.equals((LayoutBit)other);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public bool Equals(LayoutBit other)
    public boolean equals(LayoutBit other) {
        return this.index == other.index;
    }

    @Override
    public int hashCode() {
        return (new Integer(this.index)).hashCode();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public static bool operator ==(LayoutBit
    // left, LayoutBit right)
    public static boolean opEquals(LayoutBit left, LayoutBit right) {
        return left.equals(right.clone());
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public static bool operator !=(LayoutBit
    // left, LayoutBit right)
    public static boolean opNotEquals(LayoutBit left, LayoutBit right) {
        return !LayoutBit.opEquals(left.clone(), right.clone());
    }

    /**
     * The 0-based offset into the layout bitmask.
     */
    int getIndex()

    /**
     * The 0-based offset into the layout bitmask.
     */
    boolean getIsInvalid()

    /**
     * Allocates layout bits from a bitmask.
     */
    public static class Allocator {
        /**
         * The next bit to allocate.
         */
        private int next;

        /**
         * Initializes a new instance of the {@link Allocator} class.
         */
        public Allocator() {
            this.next = 0;
        }

        /**
         * The number of bytes needed to hold all bits so far allocated.
         */
        public final int getNumBytes() {
            return LayoutBit.DivCeiling(this.next, LayoutType.BitsPerByte);
        }

        /**
         * Allocates a new bit from the bitmask.
         *
         * @return The allocated bit.
         */
        public final LayoutBit Allocate() {
            return new LayoutBit(this.next++);
        }
    }
}