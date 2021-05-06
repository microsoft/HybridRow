// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "LayoutResolverNamespace.h"

// ReSharper disable CppClangTidyCppcoreguidelinesProTypeStaticCastDowncast
namespace cdb_hr
{
  class LayoutBuilder;

  LayoutResolverNamespace::LayoutResolverNamespace(
    std::unique_ptr<Namespace> schemaNamespace,
    std::unique_ptr<LayoutResolver> parent) noexcept :
    m_schemaNamespace{std::move(schemaNamespace)},
    m_parent{std::move(parent)},
    m_lock{},
    m_layouts{},
    m_layoutCache{} { }

  const Layout& LayoutResolverNamespace::Resolve(SchemaId schemaId) const noexcept
  {
    try
    {
      {
        std::shared_lock<std::shared_mutex> lock(m_lock);
        auto iter = m_layoutCache.find(schemaId.Id());
        if (iter != m_layoutCache.end())
        {
          return iter->second;
        }
      }

      for (auto& p : m_schemaNamespace->GetSchemas())
      {
        Schema& s = *p;
        if (s.GetSchemaId() == schemaId)
        {
          auto layout = s.Compile(*m_schemaNamespace);
          std::unique_lock<std::shared_mutex> lock(m_lock);
          auto [iter, added] = m_layoutCache.try_emplace(schemaId.Id(), *layout);
          if (added)
          {
            m_layouts.emplace_back(std::move(layout));
          }
          return iter->second;
        }
      }

      if (m_parent != nullptr)
      {
        const Layout& layout = m_parent->Resolve(schemaId);
        std::unique_lock<std::shared_mutex> lock(m_lock);
        auto [iter, _] = m_layoutCache.try_emplace(schemaId.Id(), layout);
        return iter->second;
      }

      cdb_core::Contract::Fail(cdb_core::make_string<std::string>("Failed to resolve schema %d", schemaId.Id()));
    }
    catch (...)
    {
      cdb_core::Contract::Fail(cdb_core::make_string<std::string>("Failed to acquire mutex while resolving schema %d",
        schemaId.Id()));
    }
  }
}
