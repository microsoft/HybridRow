// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;

    internal class SamplingStringComparer : IEqualityComparer<string>
    {
        public static readonly SamplingStringComparer Default = new SamplingStringComparer();

        public bool Equals(string x, string y)
        {
            Contract.Assert(x != null);
            Contract.Assert(y != null);

            return x.Equals(y);
        }

        public int GetHashCode(string obj)
        {
            Contract.Assert(obj != null);

            unchecked
            {
                uint hash1 = 5381;
                uint hash2 = hash1;
                const int numSamples = 4;
                const int modulus = 13;

                ReadOnlySpan<char> utf16 = obj.AsSpan();
                int max = Math.Min(utf16.Length, numSamples);
                for (int i = 0; i < max; i++)
                {
                    uint c = utf16[(i * modulus) % utf16.Length];
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
