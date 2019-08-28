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

public final class LayoutInt16 extends LayoutType<Short> {
    public LayoutInt16() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.INT_16, (Short.SIZE / Byte.SIZE));
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "int16";
    }

    @Override
    public Result readFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<Short> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(0);
            return Result.NotFound;
        }

        value.setAndGet(b.get().ReadInt16(scope.get().start() + col.getOffset()));
        return Result.Success;
    }

    @Override
    public Result readSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             Out<Short> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(0);
            return result;
        }

        value.setAndGet(b.get().ReadSparseInt16(edit));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             short value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteInt16(scope.get().start() + col.getOffset(), value);
        b.get().SetBit(scope.get().start(), col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, short value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, short value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseInt16(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, short value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}