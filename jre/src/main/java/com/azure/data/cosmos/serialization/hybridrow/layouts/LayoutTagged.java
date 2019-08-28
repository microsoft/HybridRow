//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TAGGED_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TAGGED_SCOPE;

public final class LayoutTagged extends LayoutIndexedScope {
    public LayoutTagged(boolean immutable) {
        super(immutable ? IMMUTABLE_TAGGED_SCOPE : TAGGED_SCOPE, immutable, true, true, false, true);
    }

    public String name() {
        return this.Immutable ? "im_tagged_t" : "tagged_t";
    }

    public int countTypeArgument(TypeArgumentList value) {
        checkState(value.count() == 2);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(1).type().CountTypeArgument(value.get(1).typeArgs().clone());
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        checkArgument(edit.get().index() >= 0);
        checkArgument(edit.get().scopeTypeArgs().count() > edit.get().index());
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs().get(edit.get().index()).type().LayoutCode);
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        TypeArgument[] retval = new TypeArgument[2];
        retval[0] = new TypeArgument(UInt8, TypeArgumentList.EMPTY);
        retval[1] = readTypeArgument(row, offset, lenInBytes);
        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(Reference<RowCursor> edit) {
        edit.get().cellType = edit.get().scopeTypeArgs().get(edit.get().index()).type();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs().get(edit.get().index()).typeArgs().clone();
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

        b.get().WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkArgument(value.count() == 2);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(1).type().writeTypeArgument(row, offset + lenInBytes,
            value.get(1).typeArgs().clone());
        return lenInBytes;
    }
}