// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using Newtonsoft.Json;

    public class StrictBooleanConverter : JsonConverter
    {
        public override bool CanWrite => false;

        public override bool CanConvert(Type objectType)
        {
            return objectType == typeof(bool);
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            switch (reader.TokenType)
            {
                case JsonToken.Boolean:
                    return serializer.Deserialize(reader, objectType);
                default:
                    throw new JsonSerializationException($"Token \"{reader.Value}\" of type {reader.TokenType} was not a JSON bool");
            }
        }

        [ExcludeFromCodeCoverage]
        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
        }
    }
}
