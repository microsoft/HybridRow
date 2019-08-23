//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutMongoDbObjectId extends LayoutType<MongoDbObjectId> {
    public LayoutMongoDbObjectId() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.MongoDbObjectId, azure.data.cosmos.serialization.hybridrow.MongoDbObjectId.Size);
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    // ReSharper disable once StringLiteralTypo
    @Override
    public String getName() {
        return "mongodbobjectid";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<MongoDbObjectId> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(null);
            return Result.NotFound;
        }

        value.set(b.get().ReadMongoDbObjectId(scope.get().start + col.getOffset()).clone());
        return Result.Success;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             OutObject<MongoDbObjectId> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        value.set(b.get().ReadSparseMongoDbObjectId(edit).clone());
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             MongoDbObjectId value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteMongoDbObjectId(scope.get().start + col.getOffset(), value.clone());
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, MongoDbObjectId value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                              MongoDbObjectId value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseMongoDbObjectId(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                              MongoDbObjectId value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}