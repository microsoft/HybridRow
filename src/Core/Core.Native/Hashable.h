// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_core
{
  /// <summary>Check for a GetHashCode method.</summary>
  /// <remarks>Method must have this signature: 
  ///   <code>size_t GetHashCode() const noexcept</code>
  /// </remarks>
  template<typename C>
  struct is_hashable
  {
  private:
    template<typename T>
    constexpr static typename std::is_same<decltype(&T::GetHashCode), size_t (T::*)() const noexcept>::type Check(T*)
    {
      return std::true_type{};
    }

    template<typename>
    constexpr static std::false_type Check(...) { return std::false_type{}; }

  public:
    constexpr static bool value = decltype(
      Check<std::remove_const_t<std::remove_reference_t<std::remove_pointer_t<C>>>>(nullptr))::value;
  };

  template<typename T>
  inline constexpr bool is_hashable_v = is_hashable<T>::value;
}
