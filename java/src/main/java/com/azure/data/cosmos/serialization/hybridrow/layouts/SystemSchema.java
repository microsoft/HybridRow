// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.google.common.base.Suppliers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Strings.lenientFormat;

public final class SystemSchema {

    public static final String specificationTitle = "HybridRow serialization library";

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

    private static final Supplier<LayoutResolver> layoutResolver = Suppliers.memoize(() -> {

        final Optional<Namespace> namespace;

        try (final InputStream stream = getResourceAsStream("SystemSchema.json")) {
            namespace = Namespace.parse(stream);
        } catch (IOException cause) {
            String message = lenientFormat("failed to initialize %s due to %s", cause);
            throw new IllegalStateException(message, cause);
        }

        return new LayoutResolverNamespace(namespace.orElseThrow(() -> {
            String message = lenientFormat("failed to initialize %s due to system schema parse error");
            return new IllegalStateException(message);
        }));
    });

    private SystemSchema() {
    }

    public static LayoutResolver layoutResolver() {
        return layoutResolver.get();
    }

    private static InputStream getResourceAsStream(final String name) throws IOException {
        return SystemSchema.class.getClassLoader().getResourceAsStream(name);
    }
}
