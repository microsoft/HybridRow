//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import Newtonsoft.Json.*;
import azure.data.cosmos.serialization.hybridrow.SchemaId;

import java.nio.file.Files;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(SchemaHashUnitTests.SchemaFile, "TestData")] public class
// SchemaHashUnitTests
public class SchemaHashUnitTests {
    private static final String SchemaFile = "TestData\\SchemaHashCoverageSchema.json";
    private Namespace ns;
    private Schema tableSchema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void InitializeSuite()
    public final void InitializeSuite() {
        String json = Files.readString(SchemaHashUnitTests.SchemaFile);
        this.ns = Namespace.Parse(json);
        this.tableSchema = tangible.ListHelper.find(this.ns.getSchemas(), s -> s.Name.equals("Table"));
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void SchemaHashCompileTest()
    public final void SchemaHashCompileTest() {
        Layout layout = this.tableSchema.Compile(this.ns);
        assert layout != null;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void SchemaHashTest()
    public final void SchemaHashTest() {
        // TODO: C# TO JAVA CONVERTER: Tuple variables are not converted by C# to Java Converter:
        ( long low, long high)hash = SchemaHash.ComputeHash(this.ns, this.tableSchema);
        assert (0,0) !=hash;
        // TODO: C# TO JAVA CONVERTER: Tuple variables are not converted by C# to Java Converter:
        ( long low, long high)hash2 = SchemaHash.ComputeHash(this.ns, this.tableSchema, (1, 1))
        assert hash != hash2;

        // Test clone are the same.
        Schema clone = SchemaHashUnitTests.Clone(this.tableSchema);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2;

        // Test Schema changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.setName("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2; // Name not part of the hash

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.setComment("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2; // Comment not part of the hash

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.setVersion(SchemaLanguageVersion.forValue((byte)1));
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2; // Encoding version not part of the hash

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.setSchemaId(new SchemaId(42));
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.setType(TypeKind.Int8);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Options changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getOptions().setEnablePropertyLevelTimestamp(!clone.getOptions().getEnablePropertyLevelTimestamp());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getOptions().setDisallowUnschematized(!clone.getOptions().getDisallowUnschematized());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Partition Keys changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getPartitionKeys().get(0).setPath("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Primary Sort Keys changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getPrimarySortKeys().get(0).setPath("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getPrimarySortKeys().get(0).setDirection(SortDirection.Descending);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Static Keys changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getStaticKeys().get(0).setPath("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Properties changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getProperties().get(0).setComment("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2; // Comment not part of the hash

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getProperties().get(0).setPath("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getProperties().get(0).getPropertyType().setApiType("something else");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getProperties().get(0).getPropertyType().setType(TypeKind.Binary);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        clone.getProperties().get(0).getPropertyType().setNullable(!clone.getProperties().get(0).getPropertyType().getNullable());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Primitive Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar =
            clone.getProperties().get(0).getPropertyType();
        (tempVar instanceof PrimitivePropertyType ? (PrimitivePropertyType)tempVar : null).setLength(42);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar2 =
            clone.getProperties().get(0).getPropertyType();
        (tempVar2 instanceof PrimitivePropertyType ? (PrimitivePropertyType)tempVar2 : null).setStorage(StorageKind.Variable);
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Scope Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar3 =
            clone.getProperties().get(1).getPropertyType();
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar4 =
            clone.getProperties().get(1).getPropertyType();
        (tempVar3 instanceof ScopePropertyType ? (ScopePropertyType)tempVar3 : null).setImmutable(!(tempVar4 instanceof ScopePropertyType ? (ScopePropertyType)tempVar4 : null).getImmutable());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Array Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar5 =
            clone.getProperties().get(1).getPropertyType();
        (tempVar5 instanceof ArrayPropertyType ? (ArrayPropertyType)tempVar5 : null).setItems(clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Object Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar6 =
            clone.getProperties().get(2).getPropertyType();
        (tempVar6 instanceof ObjectPropertyType ? (ObjectPropertyType)tempVar6 : null).getProperties().set(0,
            clone.getProperties().get(0));
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Map Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar7 =
            clone.getProperties().get(3).getPropertyType();
        (tempVar7 instanceof MapPropertyType ? (MapPropertyType)tempVar7 : null).setKeys(clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar8 =
            clone.getProperties().get(3).getPropertyType();
        (tempVar8 instanceof MapPropertyType ? (MapPropertyType)tempVar8 : null).setValues(clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Set Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar9 =
            clone.getProperties().get(4).getPropertyType();
        (tempVar9 instanceof SetPropertyType ? (SetPropertyType)tempVar9 : null).setItems(clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Tagged Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar10 =
            clone.getProperties().get(5).getPropertyType();
        (tempVar10 instanceof TaggedPropertyType ? (TaggedPropertyType)tempVar10 : null).getItems().set(0,
            clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test Tuple Property Type changes
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar11 =
            clone.getProperties().get(6).getPropertyType();
        (tempVar11 instanceof TuplePropertyType ? (TuplePropertyType)tempVar11 : null).getItems().set(0,
            clone.getProperties().get(0).getPropertyType());
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Test UDT Property Type changes
        try {
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar12 =
                clone.getProperties().get(7).getPropertyType();
            (tempVar12 instanceof UdtPropertyType ? (UdtPropertyType)tempVar12 : null).setName("some non-existing UDT" +
                " name");
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.Fail("Should have thrown an exception.");
        } catch (RuntimeException ex) {
            assert ex != null;
        }

        try {
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar13 =
                clone.getProperties().get(7).getPropertyType();
            (tempVar13 instanceof UdtPropertyType ? (UdtPropertyType)tempVar13 : null).setName("Table");
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.Fail("Should have thrown an exception.");
        } catch (RuntimeException ex) {
            assert ex != null;
        }

        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar14 =
            clone.getProperties().get(7).getPropertyType();
        (tempVar14 instanceof UdtPropertyType ? (UdtPropertyType)tempVar14 : null).setName("Table");
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar15 =
            clone.getProperties().get(7).getPropertyType();
        (tempVar15 instanceof UdtPropertyType ? (UdtPropertyType)tempVar15 : null).setSchemaId(new SchemaId(2));
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash != hash2;

        // Renaming an UDT is not a breaking change as long as the SchemaId has not changed.
        this.ns.getSchemas().get(0).setName("RenameActualUDT");
        clone = SchemaHashUnitTests.Clone(this.tableSchema);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas.PropertyType tempVar16 = clone.getProperties().get(7).getPropertyType();
        (tempVar16 instanceof UdtPropertyType ? (UdtPropertyType)tempVar16 : null).setName("RenameActualUDT");
        hash2 = SchemaHash.ComputeHash(this.ns, clone);
        assert hash == hash2;
    }

    private static Schema Clone(Schema s) {
        JsonSerializerSettings settings = new JsonSerializerSettings();
        settings.NullValueHandling = NullValueHandling.Ignore;
        settings.Formatting = Formatting.Indented;
        settings.CheckAdditionalContent = true;

        String json = JsonConvert.SerializeObject(s, settings);
        return JsonConvert.<Schema>DeserializeObject(json, settings);
    }
}