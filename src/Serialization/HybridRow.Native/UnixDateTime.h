// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <cstdint>

namespace cdb_hr
{
  /// <summary>A wall clock time expressed in milliseconds since the Unix Epoch.</summary>
  /// <remarks>
  /// A <see cref="UnixDateTime" /> is a fixed length value-type providing millisecond
  /// granularity as a signed offset from the Unix Epoch (midnight, January 1, 1970 UTC).
  /// </remarks>
  #pragma pack(push, 1)
  struct UnixDateTime final
  {
    /// <summary>The size (in bytes) of a UnixDateTime.</summary>
    constexpr static int Size = sizeof(int64_t);

    /// <summary>The unix epoch.</summary>
    /// <remarks><see cref="UnixDateTime" /> values are signed values centered on <see cref="Epoch" />.</remarks>
    constexpr static UnixDateTime Epoch() { return UnixDateTime{}; }

    constexpr UnixDateTime() noexcept : m_milliseconds{0} {}
    ~UnixDateTime() noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="UnixDateTime" /> struct.</summary>
    /// <param name="milliseconds">The number of milliseconds since <see cref="Epoch" />.</param>
    constexpr UnixDateTime(int64_t milliseconds) noexcept : m_milliseconds(milliseconds) { }
    UnixDateTime(const UnixDateTime& other) noexcept = default;
    UnixDateTime(UnixDateTime&& other) noexcept = default;
    UnixDateTime& operator=(const UnixDateTime& other) noexcept = default;
    UnixDateTime& operator=(UnixDateTime&& other) noexcept = default;

    bool operator==(const UnixDateTime& other) const noexcept { return m_milliseconds == other.m_milliseconds; }
    bool operator!=(const UnixDateTime& other) const noexcept { return m_milliseconds != other.m_milliseconds; }

    /// <summary>The number of milliseconds since <see cref="Epoch" />.</summary>
    /// <remarks>This value may be negative.</remarks>
    [[nodiscard]] int64_t GetMilliseconds() const noexcept { return m_milliseconds; }

  private:
    friend std::hash<cdb_hr::UnixDateTime>;

    int64_t m_milliseconds;
  };
  #pragma pack(pop)
}

namespace std
{
  template<>
  struct hash<cdb_hr::UnixDateTime>
  {
    constexpr std::size_t operator()(cdb_hr::UnixDateTime const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(s.m_milliseconds);
    }
  };
}
