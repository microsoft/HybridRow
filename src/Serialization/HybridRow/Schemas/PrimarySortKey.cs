// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>
    /// Describes a property or set of properties used to order the data set within a single
    /// partition.
    /// </summary>
    public sealed class PrimarySortKey
    {
        /// <summary>The logical path of the referenced property.</summary>
        /// <remarks>Primary keys MUST refer to properties defined within the same <see cref="Schema" />.</remarks>
        [JsonProperty(PropertyName = "path", Required = Required.Always)]
        public string Path { get; set; }

        /// <summary>The logical path of the referenced property.</summary>
        /// <remarks>Primary keys MUST refer to properties defined within the same <see cref="Schema" />.</remarks>
        [JsonProperty(PropertyName = "direction", Required = Required.DisallowNull)]
        public SortDirection Direction { get; set; }
    }
}
