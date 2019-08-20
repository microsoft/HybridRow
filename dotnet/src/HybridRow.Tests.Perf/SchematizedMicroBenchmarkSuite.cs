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
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using MongoDB.Bson.IO;
    using Newtonsoft.Json;

    [TestClass]
    [DeploymentItem(TestData.SchemaFile, TestData.Target)]
    public sealed class SchematizedMicroBenchmarkSuite : MicroBenchmarkSuiteBase
    {
        private static readonly JsonSerializerSettings JsonSettings = new JsonSerializerSettings
        {
            NullValueHandling = NullValueHandling.Ignore,
            Formatting = Formatting.Indented
        };

        private string sdl;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            this.sdl = File.ReadAllText(TestData.SchemaFile);
            Namespace schema = Namespace.Parse(this.sdl);
            this.DefaultResolver = new LayoutResolverNamespace(schema, SystemSchema.LayoutResolver);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task JsonHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task JsonRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task JsonGuestsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonWriteBenchmark("Guests", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task JsonHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task JsonRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task JsonGuestsReadBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.JsonReadBenchmark("Guests", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task BsonHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task BsonRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task BsonGuestsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonWriteBenchmark(resolver, "Guests", "Guests", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task BsonHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task BsonRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task BsonGuestsReadBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.BsonReadBenchmark(resolver, "Guests", "Guests", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task LayoutHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task LayoutRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task LayoutGuestsWriteBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 1000;
#endif
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutWriteBenchmark(resolver, "Guests", "Guests", innerLoopIterations, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task LayoutHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task LayoutRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task LayoutGuestsReadBenchmarkAsync()
        {
#if DEBUG
            const int innerLoopIterations = 1;
#else
            const int innerLoopIterations = 1000;
#endif
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.LayoutReadBenchmark(resolver, "Guests", "Guests", innerLoopIterations, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task StreamingHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task StreamingRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task StreamingGuestsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingWriteBenchmark(resolver, "Guests", "Guests", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task StreamingHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Hotels", "Hotels", 1000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task StreamingRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Available_Rooms_By_Hotel_Date", "Rooms", 10000, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task StreamingGuestsReadBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            SchematizedMicroBenchmarkSuite.StreamingReadBenchmark(resolver, "Guests", "Guests", 1000, expected);
        }

        private static void JsonWriteBenchmark(
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Encoding utf8Encoding = new UTF8Encoding();
            JsonSerializer jsonSerializer = JsonSerializer.Create(SchematizedMicroBenchmarkSuite.JsonSettings);
            using (MemoryStream jsonStream = new MemoryStream(BenchmarkSuiteBase.InitialCapacity))
            using (StreamWriter textWriter = new StreamWriter(jsonStream, utf8Encoding))
            using (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter))
            {
                BenchmarkContext ignoredContext = default;
                jsonSerializer.Converters.Add(new Utf8StringJsonConverter());

                MicroBenchmarkSuiteBase.Benchmark(
                    "Schematized",
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
            JsonSerializer jsonSerializer = JsonSerializer.Create(SchematizedMicroBenchmarkSuite.JsonSettings);
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
                "Schematized",
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

        private static void BsonWriteBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            using (BsonRowGenerator writer = new BsonRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver))
            {
                BenchmarkContext ignoredContext = default;

                MicroBenchmarkSuiteBase.Benchmark(
                    "Schematized",
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

        private static void BsonReadBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            // Serialize input data to sequence of byte buffers.
            List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            using (BsonRowGenerator writer = new BsonRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver))
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
                "Schematized",
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

        private static void LayoutWriteBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            BenchmarkContext context = new BenchmarkContext
            {
                PatchWriter = new WriteRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            MicroBenchmarkSuiteBase.Benchmark(
                "Schematized",
                "Write",
                dataSetName,
                "Layout",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> dict) =>
                {
                    ctx.PatchWriter.Reset();
                    Result r = ctx.PatchWriter.DispatchLayout(layout, dict);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> _) => ctx.PatchWriter.Length,
                expected);
        }

        private static void LayoutReadBenchmark(
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
                StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            foreach (Dictionary<Utf8String, object> tableValue in expected)
            {
                context.StreamingWriter.Reset();

                Result r = context.StreamingWriter.WriteBuffer(tableValue);
                ResultAssert.IsSuccess(r);
                expectedSerialized.Add(context.StreamingWriter.ToArray());
            }

            MicroBenchmarkSuiteBase.Benchmark(
                "Schematized",
                "Read",
                dataSetName,
                "Layout",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, byte[] tableValue) =>
                {
                    VisitRowGenerator visitor = new VisitRowGenerator(tableValue.AsSpan(), resolver);
                    Result r = visitor.DispatchLayout(layout);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }

        private static void StreamingWriteBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            BenchmarkContext context = new BenchmarkContext
            {
                StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            MicroBenchmarkSuiteBase.Benchmark(
                "Schematized",
                "Write",
                dataSetName,
                "HybridRow",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) =>
                {
                    ctx.StreamingWriter.Reset();

                    Result r = ctx.StreamingWriter.WriteBuffer(tableValue);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> _) => ctx.StreamingWriter.Length,
                expected);
        }

        private static void StreamingReadBenchmark(
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
                StreamingWriter = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            foreach (Dictionary<Utf8String, object> tableValue in expected)
            {
                context.StreamingWriter.Reset();

                Result r = context.StreamingWriter.WriteBuffer(tableValue);
                ResultAssert.IsSuccess(r);
                expectedSerialized.Add(context.StreamingWriter.ToArray());
            }

            MicroBenchmarkSuiteBase.Benchmark(
                "Schematized",
                "Read",
                dataSetName,
                "HybridRow",
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
    }
}
