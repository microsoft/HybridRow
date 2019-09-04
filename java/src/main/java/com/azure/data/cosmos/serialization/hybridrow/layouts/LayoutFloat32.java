// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutFloat32 extends LayoutType<Float> {
    public LayoutFloat32() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.FLOAT_32, (Float.SIZE / Byte.SIZE));
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "float32";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<Float> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(0);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().ReadFloat32(scope.get().start() + column.getOffset()));
        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<Float> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(0);
            return result;
        }

        value.setAndGet(b.get().ReadSparseFloat32(edit));
        return Result.SUCCESS;
    }

    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             float value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().writeFloat32(scope.get().start() + col.getOffset(), value);
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, float value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, float value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().WriteSparseFloat32(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, float value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}