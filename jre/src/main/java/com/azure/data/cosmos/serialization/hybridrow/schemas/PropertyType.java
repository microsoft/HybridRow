//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * The base class for property types both primitive and complex.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(PropertySchemaConverter))] public abstract class PropertyType
public abstract class PropertyType {
    /**
     * Api-specific type annotations for the property.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "apitype")] public string ApiType {get;set;}
    private String ApiType;
    /**
     * True if the property can be null.
     * Default: true.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DefaultValue(true)][JsonProperty(PropertyName = "nullable", DefaultValueHandling =
    // DefaultValueHandling.IgnoreAndPopulate)][JsonConverter(typeof(StrictBooleanConverter))] public bool Nullable
    // {get;set;}
    private boolean Nullable;
    /**
     * The logical type of the property.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "type")] public TypeKind Type {get;set;}
    private TypeKind Type = TypeKind.values()[0];

    protected PropertyType() {
        this.setNullable(true);
    }

    public final String getApiType() {
        return ApiType;
    }

    public final void setApiType(String value) {
        ApiType = value;
    }

    public final boolean getNullable() {
        return Nullable;
    }

    public final void setNullable(boolean value) {
        Nullable = value;
    }

    public final TypeKind getType() {
        return Type;
    }

    public final void setType(TypeKind value) {
        Type = value;
    }
}