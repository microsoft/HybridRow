// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import Newtonsoft.Json.*;

import java.util.ArrayList;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1716 // Identifiers should not match keywords


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonObject] public class Namespace
public class Namespace {
    /**
     * The standard settings used by the JSON parser for interpreting {@link Namespace}
     * documents.
     */
    private static final JsonSerializerSettings NamespaceParseSettings = new JsonSerializerSettings() {
        CheckAdditionalContent =true
    };
    /**
     * The fully qualified identifier of the namespace.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "name")] public string Name {get;set;}
    private String Name;
    /**
     * The version of the HybridRow Schema Definition Language used to encode this namespace.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "version")] public SchemaLanguageVersion Version {get;set;}
    private SchemaLanguageVersion Version = SchemaLanguageVersion.values()[0];
    /**
     * The set of schemas that make up the {@link Namespace}.
     */
    private ArrayList<Schema> schemas;

    /**
     * Initializes a new instance of the {@link Namespace} class.
     */
    public Namespace() {
        this.setSchemas(new ArrayList<Schema>());
    }

    public final String getName() {
        return Name;
    }

    public final void setName(String value) {
        Name = value;
    }

    /**
     * The set of schemas that make up the {@link Namespace}.
     * <p>
     * Namespaces may consist of zero or more table schemas along with zero or more UDT schemas.
     * Table schemas can only reference UDT schemas defined in the same namespace.  UDT schemas can
     * contain nested UDTs whose schemas are defined within the same namespace.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "schemas")] public List<Schema> Schemas
    public final ArrayList<Schema> getSchemas() {
        return this.schemas;
    }

    public final void setSchemas(ArrayList<Schema> value) {
        this.schemas = value != null ? value : new ArrayList<Schema>();
    }

    public final SchemaLanguageVersion getVersion() {
        return Version;
    }

    public final void setVersion(SchemaLanguageVersion value) {
        Version = value;
    }

    /**
     * Parse a JSON document and return a full namespace.
     *
     * @param json The JSON text to parse.
     * @return A namespace containing a set of logical schemas.
     */
    public static Namespace Parse(String json) {
        Namespace ns = JsonConvert.<Namespace>DeserializeObject(json, Namespace.NamespaceParseSettings);
        SchemaValidator.Validate(ns);
        return ns;
    }
}