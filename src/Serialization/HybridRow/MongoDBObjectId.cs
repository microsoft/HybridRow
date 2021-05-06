// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable IDE0041 // Use 'is null' check

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Core;

    /// <summary>A 12-byte MongoDB Object Identifier (in big-endian byte order).</summary>
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public unsafe struct MongoDbObjectId : IEquatable<MongoDbObjectId>
    {
        /// <summary>The size (in bytes) of a MongoObjectId.</summary>
        public const int Size = 12;

        /// <summary>The object id bytes inlined.</summary>
        private fixed byte data[MongoDbObjectId.Size];

        /// <summary>Initializes a new instance of the <see cref="MongoDbObjectId" /> struct.</summary>
        /// <param name="high">the high-order 32-bits.</param>
        /// <param name="low">the low-order 64-bits.</param>
        public MongoDbObjectId(uint high, ulong low)
        {
            Contract.Assert(BitConverter.IsLittleEndian);

            fixed (byte* p = this.data)
            {
                *(uint*)&p[0] = MongoDbObjectId.SwapByteOrder(high);
                *(ulong*)&p[4] = MongoDbObjectId.SwapByteOrder(low);
            }
        }

        /// <summary>Initializes a new instance of the <see cref="MongoDbObjectId" /> struct.</summary>
        /// <param name="src">the bytes of the object id in big-endian order.</param>
        public MongoDbObjectId(ReadOnlySpan<byte> src)
        {
            Contract.Requires(src.Length == MongoDbObjectId.Size);

            fixed (byte* p = this.data)
            {
                fixed (byte* q = src)
                {
                    *(ulong*)&p[0] = *(ulong*)&q[0];
                    *(uint*)&p[8] = *(uint*)&q[8];
                }
            }
        }

        /// <summary>Operator == overload.</summary>
        public static bool operator ==(MongoDbObjectId left, MongoDbObjectId right)
        {
            return left.Equals(right);
        }

        /// <summary>Operator != overload.</summary>
        public static bool operator !=(MongoDbObjectId left, MongoDbObjectId right)
        {
            return !left.Equals(right);
        }

        /// <summary>Returns true if this is the same value as <see cref="other" />.</summary>
        /// <param name="other">The value to compare against.</param>
        /// <returns>True if the two values are the same.</returns>
        public bool Equals(MongoDbObjectId other)
        {
            fixed (byte* p = this.data)
            {
                byte* q = other.data;
                if (*(ulong*)&q[0] != *(ulong*)&p[0] || *(uint*)&q[8] != *(uint*)&p[8])
                {
                    return false;
                }
            }

            return true;
        }

        /// <summary><see cref="object.Equals(object)" /> overload.</summary>
        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            return obj is MongoDbObjectId id && this.Equals(id);
        }

        /// <summary><see cref="object.GetHashCode" /> overload.</summary>
        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = 0;
                fixed (byte* p = this.data)
                {
                    hashCode = (hashCode * 397) ^ *(int*)&p[0];
                    hashCode = (hashCode * 397) ^ *(int*)&p[4];
                    hashCode = (hashCode * 397) ^ *(int*)&p[8];
                }

                return hashCode;
            }
        }

        /// <summary>Returns the bytes of the object id as a byte array in big-endian order.</summary>
        public byte[] ToByteArray()
        {
            byte[] bytes = new byte[MongoDbObjectId.Size];
            this.CopyTo(bytes);
            return bytes;
        }

        /// <summary>Copies the bytes of the object id to the provided buffer.</summary>
        /// <param name="dest">A buffer to receive the bytes in big-endian order.</param>
        /// <remarks>
        /// Required: The buffer must be able to accomodate the full object id (see
        /// <see cref="Size" />) at the offset indicated.
        /// </remarks>
        public void CopyTo(Span<byte> dest)
        {
            Contract.Requires(dest.Length == MongoDbObjectId.Size);

            fixed (byte* p = this.data)
            {
                Span<byte> source = new Span<byte>(p, MongoDbObjectId.Size);
                source.CopyTo(dest);
            }
        }

        private static uint SwapByteOrder(uint value)
        {
            return ((value & 0x000000FFU) << 24) |
                   ((value & 0x0000FF00U) << 8) |
                   ((value & 0x00FF0000U) >> 8) |
                   ((value & 0xFF000000U) >> 24);
        }

        // reverse byte order (64-bit)
        private static ulong SwapByteOrder(ulong value)
        {
            return ((value & 0x00000000000000FFUL) << 56) |
                   ((value & 0x000000000000FF00UL) << 40) |
                   ((value & 0x0000000000FF0000UL) << 24) |
                   ((value & 0x00000000FF000000UL) << 8) |
                   ((value & 0x000000FF00000000UL) >> 8) |
                   ((value & 0x0000FF0000000000UL) >> 24) |
                   ((value & 0x00FF000000000000UL) >> 40) |
                   ((value & 0xFF00000000000000UL) >> 56);
        }
    }
}
