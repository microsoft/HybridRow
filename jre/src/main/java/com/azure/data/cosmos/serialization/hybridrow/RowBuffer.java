//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBit;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedMap;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType.MongoDbObjectId;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public ref struct RowBuffer
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class RowBuffer {
    /**
     * A sequence of bytes managed by this {@link RowBuffer}.
     * <p>
     * A Hybrid Row begins in the 0-th byte of the {@link RowBuffer}.  Remaining byte
     * sequence is defined by the Hybrid Row grammar.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private Span<byte> buffer;
    private Span<Byte> buffer;
    /**
     * The length of row in bytes.
     */
    private int length;
    /**
     * Resizer for growing the memory buffer.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private readonly ISpanResizer<byte> resizer;
    private ISpanResizer<Byte> resizer;
    /**
     * The resolver for UDTs.
     */
    private LayoutResolver resolver;

    /**
     * Initializes a new instance of the {@link RowBuffer} struct.
     *
     * @param capacity Initial buffer capacity.
     * @param resizer  Optional memory resizer.
     */

    public RowBuffer(int capacity) {
        this(capacity, null);
    }

    public RowBuffer() {
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public RowBuffer(int capacity, ISpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public RowBuffer(int capacity, ISpanResizer<Byte> resizer) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.resizer = resizer != null ? resizer : DefaultSpanResizer<byte>.Default;
        this.resizer = resizer != null ? resizer : DefaultSpanResizer < Byte >.Default;
        this.buffer = this.resizer.Resize(capacity);
        this.length = 0;
        this.resolver = null;
    }

    /**
     * Initializes a new instance of the {@link RowBuffer} struct from an existing buffer.
     *
     * @param buffer   The buffer.  The row takes ownership of the buffer and the caller should not
     *                 maintain a pointer or mutate the buffer after this call returns.
     * @param version  The version of the Hybrid Row format to used to encoding the buffer.
     * @param resolver The resolver for UDTs.
     * @param resizer  Optional memory resizer.
     */

    public RowBuffer(Span<Byte> buffer, HybridRowVersion version, LayoutResolver resolver) {
        this(buffer, version, resolver, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public RowBuffer(Span<byte> buffer, HybridRowVersion version, LayoutResolver resolver,
	// ISpanResizer<byte> resizer = default)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public RowBuffer(Span<Byte> buffer, HybridRowVersion version, LayoutResolver resolver, ISpanResizer<Byte> resizer) {
        checkArgument(buffer.Length >= HybridRowHeader.Size);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.resizer = resizer != null ? resizer : DefaultSpanResizer<byte>.Default;
        this.resizer = resizer != null ? resizer : DefaultSpanResizer < Byte >.Default;
        this.length = buffer.Length;
        this.buffer = buffer;
        this.resolver = resolver;

        HybridRowHeader header = this.ReadHeader(0).clone();
        checkState(header.getVersion() == version);
        Layout layout = resolver.Resolve(header.getSchemaId().clone());
        checkState(SchemaId.opEquals(header.getSchemaId().clone(), layout.schemaId().clone()));
        checkState(HybridRowHeader.Size + layout.size() <= this.length);
    }

    /**
     * The root header for the row.
     */
    public HybridRowHeader getHeader() {
        return this.ReadHeader(0).clone();
    }

    /**
     * The length of row in bytes.
     */
    public int getLength() {
        return this.length;
    }

    /**
     * The resolver for UDTs.
     */
    public LayoutResolver getResolver() {
        return this.resolver;
    }

    /**
     * Compute the byte offset from the beginning of the row for a given variable column's value.
     *
     * @param layout      The (optional) layout of the current scope.
     * @param scopeOffset The 0-based offset to the beginning of the scope's value.
     * @param varIndex    The 0-based index of the variable column within the variable segment.
     * @return The byte offset from the beginning of the row where the variable column's value should be
     * located.
     */
    public int ComputeVariableValueOffset(Layout layout, int scopeOffset, int varIndex) {
        if (layout == null) {
            return scopeOffset;
        }

        int index = layout.numFixed() + varIndex;
        ReadOnlySpan<LayoutColumn> columns = layout.columns();
        checkState(index <= columns.Length);
        int offset = scopeOffset + layout.size();
        for (int i = layout.numFixed(); i < index; i++) {
            LayoutColumn col = columns[i];
            if (this.ReadBit(scopeOffset, col.getNullBit().clone())) {
                int lengthSizeInBytes;
                Out<Integer> tempOut_lengthSizeInBytes = new Out<Integer>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ulong valueSizeInBytes = this.Read7BitEncodedUInt(offset, out int lengthSizeInBytes);
                long valueSizeInBytes = this.Read7BitEncodedUInt(offset, tempOut_lengthSizeInBytes);
                lengthSizeInBytes = tempOut_lengthSizeInBytes.get();
                if (col.getType().getIsVarint()) {
                    offset += lengthSizeInBytes;
                } else {
                    offset += (int)valueSizeInBytes + lengthSizeInBytes;
                }
            }
        }

        return offset;
    }

    /**
     * Compute the number of bytes necessary to store the unsigned integer using the varuint
     * encoding.
     *
     * @param value The value to be encoded.
     * @return The number of bytes needed to store the varuint encoding of {@link value}.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal static int Count7BitEncodedUInt(ulong value)
    public static int Count7BitEncodedUInt(long value) {
        // Count the number of bytes needed to write out an int 7 bits at a time.
        int i = 0;
        while (value >= 0x80L) {
            i++;
            //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
			// operator since the left operand was originally of an unsigned type, but you should confirm this
			// replacement:
            value >>>= 7;
        }

        i++;
        return i;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void DecrementUInt32(int offset, uint decrement)
    public void DecrementUInt32(int offset, int decrement) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] -= decrement;
        MemoryMarshal.<Byte, Integer>Cast(this.buffer.Slice(offset))[0] -= decrement;
    }

    /**
     * Delete the sparse field at the indicated path.
     *
     * @param edit The field to delete.
     */
    public void DeleteSparse(Reference<RowCursor> edit) {
        // If the field doesn't exist, then nothing to do.
        if (!edit.get().exists) {
            return;
        }

        int numBytes = 0;
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        int _;
        Out<Integer> tempOut__2 = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, edit.get().cellType, edit.get().cellTypeArgs.clone(), numBytes,
			RowOptions.Delete, tempOut__, tempOut__2, tempOut_shift);
        shift = tempOut_shift.get();
        _ = tempOut__2.get();
        _ = tempOut__.get();
        this.length += shift;
    }

    public void DeleteVariable(int offset, boolean isVarint) {
        int spaceAvailable;
        Out<Integer> tempOut_spaceAvailable = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong existingValueBytes = this.Read7BitEncodedUInt(offset, out int spaceAvailable);
        long existingValueBytes = this.Read7BitEncodedUInt(offset, tempOut_spaceAvailable);
        spaceAvailable = tempOut_spaceAvailable.get();
        if (!isVarint) {
            spaceAvailable += (int)existingValueBytes; // "size" already in spaceAvailable
        }

        this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset));
        this.length -= spaceAvailable;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void IncrementUInt32(int offset, uint increment)
    public void IncrementUInt32(int offset, int increment) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] += increment;
        MemoryMarshal.<Byte, Integer>Cast(this.buffer.Slice(offset))[0] += increment;
    }

    /**
     * Initializes a row to the minimal size for the given layout.
     *
     * @param version  The version of the Hybrid Row format to use for encoding this row.
     * @param layout   The layout that describes the column layout of the row.
     * @param resolver The resolver for UDTs.
     *                 <p>
     *                 The row is initialized to default row for the given layout.  All fixed columns have their
     *                 default values.  All variable columns are null.  No sparse columns are present. The row is
     *                 valid.
     */
    public void InitLayout(HybridRowVersion version, Layout layout, LayoutResolver resolver) {
        checkArgument(layout != null);
        this.resolver = resolver;

        // Ensure sufficient space for fixed schema fields.
        this.Ensure(HybridRowHeader.Size + layout.size());
        this.length = HybridRowHeader.Size + layout.size();

        // Clear all presence bits.
        this.buffer.Slice(HybridRowHeader.Size, layout.size()).Fill(0);

        // Set the header.
        this.WriteHeader(0, new HybridRowHeader(version, layout.schemaId().clone()));
    }

    /**
     * Compute the byte offsets from the beginning of the row for a given sparse field insertion
     * into a set/map.
     *
     * @param scope   The sparse scope to insert into.
     * @param srcEdit The field to move into the set/map.
     * @return The prepared edit context.
     */
    public RowCursor PrepareSparseMove(Reference<RowCursor> scope, Reference<RowCursor> srcEdit) {
        checkArgument(scope.get().scopeType.IsUniqueScope);

        checkArgument(scope.get().index == 0);
        RowCursor dstEdit;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
		// cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.get().Clone(out dstEdit);

        dstEdit.metaOffset = scope.get().valueOffset;
        int srcSize = this.SparseComputeSize(srcEdit);
        int srcBytes = srcSize - (srcEdit.get().valueOffset - srcEdit.get().metaOffset);
        while (dstEdit.index < dstEdit.count) {
            Reference<RowCursor> tempReference_dstEdit =
				new Reference<RowCursor>(dstEdit);
            this.ReadSparseMetadata(tempReference_dstEdit);
            dstEdit = tempReference_dstEdit.get();
            Contract.Assert(dstEdit.pathOffset ==
            default)

				int elmSize = -1; // defer calculating the full size until needed.
                int cmp;
                if (scope.get().scopeType instanceof LayoutTypedMap) {
                    cmp = this.CompareKeyValueFieldValue(srcEdit.get().clone(), dstEdit);
                } else {
                    Reference<RowCursor> tempReference_dstEdit2 =
						new Reference<RowCursor>(dstEdit);
                    elmSize = this.SparseComputeSize(tempReference_dstEdit2);
                    dstEdit = tempReference_dstEdit2.get();
                    int elmBytes = elmSize - (dstEdit.valueOffset - dstEdit.metaOffset);
                    cmp = this.CompareFieldValue(srcEdit.get().clone(), srcBytes, dstEdit, elmBytes);
                }

                if (cmp <= 0) {
                    dstEdit.exists = cmp == 0;
                    return dstEdit;
                }

                Reference<RowCursor> tempReference_dstEdit3 =
					new Reference<RowCursor>(dstEdit);
                elmSize = (elmSize == -1) ? this.SparseComputeSize(tempReference_dstEdit3) : elmSize;
                dstEdit = tempReference_dstEdit3.get();
                dstEdit.index++;
                dstEdit.metaOffset += elmSize;
        }

        dstEdit.exists = false;
        dstEdit.cellType = LayoutType.EndScope;
        dstEdit.valueOffset = dstEdit.metaOffset;
        return dstEdit;
    }

    public long Read7BitEncodedInt(int offset, Out<Integer> lenInBytes) {
        return RowBuffer.RotateSignToMsb(this.Read7BitEncodedUInt(offset, lenInBytes));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong Read7BitEncodedUInt(int offset, out int lenInBytes)
    public long Read7BitEncodedUInt(int offset, Out<Integer> lenInBytes) {
        // Read out an unsigned long 7 bits at a time.  The high bit of the byte,
        // when set, indicates there are more bytes.
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong b = this.buffer[offset];
        long b = this.buffer[offset];
        if (b < 0x80L) {
            lenInBytes.setAndGet(1);
            return b;
        }

        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong retval = b & 0x7F;
        long retval = b & 0x7F;
        int shift = 7;
        do {
            checkState(shift < 10 * 7);
            b = this.buffer[++offset];
            retval |= (b & 0x7F) << shift;
            shift += 7;
        } while (b >= 0x80L);

        lenInBytes.setAndGet(shift / 7);
        return retval;
    }

    public boolean ReadBit(int offset, LayoutBit bit) {
        if (bit.getIsInvalid()) {
            return true;
        }

        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: return (this.buffer[bit.GetOffset(offset)] & unchecked((byte)(1 << bit.GetBit()))) != 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        return (this.buffer[bit.GetOffset(offset)] & (byte)(1 << bit.GetBit())) != 0;
    }

    public LocalDateTime ReadDateTime(int offset) {
        return MemoryMarshal.<LocalDateTime>Read(this.buffer.Slice(offset));
    }

    public BigDecimal ReadDecimal(int offset) {
        return MemoryMarshal.<BigDecimal>Read(this.buffer.Slice(offset));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ReadOnlySpan<byte> ReadFixedBinary(int offset, int len)
    public ReadOnlySpan<Byte> ReadFixedBinary(int offset, int len) {
        return this.buffer.Slice(offset, len);
    }

    public Utf8Span ReadFixedString(int offset, int len) {
        return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(offset, len));
    }

    public Float128 ReadFloat128(int offset) {
        return MemoryMarshal.<Float128>Read(this.buffer.Slice(offset));
    }

    public float ReadFloat32(int offset) {
        return MemoryMarshal.<Float>Read(this.buffer.Slice(offset));
    }

    public double ReadFloat64(int offset) {
        return MemoryMarshal.<Double>Read(this.buffer.Slice(offset));
    }

    /**
     * Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
     * with the associated layout and rowVersion.
     *
     * @return true if the serialization succeeded. false if the input stream was corrupted.
     */
    public boolean ReadFrom(InputStream inputStream, int bytesCount, HybridRowVersion rowVersion,
                            LayoutResolver resolver) {
        checkArgument(inputStream != null);
        checkState(bytesCount >= HybridRowHeader.Size);

        this.Reset();
        this.resolver = resolver;
        this.Ensure(bytesCount);
        checkState(this.buffer.Length >= bytesCount);
        this.length = bytesCount;
		//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Span<byte> active = this.buffer.Slice(0, bytesCount);
        Span<Byte> active = this.buffer.Slice(0, bytesCount);
        int bytesRead;
        do {
            bytesRead = inputStream.Read(active);
            active = active.Slice(bytesRead);
        } while (bytesRead != 0);

        if (active.Length != 0) {
            return false;
        }

        return this.InitReadFrom(rowVersion);
    }

    /**
     * Reads in the contents of the RowBuffer from an existing block of memory and initializes
     * the row buffer with the associated layout and rowVersion.
     *
     * @return true if the serialization succeeded. false if the input stream was corrupted.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public bool ReadFrom(ReadOnlySpan<byte> input, HybridRowVersion rowVersion, LayoutResolver
	// resolver)
    public boolean ReadFrom(ReadOnlySpan<Byte> input, HybridRowVersion rowVersion, LayoutResolver resolver) {
        int bytesCount = input.Length;
        checkState(bytesCount >= HybridRowHeader.Size);

        this.Reset();
        this.resolver = resolver;
        this.Ensure(bytesCount);
        checkState(this.buffer.Length >= bytesCount);
        input.CopyTo(this.buffer);
        this.length = bytesCount;
        return this.InitReadFrom(rowVersion);
    }

    public UUID ReadGuid(int offset) {
        return MemoryMarshal.<UUID>Read(this.buffer.Slice(offset));
    }

    public HybridRowHeader ReadHeader(int offset) {
        return MemoryMarshal.<HybridRowHeader>Read(this.buffer.Slice(offset));
    }

    public short ReadInt16(int offset) {
        return MemoryMarshal.<Short>Read(this.buffer.Slice(offset));
    }

    public int ReadInt32(int offset) {
        return MemoryMarshal.<Integer>Read(this.buffer.Slice(offset));
    }

    public long ReadInt64(int offset) {
        return MemoryMarshal.<Long>Read(this.buffer.Slice(offset));
    }

    public byte ReadInt8(int offset) {
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: return unchecked((sbyte)this.buffer[offset]);
        return (byte)this.buffer[offset];
    }

    public MongoDbObjectId ReadMongoDbObjectId(int offset) {
        return MemoryMarshal.<MongoDbObjectId>Read(this.buffer.Slice(offset));
    }

    public SchemaId ReadSchemaId(int offset) {
        return new SchemaId(this.ReadInt32(offset));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ReadOnlySpan<byte> ReadSparseBinary(ref RowCursor edit)
    public ReadOnlySpan<Byte> ReadSparseBinary(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Binary);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ReadOnlySpan<byte> span = this.ReadBinary(edit.valueOffset, out int sizeLenInBytes);
        ReadOnlySpan<Byte> span = this.ReadBinary(edit.get().valueOffset, tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset + sizeLenInBytes + span.Length;
        return span;
    }

    public boolean ReadSparseBool(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Boolean);
        edit.get().endOffset = edit.get().valueOffset;
        return edit.get().cellType == LayoutType.Boolean;
    }

    public LocalDateTime ReadSparseDateTime(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.DateTime);
        edit.get().endOffset = edit.get().valueOffset + 8;
        return this.ReadDateTime(edit.get().valueOffset);
    }

    public BigDecimal ReadSparseDecimal(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Decimal);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        edit.get().endOffset = edit.get().valueOffset + sizeof(BigDecimal);
        return this.ReadDecimal(edit.get().valueOffset);
    }

    public Float128 ReadSparseFloat128(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Float128);
        edit.get().endOffset = edit.get().valueOffset + Float128.Size;
        return this.ReadFloat128(edit.get().valueOffset).clone();
    }

    public float ReadSparseFloat32(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Float32);
        edit.get().endOffset = edit.get().valueOffset + (Float.SIZE / Byte.SIZE);
        return this.ReadFloat32(edit.get().valueOffset);
    }

    public double ReadSparseFloat64(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Float64);
        edit.get().endOffset = edit.get().valueOffset + (Double.SIZE / Byte.SIZE);
        return this.ReadFloat64(edit.get().valueOffset);
    }

    public UUID ReadSparseGuid(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Guid);
        edit.get().endOffset = edit.get().valueOffset + 16;
        return this.ReadGuid(edit.get().valueOffset);
    }

    public short ReadSparseInt16(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Int16);
        edit.get().endOffset = edit.get().valueOffset + (Short.SIZE / Byte.SIZE);
        return this.ReadInt16(edit.get().valueOffset);
    }

    public int ReadSparseInt32(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Int32);
        edit.get().endOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE);
        return this.ReadInt32(edit.get().valueOffset);
    }

    public long ReadSparseInt64(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Int64);
        edit.get().endOffset = edit.get().valueOffset + (Long.SIZE / Byte.SIZE);
        return this.ReadInt64(edit.get().valueOffset);
    }

    public byte ReadSparseInt8(Reference<RowCursor> edit) {
        // TODO: Remove calls to ReadSparsePrimitiveTypeCode once moved to V2 read.
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Int8);
        edit.get().endOffset = edit.get().valueOffset + (Byte.SIZE / Byte.SIZE);
        return this.ReadInt8(edit.get().valueOffset);
    }

    public MongoDbObjectId ReadSparseMongoDbObjectId(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, MongoDbObjectId);
        edit.get().endOffset = edit.get().valueOffset + MongoDbObjectId.Size;
        return this.ReadMongoDbObjectId(edit.get().valueOffset).clone();
    }

    public NullValue ReadSparseNull(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Null);
        edit.get().endOffset = edit.get().valueOffset;
        return NullValue.Default;
    }

    public Utf8Span ReadSparsePath(Reference<RowCursor> edit) {
        Utf8String path;
        Out<Utf8String> tempOut_path = new Out<Utf8String>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: if (edit.layout.Tokenizer.TryFindString((ulong)edit.pathToken, out Utf8String path))
        if (edit.get().layout.getTokenizer().TryFindString(edit.get().longValue().pathToken, tempOut_path)) {
            path = tempOut_path.get();
            return path.Span;
        } else {
            path = tempOut_path.get();
        }

        int numBytes = edit.get().pathToken - edit.get().layout.getTokenizer().getCount();
        return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(edit.get().pathOffset, numBytes));
    }

    public int ReadSparsePathLen(Layout layout, int offset, Out<Integer> pathLenInBytes,
								 Out<Integer> pathOffset) {
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        int token = (int)this.Read7BitEncodedUInt(offset, tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        if (token < layout.getTokenizer().getCount()) {
            pathLenInBytes.setAndGet(sizeLenInBytes);
            pathOffset.setAndGet(offset);
            return token;
        }

        int numBytes = token - layout.getTokenizer().getCount();
        pathLenInBytes.setAndGet(numBytes + sizeLenInBytes);
        pathOffset.setAndGet(offset + sizeLenInBytes);
        return token;
    }

    public Utf8Span ReadSparseString(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.Utf8);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        Utf8Span span = this.ReadString(edit.get().valueOffset, tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset + sizeLenInBytes + span.Length;
        return span;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal LayoutType ReadSparseTypeCode(int
	// offset)
    public LayoutType ReadSparseTypeCode(int offset) {
        return LayoutType.FromCode(LayoutCode.forValue(this.ReadUInt8(offset)));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ushort ReadSparseUInt16(ref RowCursor edit)
    public short ReadSparseUInt16(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.UInt16);
        edit.get().endOffset = edit.get().valueOffset + (Short.SIZE / Byte.SIZE);
        return this.ReadUInt16(edit.get().valueOffset);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal uint ReadSparseUInt32(ref RowCursor edit)
    public int ReadSparseUInt32(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.UInt32);
        edit.get().endOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE);
        return this.ReadUInt32(edit.get().valueOffset);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong ReadSparseUInt64(ref RowCursor edit)
    public long ReadSparseUInt64(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.UInt64);
        edit.get().endOffset = edit.get().valueOffset + (Long.SIZE / Byte.SIZE);
        return this.ReadUInt64(edit.get().valueOffset);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal byte ReadSparseUInt8(ref RowCursor edit)
    public byte ReadSparseUInt8(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.UInt8);
        edit.get().endOffset = edit.get().valueOffset + 1;
        return this.ReadUInt8(edit.get().valueOffset);
    }

    public UnixDateTime ReadSparseUnixDateTime(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.UnixDateTime);
        edit.get().endOffset = edit.get().valueOffset + 8;
        return this.ReadUnixDateTime(edit.get().valueOffset).clone();
    }

    public long ReadSparseVarInt(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.VarInt);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        long value = this.Read7BitEncodedInt(edit.get().valueOffset, tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset + sizeLenInBytes;
        return value;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong ReadSparseVarUInt(ref RowCursor edit)
    public long ReadSparseVarUInt(Reference<RowCursor> edit) {
        this.ReadSparsePrimitiveTypeCode(edit, LayoutType.VarUInt);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong value = this.Read7BitEncodedUInt(edit.valueOffset, out int sizeLenInBytes);
        long value = this.Read7BitEncodedUInt(edit.get().valueOffset, tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset + sizeLenInBytes;
        return value;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ushort ReadUInt16(int offset)
    public short ReadUInt16(int offset) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return MemoryMarshal.Read<ushort>(this.buffer.Slice(offset));
        return MemoryMarshal.<Short>Read(this.buffer.Slice(offset));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal uint ReadUInt32(int offset)
    public int ReadUInt32(int offset) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return MemoryMarshal.Read<uint>(this.buffer.Slice(offset));
        return MemoryMarshal.<Integer>Read(this.buffer.Slice(offset));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong ReadUInt64(int offset)
    public long ReadUInt64(int offset) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return MemoryMarshal.Read<ulong>(this.buffer.Slice(offset));
        return MemoryMarshal.<Long>Read(this.buffer.Slice(offset));
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal byte ReadUInt8(int offset)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal byte ReadUInt8(int offset)
    public byte ReadUInt8(int offset) {
        return this.buffer[offset];
    }

    public UnixDateTime ReadUnixDateTime(int offset) {
        return MemoryMarshal.<UnixDateTime>Read(this.buffer.Slice(offset));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ReadOnlySpan<byte> ReadVariableBinary(int offset)
    public ReadOnlySpan<Byte> ReadVariableBinary(int offset) {
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.ReadBinary(offset, out int _);
        ReadOnlySpan<Byte> tempVar = this.ReadBinary(offset, tempOut__);
        _ = tempOut__.get();
        return tempVar;
    }

    public long ReadVariableInt(int offset) {
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        long tempVar = this.Read7BitEncodedInt(offset, tempOut__);
        _ = tempOut__.get();
        return tempVar;
    }

    public Utf8Span ReadVariableString(int offset) {
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        Utf8Span tempVar = this.ReadString(offset, tempOut__);
        _ = tempOut__.get();
        return tempVar;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong ReadVariableUInt(int offset)
    public long ReadVariableUInt(int offset) {
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.Read7BitEncodedUInt(offset, out int _);
        long tempVar = this.Read7BitEncodedUInt(offset, tempOut__);
        _ = tempOut__.get();
        return tempVar;
    }

    /**
     * Clears all content from the row. The row is empty after this method.
     */
    public void Reset() {
        this.length = 0;
        this.resolver = null;
    }

    /**
     * Rotates the sign bit of a two's complement value to the least significant bit.
     *
     * @param value A signed value.
     * @return An unsigned value encoding the same value but with the sign bit in the LSB.
     * <p>
     * Moves the signed bit of a two's complement value to the least significant bit (LSB) by:
     * <list type="number">
     * <item>
     * <description>If negative, take the two's complement.</description>
     * </item> <item>
     * <description>Left shift the value by 1 bit.</description>
     * </item> <item>
     * <description>If negative, set the LSB to 1.</description>
     * </item>
     * </list>
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("StyleCop.CSharp.DocumentationRules",
	// "SA1629:DocumentationTextMustEndWithAPeriod", Justification = "Colon.")] internal static ulong RotateSignToLsb
	// (long value)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [SuppressMessage("StyleCop.CSharp.DocumentationRules",
	// "SA1629:DocumentationTextMustEndWithAPeriod", Justification = "Colon.")] internal static ulong RotateSignToLsb
	// (long value)
    public static long RotateSignToLsb(long value) {
        // Rotate sign to LSB
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            boolean isNegative = value < 0;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ulong uvalue = (ulong)value;
            long uvalue = value;
            uvalue = isNegative ? ((~uvalue + 1) << 1) + 1 : uvalue << 1;
            return uvalue;
        }
    }

    /**
     * Undoes the rotation introduced by {@link RotateSignToLsb}.
     *
     * @param uvalue An unsigned value with the sign bit in the LSB.
     * @return A signed two's complement value encoding the same value.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal static long RotateSignToMsb(ulong uvalue)
    public static long RotateSignToMsb(long uvalue) {
        // Rotate sign to MSB
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        unchecked
        {
            boolean isNegative = uvalue % 2 != 0;
            //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
			// operator since the left operand was originally of an unsigned type, but you should confirm this
			// replacement:
            long value = isNegative ? (~(uvalue >>> 1) + 1) | 0x8000000000000000 : uvalue >>> 1;
            return value;
        }
    }

    public void SetBit(int offset, LayoutBit bit) {
        if (bit.getIsInvalid()) {
            return;
        }

        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.buffer[bit.GetOffset(offset)] |= unchecked((byte)(1 << bit.GetBit()));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        this.buffer[bit.GetOffset(offset)] |= (byte)(1 << bit.GetBit());
    }

    /**
     * Move a sparse iterator to the next field within the same sparse scope.
     *
     * @param edit The iterator to advance.
     *
     *             <paramref name="edit.Path">
     *             On success, the path of the field at the given offset, otherwise
     *             undefined.
     *             </paramref>
     *             <paramref name="edit.MetaOffset">
     *             If found, the offset to the metadata of the field, otherwise a
     *             location to insert the field.
     *             </paramref>
     *             <paramref name="edit.cellType">
     *             If found, the layout code of the matching field found, otherwise
     *             undefined.
     *             </paramref>
     *             <paramref name="edit.ValueOffset">
     *             If found, the offset to the value of the field, otherwise
     *             undefined.
     *             </paramref>.
     * @return True if there is another field, false if there are no more.
     */
    public boolean SparseIteratorMoveNext(Reference<RowCursor> edit) {
        if (edit.get().cellType != null) {
            // Move to the next element of an indexed scope.
            if (edit.get().scopeType.IsIndexedScope) {
                edit.get().index++;
            }

            // Skip forward to the end of the current value.
            if (edit.get().endOffset != 0) {
                edit.get().metaOffset = edit.get().endOffset;
                edit.get().endOffset = 0;
            } else {
                edit.get().metaOffset += this.SparseComputeSize(edit);
            }
        }

        // Check if reached end of buffer.
        if (edit.get().metaOffset < this.length) {
            // Check if reached end of sized scope.
            if (!edit.get().scopeType.IsSizedScope || (edit.get().index != edit.get().count)) {
                // Read the metadata.
                this.ReadSparseMetadata(edit);

                // Check if reached end of sparse scope.
                if (!(edit.get().cellType instanceof LayoutEndScope)) {
                    edit.get().exists = true;
                    return true;
                }
            }
        }

        edit.get().cellType = LayoutType.EndScope;
        edit.get().exists = false;
        edit.get().valueOffset = edit.get().metaOffset;
        return false;
    }

    /**
     * Produce a new scope from the current iterator position.
     *
     * @param edit      An initialized iterator pointing at a scope.
     * @param immutable True if the new scope should be marked immutable (read-only).
     * @return A new scope beginning at the current iterator position.
     */
    public RowCursor SparseIteratorReadScope(Reference<RowCursor> edit, boolean immutable) {
        LayoutScope scopeType = edit.get().cellType instanceof LayoutScope ? (LayoutScope)edit.get().cellType :
            null;
        switch (scopeType) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutObject _:
            case LayoutObject
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutArray _:
                case LayoutArray _:
            {
                RowCursor tempVar = new RowCursor();
                tempVar.scopeType = scopeType;
                tempVar.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                tempVar.start = edit.get().valueOffset;
                tempVar.valueOffset = edit.get().valueOffset;
                tempVar.metaOffset = edit.get().valueOffset;
                tempVar.layout = edit.get().layout;
                tempVar.immutable = immutable;
                return tempVar.clone();

                break;
            }

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedArray _:
            case LayoutTypedArray
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedSet _:
                case LayoutTypedSet _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedMap _:
            case LayoutTypedMap _:
            {
                int valueOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE); // Point after the Size
                RowCursor tempVar2 = new RowCursor();
                tempVar2.scopeType = scopeType;
                tempVar2.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                tempVar2.start = edit.get().valueOffset;
                tempVar2.valueOffset = valueOffset;
                tempVar2.metaOffset = valueOffset;
                tempVar2.layout = edit.get().layout;
                tempVar2.immutable = immutable;
                tempVar2.count = this.ReadUInt32(edit.get().valueOffset);
                return tempVar2.clone();

                break;
            }

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedTuple _:
            case LayoutTypedTuple
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTuple _:
                case LayoutTuple _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged _:
            case LayoutTagged _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged2 _:
            case LayoutTagged2 _:
            {
                RowCursor tempVar3 = new RowCursor();
                tempVar3.scopeType = scopeType;
                tempVar3.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                tempVar3.start = edit.get().valueOffset;
                tempVar3.valueOffset = edit.get().valueOffset;
                tempVar3.metaOffset = edit.get().valueOffset;
                tempVar3.layout = edit.get().layout;
                tempVar3.immutable = immutable;
                tempVar3.count = edit.get().cellTypeArgs.getCount();
                return tempVar3.clone();

                break;
            }

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutNullable _:
            case LayoutNullable
                _:
                {
                    boolean hasValue = this.ReadInt8(edit.get().valueOffset) != 0;
                    if (hasValue) {
                        // Start at the T so it can be read.
                        int valueOffset = edit.get().valueOffset + 1;
                        RowCursor tempVar4 = new RowCursor();
                        tempVar4.scopeType = scopeType;
                        tempVar4.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                        tempVar4.start = edit.get().valueOffset;
                        tempVar4.valueOffset = valueOffset;
                        tempVar4.metaOffset = valueOffset;
                        tempVar4.layout = edit.get().layout;
						tempVar4.immutable = immutable;
                        tempVar4.count = 2;
                        tempVar4.index = 1;
                        return tempVar4.clone();
                    } else {
                        // Start at the end of the scope, instead of at the T, so the T will be skipped.
                        TypeArgument typeArg = edit.get().cellTypeArgs.get(0).clone();
                        int valueOffset = edit.get().valueOffset + 1 + this.CountDefaultValue(typeArg.getType(),
							typeArg.getTypeArgs().clone());
                        RowCursor tempVar5 = new RowCursor();
                        tempVar5.scopeType = scopeType;
                        tempVar5.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                        tempVar5.start = edit.get().valueOffset;
                        tempVar5.valueOffset = valueOffset;
                        tempVar5.metaOffset = valueOffset;
                        tempVar5.layout = edit.get().layout;
                        tempVar5.immutable = immutable;
                        tempVar5.count = 2;
                        tempVar5.index = 2;
                        return tempVar5.clone();
                    }

                    break;
                }

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUDT _:
            case LayoutUDT
                _:
                {
                    Layout udt = this.resolver.Resolve(edit.get().cellTypeArgs.getSchemaId().clone());
                    int valueOffset = this.ComputeVariableValueOffset(udt, edit.get().valueOffset,
						udt.numVariable());
                    RowCursor tempVar6 = new RowCursor();
                    tempVar6.scopeType = scopeType;
                    tempVar6.scopeTypeArgs = edit.get().cellTypeArgs.clone();
                    tempVar6.start = edit.get().valueOffset;
                    tempVar6.valueOffset = valueOffset;
                    tempVar6.metaOffset = valueOffset;
                    tempVar6.layout = udt;
                    tempVar6.immutable = immutable;
                    return tempVar6.clone();

                    break;
                }

            default:
                throw new IllegalStateException("Not a scope type.");
                return null;
        }
    }

    /**
     * The length of row in bytes.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.buffer.Slice(0, this.length).ToArray();
    }

    public void TypedCollectionMoveField(Reference<RowCursor> dstEdit, Reference<RowCursor> srcEdit
		, RowOptions options) {
        int encodedSize = this.SparseComputeSize(srcEdit);
        int numBytes = encodedSize - (srcEdit.get().valueOffset - srcEdit.get().metaOffset);

        // Insert the field metadata into its new location.
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shiftInsert;
        Out<Integer> tempOut_shiftInsert = new Out<Integer>();
        this.EnsureSparse(dstEdit, srcEdit.get().cellType, srcEdit.get().cellTypeArgs.clone(), numBytes,
			options, tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shiftInsert);
        shiftInsert = tempOut_shiftInsert.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(dstEdit, srcEdit.get().cellType, srcEdit.get().cellTypeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        if (srcEdit.get().metaOffset >= dstEdit.get().metaOffset) {
            srcEdit.get().metaOffset += shiftInsert;
            srcEdit.get().valueOffset += shiftInsert;
        }

        // Copy the value bits from the old location.
        this.buffer.Slice(srcEdit.get().valueOffset, numBytes).CopyTo(this.buffer.Slice(dstEdit.get().valueOffset));
        this.length += shiftInsert;

        // Delete the old location.
        Out<Integer> tempOut_metaBytes2 = new Out<Integer>();
        Out<Integer> tempOut_spaceNeeded2 = new Out<Integer>();
        int shiftDelete;
        Out<Integer> tempOut_shiftDelete = new Out<Integer>();
        this.EnsureSparse(srcEdit, srcEdit.get().cellType, srcEdit.get().cellTypeArgs.clone(), numBytes,
			RowOptions.Delete, tempOut_metaBytes2, tempOut_spaceNeeded2, tempOut_shiftDelete);
        shiftDelete = tempOut_shiftDelete.get();
        spaceNeeded = tempOut_spaceNeeded2.get();
        metaBytes = tempOut_metaBytes2.get();

        checkState(shiftDelete < 0);
        this.length += shiftDelete;
    }

    /**
     * Rebuild the unique index for a set/map scope.
     *
     * @param scope The sparse scope to rebuild an index for.
     * @return Success if the index could be built, an error otherwise.
     * <p>
     * The <paramref name="scope" /> MUST be a set or map scope.
     * <p>
     * The scope may have been built (e.g. via RowWriter) with relaxed uniqueness constraint checking.
     * This operation rebuilds an index to support verification of uniqueness constraints during
     * subsequent partial updates.  If the appropriate uniqueness constraints cannot be established (i.e.
     * a duplicate exists), this operation fails.  Before continuing, the resulting scope should either:
     * <list type="number">
     * <item>
     * <description>
     * Be repaired (e.g. by deleting duplicates) and the index rebuild operation should be
     * run again.
     * </description>
     * </item> <item>
     * <description>Be deleted.  The entire scope should be removed including its items.</description>
     * </item>
     * </list> Failure to perform one of these actions will leave the row is potentially in a corrupted
     * state where partial updates may subsequent fail.
     * </p>
     * <p>
     * The target <paramref name="scope" /> may or may not have already been indexed.  This
     * operation is idempotent.
     * </p>
     */
    public Result TypedCollectionUniqueIndexRebuild(Reference<RowCursor> scope) {
        checkArgument(scope.get().scopeType.IsUniqueScope);
        checkArgument(scope.get().index == 0);
        RowCursor dstEdit;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
		// cannot be
        // converted using the 'Out' helper class unless the method is within the code being modified:
        scope.get().Clone(out dstEdit);
        if (dstEdit.count <= 1) {
            return Result.Success;
        }

        // Compute Index Elements.
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'stackalloc' in Java:
        UniqueIndexItem item;
        Span<UniqueIndexItem> uniqueIndex = dstEdit.count < 100 ? stackalloc UniqueIndexItem[dstEdit.count] :
        new UniqueIndexItem[dstEdit.count];
        dstEdit.metaOffset = scope.get().valueOffset;
        for (; dstEdit.index < dstEdit.count; dstEdit.index++) {
            Reference<RowCursor> tempReference_dstEdit =
				new Reference<RowCursor>(dstEdit);
            this.ReadSparseMetadata(tempReference_dstEdit);
            dstEdit = tempReference_dstEdit.get();
            Contract.Assert(dstEdit.pathOffset ==
            default)
				Reference<RowCursor> tempReference_dstEdit2 =
					new Reference<RowCursor>(dstEdit);
                int elmSize = this.SparseComputeSize(tempReference_dstEdit2);
                dstEdit = tempReference_dstEdit2.get();

                UniqueIndexItem tempVar = new UniqueIndexItem();
                tempVar.Code = dstEdit.cellType.LayoutCode;
                tempVar.MetaOffset = dstEdit.metaOffset;
                tempVar.ValueOffset = dstEdit.valueOffset;
                tempVar.Size = elmSize;
                uniqueIndex[dstEdit.index] = tempVar.clone();

                dstEdit.metaOffset += elmSize;
        }

        // Create scratch space equal to the sum of the sizes of the scope's values.
        // Implementation Note: theoretically this scratch space could be eliminated by
        // performing the item move operations directly during the Insertion Sort, however,
        // doing so might result in moving the same item multiple times.  Under the assumption
        // that items are relatively large, using scratch space requires each item to be moved
        // AT MOST once.  Given that row buffer memory is likely reused, scratch space is
        // relatively memory efficient.
        int shift = dstEdit.metaOffset - scope.get().valueOffset;

        // Sort and check for duplicates.
        // TODO: C# TO JAVA CONVERTER: C# 'unsafe' code is not converted by C# to Java Converter:
        //		unsafe
        //			{
        //				Span<UniqueIndexItem> p = new Span<UniqueIndexItem>(Unsafe.AsPointer(ref uniqueIndex
		//				.GetPinnableReference()), uniqueIndex.Length);
        //				if (!this.InsertionSort(ref scope, ref dstEdit, p))
        //				{
        //					return Result.Exists;
        //				}
        //			}

        // Move elements.
        int metaOffset = scope.get().valueOffset;
        this.Ensure(this.length + shift);
        this.buffer.Slice(metaOffset, this.length - metaOffset).CopyTo(this.buffer.Slice(metaOffset + shift));
        for (UniqueIndexItem x : uniqueIndex) {
            this.buffer.Slice(x.MetaOffset + shift, x.Size).CopyTo(this.buffer.Slice(metaOffset));
            metaOffset += x.Size;
        }

        // Delete the scratch space (if necessary - if it doesn't just fall off the end of the row).
        if (metaOffset != this.length) {
            this.buffer.Slice(metaOffset + shift, this.length - metaOffset).CopyTo(this.buffer.Slice(metaOffset));
        }

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
        this.buffer.Slice(this.length, shift).Fill(0xFF);
        //#endif

        return Result.Success;
    }

    public void UnsetBit(int offset, LayoutBit bit) {
        checkState(LayoutBit.opNotEquals(bit.clone(), LayoutBit.Invalid));
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.buffer[bit.GetOffset(offset)] &= unchecked((byte)~(1 << bit.GetBit()));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        this.buffer[bit.GetOffset(offset)] &= (byte)~(1 << bit.GetBit());
    }

    public int Write7BitEncodedInt(int offset, long value) {
        return this.Write7BitEncodedUInt(offset, RowBuffer.RotateSignToLsb(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal int Write7BitEncodedUInt(int offset, ulong value)
    public int Write7BitEncodedUInt(int offset, long value) {
        // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
        // when set, indicates there are more bytes.
        int i = 0;
        while (value >= 0x80L) {
            // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
            //ORIGINAL LINE: this.WriteUInt8(offset + i++, unchecked((byte)(value | 0x80)));
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            this.WriteUInt8(offset + i++, (byte)(value | 0x80));
            //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
			// operator since the left operand was originally of an unsigned type, but you should confirm this
			// replacement:
            value >>>= 7;
        }

        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.WriteUInt8(offset + i++, (byte)value);
        this.WriteUInt8(offset + i++, (byte)value);
        return i;
    }

    public void WriteDateTime(int offset, LocalDateTime value) {
        Reference<LocalDateTime> tempReference_value = new Reference<LocalDateTime>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteDecimal(int offset, BigDecimal value) {
        Reference<BigDecimal> tempReference_value = new Reference<BigDecimal>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteFixedBinary(int offset, ReadOnlySpan<byte> value, int len)
    public void WriteFixedBinary(int offset, ReadOnlySpan<Byte> value, int len) {
        value.CopyTo(this.buffer.Slice(offset, len));
        if (value.Length < len) {
            this.buffer.Slice(offset + value.Length, len - value.Length).Fill(0);
        }
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteFixedBinary(int offset, ReadOnlySequence<byte> value, int len)
    public void WriteFixedBinary(int offset, ReadOnlySequence<Byte> value, int len) {
        value.CopyTo(this.buffer.Slice(offset, len));
        if (value.Length < len) {
            this.buffer.Slice(offset + (int)value.Length, len - (int)value.Length).Fill(0);
        }
    }

    public void WriteFixedString(int offset, Utf8Span value) {
        value.Span.CopyTo(this.buffer.Slice(offset));
    }

    public void WriteFloat128(int offset, Float128 value) {
        Reference<Float128> tempReference_value =
			new Reference<Float128>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteFloat32(int offset, float value) {
        Reference<Float> tempReference_value = new Reference<Float>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteFloat64(int offset, double value) {
        Reference<Double> tempReference_value = new Reference<Double>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteGuid(int offset, UUID value) {
        Reference<UUID> tempReference_value = new Reference<UUID>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteHeader(int offset, HybridRowHeader value) {
        Reference<HybridRowHeader> tempReference_value =
			new Reference<HybridRowHeader>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteInt16(int offset, short value) {
        Reference<Short> tempReference_value = new Reference<Short>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteInt32(int offset, int value) {
        Reference<Integer> tempReference_value = new Reference<Integer>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteInt64(int offset, long value) {
        Reference<Long> tempReference_value = new Reference<Long>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteInt8(int offset, byte value) {
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.buffer[offset] = unchecked((byte)value);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        this.buffer[offset] = value;
    }

    public void WriteMongoDbObjectId(int offset, MongoDbObjectId value) {
        Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempReference_value =
			new Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    public void WriteNullable(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                              UpdateOptions options, boolean hasValue, Out<RowCursor> newScope) {
        int numBytes = this.CountDefaultValue(scopeType, typeArgs.clone());
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        int numWritten = this.WriteDefaultValue(edit.get().valueOffset, scopeType, typeArgs.clone());
        checkState(numBytes == numWritten);
        checkState(spaceNeeded == metaBytes + numBytes);
        if (hasValue) {
            this.WriteInt8(edit.get().valueOffset, (byte)1);
        }

        int valueOffset = edit.get().valueOffset + 1;
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = valueOffset;
        newScope.get().metaOffset = valueOffset;
        newScope.get().layout = edit.get().layout;
        newScope.get().count = 2;
        newScope.get().index = 1;

        this.length += shift;
        Reference<RowBuffer> tempReference_this =
			new Reference<RowBuffer>(this);
        RowCursorExtensions.MoveNext(newScope.get().clone(), tempReference_this);
        this = tempReference_this.get();
    }

    public void WriteSchemaId(int offset, SchemaId value) {
        this.WriteInt32(offset, value.getId());
    }

    public void WriteSparseArray(Reference<RowCursor> edit, LayoutScope scopeType, UpdateOptions options,
                                 Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE); // end scope type code.
        TypeArgumentList typeArgs = TypeArgumentList.Empty;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        this.WriteSparseTypeCode(edit.get().valueOffset, LayoutCode.EndScope);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = edit.get().valueOffset;
        newScope.get().metaOffset = edit.get().valueOffset;
        newScope.get().layout = edit.get().layout;

        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySpan<byte> value, UpdateOptions
	// options)
    public void WriteSparseBinary(Reference<RowCursor> edit, ReadOnlySpan<Byte> value, UpdateOptions options) {
        int len = value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.Count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Binary, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Binary, TypeArgumentList.Empty, metaBytes);
        int sizeLenInBytes = this.WriteBinary(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySequence<byte> value, UpdateOptions
	// options)
    public void WriteSparseBinary(Reference<RowCursor> edit, ReadOnlySequence<Byte> value,
                                  UpdateOptions options) {
        int len = (int)value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.Count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Binary, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Binary, TypeArgumentList.Empty, metaBytes);
        int sizeLenInBytes = this.WriteBinary(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseBool(Reference<RowCursor> edit, boolean value, UpdateOptions options) {
        int numBytes = 0;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.Empty,
			numBytes, options, tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.Empty,
			metaBytes);
        checkState(spaceNeeded == metaBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseDateTime(Reference<RowCursor> edit, LocalDateTime value, UpdateOptions options) {
        int numBytes = 8;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.DateTime, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.DateTime, TypeArgumentList.Empty, metaBytes);
        this.WriteDateTime(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseDecimal(Reference<RowCursor> edit, BigDecimal value, UpdateOptions options) {
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        int numBytes = sizeof(BigDecimal);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Decimal, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Decimal, TypeArgumentList.Empty, metaBytes);
        this.WriteDecimal(edit.get().valueOffset, value);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        checkState(spaceNeeded == metaBytes + sizeof(BigDecimal));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseFloat128(Reference<RowCursor> edit, Float128 value, UpdateOptions options) {
        int numBytes = Float128.Size;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Float128, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float128, TypeArgumentList.Empty, metaBytes);
        this.WriteFloat128(edit.get().valueOffset, value.clone());
        checkState(spaceNeeded == metaBytes + Float128.Size);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseFloat32(Reference<RowCursor> edit, float value, UpdateOptions options) {
        int numBytes = (Float.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Float32, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float32, TypeArgumentList.Empty, metaBytes);
        this.WriteFloat32(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Float.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseFloat64(Reference<RowCursor> edit, double value, UpdateOptions options) {
        int numBytes = (Double.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Float64, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float64, TypeArgumentList.Empty, metaBytes);
        this.WriteFloat64(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Double.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseGuid(Reference<RowCursor> edit, UUID value, UpdateOptions options) {
        int numBytes = 16;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Guid, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Guid, TypeArgumentList.Empty, metaBytes);
        this.WriteGuid(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + 16);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseInt16(Reference<RowCursor> edit, short value, UpdateOptions options) {
        int numBytes = (Short.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int16, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int16, TypeArgumentList.Empty, metaBytes);
        this.WriteInt16(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Short.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseInt32(Reference<RowCursor> edit, int value, UpdateOptions options) {
        int numBytes = (Integer.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int32, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int32, TypeArgumentList.Empty, metaBytes);
        this.WriteInt32(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Integer.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseInt64(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = (Long.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int64, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int64, TypeArgumentList.Empty, metaBytes);
        this.WriteInt64(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Long.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseInt8(Reference<RowCursor> edit, byte value, UpdateOptions options) {
        int numBytes = (Byte.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int8, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, LayoutType.Int8, TypeArgumentList.Empty, metaBytes);
        this.WriteInt8(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Byte.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseMongoDbObjectId(Reference<RowCursor> edit, MongoDbObjectId value,
                                           UpdateOptions options) {
        int numBytes = MongoDbObjectId.Size;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, MongoDbObjectId, TypeArgumentList.Empty, numBytes, options,
			tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, MongoDbObjectId, TypeArgumentList.Empty, metaBytes);
        this.WriteMongoDbObjectId(edit.get().valueOffset, value.clone());
        checkState(spaceNeeded == metaBytes + MongoDbObjectId.Size);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseNull(Reference<RowCursor> edit, NullValue value, UpdateOptions options) {
        int numBytes = 0;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Null, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Null, TypeArgumentList.Empty, metaBytes);
        checkState(spaceNeeded == metaBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseObject(Reference<RowCursor> edit, LayoutScope scopeType, UpdateOptions options,
                                  Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE); // end scope type code.
        TypeArgumentList typeArgs = TypeArgumentList.Empty;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, TypeArgumentList.Empty, metaBytes);
        this.WriteSparseTypeCode(edit.get().valueOffset, LayoutCode.EndScope);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = TypeArgumentList.Empty;
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = edit.get().valueOffset;
        newScope.get().metaOffset = edit.get().valueOffset;
        newScope.get().layout = edit.get().layout;

        this.length += shift;
    }

    public void WriteSparseString(Reference<RowCursor> edit, Utf8Span value, UpdateOptions options) {
        int len = value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.Count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Utf8, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Utf8, TypeArgumentList.Empty, metaBytes);
        int sizeLenInBytes = this.WriteString(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseTuple(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs
		, UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE) * (1 + typeArgs.getCount()); // nulls for each element.
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        int valueOffset = edit.get().valueOffset;
        for (int i = 0; i < typeArgs.getCount(); i++) {
            this.WriteSparseTypeCode(valueOffset, LayoutCode.Null);
            valueOffset += (LayoutCode.SIZE / Byte.SIZE);
        }

        this.WriteSparseTypeCode(valueOffset, LayoutCode.EndScope);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = edit.get().valueOffset;
        newScope.get().metaOffset = edit.get().valueOffset;
        newScope.get().layout = edit.get().layout;
        newScope.get().count = typeArgs.getCount();

        this.length += shift;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal void WriteSparseTypeCode(int offset,
	// LayoutCode code)
    public void WriteSparseTypeCode(int offset, LayoutCode code) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.WriteUInt8(offset, (byte)code);
        this.WriteUInt8(offset, (byte)code.getValue());
    }

    public void WriteSparseUDT(Reference<RowCursor> edit, LayoutScope scopeType, Layout udt,
                               UpdateOptions options, Out<RowCursor> newScope) {
        TypeArgumentList typeArgs = new TypeArgumentList(udt.schemaId().clone());
        int numBytes = udt.size() + (LayoutCode.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);

        // Clear all presence bits.
        this.buffer.Slice(edit.get().valueOffset, udt.size()).Fill(0);

        // Write scope terminator.
        int valueOffset = edit.get().valueOffset + udt.size();
        this.WriteSparseTypeCode(valueOffset, LayoutCode.EndScope);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = valueOffset;
        newScope.get().metaOffset = valueOffset;
        newScope.get().layout = udt;

        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseUInt16(ref RowCursor edit, ushort value, UpdateOptions options)
    public void WriteSparseUInt16(Reference<RowCursor> edit, short value, UpdateOptions options) {
        int numBytes = (Short.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UInt16, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt16, TypeArgumentList.Empty, metaBytes);
        this.WriteUInt16(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Short.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseUInt32(ref RowCursor edit, uint value, UpdateOptions options)
    public void WriteSparseUInt32(Reference<RowCursor> edit, int value, UpdateOptions options) {
        int numBytes = (Integer.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UInt32, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt32, TypeArgumentList.Empty, metaBytes);
        this.WriteUInt32(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Integer.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseUInt64(ref RowCursor edit, ulong value, UpdateOptions options)
    public void WriteSparseUInt64(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = (Long.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UInt64, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt64, TypeArgumentList.Empty, metaBytes);
        this.WriteUInt64(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + (Long.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseUInt8(ref RowCursor edit, byte value, UpdateOptions options)
    public void WriteSparseUInt8(Reference<RowCursor> edit, byte value, UpdateOptions options) {
        int numBytes = 1;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UInt8, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt8, TypeArgumentList.Empty, metaBytes);
        this.WriteUInt8(edit.get().valueOffset, value);
        checkState(spaceNeeded == metaBytes + 1);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseUnixDateTime(Reference<RowCursor> edit, UnixDateTime value, UpdateOptions options) {
        int numBytes = 8;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UnixDateTime, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes
			, tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, LayoutType.UnixDateTime, TypeArgumentList.Empty, metaBytes);
        this.WriteUnixDateTime(edit.get().valueOffset, value.clone());
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    public void WriteSparseVarInt(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = RowBuffer.Count7BitEncodedInt(value);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.VarInt, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.VarInt, TypeArgumentList.Empty, metaBytes);
        int sizeLenInBytes = this.Write7BitEncodedInt(edit.get().valueOffset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == metaBytes + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseVarUInt(ref RowCursor edit, ulong value, UpdateOptions options)
    public void WriteSparseVarUInt(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = RowBuffer.Count7BitEncodedUInt(value);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.VarUInt, TypeArgumentList.Empty, numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.VarUInt, TypeArgumentList.Empty, metaBytes);
        int sizeLenInBytes = this.Write7BitEncodedUInt(edit.get().valueOffset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == metaBytes + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset + spaceNeeded;
        this.length += shift;
    }

    /**
     * Copies the content of the buffer into the target stream.
     */
    public void WriteTo(OutputStream stream) {
        stream.Write(this.buffer.Slice(0, this.length));
    }

    public void WriteTypedArray(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                                UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = (Integer.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.WriteUInt32(edit.get().valueOffset, 0);
        int valueOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = valueOffset;
        newScope.get().metaOffset = valueOffset;
        newScope.get().layout = edit.get().layout;

        this.length += shift;
    }

    public void WriteTypedMap(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                              UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = (Integer.SIZE / Byte.SIZE); // Sized scope.
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.WriteUInt32(edit.get().valueOffset, 0);
        int valueOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = valueOffset;
        newScope.get().metaOffset = valueOffset;
        newScope.get().layout = edit.get().layout;

        this.length += shift;
    }

    public void WriteTypedSet(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                              UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = (Integer.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.WriteUInt32(edit.get().valueOffset, 0);
        int valueOffset = edit.get().valueOffset + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = valueOffset;
        newScope.get().metaOffset = valueOffset;
        newScope.get().layout = edit.get().layout;

        this.length += shift;
    }

    public void WriteTypedTuple(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                                UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = this.CountDefaultValue(scopeType, typeArgs.clone());
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs.clone(), numBytes, options, tempOut_metaBytes,
			tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        int numWritten = this.WriteDefaultValue(edit.get().valueOffset, scopeType, typeArgs.clone());
        checkState(numBytes == numWritten);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType = scopeType;
        newScope.get().scopeTypeArgs = typeArgs.clone();
        newScope.get().start = edit.get().valueOffset;
        newScope.get().valueOffset = edit.get().valueOffset;
        newScope.get().metaOffset = edit.get().valueOffset;
        newScope.get().layout = edit.get().layout;
        newScope.get().count = typeArgs.getCount();

        this.length += shift;
        Reference<RowBuffer> tempReference_this =
			new Reference<RowBuffer>(this);
        RowCursorExtensions.MoveNext(newScope.get().clone(), tempReference_this);
        this = tempReference_this.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteUInt16(int offset, ushort value)
    public void WriteUInt16(int offset, short value) {
        Reference<Short> tempReference_value = new Reference<Short>(value);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteUInt32(int offset, uint value)
    public void WriteUInt32(int offset, int value) {
        Reference<Integer> tempReference_value = new Reference<Integer>(value);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteUInt64(int offset, ulong value)
    public void WriteUInt64(int offset, long value) {
        Reference<Long> tempReference_value = new Reference<Long>(value);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal void WriteUInt8(int offset, byte value)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal void WriteUInt8(int offset, byte value)
    public void WriteUInt8(int offset, byte value) {
        this.buffer[offset] = value;
    }

    public void WriteUnixDateTime(int offset, UnixDateTime value) {
        Reference<UnixDateTime> tempReference_value =
			new Reference<UnixDateTime>(value);
        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
        value = tempReference_value.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteVariableBinary(int offset, ReadOnlySpan<byte> value, bool exists, out int shift)
    public void WriteVariableBinary(int offset, ReadOnlySpan<Byte> value, boolean exists,
									Out<Integer> shift) {
        int numBytes = value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.WriteBinary(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length += shift.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteVariableBinary(int offset, ReadOnlySequence<byte> value, bool exists, out int
	// shift)
    public void WriteVariableBinary(int offset, ReadOnlySequence<Byte> value, boolean exists,
									Out<Integer> shift) {
        int numBytes = (int)value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.WriteBinary(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length += shift.get();
    }

    public void WriteVariableInt(int offset, long value, boolean exists, Out<Integer> shift) {
        int numBytes = RowBuffer.Count7BitEncodedInt(value);
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, true, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.Write7BitEncodedInt(offset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == numBytes);
        this.length += shift.get();
    }

    public void WriteVariableString(int offset, Utf8Span value, boolean exists, Out<Integer> shift) {
        int numBytes = value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.WriteString(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length += shift.get();
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteVariableUInt(int offset, ulong value, bool exists, out int shift)
    public void WriteVariableUInt(int offset, long value, boolean exists, Out<Integer> shift) {
        int numBytes = RowBuffer.Count7BitEncodedUInt(value);
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, true, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.Write7BitEncodedUInt(offset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == numBytes);
        this.length += shift.get();
    }

    public RowBuffer clone() {
        RowBuffer varCopy = new RowBuffer();

        varCopy.resizer = this.resizer;
        varCopy.buffer = this.buffer;
        varCopy.resolver = this.resolver;
        varCopy.length = this.length;

        return varCopy;
    }

    /**
     * Compares the values of two encoded fields using the hybrid row binary collation.
     *
     * @param left     An edit describing the left field.
     * @param leftLen  The size of the left field's value in bytes.
     * @param right    An edit describing the right field.
     * @param rightLen The size of the right field's value in bytes.
     * @return <list type="table">
     * <item>
     * <term>-1</term><description>left less than right.</description>
     * </item> <item>
     * <term>0</term><description>left and right are equal.</description>
     * </item> <item>
     * <term>1</term><description>left is greater than right.</description>
     * </item>
     * </list>
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1201", Justification = "Logical
    // Grouping.")] private int CompareFieldValue(RowCursor left, int leftLen, RowCursor right, int rightLen)
    private int CompareFieldValue(RowCursor left, int leftLen, RowCursor right, int rightLen) {
        if (left.cellType.LayoutCode.getValue() < right.cellType.LayoutCode.getValue()) {
            return -1;
        }

        if (left.cellType == right.cellType) {
            if (leftLen < rightLen) {
                return -1;
            }

            if (leftLen == rightLen) {
                return this.buffer.Slice(left.valueOffset, leftLen).SequenceCompareTo(this.buffer.Slice(right.valueOffset, rightLen));
            }
        }

        return 1;
    }

    /**
     * Compares the values of two encoded key-value pair fields using the hybrid row binary
     * collation.
     *
     * @param left  An edit describing the left field.
     * @param right An edit describing the right field.
     * @return <list type="table">
     * <item>
     * <term>-1</term><description>left less than right.</description>
     * </item> <item>
     * <term>0</term><description>left and right are equal.</description>
     * </item> <item>
     * <term>1</term><description>left is greater than right.</description>
     * </item>
     * </list>
     */
    private int CompareKeyValueFieldValue(RowCursor left, RowCursor right) {
        LayoutTypedTuple leftScopeType = left.cellType instanceof LayoutTypedTuple ? (LayoutTypedTuple)left.cellType :
            null;
        LayoutTypedTuple rightScopeType = right.cellType instanceof LayoutTypedTuple ?
            (LayoutTypedTuple)right.cellType : null;
        checkArgument(leftScopeType != null);
        checkArgument(rightScopeType != null);
        checkArgument(left.cellTypeArgs.getCount() == 2);
        checkArgument(left.cellTypeArgs.equals(right.cellTypeArgs.clone()));

        RowCursor leftKey = new RowCursor();
        leftKey.layout = left.layout;
        leftKey.scopeType = leftScopeType;
        leftKey.scopeTypeArgs = left.cellTypeArgs.clone();
		leftKey.start = left.valueOffset;
        leftKey.metaOffset = left.valueOffset;
        leftKey.index = 0;

        Reference<RowCursor> tempReference_leftKey =
			new Reference<RowCursor>(leftKey);
        this.ReadSparseMetadata(tempReference_leftKey);
        leftKey = tempReference_leftKey.get();
        checkState(leftKey.pathOffset == 0);
        Reference<RowCursor> tempReference_leftKey2 =
			new Reference<RowCursor>(leftKey);
        int leftKeyLen = this.SparseComputeSize(tempReference_leftKey2) - (leftKey.valueOffset - leftKey.metaOffset);
        leftKey = tempReference_leftKey2.get();

        RowCursor rightKey = new RowCursor();
        rightKey.layout = right.layout;
        rightKey.scopeType = rightScopeType;
        rightKey.scopeTypeArgs = right.cellTypeArgs.clone();
        rightKey.start = right.valueOffset;
        rightKey.metaOffset = right.valueOffset;
        rightKey.index = 0;

        Reference<RowCursor> tempReference_rightKey =
			new Reference<RowCursor>(rightKey);
        this.ReadSparseMetadata(tempReference_rightKey);
        rightKey = tempReference_rightKey.get();
        checkState(rightKey.pathOffset == 0);
        Reference<RowCursor> tempReference_rightKey2 =
			new Reference<RowCursor>(rightKey);
        int rightKeyLen = this.SparseComputeSize(tempReference_rightKey2) - (rightKey.valueOffset - rightKey.metaOffset);
        rightKey = tempReference_rightKey2.get();

        return this.CompareFieldValue(leftKey.clone(), leftKeyLen, rightKey.clone(), rightKeyLen);
    }

    /**
     * Compute the number of bytes necessary to store the signed integer using the varint
     * encoding.
     *
     * @param value The value to be encoded.
     * @return The number of bytes needed to store the varint encoding of {@link value}.
     */
    private static int Count7BitEncodedInt(long value) {
        return RowBuffer.Count7BitEncodedUInt(RowBuffer.RotateSignToLsb(value));
    }

    /**
     * Return the size (in bytes) of the default sparse value for the type.
     *
     * @param code     The type of the default value.
     * @param typeArgs
     */
    private int CountDefaultValue(LayoutType code, TypeArgumentList typeArgs) {
        // JTHTODO: convert to a virtual?
        switch (code) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNull _:
            case LayoutNull
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutBoolean _:
                case LayoutBoolean _:
            return 1;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutInt8 _:
            case LayoutInt8
                _:
                return LayoutType.Int8.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutInt16 _:
            case LayoutInt16
                _:
                return LayoutType.Int16.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutInt32 _:
            case LayoutInt32
                _:
                return LayoutType.Int32.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutInt64 _:
            case LayoutInt64
                _:
                return LayoutType.Int64.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUInt8 _:
            case LayoutUInt8
                _:
                return LayoutType.UInt8.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUInt16 _:
            case LayoutUInt16
                _:
                return LayoutType.UInt16.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUInt32 _:
            case LayoutUInt32
                _:
                return LayoutType.UInt32.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUInt64 _:
            case LayoutUInt64
                _:
                return LayoutType.UInt64.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutFloat32 _:
            case LayoutFloat32
                _:
                return LayoutType.Float32.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutFloat64 _:
            case LayoutFloat64
                _:
                return LayoutType.Float64.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutFloat128 _:
            case LayoutFloat128
                _:
                return LayoutType.Float128.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutDecimal _:
            case LayoutDecimal
                _:
                return LayoutType.Decimal.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutDateTime _:
            case LayoutDateTime
                _:
                return LayoutType.DateTime.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUnixDateTime _:
            case LayoutUnixDateTime
                _:
                return LayoutType.UnixDateTime.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutGuid _:
            case LayoutGuid
                _:
                return LayoutType.Guid.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutMongoDbObjectId _:
            case LayoutMongoDbObjectId
                _:
                return MongoDbObjectId.Size;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUtf8 _:
            case LayoutUtf8
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutBinary _:
                case LayoutBinary _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutVarInt _:
            case LayoutVarInt _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutVarUInt _:
            case LayoutVarUInt _:

            // Variable length types preceded by their varuint size take 1 byte for a size of 0.
            return 1;

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutObject _:
            case LayoutObject
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutArray _:
                case LayoutArray _:

            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            return (LayoutCode.SIZE / Byte.SIZE);

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedArray _:
            case LayoutTypedArray
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedSet _:
                case LayoutTypedSet _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedMap _:
            case LayoutTypedMap _:

            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            return (Integer.SIZE / Byte.SIZE);

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTuple _:
            case LayoutTuple
                _:

                // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
                return (LayoutCode.SIZE / Byte.SIZE) + ((LayoutCode.SIZE / Byte.SIZE) * typeArgs.getCount());

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedTuple _:
            case LayoutTypedTuple
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTagged _:
                case LayoutTagged _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged2 _:
            case LayoutTagged2 _:

            // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
            int sum = 0;
                for (TypeArgument arg : typeArgs) {
                    sum += this.CountDefaultValue(arg.getType(), arg.getTypeArgs().clone());
                }

                return sum;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNullable _:
            case LayoutNullable
                _:

                // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
                return 1 + this.CountDefaultValue(typeArgs.get(0).getType(), typeArgs.get(0).getTypeArgs().clone());

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutUDT _:
            case LayoutUDT
                _:
                Layout udt = this.resolver.Resolve(typeArgs.getSchemaId().clone());
                return udt.getSize() + (LayoutCode.SIZE / Byte.SIZE);

            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
                return 0;
        }
    }

    private static int CountSparsePath(Reference<RowCursor> edit) {
        if (!edit.get().writePathToken.getIsNull()) {
            return edit.get().writePathToken.Varint.length;
        }

        Out<StringToken> tempOut_writePathToken =
			new Out<StringToken>();
        if (edit.get().layout.getTokenizer().TryFindToken(edit.get().writePath, tempOut_writePathToken)) {
            edit.get().argValue.writePathToken = tempOut_writePathToken.get();
            return edit.get().writePathToken.Varint.length;
        } else {
            edit.get().argValue.writePathToken = tempOut_writePathToken.get();
        }

        int numBytes = edit.get().writePath.ToUtf8String().Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = RowBuffer.Count7BitEncodedUInt((ulong)(edit.layout.Tokenizer.Count +
		// numBytes));
        int sizeLenInBytes =
			RowBuffer.Count7BitEncodedUInt((long)(edit.get().layout.getTokenizer().getCount() + numBytes));
        return sizeLenInBytes + numBytes;
    }

    private void Ensure(int size) {
        if (this.buffer.Length < size) {
            this.buffer = this.resizer.Resize(size, this.buffer);
        }
    }

    /**
     * <see
     * cref="EnsureSparse(ref RowCursor, LayoutCode , TypeArgumentList , int ,RowOptions, out int, out int, out int)" />
     * .
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] private void EnsureSparse(ref RowCursor edit,
	// LayoutType cellType, TypeArgumentList typeArgs, int numBytes, UpdateOptions options, out int metaBytes, out
	// int spaceNeeded, out int shift)
    private void EnsureSparse(Reference<RowCursor> edit, LayoutType cellType, TypeArgumentList typeArgs,
                              int numBytes, UpdateOptions options, Out<Integer> metaBytes,
                              Out<Integer> spaceNeeded, Out<Integer> shift) {
        this.EnsureSparse(edit, cellType, typeArgs.clone(), numBytes, RowOptions.forValue(options), metaBytes,
			spaceNeeded, shift);
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the current value.
     *
     * @param edit        The prepared edit indicating where and in what context the current write will
     *                    happen.
     * @param cellType    The type of the field to be written.
     * @param typeArgs    The type arguments of the field to be written.
     * @param numBytes    The number of bytes needed to encode the value of the field to be written.
     * @param options     The kind of edit to be performed.
     * @param metaBytes   On success, the number of bytes needed to encode the metadata of the new
     *                    field.
     * @param spaceNeeded On success, the number of bytes needed in total to encode the new field
     *                    and its metadata.
     * @param shift       On success, the number of bytes the length of the row buffer was increased
     *                    (which may be negative if the row buffer was shrunk).
     */
    private void EnsureSparse(Reference<RowCursor> edit, LayoutType cellType, TypeArgumentList typeArgs,
                              int numBytes, RowOptions options, Out<Integer> metaBytes,
                              Out<Integer> spaceNeeded,
                              Out<Integer> shift) {
        int metaOffset = edit.get().metaOffset;
        int spaceAvailable = 0;

        // Compute the metadata offsets
        if (edit.get().scopeType.HasImplicitTypeCode(edit)) {
            metaBytes.setAndGet(0);
        } else {
            metaBytes.setAndGet(cellType.CountTypeArgument(typeArgs.clone()));
        }

        if (!edit.get().scopeType.IsIndexedScope) {
            checkState(edit.get().writePath != null);
            int pathLenInBytes = RowBuffer.CountSparsePath(edit);
            metaBytes.setAndGet(metaBytes.get() + pathLenInBytes);
        }

        if (edit.get().exists) {
            // Compute value offset for existing value to be overwritten.
            spaceAvailable = this.SparseComputeSize(edit);
        }

        spaceNeeded.setAndGet(options == RowOptions.Delete ? 0 : metaBytes.get() + numBytes);
        shift.setAndGet(spaceNeeded.get() - spaceAvailable);
        if (shift.get() > 0) {
            this.Ensure(this.length + shift.get());
        }

        this.buffer.Slice(metaOffset + spaceAvailable, this.length - (metaOffset + spaceAvailable)).CopyTo(this.buffer.Slice(metaOffset + spaceNeeded.get()));

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        if (shift.get() < 0) {
            // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
            this.buffer.Slice(this.length + shift.get(), -shift.get()).Fill(0xFF);
        }
        //#endif

        // Update the stored size (fixed arity scopes don't store the size because it is implied by the type args).
        if (edit.get().scopeType.IsSizedScope && !edit.get().scopeType.IsFixedArity) {
            if ((options == RowOptions.Insert) || (options == RowOptions.InsertAt) || ((options == RowOptions.Upsert) && !edit.get().exists)) {
                // Add one to the current scope count.
                checkState(!edit.get().exists);
                this.IncrementUInt32(edit.get().start, 1);
                edit.get().count++;
            } else if ((options == RowOptions.Delete) && edit.get().exists) {
                // Subtract one from the current scope count.
                checkState(this.ReadUInt32(edit.get().start) > 0);
                this.DecrementUInt32(edit.get().start, 1);
                edit.get().count--;
            }
        }

        if (options == RowOptions.Delete) {
            edit.get().cellType = null;
            edit.get().cellTypeArgs = null;
            edit.get().exists = false;
        } else {
            edit.get().cellType = cellType;
            edit.get().cellTypeArgs = typeArgs.clone();
            edit.get().exists = true;
        }
    }

    private void EnsureVariable(int offset, boolean isVarint, int numBytes, boolean exists,
                                Out<Integer> spaceNeeded, Out<Integer> shift) {
        int spaceAvailable = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong existingValueBytes = 0;
        long existingValueBytes = 0;
        if (exists) {
            Out<Integer> tempOut_spaceAvailable = new Out<Integer>();
            existingValueBytes = this.Read7BitEncodedUInt(offset, tempOut_spaceAvailable);
            spaceAvailable = tempOut_spaceAvailable.get();
        }

        if (isVarint) {
            spaceNeeded.setAndGet(numBytes);
        } else {
            spaceAvailable += (int)existingValueBytes; // size already in spaceAvailable
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: spaceNeeded = numBytes + RowBuffer.Count7BitEncodedUInt((ulong)numBytes);
            spaceNeeded.setAndGet(numBytes + RowBuffer.Count7BitEncodedUInt(numBytes));
        }

        shift.setAndGet(spaceNeeded.get() - spaceAvailable);
        if (shift.get() > 0) {
            this.Ensure(this.length + shift.get());
            this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded.get()));
        } else if (shift.get() < 0) {
            this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded.get()));
        }
    }

    /**
     * Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
     * with the associated layout and rowVersion.
     *
     * @return true if the serialization succeeded. false if the input stream was corrupted.
     */
    private boolean InitReadFrom(HybridRowVersion rowVersion) {
        HybridRowHeader header = this.ReadHeader(0).clone();
        Layout layout = this.resolver.Resolve(header.getSchemaId().clone());
        checkState(SchemaId.opEquals(header.getSchemaId().clone(), layout.schemaId().clone()));
		return (header.getVersion() == rowVersion) && (HybridRowHeader.Size + layout.size() <= this.length);
	}

    /**
     * Sorts the <paramref name="uniqueIndex" /> array structure using the hybrid row binary
     * collation.
     *
     * @param scope       The scope to be sorted.
     * @param dstEdit     A edit that points at the scope.
     * @param uniqueIndex A unique index array structure that identifies the row offsets of each
     *                    element in the scope.
     * @return true if the array was sorted, false if a duplicate was found during sorting.
     * <p>
     * Implementation Note:
     * <p>This method MUST guarantee that if at least one duplicate exists it will be found.</p>
     * Insertion Sort is used for this purpose as it guarantees that each value is eventually compared
	 * against its previous item in sorted order.  If any two successive items are the same they must be
     * duplicates.
     * <p>
     * Other search algorithms, such as Quick Sort or Merge Sort, may offer fewer comparisons in the
     * limit but don't necessarily guarantee that duplicates will be discovered.  If an alternative
     * algorithm is used, then an independent duplicate pass MUST be employed.
     * </p>
     * <p>
     * Under the current operational assumptions, the expected cardinality of sets and maps is
     * expected to be relatively small.  If this assumption changes, Insertion Sort may no longer be the
     * best choice.
     * </p>
     */
    private boolean InsertionSort(Reference<RowCursor> scope, Reference<RowCursor> dstEdit,
                                  Span<UniqueIndexItem> uniqueIndex) {
        RowCursor leftEdit = dstEdit.get().clone();
        RowCursor rightEdit = dstEdit.get().clone();

        for (int i = 1; i < uniqueIndex.Length; i++) {
            UniqueIndexItem x = uniqueIndex[i];
            leftEdit.cellType = LayoutType.FromCode(x.Code);
            leftEdit.metaOffset = x.MetaOffset;
            leftEdit.valueOffset = x.ValueOffset;
            int leftBytes = x.Size - (x.ValueOffset - x.MetaOffset);

            // Walk backwards searching for the insertion point for the item as position i.
            int j;
            for (j = i - 1; j >= 0; j--) {
                UniqueIndexItem y = uniqueIndex[j];
                rightEdit.cellType = LayoutType.FromCode(y.Code);
                rightEdit.metaOffset = y.MetaOffset;
                rightEdit.valueOffset = y.ValueOffset;

                int cmp;
                if (scope.get().scopeType instanceof LayoutTypedMap) {
                    cmp = this.CompareKeyValueFieldValue(leftEdit.clone(), rightEdit.clone());
                } else {
                    int rightBytes = y.Size - (y.ValueOffset - y.MetaOffset);
                    cmp = this.CompareFieldValue(leftEdit.clone(), leftBytes, rightEdit.clone(), rightBytes);
                }

                // If there are duplicates then fail.
                if (cmp == 0) {
					return false;
                }

                if (cmp > 0) {
                    break;
                }

                // Swap the jth item to the right to make space for the ith item which is smaller.
                uniqueIndex[j + 1] = uniqueIndex[j];
            }

            // Insert the ith item into the sorted array.
            uniqueIndex[j + 1] = x.clone();
        }

        return true;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private ReadOnlySpan<byte> ReadBinary(int offset, out int sizeLenInBytes)
    private ReadOnlySpan<Byte> ReadBinary(int offset, Out<Integer> sizeLenInBytes) {
        int numBytes = (int)this.Read7BitEncodedUInt(offset, sizeLenInBytes);
        return this.buffer.Slice(offset + sizeLenInBytes.get(), numBytes);
    }

    /**
     * Read the metadata of an encoded sparse field.
     *
     * @param edit The edit structure to fill in.
     *
     *             <paramref name="edit.Path">
     *             On success, the path of the field at the given offset, otherwise
     *             undefined.
     *             </paramref>
     *             <paramref name="edit.MetaOffset">
     *             On success, the offset to the metadata of the field, otherwise a
     *             location to insert the field.
     *             </paramref>
     *             <paramref name="edit.cellType">
     *             On success, the layout code of the existing field, otherwise
     *             undefined.
     *             </paramref>
     *             <paramref name="edit.TypeArgs">
     *             On success, the type args of the existing field, otherwise
     *             undefined.
     *             </paramref>
     *             <paramref name="edit.ValueOffset">
     *             On success, the offset to the value of the field, otherwise
     *             undefined.
     *             </paramref>.
     */
    private void ReadSparseMetadata(Reference<RowCursor> edit) {
        if (edit.get().scopeType.HasImplicitTypeCode(edit)) {
            edit.get().scopeType.SetImplicitTypeCode(edit);
            edit.get().valueOffset = edit.get().metaOffset;
        } else {
            edit.get().cellType = this.ReadSparseTypeCode(edit.get().metaOffset);
            edit.get().valueOffset = edit.get().metaOffset + (LayoutCode.SIZE / Byte.SIZE);
            edit.get().cellTypeArgs = TypeArgumentList.Empty;
            if (edit.get().cellType instanceof LayoutEndScope) {
                // Reached end of current scope without finding another field.
                edit.get().pathToken = 0;
                edit.get().pathOffset = 0;
                edit.get().valueOffset = edit.get().metaOffset;
                return;
            }

            Reference<RowBuffer> tempReference_this =
				new Reference<RowBuffer>(this);
            int sizeLenInBytes;
            Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
            edit.get().cellTypeArgs = edit.get().cellType.ReadTypeArgumentList(tempReference_this,
				edit.get().valueOffset, tempOut_sizeLenInBytes).clone();
            sizeLenInBytes = tempOut_sizeLenInBytes.get();
            this = tempReference_this.get();
            edit.get().valueOffset += sizeLenInBytes;
        }

        Reference<RowBuffer> tempReference_this2 =
			new Reference<RowBuffer>(this);
        edit.get().scopeType.ReadSparsePath(tempReference_this2, edit);
        this = tempReference_this2.get();
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [Conditional("DEBUG")] private void ReadSparsePrimitiveTypeCode(ref RowCursor edit, LayoutType
	// code)
    private void ReadSparsePrimitiveTypeCode(Reference<RowCursor> edit, LayoutType code) {
        checkState(edit.get().exists);

        if (edit.get().scopeType.HasImplicitTypeCode(edit)) {
            if (edit.get().scopeType instanceof LayoutNullable) {
                checkState(edit.get().scopeTypeArgs.getCount() == 1);
                checkState(edit.get().index == 1);
                checkState(edit.get().scopeTypeArgs.get(0).getType() == code);
                checkState(edit.get().scopeTypeArgs.get(0).getTypeArgs().getCount() == 0);
            } else if (edit.get().scopeType.IsFixedArity) {
                checkState(edit.get().scopeTypeArgs.getCount() > edit.get().index);
                checkState(edit.get().scopeTypeArgs.get(edit.get().index).getType() == code);
                checkState(edit.get().scopeTypeArgs.get(edit.get().index).getTypeArgs().getCount() == 0);
            } else {
                checkState(edit.get().scopeTypeArgs.getCount() == 1);
                checkState(edit.get().scopeTypeArgs.get(0).getType() == code);
                checkState(edit.get().scopeTypeArgs.get(0).getTypeArgs().getCount() == 0);
            }
        } else {
            if (code == LayoutType.Boolean) {
                code = this.ReadSparseTypeCode(edit.get().metaOffset);
                checkState(code == LayoutType.Boolean || code == LayoutType.BooleanFalse);
            } else {
                checkState(this.ReadSparseTypeCode(edit.get().metaOffset) == code);
            }
        }

        if (edit.get().scopeType.IsIndexedScope) {
            checkState(edit.get().pathOffset == 0);
            checkState(edit.get().pathToken == 0);
        } else {
            int _;
            Out<Integer> tempOut__ = new Out<Integer>();
            int pathOffset;
            Out<Integer> tempOut_pathOffset = new Out<Integer>();
            int token = this.ReadSparsePathLen(edit.get().layout,
				edit.get().metaOffset + (LayoutCode.SIZE / Byte.SIZE), tempOut__, tempOut_pathOffset);
            pathOffset = tempOut_pathOffset.get();
            _ = tempOut__.get();
            checkState(edit.get().pathOffset == pathOffset);
            checkState(edit.get().pathToken == token);
        }
    }

    private Utf8Span ReadString(int offset, Out<Integer> sizeLenInBytes) {
        int numBytes = (int)this.Read7BitEncodedUInt(offset, sizeLenInBytes);
        return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(offset + sizeLenInBytes.get(), numBytes));
    }

    /**
     * Skip over a nested scope.
     *
     * @param edit The sparse scope to search.
     * @return The 0-based byte offset immediately following the scope end marker.
     */
    private int SkipScope(Reference<RowCursor> edit) {
        while (this.SparseIteratorMoveNext(edit)) {
        }

        if (!edit.get().scopeType.IsSizedScope) {
            edit.get().metaOffset += (LayoutCode.SIZE / Byte.SIZE); // Move past the end of scope marker.
        }

        return edit.get().metaOffset;
    }

    /**
     * Compute the size of a sparse (primitive) field.
     *
     * @param cellType    The type of the current sparse field.
     * @param metaOffset  The 0-based offset from the beginning of the row where the field begins.
     * @param valueOffset The 0-based offset from the beginning of the row where the field's value
     *                    begins.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int SparseComputePrimitiveSize(LayoutType cellType, int metaOffset, int valueOffset) {
        // JTHTODO: convert to a virtual?
        int metaBytes = valueOffset - metaOffset;
        LayoutCode code = cellType.LayoutCode;
        switch (code) {
            case Null:
                checkState(LayoutType.Null.Size == 0);
                return metaBytes;

            case Boolean:
            case BooleanFalse:
                checkState(LayoutType.Boolean.Size == 0);
                return metaBytes;

            case Int8:
                return metaBytes + LayoutType.Int8.Size;

            case Int16:
                return metaBytes + LayoutType.Int16.Size;

            case Int32:
                return metaBytes + LayoutType.Int32.Size;

            case Int64:
                return metaBytes + LayoutType.Int64.Size;

            case UInt8:
                return metaBytes + LayoutType.UInt8.Size;

            case UInt16:
                return metaBytes + LayoutType.UInt16.Size;

            case UInt32:
                return metaBytes + LayoutType.UInt32.Size;

            case UInt64:
                return metaBytes + LayoutType.UInt64.Size;

            case Float32:
                return metaBytes + LayoutType.Float32.Size;

            case Float64:
                return metaBytes + LayoutType.Float64.Size;

            case Float128:
                return metaBytes + LayoutType.Float128.Size;

            case Decimal:
                return metaBytes + LayoutType.Decimal.Size;

            case DateTime:
                return metaBytes + LayoutType.DateTime.Size;

            case UnixDateTime:
                return metaBytes + LayoutType.UnixDateTime.Size;

            case Guid:
                return metaBytes + LayoutType.Guid.Size;

            case MongoDbObjectId:
                return metaBytes + MongoDbObjectId.Size;

            case Utf8:
            case Binary:
                // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
                ///#pragma warning disable SA1137 // Elements should have the same indentation
            {
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
                int numBytes = (int)this.Read7BitEncodedUInt(metaOffset + metaBytes, tempOut_sizeLenInBytes);
                sizeLenInBytes = tempOut_sizeLenInBytes.get();
                return metaBytes + sizeLenInBytes + numBytes;
            }

            case VarInt:
            case VarUInt: {
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes2 = new Out<Integer>();
                this.Read7BitEncodedUInt(metaOffset + metaBytes, tempOut_sizeLenInBytes2);
                sizeLenInBytes = tempOut_sizeLenInBytes2.get();
                return metaBytes + sizeLenInBytes;
            }
            // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
            ///#pragma warning restore SA1137 // Elements should have the same indentation

            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
                return 0;
        }
    }

    /**
     * Compute the size of a sparse field.
     *
     * @param edit The edit structure describing the field to measure.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int SparseComputeSize(Reference<RowCursor> edit) {
        if (!(edit.get().cellType instanceof LayoutScope)) {
            return this.SparseComputePrimitiveSize(edit.get().cellType, edit.get().metaOffset,
				edit.get().valueOffset);
        }

        // Compute offset to end of value for current value.
        RowCursor newScope = this.SparseIteratorReadScope(edit, true).clone();
		Reference<RowCursor> tempReference_newScope =
			new Reference<RowCursor>(newScope);
        int tempVar = this.SkipScope(tempReference_newScope) - edit.get().metaOffset;
        newScope = tempReference_newScope.get();
        return tempVar;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private int WriteBinary(int offset, ReadOnlySpan<byte> value)
    private int WriteBinary(int offset, ReadOnlySpan<Byte> value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (long)value.Length);
        value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private int WriteBinary(int offset, ReadOnlySequence<byte> value)
    private int WriteBinary(int offset, ReadOnlySequence<Byte> value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (long)value.Length);
        value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
    }

    private int WriteDefaultValue(int offset, LayoutType code, TypeArgumentList typeArgs) {
        // JTHTODO: convert to a virtual?
        switch (code) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNull _:
            case LayoutNull
                _:
                this.WriteSparseTypeCode(offset, code.LayoutCode);
                return 1;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutBoolean _:
            case LayoutBoolean
                _:
                this.WriteSparseTypeCode(offset, LayoutCode.BooleanFalse);
                return 1;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt8 _:
            case LayoutInt8
                _:
                this.WriteInt8(offset, (byte)0);
                return LayoutType.Int8.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt16 _:
            case LayoutInt16
                _:
                this.WriteInt16(offset, (short)0);
                return LayoutType.Int16.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt32 _:
            case LayoutInt32
                _:
                this.WriteInt32(offset, 0);
                return LayoutType.Int32.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt64 _:
            case LayoutInt64
                _:
                this.WriteInt64(offset, 0);
                return LayoutType.Int64.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUInt8 _:
            case LayoutUInt8
                _:
                this.WriteUInt8(offset, (byte)0);
                return LayoutType.UInt8.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUInt16 _:
            case LayoutUInt16
                _:
                this.WriteUInt16(offset, (short)0);
                return LayoutType.UInt16.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUInt32 _:
            case LayoutUInt32
                _:
                this.WriteUInt32(offset, 0);
                return LayoutType.UInt32.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUInt64 _:
            case LayoutUInt64
                _:
                this.WriteUInt64(offset, 0);
                return LayoutType.UInt64.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutFloat32 _:
            case LayoutFloat32
                _:
                this.WriteFloat32(offset, 0);
                return LayoutType.Float32.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutFloat64 _:
            case LayoutFloat64
                _:
                this.WriteFloat64(offset, 0);
                return LayoutType.Float64.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutFloat128 _:
            case LayoutFloat128
                _:
                this.WriteFloat128(offset, null);
                return LayoutType.Float128.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutDecimal _:
            case LayoutDecimal
                _:
                this.WriteDecimal(offset, 0);
                return LayoutType.Decimal.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutDateTime _:
            case LayoutDateTime
                _:
                this.WriteDateTime(offset, LocalDateTime.MIN);
                return LayoutType.DateTime.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUnixDateTime _:
            case LayoutUnixDateTime
                _:
                this.WriteUnixDateTime(offset, null);
                return LayoutType.UnixDateTime.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutGuid _:
            case LayoutGuid
                _:
                this.WriteGuid(offset, null);
                return LayoutType.Guid.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutMongoDbObjectId _:
            case LayoutMongoDbObjectId
                _:
                this.WriteMongoDbObjectId(offset, null);
                return MongoDbObjectId.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUtf8 _:
            case LayoutUtf8
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutBinary _:
                case LayoutBinary _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutVarInt _:
            case LayoutVarInt _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutVarUInt _:
            case LayoutVarUInt _:

            // Variable length types preceded by their varuint size take 1 byte for a size of 0.
            return this.Write7BitEncodedUInt(offset, 0);

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutObject _:
            case LayoutObject
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutArray _:
                case LayoutArray _:

            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            this.WriteSparseTypeCode(offset, LayoutCode.EndScope);
                return (LayoutCode.SIZE / Byte.SIZE);

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedArray _:
            case LayoutTypedArray
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTypedSet _:
                case LayoutTypedSet _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedMap _:
            case LayoutTypedMap _:

            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            this.WriteUInt32(offset, 0);
                return (Integer.SIZE / Byte.SIZE);

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTuple _:
            case LayoutTuple
                _:

                // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
                for (int i = 0; i < typeArgs.getCount(); i++) {
                    this.WriteSparseTypeCode(offset, LayoutCode.Null);
                }

                this.WriteSparseTypeCode(offset, LayoutCode.EndScope);
                return (LayoutCode.SIZE / Byte.SIZE) + ((LayoutCode.SIZE / Byte.SIZE) * typeArgs.getCount());

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTypedTuple _:
            case LayoutTypedTuple
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutTagged _:
                case LayoutTagged _:
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutTagged2 _:
            case LayoutTagged2 _:

            // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
            int sum = 0;
                for (TypeArgument arg : typeArgs) {
                    sum += this.WriteDefaultValue(offset + sum, arg.getType(), arg.getTypeArgs().clone());
                }

                return sum;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNullable _:
            case LayoutNullable
                _:

                // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
                this.WriteInt8(offset, (byte)0);
                return 1 + this.WriteDefaultValue(offset + 1, typeArgs.get(0).getType(), typeArgs.get(0).getTypeArgs().clone());

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUDT _:
            case LayoutUDT
                _:

                // Clear all presence bits.
                Layout udt = this.resolver.Resolve(typeArgs.getSchemaId().clone());
                this.buffer.Slice(offset, udt.getSize()).Fill(0);

                // Write scope terminator.
                this.WriteSparseTypeCode(offset + udt.getSize(), LayoutCode.EndScope);
                return udt.getSize() + (LayoutCode.SIZE / Byte.SIZE);

            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
                return 0;
        }
    }

    private void WriteSparseMetadata(Reference<RowCursor> edit, LayoutType cellType,
                                     TypeArgumentList typeArgs, int metaBytes) {
        int metaOffset = edit.get().metaOffset;
        if (!edit.get().scopeType.HasImplicitTypeCode(edit)) {
            Reference<RowBuffer> tempReference_this =
				new Reference<RowBuffer>(this);
            metaOffset += cellType.WriteTypeArgument(tempReference_this, metaOffset, typeArgs.clone());
            this = tempReference_this.get();
        }

        this.WriteSparsePath(edit, metaOffset);
        edit.get().valueOffset = edit.get().metaOffset + metaBytes;
        checkState(edit.get().valueOffset == edit.get().metaOffset + metaBytes);
    }

    private void WriteSparsePath(Reference<RowCursor> edit, int offset) {
        // Some scopes don't encode paths, therefore the cost is always zero.
        if (edit.get().scopeType.IsIndexedScope) {
            edit.get().pathToken = 0;
            edit.get().pathOffset = 0;
            return;
        }

        StringToken _;
        Out<StringToken> tempOut__ =
			new Out<StringToken>();
        checkState(!edit.get().layout.getTokenizer().TryFindToken(edit.get().writePath, tempOut__) || !edit.get().writePathToken.getIsNull());
        _ = tempOut__.get();
        if (!edit.get().writePathToken.getIsNull()) {
            edit.get().writePathToken.Varint.CopyTo(this.buffer.Slice(offset));
            edit.get().pathToken = edit.get().intValue().writePathToken.Id;
            edit.get().pathOffset = offset;
        } else {
			// TODO: It would be better if we could avoid allocating here when the path is UTF16.
            Utf8Span span = edit.get().writePath.ToUtf8String();
            edit.get().pathToken = edit.get().layout.getTokenizer().getCount() + span.Length;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)edit.pathToken);
            int sizeLenInBytes = this.Write7BitEncodedUInt(offset, edit.get().longValue().pathToken);
            edit.get().pathOffset = offset + sizeLenInBytes;
            span.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        }
    }

    private int WriteString(int offset, Utf8Span value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (long)value.Length);
        value.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
    }

    /**
     * {@link UniqueIndexItem} represents a single item within a set/map scope that needs
     * to be indexed.
     * <p>
     * <p>
     * This structure is used when rebuilding a set/map index during row streaming via
     * {@link IO.RowWriter}.
     * <para />
     * Each item encodes its offsets and length within the row.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerDisplay("{MetaOffset}/{ValueOffset}")] private struct UniqueIndexItem
    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ from the original:
    //ORIGINAL LINE: [DebuggerDisplay("{MetaOffset}/{ValueOffset}")] private struct UniqueIndexItem
    static final class UniqueIndexItem {
        /**
         * The layout code of the value.
         */
        public LayoutCode Code = LayoutCode.values()[0];

        /**
         * If existing, the offset to the metadata of the existing field, otherwise the location to
         * insert a new field.
         */
        public int MetaOffset;
        /**
         * Size of the target element.
         */
        public int Size;
        /**
         * If existing, the offset to the value of the existing field, otherwise undefined.
         */
        public int ValueOffset;

        public UniqueIndexItem clone() {
            UniqueIndexItem varCopy = new UniqueIndexItem();

            varCopy.Code = this.Code;
            varCopy.MetaOffset = this.MetaOffset;
            varCopy.ValueOffset = this.ValueOffset;
            varCopy.Size = this.Size;

            return varCopy;
        }
    }
}