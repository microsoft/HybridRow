// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.IO;
    using System.Text;

    internal class Measurements : IDisposable
    {
        private static readonly long RunId = DateTime.UtcNow.Ticks;
        private readonly FileStream file;
        private readonly TextWriter writer;

        public Measurements(string path)
        {
            FileInfo info = new FileInfo(path);
            if (info.Exists)
            {
                this.file = new FileStream(path, FileMode.Append);
                this.writer = new StreamWriter(this.file, Encoding.ASCII);
            }
            else
            {
                this.file = new FileStream(path, FileMode.CreateNew);
                this.writer = new StreamWriter(this.file, Encoding.ASCII);
                this.writer.WriteLine(
                    "RunId,Model,Operation,Schema,API,Iterations,Size (bytes),Total (ms),Duration (ms),Allocated (bytes),ThreadId,Gen0,Gen1,Gen2,Total Allocated (bytes)");
            }
        }

        public void Dispose()
        {
            this.writer.Flush();
            this.writer.Dispose();
            this.file.Dispose();
        }

        public void WriteMeasurement(string model, string operation, string schema, string api,
            int outerLoopIterations, int innerLoopIterations, long totalSize, double totalDurationMs, 
            int threadId, int gen0, int gen1, int gen2, long totalAllocatedBytes)
        {
            Console.WriteLine(
                "RunId: {0}, \nModel: {1} \nOperation: {2} \nSchema: {3} \nAPI: {4}",
                Measurements.RunId, 
                model,
                operation,
                schema,
                api);

            Console.WriteLine(
                "\n\nIterations: {0} \nSize (bytes): {1:F0} \nTotal (ms): {2:F4} \nDuration (ms): {3:F4} \nAllocated (bytes): {4:F4}",
                outerLoopIterations,
                totalSize / outerLoopIterations,
                totalDurationMs,
                totalDurationMs / (outerLoopIterations * innerLoopIterations),
                totalAllocatedBytes / (outerLoopIterations * innerLoopIterations));

            Console.WriteLine(
                "\n\nThread: {0} \nCollections: {1}, {2}, {3} \nTotal Allocated: {4:n0} (bytes)",
                threadId,
                gen0,
                gen1,
                gen2,
                totalAllocatedBytes);


            this.writer.WriteLine(
                "{0},{1},{2},{3},{4},{5},{6:F0},{7:F8},{8:F8},{9:F8},{10},{11},{12},{13},{14:0}",
                Measurements.RunId,
                model,
                operation,
                schema,
                api,
                outerLoopIterations,
                totalSize / outerLoopIterations,
                totalDurationMs,
                totalDurationMs / (outerLoopIterations * innerLoopIterations),
                totalAllocatedBytes / (outerLoopIterations * innerLoopIterations),
                threadId,
                gen0,
                gen1,
                gen2,
                totalAllocatedBytes);
        }
    }
}
