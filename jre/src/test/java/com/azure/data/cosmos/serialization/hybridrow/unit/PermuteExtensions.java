//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

/**
 * Extension methods for computing permutations of {@link IEnumerable{T}}.
 */
public final class PermuteExtensions {
    /**
     * Generate all permutations of a given enumerable.
     */
    public static <T> java.lang.Iterable<java.lang.Iterable<T>> Permute(java.lang.Iterable<T> list) {
        int start = 0;
        for (T element : list) {
            int index = start;
            T[] first = { element };
            java.lang.Iterable<T> rest = list.Where((s, i) -> i != index);
            if (!rest.Any()) {
                // TODO: C# TO JAVA CONVERTER: Java does not have an equivalent to the C# 'yield' keyword:
                yield return first;
            }

            for (java.lang.Iterable<T> sub :
                PermuteExtensions.Permute(rest)) {
                // TODO: C# TO JAVA CONVERTER: Java does not have an equivalent to the C# 'yield' keyword:
                yield return first.Concat(sub);
            }

            start++;
        }
    }
}