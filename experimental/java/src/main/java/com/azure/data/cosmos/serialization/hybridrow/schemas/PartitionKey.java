// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or set of properties used to partition the data set across machines.
 */
public class PartitionKey {
    /**
     * The logical path of the referenced property.
     * Partition keys MUST refer to properties defined within the same {@link Schema}.
     */
    private String path;

    public final String path() {
        return this.path;
    }

    public final PartitionKey path(String value) {
        this.path = value;
        return this;
    }
}