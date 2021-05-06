// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"
#include "ResultAssert.h"

namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(HybridRowSerializerUnitTests)
  {
  public:

  TEST_METHOD_WITH_OWNER(TypedArraySerializerTest, "jthunter")
    {
      using TS1 = cdb_hr::TypedArrayHybridRowSerializer<std::string, cdb_hr::Utf8HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs1{cdb_hr::TypeArgument{&cdb_hr::LayoutLiteral::Utf8}};
      TestSerializer<TS1>(typeArgs1, {"abc"s, "xyz"s}, {"abc"s, "ghk"s}, {"abc"s});

      using TS2 = cdb_hr::TypedArrayHybridRowSerializer<int32_t, cdb_hr::Int32HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs2{{cdb_hr::TypeArgument{&cdb_hr::LayoutLiteral::Int32}}};
      TestSerializer<TS2>(typeArgs2, {123, 456}, {123, 789}, {4733584});
    }

  TEST_METHOD_WITH_OWNER(ArraySerializerTest, "jthunter")
    {
      using TS1 = cdb_hr::ArrayHybridRowSerializer<std::string, cdb_hr::Utf8HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs1{cdb_hr::TypeArgument{&cdb_hr::LayoutLiteral::Utf8}};
      TestSerializer<TS1>(typeArgs1, {"abc"s, "xyz"s}, {"abc"s, "ghk"s}, {"abc"s});

      using TS2 = cdb_hr::ArrayHybridRowSerializer<int32_t, cdb_hr::Int32HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs2{{cdb_hr::TypeArgument{&cdb_hr::LayoutLiteral::Int32}}};
      TestSerializer<TS2>(typeArgs2, {123, 456}, {123, 789}, {4733584});
    }

  TEST_METHOD_WITH_OWNER(TypedTupleSerializerTest, "jthunter")
    {
      using TS1 = cdb_hr::TypedTupleHybridRowSerializer<
        cdb_hr::Utf8HybridRowSerializer,
        cdb_hr::Int32HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs1{
        {&cdb_hr::LayoutLiteral::Utf8, &cdb_hr::LayoutLiteral::Int32}
      };
      TestSerializer<TS1>(typeArgs1, {"abc"s, 123}, {"xyz"s, 123}, {"abc"s, 456});

      using TS2 = cdb_hr::TypedTupleHybridRowSerializer<
        cdb_hr::Int64HybridRowSerializer,
        cdb_hr::Int32HybridRowSerializer,
        cdb_hr::DateTimeHybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs2{
        {
          &cdb_hr::LayoutLiteral::Int64,
          &cdb_hr::LayoutLiteral::Int32,
          &cdb_hr::LayoutLiteral::DateTime
        }
      };
      TestSerializer<TS2>(typeArgs2, {789, 123, {456}}, {654, 123, {456}}, {789, 123, {789}});
    }

  TEST_METHOD_WITH_OWNER(NullableSerializerTest, "jthunter")
    {
      using TS1 = cdb_hr::NullableHybridRowSerializer<
        std::optional<int32_t>, int32_t, cdb_hr::Int32HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs1{
        {cdb_hr::TypeArgument{&cdb_hr::LayoutLiteral::Int32}}
      };
      TestSerializer<TS1>(typeArgs1, 123, 456, std::nullopt);
    }

  TEST_METHOD_WITH_OWNER(MapSerializerTest, "jthunter")
    {
      using TS1 = cdb_hr::TypedMapHybridRowSerializer<
        cdb_hr::Utf8HybridRowSerializer,
        cdb_hr::Int32HybridRowSerializer>;
      cdb_hr::TypeArgumentList typeArgs1{
        {&cdb_hr::LayoutLiteral::Utf8, &cdb_hr::LayoutLiteral::Int32}
      };

      TestSerializer<TS1>(typeArgs1, {{"abc"s, 123}}, {{"xyz"s, 123}}, {{"abc"s, 456}});
    }

  private:
    template<typename TS>
    static void TestSerializer(
      const cdb_hr::TypeArgumentList& typeArgs,
      typename TS::const_reference t1,
      typename TS::const_reference t2,
      typename TS::const_reference t3)
    {
      const cdb_hr::LayoutResolver& resolver = cdb_hr::SchemasHrSchema::GetLayoutResolver();
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SystemSchemaLiteral::EmptySchemaId);

      cdb_hr::MemorySpanResizer<byte> resizer{0};
      cdb_hr::RowBuffer row{0, &resizer};
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
      ResultAssert::IsSuccess(TS::Write(row, root.Clone().Find(row, "a"), false, typeArgs, t1));
      auto [r2, v1] = TS::Read(row, root.Clone().Find(row, "a"), false);
      ResultAssert::IsSuccess(r2);

      using comparer_type = typename TS::comparer_type;
      Assert::IsTrue(comparer_type{}.operator()(t1, v1));
      Assert::AreEqual(comparer_type{}.operator()(t1), comparer_type{}.operator()(v1));
      Assert::AreNotEqual(comparer_type{}.operator()(t1), size_t{0});

      Assert::IsFalse(comparer_type{}.operator()(t1, t2));
      Assert::AreNotEqual(comparer_type{}.operator()(t1), comparer_type{}.operator()(t2));

      Assert::IsFalse(comparer_type{}.operator()(t1, t3));
      Assert::AreNotEqual(comparer_type{}.operator()(t1), comparer_type{}.operator()(t3));
    }
  };
}
