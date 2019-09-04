// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Versions of HybridRow
 * <p>
 * A version from this list MUST be inserted in the version BOM at the beginning of all rows.
 */
public enum HybridRowVersion {

    INVALID((byte)0),

    /**
     * Initial version of the HybridRow format.
     */
    V1((byte)0x81);

    public static final int BYTES = Byte.BYTES;

    private static Int2ObjectMap<HybridRowVersion> mappings;
    private byte value;

    HybridRowVersion(byte value) {
        this.value = value;
        mappings().put(value, this);
    }

    public static HybridRowVersion from(byte value) {
        return mappings().get(value);
    }

    public byte value() {
        return this.value;
    }

    private static Int2ObjectMap<HybridRowVersion> mappings() {
        if (mappings == null) {
            synchronized (HybridRowVersion.class) {
                if (mappings == null) {
                    mappings = new Int2ObjectOpenHashMap<>();
                }
            }
        }
        return mappings;
    }
}