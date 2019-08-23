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

public final class LayoutVarInt extends LayoutType<Long> {
    public LayoutVarInt() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.VarInt, 0);
    }

    @Override
    public boolean getIsFixed() {
        return false;
    }

    @Override
    public boolean getIsVarint() {
        return true;
    }

    @Override
    public String getName() {
        return "varint";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<Long> value) {
        Contract.Fail("Not Implemented");
        value.set(0);
        return Result.Failure;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, OutObject<Long> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(0);
            return result;
        }

        value.set(b.get().ReadSparseVarInt(edit));
        return Result.Success;
    }

    @Override
    public Result ReadVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col, OutObject<Long> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(0);
            return Result.NotFound;
        }

        int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
            col.getOffset());
        value.set(b.get().ReadVariableInt(varOffset));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             long value) {
        Contract.Fail("Not Implemented");
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, long value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, long value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseVarInt(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, long value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    @Override
    public Result WriteVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                LayoutColumn col, long value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.get().ReadBit(scope.get().start, col.getNullBit().clone());
        int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
            col.getOffset());
        int shift;
        OutObject<Integer> tempOut_shift = new OutObject<Integer>();
        b.get().WriteVariableInt(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        scope.get().metaOffset += shift;
        scope.get().valueOffset += shift;
        return Result.Success;
    }
}