// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import com.azure.data.cosmos.serialization.hybridrow.Float128;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Float128Codec {

    public static final int BYTES = 2 * Long.BYTES;

    private Float128Codec() {
    }

    /**
     * Decode a {@link Float128} from a sequence of two {@code long}s in little endian format.
     *
     * @param bytes an array containing the serialized {@link Float128} to be decoded.
     * @return a new {@link Float128}.
     */
    public static Float128 decode(@Nonnull final byte[] bytes) {
        checkNotNull(bytes);
        return decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * Decode a {@link Float128} from a sequence of two {@code long}s in little endian format.
     *
     * @param in a {@link ByteBuf} containing the serialized {@link Float128} to be decoded.
     * @return a new {@link Float128}.
     */
    public static Float128 decode(@Nonnull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");

        checkArgument(in.readableBytes() >= BYTES, "expected %s readable bytes, not %s",
            BYTES,
            in.readableBytes());

        return new Float128(in.readLongLE(), in.readLongLE());
    }

    /**
     * Encodes a {@link Float128} as a sequence of two {@code long}s in little endian format.
     *
     * @param value a {@link Float128} to be encoded.
     * @return a new byte array containing the encoded.
     */
    public static byte[] encode(final Float128 value) {
        final byte[] bytes = new byte[BYTES];
        encode(value, Unpooled.wrappedBuffer(bytes));
        return bytes;
    }

    /**
     * Encodes a {@link Float128} as a sequence of two {@code long}s in little endian format.
     *
     * @param value a {@link Float128} to be encoded.
     * @param out  an output {@link ByteBuf}.
     */
    public static void encode(final Float128 value, final ByteBuf out) {
        out.writeLongLE(value.high());
        out.writeLongLE(value.low());
    }
}
