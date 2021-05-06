// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowStress
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Text;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.Extensions.CommandLineUtils;
    using Newtonsoft.Json;

    internal class HybridRowStressProgram
    {
        private static readonly JsonSerializerSettings JsonSettings = new JsonSerializerSettings
        {
            NullValueHandling = NullValueHandling.Ignore,
            Formatting = Formatting.Indented,
        };

        private readonly HybridRowStressConfig config;
        private readonly RandomGenerator rand;
        private readonly HybridRowValueGenerator valueGenerator;
        private readonly SchemaGenerator schemaGenerator;

        public HybridRowStressProgram(HybridRowStressConfig config)
        {
            this.config = config;
            this.rand = new RandomGenerator(new Random(this.config.Seed));
            this.valueGenerator = new HybridRowValueGenerator(this.rand, config.GeneratorConfig);
            this.schemaGenerator = new SchemaGenerator(this.rand, this.config.GeneratorConfig, this.valueGenerator);
        }

        public static int Main(string[] args)
        {
            try
            {
                HybridRowStressConfig config = HybridRowStressConfig.FromArgs(args);
                HybridRowStressProgram program = new HybridRowStressProgram(config);
                Task<long> ret = program.MainAsync();
                ret.Wait();
                return ret.Result == 0 ? 0 : 1;
            }
            catch (CommandParsingException)
            {
                return -1;
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                return -1;
            }
        }

        private static string ToJsonString(Namespace ns)
        {
            return JsonConvert.SerializeObject(ns, HybridRowStressProgram.JsonSettings);
        }

        [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")]
        private Task<long> MainAsync()
        {
            long totalErrors = 0;
            Stopwatch sw = new Stopwatch();
            double jsonWriteMs = 0;
            double writeMs = 0;
            double streamingMs = 0;
            long jsonSize = 0;
            long rowSize = 0;

            long iteration;
            for (iteration = 0; iteration != this.config.Iterations; iteration += this.config.Iterations < 0 ? 0 : 1)
            {
                if (this.config.BreakOnError && totalErrors > 0)
                {
                    break;
                }

                if ((iteration != 0) && (iteration % this.config.ReportFrequency) == 0)
                {
                    Console.WriteLine(
                        "{0}: Status: {1} - JSON: {2:F0} Size: {3:F0} JsonWrite: {4:F4} Write: {5:F4} Streaming: {6:F4}",
                        DateTime.Now,
                        iteration,
                        jsonSize / iteration,
                        rowSize / iteration,
                        jsonWriteMs / (iteration * this.config.InnerLoopCount),
                        writeMs / (iteration * this.config.InnerLoopCount),
                        streamingMs / (iteration * this.config.InnerLoopCount));
                }

                StressContext context = new StressContext(iteration, this.config);
                try
                {
                    // Generate a random namespace and table schema.
                    if (this.config.NamespaceFile != null)
                    {
                        string json = File.ReadAllText(this.config.NamespaceFile);
                        context.Namespace = Namespace.Parse(json);
                        Contract.Requires(context.Namespace != null);
                    }
                    else
                    {
                        context.Namespace = this.schemaGenerator.InitializeRandomNamespace();
                    }

                    if (this.config.TableSchemaName != null)
                    {
                        context.TableSchema = context.Namespace.Schemas.Find(s => s.Name == this.config.TableSchemaName);
                        Contract.Requires(context.TableSchema != null);
                    }
                    else
                    {
                        context.TableSchema = this.schemaGenerator.InitializeRandomSchema(context.Namespace, 0);
                    }

                    context.IsNotNull(context.Namespace.Schemas.Find(s => s.Name == context.TableSchema.Name), "Namespace contains table schema");

                    if (!HybridRowStressProgram.DoesNamespaceRoundtripToJson(context))
                    {
                        continue;
                    }

                    // Compile the table schema to get a layout.
                    if (!HybridRowStressProgram.DoesSchemaCompile(context, out Layout layout))
                    {
                        continue;
                    }

                    // Generate a value for writing.
                    LayoutResolver resolver = new LayoutResolverNamespace(context.Namespace);
                    int initialCapacity = this.config.GeneratorConfig.RowBufferInitialCapacity.Next(this.rand);
                    TypeArgument typeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(layout.SchemaId));
                    context.TableValue = (Dictionary<Utf8String, object>)this.valueGenerator.GenerateLayoutType(resolver, typeArg);

                    // Write to JSON
                    Encoding utf8Encoding = new UTF8Encoding();
                    JsonSerializer jsonSerializer = JsonSerializer.Create(HybridRowStressProgram.JsonSettings);
                    using (MemoryStream jsonStream = new MemoryStream(initialCapacity))
                    using (StreamWriter textWriter = new StreamWriter(jsonStream, utf8Encoding))
                    using (JsonTextWriter jsonWriter = new JsonTextWriter(textWriter))
                    {
                        try
                        {
                            if (this.config.GCBeforeInnerLoop)
                            {
                                GC.Collect();
                            }

                            sw.Restart();
                            for (int innerLoop = 0; innerLoop < this.config.InnerLoopCount; innerLoop++)
                            {
                                jsonStream.SetLength(0);
                                jsonSerializer.Serialize(jsonWriter, context.TableValue);
                                jsonWriter.Flush();
                            }

                            sw.Stop();
                            jsonWriteMs += sw.Elapsed.TotalMilliseconds;
                            context.Trace("JSON Write: {0,10:F5}", sw.Elapsed.TotalMilliseconds / this.config.InnerLoopCount);
                        }
                        catch (Exception ex)
                        {
                            context.Fail(ex, "Failed to write row buffer.");
                            continue;
                        }

                        jsonSize += jsonStream.Length;
                        context.Trace(JsonConvert.SerializeObject(context.TableValue, HybridRowStressProgram.JsonSettings));
                    }

                    // Write a row buffer using the layout.
                    WriteRowGenerator rowGenerator = new WriteRowGenerator(initialCapacity, layout, resolver);
                    try
                    {
                        if (this.config.GCBeforeInnerLoop)
                        {
                            GC.Collect();
                        }

                        sw.Restart();
                        for (int innerLoop = 0; innerLoop < this.config.InnerLoopCount; innerLoop++)
                        {
                            rowGenerator.Reset();

                            Result r = rowGenerator.DispatchLayout(layout, context.TableValue);
                            context.IsSuccess(r);
                        }

                        sw.Stop();
                        writeMs += sw.Elapsed.TotalMilliseconds;
                        context.Trace("Patch Write: {0,10:F5}", sw.Elapsed.TotalMilliseconds / this.config.InnerLoopCount);
                    }
                    catch (Exception ex)
                    {
                        context.Fail(ex, "Failed to write row buffer.");
                        continue;
                    }

                    // Read the row using a streaming reader.
                    try
                    {
                        rowSize += rowGenerator.Length;
                        RowReader reader = rowGenerator.GetReader();
                        context.Trace(ref reader);
                    }
                    catch (Exception ex)
                    {
                        context.Fail(ex, "Failed to read row buffer.");
                        continue;
                    }

                    // Write the same row using the streaming writer.
                    StreamingRowGenerator writer = new StreamingRowGenerator(initialCapacity, layout, resolver);
                    try
                    {
                        if (this.config.GCBeforeInnerLoop)
                        {
                            GC.Collect();
                        }

                        sw.Restart();
                        for (int innerLoop = 0; innerLoop < this.config.InnerLoopCount; innerLoop++)
                        {
                            writer.Reset();

                            Result r = writer.WriteBuffer(context.TableValue);
                            context.IsSuccess(r);
                        }

                        sw.Stop();
                        streamingMs += sw.Elapsed.TotalMilliseconds;
                        context.Trace("Streaming Write: {0,10:F5}", sw.Elapsed.TotalMilliseconds / this.config.InnerLoopCount);
                    }
                    catch (Exception ex)
                    {
                        context.Fail(ex, "Failed to write streaming row buffer.");
                        continue;
                    }

                    // Read the row again using a streaming reader.
                    try
                    {
                        Contract.Requires(rowGenerator.Length == writer.Length);
                        RowReader reader = writer.GetReader();
                        context.Trace(ref reader);
                    }
                    catch (Exception ex)
                    {
                        context.Fail(ex, "Failed to read streaming row buffer.");
                    }
                }
                finally
                {
                    totalErrors += context.Errors;
                }
            }

            Console.WriteLine(
                "{0}: Status: {1} : Errors: {2} JSON: {3:F0} Size: {4:F0} JsonWrite: {5:F4} Write: {6:F4} Streaming: {7:F4}",
                DateTime.Now,
                iteration,
                totalErrors,
                jsonSize / iteration,
                rowSize / iteration,
                jsonWriteMs / (iteration * this.config.InnerLoopCount),
                writeMs / (iteration * this.config.InnerLoopCount),
                streamingMs / (iteration * this.config.InnerLoopCount));

            return Task.FromResult(totalErrors);
        }

        private static bool DoesNamespaceRoundtripToJson(StressContext context)
        {
            string json1 = HybridRowStressProgram.ToJsonString(context.Namespace);
            context.Trace(json1);

            try
            {
                Namespace ns2 = Namespace.Parse(json1);
                string json2 = JsonConvert.SerializeObject(ns2, HybridRowStressProgram.JsonSettings);
                return context.AreEqual(json1, json2, "Namespace failed to round-trip.");
            }
            catch (Exception ex)
            {
                context.Fail(ex, "Namespace failed to parse with exception.");
                return false;
            }
        }

        private static bool DoesSchemaCompile(StressContext context, out Layout layout)
        {
            try
            {
                layout = LayoutCompiler.Compile(context.Namespace, context.TableSchema);
                return true;
            }
            catch (Exception ex)
            {
                context.Fail(ex, "Namespace failed to compile with exception.");
                layout = default;
                return false;
            }
        }
    }
}
