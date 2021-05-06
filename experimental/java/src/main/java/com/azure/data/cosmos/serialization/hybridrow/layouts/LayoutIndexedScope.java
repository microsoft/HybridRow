// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LayoutIndexedScope extends LayoutTypeScope {

    protected LayoutIndexedScope(
        @Nonnull final LayoutCode code,
        final boolean immutable,
        final boolean isSizedScope,
        final boolean isFixedArity,
        final boolean isUniqueScope,
        final boolean isTypedScope) {
        super(code, immutable, isSizedScope, true, isFixedArity, isUniqueScope, isTypedScope);
    }

    @Override
    public void readSparsePath(@Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        edit.pathToken(0);
        edit.pathOffset(0);
    }
}