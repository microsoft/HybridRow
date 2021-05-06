// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using Newtonsoft.Json;

    /// <summary>Describes the set of options that apply to the entire schema and the way it is validated.</summary>
    public sealed class SchemaOptions
    {
        /// <summary>If true then structural schema validation is enabled.</summary>
        /// <remarks>
        /// When structural schema validation is enabled then attempting to store an unschematized
        /// path in the row, or a value whose type does not conform to the type constraints defined for that
        /// path within the schema will lead to a schema validation error. When structural schema validation is
        /// NOT enabled, then storing an unschematized path or non-confirming value will lead to a sparse
        /// column override of the path.  The value will be stored (and any existing value at that path will be
        /// overwritten).  No error will be given.
        /// </remarks>
        [JsonProperty(PropertyName = "disallowUnschematized")]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool DisallowUnschematized { get; set; }

        /// <summary>
        /// If set and has the value true, then triggers behavior in the Schema that acts based on
        /// property level timestamps. In Cassandra, this means that new columns are added for each top level
        /// property that has values of the client side timestamp. This is then used in conflict resolution to
        /// independently resolve each property based on the timestamp value of that property.
        /// </summary>
        [JsonProperty(PropertyName = "enablePropertyLevelTimestamp")]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool EnablePropertyLevelTimestamp { get; set; }

        /// <summary>
        /// If the is value true, then disables prefixing the system properties with a prefix __sys_
        /// for reserved properties owned by the store layer.
        /// </summary>
        [JsonProperty(PropertyName = "disableSystemPrefix", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool DisableSystemPrefix { get; set; }

        /// <summary>If true then instances of this schema cannot be created directly, only through subtypes.</summary>
        [JsonProperty(PropertyName = "abstract", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        [JsonConverter(typeof(StrictBooleanConverter))]
        public bool Abstract { get; set; }
    }
}
