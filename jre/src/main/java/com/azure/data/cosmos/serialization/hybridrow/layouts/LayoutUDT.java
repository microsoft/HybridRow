//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

public final class LayoutUDT extends LayoutPropertyScope {
    public LayoutUDT(boolean immutable) {
        super(immutable ? com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_SCHEMA :
            com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SCHEMA, immutable);
    }

    public String name() {
        return this.Immutable ? "im_udt" : "udt";
    }

    public int countTypeArgument(TypeArgumentList value) {
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + SchemaId.SIZE;
    }

    @Override
    public TypeArgumentList readTypeArgumentList(Reference<RowBuffer> row, int offset,
                                                 Out<Integer> lenInBytes) {
        SchemaId schemaId = row.get().ReadSchemaId(offset).clone();
        lenInBytes.setAndGet(SchemaId.SIZE);
        return new TypeArgumentList(schemaId.clone());
    }

    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value) {
        return WriteScope(b, edit, typeArgs, value, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> edit,
                             TypeArgumentList typeArgs, Out<RowCursor> value, UpdateOptions options) {
        Layout udt = b.get().resolver().Resolve(typeArgs.schemaId().clone());
        Result result = prepareSparseWrite(b, edit, new TypeArgument(this, typeArgs.clone()), options);
        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        b.get().WriteSparseUDT(edit, this, udt, options, value.clone());
        return Result.Success;
    }

    @Override
    public int writeTypeArgument(Reference<RowBuffer> row, int offset, TypeArgumentList value) {
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        row.get().WriteSchemaId(offset + (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE), value.schemaId().clone());
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + SchemaId.SIZE;
    }
}