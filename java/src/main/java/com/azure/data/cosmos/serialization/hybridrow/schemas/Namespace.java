// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.core.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Namespace {

    private String name;
    private SchemaLanguageVersion version = SchemaLanguageVersion.values()[0];
    private ArrayList<Schema> schemas;

    /**
     * Initializes a new instance of the {@link Namespace} class.
     */
    public Namespace() {
        this.setSchemas(new ArrayList<Schema>());
    }

    /**
     * The fully qualified identifier of the namespace.
     */
    public final String name() {
        return this.name;
    }

    public final Namespace name(String value) {
        this.name = value;
        return this;
    }

    /**
     * The set of schemas that make up the {@link Namespace}.
     * <p>
     * Namespaces may consist of zero or more table schemas along with zero or more UDT schemas.
     * Table schemas can only reference UDT schemas defined in the same namespace.  UDT schemas can
     * contain nested UDTs whose schemas are defined within the same namespace.
     * @return
     */
    public final List<Schema> schemas() {
        return this.schemas;
    }

    public final void setSchemas(ArrayList<Schema> value) {
        this.schemas = value != null ? value : new ArrayList<Schema>();
    }

    /**
     * The version of the HybridRow Schema Definition Language used to encode this namespace.
     */
    public final SchemaLanguageVersion getVersion() {
        return this.version;
    }

    public final void setVersion(SchemaLanguageVersion value) {
        this.version = value;
    }

    /**
     * Parse a JSON document and return a full namespace.
     *
     * @param value The JSON text to parse
     * @return A namespace containing a set of logical schemas.
     */
    public static Optional<Namespace> parse(String value) {
        Optional<Namespace> ns = Json.<Namespace>parse(value);
        ns.ifPresent(SchemaValidator::Validate);
        return ns;
    }
}
