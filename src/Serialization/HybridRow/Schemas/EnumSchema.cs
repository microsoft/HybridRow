// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using System.ComponentModel;
    using Newtonsoft.Json;

    /// <summary>An enum schema describes a set of constrained integer values.</summary>
    public sealed class EnumSchema
    {
        /// <summary>A list of zero or more value definitions.</summary>
        private List<EnumValue> values;

        /// <summary>Initializes a new instance of the <see cref="EnumSchema" /> class.</summary>
        public EnumSchema()
        {
            this.Type = TypeKind.Int32;
            this.values = new List<EnumValue>();
        }

        /// <summary>An (optional) comment describing the purpose of this enum.</summary>
        /// <remarks>Comments are for documentary purpose only and do not affect the enum at runtime.</remarks>
        [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Comment { get; set; }

        /// <summary>The name of the enum.</summary>
        /// <remarks>
        /// The name of a enum MUST be unique within its namespace.
        /// <para />
        /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
        /// underscores.
        /// </remarks>
        [JsonProperty(PropertyName = "name", Required = Required.Always)]
        public string Name { get; set; }

        /// <summary>Api-specific type annotations for the property.</summary>
        [JsonProperty(PropertyName = "apitype", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string ApiType { get; set; }

        /// <summary>The logical base type of the enum.</summary>
        /// <remarks>This must be a primitive.</remarks>
        [DefaultValue(TypeKind.Int32)]
        [JsonProperty(PropertyName = "type", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public TypeKind Type { get; set; }

        /// <summary>A list of zero or more value definitions.</summary>
        /// <remarks>This field is never null.</remarks>
        [JsonProperty(PropertyName = "values")]
        public List<EnumValue> Values
        {
            get => this.values;
            set => this.values = value ?? new List<EnumValue>();
        }
    }
}
