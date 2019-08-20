// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public static class RecordIOFormatter
    {
        internal static readonly Layout SegmentLayout = SystemSchema.LayoutResolver.Resolve(SystemSchema.SegmentSchemaId);
        internal static readonly Layout RecordLayout = SystemSchema.LayoutResolver.Resolve(SystemSchema.RecordSchemaId);

        public static Result FormatSegment(Segment segment, out RowBuffer row, ISpanResizer<byte> resizer = default)
        {
            resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            int estimatedSize = HybridRowHeader.Size + RecordIOFormatter.SegmentLayout.Size + segment.Comment?.Length ??
                                0 + segment.SDL?.Length ?? 0 + 20;

            return RecordIOFormatter.FormatObject(resizer, estimatedSize, RecordIOFormatter.SegmentLayout, segment, SegmentSerializer.Write, out row);
        }

        public static Result FormatRecord(ReadOnlyMemory<byte> body, out RowBuffer row, ISpanResizer<byte> resizer = default)
        {
            resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            int estimatedSize = HybridRowHeader.Size + RecordIOFormatter.RecordLayout.Size + body.Length;
            uint crc32 = Crc32.Update(0, body.Span);
            Record record = new Record(body.Length, crc32);
            return RecordIOFormatter.FormatObject(resizer, estimatedSize, RecordIOFormatter.RecordLayout, record, RecordSerializer.Write, out row);
        }

        private static Result FormatObject<T>(
            ISpanResizer<byte> resizer,
            int initialCapacity,
            Layout layout,
            T obj,
            RowWriter.WriterFunc<T> writer,
            out RowBuffer row)
        {
            row = new RowBuffer(initialCapacity, resizer);
            row.InitLayout(HybridRowVersion.V1, layout, SystemSchema.LayoutResolver);
            Result r = RowWriter.WriteBuffer(ref row, obj, writer);
            if (r != Result.Success)
            {
                row = default;
                return r;
            }

            return Result.Success;
        }
    }
}
