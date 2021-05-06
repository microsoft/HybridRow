// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>Describes a property or set of properties used to partition the data set across machines.</summary>
    public sealed class PartitionKey
    {
        /// <summary>The logical path of the referenced property.</summary>
        /// <remarks>Partition keys MUST refer to properties defined within the same <see cref="Schema" />.</remarks>
        [JsonProperty(PropertyName = "path", Required = Required.Always)]
        public string Path { get; set; }
    }
}
