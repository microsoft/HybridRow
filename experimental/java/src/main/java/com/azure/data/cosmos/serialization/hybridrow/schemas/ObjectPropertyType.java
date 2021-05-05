// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.Collections;
import java.util.List;

/**
 * Object properties represent nested structures.
 * <p>
 * Object properties map to multiple columns depending on the number of internal properties within the defined object
 * structure.  Object properties are provided as a convince in schema design.  They are effectively equivalent to
 * defining the same properties explicitly via {@link PrimitivePropertyType} with nested property paths.
 */
public class ObjectPropertyType extends ScopePropertyType {

    private List<Property> properties;

    /**
     * Initializes a new instance of the {@link ObjectPropertyType} class.
     */
    public ObjectPropertyType() {
        this.properties = Collections.emptyList();
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema.
     *
     * @return a list of zero or more property definitions that define the columns within the schema.
     */
    public final List<Property> properties() {
        return this.properties;
    }

    public final ObjectPropertyType properties(List<Property> value) {
        this.properties = value != null ? value : Collections.emptyList();
        return this;
    }
}