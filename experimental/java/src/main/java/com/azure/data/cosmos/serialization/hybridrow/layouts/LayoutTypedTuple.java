// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutTypedTuple extends LayoutIndexedScope {

    public LayoutTypedTuple(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_TUPLE_SCOPE : LayoutCode.TYPED_TUPLE_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES + RowBuffer.count7BitEncodedUInt(value.count()), Integer::sum);
    }

    @Override
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_tuple_t" : "tuple_t";
    }

    @Nonnull
    @Override
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        final int numTypeArgs = (int) buffer.readVariableUInt(offset, lengthInBytes);
        final TypeArgument[] typeArgs = new TypeArgument[numTypeArgs];
        final Out<Integer> len = new Out<>();

        int sum = lengthInBytes.get();

        for (int i = 0; i < numTypeArgs; i++) {
            typeArgs[i] = LayoutType.readTypeArgument(buffer, offset + sum, len);
            sum += len.get();
        }

        lengthInBytes.set(sum);
        return new TypeArgumentList(typeArgs);
    }

    @Override
    public void setImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
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

        Result result = LayoutType.prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += buffer.writeVariableUInt(offset + lengthInBytes, value.count());

        for (TypeArgument arg : value.list()) {
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}