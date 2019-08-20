// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>
    /// Map properties represent an unbounded set of zero or more key-value pairs with unique
    /// keys.
    /// </summary>
    /// <remarks>
    /// Maps are typed or untyped.  Within typed maps, all key MUST be the same type, and all
    /// values MUST be the same type.  The type of both key and values is specified via <see cref="Keys" />
    /// and <see cref="Values" /> respectively. Typed maps may be stored more efficiently than untyped
    /// maps. When <see cref="Keys" /> or <see cref="Values" /> is unspecified or marked
    /// <see cref="TypeKind.Any" />, the map is untyped and its key and/or values may be heterogeneous.
    /// </remarks>
    public class MapPropertyType : ScopePropertyType
    {
        /// <summary>(Optional) type of the keys of the map, if a typed map, otherwise null.</summary>
        [JsonProperty(PropertyName = "keys")]
        public PropertyType Keys { get; set; }

        /// <summary>(Optional) type of the values of the map, if a typed map, otherwise null.</summary>
        [JsonProperty(PropertyName = "values")]
        public PropertyType Values { get; set; }
    }
}
