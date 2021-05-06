// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1716 // Identifiers should not match keywords

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using System.ComponentModel;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Newtonsoft.Json;

    [JsonObject]
    public sealed class Namespace
    {
        /// <summary>
        /// The standard settings used by the JSON parser for interpreting <see cref="Namespace" />
        /// documents.
        /// </summary>
        private static readonly JsonSerializerSettings NamespaceParseSettings = new JsonSerializerSettings()
        {
            CheckAdditionalContent = true,
            NullValueHandling = NullValueHandling.Ignore,
            Formatting = Formatting.Indented,
        };

        /// <summary>The set of enums for the <see cref="Namespace" />.</summary>
        private List<EnumSchema> enums;

        /// <summary>The set of schemas that make up the <see cref="Namespace" />.</summary>
        private List<Schema> schemas;

        /// <summary>Initializes a new instance of the <see cref="Namespace" /> class.</summary>
        public Namespace()
        {
            this.Version = SchemaLanguageVersion.V2;
            this.enums = new List<EnumSchema>();
            this.schemas = new List<Schema>();
        }

        /// <summary>The version of the HybridRow Schema Definition Language used to encode this namespace.</summary>
        [DefaultValue(SchemaLanguageVersion.V2)]
        [JsonProperty(PropertyName = "version", DefaultValueHandling = DefaultValueHandling.Populate)]
        public SchemaLanguageVersion Version { get; set; }

        /// <summary>The fully qualified identifier of the namespace.</summary>
        [JsonProperty(PropertyName = "name")]
        public string Name { get; set; }

        /// <summary>An (optional) comment describing the purpose of this namespace.</summary>
        /// <remarks>Comments are for documentary purpose only and do not affect the namespace at runtime.</remarks>
        [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string Comment { get; set; }

        /// <summary>An (optional) namespace to use when performing C++ codegen.</summary>
        [JsonProperty(PropertyName = "cppNamespace", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public string CppNamespace { get; set; }

        /// <summary>The set of enums defined in <see cref="Namespace" />.</summary>
        [JsonProperty(PropertyName = "enums", DefaultValueHandling = DefaultValueHandling.IgnoreAndPopulate)]
        public List<EnumSchema> Enums
        {
            get => this.enums;

            set => this.enums = value ?? new List<EnumSchema>();
        }

        /// <summary>The set of schemas that make up the <see cref="Namespace" />.</summary>
        /// <remarks>
        /// Namespaces may consist of zero or more table schemas along with zero or more UDT schemas.
        /// Table schemas can only reference UDT schemas defined in the same namespace.  UDT schemas can
        /// contain nested UDTs whose schemas are defined within the same namespace.
        /// </remarks>
        [JsonProperty(PropertyName = "schemas")]
        public List<Schema> Schemas
        {
            get => this.schemas;

            set => this.schemas = value ?? new List<Schema>();
        }

        /// <summary>Parse a JSON document and return a full namespace.</summary>
        /// <param name="json">The JSON text to parse.</param>
        /// <returns>A namespace containing a set of logical schemas.</returns>
        public static Namespace Parse(string json)
        {
            Namespace ns = JsonConvert.DeserializeObject<Namespace>(json, Namespace.NamespaceParseSettings);
            SchemaValidator.Validate(ns);
            return ns;
        }

        /// <summary>Writes a JSON document version of the namespace.</summary>
        /// <param name="n">The namespace to serialize as JSON.</param>
        /// <returns>The JSON document.</returns>
        public static string ToJson(Namespace n)
        {
            return JsonConvert.SerializeObject(n, Namespace.NamespaceParseSettings);
        }

        /// <summary>Read Namespace as a row.</summary>
        /// <param name="row">The row to read from.</param>
        /// <param name="value">If successful, the materialized value.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public static Result Read(ref RowBuffer row, out Namespace value)
        {
            Contract.Requires(row.Header.SchemaId == new SchemaId(NamespaceHybridRowSerializer.SchemaId));
            RowCursor root = RowCursor.Create(ref row);
            return default(NamespaceHybridRowSerializer).Read(ref row, ref root, true, out value);
        }

        /// <summary>Write Namespace as a row.</summary>
        /// <param name="row">The row to write into.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result Write(ref RowBuffer row)
        {
            Contract.Requires(row.Header.SchemaId == new SchemaId(NamespaceHybridRowSerializer.SchemaId));
            RowCursor root = RowCursor.Create(ref row);
            return default(NamespaceHybridRowSerializer).Write(ref row, ref root, true, new SchemaId(NamespaceHybridRowSerializer.SchemaId), this);
        }


        /// <summary>
        /// Returns the effective SDL language version.
        /// </summary>
        /// <returns>The effective SDL language version.</returns>
        internal SchemaLanguageVersion GetEffectiveSdlVersion()
        {
            return this.Version != SchemaLanguageVersion.Unspecified ? this.Version : SchemaLanguageVersion.Latest;
        }

        /// <summary>Controls how Json.NET serializes the <see cref="Enums" /> property.</summary>
        /// <remarks>
        /// This method is accessed by Json.NET through Reflection and is used to filter empty enum
        /// sets from the JSON serialization. This ensures backward compatible round-tripping with existing
        /// namespaces.
        /// </remarks>
        /// <returns>True if the property should be written.</returns>
        private bool ShouldSerializeEnums()
        {
            return this.enums.Count > 0;
        }
    }
}
