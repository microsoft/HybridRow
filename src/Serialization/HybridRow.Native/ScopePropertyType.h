// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "PropertyType.h"

namespace cdb_hr
{
  class ScopePropertyType : public PropertyType
  {
  public:
    ~ScopePropertyType() noexcept override = default;
    ScopePropertyType(ScopePropertyType&) = delete;
    ScopePropertyType(ScopePropertyType&&) = delete;
    ScopePropertyType& operator=(const ScopePropertyType&) = delete;
    ScopePropertyType& operator=(ScopePropertyType&&) = delete;

    /// <summary>True if the property's child elements cannot be mutated in place.</summary>
    /// <remarks>Immutable properties can still be replaced in their entirety.</remarks>
    [[nodiscard]] bool GetImmutable() const noexcept { return m_immutable; }
    void SetImmutable(bool value) noexcept { m_immutable = value; }

  private:
    friend class ArrayPropertyType;
    friend class ObjectPropertyType;
    friend class MapPropertyType;
    friend class SetPropertyType;
    friend class TaggedPropertyType;
    friend class TuplePropertyType;
    friend class UdtPropertyType;

    ScopePropertyType(TypeKind type, bool nullable = true, bool immutable = false) noexcept :
      PropertyType{type, nullable}, m_immutable{immutable} { }

    bool m_immutable;
  };
}
