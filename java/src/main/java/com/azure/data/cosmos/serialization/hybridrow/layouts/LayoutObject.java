// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutObject extends LayoutPropertyScope {
    private TypeArgument TypeArg = new TypeArgument();

    public LayoutObject(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_OBJECT_SCOPE :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.OBJECT_SCOPE, immutable);
        this.TypeArg = new TypeArgument(this);
    }

    public String name() {
        return this.Immutable ? "im_object" : "object";
    }

    public TypeArgument typeArg() {
        return TypeArg;
    }


    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return writeScope(buffer, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        buffer.get().WriteSparseObject(edit, this, options, value.clone());
        return Result.SUCCESS;
    }
}