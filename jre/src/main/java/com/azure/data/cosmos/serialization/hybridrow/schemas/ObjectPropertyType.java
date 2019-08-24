//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import java.util.ArrayList;

/**
 * Object properties represent nested structures.
 * <p>
 * Object properties map to multiple columns depending on the number of internal properties
 * within the defined object structure.  Object properties are provided as a convince in schema
 * design.  They are effectively equivalent to defining the same properties explicitly via
 * {@link PrimitivePropertyType} with nested property paths.
 */
public class ObjectPropertyType extends ScopePropertyType {
    /**
     * A list of zero or more property definitions that define the columns within the schema.
     */
    private ArrayList<Property> properties;

    /**
     * Initializes a new instance of the {@link ObjectPropertyType} class.
     */
    public ObjectPropertyType() {
        this.properties = new ArrayList<Property>();
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "properties")] public List<Property> Properties
    public final ArrayList<Property> getProperties() {
        return this.properties;
    }

    public final void setProperties(ArrayList<Property> value) {
        this.properties = value != null ? value : new ArrayList<Property>();
    }
}