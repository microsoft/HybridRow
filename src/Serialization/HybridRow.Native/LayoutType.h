// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "LayoutCode.h"
#include "LayoutBit.h"
#include "Result.h"
#include "RowBuffer.h"
#include "TypeArgument.h"
#include "UpdateOptions.h"

// ReSharper disable CppHiddenFunction
// ReSharper disable CppPolymorphicClassWithNonVirtualPublicDestructor
namespace cdb_hr
{
  using namespace std::literals;
  struct RowCursor;
  struct RowWriter;
  class LayoutEndScope;
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
  struct LayoutLiteral;

  /// <summary>The abstract base class for typed hybrid row field descriptors.</summary>
  /// <remarks><see cref="LayoutType" /> is immutable.</remarks>
  class LayoutType
  {
  public:
    /// <summary>The number of bits in a single byte on the current architecture.</summary>
    constexpr static uint32_t BitsPerByte = LayoutBit::BitsPerByte;

  protected:
    ~LayoutType() noexcept = default;
  public:
    LayoutType(const LayoutType& other) = delete;
    LayoutType(LayoutType&& other) = delete;
    LayoutType& operator=(const LayoutType& other) = delete;
    LayoutType& operator=(LayoutType&& other) = delete;

    /// <summary>The physical layout type of the field cast to the specified type.</summary>
    template<typename T, typename = std::enable_if_t<std::is_base_of_v<LayoutType, T>>>
    const T& TypeAs() const;

    [[nodiscard]] size_t GetHashCode() const noexcept;

    /// <summary>Human readable name of the type.</summary>
    [[nodiscard]] virtual std::string_view GetName() const noexcept = 0;

    /// <summary>True if this type is always fixed length.</summary>
    [[nodiscard]] virtual bool IsFixed() const noexcept = 0;

    /// <summary>True if this type is a literal null.</summary>
    [[nodiscard]] virtual bool IsNull() const noexcept { return false; }

    /// <summary>True if this type can be used in the variable-length segment.</summary>
    [[nodiscard]] bool AllowVariable() const noexcept { return !IsFixed(); }

    /// <summary>If true, this edit's nested fields cannot be updated individually.</summary>
    /// <remarks>The entire edit can still be replaced.</remarks>
    [[nodiscard]] bool IsImmutable() const noexcept { return m_immutable; }

    /// <summary>If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.</summary>
    [[nodiscard]] uint32_t GetSize() const noexcept { return m_size; }

    /// <summary>True if this type is a boolean.</summary>
    [[nodiscard]] virtual bool IsBool() const noexcept { return false; }

    /// <summary>True if this type is a variable-length encoded integer type (either signed or unsigned).</summary>
    [[nodiscard]] virtual bool IsVarint() const noexcept { return false; }

    /// <summary>True if this type is a scope.</summary>
    [[nodiscard]] virtual bool IsLayoutScope() const noexcept { return false; }

    /// <summary>True if this type is a UDT.</summary>
    [[nodiscard]] virtual bool IsUDT() const noexcept { return false; }

    /// <summary>Returns true if this is a nullable scope.</summary>
    [[nodiscard]] virtual bool IsLayoutNullable() const noexcept { return false; }

    /// <summary>Returns true if this is a typed tuple scope.</summary>
    [[nodiscard]] virtual bool IsLayoutTypedTuple() const noexcept { return false; }

    /// <summary>Returns true if this is a unique indexed scope.</summary>
    [[nodiscard]] virtual bool IsLayoutUniqueScope() const noexcept { return false; }

    /// <summary>Returns true if this is a end scope.</summary>
    [[nodiscard]] bool IsLayoutEndScope() const noexcept { return GetLayoutCode() == LayoutCode::EndScope; }

    /// <summary>The physical layout code used to represent the type within the serialization.</summary>
    [[nodiscard]] constexpr LayoutCode GetLayoutCode() const noexcept { return m_code; }

  protected:
    constexpr LayoutType(LayoutCode code, bool immutable, uint32_t size) noexcept :
      m_code(code),
      m_immutable(immutable),
      m_size(size) { }

    /// <summary>Helper for preparing the delete of a sparse field.</summary>
    /// <param name="b">The row to delete from.</param>
    /// <param name="edit">The parent edit containing the field to delete.</param>
    /// <param name="code">The expected type of the field.</param>
    /// <returns>Success if the delete is permitted, the error code otherwise.</returns>
    static Result PrepareSparseDelete(const RowBuffer& b, const RowCursor& edit, LayoutCode code) noexcept;

    /// <summary>Helper for preparing the write of a sparse field.</summary>
    /// <param name="b">The row to write to.</param>
    /// <param name="edit">The cursor for the field to write.</param>
    /// <param name="typeArg">The (optional) type constraints.</param>
    /// <param name="options">The write options.</param>
    /// <returns>Success if the write is permitted, the error code otherwise.</returns>
    static Result PrepareSparseWrite(RowBuffer& b, RowCursor& edit, const TypeArgument& typeArg,
                                     UpdateOptions options) noexcept;

    /// <summary>Helper for preparing the read of a sparse field.</summary>
    /// <param name="b">The row to read from.</param>
    /// <param name="edit">The parent edit containing the field to read.</param>
    /// <param name="code">The expected type of the field.</param>
    /// <returns>Success if the read is permitted, the error code otherwise.</returns>
    static Result PrepareSparseRead(const RowBuffer& b, const RowCursor& edit, LayoutCode code) noexcept;

    /// <summary>Helper for preparing the move of a sparse field into an existing restricted edit.</summary>
    /// <param name="b">The row to read from.</param>
    /// <param name="destinationScope">The parent set edit into which the field should be moved.</param>
    /// <param name="destinationCode">The expected type of the edit moving within.</param>
    /// <param name="elementType">The expected type of the elements within the edit.</param>
    /// <param name="srcEdit">The field to be moved.</param>
    /// <param name="options">The move options.</param>
    /// <returns>[Result result, RowCursor dstEdit]
    /// Result: Success if the move is permitted, the error code otherwise.
    /// RowCursor: If successful, a prepared insertion cursor for the destination.
    /// </returns>
    /// <remarks>The source field is delete if the move prepare fails with a destination error.</remarks>
    static std::tuple<Result, RowCursor> PrepareSparseMove(RowBuffer& b, const RowCursor& destinationScope,
                                                           const LayoutScope* destinationCode,
                                                           const TypeArgument& elementType, RowCursor& srcEdit,
                                                           UpdateOptions options) noexcept;

    [[nodiscard]] virtual uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept;
    [[nodiscard]] virtual uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                                     const TypeArgumentList& value) const noexcept;
    /// <returns>[TypeArgument typeArg, uint32_t lenInBytes]</returns>
    [[nodiscard]] static std::tuple<TypeArgument, uint32_t> ReadTypeArgument(
      const RowBuffer& row, uint32_t offset) noexcept;
    /// <returns>[TypeArgumentList typeArgs, uint32_t lenInBytes]</returns>
    [[nodiscard]] virtual std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept;

  private:
    friend class RowBuffer;
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
    friend class LayoutUDT;

    const LayoutCode m_code;
    const bool m_immutable;
    const uint32_t m_size;
  };

  /// <summary>Base class for scalar layout types.</summary>
  class ScalarLayoutTypeBase : public LayoutType
  {
  public:
    ~ScalarLayoutTypeBase() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    ScalarLayoutTypeBase(const ScalarLayoutTypeBase& other) = delete;
    ScalarLayoutTypeBase(ScalarLayoutTypeBase&& other) = delete;
    ScalarLayoutTypeBase& operator=(const ScalarLayoutTypeBase& other) = delete;
    ScalarLayoutTypeBase& operator=(ScalarLayoutTypeBase&& other) = delete;

    static Result HasValue(const RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) noexcept;
    Result DeleteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) const noexcept;

    /// <summary>Delete an existing value.</summary>
    /// <remarks>
    /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
    /// a decrease in required space.  If no value exists this operation is a no-op.
    /// </remarks>
    Result DeleteVariable(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) const noexcept;

    /// <summary>Delete an existing value.</summary>
    /// <remarks>
    /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
    /// a decrease in required space.  If no value exists this operation is a no-op.
    /// </remarks>
    Result DeleteSparse(RowBuffer& b, RowCursor& edit) const noexcept;

  protected:
    constexpr ScalarLayoutTypeBase(LayoutCode code, int size) noexcept : LayoutType(code, false, size) {}

    [[nodiscard]] TypeArgument GetTypeArg() const noexcept { return {this}; }

  private:
    friend struct RowWriter;
  };

  /// <summary>
  /// Describes the physical byte layout of a hybrid row field of a specific physical scalar type
  /// <typeparamref name="T" />.
  /// </summary>
  /// <remarks>
  /// <see cref="ScalarLayoutType{T}" /> is an immutable, stateless, helper class.  It provides
  /// methods for manipulating hybrid row fields of a particular scalar type, and properties that describe the
  /// layout of fields of that type.
  /// <para />
  /// <see cref="ScalarLayoutType{T}" /> is immutable.
  /// </remarks>
  template<typename T>
  class ScalarLayoutType : public ScalarLayoutTypeBase
  {
  public:
    // ReSharper disable once CppHidingFunction
    ~ScalarLayoutType() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    ScalarLayoutType(const ScalarLayoutType& other) = delete;
    ScalarLayoutType(ScalarLayoutType&& other) = delete;
    ScalarLayoutType& operator=(const ScalarLayoutType& other) = delete;
    ScalarLayoutType& operator=(ScalarLayoutType&& other) = delete;

    virtual Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                              const T& value) const noexcept = 0;
    [[nodiscard]] virtual std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                          const LayoutColumn& col) const noexcept = 0;

    virtual Result WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                 const T& value) const noexcept
    {
      return Result::Failure;
    }

    [[nodiscard]] virtual std::tuple<Result, T> ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                             const LayoutColumn& col) const noexcept
    {
      return {Result::Failure, T()};
    }

    virtual Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                               UpdateOptions options = UpdateOptions::Upsert) const noexcept = 0;
    virtual std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept = 0;

  protected:
    constexpr ScalarLayoutType(LayoutCode code, int size) noexcept : ScalarLayoutTypeBase(code, size) { }
  };

  class LayoutInt8 final : public ScalarLayoutType<int8_t>
  {
    using T = int8_t;
  public:
    ~LayoutInt8() noexcept = default;
    LayoutInt8(const LayoutInt8& other) = delete;
    LayoutInt8(LayoutInt8&& other) = delete;
    LayoutInt8& operator=(const LayoutInt8& other) = delete;
    LayoutInt8& operator=(LayoutInt8&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "int8"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutInt8() : ScalarLayoutType(LayoutCode::Int8, sizeof(T)) { }
  };

  class LayoutInt16 final : public ScalarLayoutType<int16_t>
  {
    using T = int16_t;
  public:
    ~LayoutInt16() noexcept = default;
    LayoutInt16(const LayoutInt16& other) = delete;
    LayoutInt16(LayoutInt16&& other) = delete;
    LayoutInt16& operator=(const LayoutInt16& other) = delete;
    LayoutInt16& operator=(LayoutInt16&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "int16"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutInt16() : ScalarLayoutType(LayoutCode::Int16, sizeof(T)) { }
  };

  class LayoutInt32 final : public ScalarLayoutType<int32_t>
  {
    using T = int32_t;
  public:
    ~LayoutInt32() noexcept = default;
    LayoutInt32(const LayoutInt32& other) = delete;
    LayoutInt32(LayoutInt32&& other) = delete;
    LayoutInt32& operator=(const LayoutInt32& other) = delete;
    LayoutInt32& operator=(LayoutInt32&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "int32"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutInt32() : ScalarLayoutType(LayoutCode::Int32, sizeof(T)) { }
  };

  class LayoutInt64 final : public ScalarLayoutType<int64_t>
  {
    using T = int64_t;
  public:
    ~LayoutInt64() noexcept = default;
    LayoutInt64(const LayoutInt64& other) = delete;
    LayoutInt64(LayoutInt64&& other) = delete;
    LayoutInt64& operator=(const LayoutInt64& other) = delete;
    LayoutInt64& operator=(LayoutInt64&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "int64"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutInt64() : ScalarLayoutType(LayoutCode::Int64, sizeof(T)) { }
  };

  class LayoutUInt8 final : public ScalarLayoutType<uint8_t>
  {
    using T = uint8_t;
  public:
    ~LayoutUInt8() noexcept = default;
    LayoutUInt8(const LayoutUInt8& other) = delete;
    LayoutUInt8(LayoutUInt8&& other) = delete;
    LayoutUInt8& operator=(const LayoutUInt8& other) = delete;
    LayoutUInt8& operator=(LayoutUInt8&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "uint8"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUInt8() : ScalarLayoutType(LayoutCode::UInt8, sizeof(T)) { }
  };

  class LayoutUInt16 final : public ScalarLayoutType<uint16_t>
  {
    using T = uint16_t;
  public:
    ~LayoutUInt16() noexcept = default;
    LayoutUInt16(const LayoutUInt16& other) = delete;
    LayoutUInt16(LayoutUInt16&& other) = delete;
    LayoutUInt16& operator=(const LayoutUInt16& other) = delete;
    LayoutUInt16& operator=(LayoutUInt16&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "uint16"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUInt16() : ScalarLayoutType(LayoutCode::UInt16, sizeof(T)) { }
  };

  class LayoutUInt32 final : public ScalarLayoutType<uint32_t>
  {
    using T = uint32_t;
  public:
    ~LayoutUInt32() noexcept = default;
    LayoutUInt32(const LayoutUInt32& other) = delete;
    LayoutUInt32(LayoutUInt32&& other) = delete;
    LayoutUInt32& operator=(const LayoutUInt32& other) = delete;
    LayoutUInt32& operator=(LayoutUInt32&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "uint32"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUInt32() : ScalarLayoutType(LayoutCode::UInt32, sizeof(T)) { }
  };

  class LayoutUInt64 final : public ScalarLayoutType<uint64_t>
  {
    using T = uint64_t;
  public:
    ~LayoutUInt64() noexcept = default;
    LayoutUInt64(const LayoutUInt64& other) = delete;
    LayoutUInt64(LayoutUInt64&& other) = delete;
    LayoutUInt64& operator=(const LayoutUInt64& other) = delete;
    LayoutUInt64& operator=(LayoutUInt64&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "uint64"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUInt64() : ScalarLayoutType(LayoutCode::UInt64, sizeof(T)) { }
  };

  class LayoutVarInt final : public ScalarLayoutType<int64_t>
  {
    using T = int64_t;
  public:
    ~LayoutVarInt() noexcept = default;
    LayoutVarInt(const LayoutVarInt& other) = delete;
    LayoutVarInt(LayoutVarInt&& other) = delete;
    LayoutVarInt& operator=(const LayoutVarInt& other) = delete;
    LayoutVarInt& operator=(LayoutVarInt&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "varint"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return false; }
    [[nodiscard]] bool IsVarint() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                       const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                     const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutVarInt() : ScalarLayoutType(LayoutCode::VarInt, 0) { }
  };

  class LayoutVarUInt final : public ScalarLayoutType<uint64_t>
  {
    using T = uint64_t;
  public:
    ~LayoutVarUInt() noexcept = default;
    LayoutVarUInt(const LayoutVarUInt& other) = delete;
    LayoutVarUInt(LayoutVarUInt&& other) = delete;
    LayoutVarUInt& operator=(const LayoutVarUInt& other) = delete;
    LayoutVarUInt& operator=(LayoutVarUInt&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "varuint"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return false; }
    [[nodiscard]] bool IsVarint() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                       const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                     const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutVarUInt() : ScalarLayoutType(LayoutCode::VarUInt, 0) { }
  };

  class LayoutFloat32 final : public ScalarLayoutType<float32_t>
  {
    using T = float32_t;
  public:
    ~LayoutFloat32() noexcept = default;
    LayoutFloat32(const LayoutFloat32& other) = delete;
    LayoutFloat32(LayoutFloat32&& other) = delete;
    LayoutFloat32& operator=(const LayoutFloat32& other) = delete;
    LayoutFloat32& operator=(LayoutFloat32&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "float32"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutFloat32() : ScalarLayoutType(LayoutCode::Float32, sizeof(T)) { }
  };

  class LayoutFloat64 final : public ScalarLayoutType<float64_t>
  {
    using T = float64_t;
  public:
    ~LayoutFloat64() noexcept = default;
    LayoutFloat64(const LayoutFloat64& other) = delete;
    LayoutFloat64(LayoutFloat64&& other) = delete;
    LayoutFloat64& operator=(const LayoutFloat64& other) = delete;
    LayoutFloat64& operator=(LayoutFloat64&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "float64"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutFloat64() : ScalarLayoutType(LayoutCode::Float64, sizeof(T)) { }
  };

  class LayoutFloat128 final : public ScalarLayoutType<float128_t>
  {
    using T = float128_t;
  public:
    ~LayoutFloat128() noexcept = default;
    LayoutFloat128(const LayoutFloat128& other) = delete;
    LayoutFloat128(LayoutFloat128&& other) = delete;
    LayoutFloat128& operator=(const LayoutFloat128& other) = delete;
    LayoutFloat128& operator=(LayoutFloat128&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "float128"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutFloat128() : ScalarLayoutType(LayoutCode::Float128, sizeof(T)) { }
  };

  class LayoutDecimal final : public ScalarLayoutType<decimal_t>
  {
    using T = decimal_t;
  public:
    ~LayoutDecimal() noexcept = default;
    LayoutDecimal(const LayoutDecimal& other) = delete;
    LayoutDecimal(LayoutDecimal&& other) = delete;
    LayoutDecimal& operator=(const LayoutDecimal& other) = delete;
    LayoutDecimal& operator=(LayoutDecimal&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "decimal"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutDecimal() : ScalarLayoutType(LayoutCode::Decimal, sizeof(T)) { }
  };

  class LayoutDateTime final : public ScalarLayoutType<DateTime>
  {
    using T = cdb_hr::DateTime;
  public:
    ~LayoutDateTime() noexcept = default;
    LayoutDateTime(const LayoutDateTime& other) = delete;
    LayoutDateTime(LayoutDateTime&& other) = delete;
    LayoutDateTime& operator=(const LayoutDateTime& other) = delete;
    LayoutDateTime& operator=(LayoutDateTime&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "datetime"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutDateTime() : ScalarLayoutType(LayoutCode::DateTime, sizeof(T)) { }
  };

  class LayoutUnixDateTime final : public ScalarLayoutType<UnixDateTime>
  {
    using T = cdb_hr::UnixDateTime;
  public:
    ~LayoutUnixDateTime() noexcept = default;
    LayoutUnixDateTime(const LayoutUnixDateTime& other) = delete;
    LayoutUnixDateTime(LayoutUnixDateTime&& other) = delete;
    LayoutUnixDateTime& operator=(const LayoutUnixDateTime& other) = delete;
    LayoutUnixDateTime& operator=(LayoutUnixDateTime&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "unixdatetime"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUnixDateTime() : ScalarLayoutType(LayoutCode::UnixDateTime, sizeof(T)) { }
  };

  class LayoutGuid final : public ScalarLayoutType<Guid>
  {
    using T = cdb_hr::Guid;
  public:
    ~LayoutGuid() noexcept = default;
    LayoutGuid(const LayoutGuid& other) = delete;
    LayoutGuid(LayoutGuid&& other) = delete;
    LayoutGuid& operator=(const LayoutGuid& other) = delete;
    LayoutGuid& operator=(LayoutGuid&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "guid"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutGuid() : ScalarLayoutType(LayoutCode::Guid, sizeof(T)) { }
  };

  class LayoutMongoDbObjectId final : public ScalarLayoutType<MongoDbObjectId>
  {
    using T = cdb_hr::MongoDbObjectId;
  public:
    ~LayoutMongoDbObjectId() noexcept = default;
    LayoutMongoDbObjectId(const LayoutMongoDbObjectId& other) = delete;
    LayoutMongoDbObjectId(LayoutMongoDbObjectId&& other) = delete;
    LayoutMongoDbObjectId& operator=(const LayoutMongoDbObjectId& other) = delete;
    LayoutMongoDbObjectId& operator=(LayoutMongoDbObjectId&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "mongodbobjectid"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutMongoDbObjectId() : ScalarLayoutType(LayoutCode::MongoDbObjectId, sizeof(T)) { }
  };

  class LayoutNull final : public ScalarLayoutType<NullValue>
  {
    using T = NullValue;
  public:
    ~LayoutNull() noexcept = default;
    LayoutNull(const LayoutNull& other) = delete;
    LayoutNull(LayoutNull&& other) = delete;
    LayoutNull& operator=(const LayoutNull& other) = delete;
    LayoutNull& operator=(LayoutNull&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "null"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }
    [[nodiscard]] bool IsNull() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutNull() : ScalarLayoutType(LayoutCode::Null, 0) { }
  };

  class LayoutBoolean final : public ScalarLayoutType<bool>
  {
    using T = bool;
  public:
    ~LayoutBoolean() noexcept = default;
    LayoutBoolean(const LayoutBoolean& other) = delete;
    LayoutBoolean(LayoutBoolean&& other) = delete;
    LayoutBoolean& operator=(const LayoutBoolean& other) = delete;
    LayoutBoolean& operator=(LayoutBoolean&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "bool"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return true; }
    [[nodiscard]] bool IsBool() const noexcept override { return true; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutBoolean(bool value) : ScalarLayoutType(value ? LayoutCode::Boolean : LayoutCode::BooleanFalse,
      0) { }
  };

  class LayoutUtf8 final : public ScalarLayoutType<std::string_view>
  {
    using T = std::string_view;
  public:
    ~LayoutUtf8() noexcept = default;
    LayoutUtf8(const LayoutUtf8& other) = delete;
    LayoutUtf8(LayoutUtf8&& other) = delete;
    LayoutUtf8& operator=(const LayoutUtf8& other) = delete;
    LayoutUtf8& operator=(LayoutUtf8&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "utf8"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return false; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                       const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                     const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutUtf8() : ScalarLayoutType(LayoutCode::Utf8, 0) { }
  };

  class LayoutBinary final : public ScalarLayoutType<cdb_core::ReadOnlySpan<byte>>
  {
    using T = cdb_core::ReadOnlySpan<byte>;
  public:
    ~LayoutBinary() noexcept = default;
    LayoutBinary(const LayoutBinary& other) = delete;
    LayoutBinary(LayoutBinary&& other) = delete;
    LayoutBinary& operator=(const LayoutBinary& other) = delete;
    LayoutBinary& operator=(LayoutBinary&& other) = delete;

    [[nodiscard]] std::string_view GetName() const noexcept override { return "binary"sv; }
    [[nodiscard]] bool IsFixed() const noexcept override { return false; }

    [[nodiscard]] Result WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                  const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                       const T& value) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                     const LayoutColumn& col) const noexcept override;
    [[nodiscard]] Result WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
    [[nodiscard]] std::tuple<Result, T> ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept override;

  private:
    friend struct LayoutLiteral;
    constexpr LayoutBinary() : ScalarLayoutType(LayoutCode::Binary, 0) { }
  };

  class LayoutScope : public LayoutType
  {
  public:
    ~LayoutScope() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    LayoutScope(const LayoutScope& other) = delete;
    LayoutScope(LayoutScope&& other) = delete;
    LayoutScope& operator=(const LayoutScope& other) = delete;
    LayoutScope& operator=(LayoutScope&& other) = delete;

    [[nodiscard]] bool IsFixed() const noexcept override { return false; }

    /// <summary>True if this type is a scope.</summary>
    [[nodiscard]] bool IsLayoutScope() const noexcept final { return true; }

    /// <summary>True if this type is a typed map scope.</summary>
    [[nodiscard]] virtual bool IsLayoutTypedMap() const noexcept { return false; }

    /// <summary>A function to write content into a <see cref="RowBuffer" />.</summary>
    /// <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
    /// <param name="b">The row to write to.</param>
    /// <param name="scope">The type of the scope to write into.</param>
    /// <param name="context">A context value provided by the caller.</param>
    /// <returns>The result.</returns>
    template<typename TContext>
    using WriterFunc = Result (*)(RowBuffer& b, const RowCursor& scope, TContext& context);

    [[nodiscard]] std::tuple<Result, RowCursor> ReadScope(const RowBuffer& b, RowCursor& edit) const noexcept;
    [[nodiscard]] virtual std::tuple<Result, RowCursor> WriteScope(
      RowBuffer& b,
      RowCursor& scope,
      const TypeArgumentList& typeArgs,
      UpdateOptions options = UpdateOptions::Upsert) const noexcept = 0;

    // TODO(jthunter): does this need to be virtual?
    template<typename TContext>
    Result WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                      TContext& context, WriterFunc<TContext> func,
                      UpdateOptions options = UpdateOptions::Upsert) const noexcept;

    Result DeleteScope(RowBuffer& b, RowCursor& edit) const noexcept;

  protected:
    constexpr LayoutScope(LayoutCode code, bool immutable, bool isSizedScope, bool isIndexedScope, bool isFixedArity,
                          bool isUniqueScope, bool isTypedScope) noexcept :
      LayoutType(code, immutable, 0),
      m_isSizedScope{isSizedScope},
      m_isIndexedScope{isIndexedScope},
      m_isFixedArity{isFixedArity},
      m_isUniqueScope{isUniqueScope},
      m_isTypedScope{isTypedScope} { }

  private:
    friend class LayoutType;
    friend struct LayoutLiteral;
    friend class RowBuffer;
    friend struct RowCursor;
    friend struct RowWriter;

    /// <summary>Returns true if this is a sized scope.</summary>
    [[nodiscard]]
    bool IsSizedScope() const noexcept { return m_isSizedScope; }

    /// <summary>Returns true if this is an indexed scope.</summary>
    [[nodiscard]]
    bool IsIndexedScope() const noexcept { return m_isIndexedScope; }

    /// <summary>Returns true if this is a fixed arity scope.</summary>
    [[nodiscard]]
    bool IsFixedArity() const noexcept { return m_isFixedArity; }

    /// <summary>Returns true if the scope's elements cannot be updated directly.</summary>
    [[nodiscard]]
    bool IsUniqueScope() const noexcept { return m_isUniqueScope; }

    /// <summary>Returns true if this is a typed scope.</summary>
    [[nodiscard]]
    bool IsTypedScope() const noexcept { return m_isTypedScope; }

    /// <summary>
    /// Returns true if writing an item in the specified typed scope would elide the type code
    /// because it is implied by the type arguments.
    /// </summary>
    /// <param name="edit"></param>
    /// <returns>True if the type code is implied (not written), false otherwise.</returns>
    [[nodiscard]] virtual bool HasImplicitTypeCode(const RowCursor& edit) const noexcept
    {
      return false;
    }

    virtual void SetImplicitTypeCode(RowCursor& edit) const noexcept
    {
      cdb_core::Contract::Fail("No implicit type codes.");
    }

    virtual void ReadSparsePath(const RowBuffer& row, RowCursor& edit) const noexcept;

    bool m_isSizedScope;
    bool m_isIndexedScope;
    bool m_isFixedArity;
    bool m_isUniqueScope;
    bool m_isTypedScope;
  };

  class LayoutPropertyScope : public LayoutScope
  {
  public:
    // ReSharper disable once CppHidingFunction
    ~LayoutPropertyScope() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    LayoutPropertyScope(const LayoutPropertyScope& other) = delete;
    LayoutPropertyScope(LayoutPropertyScope&& other) = delete;
    LayoutPropertyScope& operator=(const LayoutPropertyScope& other) = delete;
    LayoutPropertyScope& operator=(LayoutPropertyScope&& other) = delete;

  protected:
    constexpr LayoutPropertyScope(LayoutCode code, bool immutable) : LayoutScope(
      code,
      immutable,
      false, // isSizedScope
      false, // isIndexedScope
      false, // isFixedArity
      false, // isUniqueScope
      false) // isTypedScope
    { }
  };

  class LayoutEndScope final : public LayoutScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override { return "end"sv; }

  private:
    friend struct LayoutLiteral;

    constexpr LayoutEndScope() : LayoutScope(
      LayoutCode::EndScope, false,
      false, // isSizedScope
      false, // isIndexedScope
      false, // isFixedArity
      false, // isUniqueScope
      false) // isTypedScope
    { }

    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;
  };

  class LayoutObject final : public LayoutPropertyScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_object"sv : "object"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutObject(bool immutable) :
      LayoutPropertyScope(immutable ? LayoutCode::ImmutableObjectScope : LayoutCode::ObjectScope, immutable) { }
  };

  class LayoutUDT final : public LayoutPropertyScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override { return m_immutable ? "im_udt"sv : "udt"sv; }
    [[nodiscard]] bool IsUDT() const noexcept override { return true; }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutUDT(bool immutable) :
      LayoutPropertyScope(immutable ? LayoutCode::ImmutableSchema : LayoutCode::Schema, immutable) { }

    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                             const TypeArgumentList& value) const noexcept override;

    /// <returns>[TypeArgumentList typeArgs, uint32_t lenInBytes]</returns>
    [[nodiscard]]
    std::tuple<TypeArgumentList, uint32_t>
    ReadTypeArgumentList(const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutIndexedScope : public LayoutScope
  {
  public:
    // ReSharper disable once CppHidingFunction
    ~LayoutIndexedScope() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    LayoutIndexedScope(const LayoutIndexedScope& other) = delete;
    LayoutIndexedScope(LayoutIndexedScope&& other) = delete;
    LayoutIndexedScope& operator=(const LayoutIndexedScope& other) = delete;
    LayoutIndexedScope& operator=(LayoutIndexedScope&& other) = delete;

  protected:
    constexpr LayoutIndexedScope(LayoutCode code,
                                 bool immutable,
                                 bool isSizedScope,
                                 bool isFixedArity,
                                 bool isUniqueScope,
                                 bool isTypedScope) :
      LayoutScope(
        code,
        immutable,
        isSizedScope,
        true, // isIndexedScope
        isFixedArity,
        isUniqueScope,
        isTypedScope) { }

    void ReadSparsePath(const RowBuffer& row, RowCursor& edit) const noexcept override;
  };

  class LayoutArray final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override { return m_immutable ? "im_array"sv : "array"sv; }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutArray(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableArrayScope : LayoutCode::ArrayScope,
        immutable,
        false, // isSizedScope
        false, // isFixedArity
        false, // isUniqueScope
        false) // isTypedScope
    { }
  };

  class LayoutTypedArray final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_array_t"sv : "array_t"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTypedArray(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableTypedArrayScope : LayoutCode::TypedArrayScope,
        immutable,
        true, // isSizedScope
        false, // isFixedArity
        false, // isUniqueScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutTuple final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_tuple"sv : "tuple"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTuple(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableTupleScope : LayoutCode::TupleScope,
        immutable,
        false, // isSizedScope
        true, // isFixedArity
        false, // isUniqueScope
        false) // isTypedScope
    { }

    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutTypedTuple final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_tuple_t"sv : "tuple_t"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTypedTuple(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableTypedTupleScope : LayoutCode::TypedTupleScope,
        immutable,
        true, // isSizedScope
        true, // isFixedArity
        false, // isUniqueScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool IsLayoutTypedTuple() const noexcept override { return true; }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutTagged final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_tagged_t"sv : "tagged_t"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTagged(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableTaggedScope : LayoutCode::TaggedScope,
        immutable,
        true, // isSizedScope
        true, // isFixedArity
        false, // isUniqueScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutTagged2 final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_tagged2_t"sv : "tagged2_t"sv;
    }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTagged2(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableTagged2Scope : LayoutCode::Tagged2Scope,
        immutable,
        true, // isSizedScope
        true, // isFixedArity
        false, // isUniqueScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutNullable final : public LayoutIndexedScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override
    {
      return m_immutable ? "im_nullable"sv : "nullable"sv;
    }

    [[nodiscard]] bool IsLayoutNullable() const noexcept override { return true; }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             bool hasValue,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept;
    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

    static Result HasValue(const RowBuffer& b, const RowCursor& scope) noexcept;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutNullable(bool immutable) :
      LayoutIndexedScope(
        immutable ? LayoutCode::ImmutableNullableScope : LayoutCode::NullableScope,
        immutable,
        true, // isSizedScope
        true, // isFixedArity
        false, // isUniqueScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutUniqueScope : public LayoutIndexedScope
  {
  public:
    // ReSharper disable once CppHidingFunction
    ~LayoutUniqueScope() noexcept = default;  // NOLINT(clang-diagnostic-non-virtual-dtor)
    LayoutUniqueScope(const LayoutUniqueScope& other) = delete;
    LayoutUniqueScope(LayoutUniqueScope&& other) = delete;
    LayoutUniqueScope& operator=(const LayoutUniqueScope& other) = delete;
    LayoutUniqueScope& operator=(LayoutUniqueScope&& other) = delete;

    /// <summary>Returns true if this is a unique indexed scope.</summary>
    [[nodiscard]] bool IsLayoutUniqueScope() const noexcept override { return true; }

    [[nodiscard]] virtual TypeArgument FieldType(const RowCursor& scope) const noexcept = 0;

    [[nodiscard]] Result MoveField(RowBuffer& b, RowCursor& destinationScope, RowCursor& sourceEdit,
                                   UpdateOptions options = UpdateOptions::Upsert) const noexcept;

    // Force inherited overloads to be in scope.
    using LayoutIndexedScope::WriteScope;

    // TODO(jthunter): does this need to be virtual?
    template<typename TContext>
    [[nodiscard]] Result WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                    TContext& context, WriterFunc<TContext> func,
                                    UpdateOptions options = UpdateOptions::Upsert) const noexcept;

    /// <summary>Search for a matching field within a unique index.</summary>
    /// <param name="b">The row to search.</param>
    /// <param name="scope">The parent unique index edit to search.</param>
    /// <param name="patternScope">The parent edit from which the match pattern is read.</param>
    /// <returns> [Result result, RowCursor value]
    /// result: Success a matching field exists in the unique index, NotFound if no match is found, the
    /// error code otherwise.
    /// value: If successful, the updated edit.
    /// </returns>
    /// <remarks>The pattern field is delete whether the find succeeds or fails.</remarks>
    [[nodiscard]] std::tuple<Result, RowCursor> Find(RowBuffer& b, const RowCursor& scope,
                                                     RowCursor& patternScope) const noexcept;

  protected:
    constexpr LayoutUniqueScope(LayoutCode code, bool immutable, bool isSizedScope, bool isTypedScope) :
      LayoutIndexedScope(
        code,
        immutable,
        isSizedScope,
        false, // isFixedArity
        true, // isUniqueScope
        isTypedScope) { }
  };

  class LayoutTypedSet final : public LayoutUniqueScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override { return m_immutable ? "im_set_t"sv : "set_t"sv; }
    [[nodiscard]] TypeArgument FieldType(const RowCursor& scope) const noexcept override;

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTypedSet(bool immutable) :
      LayoutUniqueScope(
        immutable ? LayoutCode::ImmutableTypedSetScope : LayoutCode::TypedSetScope,
        immutable,
        true, // isSizedScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  class LayoutTypedMap final : public LayoutUniqueScope
  {
  public:
    [[nodiscard]] std::string_view GetName() const noexcept override { return m_immutable ? "im_map_t"sv : "map_t"sv; }
    [[nodiscard]] TypeArgument FieldType(const RowCursor& scope) const noexcept override;
    [[nodiscard]] bool IsLayoutTypedMap() const noexcept override { return true; }

    [[nodiscard]]
    std::tuple<Result, RowCursor> WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                             UpdateOptions options = UpdateOptions::Upsert) const noexcept override;

  private:
    friend struct LayoutLiteral;

    constexpr LayoutTypedMap(bool immutable) :
      LayoutUniqueScope(
        immutable ? LayoutCode::ImmutableTypedMapScope : LayoutCode::TypedMapScope,
        immutable,
        true, // isSizedScope
        true) // isTypedScope
    { }

    [[nodiscard]] bool HasImplicitTypeCode(const RowCursor& edit) const noexcept override;
    void SetImplicitTypeCode(RowCursor& edit) const noexcept override;
    [[nodiscard]] uint32_t CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept override;
    uint32_t WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept override;
    [[nodiscard]] std::tuple<TypeArgumentList, uint32_t> ReadTypeArgumentList(
      const RowBuffer& row, uint32_t offset) const noexcept override;
  };

  struct LayoutLiteral final
  {
    constexpr static LayoutInt8 Int8{};
    constexpr static LayoutInt16 Int16{};
    constexpr static LayoutInt32 Int32{};
    constexpr static LayoutInt64 Int64{};
    constexpr static LayoutUInt8 UInt8{};
    constexpr static LayoutUInt16 UInt16{};
    constexpr static LayoutUInt32 UInt32{};
    constexpr static LayoutUInt64 UInt64{};
    constexpr static LayoutVarInt VarInt{};
    constexpr static LayoutVarUInt VarUInt{};
    constexpr static LayoutFloat32 Float32{};
    constexpr static LayoutFloat64 Float64{};
    constexpr static LayoutFloat128 Float128{};
    constexpr static LayoutDecimal Decimal{};
    constexpr static LayoutDateTime DateTime{};
    constexpr static LayoutUnixDateTime UnixDateTime{};
    constexpr static LayoutGuid Guid{};
    constexpr static LayoutMongoDbObjectId MongoDbObjectId{};
    constexpr static LayoutNull Null{};
    constexpr static LayoutBoolean Boolean{true};
    constexpr static LayoutBoolean BooleanFalse{false};
    constexpr static LayoutUtf8 Utf8{};
    constexpr static LayoutBinary Binary{};
    constexpr static LayoutObject Object{false};
    constexpr static LayoutObject ImmutableObject{true};
    constexpr static LayoutArray Array{false};
    constexpr static LayoutArray ImmutableArray{true};
    constexpr static LayoutTypedArray TypedArray{false};
    constexpr static LayoutTypedArray ImmutableTypedArray{true};
    constexpr static LayoutTypedSet TypedSet{false};
    constexpr static LayoutTypedSet ImmutableTypedSet{true};
    constexpr static LayoutTypedMap TypedMap{false};
    constexpr static LayoutTypedMap ImmutableTypedMap{true};
    constexpr static LayoutTuple Tuple{false};
    constexpr static LayoutTuple ImmutableTuple{true};
    constexpr static LayoutTypedTuple TypedTuple{false};
    constexpr static LayoutTypedTuple ImmutableTypedTuple{true};
    constexpr static LayoutTagged Tagged{false};
    constexpr static LayoutTagged ImmutableTagged{true};
    constexpr static LayoutTagged2 Tagged2{false};
    constexpr static LayoutTagged2 ImmutableTagged2{true};
    constexpr static LayoutNullable Nullable{false};
    constexpr static LayoutNullable ImmutableNullable{true};
    constexpr static LayoutUDT UDT{false};
    constexpr static LayoutUDT ImmutableUDT{true};
    constexpr static LayoutEndScope EndScope{};

    template<LayoutCode literal> constexpr static const LayoutType* FromCode() noexcept;
    constexpr static const LayoutType* FromCode(LayoutCode code) noexcept;

  private:
    template<LayoutCode literal> constexpr static const LayoutType*
    FromCode(LayoutCode code) noexcept;  // NOLINT(clang-diagnostic-undefined-inline)
  };

  constexpr const LayoutType* LayoutLiteral::FromCode(LayoutCode code) noexcept
  {
    return FromCode<LayoutCode::Invalid>(code);
  }

  template<LayoutCode literal> constexpr const LayoutType* LayoutLiteral::FromCode() noexcept
  {
    return FromCode<literal>(literal);
  }

  template<LayoutCode literal> constexpr const LayoutType* LayoutLiteral::FromCode(LayoutCode code) noexcept
  {
    if (literal != LayoutCode::Invalid)
    {
      code = literal;
    }

    switch (code)
    {
    case LayoutCode::Int8:
      return &LayoutLiteral::Int8;
    case LayoutCode::Int16:
      return &LayoutLiteral::Int16;
    case LayoutCode::Int32:
      return &LayoutLiteral::Int32;
    case LayoutCode::Int64:
      return &LayoutLiteral::Int64;
    case LayoutCode::UInt8:
      return &LayoutLiteral::UInt8;
    case LayoutCode::UInt16:
      return &LayoutLiteral::UInt16;
    case LayoutCode::UInt32:
      return &LayoutLiteral::UInt32;
    case LayoutCode::UInt64:
      return &LayoutLiteral::UInt64;
    case LayoutCode::VarInt:
      return &LayoutLiteral::VarInt;
    case LayoutCode::VarUInt:
      return &LayoutLiteral::VarUInt;
    case LayoutCode::Float32:
      return &LayoutLiteral::Float32;
    case LayoutCode::Float64:
      return &LayoutLiteral::Float64;
    case LayoutCode::Float128:
      return &LayoutLiteral::Float128;
    case LayoutCode::Decimal:
      return &LayoutLiteral::Decimal;
    case LayoutCode::DateTime:
      return &LayoutLiteral::DateTime;
    case LayoutCode::UnixDateTime:
      return &LayoutLiteral::UnixDateTime;
    case LayoutCode::Guid:
      return &LayoutLiteral::Guid;
    case LayoutCode::MongoDbObjectId:
      return &LayoutLiteral::MongoDbObjectId;
    case LayoutCode::Null:
      return &LayoutLiteral::Null;
    case LayoutCode::Boolean:
      return &LayoutLiteral::Boolean;
    case LayoutCode::BooleanFalse:
      return &LayoutLiteral::BooleanFalse;
    case LayoutCode::Utf8:
      return &LayoutLiteral::Utf8;
    case LayoutCode::Binary:
      return &LayoutLiteral::Binary;
    case LayoutCode::ObjectScope:
      return &LayoutLiteral::Object;
    case LayoutCode::ImmutableObjectScope:
      return &LayoutLiteral::ImmutableObject;
    case LayoutCode::ArrayScope:
      return &LayoutLiteral::Array;
    case LayoutCode::ImmutableArrayScope:
      return &LayoutLiteral::ImmutableArray;
    case LayoutCode::TypedArrayScope:
      return &LayoutLiteral::TypedArray;
    case LayoutCode::ImmutableTypedArrayScope:
      return &LayoutLiteral::ImmutableTypedArray;
    case LayoutCode::TypedSetScope:
      return &LayoutLiteral::TypedSet;
    case LayoutCode::ImmutableTypedSetScope:
      return &LayoutLiteral::ImmutableTypedSet;
    case LayoutCode::TypedMapScope:
      return &LayoutLiteral::TypedMap;
    case LayoutCode::ImmutableTypedMapScope:
      return &LayoutLiteral::ImmutableTypedMap;
    case LayoutCode::TupleScope:
      return &LayoutLiteral::Tuple;
    case LayoutCode::ImmutableTupleScope:
      return &LayoutLiteral::ImmutableTuple;
    case LayoutCode::TypedTupleScope:
      return &LayoutLiteral::TypedTuple;
    case LayoutCode::ImmutableTypedTupleScope:
      return &LayoutLiteral::ImmutableTypedTuple;
    case LayoutCode::TaggedScope:
      return &LayoutLiteral::Tagged;
    case LayoutCode::ImmutableTaggedScope:
      return &LayoutLiteral::ImmutableTagged;
    case LayoutCode::Tagged2Scope:
      return &LayoutLiteral::Tagged2;
    case LayoutCode::ImmutableTagged2Scope:
      return &LayoutLiteral::ImmutableTagged2;
    case LayoutCode::NullableScope:
      return &LayoutLiteral::Nullable;
    case LayoutCode::ImmutableNullableScope:
      return &LayoutLiteral::ImmutableNullable;
    case LayoutCode::Schema:
      return &LayoutLiteral::UDT;
    case LayoutCode::ImmutableSchema:
      return &LayoutLiteral::ImmutableUDT;
    case LayoutCode::EndScope:
      return &LayoutLiteral::EndScope;
    default:
      cdb_core::Contract::Fail(cdb_core::make_string<tla::string>("Not Implemented: %d", static_cast<int>(code)));
    }
  }
}
