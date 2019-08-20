// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Versions of the HybridRow Schema Description Language.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    public enum SchemaLanguageVersion : byte
    {
        /// <summary>Initial version of the HybridRow Schema Description Lanauge.</summary>
        V1 = 0,
    }
}
