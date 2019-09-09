// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCompiler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A schema describes either table or UDT metadata
 * <p>
 * The schema of a table or UDT describes the structure of row (i.e. which columns and the types of those columns). A
 * table schema represents the description of the contents of a collection level row directly.  UDTs described nested
 * structured objects that may appear either within a table column or within another UDT (i.e. nested UDTs).
 */
public class Schema {

    private String comment;
    private String name;
    private SchemaOptions options;
    private List<PartitionKey> partitionKeys;
    private List<PrimarySortKey> primaryKeys;
    private List<Property> properties;
    private SchemaId schemaId = SchemaId.NONE;
    private List<StaticKey> staticKeys;
    private TypeKind type = TypeKind.values()[0];
    private SchemaLanguageVersion version = SchemaLanguageVersion.values()[0];

    /**
     * Initializes a new instance of the {@link Schema} class.
     */
    public Schema() {
        this.type(TypeKind.Schema);
        this.properties = Collections.emptyList();
        this.partitionKeys = Collections.emptyList();
        this.primaryKeys = Collections.emptyList();
        this.staticKeys = Collections.emptyList();
    }

    /**
     * An (optional) comment describing the purpose of this schema.
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     */
    public final String comment() {
        return this.comment;
    }

    public final Schema comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * Compiles this logical schema into a physical layout that can be used to read and write rows
     *
     * @param ns The namespace within which this schema is defined.
     * @return The layout for the schema.
     */
    public final Layout compile(Namespace ns) {

        checkNotNull(ns, "expected non-null ns");
        checkArgument(ns.schemas().contains(this));

        return LayoutCompiler.compile(ns, this);
    }

    /**
     * The name of the schema.
     * <p>
     * The name of a schema MUST be unique within its namespace.
     * <para />
     * Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
     * underscores.
     */
    public final String name() {
        return this.name;
    }

    public final Schema name(String value) {
        this.name = value;
        return this;
    }

    /**
     * Schema-wide operations.
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
        return Json.<Schema>parse(value); // TODO: DANOBLE: perform structural validation on the Schema after JSON
        // parsing
    }

    /**
     * An (optional) list of zero or more logical paths that form the partition key
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    @Nonnull
    public final List<PartitionKey> partitionKeys() {
        return this.partitionKeys;
    }

    public final Schema partitionKeys(@Nullable List<PartitionKey> value) {
        this.partitionKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * An (optional) list of zero or more logical paths that form the primary sort key
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    public final List<PrimarySortKey> primarySortKeys() {
        return this.primaryKeys;
    }

    public final Schema primarySortKeys(ArrayList<PrimarySortKey> value) {
        this.primaryKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema
     * <p>
     * This field is never null.
     *
     * @return list of zero or more property definitions that define the columns within the schema
     */
    public final List<Property> properties() {
        return this.properties;
    }

    public final Schema properties(List<Property> value) {
        this.properties = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * The unique identifier for a schema
     * <p>
     * Identifiers must be unique within the scope of the database in which they are used.
     */
    public final SchemaId schemaId() {
        return this.schemaId;
    }

    public final Schema schemaId(SchemaId value) {
        this.schemaId = value;
        return this;
    }

    /**
     * An (optional) list of zero or more logical paths that hold data shared by all documents with same partition key.
     * All paths referenced MUST map to a property within the schema.
     * <para />
     * This field is never null.
     *
     * @return
     */
    public final List<StaticKey> staticKeys() {
        return this.staticKeys;
    }

    public final Schema staticKeys(List<StaticKey> value) {
        this.staticKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * Returns a JSON string representation of the current {@link Schema}
     *
     * @return a JSON string representation of the current {@link Schema}
     */
    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * The type of this schema
     * <p>
     * This value MUST be {@link TypeKind#Schema}.
     */
    public final TypeKind type() {
        return this.type;
    }

    public final Schema type(TypeKind value) {
        this.type = value;
        return this;
    }

    /**
     * The version of the HybridRow Schema Definition Language used to encode this schema
     */
    public final SchemaLanguageVersion version() {
        return this.version;
    }

    public final Schema version(SchemaLanguageVersion value) {
        this.version = value;
        return this;
    }
}