//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import MongoDB.Bson.IO.*;
import Newtonsoft.Json.*;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tests involving fully (or mostly) unschematized test data.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass] public sealed class UnschematizedMicroBenchmarkSuite : MicroBenchmarkSuiteBase
public final class UnschematizedMicroBenchmarkSuite extends MicroBenchmarkSuiteBase {
    private static final JsonSerializerSettings JsonSettings = new JsonSerializerSettings

    {
        NullValueHandling = NullValueHandling.Ignore, Formatting = Formatting.Indented
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task BsonMessages1KReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task BsonMessages1KReadBenchmarkAsync()
    public Task BsonMessages1KReadBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.BsonReadBenchmark("Messages1K", innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task BsonMessages1KWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task BsonMessages1KWriteBenchmarkAsync()
    public Task BsonMessages1KWriteBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.BsonWriteBenchmark("Messages1K", innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task JsonMessages1KReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task JsonMessages1KReadBenchmarkAsync()
    public Task JsonMessages1KReadBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.JsonReadBenchmark("Messages1K", innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task JsonMessages1KWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task JsonMessages1KWriteBenchmarkAsync()
    public Task JsonMessages1KWriteBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.JsonWriteBenchmark("Messages1K", innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task Messages1KReadBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task Messages1KReadBenchmarkAsync()
    public Task Messages1KReadBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.JsonModelReadBenchmark(resolver, "TypedJsonHybridRowSchema", "Messages1K",
            innerLoopIterations, expected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task Messages1KWriteBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
    // public async Task Messages1KWriteBenchmarkAsync()
    public Task Messages1KWriteBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        final int innerLoopIterations = 1;
        //#else
        final int innerLoopIterations = 10;
        //#endif
        String expectedFile = TestData.Messages1KExpected;
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        UnschematizedMicroBenchmarkSuite.JsonModelWriteBenchmark(resolver, "TypedJsonHybridRowSchema", "Messages1K",
            innerLoopIterations, expected);
    }

    private static void BsonReadBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        try (BsonJsonModelRowGenerator writer = new BsonJsonModelRowGenerator(InitialCapacity)) {
            for (HashMap<Utf8String, Object> tableValue : expected) {
                writer.Reset();
                writer.WriteBuffer(tableValue);
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: expectedSerialized.Add(writer.ToArray());
                expectedSerialized.add(writer.ToArray());
            }
        }

        BenchmarkContext ignoredContext = null;
        Reference<BenchmarkContext> tempReference_ignoredContext = new Reference<BenchmarkContext>(ignoredContext);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Unschematized", "Read", dataSetName, "BSON", innerLoopIterations, ref ignoredContext, (ref BenchmarkContext _, byte[] tableValue) =>
        Benchmark("Unschematized", "Read", dataSetName, "BSON", innerLoopIterations, tempReference_ignoredContext, (ref BenchmarkContext _, byte[] tableValue) ->
        {
            try (MemoryStream stm = new MemoryStream(tableValue)) {
                try (BsonBinaryReader bsonReader = new BsonBinaryReader(stm)) {
                    bsonReader.VisitBsonDocument();
                }
            }
        }, (ref BenchmarkContext _, byte[] tableValue) -> tableValue.length, expectedSerialized);
        ignoredContext = tempReference_ignoredContext.get();
    }

    private static void BsonWriteBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        try (BsonJsonModelRowGenerator writer = new BsonJsonModelRowGenerator(InitialCapacity)) {
            BenchmarkContext ignoredContext = null;

            Reference<BenchmarkContext> tempReference_ignoredContext = new Reference<BenchmarkContext>(ignoredContext);
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are
            // not converted by C# to Java Converter:
            Benchmark("Unschematized", "Write", dataSetName, "BSON", innerLoopIterations,
                tempReference_ignoredContext, (ref BenchmarkContext _, HashMap<Utf8String, Object> tableValue) ->
            {
                writer.Reset();
                writer.WriteBuffer(tableValue);
            }, (ref BenchmarkContext _, HashMap<Utf8String, Object> tableValue) -> writer.getLength(), expected);
            ignoredContext = tempReference_ignoredContext.get();
        }
    }

    private static void JsonModelReadBenchmark(LayoutResolverNamespace resolver, String schemaName,
                                               String dataSetName, int innerLoopIterations,
                                               ArrayList<HashMap<Utf8String, Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.JsonModelWriter = new JsonModelRowGenerator(InitialCapacity, layout, resolver);

        for (HashMap<Utf8String, Object> tableValue : expected) {
            context.JsonModelWriter.Reset();

            Result r = context.JsonModelWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: expectedSerialized.Add(context.JsonModelWriter.ToArray());
            expectedSerialized.add(context.JsonModelWriter.ToArray());
        }

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Unschematized", "Read", dataSetName, "HybridRowSparse",
        // innerLoopIterations, ref context, (ref BenchmarkContext ctx, byte[] tableValue) =>
        Benchmark("Unschematized", "Read", dataSetName, "HybridRowSparse",
            innerLoopIterations, tempReference_context, (ref BenchmarkContext ctx, byte[] tableValue) ->
        {
            RowBuffer row = new RowBuffer(tableValue.AsSpan(), HybridRowVersion.V1, resolver);
            Reference<RowBuffer> tempReference_row =
                new Reference<RowBuffer>(row);
            RowReader reader = new RowReader(tempReference_row);
            row = tempReference_row.get();
            RowReaderExtensions.VisitReader(reader.clone());
        }, (ref BenchmarkContext ctx, byte[] tableValue) -> tableValue.length, expectedSerialized);
        context = tempReference_context.get();
    }

    private static void JsonModelWriteBenchmark(LayoutResolverNamespace resolver, String schemaName,
                                                String dataSetName, int innerLoopIterations,
                                                ArrayList<HashMap<Utf8String, Object>> expected) {
        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        BenchmarkContext context = new BenchmarkContext();
        context.JsonModelWriter = new JsonModelRowGenerator(InitialCapacity, layout, resolver);

        Reference<BenchmarkContext> tempReference_context = new Reference<BenchmarkContext>(context);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        Benchmark("Unschematized", "Write", dataSetName, "HybridRowSparse",
            innerLoopIterations, tempReference_context, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) ->
        {
            ctx.JsonModelWriter.Reset();

            Result r = ctx.JsonModelWriter.WriteBuffer(tableValue);
            ResultAssert.IsSuccess(r);
        }, (ref BenchmarkContext ctx, HashMap<Utf8String, Object> tableValue) -> ctx.JsonModelWriter.Length, expected);
        context = tempReference_context.get();
    }

    private static void JsonReadBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        // Serialize input data to sequence of byte buffers.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
        ArrayList<byte[]> expectedSerialized = new ArrayList<byte[]>(expected.size());
        Encoding utf8Encoding = new UTF8Encoding();
        JsonSerializer jsonSerializer = JsonSerializer.Create(UnschematizedMicroBenchmarkSuite.JsonSettings);
        try (MemoryStream jsonStream = new MemoryStream(InitialCapacity)) {
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

        Reference<BenchmarkContext> tempReference_ignoredContext = new Reference<BenchmarkContext>(ignoredContext);
        // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword - these are not
        // converted by C# to Java Converter:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MicroBenchmarkSuiteBase.Benchmark("Unschematized", "Read", dataSetName, "JSON",
        // innerLoopIterations, ref ignoredContext, (ref BenchmarkContext _, byte[] tableValue) =>
        Benchmark("Unschematized", "Read", dataSetName, "JSON", innerLoopIterations,
            tempReference_ignoredContext, (ref BenchmarkContext _, byte[] tableValue) ->
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
        ignoredContext = tempReference_ignoredContext.get();
    }

    private static void JsonWriteBenchmark(String dataSetName, int innerLoopIterations, ArrayList<HashMap<Utf8String,
        Object>> expected) {
        Encoding utf8Encoding = new UTF8Encoding();
        JsonSerializer jsonSerializer = JsonSerializer.Create(UnschematizedMicroBenchmarkSuite.JsonSettings);
        try (MemoryStream jsonStream = new MemoryStream(InitialCapacity)) {
            try (OutputStreamWriter textWriter = new OutputStreamWriter(jsonStream)) {
                try (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter)) {
                    BenchmarkContext ignoredContext = null;
                    jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                    Reference<BenchmarkContext> tempReference_ignoredContext = new Reference<BenchmarkContext>(ignoredContext);
                    // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'ref' keyword -
                    // these are not converted by C# to Java Converter:
                    Benchmark("Unschematized", "Write", dataSetName, "JSON",
                        innerLoopIterations, tempReference_ignoredContext, (ref BenchmarkContext _, HashMap<Utf8String,
                            Object> tableValue) ->
                    {
                        jsonStream.SetLength(0);
                        jsonSerializer.Serialize(jsonWriter, tableValue);
                        jsonWriter.Flush();
                    }, (ref BenchmarkContext _, HashMap<Utf8String, Object> value) -> jsonStream.Length, expected);
                    ignoredContext = tempReference_ignoredContext.get();
                }
            }
        }
    }
}