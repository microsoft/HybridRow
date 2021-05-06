// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable IDE0041 // Use 'is null' check
#pragma warning disable IDE0070 // Use 'System.HashCode'

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Core;
    using Newtonsoft.Json;

    /// <summary>The unique identifier for a schema.</summary>
    /// <remarks>Identifiers must be unique within the scope of the database in which they are used.</remarks>
    [JsonConverter(typeof(SchemaIdConverter))]
    [DebuggerDisplay("{" + nameof(SchemaId.Id) + "}")]
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public readonly struct SchemaId : IEquatable<SchemaId>
    {
        public const int Size = sizeof(int);
        public static readonly SchemaId Invalid = default;

        /// <summary>Initializes a new instance of the <see cref="SchemaId" /> struct.</summary>
        /// <param name="id">The underlying globally unique identifier of the schema.</param>
        public SchemaId(int id)
        {
            this.Id = id;
        }

        /// <summary>The underlying identifier.</summary>
        public int Id { get; }

        /// <summary>Integer conversion operator.</summary>
        [SuppressMessage("Usage", "CA2225:Operator overloads have named alternates", Justification = "Constructor")]
        public static explicit operator SchemaId(int id)
        {
            return new SchemaId(id);
        }

        /// <summary>Integer conversion operator.</summary>
        [SuppressMessage("Usage", "CA2225:Operator overloads have named alternates", Justification = "Id property")]
        public static explicit operator int(SchemaId id)
        {
            return id.Id;
        }

        /// <summary>Operator == overload.</summary>
        public static bool operator ==(SchemaId left, SchemaId right)
        {
            return left.Equals(right);
        }

        /// <summary>Operator != overload.</summary>
        public static bool operator !=(SchemaId left, SchemaId right)
        {
            return !left.Equals(right);
        }

        /// <summary><see cref="object.Equals(object)" /> overload.</summary>
        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            return obj is SchemaId id && this.Equals(id);
        }

        /// <summary><see cref="object.GetHashCode" /> overload.</summary>
        public override int GetHashCode()
        {
            return this.Id.GetHashCode();
        }

        /// <summary>Returns true if this is the same <see cref="SchemaId" /> as <see cref="other" />.</summary>
        /// <param name="other">The value to compare against.</param>
        /// <returns>True if the two values are the same.</returns>
        public bool Equals(SchemaId other)
        {
            return this.Id == other.Id;
        }

        /// <summary><see cref="object.ToString" /> overload.</summary>
        public override string ToString()
        {
            return this.Id.ToString();
        }

        /// <summary>Helper class for parsing <see cref="SchemaId" /> from JSON.</summary>
        internal class SchemaIdConverter : JsonConverter
        {
            public override bool CanWrite => true;

            public override bool CanConvert(Type objectType)
            {
                return typeof(SchemaId).IsAssignableFrom(objectType);
            }

            public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
            {
                Contract.Requires(reader.TokenType == JsonToken.Integer);
                return new SchemaId(checked((int)(long)reader.Value));
            }

            public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
            {
                writer.WriteValue((long)((SchemaId)value).Id);
            }
        }
    }
}
