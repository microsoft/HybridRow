//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.ArrayList;

/**
 * Tuple properties represent a typed, finite, ordered set of two or more items.
 */
public class TuplePropertyType extends ScopePropertyType {
    /**
     * Types of the elements of the tuple in element order.
     */
    private ArrayList<PropertyType> items;

    /**
     * Initializes a new instance of the {@link TuplePropertyType} class.
     */
    public TuplePropertyType() {
        this.items = new ArrayList<PropertyType>();
    }

    /**
     * Types of the elements of the tuple in element order.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "items")] public List<PropertyType> Items
    public final ArrayList<PropertyType> getItems() {
        return this.items;
    }

    public final void setItems(ArrayList<PropertyType> value) {
        this.items = value != null ? value : new ArrayList<PropertyType>();
    }
}