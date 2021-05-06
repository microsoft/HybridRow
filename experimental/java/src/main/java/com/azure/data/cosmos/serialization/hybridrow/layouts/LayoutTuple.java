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
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        final int numTypeArgs = (int) buffer.readVariableUInt(offset, lengthInBytes);
        final TypeArgument[] typeArgs = new TypeArgument[numTypeArgs];
        final Out<Integer> len = new Out<>();

        int sum = lengthInBytes.get();

        for (int i = 0; i < numTypeArgs; i++) {
            typeArgs[i] = readTypeArgument(buffer, offset + sum, len);
            sum += len.get();
        }

        return new TypeArgumentList(typeArgs);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options,
        @Nonnull final Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeSparseTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(@Nonnull RowBuffer buffer, int offset, @Nonnull TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += buffer.writeVariableUInt(offset + lengthInBytes, value.count());
        for (TypeArgument arg : value.list()) {
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}