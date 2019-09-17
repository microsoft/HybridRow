// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

public class SystemSchemaTest {

    private static final Path SchemaFile = Paths.get("data", "CustomerSchema.json");

    @Test
    public void testLoadSchema() {

        final LayoutResolver layoutResolver = SystemSchema.layoutResolver();

        final Layout recordLayout = layoutResolver.resolve(SystemSchema.RECORD_SCHEMA_ID);
        assertEquals(recordLayout.name(), "Record");
        assertEquals(recordLayout.schemaId(), SystemSchema.RECORD_SCHEMA_ID);

        final Layout segmentLayout = layoutResolver.resolve(SystemSchema.SEGMENT_SCHEMA_ID);
        assertEquals(segmentLayout.name(), "Segment");
        assertEquals(segmentLayout.schemaId(), SystemSchema.SEGMENT_SCHEMA_ID);
    }
}