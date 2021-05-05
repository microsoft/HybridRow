// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

@JsonSerialize(using = TypeArgumentList.JsonSerializer.class)
public final class TypeArgumentList {

    public static final TypeArgumentList EMPTY = new TypeArgumentList();

    private final TypeArgument[] args;
    private final SchemaId schemaId;

    /**
     * Initializes a new instance of the {@link TypeArgumentList} class.
     *
     * @param args arguments in the list.
     */
    public TypeArgumentList(@Nonnull final TypeArgument... args) {
        checkNotNull(args);
        this.args = args;
        this.schemaId = SchemaId.INVALID;
    }

    /**
     * Initializes a new instance of the {@link TypeArgumentList} class
     *
     * @param schemaId for UDT fields, the schema id of the nested layout
     */
    public TypeArgumentList(@Nonnull final SchemaId schemaId) {
        checkNotNull(schemaId);
        this.args = EMPTY.args;
        this.schemaId = schemaId;
    }

    private TypeArgumentList() {
        this.args = new TypeArgument[] {};
        this.schemaId = SchemaId.INVALID;
    }

    /**
     * Number of elements in this {@link TypeArgumentList}
     * <p>
     * @return number of arguments in the list
     */
    public int count() {
        return this.args.length;
    }

    public boolean equals(TypeArgumentList other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return this.schemaId().equals(other.schemaId()) && Arrays.equals(this.args, other.args);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TypeArgumentList && this.equals((TypeArgumentList) other);
    }

    /**
     * Element at the specified position in this {@link TypeArgumentList}
     * <p>
     * @param index index of the element to return
     * @return element at the specified position in this {@link TypeArgumentList}
     */
    public TypeArgument get(int index) {
        return this.args[index];
    }

    @Override
    public int hashCode() {

        int hash = 19;
        hash = (hash * 397) ^ this.schemaId().hashCode();

        for (TypeArgument a : this.args) {
            hash = (hash * 397) ^ a.hashCode();
        }

        return hash;
    }

    public List<TypeArgument> list() {
        return Collections.unmodifiableList(Arrays.asList(this.args));
    }

    /**
     * For UDT fields, the schema id of the nested layout.
     *
     * @return for UDT fields, the Schema ID of the nested layout.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * Stream for iterating over elements in this {@link TypeArgumentList}
     * <p>
     * @return a stream for iterating over elements in this {@link TypeArgumentList}
     */
    public Stream<TypeArgument> stream() {
        if (this.args.length == 0) {
            return Stream.empty();
        }
        return StreamSupport.stream(Arrays.spliterator(this.args), false);
    }

    @Override
    public String toString() {
        return Json.toString(this);
    }

    static class JsonSerializer extends StdSerializer<TypeArgumentList> {

        private JsonSerializer() {
            super(TypeArgumentList.class);
        }

        @Override
        public void serialize(TypeArgumentList value, JsonGenerator generator, SerializerProvider provider) throws IOException {

            generator.writeStartObject();
            generator.writeObjectField("schemaId", value.schemaId);
            generator.writeArrayFieldStart("args");

            for (TypeArgument element : value.args) {
                generator.writeString(element.toString());
            }

            generator.writeEndArray();
        }
    }
}