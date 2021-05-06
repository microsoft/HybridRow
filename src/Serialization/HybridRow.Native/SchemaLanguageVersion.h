// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Versions of the HybridRow Schema Description Language.</summary>
  enum class SchemaLanguageVersion : unsigned char
  {
    /// <summary>Initial version of the HybridRow Schema Description Language.</summary>
    V1 = 0,

    /// <summary>Introduced Enums, Inheritance.</summary>
    V2 = 2,

    /// <summary>The latest version.</summary>
    Latest = V2,

    /// <summary>No version is specified.</summary>
    /// <remarks>
    /// When applied to a Namespace, unspecified will map to <see cref="Latest"/>.
    /// When applied to a Schema, unspecified will map to the version given in the namespace.
    /// </remarks>
    Unspecified = 255,
  };
}
