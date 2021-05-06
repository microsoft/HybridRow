// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#pragma warning(disable : 4638)
#include "SchemaId.h"
#include "LayoutColumn.h"
#include "SamplingUtf8StringComparer.h"
#include "StringTokenizer.h"

namespace cdb_hr
{
  // Forward declaration.
  class Schema;
}

namespace cdb_hr
{
  using namespace std::literals;

  /// <summary>A Layout describes the structure of a Hybrid Row.</summary>
  /// <remarks>
  /// A layout indicates the number, order, and type of all schematized columns to be stored
  /// within a hybrid row.  The order and type of columns defines the physical ordering of bytes used to
  /// encode the row and impacts the cost of updating the row.
  /// <para />
  /// A layout is created by compiling a <see cref="Schema" /> or through a <see cref="LayoutBuilder" />.
  /// <para />
  /// <see cref="Layout" /> is immutable.
  /// </remarks>
  class Layout final
  {
  public:
    /// <summary>Name of the layout.</summary>
    /// <remarks>
    /// Usually this is the name of the <see cref="Schema" /> from which this
    /// <see cref="Layout" /> was generated.
    /// </remarks>
    [[nodiscard]] std::string_view GetName() const noexcept;

    /// <summary>Unique identifier of the schema from which this <see cref="Layout" /> was generated.</summary>
    [[nodiscard]] SchemaId GetSchemaId() const noexcept;

    /// <summary>The set of top level columns defined in the layout (in left-to-right order).</summary>
    [[nodiscard]] const tla::vector<const LayoutColumn*>& GetColumns() const noexcept;

    /// <summary>Minimum required size of a row of this layout.</summary>
    /// <remarks>
    /// This size excludes all sparse columns, and assumes all columns (including variable) are
    /// null.
    /// </remarks>
    [[nodiscard]] uint32_t GetSize() const noexcept;

    /// <summary>The number of bitmask bytes allocated within the layout.</summary>
    /// <remarks>
    /// A presence bit is allocated for each fixed and variable-length field.  Sparse columns
    /// never have presence bits.  Fixed boolean allocate an additional bit from the bitmask to store their
    /// value.
    /// </remarks>
    [[nodiscard]] uint32_t GetNumBitmaskBytes() const noexcept;

    /// <summary>The number of fixed columns.</summary>
    [[nodiscard]] uint32_t GetNumFixed() const noexcept;

    /// <summary>The number of variable-length columns.</summary>
    [[nodiscard]] uint32_t GetNumVariable() const noexcept;

    /// <summary>A tokenizer for path strings.</summary>
    [[nodiscard]] const StringTokenizer& GetTokenizer() const noexcept;

    /// <summary>Finds a column specification for a column with a matching path.</summary>
    /// <param name="path">The path of the column to find.</param>
    /// <returns>(True and the column) if a column with the path is found, otherwise (false, nullptr).</returns>
    [[nodiscard]] std::tuple<bool, const LayoutColumn*> TryFind(std::string_view path) const noexcept;

  private:
    friend class LayoutBuilder;
    friend struct LayoutConstants;
    Layout(std::string_view name, SchemaId schemaId, uint32_t numBitmaskBytes, uint32_t minRequiredSize,
           tla::vector<std::unique_ptr<LayoutColumn>> columns) noexcept;

    tla::string m_name;
    SchemaId m_schemaId;
    uint32_t m_size;
    uint32_t m_numBitmaskBytes;
    uint32_t m_numFixed;
    uint32_t m_numVariable;
    StringTokenizer m_tokenizer;
    tla::vector<std::unique_ptr<LayoutColumn>> m_columns;
    tla::vector<const LayoutColumn*> m_topColumns;
    tla::unordered_map<std::string_view, const LayoutColumn*, SamplingUtf8StringComparer> m_pathMap;
  };

  inline std::string_view Layout::GetName() const noexcept { return m_name; }
  inline SchemaId Layout::GetSchemaId() const noexcept { return m_schemaId; }
  inline const tla::vector<const LayoutColumn*>& Layout::GetColumns() const noexcept { return m_topColumns; }
  inline uint32_t Layout::GetSize() const noexcept { return m_size; }
  inline uint32_t Layout::GetNumBitmaskBytes() const noexcept { return m_numBitmaskBytes; }
  inline uint32_t Layout::GetNumFixed() const noexcept { return m_numFixed; }
  inline uint32_t Layout::GetNumVariable() const noexcept { return m_numVariable; }
  inline const StringTokenizer& Layout::GetTokenizer() const noexcept { return m_tokenizer; }
}
