//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

import Newtonsoft.Json.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The unique identifier for a schema.
 * Identifiers must be unique within the scope of the database in which they are used.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [JsonConverter(typeof(SchemaIdConverter))][DebuggerDisplay("{" + nameof(SchemaId.Id) + "}")
// ][StructLayout(LayoutKind.Sequential, Pack = 1)] public readonly struct SchemaId : IEquatable<SchemaId>
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [JsonConverter(typeof(SchemaIdConverter))][DebuggerDisplay("{" + nameof(SchemaId.Id) + "}")
// ][StructLayout(LayoutKind.Sequential, Pack = 1)] public readonly struct SchemaId : IEquatable<SchemaId>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class SchemaId implements IEquatable<SchemaId> {
    public static final SchemaId Invalid = null;
    public static final int Size = (Integer.SIZE / Byte.SIZE);
    /**
     * The underlying identifier.
     */
    private int Id;

    /**
     * Initializes a new instance of the {@link SchemaId} struct.
     *
     * @param id The underlying globally unique identifier of the schema.
     */
    public SchemaId() {
    }

    public SchemaId(int id) {
        this.Id = id;
    }

    public int getId() {
        return Id;
    }

    /**
     * {@link object.Equals(object)} overload.
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        return obj instanceof SchemaId && this.equals((SchemaId)obj);
    }

    /**
     * Returns true if this is the same {@link SchemaId} as {@link other}.
     *
     * @param other The value to compare against.
     * @return True if the two values are the same.
     */
    public boolean equals(SchemaId other) {
        return this.getId() == other.getId();
    }

    /**
     * {@link object.GetHashCode} overload.
     */
    @Override
    public int hashCode() {
        return (new Integer(this.getId())).hashCode();
    }

    /**
     * Operator == overload.
     */
    public static boolean opEquals(SchemaId left, SchemaId right) {
        return left.equals(right.clone());
    }

    /**
     * Operator != overload.
     */
    public static boolean opNotEquals(SchemaId left, SchemaId right) {
        return !left.equals(right.clone());
    }

    /**
     * {@link object.ToString} overload.
     */
    @Override
    public String toString() {
        return String.valueOf(this.getId());
    }

    /**
     * Helper class for parsing {@link SchemaId} from JSON.
     */
    public static class SchemaIdConverter extends JsonConverter {
        @Override
        public boolean getCanWrite() {
            return true;
        }

        @Override
        public boolean CanConvert(java.lang.Class objectType) {
            return objectType.isAssignableFrom(SchemaId.class);
        }

        @Override
        public Object ReadJson(JsonReader reader, java.lang.Class objectType, Object existingValue,
                               JsonSerializer serializer) {
            checkArgument(reader.TokenType == JsonToken.Integer);
            // TODO: C# TO JAVA CONVERTER: There is no Java equivalent to 'checked' in this context:
            //ORIGINAL LINE: return new SchemaId(checked((int)(long)reader.Value));
            return new SchemaId((int)(long)reader.Value);
        }

        @Override
        public void WriteJson(JsonWriter writer, Object value, JsonSerializer serializer) {
            writer.WriteValue((long)((SchemaId)value).getId());
        }
    }
}