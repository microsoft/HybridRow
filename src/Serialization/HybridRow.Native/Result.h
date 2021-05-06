// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
enum class Result
{
  Success = 0,
  Failure = 1,
  NotFound = 2,
  Exists = 3,
  TooBig = 4,

  /// <summary>
  /// The type of an existing field does not match the expected type for this operation.
  /// </summary>
  TypeMismatch = 5,

  /// <summary>
  /// An attempt to write in a read-only scope.
  /// </summary>
  InsufficientPermissions = 6,

  /// <summary>
  /// An attempt to write a field that did not match its (optional) type constraints.
  /// </summary>
  TypeConstraint = 7,

  /// <summary>
  /// The byte sequence could not be parsed as a valid row.
  /// </summary>
  InvalidRow = 8,

  /// <summary>
  /// The byte sequence was too short for the requested action.
  /// </summary>
  InsufficientBuffer = 9,

  /// <summary>
  /// The operation was cancelled.
  /// </summary>
  Canceled = 10,
};
}
