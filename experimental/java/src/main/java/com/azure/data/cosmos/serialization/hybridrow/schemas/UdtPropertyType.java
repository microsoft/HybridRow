// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UDT properties represent nested structures with an independent schema.
 * <p>
 * UDT properties include a nested row within an existing row as a column.  The schema of the nested row may be evolved
 * independently of the outer row.  Changes to the independent schema affect all outer schemas where the UDT is used.
 */
public class UdtPropertyType extends ScopePropertyType {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private SchemaId id;

    /**
     * The name of the UDT schema defining the structure of a nested row.
     * <p>
     * The UDT schema MUST be defined within the same {@link Namespace} as the schema that references it.
     *
     * @return the identifier of the UDT schema defining the structure of a nested row.
     */
    public final String name() {
        return this.name;
    }

    /**
     * Sets the name of the UDT schema defining the structure of a nested row.
     * <p>
     * The UDT schema MUST be defined within the same {@link Namespace} as the schema that references it.
     *
     * @param value the name of the UDT schema defining the structure of a nested row.
     * @return a reference to this {@link UdtPropertyType}.
     */
    public final UdtPropertyType name(String value) {
        this.name = value;
        return this;
    }

    /**
     * The unique identifier of the UDT schema defining the structure of a nested row.
     * <p>
     * Optional uniqueifier if multiple versions of {@link #name} appears within the {@link Namespace}.
     * <p>
     * If multiple versions of a UDT are defined within a {@link Namespace} the globally unique identifier of the
     * specific version referenced MUST be provided.
     *
     * @return the unique identifier of the UDT schema defining the structure of a nested row or {@code null}.
     */
    public final SchemaId schemaId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of the UDT schema defining the structure of a nested row.
     * <p>
     * Optional uniqueifier if multiple versions of {@link #name} appears within the {@link Namespace}.
     * <p>
     * If multiple versions of a UDT are defined within a {@link Namespace} the globally unique identifier of the
     * specific version referenced MUST be provided.
     *
     * @param value the unique identifier of the UDT schema defining the structure of a nested row or {@code null}.
     * @return a reference to this {@link UdtPropertyType}.
     */
    public final UdtPropertyType schemaId(SchemaId value) {
        this.id = value;
        return this;
    }
}
