// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutFloat32 extends LayoutType<Float> {

    public LayoutFloat32() {
        super(LayoutCode.FLOAT_32, Float.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "float32";
    }

    @Override
    @Nonnull
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Float> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0F);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFloat32(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(RowBuffer buffer, RowCursor edit, Out<Float> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0F);
            return result;
        }

        value.set(buffer.readSparseFloat32(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Float value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFloat32(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Float value, UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseFloat32(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Float value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }
}