// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------
namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI
{
    using System;

    // TODO: this class should go away once we move to .NET Core 2.1.
    internal static class StringExtensions
    {
        public static unsafe string AsString(this ReadOnlySpan<char> span)
        {
            fixed (char* p = span)
            {
                return new string(p, 0, span.Length);
            }
        }
    }
}
