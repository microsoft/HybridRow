// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    public abstract class ScopePropertyType : PropertyType
    {
        protected ScopePropertyType(TypeKind type) : base(type)
        {
        }

        /// <summary>True if the property's child elements cannot be mutated in place.</summary>
        /// <remarks>Immutable properties can still be replaced in their entirety.</remarks>
        [JsonProperty(PropertyName = "immutable")]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool Immutable { get; set; }
    }
}
