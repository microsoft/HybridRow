//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.internal;

import Newtonsoft.Json.*;

/**
 * Helper class for parsing <see cref="Utf8String" /> from JSON.
 */
public class Utf8StringJsonConverter extends JsonConverter {
    @Override
    public boolean getCanWrite() {
        return true;
    }

    @Override
    public boolean CanConvert(java.lang.Class objectType) {
        return objectType.isAssignableFrom(Utf8String.class);
    }

    @Override
    public Object ReadJson(JsonReader reader, java.lang.Class objectType, Object existingValue,
                           JsonSerializer serializer) {
        Contract.Requires(reader.TokenType == JsonToken.String);
        return Utf8String.TranscodeUtf16((String)reader.Value);
    }

    @Override
    public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
        writer.WriteValue(((Utf8String)value).toString());
    }
}