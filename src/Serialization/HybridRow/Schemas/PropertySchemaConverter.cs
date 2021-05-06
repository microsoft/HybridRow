// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;
    using Newtonsoft.Json.Linq;

    /// <summary>Helper class for parsing the polymorphic <see cref="PropertyType" /> subclasses from JSON.</summary>
    internal sealed class PropertySchemaConverter : JsonConverter
    {
        public override bool CanWrite => false;

        public override bool CanConvert(Type objectType)
        {
            return typeof(PropertyType).IsAssignableFrom(objectType);
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            PropertyType p;
            if (reader.TokenType != JsonToken.StartObject)
            {
                throw new JsonSerializationException();
            }

            JObject propSchema = JObject.Load(reader);
            TypeKind propType;

            if (!propSchema.TryGetValue("type", out JToken value))
            {
                throw new JsonSerializationException("Required \"type\" property missing.");
            }

            using (JsonReader typeReader = value.CreateReader())
            {
                typeReader.Read(); // Move to the start token
                propType = (TypeKind)new StringEnumConverter(true).ReadJson(typeReader, typeof(TypeKind), null, serializer);
            }

            switch (propType)
            {
                case TypeKind.Array:
                    p = new ArrayPropertyType();
                    break;
                case TypeKind.Set:
                    p = new SetPropertyType();
                    break;
                case TypeKind.Map:
                    p = new MapPropertyType();
                    break;
                case TypeKind.Object:
                    p = new ObjectPropertyType();
                    break;
                case TypeKind.Tuple:
                    p = new TuplePropertyType();
                    break;
                case TypeKind.Tagged:
                    p = new TaggedPropertyType();
                    break;
                case TypeKind.Schema:
                    p = new UdtPropertyType();
                    break;
                default:
                    p = new PrimitivePropertyType();
                    break;
            }

            serializer.Populate(propSchema.CreateReader(), p);
            return p;
        }

        [ExcludeFromCodeCoverage]
        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
        }
    }
}
