// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;

import java.io.InputStream;
import java.io.OutputStream;

public final class RecordIOStream {
/**
     * A function that produces RecordIO record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a
     * HybridRow, but any binary data is allowed.
     *
     * @param index The 0-based index of the record within the segment to be produced.
     * @return A tuple with: Success if the body was produced without error, the error code otherwise.
     * And, the byte sequence of the record body's row buffer.
     */
    public delegate ValueTask

        ProduceFuncAsync(long index);<(Result,ReadOnlyMemory<Byte>)>

    /**
     * Reads an entire RecordIO stream.
     *
     * @param stm          The stream to read from.
     * @param visitRecord  A (required) delegate that is called once for each record.
     *                     <p>
     *                     <paramref name="visitRecord" /> is passed a {@link Memory{T}} of the byte sequence
     *                     of the
     *                     record body's row buffer.
     *                     </p>
     *                     <p>If <paramref name="visitRecord" /> returns an error then the sequence is aborted.</p>
     * @param visitSegment An (optional) delegate that is called once for each segment header.
     *                     <p>
     *                     If <paramref name="visitSegment" /> is not provided then segment headers are parsed but
     *                     skipped
     *                     over.
     *                     </p>
     *                     <p>
     *                     <paramref name="visitSegment" /> is passed a {@link Memory{T}} of the byte sequence of
     *                     the segment header's row buffer.
     *                     </p>
     *                     <p>If <paramref name="visitSegment" /> returns an error then the sequence is aborted.</p>
     * @param resizer      Optional memory resizer.
     * @return Success if the stream is parsed without error, the error code otherwise.
     */

    public static Task<Result> ReadRecordIOAsync(Stream stm, Func<Memory<Byte>, Result> visitRecord,
                                                 Func<Memory<Byte>, Result> visitSegment) {
        return ReadRecordIOAsync(stm, visitRecord, visitSegment, null);
    }

    public static Task<Result> ReadRecordIOAsync(Stream stm, Func<Memory<Byte>, Result> visitRecord) {
        return ReadRecordIOAsync(stm, visitRecord, null, null);
    }

    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: public static async Task<Result> ReadRecordIOAsync(this Stream stm, Func<Memory<byte>, Result>
    // visitRecord, Func<Memory<byte>, Result> visitSegment = default, MemorySpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public static Task<Result> ReadRecordIOAsync(InputStream stm,
                                                 tangible.Func1Param<Memory<Byte>, Result> visitRecord,
                                                 tangible.Func1Param<Memory<Byte>, Result> visitSegment,
                                                 MemorySpanResizer<Byte> resizer) {
        checkArgument(stm != null);
        checkArgument(visitRecord != null);

        // Create a reusable, resizable buffer if the caller didn't provide one.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: resizer = resizer != null ? resizer : new MemorySpanResizer<byte>();
        resizer = resizer != null ? resizer : new MemorySpanResizer<Byte>();

        RecordIOParser parser = null;
        int need = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Memory<byte> active = resizer.Memory;
        Memory<Byte> active = resizer.getMemory();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Memory<byte> avail = default;
        Memory<Byte> avail = null;
        while (true) {
            checkState(avail.Length < active.Length);
            checkState(active.Length > 0);
            checkState(active.Length >= need);

            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            int read = await stm.ReadAsync(active.Slice(avail.Length));
            if (read == 0) {
                break;
            }

            avail = active.Slice(0, avail.Length + read);

            // If there isn't enough data to move the parser forward then just read again.
            if (avail.Length < need) {
                continue;
            }

            // Process the available data until no more forward progress is possible.
            while (avail.Length > 0) {
                // Loop around processing available data until we don't have anymore
                RecordIOParser.ProductionType prodType;
                Out<RecordIOParser.ProductionType> tempOut_prodType = new Out<RecordIOParser.ProductionType>();
                Memory<Byte> record;
                Out<Memory<Byte>> tempOut_record = new Out<Memory<Byte>>();
                Out<Integer> tempOut_need = new Out<Integer>();
                int consumed;
                Out<Integer> tempOut_consumed = new Out<Integer>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Result r = parser.Process(avail, out RecordIOParser.ProductionType prodType, out
                // Memory<byte> record, out need, out int consumed);
                Result r = parser.process(avail, tempOut_prodType, tempOut_record, tempOut_need, tempOut_consumed);
                consumed = tempOut_consumed.get();
                need = tempOut_need.get();
                record = tempOut_record.get();
                prodType = tempOut_prodType.get();

                if ((r != Result.SUCCESS) && (r != Result.INSUFFICIENT_BUFFER)) {
                    return r;
                }

                active = active.Slice(consumed);
                avail = avail.Slice(consumed);
                if (avail.IsEmpty) {
                    active = resizer.getMemory();
                }

                // If there wasn't enough data to move the parser forward then get more data.
                if (r == Result.INSUFFICIENT_BUFFER) {
                    if (need > active.Length) {
                        resizer.Resize(need, avail.Span);
                        active = resizer.getMemory();
                        avail = resizer.getMemory().Slice(0, avail.Length);
                    }

                    break;
                }

                // Validate the Segment
                if (prodType == RecordIOParser.ProductionType.SEGMENT) {
                    checkState(!record.IsEmpty);
                    r = visitSegment == null ? null : visitSegment.invoke(record) != null ?
                        visitSegment.invoke(record) : Result.SUCCESS;
                    if (r != Result.SUCCESS) {
                        return r;
                    }
                }

                // Consume the record.
                if (prodType == RecordIOParser.ProductionType.RECORD) {
                    checkState(!record.IsEmpty);

                    r = visitRecord.invoke(record);
                    if (r != Result.SUCCESS) {
                        return r;
                    }
                }
            }
        }

        // Make sure we processed all of the available data.
        checkState(avail.Length == 0);
        return Result.SUCCESS;
    }

    /**
     * Writes a RecordIO segment into a stream.
     *
     * @param stm     The stream to write to.
     * @param segment The segment header to write.
     * @param produce A function to produces the record bodies for the segment.
     *                <p>
     *                The <paramref name="produce" /> function is called until either an error is encountered or it
     *                produces an empty body. An empty body terminates the segment.
     *                </p>
     *                <p>If <paramref name="produce" /> returns an error then the sequence is aborted.</p>
     * @param resizer Optional memory resizer for RecordIO  metadata row buffers.
     *                <p>
     *                <em>Note:</em> This should <em>NOT</em> be the same resizer used to process any rows as both
     *                blocks of memory are used concurrently.
     *                </p>
     * @return Success if the stream is written without error, the error code otherwise.
     */

    public static Task<Result> WriteRecordIOAsync(Stream stm, Segment segment, ProduceFunc produce) {
        return WriteRecordIOAsync(stm, segment, produce, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public static Task<Result> WriteRecordIOAsync(this Stream stm, Segment segment, ProduceFunc
    // produce, MemorySpanResizer<byte> resizer = default)
    // TODO: C# TO JAVA CONVERTER: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public static Task<Result> WriteRecordIOAsync(Stream stm, Segment segment, ProduceFunc produce,
                                                  MemorySpanResizer<Byte> resizer) {
        return RecordIOStream.WriteRecordIOAsync(stm,
            segment.clone(), index ->
        {
            ReadOnlyMemory<Byte> buffer;
            Out<ReadOnlyMemory<Byte>> tempOut_buffer = new Out<ReadOnlyMemory<Byte>>();
            buffer = tempOut_buffer.get();
            return new ValueTask<(Result, ReadOnlyMemory < Byte >) > ((r,buffer))
        }, resizer);
    }

    /**
     * Writes a RecordIO segment into a stream.
     *
     * @param stm     The stream to write to.
     * @param segment The segment header to write.
     * @param produce A function to produces the record bodies for the segment.
     *                <p>
     *                The <paramref name="produce" /> function is called until either an error is encountered or it
     *                produces an empty body. An empty body terminates the segment.
     *                </p>
     *                <p>If <paramref name="produce" /> returns an error then the sequence is aborted.</p>
     * @param resizer Optional memory resizer for RecordIO  metadata row buffers.
     *                <p>
     *                <em>Note:</em> This should <em>NOT</em> be the same resizer used to process any rows as both
     *                blocks of memory are used concurrently.
     *                </p>
     * @return Success if the stream is written without error, the error code otherwise.
     */

    public static Task<Result> WriteRecordIOAsync(Stream stm, Segment segment, ProduceFuncAsync produce) {
        return WriteRecordIOAsync(stm, segment, produce, null);
    }

    // TODO: C# TO JAVA CONVERTER: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: public static async Task<Result> WriteRecordIOAsync(this Stream stm, Segment segment,
    // ProduceFuncAsync produce, MemorySpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public static Task<Result> WriteRecordIOAsync(OutputStream stm, Segment segment, ProduceFuncAsync produce,
                                                  MemorySpanResizer<Byte> resizer) {
        // Create a reusable, resizable buffer if the caller didn't provide one.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: resizer = resizer != null ? resizer : new MemorySpanResizer<byte>();
        resizer = resizer != null ? resizer : new MemorySpanResizer<Byte>();

        // Write a RecordIO stream.
        Memory<Byte> metadata;
        Out<Memory<Byte>> tempOut_metadata = new Out<Memory<Byte>>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = RecordIOStream.FormatSegment(segment, resizer, out Memory<byte> metadata);
        Result r = RecordIOStream.FormatSegment(segment.clone(), resizer, tempOut_metadata);
        metadata = tempOut_metadata.get();
        if (r != Result.SUCCESS) {
            return r;
        }

        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        await stm.WriteAsync(metadata);

        long index = 0;
        while (true) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ReadOnlyMemory<byte> body;
            ReadOnlyMemory<Byte> body;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# deconstruction assignments:
            (r, body) =await produce (index++);
            if (r != Result.SUCCESS) {
                return r;
            }

            if (body.IsEmpty) {
                break;
            }

            Out<Memory<Byte>> tempOut_metadata2 = new Out<Memory<Byte>>();
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: r = RecordIOStream.FormatRow(body, resizer, out metadata);
            r = RecordIOStream.FormatRow(body, resizer, tempOut_metadata2);
            metadata = tempOut_metadata2.get();
            if (r != Result.SUCCESS) {
                return r;
            }

            // Metadata and Body memory blocks should not overlap since they are both in
            // play at the same time. If they do this usually means that the same resizer
            // was incorrectly used for both. Check the resizer parameter passed to
            // WriteRecordIOAsync for metadata.
            checkState(!metadata.Span.Overlaps(body.Span));

            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(metadata);
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(body);
        }

        return Result.SUCCESS;
    }

    /**
     * Compute and format a record header for the given record body.
     *
     * @param body    The body whose record header should be formatted.
     * @param resizer The resizer to use in allocating a buffer for the record header.
     * @param block   The byte sequence of the written row buffer.
     * @return Success if the write completes without error, the error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static Result FormatRow(ReadOnlyMemory<byte> body, MemorySpanResizer<byte> resizer, out Memory<byte> block)
    private static Result FormatRow(ReadOnlyMemory<Byte> body, MemorySpanResizer<Byte> resizer, Out<Memory<Byte>> block) {
        RowBuffer row;
        Out<RowBuffer> tempOut_row = new Out<RowBuffer>();
        Result r = RecordIOFormatter.FormatRecord(body, tempOut_row, resizer);
        row = tempOut_row.get();
        if (r != Result.SUCCESS) {
            block.setAndGet(null);
            return r;
        }

        block.setAndGet(resizer.getMemory().Slice(0, row.Length));
        return Result.SUCCESS;
    }

    /**
     * Format a segment.
     *
     * @param segment The segment to format.
     * @param resizer The resizer to use in allocating a buffer for the segment.
     * @param block   The byte sequence of the written row buffer.
     * @return Success if the write completes without error, the error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static Result FormatSegment(Segment segment, MemorySpanResizer<byte> resizer, out
    // Memory<byte> block)
    private static Result FormatSegment(Segment segment, MemorySpanResizer<Byte> resizer,
                                        Out<Memory<Byte>> block) {
        RowBuffer row;
        Out<RowBuffer> tempOut_row =
            new Out<RowBuffer>();
        Result r = RecordIOFormatter.FormatSegment(segment.clone(), tempOut_row, resizer);
        row = tempOut_row.get();
        if (r != Result.SUCCESS) {
            block.setAndGet(null);
            return r;
        }

        block.setAndGet(resizer.getMemory().Slice(0, row.Length));
        return Result.SUCCESS;
    }

    /**
     * A function that produces RecordIO record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a
     * HybridRow, but any binary data is allowed.
     *
     * @param index  The 0-based index of the record within the segment to be produced.
     * @param buffer The byte sequence of the record body's row buffer.
     * @return Success if the body was produced without error, the error code otherwise.
     */
    @FunctionalInterface
    public interface ProduceFunc {
        Result invoke(long index, Out<ReadOnlyMemory<Byte>> buffer);
    }
}