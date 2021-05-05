// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import Newtonsoft.Json.*;

public class StrictBooleanConverter extends JsonConverter {
    @Override
    public boolean getCanWrite() {
        return false;
    }

    @Override
    public boolean CanConvert(java.lang.Class objectType) {
        return objectType == Boolean.class;
    }

    @Override
    public Object ReadJson(JsonReader reader, java.lang.Class objectType, Object existingValue,
                           JsonSerializer serializer) {
        switch (reader.TokenType) {
            case JsonToken.Boolean:
                return serializer.Deserialize(reader, objectType);
            default:
                throw new JsonSerializationException(String.format("Token \"%1$s\" of type %2$s was not a JSON bool",
                    reader.Value, reader.TokenType));
        }
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [ExcludeFromCodeCoverage] public override void WriteJson(JsonWriter writer, object value,
    // JsonSerializer serializer)
    @Override
    public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
    }
}