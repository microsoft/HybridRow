// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>The literal null value.</summary>
  /// <remarks>
  /// May be stored hybrid row to indicate the literal null value. Typically this value should
  /// not be used and the corresponding column should be absent from the row.
  /// </remarks>
  #pragma pack(push, 1)
  struct NullValue final
  {
    constexpr NullValue() noexcept = default;
    ~NullValue() = default;
    NullValue(const NullValue& other) noexcept = default;
    NullValue(NullValue&& other) noexcept = default;
    NullValue& operator=(const NullValue& other) noexcept = default;
    NullValue& operator=(NullValue&& other) noexcept = default;

    friend bool operator==(const NullValue& lhs, const NullValue& rhs) { return true; }
    friend bool operator!=(const NullValue& lhs, const NullValue& rhs) { return false; }
  };
  #pragma pack(pop)
}

namespace std
{
  template<>
  struct hash<cdb_hr::NullValue>
  {
    std::size_t operator()(cdb_hr::NullValue const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(1899077816);
    }
  };
}
