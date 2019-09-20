// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolverNamespace;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.google.common.base.Strings.lenientFormat;
import static org.testng.Assert.*;

public class RowReaderTest {

    private static final String basedir = System.getProperty("project.basedir", System.getProperty("user.dir"));
    private final File schemaFile;
    private final Path dataFile;
    private Namespace namespace;

    private RowReaderTest(File schemaFile, Path dataFile) {
        this.schemaFile = schemaFile;
        this.dataFile = dataFile;
    }

    @BeforeClass(groups = "unit")
    public void setUp() {
        assertNull(this.namespace);
        this.namespace = Namespace.parse(this.schemaFile).orElseThrow(() ->
            new AssertionError(lenientFormat("failed to load %s", this.schemaFile))
        );
    }

    @Test(groups = "unit")
    public void testRead() {

        final long length = this.dataFile.toFile().length();
        assertTrue(0 < length && length < Integer.MAX_VALUE);

        final ByteBuf data = Unpooled.buffer((int) length);

        try (InputStream stream = Files.newInputStream(this.dataFile)) {
            data.writeBytes(stream, (int) length);
        } catch (IOException error) {
            fail(lenientFormat("failed to open %s due to %s", this.dataFile, error));
        }

        LayoutResolver resolver = new LayoutResolverNamespace(this.namespace);
        RowBuffer buffer = new RowBuffer(data, HybridRowVersion.V1, resolver);
        RowReader reader = new RowReader(buffer);
        visitFields(reader, 0);
    }

    private static Result visitFields(RowReader reader, int level) {

        while (reader.read()) {

            final Utf8String path = reader.path();
            LayoutType type = reader.type();

            if (type == null) {
                fail(lenientFormat("path: %s, type: null", path));
            }

            System.out.println(lenientFormat("%s%s:\"%s\"", Strings.repeat("  ", level), path, type.layoutCode()));
            Out out = new Out();

            switch (type.layoutCode()) {
                case UINT_64: {
                    Result result = reader.readUInt64(out);
                    break;
                }
                case NULL:
                case BOOLEAN:
                case BOOLEAN_FALSE:
                case INT_8:
                case INT_16:
                case INT_32:
                case INT_64:
                case UINT_8:
                case UINT_16:
                case UINT_32:
                case VAR_INT:
                case VAR_UINT:
                case FLOAT_32:
                case FLOAT_64:
                case FLOAT_128:
                case DECIMAL:
                case DATE_TIME:
                case UNIX_DATE_TIME:
                case GUID:
                case UTF_8:
                case BINARY: {
                    break;
                }
                case NULLABLE_SCOPE:
                case IMMUTABLE_NULLABLE_SCOPE: {
                    if (!reader.hasValue()) {
                        break;
                    }
                }
                case ARRAY_SCOPE:
                case IMMUTABLE_ARRAY_SCOPE:

                case MAP_SCOPE:
                case IMMUTABLE_MAP_SCOPE:

                case OBJECT_SCOPE:
                case IMMUTABLE_OBJECT_SCOPE:

                case SCHEMA:
                case IMMUTABLE_SCHEMA:

                case SET_SCOPE:
                case IMMUTABLE_SET_SCOPE:

                case TAGGED2_SCOPE:
                case IMMUTABLE_TAGGED2_SCOPE:

                case TAGGED_SCOPE:
                case IMMUTABLE_TAGGED_SCOPE:

                case TUPLE_SCOPE:
                case IMMUTABLE_TUPLE_SCOPE:

                case TYPED_ARRAY_SCOPE:
                case IMMUTABLE_TYPED_ARRAY_SCOPE:

                case TYPED_MAP_SCOPE:
                case IMMUTABLE_TYPED_MAP_SCOPE:

                case TYPED_SET_SCOPE:
                case IMMUTABLE_TYPED_SET_SCOPE:

                case TYPED_TUPLE_SCOPE:
                case IMMUTABLE_TYPED_TUPLE_SCOPE: {

                    Result result = reader.readScope(null, (RowReader child, Object ignored) -> visitFields(child, level + 1));

                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
                }
                case END_SCOPE:
                case INVALID:
                case MONGODB_OBJECT_ID: {
                    fail(lenientFormat("unsupported layout type: %s", type));
                    break;
                }
                default: {
                    fail(lenientFormat("unknown layout type: %s", type));
                    break;
                }
            }
        }

        return Result.SUCCESS;
    }

    public static class Builder {
        @Factory
        public static Object[] create() {
            return new Object[] {
                new RowReaderTest(
                    Paths.get(basedir, "test-data", "RootSegment.json").toFile(),
                    Paths.get(basedir, "test-data", "RootSegment.hybridrow")
                )
            };
        }
    }
}