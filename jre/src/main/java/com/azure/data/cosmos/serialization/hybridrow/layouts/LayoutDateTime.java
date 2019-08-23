//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutType<DateTime> {
    public LayoutDateTime() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.DateTime, 8);
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "datetime";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<LocalDateTime> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(LocalDateTime.MIN);
            return Result.NotFound;
        }

        value.set(b.get().ReadDateTime(scope.get().start + col.getOffset()));
        return Result.Success;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             OutObject<LocalDateTime> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(LocalDateTime.MIN);
            return result;
        }

        value.set(b.get().ReadSparseDateTime(edit));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             LocalDateTime value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteDateTime(scope.get().start + col.getOffset(), value);
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, DateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                              LocalDateTime value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseDateTime(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, DateTime value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}