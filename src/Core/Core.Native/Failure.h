// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Contract.h"

namespace cdb_core
{
  /// <summary>
  /// Failure is a wrapper around Failed states in the system.
  /// </summary>
  /// <remarks>
  /// Encapsulates a Failure Code specific to an application
  /// and an associated Win32 HResult if the failure originated
  /// from a windows API call.
  /// </remarks>
  class [[nodiscard]] Failure
  {
  public:
    static const Failure None;
    constexpr static uint32_t NoneCode = 0;

    template<typename TEnum, typename = std::enable_if_t<std::is_enum<TEnum>::value, int>>
    Failure(const TEnum failureCode) noexcept;
    template<typename TEnum, typename = std::enable_if_t<std::is_enum<TEnum>::value, int>>
    Failure(const TEnum failureCode, const HRESULT hResult) noexcept;
    ~Failure() = default;
    Failure(const Failure&) = default;
    Failure(Failure&&) = default;

    Failure& operator=(const Failure& other);
    Failure& operator=(Failure&&) = default;

    [[nodiscard]] bool IsFailed() const noexcept;

    [[nodiscard]] uint32_t GetFailureCode() const noexcept;

    template<typename TEnum, typename = std::enable_if_t<std::is_enum<TEnum>::value, int>>
    [[nodiscard]] TEnum GetFailureCode() const noexcept;

    [[nodiscard]] HRESULT GetHResult() const noexcept;

  private:

    constexpr explicit Failure() : m_failureCode(NoneCode), m_hResult{0} { }

    uint32_t m_failureCode;
    HRESULT m_hResult;
  };

  template<typename TEnum, typename>
  Failure::Failure(const TEnum failureCode) noexcept : Failure(failureCode, S_OK) { }

  template<typename TEnum, typename>
  Failure::Failure(const TEnum failureCode, const HRESULT hResult) noexcept :
    m_failureCode(static_cast<uint32_t>(failureCode)),
    m_hResult(hResult)
  {
    Contract::Requires(m_failureCode != 0,
      "Failure constructor should not use FailureCode::None. Use Failure::None instead");
  }

  inline bool Failure::IsFailed() const noexcept { return m_failureCode != NoneCode; }

  inline uint32_t Failure::GetFailureCode() const noexcept { return m_failureCode; }

  template<typename TEnum, typename>
  TEnum Failure::GetFailureCode() const noexcept { return static_cast<TEnum>(m_failureCode); }

  inline HRESULT Failure::GetHResult() const noexcept { return m_hResult; }
}
