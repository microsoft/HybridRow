// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Namespace.h"
#include "SystemSchema.h"

namespace cdb_hr
{
  struct SystemSchemaLiteral final
  {
    SystemSchemaLiteral() = delete;

    /// <summary>
    /// SchemaId of the empty schema. This schema has no defined cells but can accomodate
    /// unschematized sparse content.
    /// </summary>
    constexpr static SchemaId EmptySchemaId{2147473650};

    static const Namespace& GetNamespace();
    static const LayoutResolver& GetLayoutResolver();
  };

  inline const Namespace& SystemSchemaLiteral::GetNamespace() { return SchemasHrSchema::GetNamespace(); }
  inline const LayoutResolver& SystemSchemaLiteral::GetLayoutResolver() { return SchemasHrSchema::GetLayoutResolver(); }
}
