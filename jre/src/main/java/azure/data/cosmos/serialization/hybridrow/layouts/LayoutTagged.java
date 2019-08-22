//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableTaggedScope;
import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TaggedScope;

public final class LayoutTagged extends LayoutIndexedScope {
    public LayoutTagged(boolean immutable) {
        super(immutable ? ImmutableTaggedScope : TaggedScope, immutable, true, true, false, true);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_tagged_t" : "tagged_t";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        checkState(value.getCount() == 2);
        return (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + value.get(1).getType().CountTypeArgument(value.get(1).getTypeArgs().clone());
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
        TypeArgument[] retval = new TypeArgument[2];
        retval[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.Empty);
        retval[1] = LayoutType.ReadTypeArgument(row, offset, lenInBytes);
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
        Contract.Assert(value.getCount() == 2);
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        int lenInBytes = (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
        lenInBytes += value.get(1).getType().WriteTypeArgument(row, offset + lenInBytes,
            value.get(1).getTypeArgs().clone());
        return lenInBytes;
    }
}