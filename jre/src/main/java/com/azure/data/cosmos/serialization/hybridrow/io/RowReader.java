//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;
import com.azure.data.cosmos.serialization.hybridrow.RowCursorExtensions;
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

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1034 // Nested types should not be visible


/**
 * A forward-only, streaming, field reader for <see cref="RowBuffer" />.
 * <p>
 * A <see cref="RowReader" /> allows the traversal in a streaming, left to right fashion, of
 * an entire HybridRow. The row's layout provides decoding for any schematized portion of the row.
 * However, unschematized sparse fields are read directly from the sparse segment with or without
 * schematization allowing all fields within the row, both known and unknown, to be read.
 * <para />
 * Modifying a <see cref="RowBuffer" /> invalidates any reader or child reader associated with it.  In
 * general <see cref="RowBuffer" />'s should not be mutated while being enumerated.
 */
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public ref struct RowReader
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# ref struct:
public final class RowReader {
    private int columnIndex;
    private ReadOnlySpan<LayoutColumn> columns;
    private RowCursor cursor = new RowCursor();
    private RowBuffer row = new RowBuffer();
    private int schematizedCount;
    // State that can be checkpointed.
    private States state = States.values()[0];

    /**
     * Initializes a new instance of the <see cref="RowReader" /> struct.
     *
     * @param row   The row to be read.
     * @param scope The scope whose fields should be enumerated.
     *              <p>
     *              A <see cref="RowReader" /> instance traverses all of the top-level fields of a given
     *              scope.  If the root scope is provided then all top-level fields in the row are enumerated.  Nested
     *              child <see cref="RowReader" /> instances can be access through the <see cref="ReadScope" /> method
     *              to process nested content.
     */
    public RowReader() {
    }

    /**
     * Initializes a new instance of the <see cref="RowReader" /> struct.
     *
     * @param row   The row to be read.
     * @param scope The scope whose fields should be enumerated.
     *              <p>
     *              A <see cref="RowReader" /> instance traverses all of the top-level fields of a given
     *              scope.  If the root scope is provided then all top-level fields in the row are enumerated.  Nested
     *              child <see cref="RowReader" /> instances can be access through the <see cref="ReadScope" /> method
     *              to process nested content.
     */
    public RowReader(RefObject<RowBuffer> row) {
        this(row, RowCursor.Create(row));
    }

    public RowReader(RefObject<RowBuffer> row, final Checkpoint checkpoint) {
        this.row = row.get().clone();
        this.columns = checkpoint.Cursor.layout.getColumns();
        this.schematizedCount = checkpoint.Cursor.layout.getNumFixed() + checkpoint.Cursor.layout.getNumVariable();

        this.state = checkpoint.State;
        this.cursor = checkpoint.Cursor.clone();
        this.columnIndex = checkpoint.ColumnIndex;
    }

    private RowReader(RefObject<RowBuffer> row, final RowCursor scope) {
        this.cursor = scope.clone();
        this.row = row.get().clone();
        this.columns = this.cursor.layout.getColumns();
        this.schematizedCount = this.cursor.layout.getNumFixed() + this.cursor.layout.getNumVariable();

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
                if (this.cursor.cellType instanceof LayoutNullable) {
                    RefObject<RowCursor> tempRef_cursor =
                        new RefObject<RowCursor>(this.cursor);
                    RowCursor nullableScope = this.row.SparseIteratorReadScope(tempRef_cursor, true).clone();
                    this.cursor = tempRef_cursor.get();
                    RefObject<RowBuffer> tempRef_row =
                        new RefObject<RowBuffer>(this.row);
                    RefObject<RowCursor> tempRef_nullableScope = new RefObject<RowCursor>(nullableScope);
                    boolean tempVar = LayoutNullable.HasValue(tempRef_row, tempRef_nullableScope) == Result.Success;
                    nullableScope = tempRef_nullableScope.get();
                    this.row = tempRef_row.get();
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
     * When enumerating a non-indexed scope, this value is always 0 (see <see cref="Path" />).
     */
    public int getIndex() {
        switch (this.state) {
            case Schematized:
                return 0;
            case Sparse:
                return this.cursor.index;
            default:
                return 0;
        }
    }

    /**
     * The length of row in bytes.
     */
    public int getLength() {
        return this.row.getLength();
    }

    /**
     * The path, relative to the scope, of the field (if positioned on a field, undefined
     * otherwise).
     * <p>
     * When enumerating an indexed scope, this value is always null (see <see cref="Index" />).
     */
    public UtfAnyString getPath() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Path;
            case Sparse:
                if (this.cursor.pathOffset == 0) {
                    return null;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                Utf8Span span = this.row.ReadSparsePath(tempRef_cursor);
                this.cursor = tempRef_cursor.get();
                return Utf8String.CopyFrom(span);
            default:
                return null;
        }
    }

    /**
     * The path, relative to the scope, of the field (if positioned on a field, undefined
     * otherwise).
     * <p>
     * When enumerating an indexed scope, this value is always null (see <see cref="Index" />).
     */
    public Utf8Span getPathSpan() {
        switch (this.state) {
            case Schematized:
                return this.columns[this.columnIndex].Path.Span;
            case Sparse:
                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                Utf8Span tempVar = this.row.ReadSparsePath(tempRef_cursor);
                this.cursor = tempRef_cursor.get();
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
                return this.cursor.cellType;
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
                return this.cursor.cellTypeArgs.clone();
            default:
                return TypeArgumentList.Empty;
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
                if (this.cursor.scopeType instanceof LayoutUDT) {
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

                checkState(this.cursor.scopeType instanceof LayoutUDT);
                LayoutColumn col = this.columns[this.columnIndex];

                if (!this.row.ReadBit(this.cursor.start, col.getNullBit().clone())) {
                    // Skip schematized values if they aren't present.
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Schematized;
                }

                return true;
            }

            case Sparse: {

                RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(this.row);

                if (!RowCursorExtensions.MoveNext(this.cursor.clone(),
                    tempRef_row)) {
                    this.row = tempRef_row.get();
                    this.state = States.Done;
                    // TODO: C# TO JAVA CONVERTER: There is no 'goto' in Java:
                    //   goto case States.Done;
                } else {
                    this.row = tempRef_row.get();
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
    public Result ReadBinary(OutObject<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadBinary(out ReadOnlySpan<byte> span);
        Result r = this.ReadBinary(out span);
        value.set((r == Result.Success) ? span.ToArray() :)
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
    public Result ReadBinary(OutObject<ReadOnlySpan<Byte>> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutBinary)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseBinary(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a <see cref="bool" />.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadBool(OutObject<Boolean> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutBoolean)) {
                    value.set(false);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseBool(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(false);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length <see cref="DateTime" /> value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadDateTime(OutObject<LocalDateTime> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutDateTime)) {
                    value.set(LocalDateTime.MIN);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseDateTime(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(LocalDateTime.MIN);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length <see cref="decimal" /> value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadDecimal(OutObject<BigDecimal> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutDecimal)) {
                    value.set(new BigDecimal(0));
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseDecimal(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(new BigDecimal(0));
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat128(OutObject<Float128> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutFloat128)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseFloat128(tempRef_cursor).clone());
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat32(OutObject<Float> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutFloat32)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseFloat32(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadFloat64(OutObject<Double> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutFloat64)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseFloat64(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length <see cref="Guid" /> value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadGuid(OutObject<UUID> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutGuid)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseGuid(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 16-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt16(OutObject<Short> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutInt16)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseInt16(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 32-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt32(OutObject<Integer> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutInt32)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseInt32(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 64-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt64(OutObject<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutInt64)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseInt64(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length, 8-bit, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadInt8(OutObject<Byte> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutInt8)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseInt8(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length <see cref="MongoDbObjectId" /> value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadMongoDbObjectId(OutObject<MongoDbObjectId> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutMongoDbObjectId)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseMongoDbObjectId(tempRef_cursor).clone());
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a null.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadNull(OutObject<NullValue> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutNull)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseNull(tempRef_cursor).clone());
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
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
        RefObject<RowCursor> tempRef_cursor =
            new RefObject<RowCursor>(this.cursor);
        RowCursor newScope = this.row.SparseIteratorReadScope(tempRef_cursor, true).clone();
        this.cursor = tempRef_cursor.get();
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(this.row);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        return new RowReader(ref this.row, newScope)
        this.row = tempRef_row.get();
        return tempVar;
    }

    /**
     * Read the current field as a nested, structured, sparse scope.
     * <p>
     * Child readers can be used to read all sparse scope types including typed and untyped
     * objects, arrays, tuples, set, and maps.
     */
    public <TContext> Result ReadScope(TContext context, ReaderFunc<TContext> func) {
        RefObject<RowCursor> tempRef_cursor =
            new RefObject<RowCursor>(this.cursor);
        RowCursor childScope = this.row.SparseIteratorReadScope(tempRef_cursor, true).clone();
        this.cursor = tempRef_cursor.get();
        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(this.row);
        RowReader nestedReader = new RowReader(tempRef_row, childScope.clone());
        this.row = tempRef_row.get();

        RefObject<RowReader> tempRef_nestedReader =
            new RefObject<RowReader>(nestedReader);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        Result result = func == null ? null : func.Invoke(ref nestedReader, context) ??Result.Success;
        nestedReader = tempRef_nestedReader.get();
        if (result != Result.Success) {
            return result;
        }

        RefObject<RowBuffer> tempRef_row2 =
            new RefObject<RowBuffer>(this.row);
        RefObject<RowCursor> tempRef_cursor2 =
            new RefObject<RowCursor>(nestedReader.cursor);
        RowCursorExtensions.Skip(this.cursor.clone(), tempRef_row2,
            tempRef_cursor2);
        nestedReader.cursor = tempRef_cursor2.get();
        this.row = tempRef_row2.get();
        return Result.Success;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadString(out span);
        value.set((r == Result.Success) ? span.toString() :)
        default
        return r;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(OutObject<Utf8String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadString(out span);
        value.set((r == Result.Success) ? Utf8String.CopyFrom(span) :)
        default
        return r;
    }

    /**
     * Read the current field as a variable length, UTF8 encoded, string value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadString(OutObject<Utf8Span> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUtf8)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseString(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
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
    public Result ReadUInt16(OutObject<Short> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUInt16)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseUInt16(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
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
    public Result ReadUInt32(OutObject<Integer> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUInt32)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseUInt32(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
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
    public Result ReadUInt64(OutObject<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUInt64)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseUInt64(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
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
    public Result ReadUInt8(OutObject<Byte> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUInt8)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseUInt8(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a fixed length <see cref="UnixDateTime" /> value.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadUnixDateTime(OutObject<UnixDateTime> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value.clone());
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutUnixDateTime)) {
                    value.set(null);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseUnixDateTime(tempRef_cursor).clone());
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read the current field as a variable length, 7-bit encoded, signed integer.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    public Result ReadVarInt(OutObject<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutVarInt)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseVarInt(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
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
    public Result ReadVarUInt(OutObject<Long> value) {
        switch (this.state) {
            case Schematized:
                return this.ReadPrimitiveValue(value);
            case Sparse:
                if (!(this.cursor.cellType instanceof LayoutVarUInt)) {
                    value.set(0);
                    return Result.TypeMismatch;
                }

                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                value.set(this.row.ReadSparseVarUInt(tempRef_cursor));
                this.cursor = tempRef_cursor.get();
                return Result.Success;
            default:
                value.set(0);
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
     * This method can be used when the overload of <see cref="ReadScope" /> that takes a
     * <see cref="ReaderFunc{TContext}" /> is not an option, such as when TContext is a ref struct.
     */
    public Result SkipScope(RefObject<RowReader> nestedReader) {
        if (nestedReader.get().cursor.start != this.cursor.valueOffset) {
            return Result.Failure;
        }

        RefObject<RowBuffer> tempRef_row =
            new RefObject<RowBuffer>(this.row);
        RefObject<RowCursor> tempRef_cursor =
            new RefObject<RowCursor>(nestedReader.get().cursor);
        RowCursorExtensions.Skip(this.cursor.clone(), tempRef_row,
            tempRef_cursor);
        nestedReader.get().argValue.cursor = tempRef_cursor.get();
        this.row = tempRef_row.get();
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
    private <TValue> Result ReadPrimitiveValue(OutObject<TValue> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof LayoutType<TValue>)) {
            value.set(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_row =
                    new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor =
                    new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<LayoutType<TValue>>TypeAs().ReadFixed(tempRef_row, tempRef_cursor, col, value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case Variable:
                RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 = new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<LayoutType<TValue>>TypeAs().ReadVariable(tempRef_row2, tempRef_cursor2, col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.set(null);
                return Result.Failure;
        }
    }

    /**
     * Read a generic schematized field value via the scope's layout.
     *
     * @param value On success, receives the value, undefined otherwise.
     * @return Success if the read is successful, an error code otherwise.
     */
    private Result ReadPrimitiveValue(OutObject<Utf8Span> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof ILayoutUtf8SpanReadable)) {
            value.set(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor = new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutUtf8SpanReadable>TypeAs().ReadFixed(tempRef_row, tempRef_cursor, col, value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case Variable:
                RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 = new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutUtf8SpanReadable>TypeAs().ReadVariable(tempRef_row2, tempRef_cursor2, col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.set(null);
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
    private <TElement> Result ReadPrimitiveValue(OutObject<ReadOnlySpan<TElement>> value) {
        LayoutColumn col = this.columns[this.columnIndex];
        LayoutType t = this.columns[this.columnIndex].Type;
        if (!(t instanceof ILayoutSpanReadable<TElement>)) {
            value.set(null);
            return Result.TypeMismatch;
        }

        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_row = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor = new RefObject<RowCursor>(this.cursor);
                Result tempVar = t.<ILayoutSpanReadable<TElement>>TypeAs().ReadFixed(tempRef_row, tempRef_cursor, col, value);
                this.cursor = tempRef_cursor.get();
                this.row = tempRef_row.get();
                return tempVar;
            case Variable:
                RefObject<RowBuffer> tempRef_row2 = new RefObject<RowBuffer>(this.row);
                RefObject<RowCursor> tempRef_cursor2 = new RefObject<RowCursor>(this.cursor);
                Result tempVar2 = t.<ILayoutSpanReadable<TElement>>TypeAs().ReadVariable(tempRef_row2, tempRef_cursor2, col, value);
                this.cursor = tempRef_cursor2.get();
                this.row = tempRef_row2.get();
                return tempVar2;
            default:
                throw new IllegalStateException();
                value.set(null);
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
     * A function to reader content from a <see cref="RowBuffer" />.
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param reader  A forward-only cursor for writing content.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface ReaderFunc<TContext> {
        Result invoke(RefObject<RowReader> reader, TContext context);
    }

    /**
     * An encapsulation of the current state of a <see cref="RowReader" /> that can be used to
     * recreate the <see cref="RowReader" /> in the same logical position.
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