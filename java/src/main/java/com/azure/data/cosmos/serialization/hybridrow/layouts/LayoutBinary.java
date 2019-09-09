// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class LayoutBinary extends LayoutType<byte[]> implements ILayoutSpanWritable<Byte>, ILayoutSpanReadable<Byte>, ILayoutSequenceWritable<Byte> {

    public LayoutBinary() {
        super(LayoutCode.BINARY, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public Result WriteSparse(RowBuffer buffer, RowCursor edit, ReadOnlySequence<Byte> value) {
        return writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Nonnull
    public String name() {
        return "binary";
    }

    @Override
    @Nonnull
    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result result = this.ReadFixed(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result result = this.ReadFixed(buffer, scope, column, out span);
        value.set((result == Result.SUCCESS) ? span.ToArray() :)
        default
        return result;
    }

    public Result readFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<ReadOnlySpan<Byte>> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFixedBinary(scope.start() + column.offset(), column.size()));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ReadOnlySpan<byte> value)
    public Result ReadSparse(RowBuffer buffer, RowCursor edit, Out<ReadOnlySpan<Byte>> value) {

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseBinary(edit));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out byte[] value)
    @Override
    @Nonnull
    public Result readSparse(RowBuffer buffer, RowCursor edit, Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadSparse(ref b, ref edit, out ReadOnlySpan<byte> span);
        Result r = this.ReadSparse(buffer, edit, out span);
        value.set((r == Result.SUCCESS) ? span.ToArray() :)
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result ReadVariable(ref RowBuffer buffer, ref RowCursor scope, LayoutColumn column, out
    // ReadOnlySpan<byte> value)
    public Result ReadVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<ReadOnlySpan<Byte>> value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableBinary(varOffset));
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // byte[] value)
    @Override
    @Nonnull
    public Result readVariable(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column, @Nonnull Out<byte[]> value) {
        ReadOnlySpan<Byte> span;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result r = this.ReadVariable(ref b, ref scope, col, out ReadOnlySpan<byte> span);
        Result r = this.ReadVariable(buffer, scope, column, out span);
        value.set((r == Result.SUCCESS) ? span.ToArray() :)
        default
        return r;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<Byte> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);
        checkArgument(value.Length == column.size());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFixedBinary(scope.start() + column.offset(), value, column.size());
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteFixed(ref RowBuffer buffer, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySequence<byte> value)
    public Result WriteFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, ReadOnlySequence<Byte> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);
        checkArgument(value.Length == column.size());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFixedBinary(scope.start() + column.offset(), value, column.size());
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte[]
    // value)
    @Override
    @Nonnull
    public Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, byte[] value) {
        checkArgument(value != null);
        return this.writeFixed(buffer, scope, column, new ReadOnlySpan<Byte>(value));
    }

    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, byte[] value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, byte[] value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    @Nonnull
    public Result writeSparse(RowBuffer buffer, RowCursor edit, byte[] value,
                              UpdateOptions options) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteSparse(ref b, ref edit, new ReadOnlySpan<byte>(value), options);
        return this.WriteSparse(buffer, edit, new ReadOnlySpan<Byte>(value), options);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySpan<byte> value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    public Result writeSparse(RowBuffer buffer, RowCursor edit, List<Byte> value, UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    public Result writeSparse(RowBuffer buffer, RowCursor edit, Byte value) {
        return writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    public Result WriteSparse(RowBuffer buffer, RowCursor edit, ReadOnlySequence<Byte> value, UpdateOptions options) {

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, byte[] value) {
        checkArgument(value != null);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: return this.WriteVariable(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        return this.writeVariable(buffer, scope, column, new ReadOnlySpan<Byte>(value));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col,
    // ReadOnlySpan<byte> value)
    public Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<Byte> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = value.Length;

        if ((column.size() > 0) && (length > column.size())) {
            return Result.TOO_BIG;
        }

        boolean exists = buffer.readBit(scope.start(), column.nullBit());
        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(),
            column.offset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        buffer.writeVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift.get();
        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public Result WriteVariable(ref RowBuffer buffer, ref RowCursor scope, LayoutColumn column,
    // ReadOnlySequence<byte> value)
    public Result WriteVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, ReadOnlySequence<Byte> value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        int length = (int)value.Length;
        if ((column.size() > 0) && (length > column.size())) {
            return Result.TOO_BIG;
        }

        boolean exists = buffer.readBit(scope.start(), column.nullBit());
        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(),
            column.offset());
        int shift;
        Out<Integer> tempOut_shift = new Out<Integer>();
        buffer.writeVariableBinary(varOffset, value, exists, tempOut_shift);
        shift = tempOut_shift;
        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);
        return Result.SUCCESS;
    }
}