// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.UnixDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutUnixDateTime extends LayoutType<com.azure.data.cosmos.serialization.hybridrow.UnixDateTime> {
    public LayoutUnixDateTime() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.UNIX_DATE_TIME,
            com.azure.data.cosmos.serialization.hybridrow.UnixDateTime.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "unixdatetime";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<UnixDateTime> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NotFound;
        }

        value.setAndGet(b.get().ReadUnixDateTime(scope.get().start() + column.getOffset()).clone());
        return Result.Success;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<UnixDateTime> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().ReadSparseUnixDateTime(edit).clone());
        return Result.Success;
    }

    @Override
    public Result writeFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                             UnixDateTime value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteUnixDateTime(scope.get().start() + column.getOffset(), value.clone());
        b.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, UnixDateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, UnixDateTime value
        , UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseUnixDateTime(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, UnixDateTime value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}