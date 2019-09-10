// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import Newtonsoft.Json.*;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.SortDirection;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;
import com.azure.data.cosmos.serialization.hybridrow.schemas.TypeKind;

import java.nio.file.Files;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here
// are anonymous.")] public class SchemaUnitTests
public class SchemaUnitTests {
    PrimitivePropertyType sp = (PrimitivePropertyType)p.getPropertyType();
    String tableSchema = String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s]}",
        columnSchema);
    Schema s = Schema.Parse(tableSchema);)
    Property p = s.getProperties().get(0);

    {
        case Utf8:
        case Binary:
            switch (sp.getStorage()) {
                case Fixed:
                case Variable:
                case Sparse:
                    Assert.AreEqual(expected.Len, sp.getLength(), "Json: {0}", expected.Json);
                    break;
                default:
                    Assert.Fail("Json: {0}", expected.Json);
                    break;
            }

            break;
    }
			Assert.AreEqual(1,s.getProperties().

    getType(), "Json: {0}",expected.Json)

    getType()
			Assert.AreEqual(expected.Type,p.getPropertyType().

    size(), "Json: {0}",expected.Json)

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseNamespace()
    public final void ParseNamespace() {
        {
            // Test empty schemas.
            String[] emptyNamespaceJsonInput = { "{ }", "{'schemas': [ ] }", "{'name': null }", "{'name': null, " +
                "'schemas': null }", "{'version': 'v1', 'name': null, 'schemas': null }" };

            for (String json : emptyNamespaceJsonInput) {
                Namespace n1 = Namespace.Parse(json);
                Assert.IsNull(n1.getName(), "Got: {0}, Json: {1}", n1.getName(), json);
                Assert.IsNotNull(n1.getSchemas(), "Json: {0}", json);
                Assert.AreEqual(0, n1.getSchemas().size(), "Got: {0}, Json: {1}", n1.getSchemas(), json);
                assert SchemaLanguageVersion.V1 == n1.getVersion();
            }
        }

        {
            // Test simple schemas and schema options.
            String json = "{'name': 'myschema', 'schemas': null }";
            Namespace n1 = Namespace.Parse(json);
            Assert.AreEqual("myschema", n1.getName(), "Json: {0}", json);
            Assert.IsNotNull(n1.getSchemas(), "Json: {0}", json);

            // Version defaults propertly when NOT specified.
            assert SchemaLanguageVersion.V1 == n1.getVersion();
        }

        {
            String json = "{'name': 'myschema', 'schemas': [" + "\r\n" +
                "                    {'version': 'v1', 'name': 'emptyTable', 'id': -1, 'type': 'schema', " + "\r\n" +
                "                     'options': { 'disallowUnschematized': true }, 'properties': null } ] }";

            Namespace n1 = Namespace.Parse(json);
            Assert.AreEqual("myschema", n1.getName(), "Json: {0}", json);
            Assert.AreEqual(1, n1.getSchemas().size(), "Json: {0}", json);
            Assert.AreEqual("emptyTable", n1.getSchemas().get(0).getName(), "Json: {0}", json);
            Assert.AreEqual(new SchemaId(-1), n1.getSchemas().get(0).getSchemaId().clone(), "Json: {0}", json);
            Assert.AreEqual(TypeKind.SCHEMA, n1.getSchemas().get(0).getType(), "Json: {0}", json);
            Assert.AreEqual(true, n1.getSchemas().get(0).getOptions().getDisallowUnschematized(), "Json: {0}", json);
            Assert.IsNotNull(n1.getSchemas().get(0).getProperties().size(), "Json: {0}", json);
            Assert.AreEqual(0, n1.getSchemas().get(0).getProperties().size(), "Json: {0}", json);
            assert SchemaLanguageVersion.V1 == n1.getVersion();
        }

        {
            // Test basic schema with primitive columns.
            String json = "{'name': 'myschema', 'schemas': [" + "\r\n" +
                "                    {'name': 'myUDT', 'id': 1, 'type': 'schema', 'options': { " +
                "'disallowUnschematized': false }, " + "\r\n" +
                "                     'properties': [ " + "\r\n" +
                "                        { 'path': 'a', 'type': { 'type': 'int8', 'storage': 'fixed' }}, " + "\r\n" +
                "                        { 'path': 'b', 'type': { 'type': 'utf8', 'storage': 'variable' }} " + "\r\n" +
                "                     ] }" + "\r\n" +
                "                    ] }";

            Namespace n1 = Namespace.Parse(json);
            Assert.AreEqual(1, n1.getSchemas().size(), "Json: {0}", json);
            Assert.AreEqual("myUDT", n1.getSchemas().get(0).getName(), "Json: {0}", json);
            Assert.AreEqual(new SchemaId(1), n1.getSchemas().get(0).getSchemaId().clone(), "Json: {0}", json);
            Assert.AreEqual(TypeKind.SCHEMA, n1.getSchemas().get(0).getType(), "Json: {0}", json);
            Assert.AreEqual(false, n1.getSchemas().get(0).getOptions().getDisallowUnschematized(), "Json: {0}", json);
            Assert.AreEqual(2, n1.getSchemas().get(0).getProperties().size(), "Json: {0}", json);

            class AnonymousType {
                public String Path;
                public StorageKind Storage;
                public TypeKind Type;

                public AnonymousType(String _Path,
                                     TypeKind _Type,
                                     StorageKind _Storage) {
                    Path = _Path;
                    Type = _Type;
                    Storage = _Storage;
                }
            }
            class AnonymousType2 {
                public String Path;
                public StorageKind Storage;
                public TypeKind Type;

                public AnonymousType2(String _Path,
                                      TypeKind _Type,
                                      StorageKind _Storage) {
                    Path = _Path;
                    Type = _Type;
                    Storage = _Storage;
                }
            }
            Object[] expectedProps = new Object[] { AnonymousType("a", TypeKind.INT_8, StorageKind.FIXED),
                AnonymousType2("b", TypeKind.UTF_8, StorageKind.VARIABLE) };

            for (int i = 0; i < n1.getSchemas().get(0).getProperties().size(); i++) {
                Property p = n1.getSchemas().get(0).getProperties().get(i);
                Assert.AreEqual(expectedProps[i].Path, p.getPath(), "Json: {0}", json);
                Assert.AreEqual(expectedProps[i].Type, p.getPropertyType().getType(), "Json: {0}", json);
                PrimitivePropertyType sp = (PrimitivePropertyType)p.getPropertyType();
                Assert.AreEqual(expectedProps[i].Storage, sp.getStorage(), "Json: {0}", json);
            }
        }
    }
			switch(p.getPropertyType().

        // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem("TestData\\CoverageSchema.json", "TestData")]
    // public void ParseNamespaceExample()
    public final void ParseNamespaceExample() {
        String json = Files.readString("TestData\\CoverageSchema.json");
        Namespace n1 = Namespace.Parse(json);
        JsonSerializerSettings settings = new JsonSerializerSettings();
        settings.NullValueHandling = NullValueHandling.Ignore;
        settings.Formatting = Formatting.Indented;

        String json2 = JsonConvert.SerializeObject(n1, settings);
        Namespace n2 = Namespace.Parse(json2);
        String json3 = JsonConvert.SerializeObject(n2, settings);
        assert json2 == json3;
    })

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaPrimitives()
    public final void ParseSchemaPrimitives() {
        // Test all primitive column types.
        class AnonymousType {
            public String Json;
            public TypeKind Type;

            public AnonymousType(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType10 {
            public String Json;
            public TypeKind Type;

            public AnonymousType10(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType11 {
            public String Json;
            public TypeKind Type;

            public AnonymousType11(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType12 {
            public String Json;
            public TypeKind Type;

            public AnonymousType12(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType13 {
            public String Json;
            public TypeKind Type;

            public AnonymousType13(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType14 {
            public String Json;
            public TypeKind Type;

            public AnonymousType14(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType15 {
            public String Json;
            public TypeKind Type;

            public AnonymousType15(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType16 {
            public String Json;
            public TypeKind Type;

            public AnonymousType16(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType17 {
            public String Json;
            public TypeKind Type;

            public AnonymousType17(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType18 {
            public String Json;
            public TypeKind Type;

            public AnonymousType18(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType19 {
            public String Json;
            public TypeKind Type;

            public AnonymousType19(String _Json,
                                   TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType2 {
            public String Json;
            public TypeKind Type;

            public AnonymousType2(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType20 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType20(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType21 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType21(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType22 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType22(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType23 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType23(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType24 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType24(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType25 {
            public String Json;
            public int Len;
            public TypeKind Type;

            public AnonymousType25(String _Json,
                                   TypeKind _Type, int _Len) {
                Json = _Json;
                Type = _Type;
                Len = _Len;
            }
        }

        class AnonymousType3 {
            public String Json;
            public TypeKind Type;

            public AnonymousType3(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType4 {
            public String Json;
            public TypeKind Type;

            public AnonymousType4(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType5 {
            public String Json;
            public TypeKind Type;

            public AnonymousType5(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType6 {
            public String Json;
            public TypeKind Type;

            public AnonymousType6(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType7 {
            public String Json;
            public TypeKind Type;

            public AnonymousType7(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType8 {
            public String Json;
            public TypeKind Type;

            public AnonymousType8(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }

        class AnonymousType9 {
            public String Json;
            public TypeKind Type;

            public AnonymousType9(String _Json, TypeKind _Type) {
                Json = _Json;
                Type = _Type;
            }
        }
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to the C# 'dynamic' keyword:
        dynamic[] expectedSchemas = { AnonymousType("{'type': 'bool', 'storage': 'fixed'}", TypeKind.BOOLEAN),
            AnonymousType2("{'type': 'int8', 'storage': 'fixed'}", TypeKind.INT_8), AnonymousType3("{'type': 'int16', " +
            "'storage': 'fixed'}", TypeKind.INT_16), AnonymousType4("{'type': 'int32', 'storage': 'fixed'}",
            TypeKind.INT_32), AnonymousType5("{'type': 'int64', 'storage': 'fixed'}", TypeKind.INT_64), AnonymousType6(
                "{'type': 'uint8', 'storage': 'fixed'}", TypeKind.UINT_8), AnonymousType7("{'type': 'uint16', " +
            "'storage': 'fixed'}", TypeKind.UINT_16), AnonymousType8("{'type': 'uint32', 'storage': 'fixed'}",
            TypeKind.UINT_32), AnonymousType9("{'type': 'uint64', 'storage': 'fixed'}", TypeKind.UINT_64),
            AnonymousType10("{'type': 'float32', 'storage': 'fixed'}", TypeKind.FLOAT_32), AnonymousType11("{'type': " +
            "'float64', 'storage': 'fixed'}", TypeKind.FLOAT_64), AnonymousType12("{'type': 'float128', 'storage': " +
            "'fixed'}", TypeKind.FLOAT_128), AnonymousType13("{'type': 'decimal', 'storage': 'fixed'}",
            TypeKind.DECIMAL), AnonymousType14("{'type': 'datetime', 'storage': 'fixed'}", TypeKind.DATE_TIME),
            AnonymousType15("{'type': 'unixdatetime', 'storage': 'fixed'}", TypeKind.UNIX_DATE_TIME), AnonymousType16(
                "{'type': 'guid', 'storage': 'fixed'}", TypeKind.GUID), AnonymousType17("{'type': 'mongodbobjectid', " +
            "'storage': 'fixed'}", TypeKind.MONGODB_OBJECT_ID), AnonymousType18("{'type': 'varint', 'storage': " +
            "'variable'}", TypeKind.VAR_INT), AnonymousType19("{'type': 'varuint', 'storage': 'variable'}",
            TypeKind.VAR_UINT), AnonymousType20("{'type': 'utf8', 'storage': 'fixed', 'length': 2}", TypeKind.UTF_8, 2)
            , AnonymousType21("{'type': 'binary', 'storage': 'fixed', 'length': 2}", TypeKind.BINARY, 2),
            AnonymousType22("{'type': 'utf8', 'storage': 'variable', 'length': 100}", TypeKind.UTF_8, 100),
            AnonymousType23("{'type': 'binary', 'storage': 'variable', 'length': 100}", TypeKind.BINARY, 100),
            AnonymousType24("{'type': 'utf8', 'sparse': 'variable', 'length': 1000}", TypeKind.UTF_8, 1000),
            AnonymousType25("{'type': 'binary', 'sparse': 'variable', 'length': 1000}", TypeKind.BINARY, 1000) };

        for (dynamic expected : expectedSchemas) {
            String columnSchema = String.format("{'path': 'a', 'type': %1$s", expected.Json
        }
    }
}
	}

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaArray()
public final void ParseSchemaArray()
    {

// Test array types include nested arrays.
class AnonymousType {
    public String Json;
    public TypeKind Type;

    public AnonymousType(String _Json, TypeKind _Type) {
        Json = _Json;
        Type = _Type;
    }
}

class AnonymousType2 {
    public String Json;
    public TypeKind Type;

    public AnonymousType2(String _Json, TypeKind _Type) {
        Json = _Json;
        Type = _Type;
    }
}

class AnonymousType3 {
    public String Json;
    public int Len;

    public AnonymousType3(String _Json, int _Len) {
        Json = _Json;
        Len = _Len;
    }
}

class AnonymousType4 {
    public String Json;
    public String Name;
// TODO: C# TO JAVA CONVERTER: There is no Java equivalent to the C# 'dynamic' keyword:
    dynamic[] expectedSchemas = { AnonymousType("{'type': 'int8' }", TypeKind.INT_8), AnonymousType2("{'type': " +
        "'array', 'items': {'type': 'int32'}}", TypeKind.INT_32), AnonymousType3("{'type': 'object', 'properties': " +
        "null}", 0), AnonymousType4("{'type': 'schema', 'name': 'myUDT'}", "myUDT") };

		   public AnonymousType4(String _Json, String _Name) {
        Json = _Json;
        Name = _Name;
    }
}
 for(dynamic expected:expectedSchemas)
            {
            String arrayColumnSchema=String.format("{'path': 'a', 'type': {'type': 'array', 'items': %1$s } }",
            expected.Json);
            String tableSchema=String.format("{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [%1$s] }",
            arrayColumnSchema);
            Schema s=Schema.Parse(tableSchema);
            Assert.AreEqual(1,s.getProperties().size(),"Json: {0}",expected.Json);
            Property p=s.getProperties().get(0);
            Assert.AreEqual(TypeKind.Array,p.getPropertyType().getType(),"Json: {0}",expected.Json);
            ArrayPropertyType pt=(ArrayPropertyType)p.getPropertyType();
            Assert.IsNotNull(pt.getItems(),"Json: {0}",expected.Json);
            switch(pt.getItems().getType())
            {
            case Array:
            ArrayPropertyType subArray=(ArrayPropertyType)pt.getItems();
            Assert.AreEqual(expected.Type,subArray.getItems().getType(),"Json: {0}",expected.Json);
            break;
            case Object:
            ObjectPropertyType subObj=(ObjectPropertyType)pt.getItems();
            Assert.AreEqual(expected.Len,subObj.getProperties().size(),"Json: {0}",expected.Json);
            break;
            case Schema:
            UdtPropertyType subRow=(UdtPropertyType)pt.getItems();
            Assert.AreEqual(expected.Name,subRow.getName(),"Json: {0}",expected.Json);
            break;
default:
    Assert.AreEqual(expected.Type,pt.getItems().getType(),"Json: {0}",expected.Json);
    break;
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaObject()
public final void ParseSchemaObject()
    {

// Test object types include nested objects.
class AnonymousType {
    public String Json;
    public TypeKind Type;

    public AnonymousType(String _Json, TypeKind _Type) {
        Json = _Json;
        Type = _Type;
    }
}

class AnonymousType2 {
    public String Json;
    public TypeKind Type;

    public AnonymousType2(String _Json, TypeKind _Type) {
        Json = _Json;
        Type = _Type;
    }
}

class AnonymousType3 {
    public String Json;
    public int Len;

    public AnonymousType3(String _Json, int _Len) {
        Json = _Json;
        Len = _Len;
    }
}

class AnonymousType4 {
    public String Json;
    public String Name;

    publ TO JAVA CONVERTER TODO TASK: There is no Java equivalent to the C# 'dynamic' keyword:
    dynamic[] expectedSchemas = { AnonymousType("{'path': 'b', 'type': {'type': 'int8', 'storage': 'fixed'}}",
        TypeKind.INT_8), AnonymousType2("{'path': 'b', 'type': {'type': 'array', 'items': {'type': 'int32'}}}",
        TypeKind.INT_32), AnonymousType3("{'path': 'b', 'type': {'type': 'object', 'properties': [{'path': 'c', " +
        "'type': {'type': 'bool'}}]}}", 1), AnonymousType4("{'path': 'b', 'type': {'type': 'schema', 'name': " +
        "'myUDT'}}", "myUDT") };

		for(/C#ic AnonymousType4(String _Json, String _Name) {
        Json = _Json;
        Name = _Name;
    }
}
    /dynamic expected:expectedSchemas)
            {
            String objectColumnSchema=String.format("{'path': 'a', 'type': {'type': 'object', 'properties': [%1$s] } " +
            "}",expected.Json);
            String tableSchema=String.format("{'name': 'table', 'id': -2, 'type': 'schema', 'properties': [%1$s] }",
            objectColumnSchema);
            try
            {
            Schema s=Schema.Parse(tableSchema);
            Assert.AreEqual(1,s.getProperties().size(),"Json: {0}",expected.Json);
            Property pa=s.getProperties().get(0);
            Assert.AreEqual(TypeKind.Object,pa.getPropertyType().getType(),"Json: {0}",expected.Json);
            ObjectPropertyType pt=(ObjectPropertyType)pa.getPropertyType();
            Assert.AreEqual(1,pt.getProperties().size(),"Json: {0}",expected.Json);
            Property pb=pt.getProperties().get(0);
            switch(pb.getPropertyType().getType())
            {
            case Array:
            ArrayPropertyType subArray=(ArrayPropertyType)pb.getPropertyType();
            Assert.AreEqual(expected.Type,subArray.getItems().getType(),"Json: {0}",expected.Json);
            break;
            case Object:
            ObjectPropertyType subObj=(ObjectPropertyType)pb.getPropertyType();
            Assert.AreEqual(expected.Len,subObj.getProperties().size(),"Json: {0}",expected.Json);
            break;
            case Schema:
            UdtPropertyType subRow=(UdtPropertyType)pb.getPropertyType();
            Assert.AreEqual(expected.Name,subRow.getName(),"Json: {0}",expected.Json);
            break;
default:
    Assert.AreEqual(expected.Type,pb.getPropertyType().getType(),"Json: {0}",expected.Json);
    break;
    }
    }
    catch(RuntimeException ex)
    {
    Assert.Fail("Exception: {0}, Json: {1}",ex,expected.Json);
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaPartitionPrimaryKeys()
public final void ParseSchemaPartitionPrimaryKeys()
    {

// Test parsing both partition and primary sort keys.
class AnonymousType {
    public Object[] CK;

    pulic String JsonCK
    public String JsonPK;
    public String[] PK;
    pubblic AnonymousType(String _JsonPK, String _JsonCK, String[] _PK, Object[] _CK) {
        JsonPK = _JsonPK;
        JsonCK = _JsonCK;
        PK = _PK;
        CK = _CK;
    }
}

class AnonymousType2 {
    public SortDirection Dir;

    pulic String Path

    pubblic AnonymousType2(String _Path, SortDirection _Dir) {
        Path = _Path;
        Dir = _Dir;
    }
}

class AnonymousType3 {
    public SortDirection Dir;

    pulic String Path
    pubblct[] expectedSchemas = new Object[] { AnonymousType("{'path': 'a'}", "{'path': 'b', 'direction': 'desc'}, " +
        "{'path': 'c'}", new String[] { "a" }, new Object[] { AnonymousType2("b", SortDirection.Descending),
        AnonymousType3("c", SortDirection.Ascending) }) };

		for(bjeic AnonymousType3(String _Path, SortDirection _Dir) {
        Path = _Path;
        Dir = _Dir;
    }
}
    OObject expected:expectedSchemas)
            {
            String tableSchema=String.format("{{"+"\r\n"+
            "                    'name': 'table', "+"\r\n"+
            "                    'id': -3,"+"\r\n"+
            "                    'type': 'schema', "+"\r\n"+
            "                    'properties': ["+"\r\n"+
            "                        {{'path': 'a', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},")+"\r\n"+
            "                        {{'path': 'b', 'type': {{'type': 'utf8', 'storage': 'variable', 'length': 2}}}},"
            +"\r\n"+
            "                        {{'path': 'c', 'type': {{'type': 'datetime', 'storage': 'fixed'}}}},"+"\r\n"+
            "                        ],"+"\r\n"+
            "                    'partitionkeys': [{expected.JsonPK}],"+"\r\n"+
            "                    'primarykeys': [{expected.JsonCK}]"+"\r\n"+
            "                    }}";

            try
            {
            Schema s=Schema.Parse(tableSchema);
            Assert.AreEqual(3,s.getProperties().size(),"PK: {0}, CK: {1}",expected.JsonPK,expected.JsonCK);
            for(int i=0;i<s.getPartitionKeys().size();i++)
    {
    Assert.AreEqual(expected.PK[i],s.getPartitionKeys().get(i).getPath(),"PK: {0}, CK: {1}",expected.JsonPK,
    expected.JsonCK);
    }

    for(int i=0;i<s.getPrimarySortKeys().size();i++)
    {
    Assert.AreEqual(expected.CK[i].Path,s.getPrimarySortKeys().get(i).getPath(),"PK: {0}, CK: {1}",expected.JsonPK,
    expected.JsonCK);
    Assert.AreEqual(expected.CK[i].Dir,s.getPrimarySortKeys().get(i).getDirection(),"PK: {0}, CK: {1}",
    expected.JsonPK,expected.JsonCK);
    }
    }
    catch(RuntimeException ex)
    {
    Assert.Fail("Exception: {0}, PK: {1}, CK: {2}",ex,expected.JsonPK,expected.JsonCK);
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("vahemesw")] public void ParseSchemaStaticKeys()
public final void ParseSchemaStaticKeys()
    {

class AnonymousType {
    public String JsonStaticKeys;
    public int NumberOfPaths;
    public Object[] StaticKeys;

    public AnonymousType(int _NumberOfPaths, String _JsonStaticKeys, Object[] _StaticKeys) {
        NumberOfPaths = _NumberOfPaths;
        JsonStaticKeys = _JsonStaticKeys;
        StaticKeys = _StaticKeys;
    }
}

class AnonymousType2 {
    public String Path;

    public AnonymousType2(String _Path) {
        Path = _Path;
    }
}

class AnonymousType3 {
    public String JsonStaticKeys;
    public int NumberOfPaths;
    public Object[] StaticKeys;

    public AnonymousType3(int _NumberOfPaths, String _JsonStaticKeys, Object[] _StaticKeys) {
        NumberOfPaths = _NumberOfPaths;
        JsonStaticKeys = _JsonStaticKeys;
        StaticKeys = _StaticKeys;
    }
}

class AnonymousType4 {
    public String Path;

    public AnonymousType4(String _Path) {
        Path = _Path;
    }
}

class AnonymousType5 {
    public String Path;

    publct[] expectedSchemas = new Object[] { AnonymousType(1, "{'path': 'c'}", new Object[] { AnonymousType2("c") })
        , AnonymousType3(2, "{'path': 'c'}, {'path': 'd'}",
        new Object[] { AnonymousType4("c"), AnonymousType5("d") }) };

		for(bjeic AnonymousType5(String _Path) {
        Path = _Path;
    }
}
    OObject expected:expectedSchemas)
            {
            String tableSchema=String.format("{{"+"\r\n"+
            "                    'name': 'table', "+"\r\n"+
            "                    'id': -3,"+"\r\n"+
            "                    'type': 'schema', "+"\r\n"+
            "                    'properties': ["+"\r\n"+
            "                        {{'path': 'a', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},")+"\r\n"+
            "                        {{'path': 'b', 'type': {{'type': 'utf8', 'storage': 'variable', 'length': 2}}}},"
            +"\r\n"+
            "                        {{'path': 'c', 'type': {{'type': 'datetime', 'storage': 'fixed'}}}},"+"\r\n"+
            "                        {{'path': 'd', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},"+"\r\n"+
            "                        ],"+"\r\n"+
            "                    'partitionkeys': [{{'path': 'a'}}],"+"\r\n"+
            "                    'primarykeys': [{{'path': 'b', 'direction': 'desc'}}],"+"\r\n"+
            "                    'statickeys': [{expected.JsonStaticKeys}]"+"\r\n"+
            "                    }}";

            try
            {
            Schema s=Schema.Parse(tableSchema);
            assert expected.NumberOfPaths==s.getStaticKeys().size();
            for(int i=0;i<s.getStaticKeys().size();i++)
    {
    assert expected.StaticKeys[i].Path==s.getStaticKeys().get(i).getPath();
    }
    }
    catch(RuntimeException ex)
    {
    Assert.Fail("Exception: {0}, Caught exception when deserializing the schema",ex);
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ParseSchemaApiType()
public final void ParseSchemaApiType()
    {

// Test api type specifications include elements of complex types.
class AnonymousType {
    public String ApiType;

    pulic String Json

    pubblic AnonymousType(String _Json, String _ApiType) {
        Json = _Json;
        ApiType = _ApiType;
    }
}

class AnonymousType2 {
    public String ApiType;

    pulic String Json
    pubblct[] expectedSchemas = new Object[] { AnonymousType("{'type': 'int64', 'apitype': 'counter'}", "counter"),
        AnonymousType2("{'type': 'array', 'items': {'type': 'int64', 'apitype': 'timestamp'}}", "timestamp") };

		for(bjeic AnonymousType2(String _Json, String _ApiType) {
        Json = _Json;
        ApiType = _ApiType;
    }
}
    OObject expected:expectedSchemas)
            {
            String columnSchema=String.format("{'path': 'a', 'type': %1$s",expected.Json}});
            String tableSchema=String.format("{'name': 'table', 'id': -4, 'type': 'schema', 'properties': [%1$s]}",
            columnSchema);
            Schema s=Schema.Parse(tableSchema);
            Assert.AreEqual(1,s.getProperties().size(),"Json: {0}",expected.Json);
            Property p=s.getProperties().get(0);
            switch(p.getPropertyType().getType())
            {
            case Array:
            ArrayPropertyType subArray=(ArrayPropertyType)p.getPropertyType();
            Assert.AreEqual(expected.ApiType,subArray.getItems().getApiType(),"Json: {0}",expected.Json);
            break;
default:
    Assert.AreEqual(expected.ApiType,p.getPropertyType().getApiType(),"Json: {0}",expected.Json);
    break;
    }
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void SchemaRef()
public final void SchemaRef()
    {
    NamespaceParserTest tempVar=new NamespaceParserTest();
    tempVar.setName("SchemaNameOnlyRef");
    tempVar.setJson("{'schemas': [ { 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 2, 'type': " +
    "'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A'}} ]} ]}");
    NamespaceParserTest tempVar2=new NamespaceParserTest();
    tempVar2.setName("SchemaNameAndIdRef");
    tempVar2.setJson("{'schemas': [ { 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 2, 'type': " +
    "'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 1}} ]} ]}");
    NamespaceParserTest tempVar3=new NamespaceParserTest();
    tempVar3.setName("SchemaMultipleVersionNameAndIdRef");
    tempVar3.setJson("{'schemas': [ { 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 2, 'type': " +
    "'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 3}} ]}, { 'name': 'A', " +
    "'id': 3, 'type': 'schema'} ]}");
    NamespaceParserTest[]tests=new NamespaceParserTest[]{tempVar,tempVar2,tempVar3};

    for(NamespaceParserTest t:tests)
    {
    System.out.println(t.getName());
    Namespace.Parse(t.getJson());
    }
    }

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void NegativeNamespaceParser()
public final void NegativeNamespaceParser()
    {
    NamespaceParserTest tempVar=new NamespaceParserTest();
    tempVar.setName("InvalidId");
    tempVar.setJson("{'schemas': [{ 'name': 'A', 'id': 0, 'type': 'schema'}]}");
    NamespaceParserTest tempVar2=new NamespaceParserTest();
    tempVar2.setName("InvalidNameEmpty");
    tempVar2.setJson("{'schemas': [{ 'name': '', 'id': 1, 'type': 'schema'}]}");
    NamespaceParserTest tempVar3=new NamespaceParserTest();
    tempVar3.setName("InvalidNameWhitespace");
    tempVar3.setJson("{'schemas': [{ 'name': '  ', 'id': 1, 'type': 'schema'}]}");
    NamespaceParserTest tempVar4=new NamespaceParserTest();
    tempVar4.setName("DuplicateId");
    tempVar4.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 1, 'type': " +
    "'schema'} ]}");
    NamespaceParserTest tempVar5=new NamespaceParserTest();
    tempVar5.setName("DuplicatePropertyName");
    tempVar5.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': " +
    "{'type': 'bool'}}, {'path': 'b', 'type': {'type': 'int8'}}, ]}]}");
    NamespaceParserTest tempVar6=new NamespaceParserTest();
    tempVar6.setName("MissingPK");
    tempVar6.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'partitionkeys': [{'path': 'b'}]}]}");
    NamespaceParserTest tempVar7=new NamespaceParserTest();
    tempVar7.setName("MissingPS");
    tempVar7.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'primarykeys': [{'path': 'b'}]}]}");
    NamespaceParserTest tempVar8=new NamespaceParserTest();
    tempVar8.setName("MissingStaticKey");
    tempVar8.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'statickeys': [{'path': 'b'}]}]}");
    NamespaceParserTest tempVar9=new NamespaceParserTest();
    tempVar9.setName("InvalidPropertyName");
    tempVar9.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [{'path': '', 'type': {'type': 'bool'}}]}]}");
    NamespaceParserTest tempVar10=new NamespaceParserTest();
    tempVar10.setName("InvalidLength");
    tempVar10.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'utf8', 'length': -1}} ]}]}");
    NamespaceParserTest tempVar11=new NamespaceParserTest();
    tempVar11.setName("InvalidStorage");
    tempVar11.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'array', 'items': {'type': 'utf8', 'storage': 'fixed'}}} ]}]}");
    NamespaceParserTest tempVar12=new NamespaceParserTest();
    tempVar12.setName("DuplicateObjectProperties");
    tempVar12.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'object', 'properties': [ {'path': 'c', 'type': {'type': 'bool'}}, {'path': 'c', 'type': {'type': 'int8'}} ]}} ]}]}");
    NamespaceParserTest tempVar13=new NamespaceParserTest();
    tempVar13.setName("MissingUDTName");
    tempVar13.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'B'}} ]}]}");
    NamespaceParserTest tempVar14=new NamespaceParserTest();
    tempVar14.setName("MissingUDTId");
    tempVar14.setJson("{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 3}} ]}]}");
    NamespaceParserTest tempVar15=new NamespaceParserTest();
    tempVar15.setName("MismatchedSchemaRef");
    tempVar15.setJson("{'schemas': [ { 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 2}} ]} ]}");
    NamespaceParserTest tempVar16=new NamespaceParserTest();
    tempVar16.setName("AmbiguousSchemaRef");
    tempVar16.setJson("{'schemas': [ { 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [ {'path': 'b', 'type': {'type': 'schema', 'name': 'A'}} ]}, { 'name': 'A', 'id': 3, 'type': 'schema'} ]}");
    NamespaceParserTest[]tests=new NamespaceParserTest[]{tempVar,tempVar2,tempVar3,tempVar4,tempVar5,tempVar6,tempVar7,tempVar8,tempVar9,tempVar10,tempVar11,tempVar12,tempVar13,tempVar14,tempVar15,tempVar16};

    for(NamespaceParserTest t:tests)
    {
    System.out.println(t.getName());
    AssertThrowsException.<SchemaException>ThrowsException(()->Namespace.Parse(t.getJson()));
    }
    }

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ from the original:
//ORIGINAL LINE: private struct NamespaceParserTest
private final static class NamespaceParserTest {
    private String Name;

    publblivate String Json

    publ
ic String getName() {
        return Name;
    }

    puic String getJson() {
        return Json;
    }

    publ
ic void setName(String value) {
        Name = value;
    }

    pric void setJson(String value) {
        Json = value;
    }
}
}