// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include <vector>
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>Tuple properties represent a typed, finite, ordered set of two or more items.</summary>
  class TuplePropertyType final : public ScopePropertyType
  {
  public:
    TuplePropertyType() noexcept : ScopePropertyType{TypeKind::Tuple}, m_items{} {}

    explicit TuplePropertyType(std::vector<std::unique_ptr<PropertyType>> items,
                               bool nullable = true,
                               bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Tuple, nullable, immutable},
      m_items{std::move(items)} {}

    ~TuplePropertyType() noexcept override = default;
    TuplePropertyType(TuplePropertyType&) = delete;
    TuplePropertyType(TuplePropertyType&&) = delete;
    TuplePropertyType& operator=(const TuplePropertyType&) = delete;
    TuplePropertyType& operator=(TuplePropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473666}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Tuple; }

    /// <summary>Types of the elements of the tuple in element order.</summary>
    [[nodiscard]] const std::vector<std::unique_ptr<PropertyType>>& GetItems() const noexcept { return m_items; }
    std::vector<std::unique_ptr<PropertyType>>& GetItems() noexcept { return m_items; }
    void SetItems(std::vector<std::unique_ptr<PropertyType>> value) noexcept { m_items = std::move(value); }

  private:
    std::vector<std::unique_ptr<PropertyType>> m_items;
  };
}
