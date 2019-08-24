//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

/**
 * UDT properties represent nested structures with an independent schema.
 * <p>
 * UDT properties include a nested row within an existing row as a column.  The schema of the
 * nested row may be evolved independently of the outer row.  Changes to the independent schema affect
 * all outer schemas where the UDT is used.
 */
public class UdtPropertyType extends ScopePropertyType {
    /**
     * The identifier of the UDT schema defining the structure for the nested row.
     * <p>
     * The UDT schema MUST be defined within the same {@link Namespace} as the schema that
     * references it.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "name", Required = Required.Always)] public string Name {get;set;}
    private String Name;
    /**
     * The unique identifier for a schema.
     * <p>
     * Optional uniquifier if multiple versions of {@link Name} appears within the Namespace.
     * <p>
     * If multiple versions of a UDT are defined within the {@link Namespace} then the globally
     * unique identifier of the specific version referenced MUST be provided.
     * </p>
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "id", Required = Required.DisallowNull, DefaultValueHandling =
    // DefaultValueHandling.IgnoreAndPopulate)] public SchemaId SchemaId {get;set;}
    private com.azure.data.cosmos.serialization.hybridrow.SchemaId SchemaId = new SchemaId();

    public UdtPropertyType() {
        this.setSchemaId(com.azure.data.cosmos.serialization.hybridrow.SchemaId.Invalid);
    }

    public final String getName() {
        return Name;
    }

    public final void setName(String value) {
        Name = value;
    }

    public final SchemaId getSchemaId() {
        return SchemaId;
    }

    public final void setSchemaId(SchemaId value) {
        SchemaId = value.clone();
    }
}