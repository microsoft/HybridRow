// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include <array>
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  TEST_CLASS(Crc32UnitTests)
  {
    constexpr static std::array<byte, 7> Sample1
    {
      {byte{0x45}, byte{0xB1}, byte{0xD6}, byte{0xC7}, byte{0x81}, byte{0xE1}, byte{0x3F}}
    };
    constexpr static std::array<byte, 23> Sample2
    {
      {
        byte{0xE5},
        byte{0x78},
        byte{0xBA},
        byte{0xD5},
        byte{0x00},
        byte{0xA2},
        byte{0x98},
        byte{0xFE},
        byte{0xF1},
        byte{0xEF},
        byte{0x2A},
        byte{0x90},
        byte{0x6B},
        byte{0xC9},
        byte{0x85},
        byte{0x22},
        byte{0x00},
        byte{0xA5},
        byte{0xEC},
        byte{0x20},
        byte{0x23},
        byte{0xF6},
        byte{0xB2}
      }
    };
    constexpr static std::array<byte, 23> Sample3
    {
      {
        byte{0xC3},
        byte{0x91},
        byte{0x0B},
        byte{0x50},
        byte{0xAF},
        byte{0x59},
        byte{0x5B},
        byte{0x30},
        byte{0x24},
        byte{0xDA},
        byte{0x22},
        byte{0x3C},
        byte{0x30},
        byte{0xBA},
        byte{0xDB},
        byte{0x1C},
        byte{0x18},
        byte{0x6F},
        byte{0xBB},
        byte{0xE6},
        byte{0x0B},
        byte{0x70},
        byte{0x0E}
      }
    };
    constexpr static std::array<byte, 0> Sample4{};
    constexpr static std::array<byte, 22> Sample5
    {
      byte{0xB4},
      byte{0xC7},
      byte{0xDB},
      byte{0xF4},
      byte{0x1F},
      byte{0x18},
      byte{0xEE},
      byte{0xC5},
      byte{0x67},
      byte{0x12},
      byte{0x6E},
      byte{0x96},
      byte{0x47},
      byte{0x4E},
      byte{0x98},
      byte{0x94},
      byte{0xFA},
      byte{0x6B},
      byte{0x90},
      byte{0xA6},
      byte{0x48},
      byte{0xF2}
    };

    constexpr static std::array<cdb_core::ReadOnlySpan<byte>, 5> s_samples
    {
      {
        cdb_core::ReadOnlySpan<byte>{Sample1},
        cdb_core::ReadOnlySpan<byte>{Sample2},
        cdb_core::ReadOnlySpan<byte>{Sample3},
        cdb_core::ReadOnlySpan<byte>{Sample4},
        cdb_core::ReadOnlySpan<byte>{Sample5},
      }
    };

    constexpr static uint32_t Expected[]
    {
      2786232081u,
      1744821187u,
      2853437495u,
      0u,
      2029626740u,
    };

    template<size_t N>
    static int64_t MeasureLoop(std::array<cdb_core::ReadOnlySpan<byte>, N> samples)
    {
      const int outerLoopCount = 10000;
      cdb_core::Stopwatch watch{};

      watch.Start();
      for (int j = 0; j < outerLoopCount; j++)
      {
        for (auto sample : samples)
        {
          [[maybe_unused]] int32_t crc = cdb_core::Crc32::Update(0, sample);
        }
      }

      watch.Stop();
      return watch.ElapsedRaw();
    }

  BEGIN_TEST_METHOD_ATTRIBUTE(Crc32Check)
      TEST_OWNER(L"jthunter")
    END_TEST_METHOD_ATTRIBUTE()

    TEST_METHOD(Crc32Check)
    {
      // Warm up the loop and verify correctness.
      for (size_t i = 0; i < s_samples.size(); i++)
      {
        cdb_core::ReadOnlySpan<byte> sample = s_samples[i];
        uint32_t c1 = cdb_core::Crc32::Update(0, sample);
        Assert::AreEqual(Expected[i], c1);
      }

      // Measure performance.
      int64_t ticks = MeasureLoop(s_samples);
      Logger::WriteMessage(cdb_core::make_string<std::string>("Crc32: %lld", ticks).c_str());
    }
  };
}
