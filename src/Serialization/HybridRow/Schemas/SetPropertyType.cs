// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>Set properties represent an unbounded set of zero or more unique items.</summary>
    /// <remarks>
    /// Sets may be typed or untyped.  Within typed sets, all items MUST be the same type. The
    /// type of items is specified via <see cref="Items" />. Typed sets may be stored more efficiently than
    /// untyped sets. When <see cref="Items" /> is unspecified, the set is untyped and its items may be
    /// heterogeneous. Each item within a set must be unique. Uniqueness is defined by the HybridRow
    /// encoded sequence of bytes for the item.
    /// </remarks>
    public sealed class SetPropertyType : ScopePropertyType
    {
        /// <summary>Initializes a new instance of the <see cref="SetPropertyType" /> class.</summary>
        public SetPropertyType()
            : base(TypeKind.Set)
        {
        }

        /// <summary>(Optional) type of the elements of the set, if a typed set, otherwise null.</summary>
        [JsonProperty(PropertyName = "items")]
        public PropertyType Items { get; set; }
    }
}
