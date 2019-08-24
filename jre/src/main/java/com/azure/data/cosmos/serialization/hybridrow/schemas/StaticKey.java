//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or set of properties whose values MUST be the same for all rows that share the same partition
 * key.
 */
public class StaticKey {
    /**
     * The logical path of the referenced property.
     * Static path MUST refer to properties defined within the same {@link Schema}.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "path", Required = Required.Always)] public string Path {get;set;}
    private String Path;

    public final String getPath() {
        return Path;
    }

    public final void setPath(String value) {
        Path = value;
    }
}