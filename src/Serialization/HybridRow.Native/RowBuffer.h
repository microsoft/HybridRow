// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "HybridRowHeader.h"
#include "HybridRowVersion.h"
#include "ISpanResizer.h"
#include "LayoutCode.h"
#include "LayoutResolver.h"
//#include "Layout.h"
#include "UpdateOptions.h"

namespace cdb_hr
{
  class LayoutType;
  class LayoutScope;
  class ScalarLayoutTypeBase;
  class LayoutUniqueScope;
  class LayoutInt8;
  class LayoutInt16;
  class LayoutInt32;
  class LayoutInt64;
  class LayoutUInt8;
  class LayoutUInt16;
  class LayoutUInt32;
  class LayoutUInt64;
  class LayoutVarInt;
  class LayoutVarUInt;
  class LayoutFloat32;
  class LayoutFloat64;
  class LayoutFloat128;
  class LayoutDecimal;
  class LayoutDateTime;
  class LayoutUnixDateTime;
  class LayoutGuid;
  class LayoutMongoDbObjectId;
  class LayoutNull;
  class LayoutBoolean;
  class LayoutUtf8;
  class LayoutBinary;
  class LayoutObject;
  class LayoutArray;
  class LayoutTypedArray;
  class LayoutTypedSet;
  class LayoutTypedMap;
  class LayoutTuple;
  class LayoutTypedTuple;
  class LayoutTagged;
  class LayoutTagged2;
  class LayoutNullable;
  class LayoutUDT;
  struct RowReader;
  struct RowWriter;
  enum class RowOptions;
  struct RowCursor;

  class RowBuffer final
  {
  public:
    ~RowBuffer() noexcept = default;
    RowBuffer(const RowBuffer& other) noexcept = default;
    RowBuffer(RowBuffer&& other) noexcept = default;
    RowBuffer& operator=(const RowBuffer& other) noexcept = default;
    RowBuffer& operator=(RowBuffer&& other) noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="RowBuffer" /> struct.</summary>
    /// <param name="capacity">Initial buffer capacity.</param>
    /// <param name="resizer">Optional memory resizer.</param>
    RowBuffer(uint32_t capacity, ISpanResizer<byte>* resizer) noexcept;

    /// <summary>Initializes a new instance of the <see cref="RowBuffer" /> struct from an existing buffer.</summary>
    /// <param name="buffer">The buffer.</param>
    /// <param name="version">The version of the Hybrid Row format to used to encoding the buffer.</param>
    /// <param name="resolver">The resolver for UDTs.</param>
    /// <param name="resizer">Optional memory resizer.</param>
    RowBuffer(cdb_core::Span<byte> buffer,
              HybridRowVersion version,
              const LayoutResolver* resolver,
              ISpanResizer<byte>* resizer) noexcept;

    /// <summary>The root header for the row.</summary>
    [[nodiscard]] HybridRowHeader GetHeader() const noexcept { return ReadHeader(0); }

    /// <summary>The length of row in bytes.</summary>
    [[nodiscard]] uint32_t GetLength() const noexcept { return m_length; }

    /// <summary>The full encoded content of the row.</summary>
    [[nodiscard]] cdb_core::ReadOnlySpan<byte> AsSpan() const noexcept { return m_buffer.Slice(0, m_length); }

    /// <summary>The resolver for UDTs.</summary>
    [[nodiscard]] const LayoutResolver* GetResolver() const noexcept { return m_resolver; }

    /// <summary>Clears all content from the row. The row is empty after this method.</summary>
    void Reset() noexcept;

    /// <summary>
    /// Reads in the contents of the RowBuffer from an existing block of memory and initializes
    /// the row buffer with the associated layout and rowVersion.
    /// </summary>
    /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
    [[nodiscard]]
    bool ReadFrom(cdb_core::ReadOnlySpan<byte> input, HybridRowVersion rowVersion,
                  const LayoutResolver* resolver) noexcept;

    /// <summary>Initializes a row to the minimal size for the given layout.</summary>
    /// <param name="version">The version of the Hybrid Row format to use for encoding this row.</param>
    /// <param name="layout">The layout that describes the column layout of the row.</param>
    /// <param name="resolver">The resolver for UDTs.</param>
    /// <remarks>
    /// The row is initialized to default row for the given layout.  All fixed columns have their
    /// default values.  All variable columns are null.  No sparse columns are present. The row is valid.
    /// </remarks>
    void InitLayout(HybridRowVersion version, const Layout& layout, const LayoutResolver* resolver) noexcept;

  private:
    friend struct RowCursor;
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
    friend class LayoutType;
    friend class LayoutScope;
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
    friend class LayoutUDT;
    friend class LayoutUniqueScope;
    friend class ScalarLayoutTypeBase;
    friend struct RowReader;
    friend struct RowWriter;
    template<typename, typename, typename, typename>
    friend struct TypedMapHybridRowSerializer;

    void WriteHeader(uint32_t offset, HybridRowHeader value) noexcept;
    [[nodiscard]]
    HybridRowHeader ReadHeader(uint32_t offset) const noexcept;

    void WriteSchemaId(uint32_t offset, SchemaId value) noexcept;
    [[nodiscard]]
    SchemaId ReadSchemaId(uint32_t offset) const noexcept;

    void SetBit(uint32_t offset, LayoutBit bit) noexcept;
    void UnsetBit(uint32_t offset, LayoutBit bit) noexcept;
    [[nodiscard]]
    bool ReadBit(uint32_t offset, LayoutBit bit) const noexcept;

    void DeleteVariable(uint32_t offset, bool isVarint) noexcept;

    void WriteInt8(uint32_t offset, int8_t value) noexcept;
    [[nodiscard]]
    int8_t ReadInt8(uint32_t offset) const noexcept;
    void WriteUInt8(uint32_t offset, uint8_t value) noexcept;
    [[nodiscard]]
    uint8_t ReadUInt8(uint32_t offset) const noexcept;
    void WriteInt16(uint32_t offset, int16_t value) noexcept;
    [[nodiscard]]
    int16_t ReadInt16(uint32_t offset) const noexcept;
    void WriteUInt16(uint32_t offset, uint16_t value) noexcept;
    [[nodiscard]]
    uint16_t ReadUInt16(uint32_t offset) const noexcept;

    void WriteInt32(uint32_t offset, int32_t value) noexcept;
    [[nodiscard]]
    int32_t ReadInt32(uint32_t offset) const noexcept;

    void IncrementUInt32(uint32_t offset, uint32_t increment) noexcept;
    void DecrementUInt32(uint32_t offset, uint32_t decrement) noexcept;

    void WriteUInt32(uint32_t offset, uint32_t value) noexcept;
    [[nodiscard]]
    uint32_t ReadUInt32(uint32_t offset) const noexcept;

    void WriteInt64(uint32_t offset, int64_t value) noexcept;
    [[nodiscard]]
    int64_t ReadInt64(uint32_t offset) const noexcept;

    void WriteUInt64(uint32_t offset, uint64_t value) noexcept;
    [[nodiscard]]
    uint64_t ReadUInt64(uint32_t offset) const noexcept;

    RowCursor WriteSparseUDT(RowCursor& edit, const LayoutScope* scopeType, const Layout& udt,
                             UpdateOptions options) noexcept;

    /// <summary>Delete the sparse field at the indicated path.</summary>
    /// <param name="edit">The field to delete.</param>
    void DeleteSparse(RowCursor& edit) noexcept;

    /// <summary>Rotates the sign bit of a two's complement value to the least significant bit.</summary>
    /// <param name="value">A signed value.</param>
    /// <returns>An unsigned value encoding the same value but with the sign bit in the LSB.</returns>
    /// <remarks>
    /// Moves the signed bit of a two's complement value to the least significant bit (LSB) by:
    /// <list type="number">
    /// <item>
    /// <description>If negative, take the two's complement.</description>
    /// </item> <item>
    /// <description>Left shift the value by 1 bit.</description>
    /// </item> <item>
    /// <description>If negative, set the LSB to 1.</description>
    /// </item>
    /// </list>
    /// </remarks>
    static uint64_t RotateSignToLsb(int64_t value) noexcept;

    /// <summary>Undoes the rotation introduced by <see cref="RotateSignToLsb" />.</summary>
    /// <param name="uvalue">An unsigned value with the sign bit in the LSB.</param>
    /// <returns>A signed two's complement value encoding the same value.</returns>
    static int64_t RotateSignToMsb(uint64_t uvalue) noexcept;

    [[nodiscard]]
    std::tuple<uint32_t, uint32_t, uint32_t> ReadSparsePathLen(const Layout& layout, uint32_t offset) const noexcept;
    [[nodiscard]] std::string_view ReadSparsePath(const RowCursor& edit) const noexcept;
    void WriteSparsePath(RowCursor& edit, uint32_t offset) noexcept;

    /// <returns>[std::string_view value, uint32_t sizeLenInBytes]</returns>
    /// <remarks>
    /// value: the value read.
    /// sizeLenInBytes: is size of the length encoding that precedes the value (in bytes).  This size does not include the value itself.
    /// </remarks>
    [[nodiscard]] std::tuple<std::string_view, uint32_t> ReadString(uint32_t offset) const noexcept;
    /// <returns>The size of the length encoding that precedes the value (in bytes).  This size does not include the value itself.</returns>
    [[nodiscard]] uint32_t WriteString(uint32_t offset, std::string_view value) noexcept;
    /// <returns>[ReadOnlySpan{byte} value, uint32_t sizeLenInBytes]</returns>
    /// <remarks>
    /// value: the value read.
    /// sizeLenInBytes: is size of the length encoding that precedes the value (in bytes).  This size does not include the value itself.
    /// </remarks>
    [[nodiscard]] std::tuple<cdb_core::ReadOnlySpan<byte>, uint32_t> ReadBinary(uint32_t offset) const noexcept;
    /// <returns>The size of the length encoding that precedes the value (in bytes).  This size does not include the value itself.</returns>
    [[nodiscard]] uint32_t WriteBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value) noexcept;

    /// <summary>Ensure there is at least size bytes in the internal buffer.</summary>
    void Ensure(uint32_t size) noexcept;

    /// <summary>
    /// Ensure there is at least sufficient space in the internal buffer to store a variable value.
    /// </summary>
    /// <returns>[uint32_t spaceNeeded, int32_t shift]</returns>
    /// <remarks>
    /// spaceNeeded: the number of bytes needed to encode the value.
    /// shift: the number of bytes the current content was shifted by (may be negative).
    /// </remarks>
    [[nodiscard]] std::tuple<uint32_t, int32_t> EnsureVariable(uint32_t offset, bool isVarint, uint32_t numBytes,
                                                               bool exists) noexcept;

    void ValidateSparsePrimitiveTypeCode(const RowCursor& edit, const LayoutType* code) const noexcept;

    /// <summary>Compute the byte offset from the beginning of the row for a given variable column's value.</summary>
    /// <param name="layout">The (optional) layout of the current scope.</param>
    /// <param name="scopeOffset">The 0-based offset to the beginning of the scope's value.</param>
    /// <param name="varIndex">The 0-based index of the variable column within the variable segment.</param>
    /// <returns>
    /// The byte offset from the beginning of the row where the variable column's value should be
    /// located.
    /// </returns>
    uint32_t ComputeVariableValueOffset(const Layout& layout, uint32_t scopeOffset, uint32_t varIndex) const noexcept;

    /// <summary>Move a sparse iterator to the next field within the same sparse scope.</summary>
    /// <param name="edit">The iterator to advance.</param>
    /// <remarks>
    /// <paramref name="edit.Path">
    /// On success, the path of the field at the given offset, otherwise
    /// undefined.
    /// </paramref>
    /// <paramref name="edit.MetaOffset">
    /// If found, the offset to the metadata of the field, otherwise a
    /// location to insert the field.
    /// </paramref>
    /// <paramref name="edit.m_cellType">
    /// If found, the layout code of the matching field found, otherwise
    /// undefined.
    /// </paramref>
    /// <paramref name="edit.ValueOffset">
    /// If found, the offset to the value of the field, otherwise
    /// undefined.
    /// </paramref>.
    /// </remarks>
    /// <returns>True if there is another field, false if there are no more.</returns>
    bool SparseIteratorMoveNext(RowCursor& edit) const noexcept;

    void WriteSparseMetadata(RowCursor& edit, const LayoutType* cellType, const TypeArgumentList& typeArgs,
                             uint32_t metaBytes) noexcept;

    /// <summary>Ensure that sufficient space exists in the row buffer to write the current value.</summary>
    /// <returns>(uint32_t metaBytes, uint32_t spaceNeeded, int32_t shift) </returns>
    std::tuple<uint32_t, uint32_t, int32_t> EnsureSparse(
      RowCursor& edit,
      const LayoutType* cellType,
      const TypeArgumentList& typeArgs,
      uint32_t numBytes,
      UpdateOptions options) noexcept;

    /// <summary>Ensure that sufficient space exists in the row buffer to write the current value.</summary>
    /// <param name="edit">
    /// The prepared edit indicating where and in what context the current write will
    /// happen.
    /// </param>
    /// <param name="cellType">The type of the field to be written.</param>
    /// <param name="typeArgs">The type arguments of the field to be written.</param>
    /// <param name="numBytes">The number of bytes needed to encode the value of the field to be written.</param>
    /// <param name="options">The kind of edit to be performed.</param>
    /// <remarks>
    /// <p>metaBytes: 
    /// On success, the number of bytes needed to encode the metadata of the new
    /// field.
    /// </p>
    /// <p>spaceNeeded:
    /// On success, the number of bytes needed in total to encode the new field
    /// and its metadata.
    /// </p>
    /// <p>shift:
    /// On success, the number of bytes the length of the row buffer was increased
    /// (which may be negative if the row buffer was shrunk).
    /// </p>
    /// </remarks>
    /// <returns>(uint32_t metaBytes, uint32_t spaceNeeded, int32_t shift) </returns>
    std::tuple<uint32_t, uint32_t, int32_t> EnsureSparse(
      RowCursor& edit,
      const LayoutType* cellType,
      const TypeArgumentList& typeArgs,
      uint32_t numBytes,
      RowOptions options) noexcept;

    /// <summary>Read the metadata of an encoded sparse field.</summary>
    /// <param name="edit">The edit structure to fill in.</param>
    /// <remarks>
    /// <paramref name="edit.Path">
    /// On success, the path of the field at the given offset, otherwise
    /// undefined.
    /// </paramref>
    /// <paramref name="edit.MetaOffset">
    /// On success, the offset to the metadata of the field, otherwise a
    /// location to insert the field.
    /// </paramref>
    /// <paramref name="edit.m_cellType">
    /// On success, the layout code of the existing field, otherwise
    /// undefined.
    /// </paramref>
    /// <paramref name="edit.TypeArgs">
    /// On success, the type args of the existing field, otherwise
    /// undefined.
    /// </paramref>
    /// <paramref name="edit.ValueOffset">
    /// On success, the offset to the value of the field, otherwise
    /// undefined.
    /// </paramref>.
    /// </remarks>
    void ReadSparseMetadata(RowCursor& edit) const noexcept;

    /// <summary>Produce a new scope from the current iterator position.</summary>
      /// <param name="edit">An initialized iterator pointing at a scope.</param>
      /// <param name="immutable">True if the new scope should be marked immutable (read-only).</param>
      /// <returns>A new scope beginning at the current iterator position.</returns>
    [[nodiscard]] RowCursor SparseIteratorReadScope(const RowCursor& edit, bool immutable) const noexcept;

    /// <summary>
    /// Compute the byte offsets from the beginning of the row for a given sparse field insertion
    /// into a set/map.
    /// </summary>
    /// <param name="scope">The sparse scope to insert into.</param>
    /// <param name="srcEdit">The field to move into the set/map.</param>
    /// <returns>The prepared edit context.</returns>
    RowCursor PrepareSparseMove(const RowCursor& scope, RowCursor& srcEdit) const noexcept;

    void TypedCollectionMoveField(RowCursor& dstEdit, RowCursor& srcEdit, RowOptions options) noexcept;

    /// <summary>Rebuild the unique index for a set/map scope.</summary>
    /// <param name="scope">The sparse scope to rebuild an index for.</param>
    /// <returns>Success if the index could be built, an error otherwise.</returns>
    /// <remarks>
    /// The <paramref name="scope" /> MUST be a set or map scope.
    /// <para>
    /// The scope may have been built (e.g. via RowWriter) with relaxed uniqueness constraint checking.
    /// This operation rebuilds an index to support verification of uniqueness constraints during
    /// subsequent partial updates.  If the appropriate uniqueness constraints cannot be established (i.e.
    /// a duplicate exists), this operation fails.  Before continuing, the resulting scope should either:
    /// <list type="number">
    /// <item>
    /// <description>
    /// Be repaired (e.g. by deleting duplicates) and the index rebuild operation should be
    /// run again.
    /// </description>
    /// </item> <item>
    /// <description>Be deleted.  The entire scope should be removed including its items.</description>
    /// </item>
    /// </list> Failure to perform one of these actions will leave the row is potentially in a corrupted
    /// state where partial updates may subsequent fail.
    /// </para>
    /// <para>
    /// The target <paramref name="scope" /> may or may not have already been indexed.  This
    /// operation is idempotent.
    /// </para>
    /// </remarks>
    Result TypedCollectionUniqueIndexRebuild(RowCursor& scope) noexcept;

    static uint32_t CountSparsePath(RowCursor& edit) noexcept;

    /// <summary>
    /// Compute the number of bytes necessary to store the unsigned integer using the varuint
    /// encoding.
    /// </summary>
    /// <param name="value">The value to be encoded.</param>
    /// <returns>The number of bytes needed to store the varuint encoding of <see cref="value" />.</returns>
    static uint32_t Count7BitEncodedUInt(uint64_t value) noexcept;

    /// <summary>
    /// Compute the number of bytes necessary to store the signed integer using the varint
    /// encoding.
    /// </summary>
    /// <param name="value">The value to be encoded.</param>
    /// <returns>The number of bytes needed to store the varint encoding of <see cref="value" />.</returns>
    static uint32_t Count7BitEncodedInt(int64_t value) noexcept;

    /// <summary>
    /// Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
    /// with the associated layout and rowVersion.
    /// </summary>
    /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
    [[nodiscard]] bool InitReadFrom(HybridRowVersion rowVersion) const noexcept;

    /// <summary>Skip over a nested scope.</summary>
    /// <param name="edit">The sparse scope to search.</param>
    /// <returns>The 0-based byte offset immediately following the scope end marker.</returns>
    uint32_t SkipScope(RowCursor& edit) const noexcept;

    /// <summary>Compares the values of two encoded fields using the hybrid row binary collation.</summary>
    /// <param name="left">An edit describing the left field.</param>
    /// <param name="leftLen">The size of the left field's value in bytes.</param>
    /// <param name="right">An edit describing the right field.</param>
    /// <param name="rightLen">The size of the right field's value in bytes.</param>
    /// <returns>
    /// <list type="table">
    /// <item>
    /// <term>-1</term><description>left less than right.</description>
    /// </item> <item>
    /// <term>0</term><description>left and right are equal.</description>
    /// </item> <item>
    /// <term>1</term><description>left is greater than right.</description>
    /// </item>
    /// </list>
    /// </returns>
    [[nodiscard]] int CompareFieldValue(const RowCursor& left, int leftLen, const RowCursor& right,
                                        int rightLen) const noexcept;

    /// <summary>
    /// Compares the values of two encoded key-value pair fields using the hybrid row binary
    /// collation.
    /// </summary>
    /// <param name="left">An edit describing the left field.</param>
    /// <param name="right">An edit describing the right field.</param>
    /// <returns>
    /// <list type="table">
    /// <item>
    /// <term>-1</term><description>left less than right.</description>
    /// </item> <item>
    /// <term>0</term><description>left and right are equal.</description>
    /// </item> <item>
    /// <term>1</term><description>left is greater than right.</description>
    /// </item>
    /// </list>
    /// </returns>
    [[nodiscard]] int CompareKeyValueFieldValue(const RowCursor& left, const RowCursor& right) const noexcept;

    // Forward declaration.
    struct UniqueIndexItem;

    /// <summary>
    /// Sorts the <paramref name="uniqueIndex" /> array structure using the hybrid row binary
    /// collation.
    /// </summary>
    /// <param name="scope">The scope to be sorted.</param>
    /// <param name="dstEdit">A edit that points at the scope.</param>
    /// <param name="uniqueIndex">
    /// A unique index array structure that identifies the row offsets of each
    /// element in the scope.
    /// </param>
    /// <returns>true if the array was sorted, false if a duplicate was found during sorting.</returns>
    /// <remarks>
    /// Implementation Note:
    /// <para>This method MUST guarantee that if at least one duplicate exists it will be found.</para>
    /// Insertion Sort is used for this purpose as it guarantees that each value is eventually compared
    /// against its previous item in sorted order.  If any two successive items are the same they must be
    /// duplicates.
    /// <para>
    /// Other search algorithms, such as Quick Sort or Merge Sort, may offer fewer comparisons in the
    /// limit but don't necessarily guarantee that duplicates will be discovered.  If an alternative
    /// algorithm is used, then an independent duplicate pass MUST be employed.
    /// </para>
    /// <para>
    /// Under the current operational assumptions, the expected cardinality of sets and maps is
    /// expected to be relatively small.  If this assumption changes, Insertion Sort may no longer be the
    /// best choice.
    /// </para>
    /// </remarks>
    [[nodiscard]] bool InsertionSort(const RowCursor& scope, const RowCursor& dstEdit,
                                     cdb_core::Span<UniqueIndexItem> uniqueIndex) const noexcept;

    /// <returns>The number of bytes written.</returns>
    uint32_t Write7BitEncodedUInt(uint32_t offset, uint64_t value) noexcept;
    /// <summary>Returns (uint64_t value, uint32_t lenInBytes)</summary>
    [[nodiscard]]
    std::tuple<uint64_t, uint32_t> Read7BitEncodedUInt(uint32_t offset) const noexcept;

    uint32_t Write7BitEncodedInt(uint32_t offset, int64_t value) noexcept;

    /// <summary>Returns (uint64_t value, uint32_t lenInBytes)</summary>
    [[nodiscard]] std::tuple<int64_t, uint32_t> Read7BitEncodedInt(uint32_t offset) const noexcept;

    void WriteFloat32(uint32_t offset, float32_t value) noexcept;
    [[nodiscard]] float32_t ReadFloat32(uint32_t offset) const noexcept;
    void WriteFloat64(uint32_t offset, float64_t value) noexcept;
    [[nodiscard]] float64_t ReadFloat64(uint32_t offset) const noexcept;
    void WriteFloat128(uint32_t offset, Float128 value) noexcept;
    [[nodiscard]] Float128 ReadFloat128(uint32_t offset) const noexcept;
    void WriteDecimal(uint32_t offset, Decimal value) noexcept;
    [[nodiscard]] Decimal ReadDecimal(uint32_t offset) const noexcept;
    void WriteDateTime(uint32_t offset, DateTime value) noexcept;
    [[nodiscard]] DateTime ReadDateTime(uint32_t offset) const noexcept;
    void WriteUnixDateTime(uint32_t offset, UnixDateTime value) noexcept;
    [[nodiscard]] UnixDateTime ReadUnixDateTime(uint32_t offset) const noexcept;
    void WriteGuid(uint32_t offset, Guid value) noexcept;
    [[nodiscard]] Guid ReadGuid(uint32_t offset) const noexcept;
    void WriteMongoDbObjectId(uint32_t offset, MongoDbObjectId value) noexcept;
    [[nodiscard]] MongoDbObjectId ReadMongoDbObjectId(uint32_t offset) const noexcept;

    [[nodiscard]] std::string_view ReadFixedString(uint32_t offset, uint32_t len) const noexcept;
    void WriteFixedString(uint32_t offset, std::string_view value) noexcept;
    [[nodiscard]] cdb_core::ReadOnlySpan<byte> ReadFixedBinary(uint32_t offset, uint32_t len) const noexcept;
    void WriteFixedBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value, uint32_t len) noexcept;

    [[nodiscard]] std::string_view ReadVariableString(uint32_t offset) const noexcept;
    /// <returns>number of bytes shifted (may be negative).</returns>
    int32_t WriteVariableString(uint32_t offset, std::string_view value, bool exists) noexcept;
    [[nodiscard]] cdb_core::ReadOnlySpan<byte> ReadVariableBinary(uint32_t offset) const noexcept;
    /// <returns>number of bytes shifted (may be negative).</returns>
    int32_t WriteVariableBinary(uint32_t offset, cdb_core::ReadOnlySpan<byte> value, bool exists) noexcept;
    [[nodiscard]] int64_t ReadVariableInt(uint32_t offset) const noexcept;
    /// <returns>number of bytes shifted (may be negative).</returns>
    int32_t WriteVariableInt(uint32_t offset, int64_t value, bool exists) noexcept;
    [[nodiscard]] uint64_t ReadVariableUInt(uint32_t offset) const noexcept;
    /// <returns>number of bytes shifted (may be negative).</returns>
    int32_t WriteVariableUInt(uint32_t offset, uint64_t value, bool exists) noexcept;

    [[nodiscard]] const LayoutType* ReadSparseTypeCode(uint32_t offset) const noexcept;
    void WriteSparseTypeCode(uint32_t offset, LayoutCode code) noexcept;

    template<typename T, T(RowBuffer::*ReadFunc)(uint32_t) const noexcept, typename TLayoutType>
    T ReadSparseFixed(const TLayoutType* layoutType, RowCursor& edit) const noexcept;
    template<typename T, void(RowBuffer::*WriteFunc)(uint32_t, T) noexcept, typename TLayoutType>
    void WriteSparseFixed(const TLayoutType* layoutType, RowCursor& edit, T value, UpdateOptions options) noexcept;

    [[nodiscard]] int8_t ReadSparseInt8(RowCursor& edit) const noexcept;
    void WriteSparseInt8(RowCursor& edit, int8_t value, UpdateOptions options) noexcept;
    [[nodiscard]] int16_t ReadSparseInt16(RowCursor& edit) const noexcept;
    void WriteSparseInt16(RowCursor& edit, int16_t value, UpdateOptions options) noexcept;
    [[nodiscard]] int32_t ReadSparseInt32(RowCursor& edit) const noexcept;
    void WriteSparseInt32(RowCursor& edit, int32_t value, UpdateOptions options) noexcept;
    [[nodiscard]] int64_t ReadSparseInt64(RowCursor& edit) const noexcept;
    void WriteSparseInt64(RowCursor& edit, int64_t value, UpdateOptions options) noexcept;
    [[nodiscard]] uint8_t ReadSparseUInt8(RowCursor& edit) const noexcept;
    void WriteSparseUInt8(RowCursor& edit, uint8_t value, UpdateOptions options) noexcept;
    [[nodiscard]] uint16_t ReadSparseUInt16(RowCursor& edit) const noexcept;
    void WriteSparseUInt16(RowCursor& edit, uint16_t value, UpdateOptions options) noexcept;
    [[nodiscard]] uint32_t ReadSparseUInt32(RowCursor& edit) const noexcept;
    void WriteSparseUInt32(RowCursor& edit, uint32_t value, UpdateOptions options) noexcept;
    [[nodiscard]] uint64_t ReadSparseUInt64(RowCursor& edit) const noexcept;
    void WriteSparseUInt64(RowCursor& edit, uint64_t value, UpdateOptions options) noexcept;

    [[nodiscard]] int64_t ReadSparseVarInt(RowCursor& edit) const noexcept;
    void WriteSparseVarInt(RowCursor& edit, int64_t value, UpdateOptions options) noexcept;
    [[nodiscard]] uint64_t ReadSparseVarUInt(RowCursor& edit) const noexcept;
    void WriteSparseVarUInt(RowCursor& edit, uint64_t value, UpdateOptions options) noexcept;

    [[nodiscard]] float32_t ReadSparseFloat32(RowCursor& edit) const noexcept;
    void WriteSparseFloat32(RowCursor& edit, float32_t value, UpdateOptions options) noexcept;
    [[nodiscard]] float64_t ReadSparseFloat64(RowCursor& edit) const noexcept;
    void WriteSparseFloat64(RowCursor& edit, float64_t value, UpdateOptions options) noexcept;
    [[nodiscard]] float128_t ReadSparseFloat128(RowCursor& edit) const noexcept;
    void WriteSparseFloat128(RowCursor& edit, float128_t value, UpdateOptions options) noexcept;
    [[nodiscard]] decimal_t ReadSparseDecimal(RowCursor& edit) const noexcept;
    void WriteSparseDecimal(RowCursor& edit, decimal_t value, UpdateOptions options) noexcept;

    [[nodiscard]] DateTime ReadSparseDateTime(RowCursor& edit) const noexcept;
    void WriteSparseDateTime(RowCursor& edit, DateTime value, UpdateOptions options) noexcept;
    [[nodiscard]] UnixDateTime ReadSparseUnixDateTime(RowCursor& edit) const noexcept;
    void WriteSparseUnixDateTime(RowCursor& edit, UnixDateTime value, UpdateOptions options) noexcept;
    [[nodiscard]] Guid ReadSparseGuid(RowCursor& edit) const noexcept;
    void WriteSparseGuid(RowCursor& edit, Guid value, UpdateOptions options) noexcept;
    [[nodiscard]] MongoDbObjectId ReadSparseMongoDbObjectId(RowCursor& edit) const noexcept;
    void WriteSparseMongoDbObjectId(RowCursor& edit, MongoDbObjectId value, UpdateOptions options) noexcept;

    [[nodiscard]] NullValue ReadSparseNull(RowCursor& edit) const noexcept;
    void WriteSparseNull(RowCursor& edit, NullValue value, UpdateOptions options) noexcept;
    [[nodiscard]] bool ReadSparseBool(RowCursor& edit) const noexcept;
    void WriteSparseBool(RowCursor& edit, bool value, UpdateOptions options) noexcept;

    [[nodiscard]] std::string_view ReadSparseString(RowCursor& edit) const noexcept;
    void WriteSparseString(RowCursor& edit, std::string_view value, UpdateOptions options) noexcept;
    [[nodiscard]] cdb_core::ReadOnlySpan<byte> ReadSparseBinary(RowCursor& edit) const noexcept;
    void WriteSparseBinary(RowCursor& edit, cdb_core::ReadOnlySpan<byte> value, UpdateOptions options) noexcept;

    [[nodiscard]] RowCursor WriteSparseObject(RowCursor& edit, const LayoutScope* scopeType,
                                              UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteSparseArray(RowCursor& edit, const LayoutScope* scopeType,
                                             UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteTypedArray(RowCursor& edit, const LayoutScope* scopeType,
                                            const TypeArgumentList& typeArgs,
                                            UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteTypedSet(RowCursor& edit, const LayoutScope* scopeType,
                                          const TypeArgumentList& typeArgs,
                                          UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteTypedMap(RowCursor& edit, const LayoutScope* scopeType,
                                          const TypeArgumentList& typeArgs,
                                          UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteSparseTuple(RowCursor& edit, const LayoutScope* scopeType,
                                             const TypeArgumentList& typeArgs,
                                             UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteTypedTuple(RowCursor& edit, const LayoutScope* scopeType,
                                            const TypeArgumentList& typeArgs,
                                            UpdateOptions options) noexcept;
    [[nodiscard]] RowCursor WriteNullable(RowCursor& edit, const LayoutScope* scopeType,
                                          const TypeArgumentList& typeArgs,
                                          UpdateOptions options, bool hasValue) noexcept;

    /// <summary>Compute the size of a sparse field.</summary>
    /// <param name="edit">The edit structure describing the field to measure.</param>
    /// <returns>The length (in bytes) of the encoded field including the metadata and the value.</returns>
    [[nodiscard]] uint32_t SparseComputeSize(const RowCursor& edit) const noexcept;

    /// <summary>Compute the size of a sparse (primitive) field.</summary>
    /// <param name="cellType">The type of the current sparse field.</param>
    /// <param name="metaOffset">The 0-based offset from the beginning of the row where the field begins.</param>
    /// <param name="valueOffset">
    /// The 0-based offset from the beginning of the row where the field's value
    /// begins.
    /// </param>
    /// <returns>The length (in bytes) of the encoded field including the metadata and the value.</returns>
    [[nodiscard]] uint32_t SparseComputePrimitiveSize(const LayoutType* cellType, uint32_t metaOffset,
                                                      uint32_t valueOffset) const noexcept;

    /// <summary>Return the size (in bytes) of the default sparse value for the type.</summary>
    /// <param name="layoutType">The type of the default value.</param>
    /// <param name="typeArgs"></param>
    [[nodiscard]] uint32_t CountDefaultValue(const LayoutType* layoutType,
                                             const TypeArgumentList& typeArgs) const noexcept;

    /// <summary>Write the default value of the given type at the offset provided.</summary>
    /// <param name="offset">The offset in the buffer to write at.</param>
    /// <param name="layoutType">The type of the default value.</param>
    /// <param name="typeArgs"></param>
    [[nodiscard]] uint32_t WriteDefaultValue(uint32_t offset, const LayoutType* layoutType,
                                             const TypeArgumentList& typeArgs) noexcept;

    /// <summary>Resizer for growing the memory buffer.</summary>
    ISpanResizer<byte>* m_resizer;

    /// <summary>A sequence of bytes managed by this <see cref="RowBuffer" />.</summary>
    /// <remarks>
    /// A Hybrid Row begins in the 0-th byte of the <see cref="RowBuffer" />.  Remaining byte
    /// sequence is defined by the Hybrid Row grammar.
    /// </remarks>
    cdb_core::Span<byte> m_buffer;

    /// <summary>The resolver for UDTs.</summary>
    const LayoutResolver* m_resolver;

    /// <summary>The length of row in bytes.</summary>
    uint32_t m_length;
  };
}
