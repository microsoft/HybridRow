// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;

    /// <summary>The literal null value.</summary>
    /// <remarks>
    /// May be stored hybrid row to indicate the literal null value. Typically this value should
    /// not be used and the corresponding column should be absent from the row.
    /// </remarks>
    public readonly struct NullValue : IEquatable<NullValue>
    {
        /// <summary>The default null literal.</summary>
        /// <remarks>This is the same value as default(<see cref="NullValue" />).</remarks>
        public static readonly NullValue Default = default(NullValue);

        /// <summary>Operator == overload.</summary>
        public static bool operator ==(NullValue left, NullValue right)
        {
            return left.Equals(right);
        }

        /// <summary>Operator != overload.</summary>
        public static bool operator !=(NullValue left, NullValue right)
        {
            return !left.Equals(right);
        }

        /// <summary>Returns true if this is the same value as <see cref="other" />.</summary>
        /// <param name="other">The value to compare against.</param>
        /// <returns>True if the two values are the same.</returns>
        public bool Equals(NullValue other)
        {
            return true;
        }

        /// <summary><see cref="object.Equals(object)" /> overload.</summary>
        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            return obj is NullValue && this.Equals((NullValue)obj);
        }

        /// <summary><see cref="object.GetHashCode" /> overload.</summary>
        public override int GetHashCode()
        {
            return 42;
        }
    }
}
