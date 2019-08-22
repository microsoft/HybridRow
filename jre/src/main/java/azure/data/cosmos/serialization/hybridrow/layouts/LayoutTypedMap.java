//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

public final class LayoutTypedMap extends LayoutUniqueScope {
    public LayoutTypedMap(boolean immutable) {
        super(immutable ? azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedMapScope : azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedMapScope, immutable, isSizedScope:
        true, isTypedScope:true)
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_map_t" : "map_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 2);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().CountTypeArgument(arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }

    @Override
    public TypeArgument FieldType(tangible.RefObject<RowCursor> scope) {
        return new TypeArgument(scope.argValue.scopeType.Immutable ? LayoutType.ImmutableTypedTuple :
            LayoutType.TypedTuple, scope.argValue.scopeTypeArgs.clone());
    }

    @Override
    public boolean HasImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        return true;
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(tangible.RefObject<RowBuffer> row, int offset,
                                                 tangible.OutObject<Integer> lenInBytes) {
        lenInBytes.argValue = 0;
        TypeArgument[] retval = new TypeArgument[2];
        for (int i = 0; i < 2; i++) {
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
        edit.argValue.cellType = edit.argValue.scopeType.Immutable ? LayoutType.ImmutableTypedTuple :
            LayoutType.TypedTuple;
        edit.argValue.cellTypeArgs = edit.argValue.scopeTypeArgs.clone();
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

        b.argValue.WriteTypedMap(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(tangible.RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        checkState(value.getCount() == 2);
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        for (TypeArgument arg : value) {
            lenInBytes += arg.getType().WriteTypeArgument(row, offset + lenInBytes, arg.getTypeArgs().clone());
        }

        return lenInBytes;
    }
}