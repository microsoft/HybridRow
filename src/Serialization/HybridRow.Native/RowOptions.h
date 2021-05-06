// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
/// <summary>Describes the desired behavior when mutating a hybrid row.</summary>
enum class RowOptions
{
  None = 0,

  /// <summary>Overwrite an existing value.</summary>
  /// <remarks>
  /// An existing value is assumed to exist at the offset provided.  The existing value is
  /// replaced inline.  The remainder of the row is resized to accomodate either an increase or decrease
  /// in required space.
  /// </remarks>
  Update = 1,

  /// <summary>Insert a new value.</summary>
  /// <remarks>
  /// An existing value is assumed NOT to exist at the offset provided.  The new value is
  /// inserted immediately at the offset.  The remainder of the row is resized to accomodate either an
  /// increase or decrease in required space.
  /// </remarks>
  Insert = 2,

  /// <summary>Update an existing value or insert a new value, if no value exists.</summary>
  /// <remarks>
  /// If a value exists, then this operation becomes <see cref="Update" />, otherwise it
  /// becomes <see cref="Insert" />.
  /// </remarks>
  Upsert = 3,

  /// <summary>Insert a new value moving existing values to the right.</summary>
  /// <remarks>
  /// Within an array scope, inserts a new value immediately at the index moving all subsequent
  /// items to the right. In any other scope behaves the same as <see cref="Upsert" />.
  /// </remarks>
  InsertAt = 4,

  /// <summary>Delete an existing value.</summary>
  /// <remarks>
  /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
  /// a decrease in required space.  If no value exists this operation is a no-op.
  /// </remarks>
  Delete = 5,
};
}
