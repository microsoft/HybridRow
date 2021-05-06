// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    /// <summary>Object properties represent nested structures.</summary>
    /// <remarks>
    /// Object properties map to multiple columns depending on the number of internal properties
    /// within the defined object structure.  Object properties are provided as a convince in schema
    /// design.  They are effectively equivalent to defining the same properties explicitly via
    /// <see cref="PrimitivePropertyType" /> with nested property paths.
    /// </remarks>
    public sealed class ObjectPropertyType : ScopePropertyType
    {
        /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
        private List<Property> properties;

        /// <summary>Initializes a new instance of the <see cref="ObjectPropertyType" /> class.</summary>
        public ObjectPropertyType()
            : base(TypeKind.Object)
        {
            this.properties = new List<Property>();
        }

        /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
        [JsonProperty(PropertyName = "properties")]
        public List<Property> Properties
        {
            get => this.properties;
            set => this.properties = value ?? new List<Property>();
        }
    }
}
