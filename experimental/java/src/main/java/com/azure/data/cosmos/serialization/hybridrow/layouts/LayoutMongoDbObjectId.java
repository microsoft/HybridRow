// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import javax.annotation.Nonnull;

public final class LayoutMongoDbObjectId extends LayoutType/*<MongoDbObjectId>*/ {

    public LayoutMongoDbObjectId() {
        super(LayoutCode.MONGODB_OBJECT_ID, 0/*MongoDbObjectId.Size*/);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "mongodbobjectid";
    }

    // TODO: DANOBLE: Ressurect this class implementation
//    @Override
//    @Nonnull
//    public Result readFixed(
//        RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<MongoDbObjectId> value) {
//        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
//        if (!buffer.get().readBit(scope.get().start(), column.getNullBit().clone())) {
//            value.setAndGet(null);
//            return Result.NOT_FOUND;
//        }
//
//        value.setAndGet(buffer.get().ReadMongoDbObjectId(scope.get().start() + column.getOffset()).clone());
//        return Result.SUCCESS;
//    }
//
//    @Override
//    @Nonnull
//    public Result readSparse(RowBuffer buffer, RowCursor edit,
//                             Out<MongoDbObjectId> value) {
//        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());
//        if (result != Result.SUCCESS) {
//            value.setAndGet(null);
//            return result;
//        }
//
//        value.setAndGet(buffer.get().ReadSparseMongoDbObjectId(edit).clone());
//        return Result.SUCCESS;
//    }
//
//    @Override
//    @Nonnull
//    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
//                             MongoDbObjectId value) {
//        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
//        if (scope.get().immutable()) {
//            return Result.INSUFFICIENT_PERMISSIONS;
//        }
//
//        buffer.get().WriteMongoDbObjectId(scope.get().start() + column.getOffset(), value.clone());
//        buffer.get().SetBit(scope.get().start(), column.getNullBit().clone());
//        return Result.SUCCESS;
//    }
//
//    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, MongoDbObjectId value,
//    // UpdateOptions options = UpdateOptions.Upsert)
//    @Override
//    @Nonnull
//    public Result writeSparse(RowBuffer buffer, RowCursor edit,
//                              MongoDbObjectId value, UpdateOptions options) {
//        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg().clone(), options);
//        if (result != Result.SUCCESS) {
//            return result;
//        }
//
//        buffer.get().WriteSparseMongoDbObjectId(edit, value.clone(), options);
//        return Result.SUCCESS;
//    }
//
//    @Override
//    @Nonnull
//    public Result writeSparse(RowBuffer buffer, RowCursor edit,
//                              MongoDbObjectId value) {
//        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
//    }
}