// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1066 // Type {0} should implement IEquatable<T> because it overrides Equals
#pragma warning disable CA2225 // Operator overloads have named alternates
#pragma warning disable IDE0041 // Use 'is null' check

namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;
    using System.Diagnostics;
    using System.Runtime.CompilerServices;
    using System.Text;

    // ReSharper disable once UseNameofExpression
    [DebuggerDisplay("{ToString()}")]
    public class Utf8String : IEquatable<Utf8String>, IComparable<Utf8String>, IEquatable<string>, IComparable<string>
    {
        private readonly ReadOnlyMemory<byte> buffer;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private Utf8String(ReadOnlyMemory<byte> utf8Bytes)
        {
            this.buffer = utf8Bytes;
        }

        public static readonly Utf8String Empty = new Utf8String(default);

        /// <summary>The UTF8 byte sequence.</summary>
        public Utf8Span Span
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Span);
        }

        /// <summary>The length in bytes of the UTF8 encoding.</summary>
        public int Length => this.buffer.Length;

        /// <summary>True if the length is empty.</summary>
        public bool IsEmpty => this.buffer.Length == 0;

        /// <summary>Parses the sequence of bytes to prove it is valid UTF8.</summary>
        /// <param name="utf8Bytes">The bytes to validate.</param>
        /// <param name="str">
        /// If the sequence validates a <see cref="Utf8String" /> that wraps the bytes in
        /// <paramref name="utf8Bytes" />, otherwise <see cref="t:default" />.
        /// </param>
        /// <remarks>The new <see cref="Utf8String"/> takes ownership of <paramref name="utf8Bytes"/>.</remarks>
        /// <returns>True if the sequence validates, false otherwise.</returns>
        public static bool TryParseUtf8Bytes(ReadOnlyMemory<byte> utf8Bytes, out Utf8String str)
        {
            int invalidIndex = Utf8Util.GetIndexOfFirstInvalidUtf8Sequence(utf8Bytes.Span, out int _, out int _);
            if (invalidIndex != -1)
            {
                str = default;
                return false;
            }

            str = new Utf8String(utf8Bytes);
            return true;
        }

        /// <summary>Creates a <see cref="Utf8String" /> without validating the underlying bytes.</summary>
        /// <param name="utf8Bytes">The bytes claiming to be UTF8.</param>
        /// <returns>A <see cref="Utf8String" /> wrapping <paramref name="utf8Bytes" />.</returns>
        /// <remarks>
        /// This method is dangerous as consumers of the <see cref="Utf8String" /> must assume the
        /// underlying bytes are indeed valid UTF8.  The method should <bold>only</bold> be used when the UTF8
        /// sequence has already been externally valid or is known to be valid by construction.
        /// </remarks>
        /// <remarks>The new <see cref="Utf8String"/> takes ownership of <paramref name="utf8Bytes"/>.</remarks>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static Utf8String UnsafeFromUtf8BytesNoValidation(ReadOnlyMemory<byte> utf8Bytes)
        {
            Contract.Assert(Utf8Util.GetIndexOfFirstInvalidUtf8Sequence(utf8Bytes.Span, out int _, out int _) == -1);
            return new Utf8String(utf8Bytes);
        }

        /// <summary>Creates a <see cref="Utf8String" /> from a <see cref="Utf8Span"/>.</summary>
        /// <param name="span">The bytes that are UTF8.</param>
        /// <returns>A <see cref="Utf8String" /> with contents from <paramref name="span" />.</returns>
        public static Utf8String CopyFrom(Utf8Span span)
        {
            Contract.Assert(Utf8Util.GetIndexOfFirstInvalidUtf8Sequence(span.Span, out int _, out int _) == -1);
            return new Utf8String(span.Span.ToArray());
        }

        /// <summary>Creates a <see cref="Utf8String" /> from a UTF16 encoding string.</summary>
        /// <param name="utf16String">The UTF16 encoding string.</param>
        /// <returns>A new <see cref="Utf8String" />.</returns>
        /// <remarks>
        /// This method must transcode the UTF16 into UTF8 which both requires allocation and is a
        /// size of data operation.
        /// </remarks>
        public static Utf8String TranscodeUtf16(string utf16String)
        {
            if (object.ReferenceEquals(utf16String, null))
            {
                return null;
            }

            if (string.IsNullOrEmpty(utf16String))
            {
                return new Utf8String(ReadOnlyMemory<byte>.Empty);
            }

            return new Utf8String(Encoding.UTF8.GetBytes(utf16String));
        }

        /// <summary><see cref="Utf8Span" /> over the string's content.</summary>
        /// <param name="utf8String">The string whose content is returned.</param>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static implicit operator Utf8Span(Utf8String utf8String)
        {
            return (utf8String == null) ? default : utf8String.Span;
        }

        public static implicit operator UtfAnyString(Utf8String utf8String)
        {
            return new UtfAnyString(utf8String);
        }

        public static bool operator ==(Utf8String left, Utf8String right)
        {
            if (object.ReferenceEquals(null, left))
            {
                return object.ReferenceEquals(null, right);
            }

            return left.Equals(right);
        }

        public static bool operator !=(Utf8String left, Utf8String right)
        {
            if (object.ReferenceEquals(null, left))
            {
                return !object.ReferenceEquals(null, right);
            }

            return !left.Equals(right);
        }

        public static bool operator <(Utf8String left, Utf8String right)
        {
            return object.ReferenceEquals(left, null) ? !object.ReferenceEquals(right, null) : left.CompareTo(right) < 0;
        }

        public static bool operator <=(Utf8String left, Utf8String right)
        {
            return object.ReferenceEquals(left, null) || left.CompareTo(right) <= 0;
        }

        public static bool operator >(Utf8String left, Utf8String right)
        {
            return !object.ReferenceEquals(left, null) && left.CompareTo(right) > 0;
        }

        public static bool operator >=(Utf8String left, Utf8String right)
        {
            return object.ReferenceEquals(left, null) ? object.ReferenceEquals(right, null) : left.CompareTo(right) >= 0;
        }

        public static bool operator <(Utf8String left, string right)
        {
            return object.ReferenceEquals(left, null) ? !object.ReferenceEquals(right, null) : left.CompareTo(right) < 0;
        }

        public static bool operator <=(Utf8String left, string right)
        {
            return object.ReferenceEquals(left, null) || left.CompareTo(right) <= 0;
        }

        public static bool operator >(Utf8String left, string right)
        {
            return !object.ReferenceEquals(left, null) && left.CompareTo(right) > 0;
        }

        public static bool operator >=(Utf8String left, string right)
        {
            return object.ReferenceEquals(left, null) ? object.ReferenceEquals(right, null) : left.CompareTo(right) >= 0;
        }

        public static bool operator <(string left, Utf8String right)
        {
            return object.ReferenceEquals(right, null) ? object.ReferenceEquals(left, null) : right.CompareTo(left) >= 0;
        }

        public static bool operator <=(string left, Utf8String right)
        {
            return !object.ReferenceEquals(right, null) && right.CompareTo(left) > 0;
        }

        public static bool operator >(string left, Utf8String right)
        {
            return object.ReferenceEquals(right, null) || right.CompareTo(left) <= 0;
        }

        public static bool operator >=(string left, Utf8String right)
        {
            return object.ReferenceEquals(right, null) ? !object.ReferenceEquals(left, null) : right.CompareTo(left) < 0;
        }

        /// <summary>Non-allocating enumeration of each code point in the UTF8 stream.</summary>
        public Utf8CodePointEnumerator GetEnumerator()
        {
            return this.Span.GetEnumerator();
        }

        public override string ToString()
        {
            return this.Span.ToString();
        }

        public bool Equals(Utf8Span other)
        {
            return this.Span.Equals(other);
        }

        public bool Equals(Utf8String other)
        {
            if (object.ReferenceEquals(null, other))
            {
                return false;
            }

            if (object.ReferenceEquals(this, other))
            {
                return true;
            }

            return this.Span.Equals(other.Span);
        }

        public bool Equals(string other)
        {
            if (object.ReferenceEquals(null, other))
            {
                return false;
            }

            return this.Span.Equals(other);
        }

        public override bool Equals(object other)
        {
            if (object.ReferenceEquals(null, other))
            {
                return false;
            }

            if (object.ReferenceEquals(this, other))
            {
                return true;
            }

            return this.Span.Equals(other);
        }

        public override int GetHashCode()
        {
            return this.Span.GetHashCode();
        }

        public int CompareTo(Utf8String other)
        {
            if (other == null)
            {
                return 1;
            }

            return this.Span.CompareTo(other.Span);
        }

        public int CompareTo(string other)
        {
            if (other == null)
            {
                return 1;
            }

            return this.Span.CompareTo(other);
        }

        /// <summary>
        /// Splits a <see cref="Utf8String" /> around first occurrence of <paramref name="pattern"/> into the left and right segments.
        /// The pattern is not included in either left or right results.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> split around.</param>
        /// <param name="left">The <see cref="Utf8String" /> before the pattern.</param>
        /// <param name="right">The <see cref="Utf8String" /> after the pattern.</param>
        /// <returns>True if success, false if does not contain pattern.</returns>
        public bool TrySplitFirst(Utf8Span pattern, out Utf8String left, out Utf8String right)
        {
            int indexOfPattern = this.buffer.Span.IndexOf(pattern.Span);

            if (indexOfPattern < 0)
            {
                left = default;
                right = default;
                return false;
            }

            left = new Utf8String(this.buffer.Slice(0, indexOfPattern));
            right = new Utf8String(this.buffer.Slice(indexOfPattern + pattern.Length));

            return true;
        }

        /// <summary>
        /// Removes given <see cref="Utf8Span" /> from start and outputs resultant <see cref="Utf8String" />.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> to remove.</param>
        /// <param name="output">The <see cref="Utf8String" /> with value removed from start.</param>
        /// <returns>Is success.</returns>
        public bool TryTrimLeft(Utf8Span pattern, out Utf8String output)
        {
            if (!this.Span.StartsWith(pattern))
            {
                output = default;
                return false;
            }

            output = new Utf8String(this.buffer.Slice(pattern.Length));

            return true;
        }

        /// <summary>
        /// Removes given <see cref="Utf8Span" /> from end and outputs resultant <see cref="Utf8String" />.
        /// </summary>
        /// <param name="pattern">The <see cref="Utf8Span" /> to remove.</param>
        /// <param name="output">The <see cref="Utf8String" /> with value removed from end.</param>
        /// <returns>Is success.</returns>
        public bool TryTrimRight(Utf8Span pattern, out Utf8String output)
        {
            if (!this.Span.EndsWith(pattern))
            {
                output = default;
                return false;
            }

            output = new Utf8String(this.buffer.Slice(0, this.buffer.Length - pattern.Length));

            return true;
        }

        /// <summary>
        /// Removes given <paramref name="leftPattern"/> from the start and <paramref name="rightPattern"/>
        /// from the end and outputs resultant <see cref="Utf8String" />.
        /// </summary>
        /// <param name="leftPattern">The <see cref="Utf8Span" /> to remove from left.</param>
        /// <param name="rightPattern">The <see cref="Utf8Span" /> to remove from right.</param>
        /// <param name="output">The <see cref="Utf8String" /> with value removed from end.</param>
        /// <remarks>Will return false if patterns overlap in string to trim.</remarks>
        /// <returns>Is success.</returns>
        public bool TryTrim(Utf8Span leftPattern, Utf8Span rightPattern, out Utf8String output)
        {
            if (!this.Span.StartsWith(leftPattern) ||
                !this.Span.EndsWith(rightPattern) ||
                (this.buffer.Length < leftPattern.Length + rightPattern.Length))
            {
                output = default;
                return false;
            }

            output = new Utf8String(this.buffer.Slice(leftPattern.Length, this.buffer.Length - leftPattern.Length - rightPattern.Length));

            return true;
        }

        /// <summary>
        /// Indicates whether the specified <see cref="Utf8String"/> is null or an empty.
        /// </summary>
        /// <param name="utf8String"></param>
        /// <returns>If null or empty.</returns>
        public static bool IsNullOrEmpty(Utf8String utf8String)
        {
            return utf8String == null || utf8String.IsEmpty;
        }
    }
}
