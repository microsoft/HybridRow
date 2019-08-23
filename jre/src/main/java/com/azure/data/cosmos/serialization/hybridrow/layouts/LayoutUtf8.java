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

public final class LayoutUtf8 extends LayoutType<String> implements ILayoutUtf8SpanWritable, ILayoutUtf8SpanReadable {
    public LayoutUtf8() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Utf8, 0);
    }

    @Override
    public boolean getIsFixed() {
        return false;
    }

    @Override
    public String getName() {
        return "utf8";
    }

    @Override
    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadFixed(b, scope, col, out span);
        value.set((r == Result.Success) ? span.toString() :)
        default
        return r;
    }

    public Result ReadFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                            OutObject<Utf8Span> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(null);
            return Result.NotFound;
        }

        value.set(b.get().ReadFixedString(scope.get().start + col.getOffset(), col.getSize()));
        return Result.Success;
    }

    @Override
    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                             OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadSparse(b, edit, out span);
        value.set((r == Result.Success) ? span.toString() :)
        default
        return r;
    }

    public Result ReadSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, OutObject<Utf8Span> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        value.set(b.get().ReadSparseString(edit));
        return Result.Success;
    }

    @Override
    public Result ReadVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col
        , OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadVariable(b, scope, col, out span);
        value.set((r == Result.Success) ? span.toString() :)
        default
        return r;
    }

    public Result ReadVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col
        , OutObject<Utf8Span> value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            value.set(null);
            return Result.NotFound;
        }

        int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
            col.getOffset());
        value.set(b.get().ReadVariableString(varOffset));
        return Result.Success;
    }

    @Override
    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             String value) {
        checkArgument(value != null);
        return this.WriteFixed(b, scope, col, Utf8Span.TranscodeUtf16(value));
    }

    public Result WriteFixed(RefObject<RowBuffer> b, RefObject<RowCursor> scope, LayoutColumn col,
                             Utf8Span value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        b.get().WriteFixedString(scope.get().start + col.getOffset(), value);
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, String value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, string value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, String value,
                              UpdateOptions options) {
        checkArgument(value != null);
        return this.WriteSparse(b, edit, Utf8Span.TranscodeUtf16(value), options);
    }


    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, Utf8Span value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions
    // options = UpdateOptions.Upsert)
    public Result WriteSparse(RefObject<RowBuffer> b, RefObject<RowCursor> edit, Utf8Span value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.get().WriteSparseString(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                LayoutColumn col, String value) {
        checkArgument(value != null);
        return this.WriteVariable(b, scope, col, Utf8Span.TranscodeUtf16(value));
    }

    public Result WriteVariable(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                LayoutColumn col, Utf8Span value) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        int length = value.Length;
        if ((col.getSize() > 0) && (length > col.getSize())) {
            return Result.TooBig;
        }

        boolean exists = b.get().ReadBit(scope.get().start, col.getNullBit().clone());
        int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
            col.getOffset());
        int shift;
        OutObject<Integer> tempOut_shift = new OutObject<Integer>();
        b.get().WriteVariableString(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().SetBit(scope.get().start, col.getNullBit().clone());
        scope.get().metaOffset += shift;
        scope.get().valueOffset += shift;
        return Result.Success;
    }
}