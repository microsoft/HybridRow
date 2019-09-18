// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.PrimitiveIterator;

/**
 * The base class for property types both primitive and complex.
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    // Composite types
    @Type(value = ArrayPropertyType.class, name = "array"),
    @Type(value = MapPropertyType.class, name = "map"),
    @Type(value = ObjectPropertyType.class, name="object"),
    @Type(value = ScopePropertyType.class, name="scope"),
    @Type(value = SetPropertyType.class, name="set"),
    @Type(value = TaggedPropertyType.class, name="tagged"),
    @Type(value = TuplePropertyType.class, name="tuple"),
    // Primitive types
    @Type(value = PrimitivePropertyType.class, name="int32"),
    @Type(value = PrimitivePropertyType.class, name="uint32"),
    @Type(value = PrimitivePropertyType.class, name="utf8")
})
public abstract class PropertyType {

    @JsonProperty(required = true)
    private TypeKind type;

    @JsonProperty
    private String apiType;

    @JsonProperty(defaultValue = "true")
    private boolean nullable;

    protected PropertyType() {
        this.nullable(true);
    }

    /**
     * API-specific type annotations for this {@linkplain Property property}.
     *
     * @return API-specific type annotations for this {@linkplain Property property}.
     */
    public final String apiType() {
        return this.apiType;
    }

    /**
     * Sets API-specific type annotations for this {@linkplain Property property}.
     *
     * @param value API-specific type annotations for this {@linkplain Property property}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final PropertyType apiType(String value) {
        this.apiType = value;
        return this;
    }

    /**
     * {@code true} if the {@linkplain Property property} can be {@code null}.
     * <p>
     * Default: {@code true}
     *
     * @return {@code true} if the {@linkplain Property property} can be {@code null, otherwise {@code false}}.
     */
    public final boolean nullable() {
        return this.nullable;
    }

    /**
     * Sets a flag indicating whether the {@linkplain Property property} can be {@code null}.
     *
     * @param value {@code true} indicates that this {@linkplain Property property} can be {@code null}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final PropertyType nullable(boolean value) {
        this.nullable = value;
        return this;
    }

    /**
     * The logical type of this {@linkplain Property property}.
     *
     * @return the logical type of this {@linkplain Property property}.
     */
    public final TypeKind type() {
        return this.type;
    }

    /**
     * Sets the logical type of this {@linkplain Property property}.
     *
     * @param value the logical type of this {@linkplain Property property}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final PropertyType type(TypeKind value) {
        this.type = value;
        return this;
    }
}
