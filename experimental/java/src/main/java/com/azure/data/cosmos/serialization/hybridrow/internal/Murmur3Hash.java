// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.internal;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.HashCode128;
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
 * Murmur3Hash for x86_64 (little endian).
 *
 * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
 * <p>
 */
@SuppressWarnings("UnstableApiUsage")
@Immutable
public final class Murmur3Hash {

    private static final ByteBuf FALSE = Constant.add(false);
    private static final ByteBuf TRUE = Constant.add(true);
    private static final ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    private static final ByteBuf EMPTY_STRING = Constant.add("");

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a data item.
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance
     */
    @SuppressWarnings("ConstantConditions")
    public static HashCode128 Hash128(@Nonnull final String item, @Nonnull final HashCode128 seed) {

        checkNotNull(item, "expected non-null item");
        checkNotNull(seed, "expected non-null seed");

        if (item.isEmpty()) {
            return Hash128(EMPTY_STRING, seed);
        }

        Utf8String value = Utf8String.transcodeUtf16(item);

        try {
            return Hash128(value.content(), seed);
        } finally {
            value.release();
        }
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@code boolean} data item.
     *
     * @param item The data to hash.
     * @param seed The seed with which to initialize.
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    public static HashCode128 Hash128(final boolean item, final HashCode128 seed) {
        return Murmur3Hash.Hash128(item ? TRUE : FALSE, seed);
    }

    public static HashCode128 Hash128(short item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeShortLE(item);
        return Murmur3Hash.Hash128(buffer, seed);
    }

    public static HashCode128 Hash128(byte item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeByte(item);
        return Murmur3Hash.Hash128(buffer, seed);
    }

    public static HashCode128 Hash128(int item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeIntLE(item);
        return Murmur3Hash.Hash128(buffer, seed);
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@link ByteBuf} data item.
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    public static HashCode128 Hash128(ByteBuf item, HashCode128 seed) {
        // TODO: DANOBLE: Support 128-bit hash code seeds by bringing in the murmur3 hash code from the Cosmos Java SDK
        HashFunction hashFunction = Hashing.murmur3_128(Long.valueOf(seed.high() | 0xFFFFFFFFL).intValue());
        HashCode hashCode = hashFunction.hashBytes(item.array());
        return HashCode128.from(hashCode.asBytes());
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