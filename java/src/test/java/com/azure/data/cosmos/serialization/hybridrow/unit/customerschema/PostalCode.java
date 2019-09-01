// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit.customerschema;

import azure.data.cosmos.serialization.hybridrow.unit.*;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public final class PostalCode {
    public Short Plus4;
    public int Zip;

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        return obj instanceof PostalCode && this.equals((PostalCode)obj);
    }

    @Override
    public int hashCode() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            return (this.Zip * 397) ^ this.Plus4.hashCode();
        }
    }

    private boolean equals(PostalCode other) {
        return this.Zip == other.Zip && this.Plus4 == other.Plus4;
    }
}