// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.core.Json;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Namespace {

    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private ArrayList<Schema> schemas;

    @JsonProperty(required = true)
    private SchemaLanguageVersion version;

    /**
     * The fully qualified name of the namespace.
     *
     * @return fully qualified name of the {@linkplain Namespace namespace}.
     */
    public final String name() {
        return this.name;
    }

    /**
     * Sets the fully qualified name of the namespace.
     *
     * @param value fully qualified name of the {@linkplain Namespace namespace}.
     * @return a reference to this {@linkplain Namespace namespace}.
     */
    public final Namespace name(String value) {
        this.name = value;
        return this;
    }

    /**
     * Parse a JSON document and return a full namespace.
     *
     * @param file The JSON file to parse.
     * @return A namespace containing a set of logical schemas.
     */
    public static Optional<Namespace> parse(File file) {
        Optional<Namespace> namespace = Json.parse(file, Namespace.class);
        namespace.ifPresent(SchemaValidator::validate);
        return namespace;
    }

    /**
     * Parse a JSON document and return a full namespace.
     *
     * @param stream The JSON input stream to parse.
     * @return A namespace containing a set of logical schemas.
     */
    public static Optional<Namespace> parse(InputStream stream) {
        Optional<Namespace> namespace = Json.parse(stream, Namespace.class);
        try {
            namespace.ifPresent(SchemaValidator::validate);
        } catch (SchemaException error) {
            logger.error("failed to parse {} due to ", Namespace.class, error);
        }
        return namespace;
    }

    /**
     * Parse a JSON document and return a full namespace.
     *
     * @param value The JSON text to parse.
     * @return A namespace containing a set of logical schemas.
     */
    public static Optional<Namespace> parse(String value) {
        Optional<Namespace> namespace = Json.parse(value, Namespace.class);
        namespace.ifPresent(SchemaValidator::validate);
        return namespace;
    }

    /**
     * The set of schemas that make up the {@link Namespace}.
     * <p>
     * Namespaces may consist of zero or more table schemas along with zero or more UDT schemas. Table schemas can only
     * reference UDT schemas defined in the same namespace. UDT schemas can contain nested UDTs whose schemas are
     * defined within the same namespace.
     *
     * @return list of schemas in the current {@link Namespace}.
     */
    public final List<Schema> schemas() {
        return this.schemas;
    }

    public final Namespace schemas(ArrayList<Schema> value) {
        this.schemas = value != null ? value : new ArrayList<Schema>();
        return this;
    }

    /**
     * The version of the HybridRow Schema Definition Language used to encode this namespace.
     *
     * @return {linkplain SchemaLanguageVersion version} of the HybridRow Schema Definition Language used to encode this
     * {@linkplain Namespace namespace}.
     */
    public final SchemaLanguageVersion version() {
        return this.version;
    }

    /**
     * Sets the version of the HybridRow Schema Definition Language used to encode this namespace.
     *
     * @param value {linkplain SchemaLanguageVersion version} of the HybridRow Schema Definition Language that will be
     *              used to encode this {@linkplain Namespace namespace}.
     * @return a reference to this {@linkplain Namespace namespace}.
     */
    public final Namespace version(@Nonnull SchemaLanguageVersion value) {
        Preconditions.checkNotNull(value, "expected non-null value");
        this.version = value;
        return this;
    }
}
