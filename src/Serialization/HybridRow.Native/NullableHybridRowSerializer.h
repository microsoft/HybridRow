// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "IHybridRowSerializer.h"
#include "LayoutType.h"
#include "RowCursor.h"

namespace cdb_hr
{
  template<typename TNullable, typename T, typename TSerializer,
    typename = std::enable_if_t<is_hybridrow_serializer_v<T, TSerializer>>>
  struct NullableHybridRowSerializer final
  {
    struct NullableComparerComparer;
    using value_type = TNullable;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = NullableComparerComparer;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const TNullable& value) noexcept
    {
      cdb_core::Contract::Assert(!isRoot);
      bool hasValue = HasValue(value);
      auto [r, childScope] = LayoutLiteral::Nullable.WriteScope(row, scope, typeArgs, hasValue);
      if (r != Result::Success)
      {
        return r;
      }

      if (hasValue)
      {
        r = TSerializer::Write(row, childScope, false, typeArgs[0].GetTypeArgs(), AsValue(value));
        if (r != Result::Success)
        {
          return r;
        }
      }

      scope.Skip(row, childScope);
      return Result::Success;
    }

    static std::tuple<Result, TNullable> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      cdb_core::Contract::Assert(!isRoot);
      auto [r, childScope] = LayoutLiteral::Nullable.ReadScope(row, scope);
      if (r != Result::Success)
      {
        return {r, value_type{}};
      }

      if (childScope.MoveNext(row))
      {
        auto [r, item] = TSerializer::Read(row, childScope, isRoot);
        if (r != Result::Success)
        {
          return {r, value_type{}};
        }

        scope.Skip(row, childScope);
        return {Result::Success, std::move(AsNullable(std::move(item)))};
      }

      scope.Skip(row, childScope);
      return {Result::Success, TNullable{}};
    }

    struct NullableComparerComparer final
    {
      constexpr bool operator()(const_reference x, const_reference y) const
      {
        bool xHasValue = HasValue(x);
        if (xHasValue != HasValue(y))
        {
          return false;
        }
        if (!xHasValue)
        {
          return true;
        }

        return typename TSerializer::comparer_type{}.operator()(AsValue(x), AsValue(y));
      }

      constexpr std::size_t operator()(const_reference s) const noexcept
      {
        cdb_core::HashCode hash{};
        if (HasValue(s))
        {
          hash.Add<T, typename TSerializer::comparer_type>(AsValue(s));
        }
        return hash.ToHashCode();
      }
    };

  private:
    static bool HasValue(const TNullable& value)
    {
      return value.operator bool();
    }

    static TNullable AsNullable(T value)
    {
      return TNullable{value};
    }

    static const T& AsValue(const TNullable& value)
    {
      return *value;
    }
  };
}
