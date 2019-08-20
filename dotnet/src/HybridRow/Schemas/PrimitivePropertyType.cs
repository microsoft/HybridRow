// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>A primitive property.</summary>
    /// <remarks>
    /// Primitive properties map to columns one-to-one.  Primitive properties indicate how the
    /// column should be represented within the row.
    /// </remarks>
    public class PrimitivePropertyType : PropertyType
    {
        /// <summary>The maximum allowable length in bytes.</summary>
        /// <remarks>
        /// This annotation is only valid for non-fixed length types. A value of 0 means the maximum
        /// allowable length.
        /// </remarks>
        [JsonProperty(PropertyName = "length")]
        [JsonConverter(typeof(StrictIntegerConverter))]
        public int Length { get; set; }

        /// <summary>Storage requirements of the property.</summary>
        [JsonProperty(PropertyName = "storage")]
        public StorageKind Storage { get; set; }
    }
}
