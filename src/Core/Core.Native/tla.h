// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <type_traits>
#include <string>
#include <vector>
#include <deque>
#include <forward_list>
#include <list>
#include <set>
#include <map>
#include <unordered_set>
#include <unordered_map>
#include <stack>
#include "Contract.h"

namespace tla
{
  template<typename T>
  struct allocator final : private std::allocator<T>
  {
    static_assert(!std::is_const_v<T>, "The C++ Standard forbids containers of const elements "
      "because allocator<const T> is ill-formed.");

    using value_type = T;
    using pointer = T*;
    using const_pointer = const T*;
    using reference = T&;
    using const_reference = const T&;

    using size_type = size_t;
    using difference_type = ptrdiff_t;

    using propagate_on_container_move_assignment = std::true_type;
    using is_always_equal = std::true_type;

    [[nodiscard]] constexpr allocator() noexcept = default;
    ~allocator() noexcept = default;
    [[nodiscard]] constexpr allocator(const allocator& other) noexcept = default;
    [[nodiscard]] constexpr allocator(allocator&& other) noexcept = default;
    constexpr allocator& operator=(const allocator& other) noexcept = default;
    constexpr allocator& operator=(allocator&& other) noexcept = default;

    template<class U>
    struct rebind
    {
      using other = allocator<U>;
    };

    template<class U>
    [[nodiscard]] constexpr allocator(const allocator<U>&) noexcept {}

    [[nodiscard]] T* allocate(const size_t n) noexcept
    {
      try
      {
        return std::allocator<T>::allocate(n);
      }
      catch (std::bad_alloc&)
      {
        cdb_core::Contract::Fail("Out of memory.");
      }
    }

    void deallocate(T* const p, const size_t n) noexcept
    {
      std::allocator<T>::deallocate(p, n);
    }
  };

  using string = std::basic_string<char, std::char_traits<char>, tla::allocator<char>>;
  using string_view = std::string_view;
  using wstring = std::basic_string<wchar_t, std::char_traits<wchar_t>, tla::allocator<wchar_t>>;
  using wstring_view = std::wstring_view;

  inline namespace literals
  {
    inline namespace string_literals
    {
      [[nodiscard]] inline string operator"" _s(const char* _Str, size_t _Len)
      {
        return string(_Str, _Len);
      }

      [[nodiscard]] inline wstring operator"" _s(const wchar_t* _Str, size_t _Len)
      {
        return wstring(_Str, _Len);
      }

      [[nodiscard]] constexpr string_view operator"" _sv(const char* _Str, size_t _Len) noexcept
      {
        return string_view(_Str, _Len);
      }

      [[nodiscard]] constexpr wstring_view operator"" _sv(const wchar_t* _Str, size_t _Len) noexcept
      {
        return wstring_view(_Str, _Len);
      }
    }
  }

  template<typename T>
  using vector = std::vector<T, tla::allocator<T>>;

  template<typename T>
  using deque = std::deque<T, tla::allocator<T>>;

  template<typename T, typename Container = deque<T>>
  using stack = std::stack<T, Container>;
 
  template<typename T>
  using forward_list = std::forward_list<T, tla::allocator<T>>;

  template<typename T>
  using list = std::list<T, tla::allocator<T>>;

  template<class Key, class Compare = std::less<Key>>
  using set = std::set<Key, Compare, tla::allocator<Key>>;

  template<class Key, class T, class Compare = std::less<Key>>
  using map = std::map<Key, T, Compare, tla::allocator<std::pair<const Key, T>>>;

  template<class Key, class Compare = std::less<Key>>
  using multiset = std::multiset<Key, Compare, tla::allocator<Key>>;

  template<class Key, class T, class Compare = std::less<Key>>
  using multimap = std::multimap<Key, T, Compare, tla::allocator<std::pair<const Key, T>>>;

  template<class Key, class Hash = std::hash<Key>, class Pred = std::equal_to<Key>>
  using unordered_set = std::unordered_set<Key, Hash, Pred, tla::allocator<Key>>;

  template<class Key, class T, class Hash = std::hash<Key>, class Pred = std::equal_to<Key>>
  using unordered_map = std::unordered_map<Key, T, Hash, Pred, tla::allocator<std::pair<const Key, T>>>;

  template<class Key, class Hash = std::hash<Key>, class Pred = std::equal_to<Key>>
  using unordered_multiset = std::unordered_multiset<Key, Hash, Pred, tla::allocator<Key>>;

  template<class Key, class T, class Hash = std::hash<Key>, class Pred = std::equal_to<Key>>
  using unordered_multimap = std::unordered_multimap<Key, T, Hash, Pred, tla::allocator<std::pair<const Key, T>>>;
}
