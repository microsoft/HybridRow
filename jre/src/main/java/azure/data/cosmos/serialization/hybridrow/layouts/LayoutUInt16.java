//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public sealed class LayoutUInt16 : LayoutType<ushort>
public final class LayoutUInt16 extends LayoutType<Short> {
    public LayoutUInt16() {
        super(azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.UInt16, (Short.SIZE / Byte.SIZE));
    }

    @Override
    public boolean getIsFixed() {
        return true;
    }

    @Override
    public String getName() {
        return "uint16";
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out
    // ushort value)
    @Override
    public Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                            tangible.OutObject<Short> value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            value.argValue = 0;
            return Result.NotFound;
        }

        value.argValue = b.argValue.ReadUInt16(scope.argValue.start + col.getOffset());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ushort value)
    @Override
    public Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             tangible.OutObject<Short> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = 0;
            return result;
        }

        value.argValue = b.argValue.ReadSparseUInt16(edit);
        return Result.Success;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ushort
    // value)
    @Override
    public Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col,
                             short value) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        b.argValue.WriteUInt16(scope.argValue.start + col.getOffset(), value);
        b.argValue.SetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ushort value,
    // UpdateOptions options = UpdateOptions.Upsert)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, short value,
                              UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.WriteSparseUInt16(edit, value, options);
        return Result.Success;
    }

    @Override
    public Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, short value) {
        return WriteSparse(b, edit, value, UpdateOptions.Upsert);
    }
}