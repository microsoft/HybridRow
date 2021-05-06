// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "Namespace.h"

namespace cdb_hr
{
  class Segment final
  {
  public:
    [[nodiscard]] Segment() noexcept :
      m_length{},
      m_comment{},
      m_sdl{},
      m_schema{} {}

    [[nodiscard]] int32_t GetLength() const noexcept { return m_length; }
    void SetLength(int32_t value) noexcept { m_length = value; }
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view value) noexcept { m_comment = value; }
    [[nodiscard]] std::string_view GetSDL() const noexcept { return m_sdl; }
    void SetSDL(std::string_view value) noexcept { m_sdl = value; }
    [[nodiscard]] const std::unique_ptr<Namespace>& GetSchema() const noexcept { return m_schema; }
    void SetSchema(std::unique_ptr<Namespace> value) noexcept { m_schema = std::move(value); }
  private:
    int32_t m_length;
    tla::string m_comment;
    tla::string m_sdl;
    std::unique_ptr<Namespace> m_schema;
  };

  class Record final
  {
  public:
    [[nodiscard]] Record() noexcept :
      m_length{},
      m_crc32{} {}
    [[nodiscard]] Record(int32_t length, uint32_t crc32) noexcept :
      m_length{length},
      m_crc32{crc32} {}

    [[nodiscard]] int32_t GetLength() const noexcept { return m_length; }
    void SetLength(int32_t value) noexcept { m_length = value; }
    [[nodiscard]] uint32_t GetCrc32() const noexcept { return m_crc32; }
    void SetCrc32(uint32_t value) noexcept { m_crc32 = value; }
  private:
    int32_t m_length{};
    uint32_t m_crc32{};
  };
}
