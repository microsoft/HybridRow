// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static methods for encoding and decoding {@link UUID}s serialized as {@code System.Guid}s
 *
 * {@link UUID}s are serialized like {@code System.Guid}s read and written by {@code MemoryMarshal.Read} and
 * {@code MemoryMarshal.Write}.
 *
 * <table summary="Layout of field value">
 *     <tbody>
 *      <tr><td>
 *          Bits 00-31
 *      </td><td>
 *          Contain an unsigned 32-bit int serialized in little-endian format.
 *      </td></tr>
 *      <tr><td>
 *          Bits 32-47
 *      </td><td>
 *          Contain an unsigned 16-bit int serialized in little-endian format.
 *      </td></tr>
 *      <tr><td>
 *          Bits 48-63
 *      </td><td>
 *          Contain an unsigned 16-bit int serialized in little-endian format.
 *      </td></tr>
 *      <tr><td>
 *          Bits 64-127
 *      </td><td>
 *          Contain an unsigned 64-bit int serialized in big-endian format.
 *      </td></tr>
 *   </tbody>
 * </table>
 *
 * @see <a href="https://referencesource.microsoft.com/#mscorlib/system/guid.cs">struct Guid source</a>
 */
public final class GuidCodec {

    public static final int BYTES = 2 * Long.BYTES;
    public static final UUID EMPTY = new UUID(0L, 0L);

    private GuidCodec() {
    }

    /**
     * Decode a {@link UUID} serialized like a {@code System.Guid} by {@code MemoryMarshal.Write}.
     *
     * @param bytes an array containing the serialized {@link UUID} to be decoded.
     * @return a new {@link UUID}.
     */
    public static UUID decode(@Nonnull final byte[] bytes) {
        checkNotNull(bytes);
        return decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * Decode a {@link UUID} serialized like a {@code System.Guid} by {@code MemoryMarshal.Write}.
     *
     * @param in a {@link ByteBuf} containing the serialized {@link UUID} to be decoded.
     * @return a new {@link UUID}.
     */
    public static UUID decode(@Nonnull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");

        checkArgument(in.readableBytes() >= BYTES, "expected %s readable bytes, not %s bytes",
            BYTES, in.readableBytes());

        final long mostSignificantBits = in.readUnsignedIntLE() << Integer.SIZE
            | (0x000000000000FFFFL & in.readShortLE()) << Short.SIZE
            | (0x000000000000FFFFL & in.readShortLE());

        return new UUID(mostSignificantBits, in.readLong());
    }

    /**
     * Encodes a {@link UUID} like a {@code System.Guid} serialized by {@code MemoryMarshal.Write}.
     *
     * @param uuid a {@link UUID} to be encoded.
     * @return a new byte array containing the encoded.
     */
    public static byte[] encode(final UUID uuid) {
        final byte[] bytes = new byte[BYTES];
        encode(uuid, Unpooled.wrappedBuffer(bytes).clear());
        return bytes;
    }

    /**
     * Encodes a {@link UUID} like a {@code System.Guid} serialized by {@code MemoryMarshal.Write}.
     *
     * @param uuid a {@link UUID} to be encoded.
     * @param out  an output {@link ByteBuf}.
     */
    public static void encode(final UUID uuid, final ByteBuf out) {

        final long mostSignificantBits = uuid.getMostSignificantBits();

        out.writeIntLE((int) ((mostSignificantBits & 0xFFFFFFFF00000000L) >>> 32));
        out.writeShortLE((short) ((mostSignificantBits & 0x00000000FFFF0000L) >>> 16));
        out.writeShortLE((short) (mostSignificantBits & 0x000000000000FFFFL));

        out.writeLong(uuid.getLeastSignificantBits());
    }
}
