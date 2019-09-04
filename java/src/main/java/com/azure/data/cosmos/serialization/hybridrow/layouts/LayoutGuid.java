// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutGuid extends LayoutType<UUID> {
    public LayoutGuid() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.GUID, 16);
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "guid";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<UUID> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().ReadGuid(scope.get().start() + column.getOffset()));
        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<UUID> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().ReadSparseGuid(edit));
        return Result.SUCCESS;
    }

    @Override
    public Result writeFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                             UUID value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().WriteGuid(scope.get().start() + column.getOffset(), value);
        b.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, GuidCodec value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, UUID value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().WriteSparseGuid(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit,
                              UUID value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}