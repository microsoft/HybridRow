// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer.UniqueIndexItem;
import com.azure.data.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.data.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutArray;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBinary;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBit;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBoolean;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDecimal;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat128;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutFloat64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutGuid;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt16;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutInt8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutMongoDbObjectId;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutNull;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutNullable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutObject;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged2;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedArray;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedMap;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedSet;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypes;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUDT;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt16;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUtf8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarUInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringToken;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

//import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType.MongoDbObjectId;

/**
 * Manages a sequence of bytes representing a Hybrid Row
 * <p>
 * A Hybrid Row begins in the 0-th byte of the {@link RowBuffer}. The sequence of bytes is defined by the Hybrid Row
 * grammar.
 */
public final class RowBuffer {

    private final ByteBuf buffer;
    private LayoutResolver resolver;

    /**
     * Initializes a new instance of a {@link RowBuffer}
     *
     * @param capacity Initial buffer capacity.
     */
    public RowBuffer(int capacity) {
        this(capacity, ByteBufAllocator.DEFAULT);
    }

    /**
     * Initializes a new instance of a {@link RowBuffer}
     *
     * @param capacity  Initial buffer capacity
     * @param allocator A buffer allocator
     */
    public RowBuffer(final int capacity, @Nonnull final ByteBufAllocator allocator) {
        checkArgument(capacity > 0, "capacity: %s", capacity);
        checkNotNull(allocator, "allocator");
        this.buffer = allocator.buffer(capacity);
        this.resolver = null;
    }

    /**
     * Initializes a new instance of a {@link RowBuffer} from an existing buffer
     *
     * @param buffer   An existing {@link ByteBuf} containing a Hybrid Row. This instance takes ownership of the buffer.
     *                 Hence, the caller should not maintain a reference to the buffer or mutate the buffer after this
     *                 call returns.
     * @param version  The version of the Hybrid Row format to use for encoding the buffer.
     * @param resolver The resolver for UDTs.
     */
    public RowBuffer(@Nonnull final ByteBuf buffer, @Nonnull final HybridRowVersion version, @Nonnull final LayoutResolver resolver) {

        checkNotNull(buffer, "buffer");
        checkNotNull(version, "version");
        checkNotNull(resolver, "resolver");
        checkArgument(buffer.isReadable(HybridRowHeader.SIZE));

        this.buffer = buffer;
        this.resolver = resolver;

        HybridRowHeader header = this.readHeader();
        checkState(header.version() == version, "expected version %s, not %s", version, header.version());

        Layout layout = resolver.resolve(header.schemaId());
        checkState(header.schemaId().equals(layout.schemaId()));
        checkState(HybridRowHeader.SIZE + layout.size() <= this.length());
    }

    public long Read7BitEncodedInt(int offset, Out<Integer> lenInBytes) {
        return RowBuffer.rotateSignToMsb(this.read7BitEncodedUInt(offset, lenInBytes));
    }

    public void WriteNullable(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                              UpdateOptions options, boolean hasValue, Out<RowCursor> newScope) {
        int numBytes = this.countDefaultValue(scopeType, typeArgs.clone());
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
        int numWritten = this.WriteDefaultValue(edit.get().valueOffset(), scopeType, typeArgs.clone());
        checkState(numBytes == numWritten);
        checkState(spaceNeeded == metaBytes + numBytes);
        if (hasValue) {
            this.writeInt8(edit.get().valueOffset(), (byte) 1);
        }

        int valueOffset = edit.get().valueOffset() + 1;
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(valueOffset);
        newScope.get().metaOffset(valueOffset);
        newScope.get().layout(edit.get().layout());
        newScope.get().count(2);
        newScope.get().index(1);

        this.length(this.length() + shift);
        Reference<RowBuffer> tempReference_this =
            new Reference<RowBuffer>(this);
        RowCursors.moveNext(newScope.get().clone(), tempReference_this);
        this = tempReference_this.get();
    }

    /**
     * Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
     * with the associated layout and rowVersion.
     *
     * @return true if the serialization succeeded. false if the input stream was corrupted.
     */
    public boolean readFrom(@Nonnull final InputStream inputStream, final int byteCount, @Nonnull final HybridRowVersion version, @Nonnull final LayoutResolver resolver) {

        checkNotNull(inputStream, "inputStream");
        checkNotNull(resolver, "resolver");
        checkNotNull(version, "version");
        checkState(byteCount >= HybridRowHeader.SIZE, "expected byteCount >= %s, not %s", HybridRowHeader.SIZE, byteCount);

        this.reset();
        this.ensure(byteCount);
        this.resolver = resolver;

        Span<Byte> active = this.buffer.Slice(0, byteCount);
        int bytesRead;

        do {
            bytesRead = inputStream.Read(active);
            active = active.Slice(bytesRead);
        } while (bytesRead != 0);

        if (active.Length != 0) {
            return false;
        }

        return this.InitReadFrom(version);
    }

    /**
     * Compute the byte offsets from the beginning of the row for a given sparse field insertion
     * into a set/map.
     *
     * @param scope   The sparse scope to insert into.
     * @param srcEdit The field to move into the set/map.
     * @return The prepared edit context.
     */
    public RowCursor prepareSparseMove(RowCursor scope, RowCursor srcEdit) {
        checkArgument(scope.scopeType().isUniqueScope());

        checkArgument(scope.index() == 0);
        RowCursor dstEdit;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        scope.Clone(out dstEdit);

        dstEdit.metaOffset(scope.valueOffset());
        int srcSize = this.sparseComputeSize(srcEdit);
        int srcBytes = srcSize - (srcEdit.valueOffset() - srcEdit.metaOffset());
        while (dstEdit.index() < dstEdit.count()) {
            Reference<RowCursor> tempReference_dstEdit =
                new Reference<RowCursor>(dstEdit);
            this.readSparseMetadata(tempReference_dstEdit);
            dstEdit = tempReference_dstEdit.get();
            Contract.Assert(dstEdit.pathOffset() ==
            default)

                int elmSize = -1; // defer calculating the full size until needed.
                int cmp;
                if (scope.get().scopeType() instanceof LayoutTypedMap) {
                    cmp = this.CompareKeyValueFieldValue(srcEdit.get().clone(), dstEdit);
                } else {
                    Reference<RowCursor> tempReference_dstEdit2 =
                        new Reference<RowCursor>(dstEdit);
                    elmSize = this.sparseComputeSize(tempReference_dstEdit2);
                    dstEdit = tempReference_dstEdit2.get();
                    int elmBytes = elmSize - (dstEdit.valueOffset() - dstEdit.metaOffset());
                    cmp = this.CompareFieldValue(srcEdit.get().clone(), srcBytes, dstEdit, elmBytes);
                }

                if (cmp <= 0) {
                    dstEdit.exists = cmp == 0;
                    return dstEdit;
                }

                Reference<RowCursor> tempReference_dstEdit3 =
                    new Reference<RowCursor>(dstEdit);
                elmSize = (elmSize == -1) ? this.sparseComputeSize(tempReference_dstEdit3) : elmSize;
                dstEdit = tempReference_dstEdit3.get();
                dstEdit.index(dstEdit.index() + 1);
                dstEdit.metaOffset(dstEdit.metaOffset() + elmSize);
        }

        dstEdit.exists = false;
        dstEdit.cellType = LayoutType.EndScope;
        dstEdit.valueOffset(dstEdit.metaOffset());
        return dstEdit;
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
    public boolean readFrom(ReadOnlySpan<Byte> input, HybridRowVersion rowVersion, LayoutResolver resolver) {
        int bytesCount = input.Length;
        checkState(bytesCount >= HybridRowHeader.SIZE);

        this.reset();
        this.resolver = resolver;
        this.ensure(bytesCount);
        checkState(this.buffer.Length >= bytesCount);
        input.CopyTo(this.buffer);
        this.length(bytesCount);
        return this.InitReadFrom(rowVersion);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void DecrementUInt32(int offset, uint decrement)
    public void DecrementUInt32(int offset, int decrement) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] -= decrement;
        MemoryMarshal.<Byte, Integer>Cast(this.buffer.Slice(offset))[0] -= decrement;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ReadOnlySpan<byte> ReadSparseBinary(ref RowCursor edit)
    public ReadOnlySpan<Byte> ReadSparseBinary(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Binary);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ReadOnlySpan<byte> span = this.ReadBinary(edit.valueOffset, out int sizeLenInBytes);
        ReadOnlySpan<Byte> span = this.ReadBinary(edit.get().valueOffset(), tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset() + sizeLenInBytes + span.Length;
        return span;
    }

    public void deleteVariable(int offset, boolean isVarint) {

        int start = this.buffer.readerIndex();
        this.read7BitEncodedUInt();

        ByteBuf remainder = this.buffer.slice(this.buffer.readerIndex(), this.buffer.readableBytes());
        this.buffer.readerIndex(start);
        this.buffer.setBytes(start, remainder);
        this.buffer.writerIndex(start + remainder.readableBytes());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void IncrementUInt32(int offset, uint increment)
    public void IncrementUInt32(int offset, int increment) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] += increment;
        MemoryMarshal.<Byte, Integer>Cast(this.buffer.Slice(offset))[0] += increment;
    }

    public boolean ReadSparseBool(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Boolean);
        edit.get().endOffset = edit.get().valueOffset();
        return edit.get().cellType() == LayoutType.Boolean;
    }

    public LocalDateTime ReadSparseDateTime(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.DateTime);
        edit.get().endOffset = edit.get().valueOffset() + 8;
        return this.ReadDateTime(edit.get().valueOffset());
    }

    public void WriteSchemaId(int offset, SchemaId value) {
        this.writeInt32(offset, value.value());
    }

    public long read7BitEncodedUInt() {

        long b = this.buffer.readByte() & 0xFFL;

        if (b < 0x80L) {
            return b;
        }

        long result = b & 0x7FL;
        int shift = 7;

        do {
            checkState(shift < 10 * 7);
            b = this.buffer.readByte() & 0xFFL;
            result |= (b & 0x7FL) << shift;
            shift += 7;
        } while (b >= 0x80L);

        return result;
    }

    public boolean readBit(int offset, LayoutBit bit) {

        if (bit.getIsInvalid()) {
            return true;
        }

        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: return (this.buffer[bit.GetOffset(offset)] & unchecked((byte)(1 << bit.GetBit()))) != 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        return (this.buffer[bit.offset(offset)] & (byte)(1 << bit.bit())) != 0;
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

    public BigDecimal ReadSparseDecimal(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Decimal);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        edit.get().endOffset = edit.get().valueOffset() + sizeof(BigDecimal);
        return this.ReadDecimal(edit.get().valueOffset());
    }

    public Float128 ReadSparseFloat128(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Float128);
        edit.get().endOffset = edit.get().valueOffset() + Float128.Size;
        return this.ReadFloat128(edit.get().valueOffset()).clone();
    }

    public UUID ReadGuid(int offset) {
        return MemoryMarshal.<UUID>Read(this.buffer.Slice(offset));
    }

    public float ReadSparseFloat32(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Float32);
        edit.get().endOffset = edit.get().valueOffset() + (Float.SIZE / Byte.SIZE);
        return this.ReadFloat32(edit.get().valueOffset());
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

    public double ReadSparseFloat64(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Float64);
        edit.get().endOffset = edit.get().valueOffset() + (Double.SIZE / Byte.SIZE);
        return this.ReadFloat64(edit.get().valueOffset());
    }

    public UUID ReadSparseGuid(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Guid);
        edit.get().endOffset = edit.get().valueOffset() + 16;
        return this.ReadGuid(edit.get().valueOffset());
    }

    public short ReadSparseInt16(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Int16);
        edit.get().endOffset = edit.get().valueOffset() + (Short.SIZE / Byte.SIZE);
        return this.ReadInt16(edit.get().valueOffset());
    }

    public int ReadSparseInt32(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Int32);
        edit.get().endOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE);
        return this.ReadInt32(edit.get().valueOffset());
    }

    public long ReadSparseInt64(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Int64);
        edit.get().endOffset = edit.get().valueOffset() + (Long.SIZE / Byte.SIZE);
        return this.ReadInt64(edit.get().valueOffset());
    }

    public byte ReadSparseInt8(Reference<RowCursor> edit) {
        // TODO: Remove calls to ReadSparsePrimitiveTypeCode once moved to V2 read.
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Int8);
        edit.get().endOffset = edit.get().valueOffset() + (Byte.SIZE / Byte.SIZE);
        return this.ReadInt8(edit.get().valueOffset());
    }

    public MongoDbObjectId ReadSparseMongoDbObjectId(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, MongoDbObjectId);
        edit.get().endOffset = edit.get().valueOffset() + MongoDbObjectId.Size;
        return this.ReadMongoDbObjectId(edit.get().valueOffset()).clone();
    }

    public int ReadSparsePathLen(Layout layout, int offset, Out<Integer> pathLenInBytes,
                                 Out<Integer> pathOffset) {
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        int token = (int)this.read7BitEncodedUInt(offset, tempOut_sizeLenInBytes);
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
        this.readSparsePrimitiveTypeCode(edit, LayoutType.Utf8);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        Utf8Span span = this.ReadString(edit.get().valueOffset(), tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset() + sizeLenInBytes + span.Length;
        return span;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ushort ReadSparseUInt16(ref RowCursor edit)
    public short ReadSparseUInt16(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.UInt16);
        edit.get().endOffset = edit.get().valueOffset() + (Short.SIZE / Byte.SIZE);
        return this.ReadUInt16(edit.get().valueOffset());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal uint ReadSparseUInt32(ref RowCursor edit)
    public int ReadSparseUInt32(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.UInt32);
        edit.get().endOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE);
        return this.ReadUInt32(edit.get().valueOffset());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal ulong ReadSparseUInt64(ref RowCursor edit)
    public long ReadSparseUInt64(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.UInt64);
        edit.get().endOffset = edit.get().valueOffset() + (Long.SIZE / Byte.SIZE);
        return this.ReadUInt64(edit.get().valueOffset());
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal LayoutType ReadSparseTypeCode(int
    // offset)
    public LayoutType readSparseTypeCode(int offset) {
        return LayoutType.FromCode(LayoutCode.forValue(this.ReadUInt8(offset)));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal byte ReadSparseUInt8(ref RowCursor edit)
    public byte ReadSparseUInt8(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.UInt8);
        edit.get().endOffset = edit.get().valueOffset() + 1;
        return this.ReadUInt8(edit.get().valueOffset());
    }

    public UnixDateTime ReadSparseUnixDateTime(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.UnixDateTime);
        edit.get().endOffset = edit.get().valueOffset() + 8;
        return this.ReadUnixDateTime(edit.get().valueOffset()).clone();
    }

    public long ReadSparseVarInt(Reference<RowCursor> edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutType.VarInt);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        long value = this.Read7BitEncodedInt(edit.get().valueOffset(), tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.get().endOffset = edit.get().valueOffset() + sizeLenInBytes;
        return value;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySpan<byte> value, UpdateOptions
    // options)
    public void WriteSparseBinary(Reference<RowCursor> edit, ReadOnlySpan<Byte> value, UpdateOptions options) {
        int len = value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Binary, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Binary, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.WriteBinary(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySequence<byte> value, UpdateOptions
    // options)
    public void WriteSparseBinary(Reference<RowCursor> edit, ReadOnlySequence<Byte> value,
                                  UpdateOptions options) {
        int len = (int)value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Binary, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Binary, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.WriteBinary(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseFloat128(Reference<RowCursor> edit, Float128 value, UpdateOptions options) {
        int numBytes = Float128.Size;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Float128, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float128, TypeArgumentList.EMPTY, metaBytes);
        this.writeFloat128(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + Float128.Size);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void TypedCollectionMoveField(Reference<RowCursor> dstEdit, Reference<RowCursor> srcEdit
        , RowOptions options) {
        int encodedSize = this.sparseComputeSize(srcEdit);
        int numBytes = encodedSize - (srcEdit.get().valueOffset() - srcEdit.get().metaOffset());

        // Insert the field metadata into its new location.
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shiftInsert;
        Out<Integer> tempOut_shiftInsert = new Out<Integer>();
        this.EnsureSparse(dstEdit, srcEdit.get().cellType(), srcEdit.get().cellTypeArgs().clone(), numBytes,
            options, tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shiftInsert);
        shiftInsert = tempOut_shiftInsert.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(dstEdit, srcEdit.get().cellType(), srcEdit.get().cellTypeArgs().clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        if (srcEdit.get().metaOffset() >= dstEdit.get().metaOffset()) {
            srcEdit.get().metaOffset(srcEdit.get().metaOffset() + shiftInsert);
            srcEdit.get().valueOffset(srcEdit.get().valueOffset() + shiftInsert);
        }

        // Copy the value bits from the old location.
        this.buffer.Slice(srcEdit.get().valueOffset(), numBytes).CopyTo(this.buffer.Slice(dstEdit.get().valueOffset()));
        this.length(this.length() + shiftInsert);

        // Delete the old location.
        Out<Integer> tempOut_metaBytes2 = new Out<Integer>();
        Out<Integer> tempOut_spaceNeeded2 = new Out<Integer>();
        int shiftDelete;
        Out<Integer> tempOut_shiftDelete = new Out<Integer>();
        this.EnsureSparse(srcEdit, srcEdit.get().cellType(), srcEdit.get().cellTypeArgs().clone(), numBytes,
            RowOptions.Delete, tempOut_metaBytes2, tempOut_spaceNeeded2, tempOut_shiftDelete);
        shiftDelete = tempOut_shiftDelete.get();
        spaceNeeded = tempOut_spaceNeeded2.get();
        metaBytes = tempOut_metaBytes2.get();

        checkState(shiftDelete < 0);
        this.length(this.length() + shiftDelete);
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
        checkArgument(scope.get().scopeType().isUniqueScope());
        checkArgument(scope.get().index() == 0);
        RowCursor dstEdit;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be
        // converted using the 'Out' helper class unless the method is within the code being modified:
        scope.get().Clone(out dstEdit);
        if (dstEdit.count() <= 1) {
            return Result.Success;
        }

        // Compute Index Elements.
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'stackalloc' in Java:
        UniqueIndexItem item;
        Span<UniqueIndexItem> uniqueIndex = dstEdit.count() < 100 ? stackalloc UniqueIndexItem[dstEdit.count()] :
        new UniqueIndexItem[dstEdit.count()];
        dstEdit.metaOffset(scope.get().valueOffset());
        for (; dstEdit.index() < dstEdit.count(); dstEdit.index(dstEdit.index() + 1)) {
            Reference<RowCursor> tempReference_dstEdit =
                new Reference<RowCursor>(dstEdit);
            this.readSparseMetadata(tempReference_dstEdit);
            dstEdit = tempReference_dstEdit.get();
            Contract.Assert(dstEdit.pathOffset() ==
            default)
                Reference<RowCursor> tempReference_dstEdit2 =
                    new Reference<RowCursor>(dstEdit);
                int elmSize = this.sparseComputeSize(tempReference_dstEdit2);
                dstEdit = tempReference_dstEdit2.get();

                UniqueIndexItem tempVar = new UniqueIndexItem();
                tempVar.code(dstEdit.cellType().LayoutCode);
                tempVar.metaOffset(dstEdit.metaOffset());
                tempVar.valueOffset(dstEdit.valueOffset());
                tempVar.size(elmSize);
                uniqueIndex[dstEdit.index()] = tempVar.clone();

                dstEdit.metaOffset(dstEdit.metaOffset() + elmSize);
        }

        // Create scratch space equal to the sum of the sizes of the scope's values.
        // Implementation Note: theoretically this scratch space could be eliminated by
        // performing the item move operations directly during the Insertion Sort, however,
        // doing so might result in moving the same item multiple times.  Under the assumption
        // that items are relatively large, using scratch space requires each item to be moved
        // AT MOST once.  Given that row buffer memory is likely reused, scratch space is
        // relatively memory efficient.
        int shift = dstEdit.metaOffset() - scope.get().valueOffset();

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
        int metaOffset = scope.get().valueOffset();
        this.ensure(this.length() + shift);
        this.buffer.Slice(metaOffset, this.length() - metaOffset).CopyTo(this.buffer.Slice(metaOffset + shift));
        for (UniqueIndexItem x : uniqueIndex) {
            this.buffer.Slice(x.metaOffset() + shift, x.size()).CopyTo(this.buffer.Slice(metaOffset));
            metaOffset += x.size();
        }

        // Delete the scratch space (if necessary - if it doesn't just fall off the end of the row).
        if (metaOffset != this.length()) {
            this.buffer.Slice(metaOffset + shift, this.length() - metaOffset).CopyTo(this.buffer.Slice(metaOffset));
        }

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
        this.buffer.Slice(this.length(), shift).Fill(0xFF);
        //#endif

        return Result.Success;
    }

    public void WriteSparseInt16(Reference<RowCursor> edit, short value, UpdateOptions options) {
        int numBytes = (Short.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int16, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int16, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt16(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Short.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseInt32(Reference<RowCursor> edit, int value, UpdateOptions options) {
        int numBytes = (Integer.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int32, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int32, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt32(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Integer.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseInt64(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = (Long.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int64, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Int64, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt64(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Long.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    // TODO: DANOBLE: Support MongoDbObjectId values
    //    public void WriteMongoDbObjectId(int offset, MongoDbObjectId value) {
    //        Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempReference_value =
    //            new Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>(value);
    //        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
    //        value = tempReference_value.get();
    //    }

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

    public long ReadVariableUInt(int offset) {
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.Read7BitEncodedUInt(offset, out int _);
        long tempVar = this.read7BitEncodedUInt();
        _ = tempOut__.get();
        return tempVar;
    }

    /**
     * Clears all content from the row. The row is empty after this method.
     */
    public void reset() {
        this.buffer.clear();
        this.resolver = null;
    }

    public void WriteSparseInt8(Reference<RowCursor> edit, byte value, UpdateOptions options) {
        int numBytes = (Byte.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Int8, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, LayoutType.Int8, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt8(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Byte.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseArray(Reference<RowCursor> edit, LayoutScope scopeType, UpdateOptions options,
                                 Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE); // end scope type code.
        TypeArgumentList typeArgs = TypeArgumentList.EMPTY;
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
        this.writeSparseTypeCode(edit.get().valueOffset(), LayoutCode.END_SCOPE);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(edit.get().valueOffset());
        newScope.get().metaOffset(edit.get().valueOffset());
        newScope.get().layout(edit.get().layout());

        this.length(this.length() + shift);
    }

    public void SetBit(int offset, LayoutBit bit) {
        if (bit.getIsInvalid()) {
            return;
        }

        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.buffer[bit.GetOffset(offset)] |= unchecked((byte)(1 << bit.GetBit()));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        this.buffer[bit.offset(offset)] |= (byte)(1 << bit.bit());
    }

    public void WriteSparseString(Reference<RowCursor> edit, Utf8Span value, UpdateOptions options) {
        int len = value.Length;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
        int numBytes = len + RowBuffer.count7BitEncodedUInt(len);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Utf8, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Utf8, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.WriteString(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    /**
     * The length of row in bytes.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public byte[] ToArray()
    public byte[] ToArray() {
        return this.buffer.Slice(0, this.length()).ToArray();
    }

    public void WriteSparseVarInt(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = RowBuffer.Count7BitEncodedInt(value);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.VarInt, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.VarInt, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.write7BitEncodedInt(edit.get().valueOffset(), value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == metaBytes + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteSparseVarUInt(ref RowCursor edit, ulong value, UpdateOptions options)
    public void WriteSparseVarUInt(Reference<RowCursor> edit, long value, UpdateOptions options) {
        int numBytes = RowBuffer.count7BitEncodedUInt(value);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.VarUInt, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.VarUInt, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.write7BitEncodedUInt(edit.get().valueOffset(), value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == metaBytes + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseBool(Reference<RowCursor> edit, boolean value, UpdateOptions options) {
        int numBytes = 0;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.EMPTY,
            numBytes, options, tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.EMPTY,
            metaBytes);
        checkState(spaceNeeded == metaBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void UnsetBit(int offset, LayoutBit bit) {
        checkState(LayoutBit.opNotEquals(bit.clone(), LayoutBit.INVALID));
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'unchecked' in this context:
        //ORIGINAL LINE: this.buffer[bit.GetOffset(offset)] &= unchecked((byte)~(1 << bit.GetBit()));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        this.buffer[bit.offset(offset)] &= (byte)~(1 << bit.bit());
    }

    public void WriteVariableInt(int offset, long value, boolean exists, Out<Integer> shift) {
        int numBytes = RowBuffer.Count7BitEncodedInt(value);
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, true, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.write7BitEncodedInt(offset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == numBytes);
        this.length(this.length() + shift.get());
    }

    public void WriteSparseDateTime(Reference<RowCursor> edit, LocalDateTime value, UpdateOptions options) {
        int numBytes = 8;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.DateTime, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.DateTime, TypeArgumentList.EMPTY, metaBytes);
        this.WriteDateTime(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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

    public void WriteSparseDecimal(Reference<RowCursor> edit, BigDecimal value, UpdateOptions options) {
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        int numBytes = sizeof(BigDecimal);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Decimal, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Decimal, TypeArgumentList.EMPTY, metaBytes);
        this.WriteDecimal(edit.get().valueOffset(), value);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        checkState(spaceNeeded == metaBytes + sizeof(BigDecimal));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void WriteVariableUInt(int offset, ulong value, bool exists, out int shift)
    public void WriteVariableUInt(int offset, long value, boolean exists, Out<Integer> shift) {
        int numBytes = RowBuffer.count7BitEncodedUInt(value);
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, true, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.write7BitEncodedUInt(offset, value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == numBytes);
        this.length(this.length() + shift.get());
    }

    /**
     * Compute the number of bytes necessary to store the unsigned 32-bit integer value using the varuint encoding
     *
     * @param value The value to be encoded
     * @return The number of bytes needed to store the varuint encoding of {@code value}
     */
    public static int count7BitEncodedUInt(long value) {
        checkArgument(0 <= value && value <= 0x00000000FFFFFFFFL, "value: %s", value);
        int i = 0;
        while (value >= 0x80L) {
            i++;
            value >>>= 7;
        }
        i++;
        return i;
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
    public void initLayout(HybridRowVersion version, Layout layout, LayoutResolver resolver) {

        checkNotNull(version, "version");
        checkNotNull(layout, "layout");
        checkNotNull(resolver, "resolver");

        this.writeHeader(new HybridRowHeader(version, layout.schemaId()));
        this.buffer.writeZero(layout.size());
        this.resolver = resolver;
    }

    /**
     * Rotates the sign bit of a two's complement value to the least significant bit
     *
     * @param value A signed value.
     * @return An unsigned value encoding the same value but with the sign bit in the LSB.
     * <p>
     * Moves the signed bit of a two's complement value to the least significant bit (LSB) by:
     * <list type="number">
     * <item>
     * <description>If negative, take the two's complement.</description>
     * </item><item>
     * <description>Left shift the value by 1 bit.</description>
     * </item><item>
     * <description>If negative, set the LSB to 1.</description>
     * </item>
     * </list>
     */
    public static long rotateSignToLsb(long value) {
        boolean isNegative = value < 0;
        long unsignedValue = value;
        unsignedValue = isNegative ? ((~unsignedValue + 1) << 1) + 1 : unsignedValue << 1;
        return unsignedValue;
    }

    /**
     * Undoes the rotation introduced by {@link #rotateSignToLsb}.
     *
     * @param unsignedValue An unsigned value with the sign bit in the LSB.
     * @return A signed two's complement value encoding the same value.
     */
    public static long rotateSignToMsb(long unsignedValue) {
        boolean isNegative = unsignedValue % 2 != 0;
        return isNegative ? (~(unsignedValue >>> 1) + 1) | 0x8000000000000000L : unsignedValue >>> 1;
    }

    /**
     * Produce a new scope from the current iterator position.
     *
     * @param edit      An initialized iterator pointing at a scope.
     * @param immutable True if the new scope should be marked immutable (read-only).
     * @return A new scope beginning at the current iterator position.
     */
    public RowCursor sparseIteratorReadScope(Reference<RowCursor> edit, boolean immutable) {

        LayoutScope scopeType = edit.get().cellType() instanceof LayoutScope ? (LayoutScope) edit.get().cellType() : null;

        if (scopeType instanceof LayoutObject || scopeType instanceof LayoutArray) {
            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.get().cellTypeArgs())
                .start(edit.get().valueOffset())
                .valueOffset(edit.get().valueOffset())
                .metaOffset(edit.get().valueOffset())
                .layout(edit.get().layout())
                .immutable(immutable);
        }

        if (scopeType instanceof LayoutTypedArray || scopeType instanceof LayoutTypedSet || scopeType instanceof LayoutTypedMap) {

            final int valueOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE); // Point after the Size

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.get().cellTypeArgs())
                .start(edit.get().valueOffset())
                .valueOffset(valueOffset)
                .metaOffset(valueOffset)
                .layout(edit.get().layout())
                .immutable(immutable)
                .count(this.ReadUInt32(edit.get().valueOffset()));
        }

        if (scopeType instanceof LayoutTypedTuple || scopeType instanceof LayoutTuple || scopeType instanceof LayoutTagged || scopeType instanceof LayoutTagged2) {

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.get().cellTypeArgs())
                .start(edit.get().valueOffset())
                .valueOffset(edit.get().valueOffset())
                .metaOffset(edit.get().valueOffset())
                .layout(edit.get().layout())
                .immutable(immutable)
                .count(edit.get().cellTypeArgs().count());
        }

        if (scopeType instanceof LayoutNullable) {

            boolean hasValue = this.ReadInt8(edit.get().valueOffset()) != 0;

            if (hasValue) {

                // Start at the T so it can be read.
                final int valueOffset = edit.get().valueOffset() + 1;

                return new RowCursor()
                    .scopeType(scopeType)
                    .scopeTypeArgs(edit.get().cellTypeArgs())
                    .start(edit.get().valueOffset())
                    .valueOffset(valueOffset)
                    .metaOffset(valueOffset)
                    .layout(edit.get().layout())
                    .immutable(immutable)
                    .count(2)
                    .index(1);
            } else {

                // Start at the end of the scope, instead of at the T, so the T will be skipped.
                final TypeArgument typeArg = edit.get().cellTypeArgs().get(0);
                final int valueOffset = edit.get().valueOffset() + 1 + this.countDefaultValue(typeArg.type(),
                    typeArg.typeArgs());

                return new RowCursor()
                    .scopeType(scopeType)
                    .scopeTypeArgs(edit.get().cellTypeArgs())
                    .start(edit.get().valueOffset())
                    .valueOffset(valueOffset)
                    .metaOffset(valueOffset)
                    .layout(edit.get().layout())
                    .immutable(immutable)
                    .count(2)
                    .index(2);
            }
        }

        if (scopeType instanceof LayoutUDT) {

            final Layout udt = this.resolver.resolve(edit.get().cellTypeArgs().schemaId());
            final int valueOffset = this.computeVariableValueOffset(udt, edit.get().valueOffset(), udt.numVariable());

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.get().cellTypeArgs())
                .start(edit.get().valueOffset())
                .valueOffset(valueOffset)
                .metaOffset(valueOffset)
                .layout(udt)
                .immutable(immutable);
        }

        throw new IllegalStateException(lenientFormat("Not a scope type: %s", scopeType));
    }

    public void WriteSparseFloat64(Reference<RowCursor> edit, double value, UpdateOptions options) {
        int numBytes = (Double.SIZE / Byte.SIZE);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Float64, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float64, TypeArgumentList.EMPTY, metaBytes);
        this.WriteFloat64(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Double.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseGuid(Reference<RowCursor> edit, UUID value, UpdateOptions options) {
        int numBytes = 16;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Guid, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Guid, TypeArgumentList.EMPTY, metaBytes);
        this.WriteGuid(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + 16);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public int write7BitEncodedInt(int offset, long value) {
        return this.write7BitEncodedUInt(offset, RowBuffer.rotateSignToLsb(value));
    }

    /**
     * Sets the specified 64-bit integer at the current {@link RowBuffer position} as a 7-bit encoded 32-bit value
     * <p>
     * The 64-bit integer value is written 7-bits at a time. The high bit of the byte, when set, indicates there are
     * more bytes. An {@link IllegalArgumentException} is thrown, if the specified 64-bit integer value is outside
     * the range of an unsigned 32-bit integer, [0, 0x00000000FFFFFFFFL].
     *
     * @param ignored
     * @param value   a 64-bit integer constrained to the range of an unsigned 32-bit integer, [0, 0x00000000FFFFFFFFL]
     * @return The number of bytes written
     */
    public int write7BitEncodedUInt(final int ignored, final long value) {
        checkArgument(0 <= value && value <= 0x00000000FFFFFFFFL);
        long n = value;
        int i = 0;
        while (n >= 0x80L) {
            this.buffer.writeByte((byte) (n | 0x80L));
            n >>>= 7;
        }
        this.buffer.writeByte((byte) n);
        return i;
    }

    public void writeFloat128(int offset, Float128 value) {
        this.buffer.writeLongLE(value.low());
        this.buffer.writeLongLE(value.high());
    }

    public void writeHeader(HybridRowHeader value) {
        this.buffer.writeByte(value.version().value());
        this.buffer.writeIntLE(value.schemaId().value());
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
        this.EnsureSparse(edit, MongoDbObjectId, TypeArgumentList.EMPTY, numBytes, options,
            tempOut_metaBytes, tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, MongoDbObjectId, TypeArgumentList.EMPTY, metaBytes);
        this.WriteMongoDbObjectId(edit.get().valueOffset(), value.clone());
        checkState(spaceNeeded == metaBytes + MongoDbObjectId.Size);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseNull(Reference<RowCursor> edit, NullValue value, UpdateOptions options) {
        int numBytes = 0;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.Null, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Null, TypeArgumentList.EMPTY, metaBytes);
        checkState(spaceNeeded == metaBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseObject(Reference<RowCursor> edit, LayoutScope scopeType, UpdateOptions options,
                                  Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE); // end scope type code.
        TypeArgumentList typeArgs = TypeArgumentList.EMPTY;
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
        this.WriteSparseMetadata(edit, scopeType, TypeArgumentList.EMPTY, metaBytes);
        this.writeSparseTypeCode(edit.get().valueOffset(), LayoutCode.END_SCOPE);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(TypeArgumentList.EMPTY);
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(edit.get().valueOffset());
        newScope.get().metaOffset(edit.get().valueOffset());
        newScope.get().layout(edit.get().layout());

        this.length(this.length() + shift);
    }

    public void writeInt16(final int ignored, final short value) {
        this.buffer.writeShortLE(value);
    }

    public void WriteSparseTuple(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs
        , UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = (LayoutCode.SIZE / Byte.SIZE) * (1 + typeArgs.count()); // nulls for each element.
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
        int valueOffset = edit.get().valueOffset();
        for (int i = 0; i < typeArgs.count(); i++) {
            this.writeSparseTypeCode(valueOffset, LayoutCode.NULL);
            valueOffset += (LayoutCode.SIZE / Byte.SIZE);
        }

        this.writeSparseTypeCode(valueOffset, LayoutCode.END_SCOPE);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(edit.get().valueOffset());
        newScope.get().metaOffset(edit.get().valueOffset());
        newScope.get().layout(edit.get().layout());
        newScope.get().count(typeArgs.count());

        this.length(this.length() + shift);
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
        this.buffer.Slice(edit.get().valueOffset(), udt.size()).Fill(0);

        // Write scope terminator.
        int valueOffset = edit.get().valueOffset() + udt.size();
        this.writeSparseTypeCode(valueOffset, LayoutCode.END_SCOPE);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(valueOffset);
        newScope.get().metaOffset(valueOffset);
        newScope.get().layout(udt);

        this.length(this.length() + shift);
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
        this.EnsureSparse(edit, LayoutType.UInt16, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt16, TypeArgumentList.EMPTY, metaBytes);
        this.WriteUInt16(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Short.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.EnsureSparse(edit, LayoutType.UInt32, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt32, TypeArgumentList.EMPTY, metaBytes);
        this.WriteUInt32(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Integer.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.EnsureSparse(edit, LayoutType.UInt64, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt64, TypeArgumentList.EMPTY, metaBytes);
        this.WriteUInt64(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Long.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.EnsureSparse(edit, LayoutType.UInt8, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.UInt8, TypeArgumentList.EMPTY, metaBytes);
        this.WriteUInt8(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + 1);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseUnixDateTime(Reference<RowCursor> edit, UnixDateTime value, UpdateOptions options) {
        int numBytes = 8;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutType.UnixDateTime, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes
            , tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();

        this.WriteSparseMetadata(edit, LayoutType.UnixDateTime, TypeArgumentList.EMPTY, metaBytes);
        this.WriteUnixDateTime(edit.get().valueOffset(), value.clone());
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal void WriteSparseTypeCode(int offset,
    // LayoutCode code)
    public void writeSparseTypeCode(int offset, LayoutCode code) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.WriteUInt8(offset, (byte)code);
        this.WriteUInt8(offset, (byte) code.value());
    }

    public void writeInt32(final int ignored, final int value) {
        this.buffer.writeIntLE(value);
    }

    public void writeInt64(final int ignored, final long value) {
        this.buffer.writeLongLE(value);
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
        this.WriteUInt32(edit.get().valueOffset(), 0);
        int valueOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(valueOffset);
        newScope.get().metaOffset(valueOffset);
        newScope.get().layout(edit.get().layout());

        this.length(this.length() + shift);
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
        this.WriteUInt32(edit.get().valueOffset(), 0);
        int valueOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(valueOffset);
        newScope.get().metaOffset(valueOffset);
        newScope.get().layout(edit.get().layout());

        this.length(this.length() + shift);
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
        this.WriteUInt32(edit.get().valueOffset(), 0);
        int valueOffset = edit.get().valueOffset() + (Integer.SIZE / Byte.SIZE); // Point after the Size
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(valueOffset);
        newScope.get().metaOffset(valueOffset);
        newScope.get().layout(edit.get().layout());

        this.length(this.length() + shift);
    }

    public void WriteTypedTuple(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                                UpdateOptions options, Out<RowCursor> newScope) {
        int numBytes = this.countDefaultValue(scopeType, typeArgs.clone());
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
        int numWritten = this.WriteDefaultValue(edit.get().valueOffset(), scopeType, typeArgs.clone());
        checkState(numBytes == numWritten);
        checkState(spaceNeeded == metaBytes + numBytes);
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs.clone());
        newScope.get().start(edit.get().valueOffset());
        newScope.get().valueOffset(edit.get().valueOffset());
        newScope.get().metaOffset(edit.get().valueOffset());
        newScope.get().layout(edit.get().layout());
        newScope.get().count(typeArgs.count());

        this.length(this.length() + shift);
        Reference<RowBuffer> tempReference_this =
            new Reference<RowBuffer>(this);
        RowCursors.moveNext(newScope.get().clone(), tempReference_this);
        this = tempReference_this.get();
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
        this.length(this.length() + shift.get());
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
        this.length(this.length() + shift.get());
    }

    public RowBuffer clone() {
        RowBuffer varCopy = new RowBuffer();

        varCopy.allocator = this.allocator;
        varCopy.buffer = this.buffer;
        varCopy.resolver = this.resolver;
        varCopy.length(this.length());

        return varCopy;
    }

    /**
     * Delete the sparse field at the indicated path.
     *
     * @param edit The field to delete.
     */
    public void deleteSparse(RowCursor edit) {
        // If the field doesn't exist, then nothing to do.
        if (!edit.exists()) {
            return;
        }

        int numBytes = 0;
        int _;
        Out<Integer> tempOut__ = new Out<Integer>();
        int _;
        Out<Integer> tempOut__2 = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, edit.get().cellType(), edit.get().cellTypeArgs().clone(), numBytes,
            RowOptions.Delete, tempOut__, tempOut__2, tempOut_shift);
        shift = tempOut_shift.get();
        _ = tempOut__2.get();
        _ = tempOut__.get();
        this.length(this.length() + shift);
    }

    /**
     * Copies the content of the buffer into the target stream.
     */
    public void WriteTo(OutputStream stream) {
        stream.Write(this.buffer.Slice(0, this.length()));
    }

    /**
     * The root header for the row.
     */
    public HybridRowHeader header() {
        return this.readHeader();
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
    public int computeVariableValueOffset(Layout layout, int scopeOffset, int varIndex) {
        if (layout == null) {
            return scopeOffset;
        }

        int index = layout.numFixed() + varIndex;
        ReadOnlySpan<LayoutColumn> columns = layout.columns();
        checkState(index <= columns.Length);
        int offset = scopeOffset + layout.size();
        for (int i = layout.numFixed(); i < index; i++) {
            LayoutColumn col = columns[i];
            if (this.readBit(scopeOffset, col.getNullBit().clone())) {
                int lengthSizeInBytes;
                Out<Integer> tempOut_lengthSizeInBytes = new Out<Integer>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ulong valueSizeInBytes = this.Read7BitEncodedUInt(offset, out int lengthSizeInBytes);
                long valueSizeInBytes = this.read7BitEncodedUInt(offset, tempOut_lengthSizeInBytes);
                lengthSizeInBytes = tempOut_lengthSizeInBytes.get();
                if (col.type().getIsVarint()) {
                    offset += lengthSizeInBytes;
                } else {
                    offset += (int) valueSizeInBytes + lengthSizeInBytes;
                }
            }
        }

        return offset;
    }

    public HybridRowHeader readHeader() {
        HybridRowVersion version = HybridRowVersion.from(this.buffer.readByte());
        SchemaId schemaId = SchemaId.from(this.buffer.readIntLE());
        return new HybridRowHeader(version, schemaId);
    }

    /**
     * The length of this {@link RowBuffer} in bytes.
     */
    public int length() {
        return this.buffer.readerIndex() + this.buffer.readableBytes();
    }

    /**
     * The resolver for UDTs.
     */
    public LayoutResolver resolver() {
        return this.resolver;
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

    public NullValue readSparseNull(@Nonnull RowCursor edit) {

        checkNotNull(edit);

        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.Null);
        edit.endOffset(edit.valueOffset());

        return NullValue.Default;
    }

    public Utf8String readSparsePath(RowCursor edit) {

        final Optional<Utf8String> path = edit.layout().tokenizer().tryFindString(edit.longValue().pathToken);

        if (path.isPresent()) {
            return path.get();
        }

        int numBytes = edit.pathToken() - edit.layout().tokenizer().count();
        return Utf8String.unsafeFromUtf8BytesNoValidation(this.buffer.Slice(edit.pathOffset(), numBytes));
    }

    public long readSparseVarUInt(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.VarUInt);
        int sizeLenInBytes;
        Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
        long value = this.read7BitEncodedUInt(edit.valueOffset(), tempOut_sizeLenInBytes);
        sizeLenInBytes = tempOut_sizeLenInBytes.get();
        edit.endOffset(edit.valueOffset() + sizeLenInBytes);

        return value;
    }

    public void writeInt8(final int ignored, final byte value) {
        this.buffer.writeByte(value);
    }

    public void WriteVariableString(int offset, Utf8Span value, boolean exists, Out<Integer> shift) {
        int numBytes = value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.WriteString(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length(this.length() + shift.get());
    }

    public void writeSparseFloat32(@Nonnull RowCursor edit, float value, @Nonnull UpdateOptions options) {

        int numBytes = (Float.SIZE / Byte.SIZE);
        int metaBytes;

        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.ensureSparse(edit, LayoutTypes.FLOAT_32, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
         tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.WriteSparseMetadata(edit, LayoutType.Float32, TypeArgumentList.EMPTY, metaBytes);
        this.WriteFloat32(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Float.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
    public boolean sparseIteratorMoveNext(RowCursor edit) {

        if (edit.cellType() != null) {
            // Move to the next element of an indexed scope.
            if (edit.scopeType().isIndexedScope()) {
                edit.index(edit.index() + 1);
            }

            // Skip forward to the end of the current value.
            if (edit.endOffset() != 0) {
                edit.metaOffset(edit.endOffset());
                edit.endOffset(0);
            } else {
                edit.metaOffset(edit.metaOffset() + this.sparseComputeSize(edit));
            }
        }

        // Check if reached end of buffer

        if (edit.metaOffset() < this.length()) {

            // Check if reached end of sized scope.

            if (!edit.scopeType().isSizedScope() || (edit.index() != edit.count())) {

                this.readSparseMetadata(edit);

                // Check if reached end of sparse scope.
                if (!(edit.cellType() instanceof LayoutEndScope)) {
                    edit.exists(true);
                    return true;
                }
            }
        }

        edit.cellType(LayoutTypes.EndScope);
        edit.exists(false);
        edit.valueOffset(edit.metaOffset());
        return false;
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
        if (left.cellType().LayoutCode.getValue() < right.cellType().LayoutCode.getValue()) {
            return -1;
        }

        if (left.cellType() == right.cellType()) {
            if (leftLen < rightLen) {
                return -1;
            }

            if (leftLen == rightLen) {
                return this.buffer.Slice(left.valueOffset(), leftLen).SequenceCompareTo(this.buffer.Slice(right.valueOffset(), rightLen));
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
        LayoutTypedTuple leftScopeType = left.cellType() instanceof LayoutTypedTuple ? (LayoutTypedTuple) left.cellType() :
            null;
        LayoutTypedTuple rightScopeType = right.cellType() instanceof LayoutTypedTuple ?
            (LayoutTypedTuple) right.cellType() : null;
        checkArgument(leftScopeType != null);
        checkArgument(rightScopeType != null);
        checkArgument(left.cellTypeArgs().count() == 2);
        checkArgument(left.cellTypeArgs().equals(right.cellTypeArgs().clone()));

        RowCursor leftKey = new RowCursor();
        leftKey.layout(left.layout());
        leftKey.scopeType(leftScopeType);
        leftKey.scopeTypeArgs(left.cellTypeArgs().clone());
        leftKey.start(left.valueOffset());
        leftKey.metaOffset(left.valueOffset());
        leftKey.index(0);

        Reference<RowCursor> tempReference_leftKey =
            new Reference<RowCursor>(leftKey);
        this.readSparseMetadata(tempReference_leftKey);
        leftKey = tempReference_leftKey.get();
        checkState(leftKey.pathOffset() == 0);
        Reference<RowCursor> tempReference_leftKey2 =
            new Reference<RowCursor>(leftKey);
        int leftKeyLen =
            this.sparseComputeSize(tempReference_leftKey2) - (leftKey.valueOffset() - leftKey.metaOffset());
        leftKey = tempReference_leftKey2.get();

        RowCursor rightKey = new RowCursor();
        rightKey.layout(right.layout());
        rightKey.scopeType(rightScopeType);
        rightKey.scopeTypeArgs(right.cellTypeArgs().clone());
        rightKey.start(right.valueOffset());
        rightKey.metaOffset(right.valueOffset());
        rightKey.index(0);

        Reference<RowCursor> tempReference_rightKey =
            new Reference<RowCursor>(rightKey);
        this.readSparseMetadata(tempReference_rightKey);
        rightKey = tempReference_rightKey.get();
        checkState(rightKey.pathOffset() == 0);
        Reference<RowCursor> tempReference_rightKey2 =
            new Reference<RowCursor>(rightKey);
        int rightKeyLen = this.sparseComputeSize(tempReference_rightKey2) - (rightKey.valueOffset() - rightKey.metaOffset());
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
        return RowBuffer.count7BitEncodedUInt(RowBuffer.rotateSignToLsb(value));
    }

    private static int CountSparsePath(Reference<RowCursor> edit) {

        if (!edit.get().writePathToken().isNull()) {
            return edit.get().writePathToken().varint().length;
        }

        Optional<StringToken> token = edit.get().layout().tokenizer().findToken(edit.get().writePath());

        if (token.isPresent()) {
            edit.get().writePathToken(token.get());
            return token.get().varint().length;
        }

        int numBytes = edit.get().writePath().toUtf8().length();
        int sizeLenInBytes = RowBuffer.count7BitEncodedUInt((long) (edit.get().layout().tokenizer().count() + numBytes));

        return sizeLenInBytes + numBytes;
    }

    private void ensure(int size) {
        this.buffer.ensureWritable(size);
    }

    private void EnsureVariable(int offset, boolean isVarint, int numBytes, boolean exists,
                                Out<Integer> spaceNeeded, Out<Integer> shift) {
        int spaceAvailable = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong existingValueBytes = 0;
        long existingValueBytes = 0;
        if (exists) {
            Out<Integer> tempOut_spaceAvailable = new Out<Integer>();
            existingValueBytes = this.read7BitEncodedUInt(offset, tempOut_spaceAvailable);
            spaceAvailable = tempOut_spaceAvailable.get();
        }

        if (isVarint) {
            spaceNeeded.setAndGet(numBytes);
        } else {
            spaceAvailable += (int)existingValueBytes; // size already in spaceAvailable
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: spaceNeeded = numBytes + RowBuffer.Count7BitEncodedUInt((ulong)numBytes);
            spaceNeeded.setAndGet(numBytes + RowBuffer.count7BitEncodedUInt(numBytes));
        }

        shift.setAndGet(spaceNeeded.get() - spaceAvailable);
        if (shift.get() > 0) {
            this.ensure(this.length() + shift.get());
            this.buffer.Slice(offset + spaceAvailable, this.length() - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded.get()));
        } else if (shift.get() < 0) {
            this.buffer.Slice(offset + spaceAvailable, this.length() - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded.get()));
        }
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private int WriteBinary(int offset, ReadOnlySpan<byte> value)
    private int WriteBinary(int offset, ReadOnlySpan<Byte> value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.write7BitEncodedUInt(offset, (long) value.Length);
        value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
    }

    /**
     * Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
     * with the associated layout and rowVersion.
     *
     * @return true if the serialization succeeded. false if the input stream was corrupted.
     */
    private boolean InitReadFrom(HybridRowVersion rowVersion) {
        HybridRowHeader header = this.readHeader(0).clone();
        Layout layout = this.resolver.resolve(header.schemaId().clone());
        checkState(SchemaId.opEquals(header.schemaId().clone(), layout.schemaId().clone()));
        return (header.getVersion() == rowVersion) && (HybridRowHeader.SIZE + layout.size() <= this.length());
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private int WriteBinary(int offset, ReadOnlySequence<byte> value)
    private int WriteBinary(int offset, ReadOnlySequence<Byte> value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.write7BitEncodedUInt(offset, (long) value.Length);
        value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
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
            leftEdit.cellType = LayoutType.FromCode(x.code());
            leftEdit.metaOffset(x.metaOffset());
            leftEdit.valueOffset(x.valueOffset());
            int leftBytes = x.size() - (x.valueOffset() - x.metaOffset());

            // Walk backwards searching for the insertion point for the item as position i.
            int j;
            for (j = i - 1; j >= 0; j--) {
                UniqueIndexItem y = uniqueIndex[j];
                rightEdit.cellType = LayoutType.FromCode(y.code());
                rightEdit.metaOffset(y.metaOffset());
                rightEdit.valueOffset(y.valueOffset());

                int cmp;
                if (scope.get().scopeType() instanceof LayoutTypedMap) {
                    cmp = this.CompareKeyValueFieldValue(leftEdit.clone(), rightEdit.clone());
                } else {
                    int rightBytes = y.size() - (y.valueOffset() - y.metaOffset());
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
        int numBytes = (int) this.read7BitEncodedUInt(offset, sizeLenInBytes);
        return this.buffer.Slice(offset + sizeLenInBytes.get(), numBytes);
    }

    private int WriteDefaultValue(int offset, LayoutType code, TypeArgumentList typeArgs) {
        // JTHTODO: convert to a virtual?
        switch (code) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNull _:
            case LayoutNull
                _:
                this.writeSparseTypeCode(offset, code.LayoutCode);
                return 1;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutBoolean _:
            case LayoutBoolean
                _:
                this.writeSparseTypeCode(offset, LayoutCode.BOOLEAN_FALSE);
                return 1;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt8 _:
            case LayoutInt8
                _:
                this.writeInt8(offset, (byte)0);
                return LayoutType.Int8.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt16 _:
            case LayoutInt16
                _:
                this.writeInt16(offset, (short)0);
                return LayoutType.Int16.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt32 _:
            case LayoutInt32
                _:
                this.writeInt32(offset, 0);
                return LayoutType.Int32.Size;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutInt64 _:
            case LayoutInt64
                _:
                this.writeInt64(offset, 0);
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
                this.writeFloat128(offset, null);
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
            return this.write7BitEncodedUInt(offset, 0);

                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutObject _:
            case LayoutObject
                _:
                // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
                //ORIGINAL LINE: case LayoutArray _:
                case LayoutArray _:

            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
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
                for (int i = 0; i < typeArgs.count(); i++) {
                    this.writeSparseTypeCode(offset, LayoutCode.NULL);
                }

                this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
                return (LayoutCode.SIZE / Byte.SIZE) + ((LayoutCode.SIZE / Byte.SIZE) * typeArgs.count());

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
                    sum += this.WriteDefaultValue(offset + sum, arg.type(), arg.typeArgs().clone());
                }

                return sum;

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutNullable _:
            case LayoutNullable
                _:

                // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
                this.writeInt8(offset, (byte)0);
                return 1 + this.WriteDefaultValue(offset + 1, typeArgs.get(0).type(), typeArgs.get(0).typeArgs().clone());

            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case LayoutUDT _:
            case LayoutUDT
                _:

                // Clear all presence bits.
                Layout udt = this.resolver.resolve(typeArgs.schemaId().clone());
                this.buffer.Slice(offset, udt.getSize()).Fill(0);

                // Write scope terminator.
                this.writeSparseTypeCode(offset + udt.getSize(), LayoutCode.END_SCOPE);
                return udt.getSize() + (LayoutCode.SIZE / Byte.SIZE);

            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
                return 0;
        }
    }

    private void WriteSparseMetadata(Reference<RowCursor> edit, LayoutType cellType,
                                     TypeArgumentList typeArgs, int metaBytes) {
        int metaOffset = edit.get().metaOffset();
        if (!edit.get().scopeType().hasImplicitTypeCode(edit)) {
            Reference<RowBuffer> tempReference_this =
                new Reference<RowBuffer>(this);
            metaOffset += cellType.writeTypeArgument(tempReference_this, metaOffset, typeArgs.clone());
            this = tempReference_this.get();
        }

        this.WriteSparsePath(edit, metaOffset);
        edit.get().valueOffset(edit.get().metaOffset() + metaBytes);
        checkState(edit.get().valueOffset() == edit.get().metaOffset() + metaBytes);
    }

    private Utf8Span ReadString(int offset, Out<Integer> sizeLenInBytes) {
        int numBytes = (int) this.read7BitEncodedUInt(offset, sizeLenInBytes);
        return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(offset + sizeLenInBytes.get(), numBytes));
    }

    private void WriteSparsePath(Reference<RowCursor> edit, int offset) {
        // Some scopes don't encode paths, therefore the cost is always zero.
        if (edit.get().scopeType().isIndexedScope()) {
            edit.get().pathToken = 0;
            edit.get().pathOffset = 0;
            return;
        }

        StringToken _;
        Out<StringToken> tempOut__ =
            new Out<StringToken>();
        checkState(!edit.get().layout().getTokenizer().TryFindToken(edit.get().writePath(), tempOut__) || !edit.get().writePathToken().isNull());
        _ = tempOut__.get();
        if (!edit.get().writePathToken().isNull()) {
            edit.get().writePathToken().varint().CopyTo(this.buffer.Slice(offset));
            edit.get().pathToken = edit.get().intValue().writePathToken.Id;
            edit.get().pathOffset = offset;
        } else {
            // TODO: It would be better if we could avoid allocating here when the path is UTF16.
            Utf8Span span = edit.get().writePath().ToUtf8String();
            edit.get().pathToken = edit.get().layout().getTokenizer().getCount() + span.Length;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)edit.pathToken);
            int sizeLenInBytes = this.write7BitEncodedUInt(offset, edit.get().longValue().pathToken);
            edit.get().pathOffset = offset + sizeLenInBytes;
            span.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        }
    }

    private int WriteString(int offset, Utf8Span value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.write7BitEncodedUInt(offset, (long) value.Length);
        value.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
    }

    /**
     * Return the size (in bytes) of the default sparse value for the type.
     *
     * @param code     The type of the default value.
     * @param typeArgs
     */
    private int countDefaultValue(LayoutType code, TypeArgumentList typeArgs) {

        // TODO: JTH: convert to a virtual?

        if (code instanceof LayoutNull || code instanceof LayoutBoolean) {
            return 1;
        }
        if (code instanceof LayoutInt8) {
            return LayoutTypes.INT_8.size();
        }
        if (code instanceof LayoutInt16) {
            return LayoutTypes.INT_16.size();
        }
        if (code instanceof LayoutInt32) {
            return LayoutTypes.INT_32.size();
        }
        if (code instanceof LayoutInt64) {
            return LayoutTypes.INT_64.size();
        }
        if (code instanceof LayoutUInt8) {
            return LayoutTypes.UINT_8.size();
        }
        if (code instanceof LayoutUInt16) {
            return LayoutTypes.UINT_16.size();
        }
        if (code instanceof LayoutUInt32) {
            return LayoutTypes.UINT_32.size();
        }
        if (code instanceof LayoutUInt64) {
            return LayoutTypes.UINT_64.size();
        }
        if (code instanceof LayoutFloat32) {
            return LayoutTypes.FLOAT_32.size();
        }
        if (code instanceof LayoutFloat64) {
            return LayoutTypes.FLOAT_64.size();
        }
        if (code instanceof LayoutFloat128) {
            return LayoutTypes.FLOAT_128.size();
        }
        if (code instanceof LayoutDecimal) {
            return LayoutTypes.DECIMAL.size();
        }
        if (code instanceof LayoutDateTime) {
            return LayoutTypes.DATE_TIME.size();
        }
        if (code instanceof LayoutUnixDateTime) {
            return LayoutTypes.UNIX_DATE_TIME.size();
        }
        if (code instanceof LayoutGuid) {
            return LayoutTypes.GUID.size();
        }
        if (code instanceof LayoutMongoDbObjectId) {
            // return MongoDbObjectId.size();
            throw new UnsupportedOperationException();
        }
        if (code instanceof LayoutUtf8 || code instanceof LayoutBinary || code instanceof LayoutVarInt || code instanceof LayoutVarUInt) {
            // Variable length types preceded by their varuint size take 1 byte for a size of 0.
            return 1;
        }
        if (code instanceof LayoutObject || code instanceof LayoutArray) {
            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            return (LayoutCode.SIZE / Byte.SIZE);
        }
        if (code instanceof LayoutTypedArray || code instanceof LayoutTypedSet || code instanceof LayoutTypedMap) {
            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            return (Integer.SIZE / Byte.SIZE);
        }
        if (code instanceof LayoutTuple) {
            // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
            return (LayoutCode.SIZE / Byte.SIZE) + ((LayoutCode.SIZE / Byte.SIZE) * typeArgs.count());
        }
        if (code instanceof LayoutTypedTuple || code instanceof LayoutTagged || code instanceof LayoutTagged2) {
            // Fixed arity typed collections take the sum of the default values of each element.  The scope size is
            // implied by the arity.
            int sum = 0;
            for (TypeArgument arg : typeArgs) {
                sum += this.countDefaultValue(arg.type(), arg.typeArgs().clone());
            }
            return sum;
        }
        if (code instanceof LayoutNullable) {
            // Nullables take the default values of the value plus null. The scope size is implied by the arity.
            return 1 + this.countDefaultValue(typeArgs.get(0).type(), typeArgs.get(0).typeArgs());
        }
        if (code instanceof LayoutUDT) {
            Layout udt = this.resolver.resolve(typeArgs.schemaId());
            return udt.size() + (LayoutCode.SIZE / Byte.SIZE);
        }
        throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the current value.
     *  @param edit        The prepared edit indicating where and in what context the current write will
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
     */
    private void ensureSparse(
        RowCursor edit, LayoutType cellType, TypeArgumentList typeArgs, int numBytes, RowOptions options,
        Out<Integer> metaBytes, Out<Integer> spaceNeeded, Out<Integer> shift
    ) {

        int metaOffset = edit.metaOffset();
        int spaceAvailable = 0;

        // Compute the metadata offsets
        if (edit.scopeType().hasImplicitTypeCode(edit)) {
            metaBytes.setAndGet(0);
        } else {
            metaBytes.setAndGet(cellType.CountTypeArgument(typeArgs));
        }

        if (!edit.scopeType().isIndexedScope()) {
            checkState(edit.writePath() != null);
            int pathLenInBytes = RowBuffer.CountSparsePath(edit);
            metaBytes.setAndGet(metaBytes.get() + pathLenInBytes);
        }

        if (edit.exists()) {
            // Compute value offset for existing value to be overwritten.
            spaceAvailable = this.sparseComputeSize(edit);
        }

        spaceNeeded.setAndGet(options == RowOptions.Delete ? 0 : metaBytes.get() + numBytes);
        shift.setAndGet(spaceNeeded.get() - spaceAvailable);
        if (shift.get() > 0) {
            this.ensure(this.length() + shift.get());
        }

        this.buffer.Slice(metaOffset + spaceAvailable, this.length() - (metaOffset + spaceAvailable)).CopyTo(this.buffer.Slice(metaOffset + spaceNeeded.get()));

        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        if (shift.get() < 0) {
            // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
            this.buffer.Slice(this.length() + shift.get(), -shift.get()).Fill(0xFF);
        }
        //#endif

        // Update the stored size (fixed arity scopes don't store the size because it is implied by the type args).
        if (edit.scopeType().isSizedScope() && !edit.scopeType().isFixedArity()) {
            if ((options == RowOptions.Insert) || (options == RowOptions.InsertAt) || ((options == RowOptions.Upsert) && !edit.get().exists())) {
                // Add one to the current scope count.
                checkState(!edit.exists());
                this.IncrementUInt32(edit.start(), 1);
                edit.count(edit.count() + 1);
            } else if ((options == RowOptions.Delete) && edit.exists()) {
                // Subtract one from the current scope count.
                checkState(this.ReadUInt32(edit.start()) > 0);
                this.DecrementUInt32(edit.start(), 1);
                edit.count(edit.count() - 1);
            }
        }

        if (options == RowOptions.Delete) {
            edit.cellType(null);
            edit.cellTypeArgs(null);
            edit.exists(false);
        } else {
            edit.cellType(cellType);
            edit.cellTypeArgs(typeArgs);
            edit.exists(true);
        }
    }
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
            case NULL:
                checkState(LayoutType.Null.Size == 0);
                return metaBytes;

            case BOOLEAN:
            case BOOLEAN_FALSE:
                checkState(LayoutType.Boolean.Size == 0);
                return metaBytes;

            case INT_8:
                return metaBytes + LayoutType.Int8.Size;

            case INT_16:
                return metaBytes + LayoutType.Int16.Size;

            case INT_32:
                return metaBytes + LayoutType.Int32.Size;

            case INT_64:
                return metaBytes + LayoutType.Int64.Size;

            case UINT_8:
                return metaBytes + LayoutType.UInt8.Size;

            case UINT_16:
                return metaBytes + LayoutType.UInt16.Size;

            case UINT_32:
                return metaBytes + LayoutType.UInt32.Size;

            case UINT_64:
                return metaBytes + LayoutType.UInt64.Size;

            case FLOAT_32:
                return metaBytes + LayoutType.Float32.Size;

            case FLOAT_64:
                return metaBytes + LayoutType.Float64.Size;

            case FLOAT_128:
                return metaBytes + LayoutType.Float128.Size;

            case DECIMAL:
                return metaBytes + LayoutType.Decimal.Size;

            case DATE_TIME:
                return metaBytes + LayoutType.DateTime.Size;

            case UNIX_DATE_TIME:
                return metaBytes + LayoutType.UnixDateTime.Size;

            case GUID:
                return metaBytes + LayoutType.Guid.Size;

            case MONGODB_OBJECT_ID:
                return metaBytes + MongoDbObjectId.Size;

            case UTF_8:
            case BINARY:
                // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
                ///#pragma warning disable SA1137 // Elements should have the same indentation
            {
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
                int numBytes = (int)this.read7BitEncodedUInt(metaOffset + metaBytes, tempOut_sizeLenInBytes);
                sizeLenInBytes = tempOut_sizeLenInBytes.get();
                return metaBytes + sizeLenInBytes + numBytes;
            }

            case VAR_INT:
            case VAR_UINT: {
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes2 = new Out<Integer>();
                this.read7BitEncodedUInt(metaOffset + metaBytes, tempOut_sizeLenInBytes2);
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
     * <see
     * cref="EnsureSparse(ref RowCursor, LayoutCode , TypeArgumentList , int ,RowOptions, out int, out int, out int)" />
     * .
     */
    private void ensureSparse(
        RowCursor edit, LayoutType cellType, TypeArgumentList typeArgs, int numBytes, UpdateOptions options,
        Out<Integer> metaBytes, Out<Integer> spaceNeeded, Out<Integer> shift
    ) {
        this.ensureSparse(edit, cellType, typeArgs, numBytes, RowOptions.from(options.value()), metaBytes, spaceNeeded, shift);
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
    private void readSparseMetadata(Reference<RowCursor> edit) {
        if (edit.get().scopeType().hasImplicitTypeCode(edit)) {
            edit.get().scopeType().SetImplicitTypeCode(edit);
            edit.get().valueOffset(edit.get().metaOffset());
        } else {
            edit.get().cellType = this.readSparseTypeCode(edit.get().metaOffset());
            edit.get().valueOffset(edit.get().metaOffset() + (LayoutCode.SIZE / Byte.SIZE));
            edit.get().cellTypeArgs = TypeArgumentList.EMPTY;
            if (edit.get().cellType() instanceof LayoutEndScope) {
                // Reached end of current scope without finding another field.
                edit.get().pathToken = 0;
                edit.get().pathOffset = 0;
                edit.get().valueOffset(edit.get().metaOffset());
                return;
            }

            Reference<RowBuffer> tempReference_this =
                new Reference<RowBuffer>(this);
            int sizeLenInBytes;
            Out<Integer> tempOut_sizeLenInBytes = new Out<Integer>();
            edit.get().cellTypeArgs = edit.get().cellType().readTypeArgumentList(tempReference_this,
                edit.get().valueOffset(), tempOut_sizeLenInBytes).clone();
            sizeLenInBytes = tempOut_sizeLenInBytes.get();
            this = tempReference_this.get();
            edit.get().valueOffset(edit.get().valueOffset() + sizeLenInBytes);
        }

        Reference<RowBuffer> tempReference_this2 = new Reference<RowBuffer>(this);
        edit.get().scopeType().ReadSparsePath(tempReference_this2, edit);
        this = tempReference_this2.get();
    }

    /**
     * Skip over a nested scope.
     *
     * @param edit The sparse scope to search.
     * @return The 0-based byte offset immediately following the scope end marker.
     */
    private int skipScope(RowCursor edit) {

        while (this.sparseIteratorMoveNext(edit)) {
        }

        if (!edit.scopeType().isSizedScope()) {
            edit.metaOffset(edit.metaOffset() + (LayoutCode.SIZE / Byte.SIZE)); // Move past the end of
            // scope marker.
        }

        return edit.metaOffset();
    }

    /**
     * Compute the size of a sparse field
     *
     * @param edit The edit structure describing the field to measure.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int sparseComputeSize(RowCursor edit) {

        if (!(edit.cellType() instanceof LayoutScope)) {
            return this.SparseComputePrimitiveSize(edit.cellType(), edit.metaOffset(), edit.valueOffset());
        }

        // Compute offset to end of value for current value
        RowCursor newScope = this.sparseIteratorReadScope(edit, true);
        return this.skipScope(newScope) - edit.metaOffset();
    }

    private void readSparsePrimitiveTypeCode(@Nonnull RowCursor edit, LayoutType code) {

        checkNotNull(edit);
        checkArgument(edit.exists());

        if (edit.scopeType().hasImplicitTypeCode(edit)) {
            if (edit.scopeType() instanceof LayoutNullable) {
                checkState(edit.scopeTypeArgs().count() == 1);
                checkState(edit.index() == 1);
                checkState(edit.scopeTypeArgs().get(0).type() == code);
                checkState(edit.scopeTypeArgs().get(0).typeArgs().count() == 0);
            } else if (edit.scopeType().isFixedArity()) {
                checkState(edit.scopeTypeArgs().count() > edit.index());
                checkState(edit.scopeTypeArgs().get(edit.index()).type() == code);
                checkState(edit.scopeTypeArgs().get(edit.index()).typeArgs().count() == 0);
            } else {
                checkState(edit.scopeTypeArgs().count() == 1);
                checkState(edit.scopeTypeArgs().get(0).type() == code);
                checkState(edit.scopeTypeArgs().get(0).typeArgs().count() == 0);
            }
        } else {
            if (code == LayoutTypes.Boolean) {
                code = this.readSparseTypeCode(edit.metaOffset());
                checkState(code == LayoutTypes.Boolean || code == LayoutTypes.BooleanFalse);
            } else {
                checkState(this.readSparseTypeCode(edit.metaOffset()) == code);
            }
        }

        if (edit.scopeType().isIndexedScope()) {
            checkState(edit.pathOffset() == 0);
            checkState(edit.pathToken() == 0);
        } else {
            Out<Integer> pathLenInBytes = new Out<>();
            Out<Integer> pathOffset = new Out<>();
            int token = this.ReadSparsePathLen(edit.layout(), edit.metaOffset() + (LayoutCode.SIZE / Byte.SIZE),
             pathLenInBytes, pathOffset);
            checkState(edit.pathOffset() == pathOffset.get());
            checkState(edit.pathToken() == token);
        }
    }

    /**
     * Represents a single item within a set/map scope that needs to be indexed
     * <p>
     * This structure is used when rebuilding a set/map index during row streaming via {@link RowWriter}.Each item
     * encodes its offsets and length within the row.
     */
                    static final class UniqueIndexItem {

                        private LayoutCode Code = LayoutCode.values()[0];
                        private int MetaOffset;
                        private int Size;
                        private int ValueOffset;

                        /**
                         * The layout code of the value.
                         */
                        public LayoutCode code() {
                            return Code;
                        }

                        public UniqueIndexItem code(LayoutCode code) {
                            Code = code;
                            return this;
                        }

                        /**
                         * If existing, the offset to the metadata of the existing field, otherwise the location to insert a new field
                         */
                        public int metaOffset() {
                            return MetaOffset;
                        }

                        public UniqueIndexItem metaOffset(int metaOffset) {
                            MetaOffset = metaOffset;
                            return this;
                        }

                        /**
                         * Size of the target element
                         */
                        public int size() {
                            return Size;
                        }

                        public UniqueIndexItem size(int size) {
                            Size = size;
                            return this;
                        }

                        /**
                         * If existing, the offset to the value of the existing field, otherwise undefined
                         */
                        public int valueOffset() {
                            return ValueOffset;
                        }

                        public UniqueIndexItem valueOffset(int valueOffset) {
                            ValueOffset = valueOffset;
                            return this;
                        }
    }
}