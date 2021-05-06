// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "TimeSpan.h"

namespace cdb_core
{
  class Stopwatch final
  {
  public:
    Stopwatch() noexcept = default;
    ~Stopwatch() noexcept = default;
    Stopwatch(const Stopwatch& other) = default;
    Stopwatch(Stopwatch&& other) noexcept = default;
    Stopwatch& operator=(const Stopwatch& other) = default;
    Stopwatch& operator=(Stopwatch&& other) noexcept = default;

    void Start() noexcept;
    void Stop() noexcept;
    void Reset() noexcept;
    [[nodiscard]] TimeSpan Elapsed() const noexcept;

    [[nodiscard]] int64_t ElapsedRaw() const noexcept;
    [[nodiscard]] static TimeSpan AsTimeSpan(int64_t raw) noexcept;

  private:
    bool m_running;
    LARGE_INTEGER m_start;
    LARGE_INTEGER m_elapsed;

    static LARGE_INTEGER s_frequency;
  };

  inline int64_t Stopwatch::ElapsedRaw() const noexcept { return m_elapsed.QuadPart; }

  inline TimeSpan Stopwatch::Elapsed() const noexcept
  {
    return Stopwatch::AsTimeSpan(m_elapsed.QuadPart);
  }

  inline TimeSpan Stopwatch::AsTimeSpan(int64_t raw) noexcept
  {
    return TimeSpan::FromTicks((raw * 10000000) / s_frequency.QuadPart);
  }
}
