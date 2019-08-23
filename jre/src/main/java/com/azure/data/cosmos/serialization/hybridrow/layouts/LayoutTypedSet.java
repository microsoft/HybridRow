//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedSetScope;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedSetScope;

public final class LayoutTypedSet extends LayoutUniqueScope {
    public LayoutTypedSet(boolean immutable) {
        super(immutable ? ImmutableTypedSetScope : TypedSetScope, immutable, true, true);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_set_t" : "set_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 1);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(0).getType().CountTypeArgument(value.get(0).getTypeArgs().clone());
    }

    @Override
    public TypeArgument FieldType(RefObject<RowCursor> scope) {
        return scope.get().scopeTypeArgs.get(0).clone();
    }

    @Override
    public boolean HasImplicitTypeCode(RefObject<RowCursor> edit) {
        checkState(edit.get().index >= 0);
        checkState(edit.get().scopeTypeArgs.getCount() == 1);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs.get(0).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(RefObject<RowBuffer> row, int offset,
                                                 OutObject<Integer> lenInBytes) {
        return new TypeArgumentList(new TypeArgument[] { ReadTypeArgument(row, offset, lenInBytes) });
    }

    @Override
    public void SetImplicitTypeCode(RefObject<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeTypeArgs.get(0).getType();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs.get(0).getTypeArgs().clone();
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

        b.get().WriteTypedSet(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        checkArgument(value.getCount() == 1);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).getType().WriteTypeArgument(row, offset + lenInBytes,
            value.get(0).getTypeArgs().clone());
        return lenInBytes;
    }
}