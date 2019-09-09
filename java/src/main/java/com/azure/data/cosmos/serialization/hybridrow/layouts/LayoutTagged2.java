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

public final class LayoutTagged2 extends LayoutIndexedScope {

    public LayoutTagged2(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TAGGED2_SCOPE : LayoutCode.TAGGED2_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 3);
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES, Integer::sum);
    }

    @Override
    public boolean hasImplicitTypeCode(RowCursor edit) {
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_tagged2_t" : "tagged2_t";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(RowBuffer buffer, int offset, Out<Integer> lenInBytes) {
        lenInBytes.set(0);
        TypeArgument[] retval = new TypeArgument[3];
        retval[0] = new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY);
        for (int i = 1; i < 3; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = readTypeArgument(buffer, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.set(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
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
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, UpdateOptions options,
                             Out<RowCursor> value) {

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

        checkState(value.count() == 3);

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;

        for (int i = 1; i < value.count(); i++) {
            TypeArgument arg = value.get(i);
            lenInBytes += arg.type().writeTypeArgument(buffer, offset + lenInBytes, arg.typeArgs());
        }

        return lenInBytes;
    }
}