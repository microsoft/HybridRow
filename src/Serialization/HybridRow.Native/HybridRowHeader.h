// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "HybridRowVersion.h"
#include "SchemaId.h"

namespace cdb_hr
{
  /// <summary>Describes the header the precedes all valid Hybrid Rows.</summary>
  #pragma pack(push, 1)
  struct HybridRowHeader final
  {
    /// <summary>Size (in bytes) of a serialized header.</summary>
    constexpr static uint32_t Size = sizeof(HybridRowVersion) + SchemaId::Size;

    /// <summary>Initializes a new instance of the <see cref="HybridRowHeader"/> struct.</summary>
    constexpr HybridRowHeader() noexcept : m_version{HybridRowVersion::Invalid}, m_schemaId{} {}
    ~HybridRowHeader() noexcept = default;
    HybridRowHeader(const HybridRowHeader& other) noexcept = default;
    HybridRowHeader(HybridRowHeader&& other) noexcept = default;
    HybridRowHeader& operator=(const HybridRowHeader& other) noexcept = default;
    HybridRowHeader& operator=(HybridRowHeader&& other) noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="HybridRowHeader"/> struct.</summary>
    /// <param name="version">The version of the HybridRow library used to write this row.</param>
    /// <param name="schemaId">The unique identifier of the schema whose layout was used to write this row.</param>
    constexpr HybridRowHeader(HybridRowVersion version, SchemaId schemaId);

    /// <summary>The version of the HybridRow library used to write this row.</summary>
    [[nodiscard]]
    HybridRowVersion GetVersion() const noexcept;

    /// <summary>The unique identifier of the schema whose layout was used to write this row.</summary>
    [[nodiscard]]
    SchemaId GetSchemaId() const noexcept;

  private:
    HybridRowVersion m_version;
    SchemaId m_schemaId;
  };
  #pragma pack(pop)

  static_assert(cdb_core::is_blittable_v<HybridRowHeader>);

  constexpr HybridRowHeader::HybridRowHeader(HybridRowVersion version, SchemaId schemaId) :
    m_version(version),
    m_schemaId(schemaId) {}

  inline HybridRowVersion HybridRowHeader::GetVersion() const noexcept { return m_version; }
  inline SchemaId HybridRowHeader::GetSchemaId() const noexcept { return m_schemaId; }
}
