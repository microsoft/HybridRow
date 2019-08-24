//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.perf;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolverNamespace;
import com.azure.data.cosmos.serialization.hybridrow.layouts.SystemSchema;
import com.azure.data.cosmos.serialization.hybridrow.recordio.RecordIOStream;
import com.azure.data.cosmos.serialization.hybridrow.recordio.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private


public class BenchmarkSuiteBase {
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    //ORIGINAL LINE: private protected const int InitialCapacity = 2 * 1024 * 1024;
    protected static final int InitialCapacity = 2 * 1024 * 1024;
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    //ORIGINAL LINE: private protected LayoutResolverNamespace DefaultResolver = (LayoutResolverNamespace)
    // SystemSchema.LayoutResolver;
    protected LayoutResolverNamespace DefaultResolver = (LayoutResolverNamespace)SystemSchema.LayoutResolver;

    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	private protected async Task<(List<Dictionary<Utf8String, object>>, LayoutResolverNamespace)>
    //	LoadExpectedAsync(string expectedFile)
    //		{
    //			LayoutResolverNamespace resolver = this.DefaultResolver;
    //			List<Dictionary<Utf8String, object>> expected = new List<Dictionary<Utf8String, object>>();
    //			using (Stream stm = new FileStream(expectedFile, FileMode.Open))
    //			{
    //				// Read a RecordIO stream.
    //				MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(BenchmarkSuiteBase.InitialCapacity);
    //				Result r = await stm.ReadRecordIOAsync(record =>
    //					{
    //						r = BenchmarkSuiteBase.LoadOneRow(record, resolver, out Dictionary<Utf8String, object>
    //						rowValue);
    //						ResultAssert.IsSuccess(r);
    //						expected.Add(rowValue);
    //						return Result.Success;
    //					}
    //				   , segment =>
    //					{
    //						r = SegmentSerializer.Read(segment.Span, SystemSchema.LayoutResolver, out Segment s);
    //						ResultAssert.IsSuccess(r);
    //						Assert.IsNotNull(s.SDL);
    //						resolver = new LayoutResolverNamespace(Namespace.Parse(s.SDL), resolver);
    //						return Result.Success;
    //					}
    //				   , resizer);
    //
    //				ResultAssert.IsSuccess(r);
    //			}
    //
    //			return (expected, resolver);
    //		}

    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    //ORIGINAL LINE: private protected static Result LoadOneRow(Memory<byte> buffer, LayoutResolver resolver, out
    // Dictionary<Utf8String, object> rowValue)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    protected static Result LoadOneRow(Memory<Byte> buffer, LayoutResolver resolver,
                                       Out<HashMap<Utf8String, Object>> rowValue) {
        RowBuffer row = new RowBuffer(buffer.Span, HybridRowVersion.V1, resolver);
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(row);
        RowReader reader = new RowReader(tempReference_row);
        row = tempReference_row.get();
        Reference<RowReader> tempReference_reader =
            new Reference<RowReader>(reader);
        Result tempVar = DiagnosticConverter.ReaderToDynamic(tempReference_reader, rowValue);
        reader = tempReference_reader.get();
        return tempVar;
    }

    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: private protected static async Task WriteAllRowsAsync(string file, string sdl, LayoutResolver
    // resolver, Layout layout, List<Dictionary<Utf8String, object>> rows)
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    protected static Task WriteAllRowsAsync(String file, String sdl, LayoutResolver resolver, Layout layout,
                                            ArrayList<HashMap<Utf8String, Object>> rows) {
        try (Stream stm = new FileStream(file, FileMode.Truncate)) {
            // Create a reusable, resizable buffer.
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>(BenchmarkSuiteBase
            // .InitialCapacity);
            MemorySpanResizer<Byte> resizer = new MemorySpanResizer<Byte>(BenchmarkSuiteBase.InitialCapacity);

            // Write a RecordIO stream.
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            // TODO: C# TO JAVA CONVERTER: The following lambda contained an unresolved 'out' keyword - these are
            // not converted by C# to Java Converter:
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: Result r = await stm.WriteRecordIOAsync(new Segment("HybridRow.Tests.Perf Expected
            // Results", sdl), (long index, out ReadOnlyMemory<byte> body) =>
            Result r = await RecordIOStream.WriteRecordIOAsync(stm,
                new Segment("HybridRow.Tests.Perf Expected Results", sdl), (long index, out ReadOnlyMemory<Byte>body) ->
                {
                    body = null;
                    if (index >= rows.size()) {
                        return Result.Success;
                    }

                    StreamingRowGenerator writer = new StreamingRowGenerator(BenchmarkSuiteBase.InitialCapacity, layout,
                        resolver, resizer);

                    Result r2 = writer.WriteBuffer(rows.get((int)index));
                    if (r2 != Result.Success) {
                        return r2;
                    }

                    body = resizer.getMemory().Slice(0, writer.getLength());
                    return Result.Success;
                });

            ResultAssert.IsSuccess(r);
        }
    }

    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to C# 'private protected' access:
    //ORIGINAL LINE: private protected static class ResultAssert
    protected static class ResultAssert {
        public static void IsSuccess(Result actual) {
			assert actual == Result.Success || Result.Success == actual;
        }
    }
}