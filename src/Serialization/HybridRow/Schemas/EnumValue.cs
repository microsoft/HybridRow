// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>An enum schema describes a set of constrained integer values.</summary>
    public sealed class EnumValue
    {
        /// <summary>An (optional) comment describing the purpose of this value.</summary>
        /// <remarks>Comments are for documentary purpose only and do not affect the enum at runtime.</remarks>
        [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Comment { get; set; }

        /// <summary>The name of the enum value.</summary>
        /// <remarks>
        /// The name of a value MUST be unique within its <see cref="EnumSchema"/>.
        /// <para />
        /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
        /// underscores.
        /// </remarks>
        [JsonProperty(PropertyName = "name", Required = Required.Always)]
        public string Name { get; set; }

        /// <summary>The numerical value of the enum value.</summary>
        [JsonProperty(PropertyName = "value", Required = Required.Always)]
        public long Value { get; set; }
    }
}
