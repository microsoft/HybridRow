// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutFloat64 extends LayoutTypePrimitive<Double> {

    public LayoutFloat64() {
        super(LayoutCode.FLOAT_64, Double.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "float64";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<Double> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0D);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFloat64(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Out<Double> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0D);
            return result;
        }

        value.set(buffer.readSparseFloat64(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn col, @Nonnull Double value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFloat64(scope.start() + col.offset(), value);
        buffer.setBit(scope.start(), col.nullBit());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, double value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Double value, @Nonnull UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseFloat64(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull Double value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}