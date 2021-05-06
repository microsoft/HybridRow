// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <chrono>

namespace cdb_core
{
  /// <summary>
  /// Represents a time interval.
  /// </summary>
  /// <remarks>
  /// TimeSpan mimics the behavior of C# TimeSpan and provides APIs for getting time intervals
  /// in similar APIs for various durations.
  /// </remarks>
  struct TimeSpan final
  {
  public:
    static TimeSpan FromTicks(int64_t oneHundredNanosecondTicks) noexcept;
    static TimeSpan FromMilliseconds(int64_t milliseconds) noexcept;
    static TimeSpan FromSeconds(int64_t seconds) noexcept;

    [[nodiscard]] int64_t GetTotalMilliseconds() const noexcept;
    [[nodiscard]] int64_t GetTotalSeconds() const noexcept;
    [[nodiscard]] int64_t GetTotalTicks() const noexcept;

    [[nodiscard]] double GetMilliseconds() const noexcept;
    [[nodiscard]] double GetMicroseconds() const noexcept;

  private:
    using durationTicks = std::chrono::duration<int64_t, std::ratio<1, 10000000>>;
    constexpr TimeSpan(durationTicks ticks);
    durationTicks m_ticks;
  };

  constexpr TimeSpan::TimeSpan(durationTicks ticks) : m_ticks(ticks) { }

  inline TimeSpan TimeSpan::FromTicks(int64_t oneHundredNanosecondTicks) noexcept
  {
    durationTicks durationTicks{oneHundredNanosecondTicks};
    return {durationTicks};
  }

  inline TimeSpan TimeSpan::FromMilliseconds(int64_t milliseconds) noexcept
  {
    std::chrono::milliseconds durationMs{milliseconds};
    return {std::chrono::duration_cast<durationTicks>(durationMs)};
  }

  inline TimeSpan TimeSpan::FromSeconds(int64_t seconds) noexcept
  {
    std::chrono::seconds durationSec{seconds};
    return {std::chrono::duration_cast<durationTicks>(durationSec)};
  }

  [[nodiscard]] inline int64_t TimeSpan::GetTotalMilliseconds() const noexcept
  {
    return std::chrono::duration_cast<std::chrono::milliseconds>(m_ticks).count();
  }

  [[nodiscard]] inline int64_t TimeSpan::GetTotalSeconds() const noexcept
  {
    return std::chrono::duration_cast<std::chrono::seconds>(m_ticks).count();
  }

  [[nodiscard]] inline int64_t TimeSpan::GetTotalTicks() const noexcept
  {
    return m_ticks.count();
  }

  [[nodiscard]] inline double TimeSpan::GetMilliseconds() const noexcept
  {
    return 
      std::chrono::duration_cast<std::chrono::duration<double, std::chrono::milliseconds::period>>(m_ticks).count();
  }

  [[nodiscard]] inline double TimeSpan::GetMicroseconds() const noexcept
  {
    return std::chrono::duration_cast<std::chrono::duration<double, std::chrono::microseconds::period>>(m_ticks).count();
  }
}
