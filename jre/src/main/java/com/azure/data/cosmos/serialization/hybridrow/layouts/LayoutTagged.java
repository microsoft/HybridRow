//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTaggedScope;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TaggedScope;

public final class LayoutTagged extends LayoutIndexedScope {
    public LayoutTagged(boolean immutable) {
        super(immutable ? ImmutableTaggedScope : TaggedScope, immutable, true, true, false, true);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tagged_t" : "tagged_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 2);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(1).getType().CountTypeArgument(value.get(1).getTypeArgs().clone());
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
        TypeArgument[] retval = new TypeArgument[2];
        retval[0] = new TypeArgument(UInt8, TypeArgumentList.Empty);
        retval[1] = ReadTypeArgument(row, offset, lenInBytes);
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
        Result result = PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        b.get().WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        checkArgument(value.getCount() == 2);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(1).getType().WriteTypeArgument(row, offset + lenInBytes,
            value.get(1).getTypeArgs().clone());
        return lenInBytes;
    }
}