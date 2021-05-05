// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Set properties represent an unbounded set of zero or more unique items.
 * <p>
 * Sets may be typed or untyped.  Within typed sets, all items MUST be the same type. The
 * type of items is specified via {@link #items}. Typed sets may be stored more efficiently
 * than untyped sets. When {@link #items} is unspecified, the set is untyped and its items
 * may be heterogeneous.
 * <p>
 * Each item within a set must be unique. Uniqueness is defined by the HybridRow encoded sequence
 * of bytes for the item.
 */
public class SetPropertyType extends ScopePropertyType {
    /**
     * (Optional) type of the elements of the set, if a typed set, otherwise null.
     */
    private PropertyType items;

    public final PropertyType items() {
        return this.items;
    }

    public final SetPropertyType items(PropertyType value) {
        this.items = value;
        return this;
    }
}