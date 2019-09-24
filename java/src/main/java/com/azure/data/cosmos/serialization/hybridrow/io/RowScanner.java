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
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;
import static java.util.Objects.requireNonNull;

public class RowScanner implements AutoCloseable {

    private final AtomicBoolean closed;
    private final ByteBuf data;
    private final LayoutResolver resolver;

    private RowScanner(LayoutResolver resolver, ByteBuf data) {
        this.closed = new AtomicBoolean();
        this.data = data.retain();
        this.resolver = resolver;
    }

    @Override
    public void close() throws Exception {
        if (this.closed.compareAndSet(false, true)) {
            this.data.release();
        }
    }

    public static RowScanner open(@Nonnull Namespace namespace, @Nonnull File file) throws IOException {

        checkNotNull(file, "expected non-null file");

        final long length = file.length();
        checkArgument(0 < length, "file does not exist: %s", file);
        checkArgument(length <= Integer.MAX_VALUE, "expected file length <= %s, not %s", Integer.MAX_VALUE, length);

        ByteBuf data = Unpooled.buffer((int) length);

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            data.writeBytes(stream, (int) length);
        }

        LayoutResolverNamespace resolver = new LayoutResolverNamespace(namespace);
        return new RowScanner(resolver, data);
    }

    public static RowScanner open(@Nonnull Namespace namespace, @Nonnull Path path) throws IOException {
        return RowScanner.open(namespace, requireNonNull(path, "expected non-null path").toFile());
    }

    public static RowScanner open(@Nonnull Namespace namespace, @Nonnull String path) throws IOException {
        return RowScanner.open(namespace, new File(requireNonNull(path, "expected non-null path")));
    }

    public <TContext> Result visit(BiFunction<DataItem, TContext, Result> accept, TContext context) {

        checkState(!this.closed.get(), "RowScanner is closed");

        final RowBuffer buffer = new RowBuffer(this.data, HybridRowVersion.V1, this.resolver);
        final RowReader reader = new RowReader(buffer);

        return visit(reader, new Visitor<>(accept, context, new Stack<>()));
    }

    @SuppressWarnings("unchecked")
    private static <TContext> Result visit(RowReader reader, Visitor<TContext> visitor) {

        final Out value = new Out();

        while (reader.read()) {

            Utf8String path = reader.path();
            checkState(!path.isNull(), "expected non-null value for path");

            LayoutType type = reader.type();
            checkState(type != null, "expected non-null type");

            final Result result;
            value.set(null);

            switch (type.layoutCode()) {

                case NULL: {
                    result = reader.readNull(value);
                    break;
                }
                case BOOLEAN: {
                    result = reader.readBoolean(value);
                    break;
                }
                case INT_8: {
                    result = reader.readInt8(value);
                    break;
                }
                case INT_16: {
                    result = reader.readInt16(value);
                    break;
                }
                case INT_32: {
                    result = reader.readInt32(value);
                    break;
                }
                case INT_64: {
                    result = reader.readInt64(value);
                    break;
                }
                case VAR_INT: {
                    result = reader.readVarInt(value);
                    break;
                }
                case UINT_8: {
                    result = reader.readUInt8(value);
                    break;
                }
                case UINT_16: {
                    result = reader.readUInt16(value);
                    break;
                }
                case UINT_32: {
                    result = reader.readUInt32(value);
                    break;
                }
                case UINT_64: {
                    result = reader.readUInt64(value);
                    break;
                }
                case VAR_UINT: {
                    result = reader.readVarUInt(value);
                    break;
                }
                case FLOAT_32: {
                    result = reader.readFloat32(value);
                    break;
                }
                case FLOAT_64: {
                    result = reader.readFloat64(value);
                    break;
                }
                case FLOAT_128: {
                    result = reader.readFloat128(value);
                    break;
                }
                case DECIMAL: {
                    result = reader.readDecimal(value);
                    break;
                }
                case GUID: {
                    result = reader.readGuid(value);
                    break;
                }
                case DATE_TIME: {
                    result = reader.readDateTime(value);
                    break;
                }
                case UNIX_DATE_TIME: {
                    result = reader.readUnixDateTime(value);
                    break;
                }
                case BINARY: {
                    result = reader.readBinary(value);
                    break;
                }
                case UTF_8: {
                    result = reader.readUtf8String(value);
                    break;
                }
                case NULLABLE_SCOPE:
                case IMMUTABLE_NULLABLE_SCOPE: {
                    if (!reader.hasValue()) {
                        result = Result.SUCCESS;
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

                    visitor.nodes().push(path.isEmpty()
                        ? Utf8String.transcodeUtf16(lenientFormat("[%s]", reader.index()))
                        : path);

                    result = reader.readScope(visitor, RowScanner::visit);
                    visitor.nodes().pop();

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    continue;
                }
                case MONGODB_OBJECT_ID: {
                    throw new IllegalStateException(lenientFormat("unsupported layout type: %s", type));
                }
                case BOOLEAN_FALSE:
                case END_SCOPE:
                case INVALID: {
                    throw new IllegalStateException(lenientFormat("unexpected layout type: %s", type));
                }
                default: {
                    throw new IllegalStateException(lenientFormat("unknown layout type: %s", type));
                }
            }

            if (result != Result.SUCCESS) {
                return result;
            }

            DataItem item = new DataItem(visitor.nodes(), path, type.layoutCode(), value.get());
            visitor.accept().apply(item, visitor.context());
        }

        return Result.SUCCESS;
    }

    private static class Visitor<TContext> {

        private final BiFunction<DataItem, TContext, Result> accept;
        private final TContext context;
        private final Stack<Utf8String> nodes;

        Visitor(BiFunction<DataItem, TContext, Result> accept, TContext context, Stack<Utf8String> nodes) {
            this.accept = accept;
            this.context = context;
            this.nodes = nodes;
        }

        BiFunction<DataItem, TContext, Result> accept() {
            return this.accept;
        }

        TContext context() {
            return this.context;
        }

        Stack<Utf8String> nodes() {
            return this.nodes;
        }
    }
}
