// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "RowWriter.h"

// ReSharper disable CppClangTidyCppcoreguidelinesProTypeStaticCastDowncast
namespace cdb_hr
{
  Result RowWriter::PrepareSparseWrite(std::string_view path, const TypeArgument& typeArg) noexcept
  {
    if (m_cursor.m_scopeType->IsFixedArity() && !(m_cursor.m_scopeType->IsLayoutNullable()))
    {
      if ((m_cursor.m_index < m_cursor.m_scopeTypeArgs.GetCount()) &&
        (typeArg != m_cursor.m_scopeTypeArgs[m_cursor.m_index]))
      {
        return Result::TypeConstraint;
      }
    }
    else if (m_cursor.m_scopeType->IsLayoutTypedMap())
    {
      const LayoutUniqueScope* t = static_cast<const LayoutUniqueScope*>(m_cursor.m_scopeType);
      if (typeArg != t->FieldType(m_cursor))
      {
        return Result::TypeConstraint;
      }
    }
    else if (m_cursor.m_scopeType->IsTypedScope() && (typeArg != m_cursor.m_scopeTypeArgs[0]))
    {
      return Result::TypeConstraint;
    }

    m_cursor.m_writePath = path;
    return Result::Success;
  }
}
