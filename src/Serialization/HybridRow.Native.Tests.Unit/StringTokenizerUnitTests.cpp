// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"

namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(StringTokenizerUnitTests)
  {
  public:

    TEST_METHOD_WITH_OWNER(RoundTripTest, "jthunter")
    {
      cdb_hr::StringTokenizer tokenizer{};

      const auto& fooToken = tokenizer.Add("foo"sv);
      Assert::IsFalse(fooToken.IsNull());
      Assert::AreEqual("foo"sv, fooToken.GetPath());
      Assert::AreNotEqual(static_cast<uint32_t>(0), fooToken.GetVarint().Length());
      const auto& [fooSuccess, fooString] = tokenizer.TryFindString(fooToken.GetId());
      Assert::IsTrue(fooSuccess);
      Assert::AreEqual("foo"sv, fooString);

      const auto& barToken = tokenizer.Add("bar"sv);
      Assert::IsFalse(barToken.IsNull());
      Assert::AreEqual("bar"sv, barToken.GetPath());
      Assert::AreNotEqual(static_cast<uint32_t>(0), barToken.GetVarint().Length());
      const auto& [barSuccess, barString] = tokenizer.TryFindString(barToken.GetId());
      Assert::IsTrue(barSuccess);
      Assert::AreEqual("bar"sv, barString);

      Assert::AreNotEqual(barToken.GetId(), fooToken.GetId());
      Assert::AreNotEqual(barToken.GetPath(), fooToken.GetPath());
      Assert::AreNotEqual(barToken.GetVarint(), fooToken.GetVarint());
    }
  };
}
