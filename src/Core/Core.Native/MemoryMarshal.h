// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "Memory.h"
#include "Span.h"
#include "ReadOnlySpan.h"

namespace cdb_core
{
  struct MemoryMarshal final
  {
    MemoryMarshal() = delete;

    template<typename T, typename U>
    static Span<U> Cast(Span<T> source)
    {
      const size_t lengthU = (source.m_length * sizeof(T)) / sizeof(U);
      return Span<U>(reinterpret_cast<U*>(source.m_pointer), static_cast<uint32_t>(lengthU));
    }

    template<typename T>
    static void Write(Span<std::byte> destination, const T& value)
    {
      static_assert(is_blittable_v<T>, "Only blittable types can be used in a Span<T>.");
      Contract::Requires(destination.m_length >= sizeof(T));
      *reinterpret_cast<T*>(destination.m_pointer) = value;
    }

    template<typename T>
    static const T& Read(const Span<std::byte>& source)
    {
      static_assert(is_blittable_v<T>, "Only blittable types can be used in a Span<T>.");
      Contract::Requires(source.m_length >= sizeof(T));
      return *reinterpret_cast<T*>(source.m_pointer);
    }

    template<typename T, typename U>
    static ReadOnlySpan<U> Cast(const ReadOnlySpan<T> source)
    {
      const size_t lengthU = (source.m_length * sizeof(T)) / sizeof(U);
      return ReadOnlySpan<U>(reinterpret_cast<const U*>(source.m_pointer), static_cast<uint32_t>(lengthU));
    }

    template<typename T>
    static const T& Read(const ReadOnlySpan<std::byte>& source)
    {
      static_assert(is_blittable_v<T>, "Only blittable types can be used in a ReadOnlySpan<T>.");
      Contract::Requires(source.m_length >= sizeof(T));
      return *reinterpret_cast<const T*>(source.m_pointer);
    }

    template<typename T>
    [[nodiscard]] static Memory<T> AsMemory(const ReadOnlyMemory<T>& source) noexcept
    {
      static_assert(is_blittable_v<T>, "Only blittable types can be used in a ReadOnlySpan<T>.");
      return Memory<T>(reinterpret_cast<T*>(source.m_buffer.get()) + source.m_index, source.m_length);
    }
};
}
