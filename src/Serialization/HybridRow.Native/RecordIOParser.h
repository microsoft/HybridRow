// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>
  /// A push parser for RecordIO streams.
  /// </summary>
  /// <remarks>
  /// The RecordIO parser is a linear value type.  It is intended to be created on the stack
  /// and then repeatedly pushed buffers in a sequence.  It then signals when the boundaries
  /// of the RecordIO tokens are reached.
  /// </remarks>
  struct RecordIOParser final
  {
    [[nodiscard]] RecordIOParser() noexcept = default;
    ~RecordIOParser() noexcept = default;
    RecordIOParser(const RecordIOParser& other) noexcept = delete;
    RecordIOParser(RecordIOParser&& other) noexcept = default;
    RecordIOParser& operator=(const RecordIOParser& other) noexcept = delete;
    RecordIOParser& operator=(RecordIOParser&& other) noexcept = default;

    /// <summary>Describes the type of Hybrid Rows produced by the parser.</summary>
    enum class ProductionType
    {
      /// <summary>No hybrid row was produced. The parser needs more data.</summary>
      None = 0,

      /// <summary>A new segment row was produced.</summary>
      Segment,

      /// <summary>A record in the current segment was produced.</summary>
      Record,
    };

    /// <summary>True if a valid segment has been parsed.</summary>
    [[nodiscard]] bool HaveSegment() const noexcept;

    /// <summary>If a valid segment has been parsed then current active segment, otherwise undefined.</summary>
    [[nodiscard]] const Segment& GetSegment() const noexcept;

    /// <summary>Processes one buffers worth of data possibly advancing the parser state.</summary>
    /// <param name="buffer">The buffer to consume.</param>
    /// <returns>
    /// A tuple: {result, type, record, need, consumed}
    /// <p><b>result:</b>
    /// <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result.Success" /> if no error
    /// has occurred, otherwise a valid
    /// <see cref="Microsoft.Azure.Cosmos.Serialization.HybridRow.Result" /> of the last error encountered
    /// during parsing.
    /// </p>
    /// <p><b>type: </b>Indicates the type of Hybrid Row produced in record.</p>
    /// <p><b>record: </b>If non-empty, then the body of the next record in the sequence.</p>
    /// <p><b>need: </b>
    /// The smallest number of bytes needed to advanced the parser state further. It is
    /// recommended that Process not be called again until at least this number of bytes are available.
    /// </p>
    /// <p><b>consumed: </b>
    /// The number of bytes consumed from the input buffer. This number may be less
    /// than the total buffer size if the parser moved to a new state.
    /// </p>
    /// </returns>
    std::tuple<Result, ProductionType, cdb_core::ReadOnlyMemory<byte>, uint32_t, uint32_t>
    Process(const cdb_core::ReadOnlyMemory<byte>& buffer) noexcept;

  private:
    /// <summary>The states for the internal state machine.</summary>
    /// <remarks>Note: numerical ordering of these states matters.</remarks>
    enum class State : uint8_t
    {
      Start = 0, // Start: no buffers have yet been provided to the parser.
      Error, // Unrecoverable parse error encountered.
      NeedSegmentLength, // Parsing segment header length
      NeedSegment, // Parsing segment header
      NeedHeader, // Parsing HybridRow header
      NeedRecord, // Parsing record header
      NeedRow, // Parsing row body
    };

    State m_state;
    std::unique_ptr<Segment> m_segment;
    std::unique_ptr<Record> m_record;
  };

  inline bool cdb_hr::RecordIOParser::HaveSegment() const noexcept { return m_state >= State::NeedHeader; }

  inline const cdb_hr::Segment& cdb_hr::RecordIOParser::GetSegment() const noexcept
  {
    cdb_core::Contract::Requires(HaveSegment());
    return *m_segment;
  }
}
