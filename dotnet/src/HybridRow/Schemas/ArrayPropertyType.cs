// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>Array properties represent an unbounded set of zero or more items.</summary>
    /// <remarks>
    /// Arrays may be typed or untyped.  Within typed arrays, all items MUST be the same type. The
    /// type of items is specified via <see cref="Items" />. Typed arrays may be stored more efficiently
    /// than untyped arrays. When <see cref="Items" /> is unspecified, the array is untyped and its items
    /// may be heterogeneous.
    /// </remarks>
    public class ArrayPropertyType : ScopePropertyType
    {
        /// <summary>(Optional) type of the elements of the array, if a typed array, otherwise null.</summary>
        [JsonProperty(PropertyName = "items")]
        public PropertyType Items { get; set; }
    }
}
