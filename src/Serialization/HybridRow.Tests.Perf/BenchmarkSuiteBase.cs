// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1401 // Fields should be private

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Runtime.InteropServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    public class BenchmarkSuiteBase
    {
        private protected const int InitialCapacity = 2 * 1024 * 1024;
        private protected LayoutResolverNamespace DefaultResolver = (LayoutResolverNamespace)SystemSchema.LayoutResolver;

        private protected async Task<(List<Dictionary<Utf8String, object>> Expected, LayoutResolverNamespace LayoutResolver)> LoadExpectedAsync(string expectedFile)
        {
            LayoutResolverNamespace resolver = this.DefaultResolver;
            List<Dictionary<Utf8String, object>> expected = new List<Dictionary<Utf8String, object>>();
            using (Stream stm = new FileStream(expectedFile, FileMode.Open))
            {
                // Read a RecordIO stream.
                MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(BenchmarkSuiteBase.InitialCapacity);
                Result r = await stm.ReadRecordIOAsync(
                    (ReadOnlyMemory<byte> record) =>
                    {
                        r = BenchmarkSuiteBase.LoadOneRow(record, resolver, out Dictionary<Utf8String, object> rowValue);
                        ResultAssert.IsSuccess(r);
                        expected.Add(rowValue);
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
                        r = default(SegmentHybridRowSerializer).Read(ref row, ref root, true, out Segment s);
                        ResultAssert.IsSuccess(r);
                        Assert.IsNotNull(s.Schema);
                        resolver = new LayoutResolverNamespace(s.Schema, resolver);
                        return Result.Success;
                    },
                    resizer);

                ResultAssert.IsSuccess(r);
            }

            return (expected, resolver);
        }

        private protected static async Task WriteAllRowsAsync(
            string file,
            Namespace ns,
            LayoutResolver resolver,
            Layout layout,
            List<Dictionary<Utf8String, object>> rows)
        {
            using (Stream stm = new FileStream(file, FileMode.Truncate))
            {
                // Create a reusable, resizable buffer.
                MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(BenchmarkSuiteBase.InitialCapacity);

                // Write a RecordIO stream.
                Result r = await stm.WriteRecordIOAsync(
                    new Segment("HybridRow.Tests.Perf Expected Results", ns),
                    (long index, out ReadOnlyMemory<byte> body) =>
                    {
                        body = default;
                        if (index >= rows.Count)
                        {
                            return Result.Success;
                        }

                        StreamingRowGenerator writer = new StreamingRowGenerator(
                            BenchmarkSuiteBase.InitialCapacity,
                            layout,
                            resolver,
                            resizer);

                        Result r2 = writer.WriteBuffer(rows[(int)index]);
                        if (r2 != Result.Success)
                        {
                            return r2;
                        }

                        body = resizer.Memory.Slice(0, writer.Length);
                        return Result.Success;
                    });

                ResultAssert.IsSuccess(r);
            }
        }

        private protected static Result LoadOneRow(ReadOnlyMemory<byte> buffer, LayoutResolver resolver, out Dictionary<Utf8String, object> rowValue)
        {
            RowReader reader = new RowReader(buffer, HybridRowVersion.V1, resolver);
            return DiagnosticConverter.ReaderToDynamic(ref reader, out rowValue);
        }

        private protected static class ResultAssert
        {
            public static void IsSuccess(Result actual)
            {
                if (actual != Result.Success)
                {
                    Assert.AreEqual(Result.Success, actual);
                }
            }
        }
    }
}
