// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public static class RecordIOFormatter
    {
        private static readonly Layout SegmentLayout = SchemasHrSchema.LayoutResolver.Resolve((SchemaId)SegmentHybridRowSerializer.SchemaId);
        private static readonly Layout RecordLayout = SchemasHrSchema.LayoutResolver.Resolve((SchemaId)RecordHybridRowSerializer.SchemaId);

        public static Result FormatSegment(Segment segment, out RowBuffer row, ISpanResizer<byte> resizer = default)
        {
            resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            int estimatedSize = HybridRowHeader.Size + RecordIOFormatter.SegmentLayout.Size + segment.Comment?.Length ??
                                0 + segment.SDL?.Length ?? 0 + 20;

            return RecordIOFormatter.FormatObject<Segment, SegmentHybridRowSerializer>(
                resizer, estimatedSize, RecordIOFormatter.SegmentLayout, segment, out row);
        }

        public static Result FormatRecord(ReadOnlyMemory<byte> body, out RowBuffer row, ISpanResizer<byte> resizer = default)
        {
            resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            int estimatedSize = HybridRowHeader.Size + RecordIOFormatter.RecordLayout.Size + body.Length;
            uint crc32 = Crc32.Update(0, body.Span);
            Record record = new Record(body.Length, crc32);
            return RecordIOFormatter.FormatObject<Record, RecordHybridRowSerializer>(
                resizer, estimatedSize, RecordIOFormatter.RecordLayout, record, out row);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static Result FormatObject<T, TSerializer>(
            ISpanResizer<byte> resizer,
            int initialCapacity,
            Layout layout,
            T obj,
            out RowBuffer row)
            where TSerializer : struct, IHybridRowSerializer<T>
        {
            row = new RowBuffer(initialCapacity, resizer);
            row.InitLayout(HybridRowVersion.V1, layout, SystemSchema.LayoutResolver);
            RowCursor root = RowCursor.Create(ref row);
            Result r = default(TSerializer).Write(ref row, ref root, true, default, obj);
            if (r != Result.Success)
            {
                row = default;
                return r;
            }

            return Result.Success;
        }
    }
}
