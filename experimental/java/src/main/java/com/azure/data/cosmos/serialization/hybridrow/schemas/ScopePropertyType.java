// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

public abstract class ScopePropertyType extends PropertyType {

    private boolean immutable;

    /**
     * {@code true} if the property's child elements cannot be mutated in place.
     * <p>
     * Immutable properties can still be replaced in their entirety.
     *
     * @return {@code true} if the property's child elements cannot be mutated in place.
     */
    public final boolean immutable() {
        return this.immutable;
    }

    public final void immutable(boolean value) {
        this.immutable = value;
    }
}