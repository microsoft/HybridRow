// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_core
{
  template<typename T> struct ReadOnlySpan;

  // Licensed to the .NET Foundation under one or more agreements.
  // The .NET Foundation licenses this file to you under the MIT license.
  // See the LICENSE file in the project root for more information.
  //
  // File implements Slicing-by-8 CRC Generation, as described in
  // "Novel Table Lookup-Based Algorithms for High-Performance CRC Generation"
  // IEEE TRANSACTIONS ON COMPUTERS, VOL. 57, NO. 11, NOVEMBER 2008
  //
  // Copyright(c) 2004-2006 Intel Corporation - All Rights Reserved
  //
  // This software program is licensed subject to the BSD License,
  // available at http://www.opensource.org/licenses/bsd-license.html.

  /// <summary>CRC Generator.</summary>
  struct Crc32 final
  {
    // Static Class
    [[nodiscard]] Crc32() = delete;
    ~Crc32() = delete;
    Crc32(const Crc32& other) = delete;
    Crc32(Crc32&& other) noexcept = delete;
    Crc32& operator=(const Crc32& other) = delete;
    Crc32& operator=(Crc32&& other) noexcept = delete;

    [[nodiscard]] static uint32_t Update(uint32_t crc32, ReadOnlySpan<byte> span) noexcept;
  };
}
