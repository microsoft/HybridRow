// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes the sort order direction.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(StringEnumConverter), true)] public enum SortDirection
public enum SortDirection {
    /**
     * Sorts from the lowest to the highest value.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "asc")] Ascending = 0,
    Ascending(0),

    /**
     * Sorts from the highests to the lowest value.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [EnumMember(Value = "desc")] Descending,
    Descending(1);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, SortDirection> mappings;
    private int intValue;

    SortDirection(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static SortDirection forValue(int value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Integer, SortDirection> getMappings() {
        if (mappings == null) {
            synchronized (SortDirection.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, SortDirection>();
                }
            }
        }
        return mappings;
    }
}