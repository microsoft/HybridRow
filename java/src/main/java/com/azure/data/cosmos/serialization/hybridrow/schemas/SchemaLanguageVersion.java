// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.HashMap;

/**
 * Versions of the HybridRow Schema Description Language.
 */
public enum SchemaLanguageVersion {
    /**
     * Initial version of the HybridRow Schema Description Lanauge.
     */
    V1((byte)0);

    public static final int BYTES = Byte.BYTES;

    private static HashMap<Byte, SchemaLanguageVersion> mappings;
    private byte value;

    SchemaLanguageVersion(byte value) {
        this.value = value;
        mappings().put(value, this);
    }

    public byte getValue() {
        return this.value;
    }

    public static SchemaLanguageVersion forValue(byte value) {
        return mappings().get(value);
    }

    private static HashMap<Byte, SchemaLanguageVersion> mappings() {
        if (mappings == null) {
            synchronized (SchemaLanguageVersion.class) {
                if (mappings == null) {
                    mappings = new HashMap<>();
                }
            }
        }
        return mappings;
    }
}