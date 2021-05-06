// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Core;

    public class RandomGenerator
    {
        private readonly Random root;

        public RandomGenerator(Random root)
        {
            this.root = root;
        }

        /// <summary>Returns a uniformly distributed 8-bit signed integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public sbyte NextInt8(sbyte min = sbyte.MinValue, sbyte max = sbyte.MaxValue)
        {
            Contract.Requires(min <= max);

            unchecked
            {
                sbyte result = (sbyte)(this.NextUInt8(0, (byte)(max - min)) + min);
                return result;
            }
        }

        /// <summary>Returns a uniformly distributed 16-bit signed integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public short NextInt16(short min = short.MinValue, short max = short.MaxValue)
        {
            Contract.Requires(min <= max);

            unchecked
            {
                short result = (short)(this.NextUInt16(0, (ushort)(max - min)) + min);
                return result;
            }
        }

        /// <summary>Returns a uniformly distributed 32-bit signed integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public int NextInt32(int min = int.MinValue, int max = int.MaxValue)
        {
            Contract.Requires(min <= max);

            unchecked
            {
                int result = (int)(this.NextUInt32(0, (uint)(max - min)) + min);
                return result;
            }
        }

        /// <summary>Returns a uniformly distributed 64-bit signed integer.</summary>
        public long NextInt64()
        {
            unchecked
            {
                long result = (long)this.NextUInt64();
                return result;
            }
        }

        /// <summary>Returns a uniformly distributed 8-bit unsigned integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public byte NextUInt8(byte min = byte.MinValue, byte max = byte.MaxValue)
        {
            Contract.Requires(min <= max);

            ulong result = this.NextUInt64();
            unchecked
            {
                result = (result % (((ulong)max - (ulong)min) + 1)) + (ulong)min;
                return (byte)result;
            }
        }

        /// <summary>Returns a uniformly distributed 16-bit unsigned integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public ushort NextUInt16(ushort min = ushort.MinValue, ushort max = ushort.MaxValue)
        {
            Contract.Requires(min <= max);

            ulong result = this.NextUInt64();
            unchecked
            {
                result = (result % (((ulong)max - (ulong)min) + 1)) + (ulong)min;
                return (ushort)result;
            }
        }

        /// <summary>Returns a uniformly distributed 32-bit unsigned integer in the range specified.</summary>
        /// <param name="min">The inclusive lower bound of the random number returned.</param>
        /// <param name="max">The inclusive upper bound of the random number returned.</param>
        /// <remarks>Requires <paramref name="min" /> &lt; <paramref name="max" />.</remarks>
        public uint NextUInt32(uint min = uint.MinValue, uint max = uint.MaxValue)
        {
            Contract.Requires(min <= max);

            ulong result = this.NextUInt64();
            unchecked
            {
                result = (result % (((ulong)max - (ulong)min) + 1)) + (ulong)min;
                return (uint)result;
            }
        }

        /// <summary>Returns a uniformly distributed 64-bit unsigned integer.</summary>
        public ulong NextUInt64()
        {
            Span<ulong> result = stackalloc ulong[1];
            this.root.NextBytes(MemoryMarshal.Cast<ulong, byte>(result));
            return result[0];
        }
    }
}
