// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "CppUnitTest.h"

namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  struct CollectionAssert final
  {
    template<typename T>
    static void AreEqual(cdb_core::ReadOnlySpan<T> expected, cdb_core::ReadOnlySpan<T> value)
    {
      CollectionAssert::AreEqual<T>(expected, value, ""sv);
    }

    template<typename T>
    static void AreEqual(cdb_core::ReadOnlySpan<T> expected, cdb_core::ReadOnlySpan<T> value, std::wstring_view message)
    {
      Assert::AreEqual(expected.Length(), value.Length(), message.data());
      for (uint32_t i = 0; i < expected.Length(); i++)
      {
        Assert::AreEqual(expected[i], value[i], message.data());
      }
    }
  };
}
