// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include <vector>
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>Tagged properties pair one or more typed values with an API-specific uint8 type code.</summary>
  /// <remarks>
  /// The uint8 type code is implicitly in position 0 within the resulting tagged and should not
  /// be specified in <see cref="Items" />.
  /// </remarks>
  class TaggedPropertyType final : public ScopePropertyType
  {
  public:
    constexpr static uint32_t MinTaggedArguments = 1;
    constexpr static uint32_t MaxTaggedArguments = 2;

    TaggedPropertyType() noexcept : ScopePropertyType{TypeKind::Tagged}, m_items{} {}

    explicit TaggedPropertyType(std::vector<std::unique_ptr<PropertyType>> items,
                                bool nullable = true,
                                bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Tagged, nullable, immutable},
      m_items{std::move(items)} {}

    ~TaggedPropertyType() noexcept override = default;
    TaggedPropertyType(TaggedPropertyType&) = delete;
    TaggedPropertyType(TaggedPropertyType&&) = delete;
    TaggedPropertyType& operator=(const TaggedPropertyType&) = delete;
    TaggedPropertyType& operator=(TaggedPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473667}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Tagged; }

    /// <summary>Types of the elements of the tagged in element order.</summary>
    [[nodiscard]] const std::vector<std::unique_ptr<PropertyType>>& GetItems() const noexcept { return m_items; }
    std::vector<std::unique_ptr<PropertyType>>& GetItems() noexcept { return m_items; }
    void SetItems(std::vector<std::unique_ptr<PropertyType>> value) noexcept { m_items = std::move(value); }

  private:
    std::vector<std::unique_ptr<PropertyType>> m_items;
  };
}
