// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Describes the sort order direction.</summary>
  enum class SortDirection : unsigned char
  {
    /// <summary>Sorts from the lowest to the highest value.</summary>
    Ascending = 0,

    /// <summary>Sorts from the highest to the lowest value.</summary>
    Descending,
  };
}
