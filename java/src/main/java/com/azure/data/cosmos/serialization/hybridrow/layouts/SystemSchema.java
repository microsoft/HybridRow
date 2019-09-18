// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.google.common.base.Suppliers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.Enumeration;
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

        final Optional<Namespace> namespace;

        try (final InputStream stream = getResourceAsStream("SystemSchema.json")) {

            namespace = Namespace.parse(stream);

        } catch (IOException cause) {
            String message = lenientFormat("Failed to load SystemSchema.json due to %s", cause);
            throw new IllegalStateException(message, cause);
        }

        return new LayoutResolverNamespace(namespace.get());
    });

    private SystemSchema() {
    }

    public static LayoutResolver layoutResolver() {
        return layoutResolver.get();
    }

    private static InputStream getResourceAsStream(final String name) throws IOException {

        final CodeSource codeSource = SystemSchema.class.getProtectionDomain().getCodeSource();
        final ClassLoader classLoader = SystemSchema.class.getClassLoader();
        final String location = codeSource.getLocation().toString();
        final Enumeration<URL> urls;

        urls = classLoader.getResources(name);

        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            if (url.toString().startsWith(location)) {
                return url.openStream();
            }
        }

        throw new FileNotFoundException(lenientFormat("cannot find resource at code source location %s", location));
    }
}
