// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LayoutResolverSimple extends LayoutResolver {

    private Function<SchemaId, Layout> resolver;

    public LayoutResolverSimple(Function<SchemaId, Layout> resolver) {
        this.resolver = resolver;
    }

    @Nonnull
    @Override
    public Layout resolve(@Nonnull SchemaId schemaId) {
        checkNotNull(schemaId, "expected non-null schemaId");
        return this.resolver.apply(schemaId);
    }
}