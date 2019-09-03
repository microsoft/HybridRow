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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

/**
 * A forward-only, streaming, field reader for {@link RowBuffer}.
 * <p>
 * A {@link RowReader} allows the traversal in a streaming, left to right fashion, of
 * an entire HybridRow. The row's layout provides decoding for any schematized portion of the row.
 * However, unschematized sparse fields are read directly from the sparse segment with or without
 * schematization allowing all fields within the row, both known and unknown, to be read.
 * <para />
 * Modifying a {@link RowBuffer} invalidates any reader or child reader associated with it.  In
 * general {@link RowBuffer}'s should not be mutated while being enumerated.
 */
public final class RowReader {

    private int columnIndex;
    private ReadOnlySpan<LayoutColumn> columns;
    private RowCursor cursor;
    private RowBuffer row;
    private int schematizedCount;
    private States state;  // checkpoint state

    private RowReader() {
    }

    /**
     * Initializes a new instance of the {@link RowReader} struct.
     *
     * @param row   The row to be read
     */
    public RowReader(Reference<RowBuffer> row) {
        this(row, RowCursor.Create(row));
    }

    /**
     * Initializes a new instance of the {@link RowReader} struct.
     *
     * @param row        The row to be read
     * @param checkpoint Initial state of the reader
     */
    public RowReader(Reference<RowBuffer> row, final Checkpoint checkpoint) {

        this.row = row.get().clone();
        this.columns = checkpoint.Cursor.layout().columns();
        this.schematizedCount = checkpoint.Cursor.layout().numFixed() + checkpoint.Cursor.layout().numVariable();

        this.state = checkpoint.State;
        this.cursor = checkpoint.Cursor.clone();
        this.columnIndex = checkpoint.ColumnIndex;
    }

    /**
     * Initializes a new instance of the {@link RowReader} struct.
     *
     * @param row   The row to be read
     * @param scope The scope whose fields should be enumerated
     *              <p>
     *              A {@link RowReader} instance traverses all of the top-level fields of a given scope. If the
     *              root scope is provided then all top-level fields in the row are enumerated. Nested child
     *              {@link RowReader} instances can be access through the {@link RowReader#ReadScope} method to
     *              process nested content.
     */
    private RowReader(Reference<RowBuffer> row, final RowCursor scope) {

        this.cursor = scope.clone();
        this.row = row.get().clone();
        this.columns = this.cursor.layout().columns();
        this.schematizedCount = this.cursor.layout().numFixed() + this.cursor.layout().numVariable();

        this.state = States.None;
        this.columnIndex = -1;
    }

    /**
     * True if field has a value (if positioned on a field, undefined otherwise).
     * <p>
     * If the current field is a Nullable scope, this method return true if the value is not
     * null. If the current field is a nullable Null primitive value, this method return true if the value
     * is set (even though its values is set to null).
     */
    public boolean getHasValue() {

        switch (this.state) {

            case Schematized:
                return true;

            case Sparse:
                if (this.cursor.cellType() instanceof LayoutNullable) {
                    Reference<RowCursor> cursor = new Reference<>(this.cursor);
                    RowCursor nullableScope = this.row.sparseIteratorReadScope(cursor, true).clone();
                    this.cursor = cursor.get();
                    Reference<RowBuffer> row = new Reference<>(this.row);
                    Reference<RowCursor> tempReference_nullableScope = new Reference<>(nullableScope);
                    boolean tempVar = LayoutNullable.hasValue(row, tempReference_nullableScope) == Result.Success;
                    nullableScope = tempReference_nullableScope.get();
                    this.row = row.get();
                    return tempVar;
                }
                return true;

            default:
                return false;
        }
    }

    /**
     * The 0-based index, relative to the start of the scope, of the field (if positioned on a
     * field, undefined otherwise).
     * <p>
     * When enumerating a non-indexed scope, this value is always 0 (see {@link Path}).
     */
    public int getIndex() {
        switch (this.state) {
            case Schematized:
                return 0;
            case Sparse:
                return this.cursor.index();
            default:
                return 0;
        }
    }

    /**
     * The length of row in bytes.
     */
    public int getLength() {
        return this.row.length();
    }

    /**
     * The path, relative to the scope, of the field (if positioned on a field, undefined
     * otherwise).
     * <p>
     * When enumerating an indexed scope, this value is always null (see {@link Index}).
     */
    public UtfAnyString getPath() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Path;
            case Sparse:
                if (this.cursor.pathOffset() == 0) {
                    return null;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                Utf8Span span = this.row.readSparsePath(tempReference_cursor);
                this.cursor = tempReference_cursor.get();
                return Utf8String.CopyFrom(span);
            default:
                return null;
        }
    }

    /**
     * The path, relative to the scope, of the field (if positioned on a field, undefined otherwise)
     * <p>
     * When enumerating an indexed scope, this value is always null (see {@link Index}).
     */
    public Utf8Span getPathSpan() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Path.Span;
            case Sparse:
                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                Utf8Span tempVar = this.row.readSparsePath(tempReference_cursor);
                this.cursor = tempReference_cursor.get();
                return tempVar;
            default:
                return null;
        }
    }

    /**
     * The storage placement of the field (if positioned on a field, undefined otherwise).
     */
    public StorageKind getStorage() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Storage;
            case Sparse:
                return StorageKind.Sparse;
            default:
                return null;
        }
    }

    /**
     * The type of the field  (if positioned on a field, undefined otherwise).
     */
    public LayoutType getType() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Type;
            case Sparse:
                return this.cursor.cellType();
            default:
                return null;
        }
    }

    /**
     * The type arguments of the field  (if positioned on a field, undefined otherwise).
     */
    public TypeArgumentList getTypeArgs() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].TypeArgs;
            case Sparse:
                return this.cursor.cellTypeArgs().clone();
            default:
                return TypeArgumentList.EMPTY;
        }
    }

    /**
     * Advances the reader to the next field.
     *
     * @return True, if there is another field to be read, false otherwise.
     */
    public boolean Read() {

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
                LayoutColumn col = this.columns[this.columnIndex];

                if (!this.row.readBit(this.cursor.start(), col.getNullBit().clone())) {
                    // Skip schematized values if they aren't present.
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Schematized;
                }

                return true;
            }

            case Sparse: {

                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);

                if (!RowCursors.moveNext(this.cursor.clone(),
                    tempReference_row)) {
                    this.row = tempReference_row.get();
                    this.state = States.Done;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Done;
                } else {
                    this.row = tempReference_row.get();
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
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadBinary(out byte[] value)
    public Result ReadBinary(Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadBinary(out ReadOnlySpan<byte> span);
        Result r = this.ReadBinary(out span);
        value.setAndGet((r == Result.Success) ? span.ToArray() :)
        default
        return r;
    }

    /**
     * Read the current field as a variable length, sequence of bytes.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadBinary(out ReadOnlySpan<byte> value)
    public Result ReadBinary(Out<ReadOnlySpan<Byte>> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutBinary)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseBinary(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a {@link bool}.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadBool(Out<Boolean> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutBoolean)) {
                    value.setAndGet(false);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseBoolean(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(false);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length {@link DateTime} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadDateTime(Out<LocalDateTime> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutDateTime)) {
                    value.setAndGet(LocalDateTime.MIN);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseDateTime(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(LocalDateTime.MIN);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length {@link decimal} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadDecimal(Out<BigDecimal> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutDecimal)) {
                    value.setAndGet(new BigDecimal(0));
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseDecimal(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(new BigDecimal(0));
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat128(Out<Float128> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat128)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseFloat128(tempReference_cursor).clone());
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat32(Out<Float> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat32)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseFloat32(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat64(Out<Double> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutFloat64)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseFloat64(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length {@link Guid} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadGuid(Out<UUID> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutGuid)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseGuid(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 16-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt16(Out<Short> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt16)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseInt16(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt32(Out<Integer> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt32)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseInt32(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt64(Out<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt64)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseInt64(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 8-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt8(Out<Byte> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutInt8)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseInt8(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
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
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutMongoDbObjectId)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseMongoDbObjectId(tempReference_cursor).clone());
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a null.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadNull(Out<NullValue> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutNull)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseNull(tempReference_cursor).clone());
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a nested, structured, sparse scope.
     * <p>
     * Child readers can be used to read all sparse scope types including typed and untyped
     * objects, arrays, tuples, set, and maps.
     * <para />
     * Nested child readers are independent of their parent.
     */
    public RowReader ReadScope() {
        Reference<RowCursor> tempReference_cursor =
            new Reference<RowCursor>(this.cursor);
        RowCursor newScope = this.row.sparseIteratorReadScope(tempReference_cursor, true).clone();
        this.cursor = tempReference_cursor.get();
        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.row, newScope)
        this.row = tempReference_row.get();
        return tempVar;
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
        Result result = func == null ? null : func.Invoke(ref nestedReader, context) ??Result.Success;
        nestedReader = tempReference_nestedReader.get();
        if (result != Result.Success) {
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
        return Result.Success;
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
        value.setAndGet((r == Result.Success) ? span.toString() :)
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
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.ReadString(out span);
        value.setAndGet((r == Result.Success) ? Utf8String.CopyFrom(span) :)
        default
        return r;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(Out<Utf8Span> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUtf8)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseString(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 16-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadUInt16(out ushort value)
    public Result ReadUInt16(Out<Short> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt16)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseUInt16(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadUInt32(out uint value)
    public Result ReadUInt32(Out<Integer> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt32)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseUInt32(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadUInt64(out ulong value)
    public Result ReadUInt64(Out<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt64)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseUInt64(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 8-bit, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadUInt8(out byte value)
    public Result ReadUInt8(Out<Byte> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUInt8)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseUInt8(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length {@link UnixDateTime} value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadUnixDateTime(Out<UnixDateTime> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutUnixDateTime)) {
                    value.setAndGet(null);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseUnixDateTime(tempReference_cursor).clone());
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(null);
                return Result.Failure;
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
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutVarInt)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.ReadSparseVarInt(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a variable length, 7-bit encoded, unsigned integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadVarUInt(out ulong value)
    public Result ReadVarUInt(Out<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType() instanceof LayoutVarUInt)) {
                    value.setAndGet(0);
                    return Result.TypeMismatch;
                }

                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                value.setAndGet(this.row.readSparseVarUInt(tempReference_cursor));
                this.cursor = tempReference_cursor.get();
                return Result.Success;
            default:
                value.setAndGet(0);
                return Result.Failure;
        }
    }

    public Checkpoint SaveCheckpoint() {
        return new Checkpoint(this.state, this.columnIndex, this.cursor.clone());
    }

    /**
     * Advance a reader to the end of a child reader. The child reader is also advanced to the
     * end of its scope.
     * <p>
     * <p>
     * The reader must not have been advanced since the child reader was created with ReadScope.
     * This method can be used when the overload of {@link ReadScope} that takes a
     * {@link ReaderFunc{TContext}} is not an option, such as when TContext is a ref struct.
     */
    public Result SkipScope(Reference<RowReader> nestedReader) {
        if (nestedReader.get().cursor.start() != this.cursor.valueOffset()) {
            return Result.Failure;
        }

        Reference<RowBuffer> tempReference_row =
            new Reference<RowBuffer>(this.row);
        Reference<RowCursor> tempReference_cursor =
            new Reference<RowCursor>(nestedReader.get().cursor);
        RowCursors.skip(this.cursor.clone(), tempReference_row,
            tempReference_cursor);
        nestedReader.get().argValue.cursor = tempReference_cursor.get();
        this.row = tempReference_row.get();
        return Result.Success;
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
     * Read a generic schematized field value via the scope's layout.
     * <typeparam name="TValue">The expected type of the field.</typeparam>
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private <TValue> Result ReadPrimitiveValue(Out<TValue> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof LayoutType<TValue>)) {
            value.setAndGet(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.storage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_row =
                    new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor =
                    new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<LayoutType<TValue>>typeAs().readFixed(tempReference_row, tempReference_cursor, col, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case Variable:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<LayoutType<TValue>>typeAs().readVariable(tempReference_row2, tempReference_cursor2, col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private Result ReadPrimitiveValue(Out<Utf8Span> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof ILayoutUtf8SpanReadable)) {
            value.setAndGet(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.storage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutUtf8SpanReadable>typeAs().ReadFixed(tempReference_row, tempReference_cursor, col, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case Variable:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutUtf8SpanReadable>typeAs().ReadVariable(tempReference_row2,
                    tempReference_cursor2, col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     * <typeparam name="TElement">The sub-element type of the field.</typeparam>
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private <TElement> Result ReadPrimitiveValue(Out<ReadOnlySpan<TElement>> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof ILayoutSpanReadable<TElement>)) {
            value.setAndGet(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.storage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_row = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor = new Reference<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSpanReadable<TElement>>typeAs().ReadFixed(tempReference_row, tempReference_cursor, col, value);
                this.cursor = tempReference_cursor.get();
                this.row = tempReference_row.get();
                return tempVar;
            case Variable:
                Reference<RowBuffer> tempReference_row2 = new Reference<RowBuffer>(this.row);
                Reference<RowCursor> tempReference_cursor2 = new Reference<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSpanReadable<TElement>>typeAs().ReadVariable(tempReference_row2, tempReference_cursor2, col, value);
                this.cursor = tempReference_cursor2.get();
                this.row = tempReference_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.setAndGet(null);
                return Result.Failure;
        }
    }

    /**
     * The current traversal state of the reader.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal enum States : byte
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

        public static final int SIZE = java.lang.Byte.SIZE;

        public byte getValue() {
            return this.ordinal();
        }

        public static States forValue(byte value) {
            return values()[value];
        }
    }

    /**
     * A function to reader content from a {@link RowBuffer}.
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
    //C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ from the original:
    //ORIGINAL LINE: public readonly struct Checkpoint
    //C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
    public final static class Checkpoint {
        public int ColumnIndex;
        public RowCursor Cursor = new RowCursor();
        public States State = States.values()[0];

        public Checkpoint() {
        }

        public Checkpoint(States state, int columnIndex, RowCursor cursor) {
            this.State = state;
            this.ColumnIndex = columnIndex;
            this.Cursor = cursor.clone();
        }

        public Checkpoint clone() {
            Checkpoint varCopy = new Checkpoint();

            varCopy.State = this.State;
            varCopy.ColumnIndex = this.ColumnIndex;
            varCopy.Cursor = this.Cursor.clone();

            return varCopy;
        }
    }
}