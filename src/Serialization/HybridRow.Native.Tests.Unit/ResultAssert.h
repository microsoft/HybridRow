// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "CppUnitTest.h"

namespace cdb_hr_test
{
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  struct ResultAssert final
  {
    static void IsSuccess(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::Success, actual);
    }

    static void IsSuccess(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::Success, actual, message.data());
    }

    static void NotFound(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::NotFound, actual);
    }

    static void NotFound(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::NotFound, actual, message.data());
    }

    static void Exists(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::Exists, actual);
    }

    static void Exists(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::Exists, actual, message.data());
    }

    static void TypeMismatch(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::TypeMismatch, actual);
    }

    static void TypeMismatch(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::TypeMismatch, actual, message.data());
    }

    static void InsufficientPermissions(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::InsufficientPermissions, actual);
    }

    static void InsufficientPermissions(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::InsufficientPermissions, actual, message.data());
    }

    static void TypeConstraint(cdb_hr::Result actual)
    {
      Assert::AreEqual(cdb_hr::Result::TypeConstraint, actual);
    }

    static void TypeConstraint(cdb_hr::Result actual, std::wstring_view message)
    {
      Assert::AreEqual(cdb_hr::Result::TypeConstraint, actual, message.data());
    }
  };
}
