// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft.Json;

    [TestClass]
    [SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here are anonymous.")]
    public class SchemaUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void ParseNamespace()
        {
            // Test empty schemas.
            {
                string[] emptyNamespaceJsonInput =
                {
                    @"{ }",
                    @"{'schemas': [ ] }",
                    @"{'name': null }",
                    @"{'name': null, 'schemas': null }",
                    @"{'version': 'v1', 'name': null, 'schemas': null }",
                };

                foreach (string json in emptyNamespaceJsonInput)
                {
                    Namespace n1 = Namespace.Parse(json);
                    Assert.IsNull(n1.Name, "Got: {0}, Json: {1}", n1.Name, json);
                    Assert.IsNotNull(n1.Schemas, "Json: {0}", json);
                    Assert.AreEqual(0, n1.Schemas.Count, "Got: {0}, Json: {1}", n1.Schemas, json);
                    Assert.AreEqual(SchemaLanguageVersion.V1, n1.Version);
                }
            }

            // Test simple schemas and schema options.
            {
                string json = @"{'name': 'myschema', 'schemas': null }";
                Namespace n1 = Namespace.Parse(json);
                Assert.AreEqual("myschema", n1.Name, "Json: {0}", json);
                Assert.IsNotNull(n1.Schemas, "Json: {0}", json);

                // Version defaults propertly when NOT specified.
                Assert.AreEqual(SchemaLanguageVersion.V1, n1.Version);
            }

            {
                string json = @"{'name': 'myschema', 'schemas': [
                    {'version': 'v1', 'name': 'emptyTable', 'id': -1, 'type': 'schema', 
                     'options': { 'disallowUnschematized': true }, 'properties': null } ] }";

                Namespace n1 = Namespace.Parse(json);
                Assert.AreEqual("myschema", n1.Name, "Json: {0}", json);
                Assert.AreEqual(1, n1.Schemas.Count, "Json: {0}", json);
                Assert.AreEqual("emptyTable", n1.Schemas[0].Name, "Json: {0}", json);
                Assert.AreEqual(new SchemaId(-1), n1.Schemas[0].SchemaId, "Json: {0}", json);
                Assert.AreEqual(TypeKind.Schema, n1.Schemas[0].Type, "Json: {0}", json);
                Assert.AreEqual(true, n1.Schemas[0].Options.DisallowUnschematized, "Json: {0}", json);
                Assert.IsNotNull(n1.Schemas[0].Properties.Count, "Json: {0}", json);
                Assert.AreEqual(0, n1.Schemas[0].Properties.Count, "Json: {0}", json);
                Assert.AreEqual(SchemaLanguageVersion.V1, n1.Version);
            }

            // Test basic schema with primitive columns.
            {
                string json = @"{'name': 'myschema', 'schemas': [
                    {'name': 'myUDT', 'id': 1, 'type': 'schema', 'options': { 'disallowUnschematized': false }, 
                     'properties': [ 
                        { 'path': 'a', 'type': { 'type': 'int8', 'storage': 'fixed' }}, 
                        { 'path': 'b', 'type': { 'type': 'utf8', 'storage': 'variable' }} 
                     ] }
                    ] }";

                Namespace n1 = Namespace.Parse(json);
                Assert.AreEqual(1, n1.Schemas.Count, "Json: {0}", json);
                Assert.AreEqual("myUDT", n1.Schemas[0].Name, "Json: {0}", json);
                Assert.AreEqual(new SchemaId(1), n1.Schemas[0].SchemaId, "Json: {0}", json);
                Assert.AreEqual(TypeKind.Schema, n1.Schemas[0].Type, "Json: {0}", json);
                Assert.AreEqual(false, n1.Schemas[0].Options.DisallowUnschematized, "Json: {0}", json);
                Assert.AreEqual(2, n1.Schemas[0].Properties.Count, "Json: {0}", json);

                var expectedProps = new[]
                {
                    new { Path = "a", Type = TypeKind.Int8, Storage = StorageKind.Fixed },
                    new { Path = "b", Type = TypeKind.Utf8, Storage = StorageKind.Variable },
                };

                for (int i = 0; i < n1.Schemas[0].Properties.Count; i++)
                {
                    Property p = n1.Schemas[0].Properties[i];
                    Assert.AreEqual(expectedProps[i].Path, p.Path, "Json: {0}", json);
                    Assert.AreEqual(expectedProps[i].Type, p.PropertyType.Type, "Json: {0}", json);
                    PrimitivePropertyType sp = (PrimitivePropertyType)p.PropertyType;
                    Assert.AreEqual(expectedProps[i].Storage, sp.Storage, "Json: {0}", json);
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(@"TestData\CoverageSchema.json", "TestData")]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(@"TestData\CoverageSchema.json");
            Namespace n1 = Namespace.Parse(json);
            JsonSerializerSettings settings = new JsonSerializerSettings()
            {
                NullValueHandling = NullValueHandling.Ignore,
                Formatting = Formatting.Indented,
            };

            string json2 = JsonConvert.SerializeObject(n1, settings);
            Namespace n2 = Namespace.Parse(json2);
            string json3 = JsonConvert.SerializeObject(n2, settings);
            Assert.AreEqual(json2, json3);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaPrimitives()
        {
            // Test all primitive column types.
            dynamic[] expectedSchemas =
            {
                new { Json = @"{'type': 'bool', 'storage': 'fixed'}", Type = TypeKind.Boolean },

                new { Json = @"{'type': 'int8', 'storage': 'fixed'}", Type = TypeKind.Int8 },
                new { Json = @"{'type': 'int16', 'storage': 'fixed'}", Type = TypeKind.Int16 },
                new { Json = @"{'type': 'int32', 'storage': 'fixed'}", Type = TypeKind.Int32 },
                new { Json = @"{'type': 'int64', 'storage': 'fixed'}", Type = TypeKind.Int64 },
                new { Json = @"{'type': 'uint8', 'storage': 'fixed'}", Type = TypeKind.UInt8 },
                new { Json = @"{'type': 'uint16', 'storage': 'fixed'}", Type = TypeKind.UInt16 },
                new { Json = @"{'type': 'uint32', 'storage': 'fixed'}", Type = TypeKind.UInt32 },
                new { Json = @"{'type': 'uint64', 'storage': 'fixed'}", Type = TypeKind.UInt64 },

                new { Json = @"{'type': 'float32', 'storage': 'fixed'}", Type = TypeKind.Float32 },
                new { Json = @"{'type': 'float64', 'storage': 'fixed'}", Type = TypeKind.Float64 },
                new { Json = @"{'type': 'float128', 'storage': 'fixed'}", Type = TypeKind.Float128 },
                new { Json = @"{'type': 'decimal', 'storage': 'fixed'}", Type = TypeKind.Decimal },

                new { Json = @"{'type': 'datetime', 'storage': 'fixed'}", Type = TypeKind.DateTime },
                new { Json = @"{'type': 'unixdatetime', 'storage': 'fixed'}", Type = TypeKind.UnixDateTime },

                new { Json = @"{'type': 'guid', 'storage': 'fixed'}", Type = TypeKind.Guid },
                new { Json = @"{'type': 'mongodbobjectid', 'storage': 'fixed'}", Type = TypeKind.MongoDbObjectId },

                new { Json = @"{'type': 'varint', 'storage': 'variable'}", Type = TypeKind.VarInt },
                new { Json = @"{'type': 'varuint', 'storage': 'variable'}", Type = TypeKind.VarUInt },

                new { Json = @"{'type': 'utf8', 'storage': 'fixed', 'length': 2}", Type = TypeKind.Utf8, Len = 2 },
                new { Json = @"{'type': 'binary', 'storage': 'fixed', 'length': 2}", Type = TypeKind.Binary, Len = 2 },
                new { Json = @"{'type': 'utf8', 'storage': 'variable', 'length': 100}", Type = TypeKind.Utf8, Len = 100 },
                new { Json = @"{'type': 'binary', 'storage': 'variable', 'length': 100}", Type = TypeKind.Binary, Len = 100 },
                new { Json = @"{'type': 'utf8', 'sparse': 'variable', 'length': 1000}", Type = TypeKind.Utf8, Len = 1000 },
                new { Json = @"{'type': 'binary', 'sparse': 'variable', 'length': 1000}", Type = TypeKind.Binary, Len = 1000 },
            };

            foreach (dynamic expected in expectedSchemas)
            {
                string columnSchema = $"{{'path': 'a', 'type': {expected.Json}}}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{columnSchema}]}}";
                Schema s = Schema.Parse(tableSchema);
                Assert.AreEqual(1, s.Properties.Count, "Json: {0}", expected.Json);
                Property p = s.Properties[0];
                Assert.AreEqual(expected.Type, p.PropertyType.Type, "Json: {0}", expected.Json);
                PrimitivePropertyType sp = (PrimitivePropertyType)p.PropertyType;
                switch (p.PropertyType.Type)
                {
                    case TypeKind.Utf8:
                    case TypeKind.Binary:
                        switch (sp.Storage)
                        {
                            case StorageKind.Fixed:
                            case StorageKind.Variable:
                            case StorageKind.Sparse:
                                Assert.AreEqual(expected.Len, sp.Length, "Json: {0}", expected.Json);
                                break;
                            default:
                                Assert.Fail("Json: {0}", expected.Json);
                                break;
                        }

                        break;
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaArray()
        {
            // Test array types include nested arrays.
            dynamic[] expectedSchemas =
            {
                new { Json = @"{'type': 'int8' }", Type = TypeKind.Int8 },
                new { Json = @"{'type': 'array', 'items': {'type': 'int32'}}", Type = TypeKind.Int32 },
                new { Json = @"{'type': 'object', 'properties': null}", Len = 0 },
                new { Json = @"{'type': 'schema', 'name': 'myUDT'}", Name = "myUDT" },
            };

            foreach (dynamic expected in expectedSchemas)
            {
                string arrayColumnSchema = $"{{'path': 'a', 'type': {{'type': 'array', 'items': {expected.Json} }} }}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{arrayColumnSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                Assert.AreEqual(1, s.Properties.Count, "Json: {0}", expected.Json);
                Property p = s.Properties[0];
                Assert.AreEqual(TypeKind.Array, p.PropertyType.Type, "Json: {0}", expected.Json);
                ArrayPropertyType pt = (ArrayPropertyType)p.PropertyType;
                Assert.IsNotNull(pt.Items, "Json: {0}", expected.Json);
                switch (pt.Items.Type)
                {
                    case TypeKind.Array:
                        ArrayPropertyType subArray = (ArrayPropertyType)pt.Items;
                        Assert.AreEqual(expected.Type, subArray.Items.Type, "Json: {0}", expected.Json);
                        break;
                    case TypeKind.Object:
                        ObjectPropertyType subObj = (ObjectPropertyType)pt.Items;
                        Assert.AreEqual(expected.Len, subObj.Properties.Count, "Json: {0}", expected.Json);
                        break;
                    case TypeKind.Schema:
                        UdtPropertyType subRow = (UdtPropertyType)pt.Items;
                        Assert.AreEqual(expected.Name, subRow.Name, "Json: {0}", expected.Json);
                        break;
                    default:
                        Assert.AreEqual(expected.Type, pt.Items.Type, "Json: {0}", expected.Json);
                        break;
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaObject()
        {
            // Test object types include nested objects.
            dynamic[] expectedSchemas =
            {
                new { Json = @"{'path': 'b', 'type': {'type': 'int8', 'storage': 'fixed'}}", Type = TypeKind.Int8 },
                new { Json = @"{'path': 'b', 'type': {'type': 'array', 'items': {'type': 'int32'}}}", Type = TypeKind.Int32 },
                new { Json = @"{'path': 'b', 'type': {'type': 'object', 'properties': [{'path': 'c', 'type': {'type': 'bool'}}]}}", Len = 1 },
                new { Json = @"{'path': 'b', 'type': {'type': 'schema', 'name': 'myUDT'}}", Name = "myUDT" },
            };

            foreach (dynamic expected in expectedSchemas)
            {
                string objectColumnSchema = $"{{'path': 'a', 'type': {{'type': 'object', 'properties': [{expected.Json}] }} }}";
                string tableSchema = $"{{'name': 'table', 'id': -2, 'type': 'schema', 'properties': [{objectColumnSchema}] }}";
                try
                {
                    Schema s = Schema.Parse(tableSchema);
                    Assert.AreEqual(1, s.Properties.Count, "Json: {0}", expected.Json);
                    Property pa = s.Properties[0];
                    Assert.AreEqual(TypeKind.Object, pa.PropertyType.Type, "Json: {0}", expected.Json);
                    ObjectPropertyType pt = (ObjectPropertyType)pa.PropertyType;
                    Assert.AreEqual(1, pt.Properties.Count, "Json: {0}", expected.Json);
                    Property pb = pt.Properties[0];
                    switch (pb.PropertyType.Type)
                    {
                        case TypeKind.Array:
                            ArrayPropertyType subArray = (ArrayPropertyType)pb.PropertyType;
                            Assert.AreEqual(expected.Type, subArray.Items.Type, "Json: {0}", expected.Json);
                            break;
                        case TypeKind.Object:
                            ObjectPropertyType subObj = (ObjectPropertyType)pb.PropertyType;
                            Assert.AreEqual(expected.Len, subObj.Properties.Count, "Json: {0}", expected.Json);
                            break;
                        case TypeKind.Schema:
                            UdtPropertyType subRow = (UdtPropertyType)pb.PropertyType;
                            Assert.AreEqual(expected.Name, subRow.Name, "Json: {0}", expected.Json);
                            break;
                        default:
                            Assert.AreEqual(expected.Type, pb.PropertyType.Type, "Json: {0}", expected.Json);
                            break;
                    }
                }
                catch (Exception ex)
                {
                    Assert.Fail("Exception: {0}, Json: {1}", ex, expected.Json);
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaPartitionPrimaryKeys()
        {
            // Test parsing both partition and primary sort keys.
            var expectedSchemas = new[]
            {
                new
                {
                    JsonPK = @"{'path': 'a'}",
                    JsonCK = @"{'path': 'b', 'direction': 'desc'}, {'path': 'c'}",
                    PK = new[] { "a" },
                    CK = new[] { new { Path = "b", Dir = SortDirection.Descending }, new { Path = "c", Dir = SortDirection.Ascending } },
                },
            };

            foreach (var expected in expectedSchemas)
            {
                string tableSchema = $@"{{
                    'name': 'table', 
                    'id': -3,
                    'type': 'schema', 
                    'properties': [
                        {{'path': 'a', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},
                        {{'path': 'b', 'type': {{'type': 'utf8', 'storage': 'variable', 'length': 2}}}},
                        {{'path': 'c', 'type': {{'type': 'datetime', 'storage': 'fixed'}}}},
                        ],
                    'partitionkeys': [{expected.JsonPK}],
                    'primarykeys': [{expected.JsonCK}]
                    }}";

                try
                {
                    Schema s = Schema.Parse(tableSchema);
                    Assert.AreEqual(3, s.Properties.Count, "PK: {0}, CK: {1}", expected.JsonPK, expected.JsonCK);
                    for (int i = 0; i < s.PartitionKeys.Count; i++)
                    {
                        Assert.AreEqual(expected.PK[i], s.PartitionKeys[i].Path, "PK: {0}, CK: {1}", expected.JsonPK, expected.JsonCK);
                    }

                    for (int i = 0; i < s.PrimarySortKeys.Count; i++)
                    {
                        Assert.AreEqual(expected.CK[i].Path, s.PrimarySortKeys[i].Path, "PK: {0}, CK: {1}", expected.JsonPK, expected.JsonCK);
                        Assert.AreEqual(expected.CK[i].Dir, s.PrimarySortKeys[i].Direction, "PK: {0}, CK: {1}", expected.JsonPK, expected.JsonCK);
                    }
                }
                catch (Exception ex)
                {
                    Assert.Fail("Exception: {0}, PK: {1}, CK: {2}", ex, expected.JsonPK, expected.JsonCK);
                }
            }
        }

        [TestMethod]
        [Owner("vahemesw")]
        public void ParseSchemaStaticKeys()
        {
            var expectedSchemas = new[]
            {
                new
                {
                    NumberOfPaths = 1,
                    JsonStaticKeys = @"{'path': 'c'}",
                    StaticKeys = new[] { new { Path = "c" } },
                },
                new
                {
                    NumberOfPaths = 2,
                    JsonStaticKeys = @"{'path': 'c'}, {'path': 'd'}",
                    StaticKeys = new[] { new { Path = "c" }, new { Path = "d" } },
                },
            };

            foreach (var expected in expectedSchemas)
            {
                string tableSchema = $@"{{
                    'name': 'table', 
                    'id': -3,
                    'type': 'schema', 
                    'properties': [
                        {{'path': 'a', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},
                        {{'path': 'b', 'type': {{'type': 'utf8', 'storage': 'variable', 'length': 2}}}},
                        {{'path': 'c', 'type': {{'type': 'datetime', 'storage': 'fixed'}}}},
                        {{'path': 'd', 'type': {{'type': 'int8', 'storage': 'fixed'}}}},
                        ],
                    'partitionkeys': [{{'path': 'a'}}],
                    'primarykeys': [{{'path': 'b', 'direction': 'desc'}}],
                    'statickeys': [{expected.JsonStaticKeys}]
                    }}";

                try
                {
                    Schema s = Schema.Parse(tableSchema);
                    Assert.AreEqual(expected.NumberOfPaths, s.StaticKeys.Count);
                    for (int i = 0; i < s.StaticKeys.Count; i++)
                    {
                        Assert.AreEqual(expected.StaticKeys[i].Path, s.StaticKeys[i].Path);
                    }
                }
                catch (Exception ex)
                {
                    Assert.Fail("Exception: {0}, Caught exception when deserializing the schema", ex);
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaApiType()
        {
            // Test api type specifications include elements of complex types.
            var expectedSchemas = new[]
            {
                new { Json = @"{'type': 'int64', 'apitype': 'counter'}", ApiType = "counter" },
                new { Json = @"{'type': 'array', 'items': {'type': 'int64', 'apitype': 'timestamp'}}", ApiType = "timestamp" },
            };

            foreach (var expected in expectedSchemas)
            {
                string columnSchema = $"{{'path': 'a', 'type': {expected.Json}}}";
                string tableSchema = $"{{'name': 'table', 'id': -4, 'type': 'schema', 'properties': [{columnSchema}]}}";
                Schema s = Schema.Parse(tableSchema);
                Assert.AreEqual(1, s.Properties.Count, "Json: {0}", expected.Json);
                Property p = s.Properties[0];
                switch (p.PropertyType.Type)
                {
                    case TypeKind.Array:
                        ArrayPropertyType subArray = (ArrayPropertyType)p.PropertyType;
                        Assert.AreEqual(expected.ApiType, subArray.Items.ApiType, "Json: {0}", expected.Json);
                        break;
                    default:
                        Assert.AreEqual(expected.ApiType, p.PropertyType.ApiType, "Json: {0}", expected.Json);
                        break;
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void SchemaRef()
        {
            NamespaceParserTest[] tests = new NamespaceParserTest[]
            {
                new NamespaceParserTest
                {
                    Name = "SchemaNameOnlyRef",
                    Json = @"{'schemas': [
                                { 'name': 'A', 'id': 1, 'type': 'schema'}, 
                                { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [
                                    {'path': 'b', 'type': {'type': 'schema', 'name': 'A'}}
                                    ]}
                                ]}",
                },
                new NamespaceParserTest
                {
                    Name = "SchemaNameAndIdRef",
                    Json = @"{'schemas': [
                                { 'name': 'A', 'id': 1, 'type': 'schema'}, 
                                { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [
                                    {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 1}}
                                    ]}
                                ]}",
                },
                new NamespaceParserTest
                {
                    Name = "SchemaMultipleVersionNameAndIdRef",
                    Json = @"{'schemas': [
                                { 'name': 'A', 'id': 1, 'type': 'schema'}, 
                                { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [
                                    {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 3}}
                                    ]},
                                { 'name': 'A', 'id': 3, 'type': 'schema'}
                                ]}",
                },
            };

            foreach (NamespaceParserTest t in tests)
            {
                Console.WriteLine(t.Name);
                Namespace.Parse(t.Json);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void NegativeNamespaceParser()
        {
            NamespaceParserTest[] tests = new NamespaceParserTest[]
            {
                new NamespaceParserTest
                {
                    Name = "InvalidId",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 0, 'type': 'schema'}]}",
                },
                new NamespaceParserTest
                {
                    Name = "InvalidNameEmpty",
                    Json = @"{'schemas': [{ 'name': '', 'id': 1, 'type': 'schema'}]}",
                },
                new NamespaceParserTest
                {
                    Name = "InvalidNameWhitespace",
                    Json = @"{'schemas': [{ 'name': '  ', 'id': 1, 'type': 'schema'}]}",
                },
                new NamespaceParserTest
                {
                    Name = "DuplicateId",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema'}, { 'name': 'B', 'id': 1, 'type': 'schema'} ]}",
                },
                new NamespaceParserTest
                {
                    Name = "DuplicatePropertyName",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'bool'}},
                                {'path': 'b', 'type': {'type': 'int8'}},
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MissingPK",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'partitionkeys': [{'path': 'b'}]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MissingPS",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'primarykeys': [{'path': 'b'}]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MissingStaticKey",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'statickeys': [{'path': 'b'}]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "InvalidPropertyName",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [{'path': '', 'type': {'type': 'bool'}}]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "InvalidLength",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'utf8', 'length': -1}}
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "InvalidStorage",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'array', 'items': {'type': 'utf8', 'storage': 'fixed'}}}
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "DuplicateObjectProperties",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'object', 'properties': [
                                    {'path': 'c', 'type': {'type': 'bool'}},
                                    {'path': 'c', 'type': {'type': 'int8'}}
                                    ]}}
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MissingUDTName",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'schema', 'name': 'B'}}
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MissingUDTId",
                    Json = @"{'schemas': [{ 'name': 'A', 'id': 1, 'type': 'schema', 'properties': [
                                {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 3}}
                                ]}]}",
                },
                new NamespaceParserTest
                {
                    Name = "MismatchedSchemaRef",
                    Json = @"{'schemas': [
                                { 'name': 'A', 'id': 1, 'type': 'schema'}, 
                                { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [
                                    {'path': 'b', 'type': {'type': 'schema', 'name': 'A', 'id': 2}}
                                    ]}
                                ]}",
                },
                new NamespaceParserTest
                {
                    Name = "AmbiguousSchemaRef",
                    Json = @"{'schemas': [
                                { 'name': 'A', 'id': 1, 'type': 'schema'}, 
                                { 'name': 'B', 'id': 2, 'type': 'schema', 'properties': [
                                    {'path': 'b', 'type': {'type': 'schema', 'name': 'A'}}
                                    ]},
                                { 'name': 'A', 'id': 3, 'type': 'schema'}
                                ]}",
                },
            };

            foreach (NamespaceParserTest t in tests)
            {
                Console.WriteLine(t.Name);
                AssertThrowsException.ThrowsException<SchemaException>(() => Namespace.Parse(t.Json));
            }
        }

        private struct NamespaceParserTest
        {
            public string Name { get; set; }

            public string Json { get; set; }
        }
    }
}
