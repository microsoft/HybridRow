// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutUtf8 extends LayoutTypePrimitive<String>
    implements LayoutUtf8Readable, LayoutUtf8Writable {

    public LayoutUtf8() {
        super(LayoutCode.UTF_8, 0);
    }

    public boolean isFixed() {
        return false;
    }

    @Nonnull
    public String name() {
        return "utf8";
    }

    @Override
    @Nonnull
    public Result readFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<String> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        Out<Utf8String> span = new Out<>();
        Result result = this.readFixedSpan(buffer, scope, column, span);
        value.set(result == Result.SUCCESS ? span.get().toUtf16() : null);

        return result;
    }

    @Override
    @Nonnull
    public Result readFixedSpan(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<Utf8String> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFixedString(scope.start() + column.offset(), column.size()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final Out<String> value) {

        Out<Utf8String> span = new Out<>();
        Result result = this.readSparseSpan(buffer, edit, span);
        value.set((result == Result.SUCCESS) ? span.get().toUtf16() : null);
        return result;
    }

    @Override
    @Nonnull
    public Result readSparseSpan(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final Out<Utf8String> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseString(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readVariable(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<String> value) {

        Out<Utf8String> span = new Out<>();
        Result result = this.readVariableSpan(buffer, scope, column, span);
        value.set(result == Result.SUCCESS ? span.get().toUtf16() : null);
        return result;
    }

    @Override
    @Nonnull
    public Result readVariableSpan(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Out<Utf8String> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableString(varOffset));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final String value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(column, "expected non-null column");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(value, "expected non-null value");

        return this.writeFixed(buffer, scope, column, Utf8String.transcodeUtf16(value));
    }

    @Override
    @Nonnull
    public Result writeFixed(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final LayoutColumn column,
        @Nonnull final Utf8String value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(column, "expected non-null column");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);
        checkArgument(value.encodedLength() == column.size());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFixedString(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull String value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull String value, @Nonnull UpdateOptions options) {
        checkArgument(value != null);
        return this.writeSparse(buffer, edit, Utf8String.transcodeUtf16(value), options);
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value, UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseString(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeVariable(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull String value) {
        checkArgument(value != null);
        return this.writeVariable(buffer, scope, column, Utf8String.transcodeUtf16(value));
    }

    @Override
    @Nonnull
    public Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = value.encodedLength();

        if ((column.size() > 0) && (length > column.size())) {
            return Result.TOO_BIG;
        }

        int offset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        boolean exists = buffer.readBit(scope.start(), column.nullBit());
        int shift = buffer.writeVariableString(offset, value, exists);

        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);

        return Result.SUCCESS;
    }
}