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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;
import static java.util.Objects.requireNonNull;

public class RowIterable implements AutoCloseable, Iterable<DataItem> {

    private AtomicBoolean closed;
    private final ByteBuf data;
    private final LayoutResolver resolver;

    private RowIterable(LayoutResolver resolver, ByteBuf data) {
        this.closed = new AtomicBoolean();
        this.data = data;
        this.resolver = resolver;
    }

    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.data.release();
        }
    }

    @Nonnull
    @Override
    public Iterator<DataItem> iterator() {

        checkState(!this.closed.get(), "RowIterable is closed");

        final RowBuffer buffer = new RowBuffer(this.data, HybridRowVersion.V1, this.resolver);
        final RowReader reader = new RowReader(buffer);

        return new RowIterator(reader);
    }

    public static RowIterable open(@Nonnull Namespace namespace, @Nonnull File file) throws IOException {

        checkNotNull(file, "expected non-null file");

        final long length = file.length();
        checkArgument(0 < length, "file does not exist: %s", file);
        checkArgument(length <= Integer.MAX_VALUE, "expected file length <= %s, not %s", Integer.MAX_VALUE, length);

        ByteBuf data = Unpooled.buffer((int) length);

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            data.writeBytes(stream, (int) length);
        }

        LayoutResolverNamespace resolver = new LayoutResolverNamespace(namespace);
        return new RowIterable(resolver, data);
    }

    public static RowIterable open(@Nonnull Namespace namespace, @Nonnull Path path) throws IOException {
        return RowIterable.open(namespace, requireNonNull(path, "expected non-null path").toFile());
    }

    public static RowIterable open(@Nonnull Namespace namespace, @Nonnull String path) throws IOException {
        return RowIterable.open(namespace, new File(requireNonNull(path, "expected non-null path")));
    }

    private static class RowIterator implements Iterator<DataItem> {

        final Stack<Utf8String> paths;
        final Stack<RowReader> readers;
        final Out value;

        DataItem dataItem;
        RowReader reader;

        RowIterator(RowReader reader) {
            this.readers = new Stack<>();
            this.paths = new Stack<>();
            this.reader = reader;
            this.value = new Out();
        }

        @Override
        public boolean hasNext() {

            while (this.dataItem == null) {
                if (this.reader == null) {
                    return false;
                }
                this.advance();
            }
            return true;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public DataItem next() {

            while (this.dataItem == null) {
                if (this.reader == null) {
                    throw new NoSuchElementException();
                }
                this.advance();
            }

            DataItem dataItem = this.dataItem;
            this.dataItem = null;

            return dataItem;
        }

        @SuppressWarnings("unchecked")
        private void advance() {

            do {
                while (this.reader.read()) {

                    final Result result;

                    Utf8String path = this.reader.path();
                    checkState(!path.isNull(), "expected non-null value for path");

                    LayoutType type = this.reader.type();
                    checkState(type != null, "expected non-null type");

                    switch (type.layoutCode()) {

                        case NULL: {
                            result = this.reader.readNull(this.value);
                            break;
                        }
                        case BOOLEAN: {
                            result = this.reader.readBoolean(this.value);
                            break;
                        }
                        case INT_8: {
                            result = this.reader.readInt8(this.value);
                            break;
                        }
                        case INT_16: {
                            result = this.reader.readInt16(this.value);
                            break;
                        }
                        case INT_32: {
                            result = this.reader.readInt32(this.value);
                            break;
                        }
                        case INT_64: {
                            result = this.reader.readInt64(this.value);
                            break;
                        }
                        case VAR_INT: {
                            result = this.reader.readVarInt(this.value);
                            break;
                        }
                        case UINT_8: {
                            result = this.reader.readUInt8(this.value);
                            break;
                        }
                        case UINT_16: {
                            result = this.reader.readUInt16(this.value);
                            break;
                        }
                        case UINT_32: {
                            result = this.reader.readUInt32(this.value);
                            break;
                        }
                        case UINT_64: {
                            result = this.reader.readUInt64(this.value);
                            break;
                        }
                        case VAR_UINT: {
                            result = this.reader.readVarUInt(this.value);
                            break;
                        }
                        case FLOAT_32: {
                            result = this.reader.readFloat32(this.value);
                            break;
                        }
                        case FLOAT_64: {
                            result = this.reader.readFloat64(this.value);
                            break;
                        }
                        case FLOAT_128: {
                            result = this.reader.readFloat128(this.value);
                            break;
                        }
                        case DECIMAL: {
                            result = this.reader.readDecimal(this.value);
                            break;
                        }
                        case GUID: {
                            result = this.reader.readGuid(this.value);
                            break;
                        }
                        case DATE_TIME: {
                            result = this.reader.readDateTime(this.value);
                            break;
                        }
                        case UNIX_DATE_TIME: {
                            result = this.reader.readUnixDateTime(this.value);
                            break;
                        }
                        case BINARY: {
                            result = this.reader.readBinary(this.value);
                            break;
                        }
                        case UTF_8: {
                            result = this.reader.readUtf8String(this.value);
                            break;
                        }
                        case NULLABLE_SCOPE:
                        case IMMUTABLE_NULLABLE_SCOPE: {
                            if (!this.reader.hasValue()) {
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

                            this.readers.push(this.reader);

                            this.paths.push(path.isEmpty()
                                ? Utf8String.transcodeUtf16(lenientFormat("[%s]", this.reader.index()))
                                : path);

                            this.reader = this.reader.readScope();
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
                        String message = lenientFormat("failed to read %s value for %s", type.layoutCode(), path);
                        throw new IllegalStateException(message);
                    }

                    this.dataItem = new DataItem(this.paths, path, type.layoutCode(), this.value.get());
                    return;
                }

                if (this.readers.empty()) {
                    this.reader = null;
                } else {
                    this.reader = this.readers.pop();
                    this.paths.pop();
                }
            }
            while (this.reader != null);
        }
    }
}
