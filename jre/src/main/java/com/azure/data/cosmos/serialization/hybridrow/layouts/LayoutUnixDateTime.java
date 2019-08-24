//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutUnixDateTime extends LayoutType<com.azure.data.cosmos.serialization.hybridrow.UnixDateTime> {
    public LayoutUnixDateTime() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.UnixDateTime,
            com.azure.data.cosmos.serialization.hybridrow.UnixDateTime.Size);
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "unixdatetime";
    }

    @Override
    public Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<UnixDateTime> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(null);
            return Result.NotFound;
        }

        value.set(b.get().ReadUnixDateTime(scope.get().start + col.getOffset()).clone());
        return Result.Success;
    }

    @Override
    public Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             Out<UnixDateTime> value) {
        Result result = PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        value.set(b.get().ReadSparseUnixDateTime(edit).clone());
        return Result.Success;
    }

    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             UnixDateTime value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteUnixDateTime(scope.get().start + col.getOffset(), value.clone());
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, UnixDateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, UnixDateTime value
        , UpdateOptions options) {
        Result result = PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseUnixDateTime(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, UnixDateTime value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}