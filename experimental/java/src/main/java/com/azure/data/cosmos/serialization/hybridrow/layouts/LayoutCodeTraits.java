// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

public final class LayoutCodeTraits {
    /**
     * {@code true} if the specified layout code indicates that an element type always requires a type code.
     * <p>
     * When this method returns {@code true} it indicates that the element value is in the type code.
     *
     * @param code The element type code.
     * @return {@code true} if the specified layout code indicates that an element type always requires a type code.
     */
    public static boolean alwaysRequiresTypeCode(LayoutCode code) {
        return (code == LayoutCode.BOOLEAN) || (code == LayoutCode.BOOLEAN_FALSE) || (code == LayoutCode.NULL);
    }

    /**
     * A canonicalized version of the specified layout code.
     * <p>
     * Some codes (e.g. {@link LayoutCode#BOOLEAN} use multiple type codes to also encode values. This function converts
     * actual value based code into the canonicalized type code for schema comparisons.
     *
     * @param code the code to canonicalize.
     * @return a canonicalized version of the specified layout code.
     */
    public static LayoutCode canonicalize(LayoutCode code) {
        return (code == LayoutCode.BOOLEAN_FALSE) ? LayoutCode.BOOLEAN : code;
    }

    /**
     * Returns the same scope code without the immutable bit set.
     *
     * @param code The scope type code.
     * @return the same scope code without the immutable bit set.
     */
    public static LayoutCode clearImmutableBit(LayoutCode code) {
        return LayoutCode.from((byte) (code.value() & 0xFE));
    }
}