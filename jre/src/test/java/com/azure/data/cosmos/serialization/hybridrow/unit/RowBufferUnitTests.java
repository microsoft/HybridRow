//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public class RowBufferUnitTests
public class RowBufferUnitTests {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
    // "SA1139:UseLiteralsSuffixNotationInsteadOfCasting", Justification = "Explicit")] public void VarIntTest()
    public final void VarIntTest() {
        // Brute force test all signed 16-bit values.
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++) {
            short s = (short)i;
            this.RoundTripVarInt(s);
        }

        // Test boundary conditions for larger values.
        this.RoundTripVarInt(0);
        this.RoundTripVarInt(Integer.MIN_VALUE);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.RoundTripVarInt(unchecked((int)0x80000000ul));
        this.RoundTripVarInt(0x80000000);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.RoundTripVarInt(unchecked((int)0x7FFFFFFFul));
        this.RoundTripVarInt(0x7FFFFFFF);
        this.RoundTripVarInt(Integer.MAX_VALUE);
        this.RoundTripVarInt(Long.MIN_VALUE);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.RoundTripVarInt(unchecked((long)0x8000000000000000ul));
        this.RoundTripVarInt((long)0x8000000000000000);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.RoundTripVarInt(unchecked((long)0x7FFFFFFFFFFFFFFFul));
        this.RoundTripVarInt((long)0x7FFFFFFFFFFFFFFF);
        this.RoundTripVarInt(Long.MAX_VALUE);
    }

    private void RoundTripVarInt(short s) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong encoded = RowBuffer.RotateSignToLsb(s);
        long encoded = RowBuffer.rotateSignToLsb(s);
        long decoded = RowBuffer.rotateSignToMsb(encoded);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: short t = unchecked((short)decoded);
        short t = (short)decoded;
        Assert.AreEqual(s, t, "Value: {0}", s);
    }

    private void RoundTripVarInt(int s) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong encoded = RowBuffer.RotateSignToLsb(s);
        long encoded = RowBuffer.rotateSignToLsb(s);
        long decoded = RowBuffer.rotateSignToMsb(encoded);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: int t = unchecked((int)decoded);
        int t = (int)decoded;
        Assert.AreEqual(s, t, "Value: {0}", s);
    }

    private void RoundTripVarInt(long s) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong encoded = RowBuffer.RotateSignToLsb(s);
        long encoded = RowBuffer.rotateSignToLsb(s);
        long decoded = RowBuffer.rotateSignToMsb(encoded);
        Assert.AreEqual(s, decoded, "Value: {0}", s);
    }
}