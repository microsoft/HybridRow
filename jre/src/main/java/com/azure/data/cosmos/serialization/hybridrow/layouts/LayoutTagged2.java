//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTagged2 extends LayoutIndexedScope {
    public LayoutTagged2(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTagged2Scope :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Tagged2Scope, immutable, isSizedScope:
        true, isFixedArity:true, isUniqueScope:false, isTypedScope:true)
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tagged2_t" : "tagged2_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 3);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (int i = 1; i < value.getCount(); i++) {
            TypeArgument arg = value.get(i).clone();
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        checkState(edit.get().index >= 0);
        checkState(edit.get().scopeTypeArgs.getCount() > edit.get().index);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs.get(edit.get().index).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        lenInBytes.setAndGet(0);
        TypeArgument[] retval = new TypeArgument[3];
        retval[0] = new TypeArgument(UInt8, TypeArgumentList.Empty);
        for (int i = 1; i < 3; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = ReadTypeArgument(row, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.setAndGet(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(Reference<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeTypeArgs.get(edit.get().index).getType();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs.get(edit.get().index).getTypeArgs().clone();
    }

    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return WriteScope(b, edit, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value, UpdateOptions options) {
        Result result = PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.getCount() == 3);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (int i = 1; i < value.getCount(); i++) {
            TypeArgument arg = value.get(i).clone();
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}