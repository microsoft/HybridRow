//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public sealed class LayoutBinary : LayoutType<byte[]>, ILayoutSpanWritable<byte>,
// ILayoutSpanReadable<byte>, ILayoutSequenceWritable<byte>
public final class LayoutBinary extends LayoutType<byte[]> implements ILayoutSpanWritable<Byte>,
    ILayoutSpanReadable<Byte>, ILayoutSequenceWritable<Byte> {
    public LayoutBinary() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Binary, 0);
    }

    @Override
    public boolean getIsFixed() {
        return false;
    }

    @Override
    public String getName() {
        return "binary";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // byte[] value)
    @Override
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadFixed(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result r = this.ReadFixed(b, scope, col, out span);
        value.argValue = (r == Result.Success) ? span.ToArray() :
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ReadOnlySpan<byte> value)
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<ReadOnlySpan<Byte>> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = null;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadFixedBinary(scope.argValue.start + col.getOffset(), col.getSize());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out byte[] value)
    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadSparse(ref b, ref edit, out ReadOnlySpan<byte> span);
        Result r = this.ReadSparse(b, edit, out span);
        value.argValue = (r == Result.Success) ? span.ToArray() :
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ReadOnlySpan<byte> value)
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, tangible.OutObject<ReadOnlySpan<Byte>> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        value.argValue = b.argValue.ReadSparseBinary(edit);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // byte[] value)
    @Override
    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col
        , tangible.OutObject<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadVariable(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result r = this.ReadVariable(b, scope, col, out span);
        value.argValue = (r == Result.Success) ? span.ToArray() :
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ReadOnlySpan<byte> value)
    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col
        , tangible.OutObject<ReadOnlySpan<Byte>> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = null;
            return Result.NotFound;
        }

        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        value.argValue = b.argValue.ReadVariableBinary(varOffset);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte[]
    // value)
    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             byte[] value) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteFixed(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        return this.WriteFixed(b, scope, col, new ReadOnlySpan<Byte>(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             ReadOnlySpan<Byte> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteFixedBinary(scope.argValue.start + col.getOffset(), value, col.getSize());
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySequence<byte> value)
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             ReadOnlySequence<Byte> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteFixedBinary(scope.argValue.start + col.getOffset(), value, col.getSize());
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, byte[] value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, byte[] value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, byte[] value,
                              UpdateOptions options) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteSparse(ref b, ref edit, new ReadOnlySpan<byte>(value), options);
        return this.WriteSparse(b, edit, new ReadOnlySpan<Byte>(value), options);
    }

    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              ReadOnlySpan<Byte> value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySpan<byte> value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              ReadOnlySpan<Byte> value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseBinary(edit, value, options);
        return Result.Success;
    }

    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              ReadOnlySequence<Byte> value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySequence<byte> value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                              ReadOnlySequence<Byte> value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseBinary(edit, value, options);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // byte[] value)
    @Override
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, byte[] value) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteVariable(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        return this.WriteVariable(b, scope, col, new ReadOnlySpan<Byte>(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, ReadOnlySpan<Byte> value) {
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
        b.argValue.WriteVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.argValue;
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        scope.argValue.metaOffset += shift;
        scope.argValue.valueOffset += shift;
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySequence<byte> value)
    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, ReadOnlySequence<Byte> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        int length = (int)value.Length;
        if ((col.getSize() > 0) && (length > col.getSize())) {
            return Result.TooBig;
        }

        boolean exists = b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone());
        int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
            col.getOffset());
        int shift;
        tangible.OutObject<Integer> tempOut_shift = new tangible.OutObject<Integer>();
        b.argValue.WriteVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.argValue;
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        scope.argValue.metaOffset += shift;
        scope.argValue.valueOffset += shift;
        return Result.Success;
    }
}