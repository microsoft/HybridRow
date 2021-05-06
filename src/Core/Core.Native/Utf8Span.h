// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "ReadOnlySpan.h"

namespace cdb_core
{
  struct Utf8Span final
  {
    Utf8Span() = delete;
    ~Utf8Span() = delete;
    Utf8Span(const Utf8Span& other) = delete;
    Utf8Span(Utf8Span&& other) noexcept = delete;
    Utf8Span& operator=(const Utf8Span& other) = delete;
    Utf8Span& operator=(Utf8Span&& other) noexcept = delete;

    /// <summary>Creates a <see cref="Utf8Span" /> without validating the underlying bytes.</summary>
    /// <param name="utf8Bytes">The bytes claiming to be UTF8.</param>
    /// <returns>A <see cref="Utf8Span" /> wrapping <paramref name="utf8Bytes" />.</returns>
    /// <remarks>
    /// This method is dangerous as consumers of the <see cref="Utf8Span" /> must assume the
    /// underlying bytes are indeed valid UTF8.  The method should <bold>only</bold> be used when the UTF8
    /// sequence has already been externally valid or is known to be valid by construction.
    /// </remarks>
    constexpr static std::string_view UnsafeFromUtf8BytesNoValidation(const ReadOnlySpan<std::byte>& utf8Bytes) noexcept
    {
      return (utf8Bytes.IsEmpty())
               ? std::string_view{}
               : std::string_view{reinterpret_cast<const char*>(&utf8Bytes[0]), utf8Bytes.Length()};
    }

    /// <summary>The UTF8 byte sequence.</summary>
    constexpr static ReadOnlySpan<std::byte> GetSpan(const std::string_view utf8)
    {
      // ReSharper disable once CppCStyleCast
      return ReadOnlySpan<std::byte>{
        (const std::byte*)(utf8.data()),  // NOLINT(clang-diagnostic-old-style-cast)
        static_cast<uint32_t>(utf8.size())
      };
    }
  };
}
