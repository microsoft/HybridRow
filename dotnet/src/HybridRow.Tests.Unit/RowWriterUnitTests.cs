// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Buffers;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [SuppressMessage("StyleCop.CSharp.ReadabilityRules", "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")]
    [DeploymentItem(RowWriterUnitTests.SchemaFile, "TestData")]
    public sealed class RowWriterUnitTests
    {
        private const int InitialRowSize = 2 * 1024 * 1024;
        private const string SchemaFile = @"TestData\ReaderSchema.json";
        private static readonly DateTime SampleDateTime = DateTime.Parse("2018-08-14 02:05:00.0000000");
        private static readonly Guid SampleGuid = Guid.Parse("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
        private static readonly Float128 SampleFloat128 = new Float128(0, 42);
        private static readonly UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
        private static readonly MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(0, 42);

        private Namespace schema;
        private LayoutResolver resolver;

        [TestInitialize]
        public void TestInitialize()
        {
            string json = File.ReadAllText(RowWriterUnitTests.SchemaFile);
            this.schema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.schema);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void WriteMixed()
        {
            Layout layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Mixed").SchemaId);
            Assert.IsNotNull(layout);

            RowBuffer row = new RowBuffer(RowWriterUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, layout, this.resolver);

            int writerLength = 0;
            ResultAssert.IsSuccess(
                RowWriter.WriteBuffer(
                    ref row,
                    null,
                    (ref RowWriter writer, TypeArgument rootTypeArg, object ignored) =>
                    {
                        ResultAssert.IsSuccess(writer.WriteNull("null"));
                        ResultAssert.IsSuccess(writer.WriteBool("bool", true));
                        ResultAssert.IsSuccess(writer.WriteInt8("int8", (sbyte)-86));
                        ResultAssert.IsSuccess(writer.WriteInt16("int16", (short)-21846));
                        ResultAssert.IsSuccess(writer.WriteInt32("int32", -1431655766));
                        ResultAssert.IsSuccess(writer.WriteInt64("int64", -6148914691236517206L));
                        ResultAssert.IsSuccess(writer.WriteUInt8("uint8", (byte)0xAA));
                        ResultAssert.IsSuccess(writer.WriteUInt16("uint16", (ushort)0xAAAA));
                        ResultAssert.IsSuccess(writer.WriteUInt32("uint32", 0xAAAAAAAA));
                        ResultAssert.IsSuccess(writer.WriteUInt64("uint64", 0xAAAAAAAAAAAAAAAAL));
                        ResultAssert.IsSuccess(writer.WriteFloat32("float32", 1.0F / 3.0F));
                        ResultAssert.IsSuccess(writer.WriteFloat64("float64", 1.0 / 3.0));
                        ResultAssert.IsSuccess(writer.WriteFloat128("float128", RowWriterUnitTests.SampleFloat128));
                        ResultAssert.IsSuccess(writer.WriteDecimal("decimal", 1.0M / 3.0M));
                        ResultAssert.IsSuccess(writer.WriteDateTime("datetime", RowWriterUnitTests.SampleDateTime));
                        ResultAssert.IsSuccess(writer.WriteUnixDateTime("unixdatetime", RowWriterUnitTests.SampleUnixDateTime));
                        ResultAssert.IsSuccess(writer.WriteGuid("guid", RowWriterUnitTests.SampleGuid));
                        ResultAssert.IsSuccess(writer.WriteMongoDbObjectId("mongodbobjectid", RowWriterUnitTests.SampleMongoDbObjectId));
                        ResultAssert.IsSuccess(writer.WriteString("utf8", "abc"));
                        ResultAssert.IsSuccess(writer.WriteString("utf8_span", Utf8Span.TranscodeUtf16("abc")));
                        ResultAssert.IsSuccess(writer.WriteBinary("binary", new[] { (byte)0, (byte)1, (byte)2 }));
                        ResultAssert.IsSuccess(writer.WriteBinary("binary_span", new[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
                        ResultAssert.IsSuccess(
                            writer.WriteBinary("binary_sequence", new ReadOnlySequence<byte>(new[] { (byte)0, (byte)1, (byte)2 })));

                        ResultAssert.IsSuccess(writer.WriteVarInt("var_varint", -6148914691236517206L));
                        ResultAssert.IsSuccess(writer.WriteVarUInt("var_varuint", 0xAAAAAAAAAAAAAAAAL));
                        ResultAssert.IsSuccess(writer.WriteString("var_utf8", "abc"));
                        ResultAssert.IsSuccess(writer.WriteString("var_utf8_span", Utf8Span.TranscodeUtf16("abc")));
                        ResultAssert.IsSuccess(writer.WriteBinary("var_binary", new[] { (byte)0, (byte)1, (byte)2 }));
                        ResultAssert.IsSuccess(writer.WriteBinary("var_binary_span", new[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
                        ResultAssert.IsSuccess(
                            writer.WriteBinary("var_binary_sequence", new ReadOnlySequence<byte>(new[] { (byte)0, (byte)1, (byte)2 })));

                        ResultAssert.IsSuccess(writer.WriteNull("sparse_null"));
                        ResultAssert.IsSuccess(writer.WriteBool("sparse_bool", true));
                        ResultAssert.IsSuccess(writer.WriteInt8("sparse_int8", (sbyte)-86));
                        ResultAssert.IsSuccess(writer.WriteInt16("sparse_int16", (short)-21846));
                        ResultAssert.IsSuccess(writer.WriteInt32("sparse_int32", -1431655766));
                        ResultAssert.IsSuccess(writer.WriteInt64("sparse_int64", -6148914691236517206L));
                        ResultAssert.IsSuccess(writer.WriteUInt8("sparse_uint8", (byte)0xAA));
                        ResultAssert.IsSuccess(writer.WriteUInt16("sparse_uint16", (ushort)0xAAAA));
                        ResultAssert.IsSuccess(writer.WriteUInt32("sparse_uint32", 0xAAAAAAAA));
                        ResultAssert.IsSuccess(writer.WriteUInt64("sparse_uint64", 0xAAAAAAAAAAAAAAAAL));
                        ResultAssert.IsSuccess(writer.WriteFloat32("sparse_float32", 1.0F / 3.0F));
                        ResultAssert.IsSuccess(writer.WriteFloat64("sparse_float64", 1.0 / 3.0));
                        ResultAssert.IsSuccess(writer.WriteFloat128("sparse_float128", RowWriterUnitTests.SampleFloat128));
                        ResultAssert.IsSuccess(writer.WriteDecimal("sparse_decimal", 1.0M / 3.0M));
                        ResultAssert.IsSuccess(writer.WriteDateTime("sparse_datetime", RowWriterUnitTests.SampleDateTime));
                        ResultAssert.IsSuccess(writer.WriteUnixDateTime("sparse_unixdatetime", RowWriterUnitTests.SampleUnixDateTime));
                        ResultAssert.IsSuccess(writer.WriteGuid("sparse_guid", RowWriterUnitTests.SampleGuid));
                        ResultAssert.IsSuccess(writer.WriteMongoDbObjectId("sparse_mongodbobjectid", RowWriterUnitTests.SampleMongoDbObjectId));
                        ResultAssert.IsSuccess(writer.WriteString("sparse_utf8", "abc"));
                        ResultAssert.IsSuccess(writer.WriteString("sparse_utf8_span", Utf8Span.TranscodeUtf16("abc")));
                        ResultAssert.IsSuccess(writer.WriteBinary("sparse_binary", new[] { (byte)0, (byte)1, (byte)2 }));
                        ResultAssert.IsSuccess(writer.WriteBinary("sparse_binary_span", new[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
                        ResultAssert.IsSuccess(
                            writer.WriteBinary("sparse_binary_sequence", new ReadOnlySequence<byte>(new[] { (byte)0, (byte)1, (byte)2 })));

                        LayoutColumn col;
                        Assert.IsTrue(layout.TryFind("array_t<int8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new sbyte[] { -86, -87, -88 },
                                (ref RowWriter writer2, TypeArgument typeArg, sbyte[] values) =>
                                {
                                    foreach (sbyte value in values)
                                    {
                                        ResultAssert.IsSuccess(writer2.WriteInt8(null, value));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("array_t<array_t<float32>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { new float[] { 1, 2, 3 }, new float[] { 1, 2, 3 } },
                                (ref RowWriter writer2, TypeArgument typeArg, float[][] values) =>
                                {
                                    foreach (float[] value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                typeArg.TypeArgs[0],
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, float[] values2) =>
                                                {
                                                    foreach (float value2 in values2)
                                                    {
                                                        ResultAssert.IsSuccess(writer3.WriteFloat32(null, value2));
                                                    }

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("array_t<utf8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { "abc", "def", "hij" },
                                (ref RowWriter writer2, TypeArgument typeArg, string[] values) =>
                                {
                                    foreach (string value in values)
                                    {
                                        ResultAssert.IsSuccess(writer2.WriteString(null, value));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("tuple<varint,int64>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create(-6148914691236517206L, -6148914691236517206L),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<long, long> values) =>
                                {
                                    ResultAssert.IsSuccess(writer2.WriteVarInt(null, values.Item1));
                                    ResultAssert.IsSuccess(writer2.WriteInt64(null, values.Item2));
                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("tuple<null,tuple<int8,int8>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create(NullValue.Default, Tuple.Create((sbyte)-86, (sbyte)-86)),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<NullValue, Tuple<sbyte, sbyte>> values) =>
                                {
                                    ResultAssert.IsSuccess(writer2.WriteNull(null));
                                    ResultAssert.IsSuccess(
                                        writer2.WriteScope(
                                            null,
                                            typeArg.TypeArgs[1],
                                            values.Item2,
                                            (ref RowWriter writer3, TypeArgument typeArg2, Tuple<sbyte, sbyte> values2) =>
                                            {
                                                ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item1));
                                                ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item2));
                                                return Result.Success;
                                            }));

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("tuple<bool,udt>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create(false, new RowReaderUnitTests.Point(1, 2)),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<bool, RowReaderUnitTests.Point> values) =>
                                {
                                    ResultAssert.IsSuccess(writer2.WriteBool(null, values.Item1));
                                    ResultAssert.IsSuccess(
                                        writer2.WriteScope(
                                            null,
                                            typeArg.TypeArgs[1],
                                            values.Item2,
                                            (ref RowWriter writer3, TypeArgument typeArg2, IRowSerializable values2) =>
                                                values2.Write(ref writer3, typeArg2)));

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("nullable<int32,int64>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create(default(int?), (long?)123L),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<int?, long?> values) =>
                                {
                                    RowWriter.WriterFunc<int?> f0 = null;
                                    if (values.Item1.HasValue)
                                    {
                                        f0 = (ref RowWriter writer3, TypeArgument typeArg2, int? value) => writer3.WriteInt32(null, value.Value);
                                    }

                                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.TypeArgs[0], values.Item1, f0));

                                    RowWriter.WriterFunc<long?> f1 = null;
                                    if (values.Item2.HasValue)
                                    {
                                        f1 = (ref RowWriter writer3, TypeArgument typeArg2, long? value) => writer3.WriteInt64(null, value.Value);
                                    }

                                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.TypeArgs[1], values.Item2, f1));
                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("tagged<utf8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create((byte)3, "hello"),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<byte, string> values) =>
                                {
                                    ResultAssert.IsSuccess(writer2.WriteUInt8(null, values.Item1));
                                    ResultAssert.IsSuccess(writer2.WriteString(null, values.Item2));
                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("tagged<bool,utf8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                Tuple.Create((byte)5, true, "bye"),
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<byte, bool, string> values) =>
                                {
                                    ResultAssert.IsSuccess(writer2.WriteUInt8(null, values.Item1));
                                    ResultAssert.IsSuccess(writer2.WriteBool(null, values.Item2));
                                    ResultAssert.IsSuccess(writer2.WriteString(null, values.Item3));
                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("set_t<utf8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { "abc", "xzy", "efg" },
                                (ref RowWriter writer2, TypeArgument typeArg, string[] values) =>
                                {
                                    foreach (string value in values)
                                    {
                                        ResultAssert.IsSuccess(writer2.WriteString(null, value));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("set_t<array_t<int8>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { new sbyte[] { 7, 8, 9 }, new sbyte[] { 4, 5, 6 }, new sbyte[] { 1, 2, 3 } },
                                (ref RowWriter writer2, TypeArgument typeArg, sbyte[][] values) =>
                                {
                                    foreach (sbyte[] value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                typeArg.TypeArgs[0],
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, sbyte[] values2) =>
                                                {
                                                    foreach (sbyte value2 in values2)
                                                    {
                                                        ResultAssert.IsSuccess(writer3.WriteInt8(null, value2));
                                                    }

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("set_t<set_t<int32>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { new[] { 4, 5, 6 }, new[] { 7, 8, 9 }, new[] { 1, 2, 3 } },
                                (ref RowWriter writer2, TypeArgument typeArg, int[][] values) =>
                                {
                                    foreach (int[] value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                typeArg.TypeArgs[0],
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, int[] values2) =>
                                                {
                                                    foreach (int value2 in values2)
                                                    {
                                                        ResultAssert.IsSuccess(writer3.WriteInt32(null, value2));
                                                    }

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("set_t<udt>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { new RowReaderUnitTests.Point(1, 2), new RowReaderUnitTests.Point(3, 4), new RowReaderUnitTests.Point(5, 6) },
                                (ref RowWriter writer2, TypeArgument typeArg, RowReaderUnitTests.Point[] values) =>
                                {
                                    foreach (RowReaderUnitTests.Point value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                typeArg.TypeArgs[0],
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, IRowSerializable values2) =>
                                                    values2.Write(ref writer3, typeArg2)));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("map_t<utf8,utf8>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { Tuple.Create("Harrison", "Han"), Tuple.Create("Mark", "Luke") },
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<string, string>[] values) =>
                                {
                                    foreach (Tuple<string, string> value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                new TypeArgument(LayoutType.TypedTuple, typeArg.TypeArgs),
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, Tuple<string, string> values2) =>
                                                {
                                                    ResultAssert.IsSuccess(writer3.WriteString(null, values2.Item1));
                                                    ResultAssert.IsSuccess(writer3.WriteString(null, values2.Item2));
                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("map_t<int8,array_t<int8>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[] { Tuple.Create((sbyte)2, new sbyte[] { 4, 5, 6 }), Tuple.Create((sbyte)1, new sbyte[] { 1, 2, 3 }) },
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<sbyte, sbyte[]>[] values) =>
                                {
                                    foreach (Tuple<sbyte, sbyte[]> value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                new TypeArgument(LayoutType.TypedTuple, typeArg.TypeArgs),
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, Tuple<sbyte, sbyte[]> values2) =>
                                                {
                                                    ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item1));
                                                    ResultAssert.IsSuccess(
                                                        writer3.WriteScope(
                                                            null,
                                                            typeArg2.TypeArgs[1],
                                                            values2.Item2,
                                                            (ref RowWriter writer4, TypeArgument typeArg3, sbyte[] values3) =>
                                                            {
                                                                foreach (sbyte value3 in values3)
                                                                {
                                                                    ResultAssert.IsSuccess(writer4.WriteInt8(null, value3));
                                                                }

                                                                return Result.Success;
                                                            }));

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("map_t<int16,map_t<int32,int32>>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[]
                                {
                                    Tuple.Create((short)2, new[] { Tuple.Create(7, 8), Tuple.Create(5, 6) }),
                                    Tuple.Create((short)1, new[] { Tuple.Create(3, 4), Tuple.Create(1, 2) }),
                                },
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<short, Tuple<int, int>[]>[] values) =>
                                {
                                    foreach (Tuple<short, Tuple<int, int>[]> value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                new TypeArgument(LayoutType.TypedTuple, typeArg.TypeArgs),
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, Tuple<short, Tuple<int, int>[]> values2) =>
                                                {
                                                    ResultAssert.IsSuccess(writer3.WriteInt16(null, values2.Item1));
                                                    ResultAssert.IsSuccess(
                                                        writer3.WriteScope(
                                                            null,
                                                            typeArg2.TypeArgs[1],
                                                            values2.Item2,
                                                            (ref RowWriter writer4, TypeArgument typeArg3, Tuple<int, int>[] values3) =>
                                                            {
                                                                foreach (Tuple<int, int> value3 in values3)
                                                                {
                                                                    ResultAssert.IsSuccess(
                                                                        writer4.WriteScope(
                                                                            null,
                                                                            new TypeArgument(LayoutType.TypedTuple, typeArg3.TypeArgs),
                                                                            value3,
                                                                            (ref RowWriter writer5, TypeArgument typeArg4, Tuple<int, int> values4) =>
                                                                            {
                                                                                ResultAssert.IsSuccess(writer5.WriteInt32(null, values4.Item1));
                                                                                ResultAssert.IsSuccess(writer5.WriteInt32(null, values4.Item2));
                                                                                return Result.Success;
                                                                            }));
                                                                }

                                                                return Result.Success;
                                                            }));

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        Assert.IsTrue(layout.TryFind("map_t<float64,udt>", out col));
                        ResultAssert.IsSuccess(
                            writer.WriteScope(
                                col.Path,
                                col.TypeArg,
                                new[]
                                {
                                    Tuple.Create(1.0, new RowReaderUnitTests.Point(1, 2)),
                                    Tuple.Create(2.0, new RowReaderUnitTests.Point(3, 4)),
                                    Tuple.Create(3.0, new RowReaderUnitTests.Point(5, 6)),
                                },
                                (ref RowWriter writer2, TypeArgument typeArg, Tuple<double, RowReaderUnitTests.Point>[] values) =>
                                {
                                    foreach (Tuple<double, RowReaderUnitTests.Point> value in values)
                                    {
                                        ResultAssert.IsSuccess(
                                            writer2.WriteScope(
                                                null,
                                                new TypeArgument(LayoutType.TypedTuple, typeArg.TypeArgs),
                                                value,
                                                (ref RowWriter writer3, TypeArgument typeArg2, Tuple<double, RowReaderUnitTests.Point> values2) =>
                                                {
                                                    ResultAssert.IsSuccess(writer3.WriteFloat64(null, values2.Item1));
                                                    ResultAssert.IsSuccess(
                                                        writer3.WriteScope(
                                                            null,
                                                            typeArg2.TypeArgs[1],
                                                            values2.Item2,
                                                            (ref RowWriter writer4, TypeArgument typeArg3, IRowSerializable values3) =>
                                                                values3.Write(ref writer4, typeArg3)));

                                                    return Result.Success;
                                                }));
                                    }

                                    return Result.Success;
                                }));

                        // Save the RowWriter length after everything is written for later comparison.
                        writerLength = writer.Length;
                        return Result.Success;
                    }));

            RowReader reader = new RowReader(ref row);
            Assert.AreEqual(reader.Length, writerLength);
            RowReaderUnitTests.PrintReader(ref reader, 0);
        }
    }
}
