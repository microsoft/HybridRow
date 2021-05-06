// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include "ScopePropertyType.h"

namespace cdb_hr
{
  /// <summary>
  /// Map properties represent an unbounded set of zero or more key-value pairs with unique
  /// keys.
  /// </summary>
  /// <remarks>
  /// Maps are typed or untyped.  Within typed maps, all key MUST be the same type, and all
  /// values MUST be the same type.  The type of both key and values is specified via <see cref="SetKeys" />
  /// and <see cref="SetValues" /> respectively. Typed maps may be stored more efficiently than untyped
  /// maps. When <see cref="SetKeys" /> or <see cref="SetValues" /> is unspecified or marked
  /// <see cref="TypeKind.Any" />, the map is untyped and its key and/or values may be heterogeneous.
  /// </remarks>
  class MapPropertyType final : public ScopePropertyType
  {
  public:
    MapPropertyType() noexcept : ScopePropertyType{TypeKind::Map}, m_keys{}, m_values{} {}

    MapPropertyType(std::unique_ptr<PropertyType> keys,
                    std::unique_ptr<PropertyType> values,
                    bool nullable = true,
                    bool immutable = false) noexcept :
      ScopePropertyType{TypeKind::Map, nullable, immutable},
      m_keys{std::move(keys)},
      m_values{std::move(values)} {}

    ~MapPropertyType() noexcept override = default;
    MapPropertyType(MapPropertyType&) = delete;
    MapPropertyType(MapPropertyType&&) = delete;
    MapPropertyType& operator=(const MapPropertyType&) = delete;
    MapPropertyType& operator=(MapPropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473665}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Map; }

    /// <summary>(Optional) type of the keys of the map, if a typed map, otherwise null.</summary>
    [[nodiscard]] std::optional<std::reference_wrapper<const PropertyType>> GetKeys() const noexcept
    {
      return m_keys ? std::optional<std::reference_wrapper<const PropertyType>>{*m_keys} : std::nullopt;
    }

    void SetKeys(std::unique_ptr<PropertyType> value) noexcept { m_keys = std::move(value); }

    /// <summary>(Optional) type of the values of the map, if a typed map, otherwise null.</summary>
    [[nodiscard]] std::optional<std::reference_wrapper<const PropertyType>> GetValues() const noexcept
    {
      return m_values ? std::optional<std::reference_wrapper<const PropertyType>>{*m_values} : std::nullopt;
    }

    void SetValues(std::unique_ptr<PropertyType> value) noexcept { m_values = std::move(value); }

  private:
    std::unique_ptr<PropertyType> m_keys;
    std::unique_ptr<PropertyType> m_values;
  };
}
