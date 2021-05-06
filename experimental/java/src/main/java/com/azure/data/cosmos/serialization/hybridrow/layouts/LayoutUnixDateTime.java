// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutUnixDateTime extends LayoutTypePrimitive<UnixDateTime> {

    public LayoutUnixDateTime() {
        super(LayoutCode.UNIX_DATE_TIME, UnixDateTime.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "unixdatetime";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<UnixDateTime> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readUnixDateTime(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<UnixDateTime> value) {
        Result result = prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseUnixDateTime(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull UnixDateTime value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeUnixDateTime(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());

        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull UnixDateTime value, @Nonnull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseUnixDateTime(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull UnixDateTime value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}