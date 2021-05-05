// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import Newtonsoft.Json.*;

import java.math.BigInteger;

public class StrictIntegerConverter extends JsonConverter {
    @Override
    public boolean getCanWrite() {
        return false;
    }

    @Override
    public boolean CanConvert(java.lang.Class objectType) {
        return StrictIntegerConverter.IsIntegerType(objectType);
    }

    @Override
    public Object ReadJson(JsonReader reader, java.lang.Class objectType, Object existingValue,
                           JsonSerializer serializer) {
        switch (reader.TokenType) {
            case JsonToken.Integer:
                return serializer.Deserialize(reader, objectType);
            default:
                throw new JsonSerializationException(String.format("Token \"%1$s\" of type %2$s was not a JSON " +
                    "integer", reader.Value, reader.TokenType));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [ExcludeFromCodeCoverage] public override void WriteJson(JsonWriter writer, object value,
    // JsonSerializer serializer)
    @Override
    public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
    }

    private static boolean IsIntegerType(java.lang.Class type) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: if (type == typeof(long) || type == typeof(ulong) || type == typeof(int) || type == typeof
        // (uint) || type == typeof(short) || type == typeof(ushort) || type == typeof(byte) || type == typeof(sbyte)
        // || type == typeof(BigInteger))
        return type == Long.class || type == Long.class || type == Integer.class || type == Integer.class || type == Short.class || type == Short.class || type == Byte.class || type == Byte.class || type == BigInteger.class;
    }
}