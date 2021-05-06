// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutBinary extends LayoutTypePrimitive<ByteBuf> {
    // implements
    // LayoutListWritable<Byte>,
    // LayoutListReadable<Byte>,
    // ILayoutSequenceWritable<Byte> {

    public LayoutBinary() {
        super(LayoutCode.BINARY, 0);
    }

    public boolean isFixed() {
        return false;
    }

    @Nonnull
    public String name() {
        return "binary";
    }

    @Override
    @Nonnull
    public Result readFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFixedBinary(scope.start() + column.offset(), column.size()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit, @Nonnull final Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        final Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseBinary(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readVariable(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        final int valueOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableBinary(valueOffset));

        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull ByteBuf value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        final int valueOffset = scope.start() + column.offset();
        buffer.setBit(scope.start(), column.nullBit());

        buffer.writeFixedBinary(valueOffset, value, column.size());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull ByteBuf value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public Result writeSparse(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor edit,
        @Nonnull ByteBuf value,
        @Nonnull UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeVariable(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull ByteBuf value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if ((column.size() > 0) && (value.readableBytes() > column.size())) {
            return Result.TOO_BIG;
        }

        final boolean exists = buffer.readBit(scope.start(), column.nullBit());
        final int valueOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        final Out<Integer> shift = new Out<>();

        buffer.writeVariableBinary(valueOffset, value, exists, shift);
        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift.get());
        scope.valueOffset(scope.valueOffset() + shift.get());

        return Result.SUCCESS;
    }
}