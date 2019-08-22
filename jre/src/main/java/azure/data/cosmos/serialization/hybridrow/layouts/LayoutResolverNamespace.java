//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.SchemaId;
import azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import azure.data.cosmos.serialization.hybridrow.schemas.Schema;

/**
 * An implementation of <see cref="LayoutResolver" /> which dynamically compiles schema from
 * a <see cref="Namespace" />.
 * <p>
 * <p>
 * This resolver assumes that <see cref="Schema" /> within the <see cref="Namespace" /> have
 * their <see cref="Schema.SchemaId" /> properly populated. The resolver caches compiled schema.
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
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (this.layoutCache.TryGetValue(schemaId.getId(), out layout)) {
            return layout;
        }

        for (Schema s : this.schemaNamespace.getSchemas()) {
            if (azure.data.cosmos.serialization.hybridrow.SchemaId.opEquals(s.getSchemaId().clone(),
                schemaId.clone())) {
                layout = s.Compile(this.schemaNamespace);
                layout = this.layoutCache.putIfAbsent(schemaId.getId(), layout);
                return layout;
            }
        }

        layout = this.parent == null ? null : this.parent.Resolve(schemaId.clone());
        if (layout != null) {
            // TODO: C# TO JAVA CONVERTER: There is no Java ConcurrentHashMap equivalent to this .NET
            // ConcurrentDictionary method:
            boolean succeeded = this.layoutCache.TryAdd(schemaId.getId(), layout);
            checkState(succeeded);
            return layout;
        }

        Contract.Fail(String.format("Failed to resolve schema %1$s", schemaId.clone()));
        return null;
    }
}