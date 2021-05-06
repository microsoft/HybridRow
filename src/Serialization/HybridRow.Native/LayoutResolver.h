// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "SchemaId.h"
#include "Layout.h"

namespace cdb_hr
{
struct LayoutResolver
{
  virtual ~LayoutResolver() = default;
  LayoutResolver(const LayoutResolver& other) noexcept = delete;
  LayoutResolver(LayoutResolver&& other) noexcept = delete;
  LayoutResolver& operator=(const LayoutResolver& other) noexcept = delete;
  LayoutResolver& operator=(LayoutResolver&& other) noexcept = delete;

  [[nodiscard]]
  virtual const Layout& Resolve(SchemaId schemaId) const noexcept = 0;

protected:
  LayoutResolver() noexcept = default;
};
}
