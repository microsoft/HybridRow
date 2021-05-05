// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TAGGED_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TAGGED_SCOPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutTagged extends LayoutIndexedScope {

    public LayoutTagged(boolean immutable) {
        super(immutable ? IMMUTABLE_TAGGED_SCOPE : TAGGED_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2);
        return LayoutCode.BYTES + value.get(1).type().countTypeArgument(value.get(1).typeArgs());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_tagged_t" : "tagged_t";
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        return new TypeArgumentList(
            new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY),
            readTypeArgument(buffer, offset, lengthInBytes)
        );
    }

    @Override
    public void setImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs, @Nonnull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options, final @Nonnull Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2);

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        final TypeArgument typeArg = value.get(1);
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += typeArg.type().writeTypeArgument(buffer, offset + lengthInBytes, typeArg.typeArgs());

        return lengthInBytes;
    }
}