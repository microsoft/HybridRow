// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "RowCursor.h"
#include "HybridRowHeader.h"
#include "RowBuffer.h"
#include "LayoutResolver.h"

namespace cdb_hr
{
  RowCursor RowCursor::Create(const RowBuffer& row) noexcept
  {
    SchemaId schemaId = row.ReadSchemaId(1 /* offset */);
    const Layout& layout = row.GetResolver()->Resolve(schemaId);
    uint32_t sparseSegmentOffset = row.ComputeVariableValueOffset(layout,
      HybridRowHeader::Size, layout.GetNumVariable());

    return RowCursor
    {
      layout,
      &LayoutLiteral::UDT, // scopeType
      TypeArgumentList(schemaId), // scopeTypeArgs
      HybridRowHeader::Size, // start
      sparseSegmentOffset, // metaOffset
      sparseSegmentOffset,  // valueOffset
    };
  }

  RowCursor RowCursor::CreateForAppend(const RowBuffer& row) noexcept
  {
    SchemaId schemaId = row.ReadSchemaId(1 /* offset */);
    const Layout& layout = row.GetResolver()->Resolve(schemaId);
    return RowCursor
    {
      layout,
      &LayoutLiteral::UDT, // scopeType
      TypeArgumentList(schemaId), // scopeTypeArgs
      HybridRowHeader::Size, // start
      row.GetLength(), // metaOffset
      row.GetLength(), // valueOffset
    };
  }

  tla::string RowCursor::ToString() const noexcept
  {
    static_assert(cdb_core::is_stringable_v<decltype(this), tla::string>);

    if (m_scopeType == nullptr)
    {
      return "<Invalid>";
    }

    TypeArgument scopeTypeArg = (m_scopeType == nullptr) || (m_scopeType->GetLayoutCode() == LayoutCode::EndScope)
                                  ? TypeArgument{}
                                  : TypeArgument{m_scopeType, m_scopeTypeArgs};

    TypeArgument typeArg = (m_cellType == nullptr) || (m_scopeType->GetLayoutCode() == LayoutCode::EndScope)
                             ? TypeArgument{}
                             : TypeArgument{m_cellType, m_cellTypeArgs};

    tla::string pathOrIndex = !m_writePath.empty() ? m_writePath : cdb_core::make_string("%u", m_index);
    return cdb_core::make_string("%s[%s] : %s@%u/%u%s",
      scopeTypeArg.ToString().c_str(),
      pathOrIndex.c_str(),
      typeArg.ToString().c_str(),
      m_metaOffset,
      m_valueOffset,
      m_immutable ? " immutable" : "");
  }

  RowCursor RowCursor::Clone() const noexcept
  {
    return *this;
  }

  RowCursor RowCursor::AsReadOnly() const noexcept
  {
    RowCursor dest{*this};
    dest.m_immutable = true;
    return dest;
  }

  RowCursor& RowCursor::Find(const RowBuffer& row, std::string_view path) noexcept
  {
    cdb_core::Contract::Requires(!m_scopeType->IsIndexedScope());

    if (!(m_cellType != nullptr && m_cellType->IsLayoutEndScope()))
    {
      while (row.SparseIteratorMoveNext(*this))
      {
        if (path == row.ReadSparsePath(*this))
        {
          m_exists = true;
          break;
        }
      }
    }

    m_writePath = path;
    m_writePathToken = {};
    return *this;
  }

  RowCursor& RowCursor::Find(const RowBuffer& row, const StringTokenizer::StringToken& pathToken) noexcept
  {
    cdb_core::Contract::Requires(!m_scopeType->IsIndexedScope());

    if (!(m_cellType != nullptr && m_cellType->IsLayoutEndScope()))
    {
      while (row.SparseIteratorMoveNext(*this))
      {
        if (pathToken.GetId() == static_cast<uint64_t>(m_pathToken))
        {
          m_exists = true;
          break;
        }
      }
    }

    m_writePath = pathToken.GetPath();
    m_writePathToken = pathToken;
    return *this;
  }

  bool RowCursor::MoveNext(const RowBuffer& row) noexcept
  {
    m_writePath = {};
    m_writePathToken = {};
    return row.SparseIteratorMoveNext(*this);
  }

  bool RowCursor::MoveNext(const RowBuffer& row, RowCursor& childScope) noexcept
  {
    if (childScope.m_scopeType != nullptr)
    {
      Skip(row, childScope);
    }

    return MoveNext(row);
  }

  bool RowCursor::MoveTo(const RowBuffer& row, uint32_t index) noexcept
  {
    cdb_core::Contract::Assert(m_index <= index);
    m_writePath = {};
    m_writePathToken = {};
    while (m_index < index)
    {
      if (!row.SparseIteratorMoveNext(*this))
      {
        return false;
      }
    }

    return true;
  }

  void RowCursor::Skip(const RowBuffer& row, RowCursor& childScope) noexcept
  {
    cdb_core::Contract::Requires(childScope.m_start == m_valueOffset);
    if (!(childScope.m_cellType != nullptr && childScope.m_cellType->IsLayoutEndScope()))
    {
      while (row.SparseIteratorMoveNext(childScope)) { }
    }

    if (childScope.m_scopeType->IsSizedScope())
    {
      m_endOffset = childScope.m_metaOffset;
    }
    else
    {
      m_endOffset = childScope.m_metaOffset + sizeof(LayoutCode); // Move past the end of scope marker.
    }

    #if _DEBUG
    childScope.m_scopeType = nullptr;
    #endif
  }
}
