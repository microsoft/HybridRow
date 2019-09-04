// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutMongoDbObjectId extends LayoutType<MongoDbObjectId> {
    public LayoutMongoDbObjectId() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.MONGODB_OBJECT_ID, azure.data.cosmos.serialization.hybridrow.MongoDbObjectId.Size);
    }

    public boolean isFixed() {
        return true;
    }

    // ReSharper disable once StringLiteralTypo
    public String name() {
        return "mongodbobjectid";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<MongoDbObjectId> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().ReadMongoDbObjectId(scope.get().start() + column.getOffset()).clone());
        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<MongoDbObjectId> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().ReadSparseMongoDbObjectId(edit).clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                             MongoDbObjectId value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().WriteMongoDbObjectId(scope.get().start() + column.getOffset(), value.clone());
        b.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, MongoDbObjectId value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit,
                              MongoDbObjectId value, UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().WriteSparseMongoDbObjectId(edit, value.clone(), options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit,
                              MongoDbObjectId value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}