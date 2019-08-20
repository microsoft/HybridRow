// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    /// <summary>Versions of HybridRow.</summary>
    /// <remarks>A version from this list MUST be inserted in the version BOM at the beginning of all rows.</remarks>
    public enum HybridRowVersion : byte
    {
        Invalid = 0,

        /// <summary>Initial version of the HybridRow format.</summary>
        V1 = 0x81,
    }
}
