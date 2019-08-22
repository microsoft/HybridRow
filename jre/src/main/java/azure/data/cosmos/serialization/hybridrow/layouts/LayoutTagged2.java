//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTagged2 extends LayoutIndexedScope {
    public LayoutTagged2(boolean immutable) {
        super(immutable ? azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTagged2Scope : azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Tagged2Scope, immutable, isSizedScope:
        true, isFixedArity:true, isUniqueScope:false, isTypedScope:true)
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tagged2_t" : "tagged2_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 3);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (int i = 1; i < value.getCount(); i++) {
            TypeArgument arg = value.get(i).clone();
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public boolean HasImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        checkState(edit.argValue.index >= 0);
        checkState(edit.argValue.scopeTypeArgs.getCount() > edit.argValue.index);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.argValue.scopeTypeArgs.get(edit.argValue.index).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(tangible.RefObject<RowBuffer> row, int offset,
                                                 tangible.OutObject<Integer> lenInBytes) {
        lenInBytes.argValue = 0;
        TypeArgument[] retval = new TypeArgument[3];
        retval[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.Empty);
        for (int i = 1; i < 3; i++) {
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
        checkState(value.getCount() == 3);
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (int i = 1; i < value.getCount(); i++) {
            TypeArgument arg = value.get(i).clone();
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}