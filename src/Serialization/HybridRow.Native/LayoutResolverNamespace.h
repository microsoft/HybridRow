// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "SchemaId.h"
#include "Layout.h"
#include "LayoutResolver.h"
#include "Namespace.h"

namespace cdb_hr
{
  /// <summary>
  /// An implementation of <see cref="LayoutResolver" /> which dynamically compiles schema from
  /// a <see cref="Namespace" />.
  /// </summary>
  /// <remarks>
  /// This resolver assumes that <see cref="Schema" /> within the <see cref="Namespace" /> have
  /// their <see cref="Schema.SchemaId" /> properly populated. The resolver caches compiled schema.
  /// </remarks>
  /// <remarks>All members of this class are multi-thread safe.</remarks>
  class LayoutResolverNamespace final : public LayoutResolver
  {
  public:
    LayoutResolverNamespace(std::unique_ptr<Namespace> schemaNamespace,
                            std::unique_ptr<LayoutResolver> parent = {}) noexcept;
    ~LayoutResolverNamespace() override = default;
    LayoutResolverNamespace(const LayoutResolverNamespace& other) noexcept = delete;
    LayoutResolverNamespace(LayoutResolverNamespace&& other) noexcept = delete;
    LayoutResolverNamespace& operator=(const LayoutResolverNamespace& other) noexcept = delete;
    LayoutResolverNamespace& operator=(LayoutResolverNamespace&& other) noexcept = delete;

    /// <summary>Returns a borrowed pointer to the namespace managed by this instance.</summary>
    [[nodiscard]] const Namespace* GetNamespace() const noexcept { return m_schemaNamespace.get(); };
    /// <summary>Returns a borrowed pointer to the compiled layout matching the given schema id.</summary>
    [[nodiscard]] virtual const Layout& Resolve(SchemaId schemaId) const noexcept override;

  private:
    std::unique_ptr<Namespace> m_schemaNamespace;
    std::unique_ptr<LayoutResolver> m_parent;
    mutable std::shared_mutex m_lock;
    mutable std::vector<std::unique_ptr<Layout>> m_layouts;
    mutable std::map<int, const Layout&> m_layoutCache;
  };
}
