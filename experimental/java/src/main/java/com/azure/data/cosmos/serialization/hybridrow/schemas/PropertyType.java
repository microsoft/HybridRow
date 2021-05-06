// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * The base class for property types both primitive and complex.
 */
@JsonTypeInfo(use = Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    // Composite types
    @Type(value = ArrayPropertyType.class, name = "array"),
    @Type(value = MapPropertyType.class, name = "map"),
    @Type(value = ObjectPropertyType.class, name = "object"),
    @Type(value = SetPropertyType.class, name = "set"),
    @Type(value = TaggedPropertyType.class, name = "tagged"),
    @Type(value = TuplePropertyType.class, name = "tuple"),
    @Type(value = UdtPropertyType.class, name = "schema"),
    // Primitive types
    @Type(value = PrimitivePropertyType.class, name = "null"),
    @Type(value = PrimitivePropertyType.class, name = "bool"),
    @Type(value = PrimitivePropertyType.class, name = "int8"),
    @Type(value = PrimitivePropertyType.class, name = "int16"),
    @Type(value = PrimitivePropertyType.class, name = "int32"),
    @Type(value = PrimitivePropertyType.class, name = "int64"),
    @Type(value = PrimitivePropertyType.class, name = "varint"),
    @Type(value = PrimitivePropertyType.class, name = "uint8"),
    @Type(value = PrimitivePropertyType.class, name = "uint16"),
    @Type(value = PrimitivePropertyType.class, name = "uint32"),
    @Type(value = PrimitivePropertyType.class, name = "uint64"),
    @Type(value = PrimitivePropertyType.class, name = "varuint"),
    @Type(value = PrimitivePropertyType.class, name = "float32"),
    @Type(value = PrimitivePropertyType.class, name = "float64"),
    @Type(value = PrimitivePropertyType.class, name = "float128"),
    @Type(value = PrimitivePropertyType.class, name = "decimal"),
    @Type(value = PrimitivePropertyType.class, name = "datetime"),
    @Type(value = PrimitivePropertyType.class, name = "unixdatetime"),
    @Type(value = PrimitivePropertyType.class, name = "binary"),
    @Type(value = PrimitivePropertyType.class, name = "guid"),
    @Type(value = PrimitivePropertyType.class, name = "utf8"),
    @Type(value = PrimitivePropertyType.class, name = "any")
})
public abstract class PropertyType {

    @JsonProperty
    private String apiType;

    @JsonProperty(defaultValue = "true")
    private boolean nullable;

    @JsonProperty(required = true)
    private TypeKind type;

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
