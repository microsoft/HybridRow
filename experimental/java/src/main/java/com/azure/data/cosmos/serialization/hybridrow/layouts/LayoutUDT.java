// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutUDT extends LayoutPropertyScope {

    public LayoutUDT(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_SCHEMA : LayoutCode.SCHEMA, immutable);
    }

    @Override
    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return LayoutCode.BYTES + SchemaId.BYTES;
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_udt" : "udt";
    }

    @Override
    @Nonnull
    public TypeArgumentList readTypeArgumentList(@Nonnull RowBuffer row, int offset, @Nonnull Out<Integer> lengthInBytes) {
        SchemaId schemaId = row.readSchemaId(offset);
        lengthInBytes.set(SchemaId.BYTES);
        return new TypeArgumentList(schemaId);
    }

    @Override
    @Nonnull
    public Result writeScope(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull TypeArgumentList typeArgs, @Nonnull Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull TypeArgumentList typeArgs, @Nonnull UpdateOptions options,
                             @Nonnull Out<RowCursor> value) {

        Layout udt = buffer.resolver().resolve(typeArgs.schemaId());
        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeSparseUDT(edit, this, udt, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(@Nonnull RowBuffer buffer, int offset, @Nonnull TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        buffer.writeSchemaId(offset + LayoutCode.BYTES, value.schemaId());
        return LayoutCode.BYTES + SchemaId.BYTES;
    }
}