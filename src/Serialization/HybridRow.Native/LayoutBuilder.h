// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Layout.h"
#include "LayoutBit.h"
#include "LayoutColumn.h"
#include "SchemaId.h"

namespace cdb_hr
{
  class LayoutBuilder final
  {
  public:
    LayoutBuilder(std::string_view name, SchemaId schemaId) noexcept;
    ~LayoutBuilder() noexcept = default;
    LayoutBuilder() = delete;
    LayoutBuilder(const LayoutBuilder& other) = delete;
    LayoutBuilder(LayoutBuilder&& other) noexcept = delete;
    LayoutBuilder& operator=(const LayoutBuilder& other) = delete;
    LayoutBuilder& operator=(LayoutBuilder&& other) noexcept = delete;

    void AddFixedColumn(std::string_view path, const LayoutType* type, bool nullable, uint32_t length = 0) noexcept;
    void AddVariableColumn(std::string_view path, const LayoutType* type, uint32_t length = 0) noexcept;
    void AddSparseColumn(std::string_view path, const LayoutType* type) noexcept;
    void AddObjectScope(std::string_view path, const LayoutType* type) noexcept;
    void EndObjectScope() noexcept;
    void AddTypedScope(std::string_view path, const LayoutType* type, TypeArgumentList typeArgs) noexcept;
    std::unique_ptr<Layout> Build() noexcept;

  private:
    LayoutColumn* GetParent() noexcept;
    void Reset() noexcept;

    tla::string m_name;
    SchemaId m_schemaId;
    uint32_t m_fixedSize;
    uint32_t m_fixedCount;
    uint32_t m_varCount;
    uint32_t m_sparseCount;
    LayoutBit::Allocator m_bitAllocator;
    tla::vector<std::unique_ptr<LayoutColumn>> m_fixedColumns;
    tla::vector<std::unique_ptr<LayoutColumn>> m_varColumns;
    tla::vector<std::unique_ptr<LayoutColumn>> m_sparseColumns;
    tla::vector<LayoutColumn*> m_scope;
  };
}
