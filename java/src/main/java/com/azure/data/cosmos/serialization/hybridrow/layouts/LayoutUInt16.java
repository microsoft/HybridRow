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
//ORIGINAL LINE: public sealed class LayoutUInt16 : LayoutType<ushort>
public final class LayoutUInt16 extends LayoutType<Short> {
    public LayoutUInt16() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.UINT_16, (Short.SIZE / Byte.SIZE));
    }

    public boolean isFixed() {
        return true;
    }

    public String name() {
        return "uint16";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ushort value)
    @Override
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                            Out<Short> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!buffer.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(0);
            return Result.NOT_FOUND;
        }

        value.setAndGet(buffer.get().ReadUInt16(scope.get().start() + column.getOffset()));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ushort value)
    @Override
    public Result readSparse(RowBuffer buffer, RowCursor edit,
                             Out<Short> value) {
        Result result = prepareSparseRead(buffer, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(0);
            return result;
        }

        value.setAndGet(buffer.get().ReadSparseUInt16(edit));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ushort
    // value)
    @Override
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             short value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().writeUInt16(scope.get().start() + col.getOffset(), value);
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ushort value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, short value,
                              UpdateOptions options) {
        Result result = prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().writeSparseUInt16(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, short value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}