// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;

    public ref struct Utf8CodePointEnumerator
    {
        private readonly ReadOnlySpan<byte> utf8Bytes;
        private int index;
        private uint codePoint;
        private bool hasValue;

        public Utf8CodePointEnumerator(ReadOnlySpan<byte> utf8Bytes)
        {
            this.utf8Bytes = utf8Bytes;
            this.index = 0;
            this.codePoint = 0;
            this.hasValue = false;
        }

        public uint Current
        {
            get
            {
                Contract.Requires(this.hasValue);
                return this.codePoint;
            }
        }

        public bool MoveNext()
        {
            if (this.index >= this.utf8Bytes.Length)
            {
                this.hasValue = false;
            }
            else
            {
                this.hasValue = Utf8Helper.TryDecodeCodePoint(this.utf8Bytes, this.index, out this.codePoint, out int bytesConsumed);
                Contract.Invariant(this.hasValue && bytesConsumed > 0, "Invalid code point!");
                this.index += bytesConsumed;
            }

            return this.hasValue;
        }
    }
}
