//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTypedSetScope;
import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TypedSetScope;

public final class LayoutTypedSet extends LayoutUniqueScope {
    public LayoutTypedSet(boolean immutable) {
        super(immutable ? ImmutableTypedSetScope : TypedSetScope, immutable, true, true);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_set_t" : "set_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 1);
        return (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(0).getType().CountTypeArgument(value.get(0).getTypeArgs().clone());
    }

    @Override
    public TypeArgument FieldType(tangible.RefObject<RowCursor> scope) {
        return scope.argValue.scopeTypeArgs.get(0).clone();
    }

    @Override
    public boolean HasImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        checkState(edit.argValue.index >= 0);
        checkState(edit.argValue.scopeTypeArgs.getCount() == 1);
        return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.argValue.scopeTypeArgs.get(0).getType().LayoutCode);
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(tangible.RefObject<RowBuffer> row, int offset,
                                                 tangible.OutObject<Integer> lenInBytes) {
        return new TypeArgumentList(new azure.data.cosmos.serialization.hybridrow.layouts.TypeArgument[] { LayoutType.ReadTypeArgument(row, offset, lenInBytes) });
    }

    @Override
    public void SetImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        edit.argValue.cellType = edit.argValue.scopeTypeArgs.get(0).getType();
        edit.argValue.cellTypeArgs = edit.argValue.scopeTypeArgs.get(0).getTypeArgs().clone();
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

        b.argValue.WriteTypedSet(edit, this, typeArgs.clone(), options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(tangible.RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        Contract.Assert(value.getCount() == 1);
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(0).getType().WriteTypeArgument(row, offset + lenInBytes,
            value.get(0).getTypeArgs().clone());
        return lenInBytes;
    }
}