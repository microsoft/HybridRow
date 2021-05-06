// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")]
    [DeploymentItem(RowReaderUnitTests.SchemaFile, "TestData")]
    public sealed class RowReaderUnitTests
    {
        private const string SchemaFile = @"TestData\ReaderSchema.hrschema";
        private static readonly DateTime SampleDateTime = DateTime.Parse("2018-08-14 02:05:00.0000000");
        private static readonly Guid SampleGuid = Guid.Parse("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
        private static readonly Float128 SampleFloat128 = new Float128(0, 42);
        private static readonly UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
        private static readonly MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(0, 42);

        private Namespace schema;
        private LayoutResolver resolver;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            this.schema = SchemaUtil.LoadFromHrSchema(RowReaderUnitTests.SchemaFile);
            this.resolver = new LayoutResolverNamespace(this.schema);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ReadMixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Mixed").SchemaId);
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
            d.LayoutCodeSwitch("float128", value: RowReaderUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("datetime", value: RowReaderUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("unixdatetime", value: RowReaderUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("guid", value: RowReaderUnitTests.SampleGuid);
            d.LayoutCodeSwitch("mongodbobjectid", value: RowReaderUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("utf8", value: "abc");
            d.LayoutCodeSwitch("utf8_span", value: "abc");
            d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("var_varint", value: -6148914691236517206L);
            d.LayoutCodeSwitch("var_varuint", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("var_utf8", value: "abc");
            d.LayoutCodeSwitch("var_utf8_span", value: "abc");
            d.LayoutCodeSwitch("var_binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("var_binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("sparse_null");
            d.LayoutCodeSwitch("sparse_bool", value: true);
            d.LayoutCodeSwitch("sparse_int8", value: (sbyte)-86);
            d.LayoutCodeSwitch("sparse_int16", value: (short)-21846);
            d.LayoutCodeSwitch("sparse_int32", value: -1431655766);
            d.LayoutCodeSwitch("sparse_int64", value: -6148914691236517206L);
            d.LayoutCodeSwitch("sparse_uint8", value: (byte)0xAA);
            d.LayoutCodeSwitch("sparse_uint16", value: (ushort)0xAAAA);
            d.LayoutCodeSwitch("sparse_uint32", value: 0xAAAAAAAA);
            d.LayoutCodeSwitch("sparse_uint64", value: 0xAAAAAAAAAAAAAAAAL);
            d.LayoutCodeSwitch("sparse_float32", value: 1.0F / 3.0F);
            d.LayoutCodeSwitch("sparse_float64", value: 1.0 / 3.0);
            d.LayoutCodeSwitch("sparse_float128", value: RowReaderUnitTests.SampleFloat128);
            d.LayoutCodeSwitch("sparse_decimal", value: 1.0M / 3.0M);
            d.LayoutCodeSwitch("sparse_datetime", value: RowReaderUnitTests.SampleDateTime);
            d.LayoutCodeSwitch("sparse_unixdatetime", value: RowReaderUnitTests.SampleUnixDateTime);
            d.LayoutCodeSwitch("sparse_guid", value: RowReaderUnitTests.SampleGuid);
            d.LayoutCodeSwitch("sparse_mongodbobjectid", value: RowReaderUnitTests.SampleMongoDbObjectId);
            d.LayoutCodeSwitch("sparse_utf8", value: "abc");
            d.LayoutCodeSwitch("sparse_utf8_span", value: "abc");
            d.LayoutCodeSwitch("sparse_binary", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("sparse_binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
            d.LayoutCodeSwitch("array_t<int8>", value: new sbyte[] { -86, -86, -86 });
            d.LayoutCodeSwitch("array_t<array_t<float32>>", value: new[] { new float[] { 1, 2, 3 }, new float[] { 1, 2, 3 } });
            d.LayoutCodeSwitch("array_t<utf8>", value: new[] { "abc", "def", "hij" });
            d.LayoutCodeSwitch("tuple<varint,int64>", value: Tuple.Create(-6148914691236517206L, -6148914691236517206L));
            d.LayoutCodeSwitch("tuple<null,tuple<int8,int8>>", value: Tuple.Create(NullValue.Default, Tuple.Create((sbyte)-86, (sbyte)-86)));
            d.LayoutCodeSwitch("tuple<bool,udt>", value: Tuple.Create(false, new Point(1, 2)));
            d.LayoutCodeSwitch("nullable<int32,int64>", value: Tuple.Create(default(int?), (long?)123L));
            d.LayoutCodeSwitch("tagged<utf8>", value: Tuple.Create((byte)3, "hello"));
            d.LayoutCodeSwitch("tagged<bool,utf8>", value: Tuple.Create((byte)5, true, "bye"));
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

            RowReader reader = d.GetReader();
            Assert.AreEqual(reader.Length, d.Row.Length);
            RowReaderUnitTests.PrintReader(ref reader, 0);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ReadScopes()
        {
            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(0);
            RowBuffer row = new RowBuffer(0, resizer);
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Mixed").SchemaId);
            row.InitLayout(HybridRowVersion.V1, layout, this.resolver);

            ResultAssert.IsSuccess(RowWriter.WriteBuffer(ref row, 2, RowReaderUnitTests.WriteNestedDocument));
            RowReader rowReader = new RowReader(ref row);

            ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(ref rowReader, 0));

            rowReader = new RowReader(ref row);
            ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(ref rowReader, 0));

            rowReader = new RowReader(ref row);
            ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegateWithSkipScope(ref rowReader, 0));

            // SkipScope not okay after advancing parent
            rowReader = new RowReader(ref row);
            Assert.IsTrue(rowReader.Read());
            Assert.AreEqual(rowReader.Type.LayoutCode, LayoutCode.ObjectScope);
            RowReader nestedScope = rowReader.ReadScope();
            ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(ref nestedScope, 0));
            Assert.IsTrue(rowReader.Read());
            Result result = rowReader.SkipScope(ref nestedScope);
            Assert.AreNotEqual(Result.Success, result);
        }

        internal static void PrintReader(ref RowReader reader, int indent)
        {
            string str;
            ResultAssert.IsSuccess(DiagnosticConverter.ReaderToString(ref reader, out str));
            Console.WriteLine(str);
        }

        private static Result WriteNestedDocument(ref RowWriter writer, TypeArgument typeArgument, int level)
        {
            TypeArgument tupleArgument = new TypeArgument(
                LayoutType.Tuple,
                new TypeArgumentList(
                    new[]
                    {
                        new TypeArgument(LayoutType.Int32),
                        new TypeArgument(LayoutType.Int32),
                        new TypeArgument(LayoutType.Int32),
                    }));

            Result WriteTuple(ref RowWriter tupleWriter, TypeArgument tupleTypeArgument, int unused)
            {
                ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 1));
                ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 2));
                ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 3));
                return Result.Success;
            }

            if (level == 0)
            {
                ResultAssert.IsSuccess(writer.WriteScope("x", tupleArgument, 0, WriteTuple));
                return Result.Success;
            }

            ResultAssert.IsSuccess(writer.WriteScope("a", new TypeArgument(LayoutType.Object), level - 1, RowReaderUnitTests.WriteNestedDocument));
            ResultAssert.IsSuccess(writer.WriteScope("x", tupleArgument, 0, WriteTuple));
            ResultAssert.IsSuccess(writer.WriteScope("b", new TypeArgument(LayoutType.Object), level - 1, RowReaderUnitTests.WriteNestedDocument));
            ResultAssert.IsSuccess(writer.WriteScope("y", tupleArgument, 0, WriteTuple));
            ResultAssert.IsSuccess(writer.WriteScope("c", new TypeArgument(LayoutType.Object), level - 1, RowReaderUnitTests.WriteNestedDocument));

            return Result.Success;
        }

        private static Result ReadNestedDocumentDelegate(ref RowReader reader, int context)
        {
            while (reader.Read())
            {
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.TupleScope:
                    {
                        ResultAssert.IsSuccess(reader.ReadScope(0, RowReaderUnitTests.ReadTuplePartial));
                        break;
                    }

                    case LayoutCode.ObjectScope:
                    {
                        ResultAssert.IsSuccess(reader.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                        break;
                    }
                }
            }

            return Result.Success;
        }

        private static Result ReadNestedDocumentNonDelegate(ref RowReader reader, int context)
        {
            while (reader.Read())
            {
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.TupleScope:
                    {
                        RowReader nested = reader.ReadScope();
                        ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(ref nested, 0));
                        break;
                    }

                    case LayoutCode.ObjectScope:
                    {
                        RowReader nested = reader.ReadScope();
                        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(ref nested, 0));
                        ResultAssert.IsSuccess(reader.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                        break;
                    }
                }
            }

            return Result.Success;
        }

        private static Result ReadNestedDocumentNonDelegateWithSkipScope(ref RowReader reader, int context)
        {
            while (reader.Read())
            {
                switch (reader.Type.LayoutCode)
                {
                    case LayoutCode.TupleScope:
                    {
                        RowReader nested = reader.ReadScope();
                        ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(ref nested, 0));
                        ResultAssert.IsSuccess(reader.SkipScope(ref nested));
                        break;
                    }

                    case LayoutCode.ObjectScope:
                    {
                        RowReader nested = reader.ReadScope();
                        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(ref nested, 0));
                        ResultAssert.IsSuccess(reader.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                        ResultAssert.IsSuccess(reader.SkipScope(ref nested));
                        break;
                    }
                }
            }

            return Result.Success;
        }

        private static Result ReadTuplePartial(ref RowReader reader, int unused)
        {
            // Read only part of our tuple
            Assert.IsTrue(reader.Read());
            Assert.IsTrue(reader.Read());
            return Result.Success;
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        internal sealed class Point : IDispatchable, IRowSerializable
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
                if (object.ReferenceEquals(null, obj))
                {
                    return false;
                }

                if (object.ReferenceEquals(this, obj))
                {
                    return true;
                }

                return obj is Point && this.Equals((Point)obj);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    return (this.X.GetHashCode() * 397) ^ this.Y.GetHashCode();
                }
            }

            Result IRowSerializable.Write(ref RowWriter writer, TypeArgument typeArg)
            {
                Result result = writer.WriteInt32("x", this.X);
                if (result != Result.Success)
                {
                    return result;
                }

                return writer.WriteInt32("y", this.Y);
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
    }
}
