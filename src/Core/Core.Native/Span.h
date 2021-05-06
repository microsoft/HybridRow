// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include <algorithm>
#include "Blittable.h"

namespace cdb_core
{
  template<typename T> struct ReadOnlySpan;

  /// <summary>A bounds-checked, pointer to an buffer.</summary>
  /// <remarks></remarks>
  template<class T>
  struct Span
  {
    static_assert(is_blittable_v<T>, "Only blittable types can be used in a Span<T>.");

    constexpr Span() noexcept;
    ~Span() noexcept = default;
    constexpr Span(T* pointer, uint32_t length) noexcept;
    template<size_t N> constexpr Span(std::array<T, N>&) noexcept;
    constexpr Span(std::vector<T>&) noexcept;
    Span(const Span<T>&) noexcept = default;
    Span(Span<T>&&) noexcept = default;
    Span<T>& operator=(const Span<T>& other) noexcept = default;
    Span<T>& operator=(Span<T>&& other) noexcept = default;

    /// <summary>Returns a reference to specified element of the Span.</summary>
    const T& operator[](uint32_t index) const noexcept;
    T& operator[](uint32_t index) noexcept;

    /// <summary>The number of items in the span.</summary>
    [[nodiscard]] uint32_t Length() const noexcept;

    /// <summary>Returns true if Length is 0.</summary>
    [[nodiscard]] bool IsEmpty() const noexcept;

    bool operator==(const Span<T>& rhs) const noexcept;
    bool operator!=(const Span<T>& rhs) const noexcept;

    [[nodiscard]] ReadOnlySpan<T> Slice(uint32_t start) const noexcept;
    [[nodiscard]] Span<T> Slice(uint32_t start) noexcept;

    [[nodiscard]] ReadOnlySpan<T> Slice(uint32_t start, uint32_t length) const noexcept;
    [[nodiscard]] Span<T> Slice(uint32_t start, uint32_t length) noexcept;

    void CopyTo(Span<T> destination) const noexcept;

    template<typename = std::enable_if_t<std::is_same_v<std::byte, T>>>
    void Fill(std::byte value) noexcept;

    template<typename = std::enable_if_t<std::is_same_v<std::byte, T>>>
    [[nodiscard]] constexpr int SequenceCompareTo(ReadOnlySpan<T> other) const noexcept;

    template<typename = std::enable_if_t<std::is_same_v<std::byte, T>>>
    [[nodiscard]] constexpr bool SequenceEqual(ReadOnlySpan<T> other) const noexcept;

    struct SpanIter
    {
      bool operator==(const SpanIter& other) const noexcept { return m_pointer == other.m_pointer; }
      bool operator!=(const SpanIter& other) const noexcept { return m_pointer != other.m_pointer; }

      const SpanIter& operator++() noexcept
      {
        ++m_pointer;
        return *this;
      }

      T& operator*() const noexcept { return *m_pointer; }
    private:
      explicit SpanIter(T* p) : m_pointer(p) {}
      friend struct Span<T>;
      T* m_pointer;
    };

    [[nodiscard]] SpanIter begin() const noexcept { return SpanIter{m_pointer}; }
    [[nodiscard]] SpanIter end() const noexcept { return SpanIter{m_pointer + m_length}; }

  private:
    friend struct MemoryMarshal;
    template<typename U> friend struct ReadOnlySpan;

    T* m_pointer;
    uint32_t m_length;
  };

  template<class T> constexpr Span<T>::Span() noexcept : m_pointer(nullptr), m_length(0) {}

  template<class T> 
  constexpr Span<T>::Span(T* pointer, uint32_t length) noexcept : m_pointer(pointer), m_length(length) { }

  template<class T> template<size_t N> constexpr Span<T>::Span(std::array<T, N>& arr) noexcept :
    m_pointer{arr.data()},
    m_length{static_cast<uint32_t>(arr.size())} { }

  // Class Template Argument Deduction (CTAD) hint
  template<typename T, size_t N> Span(std::array<T, N>&) noexcept -> Span<T>;

  template<class T> constexpr Span<T>::Span(std::vector<T>& arr) noexcept :
    m_pointer{arr.data()},
    m_length{static_cast<uint32_t>(arr.size())} { }

  // Class Template Argument Deduction (CTAD) hint
  template<typename T, size_t N> Span(std::vector<T>&) noexcept -> Span<T>;

  template<class T> const T& Span<T>::operator[](uint32_t index) const noexcept
  {
    Contract::Assert(index < m_length);
    return m_pointer[index];
  }

  template<class T> T& Span<T>::operator[](uint32_t index) noexcept
  {
    Contract::Assert(index < m_length);
    return m_pointer[index];
  }

  template<class T> uint32_t Span<T>::Length() const noexcept { return m_length; }

  template<class T> bool Span<T>::IsEmpty() const noexcept { return m_length == 0; }

  template<typename T>
  bool Span<T>::operator==(const Span<T>& rhs) const noexcept
  {
    return m_pointer == rhs.m_pointer && m_length == rhs.m_length;
  }

  template<typename T>
  bool Span<T>::operator!=(const Span<T>& rhs) const noexcept { return !operator==(rhs); }

  template<typename T>
  ReadOnlySpan<T> Span<T>::Slice(uint32_t start, uint32_t length) const noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return Span<T>(m_pointer + start, length);
  }

  template<typename T>
  Span<T> Span<T>::Slice(uint32_t start, uint32_t length) noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return Span<T>(m_pointer + start, length);
  }

  template<typename T>
  ReadOnlySpan<T> Span<T>::Slice(uint32_t start) const noexcept
  {
    Contract::Requires(start <= m_length);
    return ReadOnlySpan<T>(m_pointer + start, m_length - start);
  }

  template<typename T>
  Span<T> Span<T>::Slice(uint32_t start) noexcept
  {
    Contract::Requires(start <= m_length);
    return Span<T>(m_pointer + start, m_length - start);
  }

  template<typename T> void Span<T>::CopyTo(Span<T> destination) const noexcept
  {
    Contract::Requires(m_length <= destination.m_length);
    ::memmove_s(static_cast<void*>(destination.m_pointer), destination.m_length * sizeof(T),
      static_cast<void*>(m_pointer), m_length * sizeof(T));
  }

  template<typename T>
  template<typename>
  void Span<T>::Fill(std::byte value) noexcept
  {
    ::memset(m_pointer, static_cast<int>(value), m_length * sizeof(T));
  }

  template<typename T>
  template<typename>
  constexpr int Span<T>::SequenceCompareTo(ReadOnlySpan<T> other) const noexcept
  {
    uint32_t len = std::min<uint32_t>(m_length, other.m_length);
    int cmp = ::memcmp(m_pointer, other.m_pointer, len * sizeof(T));
    return (cmp == 0) ? static_cast<int>(m_length - other.m_length) : cmp;
  }

  template<typename T>
  template<typename>
  constexpr bool Span<T>::SequenceEqual(ReadOnlySpan<T> other) const noexcept
  {
    return m_length == other.m_length && 
           ::memcmp(m_pointer, other.m_pointer, m_length * sizeof(T)) == 0;
  }
}
