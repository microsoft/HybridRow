//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public readonly struct StringToken
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class StringToken {
    // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
    ///#pragma warning disable CA1051 // Do not declare visible instance fields
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public readonly ulong Id;
    public long Id;
    public Utf8String Path;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public readonly byte[] Varint;
    public byte[] Varint;
    // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
    ///#pragma warning restore CA1051 // Do not declare visible instance fields

    public StringToken() {
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public StringToken(ulong id, Utf8String path)
    public StringToken(long id, Utf8String path) {
        this.Id = id;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.Varint = new byte[StringToken.Count7BitEncodedUInt(id)];
        this.Varint = new byte[StringToken.Count7BitEncodedUInt(id)];
        StringToken.Write7BitEncodedUInt(this.Varint.AsSpan(), id);
        this.Path = path;
    }

    public StringToken clone() {
        StringToken varCopy = new StringToken();

        varCopy.Id = this.Id;
        varCopy.Varint = this.Varint;
        varCopy.Path = this.Path;

        return varCopy;
    }

    boolean getIsNull()

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static int Count7BitEncodedUInt(ulong value)
    private static int Count7BitEncodedUInt(long value) {
        // Count the number of bytes needed to write out an int 7 bits at a time.
        int i = 0;
        while (value >= 0x80L) {
            i++;
            //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
            // operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
            value >>>= 7;
        }

        i++;
        return i;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static int Write7BitEncodedUInt(Span<byte> buffer, ulong value)
    private static int Write7BitEncodedUInt(Span<Byte> buffer, long value) {
        // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
        // when set, indicates there are more bytes.
        int i = 0;
        while (value >= 0x80L) {
            // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
            //ORIGINAL LINE: buffer[i] = unchecked((byte)(value | 0x80));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            buffer[i] = (byte)(value | 0x80);
            i++;
            //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
            // operator since the left operand was originally of an unsigned type, but you should confirm this
            // replacement:
            value >>>= 7;
        }

        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: buffer[i] = (byte)value;
        buffer[i] = (byte)value;
        i++;
        return i;
    }
}