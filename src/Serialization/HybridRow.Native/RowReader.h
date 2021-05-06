// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "LayoutCodeTraits.h"
#include "Result.h"
#include "RowBuffer.h"
#include "RowCursor.h"
#include "IHybridRowSerializer.h"

namespace cdb_hr
{
  class LayoutColumn;

  /// <summary>A forward-only, streaming, field reader for <see cref="RowBuffer" />.</summary>
  /// <remarks>
  /// A <see cref="RowReader" /> allows the traversal in a streaming, left to right fashion, of
  /// an entire HybridRow. The row's layout provides decoding for any schematized portion of the row.
  /// However, unschematized sparse fields are read directly from the sparse segment with or without
  /// schematization allowing all fields within the row, both known and unknown, to be read.
  /// <para />
  /// Modifying a <see cref="RowBuffer" /> invalidates any reader or child reader associated with it.  In
  /// general <see cref="RowBuffer" />'s should not be mutated while being enumerated.
  /// </remarks>
  struct RowReader
  {
    /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
    /// <param name="row">The row to be read.</param>
    RowReader(RowBuffer& row) noexcept;

    /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
    /// <param name="buffer">The buffer.</param>
    /// <param name="version">The version of the Hybrid Row format to used to encoding the buffer.</param>
    /// <param name="resolver">The resolver for UDTs.</param>
    RowReader(cdb_core::Memory<byte> buffer, HybridRowVersion version, const LayoutResolver* resolver) noexcept;

    /// <summary>The length of row in bytes.</summary>
    [[nodiscard]] uint32_t GetLength() const noexcept;

    /// <summary>The root header for the row.</summary>
    [[nodiscard]] HybridRowHeader GetHeader() const noexcept;

    /// <summary>The storage placement of the field (if positioned on a field, undefined otherwise).</summary>
    [[nodiscard]] StorageKind GetStorage() const noexcept;

    /// <summary>The type of the field  (if positioned on a field, undefined otherwise).</summary>
    [[nodiscard]] const LayoutType* GetType() const noexcept;

    /// <summary>The type arguments of the field  (if positioned on a field, undefined otherwise).</summary>
    [[nodiscard]] const TypeArgumentList& GetTypeArgs() const noexcept;

    /// <summary>True if field has a value (if positioned on a field, undefined otherwise).</summary>
    /// <remarks>
    /// If the current field is a Nullable scope, this method return true if the value is not
    /// null. If the current field is a nullable Null primitive value, this method return true if the value
    /// is set (even though its values is set to null).
    /// </remarks>
    [[nodiscard]] bool HasValue() const noexcept;

    /// <summary>
    /// The path, relative to the scope, of the field (if positioned on a field, undefined
    /// otherwise).
    /// </summary>
    /// <remarks>When enumerating an indexed scope, this value is always empty (see <see cref="GetIndex" />).</remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept;

    /// <summary>
    /// The 0-based index, relative to the start of the scope, of the field (if positioned on a
    /// field, undefined otherwise).
    /// </summary>
    /// <remarks>When enumerating a non-indexed scope, this value is always 0 (see <see cref="GetPath" />).</remarks>
    [[nodiscard]] uint32_t GetIndex() const noexcept;

    /// <summary>Advances the reader to the next field.</summary>
    /// <returns>True, if there is another field to be read, false otherwise.</returns>
    [[nodiscard]] bool Read() noexcept;

    /// <summary>Read the current field as a <see cref="bool" />.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, bool> ReadBool() noexcept;

    /// <summary>Read the current field as a null.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, NullValue> ReadNull() noexcept;

    /// <summary>Read the current field as a fixed length, 8-bit, signed integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, int8_t> ReadInt8() noexcept;

    /// <summary>Read the current field as a fixed length, 16-bit, signed integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, int16_t> ReadInt16() noexcept;

    /// <summary>Read the current field as a fixed length, 32-bit, signed integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, int32_t> ReadInt32() noexcept;

    /// <summary>Read the current field as a fixed length, 64-bit, signed integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, int64_t> ReadInt64() noexcept;

    /// <summary>Read the current field as a fixed length, 8-bit, unsigned integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, uint8_t> ReadUInt8() noexcept;

    /// <summary>Read the current field as a fixed length, 16-bit, unsigned integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, uint16_t> ReadUInt16() noexcept;

    /// <summary>Read the current field as a fixed length, 32-bit, unsigned integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, uint32_t> ReadUInt32() noexcept;

    /// <summary>Read the current field as a fixed length, 64-bit, unsigned integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, uint64_t> ReadUInt64() noexcept;

    /// <summary>Read the current field as a variable length, 7-bit encoded, signed integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, int64_t> ReadVarInt() noexcept;

    /// <summary>Read the current field as a variable length, 7-bit encoded, unsigned integer.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, uint64_t> ReadVarUInt() noexcept;

    /// <summary>Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, float32_t> ReadFloat32() noexcept;

    /// <summary>Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, float64_t> ReadFloat64() noexcept;

    /// <summary>Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, float128_t> ReadFloat128() noexcept;

    /// <summary>Read the current field as a fixed length <see cref="decimal" /> value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, Decimal> ReadDecimal() noexcept;

    /// <summary>Read the current field as a fixed length <see cref="DateTime" /> value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, DateTime> ReadDateTime() noexcept;

    /// <summary>Read the current field as a fixed length <see cref="UnixDateTime" /> value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, UnixDateTime> ReadUnixDateTime() noexcept;

    /// <summary>Read the current field as a fixed length <see cref="Guid" /> value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, Guid> ReadGuid() noexcept;

    /// <summary>Read the current field as a fixed length <see cref="MongoDbObjectId" /> value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, MongoDbObjectId> ReadMongoDbObjectId() noexcept;

    /// <summary>Read the current field as a variable length, UTF8 encoded, string value.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, std::string_view> ReadString() noexcept;

    /// <summary>Read the current field as a variable length, sequence of bytes.</summary>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    std::tuple<Result, cdb_core::ReadOnlySpan<byte>> ReadBinary() noexcept;

    /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
    /// <remarks>
    /// Child readers can be used to read all sparse scope types including typed and untyped
    /// objects, arrays, tuples, set, and maps.
    /// <para />
    /// Nested child readers are independent of their parent.
    /// </remarks>
    RowReader ReadScope() noexcept;

    /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
    template<typename T, typename TSerializer, typename = std::enable_if_t<is_hybridrow_serializer_v<T, TSerializer>>>
    std::tuple<Result, std::unique_ptr<T>> ReadScope();

    /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
    /// <remarks>
    /// Child readers can be used to read all sparse scope types including typed and untyped
    /// objects, arrays, tuples, set, and maps.
    /// </remarks>
    template<typename TCallable, typename = std::is_nothrow_invocable_r<Result, TCallable, RowReader&>>
    Result ReadScope(TCallable& func) noexcept;

    /// <summary>
    /// Advance a reader to the end of a child reader. The child reader is also advanced to the
    /// end of its scope.
    /// </summary>
    /// <remarks>
    /// The reader must not have been advanced since the child reader was created with ReadScope.
    /// This method can be used when the overload of <see cref="ReadScope(TCallable)" /> that takes a
    /// function is not an option.
    /// </remarks>
    Result SkipScope(RowReader& nestedReader) noexcept;

  private:
    /// <summary>Read a generic schematized field value via the scope's layout.</summary>
    /// <typeparam name="TValue">The expected type of the field.</typeparam>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    template<typename TValue, LayoutCode code, TValue(RowBuffer::*ReadSparseFunc)(RowCursor& edit) const noexcept>
    std::tuple<Result, TValue> ReadPrimitiveValue() noexcept;

    /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
    /// <param name="row">The row to be read.</param>
    /// <param name="scope">The scope whose fields should be enumerated.</param>
    /// <remarks>
    /// A <see cref="RowReader" /> instance traverses all of the top-level fields of a given
    /// scope.  If the root scope is provided then all top-level fields in the row are enumerated.  Nested
    /// child <see cref="RowReader" /> instances can be access through the <see cref="ReadScope(TCallable)" /> method
    /// to process nested content.
    /// </remarks>
    RowReader(RowBuffer& row, RowCursor scope) noexcept;

    /// <summary>The current traversal state of the reader.</summary>
    enum States : unsigned char
    {
      /// <summary>The reader has not be started yet.</summary>
      None,

      /// <summary>Enumerating schematized fields (fixed and variable) from left to right.</summary>
      Schematized,

      /// <summary>Enumerating top-level fields of the current scope.</summary>
      Sparse,

      /// <summary>The reader has completed the scope.</summary>
      Done,
    };

    RowBuffer m_row;
    RowCursor m_cursor;
    const tla::vector<const LayoutColumn*>& m_columns;
    int m_schematizedCount;
    States m_state;
    int m_columnIndex;
  };

  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////
  // Template Definitions
  /////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////

  template<typename T, typename TSerializer, typename>
  std::tuple<Result, std::unique_ptr<T>> RowReader::ReadScope()
  {
    return TSerializer{}.Read(m_row, m_cursor, /* isRoot */ false);
  }

  /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
  /// <remarks>
  /// Child readers can be used to read all sparse scope types including typed and untyped
  /// objects, arrays, tuples, set, and maps.
  /// </remarks>
  template<typename TCallable, typename>
  Result RowReader::ReadScope(TCallable& func) noexcept
  {
    RowCursor childScope = m_row.SparseIteratorReadScope(m_cursor, true);
    RowReader nestedReader = RowReader{m_row, std::move(childScope)};
    Result result = func ? std::invoke(func, nestedReader) : Result::Success;
    if (result != Result::Success)
    {
      return result;
    }

    m_cursor.Skip(m_row, nestedReader.m_cursor);
    return Result::Success;
  }

  /// <summary>Read a generic schematized field value via the scope's layout.</summary>
  /// <typeparam name="TValue">The expected type of the field.</typeparam>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  template<typename TValue, LayoutCode code, TValue(RowBuffer::*ReadSparseFunc)(RowCursor& edit) const noexcept>
  std::tuple<Result, TValue> RowReader::ReadPrimitiveValue() noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
    {
      const LayoutColumn& col = *m_columns[m_columnIndex];
      const LayoutType* t = m_columns[m_columnIndex]->GetType();
      if (LayoutCodeTraits::Canonicalize(t->GetLayoutCode()) != code)
      {
        return {Result::TypeMismatch, {}};
      }

      switch (col.GetStorage())
      {
      case StorageKind::Fixed:
        return static_cast<const ScalarLayoutType<TValue>*>(t)->ReadFixed(m_row, m_cursor, col);
      case StorageKind::Variable:
        return static_cast<const ScalarLayoutType<TValue>*>(t)->ReadVariable(m_row, m_cursor, col);
      default:
        cdb_core::Contract::Assert(false);
        return {Result::Failure, {}};
      }
    }
    case States::Sparse:
      if (LayoutCodeTraits::Canonicalize(m_cursor.m_cellType->GetLayoutCode()) != code)
      {
        return {Result::TypeMismatch, {}};
      }

      return {Result::Success, std::invoke(ReadSparseFunc, m_row, m_cursor)};
    default:
      return {Result::Failure, {}};
    }
  }
}
