// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  TEST_CLASS(StringsUnitTests)
  {
  public:
    /// <summary>
    /// Verifies the code samples used in the Doc Comments actually compile.
    /// </summary>
    TEST_METHOD(DocCommentsExamples)
    {
      {
        std::string u8 = cdb_core::make_string("foo: %s", "some value");
        std::wstring u16 = cdb_core::string_join(L"foo: %s", L"some wide-string value");
        tla::string s = cdb_core::string_join("foo: %s", "some value");
      }

      {
        Logger::WriteMessage(cdb_core::make_string<std::string>("foo: %s", "some value").c_str());
        Logger::WriteMessage(cdb_core::make_string<std::wstring>(L"foo: %s", L"some wide-string value").c_str());
      }

      {
        std::string u8 = cdb_core::string_join("a", "b", "c");
        std::wstring u16 = cdb_core::string_join(L"a", L"b", L"c");
        tla::string s = cdb_core::string_join("a", "b", "c");
      }
    }

    /// <summary>
    /// Verifies that make_string inserts the arguments in the proper order.
    /// </summary>
    TEST_METHOD(MakeStringOrderTest)
    {
      MakeStringOrder<std::string>("%s %s %s", "a", "b", "c", "a b c");
      MakeStringOrder<std::wstring>(L"%s %s %s", L"a", L"b", L"c", L"a b c");
      MakeStringOrder<tla::string>("%s %s %s", "a", "b", "c", "a b c");
      MakeStringOrder<tla::wstring>(L"%s %s %s", L"a", L"b", L"c", L"a b c");
    }

    /// <summary>
    /// Verifies that make_string properly supports string precision format specifiers.
    /// </summary>
    TEST_METHOD(MakeStringPrecisionTest)
    {
      MakeStringPrecision<std::string>("a %.*s c", "b"sv, "a b c");
      MakeStringPrecision<std::wstring>(L"a %.*s c", L"b"sv, L"a b c");
      MakeStringPrecision<tla::string>("a %.*s c", "b"sv, "a b c");
      MakeStringPrecision<tla::wstring>(L"a %.*s c", L"b"sv, L"a b c");
    }

    /// <summary>
    /// Verifies that string join return type inference works across various return types.
    /// </summary>
    TEST_METHOD(StringJoinTest)
    {
      StringJoin<std::string>("a", "b", "c", "abc");
      StringJoin<std::wstring>(L"a", L"b", L"c", L"abc");
      StringJoin<tla::string>("a", "b", "c", "abc");
      StringJoin<tla::wstring>(L"a", L"b", L"c", L"abc");
    }

    /// <summary>
    /// Verifies that string join accepts heterogeneous argument types as long as the return
    /// value has an append operator with that rvalue.
    /// </summary>
    TEST_METHOD(HeterogeneousStringJoinTest)
    {
      const std::string u8 = cdb_core::string_join("a", "b"s, "c"sv);
      Assert::AreEqual("abc", u8.c_str());
      const tla::string s = cdb_core::string_join("a", "b"s, "c"sv);
      Assert::AreEqual("abc", s.c_str());
    }

  private:
    template<typename TString, typename TElem = typename TString::value_type>
    void StringJoin(const TElem* a, const TElem* b, const TElem* c, const TElem* expected)
    {
      Logger::WriteMessage(cdb_core::make_string<std::string>("%s\n", typeid(TString).name()).c_str());
      TString abc = cdb_core::string_join(a, b, c);
      Assert::AreEqual(expected, abc.c_str());

      TString abc2 = cdb_core::string_join(TString(a), TString(b), TString(c));
      Assert::AreEqual(expected, abc2.c_str());
    }

    template<typename TString, typename TElem = typename TString::value_type>
    void MakeStringOrder(const TElem* format, const TElem* a, const TElem* b, const TElem* c, const TElem* expected)
    {
      TString s = cdb_core::make_string<TString>(format, a, b, c);
      Assert::AreEqual(expected, s.data());
    }

    template<typename TString, typename TElem = typename TString::value_type>
    void MakeStringPrecision(const TElem* format, std::basic_string_view<TElem> arg, const TElem* expected)
    {
      TString s = cdb_core::make_string<TString>(format, arg.size(), arg.data());
      Assert::AreEqual(expected, s.data());
    }
  };
}
