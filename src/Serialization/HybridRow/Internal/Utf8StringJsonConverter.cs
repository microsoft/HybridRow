namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Internal
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Newtonsoft.Json;

    /// <summary>Helper class for parsing <see cref="Utf8String" /> from JSON.</summary>
    internal class Utf8StringJsonConverter : JsonConverter
    {
        public override bool CanWrite => true;

        public override bool CanConvert(Type objectType)
        {
            return typeof(Utf8String).IsAssignableFrom(objectType);
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            Contract.Requires(reader.TokenType == JsonToken.String);
            return Utf8String.TranscodeUtf16((string)reader.Value);
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            writer.WriteValue(((Utf8String)value).ToString());
        }
    }
}
