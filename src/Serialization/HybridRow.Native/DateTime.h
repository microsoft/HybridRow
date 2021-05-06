// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <cstdint>

namespace cdb_hr
{
  /// <summary>C# DateTime type.</summary>
  /// <remarks>CoreCLR DateTime definition: https://referencesource.microsoft.com/#mscorlib/system/datetime.cs </remarks>
  #pragma pack(push, 1)
  struct DateTime final
  {
    constexpr DateTime() noexcept : m_data(0) { }
    ~DateTime() noexcept = default;
    constexpr DateTime(int64_t ticks) noexcept : m_data(static_cast<uint64_t>(ticks)) { }
    DateTime(const DateTime& other) = default;
    DateTime(DateTime&& other) noexcept = default;
    DateTime& operator=(const DateTime& other) = default;
    DateTime& operator=(DateTime&& other) noexcept = default;

    [[nodiscard]] int64_t Ticks() const noexcept { return static_cast<int64_t>(m_data); }

    friend bool operator==(const DateTime& lhs, const DateTime& rhs) { return lhs.m_data == rhs.m_data; }
    friend bool operator!=(const DateTime& lhs, const DateTime& rhs) { return !(lhs == rhs); }

  private:
    friend std::hash<cdb_hr::DateTime>;

    // The data is stored as an unsigned 64-bit integer
    //   Bits 01-62: The value of 100-nanosecond ticks where 0 represents 1/1/0001 12:00am, up until the value
    //               12/31/9999 23:59:59.9999999
    //   Bits 63-64: A four-state value that describes the DateTimeKind value of the date time, with a 2nd
    //               value for the rare case where the date time is local, but is in an overlapped daylight
    //               savings time hour and it is in daylight savings time. This allows distinction of these
    //               otherwise ambiguous local times and prevents data loss when round tripping from Local to
    //               UTC time.
    uint64_t m_data;
  };
  #pragma pack(pop)
}

namespace std
{
  template<>
  struct hash<cdb_hr::DateTime>
  {
    constexpr std::size_t operator()(cdb_hr::DateTime const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(s.m_data);
    }
  };
}
