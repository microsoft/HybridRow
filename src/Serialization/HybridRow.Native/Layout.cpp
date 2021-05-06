// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Layout.h"
#include "SchemaId.h"

namespace cdb_hr
{
  Layout::Layout(tla::string_view name, SchemaId schemaId, uint32_t numBitmaskBytes, uint32_t minRequiredSize,
                 tla::vector<std::unique_ptr<LayoutColumn>> columns) noexcept :
    m_name(name),
    m_schemaId(schemaId),
    m_size(minRequiredSize),
    m_numBitmaskBytes(numBitmaskBytes),
    m_numFixed(0),
    m_numVariable(0),
    m_tokenizer(),
    m_columns(std::move(columns)),
    m_topColumns(),
    m_pathMap()
  {
    m_topColumns.reserve(m_columns.size());
    m_pathMap.reserve(m_columns.size());
    for (const auto& c : m_columns)
    {
      m_tokenizer.Add(c->GetPath());
      m_pathMap.insert({c->GetFullPath(), c.get()});
      if (c->GetStorage() == StorageKind::Fixed)
      {
        m_numFixed++;
      }
      else if (c->GetStorage() == StorageKind::Variable)
      {
        m_numVariable++;
      }

      if (c->GetParent() == nullptr)
      {
        m_topColumns.push_back(c.get());
      }
    }
  }

  std::tuple<bool, const LayoutColumn*> Layout::TryFind(std::string_view path) const noexcept
  {
    const auto got = m_pathMap.find(path);
    if (got == m_pathMap.end())
    {
      return {false, nullptr};
    }
    return {true, got->second};
  }
}
