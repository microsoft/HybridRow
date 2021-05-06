// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Core
{
    using System;
    using System.Globalization;

    public static class SpanHexExtensions
    {
        private static readonly byte[] DecodeTable =
        {
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
        };

        private static readonly uint[] EncodeTable = SpanHexExtensions.Initialize();

        public static unsafe string ToHexString(this ReadOnlySpan<byte> bytes)
        {
            int len = bytes.Length;
            string result = new string((char)0, len * 2);
            fixed (uint* lp = SpanHexExtensions.EncodeTable)
            {
                fixed (byte* bp = bytes)
                {
                    fixed (char* rp = result)
                    {
                        for (int i = 0; i < len; i++)
                        {
                            ((uint*)rp)[i] = lp[bp[i]];
                        }
                    }
                }
            }

            return result;
        }

        public static bool TryParseHexString(this ReadOnlySpan<char> hexChars, out byte[] result)
        {
            Contract.Requires(hexChars.Length % 2 == 0);

            int len = hexChars.Length;
            result = new byte[len / 2];
            if (!hexChars.TryParseHexString(result.AsSpan()))
            {
                result = default;
                return false;
            }

            return true;
        }

        public static unsafe bool TryParseHexString(this ReadOnlySpan<char> hexChars, Span<byte> result)
        {
            Contract.Requires(hexChars.Length % 2 == 0);
            Contract.Requires(result.Length == hexChars.Length / 2);

            int len = hexChars.Length;
            fixed (byte* lp = SpanHexExtensions.DecodeTable)
            {
                fixed (char* cp = hexChars)
                {
                    fixed (byte* rp = result)
                    {
                        for (int i = 0; i < len; i += 2)
                        {
                            int c1 = cp[i];
                            if (c1 < 0 || c1 > 255)
                            {
                                return false;
                            }

                            byte b1 = lp[c1];
                            if (b1 == 255)
                            {
                                return false;
                            }

                            int c2 = cp[i + 1];
                            if (c2 < 0 || c2 > 255)
                            {
                                return false;
                            }

                            byte b2 = lp[c2];
                            if (b2 == 255)
                            {
                                return false;
                            }

                            rp[i / 2] = (byte)((b1 << 4) | b2);
                        }
                    }
                }
            }

            return true;
        }

        private static uint[] Initialize()
        {
            uint[] result = new uint[256];
            for (int i = 0; i < 256; i++)
            {
                string s = i.ToString("X2", CultureInfo.InvariantCulture);
                if (BitConverter.IsLittleEndian)
                {
                    result[i] = s[0] + ((uint)s[1] << 16);
                }
                else
                {
                    result[i] = s[1] + ((uint)s[0] << 16);
                }
            }

            return result;
        }
    }
}
