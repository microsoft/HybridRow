// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "LayoutCode.h"

namespace cdb_hr
{
struct LayoutCodeTraits final
{
  /// <summary>Returns the same scope code without the immutable bit set.</summary>
  /// <param name="code">The scope type code.</param>
  static LayoutCode ClearImmutableBit(LayoutCode code) noexcept
  {
    return static_cast<LayoutCode>(static_cast<unsigned char>(code) & 0xFE);
  }

  /// <summary>
  /// Returns true if the type code indicates that, even within a typed scope, this element type
  /// always requires a type code (because the value itself is in the type code).
  /// </summary>
  /// <param name="code">The element type code.</param>
  static bool AlwaysRequiresTypeCode(LayoutCode code) noexcept
  {
    return (code == LayoutCode::Boolean) ||
      (code == LayoutCode::BooleanFalse) ||
      (code == LayoutCode::Null);
  }

  /// <summary>Returns a canonicalized version of the layout code.</summary>
  /// <remarks>
  /// Some codes (e.g. <see cref="LayoutCode::Boolean" /> use multiple type codes to also encode
  /// values.  This function converts actual value based code into the canonicalized type code for schema
  /// comparisons.
  /// </remarks>
  /// <param name="code">The code to canonicalize.</param>
  static LayoutCode Canonicalize(LayoutCode code) noexcept
  {
    return (code == LayoutCode::BooleanFalse) ? LayoutCode::Boolean : code;
  }
};
}
