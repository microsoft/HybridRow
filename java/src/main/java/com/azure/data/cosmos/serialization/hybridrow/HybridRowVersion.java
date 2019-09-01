// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import java.util.HashMap;

/**
 * Versions of HybridRow
 * <p>
 * A version from this list MUST be inserted in the version BOM at the beginning of all rows.
 */
public enum HybridRowVersion {

    Invalid((byte)0),

    /**
     * Initial version of the HybridRow format.
     */
    V1((byte)0x81);

    public static final int SIZE = java.lang.Byte.SIZE;
    private static java.util.HashMap<Byte, HybridRowVersion> mappings;
    private byte value;

    HybridRowVersion(byte value) {
        this.value = value;
        getMappings().put(value, this);
    }

    public byte value() {
        return this.value;
    }

    public static HybridRowVersion from(byte value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Byte, HybridRowVersion> getMappings() {
        if (mappings == null) {
            synchronized (HybridRowVersion.class) {
                if (mappings == null) {
                    mappings = new HashMap<>();
                }
            }
        }
        return mappings;
    }
}