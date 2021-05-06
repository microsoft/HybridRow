// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>Describes the set of options that apply to the entire schema and the way it is validated.</summary>
  class SchemaOptions final
  {
  public:
    constexpr SchemaOptions() noexcept :
      m_disallowUnschematized{false},
      m_enablePropertyLevelTimestamp{false},
      m_disableSystemPrefix{false},
      m_abstract{false} {}

    ~SchemaOptions() noexcept = default;
    SchemaOptions(const SchemaOptions& other) = delete;
    SchemaOptions(SchemaOptions&& other) = delete;
    SchemaOptions& operator=(const SchemaOptions& other) = delete;
    SchemaOptions& operator=(SchemaOptions&& other) = delete;

    /// <summary>If true then structural schema validation is enabled.</summary>
    /// <remarks>
    /// When structural schema validation is enabled then attempting to store an unschematized
    /// path in the row, or a value whose type does not conform to the type constraints defined for that
    /// path within the schema will lead to a schema validation error. When structural schema validation is
    /// NOT enabled, then storing an unschematized path or non-confirming value will lead to a sparse
    /// column override of the path.  The value will be stored (and any existing value at that path will be
    /// overwritten).  No error will be given.
    /// </remarks>
    [[nodiscard]] bool GetDisallowUnschematized() const noexcept { return m_disallowUnschematized; }
    void SetDisallowUnschematized(bool value) noexcept { m_disallowUnschematized = value; }

    /// <summary>
    /// If set and has the value true, then triggers behavior in the Schema that acts based on
    /// property level timestamps. In Cassandra, this means that new columns are added for each top level
    /// property that has values of the client side timestamp. This is then used in conflict resolution to
    /// independently resolve each property based on the timestamp value of that property.
    /// </summary>
    [[nodiscard]] bool GetEnablePropertyLevelTimestamp() const noexcept { return m_enablePropertyLevelTimestamp; }
    void SetEnablePropertyLevelTimestamp(bool value) noexcept { m_enablePropertyLevelTimestamp = value; }

    /// <summary>
    /// If the is value true, then disables prefixing the system properties with a prefix __sys_
    /// for reserved properties owned by the store layer.
    /// </summary>
    [[nodiscard]] bool GetDisableSystemPrefix() const noexcept { return m_disableSystemPrefix; }
    void SetDisableSystemPrefix(bool value) noexcept { m_disableSystemPrefix = value; }

    /// <summary>If true then instances of this schema cannot be created directly, only through subtypes.</summary>
    [[nodiscard]] bool GetAbstract() const noexcept { return m_abstract; }
    void SetAbstract(bool value) noexcept { m_abstract = value; }

  private:
    bool m_disallowUnschematized;
    bool m_enablePropertyLevelTimestamp;
    bool m_disableSystemPrefix;
    bool m_abstract;
  };
}
