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
    public boolean hasImplicitTypeCode(RowCursor edit) {
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(RowBuffer buffer, int offset, Out<Integer> lenInBytes) {
        TypeArgument[] typeArgs = new TypeArgument[2];
        typeArgs[0] = new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY);
        typeArgs[1] = readTypeArgument(buffer, offset, lenInBytes);
        return new TypeArgumentList(typeArgs);
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
    }

    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        checkArgument(value.count() == 2);
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;
        lenInBytes += value.get(1).type().writeTypeArgument(buffer, offset + lenInBytes, value.get(1).typeArgs());
        return lenInBytes;
    }
}