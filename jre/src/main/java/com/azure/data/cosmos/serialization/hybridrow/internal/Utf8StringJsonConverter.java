//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.internal;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.netty.buffer.ByteBuf;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper class for parsing <see cref="Utf8String" /> from JSON.
 */
public class Utf8StringJsonConverter extends StdSerializer<ByteBuf> {

    public Utf8StringJsonConverter() {
        this(ByteBuf.class);
    }

    public Utf8StringJsonConverter(Class<ByteBuf> type) {
        super(type);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean CanConvert(Class<?> type) {
        return type.isAssignableFrom(ByteBuf);
    }

    @Override
    public Object ReadJson(
        JsonReader reader, java.lang.Class objectType, Object existingValue, JsonSerializer serializer) {
        checkArgument(reader.TokenType == JsonToken.String);
        return Utf8String.TranscodeUtf16((String)reader.Value);
    }

    @Override
    public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
        writer.WriteValue(((Utf8String)value).toString());
    }
}