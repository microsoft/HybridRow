//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.schemas;

import Newtonsoft.Json.*;
import azure.data.cosmos.serialization.hybridrow.SchemaId;
import azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import azure.data.cosmos.serialization.hybridrow.layouts.LayoutCompiler;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A schema describes either table or UDT metadata.
 * <p>
 * The schema of a table or UDT describes the structure of row (i.e. which columns and the
 * types of those columns).  A table schema represents the description of the contents of a collection
 * level row directly.  UDTs described nested structured objects that may appear either within a table
 * column or within another UDT (i.e. nested UDTs).
 */
public class Schema {
    /**
     * An (optional) comment describing the purpose of this schema.
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "comment", DefaultValueHandling = DefaultValueHandling
    // .IgnoreAndPopulate)] public string Comment {get;set;}
    private String Comment;
    /**
     * The name of the schema.
     * <p>
     * The name of a schema MUST be unique within its namespace.
     * <para />
     * Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
     * underscores.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "name", Required = Required.Always)] public string Name {get;set;}
    private String Name;
    /**
     * Schema-wide operations.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "options")] public SchemaOptions Options {get;set;}
    private SchemaOptions Options;
    /**
     * The unique identifier for a schema.
     * Identifiers must be unique within the scope of the database in which they are used.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "id", Required = Required.Always)] public SchemaId SchemaId {get;set;}
    private SchemaId SchemaId = new SchemaId();
    /**
     * The type of this schema.  This value MUST be <see cref="TypeKind.Schema" />.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DefaultValue(TypeKind.Schema)][JsonProperty(PropertyName = "type", Required = Required
    // .DisallowNull, DefaultValueHandling = DefaultValueHandling.Populate)] public TypeKind Type {get;set;}
    private TypeKind Type = TypeKind.values()[0];
    /**
     * The version of the HybridRow Schema Definition Language used to encode this schema.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "version")] public SchemaLanguageVersion Version {get;set;}
    private SchemaLanguageVersion Version = SchemaLanguageVersion.values()[0];
    /**
     * An (optional) list of zero or more logical paths that form the partition key.
     */
    private ArrayList<PartitionKey> partitionKeys;
    /**
     * An (optional) list of zero or more logical paths that form the primary sort key.
     */
    private ArrayList<PrimarySortKey> primaryKeys;
    /**
     * A list of zero or more property definitions that define the columns within the schema.
     */
    private ArrayList<Property> properties;
    /**
     * An (optional) list of zero or more logical paths that hold data shared by all documents that have the same
     * partition key.
     */
    private ArrayList<StaticKey> staticKeys;

    /**
     * Initializes a new instance of the <see cref="Schema" /> class.
     */
    public Schema() {
        this.setType(TypeKind.Schema);
        this.properties = new ArrayList<Property>();
        this.partitionKeys = new ArrayList<PartitionKey>();
        this.primaryKeys = new ArrayList<PrimarySortKey>();
        this.staticKeys = new ArrayList<StaticKey>();
    }

    public final String getComment() {
        return Comment;
    }

    public final void setComment(String value) {
        Comment = value;
    }

    public final String getName() {
        return Name;
    }

    public final void setName(String value) {
        Name = value;
    }

    public final SchemaOptions getOptions() {
        return Options;
    }

    public final void setOptions(SchemaOptions value) {
        Options = value;
    }

    /**
     * An (optional) list of zero or more logical paths that form the partition key.
     * All paths referenced MUST map to a property within the schema.
     * <para />
     * This field is never null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "partitionkeys")] public List<PartitionKey> PartitionKeys
    public final ArrayList<PartitionKey> getPartitionKeys() {
        return this.partitionKeys;
    }

    public final void setPartitionKeys(ArrayList<PartitionKey> value) {
        this.partitionKeys = value != null ? value : new ArrayList<PartitionKey>();
    }

    /**
     * An (optional) list of zero or more logical paths that form the primary sort key.
     * All paths referenced MUST map to a property within the schema.
     * <para />
     * This field is never null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "primarykeys")] public List<PrimarySortKey> PrimarySortKeys
    public final ArrayList<PrimarySortKey> getPrimarySortKeys() {
        return this.primaryKeys;
    }

    public final void setPrimarySortKeys(ArrayList<PrimarySortKey> value) {
        this.primaryKeys = value != null ? value : new ArrayList<PrimarySortKey>();
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema.
     * This field is never null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "properties")] public List<Property> Properties
    public final ArrayList<Property> getProperties() {
        return this.properties;
    }

    public final void setProperties(ArrayList<Property> value) {
        this.properties = value != null ? value : new ArrayList<Property>();
    }

    public final SchemaId getSchemaId() {
        return SchemaId;
    }

    public final void setSchemaId(SchemaId value) {
        SchemaId = value.clone();
    }

    /**
     * An (optional) list of zero or more logical paths that hold data shared by all documents with same partition key.
     * All paths referenced MUST map to a property within the schema.
     * <para />
     * This field is never null.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [JsonProperty(PropertyName = "statickeys", DefaultValueHandling = DefaultValueHandling
    // .IgnoreAndPopulate)] public List<StaticKey> StaticKeys
    public final ArrayList<StaticKey> getStaticKeys() {
        return this.staticKeys;
    }

    public final void setStaticKeys(ArrayList<StaticKey> value) {
        this.staticKeys = value != null ? value : new ArrayList<StaticKey>();
    }

    public final TypeKind getType() {
        return Type;
    }

    public final void setType(TypeKind value) {
        Type = value;
    }

    public final SchemaLanguageVersion getVersion() {
        return Version;
    }

    public final void setVersion(SchemaLanguageVersion value) {
        Version = value;
    }

    /**
     * Compiles this logical schema into a physical layout that can be used to read and write
     * rows.
     *
     * @param ns The namespace within which this schema is defined.
     * @return The layout for the schema.
     */
    public final Layout Compile(Namespace ns) {
        checkArgument(ns != null);
        checkArgument(ns.getSchemas().contains(this));

        return LayoutCompiler.Compile(ns, this);
    }

    /**
     * Parse a JSON fragment and return a schema.
     *
     * @param json The JSON text to parse.
     * @return A logical schema.
     */
    public static Schema Parse(String json) {
        return JsonConvert.<Schema>DeserializeObject(json);

        // TODO: perform structural validation on the Schema after JSON parsing.
    }
}