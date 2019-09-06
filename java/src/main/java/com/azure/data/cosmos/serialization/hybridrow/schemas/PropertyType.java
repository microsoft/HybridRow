// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * The base class for property types both primitive and complex.
 */
public abstract class PropertyType {

    private String apiType;
    private boolean nullable;
    private TypeKind type = TypeKind.values()[0];

    protected PropertyType() {
        this.nullable(true);
    }

    /**
     * Api-specific type annotations for the property.
     */
    public final String apiType() {
        return this.apiType;
    }

    public final PropertyType apiType(String value) {
        this.apiType = value;
        return this;
    }

    /**
     * {@code true} if the property can be {@code null}
     * <p>
     * Default: {@code true}
     */
    public final boolean nullable() {
        return this.nullable;
    }

    public final PropertyType nullable(boolean value) {
        this.nullable = value;
        return this;
    }

    /**
     * The logical type of the property
     */
    public final TypeKind type() {
        return this.type;
    }

    public final PropertyType type(TypeKind value) {
        this.type = value;
        return this;
    }
}