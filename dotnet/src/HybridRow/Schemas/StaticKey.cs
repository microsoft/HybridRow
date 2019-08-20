// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>
    /// Describes a property or set of properties whose values MUST be the same for all rows that share the same partition key.
    /// </summary>
    public class StaticKey
    {
        /// <summary>The logical path of the referenced property.</summary>
        /// <remarks>Static path MUST refer to properties defined within the same <see cref="Schema" />.</remarks>
        [JsonProperty(PropertyName = "path", Required = Required.Always)]
        public string Path { get; set; }
    }
}
