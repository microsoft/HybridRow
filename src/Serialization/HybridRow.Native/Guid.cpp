// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Guid.h"
#include "winerror.h"

namespace rpc
{
  #include <rpc.h>
}

namespace cdb_hr
{
  Guid Guid::NewGuid() noexcept
  {
    Guid retval;
    rpc::RPC_STATUS status = rpc::UuidCreate(&retval.m_data);
    cdb_core::Contract::Requires(status == S_OK);
    return retval;
  }
}
