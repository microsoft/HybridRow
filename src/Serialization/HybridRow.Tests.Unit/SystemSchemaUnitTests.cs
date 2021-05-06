// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Microsoft.VisualStudio.TestTools.UnitTesting.Logging;

    [TestClass]
    public class SystemSchemaUnitTests
    {
        private const string CrossVersioningSchema = @"TestData\CrossVersioningSchema.json";
        private const string CustomerSchema = @"TestData\CustomerSchema.json";
        private const string NullableSchema = @"TestData\NullableSchema.json";
        private const string ReaderSchema = @"TestData\ReaderSchema.json";
        private const string SchemaHashCoverageSchema = @"TestData\SchemaHashCoverageSchema.json";
        private const string CoverageSchema = @"TestData\CoverageSchema.json";
        private const string BatchApiSchema = @"TestData\BatchApiSchema.json";
        private const string TaggedApiSchema = @"TestData\TaggedApiSchema.json";
        private const string PerfCounterSchema = @"TestData\PerfCounterSchema.json";
        private const string TagSchema = @"TestData\TagSchema.json";
        private const string MovieSchema = @"TestData\MovieSchema.json";
        private const string SchemaTodoSchema = @"TestData\TodoSchema.json";

        [TestMethod]
        [Owner("jthunter")]
        public void LoadGeneratedHrSchema()
        {
            Layout layout = SchemasHrSchema.LayoutResolver.Resolve((SchemaId)SchemaOptionsHybridRowSerializer.SchemaId);
            Assert.AreEqual((SchemaId)SchemaOptionsHybridRowSerializer.SchemaId, layout.SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void LoadTest()
        {
            LayoutResolver resolver = SystemSchema.LayoutResolver;

            SchemaId[] systemSchemas =
            {
                SystemSchema.EmptySchemaId,
                (SchemaId)SegmentHybridRowSerializer.SchemaId,
                (SchemaId)RecordHybridRowSerializer.SchemaId,
                (SchemaId)NamespaceHybridRowSerializer.SchemaId,
                (SchemaId)SchemaHybridRowSerializer.SchemaId,
                (SchemaId)SchemaOptionsHybridRowSerializer.SchemaId,
                (SchemaId)PartitionKeyHybridRowSerializer.SchemaId,
                (SchemaId)PrimarySortKeyHybridRowSerializer.SchemaId,
                (SchemaId)StaticKeyHybridRowSerializer.SchemaId,
                (SchemaId)PropertyHybridRowSerializer.SchemaId,
                (SchemaId)PropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)PrimitivePropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)ScopePropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)ArrayPropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)ObjectPropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)SetPropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)MapPropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)TuplePropertyTypeHybridRowSerializer.SchemaId,
                (SchemaId)TaggedPropertyTypeHybridRowSerializer.SchemaId,
            };

            // Make sure all system schemas are loadable.
            foreach (SchemaId id in systemSchemas)
            {
                Layout l = resolver.Resolve(id);
                Assert.AreEqual(id, l.SchemaId);
            }

            // Make sure all system schema ids are unique.
            foreach (SchemaId id in systemSchemas)
            {
                int count = 0;
                foreach (SchemaId other in systemSchemas)
                {
                    if (other == id)
                    {
                        count++;
                    }
                }

                Assert.AreEqual(1, count);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void SerializeSystemNamespaceTest()
        {
            Namespace ns1 = SystemSchema.GetNamespace();
            SystemSchemaUnitTests.SerializerRoundtripNamespace(ns1);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(SystemSchemaUnitTests.CrossVersioningSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.CustomerSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.NullableSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.ReaderSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.SchemaHashCoverageSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.CoverageSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.BatchApiSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.TaggedApiSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.PerfCounterSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.TagSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.MovieSchema, "TestData")]
        [DeploymentItem(SystemSchemaUnitTests.SchemaTodoSchema, "TestData")]
        public void SerializeTestSchemasTest()
        {
            string[] files =
            {
                SystemSchemaUnitTests.CrossVersioningSchema,
                SystemSchemaUnitTests.CustomerSchema,
                SystemSchemaUnitTests.NullableSchema,
                SystemSchemaUnitTests.ReaderSchema,
                SystemSchemaUnitTests.SchemaHashCoverageSchema,
                SystemSchemaUnitTests.CoverageSchema,
                SystemSchemaUnitTests.BatchApiSchema,
                SystemSchemaUnitTests.TaggedApiSchema,
                SystemSchemaUnitTests.PerfCounterSchema,
                SystemSchemaUnitTests.TagSchema,
                SystemSchemaUnitTests.MovieSchema,
                SystemSchemaUnitTests.SchemaTodoSchema,
            };

            foreach (string filename in files)
            {
                Logger.LogMessage("Filename: {0}", filename);
                string json = File.ReadAllText(filename);
                Namespace ns1 = Namespace.Parse(json);
                SystemSchemaUnitTests.SerializerRoundtripNamespace(ns1);
            }
        }

        private static void SerializerRoundtripNamespace(Namespace ns1)
        {
            Layout layout = SystemSchema.LayoutResolver.Resolve((SchemaId)NamespaceHybridRowSerializer.SchemaId);

            RowBuffer row = new RowBuffer(0);
            row.InitLayout(HybridRowVersion.V1, layout, SystemSchema.LayoutResolver);

            // Write the whole namespace to a row.
            Result r = ns1.Write(ref row);
            ResultAssert.IsSuccess(r);

            // Read the namespace back.
            r = Namespace.Read(ref row, out Namespace ns2);
            ResultAssert.IsSuccess(r);

            // Compare the materialized row with the original in-memory object model.
            Assert.AreEqual(ns1.Version, ns2.Version);
            Assert.AreEqual(ns1.Name, ns2.Name);
            Assert.AreEqual(ns1.Comment, ns2.Comment);
            Assert.AreEqual(ns1.CppNamespace, ns2.CppNamespace);
            Assert.AreEqual(ns1.Schemas.Count, ns2.Schemas.Count);
            for (int i = 0; i < ns1.Schemas.Count; i++)
            {
                Schema s1 = ns1.Schemas[i];
                Schema s2 = ns2.Schemas[i];
                SystemSchemaUnitTests.AssertEqual(s1, s2);
            }
            Assert.AreEqual(ns1.Enums.Count, ns2.Enums.Count);
            for (int i = 0; i < ns1.Enums.Count; i++)
            {
                EnumSchema s1 = ns1.Enums[i];
                EnumSchema s2 = ns2.Enums[i];
                SystemSchemaUnitTests.AssertEqual(s1, s2);
            }
        }

        private static void AssertEqual(Schema s1, Schema s2)
        {
            Assert.AreEqual(s1.Version, s2.Version);
            Assert.AreEqual(s1.Type, s2.Type);
            Assert.AreEqual(s1.SchemaId, s2.SchemaId);
            Assert.AreEqual(s1.Name, s2.Name);
            Assert.AreEqual(s1.Comment, s2.Comment);
            Assert.AreEqual(s1.BaseName, s2.BaseName);
            Assert.AreEqual(s1.BaseSchemaId, s2.BaseSchemaId);

            if (s1.Options == null)
            {
                Assert.IsNull(s2.Options);
            }
            else
            {
                Assert.IsNotNull(s2.Options);
                Assert.AreEqual(s1.Options.DisallowUnschematized, s2.Options.DisallowUnschematized);
                Assert.AreEqual(s1.Options.EnablePropertyLevelTimestamp, s2.Options.EnablePropertyLevelTimestamp);
                Assert.AreEqual(s1.Options.DisableSystemPrefix, s2.Options.DisableSystemPrefix);
                Assert.AreEqual(s1.Options.Abstract, s2.Options.Abstract);
            }

            Assert.AreEqual(s1.PartitionKeys.Count, s2.PartitionKeys.Count);
            for (int i = 0; i < s1.PartitionKeys.Count; i++)
            {
                SystemSchemaUnitTests.AssertEqual(s1.PartitionKeys[i], s2.PartitionKeys[i]);
            }

            Assert.AreEqual(s1.PrimaryKeys.Count, s2.PrimaryKeys.Count);
            for (int i = 0; i < s1.PrimaryKeys.Count; i++)
            {
                SystemSchemaUnitTests.AssertEqual(s1.PrimaryKeys[i], s2.PrimaryKeys[i]);
            }

            Assert.AreEqual(s1.StaticKeys.Count, s2.StaticKeys.Count);
            for (int i = 0; i < s1.StaticKeys.Count; i++)
            {
                SystemSchemaUnitTests.AssertEqual(s1.StaticKeys[i], s2.StaticKeys[i]);
            }

            Assert.AreEqual(s1.Properties.Count, s2.Properties.Count);
            for (int i = 0; i < s1.Properties.Count; i++)
            {
                SystemSchemaUnitTests.AssertEqual(s1.Properties[i], s2.Properties[i]);
            }
        }

        private static void AssertEqual(PartitionKey i1, PartitionKey i2)
        {
            Assert.AreEqual(i1.Path, i2.Path);
        }

        private static void AssertEqual(PrimarySortKey i1, PrimarySortKey i2)
        {
            Assert.AreEqual(i1.Path, i2.Path);
            Assert.AreEqual(i1.Direction, i2.Direction);
        }

        private static void AssertEqual(StaticKey i1, StaticKey i2)
        {
            Assert.AreEqual(i1.Path, i2.Path);
        }

        private static void AssertEqual(Property i1, Property i2)
        {
            Assert.AreEqual(i1.Comment, i2.Comment);
            Assert.AreEqual(i1.Path, i2.Path);
            Assert.AreEqual(i1.ApiName, i2.ApiName);
            Assert.AreEqual(i1.AllowEmpty, i2.AllowEmpty);
            SystemSchemaUnitTests.AssertEqual(i1.PropertyType, i2.PropertyType);
        }

        private static void AssertEqual(PropertyType i1, PropertyType i2)
        {
            Assert.AreEqual(i1.ApiType, i2.ApiType);
            Assert.AreEqual(i1.Type, i2.Type);
            Assert.AreEqual(i1.Nullable, i2.Nullable);
            Assert.AreEqual(i1.GetType(), i2.GetType());

            switch (i1)
            {
                case PrimitivePropertyType p:
                    SystemSchemaUnitTests.AssertEqual(p, (PrimitivePropertyType)i2);
                    return;
                case ScopePropertyType p:
                    SystemSchemaUnitTests.AssertEqual(p, (ScopePropertyType)i2);
                    return;
                default:
                    Assert.Fail("Type is abstract.");
                    return;
            }
        }

        private static void AssertEqual(PrimitivePropertyType i1, PrimitivePropertyType i2)
        {
            Assert.AreEqual(i1.Length, i2.Length);
            Assert.AreEqual(i1.Storage, i2.Storage);
        }

        private static void AssertEqual(ScopePropertyType i1, ScopePropertyType i2)
        {
            Assert.AreEqual(i1.Immutable, i2.Immutable);

            switch (i1)
            {
                case ArrayPropertyType p:
                    SystemSchemaUnitTests.AssertEqual(p.Items, ((ArrayPropertyType)i2).Items);
                    break;
                case ObjectPropertyType p:
                {
                    ObjectPropertyType q = (ObjectPropertyType)i2;
                    Assert.AreEqual(p.Properties.Count, q.Properties.Count);
                    for (int i = 0; i < p.Properties.Count; i++)
                    {
                        SystemSchemaUnitTests.AssertEqual(p.Properties[i], q.Properties[i]);
                    }

                    break;
                }

                case SetPropertyType p:
                    SystemSchemaUnitTests.AssertEqual(p.Items, ((SetPropertyType)i2).Items);
                    break;
                case MapPropertyType p:
                    SystemSchemaUnitTests.AssertEqual(p.Keys, ((MapPropertyType)i2).Keys);
                    SystemSchemaUnitTests.AssertEqual(p.Values, ((MapPropertyType)i2).Values);
                    break;
                case TuplePropertyType p:
                {
                    TuplePropertyType q = (TuplePropertyType)i2;
                    Assert.AreEqual(p.Items.Count, q.Items.Count);
                    for (int i = 0; i < p.Items.Count; i++)
                    {
                        SystemSchemaUnitTests.AssertEqual(p.Items[i], q.Items[i]);
                    }

                    break;
                }

                case TaggedPropertyType p:
                {
                    TaggedPropertyType q = (TaggedPropertyType)i2;
                    Assert.AreEqual(p.Items.Count, q.Items.Count);
                    for (int i = 0; i < p.Items.Count; i++)
                    {
                        SystemSchemaUnitTests.AssertEqual(p.Items[i], q.Items[i]);
                    }

                    break;
                }

                case UdtPropertyType p:
                    Assert.AreEqual(p.Name, ((UdtPropertyType)i2).Name);
                    Assert.AreEqual(p.SchemaId, ((UdtPropertyType)i2).SchemaId);
                    break;
                default:
                    Assert.Fail("Type is abstract.");
                    return;
            }
        }

        private static void AssertEqual(EnumSchema s1, EnumSchema s2)
        {
            Assert.AreEqual(s1.Type, s2.Type);
            Assert.AreEqual(s1.ApiType, s2.ApiType);
            Assert.AreEqual(s1.Name, s2.Name);
            Assert.AreEqual(s1.Comment, s2.Comment);

            Assert.AreEqual(s1.Values.Count, s2.Values.Count);
            for (int i = 0; i < s1.Values.Count; i++)
            {
                SystemSchemaUnitTests.AssertEqual(s1.Values[i], s2.Values[i]);
            }
        }

        private static void AssertEqual(EnumValue s1, EnumValue s2)
        {
            Assert.AreEqual(s1.Value, s2.Value);
            Assert.AreEqual(s1.Name, s2.Name);
            Assert.AreEqual(s1.Comment, s2.Comment);
        }
    }
}
