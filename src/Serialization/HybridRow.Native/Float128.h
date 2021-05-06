// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <cstdint>

namespace cdb_hr
{
  /// <summary>An IEEE 128-bit floating point value.</summary>
  /// <remarks>
  /// A binary integer decimal representation of a 128-bit decimal value, supporting 34 decimal digits of
  /// significand and an exponent range of -6143 to +6144.
  /// <list type="table">
  /// <listheader>
  /// <term>Source</term> <description>Link</description>
  /// </listheader> <item>
  /// <term>Wikipedia:</term>
  /// <description>https://en.wikipedia.org/wiki/Decimal128_floating-point_format</description>
  /// </item> <item>
  /// <term>The spec:</term> <description>https://ieeexplore.ieee.org/document/4610935</description>
  /// </item> <item>
  /// <term>Decimal Encodings:</term> <description>http://speleotrove.com/decimal/decbits.html</description>
  /// </item>
  /// </list>
  /// </remarks>
  #pragma pack(push, 1)
  struct Float128 final
  {
    /// <summary>The size (in bytes) of a <see cref="Float128" />.</summary>
    constexpr static int Size = sizeof(long) + sizeof(long);

    /// <summary>
    /// The low-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
    /// encoding scheme.
    /// </summary>
    int64_t Low;

    /// <summary>
    /// The high-order 64 bits of the IEEE 754-2008 128-bit decimal floating point, using the BID
    /// encoding scheme.
    /// </summary>
    int64_t High;

    /// <summary>Initializes a new instance of the <see cref="Float128" /> struct.</summary>
    /// <param name="high">the high-order 64 bits.</param>
    /// <param name="low">the low-order 64 bits.</param>
    constexpr Float128(long high, long low) noexcept : Low(low), High(high) { }
    constexpr Float128() noexcept : Low(0), High(0) { }
    ~Float128() = default;
    Float128(const Float128& other) noexcept = default;
    Float128(Float128&& other) noexcept = default;
    Float128& operator=(const Float128& other) noexcept = default;
    Float128& operator=(Float128&& other) noexcept = default;

    friend bool operator==(const Float128& lhs, const Float128& rhs)
    {
      return lhs.Low == rhs.Low
        && lhs.High == rhs.High;
    }

    friend bool operator!=(const Float128& lhs, const Float128& rhs) { return !(lhs == rhs); }
  };
  #pragma pack(pop)
}

namespace std
{
  template<>
  struct hash<cdb_hr::Float128>
  {
    constexpr std::size_t operator()(cdb_hr::Float128 const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(s.Low, s.High);
    }
  };
}
