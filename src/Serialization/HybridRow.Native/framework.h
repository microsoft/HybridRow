// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <cstdint>
#include <cstddef>
#include <functional>
#include <unordered_map>
#include <tuple>
#include <utility>
#include <shared_mutex>
#include <optional>
#include <functional>
using std::byte;

#include "../../Core/Core.Native/Core.Native.h"

typedef float_t float32_t;
typedef double_t float64_t;
#include "Float128.h"
typedef cdb_hr::Float128 float128_t;
#include "Decimal.h"
typedef cdb_hr::Decimal decimal_t;
#include "DateTime.h"
#include "UnixDateTime.h"
#include "Guid.h"
#include "MongoDbObjectId.h"
#include "NullValue.h"

