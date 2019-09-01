// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][DeploymentItem(TestData.SchemaFile, TestData.Target)] public sealed class
// GenerateBenchmarkSuite : BenchmarkSuiteBase
public final class GenerateBenchmarkSuite extends BenchmarkSuiteBase {
    private String sdl;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task GenerateGuestsBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.GuestsExpected, TestData.Target)] public
    // async Task GenerateGuestsBenchmarkAsync()
    public Task GenerateGuestsBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        await this.GenerateBenchmarkAsync("Guests", 50, TestData.GuestsExpected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task GenerateHotelBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.HotelExpected, TestData.Target)] public
    // async Task GenerateHotelBenchmarkAsync()
    public Task GenerateHotelBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        await this.GenerateBenchmarkAsync("Hotels", 100, TestData.HotelExpected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task GenerateRoomsBenchmarkAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(TestData.RoomsExpected, TestData.Target)] public
    // async Task GenerateRoomsBenchmarkAsync()
    public Task GenerateRoomsBenchmarkAsync() {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        await this.GenerateBenchmarkAsync("Available_Rooms_By_Hotel_Date", 100, TestData.RoomsExpected);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestInitialize] public void ParseNamespaceExample()
    public void ParseNamespaceExample() {
        this.sdl = Files.readString(TestData.SchemaFile);
        Namespace schema = Namespace.Parse(this.sdl);
        this.DefaultResolver = new LayoutResolverNamespace(schema, SystemSchema.LayoutResolver);
    }

    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: private async Task GenerateBenchmarkAsync(string schemaName, int outerLoopIterations, string
    // expectedFile)
    private Task GenerateBenchmarkAsync(String schemaName, int outerLoopIterations, String expectedFile) {
        // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# deconstruction declarations:
        (List < Dictionary < Utf8String, object >> expected, LayoutResolverNamespace resolver) =await
        this.LoadExpectedAsync(expectedFile);
        ArrayList<HashMap<Utf8String, Object>> rows = GenerateBenchmarkSuite.GenerateBenchmarkInputs(resolver,
            schemaName, outerLoopIterations);

        Schema tableSchema = resolver.Namespace.Schemas.Find(x = schemaName.equals( > x.Name))
        TypeArgument typeArg = new TypeArgument(LayoutType.UDT,
            new TypeArgumentList(tableSchema.getSchemaId().clone()));

        boolean allMatch = rows.size() == expected.Count;
        for (int i = 0; allMatch && i < rows.size(); i++) {
            allMatch |= HybridRowValueGenerator.DynamicTypeArgumentEquals(resolver, expected[i], rows.get(i),
                typeArg.clone());
        }

        if (!allMatch) {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await BenchmarkSuiteBase.
            WriteAllRowsAsync(expectedFile, this.sdl, resolver, resolver.Resolve(tableSchema.getSchemaId().clone()), rows);
            System.out.printf("Updated expected file at: %1$s" + "\r\n", (new File(expectedFile)).getAbsolutePath());
            Assert.IsTrue(allMatch, "Expected output does not match expected file.");
        }
    }

    private static ArrayList<HashMap<Utf8String, Object>> GenerateBenchmarkInputs(LayoutResolverNamespace resolver,
                                                                                  String schemaName,
                                                                                  int outerLoopIterations) {
        HybridRowGeneratorConfig generatorConfig = new HybridRowGeneratorConfig();
        final int seed = 42;
        RandomGenerator rand = new RandomGenerator(new Random(seed));
        HybridRowValueGenerator valueGenerator = new HybridRowValueGenerator(rand, generatorConfig);

        Layout layout = resolver.Resolve(tangible.ListHelper.find(resolver.getNamespace().getSchemas(), x =
            schemaName.equals( > x.Name)).SchemaId)
        ArrayList<HashMap<Utf8String, Object>> rows = new ArrayList<HashMap<Utf8String, Object>>(outerLoopIterations);
        for (int iteration = 0; iteration != outerLoopIterations; iteration += outerLoopIterations < 0 ? 0 : 1) {
            TypeArgument typeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(layout.getSchemaId().clone()));
            HashMap<Utf8String, Object> rowValue =
                (HashMap<Utf8String, Object>)valueGenerator.GenerateLayoutType(resolver, typeArg.clone());
            rows.add(rowValue);
        }

        return rows;
    }
}