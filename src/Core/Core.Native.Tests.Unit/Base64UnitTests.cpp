// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include <array>
#include <random>
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  TEST_CLASS(Base64UnitTests)
  {
  public:

    template<typename TChar>
    void Roundtrip()
    {
      std::array<byte, 200> bytes{};
      std::array<byte, 200> bytes2{};
      std::array<TChar, cdb_core::Base64::GetEncodeRequiredLength(static_cast<uint32_t>(bytes.size()))> chars{};

      std::random_device seedGenerator{};
      std::mt19937 rand{seedGenerator()};
      const std::uniform_int_distribution<int> distribution{};

      cdb_core::Span<int> bb = cdb_core::MemoryMarshal::Cast<byte, int>(cdb_core::Span{bytes});
      for (auto& b : bb)
      {
        b = distribution(rand);
      }
      std::fill(chars.begin(), chars.end(), '\0');

      cdb_core::Span<TChar> encoded = cdb_core::Base64::Encode(bytes, cdb_core::Span{chars});
      Logger::WriteMessage(&encoded[0]);
      cdb_core::Span<byte> decoded = cdb_core::Base64::Decode(cdb_core::ReadOnlySpan{encoded}, cdb_core::Span{bytes2});

      Assert::AreEqual(static_cast<uint32_t>(bytes.size()), decoded.Length());
      for (uint32_t i = 0; i < decoded.Length(); i++)
      {
        Assert::AreEqual(bytes[i], decoded[i]);
      }
    }

    /// <summary>
    /// Tests whether a byte string properly round-trips as Base64 text.
    /// </summary>
    TEST_METHOD(RoundtripA)
    {
      Roundtrip<char>();
    }

    /// <summary>
    /// Tests whether a byte string properly round-trips as Base64 wide text.
    /// </summary>
    TEST_METHOD(RoundtripW)
    {
      Roundtrip<wchar_t>();
    }
  };
}

// Additional string function for test diagnostics.
namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<>
  inline std::wstring ToString<std::byte>(const std::byte& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }
}
