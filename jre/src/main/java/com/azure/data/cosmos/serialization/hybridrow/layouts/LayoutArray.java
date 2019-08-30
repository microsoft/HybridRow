// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ARRAY_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_ARRAY_SCOPE;

public final class LayoutArray extends LayoutIndexedScope {
    private TypeArgument TypeArg = new TypeArgument();

    public LayoutArray(boolean immutable) {
        super(immutable ? IMMUTABLE_ARRAY_SCOPE : ARRAY_SCOPE, immutable, false, false, false, false);
        this.TypeArg = new TypeArgument(this);
    }

    public String name() {
        return this.Immutable ? "im_array" : "array";
    }

    public TypeArgument typeArg() {
        return TypeArg;
    }

    @Override
    public Result WriteScope(
        Reference<RowBuffer> b,
        Reference<RowCursor> edit,
        TypeArgumentList typeArgs,
        Out<RowCursor> value
    ) {
        return WriteScope(b, edit, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value, UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteSparseArray(edit, this, options, value.clone());
        return Result.Success;
    }
}