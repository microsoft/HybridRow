//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.recordio;

import azure.data.cosmos.serialization.hybridrow.HybridRowHeader;
import azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.io.RowReader;
import azure.data.cosmos.serialization.hybridrow.layouts.SystemSchema;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public struct RecordIOParser
public final class RecordIOParser {
    private Record record = new Record();
    private Segment segment = new Segment();
    private State state = State.values()[0];

    /**
     * True if a valid segment has been parsed.
     */
    public boolean getHaveSegment() {
        return this.state.getValue() >= State.NeedHeader.getValue();
    }

    /**
     * If a valid segment has been parsed then current active segment, otherwise undefined.
     */
    public Segment getSegment() {
        Contract.Requires(this.getHaveSegment());
        return this.segment.clone();
    }

    /**
     * Processes one buffers worth of data possibly advancing the parser state.
     *
     * @param buffer   The buffer to consume.
     * @param type     Indicates the type of Hybrid Row produced in <paramref name="record" />.
     * @param record   If non-empty, then the body of the next record in the sequence.
     * @param need     The smallest number of bytes needed to advanced the parser state further. It is
     *                 recommended that Process not be called again until at least this number of bytes are available.
     * @param consumed The number of bytes consumed from the input buffer. This number may be less
     *                 than the total buffer size if the parser moved to a new state.
     * @return <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result.Success" /> if no error
     * has occurred, otherwise a valid
     * <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result" /> of the last error encountered
     * during parsing.
     * <p>
     * >
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result Process(Memory<byte> buffer, out ProductionType type, out Memory<byte> record, out
    // int need, out int consumed)
    public Result Process(Memory<Byte> buffer, tangible.OutObject<ProductionType> type,
                          tangible.OutObject<Memory<Byte>> record, tangible.OutObject<Integer> need,
                          tangible.OutObject<Integer> consumed) {
        Result r = Result.Failure;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Memory<byte> b = buffer;
        Memory<Byte> b = buffer;
        type.argValue = ProductionType.None;
        record.argValue = null;
        switch (this.state) {
            case Start:
                this.state = State.NeedSegmentLength;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
            case NeedSegmentLength: {
                int minimalSegmentRowSize = HybridRowHeader.Size + RecordIOFormatter.SegmentLayout.getSize();
                if (b.Length < minimalSegmentRowSize) {
                    need.argValue = minimalSegmentRowSize;
                    consumed.argValue = buffer.Length - b.Length;
                    return Result.InsufficientBuffer;
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Span<byte> span = b.Span.Slice(0, minimalSegmentRowSize);
                Span<Byte> span = b.Span.Slice(0, minimalSegmentRowSize);
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowReader reader = new RowReader(tempRef_row);
                row = tempRef_row.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Segment> tempOut_segment =
                    new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Segment>();
                r = SegmentSerializer.Read(tempRef_reader, tempOut_segment);
                this.segment = tempOut_segment.argValue;
                reader = tempRef_reader.argValue;
                if (r != Result.Success) {
                    break;
                }

                this.state = State.NeedSegment;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
				goto case State.NeedSegment
            }

            case NeedSegment: {
                if (b.Length < this.segment.Length) {
                    need.argValue = this.segment.Length;
                    consumed.argValue = buffer.Length - b.Length;
                    return Result.InsufficientBuffer;
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Span<byte> span = b.Span.Slice(0, this.segment.Length);
                Span<Byte> span = b.Span.Slice(0, this.segment.Length);
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowReader reader = new RowReader(tempRef_row2);
                row = tempRef_row2.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Segment> tempOut_segment2
                    = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Segment>();
                r = SegmentSerializer.Read(tempRef_reader2, tempOut_segment2);
                this.segment = tempOut_segment2.argValue;
                reader = tempRef_reader2.argValue;
                if (r != Result.Success) {
                    break;
                }

                record.argValue = b.Slice(0, span.Length);
                b = b.Slice(span.Length);
                need.argValue = 0;
                this.state = State.NeedHeader;
                consumed.argValue = buffer.Length - b.Length;
                type.argValue = ProductionType.Segment;
                return Result.Success;
            }

            case NeedHeader: {
                if (b.Length < HybridRowHeader.Size) {
                    need.argValue = HybridRowHeader.Size;
                    consumed.argValue = buffer.Length - b.Length;
                    return Result.InsufficientBuffer;
                }

                HybridRowHeader header;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                MemoryMarshal.TryRead(b.Span, out header);
                if (header.Version != HybridRowVersion.V1) {
                    r = Result.InvalidRow;
                    break;
                }

                if (azure.data.cosmos.serialization.hybridrow.SchemaId.opEquals(header.SchemaId,
                    SystemSchema.SegmentSchemaId)) {
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case State.NeedSegment
                }

                if (azure.data.cosmos.serialization.hybridrow.SchemaId.opEquals(header.SchemaId,
                    SystemSchema.RecordSchemaId)) {
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
					goto case State.NeedRecord
                }

                r = Result.InvalidRow;
                break;
            }

            case NeedRecord: {
                int minimalRecordRowSize = HybridRowHeader.Size + RecordIOFormatter.RecordLayout.getSize();
                if (b.Length < minimalRecordRowSize) {
                    need.argValue = minimalRecordRowSize;
                    consumed.argValue = buffer.Length - b.Length;
                    return Result.InsufficientBuffer;
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: Span<byte> span = b.Span.Slice(0, minimalRecordRowSize);
                Span<Byte> span = b.Span.Slice(0, minimalRecordRowSize);
                RowBuffer row = new RowBuffer(span, HybridRowVersion.V1, SystemSchema.LayoutResolver);
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_row3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(row);
                RowReader reader = new RowReader(tempRef_row3);
                row = tempRef_row3.argValue;
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader> tempRef_reader3 = new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.io.RowReader>(reader);
                tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Record> tempOut_record = new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.recordio.Record>();
                r = RecordSerializer.Read(tempRef_reader3, tempOut_record);
                this.record = tempOut_record.argValue;
                reader = tempRef_reader3.argValue;
                if (r != Result.Success) {
                    break;
                }

                b = b.Slice(span.Length);
                this.state = State.NeedRow;
                // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
				goto case State.NeedRow
            }

            case NeedRow: {
                if (b.Length < this.record.Length) {
                    need.argValue = this.record.Length;
                    consumed.argValue = buffer.Length - b.Length;
                    return Result.InsufficientBuffer;
                }

                record.argValue = b.Slice(0, this.record.Length);

                // Validate that the record has not been corrupted.
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: uint crc32 = Crc32.Update(0, record.Span);
                int crc32 = Crc32.Update(0, record.argValue.Span);
                if (crc32 != this.record.Crc32) {
                    r = Result.InvalidRow;
                    break;
                }

                b = b.Slice(this.record.Length);
                need.argValue = 0;
                this.state = State.NeedHeader;
                consumed.argValue = buffer.Length - b.Length;
                type.argValue = ProductionType.Record;
                return Result.Success;
            }
        }

        this.state = State.Error;
        need.argValue = 0;
        consumed.argValue = buffer.Length - b.Length;
        return r;
    }

    public RecordIOParser clone() {
        RecordIOParser varCopy = new RecordIOParser();

        varCopy.state = this.state;
        varCopy.segment = this.segment.clone();
        varCopy.record = this.record.clone();

        return varCopy;
    }

    /**
     * Describes the type of Hybrid Rows produced by the parser.
     */
    public enum ProductionType {
        /**
         * No hybrid row was produced. The parser needs more data.
         */
        None(0),

        /**
         * A new segment row was produced.
         */
        Segment(1),

        /**
         * A record in the current segment was produced.
         */
        Record(2);

        public static final int SIZE = java.lang.Integer.SIZE;
        private static java.util.HashMap<Integer, ProductionType> mappings;
        private int intValue;

        ProductionType(int value) {
            intValue = value;
            getMappings().put(value, this);
        }

        public int getValue() {
            return intValue;
        }

        public static ProductionType forValue(int value) {
            return getMappings().get(value);
        }

        private static java.util.HashMap<Integer, ProductionType> getMappings() {
            if (mappings == null) {
                synchronized (ProductionType.class) {
                    if (mappings == null) {
                        mappings = new java.util.HashMap<Integer, ProductionType>();
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
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private enum State : byte
    private enum State {
        Start((byte)0), // Start: no buffers have yet been provided to the parser.
        Error(((byte)0) + 1), // Unrecoverable parse error encountered.
        NeedSegmentLength(((byte)0) + 2), // Parsing segment header length
        NeedSegment(((byte)0) + 3), // Parsing segment header
        NeedHeader(((byte)0) + 4), // Parsing HybridRow header
        NeedRecord(((byte)0) + 5), // Parsing record header
        NeedRow(((byte)0) + 6); // Parsing row body

        public static final int SIZE = java.lang.Byte.SIZE;
        private static java.util.HashMap<Byte, State> mappings;
        private byte byteValue;

        State(byte value) {
            byteValue = value;
            getMappings().put(value, this);
        }

        public byte getValue() {
            return byteValue;
        }

        public static State forValue(byte value) {
            return getMappings().get(value);
        }

        private static java.util.HashMap<Byte, State> getMappings() {
            if (mappings == null) {
                synchronized (State.class) {
                    if (mappings == null) {
                        mappings = new java.util.HashMap<Byte, State>();
                    }
                }
            }
            return mappings;
        }
    }
}