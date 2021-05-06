// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include <string>

#include "tla.h"

namespace cdb_core
{
  /// <summary>
  /// These types are not meant to be used directly but instead exist to facilitate return type inference.
  /// </summary>
  namespace Internal
  {
    // Forward references.
    template<typename TFormat, typename... Args> struct StringFormat;
    template<typename... Args> struct Joiner;
  }

  /// <summary>
  /// Makes a string using printf style format specifiers.
  /// </summary>
  /// <typeparam name="TFormat">The type of the format string.</typeparam>
  /// <typeparam name="TArgs">The types of the arguments to the format specifiers.</typeparam>
  /// <param name="format">The format string.</param>
  /// <param name="args">The arguments to the format specifiers.</param>
  /// <returns>A string object.</returns>
  /// <example>
  /// The return type of this method is not meant to be used directly, but instead to facilitate return type
  /// inference.  E.g.
  /// <code>
  /// std::string u8 = make_string("foo: %s", "some value");
  /// std::wstring u16 = string_join(L"foo: %s", L"some wide-string value");
  /// tla::string s = string_join("foo: %s", "some value");
  /// </code>
  /// </example>
  template<class TFormat, typename... TArgs>
  Internal::StringFormat<TFormat, TArgs...> make_string(TFormat format, TArgs ... args)
  {
    return Internal::StringFormat<TFormat, TArgs...>(format, args...);
  }

  /// <summary>
  /// Makes a string using <c>printf</c> style format specifiers.
  /// </summary>
  /// <remarks>This is an explicit version of the above method for return type inference.</remarks>
  /// <typeparam name="TFormat">The type of the format string.</typeparam>
  /// <typeparam name="TArgs">The types of the arguments to the format specifiers.</typeparam>
  /// <param name="format">The format string.</param>
  /// <param name="args">The arguments to the format specifiers.</param>
  /// <returns>A string object.</returns>
  /// <example>
  /// This overload allows you to specify the return type explicitly for use in expressions where
  /// the return value will be consumed instead of bound to an lvalue.
  /// <code>
  /// Logger::WriteMessage(make_string&lt;std::string&gt;("foo: %s", "some value").c_str());
  /// Logger::WriteMessage(make_string&lt;std::wstring&gt;(L"foo: %s", L"some wide-string value").c_str());
  /// </code>
  /// </example>
  template<class TResult, class TFormat, typename... TArgs>
  TResult make_string(TFormat format, TArgs ... args)
  {
    return Internal::StringFormat<TFormat, TArgs...>(format, args...);
  }

  /// <summary>
  /// Makes a string by combining existing (possibly heterogeneous) string elements together.
  /// </summary>
  /// <typeparam name="TArgs">The types of the string elements to join.</typeparam>
  /// <param name="args">The string elements to join.</param>
  /// <returns>A string object.</returns>
  /// <example>
  /// The return type of this method is not meant to be used directly, but instead to facilitate return type
  /// inference.  E.g.
  /// <code>
  /// std::string u8 = string_join("a", "b", "c");
  /// std::wstring u16 = string_join(L"a", L"b", L"c");
  /// tla::string s = string_join("a", "b", "c");
  /// </code>
  /// </example>
  template<typename... Args>
  Internal::Joiner<Args...> string_join(Args ... args)
  {
    return Internal::Joiner<Args...>{args...};
  }

  namespace Internal
  {
    template<typename... Args>
    struct StringFormatter {};

    template<>
    struct StringFormatter<>
    {
      [[nodiscard]] constexpr StringFormatter() noexcept = default;
      ~StringFormatter() noexcept = default;
      StringFormatter(const StringFormatter<>& other) noexcept = delete;
      StringFormatter(StringFormatter<>&& other) noexcept = default;
      StringFormatter<>& operator=(const StringFormatter<>& other) noexcept = delete;
      StringFormatter<>& operator=(StringFormatter<>&& other) noexcept = delete;

      #pragma warning(push)
      #pragma warning( disable: 4840 )
      template<typename TResult, typename TFormat, typename... TFormatArgs>
      std::enable_if_t<std::is_same_v<typename TResult::value_type, char>, TResult> Format(
        TFormat format, TFormatArgs ... args)
      {
        const size_t size = snprintf(nullptr, 0, &format[0], args ...) + 1; // Extra space for '\0'
        std::unique_ptr<char[]> buf(new char[size]);
        snprintf(buf.get(), size, &format[0], args...);
        return TResult(buf.get(), buf.get() + size - 1); // exclude terminating null
      }

      #pragma warning( disable: 4996 )
      template<typename TResult, typename TFormat, typename... TFormatArgs>
      std::enable_if_t<std::is_same_v<typename TResult::value_type, wchar_t>, TResult> Format(
        TFormat format, TFormatArgs ... args)
      {
        const size_t size = _snwprintf(nullptr, 0, &format[0], args ...) + 1; // Extra space for '\0'
        std::unique_ptr<wchar_t[]> buf(new wchar_t[size]);
        _snwprintf(buf.get(), size, &format[0], args...);
        return TResult(buf.get(), buf.get() + size - 1); // exclude terminating null
      }
      #pragma warning( pop )
    };

    template<typename T, typename... Args>
    struct StringFormatter<T, Args...> : private StringFormatter<Args...>
    {
      using Base = StringFormatter<Args...>;

      [[nodiscard]] constexpr StringFormatter(T first, Args ... args) noexcept : Base{args...}, m_first{first} {}
      ~StringFormatter() noexcept = default;
      StringFormatter(const StringFormatter& other) noexcept = delete;
      StringFormatter(StringFormatter&& other) noexcept = default;
      StringFormatter& operator=(const StringFormatter& other) noexcept = delete;
      StringFormatter& operator=(StringFormatter&& other) noexcept = delete;

    protected:
      template<typename TResult, typename TFormat, typename... TFormatArgs>
      TResult Format(TFormat format, TFormatArgs ... args)
      {
        return Base::template Format<TResult>(format, args..., m_first);
      }

    private:
      T m_first;
    };

    template<typename TFormat, typename... Args>
    struct StringFormat : private StringFormatter<Args...>
    {
      using Base = StringFormatter<Args...>;

      [[nodiscard]] constexpr StringFormat(TFormat format, Args ... args) noexcept : Base{args...}, m_format{format} {}
      ~StringFormat() noexcept = default;
      StringFormat(const StringFormat& other) noexcept = delete;
      StringFormat(StringFormat&& other) noexcept = default;
      StringFormat& operator=(const StringFormat& other) noexcept = delete;
      StringFormat& operator=(StringFormat&& other) noexcept = delete;

      // ReSharper disable once CppNonExplicitConversionOperator
      template<class TElem, class TTraits, class TAlloc>
      operator std::basic_string<TElem, TTraits, TAlloc>()
      {
        return Base::template Format<std::basic_string<TElem, TTraits, TAlloc>>(m_format);
      }

    private:
      TFormat m_format;
    };

    template<typename... Args>
    struct Joiner {};

    template<>
    struct Joiner<>
    {
      [[nodiscard]] constexpr Joiner() noexcept = default;
      ~Joiner() noexcept = default;
      Joiner(const Joiner<>& other) noexcept = delete;
      Joiner(Joiner<>&& other) noexcept = default;
      Joiner<>& operator=(const Joiner<>& other) noexcept = delete;
      Joiner<>& operator=(Joiner<>&& other) noexcept = delete;

      template<typename TResult>
      static void Add(TResult& ret) { }
    };

    template<typename T, typename... Args>
    struct Joiner<T, Args...> : private Joiner<Args...>
    {
      using Base = Joiner<Args...>;

      [[nodiscard]] constexpr Joiner(T first, Args ... args) noexcept : Base{args...}, m_first{first} {}
      ~Joiner() noexcept = default;
      Joiner(const Joiner& other) noexcept = delete;
      Joiner(Joiner&& other) noexcept = default;
      Joiner& operator=(const Joiner& other) noexcept = delete;
      Joiner& operator=(Joiner&& other) noexcept = delete;

      // ReSharper disable once CppNonExplicitConversionOperator
      template<typename TResult>
      operator TResult()
      {
        TResult ret{m_first};
        Base::Add(ret);
        return ret;
      }

    protected:
      template<typename TResult>
      void Add(TResult& ret)
      {
        ret += m_first;
        Base::Add(ret);
      }

    private:
      T m_first;
    };
  }
}
