//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutMongoDbObjectId extends LayoutType<MongoDbObjectId> {
    public LayoutMongoDbObjectId() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.MongoDbObjectId, Microsoft.Azure.Cosmos.Serialization.HybridRow.MongoDbObjectId.Size);
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
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<MongoDbObjectId> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = null;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadMongoDbObjectId(scope.argValue.start + col.getOffset()).clone();
        return Result.Success;
    }

    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<MongoDbObjectId> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        value.argValue = b.argValue.ReadSparseMongoDbObjectId(edit).clone();
        return Result.Success;
    }

    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             MongoDbObjectId value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteMongoDbObjectId(scope.argValue.start + col.getOffset(), value.clone());
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, MongoDbObjectId value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              MongoDbObjectId value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseMongoDbObjectId(edit, value.clone(), options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              MongoDbObjectId value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}