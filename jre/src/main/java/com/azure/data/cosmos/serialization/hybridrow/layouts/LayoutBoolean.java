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

public final class LayoutBoolean extends LayoutType<Boolean> {
    public LayoutBoolean(boolean value) {
        super(, 0);
    }

    @Override
    public boolean getIsBool() {
        return true;
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "bool";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<Boolean> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(false);
            return Result.NotFound;
        }

        value.set(b.get().ReadBit(scope.get().start, col.getBoolBit().clone()));
        return Result.Success;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             OutObject<Boolean> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(false);
            return result;
        }

        value.set(b.get().ReadSparseBool(edit));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             boolean value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        if (value) {
            b.get().SetBit(scope.get().start, col.getBoolBit().clone());
        } else {
            b.get().UnsetBit(scope.get().start, col.getBoolBit().clone());
        }

        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, bool value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, boolean value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseBool(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, boolean value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}