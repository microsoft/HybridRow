// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

/**
 * UDT properties represent nested structures with an independent schema.
 * <p>
 * UDT properties include a nested row within an existing row as a column.  The schema of the
 * nested row may be evolved independently of the outer row.  Changes to the independent schema affect
 * all outer schemas where the UDT is used.
 */
public class UdtPropertyType extends ScopePropertyType {

    private String name;
    private SchemaId schemaId;

    /**
     * Initializes a new {@link UdtPropertyType}.
     */
    public UdtPropertyType() {
        this.schemaId(SchemaId.INVALID);
    }

    /**
     * The identifier of the UDT schema defining the structure for the nested row.
     * <p>
     * The UDT schema MUST be defined within the same {@link Namespace} as the schema that references it.
     */
    public final String name() {
        return this.name;
    }

    public final void name(String value) {
        this.name = value;
    }

    /**
     * The unique identifier for a schema.
     * <p>
     * Optional uniquifier if multiple versions of {@link #name} appears within the Namespace.
     * <p>
     * If multiple versions of a UDT are defined within the {@link Namespace} then the globally
     * unique identifier of the specific version referenced MUST be provided.
     * </p>
     */
    public final SchemaId schemaId() {
        return this.schemaId;
    }

    public final UdtPropertyType schemaId(SchemaId value) {
        this.schemaId = value;
        return this;
    }
}