// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes the header that precedes all valid Hybrid Rows.
 */
public final class HybridRowHeader {
    /**
     * Size (in bytes) of a serialized header.
     */
    public static final int BYTES = HybridRowVersion.BYTES + SchemaId.BYTES;

    private final SchemaId schemaId;
    private final HybridRowVersion version;

    /**
     * Initializes a new instance of a {@link HybridRowHeader}.
     *
     * @param version  The version of the HybridRow library used to write this row.
     * @param schemaId The unique identifier of the schema whose layout was used to write this row.
     */
    public HybridRowHeader(@Nonnull final HybridRowVersion version, @Nonnull SchemaId schemaId) {

        checkNotNull(version, "expected non-null version");
        checkNotNull(schemaId, "expected non-null schemaId");

        this.version = version;
        this.schemaId = schemaId;
    }

    /**
     * The unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     *
     * @return unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     */
    @Nonnull
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * The version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     *
     * @return  version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     */
    @Nonnull
    public HybridRowVersion version() {
        return this.version;
    }
}