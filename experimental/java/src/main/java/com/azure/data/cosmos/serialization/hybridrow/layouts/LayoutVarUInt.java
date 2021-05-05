// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutVarUInt extends LayoutTypePrimitive<Long> {

    public LayoutVarUInt() {
        super(LayoutCode.VAR_UINT, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public boolean isVarint() {
        return true;
    }

    @Nonnull
    public String name() {
        return "varuint";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<Long> value) {
        assert false : "not implemented";
        value.set(0L);
        return Result.FAILURE;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<Long> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0L);
            return result;
        }

        value.set(buffer.readSparseVarUInt(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readVariable(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<Long> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0L);
            return Result.NOT_FOUND;
        }

        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableUInt(varOffset));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Long value) {
        assert false : "not implemented";
        return Result.FAILURE;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Long value, @Nonnull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseVarUInt(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Long value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public Result writeVariable(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn col, @Nonnull Long value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        final boolean exists = buffer.readBit(scope.start(), col.nullBit());
        final int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), col.offset());
        final int shift = buffer.writeVariableUInt(varOffset, value, exists);

        buffer.setBit(scope.start(), col.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);

        return Result.SUCCESS;
    }
}