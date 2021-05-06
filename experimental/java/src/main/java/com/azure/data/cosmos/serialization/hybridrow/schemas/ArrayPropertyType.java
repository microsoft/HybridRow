// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Array properties represent an unbounded set of zero or more items.
 * <p>
 * Arrays may be typed or untyped.  Within typed arrays, all items MUST be the same type. The type of items is specified
 * via {@link #items()}. Typed arrays may be stored more efficiently than untyped arrays. When {@link #items()} is
 * unspecified, the array is untyped and its items may be heterogeneous.
 */
public class ArrayPropertyType extends ScopePropertyType {

    @JsonProperty
    private PropertyType items;

    /**
     * (Optional) type of the elements of the array, if a typed array, otherwise null.
     *
     * @return type of the elements of the array or {@code null}, if the array is untyped.
     */
    public final PropertyType items() {
        return this.items;
    }

    /**
     * Set the type of the elements of the array
     *
     * @param value type of the elements of the array or {@code null}, if the array is untyped.
     * @return a reference to this {@link ArrayPropertyType}.
     */
    public final ArrayPropertyType items(PropertyType value) {
        this.items = value;
        return this;
    }
}
