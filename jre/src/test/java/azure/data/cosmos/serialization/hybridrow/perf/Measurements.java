//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.perf;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

public class Measurements implements Closeable {
    private static final long RunId = LocalDateTime.UtcNow.getTime();
    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.FileStream is
    // input or output:
    private FileStream file;
    private TextWriter writer;

    public Measurements(String path) {
        File info = new File(path);
        if (info.exists()) {
            this.file = new FileOutputStream(path, true);
            this.writer = new OutputStreamWriter(this.file, java.nio.charset.StandardCharsets.US_ASCII);
        } else {
            // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.FileStream
            // is input or output:
            this.file = new FileStream(path, FileMode.CreateNew);
            this.writer = new OutputStreamWriter(this.file, java.nio.charset.StandardCharsets.US_ASCII);
            this.writer.WriteLine("RunId,Model,Operation,Schema,API,Iterations,Size (bytes),Total (ms),Duration (ms)," +
                "Allocated (bytes),ThreadId,Gen0,Gen1,Gen2,Total Allocated (bytes)");
        }
    }

    public final void WriteMeasurement(String model, String operation, String schema, String api,
                                       int outerLoopIterations, int innerLoopIterations, long totalSize,
                                       double totalDurationMs, int threadId, int gen0, int gen1, int gen2,
                                       long totalAllocatedBytes) {
        System.out.printf("RunId: %1$s, \nModel: %2$s \nOperation: %3$s \nSchema: %4$s \nAPI: %5$s" + "\r\n",
            Measurements.RunId, model, operation, schema, api);

        System.out.printf("\n\nIterations: %1$s \nSize (bytes): %1.0f \nTotal (ms): %2.4f \nDuration (ms): %3.4f " +
            "\nAllocated (bytes): %4.4f" + "\r\n", outerLoopIterations, totalSize / outerLoopIterations,
            totalDurationMs, totalDurationMs / (outerLoopIterations * innerLoopIterations),
            totalAllocatedBytes / (outerLoopIterations * innerLoopIterations));

        // TODO: C# TO JAVA CONVERTER: The '4:n0' format specifier is not converted to Java:
        System.out.printf("\n\nThread: %1$s \nCollections: %2$s, %3$s, %4$s \nTotal Allocated: {4:n0} (bytes)" + "\r" +
            "\n", threadId, gen0, gen1, gen2, totalAllocatedBytes);


        this.writer.WriteLine("{0},{1},{2},{3},{4},{5},{6:F0},{7:F8},{8:F8},{9:F8},{10},{11},{12},{13},{14:0}",
            Measurements.RunId, model, operation, schema, api, outerLoopIterations, totalSize / outerLoopIterations,
            totalDurationMs, totalDurationMs / (outerLoopIterations * innerLoopIterations), totalAllocatedBytes / (outerLoopIterations * innerLoopIterations), threadId, gen0, gen1, gen2, totalAllocatedBytes);
    }

    public final void close() throws IOException {
        this.writer.Flush();
        this.writer.Dispose();
        this.file.Dispose();
    }
}