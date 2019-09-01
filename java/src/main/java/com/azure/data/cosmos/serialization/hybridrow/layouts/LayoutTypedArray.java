// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTypedArray extends LayoutIndexedScope {
    public LayoutTypedArray(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_ARRAY_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_ARRAY_SCOPE, immutable, true, false, false, true);
    }

    public String name() {
        return this.Immutable ? "im_array_t" : "array_t";
    }

    public int countTypeArgument(TypeArgumentList value) {
        checkState(value.count() == 1);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(0).type().CountTypeArgument(value.get(0).typeArgs().clone());
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        checkState(edit.get().index() >= 0);
        checkState(edit.get().scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs().get(0).type().LayoutCode);
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        return new TypeArgumentList(new TypeArgument[] { LayoutType.readTypeArgument(row, offset, lenInBytes) });
    }

    @Override
    public void setImplicitTypeCode(RowCursor edit) {
        edit.get().cellType = edit.get().scopeTypeArgs().get(0).type();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs().get(0).typeArgs().clone();
    }

    @Override
    public Result writeScope(RowBuffer b, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return writeScope(b, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer b, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        Result result = LayoutType.prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteTypedArray(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.count() == 1);
        row.get().writeSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).type().writeTypeArgument(row, offset + lenInBytes,
            value.get(0).typeArgs().clone());
        return lenInBytes;
    }
}