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

public final class LayoutTypedMap extends LayoutUniqueScope {
    public LayoutTypedMap(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_MAP_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_MAP_SCOPE, immutable, isSizedScope():
        true, isTypedScope():true)
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 2);
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES, Integer::sum);
    }

    @Nonnull
    @Override
    public TypeArgument fieldType(RowCursor scope) {
        return new TypeArgument(
            scope.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE,
            scope.scopeTypeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(RowCursor edit) {
        return true;
    }

    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_map_t" : "map_t";
    }

    @Override
    public TypeArgumentList readTypeArgumentList(RowBuffer row, int offset,
                                                 Out<Integer> lenInBytes) {
        lenInBytes.setAndGet(0);
        TypeArgument[] retval = new TypeArgument[2];
        for (int i = 0; i < 2; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = readTypeArgument(row, offset + lenInBytes, tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes;
            lenInBytes.setAndGet(lenInBytes + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.cellType(edit.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE);
        edit.cellTypeArgs(edit.scopeTypeArgs());
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
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedMap(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        checkState(value.count() == 2);
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().writeTypeArgument(buffer, offset + lenInBytes, arg.typeArgs());
        }

        return lenInBytes;
    }
}