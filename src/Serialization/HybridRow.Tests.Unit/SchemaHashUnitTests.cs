// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable DontUseVarForVariableTypes // Do not use 'var' for variable declarations

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft.Json;

    [TestClass]
    [DeploymentItem(SchemaHashUnitTests.SchemaFile, "TestData")]
    public class SchemaHashUnitTests
    {
        private const string SchemaFile = @"TestData\SchemaHashCoverageSchema.hrschema";
        private Namespace ns;
        private Schema tableSchema;

        [TestInitialize]
        public void InitializeSuite()
        {
            this.ns = SchemaUtil.LoadFromHrSchema(SchemaHashUnitTests.SchemaFile);
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
            var expected = new
            {
                InitialHash = (5669474132123492693ul, 9149729557667844748ul),
                SeedChange = (18392417753810872046ul, 17942086702536564543ul),
                NameChange = (5669474132123492693ul, 9149729557667844748ul),
                CommentChange = (5669474132123492693ul, 9149729557667844748ul),
                VersionChange = (3856530766227562582ul, 544919695479649775ul),
                SchemaIdChange = (14486877418262750995ul, 4531483667481505499ul),
                TypeChange = (3039179951777091547ul, 1683112938996382945ul),
                EnablePropertyLevelTimestampOptionChange = (7645756771083738022ul, 1505823556482207368ul),
                DisallowUnschematizedOptionChange = (10032203022394252833ul, 13373685281699412414ul),
                DisableSystemPrefixOptionChange = (2805049018802494774ul, 16613880718597095035ul),
                PartitionKeyChange = (2005412391729232690ul, 12469959147379506889ul),
                PrimarySortKeyChange = (17537189815393436921ul, 9806309626171519256ul),
                PrimarySortKeyDirectionChange = (3645051070505371462ul, 16796982542562331583ul),
                StaticKeyChange = (491397920750345147ul, 3021634280592375817ul),
                PropertiesPathChange = (16209488961438556450ul, 11525200464633711354ul),
                PropertiesCommentChange = (5669474132123492693ul, 9149729557667844748ul),
                PropertyTypeApiTypeChange = (4743184271904044875ul, 1966348344707118709ul),
                PropertyTypeTypeChange = (6879279770561561526ul, 14002153371235144973ul),
                PropertyTypeNullableChange = (1647792025069746688ul, 8521296509338272757ul),
                PrimitivePropertyTypeLengthChange = (15199012632295198483ul, 15751981913385622765ul),
                PrimitivePropertyTypeStorageChange = (1878445231444454307ul, 15971568751688372596ul),
                ScopePropertyTypeImmutableChange = (11741304668915234934ul, 5395444112671886365ul),
                ArrayPropertyTypeChange = (17156676977790645735ul, 4418145320474226565ul),
                ObjectPropertyTypeChange = (10699938245219323478ul, 10048100595014077038ul),
                MapPropertyTypeKeyChange = (8420584249106838521ul, 11863310851610194608ul),
                MapPropertyTypeValueChange = (12611700989791556979ul, 8459671636552654180ul),
                SetPropertyTypeChange = (15043350412225158280ul, 2432573808846394367ul),
                TaggedPropertyTypeChange = (6486848089786756212ul, 12631967091371867447ul),
                TuplePropertyTypeChange = (15457776547521378835ul, 6838666961365903865ul),
                InitialV2Hash = (6209563332574727552ul, 5024404402010362065ul),
                EnumPropertyAdd = (3147450154933785163ul, 15476171830180674562ul),
                EnumBaseTypeChange = (4795354614928101891ul, 3597527276465802490ul),
                EnumPropertyValueChange = (2592048700226976894ul, 7778124937175953351ul),
            };

            (ulong low, ulong high) hash = SchemaHash.ComputeHash(this.ns, this.tableSchema);
            Assert.AreNotEqual((0, 0), hash);
            Assert.AreEqual(expected.InitialHash, hash);

            (ulong low, ulong high) hash2 = SchemaHash.ComputeHash(this.ns, this.tableSchema, (1, 1));
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.SeedChange, hash2);

            // Test clone are the same.
            Schema clone = SchemaHashUnitTests.Clone(this.tableSchema);
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2);

            // Test Schema changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Name = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Name not part of the hash
            Assert.AreEqual(expected.NameChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Comment = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Comment not part of the hash
            Assert.AreEqual(expected.CommentChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Version = (SchemaLanguageVersion)1;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2); // Encoding version is part of the hash
            Assert.AreEqual(expected.VersionChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.SchemaId = new SchemaId(42);
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.SchemaIdChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Type = TypeKind.Int8;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.TypeChange, hash2);

            // Test Options changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Options.EnablePropertyLevelTimestamp = !clone.Options.EnablePropertyLevelTimestamp;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.EnablePropertyLevelTimestampOptionChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Options.DisallowUnschematized = !clone.Options.DisallowUnschematized;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.DisallowUnschematizedOptionChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Options.DisableSystemPrefix = !clone.Options.DisableSystemPrefix;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.DisableSystemPrefixOptionChange, hash2);

            // Test Partition Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PartitionKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PartitionKeyChange, hash2);

            // Test Primary Sort Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PrimaryKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PrimarySortKeyChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.PrimaryKeys[0].Direction = SortDirection.Descending;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PrimarySortKeyDirectionChange, hash2);

            // Test Static Keys changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.StaticKeys[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.StaticKeyChange, hash2);

            // Test static keys count changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.StaticKeys = null;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);

            // Test Properties changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].Path = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PropertiesPathChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].Comment = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreEqual(hash, hash2); // Comment not part of the hash
            Assert.AreEqual(expected.PropertiesCommentChange, hash2);

            // Test Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.ApiType = "something else";
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PropertyTypeApiTypeChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.Type = TypeKind.Binary;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PropertyTypeTypeChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties[0].PropertyType.Nullable = !clone.Properties[0].PropertyType.Nullable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PropertyTypeNullableChange, hash2);

            // Test Primitive Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[0].PropertyType as PrimitivePropertyType).Length = 42;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PrimitivePropertyTypeLengthChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[0].PropertyType as PrimitivePropertyType).Storage = StorageKind.Variable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.PrimitivePropertyTypeStorageChange, hash2);

            // Test Scope Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[1].PropertyType as ScopePropertyType).Immutable = !(clone.Properties[1].PropertyType as ScopePropertyType).Immutable;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.ScopePropertyTypeImmutableChange, hash2);

            // Test Array Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[1].PropertyType as ArrayPropertyType).Items = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.ArrayPropertyTypeChange, hash2);

            // Test Object Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[2].PropertyType as ObjectPropertyType).Properties[0] = clone.Properties[0];
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.ObjectPropertyTypeChange, hash2);

            // Test Map Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[3].PropertyType as MapPropertyType).Keys = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.MapPropertyTypeKeyChange, hash2);

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[3].PropertyType as MapPropertyType).Values = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.MapPropertyTypeValueChange, hash2);

            // Test Set Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[4].PropertyType as SetPropertyType).Items = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.SetPropertyTypeChange, hash2);

            // Test Tagged Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[5].PropertyType as TaggedPropertyType).Items[0] = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.TaggedPropertyTypeChange, hash2);

            // Test Tuple Property Type changes
            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            (clone.Properties[6].PropertyType as TuplePropertyType).Items[0] = clone.Properties[0].PropertyType;
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.TuplePropertyTypeChange, hash2);

            // Test UDT Property Type changes
            try
            {
                clone = SchemaHashUnitTests.Clone(this.tableSchema);
                (clone.Properties[7].PropertyType as UdtPropertyType).Name = "some non-existing UDT name";
                _ = SchemaHash.ComputeHash(this.ns, clone);
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
                _ = SchemaHash.ComputeHash(this.ns, clone);
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
            try
            {
                this.ns.Schemas[0].Name = "RenameActualUDT";
                clone = SchemaHashUnitTests.Clone(this.tableSchema);
                (clone.Properties[7].PropertyType as UdtPropertyType).Name = "RenameActualUDT";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(hash, hash2);
            }
            finally
            {
                this.ns.Schemas[0].Name = "UDT";
            }

            // Test SDL V2 features.
            this.tableSchema.Version = SchemaLanguageVersion.V2;
            hash2 = SchemaHash.ComputeHash(this.ns, this.tableSchema);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.InitialV2Hash, hash2);
            hash = hash2;

            clone = SchemaHashUnitTests.Clone(this.tableSchema);
            clone.Properties.Add(
                new Property
                {
                    Path = "myEnum",
                    PropertyType = new PrimitivePropertyType { Type = TypeKind.Enum, Enum = "MyEnum" }
                });
            hash2 = SchemaHash.ComputeHash(this.ns, clone);
            Assert.AreNotEqual(hash, hash2);
            Assert.AreEqual(expected.EnumPropertyAdd, hash2);

            string enumName = this.ns.Enums[0].Name;
            TypeKind enumBaseType = this.ns.Enums[0].Type;
            string enumComment = this.ns.Enums[0].Comment;
            string enumApiType = this.ns.Enums[0].ApiType;
            long enumValue = this.ns.Enums[0].Values[0].Value;
            string enumValueComment = this.ns.Enums[0].Values[0].Comment;

            try
            {
                this.ns.Enums[0].Type = TypeKind.UInt16;
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumBaseTypeChange, hash2);

                this.ns.Enums[0].Values[0].Value = 666;
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);

                this.ns.Enums[0].Values[0].Name = "Names of enum values don't matter";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);

                this.ns.Enums[0].Values[0].Comment = "Comments of enum values don't matter";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);

                (clone.Properties[8].PropertyType as PrimitivePropertyType).Enum = "Names of enums don't matter";
                this.ns.Enums[0].Name = "Names of enums don't matter";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);

                this.ns.Enums[0].Comment = "Comments of enums don't matter";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);

                this.ns.Enums[0].ApiType = "ApiType of enums don't matter";
                hash2 = SchemaHash.ComputeHash(this.ns, clone);
                Assert.AreEqual(expected.EnumPropertyValueChange, hash2);
            }
            finally
            {
                this.ns.Enums[0].Name = enumName;
                this.ns.Enums[0].Type = enumBaseType;
                this.ns.Enums[0].Comment = enumComment;
                this.ns.Enums[0].ApiType = enumApiType;
                this.ns.Enums[0].Values[0].Value = enumValue;
                this.ns.Enums[0].Values[0].Comment = enumValueComment;
            }
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
