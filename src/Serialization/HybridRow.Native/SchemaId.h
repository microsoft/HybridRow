// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>The unique identifier for a schema.</summary>
  /// <remarks>Identifiers must be unique within the scope of the database in which they are used.</remarks>
  struct SchemaId final
  {
    constexpr static uint32_t Size = sizeof(int32_t);
    constexpr static SchemaId Invalid() { return SchemaId(); }

    /// <summary>Initializes a new instance of the <see cref="SchemaId" /> struct.</summary>
    constexpr SchemaId() noexcept : m_id{0} {}
    ~SchemaId() noexcept = default;
    constexpr SchemaId(const SchemaId& other) noexcept = default;
    constexpr SchemaId(SchemaId&& other) noexcept = default;
    SchemaId& operator=(const SchemaId& other) noexcept = default;
    SchemaId& operator=(SchemaId&& other) noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="SchemaId" /> struct.</summary>
    /// <param name="id">The underlying globally unique identifier of the schema.</param>
    explicit constexpr SchemaId(int32_t id) noexcept : m_id(id) { }

    /// <summary>The underlying identifier.</summary>
    [[nodiscard]] constexpr int32_t Id() const noexcept { return m_id; }

    /// <summary>Integer conversion operator.</summary>
    explicit constexpr operator int32_t() const { return m_id; }

    /// <summary>Operator == overload.</summary>
    friend bool operator==(const SchemaId& lhs, const SchemaId& rhs) noexcept { return lhs.m_id == rhs.m_id; }

    /// <summary>Operator != overload.</summary>
    friend bool operator!=(const SchemaId& lhs, const SchemaId& rhs) noexcept { return !(lhs == rhs); }

    [[nodiscard]] std::string ToString() const noexcept
    {
      return cdb_core::make_string("%d", m_id);
    }

    [[nodiscard]] size_t GetHashCode() const noexcept
    {
      return std::hash<std::int32_t>{}(m_id);
    }

  private:
    friend struct std::hash<SchemaId>;

    /// <summary>The underlying identifier.</summary>
    int32_t m_id;
  };

  // Declare constraints.
  static_assert(cdb_core::is_blittable_v<SchemaId>);
  static_assert(cdb_core::is_hashable_v<SchemaId>);
  static_assert(cdb_core::is_stringable_v<SchemaId>);
}

namespace std
{
  template<>
  struct hash<cdb_hr::SchemaId>
  {
    std::size_t operator()(cdb_hr::SchemaId const& s) const noexcept
    {
      return s.GetHashCode();
    }
  };
}
