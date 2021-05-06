// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "RowReader.h"

namespace cdb_hr
{
  RowReader::RowReader(RowBuffer& row, RowCursor scope) noexcept :
    m_row{row},
    m_cursor{std::move(scope)},
    m_columns{m_cursor.GetLayout().GetColumns()},
    m_schematizedCount{static_cast<int>(m_cursor.GetLayout().GetNumFixed() + m_cursor.GetLayout().GetNumVariable())},
    m_state{States::None},
    m_columnIndex{-1} { }

  /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
  /// <param name="row">The row to be read.</param>
  RowReader::RowReader(RowBuffer& row) noexcept : RowReader(row, RowCursor::Create(row)) { }

  /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
  /// <param name="buffer">The buffer.</param>
  /// <param name="version">The version of the Hybrid Row format to used to encoding the buffer.</param>
  /// <param name="resolver">The resolver for UDTs.</param>
  RowReader::RowReader(cdb_core::Memory<byte> buffer, HybridRowVersion version,
                       const LayoutResolver* resolver) noexcept :
    m_row{buffer.AsSpan(), version, resolver, nullptr},
    m_cursor{RowCursor::Create(m_row)},
    m_columns{m_cursor.GetLayout().GetColumns()},
    m_schematizedCount{static_cast<int>(m_cursor.GetLayout().GetNumFixed() + m_cursor.GetLayout().GetNumVariable())},
    m_state{States::None},
    m_columnIndex{-1} { }

  /// <summary>The length of row in bytes.</summary>
  uint32_t RowReader::GetLength() const noexcept { return m_row.GetLength(); }

  /// <summary>The root header for the row.</summary>
  HybridRowHeader RowReader::GetHeader() const noexcept { return m_row.GetHeader(); };

  /// <summary>The storage placement of the field (if positioned on a field, undefined otherwise).</summary>
  StorageKind RowReader::GetStorage() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return m_columns[m_columnIndex]->GetStorage();
    case States::Sparse:
      return StorageKind::Sparse;
    default:
      return {};
    }
  }

  /// <summary>The type of the field  (if positioned on a field, undefined otherwise).</summary>
  const LayoutType* RowReader::GetType() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return m_columns[m_columnIndex]->GetType();
    case States::Sparse:
      return m_cursor.m_cellType;
    default:
      return {};
    }
  }

  /// <summary>The type arguments of the field  (if positioned on a field, undefined otherwise).</summary>
  const TypeArgumentList& RowReader::GetTypeArgs() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return m_columns[m_columnIndex]->GetTypeArgs();
    case States::Sparse:
      return m_cursor.m_cellTypeArgs;
    default:
      return TypeArgumentList::Empty;
    }
  }

  /// <summary>True if field has a value (if positioned on a field, undefined otherwise).</summary>
  /// <remarks>
  /// If the current field is a Nullable scope, this method return true if the value is not
  /// null. If the current field is a nullable Null primitive value, this method return true if the value
  /// is set (even though its values is set to null).
  /// </remarks>
  bool RowReader::HasValue() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return true;
    case States::Sparse:
      if (m_cursor.m_cellType->IsLayoutNullable())
      {
        RowCursor nullableScope = m_row.SparseIteratorReadScope(m_cursor, true);
        return LayoutNullable::HasValue(m_row, nullableScope) == Result::Success;
      }

      return true;
    default:
      return false;
    }
  }

  /// <summary>
  /// The path, relative to the scope, of the field (if positioned on a field, undefined
  /// otherwise).
  /// </summary>
  /// <remarks>When enumerating an indexed scope, this value is always null (see <see cref="Index" />).</remarks>
  std::string_view RowReader::GetPath() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return m_columns[m_columnIndex]->GetPath();
    case States::Sparse:
      return m_row.ReadSparsePath(m_cursor);
    default:
      return {};
    }
  }

  /// <summary>
  /// The 0-based index, relative to the start of the scope, of the field (if positioned on a
  /// field, undefined otherwise).
  /// </summary>
  /// <remarks>When enumerating a non-indexed scope, this value is always 0 (see <see cref="Path" />).</remarks>
  uint32_t RowReader::GetIndex() const noexcept
  {
    switch (m_state)
    {
    case States::Schematized:
      return 0;
    case States::Sparse:
      return m_cursor.m_index;
    default:
      return 0;
    }
  }

  /// <summary>Advances the reader to the next field.</summary>
  /// <returns>True, if there is another field to be read, false otherwise.</returns>
  bool RowReader::Read() noexcept
  {
    switch (m_state)
    {
    case States::None:
    {
      if (m_cursor.m_scopeType->IsUDT())
      {
        m_state = States::Schematized;
        goto Schematized;  // NOLINT(cppcoreguidelines-avoid-goto, hicpp-avoid-goto)
      }

      m_state = States::Sparse;
      goto Sparse;  // NOLINT(cppcoreguidelines-avoid-goto, hicpp-avoid-goto)
    }

    case States::Schematized:
    {
    Schematized:
      m_columnIndex++;
      if (m_columnIndex >= m_schematizedCount)
      {
        m_state = States::Sparse;
        goto Sparse;  // NOLINT(cppcoreguidelines-avoid-goto, hicpp-avoid-goto)
      }

      cdb_core::Contract::Assert(m_cursor.m_scopeType->IsUDT());
      const LayoutColumn& col = *m_columns[m_columnIndex];
      if (!m_row.ReadBit(m_cursor.m_start, col.GetNullBit()))
      {
        // Skip schematized values if they aren't present.
        goto Schematized;  // NOLINT(cppcoreguidelines-avoid-goto, hicpp-avoid-goto)
      }

      return true;
    }

    case States::Sparse:
    {
    Sparse:
      if (!m_cursor.MoveNext(m_row))
      {
        m_state = States::Done;
        goto Done;  // NOLINT(cppcoreguidelines-avoid-goto, hicpp-avoid-goto)
      }

      return true;
    }

    case States::Done:
    {
    Done:
      return false;
    }
    }

    return false;
  }

  /// <summary>Read the current field as a <see cref="bool" />.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, bool> RowReader::ReadBool() noexcept
  {
    return RowReader::ReadPrimitiveValue<bool, LayoutCode::Boolean, &RowBuffer::ReadSparseBool>();
  }

  /// <summary>Read the current field as a null.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, NullValue> RowReader::ReadNull() noexcept
  {
    return RowReader::ReadPrimitiveValue<NullValue, LayoutCode::Null, &RowBuffer::ReadSparseNull>();
  }

  /// <summary>Read the current field as a fixed length, 8-bit, signed integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, int8_t> RowReader::ReadInt8() noexcept
  {
    return RowReader::ReadPrimitiveValue<int8_t, LayoutCode::Int8, &RowBuffer::ReadSparseInt8>();
  }

  /// <summary>Read the current field as a fixed length, 16-bit, signed integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, int16_t> RowReader::ReadInt16() noexcept
  {
    return RowReader::ReadPrimitiveValue<int16_t, LayoutCode::Int16, &RowBuffer::ReadSparseInt16>();
  }

  /// <summary>Read the current field as a fixed length, 32-bit, signed integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, int32_t> RowReader::ReadInt32() noexcept
  {
    return RowReader::ReadPrimitiveValue<int32_t, LayoutCode::Int32, &RowBuffer::ReadSparseInt32>();
  }

  /// <summary>Read the current field as a fixed length, 64-bit, signed integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, int64_t> RowReader::ReadInt64() noexcept
  {
    return RowReader::ReadPrimitiveValue<int64_t, LayoutCode::Int64, &RowBuffer::ReadSparseInt64>();
  }

  /// <summary>Read the current field as a fixed length, 8-bit, unsigned integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, uint8_t> RowReader::ReadUInt8() noexcept
  {
    return RowReader::ReadPrimitiveValue<uint8_t, LayoutCode::UInt8, &RowBuffer::ReadSparseUInt8>();
  }

  /// <summary>Read the current field as a fixed length, 16-bit, unsigned integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, uint16_t> RowReader::ReadUInt16() noexcept
  {
    return RowReader::ReadPrimitiveValue<uint16_t, LayoutCode::UInt16, &RowBuffer::ReadSparseUInt16>();
  }

  /// <summary>Read the current field as a fixed length, 32-bit, unsigned integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, uint32_t> RowReader::ReadUInt32() noexcept
  {
    return RowReader::ReadPrimitiveValue<uint32_t, LayoutCode::UInt32, &RowBuffer::ReadSparseUInt32>();
  }

  /// <summary>Read the current field as a fixed length, 64-bit, unsigned integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, uint64_t> RowReader::ReadUInt64() noexcept
  {
    return RowReader::ReadPrimitiveValue<uint64_t, LayoutCode::UInt64, &RowBuffer::ReadSparseUInt64>();
  }

  /// <summary>Read the current field as a variable length, 7-bit encoded, signed integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, int64_t> RowReader::ReadVarInt() noexcept
  {
    return RowReader::ReadPrimitiveValue<int64_t, LayoutCode::VarInt, &RowBuffer::ReadSparseVarInt>();
  }

  /// <summary>Read the current field as a variable length, 7-bit encoded, unsigned integer.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, uint64_t> RowReader::ReadVarUInt() noexcept
  {
    return RowReader::ReadPrimitiveValue<uint64_t, LayoutCode::VarUInt, &RowBuffer::ReadSparseVarUInt>();
  }

  /// <summary>Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, float32_t> RowReader::ReadFloat32() noexcept
  {
    return RowReader::ReadPrimitiveValue<float32_t, LayoutCode::Float32, &RowBuffer::ReadSparseFloat32>();
  }

  /// <summary>Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, float64_t> RowReader::ReadFloat64() noexcept
  {
    return RowReader::ReadPrimitiveValue<float64_t, LayoutCode::Float64, &RowBuffer::ReadSparseFloat64>();
  }

  /// <summary>Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, float128_t> RowReader::ReadFloat128() noexcept
  {
    return RowReader::ReadPrimitiveValue<float128_t, LayoutCode::Float128, &RowBuffer::ReadSparseFloat128>();
  }

  /// <summary>Read the current field as a fixed length <see cref="decimal" /> value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, Decimal> RowReader::ReadDecimal() noexcept
  {
    return RowReader::ReadPrimitiveValue<Decimal, LayoutCode::Decimal, &RowBuffer::ReadSparseDecimal>();
  }

  /// <summary>Read the current field as a fixed length <see cref="DateTime" /> value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, DateTime> RowReader::ReadDateTime() noexcept
  {
    return RowReader::ReadPrimitiveValue<DateTime, LayoutCode::DateTime, &RowBuffer::ReadSparseDateTime>();
  }

  /// <summary>Read the current field as a fixed length <see cref="UnixDateTime" /> value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, UnixDateTime> RowReader::ReadUnixDateTime() noexcept
  {
    return RowReader::ReadPrimitiveValue<UnixDateTime, LayoutCode::UnixDateTime, &RowBuffer::ReadSparseUnixDateTime>();
  }

  /// <summary>Read the current field as a fixed length <see cref="Guid" /> value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, Guid> RowReader::ReadGuid() noexcept
  {
    return RowReader::ReadPrimitiveValue<Guid, LayoutCode::Guid, &RowBuffer::ReadSparseGuid>();
  }

  /// <summary>Read the current field as a fixed length <see cref="MongoDbObjectId" /> value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, MongoDbObjectId> RowReader::ReadMongoDbObjectId() noexcept
  {
    return RowReader::ReadPrimitiveValue<MongoDbObjectId, LayoutCode::MongoDbObjectId, &
      RowBuffer::ReadSparseMongoDbObjectId>();
  }

  /// <summary>Read the current field as a variable length, UTF8 encoded, string value.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, std::string_view> RowReader::ReadString() noexcept
  {
    return RowReader::ReadPrimitiveValue<std::string_view, LayoutCode::Utf8, &RowBuffer::ReadSparseString>();
  }

  /// <summary>Read the current field as a variable length, sequence of bytes.</summary>
  /// <returns>Success if the read is successful, an error code otherwise.</returns>
  std::tuple<Result, cdb_core::ReadOnlySpan<byte>> RowReader::ReadBinary() noexcept
  {
    return RowReader::ReadPrimitiveValue<cdb_core::ReadOnlySpan<byte>, LayoutCode::Binary, &RowBuffer::ReadSparseBinary
    >();
  }

  /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
  /// <remarks>
  /// Child readers can be used to read all sparse scope types including typed and untyped
  /// objects, arrays, tuples, set, and maps.
  /// <para />
  /// Nested child readers are independent of their parent.
  /// </remarks>
  RowReader RowReader::ReadScope() noexcept
  {
    RowCursor newScope = m_row.SparseIteratorReadScope(m_cursor, true);
    return RowReader{m_row, newScope};
  }

  /// <summary>
  /// Advance a reader to the end of a child reader. The child reader is also advanced to the
  /// end of its scope.
  /// </summary>
  /// <remarks>
  /// The reader must not have been advanced since the child reader was created with ReadScope.
  /// This method can be used when the overload of <see cref="ReadScope(TCallable)" /> that takes a
  /// function is not an option.
  /// </remarks>
  Result RowReader::SkipScope(RowReader& nestedReader) noexcept
  {
    if (nestedReader.m_cursor.m_start != m_cursor.m_valueOffset)
    {
      return Result::Failure;
    }

    m_cursor.Skip(m_row, nestedReader.m_cursor);
    return Result::Success;
  }
}
