// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Stopwatch.h"

namespace cdb_core
{
  static LARGE_INTEGER InitFrequency() noexcept
  {
    LARGE_INTEGER freq;
    QueryPerformanceFrequency(&freq);
    return freq;
  }

  LARGE_INTEGER Stopwatch::s_frequency = InitFrequency();

  void Stopwatch::Start() noexcept
  {
    if (m_running)
    {
      return;
    }
    m_running = true;
    QueryPerformanceCounter(&m_start);
  }

  void Stopwatch::Stop() noexcept
  {
    if (!m_running)
    {
      return;
    }
    m_running = false;
    LARGE_INTEGER end;
    QueryPerformanceCounter(&end);
    m_elapsed.QuadPart += end.QuadPart - m_start.QuadPart;
  }

  void Stopwatch::Reset() noexcept
  {
    m_elapsed.QuadPart = 0;
  }
}
