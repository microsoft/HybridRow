//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Map properties represent an unbounded set of zero or more key-value pairs with unique
 * keys.
 * <p>
 * <p>
 * Maps are typed or untyped.  Within typed maps, all key MUST be the same type, and all
 * values MUST be the same type.  The type of both key and values is specified via <see cref="Keys" />
 * and <see cref="Values" /> respectively. Typed maps may be stored more efficiently than untyped
 * maps. When <see cref="Keys" /> or <see cref="Values" /> is unspecified or marked
 * <see cref="TypeKind.Any" />, the map is untyped and its key and/or values may be heterogeneous.
 */
public class MapPropertyType extends ScopePropertyType {
    /**
     * (Optional) type of the keys of the map, if a typed map, otherwise null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "keys")] public PropertyType Keys {get;set;}
    private PropertyType Keys;
    /**
     * (Optional) type of the values of the map, if a typed map, otherwise null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "values")] public PropertyType Values {get;set;}
    private PropertyType Values;

    public final PropertyType getKeys() {
        return Keys;
    }

    public final void setKeys(PropertyType value) {
        Keys = value;
    }

    public final PropertyType getValues() {
        return Values;
    }

    public final void setValues(PropertyType value) {
        Values = value;
    }
}