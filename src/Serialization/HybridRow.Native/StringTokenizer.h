// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <array>
#include <unordered_map>

namespace cdb_hr_test
{
  class StringTokenizerUnitTests;
}

namespace cdb_hr
{
  struct StringTokenizer final
  {
    struct StringToken;

    /// <summary>Initializes a new instance of the <see cref="StringTokenizer" /> class.</summary>
    StringTokenizer() noexcept;
    ~StringTokenizer() noexcept = default;
    StringTokenizer(const StringTokenizer& other) = delete;
    StringTokenizer(StringTokenizer&& other) noexcept = default;
    StringTokenizer& operator=(const StringTokenizer& other) = delete;
    StringTokenizer& operator=(StringTokenizer&& other) noexcept = default;

    /// <summary>The number of unique tokens described by the encoding.</summary>
    [[nodiscard]] size_t GetCount() const noexcept;

    /// <summary>Looks up a string's corresponding token.</summary>
    /// <param name="path">The string to look up.</param>
    /// <returns>{True, the string's assigned token} if successful, {false, undefined} otherwise.</returns>
    [[nodiscard]] std::tuple<bool, const StringToken&> TryFindToken(std::string_view path) const noexcept;

    /// <summary>Looks up a token's corresponding string.</summary>
    /// <param name="token">The token to look up.</param>
    /// <returns>{True, the token's assigned string} if successful, {false, undefined} otherwise.</returns>
    [[nodiscard]] std::tuple<bool, std::string_view> TryFindString(uint64_t token) const noexcept;

    struct StringToken final
    {
      constexpr StringToken() noexcept;
      ~StringToken() noexcept = default;
      StringToken(const StringToken& other) noexcept = default;
      StringToken(StringToken&& other) noexcept = default;
      StringToken& operator=(const StringToken& other) noexcept = default;
      StringToken& operator=(StringToken&& other) noexcept = default;

      [[nodiscard]] bool IsNull() const noexcept;

      [[nodiscard]] uint64_t GetId() const noexcept { return m_id; }

      [[nodiscard]] cdb_core::ReadOnlySpan<byte> GetVarint() const noexcept
      {
        return {m_varint.data(), m_varintLength};
      }

      [[nodiscard]] std::string_view GetPath() const noexcept { return m_path; }

    private:
      friend struct StringTokenizer;
      constexpr StringToken(uint64_t id, std::string_view path) noexcept;

      uint64_t m_id;
      uint32_t m_varintLength;
      std::array<byte, 10> m_varint;
      std::string_view m_path;
    };

  private:
    friend class Layout;
    friend class cdb_hr_test::StringTokenizerUnitTests;

    /// <summary>Assign a token to the string.</summary>
    /// <remarks>If the string already has a token, that token is returned instead.</remarks>
    /// <param name="path">The string to assign a new token.</param>
    /// <returns>The token assigned to the string.</returns>
    const StringToken& Add(std::string_view path) noexcept;

    /// <summary>Allocates a new token and assigns the string to it.</summary>
    /// <param name="path">The string that needs a new token.</param>
    /// <returns>The new allocated token.</returns>
    const StringToken& AllocateToken(std::string_view path) noexcept;

    std::unordered_map<std::string_view, StringToken> m_tokens;
    std::vector<std::string_view> m_strings;
    uint64_t m_count;
  };

  constexpr StringTokenizer::StringToken::StringToken() noexcept : m_id{0}, m_varintLength{0}, m_varint{} {}
}
