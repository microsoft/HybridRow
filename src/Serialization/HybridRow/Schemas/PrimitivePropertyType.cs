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
    public sealed class PrimitivePropertyType : PropertyType
    {
        public PrimitivePropertyType()
            : base(TypeKind.Invalid)
        {
        }

        public PrimitivePropertyType(TypeKind type)
            : base(type)
        {
        }

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

        /// <summary>The identifier of the enum defining the values and base type.</summary>
        /// <remarks>
        /// This annotation is only valid for enum types.  The enum MUST be defined within the
        /// same <see cref="Namespace" /> as the schema that references it.
        /// </remarks>
        [JsonProperty(PropertyName = "enum", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Enum { get; set; }

        /// <summary>If true then during serialization this field includes the actual row buffer size.</summary>
        /// <remarks>
        /// <para>
        /// Fields with this annotation MUST be <see cref="TypeKind.Int32"/> fields with <see cref="StorageKind.Fixed"/>.
        /// Only a single field per schema have this annotation.
        /// </para>
        /// <para>
        /// During serialization this field is deferred until the very end and written last.
        /// </para>
        /// </remarks>
        [JsonProperty(PropertyName = "rowBufferSize", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool RowBufferSize { get; set; }
    }
}
