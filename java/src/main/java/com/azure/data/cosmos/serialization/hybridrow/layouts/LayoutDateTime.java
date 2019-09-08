// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.codecs.DateTimeCodec;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutType<OffsetDateTime> {

    public LayoutDateTime() {
        super(LayoutCode.DATE_TIME, DateTimeCodec.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "datetime";
    }

    @Override
    @Nonnull
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<OffsetDateTime> value) {

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
    public Result readSparse(RowBuffer buffer, RowCursor edit, Out<OffsetDateTime> value) {

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
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, OffsetDateTime value) {

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
    public Result writeSparse(RowBuffer b, RowCursor edit, OffsetDateTime value, UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        b.writeSparseDateTime(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, OffsetDateTime value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }
}