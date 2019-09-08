// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.codecs.GuidCodec;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutGuid extends LayoutType<UUID> {

    public LayoutGuid() {
        super(LayoutCode.GUID, GuidCodec.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "guid";
    }

    @Override
    @Nonnull
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<UUID> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readGuid(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(RowBuffer buffer, RowCursor edit, Out<UUID> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseGuid(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, UUID value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeGuid(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, UUID value, UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseGuid(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, UUID value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }
}