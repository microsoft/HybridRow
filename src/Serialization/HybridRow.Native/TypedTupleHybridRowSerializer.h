// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "IHybridRowSerializer.h"
#include "LayoutType.h"
#include "RowCursor.h"

namespace cdb_hr
{
  template<typename... TSerializer>
  struct TypedTupleHybridRowSerializer final
  {
    static_assert(std::conjunction_v<is_hybridrow_serializer<typename TSerializer::value_type, TSerializer>...>);
    struct TypedTupleComparer;
    using value_type = std::tuple<typename TSerializer::owning_type...>;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = TypedTupleComparer;

    static Result Write(
      RowBuffer& row,
      RowCursor& scope,
      bool isRoot,
      const TypeArgumentList& typeArgs,
      const value_type& value) noexcept
    {
      cdb_core::Contract::Assert(!isRoot);
      auto [r, childScope] = LayoutLiteral::TypedTuple.WriteScope(row, scope, typeArgs);
      WriteCols(r, row, childScope, typeArgs, value, std::make_index_sequence<std::tuple_size_v<value_type>>{});
      if (r != Result::Success)
      {
        return r;
      }

      scope.Skip(row, childScope);
      return Result::Success;
    }

    static std::tuple<Result, value_type> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      cdb_core::Contract::Assert(!isRoot);
      auto [r, childScope] = LayoutLiteral::TypedTuple.ReadScope(row, scope);
      value_type retval{};
      ReadCols(r, row, childScope, retval, std::make_index_sequence<std::tuple_size_v<value_type>>{});
      if (r != Result::Success)
      {
        return {r, value_type{}};
      }

      scope.Skip(row, childScope);
      return {Result::Success, std::move(retval)};
    }

    struct TypedTupleComparer final
    {
      constexpr bool operator()(const_reference x, const_reference y) const
      {
        return EqualCols(x, y, std::make_index_sequence<std::tuple_size_v<value_type>>{});
      }

      constexpr std::size_t operator()(const_reference s) const noexcept
      {
        cdb_core::HashCode hash{};
        HashCols(hash, s, std::make_index_sequence<std::tuple_size_v<value_type>>{});
        return hash.ToHashCode();
      }

    private:
      template<std::size_t... I>
      static bool EqualCols(const_reference x, const_reference y, std::index_sequence<I...>) noexcept
      {
        return (... && (EqualCol<TSerializer>(std::get<I>(x), std::get<I>(y))));
      }

      template<typename TS>
      static bool EqualCol(const typename TS::owning_type& x, const typename TS::owning_type& y) noexcept
      {
        return typename TS::comparer_type{}.operator()(x, y);
      }

      template<std::size_t... I>
      static void HashCols(cdb_core::HashCode& hash, const_reference s, std::index_sequence<I...>) noexcept
      {
        ((HashCol<TSerializer>(hash, std::get<I>(s))), ...);
      }

      template<typename TS>
      static void HashCol(cdb_core::HashCode& hash, const typename TS::owning_type& s) noexcept
      {
        hash.Add<typename TS::owning_type, typename TS::comparer_type>(s);
      }
    };

  private:
    template<std::size_t... I>
    static void WriteCols(Result& r, RowBuffer& row, RowCursor& childScope, const TypeArgumentList& typeArgs,
                          const value_type& value, std::index_sequence<I...>) noexcept
    {
      (((r != Result::Success)
          ? void(0)
          : WriteCol<TSerializer>(r, row, childScope, typeArgs[I].GetTypeArgs(), std::get<I>(value))), ...);
    }

    template<typename TS>
    static void WriteCol(Result& r, RowBuffer& row, RowCursor& childScope, const TypeArgumentList& typeArgs,
                         const typename TS::owning_type& value) noexcept
    {
      r = TS::Write(row, childScope, false, typeArgs, value);
      if (r == Result::Success)
      {
        childScope.MoveNext(row);
      }
    }

    template<std::size_t... I>
    static void ReadCols(Result& r, const RowBuffer& row, RowCursor& childScope, value_type& value,
                         std::index_sequence<I...>) noexcept
    {
      (((r != Result::Success) ? void(0) : ReadCol<TSerializer>(r, row, childScope, std::get<I>(value))), ...);
    }

    template<typename TS>
    static void ReadCol(Result& r, const RowBuffer& row, RowCursor& childScope, typename TS::owning_type& item)
    {
      if (childScope.MoveNext(row))
      {
        std::tie(r, item) = TS::Read(row, childScope, false);
      }
    }
  };
}
