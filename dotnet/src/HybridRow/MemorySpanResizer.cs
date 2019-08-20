// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using Microsoft.Azure.Cosmos.Core;

    public sealed class MemorySpanResizer<T> : ISpanResizer<T>
    {
        private Memory<T> memory;

        public MemorySpanResizer(int initialCapacity = 0)
        {
            Contract.Requires(initialCapacity >= 0);

            this.memory = initialCapacity == 0 ? default : new Memory<T>(new T[initialCapacity]);
        }

        public Memory<T> Memory => this.memory;

        /// <inheritdoc />
        public Span<T> Resize(int minimumLength, Span<T> buffer = default)
        {
            if (this.memory.Length < minimumLength)
            {
                this.memory = new Memory<T>(new T[Math.Max(minimumLength, buffer.Length)]);
            }

            Span<T> next = this.memory.Span;
            if (!buffer.IsEmpty && next.Slice(0, buffer.Length) != buffer)
            {
                buffer.CopyTo(next);
            }

            return next;
        }
    }
}
