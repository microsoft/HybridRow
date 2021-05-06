// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.core.Utf8String.transcodeUtf16;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A string whose memory representation may be either UTF-8 or UTF-16.
 * <p>
 * This type supports polymorphic use of {@link String} and {@link Utf8String} when equality, hashing, and comparison
 * are needed against either encoding.  An API leveraging {@link UtfAnyString} can avoid separate method overloads
 * while still accepting either encoding without imposing additional allocations.
 */
public final class UtfAnyString implements CharSequence, Comparable<UtfAnyString> {

    public static final UtfAnyString EMPTY = new UtfAnyString("");
    public static final UtfAnyString NULL = new UtfAnyString();

    private static final int NULL_HASHCODE = reduceHashCode(5_381, 5_381);

    private CharSequence buffer;

    public UtfAnyString(final String value) {
        this.buffer = value;
    }

    public UtfAnyString(final Utf8String value) {
        this.buffer = value;
    }

    private UtfAnyString() {
    }

    private UtfAnyString(final CharSequence sequence) {
        this.buffer = sequence;
    }

    /**
     * {@code true} if the {@link UtfAnyString} is empty.
     *
     * @return {@code true} if the {@link UtfAnyString} is empty.
     */
    public boolean isEmpty() {
        return this.buffer != null && this.buffer.length() == 0;
    }

    /**
     * {@code true} if the {@link UtfAnyString} is {@code null}.
     *
     * @return {@code true} if the {@link UtfAnyString} is {@code null}.
     */
    public boolean isNull() {
        return null == this.buffer;
    }

    /**
     * {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link String}.
     *
     * @return {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link String}.
     */
    public boolean isUtf16() {
        return this.buffer instanceof String;
    }

    /**
     * {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link Utf8String}.
     *
     * @return {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link Utf8String}.
     */
    public boolean isUtf8() {
        return this.buffer instanceof Utf8String;
    }

    /**
     * Returns the {@code char} value at the specified {@code index}.
     * <p>
     * An index ranges from zero to {@link UtfAnyString#length()} minus one. The first {@code char} value of the
     * sequence is at index zero, the next at index one, and so on, as for array indexing. If the {@code char}
     * value specified by the {@code index} is a surrogate, the surrogate (not the surrogate pair) is returned.
     *
     * @param index the index of the {@code char} value to be returned.
     * @return the specified {@code char} value
     * @throws IndexOutOfBoundsException     if the {@code index} argument is negative or not less than
     *                                       {@link UtfAnyString#length()}
     * @throws UnsupportedOperationException if this {@link UtfAnyString} is {@code null}.
     */
    @Override
    public char charAt(final int index) {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.charAt(index);
    }

    public int compareTo(@Nonnull final String other) {

        checkNotNull(other, "expected non-null other");

        if (other == this.buffer) {
            return 0;
        }

        if (this.buffer == null) {
            return -1;
        }

        return this.buffer instanceof String
            ? ((String) this.buffer).compareTo(other)
            : ((Utf8String) this.buffer).compareTo(other);
    }

    public int compareTo(@Nonnull final Utf8String other) {

        checkNotNull(other, "expected non-null other");

        if (other == this.buffer) {
            return 0;
        }

        if (this.buffer == null) {
            return -1;
        }

        return this.buffer instanceof String
            ? -other.compareTo((String) this.buffer)
            : ((Utf8String) this.buffer).compareTo(other);
    }

    @Override
    public int compareTo(@Nonnull final UtfAnyString other) {

        checkNotNull(other, "expected non-null other");

        if (other.buffer == this.buffer) {
            return 0;
        }

        if (other.buffer == null) {
            return 1;
        }

        if (this.buffer == null) {
            return -1;
        }

        if (this.buffer instanceof String) {
            return other.buffer instanceof String
                ? ((String) this.buffer).compareTo((String) other.buffer)
                : -((Utf8String) other.buffer).compareTo((String) this.buffer);
        }

        return ((Utf8String) this.buffer).compareTo((Utf8String) other.buffer);
    }

    public static UtfAnyString empty() {
        return EMPTY;
    }

    @Override
    public boolean equals(final Object other) {

        if (other == null) {
            return false;
        }

        if (other instanceof String) {
            return this.equals((String) other);
        }

        if (other instanceof Utf8String) {
            return this.equals((Utf8String) other);
        }

        if (other instanceof UtfAnyString) {
            return this.equals((UtfAnyString) other);
        }

        return false;
    }

    public boolean equals(final String other) {

        if (null == this.buffer) {
            return null == other;
        }

        if (this.buffer instanceof String) {
            return other.contentEquals(this.buffer);  // skips the type check that String.equals performs
        }

        return ((Utf8String) this.buffer).equals(other);
    }

    public boolean equals(final Utf8String other) {

        if (null == other) {
            return null == this.buffer;
        }

        return other.equals(this.buffer);
    }

    public boolean equals(final UtfAnyString other) {

        if (null == other) {
            return false;
        }

        if (null == this.buffer) {
            return null == other.buffer;
        }

        return this.buffer instanceof String ? other.buffer.equals(this.buffer) : this.buffer.equals(other.buffer);
    }

    @Override
    public int hashCode() {

        final long[] hash = { 5_381, 5_381 };

        if (this.buffer == null) {
            return NULL_HASHCODE;
        }

        if (this.buffer instanceof String) {

            final int ignored = ((String) this.buffer).codePoints().reduce(0, (index, codePoint) -> {
                if (index % 2 == 0) {
                    hash[0] = ((hash[0] << 5) + hash[0]) ^ codePoint;
                } else {
                    hash[1] = ((hash[1] << 5) + hash[1]) ^ codePoint;
                }
                return index;
            });

            return reduceHashCode(hash[0], hash[1]);
        }

        return this.buffer.hashCode();
    }

    /**
     * Returns the length of this character sequence.
     * <p>
     * The length is the number of 16-bit {@code char}s in the sequence.
     *
     * @return the number of {@code char}s in this sequence.
     * @throws UnsupportedOperationException if this {@link UtfAnyString} is {@code null}.
     */
    @Override
    public int length() {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.length();
    }

    /**
     * Returns a {@code CharSequence} that is a subsequence of this sequence.
     * <p>
     * The subsequence starts with the {@code char} value at the specified index and ends with the{@code char} value at
     * index {@code end - 1}. The length (in {@code char}s) of the returned sequence is {@code end - start}, so if
     * {@code start == end}, an empty sequence is returned.
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException     if {@code start} or {@code end} are negative, {@code end} is greater than
     *                                       {@link UtfAnyString#length()}, or {@code start} is greater than {@code
     *                                       end}.
     * @throws UnsupportedOperationException if string is {@code null}
     */
    @Override
    @Nonnull
    public CharSequence subSequence(final int start, final int end) {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.subSequence(start, end);
    }

    @Override
    @Nonnull
    public String toString() {
        return String.valueOf(this.buffer);
    }

    public String toUtf16() {
        if (null == this.buffer) {
            return null;
        }
        return this.buffer instanceof String ? (String) this.buffer : this.buffer.toString();
    }

    public Utf8String toUtf8() {
        if (null == this.buffer) {
            return null;
        }
        return this.buffer instanceof String ? transcodeUtf16((String) this.buffer) : (Utf8String) this.buffer;
    }

    private static int reduceHashCode(final long h1, final long h2) {
        return Long.valueOf(h1 + (h2 * 1_566_083_941L)).intValue();
    }
}