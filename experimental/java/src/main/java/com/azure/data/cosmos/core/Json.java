// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.google.common.base.Strings.lenientFormat;

public final class Json {

    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    private static final ObjectMapper mapper = new ObjectMapper(new JsonFactory()
        .enable(JsonParser.Feature.ALLOW_COMMENTS));

    private static final ObjectReader reader = mapper.reader()
        .withFeatures(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

    private static final ObjectWriter writer = mapper.writer()
        .withFeatures(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

    private Json() {
    }

    public static <T> Optional<T> parse(File file, Class<T> type) {
        try {
            return Optional.of(reader.forType(type).readValue(file));
        } catch (IOException error) {
            logger.error("failed to parse {} due to ", type.getName(), error);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> parse(InputStream stream, Class<T> type) {
        try {
            return Optional.of(reader.forType(type).readValue(stream));
        } catch (IOException error) {
            logger.error("failed to parse {} due to ", type.getName(), error);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> parse(String value, Class<T> type) {
        try {
            return Optional.of(reader.forType(type).readValue(value));
        } catch (IOException error) {
            logger.error("", error);
            return Optional.empty();
        }
    }

    public static String toString(Object object) {
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException error) {
            return lenientFormat("{\"error\": \"%s\"}", error);
        }
    }
}
