// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "RowCursor.h"
#include "RowBuffer.h"
#include "IHybridRowSerializer.h"
#include "SystemSchema.h"
#include "Segment.h"
#include "RecordIOParser.h"

// ReSharper disable CppClangTidyCppcoreguidelinesAvoidGoto
// ReSharper disable CppRedundantControlFlowJump
namespace cdb_hr
{
  std::tuple<Result, RecordIOParser::ProductionType, cdb_core::ReadOnlyMemory<byte>, uint32_t, uint32_t>
  cdb_hr::RecordIOParser::Process(const cdb_core::ReadOnlyMemory<byte>& buffer) noexcept
  {
    Result r = Result::Failure;
    cdb_core::ReadOnlyMemory<byte> b = buffer;
    switch (m_state)
    {
    case State::Start:
    {
      m_state = State::NeedSegmentLength;
      goto NeedSegmentLength;
    }

    case State::NeedSegmentLength:
    {
    NeedSegmentLength:
      uint32_t minimalSegmentRowSize = HybridRowHeader::Size + SegmentHybridRowSerializer::Size;
      if (b.Length() < minimalSegmentRowSize)
      {
        uint32_t need = minimalSegmentRowSize;
        uint32_t consumed = buffer.Length() - b.Length();
        return {Result::InsufficientBuffer, ProductionType::None, {}, need, consumed};
      }

      // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
      // The cost-cast implied by MemoryMarshal::AsMemory is only safe here because:
      // 1. Only READ operations are performed on the row.
      // 2. The row is not allowed to escape this code.
      cdb_core::ReadOnlyMemory<byte> mem = b.Slice(0, minimalSegmentRowSize);
      RowBuffer row{
        cdb_core::MemoryMarshal::AsMemory(mem).AsSpan(),
        HybridRowVersion::V1,
        &SchemasHrSchema::GetLayoutResolver(),
        nullptr
      };
      RowCursor root = RowCursor::Create(row);
      std::tie(r, m_segment) = SegmentHybridRowSerializer::Read(row, root, true);
      if (r != Result::Success)
      {
        break;
      }

      m_state = State::NeedSegment;
      goto NeedSegment;
    }

    case State::NeedSegment:
    {
    NeedSegment:
      if (b.Length() < static_cast<uint32_t>(m_segment->GetLength()))
      {
        uint32_t need = static_cast<uint32_t>(m_segment->GetLength());
        uint32_t consumed = buffer.Length() - b.Length();
        return {Result::InsufficientBuffer, ProductionType::None, {}, need, consumed};
      }

      // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
      // The cost-cast implied by MemoryMarshal::AsMemory is only safe here because:
      // 1. Only READ operations are performed on the row.
      // 2. The row is not allowed to escape this code.
      cdb_core::ReadOnlyMemory<byte> mem = b.Slice(0, static_cast<uint32_t>(m_segment->GetLength()));
      RowBuffer row{
        cdb_core::MemoryMarshal::AsMemory(mem).AsSpan(),
        HybridRowVersion::V1,
        &SchemasHrSchema::GetLayoutResolver(),
        nullptr
      };
      RowCursor root = RowCursor::Create(row);
      std::tie(r, m_segment) = SegmentHybridRowSerializer::Read(row, root, true);
      if (r != Result::Success)
      {
        break;
      }

      cdb_core::ReadOnlyMemory<byte> record = b.Slice(0, mem.Length());
      b = b.Slice(mem.Length());
      uint32_t need = 0;
      m_state = State::NeedHeader;
      uint32_t consumed = buffer.Length() - b.Length();
      return {Result::Success, ProductionType::Segment, record, need, consumed};
    }

    case State::NeedHeader:
    {
      if (b.Length() < HybridRowHeader::Size)
      {
        uint32_t need = HybridRowHeader::Size;
        uint32_t consumed = buffer.Length() - b.Length();
        return {Result::InsufficientBuffer, ProductionType::None, {}, need, consumed};
      }

      HybridRowHeader header = cdb_core::MemoryMarshal::Read<HybridRowHeader>(b.AsSpan());
      if (header.GetVersion() != HybridRowVersion::V1)
      {
        r = Result::InvalidRow;
        break;
      }

      if (header.GetSchemaId() == SegmentHybridRowSerializer::Id)
      {
        goto NeedSegment;
      }

      if (header.GetSchemaId() == RecordHybridRowSerializer::Id)
      {
        goto NeedRecord;
      }

      r = Result::InvalidRow;
      break;
    }

    case State::NeedRecord:
    {
    NeedRecord:
      uint32_t minimalRecordRowSize = HybridRowHeader::Size + RecordHybridRowSerializer::Size;
      if (b.Length() < minimalRecordRowSize)
      {
        uint32_t need = minimalRecordRowSize;
        uint32_t consumed = buffer.Length() - b.Length();
        return {Result::InsufficientBuffer, ProductionType::None, {}, need, consumed};
      }

      // TODO: remove this cost-cast when ReadOnlyRowBuffer exists.
      // The cost-cast implied by MemoryMarshal::AsMemory is only safe here because:
      // 1. Only READ operations are performed on the row.
      // 2. The row is not allowed to escape this code.
      cdb_core::ReadOnlyMemory<byte> mem = b.Slice(0, minimalRecordRowSize);
      RowBuffer row{
        cdb_core::MemoryMarshal::AsMemory(mem).AsSpan(),
        HybridRowVersion::V1,
        &SchemasHrSchema::GetLayoutResolver(),
        nullptr
      };
      RowCursor root = RowCursor::Create(row);
      std::tie(r, m_record) = RecordHybridRowSerializer::Read(row, root, true);
      if (r != Result::Success)
      {
        break;
      }

      b = b.Slice(mem.Length());
      m_state = State::NeedRow;
      goto NeedRow;
    }

    case State::NeedRow:
    {
    NeedRow:
      if (b.Length() < static_cast<uint32_t>(m_record->GetLength()))
      {
        uint32_t need = static_cast<uint32_t>(m_record->GetLength());
        uint32_t consumed = buffer.Length() - b.Length();
        return {Result::InsufficientBuffer, ProductionType::None, {}, need, consumed};
      }

      cdb_core::ReadOnlyMemory<byte> record = b.Slice(0, static_cast<uint32_t>(m_record->GetLength()));

      // Validate that the record has not been corrupted.
      uint32_t crc32 = cdb_core::Crc32::Update(0, record.AsSpan());
      if (crc32 != m_record->GetCrc32())
      {
        r = Result::InvalidRow;
        break;
      }

      b = b.Slice(m_record->GetLength());
      uint32_t need = 0;
      m_state = State::NeedHeader;
      uint32_t consumed = buffer.Length() - b.Length();
      return {Result::Success, ProductionType::Record, record, need, consumed};
    }
    default:
      cdb_core::Contract::Fail("Invalid state");
    }

    m_state = State::Error;
    uint32_t need = 0;
    uint32_t consumed = buffer.Length() - b.Length();
    return {r, ProductionType::None, {}, need, consumed};
  }
}
