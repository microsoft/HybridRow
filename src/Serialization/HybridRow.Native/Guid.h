// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

// ReSharper disable once CppUnusedIncludeDirective
#include "guiddef.h"

namespace cdb_hr
{
  /// <summary>C# Guid type.</summary>
  /// <remarks>CoreCLR Guid definition: https://referencesource.microsoft.com/#mscorlib/system/guid.cs </remarks>
  #pragma pack(push, 1)
  struct Guid final
  {
    constexpr static Guid Empty() noexcept { return Guid{}; }
    static Guid NewGuid() noexcept;

    constexpr Guid() noexcept : m_data{0, 0, 0, {0, 0, 0, 0, 0, 0, 0, 0}} { }
    ~Guid() noexcept = default;
    constexpr Guid(uint32_t a, uint16_t b, uint16_t c, uint8_t d, uint8_t e, uint8_t f, uint8_t g, uint8_t h, uint8_t i,
                   uint8_t j, uint8_t k) noexcept;
    Guid(const Guid& other) = default;
    Guid(Guid&& other) noexcept = default;
    Guid& operator=(const Guid& other) = default;
    Guid& operator=(Guid&& other) noexcept = default;

    friend bool operator==(const Guid& lhs, const Guid& rhs) noexcept;
    friend bool operator!=(const Guid& lhs, const Guid& rhs) noexcept;

  private:
    friend std::hash<Guid>;

    GUID m_data;
  };
  #pragma pack(pop)

  constexpr Guid::Guid(uint32_t a, uint16_t b, uint16_t c, uint8_t d, uint8_t e, uint8_t f, uint8_t g, uint8_t h,
                       uint8_t i, uint8_t j, uint8_t k) noexcept :
    m_data{a, b, c, {d, e, f, g, h, i, j, k}} { }

  inline bool operator==(const Guid& lhs, const Guid& rhs) noexcept
  {
    return IsEqualGUID(lhs.m_data, rhs.m_data) != 0;
  }

  inline bool operator!=(const Guid& lhs, const Guid& rhs) noexcept
  {
    return !(lhs == rhs);
  }
}

namespace std
{
  template<>
  struct hash<cdb_hr::Guid>
  {
    constexpr std::size_t operator()(cdb_hr::Guid const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(
        s.m_data.Data1, s.m_data.Data2, s.m_data.Data3, s.m_data.Data4[2], s.m_data.Data4[7]);
    }
  };
}
