// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutUtf8 extends LayoutType<String> implements ILayoutUtf8SpanWritable, ILayoutUtf8SpanReadable {
    public LayoutUtf8() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.UTF_8, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public String name() {
        return "utf8";
    }

    @Override
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.ReadFixed(buffer, scope, column, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.toString() :)
        default
        return r;
    }

    public Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn column,
                            Out<Utf8Span> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        checkArgument(column.getSize() >= 0);
        if (!b.get().readBit(scope.get().start(), column.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().readFixedString(scope.get().start() + column.getOffset(), column.getSize()));
        return Result.SUCCESS;
    }

    @Override
    public Result readSparse(RowBuffer buffer, RowCursor edit,
                             Out<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.ReadSparse(buffer, edit, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.toString() :)
        default
        return r;
    }

    public Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Out<Utf8Span> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().readSparseString(edit));
        return Result.SUCCESS;
    }

    @Override
    public Result readVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column
        , Out<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.ReadVariable(buffer, scope, column, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.toString() :)
        default
        return r;
    }

    public Result ReadVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col
        , Out<Utf8Span> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            col.getOffset());
        value.setAndGet(b.get().readVariableString(varOffset));
        return Result.SUCCESS;
    }

    @Override
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                             String value) {
        checkArgument(value != null);
        return this.writeFixed(buffer, scope, column, Utf8Span.TranscodeUtf16(value));
    }

    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column,
                             Utf8String value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        checkArgument(column.getSize() >= 0);
        checkArgument(value.Length == column.getSize());
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.get().writeFixedString(scope.get().start() + column.getOffset(), value);
        buffer.get().setBit(scope.get().start(), column.getNullBit().clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, String value) {
        return writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, string value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeSparse(RowBuffer buffer, RowCursor edit, String value,
                              UpdateOptions options) {
        checkArgument(value != null);
        return this.writeSparse(buffer, edit, Utf8Span.TranscodeUtf16(value), options);
    }


    public Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value) {
        return writeSparse(buffer, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions
    // options = UpdateOptions.Upsert)
    public Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value,
                              UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.get().writeSparseString(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    public Result writeVariable(RowBuffer buffer, RowCursor scope,
                                LayoutColumn column, String value) {
        checkArgument(value != null);
        return this.writeVariable(buffer, scope, column, Utf8Span.TranscodeUtf16(value));
    }

    public Result writeVariable(RowBuffer buffer, RowCursor scope,
                                LayoutColumn column, Utf8String value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = value.Length;
        if ((column.getSize() > 0) && (length > column.getSize())) {
            return Result.TOO_BIG;
        }

        boolean exists = buffer.get().readBit(scope.get().start(), column.getNullBit().clone());
        int varOffset = buffer.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            column.getOffset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        buffer.get().writeVariableString(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        buffer.get().setBit(scope.get().start(), column.getNullBit().clone());
        scope.get().metaOffset(scope.get().metaOffset() + shift);
        scope.get().valueOffset(scope.get().valueOffset() + shift);
        return Result.SUCCESS;
    }
}