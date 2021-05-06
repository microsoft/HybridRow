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
  struct RowWriter final
  {
    /// <summary>The resolver for UDTs.</summary>
    [[nodiscard]] const LayoutResolver* GetResolver() const noexcept { return m_row.GetResolver(); }

    /// <summary>The length of row in bytes.</summary>
    [[nodiscard]] uint32_t GetLength() const noexcept { return m_row.GetLength(); }

    /// <summary>The active layout of the current writer scope.</summary>
    [[nodiscard]] const Layout& GetLayout() const noexcept { return m_cursor.GetLayout(); }

    /// <summary>Write an entire row in a streaming left-to-right way.</summary>
    /// <typeparam name="TContext">The type of the context value to pass to <paramref name="func" />.</typeparam>
    /// <param name="row">The row to write.</param>
    /// <param name="func">A function to write the entire row.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    template<typename TCallable, typename = std::is_nothrow_invocable_r<Result, TCallable, RowWriter&, const
      TypeArgument&>>
    static Result WriteBuffer(RowBuffer& row, TCallable& func)
    {
      RowWriter writer{row, RowCursor::Create(row)};
      TypeArgument typeArg{&LayoutLiteral::UDT, {writer.GetLayout().GetSchemaId()}};
      Result result = std::invoke(func, writer, typeArg);
      row = writer.m_row;
      return result;
    }

    /// <summary>Write a field as a <see cref="bool" />.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteBool(std::string_view path, bool value)
    {
      return WritePrimitive<bool, LayoutCode::Boolean, &RowBuffer::WriteSparseBool>(
        path,
        value,
        &LayoutLiteral::Boolean);
    }

    /// <summary>Write a field as a <see cref="t:null"/>.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteNull(std::string_view path)
    {
      return WritePrimitive<NullValue, LayoutCode::Null, &RowBuffer::WriteSparseNull>(
        path,
        {},
        &LayoutLiteral::Null);
    }

    /// <summary>Write a field as a fixed length, 8-bit, signed integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteInt8(std::string_view path, int8_t value)
    {
      return WritePrimitive<int8_t, LayoutCode::Int8, &RowBuffer::WriteSparseInt8>(
        path,
        value,
        &LayoutLiteral::Int8);
    }

    /// <summary>Write a field as a fixed length, 16-bit, signed integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteInt16(std::string_view path, int16_t value)
    {
      return WritePrimitive<int16_t, LayoutCode::Int16, &RowBuffer::WriteSparseInt16>(
        path,
        value,
        &LayoutLiteral::Int16);
    }

    /// <summary>Write a field as a fixed length, 32-bit, signed integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteInt32(std::string_view path, int32_t value)
    {
      return WritePrimitive<int32_t, LayoutCode::Int32, &RowBuffer::WriteSparseInt32>(
        path,
        value,
        &LayoutLiteral::Int32);
    }

    /// <summary>Write a field as a fixed length, 64-bit, signed integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteInt64(std::string_view path, int64_t value)
    {
      return WritePrimitive<int64_t, LayoutCode::Int64, &RowBuffer::WriteSparseInt64>(
        path,
        value,
        &LayoutLiteral::Int64);
    }

    /// <summary>Write a field as a fixed length, 8-bit, unsigned integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteUInt8(std::string_view path, uint8_t value)
    {
      return WritePrimitive<uint8_t, LayoutCode::UInt8, &RowBuffer::WriteSparseUInt8>(
        path,
        value,
        &LayoutLiteral::UInt8);
    }

    /// <summary>Write a field as a fixed length, 16-bit, unsigned integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteUInt16(std::string_view path, uint16_t value)
    {
      return WritePrimitive<uint16_t, LayoutCode::UInt16, &RowBuffer::WriteSparseUInt16>(
        path,
        value,
        &LayoutLiteral::UInt16);
    }

    /// <summary>Write a field as a fixed length, 32-bit, unsigned integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteUInt32(std::string_view path, uint32_t value)
    {
      return WritePrimitive<uint32_t, LayoutCode::UInt32, &RowBuffer::WriteSparseUInt32>(
        path,
        value,
        &LayoutLiteral::UInt32);
    }

    /// <summary>Write a field as a fixed length, 64-bit, unsigned integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteUInt64(std::string_view path, uint64_t value)
    {
      return WritePrimitive<uint64_t, LayoutCode::UInt64, &RowBuffer::WriteSparseUInt64>(
        path,
        value,
        &LayoutLiteral::UInt64);
    }

    /// <summary>Write a field as a variable length, 7-bit encoded, signed integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteVarInt(std::string_view path, int64_t value)
    {
      return WritePrimitive<int64_t, LayoutCode::VarInt, &RowBuffer::WriteSparseVarInt>(
        path,
        value,
        &LayoutLiteral::VarInt);
    }

    /// <summary>Write a field as a variable length, 7-bit encoded, unsigned integer.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteVarUInt(std::string_view path, uint64_t value)
    {
      return WritePrimitive<uint64_t, LayoutCode::VarUInt, &RowBuffer::WriteSparseVarUInt>(
        path,
        value,
        &LayoutLiteral::VarUInt);
    }

    /// <summary>Write a field as a fixed length, 32-bit, IEEE-encoded floating point value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteFloat32(std::string_view path, float32_t value)
    {
      return WritePrimitive<float32_t, LayoutCode::Float32, &RowBuffer::WriteSparseFloat32>(
        path,
        value,
        &LayoutLiteral::Float32);
    }

    /// <summary>Write a field as a fixed length, 64-bit, IEEE-encoded floating point value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteFloat64(std::string_view path, float64_t value)
    {
      return WritePrimitive<float64_t, LayoutCode::Float64, &RowBuffer::WriteSparseFloat64>(
        path,
        value,
        &LayoutLiteral::Float64);
    }

    /// <summary>Write a field as a fixed length, 128-bit, IEEE-encoded floating point value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteFloat128(std::string_view path, float128_t value)
    {
      return WritePrimitive<float128_t, LayoutCode::Float128, &RowBuffer::WriteSparseFloat128>(
        path,
        value,
        &LayoutLiteral::Float128);
    }

    /// <summary>Write a field as a fixed length <see cref="decimal" /> value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteDecimal(std::string_view path, Decimal value)
    {
      return WritePrimitive<Decimal, LayoutCode::Decimal, &RowBuffer::WriteSparseDecimal>(
        path,
        value,
        &LayoutLiteral::Decimal);
    }

    /// <summary>Write a field as a fixed length <see cref="DateTime" /> value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteDateTime(std::string_view path, DateTime value)
    {
      return WritePrimitive<DateTime, LayoutCode::DateTime, &RowBuffer::WriteSparseDateTime>(
        path,
        value,
        &LayoutLiteral::DateTime);
    }

    /// <summary>Write a field as a fixed length <see cref="UnixDateTime" /> value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteUnixDateTime(std::string_view path, UnixDateTime value)
    {
      return WritePrimitive<UnixDateTime, LayoutCode::UnixDateTime, &RowBuffer::WriteSparseUnixDateTime>(
        path,
        value,
        &LayoutLiteral::UnixDateTime);
    }

    /// <summary>Write a field as a fixed length <see cref="Guid" /> value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteGuid(std::string_view path, Guid value)
    {
      return WritePrimitive<Guid, LayoutCode::Guid, &RowBuffer::WriteSparseGuid>(
        path,
        value,
        &LayoutLiteral::Guid);
    }

    /// <summary>Write a field as a fixed length <see cref="MongoDbObjectId" /> value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteMongoDbObjectId(std::string_view path, MongoDbObjectId value)
    {
      return WritePrimitive<MongoDbObjectId, LayoutCode::MongoDbObjectId, &RowBuffer::WriteSparseMongoDbObjectId>(
        path,
        value,
        &LayoutLiteral::MongoDbObjectId);
    }

    /// <summary>Write a field as a variable length, UTF8 encoded, string value.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteString(std::string_view path, std::string_view value)
    {
      return WritePrimitive<std::string_view, LayoutCode::Utf8, &RowBuffer::WriteSparseString>(
        path,
        value,
        &LayoutLiteral::Utf8);
    }

    /// <summary>Write a field as a variable length, sequence of bytes.</summary>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result WriteBinary(std::string_view path, cdb_core::ReadOnlySpan<byte> value)
    {
      return WritePrimitive<cdb_core::ReadOnlySpan<byte>, LayoutCode::Binary, &RowBuffer::WriteSparseBinary>(
        path,
        value,
        &LayoutLiteral::Binary);
    }

    template<typename T, typename TSerializer, typename = std::enable_if_t<is_hybridrow_serializer_v<T, TSerializer>>>
    Result WriteScope(std::string_view path, const TypeArgument& typeArg, const T& value) noexcept
    {
      Result result = PrepareSparseWrite(path, typeArg);
      if (result != Result::Success)
      {
        return result;
      }

      result = TSerializer{}.Write(m_row, m_cursor, /* isRoot */ false, typeArg.GetTypeArgs(), value);
      if (result != Result::Success)
      {
        return result;
      }

      m_cursor.MoveNext(m_row);
      return Result::Success;
    }

    template<typename TCallable, typename = std::is_nothrow_invocable_r<Result, TCallable, RowWriter&, const
      TypeArgument&>>
    Result WriteScope(std::string_view path, const TypeArgument& typeArg, TCallable& func)
    {
      const LayoutType* type = typeArg.GetType();
      Result result = PrepareSparseWrite(path, typeArg);
      if (result != Result::Success)
      {
        return result;
      }

      RowCursor nestedScope;
      const LayoutScope* scopeType = static_cast<const LayoutScope*>(type);
      switch (LayoutCodeTraits::ClearImmutableBit(type->GetLayoutCode()))
      {
      case LayoutCode::ObjectScope:
        nestedScope = m_row.WriteSparseObject(m_cursor, scopeType, UpdateOptions::Upsert);
        break;
      case LayoutCode::ArrayScope:
        nestedScope = m_row.WriteSparseArray(m_cursor, scopeType, UpdateOptions::Upsert);
        break;
      case LayoutCode::TypedArrayScope:
        nestedScope = m_row.WriteTypedArray(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::TupleScope:
        nestedScope = m_row.WriteSparseTuple(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::TypedTupleScope:
        nestedScope = m_row.WriteTypedTuple(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::TaggedScope:
        nestedScope = m_row.WriteTypedTuple(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::Tagged2Scope:
        nestedScope = m_row.WriteTypedTuple(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::NullableScope:
        nestedScope = m_row.WriteNullable(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert,
          func != false);

        break;
      case LayoutCode::Schema:
        const Layout& udt = m_row.GetResolver()->Resolve(typeArg.GetTypeArgs().GetSchemaId());
        nestedScope = m_row.WriteSparseUDT(m_cursor, scopeType, &udt, UpdateOptions::Upsert);
        break;

      case LayoutCode::TypedSetScope:
        nestedScope = m_row.WriteTypedSet(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;
      case LayoutCode::TypedMapScope:
        nestedScope = m_row.WriteTypedMap(
          m_cursor,
          scopeType,
          typeArg.GetTypeArgs(),
          UpdateOptions::Upsert);

        break;

      default:
        return Result::Failure;
      }

      RowWriter nestedWriter{m_row, nestedScope};
      result = func ? std::invoke(func, nestedWriter, typeArg) : Result::Success;
      m_row = nestedWriter.m_row;
      nestedScope.m_count = nestedWriter.m_cursor.m_count;

      if (result != Result::Success)
      {
        // TODO: what about unique violations here?
        return result;
      }

      if (type->IsLayoutUniqueScope())
      {
        result = m_row.TypedCollectionUniqueIndexRebuild(nestedScope);
        if (result != Result::Success)
        {
          // TODO: If the index rebuild fails then the row is corrupted.  Should we automatically clean up here?
          return result;
        }
      }

      m_cursor.MoveNext(m_row, nestedWriter.m_cursor);
      return Result::Success;
    }

  private:
    /// <summary>Helper for writing a primitive value.</summary>
    /// <typeparam name="TValue">The type of the primitive value.</typeparam>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <param name="type">The layout type.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    template<typename TValue, LayoutCode code,
      void(RowBuffer::*WriteSparseFunc)(RowCursor& edit, TValue value, UpdateOptions options) noexcept>
    Result WritePrimitive(std::string_view path, TValue value, const ScalarLayoutType<TValue>* type) noexcept
    {
      Result result = Result::NotFound;
      if (m_cursor.m_scopeType->IsUDT())
      {
        result = WriteSchematizedValue<TValue, code>(path, value);
      }

      if (result == Result::NotFound)
      {
        // Write sparse value.
        result = PrepareSparseWrite(path, type->GetTypeArg());
        if (result != Result::Success)
        {
          return result;
        }

        std::invoke(WriteSparseFunc, &m_row, m_cursor, value, UpdateOptions::Upsert);
        m_cursor.MoveNext(m_row);
      }

      return result;
    }

    /// <summary>Helper for preparing the write of a sparse field.</summary>
    /// <param name="path">The path identifying the field to write.</param>
    /// <param name="typeArg">The (optional) type constraints.</param>
    /// <returns>Success if the write is permitted, the error code otherwise.</returns>
    Result PrepareSparseWrite(std::string_view path, const TypeArgument& typeArg) noexcept;

    /// <summary>Write a generic schematized field value via the scope's layout.</summary>
    /// <typeparam name="TValue">The expected type of the field.</typeparam>
    /// <param name="path">The scope-relative path of the field to write.</param>
    /// <param name="value">The value to write.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    template<typename TValue, LayoutCode code>
    Result WriteSchematizedValue(std::string_view path, TValue value) noexcept
    {
      auto [found, pCol] = m_cursor.m_layout->TryFind(path);
      if (!found)
      {
        return Result::NotFound;
      }
      const LayoutColumn& col = *pCol;

      if (LayoutCodeTraits::Canonicalize(col.GetType()->GetLayoutCode()) != code)
      {
        return Result::NotFound;
      }

      const ScalarLayoutType<TValue>* t = static_cast<const ScalarLayoutType<TValue>*>(col.GetType());
      switch (col.GetStorage())
      {
      case StorageKind::Fixed:
        return t->WriteFixed(m_row, m_cursor, col, value);

      case StorageKind::Variable:
        return t->WriteVariable(m_row, m_cursor, col, value);

      default:
        return Result::NotFound;
      }
    }

    /// <summary>Initializes a new instance of the <see cref="RowWriter" /> struct.</summary>
    /// <param name="row">The row to be read.</param>
    /// <param name="scope">The scope into which items should be written.</param>
    /// <remarks>
    /// A <see cref="RowWriter" /> instance writes the fields of a given scope from left to right
    /// in a forward only manner. If the root scope is provided then all top-level fields in the row can be
    /// written.
    /// </remarks>
    RowWriter(RowBuffer& row, RowCursor scope) noexcept : m_row{row}, m_cursor{std::move(scope)} { }

    RowBuffer m_row;
    RowCursor m_cursor;
  };
}
