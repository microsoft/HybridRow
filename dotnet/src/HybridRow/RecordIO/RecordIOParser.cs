// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public struct RecordIOParser
    {
        private State state;
        private Segment segment;
        private Record record;

        /// <summary>Describes the type of Hybrid Rows produced by the parser.</summary>
        public enum ProductionType
        {
            /// <summary>No hybrid row was produced. The parser needs more data.</summary>
            None = 0,

            /// <summary>A new segment row was produced.</summary>
            Segment,

            /// <summary>A record in the current segment was produced.</summary>
            Record,
        }

        /// <summary>The states for the internal state machine.</summary>
        /// <remarks>Note: numerical ordering of these states matters.</remarks>
        private enum State : byte
        {
            Start = 0, // Start: no buffers have yet been provided to the parser.
            Error, // Unrecoverable parse error encountered.
            NeedSegmentLength, // Parsing segment header length
            NeedSegment, // Parsing segment header
            NeedHeader, // Parsing HybridRow header
            NeedRecord, // Parsing record header
            NeedRow, // Parsing row body
        }

        /// <summary>True if a valid segment has been parsed.</summary>
        public bool HaveSegment => this.state >= State.NeedHeader;

        /// <summary>If a valid segment has been parsed then current active segment, otherwise undefined.</summary>
        public Segment Segment
        {
            get
            {
                Contract.Requires(this.HaveSegment);
                return this.segment;
            }
        }

        /// <summary>Processes one buffers worth of data possibly advancing the parser state.</summary>
        /// <param name="buffer">The buffer to consume.</param>
        /// <param name="type">Indicates the type of Hybrid Row produced in <paramref name="record" />.</param>
        /// <param name="record">If non-empty, then the body of the next record in the sequence.</param>
        /// <param name="need">
        /// The smallest number of bytes needed to advanced the parser state further. It is
        /// recommended that Process not be called again until at least this number of bytes are available.
        /// </param>
        /// <param name="consumed">
        /// The number of bytes consumed from the input buffer. This number may be less
        /// than the total buffer size if the parser moved to a new state.
        /// </param>
        /// <returns>
        /// <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result.Success" /> if no error
        /// has occurred, otherwise a valid
        /// <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result" /> of the last error encountered
        /// during parsing.
        /// </returns>
        /// >
        public Result Process(Memory<byte> buffer, out ProductionType type, out Memory<byte> record, out int need, out int consumed)
        {
            Result r = Result.Failure;
            Memory<byte> b = buffer;
            type = ProductionType.None;
            record = default;
            switch (this.state)
            {
                case State.Start:
                {
                    this.state = State.NeedSegmentLength;
                    goto case State.NeedSegmentLength;
                }

                case State.NeedSegmentLength:
                {
                    int minimalSegmentRowSize = HybridRowHeader.Size + RecordIOFormatter.SegmentLayout.Size;
                    if (b.Length < minimalSegmentRowSize)
                    {
                        need = minimalSegmentRowSize;
                        consumed = buffer.Length - b.Length;
                        return Result.InsufficientBuffer;
                    }

                    Span<byte> span = b.Span.Slice(0, minimalSegmentRowSize);
                    RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                    RowReader reader = new RowReader(ref row);
                    r = SegmentSerializer.Read(ref reader, out this.segment);
                    if (r != Result.Success)
                    {
                        break;
                    }

                    this.state = State.NeedSegment;
                    goto case State.NeedSegment;
                }

                case State.NeedSegment:
                {
                    if (b.Length < this.segment.Length)
                    {
                        need = this.segment.Length;
                        consumed = buffer.Length - b.Length;
                        return Result.InsufficientBuffer;
                    }

                    Span<byte> span = b.Span.Slice(0, this.segment.Length);
                    RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                    RowReader reader = new RowReader(ref row);
                    r = SegmentSerializer.Read(ref reader, out this.segment);
                    if (r != Result.Success)
                    {
                        break;
                    }

                    record = b.Slice(0, span.Length);
                    b = b.Slice(span.Length);
                    need = 0;
                    this.state = State.NeedHeader;
                    consumed = buffer.Length - b.Length;
                    type = ProductionType.Segment;
                    return Result.Success;
                }

                case State.NeedHeader:
                {
                    if (b.Length < HybridRowHeader.Size)
                    {
                        need = HybridRowHeader.Size;
                        consumed = buffer.Length - b.Length;
                        return Result.InsufficientBuffer;
                    }

                    MemoryMarshal.TryRead(b.Span, out HybridRowHeader header);
                    if (header.Version != HybridRowVersion.V1)
                    {
                        r = Result.InvalidRow;
                        break;
                    }

                    if (header.SchemaId == SystemSchema.SegmentSchemaId)
                    {
                        goto case State.NeedSegment;
                    }

                    if (header.SchemaId == SystemSchema.RecordSchemaId)
                    {
                        goto case State.NeedRecord;
                    }

                    r = Result.InvalidRow;
                    break;
                }

                case State.NeedRecord:
                {
                    int minimalRecordRowSize = HybridRowHeader.Size + RecordIOFormatter.RecordLayout.Size;
                    if (b.Length < minimalRecordRowSize)
                    {
                        need = minimalRecordRowSize;
                        consumed = buffer.Length - b.Length;
                        return Result.InsufficientBuffer;
                    }

                    Span<byte> span = b.Span.Slice(0, minimalRecordRowSize);
                    RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                    RowReader reader = new RowReader(ref row);
                    r = RecordSerializer.Read(ref reader, out this.record);
                    if (r != Result.Success)
                    {
                        break;
                    }

                    b = b.Slice(span.Length);
                    this.state = State.NeedRow;
                    goto case State.NeedRow;
                }

                case State.NeedRow:
                {
                    if (b.Length < this.record.Length)
                    {
                        need = this.record.Length;
                        consumed = buffer.Length - b.Length;
                        return Result.InsufficientBuffer;
                    }

                    record = b.Slice(0, this.record.Length);

                    // Validate that the record has not been corrupted.
                    uint crc32 = Crc32.Update(0, record.Span);
                    if (crc32 != this.record.Crc32)
                    {
                        r = Result.InvalidRow;
                        break;
                    }

                    b = b.Slice(this.record.Length);
                    need = 0;
                    this.state = State.NeedHeader;
                    consumed = buffer.Length - b.Length;
                    type = ProductionType.Record;
                    return Result.Success;
                }
            }

            this.state = State.Error;
            need = 0;
            consumed = buffer.Length - b.Length;
            return r;
        }
    }
}
