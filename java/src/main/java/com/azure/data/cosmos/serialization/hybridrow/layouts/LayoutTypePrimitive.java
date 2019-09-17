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

public abstract class LayoutTypePrimitive<T> extends LayoutType implements ILayoutType {
    /**
     * Initializes a new instance of the {@link LayoutTypePrimitive<T>} class.
     *
     * @param code
     * @param immutable
     * @param size
     */
    protected LayoutTypePrimitive(@Nonnull LayoutCode code, boolean immutable, int size) {
        super(code, immutable, size);
    }

    /**
     * Initializes a new instance of the {@link LayoutTypePrimitive<T>} class.
     *
     * @param code
     * @param size
     */
    protected LayoutTypePrimitive(LayoutCode code, int size) {
        super(code, size);
    }
    // TODO: DANOBLE: move methods implemented by the C# code LayoutType<T> to this type from LayoutType<T>
    // Also:
    // * Convert LayoutType<T> to a non-generic type (LayoutType, not LayoutType<T>)
    // * Ensure that all primitive types inherit from this type

    @Nonnull
    public final Result hasValue(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor scope, @Nonnull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            return Result.NOT_FOUND;
        }
        return Result.SUCCESS;
    }

    @Nonnull
    public final Result deleteFixed(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor scope, @Nonnull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");

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
     * If a value exists, then it is removed.  The remainder of the row is resized to accommodate
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
    public final Result deleteVariable(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
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
    public abstract Result readFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<T> value);

    @Nonnull
    public abstract Result readSparse(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final  Out<T> value);

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

    @Nonnull
    public abstract Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull T value);

    @Nonnull
    public abstract Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull T value);

    @Nonnull
    public abstract Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull T value, @Nonnull UpdateOptions options);

    @Nonnull
    public Result writeVariable(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull T value) {
        return Result.FAILURE;
    }
}
