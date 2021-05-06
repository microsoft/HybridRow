// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Segment.h"
#include "IHybridRowSerializer.h"
#include "MemorySpanResizer.h"
#include "SystemSchema.h"
#include "RowCursor.h"
#include "RecordIOFormatter.h"

// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace cdb_hr
{
  template<typename T, typename TSerializer, typename = std::enable_if_t<is_hybridrow_serializer_v<T, TSerializer>>>
  static std::tuple<Result, RowBuffer> FormatObject(
    ISpanResizer<byte>& resizer,
    uint32_t initialCapacity,
    const Layout& layout,
    const T& obj) noexcept
  {
    RowBuffer row{initialCapacity, &resizer};
    const LayoutResolver& resolver = SchemasHrSchema::GetLayoutResolver();
    row.InitLayout(HybridRowVersion::V1, layout, &resolver);
    RowCursor root = RowCursor::Create(row);
    Result r = TSerializer::Write(row, root, true, {}, obj);
    if (r != Result::Success)
    {
      row.Reset();
    }

    return {r, row};
  }

  std::tuple<Result, RowBuffer> RecordIOFormatter::FormatSegment(const Segment& segment, ISpanResizer<byte>& resizer) noexcept
  {
    const Layout& layout = SchemasHrSchema::GetLayoutResolver().Resolve(SegmentHybridRowSerializer::Id);
    uint32_t estimatedSize = HybridRowHeader::Size + layout.GetSize() +
      static_cast<uint32_t>(segment.GetComment().size()) +
      static_cast<uint32_t>(segment.GetSDL().size()) +
      uint32_t{20};

    return FormatObject<Segment, SegmentHybridRowSerializer>(resizer, estimatedSize, layout, segment);
  }

  std::tuple<Result, RowBuffer> RecordIOFormatter::FormatRecord(const cdb_core::ReadOnlyMemory<byte>& body,
                                                                ISpanResizer<byte>& resizer) noexcept
  {
    const Layout& layout = SchemasHrSchema::GetLayoutResolver().Resolve(RecordHybridRowSerializer::Id);
    uint32_t estimatedSize = HybridRowHeader::Size + layout.GetSize() + body.Length();
    uint32_t crc32 = cdb_core::Crc32::Update(0, body.AsSpan());
    Record record{static_cast<int32_t>(body.Length()), crc32};
    return FormatObject<Record, RecordHybridRowSerializer>(resizer, estimatedSize, layout, record);
  }
}
