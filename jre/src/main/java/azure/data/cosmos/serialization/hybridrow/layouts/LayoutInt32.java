//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutInt32 extends LayoutType<Integer> {
    public LayoutInt32() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Int32, (Integer.SIZE / Byte.SIZE));
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "int32";
    }

    @Override
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<Integer> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = 0;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadInt32(scope.argValue.start + col.getOffset());
        return Result.Success;
    }

    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<Integer> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = 0;
            return result;
        }

        value.argValue = b.argValue.ReadSparseInt32(edit);
        return Result.Success;
    }

    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             int value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteInt32(scope.argValue.start + col.getOffset(), value);
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, int value, UpdateOptions
    // options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, int value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseInt32(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, int value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}