//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

public final class LayoutCodeTraits {
    /**
     * Returns true if the type code indicates that, even within a typed scope, this element type
     * always requires a type code (because the value itself is in the type code).
     *
     * @param code The element type code.
     */
    public static boolean AlwaysRequiresTypeCode(LayoutCode code) {
        return (code == LayoutCode.Boolean) || (code == LayoutCode.BooleanFalse) || (code == LayoutCode.Null);
    }

    /**
     * Returns a canonicalized version of the layout code.
     * <p>
     * Some codes (e.g. <see cref="LayoutCode.Boolean" /> use multiple type codes to also encode
     * values.  This function converts actual value based code into the canonicalized type code for schema
     * comparisons.
     *
     * @param code The code to canonicalize.
     */
    public static LayoutCode Canonicalize(LayoutCode code) {
        return (code == LayoutCode.BooleanFalse) ? LayoutCode.Boolean : code;
    }

    /**
     * Returns the same scope code without the immutable bit set.
     *
     * @param code The scope type code.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public static LayoutCode ClearImmutableBit
    // (LayoutCode code)
    public static LayoutCode ClearImmutableBit(LayoutCode code) {
        return code.getValue() & LayoutCode.forValue((byte)0xFE).getValue();
    }
}