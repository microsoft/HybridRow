// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a single property definition.
 */
public class Property {

    private String comment;
    private String path;
    private PropertyType propertyType;

    /**
     * An (optional) comment describing the purpose of this property
     * <p>
     * Comments are for documentary purpose only and do not affect the property at runtime.
     */
    public final String comment() {
        return this.comment;
    }

    public final Property comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * The logical path of this property.
     * <p>
     * For complex properties (e.g. objects) the logical path forms a prefix to relative paths of properties defined
     * within nested structures.
     * <p>
     * See the logical path specification for full details on both relative and absolute paths.
     */
    public final String path() {
        return this.path;
    }

    public final void setPath(String value) {
        this.path = value;
    }

    /**
     * The type of the property.
     * <p>
     * Types may be simple (e.g. int8) or complex (e.g. object).  Simple types always define a single column.  Complex
     * types may define one or more columns depending on their structure.
     */
    public final PropertyType propertyType() {
        return this.propertyType;
    }

    public final void setPropertyType(PropertyType value) {
        this.propertyType = value;
    }
}