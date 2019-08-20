// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1716 // Identifiers should not match keywords

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using Newtonsoft.Json;

    [JsonObject]
    public class Namespace
    {
        /// <summary>
        /// The standard settings used by the JSON parser for interpreting <see cref="Namespace" />
        /// documents.
        /// </summary>
        private static readonly JsonSerializerSettings NamespaceParseSettings = new JsonSerializerSettings()
        {
            CheckAdditionalContent = true,
        };

        /// <summary>The set of schemas that make up the <see cref="Namespace" />.</summary>
        private List<Schema> schemas;

        /// <summary>Initializes a new instance of the <see cref="Namespace" /> class.</summary>
        public Namespace()
        {
            this.Schemas = new List<Schema>();
        }

        /// <summary>The version of the HybridRow Schema Definition Language used to encode this namespace.</summary>
        [JsonProperty(PropertyName = "version")]
        public SchemaLanguageVersion Version { get; set; }

        /// <summary>The fully qualified identifier of the namespace.</summary>
        [JsonProperty(PropertyName = "name")]
        public string Name { get; set; }

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
    }
}
