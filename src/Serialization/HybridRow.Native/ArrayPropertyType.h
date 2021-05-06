// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>Array properties represent an unbounded set of zero or more items.</summary>
  /// <remarks>
  /// Arrays may be typed or untyped.  Within typed arrays, all items MUST be the same type. The
  /// type of items is specified via <see cref="SetItems" />. Typed arrays may be stored more efficiently
  /// than untyped arrays. When <see cref="SetItems" /> is unspecified, the array is untyped and its items
  /// may be heterogeneous.
  /// </remarks>
  class ArrayPropertyType final : public ScopePropertyType
  {
  public:
    ArrayPropertyType() noexcept : ScopePropertyType{TypeKind::Array}, m_items{} {}

    explicit ArrayPropertyType(std::unique_ptr<PropertyType> items, bool nullable = true, bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Array, nullable, immutable},
      m_items{std::move(items)} {}

    ~ArrayPropertyType() noexcept override = default;
    ArrayPropertyType(ArrayPropertyType&) = delete;
    ArrayPropertyType(ArrayPropertyType&&) = delete;
    ArrayPropertyType& operator=(const ArrayPropertyType&) = delete;
    ArrayPropertyType& operator=(ArrayPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473661}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Array; }

    /// <summary>(Optional) type of the elements of the array, if a typed array, otherwise null.</summary>
    [[nodiscard]] std::optional<std::reference_wrapper<const PropertyType>> GetItems() const noexcept
    {
      return m_items ? std::optional<std::reference_wrapper<const PropertyType>>{*m_items} : std::nullopt;
    }

    void SetItems(std::unique_ptr<PropertyType> value) noexcept { m_items = std::move(value); }

  private:
    std::unique_ptr<PropertyType> m_items;
  };
}
