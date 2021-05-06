// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "CppUnitTest.h"
// ReSharper disable CppClangTidyCppcoreguidelinesMacroUsage

// Additional MACROS to simplify definitions.
#define TEST_METHOD_WITH_OWNER(methodName, ownerAlias)\
    BEGIN_TEST_METHOD_ATTRIBUTE(methodName)\
      TEST_OWNER(L ## ownerAlias)\
    END_TEST_METHOD_ATTRIBUTE()\
    TEST_METHOD(methodName)

// Additional string function for test diagnostics.
namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<>
  inline std::wstring ToString<cdb_core::ReadOnlySpan<std::byte>>(
    const cdb_core::ReadOnlySpan<std::byte>& q)
  {
    return cdb_core::make_string(L"{p:%p l:%u}", &q[0], q.Length());
  }

  template<>
  inline std::wstring ToString<cdb_hr::Result>(
    const cdb_hr::Result& q)
  {
    return cdb_core::make_string(L"{%d}", q);
  }

  template<>
  inline std::wstring ToString<std::byte>(const std::byte& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<uint16_t>(const uint16_t& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::LayoutType>(
    const cdb_hr::LayoutType* q)
  {
    return cdb_core::make_string(L"{%p}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::Layout>(
    const cdb_hr::Layout* q)
  {
    return cdb_core::make_string(L"{%p}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::Layout>(
    const cdb_hr::Layout& q)
  {
    return cdb_core::make_string(L"{%p}", &q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::LayoutScope>(
    const cdb_hr::LayoutScope* q)
  {
    return cdb_core::make_string(L"{%p}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::LayoutUDT>(
    const cdb_hr::LayoutUDT* q)
  {
    return cdb_core::make_string(L"{%p}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::LayoutColumn>(
    const cdb_hr::LayoutColumn& q)
  {
    return cdb_core::make_string(L"{%p}", &q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::LayoutCode>(
    const cdb_hr::LayoutCode& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::TypeKind>(
    const cdb_hr::TypeKind& q)
  {
    std::string_view v = cdb_hr::ToStringView(q);
    return cdb_core::make_string(L"{%.*S}", v.size(), v.data());
  }

  template<>
  inline std::wstring ToString<cdb_hr::SortDirection>(
    const cdb_hr::SortDirection& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::AllowEmptyKind>(
    const cdb_hr::AllowEmptyKind& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::SchemaLanguageVersion>(
    const cdb_hr::SchemaLanguageVersion& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::StorageKind>(
    const cdb_hr::StorageKind& q)
  {
    switch (q)
    {
    case cdb_hr::StorageKind::Sparse:
      return cdb_core::make_string(L"Sparse{%u}", q);
    case cdb_hr::StorageKind::Fixed:
      return cdb_core::make_string(L"Fixed{%u}", q);
    case cdb_hr::StorageKind::Variable:
      return cdb_core::make_string(L"Variable{%u}", q);
    default:
      return cdb_core::make_string(L"{%u}", q);
    }
  }

  template<>
  inline std::wstring ToString<cdb_hr::HybridRowVersion>(
    const cdb_hr::HybridRowVersion& q)
  {
    return cdb_core::make_string(L"{%u}", q);
  }

  template<>
  inline std::wstring ToString<cdb_hr::SchemaId>(
    const cdb_hr::SchemaId& q)
  {
    return cdb_core::make_string(L"{%d}", q.Id());
  }

  template<>
  inline std::wstring ToString<cdb_hr::MongoDbObjectId>(
    const cdb_hr::MongoDbObjectId& q)
  {
    return L"<mongo id>";
  }

  template<>
  inline std::wstring ToString<cdb_hr::Guid>(
    const cdb_hr::Guid& q)
  {
    return L"<guid>";
  }

  template<>
  inline std::wstring ToString<cdb_hr::DateTime>(
    const cdb_hr::DateTime& q)
  {
    return cdb_core::make_string(L"{%ld}", q.Ticks());
  }

  template<>
  inline std::wstring ToString<cdb_hr::UnixDateTime>(
    const cdb_hr::UnixDateTime& q)
  {
    return cdb_core::make_string(L"{%ld}", q.GetMilliseconds());
  }

  template<>
  inline std::wstring ToString<cdb_hr::Decimal>(
    const cdb_hr::Decimal& q)
  {
    return L"<decimal>";
  }

  template<>
  inline std::wstring ToString<cdb_hr::Float128>(
    const cdb_hr::Float128& q)
  {
    return cdb_core::make_string(L"Low: {%ld}, High: {%ld}", q.Low, q.High);
  }

  template<>
  inline std::wstring ToString<cdb_hr::NullValue>(
    const cdb_hr::NullValue& q)
  {
    return L"<NullValue>";
  }
}
