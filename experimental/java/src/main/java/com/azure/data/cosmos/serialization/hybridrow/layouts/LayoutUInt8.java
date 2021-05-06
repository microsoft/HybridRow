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

public final class LayoutUInt8 extends LayoutTypePrimitive<Short> {

    public LayoutUInt8() {
        super(LayoutCode.UINT_8, 1);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "uint8";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<Short> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set((short) 0);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readUInt8(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<Short> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set((short) 0);
            return result;
        }

        value.set(buffer.readSparseUInt8(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Short value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeUInt8(scope.start() + column.offset(), value.byteValue());
        buffer.setBit(scope.start(), column.nullBit());

        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Short value, @Nonnull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseUInt8(edit, value.byteValue(), options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Short value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}