// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutBinary extends LayoutTypePrimitive<byte[]> {
    // implements
    // LayoutSpanWritable<Byte>,
    // LayoutSpanReadable<Byte>,
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
        @Nonnull final Out<byte[]> value) {

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

        value.set(ByteBufUtil.getBytes(buffer.readFixedBinary(scope.start() + column.offset(), column.size())));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit, @Nonnull final Out<byte[]> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        final Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(ByteBufUtil.getBytes(buffer.readSparseBinary(edit)));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readVariable(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull Out<byte[]> value) {

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
        value.set(ByteBufUtil.getBytes(buffer.readVariableBinary(valueOffset)));

        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull byte[] value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);
        checkArgument(value.length == column.size());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        final ByteBuf valueBuffer = Unpooled.wrappedBuffer(value).asReadOnly();
        final int valueOffset = scope.start() + column.offset();
        buffer.setBit(scope.start(), column.nullBit());

        buffer.writeFixedBinary(valueOffset, valueBuffer, column.size());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull byte[] value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public Result writeSparse(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor edit,
        @Nonnull byte[] value,
        @Nonnull UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBinary(edit, Unpooled.wrappedBuffer(value).asReadOnly(), options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeVariable(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor scope,
        @Nonnull LayoutColumn column,
        @Nonnull byte[] value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if ((column.size() > 0) && (value.length > column.size())) {
            return Result.TOO_BIG;
        }

        final boolean exists = buffer.readBit(scope.start(), column.nullBit());
        final ByteBuf valueBuffer = Unpooled.wrappedBuffer(value).asReadOnly();
        final int valueOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        final Out<Integer> shift = new Out<>();

        buffer.writeVariableBinary(valueOffset, valueBuffer, exists, shift);
        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift.get());
        scope.valueOffset(scope.valueOffset() + shift.get());

        return Result.SUCCESS;
    }
}