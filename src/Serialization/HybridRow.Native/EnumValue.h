// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>

namespace cdb_hr
{
  /// <summary>An enum schema describes a set of constrained integer values.</summary>
  class EnumValue final
  {
  public:
    /// <summary>Initializes a new instance of the <see cref="EnumValue" /> class.</summary>
    EnumValue() noexcept : m_comment{}, m_name{}, m_value{} {}

    /// <summary>An (optional) comment describing the purpose of this value.</summary>
    /// <remarks>Comments are for documentary purpose only and do not affect the enum at runtime.</remarks>
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view value) noexcept { m_comment = value; }

    /// <summary>The name of the enum value.</summary>
    /// <remarks>
    /// The name of a value MUST be unique within its EnumSchema.
    /// <para />
    /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
    /// underscores.
    /// </remarks>
    [[nodiscard]] std::string_view GetName() const noexcept { return m_name; }
    void SetName(std::string_view value) noexcept { m_name = value; }

    /// <summary>The numerical value of the enum value.</summary>
    [[nodiscard]] int64_t GetValue() const noexcept { return m_value; }
    void SetValue(int64_t value) noexcept { m_value = value; }

  private:
    tla::string m_comment;
    tla::string m_name;
    int64_t m_value;
  };
}
