// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1028 // Enum Storage should be Int32

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Runtime.Serialization;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Describes the sort order direction.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    public enum SortDirection : byte
    {
        /// <summary>Sorts from the lowest to the highest value.</summary>
        [EnumMember(Value = "asc")]
        Ascending = 0,

        /// <summary>Sorts from the highest to the lowest value.</summary>
        [EnumMember(Value = "desc")]
        Descending,
    }
}
