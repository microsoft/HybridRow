// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf;

import JetBrains.Profiler.Api.*;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;

import java.util.ArrayList;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification
// = "Perf Benchmark")] public class MicroBenchmarkSuiteBase : BenchmarkSuiteBase
public class MicroBenchmarkSuiteBase extends BenchmarkSuiteBase {
    private static final String MetricsResultFile = "HybridRowPerf.csv";
    private static final int WarmCount = 5;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods",
    // Justification = "Perf Benchmark")] private protected static void Benchmark<TValue>(string model, string
    // operation, string schema, string api, int innerLoopIterations, ref BenchmarkContext context,
    // BenchmarkBody<TValue> loopBody, BenchmarkMeasure<TValue> measure, List<TValue> expected)
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods",
    // Justification = "Perf Benchmark")] private protected static void Benchmark<TValue>(string model, string
    // operation, string schema, string api, int innerLoopIterations, ref BenchmarkContext context,
    // BenchmarkBody<TValue> loopBody, BenchmarkMeasure<TValue> measure, List<TValue> expected)
    protected static <TValue> void Benchmark(String model, String operation, String schema, String api,
                                             int innerLoopIterations, Reference<BenchmarkContext> context,
                                             BenchmarkBody<TValue> loopBody, BenchmarkMeasure<TValue> measure,
                                             ArrayList<TValue> expected) {
        Stopwatch sw = new Stopwatch();
        double durationMs = 0;
        long rowSize = 0;

        // Warm
        int warm = Math.min(MicroBenchmarkSuiteBase.WarmCount, expected.size());
        for (int i = 0; i < warm; i++) {
            for (int innerLoop = 0; innerLoop < innerLoopIterations; innerLoop++) {
                loopBody.invoke(context, expected.get(i));
            }
        }

        // Execute
        System.gc();
        System.runFinalization();
        Thread.sleep(1000);
        int gen0 = GC.CollectionCount(0);
        int gen1 = GC.CollectionCount(1);
        int gen2 = GC.CollectionCount(2);
        long allocated = GC.GetAllocatedBytesForCurrentThread();
        int threadId = Thread.currentThread().ManagedThreadId;
        ThreadPriority currentPriority = Thread.currentThread().Priority;
        Thread.currentThread().Priority = ThreadPriority.Highest;
        MemoryProfiler.CollectAllocations(true);
        MemoryProfiler.GetSnapshot();
        try {
            for (TValue tableValue : expected) {
                sw.Restart();
                MicroBenchmarkSuiteBase.BenchmarkInnerLoop(innerLoopIterations, tableValue, context, loopBody);
                sw.Stop();
                durationMs += sw.Elapsed.TotalMilliseconds;
                rowSize += measure.invoke(context, tableValue);
            }
        } finally {
            Thread.currentThread().Priority = currentPriority;
            gen0 = GC.CollectionCount(0) - gen0;
            gen1 = GC.CollectionCount(1) - gen1;
            gen2 = GC.CollectionCount(2) - gen2;
            allocated = GC.GetAllocatedBytesForCurrentThread() - allocated;
            MemoryProfiler.GetSnapshot();
            MemoryProfiler.CollectAllocations(false);
        }

        try (Measurements m = new Measurements(MicroBenchmarkSuiteBase.MetricsResultFile)) {
            m.WriteMeasurement(model, operation, schema, api, expected.size(), innerLoopIterations, rowSize,
                durationMs, threadId, gen0, gen1, gen2, allocated);
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.NoInlining)] private static void BenchmarkInnerLoop<TValue>(int
    // innerLoopIterations, TValue tableValue, ref BenchmarkContext context, BenchmarkBody<TValue> loopBody)
    private static <TValue> void BenchmarkInnerLoop(int innerLoopIterations, TValue tableValue,
                                                    Reference<BenchmarkContext> context,
                                                    BenchmarkBody<TValue> loopBody) {
        for (int innerLoop = 0; innerLoop < innerLoopIterations; innerLoop++) {
            loopBody.invoke(context, tableValue);
        }
    }

    @FunctionalInterface
    public interface BenchmarkBody<TValue> {
        void invoke(Reference<BenchmarkContext> context, TValue value);
    }

    @FunctionalInterface
    public interface BenchmarkMeasure<TValue> {
        long invoke(Reference<BenchmarkContext> context, TValue value);
    }

    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may
    // differ from the original:
    //ORIGINAL LINE: private protected ref struct BenchmarkContext
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    protected final static class BenchmarkContext {
        public CodeGenRowGenerator CodeGenWriter = new CodeGenRowGenerator();
        public JsonModelRowGenerator JsonModelWriter = new JsonModelRowGenerator();
        public WriteRowGenerator PatchWriter = new WriteRowGenerator();
        public ProtobufRowGenerator ProtobufWriter = new ProtobufRowGenerator();
        public StreamingRowGenerator StreamingWriter = new StreamingRowGenerator();

        public BenchmarkContext clone() {
            BenchmarkContext varCopy = new BenchmarkContext();

            varCopy.CodeGenWriter = this.CodeGenWriter.clone();
            varCopy.ProtobufWriter = this.ProtobufWriter.clone();
            varCopy.PatchWriter = this.PatchWriter.clone();
            varCopy.StreamingWriter = this.StreamingWriter.clone();
            varCopy.JsonModelWriter = this.JsonModelWriter.clone();

            return varCopy;
        }
    }
}