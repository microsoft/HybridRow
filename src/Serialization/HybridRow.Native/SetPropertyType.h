// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>Set properties represent an unbounded set of zero or more unique items.</summary>
  /// <remarks>
  /// Sets may be typed or untyped.  Within typed sets, all items MUST be the same type. The
  /// type of items is specified via <see cref="Items" />. Typed sets may be stored more efficiently than
  /// untyped sets. When <see cref="Items" /> is unspecified, the set is untyped and its items may be
  /// heterogeneous. Each item within a set must be unique. Uniqueness is defined by the HybridRow
  /// encoded sequence of bytes for the item.
  /// </remarks>
  class SetPropertyType final : public ScopePropertyType
  {
  public:
    SetPropertyType() noexcept : ScopePropertyType{TypeKind::Set}, m_items{} {}

    SetPropertyType(std::unique_ptr<PropertyType> items, bool nullable = true, bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Set, nullable, immutable},
      m_items{std::move(items)} {}

    ~SetPropertyType() noexcept override = default;
    SetPropertyType(SetPropertyType&) = delete;
    SetPropertyType(SetPropertyType&&) = delete;
    SetPropertyType& operator=(const SetPropertyType&) = delete;
    SetPropertyType& operator=(SetPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473664}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Set; }

    /// <summary>(Optional) type of the elements of the set, if a typed set, otherwise null.</summary>
    [[nodiscard]] std::optional<std::reference_wrapper<const PropertyType>> GetItems() const noexcept
    {
      return m_items ? std::optional<std::reference_wrapper<const PropertyType>>{*m_items} : std::nullopt;
    }

    void SetItems(std::unique_ptr<PropertyType> value) noexcept { m_items = std::move(value); }

  private:
    std::unique_ptr<PropertyType> m_items;
  };
}
