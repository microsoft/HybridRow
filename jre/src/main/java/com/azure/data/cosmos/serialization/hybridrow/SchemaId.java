//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

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

import java.io.IOException;

import static com.google.common.base.Strings.lenientFormat;

/**
 * The unique identifier for a schema
 * Identifiers must be unique within the scope of the database in which they are used.
 */
@JsonDeserialize(using = SchemaId.JsonDeserializer.class)
@JsonSerialize(using = SchemaId.JsonSerializer.class)
public final class SchemaId {

    // TODO: DANOBLE: Consider caching SchemaId instances to reduce memory footprint

    public static final SchemaId INVALID = null;
    public static final SchemaId NONE = new SchemaId(-1);
    public static final int SIZE = (Integer.SIZE / Byte.SIZE);

    private static long MAX_VALUE = 0x00000000FFFFFFFFL;
    private final int id;

    /**
     * Initializes a new instance of the {@link SchemaId} struct.
     *
     * @param id The underlying globally unique identifier of the schema.
     */
    private SchemaId(int id) {
        this.id = id;
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
        return this.id() == other.id();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.id()).hashCode();
    }

    /**
     * The underlying integer value of this {@link SchemaId}
     *
     * @return The integer value of this {@link SchemaId}
     */
    public int id() {
        return this.id;
    }

    /**
     * Returns a {@link SchemaId} from a specified underlying integer value
     *
     * @return The integer value of this {@link SchemaId}
     */
    public static SchemaId from(int id) {
        return new SchemaId(id);
    }

    @Override
    public String toString() {
        return String.valueOf(this.id());
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
            generator.writeNumber((long) value.id() & MAX_VALUE);
        }
    }
}