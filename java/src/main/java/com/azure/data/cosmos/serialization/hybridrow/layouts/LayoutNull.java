// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
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
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                            Out<NullValue> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        value.setAndGet(NullValue.Default);
        if (!buffer.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            return Result.NOT_FOUND;
        }

        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer buffer, RowCursor edit,
                             Out<NullValue> value) {
        Result result = prepareSparseRead(buffer, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(buffer.get().readSparseNull(edit).clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                             NullValue value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, NullValue value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, NullValue value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(buffer, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.get().WriteSparseNull(edit, value.clone(), options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, NullValue value) {
        return writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }
}