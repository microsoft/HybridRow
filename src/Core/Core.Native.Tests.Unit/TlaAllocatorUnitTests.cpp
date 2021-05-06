// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace tla::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  TEST_CLASS(TlaAllocatorUnitTests)
  {
  public:
    TEST_METHOD(StringTest) noexcept
    {
      const tla::string abc = cdb_core::string_join("a", "b", "c"_s);
      Assert::AreEqual("abc", abc.c_str());

      const tla::string abc2 = cdb_core::string_join(tla::string("a"), tla::string("b"), tla::string("c"));
      Assert::AreEqual("abc", abc2.c_str());

      const tla::wstring abc3 = cdb_core::string_join(tla::wstring(L"a"), tla::wstring(L"b"), tla::wstring(L"c"));
      Assert::AreEqual(L"abc", abc3.c_str());

      struct T
      {
        static size_t GetHashCode() noexcept { return 0; }
      };

      Logger::WriteMessage(typeid(&T::GetHashCode).name());
      Logger::WriteMessage("\n");
      Logger::WriteMessage(typeid(size_t (T::*)() const noexcept).name());
      Logger::WriteMessage("\n");
      const bool s = std::is_same<decltype(&T::GetHashCode), size_t (T::*)() const noexcept>::value;
      Logger::WriteMessage(cdb_core::make_string<tla::string>("%s\n", s ? "true" : "false").c_str());
    }
  };
}
