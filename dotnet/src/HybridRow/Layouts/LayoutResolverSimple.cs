// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;

    public sealed class LayoutResolverSimple : LayoutResolver
    {
        private readonly Func<SchemaId, Layout> resolver;

        public LayoutResolverSimple(Func<SchemaId, Layout> resolver)
        {
            this.resolver = resolver;
        }

        public override Layout Resolve(SchemaId schemaId)
        {
            return this.resolver(schemaId);
        }
    }
}
