//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.ArrayList;

/**
 * Tagged properties pair one or more typed values with an API-specific uint8 type code.
 * <p>
 * The uint8 type code is implicitly in position 0 within the resulting tagged and should not
 * be specified in {@link Items}.
 */
public class TaggedPropertyType extends ScopePropertyType {
    public static final int MaxTaggedArguments = 2;
    public static final int MinTaggedArguments = 1;
    /**
     * Types of the elements of the tagged in element order.
     */
    private ArrayList<PropertyType> items;

    /**
     * Initializes a new instance of the {@link TaggedPropertyType} class.
     */
    public TaggedPropertyType() {
        this.items = new ArrayList<PropertyType>();
    }

    /**
     * Types of the elements of the tagged in element order.
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