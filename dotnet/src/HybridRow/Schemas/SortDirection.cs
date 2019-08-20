// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Runtime.Serialization;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>Describes the sort order direction.</summary>
    [JsonConverter(typeof(StringEnumConverter), true)] // camelCase:true
    public enum SortDirection
    {
        /// <summary>Sorts from the lowest to the highest value.</summary>
        [EnumMember(Value = "asc")]
        Ascending = 0,

        /// <summary>Sorts from the highests to the lowest value.</summary>
        [EnumMember(Value = "desc")]
        Descending,
    }
}
