// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.internal;

import com.google.common.base.Utf8;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Murmur3Hash for x64 (Little Endian).
 * <p>Reference: https: //en.wikipedia.org/wiki/MurmurHash <br /></p>
 * <p>
 * This implementation provides span-based access for hashing content not available in a
 * {@link T:byte[]}
 * </p>
 */
@SuppressWarnings("UnstableApiUsage")
@Immutable
public final class Murmur3Hash {

    private static final ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    private static final ByteBuf FALSE = Constant.add(false);
    private static final ByteBuf TRUE = Constant.add(true);
    private static final ByteBuf EMPTY_STRING = Constant.add("");

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a data item
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link Code} instance
     */
    public static Code Hash128(@Nonnull final String item, @Nonnull final Code seed) {

        checkNotNull(item, "value: null, seed: %s", seed);
        checkNotNull(seed, "value: %s, seed: null", item);

        if (item.isEmpty()) {
            Hash128(EMPTY_STRING, seed);
        }

        final int encodedLength = Utf8.encodedLength(item);
        ByteBuf buffer = allocator.buffer(encodedLength, encodedLength);

        try {
            final int count = buffer.writeCharSequence(item, UTF_8);
            assert count == encodedLength : lenientFormat("count: %s, encodedLength: %s");
            return Hash128(buffer, seed);
        } finally {
            buffer.release();
        }
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@link boolean} data item
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link Code} instance
     */
    public static Code Hash128(boolean item, Code seed) {
        return Murmur3Hash.Hash128(item ? TRUE : FALSE, seed);
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@link ByteBuf} data item
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link Code} instance
     */
    public static Code Hash128(ByteBuf item, Code seed) {
        // TODO: DANOBLE: Fork and update or hack murmur3_128 to support a 128-bit seed value or push for a 32-bit seed
        HashFunction hashFunction = Hashing.murmur3_128(Long.valueOf(seed.high | 0xFFFFFFFFL).intValue());
        HashCode hashCode = hashFunction.hashBytes(item.array());
        return new Code(hashCode);
    }

    @Immutable
    public static final class Code {

        public final long low, high;

        public Code(long low, long high) {
            this.low = low;
            this.high = high;
        }

        private Code(HashCode hashCode) {
            ByteBuf buffer = Unpooled.wrappedBuffer(hashCode.asBytes());
            this.low = buffer.readLongLE();
            this.high = buffer.readLongLE();
        }
    }

    private static final class Constant {

        private static ByteBuf constants = allocator.heapBuffer();

        private Constant() {
        }

        static ByteBuf add(final boolean value) {
            final int start = constants.writerIndex();
            constants.writeByte(value ? 1 : 0);
            return constants.slice(start, Byte.BYTES).asReadOnly();
        }

        static ByteBuf add(final String value) {

            final int start = constants.writerIndex();
            final int encodedLength = Utf8.encodedLength(value);
            final ByteBuf buffer = allocator.buffer(encodedLength, encodedLength);

            final int count = buffer.writeCharSequence(value, UTF_8);
            assert count == encodedLength : lenientFormat("count: %s, encodedLength: %s");

            return constants.slice(start, encodedLength);
        }
    }
}