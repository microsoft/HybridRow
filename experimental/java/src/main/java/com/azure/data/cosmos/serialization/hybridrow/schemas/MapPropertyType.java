// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Map properties represent an unbounded set of zero or more key-value pairs with unique keys.
 * <p>
 * Maps are typed or untyped.  Within typed maps, all key MUST be the same type, and all values MUST be the same type.
 * The type of both key and values is specified via {@link #keys()} and {@link #values()} respectively. Typed maps may
 * be stored more efficiently than untyped maps. When {@link #keys()} or {@link #values()} is unspecified or marked
 * {@link TypeKind#ANY}, the map is untyped and its key and/or values may be heterogeneous.
 */
public class MapPropertyType extends ScopePropertyType {

    private PropertyType keys;
    private PropertyType values;

    /**
     * (Optional) type of the keys of the map, if a typed map, otherwise {@code null}.
     *
     * @return type of the keys of the map, if a type map, otherwise {@code null}.
     */
    public final PropertyType keys() {
        return this.keys;
    }

    public final MapPropertyType keys(PropertyType value) {
        this.keys = value;
        return this;
    }

    /**
     * (Optional) type of the values of the map, if a typed map, otherwise {@code null}.
     *
     * @return type of the values of the map, if a typed map, otherwise {@code null}.
     */
    public final PropertyType values() {
        return this.values;
    }

    public final MapPropertyType values(PropertyType value) {
        this.values = value;
        return this;
    }
}