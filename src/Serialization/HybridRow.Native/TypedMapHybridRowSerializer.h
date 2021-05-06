// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "IHybridRowSerializer.h"
#include "LayoutType.h"
#include "RowCursor.h"
#include "TypedTupleHybridRowSerializer.h"

namespace cdb_hr
{
  template<typename TKeySerializer, typename TValueSerializer,
  typename = std::enable_if_t<is_hybridrow_serializer_v<typename TKeySerializer::owning_type, TKeySerializer>>,
  typename = std::enable_if_t<is_hybridrow_serializer_v<typename TValueSerializer::owning_type, TValueSerializer>>>
  struct TypedMapHybridRowSerializer final
  {
    struct TypedMapComparer;
    using TKey = typename TKeySerializer::owning_type;
    using TValue = typename TValueSerializer::owning_type;
    using value_type = std::unordered_map<
      typename TKeySerializer::owning_type,
      typename TValueSerializer::owning_type,
      typename TKeySerializer::comparer_type,
      typename TKeySerializer::comparer_type
      >;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = TypedMapComparer;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                 const_reference value) noexcept
    {
      cdb_core::Contract::Assert(!isRoot);
      auto [r, uniqueScope] = LayoutLiteral::TypedMap.WriteScope(row, scope, typeArgs);
      if (r != Result::Success)
      {
        return r;
      }

      RowCursor childScope = uniqueScope.Clone();
      childScope.m_deferUniqueIndex = true;

      for (std::pair<const TKey, TValue> item : value)
      {
          r = cdb_hr::TypedTupleHybridRowSerializer<TKeySerializer, TValueSerializer>
              ::Write(row, childScope, false, typeArgs, std::make_tuple(item.first, item.second));
          if (r != Result::Success)
          {
              return r;
          }

          childScope.MoveNext(row);
      }

      uniqueScope.m_count = childScope.m_count;
      r = row.TypedCollectionUniqueIndexRebuild(uniqueScope);
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
      auto [r, childScope] = LayoutLiteral::TypedMap.ReadScope(row, scope);
      if (r != Result::Success)
      {
        return {r, value_type{}};
      }

      value_type items{};
      while (childScope.MoveNext(row))
      {
        auto [r, item] = cdb_hr::TypedTupleHybridRowSerializer<TKeySerializer, TValueSerializer>
          ::Read(row, childScope, isRoot);
        if (r != Result::Success)
        {
          return {r, value_type{}};
        }

        items.emplace(std::make_pair(std::get<0>(item), std::get<1>(item)));
      }

      scope.Skip(row, childScope);
      return {Result::Success, std::move(items)};
    }

    struct TypedMapComparer final
    {
      constexpr bool operator()(const_reference x, const_reference y) const
      {
        if (&x == &y)
        {
          return true;
        }
        if (x.size() != y.size())
        {
          return false;
        }

        for (const auto&p : x)
        {
          typename value_type::const_iterator iter = y.find(p.first);
          if (iter == y.end())
          {
              return false;
          }

          if (!typename TValueSerializer::comparer_type{}.operator()(p.second, iter->second))
          {
            return false;
          }

        }

        return true;
      }

      constexpr std::size_t operator()(const_reference s) const noexcept
      {
        cdb_core::HashCode hash{};
        for (std::pair<const TKey, TValue> p : s)
        {
            hash.Add<TKey, typename TKeySerializer::comparer_type>(p.first);
            hash.Add<TValue, typename TValueSerializer::comparer_type>(p.second);
        }
        return hash.ToHashCode();
      }
    };
  };
}
