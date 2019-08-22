//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow;

/**
 * The literal null value.
 * <p>
 * May be stored hybrid row to indicate the literal null value. Typically this value should
 * not be used and the corresponding column should be absent from the row.
 */
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: public readonly struct NullValue : IEquatable<NullValue>
//C# TO JAVA CONVERTER WARNING: Java has no equivalent to the C# readonly struct:
public final class NullValue implements IEquatable<NullValue> {
    /**
     * The default null literal.
     * This is the same value as default(<see cref="NullValue" />).
     */
    public static final NullValue Default = new NullValue();

    /**
     * Returns true if this is the same value as <see cref="other" />.
     *
     * @param other The value to compare against.
     * @return True if the two values are the same.
     */
    public boolean equals(NullValue other) {
        return true;
    }

    /**
     * <see cref="object.Equals(object)" /> overload.
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        return obj instanceof NullValue && this.equals((NullValue)obj);
    }

    /**
     * <see cref="object.GetHashCode" /> overload.
     */
    @Override
    public int hashCode() {
        return 42;
    }

    /**
     * Operator == overload.
     */
    public static boolean opEquals(NullValue left, NullValue right) {
        return left.equals(right.clone());
    }

    /**
     * Operator != overload.
     */
    public static boolean opNotEquals(NullValue left, NullValue right) {
        return !left.equals(right.clone());
    }
}