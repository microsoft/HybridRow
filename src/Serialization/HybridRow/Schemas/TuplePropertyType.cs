// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    /// <summary>Tuple properties represent a typed, finite, ordered set of two or more items.</summary>
    public sealed class TuplePropertyType : ScopePropertyType
    {
        /// <summary>Types of the elements of the tuple in element order.</summary>
        private List<PropertyType> items;

        /// <summary>Initializes a new instance of the <see cref="TuplePropertyType" /> class.</summary>
        public TuplePropertyType()
            : base(TypeKind.Tuple)
        {
            this.items = new List<PropertyType>();
        }

        /// <summary>Types of the elements of the tuple in element order.</summary>
        [JsonProperty(PropertyName = "items")]
        public List<PropertyType> Items
        {
            get => this.items;
            set => this.items = value ?? new List<PropertyType>();
        }
    }
}
