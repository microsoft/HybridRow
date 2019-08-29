//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;
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

    private static final LayoutType[] CodeIndex = new LayoutType[LayoutCode.END_SCOPE.value() + 1];

    private final boolean immutable;
    private final LayoutCode layoutCode;
    private final int size;
    private final TypeArgument typeArg;

    /**
     * Initializes a new instance of the {@link LayoutType<T>} class.
     */
    protected LayoutType(LayoutCode code, boolean immutable, int size) {
        this.layoutCode = code;
        this.immutable = immutable;
        this.size = size;
        this.typeArg = new TypeArgument(this);
        CodeIndex[code.value()] = this;
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
        return immutable;
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

    public int countTypeArgument(TypeArgumentList value) {
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
    }

    public final Result deleteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col) {

        checkArgument(scope.get().scopeType() instanceof LayoutUDT);

        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        if (col.getNullBit().getIsInvalid()) {
            // Cannot delete a non-nullable fixed column.
            return Result.TypeMismatch;
        }

        b.get().UnsetBit(scope.get().start(), col.getNullBit().clone());
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    public final Result deleteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit) {

        Result result = LayoutType.prepareSparseDelete(b, edit, this.layoutCode());

        if (result != Result.Success) {
            return result;
        }

        b.get().deleteSparse(edit);
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accommodate a decrease in
     * required space.  If no value exists this operation is a no-op.
     */
    public final Result deleteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col) {

        checkArgument(scope.get().scopeType() instanceof LayoutUDT);

        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.get().ReadBit(scope.get().start(), col.getNullBit());

        if (exists) {
            int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
                col.getOffset());
            b.get().deleteVariable(varOffset, this.isVarint());
            b.get().UnsetBit(scope.get().start(), col.getNullBit().clone());
        }

        return Result.Success;
    }

    public static LayoutType fromCode(LayoutCode code) {
        LayoutType type = LayoutType.CodeIndex[code.value()];
        assert type != null : lenientFormat("Not Implemented: %s", code);
        return type;
    }

    public final Result hasValue(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col) {
        if (!b.get().ReadBit(scope.get().start(), col.getNullBit().clone())) {
            return Result.NotFound;
        }
        return Result.Success;
    }

    /**
     * The physical layout code used to represent the type within the serialization.
     */
    public LayoutCode layoutCode() {
        return this.layoutCode;
    }

    /**
     * Human readable name of the type.
     */
    public abstract String name();

    /**
     * Helper for preparing the delete of a sparse field.
     *
     * @param b    The row to delete from.
     * @param edit The parent edit containing the field to delete.
     * @param code The expected type of the field.
     * @return Success if the delete is permitted, the error code otherwise.
     */
    public static Result prepareSparseDelete(Reference<RowBuffer> b, Reference<RowCursor> edit, LayoutCode code) {
        if (edit.get().scopeType().isFixedArity()) {
            return Result.TypeConstraint;
        }

        if (edit.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        if (edit.get().exists() && LayoutCodeTraits.Canonicalize(edit.get().cellType().layoutCode()) != code) {
            return Result.TypeMismatch;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the move of a sparse field into an existing restricted edit.
     *
     * @param b                The row to read from.
     * @param destinationScope The parent set edit into which the field should be moved.
     * @param destinationCode  The expected type of the edit moving within.
     * @param elementType      The expected type of the elements within the edit.
     * @param srcEdit          The field to be moved.
     * @param options          The move options.
     * @param dstEdit          If successful, a prepared insertion cursor for the destination.
     * @return Success if the move is permitted, the error code otherwise.
     * The source field is delete if the move prepare fails with a destination error.
     */
    public static Result prepareSparseMove(
        Reference<RowBuffer> b,
        Reference<RowCursor> destinationScope,
        LayoutScope destinationCode,
        TypeArgument elementType,
        Reference<RowCursor> srcEdit,
        UpdateOptions options,
        Out<RowCursor> dstEdit
    ) {
        checkArgument(destinationScope.get().scopeType() == destinationCode);
        checkArgument(destinationScope.get().index() == 0, "Can only insert into a edit at the root");

        // Prepare the delete of the source
        Result result = LayoutType.prepareSparseDelete(b, srcEdit, elementType.type().layoutCode());

        if (result != Result.Success) {
            dstEdit.setAndGet(null);
            return result;
        }

        if (!srcEdit.get().exists()) {
            dstEdit.setAndGet(null);
            return Result.NotFound;
        }

        if (destinationScope.get().immutable()) {
            b.get().deleteSparse(srcEdit);
            dstEdit.setAndGet(null);
            return Result.InsufficientPermissions;
        }

        if (!srcEdit.get().cellTypeArgs().equals(elementType.typeArgs())) {
            b.get().deleteSparse(srcEdit);
            dstEdit.setAndGet(null);
            return Result.TypeConstraint;
        }

        if (options == UpdateOptions.InsertAt) {
            b.get().deleteSparse(srcEdit);
            dstEdit.setAndGet(null);
            return Result.TypeConstraint;
        }

        // Prepare the insertion at the destination.
        dstEdit.setAndGet(b.get().PrepareSparseMove(destinationScope, srcEdit));
        if ((options == UpdateOptions.Update) && (!dstEdit.get().exists())) {
            b.get().deleteSparse(srcEdit);
            dstEdit.setAndGet(null);
            return Result.NotFound;
        }

        if ((options == UpdateOptions.Insert) && dstEdit.get().exists()) {
            b.get().deleteSparse(srcEdit);
            dstEdit.setAndGet(null);
            return Result.Exists;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the read of a sparse field.
     *
     * @param b    The row to read from.
     * @param edit The parent edit containing the field to read.
     * @param code The expected type of the field.
     * @return Success if the read is permitted, the error code otherwise.
     */
    public static Result prepareSparseRead(Reference<RowBuffer> b, Reference<RowCursor> edit, LayoutCode code) {

        if (!edit.get().exists()) {
            return Result.NotFound;
        }

        if (LayoutCodeTraits.Canonicalize(edit.get().cellType().layoutCode()) != code) {
            return Result.TypeMismatch;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param b       The row to write to.
     * @param edit    The cursor for the field to write.
     * @param typeArg The (optional) type constraints.
     * @param options The write options.
     * @return Success if the write is permitted, the error code otherwise.
     */
    public static Result prepareSparseWrite(Reference<RowBuffer> b, Reference<RowCursor> edit,
                                            TypeArgument typeArg, UpdateOptions options) {
        if (edit.get().immutable() || (edit.get().scopeType().isUniqueScope() && !edit.get().deferUniqueIndex())) {
            return Result.InsufficientPermissions;
        }

        if (edit.get().scopeType().isFixedArity() && !(edit.get().scopeType() instanceof LayoutNullable)) {
            if ((edit.get().index() < edit.get().scopeTypeArgs().count()) && !typeArg.equals(edit.get().scopeTypeArgs().get(edit.get().index()))) {
                return Result.TypeConstraint;
            }
        } else if (edit.get().scopeType() instanceof LayoutTypedMap) {
            if (!((typeArg.type() instanceof LayoutTypedTuple) && typeArg.typeArgs().equals(edit.get().scopeTypeArgs()))) {
                return Result.TypeConstraint;
            }
        } else if (edit.get().scopeType().isTypedScope() && !typeArg.equals(edit.get().scopeTypeArgs().get(0))) {
            return Result.TypeConstraint;
        }

        if ((options == UpdateOptions.InsertAt) && edit.get().scopeType().isFixedArity()) {
            return Result.TypeConstraint;
        }

        if ((options == UpdateOptions.InsertAt) && !edit.get().scopeType().isFixedArity()) {
            edit.get().exists = false; // InsertAt never overwrites an existing item.
        }

        if ((options == UpdateOptions.Update) && (!edit.get().exists())) {
            return Result.NotFound;
        }

        if ((options == UpdateOptions.Insert) && edit.get().exists()) {
            return Result.Exists;
        }

        return Result.Success;
    }

    public abstract Result readFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<T> value);

    public abstract Result readSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Out<T> value);

    public static TypeArgument readTypeArgument(Reference<RowBuffer> row, int offset, Out<Integer> lenInBytes) {
        LayoutType itemCode = row.get().ReadSparseTypeCode(offset);
        int argsLenInBytes;
        Out<Integer> tempOut_argsLenInBytes = new Out<Integer>();
        TypeArgumentList itemTypeArgs = itemCode.readTypeArgumentList(row, offset + (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE), tempOut_argsLenInBytes);
        argsLenInBytes = tempOut_argsLenInBytes.get();
        lenInBytes.setAndGet((com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + argsLenInBytes);
        return new TypeArgument(itemCode, itemTypeArgs);
    }

    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset, Out<Integer> lenInBytes) {
        lenInBytes.setAndGet(0);
        return TypeArgumentList.EMPTY;
    }

    public Result readVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, Out<T> value) {
        value.setAndGet(null);
        return Result.Failure;
    }

    /**
     * If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.
     */
    public int size() {
        return size;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     */
    @SuppressWarnings("unchecked")
    public final <Value extends ILayoutType> Value typeAs() {
        return (Value)this;
    }

    public abstract Result writeFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, T value);

    public abstract Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, T value);

    public abstract Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, T value, UpdateOptions options);

    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        row.get().WriteSparseTypeCode(offset, this.layoutCode());
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
    }

    public Result writeVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col, T value) {
        return Result.Failure;
    }

    TypeArgument typeArg() {
        return this.typeArg;
    }
}