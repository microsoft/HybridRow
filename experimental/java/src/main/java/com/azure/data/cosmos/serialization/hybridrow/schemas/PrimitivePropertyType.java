// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A primitive property.
 * <p>
 * Primitive properties map to columns one-to-one. Primitive properties indicate how the column should be represented
 * within the row.
 */
public class PrimitivePropertyType extends PropertyType {

    @JsonProperty
    private int length;

    @JsonProperty
    private StorageKind storage;

    /**
     * The maximum allowable length in bytes.
     * <p>
     * This annotation is only valid for non-fixed length types. A value of 0 means the maximum allowable length.
     *
     * @return the maximum allowable length in bytes.
     */
    public final int length() {
        return this.length;
    }

    public final PrimitivePropertyType length(int value) {
        this.length = value;
        return this;
    }

    /**
     * Storage requirements of the property.
     *
     * @return storage requirements of the property.
     */
    public final StorageKind storage() {
        return this.storage;
    }

    public final PrimitivePropertyType storage(StorageKind value) {
        this.storage = value;
        return this;
    }
}