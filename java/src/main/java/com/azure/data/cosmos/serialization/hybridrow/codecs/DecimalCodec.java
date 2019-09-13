// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class DecimalCodec {

    public static final int BYTES = 4 * Integer.BYTES;

    private static final int FLAGS_MASK_INVALID = 0b01111111000000001111111111111111;
    private static final int FLAGS_MASK_POWER = 0b00000000111111110000000000000000;
    private static final int FLAGS_MASK_SIGN = 0b10000000000000000000000000000000;

    private static final BigInteger MAGNITUDE_MAX = new BigInteger(new byte[] { (byte)0x00,
        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF });

    private static final BigInteger MAGNITUDE_MIN = new BigInteger(new byte[] { (byte)0xFF,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01 });

    private static final MathContext REDUCED_PRECISION = new MathContext(28, RoundingMode.HALF_EVEN);
    private static final int SCALE_MAX = 28;
    private static final int SCALE_MIN = 0;
    private static final int SCALE_SHIFT = 16;
    private static final int VALUE_LENGTH = 3 * Integer.BYTES;

    private DecimalCodec() {
    }

    /**
     * Decode an {@link BigDecimal} serialized like a {@code System.Decimal} by {@code MemoryMarshal.Write}.
     *
     * @param bytes a {@link byte} array containing the serialized {@code System.Decimal} to be decoded.
     * @return a new {@link BigDecimal}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/system/decimal.cs.html">
     * struct DecimalCodec source</a>
     */
    public static BigDecimal decode(@Nonnull final byte[] bytes) {
        checkNotNull(bytes);
        return decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * Decode an {@link BigDecimal} serialized like a {@code System.Decimal} by {@code MemoryMarshal.Write}.
     *
     * @param in a {@link ByteBuf} containing the serialized {@code System.Decimal} to be decoded.
     * @return a new {@link BigDecimal}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/system/decimal.cs.html">
     * struct DecimalCodec source</a>
     */
    public static BigDecimal decode(@Nonnull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");

        checkArgument(in.readableBytes() >= BYTES, "expected %s readable bytes, not %s",
            BYTES,
            in.readableBytes());

        // The flags field is--and must be--interpreted as follows
        //
        // Bits   Interpretation
        // -----  --------------------------------------------------------------------------------------------
        // 0-15    unused and must be zero
        // 16-23   a value between 0 and 28 specifying the number of digits to the right of the decimal point
        // 24-30   unused and must be zero
        // 31      specifies the sign of the value, 1 meaning negative and 0 meaning non-negative

        final int flags = in.readIntLE();
        checkState((flags & FLAGS_MASK_INVALID) == 0, "invalid flags field: %s", flags);

        final int scale = (flags | FLAGS_MASK_POWER) >>> SCALE_SHIFT;
        checkState(scale <= SCALE_MAX);

        final int signum = (flags & FLAGS_MASK_SIGN) == 0 ? 1 : -1;
        final byte[] buffer = new byte[VALUE_LENGTH];
        in.readBytes(buffer);

        int i = VALUE_LENGTH;
        int j = 0;

        while (i > j) {
            final byte b = buffer[--i];
            buffer[i] = buffer[j];
            buffer[j++] = b;
        }

        return new BigDecimal(new BigInteger(signum, buffer), scale);
    }

    /**
     * Encode a {@link BigDecimal} like a {@code System.Decimal} serialized by {@code MemoryMarshal.Write}.
     *
     * @param bigDecimal a {@link BigDecimal} to be encoded.
     * @return a new byte array containing the encoded {@code bigDecimal}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/system/decimal.cs.html">
     * struct DecimalCodec source</a>
     */
    public static byte[] encode(final BigDecimal bigDecimal) {
        final byte[] bytes = new byte[BYTES];
        encode(bigDecimal, Unpooled.wrappedBuffer(bytes));
        return bytes;
    }

    /**
     * Encode a {@link BigDecimal} like a {@code System.Decimal} serialized by {@code MemoryMarshal.Write}.
     *
     * @param value a {@link BigDecimal} to be encoded.
     * @param out   an output {@link ByteBuf}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/system/decimal.cs.html">
     * struct DecimalCodec source</a>
     */
    public static void encode(@Nonnull BigDecimal value, @Nonnull final ByteBuf out) {

        checkNotNull(value, "expected non-null value");
        checkNotNull(value, "expected non-null out");

        final int signum = value.signum();

        if (signum == 0) {
            out.writeZero(BYTES);
            return;
        }

        BigInteger unscaledValue = value.unscaledValue();

        if (unscaledValue.compareTo(MAGNITUDE_MIN) < 0 || unscaledValue.compareTo(MAGNITUDE_MAX) > 0) {
            value = value.stripTrailingZeros();
            unscaledValue = value.unscaledValue();
            if (unscaledValue.compareTo(MAGNITUDE_MIN) < 0 || unscaledValue.compareTo(MAGNITUDE_MAX) > 0) {
                value = new BigDecimal(unscaledValue, min(max(value.scale(), SCALE_MIN), SCALE_MAX), REDUCED_PRECISION);
                unscaledValue = value.unscaledValue();
            }
        }

        if (value.scale() < SCALE_MIN) {
            value = value.setScale(SCALE_MIN, RoundingMode.HALF_EVEN);
            unscaledValue = value.unscaledValue();
        } else if (value.scale() > SCALE_MAX) {
            value = value.setScale(SCALE_MAX, RoundingMode.HALF_EVEN);
            unscaledValue = value.unscaledValue();
        }

        final byte[] bytes = unscaledValue.toByteArray();
        final byte[] buffer = new byte[VALUE_LENGTH];
        final int flags;

        if (signum == -1) {
            flags = FLAGS_MASK_SIGN | value.scale() << SCALE_SHIFT;
            int carry = 1;
            for (int i = buffer.length, j = 0; i-- > 0; j++) {
                final int n = ((bytes[i] ^ 0b1111_1111) & 0b1111_1111) + carry;
                if ((n & 0b1_0000_0000) == 0) {
                    carry = 0;
                }
                buffer[j] = bytes[i];
            }
        } else {
            flags = value.scale() << SCALE_SHIFT;
            for (int i = buffer.length, j = 0; i-- > 0; j++) {
                buffer[j] = bytes[i];
            }
        }

        out.writeIntLE(flags);
        out.writeBytes(buffer, 0, 4);
        out.writeBytes(buffer, 8, 4);
        out.writeBytes(buffer, 4, 4);
    }
}
