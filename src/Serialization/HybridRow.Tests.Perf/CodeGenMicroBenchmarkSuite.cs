// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    /// <summary>
    /// Tests involving generated (early bound) code compiled from schema based on a partial implementation
    /// of Cassandra Hotel Schema described here: https://www.oreilly.com/ideas/cassandra-data-modeling .
    /// <para>
    /// The tests here differ from <see cref="SchematizedMicroBenchmarkSuite" /> in that they rely on
    /// the schema being known at compile time instead of runtime. This allows code to be generated that
    /// directly addresses the schema structure instead of dynamically discovering schema structure at
    /// runtime.
    /// </para>
    /// </summary>
    [TestClass]
    public sealed class CodeGenMicroBenchmarkSuite : MicroBenchmarkSuiteBase
    {
        private const int GuestCount = 1000;
        private const int HotelCount = 10000;
        private const int RoomsCount = 10000;

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task ProtobufGuestsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark("Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task ProtobufHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark("Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task ProtobufRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufWriteBenchmark(
                "Available_Rooms_By_Hotel_Date",
                "Rooms",
                CodeGenMicroBenchmarkSuite.RoomsCount,
                expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task ProtobufGuestsReadBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark("Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task ProtobufHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark("Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task ProtobufRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace _) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.ProtobufReadBenchmark(
                "Available_Rooms_By_Hotel_Date",
                "Rooms",
                CodeGenMicroBenchmarkSuite.RoomsCount,
                expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task CodeGenGuestsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(resolver, "Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task CodeGenHotelWriteBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(resolver, "Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task CodeGenRoomsWriteBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenWriteBenchmark(
                resolver,
                "Available_Rooms_By_Hotel_Date",
                "Rooms",
                CodeGenMicroBenchmarkSuite.RoomsCount,
                expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task CodeGenGuestsReadBenchmarkAsync()
        {
            string expectedFile = TestData.GuestsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(resolver, "Guests", "Guests", CodeGenMicroBenchmarkSuite.GuestCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.HotelExpected, TestData.Target)]
        public async Task CodeGenHotelReadBenchmarkAsync()
        {
            string expectedFile = TestData.HotelExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(resolver, "Hotels", "Hotels", CodeGenMicroBenchmarkSuite.HotelCount, expected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task CodeGenRoomsReadBenchmarkAsync()
        {
            string expectedFile = TestData.RoomsExpected;
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            CodeGenMicroBenchmarkSuite.CodeGenReadBenchmark(
                resolver,
                "Available_Rooms_By_Hotel_Date",
                "Rooms",
                CodeGenMicroBenchmarkSuite.RoomsCount,
                expected);
        }

        private static void ProtobufWriteBenchmark(
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            BenchmarkContext context = new BenchmarkContext
            {
                ProtobufWriter = new ProtobufRowGenerator(schemaName, BenchmarkSuiteBase.InitialCapacity)
            };

            MicroBenchmarkSuiteBase.Benchmark(
                "CodeGen",
                "Write",
                dataSetName,
                "Protobuf",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) => { ctx.ProtobufWriter.WriteBuffer(tableValue); },
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) => ctx.ProtobufWriter.Length,
                expected);
        }

        private static void ProtobufReadBenchmark(
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            // Serialize input data to sequence of byte buffers.
            List<byte[]> expectedSerialized = new List<byte[]>(expected.Count);
            BenchmarkContext context = new BenchmarkContext
            {
                ProtobufWriter = new ProtobufRowGenerator(schemaName, BenchmarkSuiteBase.InitialCapacity)
            };

            foreach (Dictionary<Utf8String, object> tableValue in expected)
            {
                context.ProtobufWriter.WriteBuffer(tableValue);
                expectedSerialized.Add(context.ProtobufWriter.ToArray());
            }

            MicroBenchmarkSuiteBase.Benchmark(
                "CodeGen",
                "Read",
                dataSetName,
                "Protobuf",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, byte[] tableValue) => ctx.ProtobufWriter.ReadBuffer(tableValue),
                (ref BenchmarkContext ctx, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }

        private static void CodeGenWriteBenchmark(
            LayoutResolverNamespace resolver,
            string schemaName,
            string dataSetName,
            int innerLoopIterations,
            List<Dictionary<Utf8String, object>> expected)
        {
            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            BenchmarkContext context = new BenchmarkContext
            {
                CodeGenWriter = new CodeGenRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            MicroBenchmarkSuiteBase.Benchmark(
                "CodeGen",
                "Write",
                dataSetName,
                "HybridRowGen",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) =>
                {
                    ctx.CodeGenWriter.Reset();

                    Result r = ctx.CodeGenWriter.WriteBuffer(tableValue);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, Dictionary<Utf8String, object> tableValue) => ctx.CodeGenWriter.Length,
                expected);
        }

        private static void CodeGenReadBenchmark(
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
                CodeGenWriter = new CodeGenRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout, resolver)
            };

            foreach (Dictionary<Utf8String, object> tableValue in expected)
            {
                context.CodeGenWriter.Reset();

                Result r = context.CodeGenWriter.WriteBuffer(tableValue);
                ResultAssert.IsSuccess(r);
                expectedSerialized.Add(context.CodeGenWriter.ToArray());
            }

            MicroBenchmarkSuiteBase.Benchmark(
                "CodeGen",
                "Read",
                dataSetName,
                "HybridRowGen",
                innerLoopIterations,
                ref context,
                (ref BenchmarkContext ctx, byte[] tableValue) =>
                {
                    Result r = ctx.CodeGenWriter.ReadBuffer(tableValue);
                    ResultAssert.IsSuccess(r);
                },
                (ref BenchmarkContext ctx, byte[] tableValue) => tableValue.Length,
                expectedSerialized);
        }
    }
}
