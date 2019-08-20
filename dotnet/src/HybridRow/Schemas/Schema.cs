// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using System.ComponentModel;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Newtonsoft.Json;

    /// <summary>A schema describes either table or UDT metadata.</summary>
    /// <remarks>
    /// The schema of a table or UDT describes the structure of row (i.e. which columns and the
    /// types of those columns).  A table schema represents the description of the contents of a collection
    /// level row directly.  UDTs described nested structured objects that may appear either within a table
    /// column or within another UDT (i.e. nested UDTs).
    /// </remarks>
    public class Schema
    {
        /// <summary>An (optional) list of zero or more logical paths that form the partition key.</summary>
        private List<PartitionKey> partitionKeys;

        /// <summary>An (optional) list of zero or more logical paths that form the primary sort key.</summary>
        private List<PrimarySortKey> primaryKeys;

        /// <summary>An (optional) list of zero or more logical paths that hold data shared by all documents that have the same partition key.</summary>
        private List<StaticKey> staticKeys;

        /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
        private List<Property> properties;

        /// <summary>Initializes a new instance of the <see cref="Schema" /> class.</summary>
        public Schema()
        {
            this.Type = TypeKind.Schema;
            this.properties = new List<Property>();
            this.partitionKeys = new List<PartitionKey>();
            this.primaryKeys = new List<PrimarySortKey>();
            this.staticKeys = new List<StaticKey>();
        }

        /// <summary>The version of the HybridRow Schema Definition Language used to encode this schema.</summary>
        [JsonProperty(PropertyName = "version")]
        public SchemaLanguageVersion Version { get; set; }

        /// <summary>An (optional) comment describing the purpose of this schema.</summary>
        /// <remarks>Comments are for documentary purpose only and do not affect the schema at runtime.</remarks>
        [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Comment { get; set; }

        /// <summary>The name of the schema.</summary>
        /// <remarks>
        /// The name of a schema MUST be unique within its namespace.
        /// <para />
        /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
        /// underscores.
        /// </remarks>
        [JsonProperty(PropertyName = "name", Required = Required.Always)]
        public string Name { get; set; }

        /// <summary>The unique identifier for a schema.</summary>
        /// <remarks>Identifiers must be unique within the scope of the database in which they are used.</remarks>
        [JsonProperty(PropertyName = "id", Required = Required.Always)]
        public SchemaId SchemaId { get; set; }

        /// <summary>Schema-wide operations.</summary>
        [JsonProperty(PropertyName = "options")]
        public SchemaOptions Options { get; set; }

        /// <summary>The type of this schema.  This value MUST be <see cref="TypeKind.Schema" />.</summary>
        [DefaultValue(TypeKind.Schema)]
        [JsonProperty(PropertyName = "type", Required = Required.DisallowNull, DefaultValueHandling = DefaultValueHandling.Populate)]
        public TypeKind Type { get; set; }

        /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
        /// <remarks>This field is never null.</remarks>
        [JsonProperty(PropertyName = "properties")]
        public List<Property> Properties
        {
            get
            {
                return this.properties;
            }

            set
            {
                this.properties = value ?? new List<Property>();
            }
        }

        /// <summary>An (optional) list of zero or more logical paths that form the partition key.</summary>
        /// <remarks>All paths referenced MUST map to a property within the schema.
        /// <para />
        /// This field is never null.</remarks>
        [JsonProperty(PropertyName = "partitionkeys")]
        public List<PartitionKey> PartitionKeys
        {
            get
            {
                return this.partitionKeys;
            }

            set
            {
                this.partitionKeys = value ?? new List<PartitionKey>();
            }
        }

        /// <summary>An (optional) list of zero or more logical paths that form the primary sort key.</summary>
        /// <remarks>All paths referenced MUST map to a property within the schema.
        /// <para />
        /// This field is never null.</remarks>
        [JsonProperty(PropertyName = "primarykeys")]
        public List<PrimarySortKey> PrimarySortKeys
        {
            get
            {
                return this.primaryKeys;
            }

            set
            {
                this.primaryKeys = value ?? new List<PrimarySortKey>();
            }
        }

        /// <summary>An (optional) list of zero or more logical paths that hold data shared by all documents with same partition key.</summary>
        /// <remarks>All paths referenced MUST map to a property within the schema.
        /// <para />
        /// This field is never null.</remarks>
        [JsonProperty(PropertyName = "statickeys", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public List<StaticKey> StaticKeys
        {
            get
            {
                return this.staticKeys;
            }

            set
            {
                this.staticKeys = value ?? new List<StaticKey>();
            }
        }

        /// <summary>Parse a JSON fragment and return a schema.</summary>
        /// <param name="json">The JSON text to parse.</param>
        /// <returns>A logical schema.</returns>
        public static Schema Parse(string json)
        {
            return JsonConvert.DeserializeObject<Schema>(json);

            // TODO: perform structural validation on the Schema after JSON parsing.
        }

        /// <summary>
        /// Compiles this logical schema into a physical layout that can be used to read and write
        /// rows.
        /// </summary>
        /// <param name="ns">The namespace within which this schema is defined.</param>
        /// <returns>The layout for the schema.</returns>
        public Layout Compile(Namespace ns)
        {
            Contract.Requires(ns != null);
            Contract.Requires(ns.Schemas.Contains(this));

            return LayoutCompiler.Compile(ns, this);
        }
    }
}
