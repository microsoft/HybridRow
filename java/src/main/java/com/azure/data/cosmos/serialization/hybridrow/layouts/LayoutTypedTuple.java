// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutTypedTuple extends LayoutIndexedScope {
    public LayoutTypedTuple(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_TUPLE_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_TUPLE_SCOPE, immutable, true, true, false, true);
    }

    public String name() {
        return this.Immutable ? "im_tuple_t" : "tuple_t";
    }

    public int countTypeArgument(TypeArgumentList value) {
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
        lenInBytes += RowBuffer.count7BitEncodedUInt(value.count());
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().CountTypeArgument(arg.typeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        checkArgument(edit.get().index() >= 0);
        checkArgument(edit.get().scopeTypeArgs().count() > edit.get().index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.get().scopeTypeArgs().get(edit.get().index()).type().LayoutCode);
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        int numTypeArgs = row.get().intValue().Read7BitEncodedUInt(offset, lenInBytes);
        TypeArgument[] retval = new TypeArgument[numTypeArgs];
        for (int i = 0; i < numTypeArgs; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = LayoutType.readTypeArgument(row, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.setAndGet(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.get().cellType = edit.get().scopeTypeArgs().get(edit.get().index()).type();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs().get(edit.get().index()).typeArgs().clone();
    }

    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return writeScope(buffer, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        Result result = LayoutType.prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        buffer.get().WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        row.get().writeSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
        lenInBytes += row.get().write7BitEncodedUInt(offset + lenInBytes, value.count());
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().writeTypeArgument(row, offset + lenInBytes, arg.typeArgs().clone());
        }

        return lenInBytes;
    }
}