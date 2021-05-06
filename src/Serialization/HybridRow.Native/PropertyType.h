// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

#include "PropertyKind.h"
#include "TypeKind.h"

namespace cdb_hr
{
  using namespace std::literals;

  /// <summary>The base class for property types both primitive and complex.</summary>
  class PropertyType
  {
  public:
    PropertyType(PropertyType&) = delete;
    PropertyType(PropertyType&&) = delete;
    PropertyType& operator=(const PropertyType&) = delete;
    PropertyType& operator=(PropertyType&&) = delete;
    virtual ~PropertyType() noexcept = default;

    /// <summary>The logical type of the property.</summary>
    [[nodiscard]] virtual SchemaId GetRuntimeSchemaId() const noexcept = 0;

    /// <summary>The logical type of the property.</summary>
    [[nodiscard]] virtual PropertyKind GetKind() const noexcept = 0;

    /// <summary>Api-specific type annotations for the property.</summary>
    [[nodiscard]] std::string_view GetApiType() const noexcept { return m_apiType; }
    void SetApiType(std::string_view value) noexcept { m_apiType = value; }

    /// <summary>The logical type of the property.</summary>
    [[nodiscard]] TypeKind GetType() const noexcept { return m_type; }
    void SetType(TypeKind value) noexcept { m_type = value; }

    /// <summary>True if the property can be null.</summary>
    /// <remarks>Default: true.</remarks>
    [[nodiscard]] bool GetNullable() const noexcept { return m_nullable; }
    void SetNullable(bool value) noexcept { m_nullable = value; }

  private:
    friend class PrimitivePropertyType;
    friend class ScopePropertyType;

    PropertyType() :
      m_type{TypeKind::Invalid},
      m_nullable{true},
      m_apiType{} { }

    explicit PropertyType(TypeKind type, bool nullable = true, std::string_view apiType = ""sv) :
      m_type{type},
      m_nullable{nullable},
      m_apiType{apiType} { }

    TypeKind m_type;
    bool m_nullable;
    tla::string m_apiType;
  };
}
