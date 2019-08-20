// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;

    public sealed class StringTokenizer
    {
        private readonly Dictionary<Utf8String, StringToken> tokens;
        private readonly Dictionary<string, StringToken> stringTokens;
        private readonly List<Utf8String> strings;

        /// <summary>Initializes a new instance of the <see cref="StringTokenizer" /> class.</summary>
        public StringTokenizer()
        {
            this.tokens = new Dictionary<Utf8String, StringToken>(SamplingUtf8StringComparer.Default)
                { { Utf8String.Empty, new StringToken(0, Utf8String.Empty) } };
            this.stringTokens = new Dictionary<string, StringToken>(SamplingStringComparer.Default)
                { { string.Empty, new StringToken(0, Utf8String.Empty) } };
            this.strings = new List<Utf8String> { Utf8String.Empty };
            this.Count = 1;
        }

        /// <summary>The number of unique tokens described by the encoding.</summary>
        public int Count { get; private set; }

        /// <summary>Looks up a string's corresponding token.</summary>
        /// <param name="path">The string to look up.</param>
        /// <param name="token">If successful, the string's assigned token.</param>
        /// <returns>True if successful, false otherwise.</returns>
        public bool TryFindToken(UtfAnyString path, out StringToken token)
        {
            if (path.IsNull)
            {
                token = default;
                return false;
            }

            if (path.IsUtf8)
            {
                return this.tokens.TryGetValue(path.ToUtf8String(), out token);
            }

            return this.stringTokens.TryGetValue(path.ToString(), out token);
        }

        /// <summary>Looks up a token's corresponding string.</summary>
        /// <param name="token">The token to look up.</param>
        /// <param name="path">If successful, the token's assigned string.</param>
        /// <returns>True if successful, false otherwise.</returns>
        public bool TryFindString(ulong token, out Utf8String path)
        {
            if (token >= (ulong)this.strings.Count)
            {
                path = default;
                return false;
            }

            path = this.strings[(int)token];
            return true;
        }

        /// <summary>Assign a token to the string.</summary>
        /// <remarks>If the string already has a token, that token is returned instead.</remarks>
        /// <param name="path">The string to assign a new token.</param>
        /// <returns>The token assigned to the string.</returns>
        internal StringToken Add(Utf8String path)
        {
            Contract.Requires(path != null);

            if (this.tokens.TryGetValue(path, out StringToken token))
            {
                return token;
            }

            token = this.AllocateToken(path);
            return token;
        }

        /// <summary>Allocates a new token and assigns the string to it.</summary>
        /// <param name="path">The string that needs a new token.</param>
        /// <returns>The new allocated token.</returns>
        private StringToken AllocateToken(Utf8String path)
        {
            ulong id = (ulong)this.Count++;
            StringToken token = new StringToken(id, path);
            this.tokens.Add(path, token);
            this.stringTokens.Add(path.ToString(), token);
            this.strings.Add(path);
            Contract.Assert((ulong)this.strings.Count - 1 == id);
            return token;
        }
    }

    public readonly struct StringToken
    {
#pragma warning disable CA1051 // Do not declare visible instance fields
        public readonly ulong Id;
        public readonly byte[] Varint;
        public readonly Utf8String Path;
#pragma warning restore CA1051 // Do not declare visible instance fields

        public StringToken(ulong id, Utf8String path)
        {
            this.Id = id;
            this.Varint = new byte[StringToken.Count7BitEncodedUInt(id)];
            StringToken.Write7BitEncodedUInt(this.Varint.AsSpan(), id);
            this.Path = path;
        }

        public bool IsNull
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.Varint == null;
        }

        private static int Write7BitEncodedUInt(Span<byte> buffer, ulong value)
        {
            // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
            // when set, indicates there are more bytes.
            int i = 0;
            while (value >= 0x80)
            {
                buffer[i] = unchecked((byte)(value | 0x80));
                i++;
                value >>= 7;
            }

            buffer[i] = (byte)value;
            i++;
            return i;
        }

        private static int Count7BitEncodedUInt(ulong value)
        {
            // Count the number of bytes needed to write out an int 7 bits at a time.
            int i = 0;
            while (value >= 0x80)
            {
                i++;
                value >>= 7;
            }

            i++;
            return i;
        }
    }
}
