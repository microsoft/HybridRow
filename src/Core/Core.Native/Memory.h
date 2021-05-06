// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <cstddef>
#include <memory>

#include "Blittable.h"
#include "Contract.h"
#include "Span.h"

namespace cdb_core
{
  template<typename T> struct ReadOnlyMemory;

  /// <summary>
  /// Memory represents a owned (possibly shared) contiguous region of arbitrary memory.
  /// </summary>
  template<typename T>
  struct Memory final
  {
    static_assert(is_blittable_v<T>, "Only blittable types can be used in a Memory<T>.");

    constexpr Memory() noexcept;
    ~Memory() noexcept = default;
    explicit constexpr Memory(uint32_t length) noexcept;
    explicit constexpr Memory(const ReadOnlySpan<T>& buffer) noexcept;
    constexpr Memory(T* buffer, uint32_t length) noexcept;
    template<class Destructor,
      std::enable_if_t<std::conjunction_v<std::is_move_constructible<Destructor>,
        std::is_nothrow_invocable<Destructor&, T*&>>, int>  = 0>
    Memory(T* buffer, uint32_t length, Destructor destructor);

    Memory(const Memory<T>&) noexcept = default;
    Memory(Memory<T>&&) noexcept = default;
    Memory<T>& operator=(const Memory<T>& other) noexcept = default;
    Memory<T>& operator=(Memory<T>&& other) noexcept = default;

    /// <summary>The number of items in the Memory.</summary>
    [[nodiscard]] uint32_t Length() const noexcept;

    [[nodiscard]] operator ReadOnlyMemory<T>() const noexcept;

    [[nodiscard]] ReadOnlySpan<T> AsSpan() const noexcept;

    [[nodiscard]] Span<T> AsSpan() noexcept;

    /// <summary>Returns true if length is 0.</summary>
    [[nodiscard]] bool IsEmpty() const noexcept;

    [[nodiscard]] ReadOnlyMemory<T> Slice(uint32_t start) const noexcept;
    [[nodiscard]] Memory<T> Slice(uint32_t start) noexcept;

    [[nodiscard]] ReadOnlyMemory<T> Slice(uint32_t start, uint32_t length) const noexcept;
    [[nodiscard]] Memory<T> Slice(uint32_t start, uint32_t length) noexcept;

    bool operator==(const Memory<T>& rhs) const noexcept;
    bool operator!=(const Memory<T>& rhs) const noexcept;

  private:
    Memory(std::shared_ptr<std::byte[]> buffer, uint32_t index, uint32_t length) noexcept;

    std::shared_ptr<std::byte[]> m_buffer;
    uint32_t m_index;
    uint32_t m_length;
  };

  template<typename T>
  constexpr Memory<T>::Memory() noexcept :
    m_buffer(),
    m_index(0),
    m_length(0) {}

  template<typename T>
  constexpr Memory<T>::Memory(uint32_t length) noexcept :
    m_buffer(reinterpret_cast<std::byte*>(new T[length])),
    m_index(0),
    m_length(length) {}

  template<typename T>
  constexpr Memory<T>::Memory(const ReadOnlySpan<T>& buffer) noexcept :
    m_buffer(buffer.Length() == 0 ? nullptr : reinterpret_cast<std::byte*>(new T[buffer.Length()])),
    m_index(0),
    m_length(buffer.Length())
  {
    buffer.CopyTo(AsSpan());
  }

  template<typename T>
  constexpr Memory<T>::Memory(T* buffer, uint32_t length) noexcept :
    m_buffer(reinterpret_cast<std::byte*>(buffer)),
    m_index(0),
    m_length(length) {}

  template<typename T>
  template<class Destructor,
    std::enable_if_t<std::conjunction_v<std::is_move_constructible<Destructor>,
      std::is_nothrow_invocable<Destructor&, T*&>>, int>>
  Memory<T>::Memory(T* buffer, uint32_t length, Destructor destructor) : m_buffer(reinterpret_cast<std::byte*>(buffer),
                                                                           destructor),
                                                                         m_index(0),
                                                                         m_length(length) {}

  template<typename T>
  Memory<T>::Memory(std::shared_ptr<std::byte[]> buffer, uint32_t index, uint32_t length) noexcept :
    m_buffer(std::move(buffer)),
    m_index(index),
    m_length(length) {}

  template<typename T>
  uint32_t Memory<T>::Length() const noexcept { return m_length; }

  template<typename T>
  Memory<T>::operator ReadOnlyMemory<T>() const noexcept
  {
    return ReadOnlyMemory<T>(reinterpret_cast<T*>(m_buffer.get()) + m_index, m_length);
  }

  template<typename T>
  ReadOnlySpan<T> Memory<T>::AsSpan() const noexcept
  {
    return ReadOnlySpan<T>(reinterpret_cast<T*>(m_buffer.get()) + m_index, m_length);
  }

  template<typename T>
  Span<T> Memory<T>::AsSpan() noexcept
  {
    return Span<T>(reinterpret_cast<T*>(m_buffer.get()) + m_index, m_length);
  }

  template<typename T>
  bool Memory<T>::IsEmpty() const noexcept { return m_length == 0; }

  template<typename T>
  ReadOnlyMemory<T> Memory<T>::Slice(uint32_t start, uint32_t length) const noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return ReadOnlyMemory<T>(m_buffer, m_index + start, length);
  }

  template<typename T>
  Memory<T> Memory<T>::Slice(uint32_t start, uint32_t length) noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return Memory<T>(m_buffer, m_index + start, length);
  }

  template<typename T>
  ReadOnlyMemory<T> Memory<T>::Slice(uint32_t start) const noexcept
  {
    Contract::Requires(start <= m_length);
    return ReadOnlyMemory<T>(m_buffer, m_index + start, m_length - start);
  }

  template<typename T>
  Memory<T> Memory<T>::Slice(uint32_t start) noexcept
  {
    Contract::Requires(start <= m_length);
    return Memory<T>(m_buffer, m_index + start, m_length - start);
  }

  template<typename T>
  bool Memory<T>::operator==(const Memory<T>& rhs) const noexcept
  {
    return m_buffer == rhs.m_buffer && m_index == rhs.m_index && m_length == rhs.m_length;
  }

  template<typename T>
  bool Memory<T>::operator!=(const Memory<T>& rhs) const noexcept { return !operator==(rhs); }
}
