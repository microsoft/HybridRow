//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.Float128;
import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import azure.data.cosmos.serialization.hybridrow.NullValue;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;
import azure.data.cosmos.serialization.hybridrow.UnixDateTime;

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

    public static void PrintReader(tangible.RefObject<RowReader> reader, int indent) {
        String str;
        tangible.OutObject<String> tempOut_str = new tangible.OutObject<String>();
        ResultAssert.IsSuccess(DiagnosticConverter.ReaderToString(reader, tempOut_str));
        str = tempOut_str.argValue;
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
        assert reader.getLength() == d.Row.getLength();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
        RowReaderUnitTests.PrintReader(tempRef_reader, 0);
        reader = tempRef_reader.argValue;
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
        row.InitLayout(HybridRowVersion.V1, layout, this.resolver);

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        ResultAssert.IsSuccess(RowWriter.WriteBuffer(tempRef_row, 2, RowReaderUnitTests.WriteNestedDocument));
        row = tempRef_row.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        RowReader rowReader = new RowReader(tempRef_row2);
        row = tempRef_row2.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_rowReader =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(tempRef_rowReader, 0));
        rowReader = tempRef_rowReader.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        rowReader = new RowReader(tempRef_row3);
        row = tempRef_row3.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_rowReader2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempRef_rowReader2, 0));
        rowReader = tempRef_rowReader2.argValue;

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row4 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        rowReader = new RowReader(tempRef_row4);
        row = tempRef_row4.argValue;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_rowReader3 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(rowReader);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegateWithSkipScope(tempRef_rowReader3, 0));
        rowReader = tempRef_rowReader3.argValue;

        // SkipScope not okay after advancing parent
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row5 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
        rowReader = new RowReader(tempRef_row5);
        row = tempRef_row5.argValue;
        assert rowReader.Read();
        assert rowReader.getType().LayoutCode == LayoutCode.ObjectScope;
        RowReader nestedScope = rowReader.ReadScope().clone();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nestedScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nestedScope);
        ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentDelegate(tempRef_nestedScope, 0));
        nestedScope = tempRef_nestedScope.argValue;
        assert rowReader.Read();
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nestedScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nestedScope);
        Result result = rowReader.SkipScope(tempRef_nestedScope2);
        nestedScope = tempRef_nestedScope2.argValue;
        assert Result.Success != result;
    }

    private static Result ReadNestedDocumentDelegate(tangible.RefObject<RowReader> reader, int context) {
        while (reader.argValue.Read()) {
            switch (reader.argValue.getType().LayoutCode) {
                case TupleScope: {
                    ResultAssert.IsSuccess(reader.argValue.ReadScope(0, RowReaderUnitTests.ReadTuplePartial));
                    break;
                }

                case ObjectScope: {
                    ResultAssert.IsSuccess(reader.argValue.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    break;
                }
            }
        }

        return Result.Success;
    }

    private static Result ReadNestedDocumentNonDelegate(tangible.RefObject<RowReader> reader, int context) {
        while (reader.argValue.Read()) {
            switch (reader.argValue.getType().LayoutCode) {
                case TupleScope: {
                    RowReader nested = reader.argValue.ReadScope().clone();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(tempRef_nested, 0));
                    nested = tempRef_nested.argValue;
                    break;
                }

                case ObjectScope: {
                    RowReader nested = reader.argValue.ReadScope().clone();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested2 =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempRef_nested2, 0));
                    nested = tempRef_nested2.argValue;
                    ResultAssert.IsSuccess(reader.argValue.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    break;
                }
            }
        }

        return Result.Success;
    }

    private static Result ReadNestedDocumentNonDelegateWithSkipScope(tangible.RefObject<RowReader> reader,
                                                                     int context) {
        while (reader.argValue.Read()) {
            switch (reader.argValue.getType().LayoutCode) {
                case TupleScope: {
                    RowReader nested = reader.argValue.ReadScope().clone();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadTuplePartial(tempRef_nested, 0));
                    nested = tempRef_nested.argValue;
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested2 =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(reader.argValue.SkipScope(tempRef_nested2));
                    nested = tempRef_nested2.argValue;
                    break;
                }

                case ObjectScope: {
                    RowReader nested = reader.argValue.ReadScope().clone();
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested3 =
                        new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(RowReaderUnitTests.ReadNestedDocumentNonDelegate(tempRef_nested3, 0));
                    nested = tempRef_nested3.argValue;
                    ResultAssert.IsSuccess(reader.argValue.ReadScope(0, RowReaderUnitTests.ReadNestedDocumentDelegate));
                    tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_nested4 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(nested);
                    ResultAssert.IsSuccess(reader.argValue.SkipScope(tempRef_nested4));
                    nested = tempRef_nested4.argValue;
                    break;
                }
            }
        }

        return Result.Success;
    }

    private static Result ReadTuplePartial(tangible.RefObject<RowReader> reader, int unused) {
        // Read only part of our tuple
        assert reader.argValue.Read();
        assert reader.argValue.Read();
        return Result.Success;
    }

    private static Result WriteNestedDocument(tangible.RefObject<RowWriter> writer, TypeArgument typeArgument,
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
            ResultAssert.IsSuccess(writer.argValue.WriteScope("x", tupleArgument.clone(), 0, WriteTuple));
            return Result.Success;
        }

        ResultAssert.IsSuccess(writer.argValue.WriteScope("a", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));
        ResultAssert.IsSuccess(writer.argValue.WriteScope("x", tupleArgument.clone(), 0, WriteTuple));
        ResultAssert.IsSuccess(writer.argValue.WriteScope("b", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));
        ResultAssert.IsSuccess(writer.argValue.WriteScope("y", tupleArgument.clone(), 0, WriteTuple));
        ResultAssert.IsSuccess(writer.argValue.WriteScope("c", new TypeArgument(LayoutType.Object), level - 1,
            RowReaderUnitTests.WriteNestedDocument));

        return Result.Success;
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

        public void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope) {
            dispatcher.argValue.LayoutCodeSwitch(scope, "x", value:this.X)
            dispatcher.argValue.LayoutCodeSwitch(scope, "y", value:this.Y)
        }

        public Result Write(tangible.RefObject<RowWriter> writer, TypeArgument typeArg) {
            Result result = writer.argValue.WriteInt32("x", this.X);
            if (result != Result.Success) {
                return result;
            }

            return writer.argValue.WriteInt32("y", this.Y);
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