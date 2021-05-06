// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

namespace cdb_hr
{
  /// <summary>Describes a property or set of properties used to partition the data set across machines.</summary>
  class PartitionKey final
  {
  public:
    PartitionKey() noexcept = default;
    explicit PartitionKey(std::string_view path)  noexcept : m_path{path} {}
    ~PartitionKey() noexcept = default;
    PartitionKey(const PartitionKey& other) = default;
    PartitionKey(PartitionKey&& other) noexcept = default;
    PartitionKey& operator=(const PartitionKey& other) = default;
    PartitionKey& operator=(PartitionKey&& other) noexcept = default;

    /// <summary>The logical path of the referenced property.</summary>
    /// <remarks>Partition keys MUST refer to properties defined within the same schema.</remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }
    void SetPath(std::string_view path) noexcept { m_path = path; }

  private:
    tla::string m_path;
  };
}
