// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Describes the empty canonicalization for properties.</summary>
  enum class AllowEmptyKind : unsigned char
  {
    /// <summary>Empty and null are treated as distinct.</summary>
    None = 0,

    /// <summary>Empty values are converted to null when written.</summary>
    EmptyAsNull = 1,

    /// <summary>Null values are converted to empty when read.</summary>
    NullAsEmpty = 2,

    /// <summary>
    /// Empty values are converted to null when written, and null values are converted to empty
    /// when read.
    /// </summary>
    Both = EmptyAsNull | NullAsEmpty,
  };
}
