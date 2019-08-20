// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Reflection;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public static class SystemSchema
    {
        /// <summary>
        /// SchemaId of the empty schema. This schema has no defined cells but can accomodate
        /// unschematized sparse content.
        /// </summary>
        public static readonly SchemaId EmptySchemaId = new SchemaId(2147473650);

        /// <summary>SchemaId of HybridRow RecordIO Segments.</summary>
        public static readonly SchemaId SegmentSchemaId = new SchemaId(2147473648);

        /// <summary>SchemaId of HybridRow RecordIO Record Headers.</summary>
        public static readonly SchemaId RecordSchemaId = new SchemaId(2147473649);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutResolver LayoutResolver = SystemSchema.LoadSchema();

        private static LayoutResolver LoadSchema()
        {
            string json = SystemSchema.GetEmbeddedResource(@"SystemSchemas\SystemSchema.json");
            Namespace ns = Namespace.Parse(json);
            LayoutResolverNamespace resolver = new LayoutResolverNamespace(ns);
            return resolver;
        }

        private static string GetEmbeddedResource(string resourceName)
        {
            Assembly assembly = Assembly.GetAssembly(typeof(RecordIOFormatter));
            resourceName = SystemSchema.FormatResourceName(assembly, resourceName);
            using (Stream resourceStream = assembly.GetManifestResourceStream(resourceName))
            {
                if (resourceStream == null)
                {
                    return null;
                }

                using (StreamReader reader = new StreamReader(resourceStream))
                {
                    return reader.ReadToEnd();
                }
            }
        }

        private static string FormatResourceName(Assembly assembly, string resourceName)
        {
            return assembly.GetName().Name + "." + resourceName.Replace(" ", "_").Replace("\\", ".").Replace("/", ".");
        }
    }
}
