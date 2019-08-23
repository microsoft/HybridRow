//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import MongoDB.Bson.IO.*;
import Newtonsoft.Json.*;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TestData.SchemaFile, TestData.Target)] public sealed class
// SchematizedMicroBenchmarkSuite : MicroBenchmarkSuiteBase
public final class SchematizedMicroBenchmarkSuite extends MicroBenchmarkSuiteBase {
    private static final JsonSerializerSettings JsonSettings = new JsonSerializerSettings
    private String sdl;

    {
        NullValueHandling = NullValueHandling.Ignore, Formatting = Formatting.Indented
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task BsonGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task BsonGuestsReadBenchmarkAsync()
    public Task BsonGuestsReadBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Guests", "Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task BsonGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task BsonGuestsWriteBenchmarkAsync()
    public Task BsonGuestsWriteBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Guests", "Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task BsonHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task BsonHotelReadBenchmarkAsync()
    public Task BsonHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task BsonHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task BsonHotelWriteBenchmarkAsync()
    public Task BsonHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task BsonRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task BsonRoomsReadBenchmarkAsync()
    public Task BsonRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task BsonRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task BsonRoomsWriteBenchmarkAsync()
    public Task BsonRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task JsonGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task JsonGuestsReadBenchmarkAsync()
    public Task JsonGuestsReadBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task JsonGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task JsonGuestsWriteBenchmarkAsync()
    public Task JsonGuestsWriteBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task JsonHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task JsonHotelReadBenchmarkAsync()
    public Task JsonHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task JsonHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task JsonHotelWriteBenchmarkAsync()
    public Task JsonHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task JsonRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task JsonRoomsReadBenchmarkAsync()
    public Task JsonRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Rooms", 10000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task JsonRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task JsonRoomsWriteBenchmarkAsync()
    public Task JsonRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace _) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Rooms", 10000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task LayoutGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task LayoutGuestsReadBenchmarkAsync()
    public Task LayoutGuestsReadBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 1000;
        //#endif
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Guests", "Guests", innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task LayoutGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task LayoutGuestsWriteBenchmarkAsync()
    public Task LayoutGuestsWriteBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 1000;
        //#endif
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Guests", "Guests", innerLoopIterations,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task LayoutHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task LayoutHotelReadBenchmarkAsync()
    public Task LayoutHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task LayoutHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task LayoutHotelWriteBenchmarkAsync()
    public Task LayoutHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task LayoutRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task LayoutRoomsReadBenchmarkAsync()
    public Task LayoutRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 1000,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task LayoutRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task LayoutRoomsWriteBenchmarkAsync()
    public Task LayoutRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 1000,
            expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        this.sdl = Files.readString(TestData.SchemaFile);
        Namespace schema = Namespace.Parse(this.sdl);
        this.DefaultResolver = new LayoutResolverNamespace(schema, SystemSchema.LayoutResolver);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task StreamingGuestsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task StreamingGuestsReadBenchmarkAsync()
    public Task StreamingGuestsReadBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Guests", "Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task StreamingGuestsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task StreamingGuestsWriteBenchmarkAsync()
    public Task StreamingGuestsWriteBenchmarkAsync() {
        String expectedFile = TestData.GuestsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Guests", "Guests", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task StreamingHotelReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task StreamingHotelReadBenchmarkAsync()
    public Task StreamingHotelReadBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task StreamingHotelWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task StreamingHotelWriteBenchmarkAsync()
    public Task StreamingHotelWriteBenchmarkAsync() {
        String expectedFile = TestData.HotelExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task StreamingRoomsReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task StreamingRoomsReadBenchmarkAsync()
    public Task StreamingRoomsReadBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms",
            10000, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task StreamingRoomsWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task StreamingRoomsWriteBenchmarkAsync()
    public Task StreamingRoomsWriteBenchmarkAsync() {
        String expectedFile = TestData.RoomsExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms",
            10000, expected);
    }

    private static void BsonReadBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName,
                                          int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        try (BsonRowGenerator writer = new BsonRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)) {
            for (HashMap<Utf8String, Object> tableValue : expected) {
                writer.Reset();
                writer.WriteBuffer(tableValue);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: expectedSerialized.Add(writer.ToArray());
                expectedSerialized.add(writer.ToArray());
            }
        }

        BenchmarkContext ignoredContext = null;
        RefObject<BenchmarkContext> tempRef_ignoredContext = new RefObject<BenchmarkContext>(ignoredContext);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "BSON",
        // innerLoopIterations, ref ignoredContext, (ref BenchmarkContext _, byte[] tableValue) =>
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "BSON", innerLoopIterations,
            tempRef_ignoredContext, (ref BenchmarkContext _, byte[] tableValue) ->
        {
            try (MemoryStream stm = new MemoryStream(tableValue)) {
                try (BsonBinaryReader bsonReader = new BsonBinaryReader(stm)) {
                    bsonReader.VisitBsonDocument();
                }
            }
        }, (ref BenchmarkContext _, byte[] tableValue) -> tableValue.length, expectedSerialized);
        ignoredContext = tempRef_ignoredContext.get();
    }

    private static void BsonWriteBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName,
                                           int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        try (BsonRowGenerator writer = new BsonRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)) {
            BenchmarkContext ignoredContext = null;

            RefObject<BenchmarkContext> tempRef_ignoredContext = new RefObject<BenchmarkContext>(ignoredContext);
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are
            // not converted by C# to Java Converter:
            MicroBenchmarkSuiteBase.Benchmark("Schematized", "Write", dataSetName, "BSON", innerLoopIterations,
                tempRef_ignoredContext, (ref BenchmarkContext _, HashMap<Utf8String, Object> tableValue) ->
            {
                writer.Reset();
                writer.WriteBuffer(tableValue);
            }, (ref BenchmarkContext _, HashMap<Utf8String, Object> tableValue) -> writer.getLength(), expected);
            ignoredContext = tempRef_ignoredContext.get();
        }
    }

    private static void JsonReadBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Encoding utf8Encoding = new UTF8Encoding();
        JsonSerializer jsonSerializer = JsonSerializer.Create(SchematizedMicroBenchmarkSuite.JsonSettings);
        try (MemoryStream jsonStream = new MemoryStream(BenchmarkSuiteBase.InitialCapacity)) {
            try (OutputStreamWriter textWriter = new OutputStreamWriter(jsonStream)) {
                try (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter)) {
                    jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                    for (HashMap<Utf8String, Object> tableValue : expected) {
                        jsonSerializer.Serialize(jsonWriter, tableValue);
                        jsonWriter.Flush();
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: expectedSerialized.Add(jsonStream.ToArray());
                        expectedSerialized.add((Byte)jsonStream.ToArray());
                        jsonStream.SetLength(0);
                    }
                }
            }
        }

        BenchmarkContext ignoredContext = null;
        jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

        RefObject<BenchmarkContext> tempRef_ignoredContext = new RefObject<BenchmarkContext>(ignoredContext);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "JSON",
        // innerLoopIterations, ref ignoredContext, (ref BenchmarkContext _, byte[] tableValue) =>
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "JSON", innerLoopIterations,
            tempRef_ignoredContext, (ref BenchmarkContext _, byte[] tableValue) ->
        {
            try (MemoryStream jsonStream = new MemoryStream(tableValue)) {
                try (InputStreamReader textReader = new InputStreamReader(jsonStream)) {
                    try (JsonTextReader jsonReader = new JsonTextReader(textReader)) {
                        while (jsonReader.Read()) {
                            // Just visit the entire structure without materializing any of the values.
                        }
                    }
                }
            }
        }, (ref BenchmarkContext _, byte[] tableValue) -> tableValue.length, expectedSerialized);
        ignoredContext = tempRef_ignoredContext.get();
    }

    private static void JsonWriteBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        Encoding utf8Encoding = new UTF8Encoding();
        JsonSerializer jsonSerializer = JsonSerializer.Create(SchematizedMicroBenchmarkSuite.JsonSettings);
        try (MemoryStream jsonStream = new MemoryStream(BenchmarkSuiteBase.InitialCapacity)) {
            try (OutputStreamWriter textWriter = new OutputStreamWriter(jsonStream)) {
                try (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter)) {
                    BenchmarkContext ignoredContext = null;
                    jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                    RefObject<BenchmarkContext> tempRef_ignoredContext = new RefObject<BenchmarkContext>(ignoredContext);
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword -
                    // these are not converted by C# to Java Converter:
                    MicroBenchmarkSuiteBase.Benchmark("Schematized", "Write", dataSetName, "JSON",
                        innerLoopIterations, tempRef_ignoredContext, (ref BenchmarkContext _, HashMap<Utf8String,
                            Object> tableValue) ->
                    {
                        jsonStream.SetLength(0);
                        jsonSerializer.Serialize(jsonWriter, tableValue);
                        jsonWriter.Flush();
                    }, (ref BenchmarkContext _, HashMap<Utf8String, Object> value) -> jsonStream.Length, expected);
                    ignoredContext = tempRef_ignoredContext.get();
                }
            }
        }
    }

    private static void LayoutReadBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName,
                                            int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        for (HashMap<Utf8String, Object> tableValue : expected) {
            context.StreamingWriter.Reset();

            Result r = context.StreamingWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: expectedSerialized.Add(context.StreamingWriter.ToArray());
            expectedSerialized.add((Byte)context.StreamingWriter.ToArray());
        }

        RefObject<BenchmarkContext> tempRef_context = new RefObject<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "Layout",
        // innerLoopIterations, ref context, (ref BenchmarkContext ctx, byte[] tableValue) =>
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "Layout", innerLoopIterations,
            tempRef_context, (ref BenchmarkContext ctx, byte[] tableValue) ->
        {
            VisitRowGenerator visitor = new VisitRowGenerator(tableValue.AsSpan(), resolver);
            Result r = visitor.DispatchLayout(layout);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, byte[] tableValue) -> tableValue.length, expectedSerialized);
        context = tempRef_context.get();
    }

    private static void LayoutWriteBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName,
                                             int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.PatchWriter = new WriteRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        RefObject<BenchmarkContext> tempRef_context = new RefObject<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Write", dataSetName, "Layout", innerLoopIterations,
            tempRef_context, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> dict) ->
        {
            ctx.PatchWriter.Reset();
            Result r = ctx.PatchWriter.DispatchLayout(layout, dict);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> _) -> ctx.PatchWriter.Length, expected);
        context = tempRef_context.get();
    }

    private static void StreamingReadBenchmark(LayoutResolverNamespace resolver, String schemaName, String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x = schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        for (HashMap<Utf8String, Object> tableValue : expected) {
            context.StreamingWriter.Reset();

            Result r = context.StreamingWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: expectedSerialized.Add(context.StreamingWriter.ToArray());
            expectedSerialized.add((Byte)context.StreamingWriter.ToArray());
        }

        RefObject<BenchmarkContext> tempRef_context = new RefObject<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "HybridRow", innerLoopIterations, ref context, (ref BenchmarkContext ctx, byte[] tableValue) =>
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Read", dataSetName, "HybridRow", innerLoopIterations, tempRef_context, (ref BenchmarkContext ctx, byte[] tableValue) ->
        {
            RowBuffer row = new RowBuffer(tableValue.AsSpan(), HybridRowVersion.V1, resolver);
            RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(row);
            RowReader reader = new RowReader(tempRef_row);
            row = tempRef_row.get();
            RowReaderExtensions.VisitReader(reader.clone());
        }, (ref BenchmarkContext ctx, byte[] tableValue) -> tableValue.length, expectedSerialized);
        context = tempRef_context.get();
    }

    private static void StreamingWriteBenchmark(LayoutResolverNamespace resolver, String schemaName,
                                                String dataSetName, int innerLoopIterations,
                                                ArrayList<HashMap<Utf8String, Object>> expected) {
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver);

        RefObject<BenchmarkContext> tempRef_context = new RefObject<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        MicroBenchmarkSuiteBase.Benchmark("Schematized", "Write", dataSetName, "HybridRow", innerLoopIterations,
            tempRef_context, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) ->
        {
            ctx.StreamingWriter.Reset();

            Result r = ctx.StreamingWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> _) -> ctx.StreamingWriter.Length, expected);
        context = tempRef_context.get();
    }
}