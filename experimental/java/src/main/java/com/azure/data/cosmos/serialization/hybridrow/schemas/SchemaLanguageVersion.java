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
    V1((byte) 0, "v1");

    public static final int BYTES = Byte.BYTES;

    private static HashMap<Byte, SchemaLanguageVersion> mappings;
    private String friendlyName;
    private byte value;

    SchemaLanguageVersion(byte value, String text) {
        this.value = value;
        this.friendlyName = text;
        mappings().put(value, this);
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     * @see #toString()
     */
    public String friendlyName() {
        return this.friendlyName;
    }

    public static SchemaLanguageVersion from(byte value) {
        return mappings().get(value);
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     * @see #friendlyName()
     */
    @Override
    public String toString() {
        return this.friendlyName;
    }

    public byte value() {
        return this.value;
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