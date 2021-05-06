// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  struct ConstHashCodeType {};

  struct ConstComparer
  {
    constexpr static size_t ConstantValue = 1234;

    std::size_t operator()(cdb_core_test::ConstHashCodeType const&) const noexcept
    {
      return ConstantValue;
    }
  };

  TEST_CLASS(HashCodeUnitTests)
  {
  public:

    TEST_METHOD(AddHashCode)
    {
      cdb_core::HashCode hc1{};
      hc1.Add("Hello"sv);

      cdb_core::HashCode hc2{};
      hc2.AddHash(std::hash<std::string_view>{}.operator()("Hello"sv));

      Assert::AreEqual(hc1.ToHashCode(), hc2.ToHashCode());
    }

    TEST_METHOD(AddGeneric)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(ConstHashCodeType{});

      cdb_core::HashCode expected{};
      expected.Add(1);
      expected.AddHash(ConstComparer::ConstantValue);

      Assert::AreEqual(expected.ToHashCode(), hc.ToHashCode());
    }

    TEST_METHOD(AddNull)
    {
      cdb_core::HashCode hc{};
      hc.Add(static_cast<char*>(nullptr));

      cdb_core::HashCode expected{};
      expected.AddHash(std::hash<char*>{}.operator()(nullptr));

      Assert::AreEqual(expected.ToHashCode(), hc.ToHashCode());
    }

    TEST_METHOD(AddGenericEqualityComparer)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add<ConstHashCodeType, ConstComparer>(ConstHashCodeType{});

      cdb_core::HashCode expected{};
      expected.Add(1);
      expected.AddHash(ConstComparer::ConstantValue);

      Assert::AreEqual(expected.ToHashCode(), hc.ToHashCode());
    }

    TEST_METHOD(Combine)
    {
      std::vector<size_t> hcs =
      {
        cdb_core::HashCode::Combine(1),
        cdb_core::HashCode::Combine(1, 2),
        cdb_core::HashCode::Combine(1, 2, 3),
        cdb_core::HashCode::Combine(1, 2, 3, 4),
        cdb_core::HashCode::Combine(1, 2, 3, 4, 5),
        cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6),
        cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6, 7),
        cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6, 7, 8),

        cdb_core::HashCode::Combine(2),
        cdb_core::HashCode::Combine(2, 3),
        cdb_core::HashCode::Combine(2, 3, 4),
        cdb_core::HashCode::Combine(2, 3, 4, 5),
        cdb_core::HashCode::Combine(2, 3, 4, 5, 6),
        cdb_core::HashCode::Combine(2, 3, 4, 5, 6, 7),
        cdb_core::HashCode::Combine(2, 3, 4, 5, 6, 7, 8),
        cdb_core::HashCode::Combine(2, 3, 4, 5, 6, 7, 8, 9),
      };

      for (size_t i = 0; i < hcs.size(); i++)
      {
        for (size_t j = 0; j < hcs.size(); j++)
        {
          if (i == j)
          {
            continue;
          }
          Assert::AreNotEqual(hcs[i], hcs[j]);
        }
      }
    }

    TEST_METHOD(CombineAdd1)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1));
    }

    TEST_METHOD(CombineAdd2)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2));
    }

    TEST_METHOD(CombineAdd3)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3));
    }

    TEST_METHOD(CombineAdd4)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      hc.Add(4);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3, 4));
    }

    TEST_METHOD(CombineAdd5)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      hc.Add(4);
      hc.Add(5);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3, 4, 5));
    }

    TEST_METHOD(CombineAdd6)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      hc.Add(4);
      hc.Add(5);
      hc.Add(6);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6));
    }

    TEST_METHOD(CombineAdd7)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      hc.Add(4);
      hc.Add(5);
      hc.Add(6);
      hc.Add(7);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6, 7));
    }

    TEST_METHOD(CombineAdd8)
    {
      cdb_core::HashCode hc{};
      hc.Add(1);
      hc.Add(2);
      hc.Add(3);
      hc.Add(4);
      hc.Add(5);
      hc.Add(6);
      hc.Add(7);
      hc.Add(8);
      Assert::AreEqual(hc.ToHashCode(), cdb_core::HashCode::Combine(1, 2, 3, 4, 5, 6, 7, 8));
    }
  };
}

namespace std
{
  template<>
  struct hash<cdb_core_test::ConstHashCodeType>
  {
    std::size_t operator()(cdb_core_test::ConstHashCodeType const&) const noexcept
    {
      return cdb_core_test::ConstComparer::ConstantValue;
    }
  };
}
