// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

public class SamplingUtf8StringComparer implements IEqualityComparer<Utf8String> {
    public static final SamplingUtf8StringComparer Default = new SamplingUtf8StringComparer();

    public final boolean equals(Utf8String x, Utf8String y) {
        checkArgument(x != null);
        checkArgument(y != null);

        return x.Span.equals(y.Span);
    }

    public final int hashCode(Utf8String obj) {
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

            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ReadOnlySpan<byte> utf8 = obj.Span.Span;
            ReadOnlySpan<Byte> utf8 = obj.Span.Span;
            int max = Math.min(utf8.Length, numSamples);
            for (int i = 0; i < max; i++) {
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: uint c = utf8[(i * modulus) % utf8.Length];
                int c = utf8[(i * modulus) % utf8.Length];
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