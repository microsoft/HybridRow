// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <vector>
#include <memory>

namespace cdb_core
{
  template<typename T>
  struct DeepComparer final
  {
    bool operator()(const T& x, const T& y) const noexcept { return x == y; }
  };

  template<typename T>
  static bool DeepCompare(const T& x, const T& y) noexcept
  {
    return DeepComparer<T>{}(x, y);
  }

  template<typename T>
  struct DeepComparer<std::vector<T>> final
  {
    bool operator()(const std::vector<T>& x, const std::vector<T>& y) const noexcept
    {
      if (x.size() != y.size())
      {
        return false;
      }

      for (size_t i = 0; i < x.size(); i++)
      {
        if (!DeepCompare(x[i], y[i]))
        {
          return false;
        }
      }

      return true;
    }
  };

  template<typename T>
  struct DeepComparer<std::unique_ptr<T>> final
  {
    bool operator()(const std::unique_ptr<T>& x, const std::unique_ptr<T>& y) const noexcept
    {
      if ((x == nullptr) && (y == nullptr))
      {
        return true;
      }
      if ((x == nullptr) || (y == nullptr))
      {
        return false;
      }
      return DeepCompare(*x, *y);
    }
  };
}
