// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
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
    public Result readFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<MongoDbObjectId> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NotFound;
        }

        value.setAndGet(b.get().ReadMongoDbObjectId(scope.get().start() + col.getOffset()).clone());
        return Result.Success;
    }

    @Override
    public Result readSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             Out<MongoDbObjectId> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().ReadSparseMongoDbObjectId(edit).clone());
        return Result.Success;
    }

    @Override
    public Result writeFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             MongoDbObjectId value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteMongoDbObjectId(scope.get().start() + col.getOffset(), value.clone());
        b.get().SetBit(scope.get().start(), col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, MongoDbObjectId value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              MongoDbObjectId value, UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseMongoDbObjectId(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result writeSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              MongoDbObjectId value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}