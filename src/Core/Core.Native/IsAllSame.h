// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <type_traits>

namespace cdb_core
{
  template<typename L, typename R, typename... Rest>
  struct is_all_same
  {
    constexpr static bool value = 
      std::is_same<L, R>::value && is_all_same<L, Rest...>::value;
  };

  template<typename L, typename R>
  struct is_all_same<L, R>
  {
    constexpr static bool value = std::is_same<L, R>::value;
  };

  template<typename L, typename... R>
  inline constexpr bool is_all_same_v = is_all_same<L, R...>::value;
}
