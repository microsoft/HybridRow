// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.IMMUTABLE_TUPLE_SCOPE;
import static com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.TUPLE_SCOPE;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutTuple extends LayoutIndexedScope {

    public LayoutTuple(boolean immutable) {
        super(
            immutable ? IMMUTABLE_TUPLE_SCOPE : TUPLE_SCOPE,
            immutable, false, true, false, false
        );
    }

    @Override
    public int countTypeArgument(@Nonnull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES + RowBuffer.count7BitEncodedUInt(value.count()), Integer::sum);
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_tuple" : "tuple";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(RowBuffer buffer, int offset, Out<Integer> lenInBytes) {

        final int numTypeArgs = buffer.read7BitEncodedUInt(offset, lenInBytes);
        final TypeArgument[] typeArgs = new TypeArgument[numTypeArgs];
        final Out<Integer> itemLength = new Out<>();

        for (int i = 0; i < numTypeArgs; i++) {
            typeArgs[i] = readTypeArgument(buffer, offset + lenInBytes.get(), itemLength);
            lenInBytes.set(lenInBytes.get() + itemLength.get());
        }

        return new TypeArgumentList(typeArgs);
    }

    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(RowBuffer buffer, RowCursor edit, TypeArgumentList typeArgs, UpdateOptions options,
                             Out<RowCursor> value) {

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