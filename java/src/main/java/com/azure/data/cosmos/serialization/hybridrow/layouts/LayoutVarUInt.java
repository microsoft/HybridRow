// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public sealed class LayoutVarUInt : LayoutType<ulong>
public final class LayoutVarUInt extends LayoutType<Long> {
    public LayoutVarUInt() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.VAR_UINT, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public boolean isVarint() {
        return true;
    }

    public String name() {
        return "varuint";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ulong value)
    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<Long> value) {
        Contract.Fail("Not Implemented");
        value.setAndGet(0);
        return Result.FAILURE;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ulong value)
    @Override
    public Result readSparse(RowBuffer b, RowCursor edit, Out<Long> value) {
        Result result = prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(0);
            return result;
        }

        value.setAndGet(b.get().readSparseVarUInt(edit));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ulong value)
    @Override
    public Result readVariable(RowBuffer b, RowCursor scope, LayoutColumn column, Out<Long> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(0);
            return Result.NOT_FOUND;
        }

        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            column.getOffset());
        value.setAndGet(b.get().ReadVariableUInt(varOffset));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong
    // value)
    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             long value) {
        Contract.Fail("Not Implemented");
        return Result.FAILURE;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ulong value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, long value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().writeSparseVarUInt(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, long value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ulong value)
    @Override
    public Result WriteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                LayoutColumn col, long value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        boolean exists = b.get().readBit(scope.get().start(), col.getNullBit().clone());
        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            col.getOffset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        b.get().writeVariableUInt(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        scope.get().metaOffset(scope.get().metaOffset() + shift);
        scope.get().valueOffset(scope.get().valueOffset() + shift);
        return Result.SUCCESS;
    }
}