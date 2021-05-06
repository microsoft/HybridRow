// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <algorithm>
#include <vector>
#include "Blittable.h"

namespace cdb_core
{
  template<typename T> struct Span;

  /// <summary>A readonly, bounds-checked, pointer to an buffer.</summary>
  /// <remarks></remarks>
  template<class T>
  struct ReadOnlySpan
  {
    static_assert(is_blittable_v<T>, "Only blittable types can be used in a ReadOnlySpan<T>.");

    constexpr ReadOnlySpan() noexcept;
    ~ReadOnlySpan() noexcept = default;
    constexpr ReadOnlySpan(const T* pointer, uint32_t length) noexcept;
    template<size_t N> constexpr ReadOnlySpan(const std::array<T, N>&) noexcept;
    constexpr ReadOnlySpan(const std::vector<T>&) noexcept;
    constexpr ReadOnlySpan(const Span<T>&) noexcept;
    ReadOnlySpan(const ReadOnlySpan<T>&) noexcept = default;
    ReadOnlySpan(ReadOnlySpan<T>&&) noexcept = default;
    ReadOnlySpan<T>& operator=(const ReadOnlySpan<T>& other) noexcept = default;
    ReadOnlySpan<T>& operator=(ReadOnlySpan<T>&& other) noexcept = default;

    /// <summary>Returns a reference to specified element of the ReadOnlySpan.</summary>
    constexpr const T& operator[](uint32_t index) const noexcept;

    /// <summary>The number of items in the span.</summary>
    [[nodiscard]]
    constexpr uint32_t Length() const noexcept;

    /// <summary>Returns true if Length is 0.</summary>
    [[nodiscard]]
    constexpr bool IsEmpty() const noexcept;

    bool operator==(const ReadOnlySpan<T>& rhs) const noexcept;
    bool operator!=(const ReadOnlySpan<T>& rhs) const noexcept;

    [[nodiscard]]
    ReadOnlySpan<T> Slice(uint32_t start) const noexcept;

    [[nodiscard]]
    ReadOnlySpan<T> Slice(uint32_t start, uint32_t length) const noexcept;

    void CopyTo(Span<T> destination) const noexcept;

    template<typename = std::enable_if_t<std::is_same_v<std::byte, T>>>
    [[nodiscard]] constexpr int SequenceCompareTo(ReadOnlySpan<T> other) const noexcept;

    template<typename = std::enable_if_t<std::is_same_v<std::byte, T>>>
    [[nodiscard]] constexpr bool SequenceEqual(ReadOnlySpan<T> other) const noexcept;

    struct ReadOnlySpanIter
    {
      bool operator==(const ReadOnlySpanIter& other) const noexcept { return m_pointer == other.m_pointer; }
      bool operator!=(const ReadOnlySpanIter& other) const noexcept { return m_pointer != other.m_pointer; }

      const ReadOnlySpanIter& operator++() noexcept
      {
        ++m_pointer;
        return *this;
      }

      const T& operator*() const noexcept { return *m_pointer; }
    private:
      explicit ReadOnlySpanIter(const T* p) : m_pointer(p) {}
      friend struct ReadOnlySpan<T>;
      const T* m_pointer;
    };

    [[nodiscard]] ReadOnlySpanIter begin() const noexcept { return ReadOnlySpanIter{m_pointer}; }
    [[nodiscard]] ReadOnlySpanIter end() const noexcept { return ReadOnlySpanIter{m_pointer + m_length}; }

  private:
    friend struct MemoryMarshal;
    template<typename U> friend struct Span;

    const T* m_pointer;
    uint32_t m_length;
  };

  template<class T> constexpr ReadOnlySpan<T>::ReadOnlySpan() noexcept : m_pointer(nullptr), m_length(0) {}

  template<class T> constexpr ReadOnlySpan<T>::ReadOnlySpan(const T* pointer, uint32_t length) noexcept :
    m_pointer(pointer),
    m_length(length) { }

  template<class T> template<size_t N> constexpr ReadOnlySpan<T>::ReadOnlySpan(const std::array<T, N>& arr) noexcept :
    m_pointer{arr.data()},
    m_length{static_cast<uint32_t>(arr.size())} { }

  // Class Template Argument Deduction (CTAD) hint
  template<typename T, size_t N> ReadOnlySpan(std::array<T, N>&) noexcept -> ReadOnlySpan<T>;

  template<class T> constexpr ReadOnlySpan<T>::ReadOnlySpan(const std::vector<T>& arr) noexcept :
    m_pointer{arr.data()},
    m_length{static_cast<uint32_t>(arr.size())} { }

  // Class Template Argument Deduction (CTAD) hint
  template<typename T> ReadOnlySpan(const std::vector<T>& arr) noexcept -> ReadOnlySpan<T>;

  template<class T> constexpr ReadOnlySpan<T>::ReadOnlySpan(const Span<T>& span) noexcept :
    m_pointer(span.m_pointer),
    m_length(span.m_length) { }

  // Class Template Argument Deduction (CTAD) hint
  template<typename T> ReadOnlySpan(const Span<T>& span) noexcept -> ReadOnlySpan<T>;

  template<class T> constexpr const T& ReadOnlySpan<T>::operator[](uint32_t index) const noexcept
  {
    Contract::Assert(index < m_length);
    return m_pointer[index];
  }

  template<class T> constexpr uint32_t ReadOnlySpan<T>::Length() const noexcept { return m_length; }

  template<class T> constexpr bool ReadOnlySpan<T>::IsEmpty() const noexcept { return m_length == 0; }

  template<typename T>
  bool ReadOnlySpan<T>::operator==(const ReadOnlySpan<T>& rhs) const noexcept
  {
    return m_pointer == rhs.m_pointer && m_length == rhs.m_length;
  }

  template<typename T>
  bool ReadOnlySpan<T>::operator!=(const ReadOnlySpan<T>& rhs) const noexcept { return !operator==(rhs); }

  template<typename T>
  ReadOnlySpan<T> ReadOnlySpan<T>::Slice(uint32_t start, uint32_t length) const noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return ReadOnlySpan<T>(m_pointer + start, length);
  }

  template<typename T>
  ReadOnlySpan<T> ReadOnlySpan<T>::Slice(uint32_t start) const noexcept
  {
    Contract::Requires(start <= m_length);
    return ReadOnlySpan<T>(m_pointer + start, m_length - start);
  }

  template<typename T> void ReadOnlySpan<T>::CopyTo(Span<T> destination) const noexcept
  {
    Contract::Requires(m_length <= destination.m_length);
    ::memmove_s(static_cast<void*>(destination.m_pointer), destination.m_length * sizeof(T),
      static_cast<void*>(const_cast<T*>(m_pointer)), m_length * sizeof(T));
  }

  template<typename T>
  template<typename>
  constexpr int ReadOnlySpan<T>::SequenceCompareTo(ReadOnlySpan<T> other) const noexcept
  {
    uint32_t len = std::min<uint32_t>(m_length, other.m_length);
    int cmp = ::memcmp(m_pointer, other.m_pointer, len * sizeof(T));
    return (cmp == 0) ? static_cast<int>(m_length - other.m_length) : cmp;
  }

  template<typename T>
  template<typename>
  constexpr bool ReadOnlySpan<T>::SequenceEqual(ReadOnlySpan<T> other) const noexcept
  {
    return m_length == other.m_length && 
           ::memcmp(m_pointer, other.m_pointer, m_length * sizeof(T)) == 0;
  }
}
