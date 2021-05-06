// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "IHybridRowSerializer.h"
#include "LayoutType.h"
#include "RowCursor.h"

namespace cdb_hr
{
  template<typename T, typename TSerializer, typename = std::enable_if_t<is_hybridrow_serializer_v<T, TSerializer>>>
  struct ArrayHybridRowSerializer final
  {
    struct ArrayComparer;
    using value_type = std::vector<typename TSerializer::owning_type>;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = ArrayComparer;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const_reference value) noexcept
    {
      auto [r, childScope] = LayoutLiteral::Array.WriteScope(row, scope, TypeArgumentList{});
      if (r != Result::Success)
      {
        return r;
      }

      for (auto& item : value)
      {
        r = TSerializer::Write(row, childScope, false, typeArgs[0].GetTypeArgs(), IHybridRowSerializer::get(item));
        if (r != Result::Success)
        {
          return r;
        }

        childScope.MoveNext(row);
      }

      scope.Skip(row, childScope);
      return Result::Success;
    }

    static std::tuple<Result, value_type> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      auto [r, childScope] = LayoutLiteral::Array.ReadScope(row, scope);
      if (r != Result::Success)
      {
        return {r, value_type{}};
      }

      value_type items{};
      while (childScope.MoveNext(row))
      {
        auto [r, item] = TSerializer::Read(row, childScope, isRoot);
        if (r != Result::Success)
        {
          return {r, value_type{}};
        }

        items.emplace_back(std::move(item));
      }

      scope.Skip(row, childScope);
      return {Result::Success, std::move(items)};
    }

    struct ArrayComparer final
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

        for (size_t i = 0; i < x.size(); i++)
        {
          if (!typename TSerializer::comparer_type{}.operator()(x[i], y[i]))
          {
            return false;
          }
        }

        return true;
      }

      constexpr std::size_t operator()(const_reference s) const noexcept
      {
        cdb_core::HashCode hash{};
        for (size_t i = 0; i < s.size(); i++)
        {
          hash.Add<T, typename TSerializer::comparer_type>(s[i]);
        }
        return hash.ToHashCode();
      }
    };
  };
}
