// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO
{
    using System;
    using System.IO;
    using System.Runtime.InteropServices;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;

    public static class RecordIOStream
    {
        /// <summary>A function that produces RecordIO record bodies.</summary>
        /// <remarks>
        /// Record bodies are returned as memory blocks. It is expected that each block is a
        /// HybridRow, but any binary data is allowed.
        /// </remarks>
        /// <param name="index">The 0-based index of the record within the segment to be produced.</param>
        /// <param name="buffer">The byte sequence of the record body's row buffer.</param>
        /// <returns>Success if the body was produced without error, the error code otherwise.</returns>
        public delegate Result ProduceFunc(long index, out ReadOnlyMemory<byte> buffer);

        /// <summary>A function that produces RecordIO record bodies.</summary>
        /// <remarks>
        /// Record bodies are returned as memory blocks. It is expected that each block is a
        /// HybridRow, but any binary data is allowed.
        /// </remarks>
        /// <param name="index">The 0-based index of the record within the segment to be produced.</param>
        /// <returns>
        /// A tuple with: Success if the body was produced without error, the error code otherwise.
        /// And, the byte sequence of the record body's row buffer.
        /// </returns>
        public delegate ValueTask<(Result Result, ReadOnlyMemory<byte> Buffer)> ProduceFuncAsync(long index);

        /// <summary>Reads an entire RecordIO stream.</summary>
        [Obsolete("Use ReadOnlyMemory<byte> override instead.")]
        public static Task<Result> ReadRecordIOAsync(
            this Stream stm,
            Func<Memory<byte>, Result> visitRecord,
            Func<Memory<byte>, Result> visitSegment = default,
            MemorySpanResizer<byte> resizer = default)
        {
            return stm.ReadRecordIOAsync(
                rom => visitRecord(MemoryMarshal.AsMemory(rom)),
                (visitSegment != null) ? rom => visitSegment(MemoryMarshal.AsMemory(rom)) : default(Func<ReadOnlyMemory<byte>, Result>),
                resizer);
        }

        /// <summary>Reads an entire RecordIO stream.</summary>
        public static Task<Result> ReadRecordIOAsync(
            this Stream stm,
            Func<ReadOnlyMemory<byte>, Result> visitRecord,
            Func<ReadOnlyMemory<byte>, Result> visitSegment = default,
            MemorySpanResizer<byte> resizer = default)
        {
            return stm.ReadRecordIOAsync(
                rom => new ValueTask<Result>(visitRecord(rom)),
                (visitSegment != null) ? rom => new ValueTask<Result>(visitSegment(rom)) : default(Func<ReadOnlyMemory<byte>, ValueTask<Result>>),
                resizer);
        }

        /// <summary>Reads an entire RecordIO stream.</summary>
        /// <param name="stm">The stream to read from.</param>
        /// <param name="visitRecord">
        /// A (required) delegate that is called once for each record.
        /// <p>
        /// <paramref name="visitRecord" /> is passed a <see cref="Memory{T}" /> of the byte sequence of the
        /// record body's row buffer.
        /// </p>
        /// <p>If <paramref name="visitRecord" /> returns an error then the sequence is aborted.</p>
        /// </param>
        /// <param name="visitSegment">
        /// An (optional) delegate that is called once for each segment header.
        /// <p>
        /// If <paramref name="visitSegment" /> is not provided then segment headers are parsed but skipped
        /// over.
        /// </p>
        /// <p>
        /// <paramref name="visitSegment" /> is passed a <see cref="Memory{T}" /> of the byte sequence of
        /// the segment header's row buffer.
        /// </p>
        /// <p>If <paramref name="visitSegment" /> returns an error then the sequence is aborted.</p>
        /// </param>
        /// <param name="resizer">Optional memory resizer.</param>
        /// <returns>Success if the stream is parsed without error, the error code otherwise.</returns>
        public static async Task<Result> ReadRecordIOAsync(
            this Stream stm,
            Func<ReadOnlyMemory<byte>, ValueTask<Result>> visitRecord,
            Func<ReadOnlyMemory<byte>, ValueTask<Result>> visitSegment = default,
            MemorySpanResizer<byte> resizer = default)
        {
            Contract.Requires(stm != null);
            Contract.Requires(visitRecord != null);

            // Create a reusable, resizable buffer if the caller didn't provide one.
            resizer = resizer ?? new MemorySpanResizer<byte>();

            RecordIOParser parser = default;
            int need = 0;
            Memory<byte> active = resizer.Memory;
            Memory<byte> avail = default;
            while (true)
            {
                Contract.Assert(avail.Length < active.Length);
                Contract.Assert(active.Length > 0);
                Contract.Assert(active.Length >= need);

                int read = await stm.ReadAsync(active.Slice(avail.Length));
                if (read == 0)
                {
                    break;
                }

                avail = active.Slice(0, avail.Length + read);

                // If there isn't enough data to move the parser forward then just read again.
                if (avail.Length < need)
                {
                    continue;
                }

                // Process the available data until no more forward progress is possible.
                while (avail.Length > 0)
                {
                    // Loop around processing available data until we don't have anymore
                    Result r = parser.Process(
                        avail,
                        out RecordIOParser.ProductionType prodType,
                        out ReadOnlyMemory<byte> record,
                        out need,
                        out int consumed);

                    if ((r != Result.Success) && (r != Result.InsufficientBuffer))
                    {
                        return r;
                    }

                    active = active.Slice(consumed);
                    avail = avail.Slice(consumed);
                    if (avail.IsEmpty)
                    {
                        active = resizer.Memory;
                    }

                    // If there wasn't enough data to move the parser forward then get more data.
                    if (r == Result.InsufficientBuffer)
                    {
                        if (need > active.Length)
                        {
                            resizer.Resize(need, avail.Span);
                            active = resizer.Memory;
                            avail = resizer.Memory.Slice(0, avail.Length);
                        }

                        break;
                    }

                    // Validate the Segment
                    if (prodType == RecordIOParser.ProductionType.Segment)
                    {
                        Contract.Assert(!record.IsEmpty);
                        if (visitSegment != null)
                        {
                            r = await visitSegment(record);
                            if (r != Result.Success)
                            {
                                return r;
                            }
                        }
                    }

                    // Consume the record.
                    if (prodType == RecordIOParser.ProductionType.Record)
                    {
                        Contract.Assert(!record.IsEmpty);

                        r = await visitRecord(record);
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    }
                }
            }

            // Make sure we processed all of the available data.
            Contract.Assert(avail.Length == 0);
            return Result.Success;
        }

        /// <summary>Writes a RecordIO segment into a stream.</summary>
        /// <param name="stm">The stream to write to.</param>
        /// <param name="segment">The segment header to write.</param>
        /// <param name="produce">
        /// A function to produces the record bodies for the segment.
        /// <p>
        /// The <paramref name="produce" /> function is called until either an error is encountered or it
        /// produces an empty body. An empty body terminates the segment.
        /// </p>
        /// <p>If <paramref name="produce" /> returns an error then the sequence is aborted.</p>
        /// </param>
        /// <param name="resizer">
        /// Optional memory resizer for RecordIO  metadata row buffers.
        /// <p>
        /// <em>Note:</em> This should <em>NOT</em> be the same resizer used to process any rows as both
        /// blocks of memory are used concurrently.
        /// </p>
        /// </param>
        /// <returns>Success if the stream is written without error, the error code otherwise.</returns>
        public static Task<Result> WriteRecordIOAsync(
            this Stream stm,
            Segment segment,
            ProduceFunc produce,
            MemorySpanResizer<byte> resizer = default)
        {
            return stm.WriteRecordIOAsync(
                segment,
                index =>
                {
                    Result r = produce(index, out ReadOnlyMemory<byte> buffer);
                    return new ValueTask<(Result, ReadOnlyMemory<byte>)>((r, buffer));
                },
                resizer);
        }

        /// <summary>Writes a RecordIO segment into a stream.</summary>
        /// <param name="stm">The stream to write to.</param>
        /// <param name="segment">The segment header to write.</param>
        /// <param name="produce">
        /// A function to produces the record bodies for the segment.
        /// <p>
        /// The <paramref name="produce" /> function is called until either an error is encountered or it
        /// produces an empty body. An empty body terminates the segment.
        /// </p>
        /// <p>If <paramref name="produce" /> returns an error then the sequence is aborted.</p>
        /// </param>
        /// <param name="resizer">
        /// Optional memory resizer for RecordIO  metadata row buffers.
        /// <p>
        /// <em>Note:</em> This should <em>NOT</em> be the same resizer used to process any rows as both
        /// blocks of memory are used concurrently.
        /// </p>
        /// </param>
        /// <returns>Success if the stream is written without error, the error code otherwise.</returns>
        public static async Task<Result> WriteRecordIOAsync(
            this Stream stm,
            Segment segment,
            ProduceFuncAsync produce,
            MemorySpanResizer<byte> resizer = default)
        {
            // Create a reusable, resizable buffer if the caller didn't provide one.
            resizer = resizer ?? new MemorySpanResizer<byte>();

            // Write a RecordIO stream.
            Result r = RecordIOStream.FormatSegment(segment, resizer, out Memory<byte> metadata);
            if (r != Result.Success)
            {
                return r;
            }

            await stm.WriteAsync(metadata);

            long index = 0;
            while (true)
            {
                ReadOnlyMemory<byte> body;
                (r, body) = await produce(index++);
                if (r != Result.Success)
                {
                    return r;
                }

                if (body.IsEmpty)
                {
                    break;
                }

                r = RecordIOStream.FormatRow(body, resizer, out metadata);
                if (r != Result.Success)
                {
                    return r;
                }

                // Metadata and Body memory blocks should not overlap since they are both in
                // play at the same time. If they do this usually means that the same resizer
                // was incorrectly used for both. Check the resizer parameter passed to
                // WriteRecordIOAsync for metadata.
                Contract.Assert(!metadata.Span.Overlaps(body.Span));

                await stm.WriteAsync(metadata);
                await stm.WriteAsync(body);
            }

            return Result.Success;
        }

        /// <summary>Format a segment.</summary>
        /// <param name="segment">The segment to format.</param>
        /// <param name="resizer">The resizer to use in allocating a buffer for the segment.</param>
        /// <param name="block">The byte sequence of the written row buffer.</param>
        /// <returns>Success if the write completes without error, the error code otherwise.</returns>
        private static Result FormatSegment(Segment segment, MemorySpanResizer<byte> resizer, out Memory<byte> block)
        {
            Result r = RecordIOFormatter.FormatSegment(segment, out RowBuffer row, resizer);
            if (r != Result.Success)
            {
                block = default;
                return r;
            }

            block = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }

        /// <summary>Compute and format a record header for the given record body.</summary>
        /// <param name="body">The body whose record header should be formatted.</param>
        /// <param name="resizer">The resizer to use in allocating a buffer for the record header.</param>
        /// <param name="block">The byte sequence of the written row buffer.</param>
        /// <returns>Success if the write completes without error, the error code otherwise.</returns>
        private static Result FormatRow(ReadOnlyMemory<byte> body, MemorySpanResizer<byte> resizer, out Memory<byte> block)
        {
            Result r = RecordIOFormatter.FormatRecord(body, out RowBuffer row, resizer);
            if (r != Result.Success)
            {
                block = default;
                return r;
            }

            block = resizer.Memory.Slice(0, row.Length);
            return Result.Success;
        }
    }
}
