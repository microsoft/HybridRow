// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(DeepCompareUnitTests)
  {
  public:

    /// <summary>
    /// Tests whether deep compare works correctly on object trees.
    /// </summary>
    TEST_METHOD(ObjectTrees)
    {
      std::vector<int> v1{0, 1, 2, 3};
      Assert::IsTrue(cdb_core::DeepCompare(v1, v1));
      Assert::IsTrue(cdb_core::DeepCompare(v1, std::vector<int>{0, 1, 2, 3}));
      Assert::IsFalse(cdb_core::DeepCompare(v1, std::vector<int>{0, 1, 2, 3, 4}));
      Assert::IsFalse(cdb_core::DeepCompare(v1, std::vector<int>{0, 1, 2, 4}));
      Assert::IsFalse(cdb_core::DeepCompare(v1, std::vector<int>{9, 1, 2, 3}));

      std::unique_ptr<int> u1 = std::make_unique<int>(42);
      Assert::IsTrue(cdb_core::DeepCompare(std::unique_ptr<int>{}, std::unique_ptr<int>{}));
      Assert::IsTrue(cdb_core::DeepCompare(u1, u1));
      Assert::IsTrue(cdb_core::DeepCompare(u1, std::make_unique<int>(42)));
      Assert::IsFalse(cdb_core::DeepCompare(u1, std::make_unique<int>(43)));

      ObjectTree o1{std::move(v1), std::move(u1)};
      Assert::IsTrue(cdb_core::DeepCompare(o1, o1));
      Assert::IsTrue(cdb_core::DeepCompare(o1, ObjectTree{{0, 1, 2, 3}, std::make_unique<int>(42)}));
      Assert::IsFalse(cdb_core::DeepCompare(o1, ObjectTree{{0, 1, 2, 4}, std::make_unique<int>(42)}));
      Assert::IsFalse(cdb_core::DeepCompare(o1, ObjectTree{{0, 1, 2, 3}, std::make_unique<int>(43)}));
    }

    struct ObjectTree final
    {
      std::vector<int> V;
      std::unique_ptr<int> U;
    };
  };
}

namespace cdb_core
{
  template<>
  struct DeepComparer<cdb_core_test::DeepCompareUnitTests::ObjectTree>
  {
    using value_type = cdb_core_test::DeepCompareUnitTests::ObjectTree;

    bool operator()(const value_type& x, const value_type& y) const noexcept
    {
      return cdb_core::DeepCompare(x.V, y.V) && cdb_core::DeepCompare(x.U, y.U);
    }
  };
}
