// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

import java.io.IOException;

import static com.google.common.base.Strings.lenientFormat;

/**
 * The unique identifier for a schema
 * Identifiers must be unique within the scope of the database in which they are used.
 */
@JsonDeserialize(using = SchemaId.JsonDeserializer.class)
@JsonSerialize(using = SchemaId.JsonSerializer.class)
public final class SchemaId {

    public static final SchemaId INVALID = null;
    public static final SchemaId NONE = new SchemaId(-1);
    public static final int SIZE = Integer.SIZE / Byte.SIZE;

    private static final long MAX_VALUE = 0x00000000FFFFFFFFL;
    private static final Int2ReferenceMap<SchemaId> cache = new Int2ReferenceOpenHashMap<>();

    private final int value;

    /**
     * Initializes a new instance of the {@link SchemaId} struct.
     *
     * @param value The underlying globally unique identifier of the schema.
     */
    private SchemaId(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SchemaId && this.equals((SchemaId) other);
    }

    /**
     * {@code true} if this is the same {@link SchemaId} as {@code other}
     *
     * @param other The value to compare against.
     * @return True if the two values are the same.
     */
    public boolean equals(SchemaId other) {
        if (null == other) {
            return false;
        }
        return this.value() == other.value();
    }

    /**
     * Returns a {@link SchemaId} from a specified underlying integer value
     *
     * @return The integer value of this {@link SchemaId}
     */
    public static SchemaId from(int value) {
        return cache.computeIfAbsent(value, SchemaId::new);
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.value()).hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(this.value());
    }

    /**
     * The underlying integer value of this {@link SchemaId}
     *
     * @return The integer value of this {@link SchemaId}
     */
    public int value() {
        return this.value;
    }

    static final class JsonDeserializer extends StdDeserializer<SchemaId> {

        private JsonDeserializer() {
            super(SchemaId.class);
        }

        @Override
        public SchemaId deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {

            final long value = parser.getLongValue();

            if (value < 0 || value > MAX_VALUE) {
                String message = lenientFormat("expected integer value in [0, 4294967295], not %s", value);
                throw MismatchedInputException.from(parser, SchemaId.class, message);
            }

            return new SchemaId((int) value);
        }
    }

    static final class JsonSerializer extends StdSerializer<SchemaId> {

        private JsonSerializer() {
            super(SchemaId.class);
        }

        @Override
        public void serialize(final SchemaId value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
            generator.writeNumber((long) value.value() & MAX_VALUE);
        }
    }
}