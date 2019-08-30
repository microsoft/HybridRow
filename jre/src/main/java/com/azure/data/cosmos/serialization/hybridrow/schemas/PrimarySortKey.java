// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or set of properties used to order the data set within a single
 * partition.
 */
public class PrimarySortKey {
    /**
     * The logical path of the referenced property.
     * Primary keys MUST refer to properties defined within the same {@link Schema}.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "direction", Required = Required.DisallowNull)] public
    // SortDirection Direction {get;set;}
    private SortDirection Direction = SortDirection.values()[0];
    /**
     * The logical path of the referenced property.
     * Primary keys MUST refer to properties defined within the same {@link Schema}.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "path", Required = Required.Always)] public string Path {get;set;}
    private String Path;

    public final SortDirection getDirection() {
        return Direction;
    }

    public final void setDirection(SortDirection value) {
        Direction = value;
    }

    public final String getPath() {
        return Path;
    }

    public final void setPath(String value) {
        Path = value;
    }
}