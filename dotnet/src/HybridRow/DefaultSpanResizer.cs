// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Diagnostics.CodeAnalysis;

    public class DefaultSpanResizer<T> : ISpanResizer<T>
    {
        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly DefaultSpanResizer<T> Default = new DefaultSpanResizer<T>();

        private DefaultSpanResizer()
        {
        }

        /// <inheritdoc />
        public Span<T> Resize(int minimumLength, Span<T> buffer = default)
        {
            Span<T> next = new Memory<T>(new T[Math.Max(minimumLength, buffer.Length)]).Span;
            if (!buffer.IsEmpty && next.Slice(0, buffer.Length) != buffer)
            {
                buffer.CopyTo(next);
            }

            return next;
        }
    }
}
