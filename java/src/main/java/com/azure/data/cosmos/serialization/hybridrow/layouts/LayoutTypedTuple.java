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

public final class LayoutTypedTuple extends LayoutIndexedScope {

    public LayoutTypedTuple(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_TUPLE_SCOPE : LayoutCode.TYPED_TUPLE_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES + RowBuffer.count7BitEncodedUInt(value.count()), Integer::sum);
    }

    @Override
    public boolean hasImplicitTypeCode(RowCursor edit) {
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_tuple_t" : "tuple_t";
    }

    @Override
    public TypeArgumentList readTypeArgumentList(RowBuffer row, int offset, Out<Integer> lenInBytes) {

        int numTypeArgs = row.read7BitEncodedUInt(offset, lenInBytes);
        TypeArgument[] typeArgs = new TypeArgument[numTypeArgs];

        for (int i = 0; i < numTypeArgs; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            typeArgs[i] = LayoutType.readTypeArgument(row, offset + lenInBytes, tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes;
            lenInBytes.setAndGet(lenInBytes + itemLenInBytes);
        }

        return new TypeArgumentList(typeArgs);
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
    }

    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
        lenInBytes += buffer.write7BitEncodedUInt(offset + lenInBytes, value.count());
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().writeTypeArgument(buffer, offset + lenInBytes, arg.typeArgs());
        }

        return lenInBytes;
    }
}