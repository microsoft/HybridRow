// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.NullValue;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutNull extends LayoutType<NullValue> {
    public LayoutNull() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.NULL, 0);
    }

    public boolean isFixed() {
        return true;
    }

    public boolean isNull() {
        return true;
    }

    public String name() {
        return "null";
    }

    @Override
    public Result readFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<NullValue> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        value.setAndGet(NullValue.Default);
        if (!b.get().ReadBit(scope.get().start(), col.getNullBit().clone())) {
            return Result.NotFound;
        }

        return Result.Success;
    }

    @Override
    public Result readSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             Out<NullValue> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().readSparseNull(edit).clone());
        return Result.Success;
    }

    @Override
    public Result writeFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             NullValue value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        b.get().SetBit(scope.get().start(), col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, NullValue value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, NullValue value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseNull(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, NullValue value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}