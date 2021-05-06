// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core
{
    using System.Runtime.CompilerServices;

    public static class Linear
    {
        /// <summary>
        /// Perform the managed equivalent of std::move by returning the value at
        /// <paramref name="src" /> while simultaneously assigning <see cref="T:default" /> to
        /// <paramref name="src" />.
        /// </summary>
        /// <typeparam name="T">The type of the value to transfer.</typeparam>
        /// <param name="src">A reference to the field whose value should be transferred.</param>
        /// <returns>The value transferred.</returns>
        /// <remarks>
        /// The value of <paramref name="src" /> after the transfer is always <see cref="T:default" />
        /// . The value is considered "consumed".
        /// </remarks>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static T Move<T>(ref T src)
        {
            T retval = src;
            src = default;
            return retval;
        }
    }
}
