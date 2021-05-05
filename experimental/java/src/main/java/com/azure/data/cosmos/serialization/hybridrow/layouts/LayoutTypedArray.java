// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class LayoutTypedArray extends LayoutIndexedScope {

    public LayoutTypedArray(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_ARRAY_SCOPE : LayoutCode.TYPED_ARRAY_SCOPE, immutable,
            true, false, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull RowCursor edit) {
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_array_t" : "array_t";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(@Nonnull RowBuffer buffer, int offset, @Nonnull Out<Integer> lenInBytes) {
        return new TypeArgumentList(LayoutType.readTypeArgument(buffer, offset, lenInBytes));
    }

    @Override
    public void setImplicitTypeCode(@Nonnull final RowCursor edit) {
        edit.cellType(edit.scopeTypeArgs().get(0).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(0).typeArgs());
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

        final TypeArgument typeArg = new TypeArgument(this, typeArgs);
        final Result result = LayoutType.prepareSparseWrite(buffer, edit, typeArg, options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedArray(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final TypeArgumentList value) {

        checkState(value.count() == 1);

        TypeArgument typeArg = value.get(0);
        buffer.writeSparseTypeCode(offset, this.layoutCode());

        return LayoutCode.BYTES + typeArg.type().writeTypeArgument(
            buffer, offset + LayoutCode.BYTES, typeArg.typeArgs()
        );
    }
}