// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TUPLE_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TUPLE_SCOPE;

public final class LayoutTuple extends LayoutIndexedScope {

    public LayoutTuple(boolean immutable) {
        super(
            immutable ? IMMUTABLE_TUPLE_SCOPE : TUPLE_SCOPE,
            immutable, false, true, false, false
        );
    }

    public String name() {
        return this.isImmutable() ? "im_tuple" : "tuple";
    }

    public int countTypeArgument(TypeArgumentList value) {
        int lenInBytes = LayoutCode.BYTES;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
        lenInBytes += RowBuffer.count7BitEncodedUInt(value.count());
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().countTypeArgument(arg.typeArgs());
        }

        return lenInBytes;
    }

    @Override
    public TypeArgumentList readTypeArgumentList(RowBuffer buffer, int offset, Out<Integer> lenInBytes) {
        int numTypeArgs = buffer.intValue().Read7BitEncodedUInt(offset, lenInBytes);
        TypeArgument[] retval = new TypeArgument[numTypeArgs];
        for (int i = 0; i < numTypeArgs; i++) {
            int itemLenInBytes;
            Out<Integer> tempOut_itemLenInBytes = new Out<Integer>();
            retval[i] = readTypeArgument(buffer, offset + lenInBytes.get(), tempOut_itemLenInBytes);
            itemLenInBytes = tempOut_itemLenInBytes.get();
            lenInBytes.set(lenInBytes.get() + itemLenInBytes);
        }

        return new TypeArgumentList(retval);
    }

    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, Out<RowCursor> value) {
        return writeScope(buffer, edit, typeArgs, UpdateOptions.Upsert, value);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope(ref RowBuffer b, ref RowCursor edit, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert)
    @Override
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, UpdateOptions options, Out<RowCursor> value) {

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeSparseTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(RowBuffer buffer, int offset, TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lenInBytes = LayoutCode.BYTES;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: lenInBytes += buffer.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
        lenInBytes += buffer.write7BitEncodedUInt(offset + lenInBytes, (long) value.count());
        for (TypeArgument arg : value) {
            lenInBytes += arg.type().writeTypeArgument(buffer, offset + lenInBytes, arg.typeArgs());
        }

        return lenInBytes;
    }
}