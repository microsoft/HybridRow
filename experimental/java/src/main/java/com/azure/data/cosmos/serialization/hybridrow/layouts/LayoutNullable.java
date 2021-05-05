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

public final class LayoutNullable extends LayoutIndexedScope {

    public LayoutNullable(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_NULLABLE_SCOPE : LayoutCode.NULLABLE_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_nullable" : "nullable";
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() == 1);
        checkArgument(edit.index() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    public static Result hasValue(@Nonnull final RowBuffer buffer, @Nonnull final RowCursor scope) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkArgument(scope.scopeType() instanceof LayoutNullable);
        checkArgument(scope.index() == 1 || scope.index() == 2);
        checkArgument(scope.scopeTypeArgs().count() == 1);
        boolean hasValue = buffer.readInt8(scope.start()) != 0;
        return hasValue ? Result.SUCCESS : Result.NOT_FOUND;
    }

    @Nonnull
    @Override
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        return new TypeArgumentList(LayoutType.readTypeArgument(buffer, offset, lengthInBytes));
    }

    @Override
    public void setImplicitTypeCode(@Nonnull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkState(edit.index() == 1);
        edit.cellType(edit.scopeTypeArgs().get(0).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(0).typeArgs());
    }

    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        boolean hasValue,
        @Nonnull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, hasValue, UpdateOptions.UPSERT, value);
    }

    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        boolean hasValue,
        @Nonnull final UpdateOptions options,
        @Nonnull final Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = LayoutType.prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        buffer.writeNullable(edit, this, typeArgs, options, hasValue);
        return Result.SUCCESS;
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
        return this.writeScope(buffer, edit, typeArgs, true, options, value);
    }

    @Override
    public int writeTypeArgument(@Nonnull final RowBuffer buffer, int offset, @Nonnull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0);
        checkArgument(value.count() == 1);

        final TypeArgument typeArg = value.get(0);
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        return LayoutCode.BYTES + typeArg.type().writeTypeArgument(buffer, offset + LayoutCode.BYTES, typeArg.typeArgs());
    }
}