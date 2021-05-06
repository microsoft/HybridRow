// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Concurrent;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Runtime.InteropServices;
    using System.Threading;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")]
    [DeploymentItem(@"TestData\*.hr", "TestData")]
    public sealed class ReaderBenchmark
    {
        private const int InitialCapacity = 2 * 1024 * 1024;
        private const int WarmCount = 5;
        private const int MeasureCount = 10;
        private const string CombinedScriptsData = @"TestData\CombinedScriptsData.hr";

        [TestMethod]
        [Owner("jthunter")]
        [DeploymentItem(ReaderBenchmark.CombinedScriptsData, "TestData")]
        public async Task RowReaderAsync()
        {
            using (BenchmarkContext context = new BenchmarkContext(ReaderBenchmark.CombinedScriptsData, true, true))
            {
                await ReaderBenchmark.BenchmarkAsync(context, ReaderBenchmark.RowReaderBenchmarkAsync);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        [Ignore]
        public async Task SpecificFileAsync()
        {
            const string filename = @"E:\TestData\HybridRow\Lastfm.hr";
            using (BenchmarkContext context = new BenchmarkContext(filename, true, true))
            {
                await ReaderBenchmark.BenchmarkAsync(context, ReaderBenchmark.RowReaderBenchmarkAsync);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        [Ignore]
        public async Task AllAsync()
        {
            const string dir = @"E:\TestData\HybridRow";
            foreach (FileInfo childFile in new DirectoryInfo(dir).EnumerateFiles(@"*.hr"))
            {
                using (BenchmarkContext context = new BenchmarkContext(childFile.FullName, false, false))
                {
                    await ReaderBenchmark.BenchmarkAsync(context, ReaderBenchmark.RowReaderBenchmarkAsync);
                }
            }
        }

        private static async Task RowReaderBenchmarkAsync(object ctx)
        {
            BenchmarkContext context = (BenchmarkContext)ctx;
            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(ReaderBenchmark.InitialCapacity);
            Result r = await context.Input.ReadRecordIOAsync(
                (ReadOnlyMemory<byte> record) =>
                {
                    context.IncrementRecordCount();
                    r = ReaderBenchmark.VisitOneRow(record, context.Resolver);
                    Assert.AreEqual(Result.Success, r);
                    return Result.Success;
                },
                (ReadOnlyMemory<byte> segment) =>
                {
                    // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
                    // The cost-cast implied by MemoryMarshal.AsMemory is only safe here because:
                    // 1. Only READ operations are performed on the row.
                    // 2. The row is not allowed to escape this code.
                    RowBuffer row = new RowBuffer(MemoryMarshal.AsMemory(segment).Span, 
                        HybridRowVersion.V1, SchemasHrSchema.LayoutResolver);
                    RowCursor root = RowCursor.Create(ref row);
                    r = default(SegmentHybridRowSerializer).Read(ref row, ref root, true, out Segment _);
                    Assert.AreEqual(Result.Success, r);

                    // TODO: do something with embedded schema.
                    return Result.Success;
                },
                resizer);

            Assert.AreEqual(Result.Success, r);
        }

        private static Result VisitOneRow(ReadOnlyMemory<byte> buffer, LayoutResolver resolver)
        {
            RowReader reader = new RowReader(buffer, HybridRowVersion.V1, resolver);
            return reader.VisitReader();
        }

        [SuppressMessage("Microsoft.Reliability", "CA2001:Avoid calling problematic methods", Justification = "Perf Benchmark")]
        private static async Task BenchmarkAsync(BenchmarkContext context, Func<object, Task> body)
        {
            using (SingleThreadedTaskScheduler scheduler = new SingleThreadedTaskScheduler())
            {
                // Warm
                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                for (int i = 0; i < ReaderBenchmark.WarmCount; i++)
                {
                    context.Reset();
                    sw.Restart();
                    await Task.Factory.StartNew(body, context, CancellationToken.None, TaskCreationOptions.None, scheduler).Unwrap();
                    sw.Stop();
                    if (context.ShowWarmSummary)
                    {
                        context.Summarize(sw.Elapsed);
                    }
                }

                // Execute
                double[] timing = new double[ReaderBenchmark.MeasureCount];
                for (int i = 0; i < ReaderBenchmark.MeasureCount; i++)
                {
                    GC.Collect();
                    GC.WaitForPendingFinalizers();
                    context.Reset();
                    sw.Restart();
                    await Task.Factory.StartNew(body, context, CancellationToken.None, TaskCreationOptions.None, scheduler).Unwrap();
                    sw.Stop();
                    if (context.ShowSummary)
                    {
                        context.Summarize(sw.Elapsed);
                    }

                    timing[i] = sw.Elapsed.TotalMilliseconds;
                }

                Array.Sort(timing);
                Console.WriteLine(
                    $"File: {Path.GetFileNameWithoutExtension(context.InputFile)}, Mean: {timing[ReaderBenchmark.MeasureCount / 2]:F4}");
            }
        }

        private sealed class BenchmarkContext : IDisposable
        {
            private readonly string inputFile;
            private readonly bool showSummary;
            private readonly bool showWarmSummary;
            private long recordCount;
            private readonly Stream input;
            private readonly LayoutResolver resolver;

            public BenchmarkContext(string inputFile, bool showSummary = true, bool showWarmSummary = false)
            {
                this.inputFile = inputFile;
                this.showSummary = showSummary;
                this.showWarmSummary = showWarmSummary;
                this.input = new FileStream(inputFile, FileMode.Open);
                this.resolver = SystemSchema.LayoutResolver;
            }

            public bool ShowSummary => this.showSummary;

            public bool ShowWarmSummary => this.showWarmSummary;

            public string InputFile => this.inputFile;

            public Stream Input => this.input;

            public LayoutResolver Resolver => this.resolver;

            public void IncrementRecordCount()
            {
                this.recordCount++;
            }

            public void Reset()
            {
                this.recordCount = 0;
                this.input.Seek(0, SeekOrigin.Begin);
            }

            public void Summarize(TimeSpan duration)
            {
                Console.Write($"Total Time: {duration.TotalMilliseconds:F4}, ");
                Console.WriteLine($"Record Count: {this.recordCount}");
            }

            public void Dispose()
            {
                this.input.Dispose();
            }
        }

        private sealed class SingleThreadedTaskScheduler : TaskScheduler, IDisposable
        {
            private readonly Thread worker;
            private readonly EventWaitHandle ready;
            private readonly ConcurrentQueue<Task> tasks;
            private readonly CancellationTokenSource cancel;

            // Creates a new instance with the specified degree of parallelism. 
            public SingleThreadedTaskScheduler()
            {
                this.tasks = new ConcurrentQueue<Task>();
                this.ready = new ManualResetEvent(false);
                this.worker = new Thread(this.DoWork);
                this.cancel = new CancellationTokenSource();
                this.worker.Start();
            }

            // Gets the maximum concurrency level supported by this scheduler. 
            public override int MaximumConcurrencyLevel => 1;

            public void Dispose()
            {
                if (!this.cancel.IsCancellationRequested)
                {
                    this.cancel.Cancel();
                    this.worker.Join();
                    this.ready?.Dispose();
                    this.cancel?.Dispose();
                }
            }

            // Queues a task to the scheduler. 
            protected override void QueueTask(Task task)
            {
                lock (this.tasks)
                {
                    this.tasks.Enqueue(task);
                    if (Thread.CurrentThread != this.worker)
                    {
                        this.ready.Set();
                    }
                }
            }

            // Attempts to execute the specified task on the current thread. 
            protected override bool TryExecuteTaskInline(Task task, bool taskWasPreviouslyQueued)
            {
                // If this thread isn't already processing a task, we don't support inlining
                if (Thread.CurrentThread != this.worker)
                {
                    return false;
                }

                // If the task was previously queued, then skip it.
                if (taskWasPreviouslyQueued)
                {
                    return false;
                }

                return this.TryExecuteTask(task);
            }

            protected override bool TryDequeue(Task task)
            {
                return false;
            }

            protected override IEnumerable<Task> GetScheduledTasks()
            {
                return null;
            }

            private void DoWork()
            {
                while (!this.cancel.IsCancellationRequested)
                {
                    if (this.tasks.TryDequeue(out Task item))
                    {
                        this.TryExecuteTask(item);
                    }
                    else
                    {
                        this.ready.WaitOne(TimeSpan.FromSeconds(1));
                    }
                }
            }
        }
    }
}
