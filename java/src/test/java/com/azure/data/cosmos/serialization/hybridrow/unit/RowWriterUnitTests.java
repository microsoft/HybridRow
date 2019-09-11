// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
// "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")][DeploymentItem(RowWriterUnitTests
// .SchemaFile, "TestData")] public sealed class RowWriterUnitTests
public final class RowWriterUnitTests {
    private static final int InitialRowSize = 2 * 1024 * 1024;
    private static final LocalDateTime SampleDateTime = LocalDateTime.parse("2018-08-14 02:05:00.0000000");
    private static final Float128 SampleFloat128 = new Float128(0, 42);
    private static final UUID SampleGuid = UUID.fromString("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
    private static final MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(0, 42);
    private static final UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
    private static final String SchemaFile = "TestData\\ReaderSchema.json";
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void TestInitialize()
    public void TestInitialize() {
        String json = Files.readString(RowWriterUnitTests.SchemaFile);
        this.schema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void WriteMixed()
    public void WriteMixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Mixed")).SchemaId);
        assert layout != null;

        RowBuffer row = new RowBuffer(RowWriterUnitTests.InitialRowSize);
        row.initLayout(HybridRowVersion.V1, layout, this.resolver);

        int writerLength = 0;
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempReference_row, null, (RowWriter RowWriter writer,
                                                                               TypeArgument rootTypeArg, Object ignored) ->
        {
            ResultAssert.IsSuccess(writer.WriteNull("null"));
            ResultAssert.IsSuccess(writer.WriteBool("bool", true));
            ResultAssert.IsSuccess(writer.WriteInt8("int8", (byte)-86));
            ResultAssert.IsSuccess(writer.WriteInt16("int16", (short)-21846));
            ResultAssert.IsSuccess(writer.WriteInt32("int32", -1431655766));
            ResultAssert.IsSuccess(writer.WriteInt64("int64", -6148914691236517206L));
            ResultAssert.IsSuccess(writer.WriteUInt8("uint8", (byte)0xAA));
            ResultAssert.IsSuccess(writer.WriteUInt16("uint16", (short)0xAAAA));
            ResultAssert.IsSuccess(writer.WriteUInt32("uint32", 0xAAAAAAAA));
            ResultAssert.IsSuccess(writer.WriteUInt64("uint64", 0xAAAAAAAAAAAAAAAAL));
            ResultAssert.IsSuccess(writer.WriteFloat32("float32", 1.0F / 3.0F));
            ResultAssert.IsSuccess(writer.WriteFloat64("float64", 1.0 / 3.0));
            ResultAssert.IsSuccess(writer.WriteFloat128("float128", RowWriterUnitTests.SampleFloat128));
            ResultAssert.IsSuccess(writer.WriteDecimal("decimal", java.math.BigDecimal.ONE / 3.0));
            ResultAssert.IsSuccess(writer.WriteDateTime("datetime", RowWriterUnitTests.SampleDateTime));
            ResultAssert.IsSuccess(writer.WriteUnixDateTime("unixdatetime", RowWriterUnitTests.SampleUnixDateTime));
            ResultAssert.IsSuccess(writer.WriteGuid("guid", RowWriterUnitTests.SampleGuid));
            ResultAssert.IsSuccess(writer.WriteMongoDbObjectId("mongodbobjectid",
                RowWriterUnitTests.SampleMongoDbObjectId));
            ResultAssert.IsSuccess(writer.WriteString("utf8", "abc"));
            ResultAssert.IsSuccess(writer.WriteString("utf8_span", Utf8Span.TranscodeUtf16("abc")));
            ResultAssert.IsSuccess(writer.WriteBinary("binary", new byte[] { (byte)0, (byte)1, (byte)2 }));
            ResultAssert.IsSuccess(writer.WriteBinary("binary_span",
                new byte[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
            ResultAssert.IsSuccess(writer.WriteBinary("binary_sequence",
                new ReadOnlySequence<Byte>(new byte[] { (byte)0, (byte)1, (byte)2 })));

            ResultAssert.IsSuccess(writer.WriteVarInt("var_varint", -6148914691236517206L));
            ResultAssert.IsSuccess(writer.WriteVarUInt("var_varuint", 0xAAAAAAAAAAAAAAAAL));
            ResultAssert.IsSuccess(writer.WriteString("var_utf8", "abc"));
            ResultAssert.IsSuccess(writer.WriteString("var_utf8_span", Utf8Span.TranscodeUtf16("abc")));
            ResultAssert.IsSuccess(writer.WriteBinary("var_binary", new byte[] { (byte)0, (byte)1, (byte)2 }));
            ResultAssert.IsSuccess(writer.WriteBinary("var_binary_span",
                new byte[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
            ResultAssert.IsSuccess(writer.WriteBinary("var_binary_sequence",
                new ReadOnlySequence<Byte>(new byte[] { (byte)0, (byte)1, (byte)2 })));

            ResultAssert.IsSuccess(writer.WriteNull("sparse_null"));
            ResultAssert.IsSuccess(writer.WriteBool("sparse_bool", true));
            ResultAssert.IsSuccess(writer.WriteInt8("sparse_int8", (byte)-86));
            ResultAssert.IsSuccess(writer.WriteInt16("sparse_int16", (short)-21846));
            ResultAssert.IsSuccess(writer.WriteInt32("sparse_int32", -1431655766));
            ResultAssert.IsSuccess(writer.WriteInt64("sparse_int64", -6148914691236517206L));
            ResultAssert.IsSuccess(writer.WriteUInt8("sparse_uint8", (byte)0xAA));
            ResultAssert.IsSuccess(writer.WriteUInt16("sparse_uint16", (short)0xAAAA));
            ResultAssert.IsSuccess(writer.WriteUInt32("sparse_uint32", 0xAAAAAAAA));
            ResultAssert.IsSuccess(writer.WriteUInt64("sparse_uint64", 0xAAAAAAAAAAAAAAAAL));
            ResultAssert.IsSuccess(writer.WriteFloat32("sparse_float32", 1.0F / 3.0F));
            ResultAssert.IsSuccess(writer.WriteFloat64("sparse_float64", 1.0 / 3.0));
            ResultAssert.IsSuccess(writer.WriteFloat128("sparse_float128", RowWriterUnitTests.SampleFloat128));
            ResultAssert.IsSuccess(writer.WriteDecimal("sparse_decimal", java.math.BigDecimal.ONE / 3.0));
            ResultAssert.IsSuccess(writer.WriteDateTime("sparse_datetime", RowWriterUnitTests.SampleDateTime));
            ResultAssert.IsSuccess(writer.WriteUnixDateTime("sparse_unixdatetime",
                RowWriterUnitTests.SampleUnixDateTime));
            ResultAssert.IsSuccess(writer.WriteGuid("sparse_guid", RowWriterUnitTests.SampleGuid));
            ResultAssert.IsSuccess(writer.WriteMongoDbObjectId("sparse_mongodbobjectid",
                RowWriterUnitTests.SampleMongoDbObjectId));
            ResultAssert.IsSuccess(writer.WriteString("sparse_utf8", "abc"));
            ResultAssert.IsSuccess(writer.WriteString("sparse_utf8_span", Utf8Span.TranscodeUtf16("abc")));
            ResultAssert.IsSuccess(writer.WriteBinary("sparse_binary", new byte[] { (byte)0, (byte)1, (byte)2 }));
            ResultAssert.IsSuccess(writer.WriteBinary("sparse_binary_span",
                new byte[] { (byte)0, (byte)1, (byte)2 }.AsSpan()));
            ResultAssert.IsSuccess(writer.WriteBinary("sparse_binary_sequence",
                new ReadOnlySequence<Byte>(new byte[] { (byte)0, (byte)1, (byte)2 })));

            LayoutColumn col;
            Out<LayoutColumn> tempOut_col =
                new Out<LayoutColumn>();
            assert layout.TryFind("array_t<int8>", tempOut_col);
            col = tempOut_col.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new byte[] { -86, -87,
                -88 }, (ref RowWriter writer2, TypeArgument typeArg, byte[] values) ->
            {
                for (byte value : values) {
                    ResultAssert.IsSuccess(writer2.WriteInt8(null, value));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col2 =
                new Out<LayoutColumn>();
            assert layout.TryFind("array_t<array_t<float32>>", tempOut_col2);
            col = tempOut_col2.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new float[][]
                {
                    new float[] { 1, 2, 3 },
                    new float[] { 1, 2, 3 }
                }, (ref RowWriter writer2, TypeArgument typeArg, float[][] values) ->
            {
                for (float[] value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(0).clone(), value,
                        (ref RowWriter writer3, TypeArgument typeArg2, float[] values2) ->
                    {
                        for (float value2 : values2) {
                            ResultAssert.IsSuccess(writer3.WriteFloat32(null, value2));
                        }

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col3 =
                new Out<LayoutColumn>();
            assert layout.TryFind("array_t<utf8>", tempOut_col3);
            col = tempOut_col3.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new String[] { "abc",
                "def", "hij" }, (ref RowWriter writer2, TypeArgument typeArg, String[] values) ->
            {
                for (String value : values) {
                    ResultAssert.IsSuccess(writer2.WriteString(null, value));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col4 =
                new Out<LayoutColumn>();
            assert layout.TryFind("tuple<varint,int64>", tempOut_col4);
            col = tempOut_col4.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(),
                Tuple.Create(-6148914691236517206L, -6148914691236517206L), (ref RowWriter writer2,
                                                                             TypeArgument typeArg,
                                                                             Tuple<Long, Long> values) ->
            {
                ResultAssert.IsSuccess(writer2.WriteVarInt(null, values.Item1));
                ResultAssert.IsSuccess(writer2.WriteInt64(null, values.Item2));
                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col5 =
                new Out<LayoutColumn>();
            assert layout.TryFind("tuple<null,tuple<int8,int8>>", tempOut_col5);
            col = tempOut_col5.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(),
                Tuple.Create(NullValue.DEFAULT, Tuple.Create((byte)-86, (byte)-86)), (ref RowWriter writer2,
                                                                                      TypeArgument typeArg,
                                                                                      Tuple<NullValue, Tuple<Byte,
                                                                                          Byte>> values) ->
            {
                ResultAssert.IsSuccess(writer2.WriteNull(null));
                ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(1).clone(), values.Item2,
                    (ref RowWriter writer3, TypeArgument typeArg2, Tuple<Byte, Byte> values2) ->
                {
                    ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item1));
                    ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item2));
                    return Result.SUCCESS;
                }));

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col6 =
                new Out<LayoutColumn>();
            assert layout.TryFind("tuple<bool,udt>", tempOut_col6);
            col = tempOut_col6.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), Tuple.Create(false,
                new RowReaderUnitTests.Point(1, 2)), (ref RowWriter writer2, TypeArgument typeArg, Tuple<Boolean,
                RowReaderUnitTests.Point> values) ->
            {
                ResultAssert.IsSuccess(writer2.WriteBool(null, values.Item1));
                Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer3 =
                    new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer3);
                ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(1).clone(), values.Item2,
                    (ref RowWriter writer3, TypeArgument typeArg2, IRowSerializable values2) -> values2.Write(tempReference_writer3, typeArg2.clone())));
                writer3 = tempReference_writer3.get();

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col7 =
                new Out<LayoutColumn>();
            assert layout.TryFind("nullable<int32,int64>", tempOut_col7);
            col = tempOut_col7.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), Tuple.Create(null,
                (Long)123L), (ref RowWriter writer2, TypeArgument typeArg, Tuple<Integer, Long> values) ->
            {
                RowWriter.WriterFunc<Integer> f0 = (com.azure.data.cosmos.serialization.hybridrow.io.RowWriter writer, TypeArgument typeArg,
                                                    Integer context) -> null.invoke(writer, typeArg.clone(), context);
                if (values.Item1 != null) {
                    f0 = (com.azure.data.cosmos.serialization.hybridrow.io.RowWriter RowWriter writer3, TypeArgument typeArg2, Integer value) -> writer3.WriteInt32(null,
                        value.intValue());
                }

                ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(0).clone(), values.Item1,
                    f0));

                RowWriter.WriterFunc<Long> f1 = (com.azure.data.cosmos.serialization.hybridrow.io.RowWriter writer, TypeArgument typeArg,
                                                 Long context) -> null.invoke(writer, typeArg.clone(), context);
                if (values.Item2 != null) {
                    f1 = (com.azure.data.cosmos.serialization.hybridrow.io.RowWriter RowWriter writer3, TypeArgument typeArg2, Long value) -> writer3.WriteInt64(null,
                        value.longValue());
                }

                ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(1).clone(), values.Item2,
                    f1));
                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col8 =
                new Out<LayoutColumn>();
            assert layout.TryFind("tagged<utf8>", tempOut_col8);
            col = tempOut_col8.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), Tuple.Create((byte)3,
                "hello"), (ref RowWriter writer2, TypeArgument typeArg, Tuple<Byte, String> values) ->
            {
                ResultAssert.IsSuccess(writer2.WriteUInt8(null, values.Item1));
                ResultAssert.IsSuccess(writer2.WriteString(null, values.Item2));
                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col9 =
                new Out<LayoutColumn>();
            assert layout.TryFind("tagged<bool,utf8>", tempOut_col9);
            col = tempOut_col9.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), Tuple.Create((byte)5,
                true, "bye"), (ref RowWriter writer2, TypeArgument typeArg, Tuple<Byte, Boolean, String> values) ->
            {
                ResultAssert.IsSuccess(writer2.WriteUInt8(null, values.Item1));
                ResultAssert.IsSuccess(writer2.WriteBool(null, values.Item2));
                ResultAssert.IsSuccess(writer2.WriteString(null, values.Item3));
                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col10 =
                new Out<LayoutColumn>();
            assert layout.TryFind("set_t<utf8>", tempOut_col10);
            col = tempOut_col10.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new String[] { "abc",
                "xzy", "efg" }, (ref RowWriter writer2, TypeArgument typeArg, String[] values) ->
            {
                for (String value : values) {
                    ResultAssert.IsSuccess(writer2.WriteString(null, value));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col11 =
                new Out<LayoutColumn>();
            assert layout.TryFind("set_t<array_t<int8>>", tempOut_col11);
            col = tempOut_col11.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new byte[][]
                {
                    new byte[] { 7, 8, 9 },
                    new byte[] { 4, 5, 6 },
                    new byte[] { 1, 2, 3 }
                }, (ref RowWriter writer2, TypeArgument typeArg, byte[][] values) ->
            {
                for (byte[] value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(0).clone(), value,
                        (ref RowWriter writer3, TypeArgument typeArg2, byte[] values2) ->
                    {
                        for (byte value2 : values2) {
                            ResultAssert.IsSuccess(writer3.WriteInt8(null, value2));
                        }

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col12 =
                new Out<LayoutColumn>();
            assert layout.TryFind("set_t<set_t<int32>>", tempOut_col12);
            col = tempOut_col12.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new int[][]
                {
                    new int[] { 4, 5, 6 },
                    new int[] { 7, 8, 9 },
                    new int[] { 1, 2, 3 }
                }, (ref RowWriter writer2, TypeArgument typeArg, int[][] values) ->
            {
                for (int[] value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(0).clone(), value,
                        (ref RowWriter writer3, TypeArgument typeArg2, int[] values2) ->
                    {
                        for (int value2 : values2) {
                            ResultAssert.IsSuccess(writer3.WriteInt32(null, value2));
                        }

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col13 =
                new Out<LayoutColumn>();
            assert layout.TryFind("set_t<udt>", tempOut_col13);
            col = tempOut_col13.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(),
                new RowReaderUnitTests.Point[]
                {
                    new RowReaderUnitTests.Point(1, 2),
                    new RowReaderUnitTests.Point(3, 4),
                    new RowReaderUnitTests.Point(5, 6)
                }, (ref RowWriter writer2, TypeArgument typeArg, RowReaderUnitTests.Point[] values) ->
            {
                for (RowReaderUnitTests.Point value : values) {
                    Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer3 =
                        new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer3);
                    ResultAssert.IsSuccess(writer2.WriteScope(null, typeArg.getTypeArgs().get(0).clone(), value,
                        (ref RowWriter writer3, TypeArgument typeArg2, IRowSerializable values2) -> values2.Write(tempReference_writer3, typeArg2.clone())));
                    writer3 = tempReference_writer3.get();
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col14 =
                new Out<LayoutColumn>();
            assert layout.TryFind("map_t<utf8,utf8>", tempOut_col14);
            col = tempOut_col14.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new System.Tuple<T1,
                T2>[] { Tuple.Create("Harrison", "Han"), Tuple.Create("Mark", "Luke") }, (ref RowWriter writer2,
                                                                                          TypeArgument typeArg,
                                                                                          Tuple<String, String>[] values) ->
            {
                for (Tuple<String, String> value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, new TypeArgument(LayoutType.TypedTuple,
                        typeArg.getTypeArgs().clone()), value, (ref RowWriter writer3, TypeArgument typeArg2,
                                                                Tuple<String, String> values2) ->
                    {
                        ResultAssert.IsSuccess(writer3.WriteString(null, values2.Item1));
                        ResultAssert.IsSuccess(writer3.WriteString(null, values2.Item2));
                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col15 =
                new Out<LayoutColumn>();
            assert layout.TryFind("map_t<int8,array_t<int8>>", tempOut_col15);
            col = tempOut_col15.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new System.Tuple<T1,
                T2>[] { Tuple.Create((byte)2, new byte[] { 4, 5, 6 }),
                Tuple.Create((byte)1, new byte[] { 1, 2, 3 }) }, (ref RowWriter writer2, TypeArgument typeArg,
                                                                  Tuple<Byte, byte[]>[] values) ->
            {
                for (Tuple<Byte, byte[]> value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, new TypeArgument(LayoutType.TypedTuple,
                        typeArg.getTypeArgs().clone()), value, (ref RowWriter writer3, TypeArgument typeArg2,
                                                                Tuple<Byte, byte[]> values2) ->
                    {
                        ResultAssert.IsSuccess(writer3.WriteInt8(null, values2.Item1));
                        ResultAssert.IsSuccess(writer3.WriteScope(null, typeArg2.getTypeArgs().get(1).clone(),
                            values2.Item2, (ref RowWriter writer4, TypeArgument typeArg3, byte[] values3) ->
                        {
                            for (byte value3 : values3) {
                                ResultAssert.IsSuccess(writer4.WriteInt8(null, value3));
                            }

                            return Result.SUCCESS;
                        }));

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col16 =
                new Out<LayoutColumn>();
            assert layout.TryFind("map_t<int16,map_t<int32,int32>>", tempOut_col16);
            col = tempOut_col16.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new System.Tuple<T1,
                T2>[] { Tuple.Create((short)2, new System.Tuple<T1, T2>[] { Tuple.Create(7, 8), Tuple.Create(5, 6) })
                , Tuple.Create((short)1, new System.Tuple<T1, T2>[] { Tuple.Create(3, 4), Tuple.Create(1, 2) }) },
                (ref RowWriter writer2, TypeArgument typeArg, Tuple<Short, Tuple<Integer, Integer>[]>[] values) ->
            {
                for (Tuple<Short, Tuple<Integer, Integer>[]> value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, new TypeArgument(LayoutType.TypedTuple,
                        typeArg.getTypeArgs().clone()), value, (ref RowWriter writer3, TypeArgument typeArg2,
                                                                Tuple<Short, Tuple<Integer, Integer>[]> values2) ->
                    {
                        ResultAssert.IsSuccess(writer3.WriteInt16(null, values2.Item1));
                        ResultAssert.IsSuccess(writer3.WriteScope(null, typeArg2.getTypeArgs().get(1).clone(),
                            values2.Item2, (ref RowWriter writer4, TypeArgument typeArg3,
                                            Tuple<Integer, Integer>[] values3) ->
                        {
                            for (Tuple<Integer, Integer> value3 : values3) {
                                ResultAssert.IsSuccess(writer4.WriteScope(null,
                                    new TypeArgument(LayoutType.TypedTuple, typeArg3.getTypeArgs().clone()), value3,
                                    (ref RowWriter writer5, TypeArgument typeArg4, Tuple<Integer, Integer> values4) ->
                                {
                                    ResultAssert.IsSuccess(writer5.WriteInt32(null, values4.Item1));
                                    ResultAssert.IsSuccess(writer5.WriteInt32(null, values4.Item2));
                                    return Result.SUCCESS;
                                }));
                            }

                            return Result.SUCCESS;
                        }));

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            Out<LayoutColumn> tempOut_col17 =
                new Out<LayoutColumn>();
            assert layout.TryFind("map_t<float64,udt>", tempOut_col17);
            col = tempOut_col17.get();
            ResultAssert.IsSuccess(writer.WriteScope(col.path(), col.typeArg().clone(), new System.Tuple<T1, T2>[] { Tuple.Create(1.0, new RowReaderUnitTests.Point(1, 2)), Tuple.Create(2.0, new RowReaderUnitTests.Point(3, 4)), Tuple.Create(3.0, new RowReaderUnitTests.Point(5, 6)) }, (ref RowWriter writer2, TypeArgument typeArg, Tuple<Double, RowReaderUnitTests.Point>[] values) ->
            {
                for (Tuple<Double, RowReaderUnitTests.Point> value : values) {
                    ResultAssert.IsSuccess(writer2.WriteScope(null, new TypeArgument(LayoutType.TypedTuple, typeArg.getTypeArgs().clone()), value, (ref RowWriter writer3, TypeArgument typeArg2, Tuple<Double, RowReaderUnitTests.Point> values2) ->
                    {
                        ResultAssert.IsSuccess(writer3.WriteFloat64(null, values2.Item1));
                        Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter> tempReference_writer4 = new Reference<com.azure.data.cosmos.serialization.hybridrow.io.RowWriter>(writer4);
                        ResultAssert.IsSuccess(writer3.WriteScope(null, typeArg2.getTypeArgs().get(1).clone(), values2.Item2, (ref RowWriter writer4, TypeArgument typeArg3, IRowSerializable values3) -> values3.Write(tempReference_writer4, typeArg3.clone())));
                        writer4 = tempReference_writer4.get();

                        return Result.SUCCESS;
                    }));
                }

                return Result.SUCCESS;
            }));

            // Save the RowWriter length after everything is written for later comparison.
            writerLength = writer.Length;
            return Result.SUCCESS;
        }));
        row = tempReference_row.get();

        Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row2);
        row = tempReference_row2.get();
        assert reader.length() == writerLength;
        Reference<RowReader> tempReference_reader = new Reference<RowReader>(reader);
        RowReaderUnitTests.PrintReader(tempReference_reader, 0);
        reader = tempReference_reader.get();
    }
}