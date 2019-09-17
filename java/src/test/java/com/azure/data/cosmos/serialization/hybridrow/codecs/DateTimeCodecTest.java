// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * Tests the DateTimeCodec using data generated from C# code
 * <p>
 * Test data was generated from code that looks like this:
 * {@code
 * var buffer = new byte[8];
 * var value = DateTime.Now;
 * MemoryMarshal.Write(buffer, ref value);
 * Console.WriteLine($"new DateTimeItem(new byte[] {{ (byte) {string.Join(", (byte) ", buffer )} }}, OffsetDateTime.parse(\"{value.ToString("o")}\"))");
 * }
 */
@Test(groups = "unit")
public class DateTimeCodecTest {

    @Test(dataProvider = "dateTimeDataProvider")
    public void testDecodeByteArray(byte[] buffer, OffsetDateTime value) {
        OffsetDateTime actual = DateTimeCodec.decode(buffer);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testDecodeByteBuf(byte[] buffer, OffsetDateTime value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
        OffsetDateTime actual = DateTimeCodec.decode(byteBuf);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testEncodeByteArray(byte[] buffer, OffsetDateTime value) {
        byte[] actual = DateTimeCodec.encode(value);
        assertEquals(actual, buffer);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testEncodeByteBuf(byte[] buffer, OffsetDateTime value) {
        ByteBuf actual = Unpooled.wrappedBuffer(new byte[DateTimeCodec.BYTES]).clear();
        DateTimeCodec.encode(value, actual);
        assertEquals(actual.array(), buffer);
    }

    @DataProvider(name = "dateTimeDataProvider")
    private static Iterator<Object[]> dateTimeData() {

        ImmutableList<DateTimeItem> items = ImmutableList.of(
            new DateTimeItem(new byte[] {
                (byte) 120, (byte) 212, (byte) 106, (byte) 251, (byte) 105, (byte) 48, (byte) 215, (byte) 136 },
                OffsetDateTime.parse("2019-09-03T12:26:44.3996280-07:00")),
            new DateTimeItem(new byte[] {
                (byte) 226, (byte) 108, (byte) 87, (byte) 194, (byte) 164, (byte) 48, (byte) 215, (byte) 72 },
                OffsetDateTime.parse("2019-09-03T19:27:28.9493730Z"))
        );

        return items.stream().map(item -> new Object[] { item.buffer, item.value }).iterator();
    }

    private static class DateTimeItem {

        private final byte[] buffer;
        private final OffsetDateTime value;

        DateTimeItem(byte[] buffer, OffsetDateTime value) {
            this.buffer = buffer;
            this.value = value;
        }

        public byte[] buffer() {
            return this.buffer;
        }

        public OffsetDateTime value() {
            return this.value;
        }
    }
}