// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

namespace cdb_hr_test
{
  class LayoutCompilerUnitTests;
}

namespace cdb_hr
{
  class LayoutBuilder;

  struct LayoutBit final
  {
    /// <summary>The number of bits in a single byte on the current architecture.</summary>
    constexpr static uint32_t BitsPerByte = 8;

    /// <summary>The empty bit.</summary>
    constexpr static LayoutBit Invalid();

    ~LayoutBit() noexcept = default;
    LayoutBit(const LayoutBit& other) noexcept = default;
    LayoutBit(LayoutBit&& other) noexcept = default;
    LayoutBit& operator=(const LayoutBit& other) noexcept = default;
    LayoutBit& operator=(LayoutBit&& other) noexcept = default;
    bool operator==(const LayoutBit& rhs) const noexcept;
    bool operator!=(const LayoutBit& rhs) const noexcept;

    /// <summary>True if the default empty value, false otherwise.</summary>
    [[nodiscard]] bool IsInvalid() const noexcept;

    /// <summary>The 0-based offset into the layout bitmask.</summary>
    [[nodiscard]] uint32_t GetIndex() const noexcept;

    /// <summary>
    /// Returns the 0-based byte offset from the beginning of the row or scope that contains the
    /// bit from the bitmask.
    /// </summary>
    /// <remarks>Also see <see cref="GetBit" /> to identify.</remarks>
    /// <param name="offset">The byte offset from the beginning of the row where the scope begins.</param>
    /// <returns>The byte offset containing this bit.</returns>
    [[nodiscard]] uint32_t GetOffset(uint32_t offset) const noexcept;

    /// <summary>Returns the 0-based bit from the beginning of the byte that contains this bit.</summary>
    /// <remarks>Also see <see cref="GetOffset" /> to identify relevant byte.</remarks>
    /// <returns>The bit of the byte within the bitmask.</returns>
    [[nodiscard]] uint32_t GetBit() const noexcept;

    [[nodiscard]] size_t GetHashCode() const noexcept;

  private:
    friend class LayoutBuilder;
    friend class cdb_hr_test::LayoutCompilerUnitTests;

    /// <summary>Compute the division rounding up to the next whole number.</summary>
    /// <param name="numerator">The numerator to divide.</param>
    /// <param name="divisor">The divisor to divide by.</param>
    /// <returns>The ceiling(numerator/divisor).</returns>
    constexpr static uint32_t DivCeiling(uint32_t numerator, uint32_t divisor);

    /// <summary>Allocates layout bits from a bitmask.</summary>
    struct Allocator final
    {
      /// <summary>Initializes a new instance of the <see cref="Allocator" /> class.</summary>
      Allocator() noexcept : m_next{0} { }
      ~Allocator() noexcept = default;
      Allocator(const Allocator& other) noexcept = default;
      Allocator(Allocator&& other) noexcept = default;
      Allocator& operator=(const Allocator& other) noexcept = default;
      Allocator& operator=(Allocator&& other) noexcept = default;

      /// <summary>The number of bytes needed to hold all bits so far allocated.</summary>
      [[nodiscard]] uint32_t GetNumBytes() const noexcept;

      /// <summary>Allocates a new bit from the bitmask.</summary>
      /// <returns>The allocated bit.</returns>
      LayoutBit Allocate() noexcept;

    private:
      /// <summary>The next bit to allocate.</summary>
      uint32_t m_next;
    };

    /// <summary>Initializes a new instance of the <see cref="LayoutBit" /> struct.</summary>
    /// <param name="index">The 0-based offset into the layout bitmask.</param>
    explicit constexpr LayoutBit(uint32_t index) noexcept : m_index{index} { }

    /// <summary>The 0-based offset into the layout bitmask.</summary>
    uint32_t m_index;
  };

  constexpr LayoutBit LayoutBit::Invalid() { return LayoutBit(static_cast<uint32_t>(-1)); }
  inline bool LayoutBit::operator==(const LayoutBit& rhs) const noexcept { return m_index == rhs.m_index; }
  inline bool LayoutBit::operator!=(const LayoutBit& rhs) const noexcept { return !operator==(rhs); }
  inline bool LayoutBit::IsInvalid() const noexcept { return m_index == static_cast<uint32_t>(-1); }
  inline uint32_t LayoutBit::GetIndex() const noexcept { return m_index; }

  inline uint32_t LayoutBit::GetOffset(uint32_t offset) const noexcept
  {
    return offset + (m_index / BitsPerByte);
  }

  inline uint32_t LayoutBit::GetBit() const noexcept { return m_index % BitsPerByte; }

  inline size_t LayoutBit::GetHashCode() const noexcept
  {
    static_assert(cdb_core::is_hashable_v<decltype(this)>);

    return std::hash<std::int32_t>{}(static_cast<int32_t>(m_index));
  }

  inline uint32_t LayoutBit::Allocator::GetNumBytes() const noexcept
  {
    return DivCeiling(m_next, BitsPerByte);
  }

  inline LayoutBit LayoutBit::Allocator::Allocate() noexcept { return LayoutBit(m_next++); }

  constexpr uint32_t LayoutBit::DivCeiling(uint32_t numerator, uint32_t divisor)
  {
    return (numerator + (divisor - 1)) / divisor;
  }
}
