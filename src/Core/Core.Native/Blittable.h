// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
namespace cdb_core
{
  template<typename C>
  struct is_blittable
  {
    constexpr static bool value =
      std::is_nothrow_default_constructible_v<C> &&
      std::is_nothrow_copy_constructible_v<C> &&
      std::is_nothrow_copy_assignable_v<C>;
  };

  template<typename T>
  inline constexpr bool is_blittable_v = is_blittable<T>::value;
}
