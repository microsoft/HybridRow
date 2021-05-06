// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

namespace cdb_core
{
  /// <summary>Check for a ToString method.</summary>
  /// <remarks>Method must have this signature: 
  ///   <code>TString ToString() const noexcept</code>
  /// </remarks>
  template<typename C, typename TString = std::string>
  struct is_stringable
  {
  private:
    template<typename T>
    constexpr static typename std::is_same<decltype(&T::ToString), TString (T::*)() const noexcept>::type Check(T*)
    {
      return std::true_type{};
    };

    template<typename>
    constexpr static std::false_type Check(...) { return std::false_type{}; };
  public:
    constexpr static bool value = decltype(Check<std::remove_const_t<std::remove_reference_t<std::remove_pointer_t<C>>>
    >(nullptr))::value;
  };

  template<typename T, typename TString = std::string>
  inline constexpr bool is_stringable_v = is_stringable<T, TString>::value;
}
