// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "TypeArgument.h"
#include "TypeArgumentList.h"
#include "LayoutType.h"

#include "LayoutCodeTraits.h"
#include "RowBuffer.h"
#include "RowCursor.h"

namespace cdb_hr
{
  size_t LayoutType::GetHashCode() const noexcept
  {
    static_assert(cdb_core::is_hashable_v<decltype(this)>);

    return std::hash<const LayoutType*>{}(this);
  }

  /// <summary>Helper for preparing the delete of a sparse field.</summary>
  /// <param name="b">The row to delete from.</param>
  /// <param name="edit">The parent edit containing the field to delete.</param>
  /// <param name="code">The expected type of the field.</param>
  /// <returns>Success if the delete is permitted, the error code otherwise.</returns>
  Result LayoutType::PrepareSparseDelete(const RowBuffer& b, const RowCursor& edit, LayoutCode code) noexcept
  {
    if (edit.m_scopeType->IsFixedArity())
    {
      return Result::TypeConstraint;
    }

    if (edit.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    if (edit.m_exists && LayoutCodeTraits::Canonicalize(edit.m_cellType->GetLayoutCode()) != code)
    {
      return Result::TypeMismatch;
    }

    return Result::Success;
  }

  Result LayoutType::PrepareSparseWrite(RowBuffer& b, RowCursor& edit, const TypeArgument& typeArg,
                                        UpdateOptions options) noexcept
  {
    if (edit.m_immutable || (edit.m_scopeType->IsUniqueScope() && !edit.m_deferUniqueIndex))
    {
      return Result::InsufficientPermissions;
    }

    if (edit.m_scopeType->IsFixedArity() && !(edit.m_scopeType->IsLayoutNullable()))
    {
      if ((edit.m_index < edit.m_scopeTypeArgs.GetCount()) && typeArg != edit.m_scopeTypeArgs[edit.m_index])
      {
        return Result::TypeConstraint;
      }
    }
    else if (edit.m_scopeType->IsLayoutTypedMap())
    {
      if (!((typeArg.GetType()->IsLayoutTypedTuple()) && typeArg.GetTypeArgs() == edit.m_scopeTypeArgs))
      {
        return Result::TypeConstraint;
      }
    }
    else if (edit.m_scopeType->IsTypedScope() && typeArg != edit.m_scopeTypeArgs[0])
    {
      return Result::TypeConstraint;
    }

    if ((options == UpdateOptions::InsertAt) && edit.m_scopeType->IsFixedArity())
    {
      return Result::TypeConstraint;
    }

    if ((options == UpdateOptions::InsertAt) && !edit.m_scopeType->IsFixedArity())
    {
      edit.m_exists = false; // InsertAt never overwrites an existing item.
    }

    if ((options == UpdateOptions::Update) && (!edit.m_exists))
    {
      return Result::NotFound;
    }

    if ((options == UpdateOptions::Insert) && edit.m_exists)
    {
      return Result::Exists;
    }

    return Result::Success;
  }

  Result LayoutType::PrepareSparseRead(const RowBuffer& b, const RowCursor& edit, LayoutCode code) noexcept
  {
    if (!edit.m_exists)
    {
      return Result::NotFound;
    }

    if (LayoutCodeTraits::Canonicalize(edit.m_cellType->GetLayoutCode()) != code)
    {
      return Result::TypeMismatch;
    }

    return Result::Success;
  }

  std::tuple<Result, RowCursor> LayoutType::PrepareSparseMove(RowBuffer& b, const RowCursor& destinationScope,
                                                              const LayoutScope* destinationCode,
                                                              const TypeArgument& elementType, RowCursor& srcEdit,
                                                              UpdateOptions options) noexcept
  {
    cdb_core::Contract::Requires(destinationScope.m_scopeType == destinationCode);
    cdb_core::Contract::Requires(destinationScope.m_index == 0, "Can only insert into a edit at the root");

    // Prepare the delete of the source.
    Result result = PrepareSparseDelete(b, srcEdit, elementType.GetType()->GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    if (!srcEdit.m_exists)
    {
      return {Result::NotFound, {}};
    }

    if (destinationScope.m_immutable)
    {
      b.DeleteSparse(srcEdit);
      return {Result::InsufficientPermissions, {}};
    }

    if (srcEdit.m_cellTypeArgs != elementType.GetTypeArgs())
    {
      b.DeleteSparse(srcEdit);
      return {Result::TypeConstraint, {}};
    }

    if (options == UpdateOptions::InsertAt)
    {
      b.DeleteSparse(srcEdit);
      return {Result::TypeConstraint, {}};
    }

    // Prepare the insertion at the destination.
    RowCursor dstEdit = b.PrepareSparseMove(destinationScope, srcEdit);
    if ((options == UpdateOptions::Update) && (!dstEdit.m_exists))
    {
      b.DeleteSparse(srcEdit);
      return {Result::NotFound, {}};
    }

    if ((options == UpdateOptions::Insert) && dstEdit.m_exists)
    {
      b.DeleteSparse(srcEdit);
      return {Result::Exists, {}};
    }

    return {Result::Success, dstEdit};
  }

  uint32_t LayoutType::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    return sizeof(LayoutCode);
  }

  uint32_t LayoutType::WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept
  {
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    return sizeof(LayoutCode);
  }

  std::tuple<TypeArgument, uint32_t> LayoutType::ReadTypeArgument(const RowBuffer& row, uint32_t offset) noexcept
  {
    const LayoutType* itemCode = row.ReadSparseTypeCode(offset);
    auto [itemTypeArgs, argsLenInBytes] = itemCode->ReadTypeArgumentList(row, offset + sizeof(LayoutCode));
    uint32_t lenInBytes = sizeof(LayoutCode) + argsLenInBytes;
    return {TypeArgument(itemCode, itemTypeArgs), lenInBytes};
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutType::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    return {{}, {}};
  }

  Result ScalarLayoutTypeBase::HasValue(const RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) noexcept
  {
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return Result::NotFound;
    }

    return Result::Success;
  }

  Result ScalarLayoutTypeBase::DeleteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    if (col.GetNullBit().IsInvalid())
    {
      // Cannot delete a non-nullable fixed column.
      return Result::TypeMismatch;
    }

    b.UnsetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  /// <summary>Delete an existing value.</summary>
  /// <remarks>
  /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
  /// a decrease in required space.  If no value exists this operation is a no-op.
  /// </remarks>
  Result ScalarLayoutTypeBase::DeleteVariable(RowBuffer& b, const RowCursor& scope,
                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    bool exists = b.ReadBit(scope.m_start, col.GetNullBit());
    if (exists)
    {
      int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
      b.DeleteVariable(varOffset, IsVarint());
      b.UnsetBit(scope.m_start, col.GetNullBit());
    }

    return Result::Success;
  }

  /// <summary>Delete an existing value.</summary>
  /// <remarks>
  /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
  /// a decrease in required space.  If no value exists this operation is a no-op.
  /// </remarks>
  Result ScalarLayoutTypeBase::DeleteSparse(RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = LayoutType::PrepareSparseDelete(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return result;
    }

    b.DeleteSparse(edit);
    return Result::Success;
  }

  Result LayoutInt8::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteInt8(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutInt8::T> LayoutInt8::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                          const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadInt8(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutInt8::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseInt8(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutInt8::T> LayoutInt8::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseInt8(edit);
    return {Result::Success, value};
  }

  Result LayoutInt16::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                 const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteInt16(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutInt16::T> LayoutInt16::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                            const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadInt16(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutInt16::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseInt16(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutInt16::T> LayoutInt16::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseInt16(edit);
    return {Result::Success, value};
  }

  Result LayoutInt32::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                 const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteInt32(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutInt32::T> LayoutInt32::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                            const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadInt32(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutInt32::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseInt32(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutInt32::T> LayoutInt32::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseInt32(edit);
    return {Result::Success, value};
  }

  Result LayoutInt64::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                 const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteInt64(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutInt64::T> LayoutInt64::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                            const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadInt64(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutInt64::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseInt64(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutInt64::T> LayoutInt64::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseInt64(edit);
    return {Result::Success, value};
  }

  Result LayoutUInt8::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                 const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteUInt8(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt8::T> LayoutUInt8::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                            const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadUInt8(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutUInt8::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseUInt8(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt8::T> LayoutUInt8::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseUInt8(edit);
    return {Result::Success, value};
  }

  Result LayoutUInt16::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                  const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteUInt16(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt16::T> LayoutUInt16::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadUInt16(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutUInt16::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseUInt16(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt16::T> LayoutUInt16::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseUInt16(edit);
    return {Result::Success, value};
  }

  Result LayoutUInt32::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                  const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteUInt32(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt32::T> LayoutUInt32::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadUInt32(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutUInt32::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseUInt32(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt32::T> LayoutUInt32::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseUInt32(edit);
    return {Result::Success, value};
  }

  Result LayoutUInt64::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                  const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteUInt64(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt64::T> LayoutUInt64::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadUInt64(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutUInt64::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseUInt64(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUInt64::T> LayoutUInt64::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseUInt64(edit);
    return {Result::Success, value};
  }

  Result LayoutFloat32::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                   const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteFloat32(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat32::T> LayoutFloat32::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadFloat32(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutFloat32::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseFloat32(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat32::T> LayoutFloat32::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseFloat32(edit);
    return {Result::Success, value};
  }

  Result LayoutFloat64::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                   const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteFloat64(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat64::T> LayoutFloat64::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadFloat64(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutFloat64::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseFloat64(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat64::T> LayoutFloat64::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseFloat64(edit);
    return {Result::Success, value};
  }

  Result LayoutFloat128::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteFloat128(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat128::T> LayoutFloat128::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                  const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadFloat128(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutFloat128::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseFloat128(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutFloat128::T> LayoutFloat128::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseFloat128(edit);
    return {Result::Success, value};
  }

  Result LayoutDecimal::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                   const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteDecimal(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutDecimal::T> LayoutDecimal::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadDecimal(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutDecimal::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseDecimal(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutDecimal::T> LayoutDecimal::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseDecimal(edit);
    return {Result::Success, value};
  }

  Result LayoutBoolean::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                   const bool& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    if (value)
    {
      b.SetBit(scope.m_start, col.GetBoolBit());
    }
    else
    {
      b.UnsetBit(scope.m_start, col.GetBoolBit());
    }

    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, bool> LayoutBoolean::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                    const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, false};
    }

    bool value = b.ReadBit(scope.m_start, col.GetBoolBit());
    return {Result::Success, value};
  }

  Result LayoutBoolean::WriteSparse(RowBuffer& b, RowCursor& edit, const bool& value,
                                    UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseBool(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, bool> LayoutBoolean::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, false};
    }

    bool value = b.ReadSparseBool(edit);
    return {Result::Success, value};
  }

  Result LayoutNull::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutNull::T> LayoutNull::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                          const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    return {Result::Success, {}};
  }

  Result LayoutNull::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseNull(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutNull::T> LayoutNull::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseNull(edit);
    return {Result::Success, value};
  }

  Result LayoutDateTime::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                    const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteDateTime(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutDateTime::T> LayoutDateTime::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                  const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadDateTime(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutDateTime::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                     UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseDateTime(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutDateTime::T> LayoutDateTime::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseDateTime(edit);
    return {Result::Success, value};
  }

  Result LayoutUnixDateTime::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                        const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteUnixDateTime(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUnixDateTime::T> LayoutUnixDateTime::ReadFixed(
    const RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadUnixDateTime(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutUnixDateTime::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                         UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseUnixDateTime(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUnixDateTime::T> LayoutUnixDateTime::ReadSparse(
    const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseUnixDateTime(edit);
    return {Result::Success, value};
  }

  Result LayoutGuid::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteGuid(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutGuid::T> LayoutGuid::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                          const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadGuid(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutGuid::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseGuid(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutGuid::T> LayoutGuid::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseGuid(edit);
    return {Result::Success, value};
  }

  Result LayoutMongoDbObjectId::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                           const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteMongoDbObjectId(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutMongoDbObjectId::T> LayoutMongoDbObjectId::ReadFixed(
    const RowBuffer& b, const RowCursor& scope, const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadMongoDbObjectId(scope.m_start + col.GetOffset());
    return {Result::Success, value};
  }

  Result LayoutMongoDbObjectId::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value,
                                            UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseMongoDbObjectId(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutMongoDbObjectId::T> LayoutMongoDbObjectId::ReadSparse(
    const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseMongoDbObjectId(edit);
    return {Result::Success, value};
  }

  Result LayoutUtf8::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    cdb_core::Contract::Requires(static_cast<uint32_t>(value.size()) == col.GetSize());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteFixedString(scope.m_start + col.GetOffset(), value);
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutUtf8::T> LayoutUtf8::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                          const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadFixedString(scope.m_start + col.GetOffset(), col.GetSize());
    return {Result::Success, value};
  }

  Result LayoutUtf8::WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                   const LayoutUtf8::T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    uint32_t length = static_cast<uint32_t>(value.size());
    if ((col.GetSize() > 0) && (length > col.GetSize()))
    {
      return Result::TooBig;
    }

    bool exists = b.ReadBit(scope.m_start, col.GetNullBit());
    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    int32_t shift = b.WriteVariableString(varOffset, value, exists);
    b.SetBit(scope.m_start, col.GetNullBit());
    scope.m_metaOffset += shift;
    scope.m_valueOffset += shift;
    return Result::Success;
  }

  std::tuple<Result, LayoutUtf8::T> LayoutUtf8::ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                             const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    T value = b.ReadVariableString(varOffset);
    return {Result::Success, value};
  }

  Result LayoutUtf8::WriteSparse(RowBuffer& b, RowCursor& edit, const LayoutUtf8::T& value,
                                 UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseString(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutUtf8::T> LayoutUtf8::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseString(edit);
    return {Result::Success, value};
  }

  Result LayoutBinary::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                  const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    cdb_core::Contract::Requires(value.Length() == col.GetSize());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    b.WriteFixedBinary(scope.m_start + col.GetOffset(), value, col.GetSize());
    b.SetBit(scope.m_start, col.GetNullBit());
    return Result::Success;
  }

  std::tuple<Result, LayoutBinary::T> LayoutBinary::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    T value = b.ReadFixedBinary(scope.m_start + col.GetOffset(), col.GetSize());
    return {Result::Success, value};
  }

  Result LayoutBinary::WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                     const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    uint32_t length = value.Length();
    if ((col.GetSize() > 0) && (length > col.GetSize()))
    {
      return Result::TooBig;
    }

    bool exists = b.ReadBit(scope.m_start, col.GetNullBit());
    uint32_t varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    int32_t shift = b.WriteVariableBinary(varOffset, value, exists);
    b.SetBit(scope.m_start, col.GetNullBit());
    scope.m_metaOffset += shift;
    scope.m_valueOffset += shift;
    return Result::Success;
  }

  std::tuple<Result, LayoutBinary::T> LayoutBinary::ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                                 const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    uint32_t varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    T value = b.ReadVariableBinary(varOffset);
    return {Result::Success, value};
  }

  Result LayoutBinary::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseBinary(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutBinary::T> LayoutBinary::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseBinary(edit);
    return {Result::Success, value};
  }

  Result LayoutVarInt::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                  const T& value) const noexcept
  {
    cdb_core::Contract::Fail("Not Implemented");
  }

  std::tuple<Result, LayoutVarInt::T> LayoutVarInt::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                              const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Fail("Not Implemented");
  }

  Result LayoutVarInt::WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                     const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    bool exists = b.ReadBit(scope.m_start, col.GetNullBit());
    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    int32_t shift = b.WriteVariableInt(varOffset, value, exists);
    b.SetBit(scope.m_start, col.GetNullBit());
    scope.m_metaOffset += shift;
    scope.m_valueOffset += shift;
    return Result::Success;
  }

  std::tuple<Result, LayoutVarInt::T> LayoutVarInt::ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                                 const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    T value = b.ReadVariableInt(varOffset);
    return {Result::Success, value};
  }

  Result LayoutVarInt::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseVarInt(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutVarInt::T> LayoutVarInt::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseVarInt(edit);
    return {Result::Success, value};
  }

  Result LayoutVarUInt::WriteFixed(RowBuffer& b, const RowCursor& scope, const LayoutColumn& col,
                                   const T& value) const noexcept
  {
    cdb_core::Contract::Fail("Not Implemented");
  }

  std::tuple<Result, LayoutVarUInt::T> LayoutVarUInt::ReadFixed(const RowBuffer& b, const RowCursor& scope,
                                                                const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Fail("Not Implemented");
  }

  Result LayoutVarUInt::WriteVariable(RowBuffer& b, RowCursor& scope, const LayoutColumn& col,
                                      const T& value) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (scope.m_immutable)
    {
      return Result::InsufficientPermissions;
    }

    bool exists = b.ReadBit(scope.m_start, col.GetNullBit());
    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    int32_t shift = b.WriteVariableUInt(varOffset, value, exists);
    b.SetBit(scope.m_start, col.GetNullBit());
    scope.m_metaOffset += shift;
    scope.m_valueOffset += shift;
    return Result::Success;
  }

  std::tuple<Result, LayoutVarUInt::T> LayoutVarUInt::ReadVariable(const RowBuffer& b, const RowCursor& scope,
                                                                   const LayoutColumn& col) const noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsUDT());
    if (!b.ReadBit(scope.m_start, col.GetNullBit()))
    {
      return {Result::NotFound, {}};
    }

    int varOffset = b.ComputeVariableValueOffset(*scope.m_layout, scope.m_start, col.GetOffset());
    T value = b.ReadVariableUInt(varOffset);
    return {Result::Success, value};
  }

  Result LayoutVarUInt::WriteSparse(RowBuffer& b, RowCursor& edit, const T& value, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, edit, GetTypeArg(), options);
    if (result != Result::Success)
    {
      return result;
    }

    b.WriteSparseVarUInt(edit, value, options);
    return Result::Success;
  }

  std::tuple<Result, LayoutVarUInt::T> LayoutVarUInt::ReadSparse(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, {}};
    }

    T value = b.ReadSparseVarUInt(edit);
    return {Result::Success, value};
  }

  std::tuple<Result, RowCursor> LayoutScope::ReadScope(const RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = LayoutType::PrepareSparseRead(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return {result, RowCursor{}};
    }

    RowCursor value = b.SparseIteratorReadScope(edit,
      IsImmutable() || edit.m_immutable || edit.m_scopeType->IsUniqueScope());
    return {Result::Success, std::move(value)};
  }

  Result LayoutScope::DeleteScope(RowBuffer& b, RowCursor& edit) const noexcept
  {
    Result result = LayoutType::PrepareSparseDelete(b, edit, GetLayoutCode());
    if (result != Result::Success)
    {
      return result;
    }

    b.DeleteSparse(edit);
    return Result::Success;
  }

  void LayoutScope::ReadSparsePath(const RowBuffer& row, RowCursor& edit) const noexcept
  {
    auto [token, pathLenInBytes, pathOffset] = row.ReadSparsePathLen(*edit.m_layout, edit.m_valueOffset);
    edit.m_pathToken = token;
    edit.m_pathOffset = pathOffset;
    edit.m_valueOffset += pathLenInBytes;
  }

  std::tuple<Result, RowCursor> LayoutEndScope::WriteScope(RowBuffer& b, RowCursor& scope,
                                                           const TypeArgumentList& typeArgs,
                                                           UpdateOptions options) const noexcept
  {
    cdb_core::Contract::Fail("Cannot write an EndScope directly");
  }

  std::tuple<Result, RowCursor> LayoutObject::WriteScope(RowBuffer& b, RowCursor& scope,
                                                         const TypeArgumentList& typeArgs,
                                                         UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteSparseObject(scope, this, options)};
  }

  std::tuple<Result, RowCursor> LayoutUDT::WriteScope(RowBuffer& b, RowCursor& scope, const TypeArgumentList& typeArgs,
                                                      UpdateOptions options) const noexcept
  {
    const Layout& udt = b.GetResolver()->Resolve(typeArgs.GetSchemaId());
    Result result = LayoutType::PrepareSparseWrite(b, scope, TypeArgument{this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteSparseUDT(scope, this, udt, options)};
  }

  uint32_t LayoutUDT::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    return sizeof(LayoutCode) + sizeof(SchemaId);
  }

  uint32_t LayoutUDT::WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept
  {
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    row.WriteSchemaId(offset + sizeof(LayoutCode), value.GetSchemaId());
    return sizeof(LayoutCode) + sizeof(SchemaId);
  }

  /// <returns>[TypeArgumentList typeArgs, uint32_t lenInBytes]</returns>
  std::tuple<TypeArgumentList, uint32_t> LayoutUDT::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    SchemaId schemaId = row.ReadSchemaId(offset);
    return {TypeArgumentList(schemaId), static_cast<uint32_t>(sizeof(SchemaId))};
  }

  void LayoutIndexedScope::ReadSparsePath(const RowBuffer& row, RowCursor& edit) const noexcept
  {
    edit.m_pathToken = 0;
    edit.m_pathOffset = 0;
  }

  std::tuple<Result, RowCursor> LayoutArray::WriteScope(RowBuffer& b, RowCursor& scope,
                                                        const TypeArgumentList& typeArgs,
                                                        UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteSparseArray(scope, this, options)};
  }

  std::tuple<Result, RowCursor> LayoutTypedArray::WriteScope(RowBuffer& b, RowCursor& scope,
                                                             const TypeArgumentList& typeArgs,
                                                             UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedArray(scope, this, typeArgs, options)};
  }

  bool LayoutTypedArray::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() == 1);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[0].GetType()->GetLayoutCode());
  }

  void LayoutTypedArray::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeTypeArgs[0].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[0].GetTypeArgs();
  }

  uint32_t LayoutTypedArray::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    return sizeof(LayoutCode) + value[0].GetType()->CountTypeArgument(value[0].GetTypeArgs());
  }

  uint32_t LayoutTypedArray::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                               const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += value[0].GetType()->WriteTypeArgument(row, offset + lenInBytes, value[0].GetTypeArgs());
    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTypedArray::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    auto [typeArg, lenInBytes] = ReadTypeArgument(row, offset);
    return {TypeArgumentList{{typeArg}}, lenInBytes};
  }

  std::tuple<Result, RowCursor> LayoutTuple::WriteScope(RowBuffer& b, RowCursor& scope,
                                                        const TypeArgumentList& typeArgs,
                                                        UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteSparseTuple(scope, this, typeArgs, options)};
  }

  uint32_t LayoutTuple::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += RowBuffer::Count7BitEncodedUInt(static_cast<uint64_t>(value.GetCount()));
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->CountTypeArgument(arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  uint32_t LayoutTuple::WriteTypeArgument(RowBuffer& row, uint32_t offset, const TypeArgumentList& value) const noexcept
  {
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, static_cast<uint64_t>(value.GetCount()));
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->WriteTypeArgument(row, offset + lenInBytes, arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTuple::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    auto [numTypeArgs, lenInBytes] = row.Read7BitEncodedUInt(offset);
    tla::vector<TypeArgument> retval;
    retval.resize(numTypeArgs);
    for (uint64_t i = 0; i < numTypeArgs; i++)
    {
      uint32_t itemLenInBytes;
      std::tie(retval[i], itemLenInBytes) = ReadTypeArgument(row, offset + lenInBytes);
      lenInBytes += itemLenInBytes;
    }
    return {std::move(retval), lenInBytes};
  }

  std::tuple<Result, RowCursor> LayoutTypedTuple::WriteScope(RowBuffer& b, RowCursor& scope,
                                                             const TypeArgumentList& typeArgs,
                                                             UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedTuple(scope, this, typeArgs, options)};
  }

  bool LayoutTypedTuple::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() > edit.m_index);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[edit.m_index].GetType()->GetLayoutCode());
  }

  void LayoutTypedTuple::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeTypeArgs[edit.m_index].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[edit.m_index].GetTypeArgs();
  }

  uint32_t LayoutTypedTuple::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += RowBuffer::Count7BitEncodedUInt(static_cast<uint64_t>(value.GetCount()));
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->CountTypeArgument(arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  uint32_t LayoutTypedTuple::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                               const TypeArgumentList& value) const noexcept
  {
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, static_cast<uint64_t>(value.GetCount()));
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->WriteTypeArgument(row, offset + lenInBytes, arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTypedTuple::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    auto [numTypeArgs, lenInBytes] = row.Read7BitEncodedUInt(offset);
    tla::vector<TypeArgument> retval;
    retval.resize(numTypeArgs);
    for (uint64_t i = 0; i < numTypeArgs; i++)
    {
      uint32_t itemLenInBytes;
      std::tie(retval[i], itemLenInBytes) = ReadTypeArgument(row, offset + lenInBytes);
      lenInBytes += itemLenInBytes;
    }
    return {std::move(retval), lenInBytes};
  }

  std::tuple<Result, RowCursor> LayoutTagged::WriteScope(RowBuffer& b, RowCursor& scope,
                                                         const TypeArgumentList& typeArgs,
                                                         UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedTuple(scope, this, typeArgs, options)};
  }

  bool LayoutTagged::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() > edit.m_index);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[edit.m_index].GetType()->GetLayoutCode());
  }

  void LayoutTagged::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeTypeArgs[edit.m_index].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[edit.m_index].GetTypeArgs();
  }

  uint32_t LayoutTagged::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 2);
    return sizeof(LayoutCode) + value[1].GetType()->CountTypeArgument(value[1].GetTypeArgs());
  }

  uint32_t LayoutTagged::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                           const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 2);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += value[1].GetType()->WriteTypeArgument(row, offset + lenInBytes, value[1].GetTypeArgs());
    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTagged::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    tla::vector<TypeArgument> retval;
    retval.resize(2);
    retval[0] = TypeArgument{&LayoutLiteral::UInt8, {}};
    uint32_t lenInBytes;
    std::tie(retval[1], lenInBytes) = ReadTypeArgument(row, offset);
    return {std::move(retval), lenInBytes};
  }

  std::tuple<Result, RowCursor> LayoutTagged2::WriteScope(RowBuffer& b, RowCursor& scope,
                                                          const TypeArgumentList& typeArgs,
                                                          UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedTuple(scope, this, typeArgs, options)};
  }

  bool LayoutTagged2::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() > edit.m_index);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[edit.m_index].GetType()->GetLayoutCode());
  }

  void LayoutTagged2::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeTypeArgs[edit.m_index].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[edit.m_index].GetTypeArgs();
  }

  uint32_t LayoutTagged2::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 3);
    uint32_t lenInBytes = sizeof(LayoutCode);
    for (size_t i = 1; i < value.GetCount(); i++)
    {
      TypeArgument arg = value[i];
      lenInBytes += arg.GetType()->CountTypeArgument(arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  uint32_t LayoutTagged2::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                            const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 3);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    for (size_t i = 1; i < value.GetCount(); i++)
    {
      TypeArgument arg = value[i];
      lenInBytes += arg.GetType()->WriteTypeArgument(row, offset + lenInBytes, arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTagged2::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    uint32_t lenInBytes = 0;
    tla::vector<TypeArgument> retval;
    retval.resize(3);
    retval[0] = TypeArgument{&LayoutLiteral::UInt8, {}};
    for (size_t i = 1; i < 3; i++)
    {
      uint32_t itemLenInBytes;
      std::tie(retval[i], itemLenInBytes) = ReadTypeArgument(row, offset + lenInBytes);
      lenInBytes += itemLenInBytes;
    }

    return {retval, lenInBytes};
  }

  std::tuple<Result, RowCursor> LayoutNullable::WriteScope(RowBuffer& b, RowCursor& scope,
                                                           const TypeArgumentList& typeArgs,
                                                           bool hasValue, UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteNullable(scope, this, typeArgs, options, hasValue)};
  }

  std::tuple<Result, RowCursor> LayoutNullable::WriteScope(RowBuffer& b, RowCursor& scope,
                                                           const TypeArgumentList& typeArgs,
                                                           UpdateOptions options) const noexcept
  {
    return WriteScope(b, scope, typeArgs, true, options);
  }

  Result LayoutNullable::HasValue(const RowBuffer& b, const RowCursor& scope) noexcept
  {
    cdb_core::Contract::Requires(scope.m_scopeType->IsLayoutNullable());
    cdb_core::Contract::Assert(scope.m_index == 1 || scope.m_index == 2, "Nullable scopes always point at the value");
    cdb_core::Contract::Assert(scope.m_scopeTypeArgs.GetCount() == 1);
    bool hasValue = b.ReadInt8(scope.m_start) != 0;
    return hasValue ? Result::Success : Result::NotFound;
  }

  bool LayoutNullable::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() == 1);
    cdb_core::Contract::Assert(edit.m_index == 1);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[0].GetType()->GetLayoutCode());
  }

  void LayoutNullable::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_index == 1);
    edit.m_cellType = edit.m_scopeTypeArgs[0].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[0].GetTypeArgs();
  }

  uint32_t LayoutNullable::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    return sizeof(LayoutCode) + value[0].GetType()->CountTypeArgument(value[0].GetTypeArgs());
  }

  uint32_t LayoutNullable::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                             const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += value[0].GetType()->WriteTypeArgument(row, offset + lenInBytes, value[0].GetTypeArgs());
    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutNullable::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    auto [typeArg, lenInBytes] = ReadTypeArgument(row, offset);
    return {TypeArgumentList{{typeArg}}, lenInBytes};
  }

  Result LayoutUniqueScope::MoveField(RowBuffer& b, RowCursor& destinationScope, RowCursor& sourceEdit,
                                      UpdateOptions options) const noexcept
  {
    auto [result, dstEdit] = PrepareSparseMove(b, destinationScope, this, FieldType(destinationScope), sourceEdit,
      options);
    if (result != Result::Success)
    {
      return result;
    }

    // Perform the move.
    b.TypedCollectionMoveField(dstEdit, sourceEdit, static_cast<RowOptions>(options));

    // TODO: it would be "better" if the destinationScope were updated to point to the 
    // highest item seen.  Then we would avoid the maximum reparse.
    destinationScope.m_count = dstEdit.m_count;
    return Result::Success;
  }

  std::tuple<Result, RowCursor> LayoutUniqueScope::Find(RowBuffer& b, const RowCursor& scope,
                                                        RowCursor& patternScope) const noexcept
  {
    auto [result, value] = PrepareSparseMove(b, scope, this, FieldType(scope), patternScope, UpdateOptions::Update);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    // Check if the search found the result.
    b.DeleteSparse(patternScope);

    return {Result::Success, value};
  }

  TypeArgument LayoutTypedSet::FieldType(const RowCursor& scope) const noexcept
  {
    return scope.m_scopeTypeArgs[0];
  }

  std::tuple<Result, RowCursor> LayoutTypedSet::WriteScope(RowBuffer& b, RowCursor& scope,
                                                           const TypeArgumentList& typeArgs,
                                                           UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedSet(scope, this, typeArgs, options)};
  }

  bool LayoutTypedSet::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    cdb_core::Contract::Assert(edit.m_scopeTypeArgs.GetCount() == 1);
    return !LayoutCodeTraits::AlwaysRequiresTypeCode(edit.m_scopeTypeArgs[0].GetType()->GetLayoutCode());
  }

  void LayoutTypedSet::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeTypeArgs[0].GetType();
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs[0].GetTypeArgs();
  }

  uint32_t LayoutTypedSet::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    return sizeof(LayoutCode) + value[0].GetType()->CountTypeArgument(value[0].GetTypeArgs());
  }

  uint32_t LayoutTypedSet::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                             const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 1);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    lenInBytes += value[0].GetType()->WriteTypeArgument(row, offset + lenInBytes, value[0].GetTypeArgs());
    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTypedSet::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    auto [typeArg, lenInBytes] = ReadTypeArgument(row, offset);
    return {TypeArgumentList{{typeArg}}, lenInBytes};
  }

  TypeArgument LayoutTypedMap::FieldType(const RowCursor& scope) const noexcept
  {
    return TypeArgument{
      scope.m_scopeType->IsImmutable() ? &LayoutLiteral::ImmutableTypedTuple : &LayoutLiteral::TypedTuple,
      scope.m_scopeTypeArgs
    };
  }

  std::tuple<Result, RowCursor> LayoutTypedMap::WriteScope(RowBuffer& b, RowCursor& scope,
                                                           const TypeArgumentList& typeArgs,
                                                           UpdateOptions options) const noexcept
  {
    Result result = PrepareSparseWrite(b, scope, {this, typeArgs}, options);
    if (result != Result::Success)
    {
      return {result, {}};
    }

    return {Result::Success, b.WriteTypedMap(scope, this, typeArgs, options)};
  }

  bool LayoutTypedMap::HasImplicitTypeCode(const RowCursor& edit) const noexcept
  {
    return true;
  }

  void LayoutTypedMap::SetImplicitTypeCode(RowCursor& edit) const noexcept
  {
    edit.m_cellType = edit.m_scopeType->IsImmutable()
                        ? &LayoutLiteral::ImmutableTypedTuple
                        : &LayoutLiteral::TypedTuple;
    edit.m_cellTypeArgs = edit.m_scopeTypeArgs;
  }

  uint32_t LayoutTypedMap::CountTypeArgument([[maybe_unused]] const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 2);
    uint32_t lenInBytes = sizeof(LayoutCode);
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->CountTypeArgument(arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  uint32_t LayoutTypedMap::WriteTypeArgument(RowBuffer& row, uint32_t offset,
                                             const TypeArgumentList& value) const noexcept
  {
    cdb_core::Contract::Assert(value.GetCount() == 2);
    row.WriteSparseTypeCode(offset, GetLayoutCode());
    uint32_t lenInBytes = sizeof(LayoutCode);
    for (const TypeArgument& arg : value)
    {
      lenInBytes += arg.GetType()->WriteTypeArgument(row, offset + lenInBytes, arg.GetTypeArgs());
    }

    return lenInBytes;
  }

  std::tuple<TypeArgumentList, uint32_t> LayoutTypedMap::ReadTypeArgumentList(
    const RowBuffer& row, uint32_t offset) const noexcept
  {
    uint32_t lenInBytes = 0;
    tla::vector<TypeArgument> retval;
    retval.resize(2);
    for (int i = 0; i < 2; i++)
    {
      uint32_t itemLenInBytes;
      std::tie(retval[i], itemLenInBytes) = ReadTypeArgument(row, offset + lenInBytes);
      lenInBytes += itemLenInBytes;
    }

    return {retval, lenInBytes};
  }
}
