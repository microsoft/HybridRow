// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.recordio.RecordIOStream;
import com.azure.data.cosmos.serialization.hybridrow.io.Segment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [TestClass][SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods",
// Justification = "Perf Benchmark")][DeploymentItem("TestData\\*.hr", "TestData")] public sealed class ReaderBenchmark
public final class ReaderBenchmark {
    private static final String CombinedScriptsData = "TestData\\CombinedScriptsData.hr";
    private static final int InitialCapacity = 2 * 1024 * 1024;
    private static final int MeasureCount = 10;
    private static final int WarmCount = 5;

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][Ignore] public async Task AllAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][Ignore] public async Task AllAsync()
    public Task AllAsync() {
        final String dir = "E:\\TestData\\HybridRow";
        for (File childFile : (new File(dir)).EnumerateFiles("*.hr")) {
            try (BenchmarkContext context = new BenchmarkContext(childFile.getPath(), false, false)) {
                // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
                await ReaderBenchmark.
                BenchmarkAsync(context, (object arg) -> ReaderBenchmark.RowReaderBenchmarkAsync(arg));
            }
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(ReaderBenchmark.CombinedScriptsData, "TestData")]
    // public async Task RowReaderAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][DeploymentItem(ReaderBenchmark.CombinedScriptsData, "TestData")]
    // public async Task RowReaderAsync()
    public Task RowReaderAsync() {
        try (BenchmarkContext context = new BenchmarkContext(ReaderBenchmark.CombinedScriptsData, true, true)) {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await ReaderBenchmark.BenchmarkAsync(context, (object arg) -> ReaderBenchmark.RowReaderBenchmarkAsync(arg));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][Ignore] public async Task SpecificFileAsync()
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [TestMethod][Owner("jthunter")][Ignore] public async Task SpecificFileAsync()
    public Task SpecificFileAsync() {
        final String filename = "E:\\TestData\\HybridRow\\Lastfm.hr";
        try (BenchmarkContext context = new BenchmarkContext(filename, true, true)) {
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await ReaderBenchmark.BenchmarkAsync(context, (object arg) -> ReaderBenchmark.RowReaderBenchmarkAsync(arg));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods",
    // Justification = "Perf Benchmark")] private static async Task BenchmarkAsync(BenchmarkContext context,
    // Func<object, Task> body)
    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods",
    // Justification = "Perf Benchmark")] private static async Task BenchmarkAsync(BenchmarkContext context,
    // Func<object, Task> body)
    private static Task BenchmarkAsync(BenchmarkContext context, tangible.Func1Param<Object, Task> body) {
        try (SingleThreadedTaskScheduler scheduler = new SingleThreadedTaskScheduler()) {
            // Warm
            System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
            for (int i = 0; i < ReaderBenchmark.WarmCount; i++) {
                context.Reset();
                sw.Restart();
                // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
                await Task.
                Factory.StartNew(body, context, CancellationToken.None, TaskCreationOptions.None, scheduler).Unwrap();
                sw.Stop();
                if (context.getShowWarmSummary()) {
                    context.Summarize(sw.Elapsed);
                }
            }

            // Execute
            double[] timing = new double[ReaderBenchmark.MeasureCount];
            for (int i = 0; i < ReaderBenchmark.MeasureCount; i++) {
                System.gc();
                System.runFinalization();
                context.Reset();
                sw.Restart();
                // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
                await Task.
                Factory.StartNew(body, context, CancellationToken.None, TaskCreationOptions.None, scheduler).Unwrap();
                sw.Stop();
                if (context.getShowSummary()) {
                    context.Summarize(sw.Elapsed);
                }

                timing[i] = sw.Elapsed.TotalMilliseconds;
            }

            Arrays.sort(timing);
            System.out.println(String.format("File: %1$s, Mean: %1.4f",
                Path.GetFileNameWithoutExtension(context.getInputFile()), timing[ReaderBenchmark.MeasureCount / 2]));
        }
    }

    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: private static async Task RowReaderBenchmarkAsync(object ctx)
    private static Task RowReaderBenchmarkAsync(Object ctx) {
        BenchmarkContext context = (BenchmarkContext)ctx;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(ReaderBenchmark.InitialCapacity);
        MemorySpanResizer<Byte> resizer = new MemorySpanResizer<Byte>(ReaderBenchmark.InitialCapacity);
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        Result r = await
        RecordIOStream.ReadRecordIOAsync(context.getInput(),
            record ->
        {
            context.IncrementRecordCount();
            r = ReaderBenchmark.VisitOneRow(record, context.getResolver());
            assert Result.SUCCESS == r;
            return Result.SUCCESS;
        }, segment ->
        {
            Segment _;
            Out<Segment> tempOut__ =
                new Out<Segment>();
            r = SegmentSerializer.Read(segment.Span, context.getResolver(), tempOut__);
            _ = tempOut__.get();
            assert Result.SUCCESS == r;

            // TODO: do something with embedded schema.
            return Result.SUCCESS;
        }, resizer);

        assert Result.SUCCESS == r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static Result VisitOneRow(Memory<byte> buffer, LayoutResolver resolver)
    private static Result VisitOneRow(Memory<Byte> buffer, LayoutResolver resolver) {
        RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row);
        row = tempReference_row.get();
        return RowReaderExtensions.VisitReader(reader.clone());
    }

    private final static class BenchmarkContext implements Closeable {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is
        // input or output:
        private Stream input;
        private String inputFile;
        private long recordCount;
        private LayoutResolver resolver;
        private boolean showSummary;
        private boolean showWarmSummary;


        public BenchmarkContext(String inputFile, boolean showSummary) {
            this(inputFile, showSummary, false);
        }

        public BenchmarkContext(String inputFile) {
            this(inputFile, true, false);
        }

        //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
        //ORIGINAL LINE: public BenchmarkContext(string inputFile, bool showSummary = true, bool showWarmSummary =
        // false)
        public BenchmarkContext(String inputFile, boolean showSummary, boolean showWarmSummary) {
            this.inputFile = inputFile;
            this.showSummary = showSummary;
            this.showWarmSummary = showWarmSummary;
            // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.FileStream
            // is input or output:
            this.input = new FileStream(inputFile, FileMode.Open);
            this.resolver = SystemSchema.LayoutResolver;
        }

        // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is
        // input or output:
        public Stream getInput() {
            return this.input;
        }

        public String getInputFile() {
            return this.inputFile;
        }

        public LayoutResolver getResolver() {
            return this.resolver;
        }

        public boolean getShowSummary() {
            return this.showSummary;
        }

        public boolean getShowWarmSummary() {
            return this.showWarmSummary;
        }

        public void IncrementRecordCount() {
            this.recordCount++;
        }

        public void Reset() {
            this.recordCount = 0;
            this.input.Seek(0, SeekOrigin.Begin);
        }

        public void Summarize(TimeSpan duration) {
            System.out.print(String.format("Total Time: %0.4f, ", duration.TotalMilliseconds));
            System.out.println(String.format("Record Count: %1$s", this.recordCount));
        }

        public void close() throws IOException {
            this.input.Dispose();
        }
    }

    private final static class SingleThreadedTaskScheduler extends TaskScheduler implements Closeable {
        private CancellationTokenSource cancel;
        private EventWaitHandle ready;
        private ConcurrentQueue<Task> tasks;
        private Thread worker;

        // Creates a new instance with the specified degree of parallelism.
        public SingleThreadedTaskScheduler() {
            this.tasks = new ConcurrentQueue<Task>();
            this.ready = new ManualResetEvent(false);
            this.worker = new Thread() {
                void run() {
                    this.DoWork();
                }
            };
            this.cancel = new CancellationTokenSource();
            this.worker.start();
        }

        // Gets the maximum concurrency level supported by this scheduler.
        @Override
        public int getMaximumConcurrencyLevel() {
            return 1;
        }

        public void close() throws IOException {
            if (!this.cancel.IsCancellationRequested) {
                this.cancel.Cancel();
                this.worker.join();
                this.ready == null ? null : this.ready.Dispose();
                this.cancel == null ? null : this.cancel.Dispose();
            }
        }

        @Override
        protected java.lang.Iterable<Task> GetScheduledTasks() {
            return null;
        }

        // Queues a task to the scheduler.
        @Override
        protected void QueueTask(Task task) {
            synchronized (this.tasks) {
                this.tasks.Enqueue(task);
                if (Thread.currentThread() != this.worker) {
                    this.ready.Set();
                }
            }
        }

        @Override
        protected boolean TryDequeue(Task task) {
            return false;
        }

        // Attempts to execute the specified task on the current thread.
        @Override
        protected boolean TryExecuteTaskInline(Task task, boolean taskWasPreviouslyQueued) {
            // If this thread isn't already processing a task, we don't support inlining
            if (Thread.currentThread() != this.worker) {
                return false;
            }

            // If the task was previously queued, then skip it.
            if (taskWasPreviouslyQueued) {
                return false;
            }

            return this.TryExecuteTask(task);
        }

        private void DoWork() {
            while (!this.cancel.IsCancellationRequested) {
                Task item;
                Out<Task> tempOut_item = new Out<Task>();
                if (this.tasks.TryDequeue(tempOut_item)) {
                    item = tempOut_item.get();
                    this.TryExecuteTask(item);
                } else {
                    item = tempOut_item.get();
                    this.ready.WaitOne(TimeSpan.FromSeconds(1));
                }
            }
        }
    }
}