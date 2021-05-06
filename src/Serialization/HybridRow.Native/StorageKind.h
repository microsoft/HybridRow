// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Describes the storage placement for primitive properties.</summary>
  enum class StorageKind : unsigned char
  {
    /// <summary>The property defines a sparse column.</summary>
    /// <remarks>
    /// Columns marked <see cref="Sparse" /> consume no space in the row when not present.  When
    /// present they appear in an unordered linked list at the end of the row.  Access time for
    /// <see cref="Sparse" /> columns is proportional to the number of <see cref="Sparse" /> columns in the
    /// row.
    /// </remarks>
    Sparse = 0,

    /// <summary>The property is a fixed-length, space-reserved column.</summary>
    /// <remarks>
    /// The column will consume 1 null-bit, and its byte-width regardless of whether the value is
    /// present in the row.
    /// </remarks>
    Fixed,

    /// <summary>The property is a variable-length column.</summary>
    /// <remarks>
    /// The column will consume 1 null-bit regardless of whether the value is present. When the value is
    /// present it will also consume a variable number of bytes to encode the length preceding the actual
    /// value.
    /// <para>
    /// When a <em>long</em> value is marked <see cref="Variable" /> then a null-bit is reserved and
    /// the value is optionally encoded as <see cref="Variable" /> if small enough to fit, otherwise the
    /// null-bit is set and the value is encoded as <see cref="Sparse" />.
    /// </para>
    /// </remarks>
    Variable,
  };
}
