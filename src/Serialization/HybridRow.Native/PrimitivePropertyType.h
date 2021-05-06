// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "PropertyType.h"
#include "StorageKind.h"

namespace cdb_hr
{
  /// <summary>A primitive property.</summary>
  /// <remarks>
  /// Primitive properties map to columns one-to-one.  Primitive properties indicate how the
  /// column should be represented within the row.
  /// </remarks>
  class PrimitivePropertyType final : public PropertyType
  {
  public:
    PrimitivePropertyType() noexcept : m_length{0}, m_storage{StorageKind::Sparse}, m_enum{}, m_rowBufferSize{} {}

    PrimitivePropertyType(TypeKind type, StorageKind storage = StorageKind::Sparse,
                          bool nullable = true, std::string_view apiType = ""sv) noexcept :
      PropertyType{type, nullable, apiType}, m_length{0}, m_storage{storage}, m_enum{} {}

    ~PrimitivePropertyType() noexcept override = default;
    PrimitivePropertyType(PrimitivePropertyType&) = delete;
    PrimitivePropertyType(PrimitivePropertyType&&) = delete;
    PrimitivePropertyType& operator=(const PrimitivePropertyType&) = delete;
    PrimitivePropertyType& operator=(PrimitivePropertyType&&) = delete;

    [[nodiscard]] SchemaId GetRuntimeSchemaId() const noexcept override { return SchemaId{2147473659}; }
    [[nodiscard]] PropertyKind GetKind() const noexcept override { return PropertyKind::Primitive; }

    /// <summary>The maximum allowable length in bytes.</summary>
    /// <remarks>
    /// This annotation is only valid for non-fixed length types. A value of 0 means the maximum
    /// allowable length.
    /// </remarks>
    [[nodiscard]] uint32_t GetLength() const noexcept { return m_length; }
    void SetLength(uint32_t value) noexcept { m_length = value; }

    /// <summary>Storage requirements of the property.</summary>
    [[nodiscard]] StorageKind GetStorage() const noexcept { return m_storage; }
    void SetStorage(StorageKind value) noexcept { m_storage = value; }

    /// <summary>The identifier of the enum defining the values and base type.</summary>
    /// <remarks>
    /// This annotation is only valid for enum types.  The enum MUST be defined within the
    /// same <see cref="Namespace" /> as the schema that references it.
    /// </remarks>
    [[nodiscard]] std::string_view GetEnum() const noexcept { return m_enum; }
    void SetEnum(std::string_view value) noexcept { m_enum = value; }

    /// <summary>If true then during serialization this field includes the actual row buffer size.</summary>
    /// <remarks>
    /// <para>
    /// Fields with this annotation MUST be <see cref="TypeKind.Int32"/> fields with <see cref="StorageKind.Fixed"/>.
    /// Only a single field per schema have this annotation.
    /// </para>
    /// <para>
    /// During serialization this field is deferred until the very end and written last.
    /// </para>
    /// </remarks>
    [[nodiscard]] bool GetRowBufferSize() const noexcept { return m_rowBufferSize; }
    void SetRowBufferSize(bool value) noexcept { m_rowBufferSize = value; }

  private:
    uint32_t m_length;
    StorageKind m_storage;
    tla::string m_enum;
    bool m_rowBufferSize;
  };
}
