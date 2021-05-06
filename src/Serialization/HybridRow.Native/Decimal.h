// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <cstdint>
#include <cstddef>

namespace cdb_hr
{
  /// <summary>C# decimal type.</summary>
  /// <remarks>CoreCLR Decimal definition: https://referencesource.microsoft.com/#mscorlib/system/decimal.cs </remarks>
  #pragma pack(push, 1)
  struct Decimal final
  {
    constexpr Decimal() noexcept : m_flags(0), m_hi(0), m_lo(0), m_mid(0) { }
    ~Decimal() noexcept = default;

    constexpr Decimal(int32_t lo, int32_t mid, int32_t hi, bool isNegative, std::byte byteScale) noexcept :
      m_flags((static_cast<int32_t>(byteScale) << 16) | (isNegative ? Decimal::SignMask : 0)),
      m_hi(hi),
      m_lo(lo),
      m_mid(mid) { }

    constexpr Decimal(int64_t value) noexcept :
      m_flags((value >= 0) ? 0 : SignMask),
      m_hi(0),
      m_lo(static_cast<int32_t>(value >= 0 ? value : -value)),
      m_mid(static_cast<int32_t>((value >= 0 ? value : -value) >> 32)) { }

    Decimal(const Decimal& other) = default;
    Decimal(Decimal&& other) noexcept = default;
    Decimal& operator=(const Decimal& other) = default;
    Decimal& operator=(Decimal&& other) noexcept = default;

    friend bool operator==(const Decimal& lhs, const Decimal& rhs)
    {
      return lhs.m_flags == rhs.m_flags
        && lhs.m_hi == rhs.m_hi
        && lhs.m_lo == rhs.m_lo
        && lhs.m_mid == rhs.m_mid;
    }

    friend bool operator!=(const Decimal& lhs, const Decimal& rhs) { return !(lhs == rhs); }

  private:
    friend std::hash<cdb_hr::Decimal>;

    // The lo, mid, hi, and flags fields contain the representation of the
    // Decimal value. The lo, mid, and hi fields contain the 96-bit integer
    // part of the Decimal. Bits 0-15 (the lower word) of the flags field are
    // unused and must be zero; bits 16-23 contain must contain a value between
    // 0 and 28, indicating the power of 10 to divide the 96-bit integer part
    // by to produce the Decimal value; bits 24-30 are unused and must be zero;
    // and finally bit 31 indicates the sign of the Decimal value, 0 meaning
    // positive and 1 meaning negative.
    int32_t m_flags;
    int32_t m_hi;
    int32_t m_lo;
    int32_t m_mid;

    // Sign mask for the flags field. A value of zero in this bit indicates a
    // positive Decimal value, and a value of one in this bit indicates a
    // negative Decimal value.
    constexpr static int32_t SignMask = static_cast<int32_t>(0x80000000);
  };
  #pragma pack(pop)
}

namespace std
{
  template<>
  struct hash<cdb_hr::Decimal>
  {
    constexpr std::size_t operator()(cdb_hr::Decimal const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(s.m_flags, s.m_hi, s.m_lo, s.m_mid);
    }
  };
}
