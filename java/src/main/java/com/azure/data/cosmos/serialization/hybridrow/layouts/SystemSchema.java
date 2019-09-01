// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

public final class SystemSchema {
    /**
     * SchemaId of the empty schema. This schema has no defined cells but can accomodate
     * unschematized sparse content.
     */
    public static final SchemaId EmptySchemaId = new SchemaId(2147473650);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutResolver LayoutResolver = SystemSchema
    // .LoadSchema();
    public static final LayoutResolver LayoutResolver = SystemSchema.LoadSchema();
    /**
     * SchemaId of HybridRow RecordIO Record Headers.
     */
    public static final SchemaId RecordSchemaId = new SchemaId(2147473649);
    /**
     * SchemaId of HybridRow RecordIO Segments.
     */
    public static final SchemaId SegmentSchemaId = new SchemaId(2147473648);

    private static String FormatResourceName(Assembly assembly, String resourceName) {
        return assembly.GetName().Name + "." + resourceName.replace(" ", "_").replace("\\", ".").replace("/", ".");
    }

    private static String GetEmbeddedResource(String resourceName) {
        Assembly assembly = Assembly.GetAssembly(RecordIOFormatter.class);
        resourceName = SystemSchema.FormatResourceName(assembly, resourceName);
        try (Stream resourceStream = assembly.GetManifestResourceStream(resourceName)) {
            if (resourceStream == null) {
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(resourceStream)) {
                return reader.ReadToEnd();
            }
        }
    }

    private static LayoutResolver LoadSchema() {
        String json = SystemSchema.GetEmbeddedResource("SystemSchemas\\SystemSchema.json");
        Namespace ns = Namespace.Parse(json);
        LayoutResolverNamespace resolver = new LayoutResolverNamespace(ns);
        return resolver;
    }
}