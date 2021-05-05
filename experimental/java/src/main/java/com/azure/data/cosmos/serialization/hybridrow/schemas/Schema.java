// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCompiler;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A schema describes either table or UDT metadata.
 * <p>
 * The schema of a table or UDT describes the structure of row (i.e. which columns and the types of those columns). A
 * table schema represents the description of the contents of a collection level row directly.  UDTs described nested
 * structured objects that may appear either within a table column or within another UDT (i.e. nested UDTs).
 */
public class Schema {

    // Required fields

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(defaultValue = "schema", required = true)
    private TypeKind type;

    // Optional fields

    @JsonProperty
    private String comment;

    @JsonProperty()
    private SchemaId id;

    @JsonProperty
    private SchemaOptions options;

    @JsonProperty
    private List<Property> properties;

    @JsonProperty
    private SchemaLanguageVersion version;

    // TODO: DANOBLE: how do these properties serialize?

    private List<PartitionKey> partitionKeys;
    private List<PrimarySortKey> primaryKeys;
    private List<StaticKey> staticKeys;

    /**
     * Initializes a new instance of the {@link Schema} class.
     */
    private Schema() {
        this.id = SchemaId.NONE;
        this.type = TypeKind.SCHEMA;
        this.partitionKeys = Collections.emptyList();
        this.primaryKeys = Collections.emptyList();
        this.staticKeys = Collections.emptyList();
    }

    /**
     * An (optional) comment describing the purpose of this schema.
     * <p>
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     *
     * @return the comment on this {@linkplain Schema schema} or {@code null}, if there is no comment.
     */
    public final String comment() {
        return this.comment;
    }

    /**
     * Sets the (optional) comment describing the purpose of this schema.
     * <p>
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     *
     * @param value a comment on this {@linkplain Schema schema} or {@code null} to remove the comment, if any, on this
     *              {@linkplain Schema schema}.
     * @return a reference to this {@linkplain Schema schema}.
     */
    public final Schema comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * Compiles this logical schema into a physical layout that can be used to read and write rows.
     *
     * @param namespace The namespace within which this schema is defined.
     * @return The layout for the schema.
     */
    public final Layout compile(Namespace namespace) {

        checkNotNull(namespace, "expected non-null ns");
        checkArgument(namespace.schemas().contains(this));

        return LayoutCompiler.compile(namespace, this);
    }

    /**
     * The name of this {@linkplain Schema schema}.
     * <p>
     * The name of a schema MUST be unique within its namespace. Names must begin with an alpha-numeric character and
     * can only contain alpha-numeric characters and underscores.
     *
     * @return the name of this {@linkplain Schema schema} or {@code null}, if the name has not yet been set.
     */
    public final String name() {
        return this.name;
    }

    /**
     * Sets the name of this {@linkplain Schema schema}.
     * <p>
     * The name of a schema MUST be unique within its namespace. Names must begin with an alpha-numeric character and
     * can only contain alpha-numeric characters and underscores.
     *
     * @param value a name for this {@linkplain Schema schema}.
     * @return a reference to this {@linkplain Schema schema}.
     */
    @Nonnull
    public final Schema name(@Nonnull String value) {
        checkNotNull(value);
        this.name = value;
        return this;
    }

    /**
     * Schema-wide options.
     *
     * @return schema-wide options.
     */
    public final SchemaOptions options() {
        return this.options;
    }

    public final Schema options(SchemaOptions value) {
        this.options = value;
        return this;
    }

    /**
     * Parse a JSON fragment and return a schema.
     *
     * @param value The JSON string value to parse
     * @return A logical schema, if the value parses.
     */
    public static Optional<Schema> parse(String value) {
        return Json.parse(value, Schema.class);
        // TODO: DANOBLE: perform structural validation on the Schema after JSON parsing
    }

    /**
     * An (optional) list of zero or more logical paths that form the partition key.
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    @Nullable
    public final List<PartitionKey> partitionKeys() {
        return this.partitionKeys;
    }

    public final Schema partitionKeys(@Nullable List<PartitionKey> value) {
        this.partitionKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * An (optional) list of zero or more logical paths that form the primary sort key.
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    @Nullable
    public final List<PrimarySortKey> primarySortKeys() {
        return this.primaryKeys;
    }

    public final Schema primarySortKeys(ArrayList<PrimarySortKey> value) {
        this.primaryKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema.
     * <p>
     * This field is never null.
     *
     * @return list of zero or more property definitions that define the columns within the schema
     */
    @Nonnull
    public final List<Property> properties() {
        return this.properties;
    }

    public final Schema properties(List<Property> value) {
        this.properties = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * The unique identifier for a schema.
     * <p>
     * Identifiers must be unique within the scope of the database in which they are used.
     *
     * @return the unique identifier for a schema.
     */
    public final SchemaId schemaId() {
        return this.id;
    }

    public final Schema schemaId(SchemaId value) {
        this.id = value;
        return this;
    }

    /**
     * A list of zero or more logical paths that hold data shared by all documents with same partition key.
     * <p>
     * All paths referenced MUST map to a property within the schema.
     * <p>
     * This field is never null.
     *
     * @return A list of zero or more logical paths that hold data shared by all documents with same partition key.
     */
    @Nonnull
    public final List<StaticKey> staticKeys() {
        return this.staticKeys;
    }

    public final Schema staticKeys(List<StaticKey> value) {
        this.staticKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * Returns a JSON string representation of the current {@link Schema}.
     *
     * @return a JSON string representation of the current {@link Schema}
     */
    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * The type of this schema.
     * <p>
     * This value MUST be {@link TypeKind#SCHEMA}.
     *
     * @return the type of this schema.
     */
    public final TypeKind type() {
        return this.type;
    }

    public final Schema type(TypeKind value) {
        this.type = value;
        return this;
    }

    /**
     * The version of the HybridRow Schema Definition Language used to encode this schema.
     *
     * @return the version of the HybridRow Schema Definition Language used to encode this schema.
     */
    public final SchemaLanguageVersion version() {
        return this.version;
    }

    public final Schema version(SchemaLanguageVersion value) {
        this.version = value;
        return this;
    }
}