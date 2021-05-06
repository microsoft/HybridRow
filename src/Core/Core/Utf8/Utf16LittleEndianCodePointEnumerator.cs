// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;

    internal struct Utf16LittleEndianCodePointEnumerator
    {
        private readonly string str;
        private int index;
        private uint codePoint;
        private bool hasValue;

        public Utf16LittleEndianCodePointEnumerator(string str)
        {
            Contract.Assert(BitConverter.IsLittleEndian);

            this.str = str;
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
            if (this.index >= this.str.Length)
            {
                this.hasValue = false;
            }
            else
            {
                this.hasValue = Utf8Helper.TryDecodeCodePointFromString(this.str, this.index, out this.codePoint, out int charsConsumed);
                Contract.Invariant(this.hasValue && charsConsumed > 0, "Invalid code point!");
                this.index += charsConsumed;
            }

            return this.hasValue;
        }
    }
}
