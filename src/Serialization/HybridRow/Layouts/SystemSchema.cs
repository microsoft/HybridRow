// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public static class SystemSchema
    {
        /// <summary>
        /// SchemaId of the empty schema. This schema has no defined cells but can accomodate
        /// unschematized sparse content.
        /// </summary>
        public static readonly SchemaId EmptySchemaId = new SchemaId(2147473650);

        /// <summary>Returns a copy of the system namespace layout.</summary>
        public static LayoutResolver LayoutResolver => SchemasHrSchema.LayoutResolver;

        /// <summary>Returns a copy of the system namespace.</summary>
        public static Namespace GetNamespace() => SchemasHrSchema.Namespace;
    }
}
