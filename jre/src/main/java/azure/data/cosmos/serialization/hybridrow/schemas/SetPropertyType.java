//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Set properties represent an unbounded set of zero or more unique items.
 * <p>
 * Sets may be typed or untyped.  Within typed sets, all items MUST be the same type. The
 * type of items is specified via <see cref="Items" />. Typed sets may be stored more efficiently
 * than untyped sets. When <see cref="Items" /> is unspecified, the set is untyped and its items
 * may be heterogeneous.
 * <p>
 * Each item within a set must be unique. Uniqueness is defined by the HybridRow encoded sequence
 * of bytes for the item.
 */
public class SetPropertyType extends ScopePropertyType {
    /**
     * (Optional) type of the elements of the set, if a typed set, otherwise null.
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