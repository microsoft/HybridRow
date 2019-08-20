// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(TestData.SchemaFile, TestData.Target)]
    public sealed class GenerateBenchmarkSuite : BenchmarkSuiteBase
    {
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
        public async Task GenerateHotelBenchmarkAsync()
        {
            await this.GenerateBenchmarkAsync("Hotels", 100, TestData.HotelExpected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.RoomsExpected, TestData.Target)]
        public async Task GenerateRoomsBenchmarkAsync()
        {
            await this.GenerateBenchmarkAsync("Available_Rooms_By_Hotel_Date", 100, TestData.RoomsExpected);
        }

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(TestData.GuestsExpected, TestData.Target)]
        public async Task GenerateGuestsBenchmarkAsync()
        {
            await this.GenerateBenchmarkAsync("Guests", 50, TestData.GuestsExpected);
        }

        private static List<Dictionary<Utf8String, object>> GenerateBenchmarkInputs(
            LayoutResolverNamespace resolver,
            string schemaName,
            int outerLoopIterations)
        {
            HybridRowGeneratorConfig generatorConfig = new HybridRowGeneratorConfig();
            const int seed = 42;
            RandomGenerator rand = new RandomGenerator(new Random(seed));
            HybridRowValueGenerator valueGenerator = new HybridRowValueGenerator(rand, generatorConfig);

            Layout layout = resolver.Resolve(resolver.Namespace.Schemas.Find(x => x.Name == schemaName).SchemaId);
            List<Dictionary<Utf8String, object>> rows = new List<Dictionary<Utf8String, object>>(outerLoopIterations);
            for (int iteration = 0; iteration != outerLoopIterations; iteration += outerLoopIterations < 0 ? 0 : 1)
            {
                TypeArgument typeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(layout.SchemaId));
                Dictionary<Utf8String, object> rowValue = (Dictionary<Utf8String, object>)valueGenerator.GenerateLayoutType(resolver, typeArg);
                rows.Add(rowValue);
            }

            return rows;
        }

        private async Task GenerateBenchmarkAsync(string schemaName, int outerLoopIterations, string expectedFile)
        {
            (List<Dictionary<Utf8String, object>> expected, LayoutResolverNamespace resolver) = await this.LoadExpectedAsync(expectedFile);
            List<Dictionary<Utf8String, object>>
                rows = GenerateBenchmarkSuite.GenerateBenchmarkInputs(resolver, schemaName, outerLoopIterations);

            Schema tableSchema = resolver.Namespace.Schemas.Find(x => x.Name == schemaName);
            TypeArgument typeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(tableSchema.SchemaId));

            bool allMatch = rows.Count == expected.Count;
            for (int i = 0; allMatch && i < rows.Count; i++)
            {
                allMatch |= HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, expected[i], rows[i], typeArg);
            }

            if (!allMatch)
            {
                await BenchmarkSuiteBase.WriteAllRowsAsync(expectedFile, this.sdl, resolver, resolver.Resolve(tableSchema.SchemaId), rows);
                Console.WriteLine("Updated expected file at: {0}", Path.GetFullPath(expectedFile));
                Assert.IsTrue(allMatch, "Expected output does not match expected file.");
            }
        }
    }
}
