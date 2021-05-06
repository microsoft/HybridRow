// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_core
{
  struct Endian final
  {
    // Static Class
    Endian() = delete;
    ~Endian() = delete;
    Endian(const Endian& other) = delete;
    Endian(Endian&& other) noexcept = delete;
    Endian& operator=(const Endian& other) = delete;
    Endian& operator=(Endian&& other) noexcept = delete;

    constexpr static bool IsLittleEndian() noexcept;
    constexpr static bool IsBigEndian() noexcept;
  };

  constexpr bool Endian::IsLittleEndian() noexcept
  {
    // TODO: this should use std::endian when we move to C++ v20.
    return true;
  }

  constexpr bool Endian::IsBigEndian() noexcept
  {
    return !Endian::IsLittleEndian();
  }
}
