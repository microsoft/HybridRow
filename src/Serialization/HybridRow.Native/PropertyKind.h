// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Describes the logical kind of a property.</summary>
  enum class PropertyKind : unsigned char
  {
    /// <summary>Reserved.</summary>
    Invalid = 0,

    Primitive,
    Object,
    Array,
    Map,
    Set,
    Tagged,
    Tuple,
    Udt,
  };
}
