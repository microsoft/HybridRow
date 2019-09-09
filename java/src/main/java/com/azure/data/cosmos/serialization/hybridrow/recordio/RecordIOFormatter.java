// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowHeader;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.ISpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.SystemSchema;

public final class RecordIOFormatter {

    public static final Layout RECORD_LAYOUT = SystemSchema.layoutResolver.resolve(SystemSchema.RECORD_SCHEMA_ID);
    public static final Layout SEGMENT_LAYOUT = SystemSchema.layoutResolver.resolve(SystemSchema.SEGMENT_SCHEMA_ID);

    public static Result FormatRecord(ReadOnlyMemory<Byte> body, Out<RowBuffer> row) {
        return FormatRecord(body, row, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public static Result FormatRecord(ReadOnlyMemory<byte> body, out RowBuffer row,
    // ISpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public static Result FormatRecord(ReadOnlyMemory<Byte> body, Out<RowBuffer> row,
                                      ISpanResizer<Byte> resizer) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: resizer = resizer != null ? resizer : DefaultSpanResizer<byte>.Default;
        resizer = resizer != null ? resizer : DefaultSpanResizer < Byte >.Default;
        int estimatedSize = HybridRowHeader.BYTES + RecordIOFormatter.RECORD_LAYOUT.getSize() + body.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: uint crc32 = Crc32.Update(0, body.Span);
        int crc32 = Crc32.Update(0, body.Span);
        Record record = new Record(body.Length, crc32);
        return RecordIOFormatter.FormatObject(resizer, estimatedSize, RecordIOFormatter.RECORD_LAYOUT, record.clone(),
            RecordSerializer.Write, row.clone());
    }

    public static Result FormatSegment(Segment segment, Out<RowBuffer> row) {
        return FormatSegment(segment, row, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public static Result FormatSegment(Segment segment, out RowBuffer row, ISpanResizer<byte>
    // resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public static Result FormatSegment(Segment segment, Out<RowBuffer> row, ISpanResizer<Byte> resizer) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: resizer = resizer != null ? resizer : DefaultSpanResizer<byte>.Default;
        resizer = resizer != null ? resizer : DefaultSpanResizer < Byte >.Default;
        int estimatedSize =
            HybridRowHeader.BYTES + RecordIOFormatter.SEGMENT_LAYOUT.getSize() + segment.comment() == null ? null :
                segment.comment().length() != null ? segment.comment().length() : 0 + segment.sdl() == null ? null :
                    segment.sdl().length() != null ? segment.sdl().length() : 0 + 20;

        return RecordIOFormatter.FormatObject(resizer, estimatedSize, RecordIOFormatter.SEGMENT_LAYOUT,
            segment.clone(), SegmentSerializer.Write, row.clone());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static Result FormatObject<T>(ISpanResizer<byte> resizer, int initialCapacity, Layout
    // layout, T obj, RowWriter.WriterFunc<T> writer, out RowBuffer row)
    private static <T> Result FormatObject(ISpanResizer<Byte> resizer, int initialCapacity, Layout layout, T obj,
                                           RowWriter.WriterFunc<T> writer, Out<RowBuffer> row) {
        row.setAndGet(new RowBuffer(initialCapacity, resizer));
        row.get().initLayout(HybridRowVersion.V1, layout, SystemSchema.layoutResolver);
        Result r = RowWriter.WriteBuffer(row.clone(), obj, writer);
        if (r != Result.SUCCESS) {
            row.setAndGet(null);
            return r;
        }

        return Result.SUCCESS;
    }
}