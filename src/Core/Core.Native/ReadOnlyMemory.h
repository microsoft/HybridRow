// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <cstddef>
#include <memory>

#include "Blittable.h"
#include "Contract.h"
#include "ReadOnlySpan.h"

namespace cdb_core
{
  struct MemoryMarshal;

  /// <summary>
  /// ReadOnlyMemory represents a read-only view of a owned (possibly shared) contiguous region of arbitrary memory.
  /// </summary>
  template<typename T>
  struct ReadOnlyMemory final
  {
    static_assert(is_blittable_v<T>, "Only blittable types can be used in a ReadOnlyMemory<T>.");

    constexpr ReadOnlyMemory() noexcept;
    ~ReadOnlyMemory() noexcept = default;
    explicit constexpr ReadOnlyMemory(const ReadOnlySpan<T>& buffer) noexcept;
    template<class Destructor,
      std::enable_if_t<std::conjunction_v<std::is_move_constructible<Destructor>,
        std::is_nothrow_invocable<Destructor&, T*&>>, int>  = 0>
    ReadOnlyMemory(T* buffer, uint32_t length, Destructor destructor);

    ReadOnlyMemory(const ReadOnlyMemory<T>&) noexcept = default;
    ReadOnlyMemory(ReadOnlyMemory<T>&&) noexcept = default;
    ReadOnlyMemory<T>& operator=(const ReadOnlyMemory<T>& other) noexcept = default;
    ReadOnlyMemory<T>& operator=(ReadOnlyMemory<T>&& other) noexcept = default;

    /// <summary>The number of items in the ReadOnlyMemory.</summary>
    [[nodiscard]]
    uint32_t Length() const noexcept;

    [[nodiscard]]
    ReadOnlySpan<T> AsSpan() const noexcept;

    /// <summary>Returns true if length is 0.</summary>
    [[nodiscard]]
    bool IsEmpty() const noexcept;

    [[nodiscard]]
    ReadOnlyMemory<T> Slice(uint32_t start) const noexcept;

    [[nodiscard]]
    ReadOnlyMemory<T> Slice(uint32_t start, uint32_t length) const noexcept;

    bool operator==(const ReadOnlyMemory<T>& rhs) const noexcept;
    bool operator!=(const ReadOnlyMemory<T>& rhs) const noexcept;

  private:
    friend struct Memory<T>;
    friend struct MemoryMarshal;
    ReadOnlyMemory(std::shared_ptr<std::byte[]> buffer, uint32_t index, uint32_t length) noexcept;

    std::shared_ptr<std::byte[]> m_buffer;
    uint32_t m_index;
    uint32_t m_length;
  };

  template<typename T>
  constexpr ReadOnlyMemory<T>::ReadOnlyMemory() noexcept :
    m_buffer(),
    m_index(0),
    m_length(0) {}

  template<typename T>
  constexpr ReadOnlyMemory<T>::ReadOnlyMemory(const ReadOnlySpan<T>& buffer) noexcept :
    m_buffer(buffer.Length() == 0 ? nullptr : reinterpret_cast<std::byte*>(new T[buffer.Length()])),
    m_index(0),
    m_length(buffer.Length())
  {
    buffer.CopyTo(AsSpan());
  }

  template<typename T>
  template<class Destructor,
    std::enable_if_t<std::conjunction_v<std::is_move_constructible<Destructor>,
      std::is_nothrow_invocable<Destructor&, T*&>>, int>>
  ReadOnlyMemory<T>::ReadOnlyMemory(T* buffer, uint32_t length, Destructor destructor) : m_buffer(reinterpret_cast<std::byte*>(buffer),
                                                                           destructor),
                                                                         m_index(0),
                                                                         m_length(length) {}

  template<typename T>
  ReadOnlyMemory<T>::ReadOnlyMemory(std::shared_ptr<std::byte[]> buffer, uint32_t index, uint32_t length) noexcept :
    m_buffer(std::move(buffer)),
    m_index(index),
    m_length(length) {}

  template<typename T>
  uint32_t ReadOnlyMemory<T>::Length() const noexcept { return m_length; }

  template<typename T>
  ReadOnlySpan<T> ReadOnlyMemory<T>::AsSpan() const noexcept
  {
    return ReadOnlySpan<T>(reinterpret_cast<T*>(m_buffer.get()) + m_index, m_length);
  }

  template<typename T>
  bool ReadOnlyMemory<T>::IsEmpty() const noexcept { return m_length == 0; }

  template<typename T>
  ReadOnlyMemory<T> ReadOnlyMemory<T>::Slice(uint32_t start, uint32_t length) const noexcept
  {
    Contract::Requires(static_cast<uint64_t>(start) + static_cast<uint64_t>(length) <= static_cast<uint64_t>(m_length));
    return ReadOnlyMemory<T>(m_buffer, m_index + start, length);
  }

  template<typename T>
  ReadOnlyMemory<T> ReadOnlyMemory<T>::Slice(uint32_t start) const noexcept
  {
    Contract::Requires(start <= m_length);
    return ReadOnlyMemory<T>(m_buffer, m_index + start, m_length - start);
  }

  template<typename T>
  bool ReadOnlyMemory<T>::operator==(const ReadOnlyMemory<T>& rhs) const noexcept
  {
    return m_buffer == rhs.m_buffer && m_index == rhs.m_index && m_length == rhs.m_length;
  }

  template<typename T>
  bool ReadOnlyMemory<T>::operator!=(const ReadOnlyMemory<T>& rhs) const noexcept { return !operator==(rhs); }
}
