//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ArrayScope;
import static azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableArrayScope;

public final class LayoutArray extends LayoutIndexedScope {
    private TypeArgument TypeArg = new TypeArgument();

    public LayoutArray(boolean immutable) {
        super(immutable ? ImmutableArrayScope : ArrayScope, immutable, false, false, false, false);
        this.TypeArg = new TypeArgument(this);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_array" : "array";
    }

    public TypeArgument getTypeArg() {
        return TypeArg;
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
        Result result = LayoutType.PrepareSparseWrite(b, edit, this.getTypeArg().clone(), options);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        b.argValue.WriteSparseArray(edit, this, options, value.clone());
        return Result.Success;
    }
}