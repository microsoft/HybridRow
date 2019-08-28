//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Schema;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

/**
 * An implementation of {@link LayoutResolver} which dynamically compiles schema from
 * a {@link Namespace}.
 * <p>
 * <p>
 * This resolver assumes that {@link Schema} within the {@link Namespace} have
 * their {@link Schema.SchemaId} properly populated. The resolver caches compiled schema.
 * <p>
 * All members of this class are multi-thread safe.
 */
public final class LayoutResolverNamespace extends LayoutResolver {
    private java.util.concurrent.ConcurrentHashMap<Integer, Layout> layoutCache;
    private LayoutResolver parent;
    private Namespace schemaNamespace;


    public LayoutResolverNamespace(Namespace schemaNamespace) {
        this(schemaNamespace, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public LayoutResolverNamespace(Namespace schemaNamespace, LayoutResolver parent = default)
    public LayoutResolverNamespace(Namespace schemaNamespace, LayoutResolver parent) {
        this.schemaNamespace = schemaNamespace;
        this.parent = parent;
        this.layoutCache = new java.util.concurrent.ConcurrentHashMap<Integer, Layout>();
    }

    public Namespace getNamespace() {
        return this.schemaNamespace;
    }

    @Override
    public Layout Resolve(SchemaId schemaId) {
        Layout layout;
        // TODO: C# TO JAVA CONVERTER: There is no Java ConcurrentHashMap equivalent to this .NET
        // ConcurrentDictionary method:
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        if (this.layoutCache.TryGetValue(schemaId.id(), out layout)) {
            return layout;
        }

        for (Schema s : this.schemaNamespace.getSchemas()) {
            if (SchemaId.opEquals(s.getSchemaId().clone(),
                schemaId.clone())) {
                layout = s.Compile(this.schemaNamespace);
                layout = this.layoutCache.putIfAbsent(schemaId.id(), layout);
                return layout;
            }
        }

        layout = this.parent == null ? null : this.parent.Resolve(schemaId.clone());
        if (layout != null) {
            // TODO: C# TO JAVA CONVERTER: There is no Java ConcurrentHashMap equivalent to this .NET
            // ConcurrentDictionary method:
            boolean succeeded = this.layoutCache.TryAdd(schemaId.id(), layout);
            checkState(succeeded);
            return layout;
        }

        throw new IllegalStateException(lenientFormat("Failed to resolve schema %s", schemaId.clone()));
        return null;
    }
}