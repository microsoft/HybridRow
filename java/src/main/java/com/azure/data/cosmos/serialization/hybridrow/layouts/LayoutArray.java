// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ARRAY_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_ARRAY_SCOPE;

public final class LayoutArray extends LayoutIndexedScope {

    public LayoutArray(final boolean immutable) {
        super(immutable ? IMMUTABLE_ARRAY_SCOPE : ARRAY_SCOPE, immutable, false, false, false, false);
    }

    public String name() {
        return this.isImmutable() ? "im_array" : "array";
    }

    @Override
    public Result writeScope(
        @Nonnull final RowBuffer b,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull Out<RowCursor> value
    ) {
        return this.writeScope(b, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer b, RowCursor edit,
                             TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {
        Result result = prepareSparseWrite(b, edit, this.typeArg(), options);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        b.writeSparseArray(edit, this, options, value);
        return Result.SUCCESS;
    }
}