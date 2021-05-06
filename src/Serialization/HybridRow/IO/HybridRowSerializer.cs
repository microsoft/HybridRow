// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using System.Runtime.CompilerServices;

    /// <summary>
    /// Provides adapters and utility functions that simplify generated code by allowing that code
    /// to address a larger variety of design choices (e.g. class vs struct) with syntactically identical
    /// generated code patterns.
    /// </summary>
    public static class HybridRowSerializer
    {
        /// <summary>Result of performing an equality reference check.</summary>
        public enum EqualityReferenceResult
        {
            /// <summary>
            /// Equality could not be determined simply by examining references, a full property-value
            /// check must be performed to determine equality.
            /// </summary>
            Unknown = -1,

            /// <summary>
            /// The values are definitely not equal (e.g. different types), no property value check is
            /// required.
            /// </summary>
            NotEqual = 0,

            /// <summary>
            /// The values are definitely equal (e.g. the same object), no property value check is
            /// required.
            /// </summary>
            Equal = 1,
        }

        /// <summary>Performs a fast equality check using only the object references.</summary>
        /// <param name="x">The left value to be compared.</param>
        /// <param name="y">The right value to be compared.</param>
        /// <returns>A result indicating whether equality could be determined or not.</returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static EqualityReferenceResult EqualityReferenceCheck<T>(T x, T y)
        {
            // Implementation Note: this conditional is known at compile-time based on the generic
            // expansion of T and will lead to this method becoming an constant (trivially inlinable)
            // for struct values of T.
            if (typeof(T).IsValueType)
            {
                return EqualityReferenceResult.Unknown;
            }

            if (object.ReferenceEquals(x, y))
            {
                return EqualityReferenceResult.Equal;
            }
            if (x is null || y is null)
            {
                return EqualityReferenceResult.NotEqual;
            }
            if (x.GetType() != y.GetType())
            {
                return EqualityReferenceResult.NotEqual;
            }

            return EqualityReferenceResult.Unknown;
        }
    }
}
