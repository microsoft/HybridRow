// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include "AllowEmptyKind.h"
#include "PropertyType.h"

namespace cdb_hr
{
  /// <summary>Describes a single property definition.</summary>
  class Property final
  {
  public:
    Property() noexcept : m_comment{}, m_path{}, m_propertyType{}, m_apiname{}, m_allowEmpty{} {}
    ~Property() = default;
    Property(Property&) = delete;
    Property(Property&&) = delete;
    Property& operator=(const Property&) = delete;
    Property& operator=(Property&&) = delete;

    /// <summary>An (optional) comment describing the purpose of this property.</summary>
    /// <remarks>Comments are for documentary purpose only and do not affect the property at runtime.</remarks>
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view comment) noexcept { m_comment = comment; }

    /// <summary>The logical path of this property.</summary>
    /// <remarks>
    /// For complex properties (e.g. objects) the logical path forms a prefix to relative paths of
    /// properties defined within nested structures.
    /// <para />
    /// See the logical path specification for full details on both relative and absolute paths.
    /// </remarks>
    [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }
    void SetPath(std::string_view path) noexcept { m_path = path; }

    /// <summary>Api-specific name annotations for the property.</summary>
    [[nodiscard]] std::string_view GetApiName() const noexcept { return m_apiname; }
    void SetApiName(std::string_view value) noexcept { m_apiname = value; }

    /// <summary>The type of the property.</summary>
    /// <remarks>
    /// Types may be simple (e.g. int8) or complex (e.g. object).  Simple types always define a
    /// single column.  Complex types may define one or more columns depending on their structure.
    /// </remarks>
    [[nodiscard]] std::optional<std::reference_wrapper<const PropertyType>> GetPropertyType() const noexcept
    {
      return m_propertyType ? std::optional<std::reference_wrapper<const PropertyType>>{*m_propertyType} : std::nullopt;
    }

    void SetPropertyType(std::unique_ptr<PropertyType> propType) noexcept { m_propertyType = std::move(propType); }

    /// <summary>Empty canonicalization for this property.</summary>
    [[nodiscard]] AllowEmptyKind GetAllowEmpty() const noexcept { return m_allowEmpty; }
    void SetAllowEmpty(AllowEmptyKind value) noexcept { m_allowEmpty = value; }

  private:
    tla::string m_comment;
    tla::string m_path;
    std::unique_ptr<PropertyType> m_propertyType;
    tla::string m_apiname;
    AllowEmptyKind m_allowEmpty;
  };
}
