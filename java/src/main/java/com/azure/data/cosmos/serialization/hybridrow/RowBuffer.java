// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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
import com.azure.data.cosmos.serialization.hybridrow.layouts.StringTokenizer;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.layouts.UpdateOptions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public RowBuffer(
        @Nonnull final ByteBuf buffer, @Nonnull final HybridRowVersion version,
        @Nonnull final LayoutResolver resolver) {

        checkNotNull(buffer, "buffer");
        checkNotNull(version, "version");
        checkNotNull(resolver, "resolver");
        checkArgument(buffer.isReadable(HybridRowHeader.BYTES));

        this.buffer = buffer;
        this.resolver = resolver;

        HybridRowHeader header = this.readHeader();
        checkState(header.version() == version, "expected version %s, not %s", version, header.version());

        Layout layout = resolver.resolve(header.schemaId());
        checkState(header.schemaId().equals(layout.schemaId()));
        checkState(HybridRowHeader.BYTES + layout.size() <= this.length());
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
            if (this.readBit(scopeOffset, col.getNullBit().clone())) {
                int lengthSizeInBytes;
                Out<Integer> tempOut_lengthSizeInBytes = new Out<Integer>();
                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: ulong valueSizeInBytes = this.Read7BitEncodedUInt(offset, out int lengthSizeInBytes);
                long valueSizeInBytes = this.read7BitEncodedUInt(offset);
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

    /**
     * Delete the sparse field at the indicated path.
     *
     * @param edit The field to delete.
     */
    public void DeleteSparse(RowCursor edit) {
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
            RowOptions.DELETE, tempOut__, tempOut__2, tempOut_shift);
        shift = tempOut_shift.get();
        _ = tempOut__2.get();
        _ = tempOut__.get();
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

        this.writeSparseMetadata(dstEdit, srcEdit.get().cellType(), srcEdit.get().cellTypeArgs().clone(), metaBytes);
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
            RowOptions.DELETE, tempOut_metaBytes2, tempOut_spaceNeeded2, tempOut_shiftDelete);
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
            return Result.SUCCESS;
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

        return Result.SUCCESS;
    }

    public void WriteNullable(Reference<RowCursor> edit, LayoutScope scopeType, TypeArgumentList typeArgs,
                              UpdateOptions options, boolean hasValue, Out<RowCursor> newScope) {
        int numBytes = this.countDefaultValue(scopeType, typeArgs);
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, scopeType, typeArgs, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.writeSparseMetadata(edit, scopeType, typeArgs, metaBytes);
        int numWritten = this.writeDefaultValue(edit.get().valueOffset(), scopeType, typeArgs);
        checkState(numBytes == numWritten);
        checkState(spaceNeeded == metaBytes + numBytes);
        if (hasValue) {
            this.writeInt8(edit.get().valueOffset(), (byte) 1);
        }

        int valueOffset = edit.get().valueOffset() + 1;
        newScope.setAndGet(new RowCursor());
        newScope.get().scopeType(scopeType);
        newScope.get().scopeTypeArgs(typeArgs);
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

    public void WriteSparseBinary(
        @Nonnull final RowCursor edit, @Nonnull final ByteBuf value, @Nonnull final UpdateOptions options) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        final LayoutType layoutType = LayoutTypes.BINARY;
        final int readableBytes = value.readableBytes();
        final int length = RowBuffer.count7BitEncodedUInt(readableBytes) + readableBytes;

        Out<Integer> metaBytes = new Out<>();
        Out<Integer> shift = new Out<>();
        Out<Integer> spaceNeeded = new Out<>();

        this.EnsureSparse(edit, layoutType, TypeArgumentList.EMPTY, length, options, metaBytes, spaceNeeded, shift);
        this.writeSparseMetadata(edit, layoutType, TypeArgumentList.EMPTY, metaBytes.get());
        this.WriteBinary(edit.valueOffset(), value);

        checkState(spaceNeeded.get() == metaBytes.get() + length);

        edit.endOffset(edit.metaOffset() + spaceNeeded.get());
    }

    public void WriteSparseBinary(Reference<RowCursor> edit, ReadOnlySequence<Byte> value,
                                  UpdateOptions options) {
        int len = (int) value.Length;
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
        this.writeSparseMetadata(edit, LayoutType.Binary, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.WriteBinary(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
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
        this.writeSparseMetadata(edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.EMPTY,
            metaBytes);
        checkState(spaceNeeded == metaBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseDateTime(RowCursor edit, OffsetDateTime value, UpdateOptions options) {
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
        this.writeSparseMetadata(edit, LayoutType.DateTime, TypeArgumentList.EMPTY, metaBytes);
        this.writeDateTime(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, LayoutType.Decimal, TypeArgumentList.EMPTY, metaBytes);
        this.writeDecimal(edit.get().valueOffset(), value);
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        checkState(spaceNeeded == metaBytes + sizeof(BigDecimal));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseFloat128(Reference<RowCursor> edit, Float128 value, UpdateOptions options) {
        int numBytes = Float128.SIZE;
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
        this.writeSparseMetadata(edit, LayoutType.Float128, TypeArgumentList.EMPTY, metaBytes);
        this.writeFloat128(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + Float128.SIZE);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    public void WriteSparseFloat32(@Nonnull RowCursor edit, float value, @Nonnull UpdateOptions options) {

        int numBytes = (Float.SIZE / Byte.SIZE);
        int metaBytes;

        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        this.EnsureSparse(edit, LayoutTypes.FLOAT_32, TypeArgumentList.EMPTY, numBytes, options, tempOut_metaBytes,
            tempOut_spaceNeeded, tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.writeSparseMetadata(edit, LayoutType.Float32, TypeArgumentList.EMPTY, metaBytes);
        this.writeFloat32(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Float.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, LayoutType.Float64, TypeArgumentList.EMPTY, metaBytes);
        this.writeFloat64(edit.get().valueOffset(), value);
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
        this.writeSparseMetadata(edit, LayoutType.Guid, TypeArgumentList.EMPTY, metaBytes);
        this.writeGuid(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + 16);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, LayoutType.Int16, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt16(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Short.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
    }

    // TODO: DANOBLE: resurrect MongoDbObjectId
    //    public MongoDbObjectId ReadMongoDbObjectId(int offset) {
    //        return MemoryMarshal.<MongoDbObjectId>Read(this.buffer.Slice(offset));
    //    }

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
        this.writeSparseMetadata(edit, LayoutType.Int32, TypeArgumentList.EMPTY, metaBytes);
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
        this.writeSparseMetadata(edit, LayoutType.Int64, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt64(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Long.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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

        this.writeSparseMetadata(edit, LayoutType.Int8, TypeArgumentList.EMPTY, metaBytes);
        this.writeInt8(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + (Byte.SIZE / Byte.SIZE));
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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

        this.writeSparseMetadata(edit, MongoDbObjectId, TypeArgumentList.EMPTY, metaBytes);
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
        this.writeSparseMetadata(edit, LayoutType.Null, TypeArgumentList.EMPTY, metaBytes);
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
        this.writeSparseMetadata(edit, scopeType, TypeArgumentList.EMPTY, metaBytes);
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

    // TODO: DANOBLE: resurrect MongoDbObjectId
    //    public MongoDbObjectId ReadSparseMongoDbObjectId(Reference<RowCursor> edit) {
    //        this.readSparsePrimitiveTypeCode(edit, MongoDbObjectId);
    //        edit.get().endOffset = edit.get().valueOffset() + MongoDbObjectId.Size;
    //        return this.ReadMongoDbObjectId(edit.get().valueOffset()).clone();
    //    }

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
        this.writeSparseMetadata(edit, LayoutType.Utf8, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.writeString(edit.get().valueOffset(), value);
        checkState(spaceNeeded == metaBytes + len + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);

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
        this.writeSparseMetadata(edit, LayoutType.UInt16, TypeArgumentList.EMPTY, metaBytes);
        this.writeUInt16(edit.get().valueOffset(), value);
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
        this.writeSparseMetadata(edit, LayoutType.UInt32, TypeArgumentList.EMPTY, metaBytes);
        this.writeUInt32(edit.get().valueOffset(), value);
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
        this.writeSparseMetadata(edit, LayoutType.UInt64, TypeArgumentList.EMPTY, metaBytes);
        this.writeUInt64(edit.get().valueOffset(), value);
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
        this.writeSparseMetadata(edit, LayoutType.UInt8, TypeArgumentList.EMPTY, metaBytes);
        this.writeUInt8(edit.get().valueOffset(), value);
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

        this.writeSparseMetadata(edit, LayoutType.UnixDateTime, TypeArgumentList.EMPTY, metaBytes);
        this.writeUnixDateTime(edit.get().valueOffset(), value.clone());
        checkState(spaceNeeded == metaBytes + 8);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, LayoutType.VarInt, TypeArgumentList.EMPTY, metaBytes);
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
        this.writeSparseMetadata(edit, LayoutType.VarUInt, TypeArgumentList.EMPTY, metaBytes);
        int sizeLenInBytes = this.write7BitEncodedUInt(edit.get().valueOffset(), value);
        checkState(sizeLenInBytes == numBytes);
        checkState(spaceNeeded == metaBytes + sizeLenInBytes);
        edit.get().endOffset = edit.get().metaOffset() + spaceNeeded;
        this.length(this.length() + shift);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.writeUInt32(edit.get().valueOffset(), 0);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.writeUInt32(edit.get().valueOffset(), 0);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        checkState(spaceNeeded == metaBytes + numBytes);
        this.writeUInt32(edit.get().valueOffset(), 0);
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
        this.writeSparseMetadata(edit, scopeType, typeArgs.clone(), metaBytes);
        int numWritten = this.writeDefaultValue(edit.get().valueOffset(), scopeType, typeArgs.clone());
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
        int numBytes = (int) value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.WriteBinary(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length(this.length() + shift.get());
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

    public void WriteVariableString(int offset, Utf8Span value, boolean exists, Out<Integer> shift) {
        int numBytes = value.Length;
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        this.EnsureVariable(offset, false, numBytes, exists, tempOut_spaceNeeded, shift);
        spaceNeeded = tempOut_spaceNeeded.get();

        int sizeLenInBytes = this.writeString(offset, value);
        checkState(spaceNeeded == numBytes + sizeLenInBytes);
        this.length(this.length() + shift.get());
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

    public void decrementUInt32(int offset, long decrement) {
        long value = this.buffer.getUnsignedIntLE(offset);
        this.buffer.setIntLE(offset, (int) (value - decrement));
    }

    public void deleteVariable(int offset, boolean isVarint) {

        int start = this.buffer.readerIndex();
        this.read7BitEncodedUInt();

        ByteBuf remainder = this.buffer.slice(this.buffer.readerIndex(), this.buffer.readableBytes());
        this.buffer.readerIndex(start);
        this.buffer.setBytes(start, remainder);
        this.buffer.writerIndex(start + remainder.readableBytes());
    }

    /**
     * The root header for the row.
     */
    public HybridRowHeader header() {
        return this.readHeader();
    }

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

        checkNotNull(version, "version");
        checkNotNull(layout, "layout");
        checkNotNull(resolver, "resolver");

        this.writeHeader(new HybridRowHeader(version, layout.schemaId()));
        this.buffer.writeZero(layout.size());
        this.resolver = resolver;
    }

    /**
     * The length of this {@link RowBuffer} in bytes.
     */
    public int length() {
        return this.buffer.readerIndex() + this.buffer.readableBytes();
    }

    /**
     * Compute the byte offsets from the beginning of the row for a given sparse field insertion
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
                cmp = this.CompareKeyValueFieldValue(srcEdit, dstEdit);
            } else {
                elmSize = this.sparseComputeSize(dstEdit);
                int elmBytes = elmSize - (dstEdit.valueOffset() - dstEdit.metaOffset());
                cmp = this.CompareFieldValue(srcEdit, srcBytes, dstEdit, elmBytes);
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

    public long read7BitEncodedInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedInt, offset);
        return item.value();
    }

    public long read7BitEncodedUInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        return item.value();
    }

    public boolean readBit(final int offset, @Nonnull final LayoutBit bit) {

        checkNotNull(bit, "expected non-null bit");

        if (bit.isInvalid()) {
            return true;
        }

        Item<Boolean> item = this.read(() -> (this.buffer.readByte() & (byte) (1 << bit.bit())) != 0, bit.offset(offset));
        return item.value();
    }

    public OffsetDateTime readDateTime(int offset) {
        Item<OffsetDateTime> item = this.read(() -> DateTimeCodec.decode(this.buffer), offset);
        return item.value();
    }

    public BigDecimal readDecimal(int offset) {
        Item<BigDecimal> item = this.read(() -> DecimalCodec.decode(this.buffer), offset);
        return item.value();
    }

    public ByteBuf readFixedBinary(int offset, int length) {
        Item<ByteBuf> item = this.read(() -> this.buffer.readSlice(length), offset);
        return item.value();
    }

    public Utf8String readFixedString(int offset, int length) {
        Item<Utf8String> item = this.read(this::readFixedString, offset, length);
        return item.value();
    }

    public Float128 readFloat128(int offset) {
        Item<Float128> item = this.read(this::readFloat128, offset);
        return item.value();
    }

    public float readFloat32(int offset) {
        Item<Float> item = this.read(this.buffer::readFloatLE, offset);
        return item.value();
    }

    public double readFloat64(int offset) {
        Item<Double> item = this.read(this.buffer::readDoubleLE, offset);
        return item.value();
    }

    /**
     * Reads in the contents of the current {@link RowBuffer} from an {@link InputStream}
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
     * Reads the contents of the current {@link RowBuffer} from a {@link ByteBuf}
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

    public UUID readGuid(int offset) {
        return this.read(() -> GuidCodec.decode(this.buffer), offset).value();
    }

    public HybridRowHeader readHeader(int offset) {
        return this.read(this::readHeader, offset).value();
    }

    public short readInt16(int offset) {
        return this.read(this.buffer::readShortLE, offset).value();
    }

    public int readInt32(int offset) {
        Item<Integer> item = this.read(this.buffer::readIntLE, offset);
        return item.value();
    }

    public long readInt64(int offset) {
        Item<Long> item = this.read(this.buffer::readLongLE, offset);
        return item.value();
    }

    public byte readInt8(int offset) {
        Item<Byte> item = this.read(this.buffer::readByte, offset);
        return item.value();
    }

    public SchemaId readSchemaId(int offset) {
        Item<SchemaId> item = this.read(() -> SchemaId.from(this.buffer.readIntLE()), offset);
        return item.value();
    }

    // TODO: DANOBLE: Support MongoDbObjectId values
    //    public void WriteMongoDbObjectId(int offset, MongoDbObjectId value) {
    //        Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId> tempReference_value =
    //            new Reference<azure.data.cosmos.serialization.hybridrow.MongoDbObjectId>(value);
    //        MemoryMarshal.Write(this.buffer.Slice(offset), tempReference_value);
    //        value = tempReference_value.get();
    //    }

    public ByteBuf readSparseBinary(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.BINARY);
        Item<ByteBuf> item = this.read(this::readVariableBinary, edit);
        return item.value();
    }

    public boolean readSparseBoolean(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.BOOLEAN);
        edit.endOffset(edit.valueOffset());
        return edit.cellType() == LayoutTypes.BOOLEAN;
    }

    public OffsetDateTime readSparseDateTime(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.DATE_TIME);
        edit.endOffset(edit.valueOffset() + Long.SIZE);
        return this.readDateTime(edit.valueOffset());
    }

    public BigDecimal readSparseDecimal(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.DECIMAL);
        Item<BigDecimal> item = this.read(this::readDecimal, edit);
        return item.value();
    }

    public Float128 readSparseFloat128(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_128);
        Item<Float128> item = this.read(this::readFloat128, edit);
        return item.value();
    }

    public float readSparseFloat32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_32);
        Item<Float> item = this.read(this.buffer::readFloatLE, edit);
        return item.value();
    }

    public double readSparseFloat64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.FLOAT_64);
        Item<Double> item = this.read(this.buffer::readDoubleLE, edit);
        return item.value();
    }

    public UUID readSparseGuid(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.GUID);
        Item<UUID> item = this.read(this::readGuid, edit);
        return item.value();
    }

    public short readSparseInt16(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_16);
        Item<Short> item = this.read(this.buffer::readShortLE, edit);
        return item.value();
    }

    public int readSparseInt32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_32);
        Item<Integer> item = this.read(this.buffer::readIntLE, edit);
        return item.value();
    }

    public long readSparseInt64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_64);
        Item<Long> item = this.read(this.buffer::readLongLE, edit);
        return item.value();
    }

    public byte readSparseInt8(RowCursor edit) {
        // TODO: Remove calls to readSparsePrimitiveTypeCode once moved to V2 read.
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.INT_8);
        Item<Byte> item = this.read(this.buffer::readByte, edit);
        return item.value();
    }

    public NullValue readSparseNull(@Nonnull RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.NULL);
        edit.endOffset(edit.valueOffset());

        return NullValue.Default;
    }

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

    public int readSparsePathLen(
        @Nonnull final Layout layout, final int offset, @Nonnull final Out<Integer> pathLenInBytes,
        @Nonnull final Out<Integer> pathOffset) {

        checkNotNull(layout, "expected non-null layout");
        checkNotNull(pathOffset, "expected non-null pathOffset");
        checkNotNull(pathLenInBytes, "expected non-null pathLenInBytes");

        final Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
        final int token = item.value().intValue();

        if (token < layout.tokenizer().count()) {
            pathLenInBytes.set(item.length());
            pathOffset.set(offset);
            return token;
        }

        final int numBytes = token - layout.tokenizer().count();
        pathLenInBytes.set(numBytes + item.length());
        pathOffset.set(offset + item.length());
        return token;
    }

    public Utf8String readSparseString(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UTF_8);
        Item<Utf8String> item = this.read(this::readUtf8String, edit);
        return item.value();
    }

    public LayoutType readSparseTypeCode(int offset) {
        return LayoutType.fromCode(LayoutCode.forValue(this.readInt8(offset)));
    }

    public int readSparseUInt16(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_16);
        Item<Integer> item = this.read(this.buffer::readUnsignedShortLE, edit);
        return item.value();
    }

    public long readSparseUInt32(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_32);
        Item<Long> item = this.read(this.buffer::readUnsignedIntLE, edit);
        return item.value();
    }

    public long readSparseUInt64(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_64);
        Item<Long> item = this.read(this.buffer::readLongLE, edit);
        return item.value;
    }

    public short readSparseUInt8(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UINT_8);
        Item<Short> item = this.read(this.buffer::readUnsignedByte, edit);
        return item.value;
    }

    public UnixDateTime readSparseUnixDateTime(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.UNIX_DATE_TIME);
        Item<UnixDateTime> item = this.read(this::readUnixDateTime, edit);
        return item.value();
    }

    public long readSparseVarInt(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.VAR_INT);
        Item<Long> item = this.read(this::read7BitEncodedInt, edit);
        return item.value();
    }

    public long readSparseVarUInt(RowCursor edit) {
        this.readSparsePrimitiveTypeCode(edit, LayoutTypes.VAR_UINT);
        Item<Long> item = this.read(this::read7BitEncodedUInt, edit);
        return item.value();
    }

    public int readUInt16(int offset) {
        Item<Integer> item = this.read(this.buffer::readUnsignedShortLE, offset);
        return item.value();
    }

    public long readUInt32(int offset) {
        Item<Long> item = this.read(this.buffer::readUnsignedIntLE, offset);
        return item.value();
    }

    public long readUInt64(int offset) {
        Item<Long> item = this.read(this.buffer::readLongLE, offset);
        return item.value();
    }

    public short readUInt8(int offset) {
        Item<Short> item = this.read(this.buffer::readUnsignedByte, offset);
        return item.value();
    }

    public UnixDateTime readUnixDateTime(int offset) {
        Item<UnixDateTime> item = this.read(this::readUnixDateTime, offset);
        return item.value();
    }

    public ByteBuf readVariableBinary(int offset) {
        Item<ByteBuf> item = this.read(this::readVariableBinary, offset);
        return item.value();
    }

    public long readVariableInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedInt, offset);
        return item.value();
    }

    public Utf8String readVariableString(int offset) {
        Item<Utf8String> item = this.read(this::readUtf8String, offset);
        return item.value();
    }

    public long readVariableUInt(int offset) {
        Item<Long> item = this.read(this::read7BitEncodedUInt, offset);
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
     */
    public LayoutResolver resolver() {
        return this.resolver;
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

        edit.cellType(LayoutTypes.END_SCOPE);
        edit.exists(false);
        edit.valueOffset(edit.metaOffset());
        return false;
    }

    /**
     * Produce a new scope from the current iterator position.
     *
     * @param edit      An initialized iterator pointing at a scope.
     * @param immutable True if the new scope should be marked immutable (read-only).
     * @return A new scope beginning at the current iterator position.
     */
    public RowCursor sparseIteratorReadScope(RowCursor edit, boolean immutable) {

        LayoutScope scopeType = edit.cellType() instanceof LayoutScope ? (LayoutScope) edit.cellType() : null;

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

            final int valueOffset = edit.valueOffset() + (Integer.SIZE / Byte.SIZE); // Point after the Size

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
            final int valueOffset = this.ComputeVariableValueOffset(udt, edit.valueOffset(), udt.numVariable());

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

    public void unsetBit(final int offset, @Nonnull final LayoutBit bit) {
        checkNotNull(bit, "expected non-null bit");
        checkArgument(!bit.isInvalid());
        final int index = bit.offset(offset);
        this.buffer.setByte(index, this.buffer.getByte(index) & (byte) ~(1 << bit.bit()));
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

    @SuppressWarnings("ConstantConditions")
    public void writeFixedString(final int offset, @Nonnull final Utf8String value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(!value.isNull(), "expected non-null value content");
        Item<ByteBuf> item = this.write(this.buffer::writeBytes, offset, value.content());
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

    public void writeSchemaId(final int offset, @Nonnull final SchemaId value) {
        checkNotNull(value, "expected non-null value");
        this.writeInt32(offset, value.value());
    }

    public void writeSparseArray(
        @Nonnull final RowCursor edit, @Nonnull final LayoutScope scopeType, @Nonnull final UpdateOptions options,
        @Nonnull final Out<RowCursor> newScope) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(scopeType, "expected non-null scopeType");
        checkNotNull(options, "expected non-null options");
        checkNotNull(newScope, "expected non-null newScope");

        int numBytes = (LayoutCode.SIZE / Byte.SIZE); // end scope type code
        TypeArgumentList typeArgs = TypeArgumentList.EMPTY;
        int metaBytes;
        Out<Integer> tempOut_metaBytes = new Out<Integer>();
        int spaceNeeded;
        Out<Integer> tempOut_spaceNeeded = new Out<Integer>();
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();

        this.EnsureSparse(edit, scopeType, typeArgs, numBytes, options, tempOut_metaBytes, tempOut_spaceNeeded,
            tempOut_shift);
        shift = tempOut_shift.get();
        spaceNeeded = tempOut_spaceNeeded.get();
        metaBytes = tempOut_metaBytes.get();
        this.writeSparseMetadata(edit, scopeType, typeArgs, metaBytes);
        this.writeSparseTypeCode(edit.valueOffset(), LayoutCode.END_SCOPE);
        checkState(spaceNeeded == metaBytes + numBytes);

        newScope.setAndGet(new RowCursor())
                .scopeType(scopeType)
                .scopeTypeArgs(typeArgs)
                .start(edit.valueOffset())
                .valueOffset(edit.valueOffset())
                .metaOffset(edit.valueOffset())
                .layout(edit.layout());
    }

    public void writeSparseTypeCode(int offset, LayoutCode code) {
        this.writeUInt8(offset, code.value());
    }

    /**
     * Writes the content of the buffer on to an {@link OutputStream}
     *
     * @param stream the target @{link OutputStream}
     * @throws IOException if the specified {@code stream} throws an {@link IOException} during output
     */
    public void writeTo(@Nonnull final OutputStream stream) throws IOException {
        checkNotNull(stream, "expected non-null stream");
        this.buffer.getBytes(0, stream, this.length());
    }

    public void writeUInt16(int offset, short value) {
        Item<Short> item = this.write(this::writeUInt16, offset, value);
    }

    public void writeUInt32(int offset, int value) {
        Item<Integer> item = this.write(this::writeUInt32, offset, value);
    }

    public void writeUInt64(int offset, long value) {
        Item<Long> item = this.write(this::writeUInt64, offset, value);
    }

    public void writeUInt8(int offset, byte value) {
        Item<Byte> item = this.write(this::writeUInt8, offset, value);
    }

    public void writeUnixDateTime(int offset, UnixDateTime value) {
        Item<Long> item = this.write(this::writeUInt64, offset, value.milliseconds());
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

        if (left.cellType().layoutCode().value() < right.cellType().layoutCode().value()) {
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
     * Compute the number of bytes necessary to store the signed integer using the varint encoding.
     *
     * @param value The value to be encoded
     * @return The number of bytes needed to store the varint encoding of {@code value}
     */
    private static int Count7BitEncodedInt(long value) {
        return RowBuffer.count7BitEncodedUInt(RowBuffer.rotateSignToLsb(value));
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the specified value
     *
     * @param edit        The prepared edit indicating where and in what context the current write will happen.
     * @param cellType    The type of the field to be written.
     * @param typeArgs    The type arguments of the field to be written.
     * @param numBytes    The number of bytes needed to encode the value of the field to be written.
     * @param options     The kind of edit to be performed.
     * @param metaBytes   On success, the number of bytes needed to encode the metadata of the new field.
     * @param spaceNeeded On success, the number of bytes needed in total to encode the new field and its metadata.
     * @param shift       On success, the number of bytes the length of the row buffer was increased.
     */
    private void EnsureSparse(
        @Nonnull final RowCursor edit, @Nonnull final LayoutType cellType, @Nonnull final TypeArgumentList typeArgs,
        final int numBytes, @Nonnull final RowOptions options, @Nonnull final Out<Integer> metaBytes,
        @Nonnull final Out<Integer> spaceNeeded, @Nonnull final Out<Integer> shift) {

        int metaOffset = edit.metaOffset();
        int spaceAvailable = 0;

        // Compute the metadata offsets

        if (edit.scopeType().hasImplicitTypeCode(edit)) {
            metaBytes.setAndGet(0);
        } else {
            metaBytes.setAndGet(cellType.countTypeArgument(typeArgs));
        }

        if (!edit.scopeType().isIndexedScope()) {
            checkState(edit.writePath() != null);
            int pathLenInBytes = RowBuffer.countSparsePath(edit);
            metaBytes.setAndGet(metaBytes.get() + pathLenInBytes);
        }

        if (edit.exists()) {
            // Compute value offset for existing value to be overwritten.
            spaceAvailable = this.sparseComputeSize(edit);
        }

        spaceNeeded.setAndGet(options == RowOptions.DELETE ? 0 : metaBytes.get() + numBytes);
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
            if ((options == RowOptions.INSERT) || (options == RowOptions.INSERT_AT) || ((options == RowOptions.UPSERT) && !edit.get().exists())) {
                // Add one to the current scope count.
                checkState(!edit.exists());
                this.incrementUInt32(edit.start(), 1);
                edit.count(edit.count() + 1);
            } else if ((options == RowOptions.DELETE) && edit.exists()) {
                // Subtract one from the current scope count.
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
            edit.cellType(cellType);
            edit.cellTypeArgs(typeArgs);
            edit.exists(true);
        }
    }

    /**
     * Ensure that sufficient space exists in the row buffer to write the specified value
     *
     * @param edit        The prepared edit indicating where and in what context the current write will happen.
     * @param cellType    The type of the field to be written.
     * @param typeArgs    The type arguments of the field to be written.
     * @param numBytes    The number of bytes needed to encode the value of the field to be written.
     * @param options     The kind of edit to be performed.
     * @param metaBytes   On success, the number of bytes needed to encode the metadata of the new field.
     * @param spaceNeeded On success, the number of bytes needed in total to encode the new field and its metadata.
     * @param shift       On success, the number of bytes the length of the row buffer was increased.
     */
    private void EnsureSparse(
        @Nonnull final RowCursor edit, @Nonnull final LayoutType cellType, @Nonnull final TypeArgumentList typeArgs,
        final int numBytes, @Nonnull final UpdateOptions options, @Nonnull final Out<Integer> metaBytes,
        @Nonnull final Out<Integer> spaceNeeded, @Nonnull final Out<Integer> shift) {
        checkNotNull(options, "expected non-null options");
        RowOptions rowOptions = RowOptions.from(options.value());
        this.EnsureSparse(edit, cellType, typeArgs, numBytes, rowOptions, metaBytes, spaceNeeded, shift);
    }

    private void EnsureVariable(int offset, boolean isVarint, int numBytes, boolean exists,
                                Out<Integer> spaceNeeded, Out<Integer> shift) {
        int spaceAvailable = 0;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: ulong existingValueBytes = 0;
        long existingValueBytes = 0;
        if (exists) {
            Out<Integer> tempOut_spaceAvailable = new Out<Integer>();
            existingValueBytes = this.read7BitEncodedUInt(offset);
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

    private void writeVariableBinary(ByteBuf value) {
        this.write7BitEncodedUInt(value.readableBytes());
        this.buffer.writeBytes(value);
    }

    private int WriteBinary(int offset, ByteBuf value) {
        Item<ByteBuf> item = this.write(this::writeVariableBinary, offset, value);
        return item.length();
    }

    private int WriteBinary(int offset, ReadOnlySequence<Byte> value) {
        int sizeLenInBytes = this.write7BitEncodedUInt(offset, (long) value.Length);
        value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
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

    private static int countSparsePath(@Nonnull final RowCursor edit) {

        if (!edit.writePathToken().isNull()) {
            StringToken token = edit.writePathToken();
            ByteBuf varint = token.varint();
            return varint.readerIndex() + varint.readableBytes();
        }

        Optional<StringToken> optional = edit.layout().tokenizer().findToken(edit.writePath());

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

    private <T> Item<T> read(@Nonnull final Supplier<T> reader, @Nonnull final RowCursor cursor) {

        checkNotNull(reader, "expected non-null supplier");
        checkNotNull(cursor, "expected non-null cursor");

        Item<T> item = this.read(reader, cursor.valueOffset());
        cursor.endOffset(this.buffer.readerIndex());

        return item;
    }

    private <T> Item<T> read(@Nonnull final Supplier<T> reader, int offset) {

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

    private long read7BitEncodedInt() {
        return RowBuffer.rotateSignToMsb(this.read7BitEncodedUInt());
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
    private void readSparseMetadata(@Nonnull final RowCursor edit) {

        checkNotNull(edit, "expected non-null edit");

        if (edit.scopeType().hasImplicitTypeCode(edit)) {
            edit.scopeType().setImplicitTypeCode(edit);
            edit.valueOffset(edit.metaOffset());
        } else {
            edit.cellType(this.readSparseTypeCode(edit.metaOffset()));
            edit.valueOffset(edit.metaOffset() + (LayoutCode.SIZE / Byte.SIZE));
            edit.cellTypeArgs(TypeArgumentList.EMPTY);
            if (edit.cellType() instanceof LayoutEndScope) {
                // Reached end of current scope without finding another field.
                edit.pathToken(0);
                edit.pathOffset(0);
                edit.valueOffset(edit.metaOffset());
                return;
            }

            Out<Integer> sizeLenInBytes = new Out<>();
            edit.cellTypeArgs(edit.cellType().readTypeArgumentList(this, edit.valueOffset(), sizeLenInBytes));
            edit.valueOffset(edit.valueOffset() + sizeLenInBytes.get());
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
                checkState(layoutType == LayoutTypes.BOOLEAN || layoutType == LayoutTypes.BooleanFalse);
            } else {
                checkState(this.readSparseTypeCode(edit.metaOffset()) == code);
            }
        }

        if (edit.scopeType().isIndexedScope()) {
            checkState(edit.pathOffset() == 0);
            checkState(edit.pathToken() == 0);
        } else {
            int offset = edit.metaOffset() + LayoutCode.SIZE;
            Out<Integer> pathLenInBytes = new Out<>();
            Out<Integer> pathOffset = new Out<>();
            int token = this.readSparsePathLen(edit.layout(), offset, pathLenInBytes, pathOffset);
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
        return this.buffer.readSlice((int)length);
    }

    /**
     * Skip over a nested scope
     *
     * @param edit The sparse scope to search
     * @return The 0-based byte offset immediately following the scope end marker
     */
    private int skipScope(RowCursor edit) {

        //noinspection StatementWithEmptyBody
        while (this.sparseIteratorMoveNext(edit)) {
        }

        if (!edit.scopeType().isSizedScope()) {
            edit.metaOffset(edit.metaOffset() + (LayoutCode.SIZE / Byte.SIZE)); // move past end of scope marker
        }

        return edit.metaOffset();
    }

    /**
     * Compute the size of a sparse (primitive) field
     *
     * @param cellType    The type of the current sparse field.
     * @param metaOffset  The 0-based offset from the beginning of the row where the field begins.
     * @param valueOffset The 0-based offset from the beginning of the row where the field's value begins.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int sparseComputePrimitiveSize(LayoutType cellType, int metaOffset, int valueOffset) {

        // TODO: JTHTODO: convert to a virtual?

        int metaBytes = valueOffset - metaOffset;
        LayoutCode code = cellType.layoutCode();

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
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes = new Out<>();
                int numBytes = (int) this.read7BitEncodedUInt(metaOffset + metaBytes);
                sizeLenInBytes = tempOut_sizeLenInBytes.get();
                return metaBytes + sizeLenInBytes + numBytes;
            }

            case VAR_INT:
            case VAR_UINT: {
                int sizeLenInBytes;
                Out<Integer> tempOut_sizeLenInBytes2 = new Out<Integer>();
                this.read7BitEncodedUInt(metaOffset + metaBytes);
                sizeLenInBytes = tempOut_sizeLenInBytes2.get();
                return metaBytes + sizeLenInBytes;
            }
            default:
                throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
                return 0;
        }
    }

    /**
     * Compute the size of a sparse field
     *
     * @param edit The edit structure describing the field to measure.
     * @return The length (in bytes) of the encoded field including the metadata and the value.
     */
    private int sparseComputeSize(RowCursor edit) {

        if (!(edit.cellType() instanceof LayoutScope)) {
            return this.sparseComputePrimitiveSize(edit.cellType(), edit.metaOffset(), edit.valueOffset());
        }

        // Compute offset to end of value for current value
        RowCursor newScope = this.sparseIteratorReadScope(edit, true);
        return this.skipScope(newScope) - edit.metaOffset();
    }

    /**
     * Reads and validates the header of the current {@link RowBuffer}
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
        this.buffer.writerIndex(offset);
        consumer.accept(value);
        return new Item<>(value, offset, this.buffer.writerIndex() - offset);
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
            this.writeFloat128(offset, null);
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
            this.writeUnixDateTime(offset, null);
            return LayoutTypes.UNIX_DATE_TIME.size();
        }

        if (code == LayoutTypes.GUID) {
            this.writeGuid(offset, null);
            return LayoutTypes.GUID.size();
        }

        if (code == LayoutTypes.MONGODB_OBJECT_ID) {
            // TODO: DANOBLE: Add support for LayoutTypes.MONGODB_OBJECT_ID
            // this.writeMongoDbObjectId(offset, null);
            // return MongoDbObjectId.Size;
            throw new UnsupportedOperationException();
        }

        if (code == LayoutTypes.UTF_8 || code == LayoutTypes.BINARY || code == LayoutTypes.VAR_INT || code == LayoutTypes.VAR_UINT) {
            // Variable length types preceded by their varuint size take 1 byte for a size of 0.
            return this.write7BitEncodedUInt(offset, 0);
        }

        if (code == LayoutTypes.OBJECT || code == LayoutTypes.ARRAY) {
            // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
            this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
            return (LayoutCode.SIZE / Byte.SIZE);
        }

        if (code == LayoutTypes.TYPED_ARRAY || code == LayoutTypes.TypedSet || code == LayoutTypes.TypedMap) {
            // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
            this.writeUInt32(offset, 0);
            return (Integer.SIZE / Byte.SIZE);
        }

        if (code == LayoutTypes.TUPLE) {
            // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
            for (int i = 0; i < typeArgs.count(); i++) {
                this.writeSparseTypeCode(offset, LayoutCode.NULL);
            }
            this.writeSparseTypeCode(offset, LayoutCode.END_SCOPE);
            return (LayoutCode.SIZE / Byte.SIZE) + ((LayoutCode.SIZE / Byte.SIZE) * typeArgs.count());
        }

        if (code == LayoutTypes.TYPED_TUPLE || code == LayoutTypes.TAGGED || code == LayoutTypes.TAGGED_2) {
            // Fixed arity typed collections take the sum of the default values of each element. The scope size is
            // implied by the arity.
            int sum = 0;
            for (final Iterator<TypeArgument> iterator = typeArgs.elements().iterator(); iterator.hasNext(); ) {
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

            // Clear all presence bits.
            Layout udt = this.resolver.resolve(typeArgs.schemaId());
            this.buffer.Slice(offset, udt.size()).Fill(0);

            // Write scope terminator.
            this.writeSparseTypeCode(offset + udt.size(), LayoutCode.END_SCOPE);
            return udt.size() + (LayoutCode.SIZE / Byte.SIZE);
        }
        throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
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

    private void writeSparsePath(RowCursor edit, int offset) {

        // Some scopes don't encode paths, therefore the cost is always zero

        if (edit.scopeType().isIndexedScope()) {
            edit.pathToken(0);
            edit.pathOffset(0);
            return;
        }

        StringToken _;
        Out<StringToken> tempOut__ =
            new Out<StringToken>();
        checkState(!edit.layout().tokenizer().TryFindToken(edit.writePath(), tempOut__) || !edit.writePathToken().isNull());
        _ = tempOut__;
        if (!edit.writePathToken().isNull()) {
            edit.writePathToken().varint().CopyTo(this.buffer.Slice(offset));
            edit.pathToken(edit.intValue().writePathToken.Id);
            edit.pathOffset(offset);
        } else {
            // TODO: It would be better if we could avoid allocating here when the path is UTF16.
            Utf8Span span = edit.writePath().ToUtf8String();
            edit.pathToken = edit.layout().getTokenizer().getCount() + span.Length;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)edit.pathToken);
            int sizeLenInBytes = this.write7BitEncodedUInt(offset, edit.longValue().pathToken);
            edit.pathOffset = offset + sizeLenInBytes;
            span.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        }
    }

    private int writeString(int offset, Utf8Span value) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
        int sizeLenInBytes = this.write7BitEncodedUInt(offset, (long) value.Length);
        value.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
        return sizeLenInBytes;
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
            return this.Code;
        }

        public UniqueIndexItem code(LayoutCode code) {
            this.Code = code;
            return this;
        }

        /**
         * If existing, the offset to the metadata of the existing field, otherwise the location to insert a new field
         */
        public int metaOffset() {
            return this.MetaOffset;
        }

        public UniqueIndexItem metaOffset(int metaOffset) {
            this.MetaOffset = metaOffset;
            return this;
        }

        /**
         * Size of the target element
         */
        public int size() {
            return this.Size;
        }

        public UniqueIndexItem size(int size) {
            this.Size = size;
            return this;
        }

        /**
         * If existing, the offset to the value of the existing field, otherwise undefined
         */
        public int valueOffset() {
            return this.ValueOffset;
        }

        public UniqueIndexItem valueOffset(int valueOffset) {
            this.ValueOffset = valueOffset;
            return this;
        }
    }
}