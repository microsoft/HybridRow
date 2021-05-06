// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>

namespace cdb_core
{
  // forward declaration
  template<class F>
  struct make_unique_with_traits;

  // function pointer
  template<class R, class Arg>
  struct make_unique_with_traits<R(*)(Arg)>
  {
    using return_type = R;
    using arg_type = Arg;
  };

  // member function pointer
  template<class C, class R, class Arg>
  struct make_unique_with_traits<R(C::*)(Arg)> : make_unique_with_traits<R(*)(Arg)> {};

  // const member function pointer
  template<class C, class R, class Arg>
  struct make_unique_with_traits<R(C::*)(Arg) const> : make_unique_with_traits<R(*)(Arg)> {};

  /// <summary>Trait to derive return type and argument type from a generic callable.</summary>
  template<class TCallable>
  struct make_unique_with_traits
  {
    using call_type = make_unique_with_traits<decltype(&TCallable::operator())>;
    using return_type = typename call_type::return_type;
    using arg_type = std::remove_reference_t<typename call_type::arg_type>;
  };

  // ReSharper disable once CppInconsistentNaming
  /// <summary>Makes a new object that uses delayed initialization.</summary>
  template<typename TCallable,
    typename Traits = make_unique_with_traits<TCallable>,
    typename T = typename Traits::arg_type,
    typename = std::enable_if_t<std::is_invocable_r_v<void, TCallable, T&>>>
  static std::unique_ptr<T> make_unique_with(TCallable&& initializer)
  {
    std::unique_ptr<T> p = std::make_unique<T>();
    initializer(*p);
    return std::move(p);
  }

  // ReSharper disable once CppInconsistentNaming
  /// <summary>Makes a new object that uses delayed initialization.</summary>
  template<typename TCallable,
    typename Traits = make_unique_with_traits<TCallable>,
    typename T = typename Traits::arg_type,
    typename = std::enable_if_t<std::is_invocable_r_v<void, TCallable, T&>>>
  static T make_with(TCallable&& initializer)
  {
    T retval = T();
    initializer(retval);
    return std::move(retval);
  }
}
