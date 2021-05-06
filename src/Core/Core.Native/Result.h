// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Failure.h"
#include <variant>

namespace cdb_core
{
  /// <summary>
  /// Result is analogous to an "Either" - holds a successful
  /// result or a failure. Result<> is a value type.
  /// </summary>
  template<typename TResult>
  class [[nodiscard]] Result
  {
  public:
    constexpr Result(TResult result) noexcept;
    constexpr Result(Failure failure) noexcept;
    Result(Result<TResult>&&) noexcept = default;
    ~Result() = default;
    Result& operator=(Result<TResult>&&) noexcept = default;

    [[nodiscard]] bool IsSuccess() const;
    TResult GetResult();

    [[nodiscard]] Failure GetFailure() const noexcept;

  private:
    std::variant<TResult, Failure> m_value;
  };

  template<typename TResult>
  constexpr Result<TResult>::Result(TResult result) noexcept : m_value{ std::in_place_index<0>, std::move(result) } { }

  template<typename TResult>
  constexpr Result<TResult>::Result(Failure failure) noexcept : m_value{ std::in_place_index<1>, std::move(failure) } { }

  template<typename TResult>
  TResult Result<TResult>::GetResult()
  {
    Contract::Requires(IsSuccess());
    return std::move(std::get<0>(m_value));
  }

  template<typename TResult>
  bool Result<TResult>::IsSuccess() const
  {
    return m_value.index() == 0;
  }

  template<typename TResult>
  Failure Result<TResult>::GetFailure() const noexcept
  {
      Contract::Requires(!IsSuccess());
      return std::move(std::get<1>(m_value));
  }

  template<>
  class [[nodiscard]] Result<void>
  {
  public:
    Result();
    explicit Result(Failure failure);
    Result(const Result<void>&) = default;
    Result(Result<void>&&) = default;
    Result<void>& operator=(const Result<void>&) = default;
    Result<void>& operator=(Result<void>&&) = default;

    [[nodiscard]]
    bool IsSuccess() const noexcept;

    [[nodiscard]]
    Failure GetFailure() const noexcept;

  private:
    Failure m_failure;
  };

  inline Result<void>::Result() : m_failure(Failure::None) { }

  inline Result<void>::Result(Failure failure) : m_failure(failure)
  {
    Contract::Requires(failure.IsFailed(),
      "Result constructed with Failure must be failed. Use Result() if no failure occurred");
  }

  inline bool Result<void>::IsSuccess() const noexcept
  {
    return !m_failure.IsFailed();
  }

  inline Failure Result<void>::GetFailure() const noexcept
  {
    return m_failure;
  }
}
