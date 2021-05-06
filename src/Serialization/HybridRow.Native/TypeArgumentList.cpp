// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "TypeArgument.h"
#include "TypeArgumentList.h"
#include "LayoutType.h"

namespace cdb_hr
{
  TypeArgumentList::TypeArgumentList(const tla::vector<TypeArgument>& args) noexcept :
    m_args{std::allocate_shared<tla::vector<TypeArgument>>(tla::allocator<tla::vector<TypeArgument>>{}, args)},
    m_schemaId{} { }

  TypeArgumentList::TypeArgumentList(tla::vector<TypeArgument>&& args) noexcept :
    m_args{std::allocate_shared<tla::vector<TypeArgument>>(tla::allocator<tla::vector<TypeArgument>>{}, args)},
    m_schemaId{} { }

  TypeArgumentList::TypeArgumentList(const TypeArgument& arg1) noexcept :
    m_args{std::allocate_shared<tla::vector<TypeArgument>>(tla::allocator<tla::vector<TypeArgument>>{}, 1, arg1)},
    m_schemaId{} { }

  TypeArgumentList::TypeArgumentList(SchemaId schemaId) noexcept :
    m_args{nullptr, std::default_delete<tla::vector<TypeArgument>>{}, tla::allocator<tla::vector<TypeArgument>>{}},
    m_schemaId{schemaId} { }

  size_t TypeArgumentList::GetCount() const noexcept { return m_args == nullptr ? 0 : m_args->size(); }
  SchemaId TypeArgumentList::GetSchemaId() const noexcept { return m_schemaId; }
  const TypeArgument& TypeArgumentList::operator[](size_t i) const noexcept { return (*m_args)[i]; }

  TypeArgumentList::Enumerator TypeArgumentList::begin() const
  {
    return TypeArgumentList::Enumerator(m_args.get(), 0);
  }

  TypeArgumentList::Enumerator TypeArgumentList::end() const
  {
    return TypeArgumentList::Enumerator(m_args.get(), GetCount());
  }

  tla::string TypeArgumentList::ToString() const noexcept
  {
    static_assert(cdb_core::is_stringable_v<decltype(this), tla::string>);

    if (m_schemaId != SchemaId::Invalid())
    {
      return cdb_core::make_string("<%s>", m_schemaId.ToString().c_str());
    }

    if (m_args == nullptr || m_args->empty())
    {
      return tla::string();
    }

    tla::string ret{};
    for (const auto& s : *m_args)
    {
      ret += s.ToString();
    }
    return cdb_core::make_string("<%s>", ret.c_str());
  }

  size_t TypeArgumentList::GetHashCode() const noexcept
  {
    static_assert(cdb_core::is_hashable_v<decltype(this)>);

    size_t hash = 19;
    hash = (hash * 397) ^ m_schemaId.GetHashCode();
    if (m_args != nullptr)
    {
      for (const auto& a : *m_args)
      {
        hash = (hash * 397) ^ a.GetHashCode();
      }
    }

    return hash;
  }

  bool operator==(const TypeArgumentList& left, const TypeArgumentList& right) noexcept
  {
    return (left.m_schemaId == right.m_schemaId) && left.m_args == right.m_args;
  }

  bool operator!=(const TypeArgumentList& left, const TypeArgumentList& right) noexcept
  {
    return !(left == right);
  }

  TypeArgumentList::Enumerator& TypeArgumentList::Enumerator::operator++() noexcept
  {
    m_index++;
    return *this;
  }

  TypeArgumentList::Enumerator TypeArgumentList::Enumerator::operator++(int) noexcept
  {
    m_index++;
    return Enumerator(m_list, m_index - 1);
  }

  const TypeArgument& TypeArgumentList::Enumerator::operator*() const noexcept
  {
    return (*m_list)[m_index];
  }

  TypeArgumentList::Enumerator::Enumerator(const tla::vector<TypeArgument>* list, size_t index) noexcept :
    m_list(list), m_index(index)
  {
    cdb_core::Contract::Requires(list != nullptr);
    cdb_core::Contract::Requires(index <= list->size());
  }

  bool operator==(const TypeArgumentList::Enumerator& lhs, const TypeArgumentList::Enumerator& rhs)
  {
    return (lhs.m_list == rhs.m_list) && (lhs.m_index == rhs.m_index);
  }

  bool operator!=(const TypeArgumentList::Enumerator& lhs, const TypeArgumentList::Enumerator& rhs)
  {
    return !(lhs == rhs);
  }
}
