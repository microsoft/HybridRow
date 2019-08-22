//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

/**
 * Describes a property or set of properties used to partition the data set across machines.
 */
public class PartitionKey {
    /**
     * The logical path of the referenced property.
     * Partition keys MUST refer to properties defined within the same <see cref="Schema" />.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "path", Required = Required.Always)] public string Path {get;set;}
    private String Path;

    public final String getPath() {
        return Path;
    }

    public final void setPath(String value) {
        Path = value;
    }
}