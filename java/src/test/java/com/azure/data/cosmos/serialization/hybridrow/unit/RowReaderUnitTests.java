// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
// "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")][DeploymentItem(RowReaderUnitTests
// .SchemaFile, "TestData")] public sealed class RowReaderUnitTests
public final class RowReaderUnitTests {
    private static final LocalDateTime SampleDateTime = LocalDateTime.parse("2018-08-14 02:05:00.0000000");
    private static final Float128 SampleFloat128 = new Float128(0, 42);
    private static final UUID SampleGuid = UUID.fromString("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
    private static final MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(0, 42);
    private static final UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
    private static final String SchemaFile = "TestData\\ReaderSchema.json";
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        String json = Files.readString(RowReaderUnitTests.SchemaFile);
        this.schema = Namespace.Parse(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
    }

    public static void PrintReader(Reference<RowReader> reader, int indent) {
        String str;
        Out<String> tempOut_str = new Out<String>();
        ResultAssert.IsSuccess(DiagnosticConverter.ReaderToString(reader, tempOut_str));
        str = tempOut_str.get();
        System.out.println(str);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ReadMixed()
    public void ReadMixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Mixed")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<WriteRowDispatcher>Create(layout, this.resolver);
        d.LayoutCodeSwitch("null");
        d.LayoutCodeSwitch("bool", value:true)
        d.LayoutCodeSwitch("int8", value:(byte)-86)
        d.LayoutCodeSwitch("int16", value:(short)-21846)
        d.LayoutCodeSwitch("int32", value:-1431655766)
        d.LayoutCodeSwitch("int64", value:-6148914691236517206L)
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("uint8", value: (byte)0xAA);
        d.LayoutCodeSwitch("uint8", value:(byte)0xAA)
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("uint16", value: (ushort)0xAAAA);
        d.LayoutCodeSwitch("uint16", value:(short)0xAAAA)
        d.LayoutCodeSwitch("uint32", value:0xAAAAAAAA)
        d.LayoutCodeSwitch("uint64", value:0xAAAAAAAAAAAAAAAAL)
        d.LayoutCodeSwitch("float32", value:1.0F / 3.0F)
        d.LayoutCodeSwitch("float64", value:1.0 / 3.0)
        d.LayoutCodeSwitch("float128", value:RowReaderUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:RowReaderUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:RowReaderUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:RowReaderUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:RowReaderUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        d.LayoutCodeSwitch("utf8_span", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary_span", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        d.LayoutCodeSwitch("var_varint", value:-6148914691236517206L)
        d.LayoutCodeSwitch("var_varuint", value:0xAAAAAAAAAAAAAAAAL)
        d.LayoutCodeSwitch("var_utf8", value:"abc")
        d.LayoutCodeSwitch("var_utf8_span", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("var_binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("var_binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("var_binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("var_binary_span", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        d.LayoutCodeSwitch("sparse_null");
        d.LayoutCodeSwitch("sparse_bool", value:true)
        d.LayoutCodeSwitch("sparse_int8", value:(byte)-86)
        d.LayoutCodeSwitch("sparse_int16", value:(short)-21846)
        d.LayoutCodeSwitch("sparse_int32", value:-1431655766)
        d.LayoutCodeSwitch("sparse_int64", value:-6148914691236517206L)
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("sparse_uint8", value: (byte)0xAA);
        d.LayoutCodeSwitch("sparse_uint8", value:(byte)0xAA)
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("sparse_uint16", value: (ushort)0xAAAA);
        d.LayoutCodeSwitch("sparse_uint16", value:(short)0xAAAA)
        d.LayoutCodeSwitch("sparse_uint32", value:0xAAAAAAAA)
        d.LayoutCodeSwitch("sparse_uint64", value:0xAAAAAAAAAAAAAAAAL)
        d.LayoutCodeSwitch("sparse_float32", value:1.0F / 3.0F)
        d.LayoutCodeSwitch("sparse_float64", value:1.0 / 3.0)
        d.LayoutCodeSwitch("sparse_float128", value:RowReaderUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("sparse_decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("sparse_datetime", value:RowReaderUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("sparse_unixdatetime", value:RowReaderUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("sparse_guid", value:RowReaderUnitTests.SampleGuid)
        d.LayoutCodeSwitch("sparse_mongodbobjectid", value:RowReaderUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("sparse_utf8", value:"abc")
        d.LayoutCodeSwitch("sparse_utf8_span", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("sparse_binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("sparse_binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("sparse_binary_span", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("sparse_binary_span", value:new byte[] { (byte)0, (byte)1, (byte)2 })
        d.LayoutCodeSwitch("array_t<int8>", value:new byte[] { -86, -86, -86 })
        d.LayoutCodeSwitch("array_t<array_t<float32>>", value:new float[][]
            {
                new float[] { 1, 2, 3 },
                new float[] { 1, 2, 3 }
            })
        d.LayoutCodeSwitch("array_t<utf8>", value:new String[] { "abc", "def", "hij" })
        d.LayoutCodeSwitch("tuple<varint,int64>", value:Tuple.Create(-6148914691236517206L, -6148914691236517206L))
        d.LayoutCodeSwitch("tuple<null,tuple<int8,int8>>", value:
        Tuple.Create(NullValue.Default, Tuple.Create((byte)-86, (byte)-86)))
        d.LayoutCodeSwitch("tuple<bool,udt>", value:Tuple.Create(false, new Point(1, 2)))
        d.LayoutCodeSwitch("nullable<int32,int64>", value:Tuple.Create(null, (Long)123L))
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("tagged<utf8>", value: Tuple.Create((byte)3, "hello"));
        d.LayoutCodeSwitch("tagged<utf8>", value:Tuple.Create((byte)3, "hello"))
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("tagged<bool,utf8>", value: Tuple.Create((byte)5, true, "bye"));
        d.LayoutCodeSwitch("tagged<bool,utf8>", value:Tuple.Create((byte)5, true, "bye"))
        d.LayoutCodeSwitch("set_t<utf8>", value:new String[] { "abc", "efg", "xzy" })
        d.LayoutCodeSwitch("set_t<array_t<int8>>", value:new byte[][]
            {
                new byte[] { 1, 2, 3 },
                new byte[] { 4, 5, 6 },
                new byte[] { 7, 8, 9 }
            })
        d.LayoutCodeSwitch("set_t<set_t<int32>>", value:new int[][]
            {
                new int[] { 1, 2, 3 },
                new int[] { 4, 5, 6 },
                new int[] { 7, 8, 9 }
            })
        d.LayoutCodeSwitch("set_t<udt>", value:new Point[]
            {
                new Point(1, 2),
                new Point(3, 4),
                new Point(5, 6)
            })
        d.LayoutCodeSwitch("map_t<utf8,utf8>", value:
        new System.Tuple<T1, T2>[] { Tuple.Create("Mark", "Luke"), Tuple.Create("Harrison", "Han") })
        d.LayoutCodeSwitch("map_t<int8,array_t<int8>>", value:
        new System.Tuple<T1, T2>[] { Tuple.Create((byte)1, new byte[] { 1, 2, 3 }), Tuple.Create((byte)2,
            new byte[] { 4, 5, 6 }) })

        d.LayoutCodeSwitch("map_t<int16,map_t<int32,int32>>", value:
        new System.Tuple<T1, T2>[] { Tuple.Create((short)1, new System.Tuple<T1, T2>[] { Tuple.Create(1, 2),
            Tuple.Create(3, 4) }), Tuple.Create((short)2, new System.Tuple<T1, T2>[] { Tuple.Create(5, 6),
            Tuple.Create(7, 8) }) })

        d.LayoutCodeSwitch("map_t<float64,udt>", value:
        new System.Tuple<T1, T2>[] { Tuple.Create(1.0, new Point(1, 2)), Tuple.Create(2.0, new Point(3, 4)),
            Tuple.Create(3.0, new Point(5, 6)) })

        RowReader reader = d.GetReader().clone();
        assert reader.length() == d.Row.length();
        Reference<RowReader> tempReference_reader =
            new Reference<RowReader>(reader);
        RowReaderUnitTests.PrintReader(tempReference_reader, 0);
        reader = tempReference_reader.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void ReadScopes()
    public void ReadScopes() {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(0);
        MemorySpanResizer<Byte> resizer = new MemorySpanResizer<Byte>(0);
        RowBuffer row = new RowBuffer(0, resizer);
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Mixed")).SchemaId);
        row.initLayout(HybridRowVersion.V1, layout, this.resolver);

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempReference_row, 2, RowReaderUnitTests.WriteNestedDocument));
        row = tempReference_row.get();
        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(row);
        RowReader rowReader = new RowReader(tempReference_row2);
        row = tempReference_row2.get();

        Reference<RowReader> tempReference_rowReader =
            new Reference<RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(tempReference_rowReader, 0));
        rowReader = tempReference_rowReader.get();

        Reference<RowBuffer> tempReference_row3 =
            new Reference<RowBuffer>(row);
        rowReader = new RowReader(tempReference_row3);
        row = tempReference_row3.get();
        Reference<RowReader> tempReference_rowReader2 =
            new Reference<RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempReference_rowReader2, 0));
        rowReader = tempReference_rowReader2.get();

        Reference<RowBuffer> tempReference_row4 =
            new Reference<RowBuffer>(row);
        rowReader = new RowReader(tempReference_row4);
        row = tempReference_row4.get();
        Reference<RowReader> tempReference_rowReader3 =
            new Reference<RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegateWithSkipScope(tempReference_rowReader3, 0));
        rowReader = tempReference_rowReader3.get();

        // SkipScope not okay after advancing parent
        Reference<RowBuffer> tempReference_row5 =
            new Reference<RowBuffer>(row);
        rowReader = new RowReader(tempReference_row5);
        row = tempReference_row5.get();
        assert rowReader.read();
        assert rowReader.type().LayoutCode == LayoutCode.ObjectScope;
        RowReader nestedScope = rowReader.readScope().clone();
        Reference<RowReader> tempReference_nestedScope =
            new Reference<RowReader>(nestedScope);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(tempReference_nestedScope, 0));
        nestedScope = tempReference_nestedScope.get();
        assert rowReader.read();
        Reference<RowReader> tempReference_nestedScope2 =
            new Reference<RowReader>(nestedScope);
        Result result = rowReader.SkipScope(tempReference_nestedScope2);
        nestedScope = tempReference_nestedScope2.get();
        assert Result.SUCCESS != result;
    }

    private static Result ReadNestedDocumentDelegate(Reference<RowReader> reader, int context) {
        while (reader.get().read()) {
            switch (reader.get().type().LayoutCode) {
                case TupleScope: {
                    ResultAssert.IsSuccess(reader.get().ReadScope(0, RowReaderUnitTests.ReadTuplePartial));
                    break;
                }

                case ObjectScope: {
                    ResultAssert.IsSuccess(reader.get().ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    break;
                }
            }
        }

        return Result.SUCCESS;
    }

    private static Result ReadNestedDocumentNonDelegate(Reference<RowReader> reader, int context) {
        while (reader.get().read()) {
            switch (reader.get().type().LayoutCode) {
                case TupleScope: {
                    RowReader nested = reader.get().readScope().clone();
                    Reference<RowReader> tempReference_nested =
                        new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(tempReference_nested, 0));
                    nested = tempReference_nested.get();
                    break;
                }

                case ObjectScope: {
                    RowReader nested = reader.get().readScope().clone();
                    Reference<RowReader> tempReference_nested2 =
                        new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempReference_nested2, 0));
                    nested = tempReference_nested2.get();
                    ResultAssert.IsSuccess(reader.get().ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    break;
                }
            }
        }

        return Result.SUCCESS;
    }

    private static Result ReadNestedDocumentNonDelegateWithSkipScope(Reference<RowReader> reader,
                                                                     int context) {
        while (reader.get().read()) {
            switch (reader.get().type().LayoutCode) {
                case TupleScope: {
                    RowReader nested = reader.get().readScope().clone();
                    Reference<RowReader> tempReference_nested =
                        new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(tempReference_nested, 0));
                    nested = tempReference_nested.get();
                    Reference<RowReader> tempReference_nested2 =
                        new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(reader.get().SkipScope(tempReference_nested2));
                    nested = tempReference_nested2.get();
                    break;
                }

                case ObjectScope: {
                    RowReader nested = reader.get().readScope().clone();
                    Reference<RowReader> tempReference_nested3 =
                        new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempReference_nested3, 0));
                    nested = tempReference_nested3.get();
                    ResultAssert.IsSuccess(reader.get().ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    Reference<RowReader> tempReference_nested4 = new Reference<RowReader>(nested);
                    ResultAssert.IsSuccess(reader.get().SkipScope(tempReference_nested4));
                    nested = tempReference_nested4.get();
                    break;
                }
            }
        }

        return Result.SUCCESS;
    }

    private static Result ReadTuplePartial(Reference<RowReader> reader, int unused) {
        // Read only part of our tuple
        assert reader.get().read();
        assert reader.get().read();
        return Result.SUCCESS;
    }

    private static Result WriteNestedDocument(Reference<RowWriter> writer, TypeArgument typeArgument,
                                              int level) {
        TypeArgument tupleArgument = new TypeArgument(LayoutType.Tuple, new TypeArgumentList(new TypeArgument[]
            {
                new TypeArgument(LayoutType.Int32),
                new TypeArgument(LayoutType.Int32),
                new TypeArgument(LayoutType.Int32)
            }));

        // TODO: C# TO JAVA CONVERTER: Local functions are not converted by C# to Java Converter:
        //		Result WriteTuple(ref RowWriter tupleWriter, TypeArgument tupleTypeArgument, int unused)
        //			{
        //				ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 1));
        //				ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 2));
        //				ResultAssert.IsSuccess(tupleWriter.WriteInt32(null, 3));
        //				return Result.Success;
        //			}

        if (level == 0) {
            ResultAssert.IsSuccess(writer.get().WriteScope("x", tupleArgument.clone(), 0, WriteTuple));
            return Result.SUCCESS;
        }

        ResultAssert.IsSuccess(writer.get().WriteScope("a", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));
        ResultAssert.IsSuccess(writer.get().WriteScope("x", tupleArgument.clone(), 0, WriteTuple));
        ResultAssert.IsSuccess(writer.get().WriteScope("b", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));
        ResultAssert.IsSuccess(writer.get().WriteScope("y", tupleArgument.clone(), 0, WriteTuple));
        ResultAssert.IsSuccess(writer.get().WriteScope("c", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));

        return Result.SUCCESS;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")] internal sealed class Point : IDispatchable, IRowSerializable
    public final static class Point implements IDispatchable, IRowSerializable {
        public int X;
        public int Y;

        public Point(int x, int y) {
            this.X = x;
            this.Y = y;
        }

        public void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope) {
            dispatcher.get().LayoutCodeSwitch(scope, "x", value:this.X)
            dispatcher.get().LayoutCodeSwitch(scope, "y", value:this.Y)
        }

        public Result Write(Reference<RowWriter> writer, TypeArgument typeArg) {
            Result result = writer.get().WriteInt32("x", this.X);
            if (result != Result.SUCCESS) {
                return result;
            }

            return writer.get().WriteInt32("y", this.Y);
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) {
                return false;
            }

            if (this == obj) {
                return true;
            }

            return obj instanceof Point && this.equals((Point)obj);
        }

        @Override
        public int hashCode() {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
            unchecked
            {
                return ((new Integer(this.X)).hashCode() * 397) ^ (new Integer(this.Y)).hashCode();
            }
        }

        private boolean equals(Point other) {
            return this.X == other.X && this.Y == other.Y;
        }
    }
}