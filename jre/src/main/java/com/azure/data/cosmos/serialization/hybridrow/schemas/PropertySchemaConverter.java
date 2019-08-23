//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import Newtonsoft.Json.*;
import Newtonsoft.Json.Converters.*;
import Newtonsoft.Json.Linq.*;

/**
 * Helper class for parsing the polymorphic <see cref="PropertyType" /> subclasses from JSON.
 */
public class PropertySchemaConverter extends JsonConverter {
    @Override
    public boolean getCanWrite() {
        return false;
    }

    @Override
    public boolean CanConvert(java.lang.Class objectType) {
        return objectType.isAssignableFrom(PropertyType.class);
    }

    @Override
    public Object ReadJson(JsonReader reader, java.lang.Class objectType, Object existingValue,
                           JsonSerializer serializer) {
        PropertyType p;
        if (reader.TokenType != JsonToken.StartObject) {
            throw new JsonSerializationException();
        }

        JObject propSchema = JObject.Load(reader);
        TypeKind propType;

        JToken value;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (!propSchema.TryGetValue("type", out value)) {
            throw new JsonSerializationException("Required \"type\" property missing.");
        }

        try (JsonReader typeReader = value.CreateReader()) {
            typeReader.Read(); // Move to the start token
            propType = TypeKind.forValue((new StringEnumConverter(true)).ReadJson(typeReader, TypeKind.class, null,
                serializer));
        }

        switch (propType) {
            case Array:
                p = new ArrayPropertyType();
                break;
            case Set:
                p = new SetPropertyType();
                break;
            case Map:
                p = new MapPropertyType();
                break;
            case Object:
                p = new ObjectPropertyType();
                break;
            case Tuple:
                p = new TuplePropertyType();
                break;
            case Tagged:
                p = new TaggedPropertyType();
                break;
            case Schema:
                p = new UdtPropertyType();
                break;
            default:
                p = new PrimitivePropertyType();
                break;
        }

        serializer.Populate(propSchema.CreateReader(), p);
        return p;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [ExcludeFromCodeCoverage] public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
    @Override
    public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
    }
}