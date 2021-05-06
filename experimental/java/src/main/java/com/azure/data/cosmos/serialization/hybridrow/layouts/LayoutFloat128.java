// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Float128;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutFloat128 extends LayoutTypePrimitive<Float128> {

    public LayoutFloat128() {
        super(LayoutCode.FLOAT_128, Float128.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "float128";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<Float128> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(buffer.readFloat128(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<Float128> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(buffer.readSparseFloat128(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Float128 value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFloat128(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Float128 value, @Nonnull UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseFloat128(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Float128 value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
