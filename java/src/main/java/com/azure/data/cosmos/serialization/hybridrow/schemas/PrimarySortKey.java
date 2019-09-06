// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or set of properties used to order the data set within a single
 * partition.
 */
public class PrimarySortKey {

    private SortDirection direction = SortDirection.values()[0];
    private String path;

    /**
     * The logical path of the referenced property.
     * Primary keys MUST refer to properties defined within the same {@link Schema}.
     */
    public final SortDirection direction() {
        return this.direction;
    }

    public final PrimarySortKey direction(SortDirection value) {
        this.direction = value;
        return this;
    }

    /**
     * The logical path of the referenced property.
     * Primary keys MUST refer to properties defined within the same {@link Schema}.
     */
    public final String path() {
        return this.path;
    }

    public final PrimarySortKey path(String value) {
        this.path = value;
        return this;
    }
}