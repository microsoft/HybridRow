// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.google.common.base.Strings.lenientFormat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

public class NamespaceTest {

    private static final String basedir = System.getProperty("project.basedir", System.getProperty("user.dir"));
    private final Path schemaPath;
    private Namespace namespace;

    private NamespaceTest(Path schemaPath) {
        this.schemaPath = schemaPath;
    }

    @BeforeClass(groups = "unit")
    public void setUp() {

        assertNull(this.namespace);

        try (InputStream stream = Files.newInputStream(this.schemaPath)) {

            this.namespace = Namespace.parse(stream).orElseThrow(() ->
                new AssertionError(lenientFormat("failed to parse %s", this.schemaPath))
            );

        } catch (IOException error) {
            fail(lenientFormat("unexpected %s", error));
        }
    }

    @Test(groups = "unit")
    public void testName() {
        String name = UUID.randomUUID().toString();
        this.namespace.name(name);
        assertEquals(this.namespace.name(), name);
    }

    @Test(groups = "unit")
    public void testSchemas() {
    }

    @Test(groups = "unit")
    public void testTestParse() {
    }

    @Test(groups = "unit")
    public void testTestSchemas() {
    }

    @Test(groups = "unit")
    public void testTestVersion() {
    }

    @Test(groups = "unit")
    public void testVersion() {
    }

    public static class Builder {
        @Factory
        public static Object[] create() {
            return new Object[] {
                new NamespaceTest(Paths.get(basedir, "test-data", "RootSegment.json"))
            };
        }
    }
}