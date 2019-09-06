// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

public abstract class ScopePropertyType extends PropertyType {

    private boolean immutable;

    /**
     * True if the property's child elements cannot be mutated in place.
     * Immutable properties can still be replaced in their entirety.
     */
    public final boolean immutable() {
        return this.immutable;
    }

    public final void immutable(boolean value) {
        this.immutable = value;
    }
}