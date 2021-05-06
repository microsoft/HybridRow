// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include "SchemaId.h"
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>UDT properties represent nested structures with an independent schema.</summary>
  /// <remarks>
  /// UDT properties include a nested row within an existing row as a column.  The schema of the
  /// nested row may be evolved independently of the outer row.  Changes to the independent schema affect
  /// all outer schemas where the UDT is used.
  /// </remarks>
  class UdtPropertyType final : public ScopePropertyType
  {
  public:
    UdtPropertyType() noexcept : ScopePropertyType{TypeKind::Schema, true}, m_name{}, m_schemaId{} { }

    UdtPropertyType(std::string_view name, 
                    SchemaId schemaId = SchemaId::Invalid(),
                    bool nullable = true,
                    bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Schema, nullable, immutable},
      m_name{name},
      m_schemaId{schemaId} { }

    ~UdtPropertyType() noexcept override = default;
    UdtPropertyType(UdtPropertyType&) = delete;
    UdtPropertyType(UdtPropertyType&&) = delete;
    UdtPropertyType& operator=(const UdtPropertyType&) = delete;
    UdtPropertyType& operator=(UdtPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473663}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Udt; }

    /// <summary>The identifier of the UDT schema defining the structure for the nested row.</summary>
    /// <remarks>
    /// The UDT schema MUST be defined within the same <see cref="Namespace" /> as the schema that
    /// references it.
    /// </remarks>
    [[nodiscard]] std::string_view GetName() const noexcept { return m_name; }
    void SetName(std::string_view value) noexcept { m_name = value; }

    /// <summary>The unique identifier for a schema.</summary>
    /// <remarks>
    /// Optional uniquifier if multiple versions of <see cref="Name" /> appears within the Namespace.
    /// <p>
    /// If multiple versions of a UDT are defined within the <see cref="Namespace" /> then the globally
    /// unique identifier of the specific version referenced MUST be provided.
    /// </p>
    /// </remarks>
    [[nodiscard]] SchemaId GetSchemaId() const noexcept { return m_schemaId; }
    void SetSchemaId(SchemaId value) noexcept { m_schemaId = value; }

  private:
    tla::string m_name;
    SchemaId m_schemaId;
  };
}
