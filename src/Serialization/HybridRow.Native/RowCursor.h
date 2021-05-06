// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include "StringTokenizer.h"
#include "TypeArgument.h"
#include "TypeArgumentList.h"
#include "LayoutType.h"
#include "Layout.h"

namespace cdb_hr_test
{
  class LayoutCompilerUnitTests;
}

namespace cdb_hr
{
  struct RowReader;
  struct RowWriter;
  class RowBuffer;

  struct RowCursor final
  {
    RowCursor() noexcept:
      m_layout{nullptr},
      m_scopeType{},
      m_scopeTypeArgs{},
      m_immutable{false},
      m_deferUniqueIndex{false},
      m_start{0},
      m_exists{false},
      m_writePath{},
      m_writePathToken{},
      m_pathOffset{0},
      m_pathToken{0},
      m_metaOffset{0},
      m_cellType{},
      m_valueOffset{0},
      m_endOffset{0},
      m_count{0},
      m_index{0},
      m_cellTypeArgs{} {}

    ~RowCursor() noexcept = default;

    RowCursor(const RowCursor& other) noexcept :  // NOLINT(modernize-use-equals-default)
      m_layout{other.m_layout},
      m_scopeType{other.m_scopeType},
      m_scopeTypeArgs{other.m_scopeTypeArgs},
      m_immutable{other.m_immutable},
      m_deferUniqueIndex{other.m_deferUniqueIndex},
      m_start{other.m_start},
      m_exists{other.m_exists},
      m_writePath{other.m_writePath},
      m_writePathToken{other.m_writePathToken},
      m_pathOffset{other.m_pathOffset},
      m_pathToken{other.m_pathToken},
      m_metaOffset{other.m_metaOffset},
      m_cellType{other.m_cellType},
      m_valueOffset{other.m_valueOffset},
      m_endOffset{other.m_endOffset},
      m_count{other.m_count},
      m_index{other.m_index},
      m_cellTypeArgs{other.m_cellTypeArgs} {}

    RowCursor(RowCursor&& other) noexcept = default;

    RowCursor& operator=(const RowCursor& other) noexcept
    {
      if(this == &other) {return *this;}
      m_layout = other.m_layout;
      m_scopeType = other.m_scopeType;
      m_scopeTypeArgs = other.m_scopeTypeArgs;
      m_immutable = other.m_immutable;
      m_deferUniqueIndex = other.m_deferUniqueIndex;
      m_start = other.m_start;
      m_exists = other.m_exists;
      m_writePath = other.m_writePath;
      m_writePathToken = other.m_writePathToken;
      m_pathOffset = other.m_pathOffset;
      m_pathToken = other.m_pathToken;
      m_metaOffset = other.m_metaOffset;
      m_cellType = other.m_cellType;
      m_valueOffset = other.m_valueOffset;
      m_endOffset = other.m_endOffset;
      m_count = other.m_count;
      m_index = other.m_index;
      m_cellTypeArgs = other.m_cellTypeArgs;
      return *this;
    }

    RowCursor& operator=(RowCursor&& other) noexcept = default;

    static RowCursor Create(const RowBuffer& row) noexcept;
    static RowCursor CreateForAppend(const RowBuffer& row) noexcept;

    /// <summary>Makes a copy of the current cursor.</summary>
    /// <remarks>
    /// The two cursors will have independent and unconnected lifetimes after cloning.  However,
    /// mutations to a <see cref="RowBuffer" /> can invalidate any active cursors over the same row.
    /// </remarks>
    [[nodiscard]]
    RowCursor Clone() const noexcept;

    /// <summary>Returns an equivalent scope that is read-only.</summary>
    [[nodiscard]]
    RowCursor AsReadOnly() const noexcept;

    /// <summary>
    /// Moves the current cursor to the child cell of the current scope with path <paramref name="path"/>.
    /// </summary>
    /// <param name="row">The row buffer.</param>
    /// <param name="path">The path to find.</param>
    /// <returns>A reference to this.</returns>
    RowCursor& Find(const RowBuffer& row, std::string_view path) noexcept;
    RowCursor& Find(const RowBuffer& row, const StringTokenizer::StringToken& pathToken) noexcept;

    bool MoveNext(const RowBuffer& row) noexcept;
    bool MoveNext(const RowBuffer& row, RowCursor& childScope) noexcept;
    bool MoveTo(const RowBuffer& row, uint32_t index) noexcept;
    void Skip(const RowBuffer& row, RowCursor& childScope) noexcept;

    /// <summary>For schematized sparse fields, the token of the path, otherwise 0.</summary>
    [[nodiscard]] uint64_t GetToken() const noexcept { return static_cast<uint64_t>(m_pathToken); }

    /// <summary>For indexed scopes (e.g. Array), the 0-based index into the scope of the next insertion.</summary>
    [[nodiscard]] uint32_t GetIndex() const noexcept { return m_index; }

    /// <summary>If true, this scope's nested fields cannot be updated individually.</summary>
    /// <remarks>The entire scope can still be replaced.</remarks>
    [[nodiscard]] bool GetImmutable() const noexcept { return m_immutable; }

    /// <summary>The kind of scope.</summary>
    [[nodiscard]] const LayoutScope* GetScopeType() const noexcept { return m_scopeType; }

    /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
    [[nodiscard]] const TypeArgumentList& GetScopeTypeArgs() const noexcept { return m_scopeTypeArgs; }

    /// <summary>The layout describing the contents of the scope, or null if the scope is unschematized.</summary>
    [[nodiscard]] const Layout& GetLayout() const noexcept { return *m_layout; }

    /// <summary>The full logical type.</summary>
    [[nodiscard]] TypeArgument GetTypeArg() const noexcept { return TypeArgument(m_cellType, m_cellTypeArgs); }

    [[nodiscard]] tla::string ToString() const noexcept;

  private:
    friend class ScalarLayoutTypeBase;
    friend class LayoutIndexedScope;
    friend class LayoutType;
    friend class LayoutScope;
    friend class LayoutInt8;
    friend class LayoutInt16;
    friend class LayoutInt32;
    friend class LayoutInt64;
    friend class LayoutUInt8;
    friend class LayoutUInt16;
    friend class LayoutUInt32;
    friend class LayoutUInt64;
    friend class LayoutVarInt;
    friend class LayoutVarUInt;
    friend class LayoutFloat32;
    friend class LayoutFloat64;
    friend class LayoutFloat128;
    friend class LayoutDecimal;
    friend class LayoutDateTime;
    friend class LayoutUnixDateTime;
    friend class LayoutGuid;
    friend class LayoutMongoDbObjectId;
    friend class LayoutNull;
    friend class LayoutBoolean;
    friend class LayoutUtf8;
    friend class LayoutBinary;
    friend class LayoutObject;
    friend class LayoutArray;
    friend class LayoutTypedArray;
    friend class LayoutTypedSet;
    friend class LayoutTypedMap;
    friend class LayoutTuple;
    friend class LayoutTypedTuple;
    friend class LayoutTagged;
    friend class LayoutTagged2;
    friend class LayoutNullable;
    friend class LayoutUniqueScope;
    friend class RowBuffer;
    friend struct RowReader;
    friend struct RowWriter;
    template<typename, typename, typename, typename>
    friend struct TypedMapHybridRowSerializer;
    friend class cdb_hr_test::LayoutCompilerUnitTests;

    [[nodiscard]]
    RowCursor(const Layout& layout,
              const LayoutScope* scopeType,
              TypeArgumentList scopeTypeArgs,
              uint32_t start,
              uint32_t metaOffset,
              uint32_t valueOffset,
              bool immutable = false,
              uint32_t count = 0,
              uint32_t index = 0) noexcept :
      m_layout{&layout},
      m_scopeType{scopeType},
      m_scopeTypeArgs{std::move(scopeTypeArgs)},
      m_immutable{immutable},
      m_deferUniqueIndex{false},
      m_start{start},
      m_exists{false},
      m_writePath{},
      m_writePathToken{},
      m_pathOffset{0},
      m_pathToken{0},
      m_metaOffset{metaOffset},
      m_cellType{nullptr},
      m_valueOffset{valueOffset},
      m_endOffset{0},
      m_count{count},
      m_index{index},
      m_cellTypeArgs{} {}

    [[nodiscard]]
    RowCursor(const Layout& layout,
              const LayoutScope* scopeType,
              TypeArgumentList scopeTypeArgs,
              bool immutable,
              bool deferUniqueIndex,
              uint32_t start,
              bool exists,
              tla::string writePath,
              StringTokenizer::StringToken writePathToken,
              uint32_t pathOffset,
              uint32_t pathToken,
              uint32_t metaOffset,
              const LayoutType* cellType,
              uint32_t valueOffset,
              uint32_t endOffset,
              uint32_t count,
              uint32_t index,
              TypeArgumentList cellTypeArgs) noexcept :
      m_layout{&layout},
      m_scopeType{scopeType},
      m_scopeTypeArgs{std::move(scopeTypeArgs)},
      m_immutable{immutable},
      m_deferUniqueIndex{deferUniqueIndex},
      m_start{start},
      m_exists{exists},
      m_writePath{std::move(writePath)},
      m_writePathToken{writePathToken},
      m_pathOffset{pathOffset},
      m_pathToken{pathToken},
      m_metaOffset{metaOffset},
      m_cellType{cellType},
      m_valueOffset{valueOffset},
      m_endOffset{endOffset},
      m_count{count},
      m_index{index},
      m_cellTypeArgs{std::move(cellTypeArgs)} {}

    /// <summary>The layout describing the contents of the scope, or null if the scope is unschematized.</summary>
    const Layout* m_layout;

    /// <summary>The kind of scope within which this edit was prepared.</summary>
    const LayoutScope* m_scopeType;

    /// <summary>The type parameters of the scope within which this edit was prepared.</summary>
    TypeArgumentList m_scopeTypeArgs;

    /// <summary>If true, this scope's nested fields cannot be updated individually.</summary>
    /// <remarks>The entire scope can still be replaced.</remarks>
    bool m_immutable;

    /// <summary>If true, this scope is an unique index scope whose index will be built after its items are written.</summary>
    bool m_deferUniqueIndex;

    /// <summary>
    /// The 0-based byte offset from the beginning of the row where the first sparse field within
    /// the scope begins.
    /// </summary>
    uint32_t m_start;

    /// <summary>True if an existing field matching the search criteria was found.</summary>
    bool m_exists;

    /// <summary>If existing, the scope relative path for writing.</summary>
    tla::string m_writePath;

    /// <summary>If WritePath is tokenized, then its token.</summary>
    StringTokenizer::StringToken m_writePathToken;

    /// <summary>If existing, the offset scope relative path for reading.</summary>
    uint32_t m_pathOffset;

    /// <summary>If existing, the layout string token of scope relative path for reading.</summary>
    uint32_t m_pathToken;

    /// <summary>
    /// If existing, the offset to the metadata of the existing field, otherwise the location to
    /// insert a new field.
    /// </summary>
    uint32_t m_metaOffset;

    /// <summary>If existing, the layout code of the existing field, otherwise undefined.</summary>
    const LayoutType* m_cellType;

    /// <summary>If existing, the offset to the value of the existing field, otherwise undefined.</summary>
    uint32_t m_valueOffset;

    /// <summary>
    /// If existing, the offset to the end of the existing field. Used as a hint when skipping
    /// forward.
    /// </summary>
    uint32_t m_endOffset;

    /// <summary>For sized scopes (e.g. Typed Array), the number of elements.</summary>
    uint32_t m_count;

    /// <summary>For indexed scopes (e.g. Array), the 0-based index into the scope of the sparse field.</summary>
    uint32_t m_index;

    /// <summary>For types with generic parameters (e.g. <see cref="LayoutTuple" />, the type parameters.</summary>
    TypeArgumentList m_cellTypeArgs;
  };
}
