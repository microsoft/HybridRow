// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_SET_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_SET_SCOPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class LayoutTypedSet extends LayoutUniqueScope {

    public LayoutTypedSet(boolean immutable) {
        super(immutable ? IMMUTABLE_TYPED_SET_SCOPE : TYPED_SET_SCOPE, immutable, true, true);
    }

    @Override
    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Nonnull
    @Override
    public TypeArgument fieldType(@Nonnull final RowCursor scope) {
        checkNotNull(scope, "expected non-null scope");
        return scope.scopeTypeArgs().get(0);
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_set_t" : "set_t";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer,
        final int offset,
        @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        return new TypeArgumentList(readTypeArgument(buffer, offset, lengthInBytes));
    }

    @Override
    public void setImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
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

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedSet(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @Nonnull final RowBuffer buffer,
        final int offset,
        @Nonnull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);
        checkArgument(value.count() == 1, "expected a single value count, not %s", value.count());

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        final TypeArgument typeArg = value.get(0);
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += typeArg.type().writeTypeArgument(buffer, offset + lengthInBytes, typeArg.typeArgs());

        return lengthInBytes;
    }
}