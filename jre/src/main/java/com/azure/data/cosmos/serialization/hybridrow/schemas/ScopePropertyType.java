//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

public abstract class ScopePropertyType extends PropertyType {
    /**
     * True if the property's child elements cannot be mutated in place.
     * Immutable properties can still be replaced in their entirety.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "immutable")][JsonConverter(typeof(StrictBooleanConverter))] public
    // bool Immutable {get;set;}
    private boolean Immutable;

    public final boolean getImmutable() {
        return Immutable;
    }

    public final void setImmutable(boolean value) {
        Immutable = value;
    }
}