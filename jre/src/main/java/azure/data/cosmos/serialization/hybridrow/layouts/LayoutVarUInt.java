//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public sealed class LayoutVarUInt : LayoutType<ulong>
public final class LayoutVarUInt extends LayoutType<Long> {
    public LayoutVarUInt() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.VarUInt, 0);
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
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<Long> value) {
        Contract.Fail("Not Implemented");
        value.argValue = 0;
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ulong value)
    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, tangible.OutObject<Long> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = 0;
            return result;
        }

        value.argValue = b.argValue.ReadSparseVarUInt(edit);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ulong value)
    @Override
    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col, tangible.OutObject<Long> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = 0;
            return Result.NotFound;
        }

        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        value.argValue = b.argValue.ReadVariableUInt(varOffset);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong
    // value)
    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             long value) {
        Contract.Fail("Not Implemented");
        return Result.Failure;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ulong value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, long value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseVarUInt(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, long value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ulong value)
    @Override
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, long value) {
        Contract.Requires(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone());
        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        int shift;
        tangible.OutObject<Integer> tempOut_shift = new tangible.OutObject<Integer>();
        b.argValue.WriteVariableUInt(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.argValue;
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        scope.argValue.metaOffset += shift;
        scope.argValue.valueOffset += shift;
        return Result.Success;
    }
}