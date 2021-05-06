// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.CompilerServices;
    using System.Threading;
    using JetBrains.Profiler.Api;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;

    [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")]
    public class MicroBenchmarkSuiteBase : BenchmarkSuiteBase
    {
        private const int WarmCount = 5;
        private const string MetricsResultFile = "HybridRowPerf.csv";

        [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")]
        private protected static void Benchmark<TValue>(
            string model,
            string operation,
            string schema,
            string api,
            int innerLoopIterations,
            ref BenchmarkContext context,
            BenchmarkBody<TValue> loopBody,
            BenchmarkMeasure<TValue> measure,
            List<TValue> expected)
        {
            Stopwatch sw = new Stopwatch();
            double durationMs = 0;
            long rowSize = 0;

            // Warm
            int warm = Math.Min(MicroBenchmarkSuiteBase.WarmCount, expected.Count);
            for (int i = 0; i < warm; i++)
            {
                for (int innerLoop = 0; innerLoop < innerLoopIterations; innerLoop++)
                {
                    loopBody(ref context, expected[i]);
                }
            }

            // Execute
            GC.Collect();
            GC.WaitForPendingFinalizers();
            Thread.Sleep(1000);
            int gen0 = GC.CollectionCount(0);
            int gen1 = GC.CollectionCount(1);
            int gen2 = GC.CollectionCount(2);
            long allocated = GC.GetAllocatedBytesForCurrentThread();
            int threadId = Thread.CurrentThread.ManagedThreadId;
            ThreadPriority currentPriority = Thread.CurrentThread.Priority;
            Thread.CurrentThread.Priority = ThreadPriority.Highest;
            MemoryProfiler.CollectAllocations(true);
            MemoryProfiler.GetSnapshot();
            try
            {
                foreach (TValue tableValue in expected)
                {
                    sw.Restart();
                    MicroBenchmarkSuiteBase.BenchmarkInnerLoop(innerLoopIterations, tableValue, ref context, loopBody);
                    sw.Stop();
                    durationMs += sw.Elapsed.TotalMilliseconds;
                    rowSize += measure(ref context, tableValue);
                }
            }
            finally
            {
                Thread.CurrentThread.Priority = currentPriority;
                gen0 = GC.CollectionCount(0) - gen0;
                gen1 = GC.CollectionCount(1) - gen1;
                gen2 = GC.CollectionCount(2) - gen2;
                allocated = GC.GetAllocatedBytesForCurrentThread() - allocated;
                MemoryProfiler.GetSnapshot();
                MemoryProfiler.CollectAllocations(false);
            }

            using (Measurements m = new Measurements(MicroBenchmarkSuiteBase.MetricsResultFile))
            {
                m.WriteMeasurement(
                    model: model,
                    operation: operation,
                    schema: schema,
                    api: api,
                    outerLoopIterations: expected.Count,
                    innerLoopIterations: innerLoopIterations,
                    totalSize: rowSize,
                    totalDurationMs: durationMs,
                    threadId: threadId,
                    gen0: gen0,
                    gen1: gen1,
                    gen2: gen2,
                    totalAllocatedBytes: allocated);
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private static void BenchmarkInnerLoop<TValue>(
            int innerLoopIterations,
            TValue tableValue,
            ref BenchmarkContext context,
            BenchmarkBody<TValue> loopBody)
        {
            for (int innerLoop = 0; innerLoop < innerLoopIterations; innerLoop++)
            {
                loopBody(ref context, tableValue);
            }
        }

        private protected ref struct BenchmarkContext
        {
            public CodeGenRowGenerator CodeGenWriter;
            public ProtobufRowGenerator ProtobufWriter;
            public WriteRowGenerator PatchWriter;
            public StreamingRowGenerator StreamingWriter;
            public JsonModelRowGenerator JsonModelWriter;
        }

        private protected delegate void BenchmarkBody<in TValue>(ref BenchmarkContext context, TValue value);

        private protected delegate long BenchmarkMeasure<in TValue>(ref BenchmarkContext context, TValue value);
    }
}
