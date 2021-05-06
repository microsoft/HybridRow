// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    public abstract class LayoutResolver
    {
        public abstract Layout Resolve(SchemaId schemaId);
    }
}
