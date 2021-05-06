// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"
#include "TaggedSchema.generated.h"
#include "ResultAssert.h"

namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(TypedArrayUnitTests)
  {
  public:
    TypedArrayUnitTests() :
      m_resolver{typed_array::TypedArrayHrSchema::GetLayoutResolver()},
      m_layout{m_resolver.Resolve(typed_array::TaggedHybridRowSerializer::Id)} { }

  TEST_METHOD_WITH_OWNER(CreateTags, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};
      row.InitLayout(cdb_hr::HybridRowVersion::V1, m_layout, &m_resolver);

      std::unique_ptr<typed_array::Tagged> t1 = cdb_core::make_unique_with([&](typed_array::Tagged& o)
      {
        o.SetTitle("Thriller");
        o.SetTags(std::vector<std::string>{"classic", "Post-disco", "funk"});
        o.SetOptions(std::vector<std::optional<int32_t>>{8, {}, 9});
        o.SetRatings(std::vector<std::vector<float64_t>>
        {
          std::vector<float64_t>{1.2, 3.0},
          std::vector<float64_t>{4.1, 5.7},
          std::vector<float64_t>{7.3, 8.12, 9.14},
        });
        o.SetSimilars(
          cdb_core::make_with([](std::vector<std::unique_ptr<typed_array::SimilarMatch>>& v)
          {
            // ReSharper disable three StringLiteralTypo
            v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
            {
              m.SetThumbprint("TRABACN128F425B784");
              m.SetScore(0.87173699999999998);
            }));
            v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
            {
              m.SetThumbprint("TRJYGLF12903CB4952");
              m.SetScore(0.75105200000000005);
            }));
            v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
            {
              m.SetThumbprint("TRWJMMB128F429D550");
              m.SetScore(0.50866100000000003);
            }));
          }));
        o.SetPriority(std::vector<std::tuple<std::string, int64_t>>
        {
          std::make_tuple("80's", 100L),
          std::make_tuple("classics", 100L),
          std::make_tuple("pop", 50L),
        });
      });

      cdb_hr::RowCursor scope = cdb_hr::RowCursor::Create(row);
      ResultAssert::IsSuccess(typed_array::TaggedHybridRowSerializer::Write(row, scope, true, {}, *t1));
      scope = cdb_hr::RowCursor::Create(row);
      auto [r, t2] = typed_array::TaggedHybridRowSerializer::Read(row, scope, true);
      ResultAssert::IsSuccess(r);

      t1->SetSimilars(
        cdb_core::make_with([](std::vector<std::unique_ptr<typed_array::SimilarMatch>>& v)
        {
          // ReSharper disable three StringLiteralTypo
          v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
          {
            m.SetThumbprint("TRABACN128F425B784");
            m.SetScore(0.87173699999999998);
          }));
          v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
          {
            m.SetThumbprint("TRJYGLF12903CB4952");
            m.SetScore(0.75105200000000005);
          }));
          v.emplace_back(cdb_core::make_unique_with([&](typed_array::SimilarMatch& m)
          {
            m.SetThumbprint("TRWJMMB128F429D550");
            m.SetScore(0.50866100000000003);
          }));
        }));
      Assert::IsTrue(cdb_core::DeepCompare(t1, t2));
    }

  private:
    constexpr static int InitialRowSize = 2 * 1024 * 1024;
    const cdb_hr::LayoutResolver& m_resolver;
    const cdb_hr::Layout& m_layout;
  };
}

namespace cdb_core
{
  template<>
  struct DeepComparer<cdb_hr_test::typed_array::Tagged>
  {
    using value_type = cdb_hr_test::typed_array::Tagged;

    bool operator()(const value_type& x, const value_type& y) const noexcept
    {
      return cdb_core::DeepCompare(x.GetTitle(), y.GetTitle()) &&
        cdb_core::DeepCompare(x.GetTags(), y.GetTags()) &&
        cdb_core::DeepCompare(x.GetOptions(), y.GetOptions()) &&
        cdb_core::DeepCompare(x.GetRatings(), y.GetRatings()) &&
        cdb_core::DeepCompare(x.GetSimilars(), y.GetSimilars()) &&
        cdb_core::DeepCompare(x.GetPriority(), y.GetPriority());
    }
  };

  template<>
  struct DeepComparer<cdb_hr_test::typed_array::SimilarMatch>
  {
    using value_type = cdb_hr_test::typed_array::SimilarMatch;

    bool operator()(const value_type& x, const value_type& y) const noexcept
    {
      return
        cdb_core::DeepCompare(x.GetThumbprint(), y.GetThumbprint()) &&
        cdb_core::DeepCompare(x.GetScore(), y.GetScore());
    }
  };
}
