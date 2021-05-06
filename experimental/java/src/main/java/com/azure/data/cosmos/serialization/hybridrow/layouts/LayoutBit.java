// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutBit {
    /**
     * The empty bit.
     */
    public static final LayoutBit INVALID = new LayoutBit(-1);

    private final int index;

    /**
     * Initializes a new instance of the {@link LayoutBit} class.
     *
     * @param index The zero-based offset into the layout bitmask.
     */
    public LayoutBit(int index) {
        checkArgument(index >= -1);
        this.index = index;
    }

    /**
     * Compute the division rounding up to the next whole number
     *
     * @param numerator The numerator to divide.
     * @param divisor   The divisor to divide by.
     * @return The ceiling(numerator/divisor).
     */
    public static int divCeiling(int numerator, int divisor) {
        return (numerator + (divisor - 1)) / divisor;
    }

    /**
     * Zero-based bit from the beginning of the byte that contains this bit.
     * <p>
     * Also see {@link #offset(int)} to identify relevant byte.
     *
     * @return The bit of the byte within the bitmask.
     */
    public int bit() {
        return this.index() % Byte.SIZE;
    }

    /**
     * Zero-based offset into the layout bitmask.
     *
     * @return zero-based offset into the layout bitmask.
     */
    public int index() {
        return this.index;
    }

    public boolean isInvalid() {
        return this.index == INVALID.index;
    }

    /**
     * Returns the zero-based byte offset from the beginning of the row or scope that contains the bit from the bitmask.
     * <p>
     * Also see {@link #bit()} to identify.
     *
     * @param offset The byte offset from the beginning of the row where the scope begins.
     * @return The byte offset containing this bit.
     */
    public int offset(int offset) {
        return offset + (this.index() / Byte.SIZE);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LayoutBit && this.equals((LayoutBit)other);
    }

    public boolean equals(LayoutBit other) {
        return other != null && this.index() == other.index();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.index()).hashCode();
    }

    /**
     * Allocates layout bits from a bitmask.
     */
    static class Allocator {
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
        public final int numBytes() {
            return LayoutBit.divCeiling(this.next, Byte.SIZE);
        }

        /**
         * Allocates a new bit from the bitmask.
         *
         * @return The allocated bit.
         */
        public final LayoutBit allocate() {
            return new LayoutBit(this.next++);
        }
    }
}