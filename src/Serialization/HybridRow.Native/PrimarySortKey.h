// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include "SortDirection.h"

namespace cdb_hr
{
  /// <summary>
  /// Describes a property or set of properties used to order the data set within a single
  /// partition.
  /// </summary>
  class PrimarySortKey final
  {
  public:
    PrimarySortKey() noexcept : m_path{}, m_direction{SortDirection::Ascending} {}

    explicit PrimarySortKey(std::string_view path, SortDirection dir = SortDirection::Ascending) noexcept :
      m_path{path},
      m_direction{dir} {}

    ~PrimarySortKey() noexcept = default;
    PrimarySortKey(const PrimarySortKey& other) = default;
    PrimarySortKey(PrimarySortKey&& other) noexcept = default;
    PrimarySortKey& operator=(const PrimarySortKey& other) = default;
    PrimarySortKey& operator=(PrimarySortKey&& other) noexcept = default;

    /// <summary>The logical path of the referenced property.</summary>
      /// <remarks>Primary keys MUST refer to properties defined within the same schema.</remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }
    void SetPath(std::string_view value) noexcept { m_path = value; }

    /// <summary>The logical path of the referenced property.</summary>
    /// <remarks>Primary keys MUST refer to properties defined within the same schema.</remarks>
    [[nodiscard]]
    SortDirection GetDirection() const noexcept { return m_direction; }

    void SetDirection(SortDirection value) noexcept { m_direction = value; }

  private:
    tla::string m_path;
    SortDirection m_direction;
  };
}
