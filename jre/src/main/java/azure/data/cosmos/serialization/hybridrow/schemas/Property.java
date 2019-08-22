//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1716 // Identifiers should not match keywords


/**
 * Describes a single property definition.
 */
public class Property {
    /**
     * An (optional) comment describing the purpose of this property.
     * Comments are for documentary purpose only and do not affect the property at runtime.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling
    // .IgnoreAndPopulate)] public string Comment {get;set;}
    private String Comment;
    /**
     * The logical path of this property.
     * <p>
     * For complex properties (e.g. objects) the logical path forms a prefix to relative paths of
     * properties defined within nested structures.
     * <para />
     * See the logical path specification for full details on both relative and absolute paths.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "path", Required = Required.Always)] public string Path {get;set;}
    private String Path;
    /**
     * The type of the property.
     * <p>
     * Types may be simple (e.g. int8) or complex (e.g. object).  Simple types always define a
     * single column.  Complex types may define one or more columns depending on their structure.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "type", Required = Required.Always)] public PropertyType
    // PropertyType {get;set;}
    private PropertyType PropertyType;

    public final String getComment() {
        return Comment;
    }

    public final void setComment(String value) {
        Comment = value;
    }

    public final String getPath() {
        return Path;
    }

    public final void setPath(String value) {
        Path = value;
    }

    public final PropertyType getPropertyType() {
        return PropertyType;
    }

    public final void setPropertyType(PropertyType value) {
        PropertyType = value;
    }
}