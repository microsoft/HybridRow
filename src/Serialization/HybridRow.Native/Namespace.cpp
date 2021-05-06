// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Namespace.h"
#include "RowCursor.h"
#include "IHybridRowSerializer.h"
#include "SystemSchema.h"

namespace cdb_hr
{
  std::tuple<Result, std::unique_ptr<Namespace>> Namespace::Read(const RowBuffer& row) noexcept
  {
    cdb_core::Contract::Requires(row.GetHeader().GetSchemaId() == NamespaceHybridRowSerializer::Id);
    RowCursor root = RowCursor::Create(row);
    return NamespaceHybridRowSerializer::Read(row, root, true);
  }

  Result Namespace::Write(RowBuffer& row) const noexcept
  {
    cdb_core::Contract::Requires(row.GetHeader().GetSchemaId() == NamespaceHybridRowSerializer::Id);
    RowCursor root = RowCursor::Create(row);
    return NamespaceHybridRowSerializer::Write(row, root, true, NamespaceHybridRowSerializer::Id, *this);
  }

  [[nodiscard]] SchemaLanguageVersion Namespace::GetEffectiveSdlVersion() const noexcept
  {
    return m_version != SchemaLanguageVersion::Unspecified ? m_version : SchemaLanguageVersion::Latest;
  }
}
