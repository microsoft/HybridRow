// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "HybridRowVersion.h"
#include "HybridRowHeader.h"
#include "LayoutCode.h"
#include "LayoutCodeTraits.h"
#include "RowOptions.h"
#include "RowCursor.h"
#include "RowBuffer.h"
#include "LayoutType.h"
#include "Layout.h"
#include "LayoutResolver.h"

namespace cdb_hr
{
  /// <summary>
  /// <see cref="UniqueIndexItem" /> represents a single item within a set/map scope that needs
  /// to be indexed.
  /// </summary>
  /// <remarks>
  /// This structure is used when rebuilding a set/map index during row streaming via
  /// <see cref="IO.RowWriter" />.
  /// <para />
  /// Each item encodes its offsets and length within the row.
  /// </remarks>
  struct RowBuffer::UniqueIndexItem final
  {
    /// <summary>The layout code of the value.</summary>
    LayoutCode Code;

    /// <summary>
    /// If existing, the offset to the metadata of the existing field, otherwise the location to
    /// insert a new field.
    /// </summary>
    uint32_t MetaOffset;

    /// <summary>If existing, the offset to the value of the existing field, otherwise undefined.</summary>
    uint32_t ValueOffset;

    /// <summary>Size of the target element.</summary>
    uint32_t Size;
  };

  RowBuffer::RowBuffer(uint32_t capacity, ISpanResizer<byte>* resizer) noexcept :
    m_resizer(resizer),
    m_buffer(m_resizer->Resize(capacity)),
    m_resolver(nullptr),
    m_length(0) { }

  RowBuffer::RowBuffer(cdb_core::Span<byte> buffer, HybridRowVersion version, const LayoutResolver* resolver,
                       ISpanResizer<byte>* resizer) noexcept :
    m_resizer(resizer),
    m_buffer(buffer),
    m_resolver(resolver),
    m_length(buffer.Length())
  {
    cdb_core::Contract::Requires(buffer.Length() >= HybridRowHeader::Size);

    HybridRowHeader header = ReadHeader(0);
    cdb_core::Contract::Invariant(header.GetVersion() == version);
    const Layout& layout = resolver->Resolve(header.GetSchemaId());
    cdb_core::Contract::Assert(header.GetSchemaId() == layout.GetSchemaId());
    cdb_core::Contract::Invariant(HybridRowHeader::Size + layout.GetSize() <= m_length);
  }

  void RowBuffer::Reset() noexcept
  {
    m_length = 0;
    m_resolver = nullptr;
  }

  #if false
        byte[] RowBuffer::ToArray() const noexcept
        {
            return m_buffer.Slice(0, m_length).ToArray();
        }

        /// <summary>Copies the content of the buffer into the target stream.</summary>
        void RowBuffer::WriteTo(Stream stream) const noexcept
        {
            stream.Write(m_buffer.Slice(0, m_length));
        }

        /// <summary>
        /// Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
        /// with the associated layout and rowVersion.
        /// </summary>
        /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
        bool RowBuffer::ReadFrom(Stream inputStream, uint32_t bytesCount, HybridRowVersion rowVersion, const LayoutResolver* resolver) noexcept
        {
            Contract::Requires(inputStream != nullptr);
            Contract::Assert(bytesCount >= HybridRowHeader::Size);

            Reset();
            m_resolver = resolver;
            Ensure(bytesCount);
            Contract::Assert(m_buffer.Length() >= bytesCount);
            m_length = bytesCount;
            Span<byte> active = m_buffer.Slice(0, bytesCount);
            int bytesRead;
            do
            {
                bytesRead = inputStream.Read(active);
                active = active.Slice(bytesRead);
            }
            while (bytesRead != 0);

            if (active.Length() != 0)
            {
                return false;
            }

            return InitReadFrom(rowVersion);
        }
  #endif

  /// <summary>
  /// Reads in the contents of the RowBuffer from an existing block of memory and initializes
  /// the row buffer with the associated layout and rowVersion.
  /// </summary>
  /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
  bool RowBuffer::ReadFrom(cdb_core::ReadOnlySpan<byte> input, HybridRowVersion rowVersion,
                           const LayoutResolver* resolver) noexcept
  {
    uint32_t bytesCount = input.Length();
    cdb_core::Contract::Assert(bytesCount >= HybridRowHeader::Size);

    Reset();
    m_resolver = resolver;
    Ensure(bytesCount);
    cdb_core::Contract::Assert(m_buffer.Length() >= bytesCount);
    input.CopyTo(m_buffer);
    m_length = bytesCount;
    return InitReadFrom(rowVersion);
  }

  /// <summary>Initializes a row to the minimal size for the given layout.</summary>
  /// <param name="version">The version of the Hybrid Row format to use for encoding this row.</param>
  /// <param name="layout">The layout that describes the column layout of the row.</param>
  /// <param name="resolver">The resolver for UDTs.</param>
  /// <remarks>
  /// The row is initialized to default row for the given layout.  All fixed columns have their
  /// default values.  All variable columns are null.  No sparse columns are present. The row is valid.
  /// </remarks>
  void RowBuffer::InitLayout(HybridRowVersion version, const Layout& layout, const LayoutResolver* resolver) noexcept
  {
    m_resolver = resolver;

    // Ensure sufficient space for fixed schema fields.
    Ensure(HybridRowHeader::Size + layout.GetSize());
    m_length = HybridRowHeader::Size + layout.GetSize();

    // Clear all presence bits.
    m_buffer.Slice(HybridRowHeader::Size, layout.GetSize()).Fill(std::byte{0});

    // Set the header.
    WriteHeader(0, HybridRowHeader{version, layout.GetSchemaId()});
  }

  void RowBuffer::WriteHeader(uint32_t offset, HybridRowHeader value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  HybridRowHeader RowBuffer::ReadHeader(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<HybridRowHeader>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteSchemaId(uint32_t offset, SchemaId value) noexcept
  {
    WriteInt32(offset, value.Id());
  }

  SchemaId RowBuffer::ReadSchemaId(uint32_t offset) const noexcept
  {
    return SchemaId(ReadInt32(offset));
  }

  void RowBuffer::SetBit(uint32_t offset, LayoutBit bit) noexcept
  {
    // If the bit to be read is itself undefined, then return true.  This is used to
    // short-circuit the non-nullable vs. nullable field cases.  For nullable fields
    // the bit indicates the presence bit to be read for the field.  For non-nullable
    // fields there is no presence bit, and so "undefined" is passed and true is always 
    // returned indicating the field *is* present (as non-nullable fields are ALWAYS
    // present).
    if (bit.IsInvalid())
    {
      return;
    }

    m_buffer[bit.GetOffset(offset)] |= static_cast<byte>(1 << bit.GetBit());
  }

  void RowBuffer::UnsetBit(uint32_t offset, LayoutBit bit) noexcept
  {
    cdb_core::Contract::Assert(bit != LayoutBit::Invalid());
    m_buffer[bit.GetOffset(offset)] &= static_cast<byte>(~(1 << bit.GetBit()));
  }

  bool RowBuffer::ReadBit(uint32_t offset, LayoutBit bit) const noexcept
  {
    // If the bit to be read is itself undefined, then return true.  This is used to
    // short-circuit the non-nullable vs. nullable field cases.  For nullable fields
    // the bit indicates the presence bit to be read for the field.  For non-nullable
    // fields there is no presence bit, and so "undefined" is passed and true is always 
    // returned indicating the field *is* present (as non-nullable fields are ALWAYS
    // present).
    if (bit.IsInvalid())
    {
      return true;
    }

    return (m_buffer[bit.GetOffset(offset)] & static_cast<byte>(1 << bit.GetBit())) != static_cast<byte>(0);
  }

  void RowBuffer::DeleteVariable(uint32_t offset, bool isVarint) noexcept
  {
    auto [existingValueBytes, spaceAvailable] = Read7BitEncodedUInt(offset);
    if (!isVarint)
    {
      cdb_core::Contract::Invariant(existingValueBytes < m_length);
      spaceAvailable += static_cast<uint32_t>(existingValueBytes); // "size" already in spaceAvailable
    }

    m_buffer.Slice(offset + spaceAvailable, m_length - (offset + spaceAvailable)).CopyTo(m_buffer.Slice(offset));
    m_length -= spaceAvailable;
  }

  void RowBuffer::WriteInt8(uint32_t offset, int8_t value) noexcept
  {
    m_buffer[offset] = static_cast<std::byte>(value);
  }

  int8_t RowBuffer::ReadInt8(uint32_t offset) const noexcept
  {
    return static_cast<int8_t>(m_buffer[offset]);
  }

  void RowBuffer::WriteUInt8(uint32_t offset, uint8_t value) noexcept
  {
    m_buffer[offset] = static_cast<std::byte>(value);
  }

  uint8_t RowBuffer::ReadUInt8(uint32_t offset) const noexcept
  {
    return static_cast<uint8_t>(m_buffer[offset]);
  }

  void RowBuffer::WriteInt16(uint32_t offset, int16_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  int16_t RowBuffer::ReadInt16(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<int16_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteUInt16(uint32_t offset, uint16_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  uint16_t RowBuffer::ReadUInt16(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<uint16_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteInt32(uint32_t offset, int32_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  int32_t RowBuffer::ReadInt32(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<int>(m_buffer.Slice(offset));
  }

  void RowBuffer::IncrementUInt32(uint32_t offset, uint32_t increment) noexcept
  {
    cdb_core::MemoryMarshal::Cast<byte, uint32_t>(m_buffer.Slice(offset))[0] += increment;
  }

  void RowBuffer::DecrementUInt32(uint32_t offset, uint32_t decrement) noexcept
  {
    cdb_core::MemoryMarshal::Cast<byte, uint32_t>(m_buffer.Slice(offset))[0] -= decrement;
  }

  void RowBuffer::WriteUInt32(uint32_t offset, uint32_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  uint32_t RowBuffer::ReadUInt32(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<uint32_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteInt64(uint32_t offset, int64_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  int64_t RowBuffer::ReadInt64(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<int64_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteUInt64(uint32_t offset, uint64_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  uint64_t RowBuffer::ReadUInt64(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<uint64_t>(m_buffer.Slice(offset));
  }

  uint32_t RowBuffer::Write7BitEncodedUInt(uint32_t offset, uint64_t value) noexcept
  {
    // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
    // when set, indicates there are more bytes.
    uint32_t i = 0;
    while (value >= 0x80)
    {
      WriteUInt8(offset + i++, static_cast<uint8_t>(value | 0x80));
      value >>= 7;
    }

    WriteUInt8(offset + i++, static_cast<uint8_t>(value));
    return i;
  }

  /// <summary>Returns (uint64_t value, uint32_t lenInBytes)</summary>
  std::tuple<uint64_t, uint32_t> RowBuffer::Read7BitEncodedUInt(uint32_t offset) const noexcept
  {
    // Read out an unsigned long 7 bits at a time.  The high bit of the byte,
    // when set, indicates there are more bytes.
    uint64_t b = static_cast<uint64_t>(m_buffer[offset]);
    if (b < 0x80)
    {
      return {b, 1};
    }

    uint64_t retval = b & 0x7F;
    int shift = 7;
    do
    {
      cdb_core::Contract::Assert(shift < 10 * 7);
      b = static_cast<uint64_t>(m_buffer[++offset]);
      retval |= (b & 0x7F) << shift;
      shift += 7;
    } while (b >= 0x80);

    return {retval, shift / 7};
  }

  uint32_t RowBuffer::Write7BitEncodedInt(uint32_t offset, int64_t value) noexcept
  {
    return Write7BitEncodedUInt(offset, RotateSignToLsb(value));
  }

  std::tuple<int64_t, uint32_t> RowBuffer::Read7BitEncodedInt(uint32_t offset) const noexcept
  {
    auto [value, lenInBytes] = Read7BitEncodedUInt(offset);
    return {RotateSignToMsb(value), lenInBytes};
  }

  void RowBuffer::WriteFloat32(uint32_t offset, float32_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  float32_t RowBuffer::ReadFloat32(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<float32_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteFloat64(uint32_t offset, float64_t value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  float64_t RowBuffer::ReadFloat64(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<float64_t>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteFloat128(uint32_t offset, Float128 value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  Float128 RowBuffer::ReadFloat128(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<Float128>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteDecimal(uint32_t offset, Decimal value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  Decimal RowBuffer::ReadDecimal(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<Decimal>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteDateTime(uint32_t offset, DateTime value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  DateTime RowBuffer::ReadDateTime(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<DateTime>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteUnixDateTime(uint32_t offset, UnixDateTime value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  UnixDateTime RowBuffer::ReadUnixDateTime(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<UnixDateTime>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteGuid(uint32_t offset, Guid value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  Guid RowBuffer::ReadGuid(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<Guid>(m_buffer.Slice(offset));
  }

  void RowBuffer::WriteMongoDbObjectId(uint32_t offset, MongoDbObjectId value) noexcept
  {
    cdb_core::MemoryMarshal::Write(m_buffer.Slice(offset), value);
  }

  MongoDbObjectId RowBuffer::ReadMongoDbObjectId(uint32_t offset) const noexcept
  {
    return cdb_core::MemoryMarshal::Read<MongoDbObjectId>(m_buffer.Slice(offset));
  }

  std::string_view RowBuffer::ReadFixedString(uint32_t offset, uint32_t len) const noexcept
  {
    return cdb_core::Utf8Span::UnsafeFromUtf8BytesNoValidation(m_buffer.Slice(offset, len));
  }

  void RowBuffer::WriteFixedString(uint32_t offset, std::string_view value) noexcept
  {
    cdb_core::Utf8Span::GetSpan(value).CopyTo(m_buffer.Slice(offset));
  }

  cdb_core::ReadOnlySpan<byte> RowBuffer::ReadFixedBinary(uint32_t offset, uint32_t len) const noexcept
  {
    return m_buffer.Slice(offset, len);
  }

  void RowBuffer::WriteFixedBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value, uint32_t len) noexcept
  {
    value.CopyTo(m_buffer.Slice(offset, len));
    if (value.Length() < len)
    {
      m_buffer.Slice(offset + value.Length(), len - value.Length()).Fill(std::byte{0});
    }
  }

  std::string_view RowBuffer::ReadVariableString(uint32_t offset) const noexcept
  {
    auto [value, sizeLenInBytes] = ReadString(offset);
    return value;
  }

  /// <returns>number of bytes shifted (may be negative).</returns>
  int32_t RowBuffer::WriteVariableString(uint32_t offset, const std::string_view value, bool exists) noexcept
  {
    uint32_t numBytes = static_cast<uint32_t>(value.size());
    auto [spaceNeeded, shift] = EnsureVariable(offset, false, numBytes, exists);

    uint32_t sizeLenInBytes = WriteString(offset, value);
    cdb_core::Contract::Assert(spaceNeeded == numBytes + sizeLenInBytes);
    m_length += shift;
    return shift;
  }

  cdb_core::ReadOnlySpan<byte> RowBuffer::ReadVariableBinary(uint32_t offset) const noexcept
  {
    auto [value, sizeLenInBytes] = ReadBinary(offset);
    return value;
  }

  /// <returns>number of bytes shifted (may be negative).</returns>
  int32_t RowBuffer::WriteVariableBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value, bool exists) noexcept
  {
    uint32_t numBytes = value.Length();
    auto [spaceNeeded, shift] = EnsureVariable(offset, false, numBytes, exists);

    uint32_t sizeLenInBytes = WriteBinary(offset, value);
    cdb_core::Contract::Assert(spaceNeeded == numBytes + sizeLenInBytes);
    m_length += shift;
    return shift;
  }

  int64_t RowBuffer::ReadVariableInt(uint32_t offset) const noexcept
  {
    auto [value, lenInBytes] = Read7BitEncodedInt(offset);
    return value;
  }

  /// <returns>number of bytes shifted (may be negative).</returns>
  int32_t RowBuffer::WriteVariableInt(uint32_t offset, int64_t value, bool exists) noexcept
  {
    uint32_t numBytes = Count7BitEncodedInt(value);
    auto [spaceNeeded, shift] = EnsureVariable(offset, true, numBytes, exists);

    uint32_t sizeLenInBytes = Write7BitEncodedInt(offset, value);
    cdb_core::Contract::Assert(sizeLenInBytes == numBytes);
    cdb_core::Contract::Assert(spaceNeeded == numBytes);
    m_length += shift;
    return shift;
  }

  uint64_t RowBuffer::ReadVariableUInt(uint32_t offset) const noexcept
  {
    auto [value, lenInBytes] = Read7BitEncodedUInt(offset);
    return value;
  }

  /// <returns>number of bytes shifted (may be negative).</returns>
  int32_t RowBuffer::WriteVariableUInt(uint32_t offset, uint64_t value, bool exists) noexcept
  {
    uint32_t numBytes = Count7BitEncodedUInt(value);
    auto [spaceNeeded, shift] = EnsureVariable(offset, true, numBytes, exists);

    uint32_t sizeLenInBytes = Write7BitEncodedUInt(offset, value);
    cdb_core::Contract::Assert(sizeLenInBytes == numBytes);
    cdb_core::Contract::Assert(spaceNeeded == numBytes);
    m_length += shift;
    return shift;
  }

  const LayoutType* RowBuffer::ReadSparseTypeCode(uint32_t offset) const noexcept
  {
    return LayoutLiteral::FromCode(static_cast<LayoutCode>(ReadUInt8(offset)));
  }

  void RowBuffer::WriteSparseTypeCode(uint32_t offset, LayoutCode code) noexcept
  {
    WriteUInt8(offset, static_cast<uint8_t>(code));
  }

  template<typename T, T(RowBuffer::*ReadFunc)(uint32_t) const noexcept, typename TLayoutType>
  T RowBuffer::ReadSparseFixed(const TLayoutType* layoutType, RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, layoutType);
    edit.m_endOffset = edit.m_valueOffset + sizeof(T);
    return std::invoke(ReadFunc, this, edit.m_valueOffset);
  }

  template<typename T, void(RowBuffer::*WriteFunc)(uint32_t, T) noexcept, typename TLayoutType>
  void RowBuffer::WriteSparseFixed(const TLayoutType* layoutType, RowCursor& edit, T value,
                                   UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(T);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, layoutType, {}, numBytes, options);
    WriteSparseMetadata(edit, layoutType, {}, metaBytes);
    std::invoke(WriteFunc, this, edit.m_valueOffset, value);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + sizeof(T));
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  int8_t RowBuffer::ReadSparseInt8(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<int8_t, &RowBuffer::ReadInt8>(&LayoutLiteral::Int8, edit);
  }

  void RowBuffer::WriteSparseInt8(RowCursor& edit, int8_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<int8_t, &RowBuffer::WriteInt8>(&LayoutLiteral::Int8, edit, value, options);
  }

  int16_t RowBuffer::ReadSparseInt16(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<int16_t, &RowBuffer::ReadInt16>(&LayoutLiteral::Int16, edit);
  }

  void RowBuffer::WriteSparseInt16(RowCursor& edit, int16_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<int16_t, &RowBuffer::WriteInt16>(&LayoutLiteral::Int16, edit, value, options);
  }

  int32_t RowBuffer::ReadSparseInt32(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<int32_t, &RowBuffer::ReadInt32>(&LayoutLiteral::Int32, edit);
  }

  void RowBuffer::WriteSparseInt32(RowCursor& edit, int32_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<int32_t, &RowBuffer::WriteInt32>(&LayoutLiteral::Int32, edit, value, options);
  }

  int64_t RowBuffer::ReadSparseInt64(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<int64_t, &RowBuffer::ReadInt64>(&LayoutLiteral::Int64, edit);
  }

  void RowBuffer::WriteSparseInt64(RowCursor& edit, int64_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<int64_t, &RowBuffer::WriteInt64>(&LayoutLiteral::Int64, edit, value, options);
  }

  uint8_t RowBuffer::ReadSparseUInt8(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<uint8_t, &RowBuffer::ReadUInt8>(&LayoutLiteral::UInt8, edit);
  }

  void RowBuffer::WriteSparseUInt8(RowCursor& edit, uint8_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<uint8_t, &RowBuffer::WriteUInt8>(&LayoutLiteral::UInt8, edit, value, options);
  }

  uint16_t RowBuffer::ReadSparseUInt16(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<uint16_t, &RowBuffer::ReadUInt16>(&LayoutLiteral::UInt16, edit);
  }

  void RowBuffer::WriteSparseUInt16(RowCursor& edit, uint16_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<uint16_t, &RowBuffer::WriteUInt16>(&LayoutLiteral::UInt16, edit, value, options);
  }

  uint32_t RowBuffer::ReadSparseUInt32(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<uint32_t, &RowBuffer::ReadUInt32>(&LayoutLiteral::UInt32, edit);
  }

  void RowBuffer::WriteSparseUInt32(RowCursor& edit, uint32_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<uint32_t, &RowBuffer::WriteUInt32>(&LayoutLiteral::UInt32, edit, value, options);
  }

  uint64_t RowBuffer::ReadSparseUInt64(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<uint64_t, &RowBuffer::ReadUInt64>(&LayoutLiteral::UInt64, edit);
  }

  void RowBuffer::WriteSparseUInt64(RowCursor& edit, uint64_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<uint64_t, &RowBuffer::WriteUInt64>(&LayoutLiteral::UInt64, edit, value, options);
  }

  int64_t RowBuffer::ReadSparseVarInt(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::VarInt);
    auto [value, sizeLenInBytes] = Read7BitEncodedInt(edit.m_valueOffset);
    edit.m_endOffset = edit.m_valueOffset + sizeLenInBytes;
    return value;
  }

  void RowBuffer::WriteSparseVarInt(RowCursor& edit, int64_t value, UpdateOptions options) noexcept
  {
    uint32_t numBytes = Count7BitEncodedInt(value);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, &LayoutLiteral::VarInt, {}, numBytes, options);
    WriteSparseMetadata(edit, &LayoutLiteral::VarInt, {}, metaBytes);
    uint32_t sizeLenInBytes = Write7BitEncodedInt(edit.m_valueOffset, value);
    cdb_core::Contract::Assert(sizeLenInBytes == numBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + sizeLenInBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  uint64_t RowBuffer::ReadSparseVarUInt(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::VarUInt);
    auto [value, sizeLenInBytes] = Read7BitEncodedUInt(edit.m_valueOffset);
    edit.m_endOffset = edit.m_valueOffset + sizeLenInBytes;
    return value;
  }

  void RowBuffer::WriteSparseVarUInt(RowCursor& edit, uint64_t value, UpdateOptions options) noexcept
  {
    uint32_t numBytes = Count7BitEncodedUInt(value);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, &LayoutLiteral::VarUInt, {}, numBytes, options);
    WriteSparseMetadata(edit, &LayoutLiteral::VarUInt, {}, metaBytes);
    uint32_t sizeLenInBytes = Write7BitEncodedUInt(edit.m_valueOffset, value);
    cdb_core::Contract::Assert(sizeLenInBytes == numBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + sizeLenInBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  float32_t RowBuffer::ReadSparseFloat32(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<float32_t, &RowBuffer::ReadFloat32>(&LayoutLiteral::Float32, edit);
  }

  void RowBuffer::WriteSparseFloat32(RowCursor& edit, float32_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<float32_t, &RowBuffer::WriteFloat32>(&LayoutLiteral::Float32, edit, value, options);
  }

  float64_t RowBuffer::ReadSparseFloat64(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<float64_t, &RowBuffer::ReadFloat64>(&LayoutLiteral::Float64, edit);
  }

  void RowBuffer::WriteSparseFloat64(RowCursor& edit, float64_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<float64_t, &RowBuffer::WriteFloat64>(&LayoutLiteral::Float64, edit, value, options);
  }

  float128_t RowBuffer::ReadSparseFloat128(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<float128_t, &RowBuffer::ReadFloat128>(&LayoutLiteral::Float128, edit);
  }

  void RowBuffer::WriteSparseFloat128(RowCursor& edit, float128_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<float128_t, &RowBuffer::WriteFloat128>(&LayoutLiteral::Float128, edit, value, options);
  }

  decimal_t RowBuffer::ReadSparseDecimal(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<decimal_t, &RowBuffer::ReadDecimal>(&LayoutLiteral::Decimal, edit);
  }

  void RowBuffer::WriteSparseDecimal(RowCursor& edit, decimal_t value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<decimal_t, &RowBuffer::WriteDecimal>(&LayoutLiteral::Decimal, edit, value, options);
  }

  DateTime RowBuffer::ReadSparseDateTime(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<DateTime, &RowBuffer::ReadDateTime>(&LayoutLiteral::DateTime, edit);
  }

  void RowBuffer::WriteSparseDateTime(RowCursor& edit, DateTime value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<DateTime, &RowBuffer::WriteDateTime>(&LayoutLiteral::DateTime, edit, value, options);
  }

  UnixDateTime RowBuffer::ReadSparseUnixDateTime(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<UnixDateTime, &RowBuffer::ReadUnixDateTime>(&LayoutLiteral::UnixDateTime, edit);
  }

  void RowBuffer::WriteSparseUnixDateTime(RowCursor& edit, UnixDateTime value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<UnixDateTime, &RowBuffer::WriteUnixDateTime>(&LayoutLiteral::UnixDateTime, edit, value, options);
  }

  Guid RowBuffer::ReadSparseGuid(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<Guid, &RowBuffer::ReadGuid>(&LayoutLiteral::Guid, edit);
  }

  void RowBuffer::WriteSparseGuid(RowCursor& edit, Guid value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<Guid, &RowBuffer::WriteGuid>(&LayoutLiteral::Guid, edit, value, options);
  }

  MongoDbObjectId RowBuffer::ReadSparseMongoDbObjectId(RowCursor& edit) const noexcept
  {
    return ReadSparseFixed<MongoDbObjectId, &RowBuffer::ReadMongoDbObjectId>(&LayoutLiteral::MongoDbObjectId, edit);
  }

  void RowBuffer::WriteSparseMongoDbObjectId(RowCursor& edit, MongoDbObjectId value, UpdateOptions options) noexcept
  {
    WriteSparseFixed<MongoDbObjectId, &RowBuffer::WriteMongoDbObjectId>(&LayoutLiteral::MongoDbObjectId, edit, value,
      options);
  }

  NullValue RowBuffer::ReadSparseNull(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::Null);
    edit.m_endOffset = edit.m_valueOffset;
    return {};
  }

  void RowBuffer::WriteSparseNull(RowCursor& edit, NullValue value, UpdateOptions options) noexcept
  {
    uint32_t numBytes = 0;
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, &LayoutLiteral::Null, {}, numBytes, options);
    WriteSparseMetadata(edit, &LayoutLiteral::Null, {}, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  bool RowBuffer::ReadSparseBool(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::Boolean);
    edit.m_endOffset = edit.m_valueOffset;
    return edit.m_cellType == &LayoutLiteral::Boolean;
  }

  void RowBuffer::WriteSparseBool(RowCursor& edit, bool value, UpdateOptions options) noexcept
  {
    uint32_t numBytes = 0;
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(
      edit,
      value ? &LayoutLiteral::Boolean : &LayoutLiteral::BooleanFalse,
      {},
      numBytes,
      options);
    WriteSparseMetadata(edit, value ? &LayoutLiteral::Boolean : &LayoutLiteral::BooleanFalse, {}, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  std::string_view RowBuffer::ReadSparseString(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::Utf8);
    auto [str, sizeLenInBytes] = ReadString(edit.m_valueOffset);
    edit.m_endOffset = edit.m_valueOffset + sizeLenInBytes + static_cast<uint32_t>(str.size());
    return str;
  }

  void RowBuffer::WriteSparseString(RowCursor& edit, std::string_view value, UpdateOptions options) noexcept
  {
    uint32_t len = static_cast<uint32_t>(value.size());
    uint32_t numBytes = len + Count7BitEncodedUInt(static_cast<uint64_t>(len));
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, &LayoutLiteral::Utf8, {}, numBytes, options);
    WriteSparseMetadata(edit, &LayoutLiteral::Utf8, {}, metaBytes);
    uint32_t sizeLenInBytes = WriteString(edit.m_valueOffset, value);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + len + sizeLenInBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  cdb_core::ReadOnlySpan<byte> RowBuffer::ReadSparseBinary(RowCursor& edit) const noexcept
  {
    ValidateSparsePrimitiveTypeCode(edit, &LayoutLiteral::Binary);
    auto [span, sizeLenInBytes] = ReadBinary(edit.m_valueOffset);
    edit.m_endOffset = edit.m_valueOffset + sizeLenInBytes + span.Length();
    return span;
  }

  void RowBuffer::WriteSparseBinary(RowCursor& edit, cdb_core::ReadOnlySpan<byte> value, UpdateOptions options) noexcept
  {
    uint32_t len = value.Length();
    uint32_t numBytes = len + Count7BitEncodedUInt(static_cast<uint64_t>(len));
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, &LayoutLiteral::Binary, {}, numBytes, options);
    WriteSparseMetadata(edit, &LayoutLiteral::Binary, {}, metaBytes);
    uint32_t sizeLenInBytes = WriteBinary(edit.m_valueOffset, value);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + len + sizeLenInBytes);
    edit.m_endOffset = edit.m_metaOffset + spaceNeeded;
    m_length += shift;
  }

  RowCursor RowBuffer::WriteSparseObject(RowCursor& edit, const LayoutScope* scopeType, UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(LayoutCode); // end scope type code.
    TypeArgumentList typeArgs{};
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, {}, metaBytes);
    WriteSparseTypeCode(edit.m_valueOffset, LayoutCode::EndScope);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);

    m_length += shift;
    return RowCursor{
      *edit.m_layout,  // layout
      scopeType, // scopeType
      {}, // scopeTypeArgs
      edit.m_valueOffset, // start
      edit.m_valueOffset, // metaOffset
      edit.m_valueOffset, // valueOffset
    };
  }

  RowCursor RowBuffer::WriteSparseArray(RowCursor& edit, const LayoutScope* scopeType, UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(LayoutCode); // end scope type code.
    TypeArgumentList typeArgs{};
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    WriteSparseTypeCode(edit.m_valueOffset, LayoutCode::EndScope);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    m_length += shift;
    return RowCursor{
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start
      edit.m_valueOffset, // metaOffset
      edit.m_valueOffset, // valueOffset
    };
  }

  RowCursor RowBuffer::WriteTypedArray(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                       UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(uint32_t);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    WriteUInt32(edit.m_valueOffset, 0);
    uint32_t valueOffset = edit.m_valueOffset + sizeof(uint32_t); // Point after the Size
    m_length += shift;
    return RowCursor{
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start - Point at the Size
      valueOffset, // metaOffset
      valueOffset, // valueOffset
    };
  }

  RowCursor RowBuffer::WriteTypedSet(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                     UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(uint32_t);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    WriteUInt32(edit.m_valueOffset, 0);
    uint32_t valueOffset = edit.m_valueOffset + sizeof(uint32_t); // Point after the Size
    m_length += shift;
    return RowCursor
    {
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start - Point at the Size
      valueOffset, // metaOffset
      valueOffset, // valueOffset
    };
  }

  RowCursor RowBuffer::WriteTypedMap(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                     UpdateOptions options) noexcept
  {
    uint32_t numBytes = sizeof(uint32_t); // Sized scope.
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    WriteUInt32(edit.m_valueOffset, 0);
    uint32_t valueOffset = edit.m_valueOffset + sizeof(uint32_t); // Point after the Size
    m_length += shift;
    return RowCursor
    {
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start - Point at the Size
      valueOffset, // metaOffset
      valueOffset, // valueOffset
    };
  }

  RowCursor RowBuffer::WriteSparseTuple(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                        UpdateOptions options) noexcept
  {
    uint32_t numBytes = static_cast<uint32_t>(sizeof(LayoutCode) * (1 + typeArgs.GetCount())
    ); // nulls for each element.
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    uint32_t valueOffset = edit.m_valueOffset;
    for (size_t i = 0; i < typeArgs.GetCount(); i++)
    {
      WriteSparseTypeCode(valueOffset, LayoutCode::Null);
      valueOffset += sizeof(LayoutCode);
    }

    WriteSparseTypeCode(valueOffset, LayoutCode::EndScope);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    m_length += shift;
    return RowCursor
    {
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start
      edit.m_valueOffset, // metaOffset
      edit.m_valueOffset, // valueOffset
      false, // immutable
      static_cast<uint32_t>(typeArgs.GetCount()), // count
    };
  }

  RowCursor RowBuffer::WriteTypedTuple(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                       UpdateOptions options) noexcept
  {
    uint32_t numBytes = CountDefaultValue(scopeType, typeArgs);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    uint32_t numWritten = WriteDefaultValue(edit.m_valueOffset, scopeType, typeArgs);
    cdb_core::Contract::Assert(numBytes == numWritten);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    m_length += shift;

    RowCursor newScope = RowCursor
    {
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start
      edit.m_valueOffset, // metaOffset
      edit.m_valueOffset, // valueOffset
      false, // immutable
      static_cast<uint32_t>(typeArgs.GetCount()), // count
    };
    newScope.MoveNext(*this);
    return newScope;
  }

  RowCursor RowBuffer::WriteNullable(RowCursor& edit, const LayoutScope* scopeType, const TypeArgumentList& typeArgs,
                                     UpdateOptions options, bool hasValue) noexcept
  {
    uint32_t numBytes = CountDefaultValue(scopeType, typeArgs);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);
    uint32_t numWritten = WriteDefaultValue(edit.m_valueOffset, scopeType, typeArgs);
    cdb_core::Contract::Assert(numBytes == numWritten);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    if (hasValue)
    {
      WriteInt8(edit.m_valueOffset, 1);
    }

    m_length += shift;
    uint32_t valueOffset = edit.m_valueOffset + 1;
    RowCursor newScope = RowCursor
    {
      *edit.m_layout, // layout
      scopeType, // scopeType
      typeArgs,  // scopeTypeArgs
      edit.m_valueOffset, // start
      valueOffset, // metaOffset
      valueOffset, // valueOffset
      false, // immutable
      2, // count
      1, // index
    };
    newScope.MoveNext(*this);
    return newScope;
  }

  RowCursor RowBuffer::WriteSparseUDT(RowCursor& edit, const LayoutScope* scopeType, const Layout& udt,
                                      UpdateOptions options) noexcept
  {
    TypeArgumentList typeArgs{udt.GetSchemaId()};
    uint32_t numBytes = udt.GetSize() + sizeof(LayoutCode);
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, scopeType, typeArgs, numBytes, options);
    WriteSparseMetadata(edit, scopeType, typeArgs, metaBytes);

    // Clear all presence bits.
    m_buffer.Slice(edit.m_valueOffset, udt.GetSize()).Fill(std::byte{0});

    // Write scope terminator.
    uint32_t valueOffset = edit.m_valueOffset + udt.GetSize();
    WriteSparseTypeCode(valueOffset, LayoutCode::EndScope);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    m_length += shift;
    return RowCursor(
      udt, // layout
      scopeType,  // scopeType
      typeArgs, // scopeTypeArgs
      edit.m_valueOffset, // start
      valueOffset, // metaOffset
      valueOffset // valueOffset
    );
  }

  void RowBuffer::DeleteSparse(RowCursor& edit) noexcept
  {
    // If the field doesn't exist, then nothing to do.
    if (!edit.m_exists)
    {
      return;
    }

    uint32_t numBytes = 0;
    auto [metaBytes, spaceNeeded, shift] = EnsureSparse(edit, edit.m_cellType, edit.m_cellTypeArgs, numBytes,
      RowOptions::Delete);
    m_length += shift;
  }

  uint64_t RowBuffer::RotateSignToLsb(int64_t value) noexcept
  {
    // Rotate sign to LSB
    bool isNegative = value < 0;
    uint64_t uvalue = static_cast<uint64_t>(value);
    uvalue = isNegative ? ((~uvalue + 1ul) << 1) + 1ul : uvalue << 1;
    return uvalue;
  }

  int64_t RowBuffer::RotateSignToMsb(uint64_t uvalue) noexcept
  {
    // Rotate sign to MSB
    bool isNegative = uvalue % 2 != 0;
    int64_t value = static_cast<int64_t>(isNegative ? (~(uvalue >> 1) + 1ul) | 0x8000000000000000ul : uvalue >> 1);
    return value;
  }

  uint32_t RowBuffer::ComputeVariableValueOffset(const Layout& layout, uint32_t scopeOffset,
                                                 uint32_t varIndex) const noexcept
  {
    uint32_t index = layout.GetNumFixed() + varIndex;
    const tla::vector<const LayoutColumn*>& columns = layout.GetColumns();
    cdb_core::Contract::Assert(index <= columns.size());
    uint32_t offset = scopeOffset + layout.GetSize();
    for (uint32_t i = layout.GetNumFixed(); i < index; i++)
    {
      const LayoutColumn& col = *columns[i];
      if (ReadBit(scopeOffset, col.GetNullBit()))
      {
        auto [valueSizeInBytes, lengthSizeInBytes] = Read7BitEncodedUInt(offset);
        if (col.GetType()->IsVarint())
        {
          offset += lengthSizeInBytes;
        }
        else
        {
          offset += static_cast<uint32_t>(valueSizeInBytes) + lengthSizeInBytes;
        }
      }
    }

    return offset;
  }

  bool RowBuffer::SparseIteratorMoveNext(RowCursor& edit) const noexcept
  {
    if (edit.m_cellType != nullptr)
    {
      // Move to the next element of an indexed scope.
      if (edit.m_scopeType->IsIndexedScope())
      {
        edit.m_index++;
      }

      // Skip forward to the end of the current value.
      if (edit.m_endOffset != 0)
      {
        edit.m_metaOffset = edit.m_endOffset;
        edit.m_endOffset = 0;
      }
      else
      {
        edit.m_metaOffset += SparseComputeSize(edit);
      }
    }

    // Check if reached end of buffer.
    if (edit.m_metaOffset < m_length)
    {
      // Check if reached end of sized scope.
      if (!edit.m_scopeType->IsSizedScope() || (edit.m_index != edit.m_count))
      {
        // Read the metadata.
        ReadSparseMetadata(edit);

        // Check if reached end of sparse scope.
        if (!(edit.m_cellType->IsLayoutEndScope()))
        {
          edit.m_exists = true;
          return true;
        }
      }
    }

    edit.m_cellType = &LayoutLiteral::EndScope;
    edit.m_exists = false;
    edit.m_valueOffset = edit.m_metaOffset;
    return false;
  }

  RowCursor RowBuffer::SparseIteratorReadScope(const RowCursor& edit, bool immutable) const noexcept
  {
    LayoutCode scopeCode = edit.m_cellType->GetLayoutCode();
    const LayoutScope* scopeType =
      static_cast<const LayoutScope*>(edit.m_cellType);    // NOLINT(cppcoreguidelines-pro-type-static-cast-downcast)
    switch (LayoutCodeTraits::ClearImmutableBit(scopeCode))
    {
    case LayoutCode::ObjectScope:
    case LayoutCode::ArrayScope:
    {
      return RowCursor{
        *edit.m_layout,        // layout
        scopeType,            // scopeType
        edit.m_cellTypeArgs,  // scopeArgs
        edit.m_valueOffset,   // start
        edit.m_valueOffset,   // metaOffset
        edit.m_valueOffset,   // valueOffset
        immutable,            // immutable
      };
    }

    case LayoutCode::TypedArrayScope:
    case LayoutCode::TypedSetScope:
    case LayoutCode::TypedMapScope:
    {
      uint32_t valueOffset = edit.m_valueOffset + sizeof(uint32_t); // Point after the Size
      return RowCursor{
        *edit.m_layout,          // layout
        scopeType,              // scopeType
        edit.m_cellTypeArgs,    // scopeTypeArgs
        edit.m_valueOffset,     // start = Point at the Size
        valueOffset,            // metaOffset
        valueOffset,            // valueOffset
        immutable,              // immutable
        ReadUInt32(edit.m_valueOffset),  // count
      };
    }

    case LayoutCode::TypedTupleScope:
    case LayoutCode::TupleScope:
    case LayoutCode::TaggedScope:
    case LayoutCode::Tagged2Scope:
    {
      return RowCursor{
        *edit.m_layout,        // layout
        scopeType,            // scopeType
        edit.m_cellTypeArgs,  // scopeTypeArgs
        edit.m_valueOffset,   // start
        edit.m_valueOffset,   // metaOffset
        edit.m_valueOffset,   // valueOffset
        immutable,            // immutable
        static_cast<uint32_t>(edit.m_cellTypeArgs.GetCount()), // count
      };
    }

    case LayoutCode::NullableScope:
    {
      bool hasValue = ReadInt8(edit.m_valueOffset) != 0;
      if (hasValue)
      {
        // Start at the T so it can be read.
        uint32_t valueOffset = edit.m_valueOffset + 1;
        return RowCursor{
          *edit.m_layout,        // layout
          scopeType,            // scopeType
          edit.m_cellTypeArgs,  // scopeTypeArgs
          edit.m_valueOffset,   // start
          valueOffset,          // metaOffset
          valueOffset,          // valueOffset
          immutable,            // immutable
          2,                    // count
          1,                    // index
        };
      }

      // Start at the end of the scope, instead of at the T, so the T will be skipped.
      TypeArgument typeArg = edit.m_cellTypeArgs[0];
      uint32_t valueOffset = edit.m_valueOffset + 1 + CountDefaultValue(typeArg.GetType(), typeArg.GetTypeArgs());
      return RowCursor{
        *edit.m_layout,        // layout
        scopeType,            // scopeType
        edit.m_cellTypeArgs,  // scopeTypeArgs
        edit.m_valueOffset,   // start
        valueOffset,          // metaOffset
        valueOffset,          // valueOffset
        immutable,            // immutable
        2,                    // count
        2,                    // index
      };
    }

    case LayoutCode::Schema:
    {
      const Layout& udt = m_resolver->Resolve(edit.m_cellTypeArgs.GetSchemaId());
      uint32_t valueOffset = ComputeVariableValueOffset(udt, edit.m_valueOffset, udt.GetNumVariable());
      return RowCursor{
        udt,                  // layout
        scopeType,            // scopeType
        edit.m_cellTypeArgs,  // scopeTypeArgs
        edit.m_valueOffset,   // start
        valueOffset,          // metaOffset
        valueOffset,          // valueOffset
        immutable,            // immutable
      };
    }

    default:
      cdb_core::Contract::Fail("Not a scope type.");
    }
  }

  RowCursor RowBuffer::PrepareSparseMove(const RowCursor& scope, RowCursor& srcEdit) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUniqueScope());

    cdb_core::Contract::Requires(scope.m_index == 0);
    RowCursor dstEdit = scope.Clone();

    dstEdit.m_metaOffset = scope.m_valueOffset;
    uint32_t srcSize = SparseComputeSize(srcEdit);
    uint32_t srcBytes = srcSize - (srcEdit.m_valueOffset - srcEdit.m_metaOffset);
    while (dstEdit.m_index < dstEdit.m_count)
    {
      ReadSparseMetadata(dstEdit);
      cdb_core::Contract::Assert(dstEdit.m_pathOffset == 0);

      uint32_t elmSize = UINT32_MAX; // defer calculating the full size until needed.
      int cmp;
      if (scope.m_scopeType->IsLayoutTypedMap())
      {
        cmp = CompareKeyValueFieldValue(srcEdit, dstEdit);
      }
      else
      {
        elmSize = SparseComputeSize(dstEdit);
        uint32_t elmBytes = elmSize - (dstEdit.m_valueOffset - dstEdit.m_metaOffset);
        cmp = CompareFieldValue(srcEdit, srcBytes, dstEdit, elmBytes);
      }

      if (cmp <= 0)
      {
        dstEdit.m_exists = cmp == 0;
        return dstEdit;
      }

      elmSize = (elmSize == UINT32_MAX) ? SparseComputeSize(dstEdit) : elmSize;
      dstEdit.m_index++;
      dstEdit.m_metaOffset += elmSize;
    }

    dstEdit.m_exists = false;
    dstEdit.m_cellType = &LayoutLiteral::EndScope;
    dstEdit.m_valueOffset = dstEdit.m_metaOffset;
    return dstEdit;
  }

  void RowBuffer::TypedCollectionMoveField(RowCursor& dstEdit, RowCursor& srcEdit, RowOptions options) noexcept
  {
    uint32_t encodedSize = SparseComputeSize(srcEdit);
    uint32_t numBytes = encodedSize - (srcEdit.m_valueOffset - srcEdit.m_metaOffset);

    // Insert the field metadata into its new location.
    auto [metaBytes, spaceNeeded, shiftInsert] = EnsureSparse(dstEdit, srcEdit.m_cellType, srcEdit.m_cellTypeArgs,
      numBytes, options);
    WriteSparseMetadata(dstEdit, srcEdit.m_cellType, srcEdit.m_cellTypeArgs, metaBytes);
    cdb_core::Contract::Assert(spaceNeeded == metaBytes + numBytes);
    if (srcEdit.m_metaOffset >= dstEdit.m_metaOffset)
    {
      srcEdit.m_metaOffset += shiftInsert;
      srcEdit.m_valueOffset += shiftInsert;
    }

    // Copy the value bits from the old location.
    m_buffer.Slice(srcEdit.m_valueOffset, numBytes).CopyTo(m_buffer.Slice(dstEdit.m_valueOffset));
    m_length += shiftInsert;

    // Delete the old location.
    auto [_, __, shiftDelete] = EnsureSparse(srcEdit, srcEdit.m_cellType, srcEdit.m_cellTypeArgs, numBytes,
      RowOptions::Delete);
    cdb_core::Contract::Assert(shiftDelete < 0);
    m_length += shiftDelete;
  }

  Result RowBuffer::TypedCollectionUniqueIndexRebuild(RowCursor& scope) noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUniqueScope());
    cdb_core::Contract::Requires(scope.m_index == 0);
    RowCursor dstEdit = scope.Clone();
    if (dstEdit.m_count <= 1)
    {
      return Result::Success;
    }

    // Compute Index Elements.
    auto stackDeleter = [](void* p) -> void { ::_freea(static_cast<void*>(p)); };
    std::unique_ptr<void, decltype(stackDeleter)> uniqueIndexBuffer{
      _malloca(sizeof(UniqueIndexItem) * dstEdit.m_count),
      stackDeleter
    };
    cdb_core::Span<UniqueIndexItem> uniqueIndex{
      static_cast<UniqueIndexItem*>(uniqueIndexBuffer.get()),
      dstEdit.m_count
    };
    dstEdit.m_metaOffset = scope.m_valueOffset;
    for (; dstEdit.m_index < dstEdit.m_count; dstEdit.m_index++)
    {
      ReadSparseMetadata(dstEdit);
      cdb_core::Contract::Assert(dstEdit.m_pathOffset == 0);
      uint32_t elmSize = SparseComputeSize(dstEdit);

      uniqueIndex[dstEdit.m_index] = UniqueIndexItem
      {
        dstEdit.m_cellType->GetLayoutCode(), // Code
        dstEdit.m_metaOffset, // MetaOffset
        dstEdit.m_valueOffset, // ValueOffset
        elmSize, // Size
      };

      dstEdit.m_metaOffset += elmSize;
    }

    // Create scratch space equal to the sum of the sizes of the scope's values.
    // Implementation Note: theoretically this scratch space could be eliminated by
    // performing the item move operations directly during the Insertion Sort, however,
    // doing so might result in moving the same item multiple times.  Under the assumption
    // that items are relatively large, using scratch space requires each item to be moved
    // AT MOST once.  Given that row buffer memory is likely reused, scratch space is
    // relatively memory efficient.
    int32_t shift = dstEdit.m_metaOffset - scope.m_valueOffset;

    // Sort and check for duplicates.
    if (!InsertionSort(scope, dstEdit, uniqueIndex))
    {
      return Result::Exists;
    }

    // Move elements.
    uint32_t metaOffset = scope.m_valueOffset;
    Ensure(m_length + shift);
    m_buffer.Slice(metaOffset, m_length - metaOffset).CopyTo(m_buffer.Slice(metaOffset + shift));
    for (const UniqueIndexItem& x : uniqueIndex)
    {
      m_buffer.Slice(x.MetaOffset + shift, x.Size).CopyTo(m_buffer.Slice(metaOffset));
      metaOffset += x.Size;
    }

    // Delete the scratch space (if necessary - if it doesn't just fall off the end of the row).
    if (metaOffset != m_length)
    {
      m_buffer.Slice(metaOffset + shift, m_length - metaOffset).CopyTo(m_buffer.Slice(metaOffset));
    }

    #if _DEBUG
    // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
    m_buffer.Slice(m_length, shift).Fill(std::byte{0xFF});
    #endif

    return Result::Success;
  }

  uint32_t RowBuffer::CountSparsePath(RowCursor& edit) noexcept
  {
    if (!edit.m_writePathToken.IsNull())
    {
      return edit.m_writePathToken.GetVarint().Length();
    }

    const auto& [found, token] = edit.m_layout->GetTokenizer().TryFindToken(edit.m_writePath);
    if (found)
    {
      edit.m_writePathToken = token;
      return edit.m_writePathToken.GetVarint().Length();
    }

    size_t numBytes = edit.m_writePath.size();
    uint32_t sizeLenInBytes = Count7BitEncodedUInt(
      static_cast<uint64_t>(edit.m_layout->GetTokenizer().GetCount() + numBytes));
    return static_cast<uint32_t>(sizeLenInBytes + numBytes);
  }

  uint32_t RowBuffer::Count7BitEncodedUInt(uint64_t value) noexcept
  {
    // Count the number of bytes needed to write out an int 7 bits at a time.
    uint32_t i = 0;
    while (value >= 0x80)
    {
      i++;
      value >>= 7;
    }

    i++;
    return i;
  }

  uint32_t RowBuffer::Count7BitEncodedInt(int64_t value) noexcept
  {
    return Count7BitEncodedUInt(RotateSignToLsb(value));
  }

  bool RowBuffer::InitReadFrom(HybridRowVersion rowVersion) const noexcept
  {
    HybridRowHeader header = ReadHeader(0);
    const Layout& layout = m_resolver->Resolve(header.GetSchemaId());
    cdb_core::Contract::Assert(header.GetSchemaId() == layout.GetSchemaId());
    if ((header.GetVersion() != rowVersion) || (HybridRowHeader::Size + layout.GetSize() > m_length))
    {
      return false;
    }

    return true;
  }

  uint32_t RowBuffer::SkipScope(RowCursor& edit) const noexcept
  {
    while (SparseIteratorMoveNext(edit)) { }

    if (!edit.m_scopeType->IsSizedScope())
    {
      edit.m_metaOffset += sizeof(LayoutCode); // Move past the end of scope marker.
    }

    return edit.m_metaOffset;
  }

  int RowBuffer::CompareFieldValue(const RowCursor& left, int leftLen, const RowCursor& right,
                                   int rightLen) const noexcept
  {
    if (left.m_cellType->GetLayoutCode() < right.m_cellType->GetLayoutCode())
    {
      return -1;
    }

    if (left.m_cellType == right.m_cellType)
    {
      if (leftLen < rightLen)
      {
        return -1;
      }

      if (leftLen == rightLen)
      {
        return m_buffer.Slice(left.m_valueOffset, leftLen).SequenceCompareTo(m_buffer.Slice(right.m_valueOffset,
          rightLen));
      }
    }

    return 1;
  }

  int RowBuffer::CompareKeyValueFieldValue(const RowCursor& left, const RowCursor& right) const noexcept
  {
    cdb_core::Contract::Requires(left.m_cellType->IsLayoutTypedTuple());
    cdb_core::Contract::Requires(right.m_cellType->IsLayoutTypedTuple());
    const LayoutTypedTuple* leftScopeType = reinterpret_cast<const LayoutTypedTuple*>(left.m_cellType);
    const LayoutTypedTuple* rightScopeType = reinterpret_cast<const LayoutTypedTuple*>(right.m_cellType);
    cdb_core::Contract::Requires(left.m_cellTypeArgs.GetCount() == 2);
    cdb_core::Contract::Requires(left.m_cellTypeArgs == right.m_cellTypeArgs);

    RowCursor leftKey = RowCursor
    {
      *left.m_layout, // layout
      leftScopeType, // scopeType
      left.m_cellTypeArgs, // scopeTypeArgs
      left.m_valueOffset, // start
      left.m_valueOffset, // metaOffset
      0, // valueOffset
    };

    ReadSparseMetadata(leftKey);
    cdb_core::Contract::Assert(leftKey.m_pathOffset == 0);
    uint32_t leftKeyLen = SparseComputeSize(leftKey) - (leftKey.m_valueOffset - leftKey.m_metaOffset);

    RowCursor rightKey = RowCursor
    {
      *right.m_layout, // layout
      rightScopeType, // scopeType
      right.m_cellTypeArgs, // scopeTypeArgs
      right.m_valueOffset, // start
      right.m_valueOffset, // metaOffset
      0, // valueOffset
    };

    ReadSparseMetadata(rightKey);
    cdb_core::Contract::Assert(rightKey.m_pathOffset == 0);
    uint32_t rightKeyLen = SparseComputeSize(rightKey) - (rightKey.m_valueOffset - rightKey.m_metaOffset);

    return CompareFieldValue(leftKey, leftKeyLen, rightKey, rightKeyLen);
  }

  bool RowBuffer::InsertionSort(const RowCursor& scope, const RowCursor& dstEdit,
                                cdb_core::Span<UniqueIndexItem> uniqueIndex) const noexcept
  {
    RowCursor leftEdit = dstEdit;
    RowCursor rightEdit = dstEdit;

    for (int i = 1; i < static_cast<int>(uniqueIndex.Length()); i++)
    {
      UniqueIndexItem x = uniqueIndex[i];
      leftEdit.m_cellType = LayoutLiteral::FromCode(x.Code);
      leftEdit.m_metaOffset = x.MetaOffset;
      leftEdit.m_valueOffset = x.ValueOffset;
      uint32_t leftBytes = x.Size - (x.ValueOffset - x.MetaOffset);

      // Walk backwards searching for the insertion point for the item as position i.
      int j;
      for (j = i - 1; j >= 0; j--)
      {
        UniqueIndexItem y = uniqueIndex[j];
        rightEdit.m_cellType = LayoutLiteral::FromCode(y.Code);
        rightEdit.m_metaOffset = y.MetaOffset;
        rightEdit.m_valueOffset = y.ValueOffset;

        int cmp;
        if (scope.m_scopeType->IsLayoutTypedMap())
        {
          cmp = CompareKeyValueFieldValue(leftEdit, rightEdit);
        }
        else
        {
          uint32_t rightBytes = y.Size - (y.ValueOffset - y.MetaOffset);
          cmp = CompareFieldValue(leftEdit, leftBytes, rightEdit, rightBytes);
        }

        // If there are duplicates then fail.
        if (cmp == 0)
        {
          return false;
        }

        if (cmp > 0)
        {
          break;
        }

        // Swap the jth item to the right to make space for the ith item which is smaller.
        uniqueIndex[j + 1] = uniqueIndex[j];
      }

      // Insert the ith item into the sorted array.
      uniqueIndex[j + 1] = x;
    }

    return true;
  }

  /// <summary>Reads the length of an encoded, optionally tokenized, sparse path at the given offset.</summary>
  /// <remarks>
  /// If the path is tokenized, then returns the token, the size of the token, and the path has no offset.
  /// <p/>
  /// If the path is not tokenized, then returns the token, the length of the path include the size prefix, the path offset begins after the length prefix.
  /// </remarks>
  /// <returns>[uint32_t token, uint32_t pathLenInBytes, uint32_t pathOffset]</returns>
  std::tuple<uint32_t, uint32_t, uint32_t> RowBuffer::ReadSparsePathLen(
    const Layout& layout, uint32_t offset) const noexcept
  {
    auto [token, sizeLenInBytes] = Read7BitEncodedUInt(offset);
    const auto& tokenizer = layout.GetTokenizer();
    if (token < tokenizer.GetCount())
    {
      return {static_cast<uint32_t>(token), sizeLenInBytes, offset};
    }

    uint32_t numBytes = static_cast<uint32_t>(token) - static_cast<uint32_t>(tokenizer.GetCount());
    return {static_cast<uint32_t>(token), numBytes + sizeLenInBytes, offset + sizeLenInBytes};
  }

  std::string_view RowBuffer::ReadSparsePath(const RowCursor& edit) const noexcept
  {
    const auto& [found, path] = edit.m_layout->GetTokenizer().TryFindString(static_cast<uint64_t>(edit.m_pathToken));
    if (found)
    {
      return path;
    }

    uint32_t numBytes = edit.m_pathToken - static_cast<uint32_t>(edit.m_layout->GetTokenizer().GetCount());
    return cdb_core::Utf8Span::UnsafeFromUtf8BytesNoValidation(m_buffer.Slice(edit.m_pathOffset, numBytes));
  }

  void RowBuffer::WriteSparsePath(RowCursor& edit, uint32_t offset) noexcept
  {
    // Some scopes don't encode paths, therefore the cost is always zero.
    if (edit.m_scopeType->IsIndexedScope())
    {
      edit.m_pathToken = {};
      edit.m_pathOffset = {};
      return;
    }

    #if _DEBUG
    const auto& [found, _] = edit.m_layout->GetTokenizer().TryFindToken(edit.m_writePath);
    cdb_core::Contract::Assert(!found || !edit.m_writePathToken.IsNull());
    #endif

    if (!edit.m_writePathToken.IsNull())
    {
      edit.m_writePathToken.GetVarint().CopyTo(m_buffer.Slice(offset));
      edit.m_pathToken = static_cast<uint32_t>(edit.m_writePathToken.GetId());
      edit.m_pathOffset = offset;
    }
    else
    {
      const tla::string& span = edit.m_writePath;
      edit.m_pathToken = static_cast<uint32_t>(edit.m_layout->GetTokenizer().GetCount() + span.size());
      uint32_t sizeLenInBytes = Write7BitEncodedUInt(offset, static_cast<uint64_t>(edit.m_pathToken));
      edit.m_pathOffset = offset + sizeLenInBytes;
      cdb_core::Utf8Span::GetSpan(span).CopyTo(m_buffer.Slice(offset + sizeLenInBytes));
    }
  }

  std::tuple<std::string_view, uint32_t> RowBuffer::ReadString(uint32_t offset) const noexcept
  {
    auto [numBytes, sizeLenInBytes] = Read7BitEncodedUInt(offset);
    return {
      cdb_core::Utf8Span::UnsafeFromUtf8BytesNoValidation(
        m_buffer.Slice(offset + sizeLenInBytes, static_cast<uint32_t>(numBytes))),
      sizeLenInBytes
    };
  }

  uint32_t RowBuffer::WriteString(uint32_t offset, std::string_view value) noexcept
  {
    uint32_t sizeLenInBytes = Write7BitEncodedUInt(offset, static_cast<uint64_t>(value.size()));
    cdb_core::Utf8Span::GetSpan(value).CopyTo(m_buffer.Slice(offset + sizeLenInBytes));
    return sizeLenInBytes;
  }

  std::tuple<cdb_core::ReadOnlySpan<byte>, uint32_t> RowBuffer::ReadBinary(uint32_t offset) const noexcept
  {
    auto [numBytes, sizeLenInBytes] = Read7BitEncodedUInt(offset);
    return {m_buffer.Slice(offset + sizeLenInBytes, static_cast<uint32_t>(numBytes)), sizeLenInBytes};
  }

  uint32_t RowBuffer::WriteBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value) noexcept
  {
    uint32_t sizeLenInBytes = Write7BitEncodedUInt(offset, static_cast<uint64_t>(value.Length()));
    value.CopyTo(m_buffer.Slice(offset + sizeLenInBytes));
    return sizeLenInBytes;
  }

  void RowBuffer::Ensure(uint32_t size) noexcept
  {
    if (m_buffer.Length() < size)
    {
      cdb_core::Contract::Invariant(m_resizer != nullptr);
      m_buffer = m_resizer->Resize(size, m_buffer);
    }
  }

  /// <returns>[uint32_t spaceNeeded, int32_t shift]</returns>
  /// <remarks>
  /// spaceNeeded: the number of bytes needed to encode the value.
  /// shift: the number of bytes the current content was shifted by (may be negative).
  /// </remarks>
  std::tuple<uint32_t, int32_t> RowBuffer::EnsureVariable(uint32_t offset, bool isVarint, uint32_t numBytes,
                                                          bool exists) noexcept
  {
    uint32_t spaceNeeded;
    uint32_t spaceAvailable = 0;
    uint64_t existingValueBytes = 0;
    if (exists)
    {
      std::tie(existingValueBytes, spaceAvailable) = Read7BitEncodedUInt(offset);
    }

    if (isVarint)
    {
      spaceNeeded = numBytes;
    }
    else
    {
      spaceAvailable += static_cast<uint32_t>(existingValueBytes); // size already in spaceAvailable
      spaceNeeded = numBytes + Count7BitEncodedUInt(static_cast<uint64_t>(numBytes));
    }

    int32_t shift = spaceNeeded - spaceAvailable;
    if (shift > 0)
    {
      Ensure(m_length + shift);
    }
    m_buffer.Slice(offset + spaceAvailable, m_length - (offset + spaceAvailable)).CopyTo(
      m_buffer.Slice(offset + spaceNeeded));
    return {spaceNeeded, shift};
  }

  #if !_DEBUG
    void RowBuffer::ValidateSparsePrimitiveTypeCode(const RowCursor& edit, const LayoutType* code) const noexcept {}
  #else
  void RowBuffer::ValidateSparsePrimitiveTypeCode(const RowCursor& edit, const LayoutType* code) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_exists);

    if (edit.m_scopeType->HasImplicitTypeCode(edit))
    {
      if (edit.m_scopeType->IsLayoutNullable())
      {
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() == 1);
        cdb_core::Contract::Assert(edit.m_index == 1);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[0].GetType() == code);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[0].GetTypeArgs().GetCount() == 0);
      }
      else if (edit.m_scopeType->IsFixedArity())
      {
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() > edit.m_index);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[edit.m_index].GetType() == code);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[edit.m_index].GetTypeArgs().GetCount() == 0);
      }
      else
      {
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() == 1);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[0].GetType() == code);
        cdb_core::Contract::Assert(edit.m_scopeTypeArgs[0].GetTypeArgs().GetCount() == 0);
      }
    }
    else
    {
      if (code == &LayoutLiteral::Boolean)
      {
        code = ReadSparseTypeCode(edit.m_metaOffset);
        cdb_core::Contract::Assert(code == &LayoutLiteral::Boolean || code == &LayoutLiteral::BooleanFalse);
      }
      else
      {
        cdb_core::Contract::Assert(ReadSparseTypeCode(edit.m_metaOffset) == code);
      }
    }

    if (edit.m_scopeType->IsIndexedScope())
    {
      cdb_core::Contract::Assert(edit.m_pathOffset == 0);
      cdb_core::Contract::Assert(edit.m_pathToken == 0);
    }
    else
    {
      auto [token, pathLenInBytes, pathOffset] = ReadSparsePathLen(*edit.m_layout,
        edit.m_metaOffset + sizeof(LayoutCode));
      cdb_core::Contract::Assert(edit.m_pathOffset == pathOffset);
      cdb_core::Contract::Assert(edit.m_pathToken == token);
    }
  }
  #endif

  void RowBuffer::WriteSparseMetadata(RowCursor& edit, const LayoutType* cellType, const TypeArgumentList& typeArgs,
                                      uint32_t metaBytes) noexcept
  {
    int metaOffset = edit.m_metaOffset;
    if (!edit.m_scopeType->HasImplicitTypeCode(edit))
    {
      metaOffset += cellType->WriteTypeArgument(*this, metaOffset, typeArgs);
    }

    WriteSparsePath(edit, metaOffset);
    edit.m_valueOffset = edit.m_metaOffset + metaBytes;
    cdb_core::Contract::Assert(edit.m_valueOffset == edit.m_metaOffset + metaBytes);
  }

  /// <returns>(uint32_t metaBytes, uint32_t spaceNeeded, int32_t shift) </returns>
  std::tuple<uint32_t, uint32_t, int32_t> RowBuffer::EnsureSparse(
    RowCursor& edit,
    const LayoutType* cellType,
    const TypeArgumentList& typeArgs,
    uint32_t numBytes,
    UpdateOptions options) noexcept
  {
    return EnsureSparse(edit, cellType, typeArgs, numBytes, static_cast<RowOptions>(options));
  }

  /// <returns>(uint32_t metaBytes, uint32_t spaceNeeded, int32_t shift) </returns>
  std::tuple<uint32_t, uint32_t, int32_t> RowBuffer::EnsureSparse(
    RowCursor& edit,
    const LayoutType* cellType,
    const TypeArgumentList& typeArgs,
    uint32_t numBytes,
    RowOptions options) noexcept
  {
    uint32_t metaBytes;
    uint32_t metaOffset = edit.m_metaOffset;
    uint32_t spaceAvailable = 0;

    // Compute the metadata offsets
    if (edit.m_scopeType->HasImplicitTypeCode(edit))
    {
      metaBytes = 0;
    }
    else
    {
      metaBytes = cellType->CountTypeArgument(typeArgs);
    }

    if (!edit.m_scopeType->IsIndexedScope())
    {
      int pathLenInBytes = CountSparsePath(edit);
      metaBytes += pathLenInBytes;
    }

    if (edit.m_exists)
    {
      // Compute value offset for existing value to be overwritten.
      spaceAvailable = SparseComputeSize(edit);
    }

    uint32_t spaceNeeded = options == RowOptions::Delete ? 0 : metaBytes + numBytes;
    int32_t shift = spaceNeeded - spaceAvailable;
    if (shift > 0)
    {
      Ensure(m_length + shift);
    }

    m_buffer.Slice(metaOffset + spaceAvailable, m_length - (metaOffset + spaceAvailable))
            .CopyTo(m_buffer.Slice(metaOffset + spaceNeeded));

    #if _DEBUG
    if (shift < 0)
    {
      // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
      m_buffer.Slice(m_length + shift, -shift).Fill(std::byte{0xFF});
    }
    #endif

    // Update the stored size (fixed arity scopes don't store the size because it is implied by the type args).
    if (edit.m_scopeType->IsSizedScope() && !edit.m_scopeType->IsFixedArity())
    {
      if ((options == RowOptions::Insert) || (options == RowOptions::InsertAt) || ((options == RowOptions::Upsert) && !
        edit.m_exists))
      {
        // Add one to the current scope count.
        cdb_core::Contract::Assert(!edit.m_exists);
        IncrementUInt32(edit.m_start, 1);
        edit.m_count++;
      }
      else if ((options == RowOptions::Delete) && edit.m_exists)
      {
        // Subtract one from the current scope count.
        cdb_core::Contract::Assert(ReadUInt32(edit.m_start) > 0);
        DecrementUInt32(edit.m_start, 1);
        edit.m_count--;
      }
    }

    if (options == RowOptions::Delete)
    {
      edit.m_cellType = nullptr;
      edit.m_cellTypeArgs = {};
      edit.m_exists = false;
    }
    else
    {
      edit.m_cellType = cellType;
      edit.m_cellTypeArgs = typeArgs;
      edit.m_exists = true;
    }

    return {metaBytes, spaceNeeded, shift};
  }

  void RowBuffer::ReadSparseMetadata(RowCursor& edit) const noexcept
  {
    if (edit.m_scopeType->HasImplicitTypeCode(edit))
    {
      edit.m_scopeType->SetImplicitTypeCode(edit);
      edit.m_valueOffset = edit.m_metaOffset;
    }
    else
    {
      edit.m_cellType = ReadSparseTypeCode(edit.m_metaOffset);
      edit.m_valueOffset = edit.m_metaOffset + sizeof(LayoutCode);
      edit.m_cellTypeArgs = {};
      if (edit.m_cellType->IsLayoutEndScope())
      {
        // Reached end of current scope without finding another field.
        edit.m_pathToken = 0;
        edit.m_pathOffset = 0;
        edit.m_valueOffset = edit.m_metaOffset;
        return;
      }

      uint32_t sizeLenInBytes;
      std::tie(edit.m_cellTypeArgs, sizeLenInBytes) = edit.m_cellType->ReadTypeArgumentList(*this, edit.m_valueOffset);
      edit.m_valueOffset += sizeLenInBytes;
    }

    edit.m_scopeType->ReadSparsePath(*this, edit);
  }

  uint32_t RowBuffer::SparseComputeSize(const RowCursor& edit) const noexcept
  {
    if (!edit.m_cellType->IsLayoutScope())
    {
      return SparseComputePrimitiveSize(edit.m_cellType, edit.m_metaOffset, edit.m_valueOffset);
    }

    // Compute offset to end of value for current value.
    RowCursor newScope = SparseIteratorReadScope(edit, true);
    return SkipScope(newScope) - edit.m_metaOffset;
  }

  /// <summary>Compute the size of a sparse (primitive) field.</summary>
  /// <param name="cellType">The type of the current sparse field.</param>
  /// <param name="metaOffset">The 0-based offset from the beginning of the row where the field begins.</param>
  /// <param name="valueOffset">
  /// The 0-based offset from the beginning of the row where the field's value
  /// begins.
  /// </param>
  /// <returns>The length (in bytes) of the encoded field including the metadata and the value.</returns>
  uint32_t RowBuffer::SparseComputePrimitiveSize(const LayoutType* cellType, uint32_t metaOffset,
                                                 uint32_t valueOffset) const noexcept
  {
    uint32_t metaBytes = valueOffset - metaOffset;
    LayoutCode code = cellType->GetLayoutCode();
    switch (code)
    {
    case LayoutCode::Null:
      cdb_core::Contract::Assert(LayoutLiteral::Null.GetSize() == 0);
      return metaBytes;

    case LayoutCode::Boolean:
    case LayoutCode::BooleanFalse:
      cdb_core::Contract::Assert(LayoutLiteral::Boolean.GetSize() == 0);
      return metaBytes;

    case LayoutCode::Int8:
      return metaBytes + sizeof(int8_t);

    case LayoutCode::Int16:
      return metaBytes + sizeof(int16_t);

    case LayoutCode::Int32:
      return metaBytes + sizeof(int32_t);

    case LayoutCode::Int64:
      return metaBytes + sizeof(int64_t);

    case LayoutCode::UInt8:
      return metaBytes + sizeof(uint8_t);

    case LayoutCode::UInt16:
      return metaBytes + sizeof(uint16_t);

    case LayoutCode::UInt32:
      return metaBytes + sizeof(uint32_t);

    case LayoutCode::UInt64:
      return metaBytes + sizeof(uint64_t);

    case LayoutCode::Float32:
      return metaBytes + sizeof(float32_t);

    case LayoutCode::Float64:
      return metaBytes + sizeof(float64_t);

    case LayoutCode::Float128:
      return metaBytes + sizeof(float128_t);

    case LayoutCode::Decimal:
      return metaBytes + sizeof(decimal_t);

    case LayoutCode::DateTime:
      return metaBytes + sizeof(DateTime);

    case LayoutCode::UnixDateTime:
      return metaBytes + sizeof(UnixDateTime);

    case LayoutCode::Guid:
      return metaBytes + sizeof(Guid);

    case LayoutCode::MongoDbObjectId:
      return metaBytes + sizeof(MongoDbObjectId);

    case LayoutCode::Utf8:
    case LayoutCode::Binary:
    {
      auto [numBytes, sizeLenInBytes] = Read7BitEncodedUInt(metaOffset + metaBytes);
      return metaBytes + sizeLenInBytes + static_cast<uint32_t>(numBytes);
    }

    case LayoutCode::VarInt:
    case LayoutCode::VarUInt:
    {
      auto [_, sizeLenInBytes] = Read7BitEncodedUInt(metaOffset + metaBytes);
      return metaBytes + sizeLenInBytes;
    }

    default:
      cdb_core::Contract::Fail(cdb_core::make_string<tla::string>("Not Implemented: %d", static_cast<int>(code)));
    }
  }

  uint32_t RowBuffer::CountDefaultValue(const LayoutType* layoutType, const TypeArgumentList& typeArgs) const noexcept
  {
    LayoutCode code = layoutType->GetLayoutCode();
    switch (code)
    {
    case LayoutCode::Null:
    case LayoutCode::Boolean:
    case LayoutCode::BooleanFalse:
      return 1;

    case LayoutCode::Int8:
      return sizeof(int8_t);

    case LayoutCode::Int16:
      return sizeof(int16_t);

    case LayoutCode::Int32:
      return sizeof(int32_t);

    case LayoutCode::Int64:
      return sizeof(int64_t);

    case LayoutCode::UInt8:
      return sizeof(uint8_t);

    case LayoutCode::UInt16:
      return sizeof(uint16_t);

    case LayoutCode::UInt32:
      return sizeof(uint32_t);

    case LayoutCode::UInt64:
      return sizeof(uint64_t);

    case LayoutCode::Float32:
      return sizeof(float32_t);

    case LayoutCode::Float64:
      return sizeof(float64_t);

    case LayoutCode::Float128:
      return sizeof(float128_t);

    case LayoutCode::Decimal:
      return sizeof(decimal_t);

    case LayoutCode::DateTime:
      return sizeof(DateTime);

    case LayoutCode::UnixDateTime:
      return sizeof(UnixDateTime);

    case LayoutCode::Guid:
      return sizeof(Guid);

    case LayoutCode::MongoDbObjectId:
      return sizeof(MongoDbObjectId);

    case LayoutCode::Utf8:
    case LayoutCode::Binary:
    case LayoutCode::VarInt:
    case LayoutCode::VarUInt:

      // Variable length types preceded by their varuint size take 1 byte for a size of 0.
      return 1;

    case LayoutCode::ObjectScope:
    case LayoutCode::ImmutableObjectScope:
    case LayoutCode::ArrayScope:
    case LayoutCode::ImmutableArrayScope:

      // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
      return sizeof(LayoutCode);

    case LayoutCode::TypedArrayScope:
    case LayoutCode::ImmutableTypedArrayScope:
    case LayoutCode::TypedSetScope:
    case LayoutCode::ImmutableTypedSetScope:
    case LayoutCode::TypedMapScope:
    case LayoutCode::ImmutableTypedMapScope:

      // Variable length typed collection scopes preceded by their scope size take sizeof(uint32_t) for a size of 0.
      return sizeof(uint32_t);

    case LayoutCode::TupleScope:
    case LayoutCode::ImmutableTupleScope:
    {
      // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
      size_t length = sizeof(LayoutCode) + (sizeof(LayoutCode) * typeArgs.GetCount());
      cdb_core::Contract::Invariant(length <= UINT32_MAX);
      return static_cast<uint32_t>(length);
    }
    case LayoutCode::TypedTupleScope:
    case LayoutCode::ImmutableTypedTupleScope:
    case LayoutCode::TaggedScope:
    case LayoutCode::ImmutableTaggedScope:
    case LayoutCode::Tagged2Scope:
    case LayoutCode::ImmutableTagged2Scope:
    {
      // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
      int sum = 0;
      for (const TypeArgument& arg : typeArgs)
      {
        sum += CountDefaultValue(arg.GetType(), arg.GetTypeArgs());
      }

      return sum;
    }
    case LayoutCode::NullableScope:
    case LayoutCode::ImmutableNullableScope:

      // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
      return 1 + CountDefaultValue(typeArgs[0].GetType(), typeArgs[0].GetTypeArgs());

    case LayoutCode::Schema:
    case LayoutCode::ImmutableSchema:
    {
      const Layout& udt = m_resolver->Resolve(typeArgs.GetSchemaId());
      return udt.GetSize() + sizeof(LayoutCode);
    }
    default:
      cdb_core::Contract::Fail(cdb_core::make_string<tla::string>("Not Implemented: %d", static_cast<int>(code)));
    }
  }

  uint32_t RowBuffer::WriteDefaultValue(uint32_t offset, const LayoutType* layoutType,
                                        const TypeArgumentList& typeArgs) noexcept
  {
    LayoutCode code = layoutType->GetLayoutCode();
    switch (code)
    {
    case LayoutCode::Null:
      WriteSparseTypeCode(offset, LayoutCode::Null);
      return 1;

    case LayoutCode::Boolean:
    case LayoutCode::BooleanFalse:
      WriteSparseTypeCode(offset, LayoutCode::BooleanFalse);
      return 1;

    case LayoutCode::Int8:
      WriteInt8(offset, 0);
      return sizeof(int8_t);

    case LayoutCode::Int16:
      WriteInt16(offset, 0);
      return sizeof(int16_t);

    case LayoutCode::Int32:
      WriteInt32(offset, 0);
      return sizeof(int32_t);

    case LayoutCode::Int64:
      WriteInt64(offset, 0);
      return sizeof(int64_t);

    case LayoutCode::UInt8:
      WriteUInt8(offset, 0);
      return sizeof(uint8_t);

    case LayoutCode::UInt16:
      WriteUInt16(offset, 0);
      return sizeof(uint16_t);

    case LayoutCode::UInt32:
      WriteUInt32(offset, 0);
      return sizeof(uint32_t);

    case LayoutCode::UInt64:
      WriteUInt64(offset, 0);
      return sizeof(uint64_t);

    case LayoutCode::Float32:
      WriteFloat32(offset, {});
      return sizeof(float32_t);

    case LayoutCode::Float64:
      WriteFloat64(offset, {});
      return sizeof(float64_t);

    case LayoutCode::Float128:
      WriteFloat128(offset, {});
      return sizeof(float128_t);

    case LayoutCode::Decimal:
      WriteDecimal(offset, {});
      return sizeof(decimal_t);

    case LayoutCode::DateTime:
      WriteDateTime(offset, {});
      return sizeof(DateTime);

    case LayoutCode::UnixDateTime:
      WriteUnixDateTime(offset, {});
      return sizeof(UnixDateTime);

    case LayoutCode::Guid:
      WriteGuid(offset, {});
      return sizeof(Guid);

    case LayoutCode::MongoDbObjectId:
      WriteMongoDbObjectId(offset, {});
      return sizeof(MongoDbObjectId);

    case LayoutCode::Utf8:
    case LayoutCode::Binary:
    case LayoutCode::VarInt:
    case LayoutCode::VarUInt:

      // Variable length types preceded by their varuint size take 1 byte for a size of 0.
      return Write7BitEncodedUInt(offset, 0);

    case LayoutCode::ObjectScope:
    case LayoutCode::ImmutableObjectScope:
    case LayoutCode::ArrayScope:
    case LayoutCode::ImmutableArrayScope:

      // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
      WriteSparseTypeCode(offset, LayoutCode::EndScope);
      return sizeof(LayoutCode);

    case LayoutCode::TypedArrayScope:
    case LayoutCode::ImmutableTypedArrayScope:
    case LayoutCode::TypedSetScope:
    case LayoutCode::ImmutableTypedSetScope:
    case LayoutCode::TypedMapScope:
    case LayoutCode::ImmutableTypedMapScope:

      // Variable length typed collection scopes preceded by their scope size take sizeof(uint32_t) for a size of 0.
      WriteUInt32(offset, 0);
      return sizeof(uint32_t);

    case LayoutCode::TupleScope:
    case LayoutCode::ImmutableTupleScope:
    {
      // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
      for (size_t i = 0; i < typeArgs.GetCount(); i++)
      {
        WriteSparseTypeCode(offset, LayoutCode::Null);
      }

      WriteSparseTypeCode(offset, LayoutCode::EndScope);
      return sizeof(LayoutCode) + (sizeof(LayoutCode) * static_cast<uint32_t>(typeArgs.GetCount()));
    }
    case LayoutCode::TypedTupleScope:
    case LayoutCode::ImmutableTypedTupleScope:
    case LayoutCode::TaggedScope:
    case LayoutCode::ImmutableTaggedScope:
    case LayoutCode::Tagged2Scope:
    case LayoutCode::ImmutableTagged2Scope:
    {
      // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
      uint32_t sum = 0;
      for (const TypeArgument& arg : typeArgs)
      {
        sum += WriteDefaultValue(offset + sum, arg.GetType(), arg.GetTypeArgs());
      }

      return sum;
    }
    case LayoutCode::NullableScope:
    case LayoutCode::ImmutableNullableScope:

      // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
      WriteInt8(offset, 0);
      return 1 + WriteDefaultValue(offset + 1, typeArgs[0].GetType(), typeArgs[0].GetTypeArgs());

    case LayoutCode::Schema:
    case LayoutCode::ImmutableSchema:
    {
      // Clear all presence bits.
      const Layout& udt = m_resolver->Resolve(typeArgs.GetSchemaId());
      uint32_t udtSize = udt.GetSize();
      m_buffer.Slice(offset, udtSize).Fill(std::byte{0});

      // Write scope terminator.
      WriteSparseTypeCode(offset + udtSize, LayoutCode::EndScope);
      return udtSize + sizeof(LayoutCode);
    }
    default:
      cdb_core::Contract::Fail(cdb_core::make_string<tla::string>("Not Implemented: %d", static_cast<int>(code)));
    }
  }
}
