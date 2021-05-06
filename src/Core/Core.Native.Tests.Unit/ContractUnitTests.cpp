// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(ContractUnitTests)
  {
  public:

    /// <summary>
    /// Tests whether the string formatting in Contract works correctly for both
    /// empty and non-empty strings and views.
    /// </summary>
    TEST_METHOD(ContractFormatting)
    {
      std::wstring error = cdb_core::Contract::MakeError("Assert", "");
      Assert::AreEqual(0ull, error.find(L"Assert"s));
      error = cdb_core::Contract::MakeError("Assert", std::string_view());
      Assert::AreEqual(0ull, error.find(L"Assert"s));
      error = cdb_core::Contract::MakeError("Assert", "Some error message.");
      Assert::AreEqual(0ull, error.find(L"Assert"s));
      Assert::AreNotEqual(std::string::npos, error.find(L"Some error message."s));

      // ReSharper disable once StringLiteralTypo
      error = cdb_core::Contract::MakeError("Assert", "Some error message \0 with a null ASDFGHJKL.");
      Assert::AreEqual(0ull, error.find(L"Assert"s));
      Assert::AreNotEqual(std::string::npos, error.find(L"Some error message "s));
      Assert::AreEqual(std::string::npos, error.find(L"ASDFGHJKL"s));

      // ReSharper disable once StringLiteralTypo
      const std::string_view truncated = "some very long message ASDFGHJKL";
      error = cdb_core::Contract::MakeError("Assert", truncated.substr(0, 4));
      Assert::AreEqual(0ull, error.find(L"Assert"s));
      Assert::AreNotEqual(std::string::npos, error.find(L"some"s));
      Assert::AreEqual(std::string::npos, error.find(L"ASDFGHJKL"s));
    }
  };
}
