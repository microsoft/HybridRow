// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

/**
 * Describes a single property definition.
 */
public class Property {

    @JsonProperty(required = false)
    private String comment;

    @JsonProperty(required = true)
    private String path;

    @JsonProperty(required = true)
    private PropertyType type;

    /**
     * An (optional) comment describing the purpose of this {@linkplain Property property}.
     * <p>
     * Comments are for documentary purpose only and do not affect the property at runtime.
     *
     * @return the comment on this {@linkplain Schema property} or {@code null}, if there is no comment.
     */
    public final String comment() {
        return this.comment;
    }

    /**
     * Sets the (optional) comment describing the purpose of this {@linkplain Property property}.
     * <p>
     * Comments are for documentary purpose only and do not affect the property at runtime.
     *
     * @param value a comment on this {@linkplain Property property} or {@code null} to remove the comment, if any, on
     *              this {@linkplain Property property}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final Property comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * The logical path of this {@linkplain Property property}.
     * <p>.
     * For complex properties (e.g. objects) the logical path forms a prefix to relative paths of properties defined
     * within nested structures.
     * <p>
     * See the logical path specification for full details on both relative and absolute paths.
     *
     * @return the logical path of this {@linkplain Property property}.
     */
    public final String path() {
        return this.path;
    }

    /**
     * Sets the logical path of this {@linkplain Property property}.
     * <p>.
     * For complex properties (e.g. objects) the logical path forms a prefix to relative paths of properties defined
     * within nested structures.
     * <p>
     * See the logical path specification for full details on both relative and absolute paths.
     *
     * @param value the logical path of this {@linkplain Property property}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final Property path(@Nonnull String value) {
        this.path = value;
        return this;
    }

    /**
     * The type of this {@linkplain Property property}.
     * <p>
     * Types may be simple (e.g. int8) or complex (e.g. object). Simple types always define a single column. Complex
     * types may define one or more columns depending on their structure.
     *
     * @return the type of this {@linkplain Property property}.
     */
    public final PropertyType type() {
        return this.type;
    }

    /**
     * Sets the type of this {@linkplain Property property}.
     * <p>
     * Types may be simple (e.g. int8) or complex (e.g. object). Simple types always define a single column. Complex
     * types may define one or more columns depending on their structure.
     *
     * @param value the type of this {@linkplain Property property}.
     * @return a reference to this {@linkplain Property property}.
     */
    public final Property type(PropertyType value) {
        this.type = value;
        return this;
    }
}
