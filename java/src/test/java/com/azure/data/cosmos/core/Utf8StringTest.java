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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
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

        // TODO: DANOBLE: create new test from the following assertions because they don't relate to the item under test

        assertThrows(NullPointerException.class, () -> {
            //noinspection ConstantConditions,ResultOfMethodCallIgnored
            value.compareTo((Utf8String) null);
        });

        assertEquals(Utf8String.NULL.compareTo(item.value()), -1);
        assertEquals(Utf8String.NULL.compareTo((String) null), 0);
        assertEquals(Utf8String.EMPTY.compareTo((String) null), 1);
        assertEquals(Utf8String.EMPTY.compareTo(Utf8String.NULL), 1);
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testEncodedLength(UnicodeTextItem item) {
        final int encodedLength = item.buffer.length;
        assertEquals(Utf8String.from(item.byteBuf()).orElseThrow(AssertionError::new).encodedLength(), encodedLength);
        assertEquals(Utf8String.fromUnsafe(item.byteBuf()).encodedLength(), encodedLength);
        assertEquals(Utf8String.transcodeUtf16(item.value()).encodedLength(), encodedLength);
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testTestEquals(UnicodeTextItem item) {

        TestEquals testEquals = new TestEquals(item);

        testEquals.invoke(item.byteBuf());
        testEquals.invoke(item.value());
        testEquals.invoke(Utf8String.fromUnsafe(item.byteBuf()));
        testEquals.invoke((Object)Utf8String.fromUnsafe(item.byteBuf()));

        TestNotEquals testNotEquals = new TestNotEquals(item);

        testNotEquals.invoke(Utf8String.EMPTY);
        testNotEquals.invoke(Unpooled.EMPTY_BUFFER);
        testNotEquals.invoke((Object) Utf8String.EMPTY);
        testNotEquals.invoke("");

        testNotEquals.invoke(Utf8String.NULL);
        testNotEquals.invoke((ByteBuf) null);
        testNotEquals.invoke((Object) null);
        testNotEquals.invoke((String) null);

        testNotEquals.invoke((Object) item.byteBuf());
        testNotEquals.invoke((Object) item.value());

        testNotEquals.invoke((Utf8String) null);
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testFrom(UnicodeTextItem item) {
        Optional<Utf8String> value = Utf8String.from(item.byteBuf());
        assertTrue(value.isPresent());
        assertTrue(value.get().equals(value.get()));
        assertTrue(value.get().equals(item.value()));
        assertTrue(value.get().equals(item.byteBuf()));
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testFromUnsafe(UnicodeTextItem item) {
        Utf8String value = Utf8String.fromUnsafe(item.byteBuf());
        assertTrue(value.equals(value));
        assertTrue(value.equals(item.value()));
        assertTrue(value.equals(item.byteBuf()));
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testHashCode(UnicodeTextItem item) {
        Utf8String value = Utf8String.fromUnsafe(item.byteBuf());
        assertEquals(value.hashCode(), item.byteBuf().hashCode());
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testLength(UnicodeTextItem item) {
        assertEquals(Utf8String.fromUnsafe(item.byteBuf()).length(), item.value().length());
    }

    @Test
    public void testToString() {
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testToUtf16(UnicodeTextItem item) {
        assertEquals(Utf8String.fromUnsafe(item.byteBuf()).toUtf16(), item.value());
    }

    @Test(dataProvider = "unicodeTextDataProvider")
    public void testTranscodeUtf16(UnicodeTextItem item) {
        assertEquals(Utf8String.transcodeUtf16(item.value()).toUtf16(), item.value());
    }

    @DataProvider(name = "unicodeTextDataProvider")
    private static Iterator<Object[]> unicodeTextData() {

        ImmutableList<UnicodeTextItem> items = ImmutableList.of(

            // US ASCII (7-bit encoding)
            // ..English
            new UnicodeTextItem("The quick brown fox jumps over the lazy dog."),

            // ISO-8859-1 (8-bit encoding)
            // ..German
            new UnicodeTextItem("Der schnelle braune Fuchs springt über den faulen Hund."),
            // ..Icelandic
            new UnicodeTextItem("Skjótur brúni refurinn hoppar yfir lata hundinn."),
            // ..Spanish
            new UnicodeTextItem("El rápido zorro marrón salta sobre el perro perezoso."),

            // ISO 8859-7 (11-bit encoding)
            // ..Greek
            new UnicodeTextItem("Η γρήγορη καφέ αλεπού πηδάει πάνω από το τεμπέλικο σκυλί."),

            // Katakana code block (16-bit encoding)
            // ..Japanese
            new UnicodeTextItem("速い茶色のキツネは怠laな犬を飛び越えます。"),

             // Deseret code block (21-bit encoding containing an English alphabet invented by the LDS Church)
             // ..Deseret
            new UnicodeTextItem("\uD801\uDC10\uD801\uDC2F\uD801\uDC4A\uD801\uDC2C, \uD801\uDC38\uD801\uDC35 \uD801\uDC2A\uD801\uDC49 \uD801\uDC4F?")
        );

        return items.stream().map(item -> new Object[] { item }).iterator();
    }

    private static class TestEquals {

        private final UnicodeTextItem item;
        private final Utf8String[] variants;

        public TestEquals(UnicodeTextItem item) {

            this.item = item;

            this.variants = new Utf8String[] {
                Utf8String.from(this.item.byteBuf()).orElseThrow(AssertionError::new),
                Utf8String.fromUnsafe(this.item.byteBuf()),
                Utf8String.transcodeUtf16(this.item.value())
            };
        }

        public void invoke(ByteBuf other) {
            for (Utf8String variant : this.variants) {
                assertTrue(variant.equals(other));
            }
        }

        public void invoke(Object other) {
            for (Utf8String variant : this.variants) {
                assertTrue(variant.equals(other));
            }
        }

        public void invoke(String other) {
            for (Utf8String variant : this.variants) {
                assertTrue(variant.equals(other));
            }
        }

        public void invoke(Utf8String other) {
            for (Utf8String variant : this.variants) {
                assertTrue(variant.equals(other));
            }
        }
    }

    private static class TestNotEquals {

        private final UnicodeTextItem item;
        private final Utf8String[] variants;

        public TestNotEquals(UnicodeTextItem item) {

            this.item = item;

            this.variants = new Utf8String[] {
                Utf8String.from(this.item.byteBuf()).orElseThrow(AssertionError::new),
                Utf8String.fromUnsafe(this.item.byteBuf()),
                Utf8String.transcodeUtf16(this.item.value())
            };
        }

        public void invoke(ByteBuf other) {
            for (Utf8String variant : this.variants) {
                assertFalse(variant.equals(other));
            }
        }

        public void invoke(Object other) {
            for (Utf8String variant : this.variants) {
                assertFalse(variant.equals(other));
            }
        }

        public void invoke(String other) {
            for (Utf8String variant : this.variants) {
                assertFalse(variant.equals(other));
            }
        }

        public void invoke(Utf8String other) {
            for (Utf8String variant : this.variants) {
                assertFalse(variant.equals(other));
            }
        }
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