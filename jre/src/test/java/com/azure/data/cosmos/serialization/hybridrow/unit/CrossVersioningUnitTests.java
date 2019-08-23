//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import Newtonsoft.Json.*;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Public Fields


// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here
// are anonymous.")][DeploymentItem(CrossVersioningUnitTests.SchemaFile, "TestData")][DeploymentItem
// (CrossVersioningUnitTests.ExpectedFile, "TestData")] public sealed class CrossVersioningUnitTests
public final class CrossVersioningUnitTests {
    private static final String ExpectedFile = "TestData\\CrossVersioningExpected.json";
    private static final LocalDateTime SampleDateTime = LocalDateTime.parse("2018-08-14 02:05:00.0000000");
    private static final Float128 SampleFloat128 = new Float128(0, 42);
    private static final UUID SampleGuid = UUID.fromString("{2A9C25B9-922E-4611-BB0A-244A9496503C}");
    private static final MongoDbObjectId SampleMongoDbObjectId = new MongoDbObjectId(704643072, 0); // 42 in big-endian
    private static final UnixDateTime SampleUnixDateTime = new UnixDateTime(42);
    private static final String SchemaFile = "TestData\\CrossVersioningSchema.json";
    private Expected expected;
    private LayoutResolver resolver;
    private Namespace schema;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
    // "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")] public void CrossVersionDeleteSparse()
    public void CrossVersionDeleteSparse() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Sparse")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<DeleteRowDispatcher>ReadFrom(this.resolver,
            this.expected.getCrossVersionSparse());
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
        d.LayoutCodeSwitch("float128", value:CrossVersioningUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:CrossVersioningUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:CrossVersioningUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:CrossVersioningUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:CrossVersioningUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
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

        assert this.expected.getCrossVersionNullSparse() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionReadFixed()
    public void CrossVersionReadFixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Fixed")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<ReadRowDispatcher>ReadFrom(this.resolver,
            this.expected.getCrossVersionFixed());
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
        d.LayoutCodeSwitch("float128", value:CrossVersioningUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:CrossVersioningUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:CrossVersioningUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:CrossVersioningUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:CrossVersioningUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionReadNullFixed()
    public void CrossVersionReadNullFixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Fixed")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<NullRowDispatcher>Create(layout, this.resolver);
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

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionReadNullSparse()
    public void CrossVersionReadNullSparse() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Sparse")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<NullRowDispatcher>ReadFrom(this.resolver,
            this.expected.getCrossVersionNullSparse());
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

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionReadNullVariable()
    public void CrossVersionReadNullVariable() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Variable")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<NullRowDispatcher>Create(layout, this.resolver);
        d.LayoutCodeSwitch("varint");
        d.LayoutCodeSwitch("varuint");
        d.LayoutCodeSwitch("utf8");
        d.LayoutCodeSwitch("binary");
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
    // "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")] public void CrossVersionReadSparse()
    public void CrossVersionReadSparse() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Sparse")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<ReadRowDispatcher>ReadFrom(this.resolver,
            this.expected.getCrossVersionSparse());
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
        d.LayoutCodeSwitch("float128", value:CrossVersioningUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:CrossVersioningUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:CrossVersioningUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:CrossVersioningUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:CrossVersioningUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
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
        new System.Tuple<T1, T2>[] { Tuple.Create(2.0, new Point(3, 4)), Tuple.Create(3.0, new Point(5, 6)),
            Tuple.Create(1.0, new Point(1, 2)) })
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionReadVariable()
    public void CrossVersionReadVariable() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Variable")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<ReadRowDispatcher>ReadFrom(this.resolver,
            this.expected.getCrossVersionVariable());
        d.LayoutCodeSwitch("varint", value:-6148914691236517206L)
        d.LayoutCodeSwitch("varuint", value:0xAAAAAAAAAAAAAAAAL)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionWriteFixed()
    public void CrossVersionWriteFixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Fixed")).SchemaId);
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
        d.LayoutCodeSwitch("float128", value:CrossVersioningUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:CrossVersioningUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:CrossVersioningUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:CrossVersioningUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:CrossVersioningUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })

        assert this.expected.getCrossVersionFixed() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionWriteNullFixed()
    public void CrossVersionWriteNullFixed() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Fixed")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<WriteRowDispatcher>Create(layout, this.resolver);
        assert this.expected.getCrossVersionNullFixed() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionWriteNullSparse()
    public void CrossVersionWriteNullSparse() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Sparse")).SchemaId);
        assert layout != null;
        RowOperationDispatcher d = RowOperationDispatcher.<WriteRowDispatcher>Create(layout, this.resolver);
        assert this.expected.getCrossVersionNullSparse() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionWriteNullVariable()
    public void CrossVersionWriteNullVariable() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Variable")).SchemaId);
        assert layout != null;
        RowOperationDispatcher d = RowOperationDispatcher.<WriteRowDispatcher>Create(layout, this.resolver);
        assert this.expected.getCrossVersionNullVariable() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][SuppressMessage("StyleCop.CSharp.ReadabilityRules",
    // "SA1118:ParameterMustNotSpanMultipleLines", Justification = "Test code.")] public void CrossVersionWriteSparse()
    public void CrossVersionWriteSparse() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Sparse")).SchemaId);
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
        d.LayoutCodeSwitch("float128", value:CrossVersioningUnitTests.SampleFloat128)
        d.LayoutCodeSwitch("decimal", value:java.math.BigDecimal.ONE / 3.0)
        d.LayoutCodeSwitch("datetime", value:CrossVersioningUnitTests.SampleDateTime)
        d.LayoutCodeSwitch("unixdatetime", value:CrossVersioningUnitTests.SampleUnixDateTime)
        d.LayoutCodeSwitch("guid", value:CrossVersioningUnitTests.SampleGuid)
        d.LayoutCodeSwitch("mongodbobjectid", value:CrossVersioningUnitTests.SampleMongoDbObjectId)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })
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

        assert this.expected.getCrossVersionSparse() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")] public void CrossVersionWriteVariable()
    public void CrossVersionWriteVariable() {
        Layout layout = this.resolver.Resolve(tangible.ListHelper.find(this.schema.getSchemas(), x -> x.Name.equals(
            "Variable")).SchemaId);
        assert layout != null;

        RowOperationDispatcher d = RowOperationDispatcher.<WriteRowDispatcher>Create(layout, this.resolver);
        d.LayoutCodeSwitch("varint", value:-6148914691236517206L)
        d.LayoutCodeSwitch("varuint", value:0xAAAAAAAAAAAAAAAAL)
        d.LayoutCodeSwitch("utf8", value:"abc")
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: d.LayoutCodeSwitch("binary", value: new[] { (byte)0, (byte)1, (byte)2 });
        d.LayoutCodeSwitch("binary", value:new byte[] { (byte)0, (byte)1, (byte)2 })

        assert this.expected.getCrossVersionVariable() == d.RowToHex();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespace()
    public void ParseNamespace() {
        String json = Files.readString(CrossVersioningUnitTests.SchemaFile);
        this.schema = Namespace.Parse(json);
        json = Files.readString(CrossVersioningUnitTests.ExpectedFile);
        this.expected = JsonConvert.<Expected>DeserializeObject(json);
        this.resolver = new LayoutResolverNamespace(this.schema);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Performance", "CA1812:AvoidUninstantiatedInternalClasses", Justification = "Instantiated through Reflection.")] private sealed class Expected
    private final static class Expected {
        private String CrossVersionFixed;
        private String CrossVersionNullFixed;
        private String CrossVersionNullSparse;
        private String CrossVersionNullVariable;
        private String CrossVersionSparse;
        private String CrossVersionVariable;

        public String getCrossVersionFixed() {
            return CrossVersionFixed;
        }

        public void setCrossVersionFixed(String value) {
            CrossVersionFixed = value;
        }

        public String getCrossVersionNullFixed() {
            return CrossVersionNullFixed;
        }

        public void setCrossVersionNullFixed(String value) {
            CrossVersionNullFixed = value;
        }

        public String getCrossVersionNullSparse() {
            return CrossVersionNullSparse;
        }

        public void setCrossVersionNullSparse(String value) {
            CrossVersionNullSparse = value;
        }

        public String getCrossVersionNullVariable() {
            return CrossVersionNullVariable;
        }

        public void setCrossVersionNullVariable(String value) {
            CrossVersionNullVariable = value;
        }

        public String getCrossVersionSparse() {
            return CrossVersionSparse;
        }

        public void setCrossVersionSparse(String value) {
            CrossVersionSparse = value;
        }

        public String getCrossVersionVariable() {
            return CrossVersionVariable;
        }

        public void setCrossVersionVariable(String value) {
            CrossVersionVariable = value;
        }
    }

    private final static class Point implements IDispatchable {
        public int X;
        public int Y;

        public Point(int x, int y) {
            this.X = x;
            this.Y = y;
        }

        public void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope) {
            dispatcher.get().LayoutCodeSwitch(scope, "x", value:this.X)
            dispatcher.get().LayoutCodeSwitch(scope, "y", value:this.Y)
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof null)
            {
                return false;
            }

            if (this == obj) {
                return true;
            }

            boolean tempVar = obj instanceof Point;
            Point point = tempVar ? (Point)obj : null;
            return tempVar && this.equals(point);
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