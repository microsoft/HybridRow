// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceOpenHashMap;

import java.util.function.Supplier;

/**
 * Versions of HybridRow.
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

    private static final Supplier<Byte2ReferenceMap<HybridRowVersion>> mappings = Suppliers.memoize(() -> {
        final HybridRowVersion[] constants = HybridRowVersion.class.getEnumConstants();
        final byte[] values = new byte[constants.length];
        for (int i = 0; i < constants.length; i++) {
            values[i] = constants[i].value();
        }
        return new Byte2ReferenceOpenHashMap<>(values, constants);
    });

    private final byte value;

    HybridRowVersion(final byte value) {
        this.value = value;
    }

    public static HybridRowVersion from(final byte value) {
        return mappings.get().get(value);
    }

    public byte value() {
        return this.value;
    }
}