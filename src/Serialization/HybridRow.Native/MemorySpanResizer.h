// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "ISpanResizer.h"

namespace cdb_hr
{
  template<typename T>
  class MemorySpanResizer : public ISpanResizer<T>
  {
  public:
    MemorySpanResizer(uint32_t initialCapacity = 0) : m_memory{initialCapacity} { }
    MemorySpanResizer(cdb_core::Memory<T> buffer) : m_memory{std::move(buffer)} { }

    cdb_core::Memory<T> GetMemory() const noexcept { return m_memory; }

    cdb_core::Span<T> Resize(uint32_t minimumLength, cdb_core::Span<T> buffer) override
    {
      // ReSharper disable once CppEntityAssignedButNoRead
      cdb_core::Memory<T> old{};
      if (m_memory.Length() < minimumLength)
      {
        // Keep the old allocation alive till the end of the method
        old = std::move(m_memory);
        // Allocate a new block.
        m_memory = cdb_core::Memory<T>(std::max(minimumLength, buffer.Length()));
      }

      cdb_core::Span<T> next = m_memory.AsSpan();
      if (!buffer.IsEmpty() && next.Slice(0, buffer.Length()) != buffer)
      {
        buffer.CopyTo(next);
      }

      return next;
    }

  private:
    cdb_core::Memory<T> m_memory;
  };
}
