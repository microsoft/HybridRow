//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutInt16 extends LayoutType<Short> {
    public LayoutInt16() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Int16, (Short.SIZE / Byte.SIZE));
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "int16";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<Short> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(0);
            return Result.NotFound;
        }

        value.set(b.get().ReadInt16(scope.get().start + col.getOffset()));
        return Result.Success;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             OutObject<Short> value) {
        Result result = PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(0);
            return result;
        }

        value.set(b.get().ReadSparseInt16(edit));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             short value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteInt16(scope.get().start + col.getOffset(), value);
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, short value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, short value,
                              UpdateOptions options) {
        Result result = PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseInt16(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, short value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}