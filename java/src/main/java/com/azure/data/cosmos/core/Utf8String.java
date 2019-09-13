// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Objects;
import com.google.common.base.Suppliers;
import com.google.common.base.Utf8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

@JsonSerialize(using = Utf8String.JsonSerializer.class)
@SuppressWarnings("UnstableApiUsage")
public final class Utf8String implements ByteBufHolder, CharSequence, Comparable<Utf8String> {

    public static final Utf8String EMPTY = new Utf8String(Unpooled.EMPTY_BUFFER);
    public static final Utf8String NULL = new Utf8String(null);

    private final ByteBuf buffer;
    private final Supplier<Integer> codePointCount;
    private final Supplier<Integer> utf16CodeUnitCount;

    private Utf8String(@Nullable final ByteBuf buffer) {

        if (buffer == null) {
            this.buffer = null;
            this.codePointCount = Suppliers.memoize(() -> -1);
            this.utf16CodeUnitCount = Suppliers.memoize(() -> -1);
            return;
        }

        if (buffer.writerIndex() == 0) {
            this.buffer = Unpooled.EMPTY_BUFFER;
            this.codePointCount = Suppliers.memoize(() -> 0);
            this.utf16CodeUnitCount = Suppliers.memoize(() -> 0);
            return;
        }

        this.buffer = buffer;

        this.codePointCount = Suppliers.memoize(() -> {
            final UTF8CodePointCounter counter = new UTF8CodePointCounter();
            this.buffer.forEachByte(0, this.buffer.writerIndex(), counter);
            return counter.value();
        });

        this.utf16CodeUnitCount = Suppliers.memoize(() -> {
            final UTF16CodeUnitCounter counter = new UTF16CodeUnitCounter();
            this.buffer.forEachByte(0, this.buffer.writerIndex(), counter);
            return counter.value();
        });
    }

    /**
     * {@code true} if the length of this instance is zero.
     */
    public final boolean isEmpty() {
        return this.buffer != null && this.buffer.writerIndex() == 0;
    }

    /**
     * {@code true} if this instance is null.
     */
    public final boolean isNull() {
        return this.buffer == null;
    }

    @Override
    public char charAt(final int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Non-allocating enumeration of each character in the UTF-8 stream.
     */
    @Override
    public IntStream chars() {
        throw new UnsupportedOperationException();
    }

    /**
     * Non-allocating enumeration of each code point in the UTF-8 stream.
     */
    public final IntStream codePoints() {

        if (this.buffer == null || this.buffer.writerIndex() == 0) {
            return IntStream.empty();
        }

        return StreamSupport.intStream(
            () -> Spliterators.spliteratorUnknownSize(new CodePointIterator(this.buffer), Spliterator.ORDERED),
            Spliterator.ORDERED,false);
    }

    public final int compareTo(@Nonnull final Utf8String other) {

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

        return this.buffer.compareTo(other.buffer);
    }

    public final int compareTo(final String other) {

        if (this.buffer == null) {
            return other == null ? 0 : -1;
        }

        PrimitiveIterator.OfInt t = this.codePoints().iterator();
        PrimitiveIterator.OfInt o = other.codePoints().iterator();

        while (t.hasNext() && o.hasNext()) {
            final int compare = t.nextInt() - o.nextInt();
            if (compare != 0) {
                return compare;
            }
        }

        return this.length() - other.length();
    }

    /**
     * Returns a reference to the read-only {@link ByteBuf} holding the content of this {@link Utf8String}.
     * <p>
     * A value of {@code null} is returns, if this {@link Utf8String} is null.
     * @return reference to the read-only {@link ByteBuf} holding the content of this {@link Utf8String}.
     */
    @Nullable
    public ByteBuf content() {
        return this.buffer;
    }

    /**
     * Creates a deep copy of this {@link Utf8String}.
     */
    @Override
    public Utf8String copy() {
        throw new UnsupportedOperationException();
    }

    /**
     * Duplicates this {@link Utf8String}.
     * <p>
     * Be aware that this will not automatically call {@link #retain()}.
     */
    @Override
    public Utf8String duplicate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Encoded length of this {@link Utf8String}.
     * <p>
     * This is the same value as would be returned by {@link String#getBytes()#utf16CodeUnitCount} with no time or space
     * overhead.
     *
     * @return encoded length of {@link Utf8String}
     */
    public final int encodedLength() {
        return this.buffer == null ? 0 : this.buffer.writerIndex();
    }

    public final boolean equals(ByteBuf other) {
        return Objects.equal(this.buffer, other);
    }

    public final boolean equals(String other) {
        if (other == null) {
            return false;
        }
        return this.compareTo(other) == 0;
    }

    public final boolean equals(Utf8String other) {
        if (this == other) {
            return true;
        }
        if (null == other) {
            return false;
        }
        return Objects.equal(this.buffer, other.buffer);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (null == other || this.getClass() != other.getClass()) {
            return false;
        }
        Utf8String that = (Utf8String) other;
        return this.utf16CodeUnitCount == that.utf16CodeUnitCount && Objects.equal(this.buffer, that.buffer);
    }

    /**
     * Creates a new {@link Utf8String} from a {@link ByteBuf} with UTF-8 character validation.
     * <p>
     * The {@link Utf8String} created retains the {@link ByteBuf}. No data is transferred.
     *
     * @param buffer The {@link ByteBuf} to validate and assign to the {@link Utf8String} created.
     * @return A {@link Utf8String} instance, if the @{code buffer} validates or a value of @{link Optional#empty}
     * otherwise.
     */
    @Nonnull
    public static Optional<Utf8String> from(@Nonnull final ByteBuf buffer) {
        checkNotNull(buffer, "expected non-null buffer");
        return Utf8.isWellFormed(buffer.array()) ? Optional.of(new Utf8String(buffer)) : Optional.empty();
    }

    /**
     * Creates a new {@link Utf8String} from a {@link ByteBuf} without UTF-8 character validation.
     * <p>
     * The {@link Utf8String} created retains the {@link ByteBuf} and ensures it is read-only by calling
     * {@link ByteBuf#asReadOnly}. No data is transferred.
     *
     * @param buffer a {@link ByteBuf} to assign to the {@link Utf8String} created.
     * @return a new {@link Utf8String}
     */
    @Nonnull
    public static Utf8String fromUnsafe(@Nonnull ByteBuf buffer) {
        checkNotNull(buffer, "expected non-null buffer");
        return new Utf8String(buffer);
    }

    @Override
    public int hashCode() {
        return this.buffer.hashCode();
    }

    /**
     * Returns the length of this character sequence.
     * <p>
     * The length is the number of UTF-16 code units in the sequence. This is the same value as would be returned by
     * {@link Utf8String#toUtf16()#length()} with no time or space overhead.
     */
    public final int length() {
        return this.utf16CodeUnitCount.get();
    }

    /**
     * Returns the reference count of this {@link Utf8String}.
     * <p>
     * If {@code 0}, it means this object has been deallocated.
     */
    @Override
    public int refCnt() {
        return this.buffer.refCnt();
    }

    /**
     * Decreases the reference count by {@code 1}.
     *
     * The underlying storage for this instance is deallocated, if the reference count reaches {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated.
     */
    @Override
    public boolean release() {
        return this.buffer.release();
    }

    /**
     * Decreases the reference count by the specified {@code decrement}.
     *
     * The underlying storage for this instance is deallocated, if the reference count reaches {@code 0}.
     *
     * @param decrement the value to subtract from the reference count.
     * @return {@code true} if and only if the reference count became {@code 0}.
     */
    @Override
    public boolean release(int decrement) {
        return false;
    }

    /**
     * Returns a new {@link Utf8String} which contains the specified {@code content}.
     *
     * @param content text of the {@link Utf8String} to be created.
     */
    @Override
    public Utf8String replace(ByteBuf content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Utf8String retain() {
        this.buffer.retain();
        return this;
    }

    @Override
    public Utf8String retain(int increment) {
        this.buffer.retain(increment);
        return this;
    }

    /**
     * Duplicates this {@link Utf8String}.
     * <p>
     * This method returns a retained duplicate unlike {@link #duplicate()}.
     *
     * @see ByteBuf#retainedDuplicate()
     */
    @Override
    public Utf8String retainedDuplicate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        checkArgument(start < 0 || end < 0 || start > end || end > this.length(), "start: %s, end: %s", start, end);
        return new Utf8String(this.buffer.slice(start, end));
    }

    @Override
    public String toString() {
        return this.buffer.getCharSequence(0, this.buffer.writerIndex(), UTF_8).toString();
    }

    public String toUtf16() {
        return this.buffer.getCharSequence(0, this.buffer.writerIndex(), UTF_8).toString();
    }

    @Override
    public Utf8String touch() {
        this.buffer.touch();
        return this;
    }

    @Override
    public Utf8String touch(Object hint) {
        this.buffer.touch(hint);
        return this;
    }

    /**
     * Creates a {@link Utf8String} from a UTF16 encoding string.
     * <p>
     * This method must transcode the UTF-16 into UTF-8 which both requires allocation and is a size of data operation.
     *
     * @param string A UTF-16 encoding string or {@code null}
     * @return A new {@link Utf8String} or {@code null}, if {@code string} is {@code null}
     */
    @Nullable
    public static Utf8String transcodeUtf16(@Nullable final String string) {

        if (string == null) {
            return null;
        }

        if (string.isEmpty()) {
            return EMPTY;
        }

        final int length = Utf8.encodedLength(string);
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[length]);
        final int count = buffer.writeCharSequence(string, UTF_8);

        checkState(count == length, "count: %s, length: %s", count, length);

        return new Utf8String(buffer);
    }

    private static final class CodePointIterator extends UTF8CodePointGetter implements IntIterator.OfInt {

        private final ByteBuf buffer;
        private int start, length;

        CodePointIterator(final ByteBuf buffer) {
            this.buffer = buffer;
            this.start = 0;
            this.length = buffer.writerIndex();
        }

        @Override
        public boolean hasNext() {
            return this.length > 0;
        }

        /**
         * Returns the next {@code int} element in the iteration.
         *
         * @return the next {@code int} element in the iteration.
         * @throws NoSuchElementException if the iteration has no more elements.
         */
        @Override
        public int nextInt() {

            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            this.start = this.buffer.forEachByte(this.start, this.length, this);
            this.length -= this.start;

            return this.codePoint();
        }
    }

    static final class Deserializer extends StdDeserializer<Utf8String> {

        private Deserializer() {
            super(Utf8String.class);
        }

        @Override
        public Utf8String deserialize(JsonParser parser, DeserializationContext context) throws IOException {

            final JsonNode node = parser.getCodec().readTree(parser);
            final JsonNodeType type = node.getNodeType();

            switch (type) {
                case STRING:
                    return Utf8String.transcodeUtf16(node.textValue());
                case NULL:
                    return null;
                default: {
                    String message = lenientFormat("expected string value or null, not %s", type.name().toLowerCase());
                    throw new JsonParseException(parser, message);
                }
            }
        }
    }

    static final class JsonSerializer extends StdSerializer<Utf8String> {

        private JsonSerializer() {
            super(Utf8String.class);
        }

        @Override
        public void serialize(Utf8String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(value.toString());
        }
    }

    /**
     * A {@link ByteProcessor} used by to count the number of UTF-16 code units in a UTF-8 encoded string.
     *
     * This class makes use of the fact that code points that UTF-16 encodes with two 16-bit code units, UTF-8 encodes
     * with 4 8-bit code units, and vice versa. Lead bytes are identified and counted. All other bytes are skipped.
     * Code points are not validated. The {@link #process} method counts undefined leading bytes as an undefined UTF-16
     * code unit to be replaced.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3629">RFC 3629: UTF-8, a transformation format of ISO 10646</a>
     */
    private static final class UTF16CodeUnitCounter implements ByteProcessor {

        private int count = 0;
        private int skip = 0;

        @Override
        public boolean process(byte value) {

            if (this.skip > 0) {
                this.skip--;
            } else {
                final int leadingByte = value & 0xFF;
                if (leadingByte < 0x7F) {
                    // UTF-8-1 = 0x00-7F
                    this.skip = 0;
                    this.count++;
                } else if (0xC2 <= leadingByte && leadingByte <= 0xDF) {
                    // UTF8-8-2 = 0xC2-DF UTF8-tail
                    this.skip = 1;
                    this.count++;
                } else if (0xE0 <= leadingByte && leadingByte <= 0xEF) {
                    // UTF-8-3 = 0xE0 0xA0-BF UTF8-tail / 0xE1-EC 2(UTF8-tail) / 0xED 0x80-9F UTF8-tail / 0xEE-EF 2(UTF8-tail)
                    this.skip = 2;
                    this.count++;
                } else if (0xF0 <= leadingByte && leadingByte <= 0xF4) {
                    // UTF8-4 = 0xF0 0x90-BF 2( UTF8-tail ) / 0xF1-F3 3( UTF8-tail ) / 0xF4 0x80-8F 2( UTF8-tail )
                    this.skip = 3;
                    this.count += 2;
                } else {
                    this.skip = 0;
                    this.count++;
                }
            }
            return true;
        }

        public int value() {
            return this.count;
        }
    }

    /**
     * A {@link ByteProcessor} used by to count the number of Unicode code points in a UTF-8 encoded string.
     *
     * Lead bytes are identified and counted. All other bytes are skipped. Code points are not validated. The
     * {@link #process} method counts undefined lead bytes as a single code point to be replaced.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3629">RFC 3629: UTF-8, a transformation format of ISO 10646</a>
     */
    private static final class UTF8CodePointCounter implements ByteProcessor {

        private int count = 0;
        private int skip = 0;

        @Override
        public boolean process(byte value) {

            if (this.skip > 0) {
                this.skip--;
            } else {
                final int leadingByte = value & 0xFF;
                if (leadingByte < 0x7F) {
                    // UTF-8-1 = 0x00-7F
                    this.skip = 0;
                } else if (0xC2 <= leadingByte && leadingByte <= 0xDF) {
                    // UTF8-8-2 = 0xC2-DF UTF8-tail
                    this.skip = 1;
                } else if (0xE0 <= leadingByte && leadingByte <= 0xEF) {
                    // UTF-8-3 = 0xE0 0xA0-BF UTF8-tail / 0xE1-EC 2(UTF8-tail) / 0xED 0x80-9F UTF8-tail / 0xEE-EF 2(UTF8-tail)
                    this.skip = 2;
                } else if (0xF0 <= leadingByte && leadingByte <= 0xF4) {
                    // UTF8-4 = 0xF0 0x90-BF 2( UTF8-tail ) / 0xF1-F3 3( UTF8-tail ) / 0xF4 0x80-8F 2( UTF8-tail )
                    this.skip = 3;
                } else {
                    // Undefined leading byte
                    this.skip = 0;
                }
                this.count++;
            }
            return true;
        }

        public int value() {
            return this.count;
        }
    }

    /**
     * A {@link ByteProcessor} used to read a UTF-8 encoded string one Unicode code point at a time.
     * <p>
     * This {@link #process(byte)} method reads a single code point at a time. The first byte read following
     * construction of an instance of this class must be a leading byte. This is used to determine the number of
     * single-byte UTF-8 code units in the code point.
     * <p>
     * Code points are validated. The {@link #process(byte)} method returns the Unicode
     * <a href="https://en.wikipedia.org/wiki/Specials_(Unicode_block)#Replacement_character">Replacement Character</a>
     * when an undefined code point is encountered.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3629">RFC 3629: UTF-8, a transformation format of ISO 10646</a>
     */
    private static class UTF8CodePointGetter implements ByteProcessor {

        private static final int REPLACEMENT_CHARACTER = 0xFFFD;

        private int codePoint = 0;
        private int shift = -1;

        /**
         * Gets the next code unit in a UTF-8 code point sequence.
         *
         * @param value the next code unit in a UTF-8
         *
         * @return {@code true} if additional code units must be read to complete the code point; otherwise, if the
         * code point is complete, a value of {@code false} is returned.
         */
        @Override
        public boolean process(byte value) {

            switch (this.shift) {

                default: {

                    // Next unit of code point sequence

                    this.codePoint |= (value & 0xFF << this.shift);
                    this.shift -= Byte.SIZE;
                    return true;
                }
                case 0: {

                    // End of code point sequence

                    this.codePoint |= value & 0xFF;
                    this.shift = -1;

                    if (!Character.isDefined(this.codePoint)) {
                        this.codePoint = REPLACEMENT_CHARACTER;
                    }

                    return false;
                }
                case -1: {

                    // Start of code point sequence

                    final int leadingByte = value & 0xFF;

                    if (leadingByte < 0x7F) {
                        // UTF-8-1 = 0x00-7F
                        this.codePoint = leadingByte;
                        return false;
                    }

                    if (0xC2 <= leadingByte && leadingByte <= 0xDF) {
                        // UTF8-8-2 = 0xC2-DF UTF8-tail
                        this.codePoint = leadingByte << Byte.SIZE;
                        this.shift = 0;
                        return true;
                    }

                    if (0xE0 <= leadingByte && leadingByte <= 0xEF) {
                        // UTF-8-3 = 0xE0 0xA0-BF UTF8-tail / 0xE1-EC 2(UTF8-tail) / 0xED 0x80-9F UTF8-tail / 0xEE-EF 2(UTF8-tail)
                        this.codePoint = leadingByte << 2 * Byte.SIZE;
                        this.shift = Byte.SIZE;
                        return true;
                    }

                    if (0xF0 <= leadingByte && leadingByte <= 0xF4) {
                        // UTF8-4 = 0xF0 0x90-BF 2( UTF8-tail ) / 0xF1-F3 3( UTF8-tail ) / 0xF4 0x80-8F 2( UTF8-tail )
                        this.codePoint = leadingByte << 3 * Byte.SIZE;
                        this.shift = 3 * Byte.SIZE;
                        return true;
                    }

                    this.codePoint = REPLACEMENT_CHARACTER;
                    return false;
                }
            }
        }

        /**
         * Returns the value of the most-recently read code point.
         *
         * @return value of the most-recently read code point.
         */
        int codePoint() {
            return this.codePoint;
        }
    }
}
