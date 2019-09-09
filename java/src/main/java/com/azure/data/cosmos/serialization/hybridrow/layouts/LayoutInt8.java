// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutInt8 extends LayoutType<Byte> {

    public LayoutInt8() {
        super(LayoutCode.INT_8, Byte.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "int8";
    }

    @Override
    @Nonnull
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Byte> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set((byte) 0);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readInt8(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(RowBuffer buffer, RowCursor edit, Out<Byte> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set((byte) 0);
            return result;
        }

        value.set(buffer.readSparseInt8(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Byte value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeInt8(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Byte value, UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseInt8(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Byte value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}