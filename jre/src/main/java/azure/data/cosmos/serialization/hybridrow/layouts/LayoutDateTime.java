//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutType<DateTime> {
    public LayoutDateTime() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.DateTime, 8);
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
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<LocalDateTime> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = LocalDateTime.MIN;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadDateTime(scope.argValue.start + col.getOffset());
        return Result.Success;
    }

    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<LocalDateTime> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = LocalDateTime.MIN;
            return result;
        }

        value.argValue = b.argValue.ReadSparseDateTime(edit);
        return Result.Success;
    }

    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             LocalDateTime value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteDateTime(scope.argValue.start + col.getOffset(), value);
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, DateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              LocalDateTime value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseDateTime(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, DateTime value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}