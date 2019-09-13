// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DateTimeCodec {

    public static final int BYTES = Long.BYTES;

    private static final long FLAGS_MASK = 0xC000000000000000L;
    private static final long KIND_AMBIGUOUS = 0xC000000000000000L;
    private static final long KIND_LOCAL = 0x8000000000000000L;
    private static final long KIND_UTC = 0x4000000000000000L;
    private static final long TICKS_MASK = 0x3FFFFFFFFFFFFFFFL;

    private static final ZoneOffset ZONE_OFFSET_LOCAL = OffsetDateTime.now().getOffset();
    private static final int ZONE_OFFSET_LOCAL_TOTAL_SECONDS = ZONE_OFFSET_LOCAL.getTotalSeconds();
    private static final int ZONE_OFFSET_UTC_TOTAL_SECONDS = ZoneOffset.UTC.getTotalSeconds();

    private DateTimeCodec() {
    }

    /**
     * Decode an {@link OffsetDateTime} serialized like a {@code System.DateTime} by {@code MemoryMarshal.Write}.
     *
     * @param bytes a {@link byte} array containing the serialized value to be decoded.
     * @return a new {@link OffsetDateTime}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">
     * struct DateTimeCodec source</a>
     */
    public static OffsetDateTime decode(@Nonnull final byte[] bytes) {
        checkNotNull(bytes);
        return decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * Decode an {@link OffsetDateTime} serialized like a {@code System.DateTime} by {@code MemoryMarshal.Write}.
     *
     * @param in a {@link ByteBuf} containing the serialized value to be decoded.
     * @return a new {@link OffsetDateTime}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">
     * struct DateTimeCodec source</a>
     */
    public static OffsetDateTime decode(@Nonnull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");

        checkArgument(in.readableBytes() >= BYTES, "expected %s readable bytes, not %s",
            BYTES,
            in.readableBytes());

        final long data = in.readLongLE();
        final long epochSecond = data & TICKS_MASK;
        final ZoneOffset zoneOffset = (data & FLAGS_MASK) == KIND_UTC ? ZoneOffset.UTC : ZONE_OFFSET_LOCAL;

        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zoneOffset);
    }

    /**
     * Encode an {@link OffsetDateTime} like a {@code System.DateTime} serialized by {@code MemoryMarshal.Write}.
     *
     * @param offsetDateTime an {@link OffsetDateTime} to be encoded.
     * @return a new byte array containing the encoded {@code offsetDateTime}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">
     * struct DateTimeCodec source</a>
     */
    public static byte[] encode(final OffsetDateTime offsetDateTime) {
        final byte[] bytes = new byte[BYTES];
        encode(offsetDateTime, Unpooled.wrappedBuffer(bytes));
        return bytes;
    }

    /**
     * Encode an {@link OffsetDateTime} like a {@code System.DateTime} produced by {@code MemoryMarshal.Write}.
     *
     * @param offsetDateTime an {@link OffsetDateTime} to be encoded.
     * @param out            an output {@link ByteBuf}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">
     * struct DateTimeCodec source</a>
     */
    public static void encode(final OffsetDateTime offsetDateTime, final ByteBuf out) {

        final long epochSecond = offsetDateTime.toEpochSecond();

        checkArgument(epochSecond <= TICKS_MASK, "expected offsetDateTime epoch second in range [0, %s], not %s",
            TICKS_MASK,
            epochSecond);

        final int zoneOffsetTotalSeconds = offsetDateTime.getOffset().getTotalSeconds();
        final long value;

        if (zoneOffsetTotalSeconds == ZONE_OFFSET_UTC_TOTAL_SECONDS) {
            value = epochSecond | KIND_UTC;
        } else if (zoneOffsetTotalSeconds == ZONE_OFFSET_LOCAL_TOTAL_SECONDS) {
            value = epochSecond | KIND_LOCAL;
        } else {
            value = epochSecond | KIND_AMBIGUOUS;
        }

        out.writeLongLE(value);
    }
}
