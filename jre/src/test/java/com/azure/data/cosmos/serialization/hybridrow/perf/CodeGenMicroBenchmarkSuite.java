//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tests involving generated (early bound) code compiled from schema based on a partial implementation
 * of Cassandra Hotel Schema described here: https: //www.oreilly.com/ideas/cassandra-data-modeling .
 * <p>
 * The tests here differ from {@link SchematizedMicroBenchmarkSuite} in that they rely on
 * the schema being known at compile time instead of runtime. This allows code to be generated that
 * directly addresses the schema structure instead of dynamically discovering schema structure at
 * runtime.
 * </p>
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public sealed class CodeGenMicroBenchmarkSuite : MicroBenchmarkSuiteBase
public final class CodeGenMicroBenchmarkSuite extends MicroBenchmarkSuiteBase {
    private static final int GuestCount = 1000;
    private static final int HotelCount = 10000;
    private static final int RoomsCount = 10000;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task CodeGenGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task CodeGenGuestsReadBenchmarkAsync()
    public Task CodeGenGuestsReadBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(resolver, "Guests", "Guests",
            CodeGenMicroBenchmarkSuite.GuestCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task CodeGenGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task CodeGenGuestsWriteBenchmarkAsync()
    public Task CodeGenGuestsWriteBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(resolver, "Guests", "Guests",
            CodeGenMicroBenchmarkSuite.GuestCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task CodeGenHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task CodeGenHotelReadBenchmarkAsync()
    public Task CodeGenHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(resolver, "Hotels", "Hotels",
            CodeGenMicroBenchmarkSuite.HotelCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task CodeGenHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task CodeGenHotelWriteBenchmarkAsync()
    public Task CodeGenHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(resolver, "Hotels", "Hotels",
            CodeGenMicroBenchmarkSuite.HotelCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task CodeGenRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task CodeGenRoomsReadBenchmarkAsync()
    public Task CodeGenRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms",
            CodeGenMicroBenchmarkSuite.RoomsCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task CodeGenRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task CodeGenRoomsWriteBenchmarkAsync()
    public Task CodeGenRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms",
            CodeGenMicroBenchmarkSuite.RoomsCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task ProtobufGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task ProtobufGuestsReadBenchmarkAsync()
    public Task ProtobufGuestsReadBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark("Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task ProtobufGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task ProtobufGuestsWriteBenchmarkAsync()
    public Task ProtobufGuestsWriteBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark("Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task ProtobufHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task ProtobufHotelReadBenchmarkAsync()
    public Task ProtobufHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark("Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task ProtobufHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task ProtobufHotelWriteBenchmarkAsync()
    public Task ProtobufHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark("Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task ProtobufRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task ProtobufRoomsReadBenchmarkAsync()
    public Task ProtobufRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark("Available_Rooms_By_Hotel_Date", "Rooms",
            CodeGenMicroBenchmarkSuite.RoomsCount, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task ProtobufRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task ProtobufRoomsWriteBenchmarkAsync()
    public Task ProtobufRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark("Available_Rooms_By_Hotel_Date", "Rooms",
            CodeGenMicroBenchmarkSuite.RoomsCount, expected);
    }

    private static void CodeGenReadBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName,
                                             int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.CodeGenWriter = new CodeGenRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        for (HashMap<Utf8String, Object> tableValue : expected) {
            context.CodeGenWriter.Reset();

            Result r = context.CodeGenWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: expectedSerialized.Add(context.CodeGenWriter.ToArray());
            expectedSerialized.add(context.CodeGenWriter.ToArray());
        }

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Read", dataSetName, "HybridRowGen", innerLoopIterations, ref context, (ref BenchmarkContext ctx, byte[] tableValue) =>
        MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Read", dataSetName, "HybridRowGen", innerLoopIterations,
            tempReference_context, (ref BenchmarkContext ctx, byte[] tableValue) ->
        {
            Result r = ctx.CodeGenWriter.ReadBuffer(tableValue);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, byte[] tableValue) -> tableValue.length, expectedSerialized);
        context = tempReference_context.get();
    }

    private static void CodeGenWriteBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName
        , int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.CodeGenWriter = new CodeGenRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Write", dataSetName, "HybridRowGen", innerLoopIterations,
            tempReference_context, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) ->
        {
            ctx.CodeGenWriter.Reset();

            Result r = ctx.CodeGenWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) -> ctx.CodeGenWriter.Length, expected);
        context = tempReference_context.get();
    }

    private static void ProtobufReadBenchmark(String schemaName, String dataSetName, int innerLoopIterations,
                                              ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        BenchmarkContext context = new BenchmarkContext();
        context.ProtobufWriter = new ProtobufRowGenerator(schemaName, BenchmarkSuiteBase.InitialCapacity);

        for (HashMap<Utf8String, Object> tableValue : expected) {
            context.ProtobufWriter.WriteBuffer(tableValue);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: expectedSerialized.Add(context.ProtobufWriter.ToArray());
            expectedSerialized.add(context.ProtobufWriter.ToArray());
        }

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Read", dataSetName, "Protobuf",
        // innerLoopIterations, ref context, (ref BenchmarkContext ctx, byte[] tableValue) => ctx.ProtobufWriter
        // .ReadBuffer(tableValue), (ref BenchmarkContext ctx, byte[] tableValue) => tableValue.Length,
        // expectedSerialized);
        MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Read", dataSetName, "Protobuf", innerLoopIterations,
            tempReference_context,
            (ref BenchmarkContext ctx, byte[] tableValue) -> ctx.ProtobufWriter.ReadBuffer(tableValue),
            (ref BenchmarkContext ctx, byte[] tableValue) -> tableValue.length, expectedSerialized);
        context = tempReference_context.get();
    }

    private static void ProtobufWriteBenchmark(String schemaName, String dataSetName, int innerLoopIterations,
                                               ArrayList<HashMap<Utf8String, Object>> expected) {
        BenchmarkContext context = new BenchmarkContext();
        context.ProtobufWriter = new ProtobufRowGenerator(schemaName, BenchmarkSuiteBase.InitialCapacity);

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        MicroBenchmarkSuiteBase.Benchmark("CodeGen", "Write", dataSetName, "Protobuf", innerLoopIterations,
            tempReference_context, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) ->
        {
            ctx.ProtobufWriter.WriteBuffer(tableValue);
        }, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) -> ctx.ProtobufWriter.Length, expected);
        context = tempReference_context.get();
    }
}