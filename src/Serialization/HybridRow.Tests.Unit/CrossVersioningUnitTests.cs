// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Public Fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Newtonsoft.Json;

    [TestClass]
    [SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here are anonymous.")]
    [DeploymentItem(CrossVersioningUnitTests.SchemaFile, "TestData")]
    [DeploymentItem(CrossVersioningUnitTests.ExpectedFile, "TestData")]
    public sealed class CrossVersioningUnitTests
    {
        private const string SchemaFile = @"TestData\CrossVersioningSchema.hrschema";
        private const string ExpectedFile = @"TestData\CrossVersioningExpected.json";
        private static readonly DateTime SampleDateTime = DateTime.Parse("2018-08-14 02:05:00.0000000");
        private static readonly Guid SampleGuid = Guid.Parse("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
        private static readonly Float128 SampleFloat128 = new Float128(0, 42);
        private static readonly UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
        private static readonly MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(704643072U, 0); // 42 in big-endian

        private Namespace schema;
        private LayoutResolver resolver;
        private Expected expected;

        [TestInitialize]
        public void ParseNamespace()
        {
            this.schema = SchemaUtil.LoadFromHrSchema(CrossVersioningUnitTests.SchemaFile);
            string json = File.ReadAllText(CrossVersioningUnitTests.ExpectedFile);
            this.expected = JsonConvert.DeserializeObject<Expected>(json);
            this.resolver = new LayoutResolverNamespace(this.schema);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionWriteFixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Fixed").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool", value: true);
            d.LayoutCodeSwitch("int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("int16", value: (short)-21846);
            d.LayoutCodeSwitch("int32", value: -1431655766);
            d.LayoutCodeSwitch("int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("float128", value: CrossVersioningUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: CrossVersioningUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: CrossVersioningUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: CrossVersioningUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: CrossVersioningUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });

            Assert.AreEqual(this.expected.CrossVersionFixed, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionReadFixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Fixed").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.ReadFrom<ReadRowDispatcher>(this.resolver, this.expected.CrossVersionFixed);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool", value: true);
            d.LayoutCodeSwitch("int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("int16", value: (short)-21846);
            d.LayoutCodeSwitch("int32", value: -1431655766);
            d.LayoutCodeSwitch("int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("float128", value: CrossVersioningUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: CrossVersioningUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: CrossVersioningUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: CrossVersioningUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: CrossVersioningUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionWriteNullFixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Fixed").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            Assert.AreEqual(this.expected.CrossVersionNullFixed, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionReadNullFixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Fixed").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<NullRowDispatcher>(layout, this.resolver);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool");
            d.LayoutCodeSwitch("int8");
            d.LayoutCodeSwitch("int16");
            d.LayoutCodeSwitch("int32");
            d.LayoutCodeSwitch("int64");
            d.LayoutCodeSwitch("uint8");
            d.LayoutCodeSwitch("uint16");
            d.LayoutCodeSwitch("uint32");
            d.LayoutCodeSwitch("uint64");
            d.LayoutCodeSwitch("float32");
            d.LayoutCodeSwitch("float64");
            d.LayoutCodeSwitch("float128");
            d.LayoutCodeSwitch("decimal");
            d.LayoutCodeSwitch("datetime");
            d.LayoutCodeSwitch("unixdatetime");
            d.LayoutCodeSwitch("guid");
            d.LayoutCodeSwitch("mongodbobjectid");
            d.LayoutCodeSwitch("utf8");
            d.LayoutCodeSwitch("binary");
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionWriteVariable()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Variable").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            d.LayoutCodeSwitch("varint", value: -6148914691236517206L);
            d.LayoutCodeSwitch("varuint", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });

            Assert.AreEqual(this.expected.CrossVersionVariable, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionReadVariable()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Variable").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.ReadFrom<ReadRowDispatcher>(this.resolver, this.expected.CrossVersionVariable);
            d.LayoutCodeSwitch("varint", value: -6148914691236517206L);
            d.LayoutCodeSwitch("varuint", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionWriteNullVariable()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Variable").SchemaId);
            Assert.IsNotNull(layout);
            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            Assert.AreEqual(this.expected.CrossVersionNullVariable, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionReadNullVariable()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Variable").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<NullRowDispatcher>(layout, this.resolver);
            d.LayoutCodeSwitch("varint");
            d.LayoutCodeSwitch("varuint");
            d.LayoutCodeSwitch("utf8");
            d.LayoutCodeSwitch("binary");
        }

        [TestMethod]
        [Owner("jthunter")]
        [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")]
        public void CrossVersionWriteSparse()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Sparse").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool", value: true);
            d.LayoutCodeSwitch("int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("int16", value: (short)-21846);
            d.LayoutCodeSwitch("int32", value: -1431655766);
            d.LayoutCodeSwitch("int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("float128", value: CrossVersioningUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: CrossVersioningUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: CrossVersioningUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: CrossVersioningUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: CrossVersioningUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("array_t<int8>", value: new sbyte[] { -86, -86, -86 });
            d.LayoutCodeSwitch("array_t<array_t<float32>>", value: new[] { new float[] { 1, 2, 3 }, new float[] { 1, 2, 3 } });
            d.LayoutCodeSwitch("array_t<utf8>", value: new[] { "abc", "def", "hij" });
            d.LayoutCodeSwitch("tuple<varint,int64>", value: Tuple.Create(-6148914691236517206L, -6148914691236517206L));
            d.LayoutCodeSwitch("tuple<null,tuple<int8,int8>>", value: Tuple.Create(NullValue.Default, Tuple.Create((sbyte)-86, (sbyte)-86)));
            d.LayoutCodeSwitch("tuple<bool,udt>", value: Tuple.Create(false, new Point(1, 2)));
            d.LayoutCodeSwitch("set_t<utf8>", value: new[] { "abc", "efg", "xzy" });
            d.LayoutCodeSwitch("set_t<array_t<int8>>", value: new[] { new sbyte[] { 1, 2, 3 }, new sbyte[] { 4, 5, 6 }, new sbyte[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<set_t<int32>>", value: new[] { new[] { 1, 2, 3 }, new[] { 4, 5, 6 }, new[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<udt>", value: new[] { new Point(1, 2), new Point(3, 4), new Point(5, 6) });
            d.LayoutCodeSwitch("map_t<utf8,utf8>", value: new[] { Tuple.Create("Mark", "Luke"), Tuple.Create("Harrison", "Han") });
            d.LayoutCodeSwitch(
                "map_t<int8,array_t<int8>>",
                value: new[] { Tuple.Create((sbyte)1, new sbyte[] { 1, 2, 3 }), Tuple.Create((sbyte)2, new sbyte[] { 4, 5, 6 }) });

            d.LayoutCodeSwitch(
                "map_t<int16,map_t<int32,int32>>",
                value: new[]
                {
                    Tuple.Create((short)1, new[] { Tuple.Create(1, 2), Tuple.Create(3, 4) }),
                    Tuple.Create((short)2, new[] { Tuple.Create(5, 6), Tuple.Create(7, 8) }),
                });

            d.LayoutCodeSwitch(
                "map_t<float64,udt>",
                value: new[]
                {
                    Tuple.Create(1.0, new Point(1, 2)),
                    Tuple.Create(2.0, new Point(3, 4)),
                    Tuple.Create(3.0, new Point(5, 6)),
                });

            Assert.AreEqual(this.expected.CrossVersionSparse, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")]
        public void CrossVersionReadSparse()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Sparse").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.ReadFrom<ReadRowDispatcher>(this.resolver, this.expected.CrossVersionSparse);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool", value: true);
            d.LayoutCodeSwitch("int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("int16", value: (short)-21846);
            d.LayoutCodeSwitch("int32", value: -1431655766);
            d.LayoutCodeSwitch("int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("float128", value: CrossVersioningUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: CrossVersioningUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: CrossVersioningUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: CrossVersioningUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: CrossVersioningUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("array_t<int8>", value: new sbyte[] { -86, -86, -86 });
            d.LayoutCodeSwitch("array_t<array_t<float32>>", value: new[] { new float[] { 1, 2, 3 }, new float[] { 1, 2, 3 } });
            d.LayoutCodeSwitch("array_t<utf8>", value: new[] { "abc", "def", "hij" });
            d.LayoutCodeSwitch("tuple<varint,int64>", value: Tuple.Create(-6148914691236517206L, -6148914691236517206L));
            d.LayoutCodeSwitch("tuple<null,tuple<int8,int8>>", value: Tuple.Create(NullValue.Default, Tuple.Create((sbyte)-86, (sbyte)-86)));
            d.LayoutCodeSwitch("tuple<bool,udt>", value: Tuple.Create(false, new Point(1, 2)));
            d.LayoutCodeSwitch("set_t<utf8>", value: new[] { "abc", "efg", "xzy" });
            d.LayoutCodeSwitch("set_t<array_t<int8>>", value: new[] { new sbyte[] { 1, 2, 3 }, new sbyte[] { 4, 5, 6 }, new sbyte[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<set_t<int32>>", value: new[] { new[] { 1, 2, 3 }, new[] { 4, 5, 6 }, new[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<udt>", value: new[] { new Point(1, 2), new Point(3, 4), new Point(5, 6) });
            d.LayoutCodeSwitch("map_t<utf8,utf8>", value: new[] { Tuple.Create("Mark", "Luke"), Tuple.Create("Harrison", "Han") });
            d.LayoutCodeSwitch(
                "map_t<int8,array_t<int8>>",
                value: new[] { Tuple.Create((sbyte)1, new sbyte[] { 1, 2, 3 }), Tuple.Create((sbyte)2, new sbyte[] { 4, 5, 6 }) });

            d.LayoutCodeSwitch(
                "map_t<int16,map_t<int32,int32>>",
                value: new[]
                {
                    Tuple.Create((short)1, new[] { Tuple.Create(1, 2), Tuple.Create(3, 4) }),
                    Tuple.Create((short)2, new[] { Tuple.Create(5, 6), Tuple.Create(7, 8) }),
                });

            d.LayoutCodeSwitch(
                "map_t<float64,udt>",
                value: new[]
                {
                    Tuple.Create(2.0, new Point(3, 4)),
                    Tuple.Create(3.0, new Point(5, 6)),
                    Tuple.Create(1.0, new Point(1, 2)),
                });
        }

        [TestMethod]
        [Owner("jthunter")]
        [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")]
        public void CrossVersionDeleteSparse()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Sparse").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.ReadFrom<DeleteRowDispatcher>(this.resolver, this.expected.CrossVersionSparse);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool", value: true);
            d.LayoutCodeSwitch("int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("int16", value: (short)-21846);
            d.LayoutCodeSwitch("int32", value: -1431655766);
            d.LayoutCodeSwitch("int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("float128", value: CrossVersioningUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: CrossVersioningUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: CrossVersioningUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: CrossVersioningUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: CrossVersioningUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("array_t<int8>", value: new sbyte[] { -86, -86, -86 });
            d.LayoutCodeSwitch("array_t<array_t<float32>>", value: new[] { new float[] { 1, 2, 3 }, new float[] { 1, 2, 3 } });
            d.LayoutCodeSwitch("array_t<utf8>", value: new[] { "abc", "def", "hij" });
            d.LayoutCodeSwitch("tuple<varint,int64>", value: Tuple.Create(-6148914691236517206L, -6148914691236517206L));
            d.LayoutCodeSwitch("tuple<null,tuple<int8,int8>>", value: Tuple.Create(NullValue.Default, Tuple.Create((sbyte)-86, (sbyte)-86)));
            d.LayoutCodeSwitch("tuple<bool,udt>", value: Tuple.Create(false, new Point(1, 2)));
            d.LayoutCodeSwitch("set_t<utf8>", value: new[] { "abc", "efg", "xzy" });
            d.LayoutCodeSwitch("set_t<array_t<int8>>", value: new[] { new sbyte[] { 1, 2, 3 }, new sbyte[] { 4, 5, 6 }, new sbyte[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<set_t<int32>>", value: new[] { new[] { 1, 2, 3 }, new[] { 4, 5, 6 }, new[] { 7, 8, 9 } });
            d.LayoutCodeSwitch("set_t<udt>", value: new[] { new Point(1, 2), new Point(3, 4), new Point(5, 6) });
            d.LayoutCodeSwitch("map_t<utf8,utf8>", value: new[] { Tuple.Create("Mark", "Luke"), Tuple.Create("Harrison", "Han") });
            d.LayoutCodeSwitch(
                "map_t<int8,array_t<int8>>",
                value: new[] { Tuple.Create((sbyte)1, new sbyte[] { 1, 2, 3 }), Tuple.Create((sbyte)2, new sbyte[] { 4, 5, 6 }) });

            d.LayoutCodeSwitch(
                "map_t<int16,map_t<int32,int32>>",
                value: new[]
                {
                    Tuple.Create((short)1, new[] { Tuple.Create(1, 2), Tuple.Create(3, 4) }),
                    Tuple.Create((short)2, new[] { Tuple.Create(5, 6), Tuple.Create(7, 8) }),
                });

            d.LayoutCodeSwitch(
                "map_t<float64,udt>",
                value: new[]
                {
                    Tuple.Create(1.0, new Point(1, 2)),
                    Tuple.Create(2.0, new Point(3, 4)),
                    Tuple.Create(3.0, new Point(5, 6)),
                });

            Assert.AreEqual(this.expected.CrossVersionNullSparse, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionWriteNullSparse()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Sparse").SchemaId);
            Assert.IsNotNull(layout);
            RowOperationDispatcher d = RowOperationDispatcher.Create<WriteRowDispatcher>(layout, this.resolver);
            Assert.AreEqual(this.expected.CrossVersionNullSparse, d.RowToHex());
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CrossVersionReadNullSparse()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Sparse").SchemaId);
            Assert.IsNotNull(layout);

            RowOperationDispatcher d = RowOperationDispatcher.ReadFrom<NullRowDispatcher>(this.resolver, this.expected.CrossVersionNullSparse);
            d.LayoutCodeSwitch("null");
            d.LayoutCodeSwitch("bool");
            d.LayoutCodeSwitch("int8");
            d.LayoutCodeSwitch("int16");
            d.LayoutCodeSwitch("int32");
            d.LayoutCodeSwitch("int64");
            d.LayoutCodeSwitch("uint8");
            d.LayoutCodeSwitch("uint16");
            d.LayoutCodeSwitch("uint32");
            d.LayoutCodeSwitch("uint64");
            d.LayoutCodeSwitch("float32");
            d.LayoutCodeSwitch("float64");
            d.LayoutCodeSwitch("float128");
            d.LayoutCodeSwitch("decimal");
            d.LayoutCodeSwitch("datetime");
            d.LayoutCodeSwitch("unixdatetime");
            d.LayoutCodeSwitch("guid");
            d.LayoutCodeSwitch("mongodbobjectid");
            d.LayoutCodeSwitch("utf8");
            d.LayoutCodeSwitch("binary");
        }

        private sealed class Point : IDispatchable
        {
            public readonly int X;
            public readonly int Y;

            public Point(int x, int y)
            {
                this.X = x;
                this.Y = y;
            }

            public override bool Equals(object obj)
            {
                if (obj is null)
                {
                    return false;
                }

                if (object.ReferenceEquals(this, obj))
                {
                    return true;
                }

                return obj is Point point && this.Equals(point);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    return (this.X.GetHashCode() * 397) ^ this.Y.GetHashCode();
                }
            }

            void IDispatchable.Dispatch(ref RowOperationDispatcher dispatcher, ref RowCursor scope)
            {
                dispatcher.LayoutCodeSwitch(ref scope, "x", value: this.X);
                dispatcher.LayoutCodeSwitch(ref scope, "y", value: this.Y);
            }

            private bool Equals(Point other)
            {
                return this.X == other.X && this.Y == other.Y;
            }
        }

        [SuppressMessage("Microsoft.Performance", "CA1812:AvoidUninstantiatedInternalClasses", Justification = "Instantiated through Reflection.")]
        private sealed class Expected
        {
            public string CrossVersionFixed { get; set; }

            public string CrossVersionNullFixed { get; set; }

            public string CrossVersionVariable { get; set; }

            public string CrossVersionNullVariable { get; set; }

            public string CrossVersionSparse { get; set; }

            public string CrossVersionNullSparse { get; set; }
        }
    }
}
