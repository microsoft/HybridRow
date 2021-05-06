// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Diagnostics;
    using System.Runtime.InteropServices;

    /// <summary>A wall clock time expressed in milliseconds since the Unix Epoch.</summary>
    /// <remarks>
    /// A <see cref="UnixDateTime" /> is a fixed length value-type providing millisecond
    /// granularity as a signed offset from the Unix Epoch (midnight, January 1, 1970 UTC).
    /// </remarks>
    [DebuggerDisplay("{" + nameof(UnixDateTime.Milliseconds) + "}")]
    [StructLayout(LayoutKind.Sequential, Pack = 1)]
    public readonly struct UnixDateTime : IEquatable<UnixDateTime>
    {
        /// <summary>The size (in bytes) of a UnixDateTime.</summary>
        public const int Size = sizeof(long);

        /// <summary>The unix epoch.</summary>
        /// <remarks><see cref="UnixDateTime" /> values are signed values centered on <see cref="Epoch" />.
        /// <para />
        /// This is the same value as default(<see cref="UnixDateTime" />).</remarks>
        public static readonly UnixDateTime Epoch = default(UnixDateTime);

        /// <summary>Initializes a new instance of the <see cref="UnixDateTime" /> struct.</summary>
        /// <param name="milliseconds">The number of milliseconds since <see cref="Epoch" />.</param>
        public UnixDateTime(long milliseconds)
        {
            this.Milliseconds = milliseconds;
        }

        /// <summary>The number of milliseconds since <see cref="Epoch" />.</summary>
        /// <remarks>This value may be negative.</remarks>
        public long Milliseconds { get; }

        /// <summary>Operator == overload.</summary>
        public static bool operator ==(UnixDateTime left, UnixDateTime right)
        {
            return left.Equals(right);
        }

        /// <summary>Operator != overload.</summary>
        public static bool operator !=(UnixDateTime left, UnixDateTime right)
        {
            return !left.Equals(right);
        }

        /// <summary>Returns true if this is the same value as <see cref="other" />.</summary>
        /// <param name="other">The value to compare against.</param>
        /// <returns>True if the two values are the same.</returns>
        public bool Equals(UnixDateTime other)
        {
            return this.Milliseconds == other.Milliseconds;
        }

        /// <summary><see cref="object.Equals(object)" /> overload.</summary>
        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(null, obj))
            {
                return false;
            }

            return obj is UnixDateTime && this.Equals((UnixDateTime)obj);
        }

        /// <summary><see cref="object.GetHashCode" /> overload.</summary>
        public override int GetHashCode()
        {
            return this.Milliseconds.GetHashCode();
        }
    }
}
