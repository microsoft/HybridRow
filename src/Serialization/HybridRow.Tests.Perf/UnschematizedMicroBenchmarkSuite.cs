// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Internal;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using MongoDB.Bson.IO;
    using Newtonsoft.Json;

    /// <summary>Tests involving fully (or mostly) unschematized test data.</summary>
    [TestClass]
    public sealed class UnschematizedMicroBenchmarkSuite : MicroBenchmarkSuiteBase
    {
        private static readonly JsonSerializerSettings JsonSettings = new JsonSerializerSettings
        {
            NullValueHandling = NullValueHandling.Ignore,
            Formatting = Formatting.Indented
        };

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task Messages1KWriteBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.JsonModelWriteBenchmark(
                resolver,
                "TypedJsonHybridRowSchema",
                "Messages1K",
                innerLoopIterations,
                expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task Messages1KReadBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.JsonModelReadBenchmark(
                resolver,
                "TypedJsonHybridRowSchema",
                "Messages1K",
                innerLoopIterations,
                expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task BsonMessages1KWriteBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.BsonWriteBenchmark("Messages1K", innerLoopIterations, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task BsonMessages1KReadBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.BsonReadBenchmark("Messages1K", innerLoopIterations, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task JsonMessages1KWriteBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.JsonWriteBenchmark("Messages1K", innerLoopIterations, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.Messages1KExpected, TestData.Target)]
        public async Task JsonMessages1KReadBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 10;
#endif
            string expectedFile = TestData.Messages1KExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            UnschematizedMicroBenchmarkSuite.JsonReadBenchmark("Messages1K", innerLoopIterations, expected);
        }

        private static void JsonModelWriteBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            BenchmarkContext context = new BenchmarkContext
            {
                JsonModelWriter = new JsonModelRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            MicroBenchmarkSuiteBase.Benchmark(
                "Unschematized",
                "Write",
                dataSetName,
                "HybridRowSparse",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) =>
                {
                    ctx.JsonModelWriter.Reset();

                    Result r = ctx.JsonModelWriter.WriteBuffer(tableValue);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) => ctx.JsonModelWriter.Length,
                expected);
        }

        private static void JsonModelReadBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            // Serialize input data to sequence of byte buffers.
            List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            BenchmarkContext context = new BenchmarkContext
            {
                JsonModelWriter = new JsonModelRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            foreach (Dictionary<Utf8String, object> tableValue in expected)
            {
                context.JsonModelWriter.Reset();

                Result r = context.JsonModelWriter.WriteBuffer(tableValue);
                ResultAssert.IsSuccess(r);
                expectedSerialized.Add(context.JsonModelWriter.ToArray());
            }

            MicroBenchmarkSuiteBase.Benchmark(
                "Unschematized",
                "Read",
                dataSetName,
                "HybridRowSparse",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, byte[] tableValue) =>
                {
                    RowBuffer row = new RowBuffer(tableValue.AsSpan(), HybridRowVersion.V1, resolver);
                    RowReader reader = new RowReader(ref row);
                    reader.VisitReader();
                },
                (ref BenchmarkContext ctx, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }

        private static void JsonWriteBenchmark(
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Encoding utf8Encoding = new UTF8Encoding();
            JsonSerializer jsonSerializer = JsonSerializer.Create(UnschematizedMicroBenchmarkSuite.JsonSettings);
            using (MemoryStream jsonStream = new MemoryStream(BenchmarkSuiteBase.InitialCapacity))
            using (StreamWriter textWriter = new StreamWriter(jsonStream, utf8Encoding))
            using (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter))
            {
                BenchmarkContext ignoredContext = default;
                jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                MicroBenchmarkSuiteBase.Benchmark(
                    "Unschematized",
                    "Write",
                    dataSetName,
                    "JSON",
                    innerLoopIterations,
                    ref ignoredContext,
                    (ref BenchmarkContext _, Dictionary<Utf8String, object> tableValue) =>
                    {
                        jsonStream.SetLength(0);
                        jsonSerializer.Serialize(jsonWriter, tableValue);
                        jsonWriter.Flush();
                    },
                    (ref BenchmarkContext _, Dictionary<Utf8String, object> value) => jsonStream.Length,
                    expected);
            }
        }

        private static void JsonReadBenchmark(string dataSetName, int innerLoopIterations, List<Dictionary<Utf8String, object>> expected)
        {
            // Serialize input data to sequence of byte buffers.
            List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
            Encoding utf8Encoding = new UTF8Encoding();
            JsonSerializer jsonSerializer = JsonSerializer.Create(UnschematizedMicroBenchmarkSuite.JsonSettings);
            using (MemoryStream jsonStream = new MemoryStream(BenchmarkSuiteBase.InitialCapacity))
            using (StreamWriter textWriter = new StreamWriter(jsonStream, utf8Encoding))
            using (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter))
            {
                jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                foreach (Dictionary<Utf8String, object> tableValue in expected)
                {
                    jsonSerializer.Serialize(jsonWriter, tableValue);
                    jsonWriter.Flush();
                    expectedSerialized.Add(jsonStream.ToArray());
                    jsonStream.SetLength(0);
                }
            }

            BenchmarkContext ignoredContext = default;
            jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

            MicroBenchmarkSuiteBase.Benchmark(
                "Unschematized",
                "Read",
                dataSetName,
                "JSON",
                innerLoopIterations,
                ref ignoredContext,
                (ref BenchmarkContext _, byte[] tableValue) =>
                {
                    using (MemoryStream jsonStream = new MemoryStream(tableValue))
                    using (StreamReader textReader = new StreamReader(jsonStream, utf8Encoding))
                    using (JsonTextReader jsonReader = new JsonTextReader(textReader))
                    {
                        while (jsonReader.Read())
                        {
                            // Just visit the entire structure without materializing any of the values.
                        }
                    }
                },
                (ref BenchmarkContext _, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }

        private static void BsonWriteBenchmark(string dataSetName, int innerLoopIterations, List<Dictionary<Utf8String, object>> expected)
        {
            using (BsonJsonModelRowGenerator writer = new BsonJsonModelRowGenerator(BenchmarkSuiteBase.InitialCapacity))
            {
                BenchmarkContext ignoredContext = default;

                MicroBenchmarkSuiteBase.Benchmark(
                    "Unschematized",
                    "Write",
                    dataSetName,
                    "BSON",
                    innerLoopIterations,
                    ref ignoredContext,
                    (ref BenchmarkContext _, Dictionary<Utf8String, object> tableValue) =>
                    {
                        writer.Reset();
                        writer.WriteBuffer(tableValue);
                    },
                    (ref BenchmarkContext _, Dictionary<Utf8String, object> tableValue) => writer.Length,
                    expected);
            }
        }

        private static void BsonReadBenchmark(string dataSetName, int innerLoopIterations, List<Dictionary<Utf8String, object>> expected)
        {
            // Serialize input data to sequence of byte buffers.
            List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
            using (BsonJsonModelRowGenerator writer = new BsonJsonModelRowGenerator(BenchmarkSuiteBase.InitialCapacity))
            {
                foreach (Dictionary<Utf8String, object> tableValue in expected)
                {
                    writer.Reset();
                    writer.WriteBuffer(tableValue);
                    expectedSerialized.Add(writer.ToArray());
                }
            }

            BenchmarkContext ignoredContext = default;
            MicroBenchmarkSuiteBase.Benchmark(
                "Unschematized",
                "Read",
                dataSetName,
                "BSON",
                innerLoopIterations,
                ref ignoredContext,
                (ref BenchmarkContext _, byte[] tableValue) =>
                {
                    using (MemoryStream stm = new MemoryStream(tableValue))
                    using (BsonBinaryReader bsonReader = new BsonBinaryReader(stm))
                    {
                        bsonReader.VisitBsonDocument();
                    }
                },
                (ref BenchmarkContext _, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }
    }
}
