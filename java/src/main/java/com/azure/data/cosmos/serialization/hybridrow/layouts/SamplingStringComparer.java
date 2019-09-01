// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

public class SamplingStringComparer implements IEqualityComparer<String> {
    public static final SamplingStringComparer Default = new SamplingStringComparer();

    public final boolean equals(String x, String y) {
        checkArgument(x != null);
        checkArgument(y != null);

        return x.equals(y);
    }

    public final int hashCode(String obj) {
        checkArgument(obj != null);

        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: uint hash1 = 5381;
            int hash1 = 5381;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: uint hash2 = hash1;
            int hash2 = hash1;
            final int numSamples = 4;
            final int modulus = 13;

            ReadOnlySpan<Character> utf16 = obj.AsSpan();
            int max = Math.min(utf16.Length, numSamples);
            for (int i = 0; i < max; i++) {
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: uint c = utf16[(i * modulus) % utf16.Length];
                int c = utf16[(i * modulus) % utf16.Length];
                if (i % 2 == 0) {
                    hash1 = ((hash1 << 5) + hash1) ^ c;
                } else {
                    hash2 = ((hash2 << 5) + hash2) ^ c;
                }
            }

            return hash1 + (hash2 * 1566083941);
        }
    }
}