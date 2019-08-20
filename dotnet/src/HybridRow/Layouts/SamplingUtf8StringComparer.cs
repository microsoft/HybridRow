// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;

    internal class SamplingUtf8StringComparer : IEqualityComparer<Utf8String>
    {
        public static readonly SamplingUtf8StringComparer Default = new SamplingUtf8StringComparer();

        public bool Equals(Utf8String x, Utf8String y)
        {
            Contract.Assert(x != null);
            Contract.Assert(y != null);

            return x.Span.Equals(y.Span);
        }

        public int GetHashCode(Utf8String obj)
        {
            Contract.Assert(obj != null);

            unchecked
            {
                uint hash1 = 5381;
                uint hash2 = hash1;
                const int numSamples = 4;
                const int modulus = 13;

                ReadOnlySpan<byte> utf8 = obj.Span.Span;
                int max = Math.Min(utf8.Length, numSamples);
                for (int i = 0; i < max; i++)
                {
                    uint c = utf8[(i * modulus) % utf8.Length];
                    if (i % 2 == 0)
                    {
                        hash1 = ((hash1 << 5) + hash1) ^ c;
                    }
                    else
                    {
                        hash2 = ((hash2 << 5) + hash2) ^ c;
                    }
                }

                return (int)(hash1 + (hash2 * 1566083941));
            }
        }
    }
}
