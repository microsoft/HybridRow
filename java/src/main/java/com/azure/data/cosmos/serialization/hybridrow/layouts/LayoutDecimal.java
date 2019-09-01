// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutDecimal extends LayoutType<BigDecimal> {
    public LayoutDecimal() {
        // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'sizeof':
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.DECIMAL, sizeof(BigDecimal));
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "decimal";
    }

    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<BigDecimal> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(new BigDecimal(0));
            return Result.NotFound;
        }

        value.setAndGet(b.get().ReadDecimal(scope.get().start() + column.getOffset()));
        return Result.Success;
    }

    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<BigDecimal> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.setAndGet(new BigDecimal(0));
            return result;
        }

        value.setAndGet(b.get().ReadSparseDecimal(edit));
        return Result.Success;
    }

    @Override
    public Result writeFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                             BigDecimal value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteDecimal(scope.get().start() + column.getOffset(), value);
        b.get().SetBit(scope.get().start(), column.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, decimal value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, BigDecimal value,
                              UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseDecimal(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit,
                              BigDecimal value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }
}