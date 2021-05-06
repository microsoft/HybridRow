// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.codecs.DateTimeCodec;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutTypePrimitive<OffsetDateTime> {

    public LayoutDateTime() {
        super(LayoutCode.DATE_TIME, DateTimeCodec.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "datetime";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<OffsetDateTime> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(OffsetDateTime.MIN);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readDateTime(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<OffsetDateTime> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(OffsetDateTime.MIN);
            return result;
        }

        value.set(buffer.readSparseDateTime(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull OffsetDateTime value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeDateTime(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull OffsetDateTime value, @Nonnull UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseDateTime(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull OffsetDateTime value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}