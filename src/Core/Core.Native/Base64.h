// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Contract.h"
#include "ReadOnlySpan.h"
#include "Span.h"

namespace cdb_core
{
  template<class T> struct ReadOnlySpan;
  template<class T> struct Span;

  struct Base64 final
  {
    enum class Flags
    {
      None = 0,
      NoPad = 1,
      NoLinefeed = 2,
      Url = 4,
    };

    // length functions
    constexpr static uint32_t GetEncodeRequiredLength(uint32_t srcLen, Flags flags = Flags::None) noexcept;
    template<class TElem, class TTraits>
    constexpr static uint32_t GetDecodeRequiredLength(std::basic_string_view<TElem, TTraits> src) noexcept;
    constexpr static uint32_t GetDecodeRequiredLength(uint32_t srcLen) noexcept;

    /// <summary>
    /// Converts a sequence of bytes into a Base64 text string.
    /// </summary>
    /// <typeparam name="TChar">The kind of text element.</typeparam>
    /// <param name="src">The source bytes to convert.</param>
    /// <param name="dest">A buffer to receive the text string.  This buffer MUST be at least as large as indicated by <see cref="GetEncodeRequiredLength" />.</param>
    /// <param name="flags">Optional flags</param>
    /// <returns>A sub-span over <paramref name="dest"/> containing the encoded string.</returns>
    template<typename TChar>
    constexpr static Span<TChar> Encode(ReadOnlySpan<std::byte> src, Span<TChar> dest,
                                        Flags flags = Flags::None) noexcept;

    /// <summary>
    /// Converts a Base64 text string back into a sequence of bytes.
    /// </summary>
    /// <typeparam name="TChar">The kind of text element.</typeparam>
    /// <param name="src">The source Base64 string to convert.</param>
    /// <param name="dest">A buffer to receive the byte sequence.  This buffer MUST be at least as large as indicated by <see cref="GetDecodeRequiredLength" />.</param>
    /// <param name="flags">Optional flags</param>
    /// <returns>A sub-span over <paramref name="dest"/> containing the encoded string.</returns>
    template<class TElem, class TTraits>
    constexpr static Span<std::byte> Decode(std::basic_string_view<TElem, TTraits> src, Span<std::byte> dest,
                                            Flags flags = Flags::None) noexcept;

    /// <summary>
    /// Converts a Base64 text string back into a sequence of bytes.
    /// </summary>
    /// <typeparam name="TChar">The kind of text element.</typeparam>
    /// <param name="src">The source Base64 string to convert.</param>
    /// <param name="dest">A buffer to receive the byte sequence.  This buffer MUST be at least as large as indicated by <see cref="GetDecodeRequiredLength" />.</param>
    /// <param name="flags">Optional flags</param>
    /// <returns>A sub-span over <paramref name="dest"/> containing the encoded string.</returns>
    template<typename TChar>
    constexpr static Span<std::byte> Decode(ReadOnlySpan<TChar> src, Span<std::byte> dest,
                                            Flags flags = Flags::None) noexcept;
  private:
    // Helper functions
    // Overloaded function to get encoding characters
    // i from 0 to 63 -> base64 encoding character
    // i = 64 '='
    // i = 65 '\r'
    // i = 66 '\n'
    // i = 67 '\0'
    template<typename TChar> constexpr static TChar EncodeBase64Char(std::byte nIndex, Flags flags) noexcept;
    template<typename TChar> constexpr static int DecodeBase64Char(TChar ch, Flags flags) noexcept;
  };

  template<typename TChar> constexpr TChar Base64::EncodeBase64Char(std::byte nIndex, Flags flags) noexcept
  {
    // ReSharper disable once CppStaticAssertFailure
    static_assert(false, "Unknown TChar");
    return 0;
  }

  template<typename TChar> constexpr int Base64::DecodeBase64Char(const TChar ch, Flags flags) noexcept
  {
    // ReSharper disable once CppStaticAssertFailure
    static_assert(false, "Unknown TChar");
    return 0;
  }

  constexpr Base64::Flags operator|(Base64::Flags a, Base64::Flags b) noexcept
  {
    return static_cast<Base64::Flags>(static_cast<int>(a) | static_cast<int>(b));
  }

  constexpr Base64::Flags operator&(Base64::Flags a, Base64::Flags b) noexcept
  {
    return static_cast<Base64::Flags>(static_cast<int>(a) & static_cast<int>(b));
  }

  template<typename TChar>
  constexpr Span<TChar> Base64::Encode(ReadOnlySpan<std::byte> src, Span<TChar> dest, Flags flags) noexcept
  {
    Contract::Requires(dest.Length() >= GetEncodeRequiredLength(src.Length(), flags));

    const std::byte* pSrc = &src[0];
    int srcLen = src.Length();
    TChar* pDest = &dest[0];

    int written(0);
    int len1((srcLen / 3) * 4);
    int len2(len1 / 76);
    int len3(19);

    for (int i = 0; i <= len2; i++)
    {
      if (i == len2)
      {
        len3 = (len1 % 76) / 4;
      }

      for (int j = 0; j < len3; j++)
      {
        uint32_t current(0);
        for (int n = 0; n < 3; n++)
        {
          current |= static_cast<uint32_t>(*pSrc++);
          current <<= 8;
        }
        for (int k = 0; k < 4; k++)
        {
          std::byte b = static_cast<std::byte>(current >> 26);
          *pDest = EncodeBase64Char<TChar>(b, flags);
          ++pDest;
          current <<= 6;
        }
      }
      written += len3 * 4;

      if ((flags & Flags::NoLinefeed) == Flags::None)
      {
        // Insert \r\n here
        *pDest = EncodeBase64Char<TChar>(byte{65}, Flags::None);
        ++pDest;
        *pDest = EncodeBase64Char<TChar>(byte{66}, Flags::None);
        ++pDest;
        written += 2;
      }
    }

    if ((written != 0) && ((flags & Flags::NoLinefeed) == Flags::None))
    {
      pDest -= 2;
      written -= 2;
    }

    len2 = (srcLen % 3) ? (srcLen % 3 + 1) : 0;
    if (len2)
    {
      uint32_t current(0);
      for (int n = 0; n < 3; n++)
      {
        if (n < (srcLen % 3))
        {
          current |= static_cast<uint32_t>(*pSrc++);
        }
        current <<= 8;
      }
      for (int k = 0; k < len2; k++)
      {
        std::byte b = static_cast<std::byte>(current >> 26);
        *pDest = EncodeBase64Char<TChar>(b, flags);
        ++pDest;
        current <<= 6;
      }
      written += len2;
      if ((flags & Flags::NoPad) == Flags::None)
      {
        len3 = len2 ? 4 - len2 : 0;
        for (int j = 0; j < len3; j++)
        {
          // Insert '=' here
          *pDest = EncodeBase64Char<TChar>(byte{64}, flags);
          ++pDest;
        }
        written += len3;
      }
    }

    return dest.Slice(0, written);
  }

  template<class TElem, class TTraits>
  constexpr Span<std::byte> Base64::Decode(std::basic_string_view<TElem, TTraits> src, Span<std::byte> dest,
                                           Flags flags) noexcept
  {
    Contract::Requires(static_cast<uint64_t>(src.size()) <= UINT32_MAX);
    return Base64::Decode(ReadOnlySpan<TElem>{src.data(), static_cast<uint32_t>(src.size())}, dest, flags);
  }

  template<typename TChar>
  constexpr Span<std::byte> Base64::Decode(ReadOnlySpan<TChar> src, Span<std::byte> dest, Flags flags) noexcept
  {
    const TChar* pSrc = &src[0];
    std::byte* pDest = &dest[0];

    // Walk the source buffer, each four character sequence is converted to 3 bytes.
    // CRLFs and =, and any characters not in the encoding table are skipped.
    const TChar* pEnd = pSrc + src.Length();
    uint32_t written = 0;
    while (pSrc < pEnd && (*pSrc) != 0)
    {
      uint32_t current = 0;
      uint32_t bits = 0;
      for (int i = 0; i < 4; i++)
      {
        if (pSrc >= pEnd)
        {
          break;
        }

        int ch = DecodeBase64Char<TChar>(*pSrc, flags);
        ++pSrc;
        if (ch == -1)
        {
          // skip this char
          // TODO: Change signature of method to support failure.
          i--;
          continue;
        }
        current <<= 6;
        current |= ch;
        bits += 6;
      }

      Contract::Invariant(written + (bits / 8) <= dest.Length());

      // current has the 3 bytes to write to the output buffer, left to right
      current <<= 24 - bits;
      for (uint32_t i = 0; i < bits / 8; i++)
      {
        *pDest = static_cast<std::byte>((current & 0x00ff0000) >> 16);
        pDest++;
        current <<= 8;
        written++;
      }
    }

    return dest.Slice(0, written);
  }

  constexpr uint32_t Base64::GetEncodeRequiredLength(const uint32_t srcLen, const Flags flags) noexcept
  {
    uint32_t nSrcLen4 = srcLen * 4;
    uint32_t retval = nSrcLen4 / 3;
    if ((flags & Flags::NoPad) == Flags::None)
    {
      retval += srcLen % 3;
    }

    uint32_t numLinefeed = retval / 76 + 1;
    uint32_t onLastLine = retval % 76;

    if (onLastLine && onLastLine % 4)
    {
      retval += 4 - (onLastLine % 4);
    }

    numLinefeed *= 2;
    if ((flags & Flags::NoLinefeed) == Flags::None)
    {
      retval += numLinefeed;
    }

    return retval;
  }

  template<class TElem, class TTraits>
  constexpr uint32_t Base64::GetDecodeRequiredLength(std::basic_string_view<TElem, TTraits> src) noexcept
  {
    Contract::Requires(static_cast<uint64_t>(src.size()) <= UINT32_MAX);
    return static_cast<uint32_t>(src.size());
  }

  constexpr uint32_t Base64::GetDecodeRequiredLength(const uint32_t srcLen) noexcept
  {
    return srcLen;
  }

  constexpr char Base64CharEncodingTable[68] = {
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z',
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    '+',
    '/',
    '=',
    '\r',
    '\n',
    '\0'
  };

  template<>
  constexpr char Base64::EncodeBase64Char<char>(std::byte index, const Flags flags) noexcept
  {
    char retval = Base64CharEncodingTable[static_cast<int>(index)];
    if ((flags & Flags::Url) != Flags::None)
    {
      if (retval == '+')
      {
        retval = '-';
      }
      if (retval == '/')
      {
        retval = '_';
      }
    }
    return retval;
  }

  constexpr wchar_t Base64WideEncodingTable[68] = {
    L'A',
    L'B',
    L'C',
    L'D',
    L'E',
    L'F',
    L'G',
    L'H',
    L'I',
    L'J',
    L'K',
    L'L',
    L'M',
    L'N',
    L'O',
    L'P',
    L'Q',
    L'R',
    L'S',
    L'T',
    L'U',
    L'V',
    L'W',
    L'X',
    L'Y',
    L'Z',
    L'a',
    L'b',
    L'c',
    L'd',
    L'e',
    L'f',
    L'g',
    L'h',
    L'i',
    L'j',
    L'k',
    L'l',
    L'm',
    L'n',
    L'o',
    L'p',
    L'q',
    L'r',
    L's',
    L't',
    L'u',
    L'v',
    L'w',
    L'x',
    L'y',
    L'z',
    L'0',
    L'1',
    L'2',
    L'3',
    L'4',
    L'5',
    L'6',
    L'7',
    L'8',
    L'9',
    L'+',
    L'/',
    L'=',
    L'\r',
    L'\n',
    L'\0'
  };

  template<>
  constexpr wchar_t Base64::EncodeBase64Char<wchar_t>(std::byte index, const Flags flags) noexcept
  {
    wchar_t retval = Base64WideEncodingTable[static_cast<int>(index)];

    if ((flags & Flags::Url) != Flags::None)
    {
      if (retval == L'+')
      {
        retval = L'-';
      }
      if (retval == L'/')
      {
        retval = L'_';
      }
    }
    return retval;
  }

  template<>
  constexpr int Base64::DecodeBase64Char<char>(const char ch, const Flags flags) noexcept
  {
    // returns -1 if the character is invalid
    // or should be skipped
    // otherwise, returns the 6-bit code for the character
    // from the encoding table
    if (ch >= 'A' && ch <= 'Z')
    {
      return ch - 'A' + 0;     // 0 range starts at 'A'
    }
    if (ch >= 'a' && ch <= 'z')
    {
      return ch - 'a' + 26;    // 26 range starts at 'a'
    }
    if (ch >= '0' && ch <= '9')
    {
      return ch - '0' + 52;    // 52 range starts at '0'
    }

    if ((flags & Flags::Url) != Flags::None)
    {
      if (ch == '-')
      {
        return 62;    // base64url '+' -> '-'
      }
      if (ch == '_')
      {
        return 63;    // base64url '/' -> '_'
      }
    }

    if (ch == '+')
    {
      return 62;    // base64 '+' is 62
    }
    if (ch == '/')
    {
      return 63;    // base64 '/' is 63
    }

    return -1;
  }

  template<>
  constexpr int Base64::DecodeBase64Char<wchar_t>(const wchar_t ch, const Flags flags) noexcept
  {
    // returns -1 if the character is invalid
    // or should be skipped
    // otherwise, returns the 6-bit code for the character
    // from the encoding table
    if (ch >= L'A' && ch <= L'Z')
    {
      return ch - L'A' + 0;     // 0 range starts at 'A'
    }
    if (ch >= L'a' && ch <= L'z')
    {
      return ch - L'a' + 26;    // 26 range starts at 'a'
    }
    if (ch >= L'0' && ch <= L'9')
    {
      return ch - L'0' + 52;    // 52 range starts at '0'
    }

    if ((flags & Flags::Url) != Flags::None)
    {
      if (ch == L'-')
      {
        return 62;    // base64url '+' -> '-'
      }
      if (ch == L'_')
      {
        return 63;    // base64url '/' -> '_'
      }
    }

    if (ch == L'+')
    {
      return 62;    // base64 '+' is 62
    }
    if (ch == L'/')
    {
      return 63;    // base64 '/' is 63
    }

    return -1;
  }
}
