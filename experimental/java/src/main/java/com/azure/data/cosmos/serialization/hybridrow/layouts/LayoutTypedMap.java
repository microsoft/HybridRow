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
import static com.google.common.base.Preconditions.checkState;

public final class LayoutTypedMap extends LayoutUniqueScope {

    public LayoutTypedMap(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_MAP_SCOPE : LayoutCode.TYPED_MAP_SCOPE, immutable,
            true, true);
    }

    @Override
    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 2);
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES, Integer::sum);
    }

    @Nonnull
    @Override
    public TypeArgument fieldType(@Nonnull final RowCursor scope) {
        checkNotNull(scope, "expected non-null scope");
        return new TypeArgument(
            scope.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE,
            scope.scopeTypeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        return true;
    }

    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_map_t" : "map_t";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");

        TypeArgument[] typeArguments = new TypeArgument[2];
        Out<Integer> length = new Out<>();
        int index = 0;

        for (int i = 0; i < 2; i++) {
            typeArguments[i] = readTypeArgument(buffer, offset + index, length);
            index += length.get();
        }

        lengthInBytes.set(index);
        return new TypeArgumentList(typeArguments);
    }

    @Override
    public void setImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE);
        edit.cellTypeArgs(edit.scopeTypeArgs());
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options,
        @Nonnull final Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        final Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedMap(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2, "expected value count of 2, not %s", value.count());

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;

        for (TypeArgument arg : value.list()) {
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}