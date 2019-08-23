//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow;

/**
 * A wall clock time expressed in milliseconds since the Unix Epoch.
 * <p>
 * A <see cref="UnixDateTime" /> is a fixed length value-type providing millisecond
 * granularity as a signed offset from the Unix Epoch (midnight, January 1, 1970 UTC).
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{" + nameof(UnixDateTime.Milliseconds) + "}")][StructLayout(LayoutKind.Sequential,
// Pack = 1)] public readonly struct UnixDateTime : IEquatable<UnixDateTime>
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: [DebuggerDisplay("{" + nameof(UnixDateTime.Milliseconds) + "}")][StructLayout(LayoutKind.Sequential,
// Pack = 1)] public readonly struct UnixDateTime : IEquatable<UnixDateTime>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class UnixDateTime implements IEquatable<UnixDateTime> {
    /**
     * The unix epoch.
     * <see cref="UnixDateTime" /> values are signed values centered on <see cref="Epoch" />.
     * <para />
     * This is the same value as default(<see cref="UnixDateTime" />).
     */
    public static final UnixDateTime Epoch = new UnixDateTime();
    /**
     * The size (in bytes) of a UnixDateTime.
     */
    public static final int Size = (Long.SIZE / Byte.SIZE);
    /**
     * The number of milliseconds since <see cref="Epoch" />.
     * This value may be negative.
     */
    private long Milliseconds;

    /**
     * Initializes a new instance of the <see cref="UnixDateTime" /> struct.
     *
     * @param milliseconds The number of milliseconds since <see cref="Epoch" />.
     */
    public UnixDateTime() {
    }

    public UnixDateTime(long milliseconds) {
        this.Milliseconds = milliseconds;
    }

    public long getMilliseconds() {
        return Milliseconds;
    }

    /**
     * Returns true if this is the same value as <see cref="other" />.
     *
     * @param other The value to compare against.
     * @return True if the two values are the same.
     */
    public boolean equals(UnixDateTime other) {
        return this.getMilliseconds() == other.getMilliseconds();
    }

    /**
     * <see cref="object.Equals(object)" /> overload.
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        return obj instanceof UnixDateTime && this.equals((UnixDateTime)obj);
    }

    /**
     * <see cref="object.GetHashCode" /> overload.
     */
    @Override
    public int hashCode() {
        return (new Long(this.getMilliseconds())).hashCode();
    }

    /**
     * Operator == overload.
     */
    public static boolean opEquals(UnixDateTime left, UnixDateTime right) {
        return left.equals(right.clone());
    }

    /**
     * Operator != overload.
     */
    public static boolean opNotEquals(UnixDateTime left, UnixDateTime right) {
        return !left.equals(right.clone());
    }
}