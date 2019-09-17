// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * Describes the header that precedes all valid Hybrid Rows.
 */
public final class HybridRowHeader {
    /**
     * Size (in bytes) of a serialized header.
     */
    public static final int BYTES = SchemaId.BYTES;

    private SchemaId schemaId;
    private HybridRowVersion version = HybridRowVersion.values()[0];

    /**
     * Initializes a new instance of a {@link HybridRowHeader}.
     *
     * @param version  The version of the HybridRow library used to write this row.
     * @param schemaId The unique identifier of the schema whose layout was used to write this row.
     */
    public HybridRowHeader(HybridRowVersion version, SchemaId schemaId) {
        this.version = version;
        this.schemaId = SchemaId.from(schemaId.value());
    }

    /**
     * The unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     *
     * @return unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * The version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     *
     * @return  version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     */
    public HybridRowVersion version() {
        return this.version;
    }
}