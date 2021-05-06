// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    /// <summary>Tagged properties pair one or more typed values with an API-specific uint8 type code.</summary>
    /// <remarks>
    /// The uint8 type code is implicitly in position 0 within the resulting tagged and should not
    /// be specified in <see cref="Items" />.
    /// </remarks>
    public sealed class TaggedPropertyType : ScopePropertyType
    {
        internal const int MinTaggedArguments = 1;
        internal const int MaxTaggedArguments = 2;

        /// <summary>Types of the elements of the tagged in element order.</summary>
        private List<PropertyType> items;

        /// <summary>Initializes a new instance of the <see cref="TaggedPropertyType" /> class.</summary>
        public TaggedPropertyType()
            : base(TypeKind.Tagged)
        {
            this.items = new List<PropertyType>();
        }

        /// <summary>Types of the elements of the tagged in element order.</summary>
        [JsonProperty(PropertyName = "items")]
        public List<PropertyType> Items
        {
            get => this.items;
            set => this.items = value ?? new List<PropertyType>();
        }
    }
}
