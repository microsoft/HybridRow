// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Failure.h"

namespace cdb_core
{
  const Failure Failure::None = Failure();

  Failure& Failure::operator=(const Failure& other)
  {
    if (this == &other)
    {
      return *this;
    }

    m_failureCode = other.m_failureCode;
    m_hResult = other.m_hResult;

    return *this;
  }
}
