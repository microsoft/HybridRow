// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Array properties represent an unbounded set of zero or more items.
 * <p>
 * Arrays may be typed or untyped.  Within typed arrays, all items MUST be the same type. The
 * type of items is specified via {@link Items}. Typed arrays may be stored more efficiently
 * than untyped arrays. When {@link Items} is unspecified, the array is untyped and its items
 * may be heterogeneous.
 */
public class ArrayPropertyType extends ScopePropertyType {
    /**
     * (Optional) type of the elements of the array, if a typed array, otherwise null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "items")] public PropertyType Items {get;set;}
    private PropertyType Items;

    public final PropertyType getItems() {
        return Items;
    }

    public final void setItems(PropertyType value) {
        Items = value;
    }
}