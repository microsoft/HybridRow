// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "Contract.h"
#include "Strings.h"

namespace cdb_core
{
  std::wstring Contract::MakeError(std::string_view api, std::string_view message)
  {
    if (message.empty())
    {
      return make_string(L"%.*S", api.size(), api.data());
    }
    return make_string(L"%.*S Failure: %.*S", api.size(), api.data(), message.size(), message.data());
  }

  [[noreturn]] void Contract::Fail(std::string_view api, std::string_view message)
  {
    std::wstring error = MakeError(api, message);
    _ASSERT_EXPR(false, error.c_str());
    std::terminate();
  }
}
