// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static com.google.common.base.Strings.lenientFormat;

public final class Json {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writer();

    private Json() {
    }

    public static String toString(Object object) {
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException error) {
            return lenientFormat("{\"error\": \"%s\"}", error);
        }
    }
}
