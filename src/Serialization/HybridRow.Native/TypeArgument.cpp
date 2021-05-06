// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include <utility>
#include "TypeArgument.h"
#include "TypeArgumentList.h"
#include "LayoutType.h"

namespace cdb_hr
{
  using namespace tla::literals;

  TypeArgument::TypeArgument(const LayoutType* type, TypeArgumentList typeArgs) noexcept :
    m_type{type},
    m_typeArgs(std::move(typeArgs))
  {
    cdb_core::Contract::Requires(type != nullptr);
  }

  const LayoutType* TypeArgument::GetType() const noexcept { return m_type; }
  const TypeArgumentList& TypeArgument::GetTypeArgs() const noexcept { return m_typeArgs; }

  tla::string TypeArgument::ToString() const noexcept
  {
    static_assert(cdb_core::is_stringable_v<decltype(this), tla::string>);

    if (m_type == nullptr)
    {
      return ""_s;
    }

    tla::string_view name = m_type->GetName();
    return tla::string(name.data(), name.size()) + m_typeArgs.ToString();
  }

  size_t TypeArgument::GetHashCode() const noexcept
  {
    static_assert(cdb_core::is_hashable_v<decltype(this)>);

    if (m_type == nullptr)
    {
      return 397;
    }

    return (m_type->GetHashCode() * 397) ^ m_typeArgs.GetHashCode();
  }

  bool operator==(const TypeArgument& lhs, const TypeArgument& rhs)
  {
    return lhs.m_type == rhs.m_type && lhs.m_typeArgs == rhs.m_typeArgs;
  }

  bool operator!=(const TypeArgument& lhs, const TypeArgument& rhs) { return !(lhs == rhs); }
}
