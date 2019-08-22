//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutVarInt extends LayoutType<Long> {
    public LayoutVarInt() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.VarInt, 0);
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
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<Long> value) {
        Contract.Fail("Not Implemented");
        value.argValue = 0;
        return Result.Failure;
    }

    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, tangible.OutObject<Long> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = 0;
            return result;
        }

        value.argValue = b.argValue.ReadSparseVarInt(edit);
        return Result.Success;
    }

    @Override
    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col, tangible.OutObject<Long> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = 0;
            return Result.NotFound;
        }

        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        value.argValue = b.argValue.ReadVariableInt(varOffset);
        return Result.Success;
    }

    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             long value) {
        Contract.Fail("Not Implemented");
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, long value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, long value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseVarInt(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, long value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    @Override
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, long value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone());
        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        int shift;
        tangible.OutObject<Integer> tempOut_shift = new tangible.OutObject<Integer>();
        b.argValue.WriteVariableInt(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.argValue;
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        scope.argValue.metaOffset += shift;
        scope.argValue.valueOffset += shift;
        return Result.Success;
    }
}