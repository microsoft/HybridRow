// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Json
{
    public readonly struct RowReaderJsonSettings
    {
        /// <summary>If non-null then child objects are indented by one copy of this string per level.</summary>
        public readonly string IndentChars;

        /// <summary>The quote character to use.</summary>
        /// <remarks>May be <see cref="lang:\""/> or <see cref="'" />.</remarks>
        public readonly char QuoteChar;

        public RowReaderJsonSettings(string indentChars = "  ", char quoteChar = '"')
        {
            this.IndentChars = indentChars;
            this.QuoteChar = quoteChar;
        }
    }
}
