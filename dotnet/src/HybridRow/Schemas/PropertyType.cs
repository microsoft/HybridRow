// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.ComponentModel;
    using Newtonsoft.Json;

    /// <summary>The base class for property types both primitive and complex.</summary>
    [JsonConverter(typeof(PropertySchemaConverter))]
    public abstract class PropertyType
    {
        protected PropertyType()
        {
            this.Nullable = true;
        }

        /// <summary>Api-specific type annotations for the property.</summary>
        [JsonProperty(PropertyName = "apitype")]
        public string ApiType { get; set; }

        /// <summary>The logical type of the property.</summary>
        [JsonProperty(PropertyName = "type")]
        public TypeKind Type { get; set; }

        /// <summary>True if the property can be null.</summary>
        /// <remarks>Default: true.</remarks>
        [DefaultValue(true)]
        [JsonProperty(PropertyName = "nullable", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool Nullable { get; set; }
    }
}
