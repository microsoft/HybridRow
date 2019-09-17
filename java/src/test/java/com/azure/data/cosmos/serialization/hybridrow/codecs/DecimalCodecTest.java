// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * Tests the DecimalCodec using data generated from C# code
 * <p>
 * Test data was generated from code that looks like this:
 * {@code
 * using System.Runtime.InteropServices;
 * var buffer = new byte[16];
 * var value = decimal.MaxValue;
 * MemoryMarshal.Write(buffer, ref value);
 * Console.WriteLine($"new DecimalItem(new byte[] {{ (byte) {string.Join(", (byte) ", buffer )} }}, new BigDecimal(\"
 * {value.ToString()}\"))");
 * }
 */
@Test(groups = "unit")
public class DecimalCodecTest {

    @Test(dataProvider = "decimalDataProvider")
    public void testDecodeByteArray(byte[] buffer, BigDecimal value) {
        BigDecimal actual = DecimalCodec.decode(buffer);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "decimalDataProvider")
    public void testDecodeByteBuf(byte[] buffer, BigDecimal value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
        BigDecimal actual = DecimalCodec.decode(byteBuf);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "decimalDataProvider")
    public void testEncodeByteArray(byte[] buffer, BigDecimal value) {
        byte[] actual = DecimalCodec.encode(value);
        assertEquals(actual, buffer);
    }

    @Test(dataProvider = "decimalDataProvider")
    public void testEncodeByteBuf(byte[] buffer, BigDecimal value) {
        ByteBuf actual = Unpooled.wrappedBuffer(new byte[DecimalCodec.BYTES]).clear();
        DecimalCodec.encode(value, actual);
        assertEquals(actual.array(), buffer);
    }

    @DataProvider(name = "decimalDataProvider")
    private static Iterator<Object[]> decimalData() {

        ImmutableList<DecimalItem> items = ImmutableList.of(
            new DecimalItem( // decimal.MinusOne
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 128, // flags
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,   // high
                    (byte) 1, (byte) 0, (byte) 0, (byte) 0,   // low
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,   // mid
                },
                new BigDecimal("-1")),
            new DecimalItem( // decimal.Zero
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // flags
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // high
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // low
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // mid
                },
                new BigDecimal("0")),
            new DecimalItem( // decimal.One
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // flags
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // high
                    (byte) 1, (byte) 0, (byte) 0, (byte) 0,  // low
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,  // mid
                },
                new BigDecimal("1")),
            new DecimalItem( // decimal.MinValue
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 128,        // flags
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // high
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // low
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // mid
                },
                new BigDecimal("-79228162514264337593543950335")),
            new DecimalItem( // decimal.MaxValue
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,          // flags
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // high
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // low
                    (byte) 255, (byte) 255, (byte) 255, (byte) 255,  // mid
                },
                new BigDecimal("79228162514264337593543950335")),
            new DecimalItem( // new decimal(Math.PI)
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 14, (byte) 0,         // flags
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,          // high
                    (byte) 131, (byte) 36, (byte) 106, (byte) 231,   // low
                    (byte) 185, (byte) 29, (byte) 1, (byte) 0 },     // mid
                new BigDecimal("3.14159265358979"))
        );

        return items.stream().map(item -> new Object[] { item.buffer, item.value }).iterator();
    }

    private static class DecimalItem {

        private final byte[] buffer;
        private final BigDecimal value;

        DecimalItem(byte[] buffer, BigDecimal value) {
            this.buffer = buffer;
            this.value = value;
        }

        public byte[] buffer() {
            return this.buffer;
        }

        public BigDecimal value() {
            return this.value;
        }
    }
}
