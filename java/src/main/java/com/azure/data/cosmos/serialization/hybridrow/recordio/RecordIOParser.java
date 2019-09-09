// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.recordio;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowHeader;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.data.cosmos.serialization.hybridrow.layouts.SystemSchema;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkState;

public final class RecordIOParser {

    private Record record;
    private Segment segment;
    private State state = State.values()[0];

    /**
     * Processes one buffers worth of data possibly advancing the parser state
     *
     * @param buffer1   The buffer to consume
     * @param type     Indicates the type of Hybrid Row produced in {@code record}
     * @param record   If non-empty, then the body of the next record in the sequence
     * @param need     The smallest number of bytes needed to advanced the parser state further
     *                 <p>
     *                 It is recommended that this method not be called again until at least this number of bytes are
     *                 available.
     * @param consumed The number of bytes consumed from the input buffer
     *                 <p>
     *                 This number may be less than the total buffer size if the parser moved to a new state.
     * @return {@link Result#SUCCESS} if no error has occurred;, otherwise the {@link Result} of the last error
     * encountered during parsing.
     * <p>
     * >
     */
    @Nonnull
    public Result process(
        @Nonnull final ByteBuf buffer,
        @Nonnull final Out<ProductionType> type,
        @Nonnull final Out<ByteBuf> record,
        @Nonnull final Out<Integer> need,
        @Nonnull final Out<Integer> consumed) {

        Result result = Result.FAILURE;
        type.set(ProductionType.NONE);
        record.set(null);

        final int start = buffer.readerIndex();

        switch (this.state) {

            case STATE:
                this.state = State.NEED_SEGMENT_LENGTH;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                //  goto case State.NeedSegmentLength;

            case NEED_SEGMENT_LENGTH: {

                final int minimalSegmentRowSize = HybridRowHeader.BYTES + RecordIOFormatter.SEGMENT_LAYOUT.size();

                if (buffer.readableBytes() < minimalSegmentRowSize) {
                    consumed.set(buffer.readerIndex() - start);
                    need.set(minimalSegmentRowSize);
                    return Result.INSUFFICIENT_BUFFER;
                }

                ByteBuf span = buffer.slice(buffer.readerIndex(), minimalSegmentRowSize);
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.layoutResolver);
                Reference<RowBuffer> tempReference_row =
                    new Reference<RowBuffer>(row);
                RowReader reader = new RowReader(tempReference_row);
                row = tempReference_row.get();
                Reference<RowReader> tempReference_reader =
                    new Reference<RowReader>(reader);
                Out<Segment> tempOut_segment =
                    new Out<Segment>();
                result = SegmentSerializer.Read(tempReference_reader, tempOut_segment);
                this.segment = tempOut_segment.get();
                reader = tempReference_reader.get();
                if (result != Result.SUCCESS) {
                    break;
                }

                this.state = State.NEED_SEGMENT;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
				goto case State.NEED_SEGMENT
            }

            case NEED_SEGMENT: {
                if (buffer.Length < this.segment.length()) {
                    need.set(this.segment.length());
                    consumed.set(buffer.Length - buffer.Length);
                    return Result.INSUFFICIENT_BUFFER;
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Span<byte> span = b.Span.Slice(0, this.segment.Length);
                Span<Byte> span = buffer.Span.Slice(0, this.segment.length());
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.layoutResolver);
                Reference<RowBuffer> tempReference_row2 =
                    new Reference<RowBuffer>(row);
                RowReader reader = new RowReader(tempReference_row2);
                row = tempReference_row2.get();
                Reference<RowReader> tempReference_reader2 =
                    new Reference<RowReader>(reader);
                Out<Segment> tempOut_segment2
                    = new Out<Segment>();
                result = SegmentSerializer.Read(tempReference_reader2, tempOut_segment2);
                this.segment = tempOut_segment2.get();
                reader = tempReference_reader2.get();
                if (result != Result.SUCCESS) {
                    break;
                }

                record.set(buffer.Slice(0, span.Length));
                buffer = buffer.Slice(span.Length);
                need.set(0);
                this.state = State.NEED_HEADER;
                consumed.set(buffer.Length - buffer.Length);
                type.set(ProductionType.SEGMENT);
                return Result.SUCCESS;
            }

            case NEED_HEADER: {
                if (buffer.Length < HybridRowHeader.BYTES) {
                    need.set(HybridRowHeader.BYTES);
                    consumed.set(buffer.Length - buffer.Length);
                    return Result.INSUFFICIENT_BUFFER;
                }

                HybridRowHeader header;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                MemoryMarshal.TryRead(buffer.Span, out header);
                if (header.Version != HybridRowVersion.V1) {
                    result = Result.INVALID_ROW;
                    break;
                }

                if (SchemaId.opEquals(header.SchemaId,
                    SystemSchema.SEGMENT_SCHEMA_ID)) {
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case State.NEED_SEGMENT
                }

                if (SchemaId.opEquals(header.SchemaId,
                    SystemSchema.RECORD_SCHEMA_ID)) {
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case State.NEED_RECORD
                }

                result = Result.INVALID_ROW;
                break;
            }

            case NEED_RECORD: {
                int minimalRecordRowSize = HybridRowHeader.BYTES + RecordIOFormatter.RECORD_LAYOUT.size();
                if (buffer.Length < minimalRecordRowSize) {
                    need.set(minimalRecordRowSize);
                    consumed.set(buffer.Length - buffer.Length);
                    return Result.INSUFFICIENT_BUFFER;
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Span<byte> span = b.Span.Slice(0, minimalRecordRowSize);
                Span<Byte> span = buffer.Span.Slice(0, minimalRecordRowSize);
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.layoutResolver);
                Reference<RowBuffer> tempReference_row3 =
                    new Reference<RowBuffer>(row);
                RowReader reader = new RowReader(tempReference_row3);
                row = tempReference_row3.get();
                Reference<RowReader> tempReference_reader3 = new Reference<RowReader>(reader);
                Out<Record> tempOut_record = new Out<Record>();
                result = RecordSerializer.read(tempReference_reader3, tempOut_record);
                this.record = tempOut_record.get();
                reader = tempReference_reader3.get();
                if (result != Result.SUCCESS) {
                    break;
                }

                buffer = buffer.Slice(span.Length);
                this.state = State.NEED_ROW;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
				goto case State.NEED_ROW
            }

            case NEED_ROW: {
                if (buffer.Length < this.record.length()) {
                    need.set(this.record.length());
                    consumed.set(buffer.Length - buffer.Length);
                    return Result.INSUFFICIENT_BUFFER;
                }

                record.set(buffer.Slice(0, this.record.length()));

                // Validate that the record has not been corrupted.
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: uint crc32 = Crc32.Update(0, record.Span);
                int crc32 = Crc32.Update(0, record.get().Span);
                if (crc32 != this.record.crc32()) {
                    result = Result.INVALID_ROW;
                    break;
                }

                buffer = buffer.Slice(this.record.length());
                need.set(0);
                this.state = State.NEED_HEADER;
                consumed.set(buffer.Length - buffer.Length);
                type.set(ProductionType.RECORD);
                return Result.SUCCESS;
            }
        }

        this.state = State.ERROR;
        need.set(0);
        consumed.set(buffer.Length - buffer.Length);
        return result;
    }

    /**
     * True if a valid segment has been parsed.
     */
    public boolean haveSegment() {
        return this.state.value() >= State.NEED_HEADER.value();
    }

    /**
     * If a valid segment has been parsed then current active segment, otherwise undefined.
     */
    public Segment segment() {
        checkState(this.haveSegment());
        return this.segment;
    }

    /**
     * Describes the type of Hybrid Rows produced by the parser.
     */
    public enum ProductionType {
        /**
         * No hybrid row was produced. The parser needs more data.
         */
        NONE(0),

        /**
         * A new segment row was produced.
         */
        SEGMENT(1),

        /**
         * A record in the current segment was produced.
         */
        RECORD(2);

        public static final int BYTES = Integer.BYTES;

        private static Int2ObjectMap<ProductionType> mappings;
        private int value;

        ProductionType(int value) {
            this.value = value;
            mappings().put(value, this);
        }

        public int value() {
            return this.value;
        }

        public static ProductionType from(int value) {
            return mappings().get(value);
        }

        private static Int2ObjectMap<ProductionType> mappings() {
            if (mappings == null) {
                synchronized (ProductionType.class) {
                    if (mappings == null) {
                        mappings = new Int2ObjectOpenHashMap<>();
                    }
                }
            }
            return mappings;
        }
    }

    /**
     * The states for the internal state machine.
     * Note: numerical ordering of these states matters.
     */
    private enum State {
        STATE(
            (byte) 0, "Start: no buffers have yet been provided to the parser"),
        ERROR(
            (byte) 1, "Unrecoverable parse error encountered"),
        NEED_SEGMENT_LENGTH(
            (byte) 2, "Parsing segment header length"),
        NEED_SEGMENT(
            (byte) 3, "Parsing segment header"),
        NEED_HEADER(
            (byte) 4, "Parsing HybridRow header"),
        NEED_RECORD(
            (byte) 5, "Parsing record header"),
        NEED_ROW(
            (byte) 6, "Parsing row body");

        public static final int BYTES = Byte.SIZE;

        private static Byte2ObjectMap<State> mappings;
        private final String description;
        private final byte value;

        State(byte value, String description) {
            this.description = description;
            this.value = value;
            mappings().put(value, this);
        }

        public String description() {
            return this.description;
        }

        public byte value() {
            return this.value;
        }

        public static State from(byte value) {
            return mappings().get(value);
        }

        private static Byte2ObjectMap<State> mappings() {
            if (mappings == null) {
                synchronized (State.class) {
                    if (mappings == null) {
                        mappings = new Byte2ObjectOpenHashMap<>();
                    }
                }
            }
            return mappings;
        }
    }
}