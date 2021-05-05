// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuple properties represent a typed, finite, ordered set of two or more items.
 */
public class TuplePropertyType extends ScopePropertyType {
    /**
     * Types of the elements of the tuple in element order.
     */
    private List<PropertyType> items;

    /**
     * Initializes a new instance of the {@link TuplePropertyType} class.
     */
    public TuplePropertyType() {
        this.items = new ArrayList<PropertyType>();
    }

    /**
     * Types of the elements of the tuple in element order.
     *
     * @return types of the elements of the tuple in element order.
     */
    public final List<PropertyType> items() {
        return this.items;
    }

    public final void items(List<PropertyType> value) {
        this.items = value != null ? value : new ArrayList<PropertyType>();
    }
}