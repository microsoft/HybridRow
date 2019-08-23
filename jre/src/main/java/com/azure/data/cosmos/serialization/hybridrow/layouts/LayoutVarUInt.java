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

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public sealed class LayoutVarUInt : LayoutType<ulong>
public final class LayoutVarUInt extends LayoutType<Long> {
    public LayoutVarUInt() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.VarUInt, 0);
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
        return "varuint";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ulong value)
    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<Long> value) {
        Contract.Fail("Not Implemented");
        value.set(0);
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ulong value)
    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, OutObject<Long> value) {
        Result result = PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(0);
            return result;
        }

        value.set(b.get().ReadSparseVarUInt(edit));
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ulong value)
    @Override
    public Result ReadVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col, OutObject<Long> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(0);
            return Result.NotFound;
        }

        int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
            col.getOffset());
        value.set(b.get().ReadVariableUInt(varOffset));
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong
    // value)
    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             long value) {
        Contract.Fail("Not Implemented");
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ulong value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, long value,
                              UpdateOptions options) {
        Result result = PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseVarUInt(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, long value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ulong value)
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
        b.get().WriteVariableUInt(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        scope.get().metaOffset += shift;
        scope.get().valueOffset += shift;
        return Result.Success;
    }
}