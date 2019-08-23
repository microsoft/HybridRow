//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTypedMap extends LayoutUniqueScope {
    public LayoutTypedMap(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedMapScope :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedMapScope, immutable, isSizedScope:
        true, isTypedScope:true)
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_map_t" : "map_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 2);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public TypeArgument FieldType(RefObject<RowCursor> scope) {
        return new TypeArgument(scope.get().scopeType.Immutable ? ImmutableTypedTuple :
            TypedTuple, scope.get().scopeTypeArgs.clone());
    }

    @Override
    public boolean HasImplicitTypeCode(RefObject<RowCursor> edit) {
        return true;
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(RefObject<RowBuffer> row, int offset,
                                                 OutObject<Integer> lenInBytes) {
        lenInBytes.set(0);
        TypeArgument[] retval = new TypeArgument[2];
        for (int i = 0; i < 2; i++) {
            int itemLenInBytes;
            OutObject<Integer> tempOut_itemLenInBytes = new OutObject<Integer>();
            retval[i] = ReadTypeArgument(row, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.set(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(RefObject<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeType.Immutable ? ImmutableTypedTuple :
            TypedTuple;
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs.clone();
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
        Result result = PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        b.get().WriteTypedMap(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.getCount() == 2);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}