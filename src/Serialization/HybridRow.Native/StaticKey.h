// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

namespace cdb_hr
{
  /// <summary>
  /// Describes a property or set of properties whose values MUST be the same for all rows that share the same partition key.
  /// </summary>
  class StaticKey final
  {
  public:
    StaticKey() noexcept : m_path{} {}
    StaticKey(std::string_view path) noexcept : m_path{path} {}
    ~StaticKey() noexcept = default;
    StaticKey(const StaticKey& other) = default;
    StaticKey(StaticKey&& other) noexcept = default;
    StaticKey& operator=(const StaticKey& other) = default;
    StaticKey& operator=(StaticKey&& other) noexcept = default;

    /// <summary>The logical path of the referenced property.</summary>
    /// <remarks>Static path MUST refer to properties defined within the same schema.</remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }
    void SetPath(std::string_view value) noexcept { m_path = value; }

  private:
    tla::string m_path;
  };
}
