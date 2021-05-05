// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutObject extends LayoutPropertyScope {

    private TypeArgument typeArg;

    public LayoutObject(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_OBJECT_SCOPE : LayoutCode.OBJECT_SCOPE, immutable);
        this.typeArg = new TypeArgument(this);
    }

    @Override
    @Nonnull
    public String name() {
        return this.isImmutable() ? "im_object" : "object";
    }

    public TypeArgument typeArg() {
        return this.typeArg;
    }


    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor edit,
        @Nonnull TypeArgumentList typeArgs, @Nonnull Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull RowBuffer buffer,
        @Nonnull RowCursor edit,
        @Nonnull TypeArgumentList typeArgs,
        @Nonnull UpdateOptions options, @Nonnull Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeSparseObject(edit, this, options));
        return Result.SUCCESS;
    }
}