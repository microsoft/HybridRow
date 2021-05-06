// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "StringTokenizer.h"

namespace cdb_hr
{
  using namespace std::literals;

  constexpr StringTokenizer::StringToken Empty = {};

  StringTokenizer::StringTokenizer() noexcept :
    m_tokens(),
    m_strings(),
    m_count(1)
  {
    try
    {
      m_tokens.emplace(""sv, StringToken(0, ""sv));
      m_strings.emplace_back(""sv);
    }
    catch (std::bad_alloc&)
    {
      cdb_core::Contract::Fail("Allocation failure");
    }
  }

  size_t StringTokenizer::GetCount() const noexcept { return m_count; }

  std::tuple<bool, const StringTokenizer::StringToken&> StringTokenizer::TryFindToken(
    std::string_view path) const noexcept
  {
    const auto iter = m_tokens.find(path);
    if (iter == m_tokens.end())
    {
      return {false, Empty};
    }
    return {true, iter->second};
  }

  std::tuple<bool, std::string_view> StringTokenizer::TryFindString(uint64_t token) const noexcept
  {
    if (token >= static_cast<uint64_t>(m_strings.size()))
    {
      return {false, ""sv};
    }

    const auto& path = m_strings[static_cast<size_t>(token)];
    return {true, path};
  }

  const StringTokenizer::StringToken& StringTokenizer::Add(std::string_view path) noexcept
  {
    const auto iter = m_tokens.find(path);
    if (iter == m_tokens.end())
    {
      return AllocateToken(path);
    }
    return iter->second;
  }

  /// <summary>Allocates a new token and assigns the string to it.</summary>
  /// <param name="path">The string that needs a new token.</param>
  /// <returns>The new allocated token.</returns>
  const StringTokenizer::StringToken& StringTokenizer::AllocateToken(std::string_view path) noexcept
  {
    try
    {
      uint64_t id = static_cast<uint64_t>(m_count++);
      StringToken token = StringToken(id, path);
      const auto [iter, success] = m_tokens.emplace(path, token);
      cdb_core::Contract::Invariant(success);
      m_strings.emplace_back(path);
      cdb_core::Contract::Assert(static_cast<uint64_t>(m_strings.size()) - 1 == id);
      return iter->second;
    }
    catch (std::bad_alloc&)
    {
      cdb_core::Contract::Fail("Allocation failure");
    }
  }

  constexpr StringTokenizer::StringToken::StringToken(uint64_t id, std::string_view path) noexcept :
    m_id{id},
    m_varintLength{0},
    m_varint{},
    m_path{path}
  {
    // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
    // when set, indicates there are more bytes.
    while (id >= 0x80)
    {
      m_varint[m_varintLength] = static_cast<byte>(id | 0x80);
      m_varintLength++;
      id >>= 7;
    }

    m_varint[m_varintLength] = static_cast<byte>(id);
    m_varintLength++;
  }

  bool StringTokenizer::StringToken::IsNull() const noexcept
  {
    return m_varintLength == 0;
  }
}
