// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft.Json;

    [TestClass]
    [DeploymentItem(SchemaHashUnitTests.SchemaFile, "TestData")]
    public class SchemaHashUnitTests
    {
        private const string SchemaFile = @"TestData\SchemaHashCoverageSchema.json";
        private Namespace ns;
        private Schema tableSchema;

        [TestInitialize]
        public void InitializeSuite()
        {
            string json = File.ReadAllText(SchemaHashUnitTests.SchemaFile);
            this.ns = Namespace.Parse(json);
            this.tableSchema = this.ns.Schemas.Find(s => s.Name == "Table");
        }

        [TestMethod]
        [Owner("jthunter")]
        public void SchemaHashCompileTest()
        {
            Layout layout = this.tableSchema.Compile(this.ns);
            Assert.IsNotNull(layout);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void SchemaHashTest()
        {
            (ulong low, ulong high) hash = SchemaHash.ComputeHash(this.ns, this.tableSchema);
            Assert.AreNotEqual((0, 0), hash);
            (ulong low, ulong high) hash2 = SchemaHash.ComputeHash(this.ns, this.tableSchema, (1, 1));
            Assert.AreNotEqual(hash, hash2);

            // Test clone are the same.
            Schema clone = SchemaHashUnitTests.Clone(this.tableSchema);
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2);

            // Test Schema changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Name = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Name not part of the hash

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Comment = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Comment not part of the hash

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Version = (SchemaLanguageVersion)1;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Encoding version not part of the hash

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.SchemaId = new SchemaId(42);
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Type = TypeKind.Int8;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Options changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Options.EnablePropertyLevelTimestamp = !clone.Options.EnablePropertyLevelTimestamp;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Options.DisallowUnschematized = !clone.Options.DisallowUnschematized;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Partition Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PartitionKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Primary Sort Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PrimarySortKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PrimarySortKeys[0].Direction = SortDirection.Descending;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Static Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.StaticKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Properties changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].Comment = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Comment not part of the hash

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.ApiType = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.Type = TypeKind.Binary;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.Nullable = !clone.Properties[0].PropertyType.Nullable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Primitive Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[0].PropertyType as PrimitivePropertyType).Length = 42;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[0].PropertyType as PrimitivePropertyType).Storage = StorageKind.Variable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Scope Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[1].PropertyType as ScopePropertyType).Immutable = !(clone.Properties[1].PropertyType as ScopePropertyType).Immutable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Array Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[1].PropertyType as ArrayPropertyType).Items = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Object Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[2].PropertyType as ObjectPropertyType).Properties[0] = clone.Properties[0];
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Map Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[3].PropertyType as MapPropertyType).Keys = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[3].PropertyType as MapPropertyType).Values = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Set Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[4].PropertyType as SetPropertyType).Items = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Tagged Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[5].PropertyType as TaggedPropertyType).Items[0] = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Tuple Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[6].PropertyType as TuplePropertyType).Items[0] = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test UDT Property Type changes
            try
            {
                clone = SchemaHashUnitTests.Clone(this.tableSchema);
                (clone.Properties[7].PropertyType as UdtPropertyType).Name = "some non-existing UDT name";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.Fail("Should have thrown an exception.");
            }
            catch (Exception ex)
            {
                Assert.IsNotNull(ex);
            }

            try
            {
                clone = SchemaHashUnitTests.Clone(this.tableSchema);
                (clone.Properties[7].PropertyType as UdtPropertyType).Name = "Table";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.Fail("Should have thrown an exception.");
            }
            catch (Exception ex)
            {
                Assert.IsNotNull(ex);
            }

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[7].PropertyType as UdtPropertyType).Name = "Table";
            (clone.Properties[7].PropertyType as UdtPropertyType).SchemaId = new SchemaId(2);
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Renaming an UDT is not a breaking change as long as the SchemaId has not changed.
            this.ns.Schemas[0].Name = "RenameActualUDT";
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[7].PropertyType as UdtPropertyType).Name = "RenameActualUDT";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2);
        }

        private static Schema Clone(Schema s)
        {
            JsonSerializerSettings settings = new JsonSerializerSettings()
            {
                NullValueHandling = NullValueHandling.Ignore,
                Formatting = Formatting.Indented,
                CheckAdditionalContent = true,
            };

            string json = JsonConvert.SerializeObject(s, settings);
            return JsonConvert.DeserializeObject<Schema>(json, settings);
        }
    }
}
