// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

import javax.annotation.Nonnull;

public abstract class LayoutResolver {
    @Nonnull
    public abstract Layout resolve(@Nonnull SchemaId schemaId);
}