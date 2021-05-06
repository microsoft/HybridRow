// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "LayoutCompiler.h"
#include "Namespace.h"

namespace cdb_hr
{
  std::unique_ptr<Layout> Schema::Compile(const Namespace& ns) const noexcept(false)
  {
    cdb_core::Contract::Requires(std::find_if(ns.GetSchemas().begin(), ns.GetSchemas().end(),
      [schema = this](const std::unique_ptr<Schema>& p) { return p.get() == schema; }) != ns.GetSchemas().end());

    return LayoutCompiler::Compile(ns, *this);
  }

  [[nodiscard]] SchemaLanguageVersion Schema::GetEffectiveSdlVersion(const Namespace& ns) const noexcept
  {
    return m_version != SchemaLanguageVersion::Unspecified ? m_version : ns.GetEffectiveSdlVersion();
  }
}
