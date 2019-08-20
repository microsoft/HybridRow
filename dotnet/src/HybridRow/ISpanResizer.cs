// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;

    public interface ISpanResizer<T>
    {
        /// <summary>Resizes an existing a buffer.</summary>
        /// <typeparam name="T">The type of the elements of the memory.</typeparam>
        /// <param name="minimumLength">The minimum required length (in elements) of the memory.</param>
        /// <param name="buffer">
        /// Optional existing memory to be copied to the new buffer.  Ownership of <paramref name="buffer" /> is
        /// transferred as part of this call and it should not be used by the caller after this call completes.
        /// </param>
        /// <returns>
        /// A new memory whose size is <em>at least as big</em> as <paramref name="minimumLength" />
        /// and containing the content of <paramref name="buffer" />.
        /// </returns>
        Span<T> Resize(int minimumLength, Span<T> buffer = default);
    }
}
