// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.codecs.DateTimeCodec;
import com.azure.data.cosmos.serialization.hybridrow.codecs.DecimalCodec;
import com.azure.data.cosmos.serialization.hybridrow.codecs.Float128Codec;
import com.azure.data.cosmos.serialization.hybridrow.codecs.GuidCodec;
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
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTagged2;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTuple;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypeScope;
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
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringTokenizer;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

// import com.azure.data.cosmos.serialization.hybridrow.RowBuffer.UniqueIndexItem;

//import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypes.MongoDbObjectId;

/**
 * Manages a sequence of bytes representing a Hybrid Row.
 * <p>
 * A Hybrid Row begins in the 0-th byte of the {@link RowBuffer}. The sequence of bytes is defined by the Hybrid Row
 * grammar.
 */
public final class RowBuffer {

    private final ByteBuf buffer;
    private LayoutResolver resolver;

    /**
     * Initializes a new instance of a {@link RowBuffer}.
     *
     * @param capacity Initial buffer capacity.
     */
    public RowBuffer(int capacity) {
        this(capacity, ByteBufAllocator.DEFAULT);
    }

    /**
     * Initializes a new instance of a {@link RowBuffer}.
     *
     * @param capacity  Initial buffer capacity
     * @param allocator A buffer allocator
     */
    public RowBuffer(final int capacity, @Nonnull final ByteBufAllocator allocator) {
        checkArgument(capacity > 0, "capacity: %s", capacity);
        checkNotNull(allocator, "expected non-null allocator");
        this.buffer = allocator.buffer(capacity);
        this.resolver = null;
    }

    /**
     * Initializes a new instance of a {@link RowBuffer} from an existing buffer.
     *
     * @param buffer   An existing {@link ByteBuf} containing a Hybrid Row. This instance takes ownership of the buffer.
     *                 Hence, the caller should not maintain a reference to the buffer or mutate the buffer after this
     *                 call returns.
     * @param version  The version of the Hybrid Row format to use for encoding the buffer.
     * @param resolver The resolver for UDTs.
     */
    public RowBuffer(
        @Nonnull final ByteBuf buffer,
        @Nonnull final HybridRowVersion version,
        @Nonnull final LayoutResolver resolver) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(version, "expected non-null version");
        checkNotNull(resolver, "expected non-null resolver");

        final int length = buffer.writerIndex();

        checkArgument(length >= HybridRowHeader.BYTES,
            "expected buffer with at least %s, not %s bytes", HybridRowHeader.BYTES, length);

        this.buffer = buffer;
        this.resolver = resolver;

        final Item<HybridRowHeader> item = this.read(this::readHeader, 0);
        final HybridRowHeader header = item.value();
        checkState(header.version() == version, "expected header version %s, not %s", version, header.version());

        final Layout layout = resolver.resolve(header.schemaId());
        checkState(header.schemaId().equals(layout.schemaId()));
        checkState(HybridRowHeader.BYTES + layout.size() <= this.length());
    }

    /**
     * Compute the byte offset from the beginning of the row for a given variable's value.
     *
     * @param layout      The (optional) layout of the current scope.
     * @param scopeOffset The zero-based offset to the beginning of the scope's value.
     * @param varIndex    The zero-based index of the variable within the variable segment.
     * @return The byte offset from the beginning of the row where the variable's value should be located.
     */
    public int computeVariableValueOffset(@Nullable final Layout layout, final int scopeOffset, final int varIndex) {

        checkArgument(scopeOffset >= 0, "expected non-negative scopeOffset, not %s", scopeOffset);
        checkArgument(varIndex >= 0, "expected non-negative varIndex, not %s", varIndex);

        if (layout == null) {
            return scopeOffset;
        }

        final List<LayoutColumn> columns = layout.columns();
        final int index = layout.numFixed() + varIndex;
        checkState(index <= columns.size());

        int offset = scopeOffset + layout.size();

        for (int i = layout.numFixed(); i < index; i++) {

            LayoutColumn column = columns.get(i);

            if (this.readBit(scopeOffset, column.nullBit())) {
                Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
                if (column.type().isVarint()) {
                    offset += item.length();
                } else {
                    offset += item.length() + item.value();
                }
            }
        }

        return offset;
    }

    /**
     * Compute the number of bytes necessary to store the unsigned 32-bit integer value using the varuint encoding.
     *
     * @param value the value to be encoded
     * @return the number of bytes needed to store the varuint encoding of {@code value}
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
     * Decrement the unsigned 32-bit integer value at the given {@code offset} in this {@link RowBuffer}.
     *
     * @param offset    offset of a 32-bit unsigned integer value in this {@link RowBuffer}.
     * @param decrement the decrement value.
     */
    public void decrementUInt32(int offset, long decrement) {
        long value = this.buffer.getUnsignedIntLE(offset);
        this.buffer.setIntLE(offset, (int) (value - decrement));
    }

    /**
     * Delete the sparse field at the specified cursor position.
     *
     * @param edit identifies the field to delete
     */
    public void deleteSparse(@Nonnull final RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        if (!edit.exists()) {
            return; // do nothing
        }

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(0, edit, edit.cellType(), edit.cellTypeArgs(), RowOptions.DELETE, metaBytes, spaceNeeded, shift);

        checkState(this.length() == priorLength + shift.get());
    }

    /**
     * Delete the variable-length field at a specified {@code offset}.
     * <p>
     * The field is interpreted as either a variable-length integer or a variable-length sequence of bytes as indicated
     * by the value of {@code isVarint}.
     *
     * @param offset   index of the field in this {@link RowBuffer}
     * @param isVarint {@code true}, if the field should be interpreted as a variable-length integer value;
     *                 {@code false}, if the field should be interpreted as a variable-length sequence of bytes.
     */
    public void deleteVariable(final int offset, final boolean isVarint) {

        final Item<Long> item = this.read(this::read7BitEncodedUInt, offset);

        final int source = isVarint
            ? offset + item.length()  // because this is a varint value
            : offset + item.length() + item.value().intValue();  // because this is a variable-length sequence of bytes

        final int length = this.buffer.writerIndex() - source;

        this.buffer.setBytes(offset, this.buffer, source, length);
        this.buffer.writerIndex(this.buffer.writerIndex() - length);
    }

    /**
     * The root header of this {@link RowBuffer}.
     *
     * @return root header of this {@link RowBuffer}.
     */
    public HybridRowHeader header() {
        return this.readHeader();
    }

    // TODO: DANOBLE: ressurrect this method
    //    public void WriteSparseMongoDbObjectId(@Nonnull final RowCursor edit, MongoDbObjectId value,
    //                                           UpdateOptions options) {
    //        int numBytes = MongoDbObjectId.Size;
    //        int metaBytes;
    //        final Out<Integer> metaBytes = new Out<>();
    //        int spaceNeeded;
    //        final Out<Integer> spaceNeeded = new Out<>();
    //        int shift;
    //        final Out<Integer> shift = new Out<>();
    //        this.ensureSparse(numBytes, edit, MongoDbObjectId, TypeArgumentList.EMPTY, options,
    //            metaBytes, spaceNeeded, shift);
    //        this.writeSparseMetadata(edit, MongoDbObjectId, TypeArgumentList.EMPTY, metaBytes);
    //        this.WriteMongoDbObjectId(edit.valueOffset(), value.clone());
    //        checkState(spaceNeeded == metaBytes + MongoDbObjectId.Size);
    //        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    //        this.buffer.writerIndex(this.length() + shift);
    //    }

    /**
     * Decrement the unsigned 32-bit integer value at the given {@code offset} in this {@link RowBuffer}.
     *
     * @param offset    offset of a 32-bit unsigned integer value in this {@link RowBuffer}.
     * @param increment the increment value.
     */
    public void incrementUInt32(final int offset, final long increment) {
        final long value = this.buffer.getUnsignedIntLE(offset);
        this.buffer.setIntLE(offset, (int) (value + increment));
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

        checkNotNull(version, "expected non-null version");
        checkNotNull(layout, "expected non-null layout");
        checkNotNull(resolver, "expected non-null resolver");

        this.writeHeader(new HybridRowHeader(version, layout.schemaId()));
        this.buffer.writeZero(layout.size());
        this.resolver = resolver;
    }

    /**
     * The length of this {@link RowBuffer} in bytes.
     *
     * @return The length of this {@link RowBuffer} in bytes.
     */
    public int length() {
        return this.buffer.writerIndex();
    }

    /**
     * Compute the byte offsets from the beginning of the row for a given sparse field insertion.
     * into a set/map.
     *
     * @param scope   The sparse scope to insert into.
     * @param srcEdit The field to move into the set/map.
     * @return The prepared edit context.
     */
    @Nonnull
    public RowCursor prepareSparseMove(@Nonnull final RowCursor scope, @Nonnull final RowCursor srcEdit) {

        checkNotNull(srcEdit, "expected non-null srcEdit");
        checkNotNull(scope, "expected non-null scope");
        checkArgument(scope.index() == 0);
        checkArgument(scope.scopeType().isUniqueScope());

        RowCursor dstEdit = scope.clone().metaOffset(scope.valueOffset());
        int srcSize = this.sparseComputeSize(srcEdit);
        int srcBytes = srcSize - (srcEdit.valueOffset() - srcEdit.metaOffset());

        while (dstEdit.index() < dstEdit.count()) {

            this.readSparseMetadata(dstEdit);
            checkState(dstEdit.pathOffset() == 0);

            int elmSize = -1; // defer calculating the full size until needed
            int cmp;

            if (scope.scopeType() instanceof LayoutTypedMap) {
                cmp = this.compareKeyValueFieldValue(srcEdit, dstEdit);
            } else {
                elmSize = this.sparseComputeSize(dstEdit);
                int elmBytes = elmSize - (dstEdit.valueOffset() - dstEdit.metaOffset());
                cmp = this.compareFieldValue(srcEdit, srcBytes, dstEdit, elmBytes);
            }

            if (cmp <= 0) {
                dstEdit.exists(cmp == 0);
                return dstEdit;
            }

            elmSize = elmSize == -1 ? this.sparseComputeSize(dstEdit) : elmSize;
            dstEdit.index(dstEdit.index() + 1);
            dstEdit.metaOffset(dstEdit.metaOffset() + elmSize);
        }

        dstEdit.exists(false);
        dstEdit.cellType(LayoutTypes.END_SCOPE);
        dstEdit.valueOffset(dstEdit.metaOffset());

        return dstEdit;
    }

    /**
     * Read the value of a bit within the bit field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a bit field within this {@link RowBuffer}.
     * @param bit the bit to read.
     * @return {@code true} if the {@code bit} is set, otherwise {@code false}.
     */
    public boolean readBit(final int offset, @Nonnull final LayoutBit bit) {

        checkNotNull(bit, "expected non-null bit");

        if (bit.isInvalid()) {
            return true;
        }

        Item<Boolean> item = this.read(() -> (this.buffer.readByte() & (byte) (1 << bit.bit())) != 0, bit.offset(offset));
        return item.value();
    }

    /**
     * Read the value of the {@code DateTime} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code DateTime} field within this {@link RowBuffer}.
     * @return the {@code DateTime} value read.
     */
    public OffsetDateTime readDateTime(int offset) {
        Item<OffsetDateTime> item = this.read(() -> DateTimeCodec.decode(this.buffer), offset);
        return item.value();
    }

    // TODO: DANOBLE: resurrect this method
    //    public MongoDbObjectId ReadMongoDbObjectId(int offset) {
    //        return MemoryMarshal.<MongoDbObjectId>Read(this.buffer.Slice(offset));
    //    }

    /**
     * Read the value of the {@code Decimal} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Decimal} field within this {@link RowBuffer}.
     * @return the {@code Decimal} value read.
     */
    public BigDecimal readDecimal(int offset) {
        Item<BigDecimal> item = this.read(() -> DecimalCodec.decode(this.buffer), offset);
        return item.value();
    }

    /**
     * Read the value of a {@code FixedBinary} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code FixedBinary} field within this {@link RowBuffer}.
     * @param length number of bytes to read.
     * @return the {@code FixedBinary} value read.
     */
    public ByteBuf readFixedBinary(int offset, int length) {
        Item<ByteBuf> item = this.read(() -> this.buffer.readSlice(length), offset);
        return item.value();
    }

    /**
     * Read the value of a {@code FixedString} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code FixedString} field within this {@link RowBuffer}.
     * @param length number of bytes in the {@code FixedString} field.
     * @return the {@code FixedString} value read.
     */
    public Utf8String readFixedString(int offset, int length) {
        Item<Utf8String> item = this.read(this::readFixedString, offset, length);
        return item.value();
    }

    /**
     * Read the value of a {@code Float128} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Float128} field within this {@link RowBuffer}.
     * @return the {@code Float128} value read.
     */
    public Float128 readFloat128(int offset) {
        Item<Float128> item = this.read(this::readFloat128, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code Float32} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Float32} field within this {@link RowBuffer}.
     * @return the {@code Float32} value read.
     */
    public float readFloat32(int offset) {
        Item<Float> item = this.read(this.buffer::readFloatLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code Float64} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Float64} field within this {@link RowBuffer}.
     * @return the {@code Float64} value read.
     */
    public double readFloat64(int offset) {
        Item<Double> item = this.read(this.buffer::readDoubleLE, offset);
        return item.value();
    }

    // TODO: DANOBLE: resurrect this method
    //    public MongoDbObjectId ReadSparseMongoDbObjectId(Reference<RowCursor> edit) {
    //        this.readSparsePrimitiveTypeCode(edit, MongoDbObjectId);
    //        edit.endOffset = edit.valueOffset() + MongoDbObjectId.Size;
    //        return this.ReadMongoDbObjectId(edit.valueOffset()).clone();
    //    }

    /**
     * Reads in the contents of the current {@link RowBuffer} from an {@link InputStream}.
     * <p>
     * The {@link RowBuffer} is initialized with the associated layout and row {@code version}.
     *
     * @param inputStream the stream from which the contents of the current {@link RowBuffer} should be read
     * @param byteCount   the number of bytes to be read from the {@code inputStream}
     * @param version     the {@link HybridRowVersion} to be assigned to the current {@link RowBuffer}
     * @param resolver    the layout resolver to be used in parsing the {@code inputStream}
     * @return {@code true} if the read succeeded; otherwise, if the {@link InputStream} was corrupted, {@code false}
     */
    public boolean readFrom(
        @Nonnull final InputStream inputStream, final int byteCount, @Nonnull final HybridRowVersion version,
        @Nonnull final LayoutResolver resolver) {

        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(resolver, "expected non-null resolver");
        checkNotNull(version, "expected non-null version");
        checkState(byteCount >= HybridRowHeader.BYTES, "expected byteCount >= %s, not %s", HybridRowHeader.BYTES,
            byteCount);

        this.reset();
        this.ensure(byteCount);
        this.resolver = resolver;

        final int bytesRead;

        try {
            bytesRead = this.buffer.writeBytes(inputStream, byteCount);
        } catch (IOException error) {
            return false;
        }

        if (bytesRead != byteCount) {
            return false;
        }

        return this.validateHeader(version);
    }

    /**
     * Reads the contents of the current {@link RowBuffer} from a {@link ByteBuf}.
     * <p>
     * The {@link RowBuffer} is initialized with a copy of the specified input {@link ByteBuf} and the associated layout
     * and row {@code version}.
     *
     * @param input    the buffer from which the contents of the current {@link RowBuffer} should be read
     * @param version  the {@link HybridRowVersion} to be assigned to the current {@link RowBuffer}
     * @param resolver the layout resolver to be used in parsing the {@code inputStream}
     * @return {@code true} if the read succeeded; otherwise, if the {@link InputStream} was corrupted, {@code false}
     */
    public boolean readFrom(
        @Nonnull final ByteBuf input, @Nonnull final HybridRowVersion version, @Nonnull final LayoutResolver resolver) {

        checkNotNull(input, "expected non-null input");
        checkNotNull(version, "expected non-null version");
        checkNotNull(resolver, "expected non-null resolver");
        checkState(input.readableBytes() >= HybridRowHeader.BYTES);

        this.reset();
        this.resolver = resolver;
        this.buffer.writeBytes(this.buffer);

        return this.validateHeader(version);
    }

    /**
     * Read the value of a {@code Guid} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Guid} field within this {@link RowBuffer}.
     * @return the {@code Guid} value read.
     */
    public UUID readGuid(int offset) {
        return this.read(() -> GuidCodec.decode(this.buffer), offset).value();
    }

    /**
     * Read the value of a {@code Header} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Header} field within this {@link RowBuffer}.
     * @return the {@code Header} value read.
     */
    public HybridRowHeader readHeader(int offset) {
        return this.read(this::readHeader, offset).value();
    }

    /**
     * Read the value of a {@code Int16} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Int16} field within this {@link RowBuffer}.
     * @return the {@code Int16} value read.
     */
    public short readInt16(int offset) {
        return this.read(this.buffer::readShortLE, offset).value();
    }

    /**
     * Read the value of a {@code Int32} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Int32} field within this {@link RowBuffer}.
     * @return the {@code Int32} value read.
     */
    public int readInt32(int offset) {
        Item<Integer> item = this.read(this.buffer::readIntLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code Int64} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Int64} field within this {@link RowBuffer}.
     * @return the {@code Int64} value read.
     */
    public long readInt64(int offset) {
        Item<Long> item = this.read(this.buffer::readLongLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code Int8} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code Int8} field within this {@link RowBuffer}.
     * @return the {@code Int8} value read.
     */
    public byte readInt8(int offset) {
        Item<Byte> item = this.read(this.buffer::readByte, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code SchemaId} field at the given {@code offset} within this {@link RowBuffer}.
     *
     * @param offset offset of a {@code SchemaId} field within this {@link RowBuffer}.
     * @return the {@code SchemaId} value read.
     */
    public SchemaId readSchemaId(int offset) {
        Item<SchemaId> item = this.read(() -> SchemaId.from(this.buffer.readIntLE()), offset);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseBinary} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseBinary} field within this {@link RowBuffer}.
     * @return the {@code SparseBinary} value read.
     */
    public ByteBuf readSparseBinary(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.BINARY);
        Item<ByteBuf> item = this.read(this::readVariableBinary, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseBoolean} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseBoolean} field within this {@link RowBuffer}.
     * @return the {@code SparseBoolean} value read.
     */
    public boolean readSparseBoolean(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.BOOLEAN);
        edit.endOffset(edit.valueOffset());
        return edit.cellType() == LayoutTypes.BOOLEAN;
    }

    /**
     * Read the value of a {@code SparseDateTime} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseDateTime} field within this {@link RowBuffer}.
     * @return the {@code SparseDateTime} value read.
     */
    public OffsetDateTime readSparseDateTime(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.DATE_TIME);
        edit.endOffset(edit.valueOffset() + Long.SIZE);
        return this.readDateTime(edit.valueOffset());
    }

    /**
     * Read the value of a {@code SparseDecimal} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseDecimal} field within this {@link RowBuffer}.
     * @return the {@code SparseDecimal} value read.
     */
    public BigDecimal readSparseDecimal(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.DECIMAL);
        Item<BigDecimal> item = this.read(this::readDecimal, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseFloat128} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseFloat128} field within this {@link RowBuffer}.
     * @return the {@code SparseFloat128} value read.
     */
    public Float128 readSparseFloat128(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_128);
        Item<Float128> item = this.read(this::readFloat128, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseFloat32} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseFloat32} field within this {@link RowBuffer}.
     * @return the {@code SparseFloat32} value read.
     */
    public float readSparseFloat32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_32);
        Item<Float> item = this.read(this.buffer::readFloatLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseFloat64} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseFloat64} field within this {@link RowBuffer}.
     * @return the {@code SparseFloat64} value read.
     */
    public double readSparseFloat64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_64);
        Item<Double> item = this.read(this.buffer::readDoubleLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseGuid} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseGuid} field within this {@link RowBuffer}.
     * @return the {@code SparseGuid} value read.
     */
    public UUID readSparseGuid(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.GUID);
        Item<UUID> item = this.read(this::readGuid, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseInt16} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseInt16} field within this {@link RowBuffer}.
     * @return the {@code SparseInt16} value read.
     */
    public short readSparseInt16(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_16);
        Item<Short> item = this.read(this.buffer::readShortLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseInt32} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseInt32} field within this {@link RowBuffer}.
     * @return the {@code SparseInt32} value read.
     */
    public int readSparseInt32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_32);
        Item<Integer> item = this.read(this.buffer::readIntLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseInt64} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseInt64} field within this {@link RowBuffer}.
     * @return the {@code SparseInt64} value read.
     */
    public long readSparseInt64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_64);
        Item<Long> item = this.read(this.buffer::readLongLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseInt8} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseInt8} field within this {@link RowBuffer}.
     * @return the {@code SparseInt8} value read.
     */
    public byte readSparseInt8(RowCursor edit) {
        // TODO: Remove calls to readSparsePrimitiveTypeCode once moved to V2 read.
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_8);
        Item<Byte> item = this.read(this.buffer::readByte, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseNull} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseNull} field within this {@link RowBuffer}.
     * @return the {@code SparseNull} value read.
     */
    public NullValue readSparseNull(@Nonnull RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.NULL);
        edit.endOffset(edit.valueOffset());

        return NullValue.DEFAULT;
    }

    /**
     * Read the value of a {@code SparsePath} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparsePath} field within this {@link RowBuffer}.
     * @return the {@code SparsePath} value read.
     */
    public Utf8String readSparsePath(@Nonnull final RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        final StringTokenizer tokenizer = edit.layout().tokenizer();
        final Optional<Utf8String> path = tokenizer.tryFindString(edit.pathToken());

        if (path.isPresent()) {
            return path.get();
        }

        final int length = edit.pathToken() - tokenizer.count();
        Item<Utf8String> item = this.read(this::readFixedString, edit.pathOffset(), length);

        return item.value();
    }

    /**
     * Read the value of a {@code SparsePathLen} field at the given {@code offset} position.
     *
     * @param layout            layout of the {@code SparsePathLen} field.
     * @param offset            position of the {@code SparsePathLen} field..
     * @param pathOffset        [output] position of the {@code SparsePathLen} field value.
     * @param pathLengthInBytes [output] length of the {@code SparsePathLen} field value.
     * @return the {@code SparsePathLen} value read.
     */
    public int readSparsePathLen(
        @Nonnull final Layout layout,
        final int offset,
        @Nonnull final Out<Integer> pathOffset,
        @Nonnull final Out<Integer> pathLengthInBytes) {

        checkNotNull(layout, "expected non-null layout");
        checkNotNull(pathOffset, "expected non-null pathOffset");
        checkNotNull(pathLengthInBytes, "expected non-null pathLengthInBytes");

        final Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        final int token = item.value().intValue();

        if (token < layout.tokenizer().count()) {
            pathLengthInBytes.set(item.length());
            pathOffset.set(offset);
            return token;
        }

        final int numBytes = token - layout.tokenizer().count();
        pathLengthInBytes.set(numBytes + item.length());
        pathOffset.set(offset + item.length());

        return token;
    }

    /**
     * Read the value of a {@code SparseString} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseString} field within this {@link RowBuffer}.
     * @return the {@code SparseString} value read.
     */
    public Utf8String readSparseString(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UTF_8);
        Item<Utf8String> item = this.read(this::readUtf8String, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseTypeCode} field at the given {@code offset} position.
     *
     * @param offset position of a {@code SparseTypeCode} field within this {@link RowBuffer}.
     * @return the {@code SparseTypeCode} value read.
     */
    public LayoutType readSparseTypeCode(int offset) {
        byte value = this.readInt8(offset);
        LayoutCode code = LayoutCode.from(value);
        checkState(code != null, "expected layout code at offset %s, not %s", offset, code);
        return LayoutType.fromLayoutCode(code);
    }

    /**
     * Read the value of a {@code SparseUInt16} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseUInt16} field within this {@link RowBuffer}.
     * @return the {@code SparseUInt16} value read.
     */
    public int readSparseUInt16(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_16);
        Item<Integer> item = this.read(this.buffer::readUnsignedShortLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseUInt32} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseUInt32} field within this {@link RowBuffer}.
     * @return the {@code SparseUInt32} value read.
     */
    public long readSparseUInt32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_32);
        Item<Long> item = this.read(this.buffer::readUnsignedIntLE, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseUInt64} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseUInt64} field within this {@link RowBuffer}.
     * @return the {@code SparseUInt64} value read.
     */
    public long readSparseUInt64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_64);
        Item<Long> item = this.read(this.buffer::readLongLE, edit);
        return item.value;
    }

    /**
     * Read the value of a {@code SparseUInt8} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseUInt8} field within this {@link RowBuffer}.
     * @return the {@code SparseUInt8} value read.
     */
    public short readSparseUInt8(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_8);
        Item<Short> item = this.read(this.buffer::readUnsignedByte, edit);
        return item.value;
    }

    /**
     * Read the value of a {@code SparseUnixDateTime} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseUnixDateTime} field within this {@link RowBuffer}.
     * @return the {@code SparseUnixDateTime} value read.
     */
    public UnixDateTime readSparseUnixDateTime(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UNIX_DATE_TIME);
        Item<UnixDateTime> item = this.read(this::readUnixDateTime, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseVarInt} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseVarInt} field within this {@link RowBuffer}.
     * @return the {@code SparseVarInt} value read.
     */
    public long readSparseVarInt(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.VAR_INT);
        Item<Long> item = this.read(this::read7BitEncodedInt, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code SparseVarUInt} field at the given {@link RowCursor edit} position.
     *
     * @param edit {@link RowCursor edit} position of a {@code SparseVarUInt} field within this {@link RowBuffer}.
     * @return the {@code SparseVarUInt} value read.
     */
    public long readSparseVarUInt(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.VAR_UINT);
        Item<Long> item = this.read(this::read7BitEncodedUInt, edit);
        return item.value();
    }

    /**
     * Read the value of a {@code UInt16} field at the given {@code offset} position.
     *
     * @param offset position of a {@code UInt16} field within this {@link RowBuffer}.
     * @return the {@code UInt16} value read.
     */
    public int readUInt16(int offset) {
        Item<Integer> item = this.read(this.buffer::readUnsignedShortLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code UInt32} field at the given {@code offset} position.
     *
     * @param offset position of a {@code UInt32} field within this {@link RowBuffer}.
     * @return the {@code UInt32} value read.
     */
    public long readUInt32(int offset) {
        Item<Long> item = this.read(this.buffer::readUnsignedIntLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code UInt64} field at the given {@code offset} position.
     *
     * @param offset position of a {@code UInt64} field within this {@link RowBuffer}.
     * @return the {@code UInt64} value read.
     */
    public long readUInt64(int offset) {
        Item<Long> item = this.read(this.buffer::readLongLE, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code UInt8} field at the given {@code offset} position.
     *
     * @param offset position of a {@code UInt8} field within this {@link RowBuffer}.
     * @return the {@code UInt8} value read.
     */
    public short readUInt8(int offset) {
        Item<Short> item = this.read(this.buffer::readUnsignedByte, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code UnixDateTime} field at the given {@code offset} position.
     *
     * @param offset position of a {@code UnixDateTime} field within this {@link RowBuffer}.
     * @return the {@code UnixDateTime} value read.
     */
    public UnixDateTime readUnixDateTime(int offset) {
        Item<UnixDateTime> item = this.read(this::readUnixDateTime, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code VariableBinary} field at the given {@code offset} position.
     *
     * @param offset position of a {@code VariableBinary} field within this {@link RowBuffer}.
     * @return the {@code VariableBinary} value read.
     */
    public ByteBuf readVariableBinary(int offset) {
        Item<ByteBuf> item = this.read(this::readVariableBinary, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code VariableInt} field at the given {@code offset} position.
     *
     * @param offset position of a {@code VariableInt} field within this {@link RowBuffer}.
     * @return the {@code VariableInt} value read.
     */
    public long readVariableInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedInt, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code VariableString} field at the given {@code offset} position.
     *
     * @param offset position of a {@code VariableString} field within this {@link RowBuffer}.
     * @return the {@code VariableString} value read.
     */
    public Utf8String readVariableString(final int offset) {
        Item<Utf8String> item = this.read(this::readUtf8String, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code VariableUInt} field at the given {@code offset} position.
     *
     * @param offset position of a {@code VariableUInt} field within this {@link RowBuffer}.
     * @return the {@code VariableUInt} value read.
     */
    public long readVariableUInt(final int offset) {
        Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        return item.value();
    }

    /**
     * Read the value of a {@code VariableUInt} field at the given {@code offset} position.
     *
     * @param offset position of a {@code VariableUInt} field within this {@link RowBuffer}.
     * @param length on return, the number of bytes read.
     * @return the {@code VariableUInt} value read.
     */
    public long readVariableUInt(final int offset, @Nonnull final Out<Integer> length) {
        Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        length.set(item.length());
        return item.value();
    }

    /**
     * Clears all content from the row. The row is empty after this method.
     */
    public void reset() {
        this.buffer.clear();
        this.resolver = null;
    }

    /**
     * The resolver for UDTs.
     *
     * @return reference to the resolver for UDTs.
     */
    public LayoutResolver resolver() {
        return this.resolver;
    }

    /**
     * Rotates the sign bit of a two's complement value to the least significant bit.
     *
     * @param value A signed value.
     * @return An unsigned value encoding the same value but with the sign bit in the LSB.
     * <p>
     * Moves the signed bit of a two's complement value to the least significant bit (LSB) by:
     * <ol>
     * <li>If negative, take the two's complement.
     * <li>Left shift the value by 1 bit.
     * <li>If negative, set the LSB to 1.
     * </ol>
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

    // TODO: DANOBLE: Support MongoDbObjectId values
    //    public void WriteMongoDbObjectId(int offset, MongoDbObjectId value) {
    //        Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempReference_value =
    //            new Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>(value);
    //        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
    //        value = tempReference_value.get();
    //    }

    public void setBit(final int offset, @Nonnull final LayoutBit bit) {
        checkNotNull(bit, "expected non-null bit");
        if (bit.isInvalid()) {
            return;
        }
        final int index = bit.offset(offset);
        this.buffer.setByte(index, this.buffer.getByte(bit.offset(offset)) | (byte) (1 << bit.bit()));
    }

    /**
     * Move a sparse iterator to the next field within the same sparse scope.
     *
     * @param edit The iterator to advance.
     *
     *             {@code edit.Path}
     *             On success, the path of the field at the given offset, otherwise
     *             undefined.
     *
     *             {@code edit.MetaOffset}
     *             If found, the offset to the metadata of the field, otherwise a
     *             location to insert the field.
     *
     *             {@code edit.cellType}
     *             If found, the layout code of the matching field found, otherwise
     *             undefined.
     *
     *             {@code edit.ValueOffset}
     *             If found, the offset to the value of the field, otherwise
     *             undefined.
     *.
     *
     * @return {@code true} if there is another field; {@code false} if there is not.
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

                if (!(edit.cellType() instanceof LayoutEndScope)) {
                    // End of sparse scope
                    edit.exists(true);
                    return true;
                }
            }
        }

        edit.cellType(LayoutTypes.END_SCOPE);
        edit.exists(false);
        edit.valueOffset(edit.metaOffset());
        return false;
    }

    /**
     * Produce a new scope from the current iterator position.
     *
     * @param edit      An initialized iterator pointing at a scope.
     * @param immutable {@code true} if the new scope should be marked immutable (read-only).
     * @return A new scope beginning at the current iterator position.
     */
    public RowCursor sparseIteratorReadScope(@Nonnull final RowCursor edit, boolean immutable) {

        LayoutTypeScope scopeType = edit.cellType() instanceof LayoutTypeScope ? (LayoutTypeScope) edit.cellType() : null;

        if (scopeType instanceof LayoutObject || scopeType instanceof LayoutArray) {
            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.cellTypeArgs())
                .start(edit.valueOffset())
                .valueOffset(edit.valueOffset())
                .metaOffset(edit.valueOffset())
                .layout(edit.layout())
                .immutable(immutable);
        }

        if (scopeType instanceof LayoutTypedArray || scopeType instanceof LayoutTypedSet || scopeType instanceof LayoutTypedMap) {

            final int valueOffset = edit.valueOffset() + Integer.BYTES; // Point after the Size

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.cellTypeArgs())
                .start(edit.valueOffset())
                .valueOffset(valueOffset)
                .metaOffset(valueOffset)
                .layout(edit.layout())
                .immutable(immutable)
                .count(this.readInt32(edit.valueOffset()));
        }

        if (scopeType instanceof LayoutTypedTuple || scopeType instanceof LayoutTuple || scopeType instanceof LayoutTagged || scopeType instanceof LayoutTagged2) {

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.cellTypeArgs())
                .start(edit.valueOffset())
                .valueOffset(edit.valueOffset())
                .metaOffset(edit.valueOffset())
                .layout(edit.layout())
                .immutable(immutable)
                .count(edit.cellTypeArgs().count());
        }

        if (scopeType instanceof LayoutNullable) {

            boolean hasValue = this.readInt8(edit.valueOffset()) != 0;

            if (hasValue) {

                // Start at the T so it can be read.
                final int valueOffset = edit.valueOffset() + 1;

                return new RowCursor()
                    .scopeType(scopeType)
                    .scopeTypeArgs(edit.cellTypeArgs())
                    .start(edit.valueOffset())
                    .valueOffset(valueOffset)
                    .metaOffset(valueOffset)
                    .layout(edit.layout())
                    .immutable(immutable)
                    .count(2)
                    .index(1);
            } else {

                // Start at the end of the scope, instead of at the T, so the T will be skipped.
                final TypeArgument typeArg = edit.cellTypeArgs().get(0);
                final int valueOffset = edit.valueOffset() + 1 + this.countDefaultValue(typeArg.type(),
                    typeArg.typeArgs());

                return new RowCursor()
                    .scopeType(scopeType)
                    .scopeTypeArgs(edit.cellTypeArgs())
                    .start(edit.valueOffset())
                    .valueOffset(valueOffset)
                    .metaOffset(valueOffset)
                    .layout(edit.layout())
                    .immutable(immutable)
                    .count(2)
                    .index(2);
            }
        }

        if (scopeType instanceof LayoutUDT) {

            final Layout udt = this.resolver.resolve(edit.cellTypeArgs().schemaId());
            final int valueOffset = this.computeVariableValueOffset(udt, edit.valueOffset(), udt.numVariable());

            return new RowCursor()
                .scopeType(scopeType)
                .scopeTypeArgs(edit.cellTypeArgs())
                .start(edit.valueOffset())
                .valueOffset(valueOffset)
                .metaOffset(valueOffset)
                .layout(udt)
                .immutable(immutable);
        }

        throw new IllegalStateException(lenientFormat("Not a scope type: %s", scopeType));
    }

    public byte[] toArray() {
        byte[] content = new byte[this.length()];
        this.buffer.getBytes(0, content);
        return content;
    }

    public void typedCollectionMoveField(
        @Nonnull final RowCursor dstEdit,
        @Nonnull final RowCursor srcEdit,
        @Nonnull final RowOptions options) {

        final int length = this.sparseComputeSize(srcEdit) - (srcEdit.valueOffset() - srcEdit.metaOffset());

        // Insert the field metadata into its new location

        Out<Integer> metaBytes = new Out<>();
        Out<Integer> spaceNeeded = new Out<>();
        Out<Integer> shiftInsert = new Out<>();
        Out<Integer> shiftDelete = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(
            length, dstEdit, srcEdit.cellType(), srcEdit.cellTypeArgs(), options, metaBytes, spaceNeeded, shiftInsert);
        this.writeSparseMetadata(dstEdit, srcEdit.cellType(), srcEdit.cellTypeArgs(), metaBytes.get());

        if (srcEdit.metaOffset() >= dstEdit.metaOffset()) {
            srcEdit.metaOffset(srcEdit.metaOffset() + shiftInsert.get());
            srcEdit.valueOffset(srcEdit.valueOffset() + shiftInsert.get());
        }

        // Copy the value bits from the old location

        this.writeFixedBinary(dstEdit.valueOffset(), this.buffer, srcEdit.valueOffset(), length);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shiftInsert.get());

        // Delete the old location

        this.ensureSparse(
            length, srcEdit, srcEdit.cellType(), srcEdit.cellTypeArgs(), RowOptions.DELETE, metaBytes, spaceNeeded,
            shiftDelete);

        checkState(shiftDelete.get() < 0);
        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shiftDelete.get());
    }

    /**
     * Rebuild the unique index for a set/map scope.
     *
     * @param scope The sparse scope to rebuild an index for.
     * @return Success if the index could be built, an error otherwise.
     * <p>
     * The {@code scope} MUST be a set or map scope.
     * <p>
     * The scope may have been built (e.g. via RowWriter) with relaxed uniqueness constraint checking.
     * This operation rebuilds an index to support verification of uniqueness constraints during
     * subsequent partial updates.  If the appropriate uniqueness constraints cannot be established (i.e.
     * a duplicate exists), this operation fails.  Before continuing, the resulting scope should either:
     * <ol>
     * <li>Be repaired (e.g. by deleting duplicates) and the index rebuild operation should be run again.
     * <li>Be deleted. The entire scope should be removed including its items.
     * </ol>
     * Failure to perform one of these actions will leave the row is potentially in a corrupted state where partial
     * updates may subsequent fail.
     * <p>
     * The target {@code scope} may or may not have already been indexed. This operation is idempotent.
     */
    @Nonnull
    public Result typedCollectionUniqueIndexRebuild(@Nonnull final RowCursor scope) {

        checkNotNull(scope, "expected non-null scope");
        checkArgument(scope.scopeType().isUniqueScope(), "expected unique scope type");
        checkArgument(scope.index() == 0, "expected scope index of zero");

        RowCursor edit = scope.clone();

        if (edit.count() <= 1) {
            return Result.SUCCESS;
        }

        // Compute index elements

        UniqueIndexItem[] uniqueIndex = new UniqueIndexItem[edit.count()];
        edit.metaOffset(scope.valueOffset());

        for (; edit.index() < edit.count(); edit.index(edit.index() + 1)) {

            final RowCursor dstEdit = new RowCursor();
            this.readSparseMetadata(dstEdit);

            checkState(edit.pathOffset() == 0);

            final int elementSize = this.sparseComputeSize(edit);

            uniqueIndex[edit.index()] = new UniqueIndexItem()
                .code(edit.cellType().layoutCode())
                .metaOffset(edit.metaOffset())
                .valueOffset(edit.valueOffset())
                .size(elementSize);

            edit.metaOffset(edit.metaOffset() + elementSize);
        }

        // Create scratch space equal to the sum of the sizes of the scope's values.
        // Implementation Note: theoretically this scratch space could be eliminated by
        // performing the item move operations directly during the Insertion Sort, however,
        // doing so might result in moving the same item multiple times.  Under the assumption
        // that items are relatively large, using scratch space requires each item to be moved
        // AT MOST once.  Given that row buffer memory is likely reused, scratch space is
        // relatively memory efficient.

        int shift = edit.metaOffset() - scope.valueOffset();

        // Sort and check for duplicates

        if (!this.insertionSort(scope, edit, Arrays.asList(uniqueIndex))) {
            return Result.EXISTS;
        }

        // Move elements

        int metaOffset = scope.valueOffset();
        this.ensure(this.length() + shift);
        this.writeFixedBinary(metaOffset + shift, this.buffer, metaOffset, this.length() - metaOffset);

        for (UniqueIndexItem item : uniqueIndex) {
            this.writeFixedBinary(metaOffset, this.buffer, item.metaOffset() + shift, item.size());
            metaOffset += item.size();
        }

        // Delete the scratch space (if necessary - if it doesn't just fall off the end of the row)

        if (metaOffset != this.length()) {
            this.writeFixedBinary(metaOffset, this.buffer, metaOffset + shift, this.length() - metaOffset);
        }

        return Result.SUCCESS;
    }

    public void unsetBit(final int offset, @Nonnull final LayoutBit bit) {
        checkNotNull(bit, "expected non-null bit");
        checkArgument(!bit.isInvalid());
        final int index = bit.offset(offset);
        this.buffer.setByte(index, this.buffer.getByte(index) & (byte) ~(1 << bit.bit()));
    }

    public int write7BitEncodedInt(final long value) {
        return this.write7BitEncodedUInt(RowBuffer.rotateSignToLsb(value));
    }

    /**
     * Sets the specified 64-bit integer at the current {@link RowBuffer position} as a 7-bit encoded 32-bit value.
     * <p>
     * The 64-bit integer value is written 7-bits at a time. The high bit of the byte, when set, indicates there are
     * more bytes. An {@link IllegalArgumentException} is thrown, if the specified 64-bit integer value is outside
     * the range of an unsigned 32-bit integer, [0, 0x00000000FFFFFFFFL].
     *
     * @param value   a 64-bit integer constrained to the range of an unsigned 32-bit integer, [0, 0x00000000FFFFFFFFL]
     * @return The number of bytes written
     */
    public int write7BitEncodedUInt(final long value) {
        checkArgument(0 <= value && value <= 0x00000000FFFFFFFFL, "expected value in range [0, %s], not %s", 0x00000000FFFFFFFFL, value);
        long n = value;
        int i = 0;
        while (n >= 0x80L) {
            this.buffer.writeByte((byte) (n | 0x80L));
            n >>>= 7;
        }
        this.buffer.writeByte((byte) n);
        return i;
    }

    public void writeDateTime(int offset, OffsetDateTime value) {
        Item<OffsetDateTime> item = this.write(this::writeDateTime, offset, value);
    }

    public void writeDecimal(int offset, BigDecimal value) {
        Item<BigDecimal> item = this.write(this::writeDecimal, offset, value);
    }

    public void writeFixedBinary(final int offset, @Nonnull final ByteBuf value, final int length) {

        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected offset >= 0, not %s", offset);
        checkArgument(length >= 0, "expected length >= 0, not %s", length);

        Item<ByteBuf> item = this.write(buffer -> {
            int writableBytes = Math.min(length, buffer.readableBytes());
            this.buffer.writeBytes(buffer, writableBytes);
            if (writableBytes < length) {
                this.buffer.writeZero(length - writableBytes);
            }
        }, offset, value);
    }

    public void writeFixedBinary(final int offset, @Nonnull final ByteBuf value, final int index, final int length) {
        checkArgument(index >= 0, "expected index >= 0, not %s", index);
        value.markReaderIndex().readerIndex(index);
        this.writeFixedBinary(offset, value, length);
        value.resetReaderIndex();
    }

    public void writeFixedBinary(final int offset, @Nonnull final byte[] value, final int index, final int length) {

        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected offset >= 0, not %s", offset);
        checkArgument(length >= 0, "expected length >= 0, not %s", length);
        checkArgument(0 <= index && index < value.length, "expected in range [0, %s), not index", index);

        Item<byte[]> item = this.write(buffer -> {
            int writableBytes = Math.min(length, buffer.length - index);
            this.buffer.writeBytes(buffer, index, writableBytes);
            if (writableBytes < length) {
                this.buffer.writeZero(length - writableBytes);
            }
        }, offset, value);
    }

    public void writeFixedString(final int offset, @Nonnull final Utf8String value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(!value.isNull(), "expected non-null value content");
        Item<Utf8String> item = this.write(this::writeFixedString, offset, value);
    }

    public void writeFloat128(int offset, Float128 value) {
        this.buffer.writeLongLE(value.low());
        this.buffer.writeLongLE(value.high());
    }

    public void writeFloat32(final int offset, final float value) {
        Item<Float> item = this.write(this.buffer::writeFloatLE, offset, value);
    }

    public void writeFloat64(final int offset, final double value) {
        Item<Double> item = this.write(this.buffer::writeDoubleLE, offset, value);
    }

    public void writeGuid(final int offset, @Nonnull final UUID value) {
        checkNotNull(value, "expected non-null value");
        Item<UUID> item = this.write(this::writeGuid, offset, value);
    }

    public void writeHeader(HybridRowHeader value) {
        this.buffer.writeByte(value.version().value());
        this.buffer.writeIntLE(value.schemaId().value());
    }

    public void writeInt16(final int ignored, final short value) {
        this.buffer.writeShortLE(value);
    }

    public void writeInt32(final int ignored, final int value) {
        this.buffer.writeIntLE(value);
    }

    public void writeInt64(final int ignored, final long value) {
        this.buffer.writeLongLE(value);
    }

    public void writeInt8(final int ignored, final byte value) {
        this.buffer.writeByte(value);
    }

    @Nonnull
    public RowCursor writeNullable(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options,
        boolean hasValue) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");

        final int length = this.countDefaultValue(scope, typeArgs);

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());

        final int numWritten = this.writeDefaultValue(edit.valueOffset(), scope, typeArgs);
        checkState(length == numWritten);

        if (hasValue) {
            this.writeInt8(edit.valueOffset(), (byte) 1);
        }

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        final int valueOffset = edit.valueOffset() + 1;

        RowCursor newScope = new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(valueOffset)
            .metaOffset(valueOffset)
            .layout(edit.layout())
            .count(2)
            .index(1);

        RowCursors.moveNext(newScope, this);
        return newScope;
    }

    public void writeSchemaId(final int offset, @Nonnull final SchemaId value) {
        checkNotNull(value, "expected non-null value");
        this.writeInt32(offset, value.value());
    }

    @Nonnull
    public RowCursor writeSparseArray(
        @Nonnull final RowCursor edit, @Nonnull final LayoutTypeScope scope, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(options, "expected non-null options");

        int length = LayoutCode.BYTES;
        TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.writeSparseTypeCode(edit.valueOffset(), LayoutCode.END_SCOPE);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(edit.valueOffset())
            .metaOffset(edit.valueOffset())
            .layout(edit.layout());
    }

    public void writeSparseBinary(
        @Nonnull final RowCursor edit, @Nonnull final ByteBuf value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        final int length = RowBuffer.count7BitEncodedUInt(value.readableBytes()) + value.readableBytes();
        final LayoutType type = LayoutTypes.BINARY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> shift = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeVariableBinary(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseBoolean(
        @Nonnull final RowCursor edit, final boolean value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = 0;
        final LayoutType type = value ? LayoutTypes.BOOLEAN : LayoutTypes.BOOLEAN_FALSE;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, typeArgs, metaBytes.get());

        checkState(spaceNeeded.get() == (int) metaBytes.get());
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseDateTime(
        @Nonnull final RowCursor edit, @Nonnull final OffsetDateTime value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        LayoutType type = LayoutTypes.DATE_TIME;
        int length = DateTimeCodec.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeDateTime(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseDecimal(
        @Nonnull final RowCursor edit, @Nonnull final BigDecimal value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.DECIMAL;
        final int length = DecimalCodec.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeDecimal(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseFloat128(
        @Nonnull final RowCursor edit, @Nonnull final Float128 value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.FLOAT_128;
        final int length = Float128.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeFloat128(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseFloat32(@Nonnull RowCursor edit, float value, @Nonnull UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.FLOAT_32;
        final int length = Float.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeFloat32(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseFloat64(@Nonnull final RowCursor edit, double value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.FLOAT_64;
        final int length = Double.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeFloat64(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseGuid(
        @Nonnull final RowCursor edit, @Nonnull final UUID value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.GUID;
        final int length = GuidCodec.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY,  options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeGuid(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseInt16(@Nonnull final RowCursor edit, short value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.INT_16;
        final int length = Short.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeInt16(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseInt32(@Nonnull final RowCursor edit, int value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.INT_32;
        final int length = Integer.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeInt32(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseInt64(@Nonnull final RowCursor edit, long value, UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.INT_64;
        final int length = Long.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeInt64(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseInt8(@Nonnull final RowCursor edit, byte value, UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = Long.BYTES;
        final LayoutType type = LayoutTypes.INT_8;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, typeArgs, metaBytes.get());
        this.writeInt8(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseNull(
        @Nonnull final RowCursor edit, @Nonnull final NullValue value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = 0;
        final LayoutType type = LayoutTypes.NULL;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, typeArgs, metaBytes.get());

        checkState(spaceNeeded.get() == (int)metaBytes.get());
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public RowCursor writeSparseObject(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(options, "expected non-null options");

        int length = LayoutCode.BYTES; // end scope type code.
        TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.writeSparseTypeCode(edit.valueOffset(), LayoutCode.END_SCOPE);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(TypeArgumentList.EMPTY)
            .start(edit.valueOffset())
            .valueOffset(edit.valueOffset())
            .metaOffset(edit.valueOffset())
            .layout(edit.layout());
    }

    public void writeSparseString(
        @Nonnull final RowCursor edit, @Nonnull final Utf8String value, @Nonnull final UpdateOptions options) {

        final LayoutType type = LayoutTypes.UTF_8;
        final TypeArgumentList args = TypeArgumentList.EMPTY;
        final int length = RowBuffer.count7BitEncodedUInt(value.encodedLength()) + value.encodedLength();

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, args, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, args, metaBytes.get());
        this.write(this::writeVariableString, edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    @Nonnull
    public RowCursor writeSparseTuple(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options) {

        int length = LayoutCode.BYTES * (1 + typeArgs.count()); // nulls for each element

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());

        int valueOffset = edit.valueOffset();

        for (int i = 0; i < typeArgs.count(); i++) {
            this.writeSparseTypeCode(valueOffset, LayoutCode.NULL);
            valueOffset += LayoutCode.BYTES;
        }

        this.writeSparseTypeCode(valueOffset, LayoutCode.END_SCOPE);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(edit.valueOffset())
            .metaOffset(edit.valueOffset())
            .layout(edit.layout())
            .count(typeArgs.count());
    }

    public void writeSparseTypeCode(int offset, LayoutCode code) {
        this.writeUInt8(offset, code.value());
    }

    @Nonnull
    public RowCursor writeSparseUDT(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final Layout udt,
        @Nonnull final UpdateOptions options) {

        TypeArgumentList typeArgs = new TypeArgumentList(udt.schemaId());
        int length = udt.size() + LayoutCode.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.write(this.buffer::writeZero, edit.valueOffset(), udt.size());  // clear all presence bits

        // Write scope terminator

        int valueOffset = edit.valueOffset() + udt.size();
        this.writeSparseTypeCode(valueOffset, LayoutCode.END_SCOPE);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(valueOffset)
            .metaOffset(valueOffset)
            .layout(udt);
    }

    public void writeSparseUInt16(
        @Nonnull final RowCursor edit, final short value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = Short.BYTES;
        final LayoutType type = LayoutTypes.UINT_16;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeUInt16(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseUInt32(
        @Nonnull final RowCursor edit, final int value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = Integer.BYTES;
        final LayoutType type = LayoutTypes.UINT_32;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeUInt32(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseUInt64(@Nonnull final RowCursor edit, long value, @Nonnull UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final int length = Long.BYTES;
        final LayoutType type = LayoutTypes.UINT_64;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeUInt64(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseUInt8(
        @Nonnull final RowCursor edit, final byte value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.UINT_8;
        final int length = Byte.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeUInt8(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseUnixDateTime(
        @Nonnull final RowCursor edit, @Nonnull final UnixDateTime value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        LayoutType type = LayoutTypes.UNIX_DATE_TIME;
        final int length = UnixDateTime.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.writeUnixDateTime(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseVarInt(@Nonnull final RowCursor edit, final long value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(options, "expected non-null options");

        final LayoutType type = LayoutTypes.VAR_INT;
        final int length = RowBuffer.count7BitEncodedInt(value);

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, TypeArgumentList.EMPTY, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, TypeArgumentList.EMPTY, metaBytes.get());
        this.write(this::write7BitEncodedInt, edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void writeSparseVarUInt(
        @Nonnull final RowCursor edit, final long value, @Nonnull final UpdateOptions options) {

        final LayoutType type = LayoutTypes.VAR_UINT;
        final TypeArgumentList typeArgs = TypeArgumentList.EMPTY;
        final int length = RowBuffer.count7BitEncodedUInt(value);

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, type, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, type, typeArgs, metaBytes.get());
        this.write(this::write7BitEncodedUInt, edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    /**
     * Writes the content of the buffer on to an {@link OutputStream}.
     *
     * @param stream the target @{link OutputStream}
     * @throws IOException if the specified {@code stream} throws an {@link IOException} during output
     */
    public void writeTo(@Nonnull final OutputStream stream) throws IOException {
        checkNotNull(stream, "expected non-null stream");
        this.buffer.getBytes(0, stream, this.length());
    }

    @Nonnull
    public RowCursor writeTypedArray(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options) {

        final int length = Integer.BYTES;
        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.writeUInt32(edit.valueOffset(), 0);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        int valueOffset = edit.valueOffset() + Integer.BYTES; // point after the size

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(valueOffset)
            .metaOffset(valueOffset)
            .layout(edit.layout());
    }

    @Nonnull
    public RowCursor writeTypedMap(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options) {

        final int length = Integer.BYTES; // sized scope

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.writeUInt32(edit.valueOffset(), 0);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        int valueOffset = edit.valueOffset() + Integer.BYTES; // point after the size

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(valueOffset)
            .metaOffset(valueOffset)
            .layout(edit.layout());
    }

    @Nonnull
    public RowCursor writeTypedSet(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options) {

        final int length = Integer.BYTES;

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());
        this.writeUInt32(edit.valueOffset(), 0);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        final int valueOffset = edit.valueOffset() + Integer.BYTES; // point after the size

        return new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(valueOffset)
            .metaOffset(valueOffset)
            .layout(edit.layout());
    }

    public RowCursor writeTypedTuple(
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutTypeScope scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options) {

        final int length = this.countDefaultValue(scope, typeArgs);

        final Out<Integer> metaBytes = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();
        final Out<Integer> shift = new Out<>();

        final int priorLength = this.length();

        this.ensureSparse(length, edit, scope, typeArgs, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, scope, typeArgs, metaBytes.get());

        final int numWritten = this.writeDefaultValue(edit.valueOffset(), scope, typeArgs);
        checkState(length == numWritten);

        checkState(spaceNeeded.get() == metaBytes.get() + length);
        checkState(this.length() == priorLength + shift.get());

        final RowCursor newScope = new RowCursor()
            .scopeType(scope)
            .scopeTypeArgs(typeArgs)
            .start(edit.valueOffset())
            .valueOffset(edit.valueOffset())
            .metaOffset(edit.valueOffset())
            .layout(edit.layout())
            .count(typeArgs.count());

        RowCursors.moveNext(newScope, this);
        return newScope;
    }

    public void writeUInt16(final int offset, final short value) {
        final Item<Short> item = this.write(this::writeUInt16, offset, value);
    }

    public void writeUInt32(final int offset, final int value) {
        final Item<Integer> item = this.write(this::writeUInt32, offset, value);
    }

    public void writeUInt64(final int offset, final long value) {
        final Item<Long> item = this.write(this::writeUInt64, offset, value);
    }

    public void writeUInt8(int offset, byte value) {
        final Item<Byte> item = this.write(this::writeUInt8, offset, value);
    }

    public void writeUnixDateTime(int offset, UnixDateTime value) {
        final Item<Long> item = this.write(this::writeUInt64, offset, value.milliseconds());
    }

    public void writeVariableBinary(
        final int offset, @Nonnull final ByteBuf value, final boolean exists, @Nonnull final Out<Integer> shift) {

        checkNotNull(value, "expected non-null value");
        checkNotNull(shift, "expected non-null shift");

        final int length = value.readableBytes();
        final Out<Integer> spaceNeeded = new Out<>();

        final int priorLength = this.length();

        this.ensureVariable(offset, false, length, exists, spaceNeeded, shift);
        final Item<ByteBuf> item = this.write(this::writeVariableBinary, offset, value);

        checkState(spaceNeeded.get() == length + item.length());
        checkState(this.length() == priorLength + shift.get());
    }

    public int writeVariableInt(int offset, long value, boolean exists) {

        final int length = RowBuffer.count7BitEncodedInt(value);
        final Out<Integer> shift = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();

        final int priorLength = this.length();

        this.ensureVariable(offset, true, length, exists, spaceNeeded, shift);
        final Item<Long> item = this.write(this::write7BitEncodedInt, offset, value);

        checkState(item.length == length);
        checkState(spaceNeeded.get() == length);
        checkState(this.length() == priorLength + shift.get());

        return shift.get();
    }

    public int writeVariableString(
        final int offset, @Nonnull final Utf8String value, final boolean exists) {

        checkNotNull(value, "expected non-null value");
        checkArgument(!value.isNull(), "expected non-null value content");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        final int length = value.encodedLength();
        final Out<Integer> shift = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();

        final int priorLength = this.length();

        this.ensureVariable(offset, false, length, exists, spaceNeeded, shift);
        Item<Utf8String> item = this.write(this::writeVariableString, offset, value);

        checkState(spaceNeeded.get() == length + item.length());
        checkState(this.length() == priorLength + shift.get());

        return shift.get();
    }

    public int writeVariableUInt(final int offset, final long value) {
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);
        Item<Long> item = this.write(this::write7BitEncodedUInt, offset, value);
        return item.length();
    }

    public int writeVariableUInt(final int offset, final long value, final boolean exists) {

        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        final int length = RowBuffer.count7BitEncodedUInt(value);
        final Out<Integer> shift = new Out<>();
        final Out<Integer> spaceNeeded = new Out<>();

        final int priorLength = this.length();

        this.ensureVariable(offset, true, length, exists, spaceNeeded, shift);
        final Item<Long> item = this.write(this::write7BitEncodedUInt, offset, value);

        checkState(item.length == length);
        checkState(spaceNeeded.get() == length);
        checkState(this.length() == priorLength + shift.get());

        return shift.get();
    }

    /**
     * Compares the values of two encoded fields using the hybrid row binary collation.
     *
     * @param left     An edit describing the left field.
     * @param leftLength  The size of the left field's value in bytes.
     * @param right    An edit describing the right field.
     * @param rightLength The size of the right field's value in bytes.
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
    private int compareFieldValue(
        @Nonnull final RowCursor left, final int leftLength, @Nonnull final RowCursor right, final int rightLength) {

        checkNotNull(left, "expected non-null left");
        checkNotNull(right, "expected non-null right");
        checkArgument(leftLength >= 0, "expected non-negative leftLength");
        checkArgument(rightLength >= 0, "expected non-negative rightLength");

        if (left.cellType().layoutCode().value() < right.cellType().layoutCode().value()) {
            return -1;
        }

        if (left.cellType() != right.cellType()) {
            return 1;
        }

        if (leftLength > rightLength) {
            return 1;
        }

        if (leftLength < rightLength) {
            return -1;
        }

        ByteBuf sliceLeft = this.buffer.slice(left.valueOffset(), leftLength);
        ByteBuf sliceRight = this.buffer.slice(right.valueOffset(), rightLength);

        return sliceLeft.compareTo(sliceRight);
    }

    /**
     * Compares the values of two encoded key-value pair fields using the hybrid row binary.
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
    private int compareKeyValueFieldValue(RowCursor left, RowCursor right) {

        LayoutTypedTuple leftScopeType = left.cellType() instanceof LayoutTypedTuple
            ? (LayoutTypedTuple) left.cellType()
            : null;
        LayoutTypedTuple rightScopeType = right.cellType() instanceof LayoutTypedTuple
            ? (LayoutTypedTuple) right.cellType()
            : null;

        checkArgument(leftScopeType != null);
        checkArgument(rightScopeType != null);
        checkArgument(left.cellTypeArgs().count() == 2);
        checkArgument(left.cellTypeArgs().equals(right.cellTypeArgs()));

        RowCursor leftKey = new RowCursor();
        leftKey.layout(left.layout());
        leftKey.scopeType(leftScopeType);
        leftKey.scopeTypeArgs(left.cellTypeArgs());
        leftKey.start(left.valueOffset());
        leftKey.metaOffset(left.valueOffset());
        leftKey.index(0);

        this.readSparseMetadata(leftKey);
        checkState(leftKey.pathOffset() == 0);
        int leftKeyLen = this.sparseComputeSize(leftKey) - (leftKey.valueOffset() - leftKey.metaOffset());

        RowCursor rightKey = new RowCursor();
        rightKey.layout(right.layout());
        rightKey.scopeType(rightScopeType);
        rightKey.scopeTypeArgs(right.cellTypeArgs());
        rightKey.start(right.valueOffset());
        rightKey.metaOffset(right.valueOffset());
        rightKey.index(0);

        this.readSparseMetadata(rightKey);
        checkState(rightKey.pathOffset() == 0);
        int rightKeyLen = this.sparseComputeSize(rightKey) - (rightKey.valueOffset() - rightKey.metaOffset());

        return this.compareFieldValue(leftKey, leftKeyLen, rightKey, rightKeyLen);
    }

    /**
     * Compute the number of bytes necessary to store the signed integer using the varint encoding.
     *
     * @param value The value to be encoded
     * @return The number of bytes needed to store the varint encoding of {@code value}
     */
    private static int count7BitEncodedInt(long value) {
        return RowBuffer.count7BitEncodedUInt(RowBuffer.rotateSignToLsb(value));
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
            return LayoutCode.BYTES;
        }
        if (code instanceof LayoutTypedArray || code instanceof LayoutTypedSet || code instanceof LayoutTypedMap) {
            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            return Integer.BYTES;
        }
        if (code instanceof LayoutTuple) {
            // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
            return LayoutCode.BYTES + (LayoutCode.BYTES * typeArgs.count());
        }
        if (code instanceof LayoutTypedTuple || code instanceof LayoutTagged || code instanceof LayoutTagged2) {
            // Fixed arity typed collections take the sum of the default values of each element.  The scope size is
            // implied by the arity.
            return typeArgs.stream()
                .map(arg -> this.countDefaultValue(arg.type(), arg.typeArgs()))
                .reduce(0, Integer::sum);
        }
        if (code instanceof LayoutNullable) {
            // Nullables take the default values of the value plus null. The scope size is implied by the arity.
            return 1 + this.countDefaultValue(typeArgs.get(0).type(), typeArgs.get(0).typeArgs());
        }
        if (code instanceof LayoutUDT) {
            Layout udt = this.resolver.resolve(typeArgs.schemaId());
            return udt.size() + LayoutCode.BYTES;
        }
        throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
    }

    private static int countSparsePath(@Nonnull final RowCursor edit) {

        if (!edit.writePathToken().isNull()) {
            StringToken token = edit.writePathToken();
            ByteBuf varint = token.varint();
            return varint.readerIndex() + varint.readableBytes();
        }

        Optional<StringToken> optional = edit.layout().tokenizer().tryFindToken(edit.writePath());

        if (optional.isPresent()) {
            StringToken token = optional.get();
            edit.writePathToken(token);
            ByteBuf varint = token.varint();
            return varint.readerIndex() + varint.readableBytes();
        }

        Utf8String path = edit.writePath().toUtf8();
        assert path != null;

        int numBytes = path.length();
        int sizeLenInBytes = RowBuffer.count7BitEncodedUInt(edit.layout().tokenizer().count() + numBytes);

        return sizeLenInBytes + numBytes;
    }

    private void ensure(int size) {
        this.buffer.ensureWritable(size);
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the specified value.
     *
     * @param length      The number of bytes needed to encode the value of the field to be written.
     * @param edit        The prepared edit indicating where and in what context the current write will happen.
     * @param type        The type of the field to be written.
     * @param typeArgs    The type arguments of the field to be written.
     * @param options     The kind of edit to be performed.
     * @param metaBytes   On success, the number of bytes needed to encode the metadata of the new field.
     * @param spaceNeeded On success, the number of bytes needed in total to encode the new field and its metadata.
     * @param shift       On success, the number of bytes the length of the row buffer was increased.
     */
    private void ensureSparse(
        final int length,
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutType type,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final RowOptions options,
        @Nonnull final Out<Integer> metaBytes,
        @Nonnull final Out<Integer> spaceNeeded,
        @Nonnull final Out<Integer> shift
    ) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(type, "expected non-null type");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(metaBytes, "expected non-null metaBytes");
        checkNotNull(spaceNeeded, "expected non-null spaceNeeded");
        checkNotNull(shift, "expected non-null shift");

        int metaOffset = edit.metaOffset();
        int spaceAvailable = 0;

        // Compute the metadata offsets

        if (edit.scopeType().hasImplicitTypeCode(edit)) {
            metaBytes.set(0);
        } else {
            metaBytes.set(type.countTypeArgument(typeArgs));
        }

        if (!edit.scopeType().isIndexedScope()) {
            checkState(edit.writePath() != null);
            int pathLenInBytes = RowBuffer.countSparsePath(edit);
            metaBytes.set(metaBytes.get() + pathLenInBytes);
        }

        if (edit.exists()) {
            // Compute value offset for existing value to be overwritten
            spaceAvailable = this.sparseComputeSize(edit);
        }

        spaceNeeded.set(options == RowOptions.DELETE ? 0 : metaBytes.get() + length);
        shift.set(spaceNeeded.get() - spaceAvailable);

        // Shift the contents of the buffer tail left or right as required to snugly fit the specified value

        final int destination = metaOffset + spaceNeeded.get();
        final int source = metaOffset + spaceAvailable;

        this.shift(destination, source, this.length() - (metaOffset + spaceAvailable));

        // Update the stored size (fixed arity scopes don't store the size because it is implied by the type args)

        if (edit.scopeType().isSizedScope() && !edit.scopeType().isFixedArity()) {

            if ((options == RowOptions.INSERT) || (options == RowOptions.INSERT_AT) || ((options == RowOptions.UPSERT) && !edit.exists())) {
                // Add one to the current scope count
                checkState(!edit.exists());
                this.incrementUInt32(edit.start(), 1);
                edit.count(edit.count() + 1);
            } else if ((options == RowOptions.DELETE) && edit.exists()) {
                // Subtract one from the current scope count
                checkState(this.readUInt32(edit.start()) > 0);
                this.decrementUInt32(edit.start(), 1);
                edit.count(edit.count() - 1);
            }
        }

        if (options == RowOptions.DELETE) {
            edit.cellType(null);
            edit.cellTypeArgs(null);
            edit.exists(false);
        } else {
            edit.cellType(type);
            edit.cellTypeArgs(typeArgs);
            edit.exists(true);
        }
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the specified value.
     *
     * @param length      The number of bytes needed to encode the value of the field to be written.
     * @param edit        The prepared edit indicating where and in what context the current write will happen.
     * @param type        The type of the field to be written.
     * @param typeArgs    The type arguments of the field to be written.
     * @param options     The kind of edit to be performed.
     * @param metaBytes   On success, the number of bytes needed to encode the metadata of the new field.
     * @param spaceNeeded On success, the number of bytes needed in total to encode the new field and its metadata.
     * @param shift       On success, the number of bytes the length of the row buffer was increased.
     */
    private void ensureSparse(
        final int length,
        @Nonnull final RowCursor edit,
        @Nonnull final LayoutType type,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options,
        @Nonnull final Out<Integer> metaBytes,
        @Nonnull final Out<Integer> spaceNeeded,
        @Nonnull final Out<Integer> shift
    ) {
        checkNotNull(options, "expected non-null options");
        this.ensureSparse(length, edit, type, typeArgs, RowOptions.from(options.value()), metaBytes, spaceNeeded, shift);
    }

    private void ensureVariable(
        final int offset,
        final boolean isVarint,
        final int length,
        final boolean exists,
        @Nonnull final Out<Integer> spaceNeeded,
        @Nonnull final Out<Integer> shift) {

        int spaceAvailable = 0;
        long existingValueBytes = exists ? 0 : this.read7BitEncodedUInt(offset);

        if (isVarint) {
            spaceNeeded.set(length);
        } else {
            assert existingValueBytes <= Integer.MAX_VALUE;
            spaceAvailable += (int) existingValueBytes;
            spaceNeeded.set(length + RowBuffer.count7BitEncodedUInt(length));
        }

        shift.set(spaceNeeded.get() - spaceAvailable);

        if (shift.get() != 0) {
            final int destination = offset + spaceNeeded.get();
            final int source = offset + spaceAvailable;
            this.shift(destination, source, this.length() - (offset + spaceAvailable));
        }
    }

    /**
     * Sorts a {@code uniqueIndex} list using the hybrid row binary collation.
     *
     * @param scope       The scope to be sorted.
     * @param edit     A edit that points at the scope.
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
    private boolean insertionSort(
        @Nonnull final RowCursor scope,
        @Nonnull final RowCursor edit,
        @Nonnull final List<UniqueIndexItem> uniqueIndex) {

        checkNotNull(scope, "expected non-null scope");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(uniqueIndex, "expected non-null uniqueIndex");

        RowCursor leftEdit = edit.clone();
        RowCursor rightEdit = edit.clone();

        for (int i = 1; i < uniqueIndex.size(); i++) {

            UniqueIndexItem x = uniqueIndex.get(i);
            leftEdit.cellType(LayoutType.fromLayoutCode(x.code()));
            leftEdit.metaOffset(x.metaOffset());
            leftEdit.valueOffset(x.valueOffset());

            final int leftBytes = x.size() - (x.valueOffset() - x.metaOffset());

            // Walk backwards searching for the insertion point for the item as position i.
            int j;
            for (j = i - 1; j >= 0; j--) {
                UniqueIndexItem y = uniqueIndex.get(j);
                rightEdit.cellType(LayoutType.fromLayoutCode(y.code()));
                rightEdit.metaOffset(y.metaOffset());
                rightEdit.valueOffset(y.valueOffset());

                int cmp;
                if (scope.scopeType() instanceof LayoutTypedMap) {
                    cmp = this.compareKeyValueFieldValue(leftEdit.clone(), rightEdit.clone());
                } else {
                    int rightBytes = y.size() - (y.valueOffset() - y.metaOffset());
                    cmp = this.compareFieldValue(leftEdit.clone(), leftBytes, rightEdit.clone(), rightBytes);
                }

                // If there are duplicates then fail.
                if (cmp == 0) {
                    return false;
                }

                if (cmp > 0) {
                    break;
                }

                // Swap the jth item to the right to make space for the ith item which is smaller.
                uniqueIndex.set(j + 1, uniqueIndex.get(j));
            }

            // Insert the ith item into the sorted array.
            uniqueIndex.set(j + 1, x);
        }

        return true;
    }

    private <T> Item<T> read(@Nonnull final Supplier<T> reader, @Nonnull final RowCursor cursor) {

        checkNotNull(reader, "expected non-null reader");
        checkNotNull(cursor, "expected non-null cursor");

        Item<T> item = this.read(reader, cursor.valueOffset());
        cursor.endOffset(this.buffer.readerIndex());

        return item;
    }

    private <T> Item<T> read(@Nonnull final Supplier<T> reader, final int offset) {

        checkNotNull(reader, "expected non-null reader");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        this.buffer.readerIndex(offset);
        final T value = reader.get();

        return Item.of(value, offset, this.buffer.readerIndex() - offset);
    }

    private <T> Item<T> read(@Nonnull final Function<Integer, T> reader, final int offset, final int length) {

        checkNotNull(reader, "expected non-null reader");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);
        checkArgument(length >= 0, "expected non-negative length, not %s", length);

        this.buffer.readerIndex(offset);
        final T value = reader.apply(length);
        final int actualLength = this.buffer.readerIndex() - offset;

        checkState(actualLength == length, "expected read of length %s, not %s", length, actualLength);
        return Item.of(value, offset, actualLength);
    }

    private long read7BitEncodedInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedInt, offset);
        return item.value();
    }

    private long read7BitEncodedInt() {
        return RowBuffer.rotateSignToMsb(this.read7BitEncodedUInt());
    }

    private long read7BitEncodedUInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        return item.value();
    }

    private long read7BitEncodedUInt() {

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

    private BigDecimal readDecimal() {
        return DecimalCodec.decode(this.buffer);
    }

    private Utf8String readFixedString(int length) {
        return Utf8String.fromUnsafe(this.buffer.readSlice(length));
    }

    private Float128 readFloat128() {
        return Float128Codec.decode(this.buffer);
    }

    private UUID readGuid() {
        return GuidCodec.decode(this.buffer);
    }

    private HybridRowHeader readHeader() {
        HybridRowVersion version = HybridRowVersion.from(this.buffer.readByte());
        SchemaId schemaId = SchemaId.from(this.buffer.readIntLE());
        return new HybridRowHeader(version, schemaId);
    }

    /**
     * Read the metadata of an encoded sparse field.
     *
     * @param edit The edit structure to fill in.
     *
     *             {@code edit.Path}
     *             On success, the path of the field at the given offset, otherwise
     *             undefined.
     *
     *             {@code edit.MetaOffset}
     *             On success, the offset to the metadata of the field, otherwise a
     *             location to insert the field.
     *
     *             {@code edit.cellType}
     *             On success, the layout code of the existing field, otherwise
     *             undefined.
     *
     *             {@code edit.TypeArgs}
     *             On success, the type args of the existing field, otherwise
     *             undefined.
     *
     *             {@code edit.ValueOffset}
     *             On success, the offset to the value of the field, otherwise
     *             undefined.
     *.
     */
    private void readSparseMetadata(@Nonnull final RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        if (edit.scopeType().hasImplicitTypeCode(edit)) {

            edit.scopeType().setImplicitTypeCode(edit);
            edit.valueOffset(edit.metaOffset());

        } else {

            int metaOffset = edit.metaOffset();
            LayoutType layoutType = this.readSparseTypeCode(metaOffset);

            edit.cellType(layoutType);
            edit.valueOffset(metaOffset + LayoutCode.BYTES);
            edit.cellTypeArgs(TypeArgumentList.EMPTY);

            if (edit.cellType() instanceof LayoutEndScope) {
                // Reached end of current scope without finding another field.
                edit.pathToken(0);
                edit.pathOffset(0);
                edit.valueOffset(edit.metaOffset());
                return;
            }

            Out<Integer> lengthInBytes = new Out<>();
            edit.cellTypeArgs(edit.cellType().readTypeArgumentList(this, edit.valueOffset(), lengthInBytes));
            edit.valueOffset(edit.valueOffset() + lengthInBytes.get());
        }

        edit.scopeType().readSparsePath(this, edit);
    }

    private void readSparsePrimitiveTypeCode(@Nonnull final RowCursor edit, @Nonnull final LayoutType code) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(code, "expected non-null code");
        checkArgument(edit.exists(), "expected edit.exists value of true, not false");

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
            if (code == LayoutTypes.BOOLEAN) {
                final LayoutType layoutType = this.readSparseTypeCode(edit.metaOffset());
                checkState(layoutType == LayoutTypes.BOOLEAN || layoutType == LayoutTypes.BOOLEAN_FALSE);
            } else {
                checkState(this.readSparseTypeCode(edit.metaOffset()) == code);
            }
        }

        if (edit.scopeType().isIndexedScope()) {
            checkState(edit.pathOffset() == 0);
            checkState(edit.pathToken() == 0);
        } else {
            int offset = edit.metaOffset() + LayoutCode.BYTES;
            Out<Integer> pathLenInBytes = new Out<>();
            Out<Integer> pathOffset = new Out<>();
            int token = this.readSparsePathLen(edit.layout(), offset, pathOffset, pathLenInBytes);
            checkState(edit.pathOffset() == pathOffset.get());
            checkState(edit.pathToken() == token);
        }
    }

    private UnixDateTime readUnixDateTime() {
        return new UnixDateTime(this.buffer.readLongLE());
    }

    private Utf8String readUtf8String() {
        long length = this.read7BitEncodedUInt();
        checkState(length <= Integer.MAX_VALUE, "expected length <= %s, not %s", Integer.MAX_VALUE, length);
        return Utf8String.fromUnsafe(this.buffer.readSlice((int)length));
    }

    private ByteBuf readVariableBinary() {
        long length = this.read7BitEncodedUInt();
        checkState(length <= Integer.MAX_VALUE, "expected length <= %s, not %s", Integer.MAX_VALUE, length);
        return this.buffer.readSlice((int)length).asReadOnly();
    }

    private void shift(int destination, int source, int length) {
        if (source != destination) {
            if (length > 0) {
                this.buffer.setBytes(destination, this.buffer, source, length);
            }
            this.buffer.writerIndex(destination + length);
        }
    }

    /**
     * Skip over a nested scope.
     *
     * @param edit The sparse scope to search
     * @return The zero-based byte offset immediately following the scope end marker
     */
    private int skipScope(RowCursor edit) {

        //noinspection StatementWithEmptyBody
        while (this.sparseIteratorMoveNext(edit)) {
        }

        if (!edit.scopeType().isSizedScope()) {
            edit.metaOffset(edit.metaOffset() + LayoutCode.BYTES); // move past end of scope marker
        }

        return edit.metaOffset();
    }

    /**
     * Compute the size of a sparse (primitive) field.
     *
     * @param type    The type of the current sparse field.
     * @param metaOffset  The zero-based offset from the beginning of the row where the field begins.
     * @param valueOffset The zero-based offset from the beginning of the row where the field's value begins.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int sparseComputePrimitiveSize(LayoutType type, int metaOffset, int valueOffset) {

        // TODO: JTH: convert to a virtual?

        int metaBytes = valueOffset - metaOffset;
        LayoutCode code = type.layoutCode();

        switch (code) {
            case NULL:
                checkState(LayoutTypes.NULL.size() == 0);
                return metaBytes;

            case BOOLEAN:
            case BOOLEAN_FALSE:
                checkState(LayoutTypes.BOOLEAN.size() == 0);
                return metaBytes;

            case INT_8:
                return metaBytes + LayoutTypes.INT_8.size();

            case INT_16:
                return metaBytes + LayoutTypes.INT_16.size();

            case INT_32:
                return metaBytes + LayoutTypes.INT_32.size();

            case INT_64:
                return metaBytes + LayoutTypes.INT_64.size();

            case UINT_8:
                return metaBytes + LayoutTypes.UINT_8.size();

            case UINT_16:
                return metaBytes + LayoutTypes.UINT_16.size();

            case UINT_32:
                return metaBytes + LayoutTypes.UINT_32.size();

            case UINT_64:
                return metaBytes + LayoutTypes.UINT_64.size();

            case FLOAT_32:
                return metaBytes + LayoutTypes.FLOAT_32.size();

            case FLOAT_64:
                return metaBytes + LayoutTypes.FLOAT_64.size();

            case FLOAT_128:
                return metaBytes + LayoutTypes.FLOAT_128.size();

            case DECIMAL:
                return metaBytes + LayoutTypes.DECIMAL.size();

            case DATE_TIME:
                return metaBytes + LayoutTypes.DATE_TIME.size();

            case UNIX_DATE_TIME:
                return metaBytes + LayoutTypes.UNIX_DATE_TIME.size();

            case GUID:
                return metaBytes + LayoutTypes.GUID.size();

            case MONGODB_OBJECT_ID:
                // return metaBytes + MongoDbObjectId.size();
                throw new UnsupportedOperationException();

            case UTF_8:
            case BINARY: {
                Item<Long> item = this.read(this::read7BitEncodedUInt, metaOffset + metaBytes);
                return metaBytes + item.length() + item.value().intValue();
            }
            case VAR_INT:
            case VAR_UINT: {
                Item<Long> item = this.read(this::read7BitEncodedUInt, metaOffset + metaBytes);
                return metaBytes + item.length();
            }
            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
        }
    }

    /**
     * Compute the size of a sparse field.
     *
     * @param edit The edit structure describing the field to measure.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int sparseComputeSize(RowCursor edit) {

        if (!(edit.cellType() instanceof LayoutTypeScope)) {
            return this.sparseComputePrimitiveSize(edit.cellType(), edit.metaOffset(), edit.valueOffset());
        }

        // Compute offset to end of value for current value
        RowCursor newScope = this.sparseIteratorReadScope(edit, true);
        return this.skipScope(newScope) - edit.metaOffset();
    }

    /**
     * Reads and validates the header of the current {@link RowBuffer}.
     *
     * @return {@code true} if the header validation succeeded; otherwise, if the header is invalid, {@code false}
     */
    private boolean validateHeader(@Nonnull final HybridRowVersion version) {

        checkNotNull(version, "expected non-null version");

        final Item<HybridRowHeader> item = this.read(this::readHeader, 0);
        final HybridRowHeader header = item.value();
        final Layout layout = this.resolver.resolve(header.schemaId());

        checkState(header.schemaId().equals(layout.schemaId()));
        return header.version().equals(version) && (HybridRowHeader.BYTES + layout.size()) <= this.length();
    }

    private <T> Item<T> write(@Nonnull final Consumer<T> consumer, final int offset, @Nonnull final T value) {

        checkNotNull(consumer, "expected non-null consumer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected non-negative offset");

        final int priorWriterIndex = this.buffer.writerIndex();
        this.buffer.writerIndex(offset);
        final int length;

        try {
            consumer.accept(value);
            length = this.buffer.writerIndex() - offset;
        } finally {
            if (priorWriterIndex > this.buffer.writerIndex()) {
                this.buffer.writerIndex(priorWriterIndex);
            }
        }

        return new Item<>(value, offset, length);
    }

    private void writeDateTime(OffsetDateTime value) {
        DateTimeCodec.encode(value, this.buffer);
    }

    private void writeDecimal(BigDecimal value) {
        DecimalCodec.encode(value, this.buffer);
    }

    private int writeDefaultValue(int offset, LayoutType code, TypeArgumentList typeArgs) {

        // TODO: DANOBLE: Put default values in a central location (LayoutTypes?) and use them in this method
        //   ensure that there are no null default values (which this method currently uses)
        // TODO: JTH: convert to a virtual?

        if (code == LayoutTypes.NULL) {
            this.writeSparseTypeCode(offset, code.layoutCode());
            return 1;
        }

        if (code == LayoutTypes.BOOLEAN) {
            this.writeSparseTypeCode(offset, LayoutCode.BOOLEAN_FALSE);
            return 1;
        }

        if (code == LayoutTypes.INT_8) {
            this.writeInt8(offset, (byte) 0);
            return LayoutTypes.INT_8.size();
        }

        if (code == LayoutTypes.INT_16) {
            this.writeInt16(offset, (short) 0);
            return LayoutTypes.INT_16.size();
        }

        if (code == LayoutTypes.INT_32) {
            this.writeInt32(offset, 0);
            return LayoutTypes.INT_32.size();
        }

        if (code == LayoutTypes.INT_64) {
            this.writeInt64(offset, 0);
            return LayoutTypes.INT_64.size();
        }

        if (code == LayoutTypes.UINT_8) {
            this.writeUInt8(offset, (byte) 0);
            return LayoutTypes.UINT_8.size();
        }

        if (code == LayoutTypes.UINT_16) {
            this.writeUInt16(offset, (short) 0);
            return LayoutTypes.UINT_16.size();
        }

        if (code == LayoutTypes.UINT_32) {
            this.writeUInt32(offset, 0);
            return LayoutTypes.UINT_32.size();
        }

        if (code == LayoutTypes.UINT_64) {
            this.writeUInt64(offset, 0);
            return LayoutTypes.UINT_64.size();
        }

        if (code == LayoutTypes.FLOAT_32) {
            this.writeFloat32(offset, 0);
            return LayoutTypes.FLOAT_32.size();
        }

        if (code == LayoutTypes.FLOAT_64) {
            this.writeFloat64(offset, 0);
            return LayoutTypes.FLOAT_64.size();
        }

        if (code == LayoutTypes.FLOAT_128) {
            this.writeFloat128(offset, Float128.ZERO);
            return LayoutTypes.FLOAT_128.size();
        }

        if (code == LayoutTypes.DECIMAL) {
            this.writeDecimal(offset, BigDecimal.ZERO);
            return LayoutTypes.DECIMAL.size();
        }

        if (code == LayoutTypes.DATE_TIME) {
            this.writeDateTime(offset, OffsetDateTime.MIN);
            return LayoutTypes.DATE_TIME.size();
        }

        if (code == LayoutTypes.UNIX_DATE_TIME) {
            this.writeUnixDateTime(offset, UnixDateTime.EPOCH);
            return LayoutTypes.UNIX_DATE_TIME.size();
        }

        if (code == LayoutTypes.GUID) {
            this.writeGuid(offset, GuidCodec.EMPTY);
            return LayoutTypes.GUID.size();
        }

        if (code == LayoutTypes.MONGODB_OBJECT_ID) {
            // TODO: DANOBLE: Add support for LayoutTypes.MONGODB_OBJECT_ID
            // this.writeMongoDbObjectId(offset, null);
            // return MongoDbObjectId.Size;
            throw new UnsupportedOperationException();
        }

        if (code == LayoutTypes.UTF_8 || code == LayoutTypes.BINARY || code == LayoutTypes.VAR_INT || code == LayoutTypes.VAR_UINT) {
            // Variable length types preceded by their varuint size take 1 byte for a size of 0
            return this.write(this::write7BitEncodedUInt, offset, 0L).length();
        }

        if (code == LayoutTypes.OBJECT || code == LayoutTypes.ARRAY) {
            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
            return LayoutCode.BYTES;
        }

        if (code == LayoutTypes.TYPED_ARRAY || code == LayoutTypes.TYPED_SET || code == LayoutTypes.TYPED_MAP) {
            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            this.writeUInt32(offset, 0);
            return Integer.BYTES;
        }

        if (code == LayoutTypes.TUPLE) {
            // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
            for (int i = 0; i < typeArgs.count(); i++) {
                this.writeSparseTypeCode(offset, LayoutCode.NULL);
            }
            this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
            return LayoutCode.BYTES + (LayoutCode.BYTES * typeArgs.count());
        }

        if (code == LayoutTypes.TYPED_TUPLE || code == LayoutTypes.TAGGED || code == LayoutTypes.TAGGED_2) {
            // Fixed arity typed collections take the sum of the default values of each element. The scope size is
            // implied by the arity.
            int sum = 0;
            for (final Iterator<TypeArgument> iterator = typeArgs.stream().iterator(); iterator.hasNext(); ) {
                final TypeArgument arg = iterator.next();
                sum += this.writeDefaultValue(offset + sum, arg.type(), arg.typeArgs());
            }
            return sum;
        }

        if (code == LayoutTypes.NULLABLE) {
            // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
            this.writeInt8(offset, (byte) 0);
            return 1 + this.writeDefaultValue(offset + 1, typeArgs.get(0).type(), typeArgs.get(0).typeArgs());
        }

        if (code == LayoutTypes.UDT) {

            // Clear all presence bits
            Layout udt = this.resolver.resolve(typeArgs.schemaId());
            this.write(this.buffer::writeZero, offset, udt.size());

            // Write scope terminator
            this.writeSparseTypeCode(offset + udt.size(), LayoutCode.END_SCOPE);
            return udt.size() + LayoutCode.BYTES;
        }

        throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
    }

    private void writeFixedString(Utf8String value) {
        this.buffer.writeBytes(value.content(), value.encodedLength());
    }

    private void writeGuid(UUID value) {
        GuidCodec.encode(value, this.buffer);
    }

    private void writeSparseMetadata(
        @Nonnull final RowCursor edit, @Nonnull final LayoutType cellType, @Nonnull final TypeArgumentList typeArgs,
        final int metaBytes) {

        int metaOffset = edit.metaOffset();

        if (!edit.scopeType().hasImplicitTypeCode(edit)) {
            metaOffset += cellType.writeTypeArgument(this, metaOffset, typeArgs);
        }

        this.writeSparsePath(edit, metaOffset);
        edit.valueOffset(edit.metaOffset() + metaBytes);

        checkState(edit.valueOffset() == edit.metaOffset() + metaBytes);
    }

    private void writeSparsePath(@Nonnull final RowCursor edit, final int offset) {

        checkNotNull(edit, "expected non-null edit");
        checkArgument(offset >= 0, "expected non-negative offset");

        // Some scopes don't encode paths, therefore the cost is always zero

        if (edit.scopeType().isIndexedScope()) {
            edit.pathToken(0);
            edit.pathOffset(0);
            return;
        }

        final StringTokenizer tokenizer = edit.layout().tokenizer();
        final Optional<StringToken> writePathToken = tokenizer.tryFindToken(edit.writePath());

        checkState(!(writePathToken.isPresent() && edit.writePathToken().isNull()));

        if (!edit.writePathToken().isNull()) {
            this.write(this.buffer::writeBytes, offset, edit.writePathToken().varint());
            edit.pathToken((int) edit.writePathToken().id());
            edit.pathOffset(offset);
        } else {
            // TODO: It would be better if we could avoid allocating here when the path is UTF16
            Utf8String writePath = edit.writePath().toUtf8();
            checkState(writePath != null);
            edit.pathToken(tokenizer.count() + writePath.encodedLength());
            Item<Long> item = this.write(this::write7BitEncodedUInt, offset, (long) edit.pathToken());
            edit.pathOffset(offset + item.length());
            this.write(this::writeFixedString, edit.pathOffset(), writePath);
        }
    }


    private void writeUInt16(Short value) {
        this.buffer.writeShortLE(value);
    }

    private void writeUInt32(Integer value) {
        this.buffer.writeIntLE(value);
    }

    private void writeUInt64(Long value) {
        this.buffer.writeLongLE(value);
    }

    private void writeUInt8(Byte value) {
        this.buffer.writeByte(value);
    }

    private int writeVariableBinary(int offset, ByteBuf value) {
        Item<ByteBuf> item = this.write(this::writeVariableBinary, offset, value);
        return item.length();
    }

    private void writeVariableBinary(ByteBuf value) {
        this.write7BitEncodedUInt(value.readableBytes());
        this.buffer.writeBytes(value);
    }

    private void writeVariableString(@Nonnull final Utf8String value) {
        final int length = this.write7BitEncodedUInt((long) value.encodedLength());
        assert length == value.encodedLength();
        assert value.content() != null;
        this.buffer.writeBytes(value.content().readerIndex(0));
    }

    private static class Item<T> {

        private int length;
        private int offset;
        private T value;

        private Item(T value, int offset, int length) {
            this.value = value;
            this.offset = offset;
            this.length = length;
        }

        public int length() {
            return this.length;
        }

        public static <T> Item<T> of(T value, int offset, int length) {
            return new Item<>(value, offset, length);
        }

        public int offset() {
            return this.offset;
        }

        public T value() {
            return this.value;
        }
    }

    /**
     * Represents a single item within a set/map scope that needs to be indexed.
     * <p>
     * This structure is used when rebuilding a set/map index during row streaming via {@link RowWriter}. Each item
     * encodes its offsets and length within the row.
     */
    static final class UniqueIndexItem {

        private LayoutCode code = LayoutCode.values()[0];
        private int metaOffset;
        private int size;
        private int valueOffset;

        /**
         * The layout code of the value.
         */
        public LayoutCode code() {
            return this.code;
        }

        public UniqueIndexItem code(LayoutCode code) {
            this.code = code;
            return this;
        }

        /**
         * If existing, the offset to the metadata of the existing field, otherwise the location to insert a new field.
         */
        public int metaOffset() {
            return this.metaOffset;
        }

        public UniqueIndexItem metaOffset(int metaOffset) {
            this.metaOffset = metaOffset;
            return this;
        }

        /**
         * Size of the target element.
         */
        public int size() {
            return this.size;
        }

        public UniqueIndexItem size(int size) {
            this.size = size;
            return this;
        }

        /**
         * If existing, the offset to the value of the existing field, otherwise undefined.
         */
        public int valueOffset() {
            return this.valueOffset;
        }

        public UniqueIndexItem valueOffset(int valueOffset) {
            this.valueOffset = valueOffset;
            return this;
        }
    }
}