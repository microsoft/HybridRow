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
import com.google.common.base.Utf8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
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

    public static final Utf8String EMPTY = new Utf8String(Unpooled.EMPTY_BUFFER, 0);
    public static final Utf8String NULL = new Utf8String();

    private final ByteBuf buffer;
    private final int length;

    private Utf8String() {
        this.buffer = null;
        this.length = -1;
    }

    private Utf8String(@Nonnull final ByteBuf buffer) {
        this(buffer, decodedLength(buffer));
    }

    private Utf8String(@Nonnull final ByteBuf buffer, final int decodedLength) {
        checkNotNull(buffer);
        this.buffer = buffer.asReadOnly();
        this.length = decodedLength;
    }

    /**
     * {@code true} if the length of this instance is zero
     */
    public final boolean isEmpty() {
        return this.length == 0;
    }

    /**
     * {@code true} if this instance is null
     */
    public final boolean isNull() {
        return this.buffer == null;
    }

    /**
     * Returns a reference to the read-only {@link ByteBuf} holding the content of this {@link Utf8String}
     * <p>
     * A value of {@code null} is returns, if this {@link Utf8String} is null.
     * @return reference to the read-only {@link ByteBuf} holding the content of this {@link Utf8String}.
     */
    @Nullable
    public ByteBuf content() {
        return this.buffer;
    }

    /**
     * Creates a deep copy of this {@link Utf8String}
     */
    @Override
    public Utf8String copy() {
        throw new UnsupportedOperationException();
    }

    /**
     * Duplicates this {@link Utf8String}
     * <p>
     * Be aware that this will not automatically call {@link #retain()}.
     */
    @Override
    public Utf8String duplicate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Duplicates this {@link Utf8String}
     * <p>
     * This method returns a retained duplicate unlike {@link #duplicate()}.
     *
     * @see ByteBuf#retainedDuplicate()
     */
    @Override
    public Utf8String retainedDuplicate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a new {@link Utf8String} which contains the specified {@code content}
     *
     * @param content text of the {@link Utf8String} to be created.
     */
    @Override
    public Utf8String replace(ByteBuf content) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the reference count of this {@link Utf8String}
     * <p>
     * If {@code 0}, it means this object has been deallocated.
     */
    @Override
    public int refCnt() {
        return this.buffer.refCnt();
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
     * Decreases the reference count by {@code 1} and deallocates this object if the reference count reaches at
     * {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    @Override
    public boolean release() {
        return this.buffer.release();
    }

    /**
     * Decreases the reference count by the specified {@code decrement} and deallocates this object if the reference
     * count reaches at {@code 0}.
     *
     * @param decrement
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    @Override
    public boolean release(int decrement) {
        return false;
    }

    @Override
    public char charAt(final int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Non-allocating enumeration of each character in the UTF-8 stream
     */
    @Override
    public IntStream chars() {
        throw new UnsupportedOperationException();
    }

    /**
     * Non-allocating enumeration of each code point in the UTF-8 stream
     */
    public final IntStream codePoints() {
        if (this.length == 0) {
            return IntStream.empty();
        }
        return StreamSupport.intStream(new CodePointIterable(this.buffer, this.length), false);
    }

    public final int compareTo(@Nonnull final Utf8String other) {

        checkNotNull(other);

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

        final int length = this.length();
        final int otherLength = other.length();
        final int limit = Math.min(length, otherLength);

        if (limit > 0) {

            final CodePointIterable iterable = new CodePointIterable(this.buffer, this.length);
            int index = 0;

            for (int codePoint : iterable) {

                final int otherCodePoint = other.codePointAt(index++);

                if (codePoint != otherCodePoint) {
                    return codePoint - otherCodePoint;
                }

                if (index >= limit) {
                    break;
                }
            }
        }

        return length - otherLength;
    }

    public final boolean equals(ByteBuf other) {
        return this.buffer.equals(other);
    }

    public final boolean equals(String other) {
        if (other == null) {
            return false;
        }
        return this.compareTo(other) == 0;
    }

    public final boolean equals(Utf8String other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return this.buffer.equals(other.buffer);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other instanceof ByteBuf) {
            return this.equals((ByteBuf)other);
        }
        if (other instanceof String) {
            return this.equals((String)other);
        }
        if (other instanceof Utf8String) {
            return this.equals((Utf8String)other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.buffer.hashCode();
    }

    /**
     * Returns the length of this character sequence
     * <p>
     * The length is the number of Unicode characters in the sequence.
     */
    public final int length() {
        return this.length;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        checkArgument(start < 0 || end < 0 || start > end || end > this.length, "start: %s, end: %s", start, end);
        // TODO: DANOBLE: compute buffer index for start and end character positions and use them in the slice
        return new Utf8String(this.buffer.slice(), end - start);
    }

    @Override
    public String toString() {
        return this.buffer.getCharSequence(0, this.buffer.capacity(), UTF_8).toString();
    }

    public String toUtf16() {
        return this.buffer.getCharSequence(0, this.buffer.capacity(), UTF_8).toString();
    }

    /**
     * Creates a {@link Utf8String} from a UTF16 encoding string
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

        return new Utf8String(buffer, string.length());
    }

    /**
     * Creates a new {@link Utf8String} from a {@link ByteBuf} with UTF-8 character validation
     * <p>
     * The {@link Utf8String} created retains the {@link ByteBuf}. (No data is transferred.)
     *
     * @param buffer The {@link ByteBuf} to validate and assign to the {@link Utf8String} created.
     * @return A {@link Utf8String} instance, if the @{code buffer} validates or a value of @{link Optional#empty}
     * otherwise.
     */
    @Nonnull
    public static Optional<Utf8String> from(@Nonnull final ByteBuf buffer) {
        checkNotNull(buffer);
        return Utf8.isWellFormed(buffer.array()) ? Optional.of(new Utf8String(buffer)) : Optional.empty();
    }

    /**
     * Creates a new {@link Utf8String} from a {@link ByteBuf} without UTF-8 character validation
     * <p>
     * The {@link Utf8String} created retains the {@link ByteBuf}. (No data is transferred.)
     *
     * @param buffer The {@link ByteBuf} to assign to the {@link Utf8String} created.
     * @return a new {@link Utf8String}
     */
    @Nonnull
    public static Utf8String fromUnsafe(@Nonnull ByteBuf buffer) {
        checkNotNull(buffer);
        return new Utf8String(buffer);
    }

    private static int decodedLength(final ByteBuf buffer) {

        final CodePointIterable iterable = new CodePointIterable(buffer, -1);
        int decodedLength = 0;

        for (int ignored : iterable) {
            decodedLength++;
        }

        return decodedLength;
    }

    private static final class CodePointIterable implements Iterable<Integer>, Iterator<Integer>, Spliterator.OfInt {

        private static final int CHARACTERISTICS = Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED;

        private final ByteBuf buffer;
        private final int length;
        private int index;

        CodePointIterable(final ByteBuf buffer, final int length) {
            this.buffer = buffer;
            this.length = length;
            this.index = 0;
        }

        @Override
        public int characteristics() {
            return this.length == -1 ? CHARACTERISTICS : CHARACTERISTICS | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        @Override
        public long estimateSize() {
            return this.length < 0 ? Long.MAX_VALUE : this.length;
        }

        @Override
        public void forEachRemaining(final Consumer<? super Integer> action) {
            OfInt.super.forEachRemaining(action);
        }

        @Override
        public boolean hasNext() {
            return this.index < this.buffer.capacity();
        }

        @Override
        @Nonnull
        public Iterator<Integer> iterator() {
            return this;
        }

        @Override
        public Integer next() {

            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            final int leadingByte = this.buffer.getByte(this.index++) & 0xFF;

            // A 1-byte UTF-8 code point is a special case that covers the 7-bit ASCII character set

            if (leadingByte < 0x80) {
                return leadingByte;
            }

            // The initial byte of 2-, 3- and 4-byte UTF-8 code points start with 2, 3, or 4 one bits followed by a 0
            // bit

            final int codePoint;

            if ((leadingByte & 0b1110_0000) == 0b1100_0000) {

                // 110xxxxx 10xxxxxx => 0x00000080 - 0x000007FF

                codePoint = ((leadingByte & 0b0001_1111) << 6) |
                    (this.buffer.getByte(this.index++) & 0b0011_1111);

            } else if ((leadingByte & 0b1111_0000) == 0b1110_0000) {

                // 1110xxxx 10xxxxxx 10xxxxxx => 0x00000800 - 0x0000FFFF

                codePoint = ((leadingByte & 0b0000_1111) << 12) |
                    ((this.buffer.getByte(this.index++) & 0b0011_1111) << 6) |
                    ((this.buffer.getByte(this.index++) & 0b0011_1111));

            } else if ((leadingByte & 0b1111_1000) == 0b1111_0000) {

                // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx => 0x00010000 - 0x001FFFFF

                codePoint = ((leadingByte & 0b0000_0111) << 18) |
                    ((this.buffer.getByte(this.index++) & 0b0011_1111) << 12) |
                    ((this.buffer.getByte(this.index++) & 0b0011_1111) << 6) |
                    ((this.buffer.getByte(this.index++) & 0b0011_1111));

            } else {
                // leading byte is improperly encoded and we'll detect that before returning
                codePoint = leadingByte;
            }

            checkState(Character.isDefined(codePoint), "invalid character: %s", codePoint);
            return codePoint;
        }

        @Override
        public Spliterator<Integer> spliterator() {
            return this;
        }

        @Override
        public boolean tryAdvance(@Nonnull final IntConsumer action) {

            checkNotNull(action);

            if (this.hasNext()) {
                action.accept(this.next());
                return true;
            }

            return false;
        }

        @Override
        public OfInt trySplit() {
            return null;
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
}
