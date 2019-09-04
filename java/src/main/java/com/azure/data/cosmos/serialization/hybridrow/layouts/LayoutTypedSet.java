// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TYPED_SET_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TYPED_SET_SCOPE;

public final class LayoutTypedSet extends LayoutUniqueScope {
    public LayoutTypedSet(boolean immutable) {
        super(immutable ? IMMUTABLE_TYPED_SET_SCOPE : TYPED_SET_SCOPE, immutable, true, true);
    }

    public String name() {
        return this.Immutable ? "im_set_t" : "set_t";
    }

    public int countTypeArgument(TypeArgumentList value) {
        checkState(value.count() == 1);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(0).type().CountTypeArgument(value.get(0).typeArgs().clone());
    }

    @Override
    public TypeArgument FieldType(Reference<RowCursor> scope) {
        return scope.get().scopeTypeArgs().get(0).clone();
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
        return new TypeArgumentList(new TypeArgument[] { readTypeArgument(row, offset, lenInBytes) });
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
        Result result = prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteTypedSet(edit, this, typeArgs.clone(), options, value.clone());
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkArgument(value.count() == 1);
        row.get().writeSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).type().writeTypeArgument(row, offset + lenInBytes,
            value.get(0).typeArgs().clone());
        return lenInBytes;
    }
}