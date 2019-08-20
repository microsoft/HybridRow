// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1716 // Identifiers should not match keywords

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>Describes a single property definition.</summary>
    public class Property
    {
        /// <summary>An (optional) comment describing the purpose of this property.</summary>
        /// <remarks>Comments are for documentary purpose only and do not affect the property at runtime.</remarks>
        [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Comment { get; set; }

        /// <summary>The logical path of this property.</summary>
        /// <remarks>
        /// For complex properties (e.g. objects) the logical path forms a prefix to relative paths of
        /// properties defined within nested structures.
        /// <para />
        /// See the logical path specification for full details on both relative and absolute paths.
        /// </remarks>
        [JsonProperty(PropertyName = "path", Required = Required.Always)]
        public string Path { get; set; }

        /// <summary>The type of the property.</summary>
        /// <remarks>
        /// Types may be simple (e.g. int8) or complex (e.g. object).  Simple types always define a
        /// single column.  Complex types may define one or more columns depending on their structure.
        /// </remarks>
        [JsonProperty(PropertyName = "type", Required = Required.Always)]
        public PropertyType PropertyType { get; set; }
    }
}
