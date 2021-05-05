// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or property set whose values MUST be the same for all rows that share the same partition key.
 */
public class StaticKey {

    private String path;

    /**
     * The logical path of the referenced property.
     * <p>
     * Static path MUST refer to properties defined within the same {@link Schema}.
     *
     * @return the logical path of the referenced property.
     */
    public final String path() {
        return this.path;
    }

    public final StaticKey path(String value) {
        this.path = value;
        return this;
    }
}