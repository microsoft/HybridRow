// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr
{
  /// <summary>A 12-byte MongoDB Object Identifier (in big-endian byte order).</summary>
  #pragma pack(push, 1)
  struct MongoDbObjectId final
  {
    /// <summary>The size (in bytes) of a MongoObjectId.</summary>
    constexpr static int Size = 12;

    [[nodiscard]] constexpr MongoDbObjectId() noexcept : m_data{{0, 0}} {}
    ~MongoDbObjectId() noexcept = default;
    MongoDbObjectId(const MongoDbObjectId& other) noexcept = default;
    MongoDbObjectId(MongoDbObjectId&& other) noexcept = default;
    MongoDbObjectId& operator=(const MongoDbObjectId& other) noexcept = default;
    MongoDbObjectId& operator=(MongoDbObjectId&& other) noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="MongoDbObjectId" /> struct.</summary>
    /// <param name="high">the high-order 32-bits.</param>
    /// <param name="low">the low-order 64-bits.</param>
    constexpr MongoDbObjectId(uint32_t high, uint64_t low) noexcept;

    /// <summary>Initializes a new instance of the <see cref="MongoDbObjectId" /> struct.</summary>
    /// <param name="src">the bytes of the object id in big-endian order.</param>
    MongoDbObjectId(const cdb_core::Span<byte>& src) noexcept;

    friend bool operator==(const MongoDbObjectId& left, const MongoDbObjectId& right) noexcept;
    friend bool operator!=(const MongoDbObjectId& left, const MongoDbObjectId& right) noexcept;

    /// <summary>Returns true if this is the same value as <paramref name="other" />.</summary>
    /// <param name="other">The value to compare against.</param>
    /// <returns>True if the two values are the same.</returns>
    [[nodiscard]]
    bool Equals(const MongoDbObjectId& other) const noexcept;

    /// <summary>Returns a hash code.</summary>
    [[nodiscard]]
    size_t GetHashCode() const noexcept;

    /// <summary>Returns the bytes of the object id as a byte array in big-endian order.</summary>
    [[nodiscard]]
    cdb_core::Memory<byte> ToByteArray() const noexcept;

    /// <summary>Copies the bytes of the object id to the provided buffer.</summary>
    /// <param name="dest">A buffer to receive the bytes in big-endian order.</param>
    /// <remarks>
    /// Required: The buffer must be able to accomodate the full object id (see
    /// <see cref="Size" />) at the offset indicated.
    /// </remarks>
    void CopyTo(cdb_core::Span<byte> dest) const noexcept;

  private:
    friend std::hash<MongoDbObjectId>;

    constexpr static uint32_t SwapByteOrder(uint32_t value);

    // reverse byte order (64-bit)
    constexpr static uint64_t SwapByteOrder(uint64_t value);

    /// <summary>The object id bytes inlined.</summary>
    union
    {
      struct
      {
        uint32_t High;
        uint64_t Low;
      };

      byte Data[MongoDbObjectId::Size];
    } m_data;
  };
  #pragma pack(pop)

  constexpr MongoDbObjectId::MongoDbObjectId(uint32_t high, uint64_t low) noexcept :
    m_data{{MongoDbObjectId::SwapByteOrder(high), MongoDbObjectId::SwapByteOrder(low)}}
  {
    static_assert(cdb_core::Endian::IsLittleEndian());
  }

  /// <summary>Initializes a new instance of the <see cref="MongoDbObjectId" /> struct.</summary>
  /// <param name="src">the bytes of the object id in big-endian order.</param>
  inline MongoDbObjectId::MongoDbObjectId(const cdb_core::Span<byte>& src) noexcept : m_data{}
  {
    cdb_core::Contract::Requires(src.Length() == MongoDbObjectId::Size);

    const byte* q = &src[0];
    m_data.High = *reinterpret_cast<const uint32_t*>(&q[0]);
    m_data.Low = *reinterpret_cast<const uint64_t*>(&q[4]);
  }

  /// <summary>Operator == overload.</summary>
  inline bool operator==(const MongoDbObjectId& left, const MongoDbObjectId& right) noexcept
  {
    return left.Equals(right);
  }

  /// <summary>Operator != overload.</summary>
  inline bool operator!=(const MongoDbObjectId& left, const MongoDbObjectId& right) noexcept
  {
    return !left.Equals(right);
  }

  inline bool MongoDbObjectId::Equals(const MongoDbObjectId& other) const noexcept
  {
    return m_data.High == other.m_data.High && m_data.Low == other.m_data.Low;
  }

  inline size_t MongoDbObjectId::GetHashCode() const noexcept
  {
    size_t hashCode = 0;
    const byte* p = m_data.Data;
    hashCode = (hashCode * 397) ^ *reinterpret_cast<const int32_t*>(&p[0]);
    hashCode = (hashCode * 397) ^ *reinterpret_cast<const int32_t*>(&p[4]);
    hashCode = (hashCode * 397) ^ *reinterpret_cast<const int32_t*>(&p[8]);
    return hashCode;
  }

  inline cdb_core::Memory<byte> MongoDbObjectId::ToByteArray() const noexcept
  {
    cdb_core::Memory<byte> bytes{MongoDbObjectId::Size};
    CopyTo(bytes.AsSpan());
    return bytes;
  }

  inline void MongoDbObjectId::CopyTo(cdb_core::Span<byte> dest) const noexcept
  {
    cdb_core::Contract::Requires(dest.Length() == MongoDbObjectId::Size);
    const cdb_core::Span<byte> source = cdb_core::Span<byte>(const_cast<byte*>(m_data.Data), MongoDbObjectId::Size);
    source.CopyTo(dest);
  }

  constexpr uint32_t MongoDbObjectId::SwapByteOrder(uint32_t value)
  {
    return ((value & 0x000000FFU) << 24) |
      ((value & 0x0000FF00U) << 8) |
      ((value & 0x00FF0000U) >> 8) |
      ((value & 0xFF000000U) >> 24);
  }

  constexpr uint64_t MongoDbObjectId::SwapByteOrder(uint64_t value)
  {
    return ((value & 0x00000000000000FFUL) << 56) |
      ((value & 0x000000000000FF00UL) << 40) |
      ((value & 0x0000000000FF0000UL) << 24) |
      ((value & 0x00000000FF000000UL) << 8) |
      ((value & 0x000000FF00000000UL) >> 8) |
      ((value & 0x0000FF0000000000UL) >> 24) |
      ((value & 0x00FF000000000000UL) >> 40) |
      ((value & 0xFF00000000000000UL) >> 56);
  }
}

namespace std
{
  template<>
  struct hash<cdb_hr::MongoDbObjectId>
  {
    constexpr std::size_t operator()(cdb_hr::MongoDbObjectId const& s) const noexcept
    {
      return cdb_core::HashCode::Combine(s.m_data.Low, s.m_data.High);
    }
  };
}
