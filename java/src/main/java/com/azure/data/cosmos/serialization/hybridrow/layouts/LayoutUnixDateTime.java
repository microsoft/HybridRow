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
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                            Out<UnixDateTime> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!buffer.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(buffer.get().ReadUnixDateTime(scope.get().start() + column.getOffset()).clone());
        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer buffer, RowCursor edit,
                             Out<UnixDateTime> value) {
        Result result = prepareSparseRead(buffer, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(buffer.get().ReadSparseUnixDateTime(edit).clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                             UnixDateTime value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.get().WriteUnixDateTime(scope.get().start() + column.getOffset(), value.clone());
        buffer.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, UnixDateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, UnixDateTime value
        , UpdateOptions options) {
        Result result = prepareSparseWrite(buffer, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.get().WriteSparseUnixDateTime(edit, value.clone(), options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, UnixDateTime value) {
        return writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }
}