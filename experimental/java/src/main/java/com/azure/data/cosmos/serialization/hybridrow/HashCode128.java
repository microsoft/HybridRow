// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable 128-bit hash code.
 *
 * The hash code is represented by two {@code long} values: {@link #low()} and {@link #high()}.
 */
@Immutable
public class HashCode128 {

    private final long high;
    private final long low;

    private HashCode128(final long low, final long high) {
        this.low = low;
        this.high = high;
    }

    private HashCode128(ByteBuf buffer) {
        this.low = buffer.readLongLE();
        this.high = buffer.readLongLE();
    }

    public long high() {
        return this.high;
    }

    public long low() {
        return this.low;
    }

    @Nonnull
    public static HashCode128 from(@Nonnull final byte[] buffer) {

        checkNotNull(buffer, "expected non-null buffer");
        checkArgument(buffer.length >= 2 * Long.BYTES, "expected buffer length >= 16, not %s", buffer.length);

        return new HashCode128(Unpooled.wrappedBuffer(buffer));
    }

    /**
     * Reads a {@link HashCode128} from a {@link ByteBuf}.
     * <p>
     * The hash code is read as a pair of long values serialized in little-endian format. The values are read from the
     * buffer's current reader index which is advanced by 16 bytes: the length of two long values.
     *
     * @param buffer The buffer from which to read the hash code.
     * @return The hash code read.
     */
    @Nonnull
    public static HashCode128 from(@Nonnull final ByteBuf buffer) {

        checkNotNull(buffer, "expected non-null buffer");

        final int length = buffer.writerIndex() - buffer.readerIndex();
        checkArgument(length >= 2 * Long.BYTES, "expected at least 16 readable bytes in buffer, not %s", length);

        return new HashCode128(buffer);
    }

    @Nonnull
    public static HashCode128 of(long low, long high) {
        return new HashCode128(low, high);
    }
}
