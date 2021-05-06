// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core.Utf8
{
    using System;

    internal static class Utf8Helper
    {
        public const int MaxCodeUnitsPerCodePoint = 4;

        public static bool TryDecodeCodePoint(ReadOnlySpan<byte> utf8, int index, out uint codePoint, out int bytesConsumed)
        {
            if (index >= utf8.Length)
            {
                codePoint = default;
                bytesConsumed = 0;
                return false;
            }

            byte first = utf8[index];

            bytesConsumed = Utf8Helper.GetEncodedBytes(first);
            if (bytesConsumed == 0 || utf8.Length - index < bytesConsumed)
            {
                bytesConsumed = 0;
                codePoint = default;
                return false;
            }

            switch (bytesConsumed)
            {
                case 1:
                    codePoint = first;
                    break;

                case 2:
                    codePoint = (uint)(first & Utf8Helper.B0001_1111U);
                    break;

                case 3:
                    codePoint = (uint)(first & Utf8Helper.B0000_1111U);
                    break;

                case 4:
                    codePoint = (uint)(first & Utf8Helper.B0000_0111U);
                    break;

                default:
                    codePoint = default;
                    bytesConsumed = 0;
                    return false;
            }

            for (int i = 1; i < bytesConsumed; i++)
            {
                uint current = utf8[index + i];
                if ((current & Utf8Helper.B1100_0000U) != Utf8Helper.B1000_0000U)
                {
                    bytesConsumed = 0;
                    codePoint = default;
                    return false;
                }

                codePoint = (codePoint << 6) | (Utf8Helper.B0011_1111U & current);
            }

            return true;
        }

        public static bool TryDecodeCodePointFromString(string s, int index, out uint codePoint, out int encodedChars)
        {
            if (index < 0 || index >= s.Length)
            {
                codePoint = default;
                encodedChars = 0;
                return false;
            }

            if (index == s.Length - 1 && char.IsSurrogate(s[index]))
            {
                codePoint = default;
                encodedChars = 0;
                return false;
            }

            encodedChars = char.IsHighSurrogate(s[index]) ? 2 : 1;
            codePoint = unchecked((uint)char.ConvertToUtf32(s, index));

            return true;
        }

        private static int GetEncodedBytes(byte b)
        {
            if ((b & Utf8Helper.B1000_0000U) == 0)
            {
                return 1;
            }

            if ((b & Utf8Helper.B1110_0000U) == Utf8Helper.B1100_0000U)
            {
                return 2;
            }

            if ((b & Utf8Helper.B1111_0000U) == Utf8Helper.B1110_0000U)
            {
                return 3;
            }

            if ((b & Utf8Helper.B1111_1000U) == Utf8Helper.B1111_0000U)
            {
                return 4;
            }

            return 0;
        }

        // ReSharper disable InconsistentNaming
#pragma warning disable SA1310 // Field names should not contain underscore
        private const byte B0000_0111U = 0x07; //7
        private const byte B0000_1111U = 0x0F; //15
        private const byte B0001_1111U = 0x1F; //31
        private const byte B0011_1111U = 0x3F; //63
        private const byte B1000_0000U = 0x80; //128
        private const byte B1100_0000U = 0xC0; //192
        private const byte B1110_0000U = 0xE0; //224
        private const byte B1111_0000U = 0xF0; //240
        private const byte B1111_1000U = 0xF8; //248

        // ReSharper restore InconsistentNaming
#pragma warning restore SA1310 // Field names should not contain underscore
    }
}
