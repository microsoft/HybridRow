// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

public final class LayoutEndScope extends LayoutTypeScope {

    public LayoutEndScope() {
        super(LayoutCode.END_SCOPE, false, false, false, false, false, false);
    }

    @Nonnull
    public String name() {
        return "end";
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final Out<RowCursor> value) {
        return this.writeScope(buffer, scope, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @Nonnull
    public Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final UpdateOptions options,
        @Nonnull final Out<RowCursor> value) {

        assert false : "cannot write an EndScope directly";
        value.set(null);

        return Result.FAILURE;
    }
}