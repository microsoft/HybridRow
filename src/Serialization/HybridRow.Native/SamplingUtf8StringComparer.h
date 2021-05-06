// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <algorithm>

namespace cdb_hr
{
struct SamplingUtf8StringComparer
{
  std::size_t operator()(std::string_view obj) const noexcept
  {
    uint32_t hash1 = 5381;
    uint32_t hash2 = hash1;
    const size_t numSamples = 4;
    const size_t modulus = 13;

    const char* utf8 = obj.data();
    const size_t length = obj.size();
    const size_t max = std::min(length, numSamples);
    for (size_t i = 0; i < max; i++)
    {
      const auto c = static_cast<uint32_t>(static_cast<std::byte>(utf8[(i * modulus) % length]));
      if (i % 2 == 0)
      {
        hash1 = ((hash1 << 5) + hash1) ^ c;
      }
      else
      {
        hash2 = ((hash2 << 5) + hash2) ^ c;
      }
    }

    return (hash1 + (hash2 * 1566083941));
  }
};
}
