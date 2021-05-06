// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "Result.h"
#include "RowBuffer.h"

namespace cdb_hr
{
  class Segment;

  struct RecordIOFormatter final
  {
    // Static Class
    [[nodiscard]] RecordIOFormatter() = delete;
    RecordIOFormatter(const RecordIOFormatter& other) = delete;
    RecordIOFormatter(RecordIOFormatter&& other) noexcept = delete;
    RecordIOFormatter& operator=(const RecordIOFormatter& other) = delete;
    RecordIOFormatter& operator=(RecordIOFormatter&& other) noexcept = delete;

    static std::tuple<Result, RowBuffer> FormatSegment(const Segment& segment, ISpanResizer<byte>& resizer) noexcept;
    static std::tuple<Result, RowBuffer> FormatRecord(const cdb_core::ReadOnlyMemory<byte>& body,
                                                      ISpanResizer<byte>& resizer)  noexcept;
  };
}
