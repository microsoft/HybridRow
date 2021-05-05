// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.io;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutResolverNamespace;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;
import static java.lang.System.out;
import static java.util.Objects.requireNonNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertNotNull;

public class RowReaderTest {

    // region Fields

    private static final String basedir = System.getProperty("project.basedir", System.getProperty("user.dir"));

    private final Path dataFile;
    private final File schemaFile;

    private Namespace namespace;

    // endregion

    // region Construction and Setup

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

    // endregion

    @Test(groups = "unit")
    public void testIterable() throws IOException {
        try (final RowScanner scanner = RowScanner.open(this.namespace, this.dataFile)) {
            for (DataItem item : scanner) {
                assertNotNull(item);
                out.println(item);
            }
        }
    }

    @Test(groups = "unit")
    public void testReader() {

        final long length = this.dataFile.toFile().length();
        assertTrue(0 < length && length < Integer.MAX_VALUE);

        final ByteBuf data = Unpooled.buffer((int) length);

        try (InputStream stream = Files.newInputStream(this.dataFile)) {
            data.writeBytes(stream, (int) length);
        } catch (IOException error) {
            fail(lenientFormat("failed to open %s due to %s", this.dataFile, error));
        }

        final LayoutResolver resolver = new LayoutResolverNamespace(this.namespace);
        final RowBuffer buffer = new RowBuffer(data, HybridRowVersion.V1, resolver);
        final RowReader reader = new RowReader(buffer);

        final Result result;

        try {
            result = visitFields(reader, 0);
        } catch (IllegalStateException error) {
            throw new AssertionError(lenientFormat("row reader on %s failed due to %s", this.dataFile, error));
        }

        assertEquals(result, Result.SUCCESS);
    }

    @Test(groups = "unit")
    public void testScanner() throws Exception {
        try (final RowScanner scanner = RowScanner.open(this.namespace, this.dataFile)) {
            scanner.visit((DataItem item, Object context) -> {
                assertNull(context);
                assertNotNull(item);
                out.println(item);
                return Result.SUCCESS;
            }, null);
        }
    }

    // region Privates

    @SuppressWarnings("unchecked")
    private static Result visitFields(RowReader reader, int level) {

        Out out = new Out();

        while (reader.read()) {

            Utf8String path = reader.path();
            LayoutType type = reader.type();

            if (path.isNull() || type == null) {
                fail(lenientFormat("path: %s, type: %s", path, type));
            }

            Result result = Result.SUCCESS;
            out.set(null);

            switch (type.layoutCode()) {

                case BOOLEAN: {
                    result = reader.readBoolean(out);
                    break;
                }
                case INT_16: {
                    result = reader.readInt16(out);
                    break;
                }
                case INT_32: {
                    result = reader.readInt32(out);
                    break;
                }
                case INT_64: {
                    result = reader.readInt64(out);
                    break;
                }
                case UINT_8: {
                    result = reader.readUInt8(out);
                    break;
                }
                case UINT_32: {
                    result = reader.readUInt32(out);
                    break;
                }
                case UINT_64: {
                    result = reader.readUInt64(out);
                    break;
                }
                case BINARY: {
                    result = reader.readBinary(out);
                    break;
                }
                case GUID: {
                    result = reader.readGuid(out);
                    break;
                }
                case NULL:
                case BOOLEAN_FALSE:
                case INT_8:
                case UINT_16:
                case VAR_INT:
                case VAR_UINT:
                case FLOAT_32:
                case FLOAT_64:
                case FLOAT_128:
                case DECIMAL:
                case DATE_TIME:
                case UNIX_DATE_TIME:
                case UTF_8: {
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

                    System.out.print(Strings.repeat("  ", level));
                    System.out.println(lenientFormat("%s: %s", path, type.name()));

                    result = reader.readScope(null, (RowReader child, Object ignored) -> visitFields(child, level + 1));

                    System.out.print(Strings.repeat("  ", level));
                    System.out.println("end");
                    break;
                }
                case END_SCOPE: {
                    fail(lenientFormat("unexpected layout type: %s", type));
                    break;
                }
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

            if (result != Result.SUCCESS) {
                return result;
            }

            if (out.isPresent()) {
                Object value = out.get();
                System.out.print(Strings.repeat("  ", level));
                System.out.println(lenientFormat("%s: %s = %s",
                    path, type.name(), value instanceof ByteBuf ? ByteBufUtil.hexDump((ByteBuf)value) : value)
                );
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

    // endregion
}