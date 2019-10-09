// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.codecs;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

/**
 * Tests the GuidCodec using data generated from C# code
 * <p>
 * Test data was generated from code that looks like this:
 * {@code
 * var buffer = new byte[16];
 * var value = Guid.NewGuid();
 * MemoryMarshal.Write(buffer, ref value);
 * Console.WriteLine($"new GuidItem(new byte[] {{ (byte) {string.Join(", (byte) ", buffer )} }}, UUID.fromString(\"{value.ToString()}\"))");
 * }
 */
@Test(groups = "unit")
public class GuidCodecTest {

    @Test(dataProvider = "guidDataProvider")
    public void testDecodeByteArray(byte[] buffer, UUID value) {
        UUID actual = GuidCodec.decode(buffer);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "guidDataProvider")
    public void testDecodeByteBuf(byte[] buffer, UUID value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
        UUID actual = GuidCodec.decode(byteBuf);
        assertEquals(actual, value);
    }

    @Test(dataProvider = "guidDataProvider")
    public void testEncodeByteArray(byte[] buffer, UUID value) {
        byte[] actual = GuidCodec.encode(value);
        assertEquals(actual, buffer);
    }

    @Test(dataProvider = "guidDataProvider")
    public void testEncodeByteBuf(byte[] buffer, UUID value) {
        ByteBuf actual = Unpooled.wrappedBuffer(new byte[GuidCodec.BYTES]).clear();
        GuidCodec.encode(value, actual);
        assertEquals(actual.array(), buffer);
    }

    @DataProvider(name = "guidDataProvider")
    private static Iterator<Object[]> guidData() {

        ImmutableList<GuidItem> items = ImmutableList.of(
            new GuidItem(
                new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0
                },
                UUID.fromString("00000000-0000-0000-0000-000000000000")),
            new GuidItem(
                new byte[] {
                    (byte) 191, (byte) 17, (byte) 214, (byte) 27, (byte) 22, (byte) 170, (byte) 84, (byte) 69,
                    (byte) 147, (byte) 105, (byte) 195, (byte) 216, (byte) 1, (byte) 81, (byte) 34, (byte) 107
                },
                UUID.fromString("1bd611bf-aa16-4554-9369-c3d80151226b"))
        );

        return items.stream().map(item -> new Object[] { item.buffer, item.value }).iterator();
    }

    private static class GuidItem {

        private final byte[] buffer;
        private final UUID value;

        GuidItem(byte[] buffer, UUID value) {
            this.buffer = buffer;
            this.value = value;
        }

        public byte[] buffer() {
            return this.buffer;
        }

        public UUID value() {
            return this.value;
        }
    }
}
