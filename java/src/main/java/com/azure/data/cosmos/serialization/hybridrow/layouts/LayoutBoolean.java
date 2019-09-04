// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutBoolean extends LayoutType<Boolean> {
    public LayoutBoolean(boolean value) {
        super(, 0);
    }

    public boolean isBoolean() {
        return true;
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "bool";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<Boolean> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(false);
            return Result.NotFound;
        }

        value.setAndGet(b.get().readBit(scope.get().start(), column.getBooleanBit().clone()));
        return Result.Success;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<Boolean> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(false);
            return result;
        }

        value.setAndGet(b.get().ReadSparseBool(edit));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             boolean value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        if (value) {
            b.get().setBit(scope.get().start(), col.getBooleanBit().clone());
        } else {
            b.get().unsetBit(scope.get().start(), col.getBooleanBit().clone());
        }

        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, bool value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, boolean value,
                              UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseBool(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, boolean value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}