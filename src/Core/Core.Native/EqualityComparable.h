// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

namespace cdb_core
{
  template<typename T, typename = void>
  struct is_equality_comparable : std::false_type { };

  /// <summary>
  /// Check for operator==.
  /// </summary>
  // ReSharper disable once CppIdenticalOperandsInBinaryExpression
  template<typename T>
  struct is_equality_comparable<T, typename std::enable_if<true, decltype(std::declval<T&>() == std::declval<T&>(), (
      void)0)>::type> : std::true_type { };

  template<typename T>
  inline constexpr bool is_equality_comparable_v = is_equality_comparable<T>::value;
}
