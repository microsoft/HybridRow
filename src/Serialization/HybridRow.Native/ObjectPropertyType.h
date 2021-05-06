// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include <vector>
#include "Property.h"
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>Object properties represent nested structures.</summary>
  /// <remarks>
  /// Object properties map to multiple columns depending on the number of internal properties
  /// within the defined object structure.  Object properties are provided as a convince in schema
  /// design.  They are effectively equivalent to defining the same properties explicitly via
  /// <see cref="PrimitivePropertyType" /> with nested property paths.
  /// </remarks>
  class ObjectPropertyType final : public ScopePropertyType
  {
  public:
    ObjectPropertyType() noexcept : ScopePropertyType{TypeKind::Object}, m_properties{} {}

    explicit ObjectPropertyType(std::vector<std::unique_ptr<Property>> props,
                                bool nullable = true,
                                bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Object, nullable, immutable},
      m_properties{std::move(props)} {}

    ~ObjectPropertyType() noexcept override = default;
    ObjectPropertyType(ObjectPropertyType&) = delete;
    ObjectPropertyType(ObjectPropertyType&&) = delete;
    ObjectPropertyType& operator=(const ObjectPropertyType&) = delete;
    ObjectPropertyType& operator=(ObjectPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473662}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Object; }

    /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
    [[nodiscard]] const std::vector<std::unique_ptr<Property>>& GetProperties() const noexcept { return m_properties; }
    std::vector<std::unique_ptr<Property>>& GetProperties() noexcept { return m_properties; }
    void SetProperties(std::vector<std::unique_ptr<Property>> value) noexcept { m_properties = std::move(value); }

  private:
    /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
    std::vector<std::unique_ptr<Property>> m_properties;
  };
}
