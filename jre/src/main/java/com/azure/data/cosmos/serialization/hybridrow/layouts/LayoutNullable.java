//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class LayoutNullable extends LayoutIndexedScope {
    public LayoutNullable(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_NULLABLE_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.NULLABLE_SCOPE, immutable, true, true, false, true);
    }

    public String name() {
        return this.Immutable ? "im_nullable" : "nullable";
    }

    public int countTypeArgument(TypeArgumentList value) {
        checkState(value.count() == 1);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(0).type().CountTypeArgument(value.get(0).typeArgs().clone());
    }

    @Override
    public boolean HasImplicitTypeCode(Reference<RowCursor> edit) {
        checkState(edit.get().index() >= 0);
        checkState(edit.get().scopeTypeArgs().count() == 1);
        checkState(edit.get().index() == 1);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.get().scopeTypeArgs().get(0).type().LayoutCode);
    }

    public static Result HasValue(Reference<RowBuffer> b, Reference<RowCursor> scope) {
        checkArgument(scope.get().scopeType() instanceof LayoutNullable);
        checkState(scope.get().index() == 1 || scope.get().index() == 2, "Nullable scopes always point at the value");
        checkState(scope.get().scopeTypeArgs().count() == 1);
        boolean hasValue = b.get().ReadInt8(scope.get().start()) != 0;
        return hasValue ? Result.Success : Result.NotFound;
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        return new TypeArgumentList(new TypeArgument[] { LayoutType.readTypeArgument(row, offset, lenInBytes) });
    }

    @Override
    public void SetImplicitTypeCode(Reference<RowCursor> edit) {
        checkState(edit.get().index() == 1);
        edit.get().cellType = edit.get().scopeTypeArgs().get(0).type();
        edit.get().cellTypeArgs = edit.get().scopeTypeArgs().get(0).typeArgs().clone();
    }

    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, boolean hasValue, Out<RowCursor> value) {
        return WriteScope(b, edit, typeArgs, hasValue, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList typeArgs, bool
    // hasValue, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, boolean hasValue, Out<RowCursor> value,
                             UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteNullable(edit, this, typeArgs.clone(), options, hasValue, value.clone());
        return Result.Success;
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
        return this.WriteScope(b, edit, typeArgs.clone(), true, value, options);
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.count() == 1);
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).type().writeTypeArgument(row, offset + lenInBytes,
            value.get(0).typeArgs().clone());
        return lenInBytes;
    }
}