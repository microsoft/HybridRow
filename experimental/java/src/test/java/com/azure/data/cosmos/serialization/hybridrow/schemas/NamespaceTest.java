// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import static com.google.common.base.Strings.lenientFormat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.fail;

public class NamespaceTest {

    private static final String basedir = System.getProperty("project.basedir", System.getProperty("user.dir"));
    private final File schemaFile;
    private Namespace namespace;

    private NamespaceTest(File schemaFile) {
        this.schemaFile = schemaFile;
    }

    @BeforeClass(groups = "unit")
    public void setUp() {
        assertNull(this.namespace);
        this.namespace = Namespace.parse(this.schemaFile).orElseThrow(() ->
            new AssertionError(lenientFormat("failed to load %s", this.schemaFile))
        );
    }

    @Test(groups = "unit")
    public void testName() {
        String name = UUID.randomUUID().toString();
        this.namespace.name(name);
        assertEquals(this.namespace.name(), name);
    }

    @Test(groups = "unit")
    public void testSchemas() {

        assertNotNull(this.namespace.schemas());

        for (Schema schema : this.namespace.schemas()) {

            assertNotNull(schema.name());
            assertNotNull(schema.schemaId());
            assertNotNull(schema.properties());

            for (Property property : schema.properties()) {

                assertNotNull(property.path());
                assertNotNull(property.type());

                assertValidPropertyType(property.type());
            }
        }
    }

    @Test(groups = "unit")
    public void testVersion() {

        assertNotNull(this.namespace.version());
        assertThrows(NullPointerException.class, () -> this.namespace.version(null));

        for (SchemaLanguageVersion version : SchemaLanguageVersion.values()) {
            this.namespace.version(version);
            assertEquals(this.namespace.version(), version);
        }
    }

    private static void assertValidPropertyType(PropertyType propertyType) {

        if (propertyType instanceof UdtPropertyType) {
            UdtPropertyType value = (UdtPropertyType) propertyType;
            assertNotNull(value.name());
            assertNotNull(value.schemaId());
            assertEquals(value.type(), TypeKind.SCHEMA);
            return;
        }
        if (propertyType instanceof ArrayPropertyType) {
            ArrayPropertyType value = (ArrayPropertyType) propertyType;
            assertEquals(value.type(), TypeKind.ARRAY);
            if (value.items() != null) {
                assertValidPropertyType(value.items());
            }
            return;
        }
        if (propertyType instanceof PrimitivePropertyType) {

            PrimitivePropertyType value = (PrimitivePropertyType) propertyType;

            switch (value.type()) {
                case BOOLEAN:
                case NULL:
                case INT_8:
                case INT_16:
                case INT_32:
                case INT_64:
                case VAR_INT:
                case UINT_8:
                case UINT_16:
                case UINT_32:
                case UINT_64:
                case VAR_UINT:
                case DECIMAL:
                case FLOAT_32:
                case FLOAT_64:
                case FLOAT_128:
                case GUID:
                case DATE_TIME:
                case UNIX_DATE_TIME:
                case BINARY:
                case UTF_8:
                    assertNotNull(value.storage());
                    assertNotEquals(value.storage(), StorageKind.NONE);
                    break;
                default:
                    fail(lenientFormat("not a primitive TypeKind: %s", value.type()));
            }

            return;
        }
        fail(lenientFormat("untested property type: %s", propertyType));
    }

    public static class Builder {
        @Factory
        public static Object[] create() {
            return new Object[] {
                new NamespaceTest(Paths.get(basedir, "test-data", "RootSegment.json").toFile())
            };
        }
    }
}
