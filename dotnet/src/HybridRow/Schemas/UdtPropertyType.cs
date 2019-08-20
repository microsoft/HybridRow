// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>UDT properties represent nested structures with an independent schema.</summary>
    /// <remarks>
    /// UDT properties include a nested row within an existing row as a column.  The schema of the
    /// nested row may be evolved independently of the outer row.  Changes to the independent schema affect
    /// all outer schemas where the UDT is used.
    /// </remarks>
    public class UdtPropertyType : ScopePropertyType
    {
        public UdtPropertyType()
        {
            this.SchemaId = SchemaId.Invalid;
        }

        /// <summary>The identifier of the UDT schema defining the structure for the nested row.</summary>
        /// <remarks>
        /// The UDT schema MUST be defined within the same <see cref="Namespace" /> as the schema that
        /// references it.
        /// </remarks>
        [JsonProperty(PropertyName = "name", Required = Required.Always)]
        public string Name { get; set; }

        /// <summary>The unique identifier for a schema.</summary>
        /// <remarks>
        /// Optional uniquifier if multiple versions of <see cref="Name" /> appears within the Namespace.
        /// <p>
        /// If multiple versions of a UDT are defined within the <see cref="Namespace" /> then the globally
        /// unique identifier of the specific version referenced MUST be provided.
        /// </p>
        /// </remarks>
        [JsonProperty(PropertyName = "id", Required = Required.DisallowNull, DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public SchemaId SchemaId { get; set; }
    }
}
