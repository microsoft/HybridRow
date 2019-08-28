//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;

public final class StringToken implements Cloneable {

    public long id;
    public Utf8String path;
    public byte[] varint;

    public StringToken() {
    }

    public StringToken(long id, Utf8String path) {
        this.varint = new byte[StringToken.Count7BitEncodedUInt(id)];
        StringToken.Write7BitEncodedUInt(this.varint, id);
        this.path = path;
        this.id = id;
    }

    @Override
    public StringToken clone() {

        try {
            final StringToken token = (StringToken)super.clone();
            token.id = this.id;
            token.path = this.path;
            token.varint = this.varint;
            return token;
        } catch (CloneNotSupportedException error) {
            assert false : error;
            throw new IllegalStateException(error);
        }
    }

    public boolean isNull() {
        return this.varint == null;
    }

    private static int Count7BitEncodedUInt(long value) {

        // Count the number of bytes needed to write out an int 7 bits at a time.
        int i = 0;

        while (value >= 0x80L) {
            i++;
            value >>>= 7;
        }

        return ++i;
    }

    private static int Write7BitEncodedUInt(byte[] buffer, long value) {

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