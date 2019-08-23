//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * A primitive property.
 * <p>
 * Primitive properties map to columns one-to-one.  Primitive properties indicate how the
 * column should be represented within the row.
 */
public class PrimitivePropertyType extends PropertyType {
    /**
     * The maximum allowable length in bytes.
     * <p>
     * This annotation is only valid for non-fixed length types. A value of 0 means the maximum
     * allowable length.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "length")][JsonConverter(typeof(StrictIntegerConverter))] public
    // int Length {get;set;}
    private int Length;
    /**
     * Storage requirements of the property.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "storage")] public StorageKind Storage {get;set;}
    private StorageKind Storage = StorageKind.values()[0];

    public final int getLength() {
        return Length;
    }

    public final void setLength(int value) {
        Length = value;
    }

    public final StorageKind getStorage() {
        return Storage;
    }

    public final void setStorage(StorageKind value) {
        Storage = value;
    }
}