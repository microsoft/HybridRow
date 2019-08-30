// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1028 // Enum Storage should be Int32


/**
 * Versions of the HybridRow Schema Description Language.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(StringEnumConverter), true)] public enum SchemaLanguageVersion : byte
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: [JsonConverter(typeof(StringEnumConverter), true)] public enum SchemaLanguageVersion : byte
public enum SchemaLanguageVersion {
    /**
     * Initial version of the HybridRow Schema Description Lanauge.
     */
    V1((byte)0);

    public static final int SIZE = java.lang.Byte.SIZE;
    private static java.util.HashMap<Byte, SchemaLanguageVersion> mappings;
    private byte byteValue;

    SchemaLanguageVersion(byte value) {
        byteValue = value;
        getMappings().put(value, this);
    }

    public byte getValue() {
        return byteValue;
    }

    public static SchemaLanguageVersion forValue(byte value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Byte, SchemaLanguageVersion> getMappings() {
        if (mappings == null) {
            synchronized (SchemaLanguageVersion.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Byte, SchemaLanguageVersion>();
                }
            }
        }
        return mappings;
    }
}