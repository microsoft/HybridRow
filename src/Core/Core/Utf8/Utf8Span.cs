// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1066 // Type {0} should implement IEquatable<T> because it overrides Equals

namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;
    using System.Diagnostics;
    using System.Runtime.CompilerServices;
    using System.Text;

    // ReSharper disable once UseNameofExpression
    [DebuggerDisplay("{ToString()}")]
    public readonly ref struct Utf8Span
    {
        public static Utf8Span Empty => default;

        private readonly ReadOnlySpan<byte> buffer;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private Utf8Span(ReadOnlySpan<byte> utf8Bytes)
        {
            this.buffer = utf8Bytes;
        }

        /// <summary>Parses the sequence of bytes to prove it is valid UTF8.</summary>
        /// <param name="utf8Bytes">The bytes to validate.</param>
        /// <param name="span">
        /// If the sequence validates a <see cref="Utf8Span" /> that wraps the bytes in
        /// <paramref name="utf8Bytes" />, otherwise <see cref="t:default" />.
        /// </param>
        /// <returns>True if the sequence validates, false otherwise.</returns>
        public static bool TryParseUtf8Bytes(ReadOnlySpan<byte> utf8Bytes, out Utf8Span span)
        {
            int invalidIndex = Utf8Util.GetIndexOfFirstInvalidUtf8Sequence(utf8Bytes, out int _, out int _);
            if (invalidIndex != -1)
            {
                span = default;
                return false;
            }

            span = new Utf8Span(utf8Bytes);
            return true;
        }

        /// <summary>Creates a <see cref="Utf8Span" /> without validating the underlying bytes.</summary>
        /// <param name="utf8Bytes">The bytes claiming to be UTF8.</param>
        /// <returns>A <see cref="Utf8Span" /> wrapping <paramref name="utf8Bytes" />.</returns>
        /// <remarks>
        /// This method is dangerous as consumers of the <see cref="Utf8Span" /> must assume the
        /// underlying bytes are indeed valid UTF8.  The method should <bold>only</bold> be used when the UTF8
        /// sequence has already been externally valid or is known to be valid by construction.
        /// </remarks>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static Utf8Span UnsafeFromUtf8BytesNoValidation(ReadOnlySpan<byte> utf8Bytes)
        {
            return new Utf8Span(utf8Bytes);
        }

        /// <summary>Creates a <see cref="Utf8Span" /> from a UTF16 encoding string.</summary>
        /// <param name="utf16String">The UTF16 encoding string.</param>
        /// <returns>A new <see cref="Utf8Span" />.</returns>
        /// <remarks>
        /// This method must transcode the UTF16 into UTF8 which both requires allocation and is a
        /// size of data operation.
        /// </remarks>
        public static Utf8Span TranscodeUtf16(string utf16String)
        {
            Contract.Requires(utf16String != null);

            if (string.IsNullOrEmpty(utf16String))
            {
                return new Utf8Span(ReadOnlySpan<byte>.Empty);
            }

            return new Utf8Span(Encoding.UTF8.GetBytes(utf16String));
        }

        /// <summary>The UTF8 byte sequence.</summary>
        public ReadOnlySpan<byte> Span
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.buffer;
        }

        /// <summary>The length in bytes of the UTF8 encoding.</summary>
        public int Length => this.Span.Length;

        /// <summary>True if the length is empty.</summary>
        public bool IsEmpty => this.Span.Length == 0;

        /// <summary>Non-allocating enumeration of each code point in the UTF8 stream.</summary>
        public Utf8CodePointEnumerator GetEnumerator()
        {
            return new Utf8CodePointEnumerator(this.buffer);
        }

        public override string ToString()
        {
            if (this.buffer.IsEmpty)
            {
                return string.Empty;
            }

            unsafe
            {
                // ReSharper disable once ImpureMethodCallOnReadonlyValueField
                fixed (byte* bytes = &this.buffer.GetPinnableReference())
                {
                    return Encoding.UTF8.GetString(bytes, this.buffer.Length);
                }
            }
        }

        public bool ReferenceEquals(Utf8Span other)
        {
            return this.buffer == other.buffer;
        }

        public bool Equals(Utf8Span other)
        {
            return this.buffer.SequenceEqual(other.buffer);
        }

        public bool Equals(string other)
        {
            Contract.Requires(other != null);

            Utf8CodePointEnumerator thisEnumerator = this.GetEnumerator();
            Utf16LittleEndianCodePointEnumerator otherEnumerator = new Utf16LittleEndianCodePointEnumerator(other);

            while (true)
            {
                bool hasNext = thisEnumerator.MoveNext();
                if (hasNext != otherEnumerator.MoveNext())
                {
                    return false;
                }

                if (!hasNext)
                {
                    return true;
                }

                if (thisEnumerator.Current != otherEnumerator.Current)
                {
                    return false;
                }
            }
        }

        public override bool Equals(object obj)
        {
            switch (obj)
            {
                case string s:
                    return this.Equals(s);
                case Utf8String s:
                    return this.Equals(s.Span);
                default:
                    return false;
            }
        }

        public override int GetHashCode()
        {
            unchecked
            {
                uint hash1 = 5381;
                uint hash2 = hash1;

                Utf8CodePointEnumerator thisEnumerator = this.GetEnumerator();
                for (int i = 0; thisEnumerator.MoveNext(); i++)
                {
                    uint c = thisEnumerator.Current;
                    if (i % 2 == 0)
                    {
                        hash1 = ((hash1 << 5) + hash1) ^ c;
                    }
                    else
                    {
                        hash2 = ((hash2 << 5) + hash2) ^ c;
                    }
                }

                return (int)(hash1 + (hash2 * 1566083941));
            }
        }

        public static bool operator ==(Utf8Span left, Utf8Span right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(Utf8Span left, Utf8Span right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(Utf8Span left, string right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(Utf8Span left, string right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(string left, Utf8Span right)
        {
            return right.Equals(left);
        }

        public static bool operator !=(string left, Utf8Span right)
        {
            return !right.Equals(left);
        }

        public static bool operator <(Utf8Span left, Utf8Span right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(Utf8Span left, Utf8Span right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(Utf8Span left, Utf8Span right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(Utf8Span left, Utf8Span right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(Utf8Span left, string right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(Utf8Span left, string right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(Utf8Span left, string right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(Utf8Span left, string right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(string left, Utf8Span right)
        {
            return right.CompareTo(left) >= 0;
        }

        public static bool operator <=(string left, Utf8Span right)
        {
            return right.CompareTo(left) > 0;
        }

        public static bool operator >(string left, Utf8Span right)
        {
            return right.CompareTo(left) <= 0;
        }

        public static bool operator >=(string left, Utf8Span right)
        {
            return right.CompareTo(left) < 0;
        }

        public int CompareTo(Utf8Span other)
        {
            ReadOnlySpan<byte> left = this.Span;
            ReadOnlySpan<byte> right = other.Span;
            int minLength = left.Length;
            if (minLength > right.Length)
            {
                minLength = right.Length;
            }

            for (int i = 0; i < minLength; i++)
            {
                int result = left[i].CompareTo(right[i]);
                if (result != 0)
                {
                    return result;
                }
            }

            return left.Length.CompareTo(right.Length);
        }

        public int CompareTo(string other)
        {
            Contract.Requires(other != null);

            Utf8CodePointEnumerator thisEnumerator = this.GetEnumerator();
            Utf16LittleEndianCodePointEnumerator otherEnumerator = new Utf16LittleEndianCodePointEnumerator(other);

            while (true)
            {
                bool thisHasNext = thisEnumerator.MoveNext();
                bool otherHasNext = otherEnumerator.MoveNext();
                if (!thisHasNext && !otherHasNext)
                {
                    return 0;
                }

                if (!thisHasNext)
                {
                    return -1;
                }

                if (!otherHasNext)
                {
                    return 1;
                }

                uint thisCurrent = thisEnumerator.Current;
                uint otherCurrent = otherEnumerator.Current;

                if (thisCurrent == otherCurrent)
                {
                    continue;
                }

                return thisCurrent.CompareTo(otherCurrent);
            }
        }

        /// <summary>
        /// Returns true if this <see cref="Utf8Span" /> starts with (or equals) the second.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> to compare.</param>
        /// <returns>If starts with.</returns>
        public bool StartsWith(Utf8Span pattern)
        {
            return this.Span.StartsWith(pattern.Span);
        }

        /// <summary>
        /// Returns true if this <see cref="Utf8Span" /> ends with (or equals) the second.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> to compare.</param>
        /// <returns>If starts with.</returns>
        public bool EndsWith(Utf8Span pattern)
        {
            return this.Span.EndsWith(pattern.Span);
        }

        /// <summary>
        /// Returns true if a specified <see cref="Utf8Span" /> occurs within this <see cref="Utf8Span" />.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> to compare.</param>
        /// <returns>If contains.</returns>
        public bool Contains(Utf8Span pattern)
        {
            return this.Span.IndexOf(pattern.Span) >= 0;
        }

        /// <summary>
        /// Splits a <see cref="Utf8Span" /> around first occurrence of <paramref name="pattern"/> into the left and right segments.
        /// The pattern is not included in either left or right results.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> split around.</param>
        /// <param name="left">The <see cref="Utf8Span" /> before the pattern.</param>
        /// <param name="right">The <see cref="Utf8Span" /> after the pattern.</param>
        /// <returns>True if success, false if does not contain pattern.</returns>
        public bool TrySplitFirst(Utf8Span pattern, out Utf8Span left, out Utf8Span right)
        {
            int indexOfPattern = this.Span.IndexOf(pattern.Span);

            if (indexOfPattern < 0)
            {
                left = default;
                right = default;
                return false;
            }

            left = new Utf8Span(this.Span.Slice(0, indexOfPattern));
            right = new Utf8Span(this.Span.Slice(indexOfPattern + pattern.Length));

            return true;
        }
    }
}
