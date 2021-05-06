// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Schema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An implementation of {@link LayoutResolver} which dynamically compiles schema from a {@link Namespace}.
 * <p>
 * This resolver assumes that {@link Schema} within the {@link Namespace} have their {@link Schema#schemaId()} properly
 * populated. The resolver caches compiled schema.
 * <p>
 * All members of this class are multi-thread safe.
 */
public final class LayoutResolverNamespace extends LayoutResolver {

    private final ConcurrentHashMap<SchemaId, Layout> layoutCache;
    private final LayoutResolver parent;
    private final Namespace schemaNamespace;

    public LayoutResolverNamespace(@Nonnull final Namespace namespace) {
        this(namespace, null);
    }

    public LayoutResolverNamespace(@Nonnull final Namespace schemaNamespace, @Nullable final LayoutResolver parent) {
        checkNotNull(schemaNamespace, "expected non-null schemaNamespace");
        this.schemaNamespace = schemaNamespace;
        this.parent = parent;
        this.layoutCache = new ConcurrentHashMap<>();
    }

    public Namespace namespace() {
        return this.schemaNamespace;
    }

    @Nonnull
    @Override
    public Layout resolve(@Nonnull SchemaId schemaId) {

        checkNotNull(schemaId, "expected non-null schemaId");

        Layout layout = this.layoutCache.computeIfAbsent(schemaId, id -> {
            for (Schema schema : this.namespace().schemas()) {
                if (schema.schemaId().equals(id)) {
                    return schema.compile(this.schemaNamespace);
                }
            }
            return this.parent == null ? null : this.parent.resolve(schemaId);
        });

        checkState(layout != null, "failed to resolve schema %s", schemaId);
        return layout;
    }
}