// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Numerics;
    using Newtonsoft.Json;

    internal sealed class StrictIntegerConverter : JsonConverter
    {
        public override bool CanWrite => false;

        public override bool CanConvert(Type objectType)
        {
            return StrictIntegerConverter.IsIntegerType(objectType);
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            switch (reader.TokenType)
            {
                case JsonToken.Integer:
                    return serializer.Deserialize(reader, objectType);
                default:
                    throw new JsonSerializationException($"Token \"{reader.Value}\" of type {reader.TokenType} was not a JSON integer");
            }
        }

        [ExcludeFromCodeCoverage]
        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
        }

        private static bool IsIntegerType(Type type)
        {
            if (type == typeof(long) ||
                type == typeof(ulong) ||
                type == typeof(int) ||
                type == typeof(uint) ||
                type == typeof(short) ||
                type == typeof(ushort) ||
                type == typeof(byte) ||
                type == typeof(sbyte) ||
                type == typeof(BigInteger))
            {
                return true;
            }

            return false;
        }
    }
}
