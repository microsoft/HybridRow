// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Runtime.CompilerServices;

    internal static class LayoutCodeTraits
    {
        /// <summary>Returns the same scope code without the immutable bit set.</summary>
        /// <param name="code">The scope type code.</param>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static LayoutCode ClearImmutableBit(LayoutCode code)
        {
            return code & (LayoutCode)0xFE;
        }

        /// <summary>
        /// Returns true if the type code indicates that, even within a typed scope, this element type
        /// always requires a type code (because the value itself is in the type code).
        /// </summary>
        /// <param name="code">The element type code.</param>
        internal static bool AlwaysRequiresTypeCode(LayoutCode code)
        {
            return (code == LayoutCode.Boolean) ||
                   (code == LayoutCode.BooleanFalse) ||
                   (code == LayoutCode.Null);
        }

        /// <summary>Returns a canonicalized version of the layout code.</summary>
        /// <remarks>
        /// Some codes (e.g. <see cref="LayoutCode.Boolean" /> use multiple type codes to also encode
        /// values.  This function converts actual value based code into the canonicalized type code for schema
        /// comparisons.
        /// </remarks>
        /// <param name="code">The code to canonicalize.</param>
        internal static LayoutCode Canonicalize(LayoutCode code)
        {
            return (code == LayoutCode.BooleanFalse) ? LayoutCode.Boolean : code;
        }
    }
}
