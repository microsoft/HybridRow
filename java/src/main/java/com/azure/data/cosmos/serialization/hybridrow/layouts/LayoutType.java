// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;

/**
 * Describes the physical byte layout of a hybrid row field of a specific physical type {@code T}
 *
 * {@link LayoutType<T>} is an immutable, stateless, helper class. It provides methods for manipulating hybrid row
 * fields of a particular type, and properties that describe the layout of fields of that type.
 * <p>
 * {@param <T>} The specific physical type whose byte layout is represented by this class.
 */
public abstract class LayoutType<T> implements ILayoutType {

    private static final LayoutType[] codeIndex = new LayoutType[LayoutCode.END_SCOPE.value() + 1];

    private final boolean immutable;
    private final LayoutCode layoutCode;
    private final int size;
    private final TypeArgument typeArg;

    /**
     * Initializes a new instance of the {@link LayoutType<T>} class.
     */
    protected LayoutType(@Nonnull final LayoutCode code, final boolean immutable, final int size) {

        checkNotNull(code, "expected non-null code");

        this.layoutCode = code;
        this.immutable = immutable;
        this.size = size;
        this.typeArg = new TypeArgument(this);

        codeIndex[code.value()] = this;
    }

    /**
     * Initializes a new instance of the {@link LayoutType<T>} class.
     */
    protected LayoutType(LayoutCode code, int size) {
        this(code, false, size);
    }

    /**
     * True if this type is a boolean.
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * True if this type is always fixed length.
     */
    public abstract boolean isFixed();

    /**
     * If true, this edit's nested fields cannot be updated individually.
     * The entire edit can still be replaced.
     */
    public boolean isImmutable() {
        return this.immutable;
    }

    /**
     * True if this type is a literal null.
     */
    public boolean isNull() {
        return false;
    }

    /**
     * True if this type is a variable-length encoded integer type (either signed or unsigned).
     */
    public boolean isVarint() {
        return false;
    }

    /**
     * True if this type can be used in the variable-length segment.
     */
    public final boolean allowVariable() {
        return !this.isFixed();
    }

    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        return LayoutCode.BYTES;
    }

    @Nonnull
    public final Result deleteFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (column.nullBit().isInvalid()) {
            // Cannot delete a non-nullable fixed column.
            return Result.TYPE_MISMATCH;
        }

        buffer.unsetBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     * @param buffer
     * @param edit
     */
    @Nonnull
    public final Result deleteSparse(RowBuffer buffer, RowCursor edit) {

        Result result = LayoutType.prepareSparseDelete(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.deleteSparse(edit);
        return Result.SUCCESS;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accommodate a decrease in
     * required space.  If no value exists this operation is a no-op.
     */
    @Nonnull
    public final Result deleteVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        boolean exists = buffer.readBit(scope.start(), column.nullBit());

        if (exists) {
            int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
            buffer.deleteVariable(varOffset, this.isVarint());
            buffer.unsetBit(scope.start(), column.nullBit());
        }

        return Result.SUCCESS;
    }

    @Nonnull
    public static LayoutType fromCode(LayoutCode code) {
        LayoutType type = LayoutType.codeIndex[code.value()];
        assert type != null : lenientFormat("Not Implemented: %s", code);
        return type;
    }

    @Nonnull
    public final Result hasValue(RowBuffer buffer, RowCursor scope, LayoutColumn column) {
        if (!buffer.readBit(scope.start(), column.nullBit())) {
            return Result.NOT_FOUND;
        }
        return Result.SUCCESS;
    }

    /**
     * The physical layout code used to represent the type within the serialization.
     */
    @Nonnull
    public LayoutCode layoutCode() {
        return this.layoutCode;
    }

    /**
     * Human readable name of the type.
     */
    @Nonnull
    public abstract String name();

    /**
     * Helper for preparing the delete of a sparse field.
     *
     * @param buffer    The row to delete from.
     * @param edit The parent edit containing the field to delete.
     * @param code The expected type of the field.
     * @return Success if the delete is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseDelete(RowBuffer buffer, RowCursor edit, LayoutCode code) {

        if (edit.scopeType().isFixedArity()) {
            return Result.TYPE_CONSTRAINT;
        }

        if (edit.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (edit.exists() && LayoutCodeTraits.canonicalize(edit.cellType().layoutCode()) != code) {
            return Result.TYPE_MISMATCH;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the move of a sparse field into an existing restricted edit.
     *
     * @param buffer                The row to read from.
     * @param destinationScope The parent set edit into which the field should be moved.
     * @param destinationCode  The expected type of the edit moving within.
     * @param elementType      The expected type of the elements within the edit.
     * @param srcEdit          The field to be moved.
     * @param options          The move options.
     * @param dstEdit          If successful, a prepared insertion cursor for the destination.
     * @return Success if the move is permitted, the error code otherwise.
     * The source field is delete if the move prepare fails with a destination error.
     */
    @Nonnull
    public static Result prepareSparseMove(
        RowBuffer buffer,
        RowCursor destinationScope,
        LayoutScope destinationCode,
        TypeArgument elementType,
        RowCursor srcEdit,
        UpdateOptions options,
        Out<RowCursor> dstEdit) {

        checkArgument(destinationScope.scopeType() == destinationCode);
        checkArgument(destinationScope.index() == 0, "Can only insert into a edit at the root");

        // Prepare the delete of the source
        Result result = LayoutType.prepareSparseDelete(buffer, srcEdit, elementType.type().layoutCode());

        if (result != Result.SUCCESS) {
            dstEdit.set(null);
            return result;
        }

        if (!srcEdit.exists()) {
            dstEdit.set(null);
            return Result.NOT_FOUND;
        }

        if (destinationScope.immutable()) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (!srcEdit.cellTypeArgs().equals(elementType.typeArgs())) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TYPE_CONSTRAINT;
        }

        if (options == UpdateOptions.INSERT_AT) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TYPE_CONSTRAINT;
        }

        // Prepare the insertion at the destination.
        dstEdit.set(buffer.prepareSparseMove(destinationScope, srcEdit));

        if ((options == UpdateOptions.UPDATE) && (!dstEdit.get().exists())) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.NOT_FOUND;
        }

        if ((options == UpdateOptions.INSERT) && dstEdit.get().exists()) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.EXISTS;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the read of a sparse field.
     *
     * @param buffer    The row to read from.
     * @param edit The parent edit containing the field to read.
     * @param code The expected type of the field.
     * @return Success if the read is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseRead(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit, @Nonnull LayoutCode code) {

        if (!edit.exists()) {
            return Result.NOT_FOUND;
        }

        if (LayoutCodeTraits.canonicalize(edit.cellType().layoutCode()) != code) {
            return Result.TYPE_MISMATCH;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param buffer       The row to write to.
     * @param edit    The cursor for the field to write.
     * @param typeArg The (optional) type constraints.
     * @param options The write options.
     * @return Success if the write is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseWrite(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgument typeArg,
        @Nonnull final UpdateOptions options) {

        if (edit.immutable() || (edit.scopeType().isUniqueScope() && !edit.deferUniqueIndex())) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (edit.scopeType().isFixedArity() && !(edit.scopeType() instanceof LayoutNullable)) {
            if ((edit.index() < edit.scopeTypeArgs().count()) && !typeArg.equals(edit.scopeTypeArgs().get(edit.index()))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (edit.scopeType() instanceof LayoutTypedMap) {
            if (!((typeArg.type() instanceof LayoutTypedTuple) && typeArg.typeArgs().equals(edit.scopeTypeArgs()))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (edit.scopeType().isTypedScope() && !typeArg.equals(edit.scopeTypeArgs().get(0))) {
            return Result.TYPE_CONSTRAINT;
        }

        if ((options == UpdateOptions.INSERT_AT) && edit.scopeType().isFixedArity()) {
            return Result.TYPE_CONSTRAINT;
        }

        if ((options == UpdateOptions.INSERT_AT) && !edit.scopeType().isFixedArity()) {
            edit.exists(false); // InsertAt never overwrites an existing item.
        }

        if ((options == UpdateOptions.UPDATE) && (!edit.exists())) {
            return Result.NOT_FOUND;
        }

        if ((options == UpdateOptions.INSERT) && edit.exists()) {
            return Result.EXISTS;
        }

        return Result.SUCCESS;
    }

    @Nonnull
    public abstract Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<T> value);

    @Nonnull
    public abstract Result readSparse(RowBuffer buffer, RowCursor edit, Out<T> value);

    @Nonnull
    public static TypeArgument readTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        LayoutType type = buffer.readSparseTypeCode(offset);
        TypeArgumentList typeArgs = type.readTypeArgumentList(buffer, offset + LayoutCode.BYTES, lengthInBytes);
        lengthInBytes.set(LayoutCode.BYTES + lengthInBytes.get());

        return new TypeArgument(type, typeArgs);
    }

    @Nonnull
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        lengthInBytes.set(0);
        return TypeArgumentList.EMPTY;
    }

    @Nonnull
    public Result readVariable(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<T> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        value.set(null);
        return Result.FAILURE;
    }

    /**
     * If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.
     */
    public int size() {
        return this.size;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     */
    @SuppressWarnings("unchecked")
    public final <Value extends ILayoutType> Value typeAs() {
        return (Value)this;
    }

    @Nonnull
    public abstract Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, T value);

    @Nonnull
    public abstract Result writeSparse(RowBuffer buffer, RowCursor edit, T value);

    @Nonnull
    public abstract Result writeSparse(RowBuffer buffer, RowCursor edit, T value, UpdateOptions options);

    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        return LayoutCode.BYTES;
    }

    @Nonnull
    public Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, T value) {
        return Result.FAILURE;
    }

    TypeArgument typeArg() {
        return this.typeArg;
    }
}