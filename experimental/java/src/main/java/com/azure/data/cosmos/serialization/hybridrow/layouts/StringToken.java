// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StringToken implements Cloneable {

    public static final StringToken NONE = new StringToken();

    private final long id;
    private final Utf8String path;
    private final ByteBuf varint;

    public StringToken(long id, @Nonnull Utf8String path) {

        checkNotNull(path);

        byte[] buffer = new byte[count7BitEncodedUInt(id)];
        StringToken.write7BitEncodedUInt(buffer, id);

        this.varint = Unpooled.wrappedBuffer(buffer).asReadOnly();
        this.path = path;
        this.id = id;
    }

    private StringToken() {
        this.id = 0L;
        this.path = Utf8String.EMPTY;
        this.varint = Unpooled.wrappedBuffer(new byte[1]).asReadOnly();
    }

    public boolean isNull() {
        return this.varint() == null;
    }

    public long id() {
        return this.id;
    }

    public Utf8String path() {
        return this.path;
    }

    public ByteBuf varint() {
        return this.varint;
    }

    @Override
    protected StringToken clone() {

        try {
            return (StringToken) super.clone();
        } catch (CloneNotSupportedException error) {
            assert false : error;
            throw new IllegalStateException(error);
        }
    }

    private static int count7BitEncodedUInt(long value) {

        // Count the number of bytes needed to write out an int 7 bits at a time.
        int i = 0;

        while (value >= 0x80L) {
            i++;
            value >>>= 7;
        }

        return ++i;
    }

    private static int write7BitEncodedUInt(byte[] buffer, long value) {

        // Write an unsigned long 7 bits at a time. The high bit of the byte, when set, indicates there are more bytes.
        int i = 0;

        while (value >= 0x80L) {
            buffer[i++] = (byte) (value | 0x80);
            value >>>= 7;
        }

        buffer[i++] = (byte) value;
        return i;
    }
}