// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.google.common.base.Suppliers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

public final class SystemSchema {

    /**
     * SchemaId of the empty schema. This schema has no defined cells but can accomodate
     * unschematized sparse content.
     */
    public static final SchemaId EMPTY_SCHEMA_ID = SchemaId.from(2147473650);

    /**
     * SchemaId of HybridRow RecordIO Record Headers.
     */
    public static final SchemaId RECORD_SCHEMA_ID = SchemaId.from(2147473649);

    /**
     * SchemaId of HybridRow RecordIO Segments.
     */
    public static final SchemaId SEGMENT_SCHEMA_ID = SchemaId.from(2147473648);

    @SuppressWarnings("StatementWithEmptyBody")
    private static final Supplier<LayoutResolver> layoutResolver = Suppliers.memoize(() -> {

        final String json;

        try (final InputStream stream = SystemSchema.class.getResourceAsStream("SystemSchema.json")) {
            ByteBuf buffer = Unpooled.buffer();
            while (buffer.writeBytes(stream, 8192) == 8192) { }
            json = buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8).toString();
        } catch (IOException cause) {
            String message = lenientFormat("failed to load SystemSchema.json due to %s", cause);
            throw new IllegalStateException(message, cause);
        }

        Optional<Namespace> namespace = Namespace.parse(json);
        checkState(namespace.isPresent(), "failed to load SystemSchema.json");

        return new LayoutResolverNamespace(namespace.get());
    });

    private SystemSchema() {
    }

    public static LayoutResolver layoutResolver() {
        return layoutResolver.get();
    }
}
