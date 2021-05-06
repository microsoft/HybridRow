// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include <vector>
#include <memory>
#include "TypeKind.h"
#include "EnumValue.h"

namespace cdb_hr
{
  /// <summary>An enum schema describes a set of constrained integer values.</summary>
  class EnumSchema final
  {
  public:
    /// <summary>Initializes a new instance of the <see cref="EnumSchema" /> class.</summary>
    EnumSchema() noexcept :
      m_comment{},
      m_name{},
      m_apitype{},
      m_type{TypeKind::Int32},
      m_values{} {}

    /// <summary>An (optional) comment describing the purpose of this enum.</summary>
    /// <remarks>Comments are for documentary purpose only and do not affect the enum at runtime.</remarks>
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view value) noexcept { m_comment = value; }

    /// <summary>The name of the enum.</summary>
    /// <remarks>
    /// The name of a enum MUST be unique within its namespace.
    /// <para />
    /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
    /// underscores.
    /// </remarks>
    [[nodiscard]] std::string_view GetName() const noexcept { return m_name; }
    void SetName(std::string_view value) noexcept { m_name = value; }

    /// <summary>Api-specific type annotations for the property.</summary>
    [[nodiscard]] std::string_view GetApiType() const noexcept { return m_apitype; }
    void SetApiType(std::string_view value) noexcept { m_apitype = value; }

    /// <summary>The logical base type of the enum.</summary>
    /// <remarks>This must be a primitive.</remarks>
    [[nodiscard]] TypeKind GetType() const noexcept { return m_type; }
    void SetType(TypeKind value) noexcept { m_type = value; }

    /// <summary>A list of zero or more value definitions.</summary>
    /// <remarks>This field is never null.</remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<EnumValue>>& GetValues() const noexcept { return m_values; }
    std::vector<std::unique_ptr<EnumValue>>& GetValues() noexcept { return m_values; }
    void SetValues(std::vector<std::unique_ptr<EnumValue>> value) noexcept { m_values = std::move(value); }

  private:
    tla::string m_comment;
    tla::string m_name;
    tla::string m_apitype;
    TypeKind m_type;

    /// <summary>A list of zero or more value definitions.</summary>
    std::vector<std::unique_ptr<EnumValue>> m_values;
  };
}
