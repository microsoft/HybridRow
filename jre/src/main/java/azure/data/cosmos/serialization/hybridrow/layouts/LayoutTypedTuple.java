//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTypedTuple extends LayoutIndexedScope {
    public LayoutTypedTuple(boolean immutable) {
        super(immutable ? azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedTupleScope : azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedTupleScope, immutable, isSizedScope:
        true, isFixedArity:true, isUniqueScope:false, isTypedScope:true)
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tuple_t" : "tuple_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
        lenInBytes += RowBuffer.Count7BitEncodedUInt(value.getCount());
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public boolean HasImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        Contract.Assert(edit.argValue.index >= 0);
        Contract.Assert(edit.argValue.scopeTypeArgs.getCount() > edit.argValue.index);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.argValue.scopeTypeArgs.get(edit.argValue.index).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(tangible.RefObject<RowBuffer> row, int offset,
                                                 tangible.OutObject<Integer> lenInBytes) {
        int numTypeArgs = row.argValue.intValue().Read7BitEncodedUInt(offset, lenInBytes);
        TypeArgument[] retval = new TypeArgument[numTypeArgs];
        for (int i = 0; i < numTypeArgs; i++) {
            int itemLenInBytes;
            tangible.OutObject<Integer> tempOut_itemLenInBytes = new tangible.OutObject<Integer>();
            retval[i] = LayoutType.ReadTypeArgument(row, offset + lenInBytes.argValue, tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.argValue;
            lenInBytes.argValue += itemLenInBytes;
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public void SetImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        edit.argValue.cellType = edit.argValue.scopeTypeArgs.get(edit.argValue.index).getType();
        edit.argValue.cellTypeArgs = edit.argValue.scopeTypeArgs.get(edit.argValue.index).getTypeArgs().clone();
    }

    @Override
    public Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value) {
        return WriteScope(b, edit, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                             TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value, UpdateOptions options) {
        Result result = LayoutType.PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        b.argValue.WriteTypedTuple(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(tangible.RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
        lenInBytes += row.argValue.Write7BitEncodedUInt(offset + lenInBytes, value.getCount());
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}