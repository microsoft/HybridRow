//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutTypedTuple extends LayoutIndexedScope {
    public LayoutTypedTuple(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedTupleScope :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedTupleScope, immutable, true, true, false, true);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tuple_t" : "tuple_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
        lenInBytes += RowBuffer.Count7BitEncodedUInt(value.getCount());
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public boolean HasImplicitTypeCode(RefObject<RowCursor> edit) {
        checkArgument(edit.get().index >= 0);
        checkArgument(edit.get().scopeTypeArgs.getCount() > edit.get().index);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs.get(edit.get().index).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(RefObject<RowBuffer> row, int offset,
                                                 OutObject<Integer> lenInBytes) {
        int numTypeArgs = row.get().intValue().Read7BitEncodedUInt(offset, lenInBytes);
        TypeArgument[] retval = new TypeArgument[numTypeArgs];
        for (int i = 0; i < numTypeArgs; i++) {
            int itemLenInBytes;
            OutObject<Integer> tempOut_itemLenInBytes = new OutObject<Integer>();
            retval[i] = LayoutType.ReadTypeArgument(row, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.set(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(RefObject<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeTypeArgs.get(edit.get().index).getType();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs.get(edit.get().index).getTypeArgs().clone();
    }

    @Override
    public Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             TypeArgumentList typeArgs, OutObject<RowCursor> value) {
        return WriteScope(b, edit, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             TypeArgumentList typeArgs, OutObject<RowCursor> value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        b.get().WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
        lenInBytes += row.get().Write7BitEncodedUInt(offset + lenInBytes, value.getCount());
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}