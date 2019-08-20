// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Collections.Concurrent;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    /// <summary>
    /// An implementation of <see cref="LayoutResolver" /> which dynamically compiles schema from
    /// a <see cref="Namespace" />.
    /// </summary>
    /// <remarks>
    /// This resolver assumes that <see cref="Schema" /> within the <see cref="Namespace" /> have
    /// their <see cref="Schema.SchemaId" /> properly populated. The resolver caches compiled schema.
    /// </remarks>
    /// <remarks>All members of this class are multi-thread safe.</remarks>
    public sealed class LayoutResolverNamespace : LayoutResolver
    {
        private readonly ConcurrentDictionary<int, Layout> layoutCache;
        private readonly LayoutResolver parent;
        private readonly Namespace schemaNamespace;

        public LayoutResolverNamespace(Namespace schemaNamespace, LayoutResolver parent = default)
        {
            this.schemaNamespace = schemaNamespace;
            this.parent = parent;
            this.layoutCache = new ConcurrentDictionary<int, Layout>();
        }

        public Namespace Namespace => this.schemaNamespace;

        public override Layout Resolve(SchemaId schemaId)
        {
            if (this.layoutCache.TryGetValue(schemaId.Id, out Layout layout))
            {
                return layout;
            }

            foreach (Schema s in this.schemaNamespace.Schemas)
            {
                if (s.SchemaId == schemaId)
                {
                    layout = s.Compile(this.schemaNamespace);
                    layout = this.layoutCache.GetOrAdd(schemaId.Id, layout);
                    return layout;
                }
            }

            layout = this.parent?.Resolve(schemaId);
            if (layout != null)
            {
                bool succeeded = this.layoutCache.TryAdd(schemaId.Id, layout);
                Contract.Assert(succeeded);
                return layout;
            }

            Contract.Fail($"Failed to resolve schema {schemaId}");
            return null;
        }
    }
}
