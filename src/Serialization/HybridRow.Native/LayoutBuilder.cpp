// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "LayoutBuilder.h"

#include "LayoutType.h"

// ReSharper disable IdentifierTypo
namespace cdb_hr
{
  LayoutBuilder::LayoutBuilder(std::string_view name, SchemaId schemaId) noexcept :
    m_name{name},
    m_schemaId{schemaId},
    m_fixedSize{},
    m_fixedCount{},
    m_varCount{},
    m_sparseCount{},
    m_bitAllocator{},
    m_fixedColumns{},
    m_varColumns{},
    m_sparseColumns{},
    m_scope{}
  {
    // [ <present bits>
    //   <bool bits>
    //   <fixed_1> <fixed_2> ... <fixed_n>
    //   <var_1> <var_2> ... <var_n>
    //   <sparse_1> <sparse_2> ... <sparse_o>
    // ]
  }

  LayoutColumn* LayoutBuilder::GetParent() noexcept
  {
    if (m_scope.empty())
    {
      return nullptr;
    }

    return m_scope.back();
  }

  void LayoutBuilder::AddFixedColumn(std::string_view path, const LayoutType* type, bool nullable,
                                     uint32_t length) noexcept
  {
    cdb_core::Contract::Requires(!type->IsVarint());

    std::unique_ptr<LayoutColumn> col;
    if (type->IsNull())
    {
      cdb_core::Contract::Requires(nullable);
      LayoutBit nullbit = m_bitAllocator.Allocate();
      col.reset(new LayoutColumn(
        path,
        type,
        {},
        StorageKind::Fixed,
        GetParent(),
        m_fixedCount,
        0,
        nullbit,
        LayoutBit::Invalid() // boolBit
      ));
    }
    else if (type->IsBool())
    {
      LayoutBit nullbit = nullable ? m_bitAllocator.Allocate() : LayoutBit::Invalid();
      LayoutBit boolbit = m_bitAllocator.Allocate();
      col.reset(new LayoutColumn(
        path,
        type,
        {},
        StorageKind::Fixed,
        GetParent(),
        m_fixedCount,
        0,
        nullbit,
        boolbit // boolBit
      ));
    }
    else
    {
      LayoutBit nullbit = nullable ? m_bitAllocator.Allocate() : LayoutBit::Invalid();
      col.reset(new LayoutColumn(
        path,
        type,
        {},
        StorageKind::Fixed,
        GetParent(),
        m_fixedCount,
        m_fixedSize,
        nullbit,
        LayoutBit::Invalid(), // boolBit
        length));

      m_fixedSize += type->IsFixed() ? type->GetSize() : length;
    }

    m_fixedCount++;
    cdb_core::Contract::Assert(col != nullptr);
    m_fixedColumns.push_back(std::move(col));
  }

  void LayoutBuilder::AddVariableColumn(std::string_view path, const LayoutType* type, uint32_t length) noexcept
  {
    cdb_core::Contract::Requires(type->AllowVariable());

    std::unique_ptr<LayoutColumn> col{
      new LayoutColumn{
        path,
        type,
        {},
        StorageKind::Variable,
        GetParent(),
        m_varCount,
        m_varCount,
        m_bitAllocator.Allocate(), // nullBit
        LayoutBit::Invalid(), // boolBit
        length
      }
    };

    m_varCount++;
    m_varColumns.push_back(std::move(col));
  }

  void LayoutBuilder::AddSparseColumn(std::string_view path, const LayoutType* type) noexcept
  {
    std::unique_ptr<LayoutColumn> col{
      new LayoutColumn{
        path,
        type,
        {},
        StorageKind::Sparse,
        GetParent(),
        m_sparseCount,
        UINT_MAX,
        LayoutBit::Invalid(), // nullBit
        LayoutBit::Invalid() // boolBit
      }
    };

    m_sparseCount++;
    m_sparseColumns.push_back(std::move(col));
  }

  void LayoutBuilder::AddObjectScope(std::string_view path, const LayoutType* type) noexcept
  {
    std::unique_ptr<LayoutColumn> col{
      new LayoutColumn{
        path,
        type,
        {},
        StorageKind::Sparse,
        GetParent(),
        m_sparseCount,
        UINT_MAX,
        LayoutBit::Invalid(), // nullBit
        LayoutBit::Invalid() // boolBit
      }
    };

    m_sparseCount++;
    m_scope.push_back(col.get());
    m_sparseColumns.push_back(std::move(col));
  }

  void LayoutBuilder::EndObjectScope() noexcept
  {
    cdb_core::Contract::Requires(!m_scope.empty());
    m_scope.pop_back();
  }

  void LayoutBuilder::AddTypedScope(std::string_view path, const LayoutType* type, TypeArgumentList typeArgs) noexcept
  {
    std::unique_ptr<LayoutColumn> col{
      new LayoutColumn{
        path,
        type,
        std::move(typeArgs),
        StorageKind::Sparse,
        GetParent(),
        m_sparseCount,
        UINT_MAX,
        LayoutBit::Invalid(), // nullBit
        LayoutBit::Invalid() // boolBit
      }
    };

    m_sparseCount++;
    m_sparseColumns.push_back(std::move(col));
  }

  std::unique_ptr<Layout> LayoutBuilder::Build() noexcept
  {
    // Compute offset deltas.  Offset bools by the present byte count, and fixed fields by the sum of the present and bool count.
    uint32_t fixedDelta = m_bitAllocator.GetNumBytes();
    uint32_t varIndexDelta = m_fixedCount;

    // Update the fixedColumns with the delta before freezing them.
    tla::vector<std::unique_ptr<LayoutColumn>> updatedColumns{};
    updatedColumns.reserve(m_fixedColumns.size() + m_varColumns.size());

    for (auto& c : m_fixedColumns)
    {
      c->SetOffset(c->GetOffset() + fixedDelta);
      updatedColumns.push_back(std::move(c));
    }

    for (auto& c : m_varColumns)
    {
      // Adjust variable column indexes such that they begin immediately following the last fixed column.
      c->SetIndex(c->GetIndex() + varIndexDelta);
      updatedColumns.push_back(std::move(c));
    }

    for (auto& c : m_sparseColumns)
    {
      updatedColumns.push_back(std::move(c));
    }

    std::unique_ptr<Layout> layout = std::unique_ptr<Layout>{
      new Layout(m_name, m_schemaId, m_bitAllocator.GetNumBytes(), m_fixedSize + fixedDelta, std::move(updatedColumns))
    };
    Reset();
    return std::move(layout);
  }

  void LayoutBuilder::Reset() noexcept
  {
    m_bitAllocator = LayoutBit::Allocator();
    m_fixedSize = 0;
    m_fixedCount = 0;
    m_fixedColumns.clear();
    m_varCount = 0;
    m_varColumns.clear();
    m_sparseCount = 0;
    m_sparseColumns.clear();
    m_scope.clear();
  }
}
