//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.internal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * MurmurHash3 for x64 (Little Endian).
 * <p>Reference: https: //en.wikipedia.org/wiki/MurmurHash <br /></p>
 * <p>
 * This implementation provides span-based access for hashing content not available in a
 * <see cref="T:byte[]" />
 * </p>
 */
public final class MurmurHash3 {
    /** MurmurHash3 128-bit implementation.
     @param value The data to hash.
     @param seed The seed to initialize with.
     @return The 128-bit hash represented as two 64-bit words.
     */
    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    public static Value Hash128(String value, Value seed) {
        checkArgument(value != null);
        int size = Encoding.UTF8.GetMaxByteCount(value.length());
        Span<byte> span = size <= 256 ? stackalloc byte[size] :new byte[size];
        int len = Encoding.UTF8.GetBytes(value.AsSpan(), span);
        return MurmurHash3.Hash128(span.Slice(0, len), seed);
    }

    /** MurmurHash3 128-bit implementation.
     @param value The data to hash.
     @param seed The seed to initialize with.
     @return The 128-bit hash represented as two 64-bit words.
     */
    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    public static Value Hash128(boolean value, Value seed) {
        // Ensure that a bool is ALWAYS a single byte encoding with 1 for true and 0 for false.
        return MurmurHash3.Hash128((byte)(value ? 1 : 0), seed);
    }

    /** MurmurHash3 128-bit implementation.
     @param value The data to hash.
     @param seed The seed to initialize with.
     @return The 128-bit hash represented as two 64-bit words.
     */
    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	public static unsafe(ulong low, ulong high) Hash128<T>(T value, (ulong high, ulong low) seed) where T :
    //	unmanaged
    //		{
    //			ReadOnlySpan<T> span = new ReadOnlySpan<T>(&value, 1);
    //			return MurmurHash3.Hash128(MemoryMarshal.AsBytes(span), seed);
    //		}

    /**
     * MurmurHash3 128-bit implementation.
     *
     * @param span The data to hash.
     * @param seed The seed to initialize with.
     * @return The 128-bit hash represented as two 64-bit words.
     */
    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	public static unsafe(ulong low, ulong high) Hash128(ReadOnlySpan<byte> span, (ulong high, ulong low) seed)
    //		{
    //			Contract.Assert(BitConverter.IsLittleEndian, "Little Endian expected");
    //			const ulong c1 = 0x87c37b91114253d5;
    //			const ulong c2 = 0x4cf5ad432745937f;
    //
    //			(ulong h1, ulong h2) = seed;
    //
    //			// body
    //			unchecked
    //			{
    //				fixed (byte* words = span)
    //				{
    //					int position;
    //					for (position = 0; position < span.Length - 15; position += 16)
    //					{
    //						ulong k1 = *(ulong*)(words + position);
    //						ulong k2 = *(ulong*)(words + position + 8);
    //
    //						// k1, h1
    //						k1 *= c1;
    //						k1 = MurmurHash3.RotateLeft64(k1, 31);
    //						k1 *= c2;
    //
    //						h1 ^= k1;
    //						h1 = MurmurHash3.RotateLeft64(h1, 27);
    //						h1 += h2;
    //						h1 = (h1 * 5) + 0x52dce729;
    //
    //						// k2, h2
    //						k2 *= c2;
    //						k2 = MurmurHash3.RotateLeft64(k2, 33);
    //						k2 *= c1;
    //
    //						h2 ^= k2;
    //						h2 = MurmurHash3.RotateLeft64(h2, 31);
    //						h2 += h1;
    //						h2 = (h2 * 5) + 0x38495ab5;
    //					}
    //
    //					{
    //						// tail
    //						ulong k1 = 0;
    //						ulong k2 = 0;
    //
    //						int n = span.Length & 15;
    //						if (n >= 15)
    //						{
    //							k2 ^= (ulong)words[position + 14] << 48;
    //						}
    //
    //						if (n >= 14)
    //						{
    //							k2 ^= (ulong)words[position + 13] << 40;
    //						}
    //
    //						if (n >= 13)
    //						{
    //							k2 ^= (ulong)words[position + 12] << 32;
    //						}
    //
    //						if (n >= 12)
    //						{
    //							k2 ^= (ulong)words[position + 11] << 24;
    //						}
    //
    //						if (n >= 11)
    //						{
    //							k2 ^= (ulong)words[position + 10] << 16;
    //						}
    //
    //						if (n >= 10)
    //						{
    //							k2 ^= (ulong)words[position + 09] << 8;
    //						}
    //
    //						if (n >= 9)
    //						{
    //							k2 ^= (ulong)words[position + 08] << 0;
    //						}
    //
    //						k2 *= c2;
    //						k2 = MurmurHash3.RotateLeft64(k2, 33);
    //						k2 *= c1;
    //						h2 ^= k2;
    //
    //						if (n >= 8)
    //						{
    //							k1 ^= (ulong)words[position + 7] << 56;
    //						}
    //
    //						if (n >= 7)
    //						{
    //							k1 ^= (ulong)words[position + 6] << 48;
    //						}
    //
    //						if (n >= 6)
    //						{
    //							k1 ^= (ulong)words[position + 5] << 40;
    //						}
    //
    //						if (n >= 5)
    //						{
    //							k1 ^= (ulong)words[position + 4] << 32;
    //						}
    //
    //						if (n >= 4)
    //						{
    //							k1 ^= (ulong)words[position + 3] << 24;
    //						}
    //
    //						if (n >= 3)
    //						{
    //							k1 ^= (ulong)words[position + 2] << 16;
    //						}
    //
    //						if (n >= 2)
    //						{
    //							k1 ^= (ulong)words[position + 1] << 8;
    //						}
    //
    //						if (n >= 1)
    //						{
    //							k1 ^= (ulong)words[position + 0] << 0;
    //						}
    //
    //						k1 *= c1;
    //						k1 = MurmurHash3.RotateLeft64(k1, 31);
    //						k1 *= c2;
    //						h1 ^= k1;
    //					}
    //				}
    //
    //				// finalization
    //				h1 ^= (ulong)span.Length;
    //				h2 ^= (ulong)span.Length;
    //				h1 += h2;
    //				h2 += h1;
    //				h1 = MurmurHash3.Mix(h1);
    //				h2 = MurmurHash3.Mix(h2);
    //				h1 += h2;
    //				h2 += h1;
    //			}
    //
    //			return (h1, h2);
    //		}

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] private static ulong Mix(ulong h)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] private static ulong Mix(ulong h)
    private static long Mix(long h) {
        // TODO: C# TO JAVA CONVERTER: There is no equivalent to an 'unchecked' block in Java:
        //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
        // operator since the left operand was originally of an unsigned type, but you should confirm this
        // replacement:
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
        // operator since the left operand was originally of an unsigned type, but you should confirm this
        // replacement:
        h ^= h >>> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift
        // operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
        h ^= h >>> 33;
        return h;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] private static ulong RotateLeft64(ulong n,
    // int numBits)
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] private static ulong RotateLeft64(ulong n,
    // int numBits)
    private static long RotateLeft64(long n, int numBits) {
        checkArgument(numBits < 64, "numBits < 64");
        //C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift operator
        // since the left operand was originally of an unsigned type, but you should confirm this replacement:
        return (n << numBits) | (n >>> (64 - numBits));
    }

    public static final class Value {

        public final long low, high;

        public Value(long low, long high) {
            this.low = low;
            this.high = high;
        }
    }
}