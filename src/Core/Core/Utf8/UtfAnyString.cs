// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA2225 // Operator overloads have named alternates

// ReSharper disable once UseNameofExpression
namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;
    using System.Diagnostics;
    using System.Runtime.CompilerServices;

    /// <summary>A string whose memory representation may be either UTF8 or UTF16.</summary>
    /// <remarks>
    /// This type supports polymorphic use of <see cref="string" /> and <see cref="Utf8String" />
    /// when equality, hashing, and comparison are needed against either encoding.  An API leveraging
    /// <see cref="UtfAnyString" /> can avoid separate method overloads while still accepting either
    /// encoding without imposing additional allocations.
    /// </remarks>
    [DebuggerDisplay("{ToString()}")]
    public readonly struct UtfAnyString :
        IEquatable<UtfAnyString>, IComparable<UtfAnyString>,
        IEquatable<Utf8String>, IComparable<Utf8String>,
        IEquatable<string>, IComparable<string>
    {
        public static UtfAnyString Empty => string.Empty;

        private readonly object buffer;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public UtfAnyString(string utf16String)
        {
            this.buffer = utf16String;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public UtfAnyString(Utf8String utf8String)
        {
            this.buffer = utf8String;
        }

        public bool IsUtf8 => this.buffer is Utf8String;

        public bool IsUtf16 => this.buffer is string;

        /// <summary>True if the length is empty.</summary>
        public bool IsNull => object.ReferenceEquals(null, this.buffer);

        /// <summary>True if the length is empty.</summary>
        public bool IsEmpty
        {
            get
            {
                if (object.ReferenceEquals(null, this.buffer))
                {
                    return false;
                }

                switch (this.buffer)
                {
                    case string s:
                        return s.Length == 0;
                    default:
                        return ((Utf8String)this.buffer).IsEmpty;
                }
            }
        }

        public static implicit operator UtfAnyString(string utf16String)
        {
            return new UtfAnyString(utf16String);
        }

        public static implicit operator string(UtfAnyString str)
        {
            return str.buffer?.ToString();
        }

        public override string ToString()
        {
            // ReSharper disable once AssignNullToNotNullAttribute
            return this.buffer?.ToString();
        }

        public Utf8String ToUtf8String()
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return null;
            }

            switch (this.buffer)
            {
                case string s:
                    return Utf8String.TranscodeUtf16(s);
                default:
                    return (Utf8String)this.buffer;
            }
        }

        public bool ReferenceEquals(UtfAnyString other)
        {
            return this.buffer == other.buffer;
        }

        public bool Equals(UtfAnyString other)
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return object.ReferenceEquals(null, other.buffer);
            }

            switch (this.buffer)
            {
                case string s:
                    return other.Equals(s);
                default:
                    return other.Equals((Utf8String)this.buffer);
            }
        }

        public bool Equals(Utf8Span other)
        {
            return other.Equals(this.buffer);
        }

        public override bool Equals(object obj)
        {
            switch (obj)
            {
                case string s:
                    return this.Equals(s);
                case Utf8String s:
                    return this.Equals(s);
                case UtfAnyString s:
                    return this.Equals(s);
            }

            return false;
        }

        public bool Equals(Utf8String other)
        {
            if (object.ReferenceEquals(null, other))
            {
                return object.ReferenceEquals(null, this.buffer);
            }

            return other.Equals(this.buffer);
        }

        public bool Equals(string other)
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return object.ReferenceEquals(null, other);
            }

            switch (this.buffer)
            {
                case string s:
                    return string.Equals(s, other, StringComparison.Ordinal);
                default:
                    return ((Utf8String)this.buffer).Equals(other);
            }
        }

        public static bool operator ==(UtfAnyString left, UtfAnyString right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(UtfAnyString left, UtfAnyString right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(UtfAnyString left, string right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(UtfAnyString left, string right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(string left, UtfAnyString right)
        {
            return right.Equals(left);
        }

        public static bool operator !=(string left, UtfAnyString right)
        {
            return !right.Equals(left);
        }

        public static bool operator ==(UtfAnyString left, Utf8String right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(UtfAnyString left, Utf8String right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(Utf8String left, UtfAnyString right)
        {
            return right.Equals(left);
        }

        public static bool operator !=(Utf8String left, UtfAnyString right)
        {
            return !right.Equals(left);
        }

        public static bool operator ==(UtfAnyString left, Utf8Span right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(UtfAnyString left, Utf8Span right)
        {
            return !left.Equals(right);
        }

        public static bool operator ==(Utf8Span left, UtfAnyString right)
        {
            return right.Equals(left);
        }

        public static bool operator !=(Utf8Span left, UtfAnyString right)
        {
            return !right.Equals(left);
        }

        public override int GetHashCode()
        {
            uint hash1 = 5381;
            uint hash2 = hash1;

            if (object.ReferenceEquals(null, this.buffer))
            {
                return unchecked((int)(hash1 + (hash2 * 1566083941)));
            }

            switch (this.buffer)
            {
                case string s:
                    unchecked
                    {
                        Utf16LittleEndianCodePointEnumerator thisEnumerator = new Utf16LittleEndianCodePointEnumerator(s);
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

                default:
                    return this.buffer.GetHashCode();
            }
        }

        public static bool operator <(UtfAnyString left, UtfAnyString right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(UtfAnyString left, UtfAnyString right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(UtfAnyString left, UtfAnyString right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(UtfAnyString left, UtfAnyString right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(UtfAnyString left, string right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(UtfAnyString left, string right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(UtfAnyString left, string right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(UtfAnyString left, string right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(string left, UtfAnyString right)
        {
            return right.CompareTo(left) >= 0;
        }

        public static bool operator <=(string left, UtfAnyString right)
        {
            return right.CompareTo(left) > 0;
        }

        public static bool operator >(string left, UtfAnyString right)
        {
            return right.CompareTo(left) <= 0;
        }

        public static bool operator >=(string left, UtfAnyString right)
        {
            return right.CompareTo(left) < 0;
        }

        public static bool operator <(UtfAnyString left, Utf8String right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(UtfAnyString left, Utf8String right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(UtfAnyString left, Utf8String right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(UtfAnyString left, Utf8String right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(Utf8String left, UtfAnyString right)
        {
            return right.CompareTo(left) >= 0;
        }

        public static bool operator <=(Utf8String left, UtfAnyString right)
        {
            return right.CompareTo(left) > 0;
        }

        public static bool operator >(Utf8String left, UtfAnyString right)
        {
            return right.CompareTo(left) <= 0;
        }

        public static bool operator >=(Utf8String left, UtfAnyString right)
        {
            return right.CompareTo(left) < 0;
        }

        public static bool operator <(UtfAnyString left, Utf8Span right)
        {
            return left.CompareTo(right) < 0;
        }

        public static bool operator <=(UtfAnyString left, Utf8Span right)
        {
            return left.CompareTo(right) <= 0;
        }

        public static bool operator >(UtfAnyString left, Utf8Span right)
        {
            return left.CompareTo(right) > 0;
        }

        public static bool operator >=(UtfAnyString left, Utf8Span right)
        {
            return left.CompareTo(right) >= 0;
        }

        public static bool operator <(Utf8Span left, UtfAnyString right)
        {
            return right.CompareTo(left) >= 0;
        }

        public static bool operator <=(Utf8Span left, UtfAnyString right)
        {
            return right.CompareTo(left) > 0;
        }

        public static bool operator >(Utf8Span left, UtfAnyString right)
        {
            return right.CompareTo(left) <= 0;
        }

        public static bool operator >=(Utf8Span left, UtfAnyString right)
        {
            return right.CompareTo(left) < 0;
        }

        public int CompareTo(UtfAnyString other)
        {
            if (object.ReferenceEquals(null, other.buffer))
            {
                return object.ReferenceEquals(null, this.buffer) ? 0 : 1;
            }

            switch (other.buffer)
            {
                case string s:
                    return this.CompareTo(s);
                default:
                    return this.CompareTo((Utf8String)other.buffer);
            }
        }

        public int CompareTo(Utf8String other)
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return object.ReferenceEquals(null, other) ? 0 : -1;
            }

            switch (this.buffer)
            {
                case string s:
                    return -other.Span.CompareTo(s);
                default:
                    return -other.Span.CompareTo((Utf8String)this.buffer);
            }
        }

        public int CompareTo(Utf8Span other)
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return -1;
            }

            switch (this.buffer)
            {
                case string s:
                    return -other.CompareTo(s);
                default:
                    return -other.CompareTo((Utf8String)this.buffer);
            }
        }

        public int CompareTo(string other)
        {
            if (object.ReferenceEquals(null, this.buffer))
            {
                return object.ReferenceEquals(null, other) ? 0 : -1;
            }

            switch (this.buffer)
            {
                case string s:
                    return string.Compare(s, other, StringComparison.Ordinal);
                default:
                    return ((Utf8String)this.buffer).CompareTo(other);
            }
        }
    }
}
