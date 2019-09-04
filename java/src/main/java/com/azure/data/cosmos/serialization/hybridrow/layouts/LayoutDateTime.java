// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutType<DateTime> {
    public LayoutDateTime() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.DATE_TIME, 8);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "datetime";
    }

    @Override
    public Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<LocalDateTime> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(LocalDateTime.MIN);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().readDateTime(scope.get().start() + col.getOffset()));
        return Result.SUCCESS;
    }

    @Override
    public Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             Out<LocalDateTime> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(LocalDateTime.MIN);
            return result;
        }

        value.setAndGet(b.get().readSparseDateTime(edit));
        return Result.SUCCESS;
    }

    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             LocalDateTime value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().writeDateTime(scope.get().start() + col.getOffset(), value);
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, DateTime value,
    // UpdateOptions options = UpdateOptions.Upsert)
    public Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              LocalDateTime value, UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().WriteSparseDateTime(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, DateTime value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}