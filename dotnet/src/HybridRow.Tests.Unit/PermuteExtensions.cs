// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System.Collections.Generic;
    using System.Linq;

    /// <summary>
    /// Extension methods for computing permutations of <see cref="IEnumerable{T}"/>.
    /// </summary>
    public static class PermuteExtensions
    {
        /// <summary>Generate all permutations of a given enumerable.</summary>
        public static IEnumerable<IEnumerable<T>> Permute<T>(this IEnumerable<T> list)
        {
            int start = 0;
            foreach (T element in list)
            {
                int index = start;
                T[] first = { element };
                IEnumerable<T> rest = list.Where((s, i) => i != index);
                if (!rest.Any())
                {
                    yield return first;
                }

                foreach (IEnumerable<T> sub in rest.Permute())
                {
                    yield return first.Concat(sub);
                }

                start++;
            }
        }
    }
}
