// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"

namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(LayoutLiteralUnitTests)
  {
  public:

    template<typename T, cdb_hr::LayoutCode code, typename = std::enable_if_t<std::is_base_of_v<cdb_hr::LayoutType, T>>>
    struct LayoutTypeCheck
    {
      static void Check(const cdb_hr::LayoutType& literal) noexcept
      {
        const cdb_hr::LayoutType* q = static_cast<const cdb_hr::LayoutType*>(&literal);
        Assert::AreEqual(q, cdb_hr::LayoutLiteral::FromCode<code>());
        Assert::AreEqual(code, literal.GetLayoutCode());
        const T& r = q->TypeAs<T>();
        Assert::AreEqual(q, static_cast<const cdb_hr::LayoutType*>(&r));
      }
    };

  TEST_METHOD_WITH_OWNER(TypeAsTest, "jthunter")
    {
      LayoutTypeCheck<cdb_hr::LayoutInt8, cdb_hr::LayoutCode::Int8>::Check(cdb_hr::LayoutLiteral::Int8);

      LayoutTypeCheck<cdb_hr::LayoutInt8, cdb_hr::LayoutCode::Int8>::Check(cdb_hr::LayoutLiteral::Int8);
      LayoutTypeCheck<cdb_hr::LayoutInt16, cdb_hr::LayoutCode::Int16>::Check(cdb_hr::LayoutLiteral::Int16);
      LayoutTypeCheck<cdb_hr::LayoutInt32, cdb_hr::LayoutCode::Int32>::Check(cdb_hr::LayoutLiteral::Int32);
      LayoutTypeCheck<cdb_hr::LayoutInt64, cdb_hr::LayoutCode::Int64>::Check(cdb_hr::LayoutLiteral::Int64);
      LayoutTypeCheck<cdb_hr::LayoutUInt8, cdb_hr::LayoutCode::UInt8>::Check(cdb_hr::LayoutLiteral::UInt8);
      LayoutTypeCheck<cdb_hr::LayoutUInt16, cdb_hr::LayoutCode::UInt16>::Check(cdb_hr::LayoutLiteral::UInt16);
      LayoutTypeCheck<cdb_hr::LayoutUInt32, cdb_hr::LayoutCode::UInt32>::Check(cdb_hr::LayoutLiteral::UInt32);
      LayoutTypeCheck<cdb_hr::LayoutUInt64, cdb_hr::LayoutCode::UInt64>::Check(cdb_hr::LayoutLiteral::UInt64);
      LayoutTypeCheck<cdb_hr::LayoutVarInt, cdb_hr::LayoutCode::VarInt>::Check(cdb_hr::LayoutLiteral::VarInt);
      LayoutTypeCheck<cdb_hr::LayoutVarUInt, cdb_hr::LayoutCode::VarUInt>::Check(cdb_hr::LayoutLiteral::VarUInt);
      LayoutTypeCheck<cdb_hr::LayoutFloat32, cdb_hr::LayoutCode::Float32>::Check(cdb_hr::LayoutLiteral::Float32);
      LayoutTypeCheck<cdb_hr::LayoutFloat64, cdb_hr::LayoutCode::Float64>::Check(cdb_hr::LayoutLiteral::Float64);
      LayoutTypeCheck<cdb_hr::LayoutFloat128, cdb_hr::LayoutCode::Float128>::Check(cdb_hr::LayoutLiteral::Float128);
      LayoutTypeCheck<cdb_hr::LayoutDecimal, cdb_hr::LayoutCode::Decimal>::Check(cdb_hr::LayoutLiteral::Decimal);
      LayoutTypeCheck<cdb_hr::LayoutDateTime, cdb_hr::LayoutCode::DateTime>::Check(cdb_hr::LayoutLiteral::DateTime);
      LayoutTypeCheck<cdb_hr::LayoutUnixDateTime, cdb_hr::LayoutCode::UnixDateTime>::Check(
        cdb_hr::LayoutLiteral::UnixDateTime);
      LayoutTypeCheck<cdb_hr::LayoutGuid, cdb_hr::LayoutCode::Guid>::Check(cdb_hr::LayoutLiteral::Guid);
      LayoutTypeCheck<cdb_hr::LayoutMongoDbObjectId, cdb_hr::LayoutCode::MongoDbObjectId>::Check(
        cdb_hr::LayoutLiteral::MongoDbObjectId);
      LayoutTypeCheck<cdb_hr::LayoutNull, cdb_hr::LayoutCode::Null>::Check(cdb_hr::LayoutLiteral::Null);
      LayoutTypeCheck<cdb_hr::LayoutBoolean, cdb_hr::LayoutCode::Boolean>::Check(cdb_hr::LayoutLiteral::Boolean);
      LayoutTypeCheck<cdb_hr::LayoutBoolean, cdb_hr::LayoutCode::BooleanFalse>::Check(
        cdb_hr::LayoutLiteral::BooleanFalse);
      LayoutTypeCheck<cdb_hr::LayoutUtf8, cdb_hr::LayoutCode::Utf8>::Check(cdb_hr::LayoutLiteral::Utf8);
      LayoutTypeCheck<cdb_hr::LayoutBinary, cdb_hr::LayoutCode::Binary>::Check(cdb_hr::LayoutLiteral::Binary);

      LayoutTypeCheck<cdb_hr::LayoutObject, cdb_hr::LayoutCode::ObjectScope>::Check(cdb_hr::LayoutLiteral::Object);
      LayoutTypeCheck<cdb_hr::LayoutObject, cdb_hr::LayoutCode::ImmutableObjectScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableObject);
      LayoutTypeCheck<cdb_hr::LayoutArray, cdb_hr::LayoutCode::ArrayScope>::Check(cdb_hr::LayoutLiteral::Array);
      LayoutTypeCheck<cdb_hr::LayoutArray, cdb_hr::LayoutCode::ImmutableArrayScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableArray);
      LayoutTypeCheck<cdb_hr::LayoutTypedArray, cdb_hr::LayoutCode::TypedArrayScope>::Check(
        cdb_hr::LayoutLiteral::TypedArray);
      LayoutTypeCheck<cdb_hr::LayoutTypedArray, cdb_hr::LayoutCode::ImmutableTypedArrayScope>
        ::Check(cdb_hr::LayoutLiteral::ImmutableTypedArray);
      LayoutTypeCheck<cdb_hr::LayoutTypedSet, cdb_hr::LayoutCode::TypedSetScope
      >::Check(cdb_hr::LayoutLiteral::TypedSet);
      LayoutTypeCheck<cdb_hr::LayoutTypedSet, cdb_hr::LayoutCode::ImmutableTypedSetScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableTypedSet);
      LayoutTypeCheck<cdb_hr::LayoutTypedMap, cdb_hr::LayoutCode::TypedMapScope
      >::Check(cdb_hr::LayoutLiteral::TypedMap);
      LayoutTypeCheck<cdb_hr::LayoutTypedMap, cdb_hr::LayoutCode::ImmutableTypedMapScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableTypedMap);
      LayoutTypeCheck<cdb_hr::LayoutTuple, cdb_hr::LayoutCode::TupleScope>::Check(cdb_hr::LayoutLiteral::Tuple);
      LayoutTypeCheck<cdb_hr::LayoutTuple, cdb_hr::LayoutCode::ImmutableTupleScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableTuple);
      LayoutTypeCheck<cdb_hr::LayoutTypedTuple, cdb_hr::LayoutCode::TypedTupleScope>::Check(
        cdb_hr::LayoutLiteral::TypedTuple);
      LayoutTypeCheck<cdb_hr::LayoutTypedTuple, cdb_hr::LayoutCode::ImmutableTypedTupleScope>
        ::Check(cdb_hr::LayoutLiteral::ImmutableTypedTuple);
      LayoutTypeCheck<cdb_hr::LayoutTagged, cdb_hr::LayoutCode::TaggedScope>::Check(cdb_hr::LayoutLiteral::Tagged);
      LayoutTypeCheck<cdb_hr::LayoutTagged, cdb_hr::LayoutCode::ImmutableTaggedScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableTagged);
      LayoutTypeCheck<cdb_hr::LayoutTagged2, cdb_hr::LayoutCode::Tagged2Scope>::Check(cdb_hr::LayoutLiteral::Tagged2);
      LayoutTypeCheck<cdb_hr::LayoutTagged2, cdb_hr::LayoutCode::ImmutableTagged2Scope>::Check(
        cdb_hr::LayoutLiteral::ImmutableTagged2);
      LayoutTypeCheck<cdb_hr::LayoutNullable, cdb_hr::LayoutCode::NullableScope
      >::Check(cdb_hr::LayoutLiteral::Nullable);
      LayoutTypeCheck<cdb_hr::LayoutNullable, cdb_hr::LayoutCode::ImmutableNullableScope>::Check(
        cdb_hr::LayoutLiteral::ImmutableNullable);
      LayoutTypeCheck<cdb_hr::LayoutUDT, cdb_hr::LayoutCode::Schema>::Check(cdb_hr::LayoutLiteral::UDT);
      LayoutTypeCheck<cdb_hr::LayoutUDT, cdb_hr::LayoutCode::ImmutableSchema>::Check(
        cdb_hr::LayoutLiteral::ImmutableUDT);

      LayoutTypeCheck<cdb_hr::LayoutEndScope, cdb_hr::LayoutCode::EndScope>::Check(cdb_hr::LayoutLiteral::EndScope);
    }
  };
}
