//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutUtf8 extends LayoutType<String> implements ILayoutUtf8SpanWritable, ILayoutUtf8SpanReadable {
    public LayoutUtf8() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Utf8, 0);
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
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadFixed(b, scope, col, out span);
        value.argValue = (r == Result.Success) ? span.toString() :
        default
        return r;
    }

    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<Utf8Span> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = null;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadFixedString(scope.argValue.start + col.getOffset(), col.getSize());
        return Result.Success;
    }

    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadSparse(b, edit, out span);
        value.argValue = (r == Result.Success) ? span.toString() :
        default
        return r;
    }

    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, tangible.OutObject<Utf8Span> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        value.argValue = b.argValue.ReadSparseString(edit);
        return Result.Success;
    }

    @Override
    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col
        , tangible.OutObject<String> value) {
        Utf8Span span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.ReadVariable(b, scope, col, out span);
        value.argValue = (r == Result.Success) ? span.toString() :
        default
        return r;
    }

    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col
        , tangible.OutObject<Utf8Span> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = null;
            return Result.NotFound;
        }

        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        value.argValue = b.argValue.ReadVariableString(varOffset);
        return Result.Success;
    }

    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             String value) {
        checkArgument(value != null);
        return this.WriteFixed(b, scope, col, Utf8Span.TranscodeUtf16(value));
    }

    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             Utf8Span value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteFixedString(scope.argValue.start + col.getOffset(), value);
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, String value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, string value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, String value,
                              UpdateOptions options) {
        checkArgument(value != null);
        return this.WriteSparse(b, edit, Utf8Span.TranscodeUtf16(value), options);
    }


    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, Utf8Span value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions
    // options = UpdateOptions.Upsert)
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, Utf8Span value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseString(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, String value) {
        checkArgument(value != null);
        return this.WriteVariable(b, scope, col, Utf8Span.TranscodeUtf16(value));
    }

    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, Utf8Span value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        int length = value.Length;
        if ((col.getSize() > 0) && (length > col.getSize())) {
            return Result.TooBig;
        }

        boolean exists = b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone());
        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        int shift;
        tangible.OutObject<Integer> tempOut_shift = new tangible.OutObject<Integer>();
        b.argValue.WriteVariableString(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.argValue;
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        scope.argValue.metaOffset += shift;
        scope.argValue.valueOffset += shift;
        return Result.Success;
    }
}