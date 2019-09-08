// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

public final class LayoutResolverSimple extends LayoutResolver {

    private tangible.Func1Param<SchemaId, Layout> resolver;

    public LayoutResolverSimple(tangible.Func1Param<SchemaId, Layout> resolver) {
        this.resolver = (SchemaId arg) -> resolver.invoke(arg);
    }

    @Override
    public Layout resolve(SchemaId schemaId) {
        return this.resolver.invoke(schemaId);
    }
}