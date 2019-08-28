//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTypedMap extends LayoutUniqueScope {
    public LayoutTypedMap(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_MAP_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_MAP_SCOPE, immutable, isSizedScope():
        true, isTypedScope():true)
    }

    public String name() {
        return this.Immutable ? "im_map_t" : "map_t";
    }

    public int countTypeArgument(TypeArgumentList value) {
        checkState(value.count() == 2);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().CountTypeArgument(arg.typeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public TypeArgument FieldType(Reference<RowCursor> scope) {
        return new TypeArgument(scope.get().scopeType().Immutable ? ImmutableTypedTuple :
            TypedTuple, scope.get().scopeTypeArgs().clone());
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        return true;
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        lenInBytes.setAndGet(0);
        TypeArgument[] retval = new TypeArgument[2];
        for (int i = 0; i < 2; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = readTypeArgument(row, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.setAndGet(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(Reference<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeType().Immutable ? ImmutableTypedTuple :
            TypedTuple;
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs().clone();
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
        Result result = prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteTypedMap(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.count() == 2);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().writeTypeArgument(row, offset + lenInBytes, arg.typeArgs().clone());
        }

        return lenInBytes;
    }
}