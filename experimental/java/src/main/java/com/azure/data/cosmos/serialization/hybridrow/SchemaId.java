// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.google.common.base.Strings.lenientFormat;
import static it.unimi.dsi.fastutil.HashCommon.mix;

/**
 * The unique identifier for a schema.
 * <p>
 * Identifiers must be unique within the scope of the database in which they are used.
 */
@JsonDeserialize(using = SchemaId.JsonDeserializer.class)
@JsonSerialize(using = SchemaId.JsonSerializer.class)
public final class SchemaId implements Comparable<SchemaId> {

    public static final int BYTES = Integer.BYTES;
    public static final SchemaId INVALID;
    public static final SchemaId NONE;

    private static final Int2ReferenceMap<SchemaId> cache;

    static {
        cache = new Int2ReferenceOpenHashMap<>();
        cache.put(0, INVALID = NONE = new SchemaId(0));
    }

    private final int value;

    /**
     * Initializes a new instance of the {@link SchemaId} class.
     *
     * @param value The underlying globally unique identifier of the schema.
     */
    private SchemaId(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(@Nonnull SchemaId other) {
        return Integer.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        SchemaId schemaId = (SchemaId) other;
        return this.value == schemaId.value;
    }

    /**
     * {@code true} if this is the same {@link SchemaId} as {@code other}.
     *
     * @param other The value to compare against.
     * @return {@code true} if the two values are the same.
     */
    public boolean equals(SchemaId other) {
        if (null == other) {
            return false;
        }
        return this.value() == other.value();
    }

    /**
     * Returns a {@link SchemaId} with the given underlying integer value.
     *
     * @param value an integer.
     * @return a {@link SchemaId} with the given underlying integer {@code value}.
     */
    public static SchemaId from(int value) {
        return cache.computeIfAbsent(value, SchemaId::new);
    }

    /**
     * Returns the hash code value for this {@link SchemaId}.
     * <p>
     * This method mixes the bits of the underlying {@code int} value of the {@link SchemaId} by multiplying by the
     * golden ratio and xor-shifting the result. It has slightly worse behavior than MurmurHash3. In open-addressing
     * In open-addressing tables the average number of probes is slightly larger, but the computation of the value
     * is faster.
     *
     * @return the hash code value for this {@link SchemaId}.
     * @see HashCommon#mix(int)
     * @see <a href="https://github.com/OpenHFT/Koloboke">Koloboke</a>
     * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
     */
    @Override
    public int hashCode() {
        return mix(this.value);
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

    /**
     * The underlying integer value of this {@link SchemaId}.
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
        public SchemaId deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {

            final int value = parser.getIntValue();

            if (value == 0) {
                String message = "expected non-zero int value for SchemaId";
                throw MismatchedInputException.from(parser, SchemaId.class, message);
            }

            return SchemaId.from(value);
        }
    }

    static final class JsonSerializer extends StdSerializer<SchemaId> {

        private JsonSerializer() {
            super(SchemaId.class);
        }

        @Override
        public void serialize(
            final SchemaId value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
            generator.writeNumber(value.value());
        }
    }
}
