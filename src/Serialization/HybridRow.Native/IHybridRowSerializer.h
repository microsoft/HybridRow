// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Result.h"
#include "RowBuffer.h"

namespace cdb_hr
{
  template<typename TSerializer>
  struct is_write_serializable;

  /// <summary>Write the object to a row.</summary>
  /// <param name="row">The row to write into.</param>
  /// <param name="scope">The position in the row at which to write.</param>
  /// <param name="isRoot">
  /// True if this object is the top-most element within the row such that the row's
  /// layout is the object, or false if object is a nested UDT within the column of some other object.
  /// </param>
  /// <param name="typeArgs">Type arguments if the object is a generic collection.</param>
  /// <param name="value">The object to write.</param>
  /// <returns>Success if the write is successful, an error code otherwise.</returns>
  template<class T, class TSerializer>
  struct is_write_serializable<
      Result(TSerializer::*)(RowBuffer&, RowCursor&, bool, const TypeArgumentList&, const T&) const noexcept>
  {
    constexpr static bool value = true;
  };

  template<class T>
  struct is_write_serializable<Result(*)(RowBuffer&, RowCursor&, bool, const TypeArgumentList&, const T&) noexcept>
  {
    constexpr static bool value = true;
  };

  template<typename TSerializer>
  struct is_write_serializable
  {
    constexpr static bool value = is_write_serializable<decltype(&TSerializer::Write)>::value;
  };

  template<typename TSerializer>
  struct is_read_serializable;

  /// <summary>Read and materialize and object from a row.</summary>
  /// <param name="row">The row to read from.</param>
  /// <param name="scope">The position in the row to read at.</param>
  /// <param name="isRoot">
  /// True if this object is the top-most element within the row such that the row's
  /// layout is the object, or false if object is a nested UDT within the column of some other object.
  /// </param>
  /// <returns>Success if the write is successful, an error code otherwise.</returns>
  template<class T, class TSerializer>
  struct is_read_serializable<
      std::tuple<Result, std::unique_ptr<T>>(TSerializer::*)(const RowBuffer&, RowCursor&, bool) const>
  {
    constexpr static bool value = true;
  };

  template<class T>
  struct is_read_serializable<std::tuple<Result, std::unique_ptr<T>>(*)(const RowBuffer&, RowCursor&, bool)>
  {
    constexpr static bool value = true;
  };

  template<class T>
  struct is_read_serializable<std::tuple<Result, T>(*)(const RowBuffer&, RowCursor&, bool)>
  {
    constexpr static bool value = true;
  };

  template<typename TSerializer>
  struct is_read_serializable
  {
    constexpr static bool value = is_read_serializable<decltype(&TSerializer::Read)>::value;
  };

  // ReSharper disable once CppInconsistentNaming
  template<typename T, typename TSerializer>
  struct is_hybridrow_serializer
  {
    constexpr static bool value =
      std::is_nothrow_default_constructible_v<TSerializer>
      && is_write_serializable<TSerializer>::value
      && is_read_serializable<TSerializer>::value;
  };

  // ReSharper disable once CppInconsistentNaming
  template<typename T, typename TSerializer>
  inline constexpr bool is_hybridrow_serializer_v = is_hybridrow_serializer<T, TSerializer>::value;

  struct IHybridRowSerializer final
  {
    static const LayoutColumn& InitLayoutColumn(const Layout& layout, const std::string_view& name) noexcept
    {
      auto [found, col] = layout.TryFind(name);
      cdb_core::Contract::Invariant(found);
      cdb_core::Contract::Invariant(col != nullptr);
      return *col;
    }

    static const StringTokenizer::StringToken& InitStringToken(const Layout& layout,
                                                               const std::string_view& name) noexcept
    {
      auto [found, tok] = layout.GetTokenizer().TryFindToken(name);
      cdb_core::Contract::Invariant(found);
      return tok;
    }

    template<typename T>
    constexpr static bool is_default(const T* value) { return value == nullptr; }

    template<typename T>
    constexpr static bool is_default(const std::unique_ptr<T>& value) { return value == nullptr; }

    template<typename T>
    constexpr static bool is_default(const std::optional<T> value) { return !value.has_value(); }

    template<typename T,
      typename = std::enable_if_t<std::conjunction_v<std::is_default_constructible<T>,
        std::disjunction<cdb_core::is_equality_comparable<T>, std::is_enum<T>>>>>
    constexpr static bool is_default(const T& value) { return value == T{}; }

    template<typename TElem, typename TTraits>
    constexpr static bool is_default(const std::basic_string_view<TElem, TTraits> value) { return false; }

    template<typename TElem, typename TTraits, typename TAlloc>
    constexpr static bool is_default(const std::basic_string<TElem, TTraits, TAlloc> value) { return false; }

    template<typename T>
    constexpr static bool is_default(const std::vector<T>& value) { return false; }

    template<typename T,
      typename = std::enable_if_t<std::conjunction_v<std::is_default_constructible<T>,
        std::disjunction<cdb_core::is_equality_comparable<T>, std::is_enum<T>>>>>
    constexpr static bool is_default_or_empty(T value) { return value == T{}; }

    template<typename T>
    constexpr static bool is_default_or_empty(const std::vector<T>& value) { return value.empty(); }

    template<typename TElem, typename TTraits>
    constexpr static bool is_default_or_empty(const std::basic_string_view<TElem, TTraits> value)
    {
      return value.empty();
    }

    template<typename TElem, typename TTraits, typename TAlloc>
    constexpr static bool is_default_or_empty(const std::basic_string<TElem, TTraits, TAlloc> value)
    {
      return value.empty();
    }

    template<typename T>
    constexpr static bool is_default_or_empty(const std::optional<T> value)
    {
      return !value.has_value() || IHybridRowSerializer::is_default_or_empty(*value);
    }

    template<typename T>
    constexpr static const T& get(const T* value)
    {
      cdb_core::Contract::Requires(value != nullptr);
      return *value;
    }

    template<typename T>
    constexpr static const T& get(const std::unique_ptr<T>& value)
    {
      cdb_core::Contract::Requires(value != nullptr);
      return *value;
    }

    template<typename T>
    constexpr static const T& get(std::optional<std::reference_wrapper<const T>> value)
    {
      cdb_core::Contract::Requires(value.has_value());
      return *value;
    }

    template<typename T>
    constexpr static const T& get(const T& value)
    {
      return value;
    }

    template<typename TReturn, typename... TArgs>
    constexpr static std::vector<std::unique_ptr<TReturn>> make_unique_vector(std::unique_ptr<TArgs>... args)
    {
      std::vector<std::unique_ptr<TReturn>> retval{};
      retval.reserve(std::tuple_size_v<std::tuple<TArgs...>>);
      (retval.emplace_back(std::move(args)), ...);
      return std::move(retval);
    }
  };
}
