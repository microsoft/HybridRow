// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class Utf8StringTest {

    @Test
    public void testIsEmpty() {

        Utf8String value;

        value = Utf8String.transcodeUtf16("");
        assertTrue(value.isEmpty());
        assertSame(value, Utf8String.EMPTY);

        value = Utf8String.fromUnsafe(Unpooled.EMPTY_BUFFER);
        assertTrue(value.isEmpty());
        assertSame(value, Utf8String.EMPTY);

        Optional<Utf8String> optional = Utf8String.from(Unpooled.EMPTY_BUFFER);
        assertTrue(optional.isPresent());
        assertTrue(optional.get().isEmpty());
        assertSame(optional.get(), Utf8String.EMPTY);
    }

    @Test
    public void testIsNull() {
        Utf8String value = Utf8String.transcodeUtf16(null);
        assertTrue(value.isNull());
        assertSame(value, Utf8String.NULL);
    }

    @Test
    public void testChars() {
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testCodePoints(UnicodeTextItem item) {
        Utf8String value = Utf8String.transcodeUtf16(item.value());
        assertEquals(value.codePoints().iterator(), item.value().codePoints().iterator());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test(dataProvider = "unicodeTextDataProvider")
    public void testCompareTo(UnicodeTextItem item) {

        Utf8String value = Utf8String.transcodeUtf16(item.value());
        assertEquals(value.compareTo(value), 0);
        assertEquals(value.compareTo(item.value()), 0);

        Utf8String unsafe = Utf8String.fromUnsafe(item.byteBuf());
        assertEquals(unsafe.compareTo(value), 0);
        assertEquals(unsafe.compareTo(item.value()), 0);

        Optional<Utf8String> optional = Utf8String.from(item.byteBuf());
        assertTrue(optional.isPresent());
        assertEquals(optional.get().compareTo(value), 0);
        assertEquals(optional.get().compareTo(item.value()), 0);

        assertThrows(NullPointerException.class, () -> {
            //noinspection ConstantConditions,ResultOfMethodCallIgnored
            value.compareTo((Utf8String) null);
        });

        assertEquals(Utf8String.NULL.compareTo(item.value()), -1);
        assertEquals(Utf8String.NULL.compareTo((String) null), 0);
        assertEquals(Utf8String.EMPTY.compareTo((String) null), 1);
        assertEquals(Utf8String.EMPTY.compareTo(Utf8String.NULL), 1);
    }

    @Test
    public void testEncodedLength() {
    }

    @Test
    public void testTestEquals() {
    }

    @Test
    public void testTestEquals1() {
    }

    @Test
    public void testTestEquals2() {
    }

    @Test
    public void testTestEquals3() {
    }

    @Test
    public void testFrom() {
    }

    @Test
    public void testFromUnsafe() {
    }

    @Test
    public void testTestHashCode() {
    }

    @Test
    public void testLength() {
    }

    @Test
    public void testTestToString() {
    }

    @Test
    public void testToUtf16() {
    }

    @Test
    public void testTranscodeUtf16() {
    }

    @DataProvider(name = "unicodeTextDataProvider")
    private static Iterator<Object[]> unicodeTextData() {

        ImmutableList<UnicodeTextItem> items = ImmutableList.of(
            // US ASCII (7-bit encoding)
            // ..English
            new UnicodeTextItem("The quick brown fox jumps over the lazy dog."),
            // ISO-8859-1 (8-bit encoding that adds Latin-1 supplement to US ASCII)
            // ..German
            new UnicodeTextItem("Der schnelle braune Fuchs springt über den faulen Hund."),
            // ..Swedish
            new UnicodeTextItem("Den snabbbruna räven hoppar över den lata hunden."),
            // ISO 8859-7 (11-bit encoding that covers the Greek and Coptic alphabets)
            // ..Greek
            new UnicodeTextItem("Η γρήγορη καφέ αλεπού πηδάει πάνω από το τεμπέλικο σκυλί."),
            // Katakana code block (16-bit encoding containing katakana characters for the Japanese and Ainu languages)
            // ..Japanese
            new UnicodeTextItem("速い茶色のキツネは怠laな犬を飛び越えます。"),
            // Deseret code block (21-bit encoding containing an English alphabet invented by the LDS Church)
            // ..Deseret
            new UnicodeTextItem("\uD801\uDC10\uD801\uDC2F\uD801\uDC4A\uD801\uDC2C, \uD801\uDC38\uD801\uDC35 \uD801\uDC2A\uD801\uDC49 \uD801\uDC4F?")
        );

        return items.stream().map(item -> new Object[] { item }).iterator();
    }

    private static class UnicodeTextItem {

        private final byte[] buffer;
        private final String value;

        UnicodeTextItem(String value) {
            this.buffer = value.getBytes(StandardCharsets.UTF_8);
            this.value = value;
        }

        public byte[] buffer() {
            return this.buffer;
        }

        public ByteBuf byteBuf() {
            return Unpooled.wrappedBuffer(this.buffer);
        }

        @Override
        public String toString() {
            return this.value.toString();
        }

        public String value() {
            return this.value;
        }
    }
}