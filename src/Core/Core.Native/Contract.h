// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

// Forward Declarations
namespace cdb_core_test
{
  class ContractUnitTests;
}

namespace cdb_core
{
  class Contract final
  {
  public:
    Contract() = delete;
    ~Contract() = delete;
    Contract(const Contract& other) = delete;
    Contract(Contract&& other) noexcept = delete;
    Contract& operator=(const Contract& other) = delete;
    Contract& operator=(Contract&& other) noexcept = delete;

    constexpr static void Assert([[maybe_unused]] bool condition)
    {
      #if _DEBUG
      if (!condition)
      {
        Fail("Assert", std::string_view());
      }
      #endif
    }

    constexpr static void Assert([[maybe_unused]] bool condition, std::string_view message)
    {
      #if _DEBUG
      if (!condition)
      {
        Fail("Assert", message);
      }
      #endif
    }

    constexpr static void Requires(bool condition)
    {
      if (!condition)
      {
        Fail("Requires", std::string_view());
      }
    }

    constexpr static void Requires(bool condition, std::string_view message)
    {
      if (!condition)
      {
        Fail("Requires", message);
      }
    }

    constexpr static void Invariant(bool condition)
    {
      if (!condition)
      {
        Fail("Invariant", std::string_view());
      }
    }

    constexpr static void Invariant(bool condition, std::string_view message)
    {
      if (!condition)
      {
        Fail("Invariant", message);
      }
    }

    [[noreturn]] static void Fail()
    {
      Fail("Fail", std::string_view());
    }

    [[noreturn]] static void Fail(std::string_view message)
    {
      Fail("Fail", message);
    }

  private:
    friend class cdb_core_test::ContractUnitTests;
    [[noreturn]] static void Fail(std::string_view api, std::string_view message);
    [[nodiscard]] static std::wstring MakeError(std::string_view api, std::string_view message);
  };
}
