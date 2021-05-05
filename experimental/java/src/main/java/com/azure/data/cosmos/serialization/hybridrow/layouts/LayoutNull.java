// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutNull extends LayoutTypePrimitive<NullValue> implements ILayoutType {

    public LayoutNull() {
        super(LayoutCode.NULL, 0);
    }

    public boolean isFixed() {
        return true;
    }

    public boolean isNull() {
        return true;
    }

    @Nonnull
    public String name() {
        return "null";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<NullValue> value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        value.set(NullValue.DEFAULT);
        if (!buffer.readBit(scope.start(), column.nullBit())) {
            return Result.NOT_FOUND;
        }
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<NullValue> value) {
        Result result = prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }
        value.set(buffer.readSparseNull(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull NullValue value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull NullValue value, @Nonnull UpdateOptions options) {
        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);
        if (result != Result.SUCCESS) {
            return result;
        }
        buffer.writeSparseNull(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull NullValue value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}