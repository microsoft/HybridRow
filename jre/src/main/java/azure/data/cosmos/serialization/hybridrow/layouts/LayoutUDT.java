//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;
import azure.data.cosmos.serialization.hybridrow.SchemaId;

public final class LayoutUDT extends LayoutPropertyScope {
    public LayoutUDT(boolean immutable) {
        super(immutable ? azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.ImmutableSchema : azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.Schema, immutable);
    }

    @Override
    public String getName() {
        return this.Immutable ? "im_udt" : "udt";
    }

    @Override
    public int CountTypeArgument(TypeArgumentList value) {
        return (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + SchemaId.Size;
    }

    @Override
    public TypeArgumentList ReadTypeArgumentList(tangible.RefObject<RowBuffer> row, int offset,
                                                 tangible.OutObject<Integer> lenInBytes) {
        SchemaId schemaId = row.argValue.ReadSchemaId(offset).clone();
        lenInBytes.argValue = SchemaId.Size;
        return new TypeArgumentList(schemaId.clone());
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
        Layout udt = b.argValue.getResolver().Resolve(typeArgs.getSchemaId().clone());
        Result result = LayoutType.PrepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        b.argValue.WriteSparseUDT(edit, this, udt, options, value.clone());
        return Result.Success;
    }

    @Override
    public int WriteTypeArgument(tangible.RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        row.argValue.WriteSparseTypeCode(offset, this.LayoutCode);
        row.argValue.WriteSchemaId(offset + (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE), value.getSchemaId().clone());
        return (azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + SchemaId.Size;
    }
}