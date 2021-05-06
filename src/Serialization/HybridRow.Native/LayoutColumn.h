// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "StorageKind.h"
#include "TypeArgument.h"
#include "TypeArgumentList.h"
#include "LayoutBit.h"

namespace cdb_hr
{
  class LayoutColumn final
  {
  public:
    ~LayoutColumn() noexcept = default;
    LayoutColumn(const LayoutColumn& other) = delete;
    LayoutColumn(LayoutColumn&& other) noexcept = delete;
    LayoutColumn& operator=(const LayoutColumn& other) = delete;
    LayoutColumn& operator=(LayoutColumn&& other) noexcept = delete;

    /// <summary>The relative path of the field within its parent scope.</summary>
    /// <remarks>
    /// Paths are expressed in dotted notation: e.g. a relative <see cref="GetPath()" /> of 'b.c'
    /// within the scope 'a' yields a <see cref="GetFullPath()" /> of 'a.b.c'.
    /// </remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }

    /// <summary>The full logical path of the field within the row.</summary>
    /// <remarks>
    /// Paths are expressed in dotted notation: e.g. a relative <see cref="GetPath()" /> of 'b.c'
    /// within the scope 'a' yields a <see cref="GetFullPath()" /> of 'a.b.c'.
    /// </remarks>
    [[nodiscard]] std::string_view GetFullPath() const noexcept { return m_fullPath; }

    /// <summary>The physical layout type of the field.</summary>
    [[nodiscard]] const LayoutType* GetType() const noexcept { return m_typeArg.GetType(); }

    /// <summary>The storage kind of the field.</summary>
    [[nodiscard]] StorageKind GetStorage() const noexcept { return m_storage; }

    /// <summary>The layout of the parent scope, if a nested column, otherwise null.</summary>
    [[nodiscard]] const LayoutColumn* GetParent() const noexcept { return m_parent; }

    /// <summary>The full logical type.</summary>
    [[nodiscard]] const TypeArgument& GetTypeArg() const noexcept { return m_typeArg; }

    /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
    [[nodiscard]] const TypeArgumentList& GetTypeArgs() const noexcept { return m_typeArg.GetTypeArgs(); }

    /// <summary>
    /// 0-based index of the column within the structure.  Also indicates which presence bit
    /// controls this column.
    /// </summary>
    [[nodiscard]] uint32_t GetIndex() const noexcept { return m_index; }

    /// <summary>
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Fixed" /> then the byte offset to
    /// the field location.
    /// <para />
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Variable" /> then the 0-based index of the
    /// field from the beginning of the variable length segment.
    /// <para />
    /// For all other values of <see cref="GetStorage()" />, <see cref="GetOffset()" /> is ignored.
    /// </summary>
    [[nodiscard]] uint32_t GetOffset() const noexcept { return m_offset; }

    /// <summary>For nullable fields, the the bit in the layout bitmask for the null bit.</summary>
    [[nodiscard]] LayoutBit GetNullBit() const noexcept { return m_nullBit; }

    /// <summary>For bool fields, 0-based index into the bit mask for the bool value.</summary>
    [[nodiscard]] LayoutBit GetBoolBit() const noexcept { return m_boolBit; }

    /// <summary>
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Fixed" /> then the fixed number of
    /// bytes reserved for the value.
    /// <para />
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Variable" /> then the maximum number of
    /// bytes allowed for the value.
    /// </summary>
    [[nodiscard]] uint32_t GetSize() const noexcept { return m_size; }

    /// <summary>The physical layout type of the field cast to the specified type.</summary>
    template<typename T, typename = std::enable_if_t<std::is_base_of_v<LayoutType, T>>>
    const T& TypeAs() const;

  private:
    friend class LayoutBuilder;

    /// <summary>Initializes a new instance of the <see cref="LayoutColumn" /> class.</summary>
    /// <param name="path">The path to the field relative to parent scope.</param>
    /// <param name="type">Type of the field.</param>
    /// <param name="storage">Storage encoding of the field.</param>
    /// <param name="parent">The layout of the parent scope, if a nested column.</param>
    /// <param name="index">0-based column index.</param>
    /// <param name="offset">0-based Offset from beginning of serialization.</param>
    /// <param name="nullBit">0-based index into the bit mask for the null bit.</param>
    /// <param name="boolBit">For bool fields, 0-based index into the bit mask for the bool value.</param>
    /// <param name="length">For variable length types the length, otherwise 0.</param>
    /// <param name="typeArgs">
    /// For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type
    /// parameters.
    /// </param>
    LayoutColumn(std::string_view path, const LayoutType* type, TypeArgumentList typeArgs,
                 StorageKind storage, const LayoutColumn* parent,
                 uint32_t index, uint32_t offset, LayoutBit nullBit, LayoutBit boolBit, uint32_t length = 0) noexcept;

    void SetIndex(uint32_t index) { m_index = index; }
    void SetOffset(uint32_t offset) { m_offset = offset; }

    /// <summary>Computes the full logical path to the column.</summary>
    /// <param name="parent">The layout of the parent scope, if a nested column, otherwise null.</param>
    /// <param name="path">The path to the field relative to parent scope.</param>
    /// <returns>The full logical path.</returns>
    [[nodiscard]]
    static tla::string GetFullPath(const LayoutColumn* parent, std::string_view path) noexcept;

    /// <summary>
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Fixed" /> then the fixed number of
    /// bytes reserved for the value.
    /// <para />
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Variable" /> then the maximum number of
    /// bytes allowed for the value.
    /// </summary>
    uint32_t m_size;

    /// <summary>The relative path of the field within its parent scope.</summary>
    std::string m_path;

    /// <summary>The full logical path of the field within the row.</summary>
    std::string m_fullPath;

    /// <summary>The physical layout type of the field.</summary>
    TypeArgument m_typeArg;

    /// <summary>The storage kind of the field.</summary>
    StorageKind m_storage;

    /// <summary>The layout of the parent scope, if a nested column, otherwise null.</summary>
    const LayoutColumn* m_parent;

    /// <summary>
    /// 0-based index of the column within the structure.  Also indicates which presence bit
    /// controls this column.
    /// </summary>
    uint32_t m_index;

    /// <summary>
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Fixed" /> then the byte offset to
    /// the field location.
    /// <para />
    /// If <see cref="GetStorage()" /> equals <see cref="StorageKind::Variable" /> then the 0-based index of the
    /// field from the beginning of the variable length segment.
    /// <para />
    /// For all other values of <see cref="GetStorage()" />, <see cref="GetOffset()" /> is ignored.
    /// </summary>
    uint32_t m_offset;

    /// <summary>For nullable fields, the 0-based index into the bit mask for the null bit.</summary>
    LayoutBit m_nullBit;

    /// <summary>For bool fields, 0-based index into the bit mask for the bool value.</summary>
    LayoutBit m_boolBit;
  };
}
