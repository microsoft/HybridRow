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
//ORIGINAL LINE: public sealed class LayoutBinary : LayoutType<byte[]>, ILayoutSpanWritable<byte>,
// ILayoutSpanReadable<byte>, ILayoutSequenceWritable<byte>
public final class LayoutBinary extends LayoutType<byte[]> implements ILayoutSpanWritable<Byte>,
    ILayoutSpanReadable<Byte>, ILayoutSequenceWritable<Byte> {
    public LayoutBinary() {
        super(com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.BINARY, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public String name() {
        return "binary";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // byte[] value)
    @Override
    public Result readFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                            Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadFixed(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result r = this.ReadFixed(b, scope, column, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.ToArray() :)
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ReadOnlySpan<byte> value)
    public Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                            Out<ReadOnlySpan<Byte>> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        if (!b.get().readBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(b.get().readFixedBinary(scope.get().start() + col.getOffset(), col.getSize()));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out byte[] value)
    @Override
    public Result readSparse(RowBuffer b, RowCursor edit,
                             Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadSparse(ref b, ref edit, out ReadOnlySpan<byte> span);
        Result r = this.ReadSparse(b, edit, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.ToArray() :)
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ReadOnlySpan<byte> value)
    public Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Out<ReadOnlySpan<Byte>> value) {
        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().readSparseBinary(edit));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // byte[] value)
    @Override
    public Result readVariable(RowBuffer b, RowCursor scope, LayoutColumn column
        , Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadVariable(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result r = this.ReadVariable(b, scope, column, out span);
        value.setAndGet((r == Result.SUCCESS) ? span.ToArray() :)
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ReadOnlySpan<byte> value)
    public Result ReadVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col
        , Out<ReadOnlySpan<Byte>> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (!b.get().readBit(scope.get().start(), col.getNullBit().clone())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            col.getOffset());
        value.setAndGet(b.get().readVariableBinary(varOffset));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte[]
    // value)
    @Override
    public Result writeFixed(RowBuffer b, RowCursor scope, LayoutColumn column,
                             byte[] value) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteFixed(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        return this.WriteFixed(b, scope, column, new ReadOnlySpan<Byte>(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             ReadOnlySpan<Byte> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().WriteFixedBinary(scope.get().start() + col.getOffset(), value, col.getSize());
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySequence<byte> value)
    public Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col,
                             ReadOnlySequence<Byte> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        checkArgument(col.getSize() >= 0);
        checkArgument(value.Length == col.getSize());
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        b.get().WriteFixedBinary(scope.get().start() + col.getOffset(), value, col.getSize());
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        return Result.SUCCESS;
    }

    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, byte[] value) {
        return writeSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, byte[] value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result writeSparse(RowBuffer b, RowCursor edit, byte[] value,
                              UpdateOptions options) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteSparse(ref b, ref edit, new ReadOnlySpan<byte>(value), options);
        return this.WriteSparse(b, edit, new ReadOnlySpan<Byte>(value), options);
    }

    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              ReadOnlySpan<Byte> value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySpan<byte> value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              ReadOnlySpan<Byte> value, UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              ReadOnlySequence<Byte> value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySequence<byte> value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit,
                              ReadOnlySequence<Byte> value, UpdateOptions options) {
        Result result = LayoutType.prepareSparseWrite(b, edit, this.typeArg().clone(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        b.get().writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // byte[] value)
    @Override
    public Result writeVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                LayoutColumn col, byte[] value) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteVariable(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        return this.WriteVariable(b, scope, col, new ReadOnlySpan<Byte>(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result WriteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                LayoutColumn col, ReadOnlySpan<Byte> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = value.Length;
        if ((col.getSize() > 0) && (length > col.getSize())) {
            return Result.TOO_BIG;
        }

        boolean exists = b.get().readBit(scope.get().start(), col.getNullBit().clone());
        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            col.getOffset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        b.get().writeVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        scope.get().metaOffset(scope.get().metaOffset() + shift);
        scope.get().valueOffset(scope.get().valueOffset() + shift);
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySequence<byte> value)
    public Result WriteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                LayoutColumn col, ReadOnlySequence<Byte> value) {
        checkArgument(scope.get().scopeType() instanceof LayoutUDT);
        if (scope.get().immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = (int)value.Length;
        if ((col.getSize() > 0) && (length > col.getSize())) {
            return Result.TOO_BIG;
        }

        boolean exists = b.get().readBit(scope.get().start(), col.getNullBit().clone());
        int varOffset = b.get().computeVariableValueOffset(scope.get().layout(), scope.get().start(),
            col.getOffset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        b.get().writeVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        b.get().setBit(scope.get().start(), col.getNullBit().clone());
        scope.get().metaOffset(scope.get().metaOffset() + shift);
        scope.get().valueOffset(scope.get().valueOffset() + shift);
        return Result.SUCCESS;
    }
}