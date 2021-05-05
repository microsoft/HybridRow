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

public final class LayoutBoolean extends LayoutTypePrimitive<Boolean> implements ILayoutType {

    public LayoutBoolean(boolean value) {
        super(value ? LayoutCode.BOOLEAN : LayoutCode.BOOLEAN_FALSE, 0);
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    @Nonnull
    public String name() {
        return "bool";
    }

    @Override
    @Nonnull
    public Result readFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<Boolean> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(false);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readBit(scope.start(), column.booleanBit()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final Out<Boolean> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.set(false);
            return result;
        }

        value.set(buffer.readSparseBoolean(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Boolean value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT,
            "expected scope of %s, not %s", LayoutUDT.class, scope.scopeType().getClass());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (value) {
            buffer.setBit(scope.start(), column.booleanBit());
        } else {
            buffer.unsetBit(scope.start(), column.booleanBit());
        }

        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final Boolean value,
        @Nonnull final UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBoolean(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Boolean value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}