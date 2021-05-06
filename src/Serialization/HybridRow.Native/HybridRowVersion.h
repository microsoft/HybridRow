// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
    /// <summary>Versions of HybridRow.</summary>
    /// <remarks>A version from this list MUST be inserted in the version BOM at the beginning of all rows.</remarks>
    enum class HybridRowVersion : unsigned char
    {
        Invalid = 0,

        /// <summary>Initial version of the HybridRow format.</summary>
        V1 = 0x81,
    };
}
