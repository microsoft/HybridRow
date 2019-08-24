package Microsoft.Azure.Cosmos.Core.Utf8;

// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable CA2225 // Operator overloads have named alternates

// ReSharper disable once UseNameofExpression

/** A string whose memory representation may be either UTF8 or UTF16.
 
 This type supports polymorphic use of <see cref="string" /> and <see cref="Utf8String" />
 when equality, hashing, and comparison are needed against either encoding.  An API leveraging
 <see cref="UtfAnyString" /> can avoid separate method overloads while still accepting either
 encoding without imposing additional allocations.
 
*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{ToString()}")] public readonly struct UtfAnyString : IEquatable<UtfAnyString>, IComparable<UtfAnyString>, IEquatable<Utf8String>, IComparable<Utf8String>, IEquatable<string>, IComparable<string>
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ from the original:
//ORIGINAL LINE: [DebuggerDisplay("{ToString()}")] public readonly struct UtfAnyString : IEquatable<UtfAnyString>, IComparable<UtfAnyString>, IEquatable<Utf8String>, IComparable<Utf8String>, IEquatable<string>, IComparable<string>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class UtfAnyString implements IEquatable<UtfAnyString>, java.lang.Comparable<UtfAnyString>, IEquatable<Utf8String>, java.lang.Comparable<Utf8String>, IEquatable<String>, java.lang.Comparable<String>
{
	public static UtfAnyString getEmpty()
	{
		return "";
	}

	private Object buffer;

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public UtfAnyString(string utf16String)
	public UtfAnyString()
	{
	}

	public UtfAnyString(String utf16String)
	{
		this.buffer = utf16String;
	}

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] public UtfAnyString(Utf8String utf8String)
	public UtfAnyString(Utf8String utf8String)
	{
		this.buffer = utf8String;
	}

	public boolean getIsUtf8()
	{
		return this.buffer instanceof Utf8String;
	}

	public boolean getIsUtf16()
	{
		return this.buffer instanceof String;
	}

	/** True if the length is empty.
	*/
	public boolean getIsNull()
	{
		return null == this.buffer;
	}

	/** True if the length is empty.
	*/
	public boolean getIsEmpty()
	{
		if (null == this.buffer)
		{
			return false;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return s.Length == 0;
			default:
				return ((Utf8String)this.buffer).getIsEmpty();
		}
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit operator UtfAnyString(String utf16String)
	{
		return new UtfAnyString(utf16String);
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit operator string(UtfAnyString str)
	{
		return str.buffer == null ? null : str.buffer.toString();
	}

	@Override
	public String toString()
	{
		// ReSharper disable once AssignNullToNotNullAttribute
		return this.buffer == null ? null : this.buffer.toString();
	}

	public Utf8String ToUtf8String()
	{
		if (null == this.buffer)
		{
			return null;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return Utf8String.TranscodeUtf16(s);
			default:
				return (Utf8String)this.buffer;
		}
	}

	public boolean ReferenceEquals(UtfAnyString other)
	{
		return this.buffer == other.buffer;
	}

	public boolean equals(UtfAnyString other)
	{
		if (null == this.buffer)
		{
			return null == other.buffer;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return other.equals(s);
			default:
				return other.equals((Utf8String)this.buffer);
		}
	}

	public boolean equals(Utf8Span other)
	{
		return other.equals(this.buffer);
	}

	@Override
	public boolean equals(Object obj)
	{
		switch (obj)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return this.equals(s);
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case Utf8String s:
			case Utf8String s:
				return this.equals(s);
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case UtfAnyString s:
			case UtfAnyString s:
				return this.equals(s);
		}

		return false;
	}

	public boolean equals(Utf8String other)
	{
		if (null == other)
		{
			return null == this.buffer;
		}

		return other.equals(this.buffer);
	}

	public boolean equals(String other)
	{
		if (null == this.buffer)
		{
			return null == other;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return String.equals(s, other);
			default:
				return ((Utf8String)this.buffer).equals(other);
		}
	}

	public static boolean opEquals(UtfAnyString left, UtfAnyString right)
	{
		return left.equals(right.clone());
	}

	public static boolean opNotEquals(UtfAnyString left, UtfAnyString right)
	{
		return !left.equals(right.clone());
	}

	public static boolean opEquals(UtfAnyString left, String right)
	{
		return left.equals(right);
	}

	public static boolean opNotEquals(UtfAnyString left, String right)
	{
		return !left.equals(right);
	}

	public static boolean opEquals(String left, UtfAnyString right)
	{
		return right.equals(left);
	}

	public static boolean opNotEquals(String left, UtfAnyString right)
	{
		return !right.equals(left);
	}

	public static boolean opEquals(UtfAnyString left, Utf8String right)
	{
		return left.equals(right);
	}

	public static boolean opNotEquals(UtfAnyString left, Utf8String right)
	{
		return !left.equals(right);
	}

	public static boolean opEquals(Utf8String left, UtfAnyString right)
	{
		return right.equals(left);
	}

	public static boolean opNotEquals(Utf8String left, UtfAnyString right)
	{
		return !right.equals(left);
	}

	public static boolean opEquals(UtfAnyString left, Utf8Span right)
	{
		return left.equals(right);
	}

	public static boolean opNotEquals(UtfAnyString left, Utf8Span right)
	{
		return !left.equals(right);
	}

	public static boolean opEquals(Utf8Span left, UtfAnyString right)
	{
		return right.equals(left);
	}

	public static boolean opNotEquals(Utf8Span left, UtfAnyString right)
	{
		return !right.equals(left);
	}

	@Override
	public int hashCode()
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint hash1 = 5381;
		int hash1 = 5381;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint hash2 = hash1;
		int hash2 = hash1;

		if (null == this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'unchecked' in this context:
//ORIGINAL LINE: return unchecked((int)(hash1 + (hash2 * 1566083941)));
			return (int)(hash1 + (hash2 * 1566083941));
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to an 'unchecked' block in Java:
				unchecked
				{
					Utf16LittleEndianCodePointEnumerator thisEnumerator = new Utf16LittleEndianCodePointEnumerator(s);
					for (int i = 0; thisEnumerator.MoveNext(); i++)
					{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint c = thisEnumerator.Current;
						int c = thisEnumerator.Current;
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
				return this.buffer.hashCode();
		}
	}

	public static boolean opLessThan(UtfAnyString left, UtfAnyString right)
	{
		return left.CompareTo(right.clone()) < 0;
	}

	public static boolean opLessThanOrEquals(UtfAnyString left, UtfAnyString right)
	{
		return left.CompareTo(right.clone()) <= 0;
	}

	public static boolean opGreaterThan(UtfAnyString left, UtfAnyString right)
	{
		return left.CompareTo(right.clone()) > 0;
	}

	public static boolean opGreaterThanOrEquals(UtfAnyString left, UtfAnyString right)
	{
		return left.CompareTo(right.clone()) >= 0;
	}

	public static boolean opLessThan(UtfAnyString left, String right)
	{
		return left.CompareTo(right) < 0;
	}

	public static boolean opLessThanOrEquals(UtfAnyString left, String right)
	{
		return left.CompareTo(right) <= 0;
	}

	public static boolean opGreaterThan(UtfAnyString left, String right)
	{
		return left.CompareTo(right) > 0;
	}

	public static boolean opGreaterThanOrEquals(UtfAnyString left, String right)
	{
		return left.CompareTo(right) >= 0;
	}

	public static boolean opLessThan(String left, UtfAnyString right)
	{
		return right.CompareTo(left) >= 0;
	}

	public static boolean opLessThanOrEquals(String left, UtfAnyString right)
	{
		return right.CompareTo(left) > 0;
	}

	public static boolean opGreaterThan(String left, UtfAnyString right)
	{
		return right.CompareTo(left) <= 0;
	}

	public static boolean opGreaterThanOrEquals(String left, UtfAnyString right)
	{
		return right.CompareTo(left) < 0;
	}

	public static boolean opLessThan(UtfAnyString left, Utf8String right)
	{
		return left.CompareTo(right) < 0;
	}

	public static boolean opLessThanOrEquals(UtfAnyString left, Utf8String right)
	{
		return left.CompareTo(right) <= 0;
	}

	public static boolean opGreaterThan(UtfAnyString left, Utf8String right)
	{
		return left.CompareTo(right) > 0;
	}

	public static boolean opGreaterThanOrEquals(UtfAnyString left, Utf8String right)
	{
		return left.CompareTo(right) >= 0;
	}

	public static boolean opLessThan(Utf8String left, UtfAnyString right)
	{
		return right.CompareTo(left) >= 0;
	}

	public static boolean opLessThanOrEquals(Utf8String left, UtfAnyString right)
	{
		return right.CompareTo(left) > 0;
	}

	public static boolean opGreaterThan(Utf8String left, UtfAnyString right)
	{
		return right.CompareTo(left) <= 0;
	}

	public static boolean opGreaterThanOrEquals(Utf8String left, UtfAnyString right)
	{
		return right.CompareTo(left) < 0;
	}

	public static boolean opLessThan(UtfAnyString left, Utf8Span right)
	{
		return left.CompareTo(right) < 0;
	}

	public static boolean opLessThanOrEquals(UtfAnyString left, Utf8Span right)
	{
		return left.CompareTo(right) <= 0;
	}

	public static boolean opGreaterThan(UtfAnyString left, Utf8Span right)
	{
		return left.CompareTo(right) > 0;
	}

	public static boolean opGreaterThanOrEquals(UtfAnyString left, Utf8Span right)
	{
		return left.CompareTo(right) >= 0;
	}

	public static boolean opLessThan(Utf8Span left, UtfAnyString right)
	{
		return right.CompareTo(left) >= 0;
	}

	public static boolean opLessThanOrEquals(Utf8Span left, UtfAnyString right)
	{
		return right.CompareTo(left) > 0;
	}

	public static boolean opGreaterThan(Utf8Span left, UtfAnyString right)
	{
		return right.CompareTo(left) <= 0;
	}

	public static boolean opGreaterThanOrEquals(Utf8Span left, UtfAnyString right)
	{
		return right.CompareTo(left) < 0;
	}

	public int CompareTo(UtfAnyString other)
	{
		if (null == other.buffer)
		{
			return null == this.buffer ? 0 : 1;
		}

		switch (other.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return this.CompareTo(s);
			default:
				return this.CompareTo((Utf8String)other.buffer);
		}
	}

	public int CompareTo(Utf8String other)
	{
		if (null == this.buffer)
		{
			return null == other ? 0 : -1;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return -other.getSpan().CompareTo(s);
			default:
				return -other.getSpan().CompareTo((Utf8String)this.buffer);
		}
	}

	public int CompareTo(Utf8Span other)
	{
		if (null == this.buffer)
		{
			return -1;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return -other.CompareTo(s);
			default:
				return -other.CompareTo((Utf8String)this.buffer);
		}
	}

	public int CompareTo(String other)
	{
		if (null == this.buffer)
		{
			return null == other ? 0 : -1;
		}

		switch (this.buffer)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'case' statements:
//ORIGINAL LINE: case string s:
			case String s:
				return String.Compare(s, other, StringComparison.Ordinal);
			default:
				return ((Utf8String)this.buffer).compareTo(other);
		}
	}

	public UtfAnyString clone()
	{
		UtfAnyString varCopy = new UtfAnyString();

		varCopy.buffer = this.buffer;

		return varCopy;
	}
}