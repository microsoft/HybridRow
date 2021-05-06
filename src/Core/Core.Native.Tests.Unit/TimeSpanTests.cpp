// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(TimeSpanTests)
  {
    TEST_METHOD(TestTimeSpanCreationMilliseconds)
    {
      for (int64_t i = INT16_MIN; i < INT16_MAX; i += 97)
      {
        cdb_core::TimeSpan timeSpan = cdb_core::TimeSpan::FromMilliseconds(i);
        Assert::AreEqual(i, timeSpan.GetTotalMilliseconds());
        Assert::AreEqual(i / 1000, timeSpan.GetTotalSeconds());

        // 10,000 ticks in 1 ms
        Assert::AreEqual(i * 10000, timeSpan.GetTotalTicks());
      }
    }

    TEST_METHOD(TestTimeSpanCreationSeconds)
    {
      for (int64_t i = INT16_MIN; i < INT16_MAX; i += 137)
      {
        cdb_core::TimeSpan timeSpan = cdb_core::TimeSpan::FromSeconds(i);
        Assert::AreEqual(i * 1000, timeSpan.GetTotalMilliseconds());
        Assert::AreEqual(i, timeSpan.GetTotalSeconds());

        // 10,000,000 ticks in 1s
        Assert::AreEqual(i * 10000000, timeSpan.GetTotalTicks());
      }
    }
  };
}
