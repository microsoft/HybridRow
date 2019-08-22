//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.recordio;

import azure.data.cosmos.serialization.hybridrow.MemorySpanResizer;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;

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
     *                     <paramref name="visitRecord" /> is passed a <see cref="Memory{T}" /> of the byte sequence
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
     *                     <paramref name="visitSegment" /> is passed a <see cref="Memory{T}" /> of the byte sequence of
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
        Contract.Requires(stm != null);
        Contract.Requires(visitRecord != null);

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
                tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO.RecordIOParser.ProductionType> tempOut_prodType = new tangible.OutObject<Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO.RecordIOParser.ProductionType>();
                Memory<Byte> record;
                tangible.OutObject<Memory<Byte>> tempOut_record = new tangible.OutObject<Memory<Byte>>();
                tangible.OutObject<Integer> tempOut_need = new tangible.OutObject<Integer>();
                int consumed;
                tangible.OutObject<Integer> tempOut_consumed = new tangible.OutObject<Integer>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Result r = parser.Process(avail, out RecordIOParser.ProductionType prodType, out
                // Memory<byte> record, out need, out int consumed);
                Result r = parser.Process(avail, tempOut_prodType, tempOut_record, tempOut_need, tempOut_consumed);
                consumed = tempOut_consumed.argValue;
                need = tempOut_need.argValue;
                record = tempOut_record.argValue;
                prodType = tempOut_prodType.argValue;

                if ((r != Result.Success) && (r != Result.InsufficientBuffer)) {
                    return r;
                }

                active = active.Slice(consumed);
                avail = avail.Slice(consumed);
                if (avail.IsEmpty) {
                    active = resizer.getMemory();
                }

                // If there wasn't enough data to move the parser forward then get more data.
                if (r == Result.InsufficientBuffer) {
                    if (need > active.Length) {
                        resizer.Resize(need, avail.Span);
                        active = resizer.getMemory();
                        avail = resizer.getMemory().Slice(0, avail.Length);
                    }

                    break;
                }

                // Validate the Segment
                if (prodType == RecordIOParser.ProductionType.Segment) {
                    checkState(!record.IsEmpty);
                    r = visitSegment == null ? null : visitSegment.invoke(record) != null ?
                        visitSegment.invoke(record) : Result.Success;
                    if (r != Result.Success) {
                        return r;
                    }
                }

                // Consume the record.
                if (prodType == RecordIOParser.ProductionType.Record) {
                    checkState(!record.IsEmpty);

                    r = visitRecord.invoke(record);
                    if (r != Result.Success) {
                        return r;
                    }
                }
            }
        }

        // Make sure we processed all of the available data.
        Contract.Assert(avail.Length == 0);
        return Result.Success;
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
        return Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO.RecordIOStream.WriteRecordIOAsync(stm,
            segment.clone(), index ->
        {
            ReadOnlyMemory<Byte> buffer;
            tangible.OutObject<ReadOnlyMemory<Byte>> tempOut_buffer = new tangible.OutObject<ReadOnlyMemory<Byte>>();
            buffer = tempOut_buffer.argValue;
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
        tangible.OutObject<Memory<Byte>> tempOut_metadata = new tangible.OutObject<Memory<Byte>>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = RecordIOStream.FormatSegment(segment, resizer, out Memory<byte> metadata);
        Result r = RecordIOStream.FormatSegment(segment.clone(), resizer, tempOut_metadata);
        metadata = tempOut_metadata.argValue;
        if (r != Result.Success) {
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
            if (r != Result.Success) {
                return r;
            }

            if (body.IsEmpty) {
                break;
            }

            tangible.OutObject<Memory<Byte>> tempOut_metadata2 = new tangible.OutObject<Memory<Byte>>();
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: r = RecordIOStream.FormatRow(body, resizer, out metadata);
            r = RecordIOStream.FormatRow(body, resizer, tempOut_metadata2);
            metadata = tempOut_metadata2.argValue;
            if (r != Result.Success) {
                return r;
            }

            // Metadata and Body memory blocks should not overlap since they are both in
            // play at the same time. If they do this usually means that the same resizer
            // was incorrectly used for both. Check the resizer parameter passed to
            // WriteRecordIOAsync for metadata.
            Contract.Assert(!metadata.Span.Overlaps(body.Span));

            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(metadata);
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(body);
        }

        return Result.Success;
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
    private static Result FormatRow(ReadOnlyMemory<Byte> body, MemorySpanResizer<Byte> resizer, tangible.OutObject<Memory<Byte>> block) {
        RowBuffer row;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempOut_row = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>();
        Result r = RecordIOFormatter.FormatRecord(body, tempOut_row, resizer);
        row = tempOut_row.argValue;
        if (r != Result.Success) {
            block.argValue = null;
            return r;
        }

        block.argValue = resizer.getMemory().Slice(0, row.Length);
        return Result.Success;
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
                                        tangible.OutObject<Memory<Byte>> block) {
        RowBuffer row;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempOut_row =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>();
        Result r = RecordIOFormatter.FormatSegment(segment.clone(), tempOut_row, resizer);
        row = tempOut_row.argValue;
        if (r != Result.Success) {
            block.argValue = null;
            return r;
        }

        block.argValue = resizer.getMemory().Slice(0, row.Length);
        return Result.Success;
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
        Result invoke(long index, tangible.OutObject<ReadOnlyMemory<Byte>> buffer);
    }
}