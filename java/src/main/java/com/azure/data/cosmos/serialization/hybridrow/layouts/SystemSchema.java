// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

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

    public static final LayoutResolver layoutResolver = SystemSchema.loadSchema();

    private SystemSchema() {
    }

    /*
    private static String FormatResourceName(Assembly assembly, String resourceName) {
        return assembly.GetName().Name + "." + resourceName.replace(" ", "_").replace("\\", ".").replace("/", ".");
    }
    */

    static LayoutResolver loadSchema() {

        final String json;

        try {
            json = SystemSchema.readFromResourceFile("system-schema.json");
        } catch (IOException cause) {
            throw new IllegalStateException("failed to load system-schema.json", cause);
        }

        Optional<Namespace> ns = Namespace.parse(json);
        checkState(ns.isPresent(), "failed to load system-schema.json");

        return new LayoutResolverNamespace(ns.get());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private static String readFromResourceFile(String name) throws IOException {
        try (final InputStream stream = SystemSchema.class.getResourceAsStream(name)) {
            ByteBuf buffer = Unpooled.buffer();
            while (buffer.writeBytes(stream, 8192) == 8192) {
            }
            return buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8).toString();
        }
    }
}
