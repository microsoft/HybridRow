// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.core.UtfAnyString;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.ILayoutSpanReadable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.ILayoutUtf8SpanReadable;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBinary;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutBoolean;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutColumn;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutDecimal;
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
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUDT;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt16;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt32;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt64;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUInt8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutUtf8;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutVarUInt;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;
import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

/**
 * A forward-only, streaming, field reader for {@link RowBuffer}.
 * <p>
 * A {@link RowReader} allows the traversal in a streaming, left to right fashion, of an entire HybridRow. The row's
 * layout provides decoding for any schematized portion of the row. However, unschematized sparse fields are read
 * directly from the sparse segment with or without schematization allowing all fields within the row, both known and
 * unknown, to be read.
 * <p>
 * Modifying a {@link RowBuffer} invalidates any reader or child reader associated with it. In general{@link RowBuffer}s
 * should not be mutated while being enumerated.
 */
public final class RowReader {

    private int columnIndex;
    private List<LayoutColumn> columns;
    private RowCursor cursor;
    private RowBuffer row;
    private int schematizedCount;
    private States state;  // checkpoint state

    /**
     * Initializes a new instance of the {@link RowReader} class
     *
     * @param row   The row to be read
     */
    public RowReader(RowBuffer row) {
        this(row, RowCursor.create(row));
    }

    /**
     * Initializes a new instance of the {@link RowReader} class.
     *  @param row        The row to be read
     * @param checkpoint Initial state of the reader
     */
    public RowReader(@Nonnull final RowBuffer row, @Nonnull final Checkpoint checkpoint) {

        this.row = row;
        this.columns = checkpoint.cursor().layout().columns();
        this.schematizedCount = checkpoint.cursor().layout().numFixed() + checkpoint.cursor().layout().numVariable();

        this.state = checkpoint.state();
        this.cursor = checkpoint.cursor();
        this.columnIndex = checkpoint.columnIndex();
    }

    private RowReader() {
    }

    /**
     * Initializes a new instance of the {@link RowReader} class
     *
     * @param row   The row to be read
     * @param scope Cursor defining the scope of the fields to be read
     *              <p>
     *              A {@link RowReader} instance traverses all of the top-level fields of a given scope. If the root
     *              scope is provided then all top-level fields in the row are enumerated. Nested child
     *              {@link RowReader} instances can be access through the {@link RowReader#readScope} method to
     *              process nested content.
     */
    private RowReader(RowBuffer row, RowCursor scope) {

        this.cursor = scope;
        this.row = row;
        this.columns = this.cursor.layout().columns();
        this.schematizedCount = this.cursor.layout().numFixed() + this.cursor.layout().numVariable();

        this.state = States.None;
        this.columnIndex = -1;
    }

    /**
     * Read the current field as a fixed length {@link MongoDbObjectId} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadMongoDbObjectId(Out<MongoDbObjectId> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutMongoDbObjectId)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseMongoDbObjectId(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a nested, structured, sparse scope.
     * <p>
     * Child readers can be used to read all sparse scope types including typed and untyped
     * objects, arrays, tuples, set, and maps.
     */
    public <TContext> Result ReadScope(TContext context, ReaderFunc<TContext> func) {
        Reference<RowCursor> tempReference_cursor =
            new Reference<RowCursor>(this.cursor);
        RowCursor childScope = this.row.sparseIteratorReadScope(tempReference_cursor, true).clone();
        this.cursor = tempReference_cursor.get();
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        RowReader nestedReader = new RowReader(tempReference_row, childScope.clone());
        this.row = tempReference_row.get();

        Reference<RowReader> tempReference_nestedReader =
            new Reference<RowReader>(nestedReader);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        Result result = func == null ? null : func.Invoke(ref nestedReader, context) ??Result.SUCCESS;
        nestedReader = tempReference_nestedReader.get();
        if (result != Result.SUCCESS) {
            return result;
        }

        Reference<RowBuffer> tempReference_row2 =
            new Reference<RowBuffer>(this.row);
        Reference<RowCursor> tempReference_cursor2 =
            new Reference<RowCursor>(nestedReader.cursor);
        RowCursors.skip(this.cursor.clone(), tempReference_row2,
            tempReference_cursor2);
        nestedReader.cursor = tempReference_cursor2.get();
        this.row = tempReference_row2.get();
        return Result.SUCCESS;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(Out<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.ReadString(out span);
        value.set((r == Result.SUCCESS) ? span.toString() :)
        default
        return r;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(Out<Utf8String> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUtf8)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseString(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a variable length, 7-bit encoded, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadVarInt(Out<Long> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutVarInt)) {
                    value.set(0L);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseVarInt(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0L);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a variable length, 7-bit encoded, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadVarUInt(Out<Long> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutVarUInt)) {
                    value.set(0L);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseVarUInt(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0L);
                return Result.FAILURE;
        }
    }

    public Checkpoint SaveCheckpoint() {
        return new Checkpoint(this.state, this.columnIndex, this.cursor);
    }

    /**
     * Advance a reader to the end of a child reader. The child reader is also advanced to the
     * end of its scope.
     * <p>
     * <p>
     * The reader must not have been advanced since the child reader was created with ReadScope.
     * This method can be used when the overload of {@link #readScope} that takes a
     * {@link ReaderFunc{TContext}} is not an option, such as when TContext is a ref struct.
     */
    public Result SkipScope(Reference<RowReader> nestedReader) {
        if (nestedReader.get().cursor.start() != this.cursor.valueOffset()) {
            return Result.FAILURE;
        }

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        Reference<RowCursor> tempReference_cursor =
            new Reference<RowCursor>(nestedReader.get().cursor);
        RowCursors.skip(this.cursor.clone(), tempReference_row,
            tempReference_cursor);
        nestedReader.get().argValue.cursor = tempReference_cursor.get();
        this.row = tempReference_row.get();
        return Result.SUCCESS;
    }

    public RowReader clone() {
        RowReader varCopy = new RowReader();

        varCopy.schematizedCount = this.schematizedCount;
        varCopy.columns = this.columns;
        varCopy.row = this.row.clone();
        varCopy.state = this.state;
        varCopy.columnIndex = this.columnIndex;
        varCopy.cursor = this.cursor.clone();

        return varCopy;
    }

    /**
     * {@code true} if field has a value--if positioned on a field--undefined otherwise
     * <p>
     * If the current field is a Nullable scope, this method return true if the value is not null. If the current field
     * is a nullable Null primitive value, this method return true if the value is set (even though its values is set
     * to null).
     */
    public boolean hasValue() {

        switch (this.state) {

            case Schematized:
                return true;

            case Sparse:
                if (this.cursor.cellType() instanceof LayoutNullable) {
                    RowCursor nullableScope = this.row.sparseIteratorReadScope(this.cursor, true);
                    return LayoutNullable.hasValue(this.row, nullableScope) == Result.SUCCESS;
                }
                return true;

            default:
                return false;
        }
    }

    /**
     * Zero-based index, relative to the scope, of the field--if positioned on a field--undefined otherwise
     * <p>
     * When enumerating a non-indexed scope, this value is always 0 (see {@link #path}).
     */
    public int index() {
        return this.state == States.Sparse ? this.cursor.index() : 0;
    }

    /**
     * The length of row in bytes.
     */
    public int length() {
        return this.row.length();
    }

    /**
     * The path, relative to the scope, of the field--if positioned on a field--undefined otherwise
     * <p>
     * When enumerating an indexed scope, this value is always null (see {@link #index}).
     */
    public UtfAnyString path() {

        Utf8String value;

        switch (this.state) {

            case Schematized:
                value = this.columns.get(this.columnIndex).path();
                break;

            case Sparse:
                value = this.cursor.pathOffset() == 0 ? null : this.row.readSparsePath(this.cursor);
                break;

            default:
                value = null;
                break;
        }

        return new UtfAnyString(value);
    }

    /**
     * The path, relative to the scope, of the field--if positioned on a field--undefined otherwise
     * <p>
     * When enumerating an indexed scope, this value is always null (see {@link #index}).
     */
    public Utf8String pathSpan() {
        switch (this.state) {
            case Schematized:
                return this.columns.get(this.columnIndex).path();
            case Sparse:
                return this.row.readSparsePath(this.cursor);
            default:
                return null;
        }
    }

    /**
     * Advances the reader to the next field.
     *
     * @return True, if there is another field to be read, false otherwise.
     */
    public boolean read() {

        switch (this.state) {

            case None: {
                if (this.cursor.scopeType() instanceof LayoutUDT) {
                    this.state = States.Schematized;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Schematized;
                } else {
                    this.state = States.Sparse;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Sparse;
                }
            }
            case Schematized: {

                this.columnIndex++;

                if (this.columnIndex >= this.schematizedCount) {
                    this.state = States.Sparse;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Sparse;
                }

                checkState(this.cursor.scopeType() instanceof LayoutUDT);
                LayoutColumn column = this.columns.get(this.columnIndex);

                if (!this.row.readBit(this.cursor.start(), column.nullBit())) {
                    // Skip schematized values if they aren't present
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Schematized;
                }

                return true;
            }

            case Sparse: {

                if (!RowCursors.moveNext(this.cursor, this.row)) {
                    this.state = States.Done;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Done;
                }
                return true;
            }

            case Done: {
                return false;
            }
        }

        return false;
    }

    /**
     * Read the current field as a variable length, sequence of bytes.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readBinary(Out<ByteBuf> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:

                if (!(this.cursor.cellType() instanceof LayoutBinary)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseBinary(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a variable length, sequence of bytes
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readBinaryArray(Out<byte[]> value) {

        Out<ByteBuf> buffer = new Out<>();
        Result result = this.readBinary(buffer);

        if (result == Result.SUCCESS) {
            byte[] array = new byte[buffer.get().writerIndex()];
            buffer.get().getBytes(0, array);
            value.set(array);
        }

        return result;
    }

    /**
     * Read the current field as a {@link Boolean}.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readBoolean(Out<Boolean> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutBoolean)) {
                    value.set(false);
                    return Result.TYPE_MISMATCH;
                }

                value.set(this.row.readSparseBoolean(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(false);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length {@code DateTime} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readDateTime(Out<OffsetDateTime> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutDateTime)) {
                    value.set(OffsetDateTime.MIN);
                    return Result.TYPE_MISMATCH;
                }

                value.set(this.row.readSparseDateTime(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(OffsetDateTime.MIN);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length decimal value
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readDecimal(Out<BigDecimal> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutDecimal)) {
                    value.set(new BigDecimal(0));
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseDecimal(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(new BigDecimal(0));
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readFloat128(Out<Float128> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat128)) {
                    value.setAndGet(null);
                    return Result.TYPE_MISMATCH;
                }
                value.setAndGet(this.row.readSparseFloat128(this.cursor));
                return Result.SUCCESS;

            default:
                value.setAndGet(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readFloat32(Out<Float> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat32)) {
                    value.set(0F);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseFloat32(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0F);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readFloat64(Out<Double> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat64)) {
                    value.set(0D);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseFloat64(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0D);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length GUID value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readGuid(Out<UUID> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutGuid)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }

                value.set(this.row.readSparseGuid(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 16-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readInt16(Out<Short> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt16)) {
                    value.set((short)0);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseInt16(this.cursor));
                return Result.SUCCESS;

            default:
                value.set((short)0);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readInt32(Out<Integer> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt32)) {
                    value.set(0);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseInt32(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readInt64(Out<Long> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt64)) {
                    value.setAndGet(0L);
                    return Result.TYPE_MISMATCH;
                }
                value.setAndGet(this.row.readSparseInt64(this.cursor));
                return Result.SUCCESS;

            default:
                value.setAndGet(0L);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 8-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readInt8(Out<Byte> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt8)) {
                    value.set((byte)0);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseInt8(this.cursor));
                return Result.SUCCESS;

            default:
                value.set((byte)0);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a null.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readNull(Out<NullValue> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutNull)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseNull(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a nested, structured, sparse scope
     * <p>
     * Child readers can be used to read all sparse scope types including typed and untyped objects, arrays, tuples,
     * set, and maps.
     * <p>
     * Nested child readers are independent of their parent.
     */
    public @Nonnull RowReader readScope() {
        RowCursor newScope = this.row.sparseIteratorReadScope(this.cursor, true);
        return new RowReader(this.row, newScope);
    }

    /**
     * Read the current field as a fixed length, 16-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readUInt16(Out<Integer> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt16)) {
                    value.set(0);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseUInt16(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readUInt32(Out<Long> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt32)) {
                    value.set(0L);
                    return Result.TYPE_MISMATCH;
                }

                value.set(this.row.readSparseUInt32(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0L);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readUInt64(Out<Long> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt64)) {
                    value.set(0L);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseUInt64(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(0L);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length, 8-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readUInt8(Out<Short> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt8)) {
                    value.set((short)0);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseUInt8(this.cursor));
                return Result.SUCCESS;

            default:
                value.set((short)0);
                return Result.FAILURE;
        }
    }

    /**
     * Read the current field as a fixed length {@link UnixDateTime} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result readUnixDateTime(Out<UnixDateTime> value) {

        switch (this.state) {

            case Schematized:
                return this.readPrimitiveValue(value);

            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUnixDateTime)) {
                    value.set(null);
                    return Result.TYPE_MISMATCH;
                }
                value.set(this.row.readSparseUnixDateTime(this.cursor));
                return Result.SUCCESS;

            default:
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * The storage placement of the field--if positioned on a field--undefined otherwise
     */
    public StorageKind storage() {
        switch (this.state) {
            case Schematized:
                return this.columns.get(this.columnIndex).storage();
            case Sparse:
                return StorageKind.SPARSE;
            default:
                return null;
        }
    }

    /**
     * The type of the field--if positioned on a field--undefined otherwise
     */
    public LayoutType type() {

        switch (this.state) {
            case Schematized:
                return this.columns.get(this.columnIndex).type();
            case Sparse:
                return this.cursor.cellType();
            default:
                return null;
        }
    }

    /**
     * The type arguments of the field  (if positioned on a field, undefined otherwise).
     */
    public TypeArgumentList typeArgs() {

        switch (this.state) {
            case Schematized:
                return this.columns.get(this.columnIndex).typeArgs();
            case Sparse:
                return this.cursor.cellTypeArgs();
            default:
                return TypeArgumentList.EMPTY;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     * <typeparam name="TValue">The expected type of the field.</typeparam>
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private <TValue> Result readPrimitiveValue(Out<TValue> value) {

        final LayoutColumn column = this.columns.get(this.columnIndex);
        final LayoutType type = this.columns.get(this.columnIndex).type();

        if (!(type instanceof LayoutType<TValue>)) {
            value.set(null);
            return Result.TYPE_MISMATCH;
        }

        final StorageKind storage = column == null ? StorageKind.NONE : column.storage();

        switch (storage) {
            case FIXED:
                return type.<LayoutType<TValue>>typeAs().readFixed(this.row, this.cursor, column, value);
            case VARIABLE:
                return type.<LayoutType<TValue>>typeAs().readVariable(this.row, this.cursor, column, value);
            default:
                assert false : lenientFormat("expected FIXED or VARIABLE column storage, not %s", storage);
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private Result readPrimitiveValue(Out<Utf8Span> value) {
        LayoutColumn column = this.columns.get(this.columnIndex);
        LayoutType t = this.columns.get(this.columnIndex).Type;
        if (!(t instanceof ILayoutUtf8SpanReadable)) {
            value.set(null);
            return Result.TYPE_MISMATCH;
        }

        switch (column == null ? null : column.storage()) {
            case FIXED:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutUtf8SpanReadable>typeAs().ReadFixed(tempReference_row, tempReference_cursor, column, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case VARIABLE:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutUtf8SpanReadable>typeAs().ReadVariable(tempReference_row2,
                    tempReference_cursor2, column, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private <TElement> Result readPrimitiveValue(Out<ReadOnlySpan<TElement>> value) {

        LayoutColumn column = this.columns.get(this.columnIndex);
        LayoutType t = this.columns.get(this.columnIndex).type();

        if (!(t instanceof ILayoutSpanReadable<TElement>)) {
            value.set(null);
            return Result.TYPE_MISMATCH;
        }

        switch (column == null ? null : column.storage()) {
            case FIXED:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSpanReadable<TElement>>typeAs().ReadFixed(tempReference_row,
                    tempReference_cursor, column, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case VARIABLE:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSpanReadable<TElement>>typeAs().ReadVariable(tempReference_row2,
                    tempReference_cursor2, column, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.set(null);
                return Result.FAILURE;
        }
    }

    /**
     * The current traversal state of the reader.
     */
    public enum States {
        /**
         * The reader has not be started yet.
         */
        None,

        /**
         * Enumerating schematized fields (fixed and variable) from left to right.
         */
        Schematized,

        /**
         * Enumerating top-level fields of the current scope.
         */
        Sparse,

        /**
         * The reader has completed the scope.
         */
        Done;

        public static final int BYTES = Byte.BYTES;

        public static States from(byte value) {
            return values()[value];
        }

        public byte value() {
            return (byte) this.ordinal();
        }
    }

    /**
     * A function to reader content from a {@link RowBuffer}.
     * <p>
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param reader  A forward-only cursor for writing content.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface ReaderFunc<TContext> {
        Result invoke(Reference<RowReader> reader, TContext context);
    }

    /**
     * An encapsulation of the current state of a {@link RowReader} that can be used to
     * recreate the {@link RowReader} in the same logical position.
     */
    public final static class Checkpoint {

        private int columnIndex;
        private RowCursor cursor;
        private States state;

        public Checkpoint(States state, int columnIndex, RowCursor cursor) {
            this.state(state);
            this.columnIndex(columnIndex);
            this.cursor(cursor);
        }

        public int columnIndex() {
            return this.columnIndex;
        }

        public Checkpoint columnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
            return this;
        }

        public RowCursor cursor() {
            return this.cursor;
        }

        public Checkpoint cursor(RowCursor cursor) {
            this.cursor = cursor;
            return this;
        }

        public States state() {
            return this.state;
        }

        public Checkpoint state(States state) {
            this.state = state;
            return this;
        }
    }
}