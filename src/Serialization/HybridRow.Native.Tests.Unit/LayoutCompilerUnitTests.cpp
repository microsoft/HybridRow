// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"
#include "ResultAssert.h"
#include "CollectionAssert.h"

// ReSharper disable CppImplicitDefaultConstructorNotAvailable
// ReSharper disable CppClangTidyCppcoreguidelinesProTypeMemberInit
namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  TEST_CLASS(LayoutCompilerUnitTests)
  {
    constexpr static uint32_t InitialRowSize = 2 * 1024 * 1024;

  public:
  TEST_METHOD_WITH_OWNER(PackNullAndBoolBits, "jthunter")
    {
      // Test that null bits and bool bits are packed tightly in the layout.
      cdb_hr::Namespace ns{};
      ns.GetSchemas().emplace_back(cdb_core::make_unique_with([](cdb_hr::Schema& s)
      {
        s.SetName("TestSchema");
        s.SetSchemaId(cdb_hr::SchemaId{1});
      }));
      cdb_hr::Schema& s = *ns.GetSchemas().back();

      for (int32_t i = 0; i < 32; i++)
      {
        s.GetProperties().emplace_back(cdb_core::make_unique_with([i](cdb_hr::Property& p)
        {
          p.SetPath(cdb_core::make_string<std::string>("%d", i));
          p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
          {
            pt.SetType(cdb_hr::TypeKind::Boolean);
            pt.SetStorage(cdb_hr::StorageKind::Fixed);
          }));
        }));

        auto layout = s.Compile(ns);
        Assert::IsTrue(layout->GetSize() == cdb_hr::LayoutBit::DivCeiling((i + 1) * 2, cdb_hr::LayoutType::BitsPerByte),
          cdb_core::make_string<std::wstring>(L"Size: %u, i: %d", layout->GetSize(), i).c_str());
      }
    }

    template<typename TClosure, typename TLayout, typename TValue>
    // where TLayout : LayoutType<TValue>;
    struct TestActionDispatcher
    {
      TestActionDispatcher() = default;
      virtual ~TestActionDispatcher() = default;
      TestActionDispatcher(const TestActionDispatcher& other) = delete;
      TestActionDispatcher(TestActionDispatcher&& other) noexcept = delete;
      TestActionDispatcher& operator=(const TestActionDispatcher& other) = delete;
      TestActionDispatcher& operator=(TestActionDispatcher&& other) noexcept = delete;
      virtual void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, TClosure& closure) = 0;
    };

    template<typename TClosure>
    struct TestActionObjectDispatcher
    {
      TestActionObjectDispatcher() = default;
      virtual ~TestActionObjectDispatcher() = default;
      TestActionObjectDispatcher(const TestActionObjectDispatcher& other) = delete;
      TestActionObjectDispatcher(TestActionObjectDispatcher&& other) noexcept = delete;
      TestActionObjectDispatcher& operator=(const TestActionObjectDispatcher& other) = delete;
      TestActionObjectDispatcher& operator=(TestActionObjectDispatcher&& other) noexcept = delete;
      virtual void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, TClosure& closure) = 0;
    };

    template<typename TTestCase,
      template <typename, typename> typename TDispatcher = TTestCase::Dispatcher,
      typename TObjectDispatcher = typename TTestCase::ObjectDispatcher,
      template <typename> typename TClosure = TTestCase::Closure>
    static void LayoutCodeSwitch(cdb_hr::LayoutCode code, cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, void* closure)
    {
      switch (code)
      {
      case cdb_hr::LayoutCode::Null:
        TDispatcher<cdb_hr::LayoutNull, cdb_hr::NullValue>{}.Dispatch(row, scope, *static_cast<TClosure<cdb_hr::NullValue>*>(closure));
        break;
      case cdb_hr::LayoutCode::Boolean:
        TDispatcher<cdb_hr::LayoutBoolean, bool>{}.Dispatch(row, scope, *static_cast<TClosure<bool>*>(closure));
        break;
      case cdb_hr::LayoutCode::Int8:
        TDispatcher<cdb_hr::LayoutInt8, int8_t>{}.Dispatch(row, scope, *static_cast<TClosure<int8_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Int16:
        TDispatcher<cdb_hr::LayoutInt16, int16_t>{}.Dispatch(row, scope, *static_cast<TClosure<int16_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Int32:
        TDispatcher<cdb_hr::LayoutInt32, int32_t>{}.Dispatch(row, scope, *static_cast<TClosure<int32_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Int64:
        TDispatcher<cdb_hr::LayoutInt64, int64_t>{}.Dispatch(row, scope, *static_cast<TClosure<int64_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::UInt8:
        TDispatcher<cdb_hr::LayoutUInt8, uint8_t>{}.Dispatch(row, scope, *static_cast<TClosure<uint8_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::UInt16:
        TDispatcher<cdb_hr::LayoutUInt16, uint16_t>{}.Dispatch(row, scope, *static_cast<TClosure<uint16_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::UInt32:
        TDispatcher<cdb_hr::LayoutUInt32, uint32_t>{}.Dispatch(row, scope, *static_cast<TClosure<uint32_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::UInt64:
        TDispatcher<cdb_hr::LayoutUInt64, uint64_t>{}.Dispatch(row, scope, *static_cast<TClosure<uint64_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::VarInt:
        TDispatcher<cdb_hr::LayoutVarInt, int64_t>{}.Dispatch(row, scope, *static_cast<TClosure<int64_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::VarUInt:
        TDispatcher<cdb_hr::LayoutVarUInt, uint64_t>{}.Dispatch(row, scope, *static_cast<TClosure<uint64_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Float32:
        TDispatcher<cdb_hr::LayoutFloat32, float32_t>{}.Dispatch(row, scope, *static_cast<TClosure<float32_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Float64:
        TDispatcher<cdb_hr::LayoutFloat64, float64_t>{}.Dispatch(row, scope, *static_cast<TClosure<float64_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Float128:
        TDispatcher<cdb_hr::LayoutFloat128, float128_t>{}.Dispatch(row, scope, *static_cast<TClosure<float128_t>*>(closure));
        break;
      case cdb_hr::LayoutCode::Decimal:
        TDispatcher<cdb_hr::LayoutDecimal, cdb_hr::Decimal>{}.Dispatch(row, scope, *static_cast<TClosure<cdb_hr::Decimal>*>(closure));
        break;
      case cdb_hr::LayoutCode::DateTime:
        TDispatcher<cdb_hr::LayoutDateTime, cdb_hr::DateTime>{}.Dispatch(row, scope, *static_cast<TClosure<cdb_hr::DateTime>*>(closure));
        break;
      case cdb_hr::LayoutCode::UnixDateTime:
        TDispatcher<cdb_hr::LayoutUnixDateTime, cdb_hr::UnixDateTime>{}.Dispatch(row, scope,
          *static_cast<TClosure<cdb_hr::UnixDateTime>*>(closure));
        break;
      case cdb_hr::LayoutCode::Guid:
        TDispatcher<cdb_hr::LayoutGuid, cdb_hr::Guid>{}.Dispatch(row, scope, *static_cast<TClosure<cdb_hr::Guid>*>(closure));
        break;
      case cdb_hr::LayoutCode::MongoDbObjectId:
        TDispatcher<cdb_hr::LayoutMongoDbObjectId, cdb_hr::MongoDbObjectId>{}.Dispatch(row, scope,
          *static_cast<TClosure<cdb_hr::MongoDbObjectId>*>(closure));
        break;
      case cdb_hr::LayoutCode::Utf8:
        TDispatcher<cdb_hr::LayoutUtf8, std::string_view>{}.Dispatch(row, scope,
          *static_cast<TClosure<std::string_view>*>(closure));
        break;
      case cdb_hr::LayoutCode::Binary:
        TDispatcher<cdb_hr::LayoutBinary, cdb_core::ReadOnlySpan<byte>>{}.Dispatch(row, scope,
          *static_cast<TClosure<cdb_core::ReadOnlySpan<byte>>*>(closure));
        break;
      case cdb_hr::LayoutCode::ObjectScope:
        TObjectDispatcher{}.DispatchObject(row, scope, *static_cast<TClosure<nullptr_t>*>(closure));
        break;
      default:
        cdb_core::Contract::Assert(false, cdb_core::make_string<std::string>("Unknown type will be ignored: %u", code).c_str());
        break;
      }
    }

    struct RoundTripFixed final
    {
      template<typename TValue>
      struct Expected final
      {
        cdb_hr::TypeKind TypeKind;
        TValue Default;
        TValue Value;
        uint32_t Length = 0;
        std::wstring Tag = L""s;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& Col;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher final : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          Expected<TValue>& expected = closure.Expected;
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          if (col.GetNullBit() != cdb_hr::LayoutBit::Invalid())
          {
            auto [r, value] = t.ReadFixed(row, root, col);
            ResultAssert::NotFound(r, expected.Tag);
          }
          else
          {
            auto [r, value] = t.ReadFixed(row, root, col);
            ResultAssert::IsSuccess(r, expected.Tag);
            if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
            {
              CollectionAssert::AreEqual(expected.Default, value, expected.Tag);
            }
            else
            {
              Assert::AreEqual(expected.Default, value, expected.Tag.data());
            }
          }

          ResultAssert::IsSuccess(t.WriteFixed(row, root, col, expected.Value), expected.Tag.data());
          auto [r, value] = t.ReadFixed(row, root, col);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(expected.Value, value, expected.Tag);
          }
          else
          {
            Assert::AreEqual(expected.Value, value, expected.Tag.data());
          }

          cdb_hr::RowCursor roRoot = root.AsReadOnly();
          ResultAssert::InsufficientPermissions(t.WriteFixed(row, roRoot, col, expected.Value));
          ResultAssert::InsufficientPermissions(t.DeleteFixed(row, roRoot, col));

          if (col.GetNullBit() != cdb_hr::LayoutBit::Invalid())
          {
            ResultAssert::IsSuccess(t.DeleteFixed(row, root, col));
          }
          else
          {
            ResultAssert::TypeMismatch(t.DeleteFixed(row, root, col));
          }
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    template<typename TValue>
    void ParseSchemaFixedCase(RoundTripFixed::Expected<TValue> expected)
    {
      for (bool nullable : {true, false})
      {
        expected.Tag = cdb_core::make_string(L"{{'%S', 'length': %d, 'nullable': %d}}\n",
          ToStringView(expected.TypeKind).data(), expected.Length, nullable);
        Logger::WriteMessage(expected.Tag.data());

        // Build a schema and namespace.
        auto schema = cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("table"s);
          s.SetSchemaId(cdb_hr::SchemaId{-1});
          s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
          {
            p.SetPath("a"s);
            p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(expected.TypeKind);
              pt.SetStorage(cdb_hr::StorageKind::Fixed);
              pt.SetLength(expected.Length);
              pt.SetNullable(nullable);
            }));
          }));
        });
        auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
        {
          n.GetSchemas().emplace_back(std::move(schema));
        });

        // Verify schema compilation failures for invalid configurations.
        if ((expected.TypeKind == cdb_hr::TypeKind::Null) && (!nullable))
        {
          try
          {
            auto& s = ns->GetSchemas()[0];
            auto layout = s->Compile(*ns);
            Assert::IsNull(layout.get(), expected.Tag.data());  // Should never get here.
          }
          catch (cdb_hr::LayoutCompiler::LayoutCompilationException&)
          {
            return;  // Schema compile failed as expected.
          }
        }

        // Round-trip a value of the indicated type.
        cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
        cdb_hr::RowBuffer row{InitialRowSize, &resizer};
        cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
        const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});
        Assert::AreEqual(size_t{1}, layout.GetColumns().size(), expected.Tag.data());
        Assert::AreEqual("table", layout.GetName().data(), expected.Tag.data());
        std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
        Assert::IsTrue(std::get<0>(found), expected.Tag.data());
        const cdb_hr::LayoutColumn& col = *std::get<1>(found);
        Assert::AreEqual(cdb_hr::StorageKind::Fixed, col.GetStorage(), expected.Tag.data());
        Assert::AreEqual(expected.Length == 0, col.GetType()->IsFixed(), expected.Tag.data());

        // Try writing a row using the layout.
        row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);
        cdb_hr::HybridRowHeader header = row.GetHeader();
        Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
        Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());
        cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);

        RoundTripFixed::Closure<TValue> closure{col, expected};
        LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripFixed>(col.GetType()->GetLayoutCode(), row, root, &closure);
      }
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaFixed, "jthunter")
    {
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_hr::NullValue>{cdb_hr::TypeKind::Null, cdb_hr::NullValue{}, cdb_hr::NullValue{}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<bool>{cdb_hr::TypeKind::Boolean, false, false});
      ParseSchemaFixedCase(RoundTripFixed::Expected<int8_t>{cdb_hr::TypeKind::Int8, int8_t{0}, int8_t{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<int16_t>{cdb_hr::TypeKind::Int16, int16_t{0}, int16_t{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<int32_t>{cdb_hr::TypeKind::Int32, int32_t{0}, int32_t{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<int64_t>{cdb_hr::TypeKind::Int64, int64_t{0}, int64_t{42L}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<uint8_t>{cdb_hr::TypeKind::UInt8, uint8_t{0}, uint8_t{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<uint16_t>{cdb_hr::TypeKind::UInt16, uint16_t{0}, uint16_t{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<uint32_t>{cdb_hr::TypeKind::UInt32, uint32_t{0}, uint32_t{42U}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<uint64_t>{cdb_hr::TypeKind::UInt64, uint64_t{0}, uint64_t{42UL}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<float32_t>{cdb_hr::TypeKind::Float32, float{0}, float{4.2F}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<float64_t>{cdb_hr::TypeKind::Float64, double{0}, double{4.2}});
      ParseSchemaFixedCase(
        RoundTripFixed::Expected<float128_t>{cdb_hr::TypeKind::Float128, float128_t{0, 0}, float128_t{0, 42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_hr::Decimal>{cdb_hr::TypeKind::Decimal, cdb_hr::Decimal{0}, cdb_hr::Decimal{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_hr::DateTime>{cdb_hr::TypeKind::DateTime, cdb_hr::DateTime{0}, cdb_hr::DateTime{42}});
      ParseSchemaFixedCase(
        RoundTripFixed::Expected<cdb_hr::UnixDateTime>{cdb_hr::TypeKind::UnixDateTime, cdb_hr::UnixDateTime{0}, cdb_hr::UnixDateTime{42}});
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_hr::Guid>{cdb_hr::TypeKind::Guid, cdb_hr::Guid{}, cdb_hr::Guid::NewGuid()});
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_hr::MongoDbObjectId>{
        cdb_hr::TypeKind::MongoDbObjectId,
        cdb_hr::MongoDbObjectId{},
        cdb_hr::MongoDbObjectId{0, 42}
      });

      ParseSchemaFixedCase(RoundTripFixed::Expected<std::string_view>{cdb_hr::TypeKind::Utf8, "\0\0"sv, "AB"sv, 2});
      std::array<byte, 2> defaultBinary{{byte{0x00}, byte{0x00}}};
      std::array<byte, 2> valueBinary{{byte{0x01}, byte{0x02}}};
      ParseSchemaFixedCase(RoundTripFixed::Expected<cdb_core::ReadOnlySpan<byte>>{
        cdb_hr::TypeKind::Binary,
        defaultBinary,
        valueBinary,
        static_cast<uint32_t>(valueBinary.size())
      });
    }

    struct RoundTripVariable
    {
      template<typename TValue>
      struct Expected final
      {
        cdb_hr::TypeKind TypeKind;
        uint32_t Length;
        std::wstring Tag;
        TValue Short;
        TValue Value;
        TValue Long;
        TValue TooBig;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& Col;
        const cdb_hr::Layout& Layout;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          Expected<TValue>& expected = closure.Expected;
          Logger::WriteMessage(expected.Tag.data());
          RoundTrip(row, root, col, expected.Value, expected);
        }

      protected:
        static void RoundTrip(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::LayoutColumn& col, TValue exValue,
                              Expected<TValue>& expected)
        // where TLayout : LayoutType<TValue>
        {
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          cdb_hr::Result r = t.WriteVariable(row, root, col, exValue);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          Compare(row, root, col, exValue, expected);

          cdb_hr::RowCursor roRoot = root.AsReadOnly();
          ResultAssert::InsufficientPermissions(t.WriteVariable(row, roRoot, col, expected.Value));
        }

        static void Compare(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::LayoutColumn& col, TValue exValue,
                            Expected<TValue>& expected)
        // where TLayout : LayoutType<TValue>
        {
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          auto [r, value] = t.ReadVariable(row, root, col);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(exValue, value, expected.Tag);
          }
          else
          {
            Assert::AreEqual(exValue, value, expected.Tag.data());
          }
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    struct VariableInterleaving : RoundTripVariable
    {
      template<typename TLayout, typename TValue>
      struct Dispatcher final : RoundTripVariable::Dispatcher<TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::Layout& layout = closure.Layout;
          Expected<TValue>& expected = closure.Expected;

          Logger::WriteMessage(expected.Tag.data());

          const cdb_hr::LayoutColumn& a = Verify(row, root, layout, "a", expected);
          const cdb_hr::LayoutColumn& b = Verify(row, root, layout, "b", expected);
          const cdb_hr::LayoutColumn& c = Verify(row, root, layout, "c", expected);

          RoundTripVariable::Dispatcher<TLayout, TValue>::RoundTrip(row, root, b, expected.Value, expected);
          RoundTripVariable::Dispatcher<TLayout, TValue>::RoundTrip(row, root, a, expected.Value, expected);
          RoundTripVariable::Dispatcher<TLayout, TValue>::RoundTrip(row, root, c, expected.Value, expected);

          // Make the var column shorter.
          uint32_t rowSizeBeforeShrink = row.GetLength();
          RoundTripVariable::Dispatcher<TLayout, TValue>::RoundTrip(row, root, a, expected.Short, expected);
          RoundTripVariable::Dispatcher<TLayout, TValue>::Compare(row, root, c, expected.Value, expected);
          uint32_t rowSizeAfterShrink = row.GetLength();
          Assert::IsTrue(rowSizeAfterShrink < rowSizeBeforeShrink, expected.Tag.data());

          // Make the var column longer.
          RoundTripVariable::Dispatcher<TLayout, TValue>::RoundTrip(row, root, a, expected.Long, expected);
          RoundTripVariable::Dispatcher<TLayout, TValue>::Compare(row, root, c, expected.Value, expected);
          uint32_t rowSizeAfterGrow = row.GetLength();
          Assert::IsTrue(rowSizeAfterGrow > rowSizeAfterShrink, expected.Tag.data());
          Assert::IsTrue(rowSizeAfterGrow > rowSizeBeforeShrink, expected.Tag.data());

          // Check for size overflow errors.
          if (a.GetSize() > 0)
          {
            TooBig(row, root, a, expected);
          }

          // Delete the var column.
          Delete(row, root, b, expected);
          Delete(row, root, c, expected);
          Delete(row, root, a, expected);
        }

        static const cdb_hr::LayoutColumn& Verify(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::Layout& layout, std::string_view path,
                                          Expected<TValue>& expected)
        {
          std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind(path);
          Assert::IsTrue(std::get<0>(found), expected.Tag.data());
          const cdb_hr::LayoutColumn& col = *std::get<1>(found);

          Assert::IsTrue(col.GetType()->AllowVariable());
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          auto [r, value] = t.ReadVariable(row, root, col);
          ResultAssert::NotFound(r, expected.Tag.data());
          return col;
        }

        static void TooBig(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::LayoutColumn& col, Expected<TValue>& expected)
        {
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          cdb_hr::Result r = t.WriteVariable(row, root, col, expected.TooBig);
          Assert::AreEqual(cdb_hr::Result::TooBig, r, expected.Tag.data());
        }

        static void Delete(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::LayoutColumn& col, Expected<TValue>& expected)
        {
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          cdb_hr::RowCursor roRoot = root.AsReadOnly();
          ResultAssert::InsufficientPermissions(t.DeleteVariable(row, roRoot, col));
          cdb_hr::Result r = t.DeleteVariable(row, root, col);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          auto [r2, _] = t.ReadVariable(row, root, col);
          ResultAssert::NotFound(r2, expected.Tag.data());
        }
      };
    };

    template<typename TValue>
    void ParseSchemaVariableCase(RoundTripVariable::Expected<TValue> expected)
    {
      auto schema = cdb_core::make_unique_with([&](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
        {
          p.SetPath("a"s);
          p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
          {
            pt.SetType(expected.TypeKind);
            pt.SetStorage(cdb_hr::StorageKind::Variable);
            pt.SetLength(expected.Length);
          }));
        }));
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
        {
          p.SetPath("b"s);
          p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
          {
            pt.SetType(expected.TypeKind);
            pt.SetStorage(cdb_hr::StorageKind::Variable);
            pt.SetLength(expected.Length);
          }));
        }));
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
        {
          p.SetPath("c"s);
          p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
          {
            pt.SetType(expected.TypeKind);
            pt.SetStorage(cdb_hr::StorageKind::Variable);
            pt.SetLength(expected.Length);
          }));
        }));
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.GetSchemas().emplace_back(std::move(schema));
      });

      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);

      Assert::IsTrue(col.GetType()->AllowVariable());
      Assert::AreEqual(cdb_hr::StorageKind::Variable, col.GetStorage(), expected.Tag.data());

      // Try writing a row using the layout.
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
      RoundTripVariable::Closure<TValue> closure{col, layout, expected};
      LayoutCompilerUnitTests::LayoutCodeSwitch<VariableInterleaving>(
        col.GetType()->GetLayoutCode(), row, root, &closure);
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaVariable, "jthunter")
    {
      struct Make
      {
        // Helper functions to create sample arrays.
        static std::string S(size_t size)
        {
          std::string ret(size, 'a');
          for (size_t i = 0; i < size; i++)
          {
            ret[i] = static_cast<char>('a' + (i % 26));
          }

          return ret;
        }

        static std::vector<byte> B(size_t size)
        {
          std::vector<byte> ret{size};
          for (size_t i = 0; i < size; i++)
          {
            ret[i] = static_cast<byte>(i + 1);
          }

          return ret;
        }
      };

      ParseSchemaVariableCase(RoundTripVariable::Expected<std::string_view>{
        cdb_hr::TypeKind::Utf8,
        100,
        L"utf8"s,
        Make::S(2),
        Make::S(20),
        Make::S(100),
        Make::S(200)
      });

      ParseSchemaVariableCase(RoundTripVariable::Expected<cdb_core::ReadOnlySpan<byte>>{
        cdb_hr::TypeKind::Binary,
        100,
        L"binary"s,
        Make::B(2),
        Make::B(20),
        Make::B(100),
        Make::B(200)
      });

      ParseSchemaVariableCase(RoundTripVariable::Expected<int64_t>{
        cdb_hr::TypeKind::VarInt,
        0,
        L"varint"s,
        1i64,
        255i64,
        INT64_MAX,
        0i64
      });

      ParseSchemaVariableCase(RoundTripVariable::Expected<uint64_t>{
        cdb_hr::TypeKind::VarUInt,
        0,
        L"varuint"s,
        1ui64,
        255ui64,
        UINT64_MAX,
        0ui64
      });
    }

    struct RoundTripSparseOrdering final
    {
      template<typename TValue>
      struct Expected final
      {
        std::string_view Path;
        const cdb_hr::LayoutType& Type;
        TValue Value;
      };

      template<typename TValue>
      struct Closure final
      {
        std::wstring_view Tag;
        Expected<TValue>& Expected;
      };

      // Class Template Argument Deduction (CTAD) hint
      template<class TValue> Closure(std::wstring_view tag, Expected<TValue>& expected) -> Closure<TValue>;

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutType& type = closure.Expected.Type;
          std::string_view path = closure.Expected.Path;
          TValue exValue = closure.Expected.Value;
          std::wstring_view tag = closure.Tag;

          const TLayout& t = static_cast<const TLayout&>(type);
          TValue value = exValue;
          cdb_hr::RowCursor field = root.Clone().Find(row, path);
          cdb_hr::Result r = t.WriteSparse(row, field, value);
          ResultAssert::IsSuccess(r, tag);
          std::tie(r, value) = t.ReadSparse(row, field);
          ResultAssert::IsSuccess(r, tag);

          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(exValue, value, tag);
          }
          else
          {
            Assert::AreEqual(exValue, value, tag.data());
          }

          if (t.IsNull())
          {
            r = cdb_hr::LayoutLiteral::Boolean.WriteSparse(row, field, false);
            ResultAssert::IsSuccess(r, tag);
            std::tie(r, value) = t.ReadSparse(row, field);
            ResultAssert::TypeMismatch(r, tag);
          }
          else
          {
            r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
            ResultAssert::IsSuccess(r, tag);
            std::tie(r, value) = t.ReadSparse(row, field);
            ResultAssert::TypeMismatch(r, tag);
          }
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    template<typename ... TArgs>
    void SparseOrderingCase(std::tuple<TArgs...> expected)
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};
      auto schema = cdb_core::make_unique_with([&](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n) { n.GetSchemas().emplace_back(std::move(schema)); });
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      constexpr size_t N = std::tuple_size<decltype(expected)>::value;
      std::array<size_t, N> permutation{};
      for (size_t i = 0; i < N; i++)
      {
        permutation[i] = i;
      }

      do
      {
        // Make a tag from the permutation.
        std::wstring tag{};
        for (size_t i : permutation)
        {
          if (!tag.empty())
          {
            tag += L", ";
          }

          if (i == 0)
          {
            auto& field = std::get<0>(expected);
            tag += cdb_core::make_string(L"%S: %S", field.Path.data(), field.Type.GetName().data());
          }
          else if (i == 1)
          {
            auto& field = std::get<1>(expected);
            tag += cdb_core::make_string(L"%S: %S", field.Path.data(), field.Type.GetName().data());
          }
          if constexpr (N > 2)
          {
            if (i == 2)
            {
              auto& field = std::get<2>(expected);
              tag += cdb_core::make_string(L"%S: %S", field.Path.data(), field.Type.GetName().data());
            }
            else if (i == 3)
            {
              auto& field = std::get<3>(expected);
              tag += cdb_core::make_string(L"%S: %S", field.Path.data(), field.Type.GetName().data());
            }
          }
        }
        tag += L"\n";
        Logger::WriteMessage(tag.data());

        // Run test on the permutation.
        row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);
        for (size_t i : permutation)
        {
          cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
          if (i == 0)
          {
            RoundTripSparseOrdering::Closure closure{tag, std::get<0>(expected)};
            LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseOrdering>(
              closure.Expected.Type.GetLayoutCode(), row, root, &closure);
          }
          else if (i == 1)
          {
            RoundTripSparseOrdering::Closure closure{tag, std::get<1>(expected)};
            LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseOrdering>(
              closure.Expected.Type.GetLayoutCode(), row, root, &closure);
          }
          if constexpr (N > 2)
          {
            if (i == 2)
            {
              RoundTripSparseOrdering::Closure closure{tag, std::get<2>(expected)};
              LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseOrdering>(
                closure.Expected.Type.GetLayoutCode(), row, root, &closure);
            }
            else if (i == 3)
            {
              RoundTripSparseOrdering::Closure closure{tag, std::get<3>(expected)};
              LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseOrdering>(
                closure.Expected.Type.GetLayoutCode(), row, root, &closure);
            }
          }
        }
      } while (std::next_permutation(permutation.begin(), permutation.end()));
    }

  TEST_METHOD_WITH_OWNER(SparseOrdering, "jthunter")
    {
      // Test various orderings of multiple sparse column types.
      SparseOrderingCase(std::make_tuple(
        RoundTripSparseOrdering::Expected<std::string_view>{"a"sv, cdb_hr::LayoutLiteral::Utf8, "aa"sv},
        RoundTripSparseOrdering::Expected<std::string_view>{"b"sv, cdb_hr::LayoutLiteral::Utf8, "bb"sv}
      ));

      SparseOrderingCase(std::make_tuple(
        RoundTripSparseOrdering::Expected<std::int64_t>{"a"sv, cdb_hr::LayoutLiteral::VarInt, 42i64},
        RoundTripSparseOrdering::Expected<std::int64_t>{"b"sv, cdb_hr::LayoutLiteral::Int64, 43i64}
      ));

      SparseOrderingCase(std::make_tuple(
        RoundTripSparseOrdering::Expected<std::int64_t>{"a"sv, cdb_hr::LayoutLiteral::VarInt, 42i64},
        RoundTripSparseOrdering::Expected<std::string_view>{"b"sv, cdb_hr::LayoutLiteral::Utf8, "aa"sv},
        RoundTripSparseOrdering::Expected<cdb_hr::NullValue>{"b"sv, cdb_hr::LayoutLiteral::Null, {}},
        RoundTripSparseOrdering::Expected<bool>{"b"sv, cdb_hr::LayoutLiteral::Boolean, true}
      ));
    }

    struct RoundTripSparseSimple final
    {
      template<typename TValue>
      struct Expected final
      {
        cdb_hr::TypeKind TypeKind;
        std::wstring_view Tag;
        TValue Value;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& Col;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          Expected<TValue>& expected = closure.Expected;

          Logger::WriteMessage(col.GetType()->GetName().data());
          const TLayout& t = *static_cast<const TLayout*>(col.GetType());
          cdb_hr::RowCursor field = root.Clone().Find(row, col.GetPath());
          auto [r, value] = t.ReadSparse(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());
          r = t.WriteSparse(row, field, expected.Value, cdb_hr::UpdateOptions::Update);
          ResultAssert::NotFound(r, expected.Tag.data());
          r = t.WriteSparse(row, field, expected.Value);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          r = t.WriteSparse(row, field, expected.Value, cdb_hr::UpdateOptions::Insert);
          ResultAssert::Exists(r, expected.Tag.data());
          std::tie(r, value) = t.ReadSparse(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(expected.Value, value, expected.Tag.data());
          }
          else
          {
            Assert::AreEqual(expected.Value, value, expected.Tag.data());
          }

          cdb_hr::RowCursor roRoot = root.AsReadOnly().Find(row, col.GetPath());
          ResultAssert::InsufficientPermissions(t.DeleteSparse(row, roRoot));
          ResultAssert::InsufficientPermissions(t.WriteSparse(row, roRoot, expected.Value, cdb_hr::UpdateOptions::Update));

          if (t.IsNull())
          {
            r = cdb_hr::LayoutLiteral::Boolean.WriteSparse(row, field, false);
            ResultAssert::IsSuccess(r, expected.Tag.data());
            std::tie(r, value) = t.ReadSparse(row, field);
            ResultAssert::TypeMismatch(r, expected.Tag.data());
          }
          else
          {
            r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
            ResultAssert::IsSuccess(r, expected.Tag.data());
            std::tie(r, value) = t.ReadSparse(row, field);
            ResultAssert::TypeMismatch(r, expected.Tag.data());
          }

          r = t.DeleteSparse(row, field);
          ResultAssert::TypeMismatch(r, expected.Tag.data());

          // Overwrite it again, then delete it.
          r = t.WriteSparse(row, field, expected.Value, cdb_hr::UpdateOptions::Update);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          r = t.DeleteSparse(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          std::tie(r, value) = t.ReadSparse(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    template<typename TValue>
    void ParseSchemaSparseSimpleCase(cdb_hr::RowBuffer& row, RoundTripSparseSimple::Expected<TValue> expected)
    {
      auto schema = cdb_core::make_unique_with([&](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
        {
          p.SetPath("a"s);
          p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
          {
            pt.SetType(expected.TypeKind);
            pt.SetStorage(cdb_hr::StorageKind::Sparse);
          }));
        }));
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.GetSchemas().emplace_back(std::move(schema));
      });
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      Assert::AreEqual(size_t{1}, layout.GetColumns().size(), expected.Tag.data());
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);

      Assert::AreEqual(cdb_hr::StorageKind::Sparse, col.GetStorage(), expected.Tag.data());

      // Try writing a row using the layout.
      row.Reset();
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
      RoundTripSparseSimple::Closure<TValue> closure{col, expected};
      LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseSimple>(
        col.GetType()->GetLayoutCode(), row, root, &closure);
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseSimple, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      // Test all sparse column types.
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<cdb_hr::NullValue>{cdb_hr::TypeKind::Null, L"null"sv, {}});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<bool>{cdb_hr::TypeKind::Boolean, L"bool"sv, true});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<bool>{cdb_hr::TypeKind::Boolean, L"bool"sv, false});

      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<int8_t>{cdb_hr::TypeKind::Int8, L"int8"sv, int8_t{42}});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<int16_t>{cdb_hr::TypeKind::Int16, L"int16"sv, int16_t{42}});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<int32_t>{cdb_hr::TypeKind::Int32, L"int32"sv, 42});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<int64_t>{cdb_hr::TypeKind::Int64, L"int64"sv, 42i64});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<uint8_t>{cdb_hr::TypeKind::UInt8, L"uint8"sv, uint8_t{42}});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<uint16_t>{cdb_hr::TypeKind::UInt16, L"uint16"sv, uint16_t{42}});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<uint32_t>{cdb_hr::TypeKind::UInt32, L"uint32"sv, 42ui32});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<uint64_t>{cdb_hr::TypeKind::UInt64, L"uint64"sv, 42ui64});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<int64_t>{cdb_hr::TypeKind::VarInt, L"varint"sv, 42i64});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<uint64_t>{cdb_hr::TypeKind::VarUInt, L"varuint"sv, 42ui64});

      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<float32_t>{cdb_hr::TypeKind::Float32, L"float32"sv, 4.2F});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<float64_t>{cdb_hr::TypeKind::Float64, L"float64"sv, 4.2});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<float128_t>{cdb_hr::TypeKind::Float128, L"float128"sv, {0, 42}});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<cdb_hr::Decimal>{cdb_hr::TypeKind::Decimal, L"decimal"sv, {42}});

      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<cdb_hr::DateTime>{cdb_hr::TypeKind::DateTime, L"datetime"sv, {42}});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<cdb_hr::UnixDateTime>{cdb_hr::TypeKind::UnixDateTime, L"unixdatetime"sv, {42}});

      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<cdb_hr::Guid>{cdb_hr::TypeKind::Guid, L"guid"sv, cdb_hr::Guid::NewGuid()});
      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<cdb_hr::MongoDbObjectId>{cdb_hr::TypeKind::MongoDbObjectId, L"mongodbobjectid"sv, {0, 42}});

      ParseSchemaSparseSimpleCase(row,
        RoundTripSparseSimple::Expected<std::string_view>{cdb_hr::TypeKind::Utf8, L"utf8"sv, "AB"});
      ParseSchemaSparseSimpleCase(row, RoundTripSparseSimple::Expected<cdb_core::ReadOnlySpan<byte>>{
        cdb_hr::TypeKind::Binary,
        L"binary"sv,
        std::array<byte, 2>{{byte{0x01}, byte{0x02}}}
      });
    }

    template<typename TValue, template<typename> typename TExpected>
    void ParseSchemaUDTCase(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& udtScope1, std::string_view path, TExpected<TValue> expected)
    {
      const cdb_hr::Layout& layout = udtScope1.GetLayout();
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind(path);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);

      if constexpr (std::is_same_v<TExpected<TValue>, RoundTripFixed::Expected<TValue>>)
      {
        RoundTripFixed::Closure<TValue> closure{col, expected};
        LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripFixed>(
          col.GetType()->GetLayoutCode(),
          row,
          udtScope1,
          &closure);
      }
      else
      {
        RoundTripVariable::Closure<TValue> closure{col, layout, expected};
        LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripVariable>(
          col.GetType()->GetLayoutCode(),
          row,
          udtScope1,
          &closure);
      }
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaUDT, "jthunter")
    {
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.SetName("myNamespace");
        n.GetSchemas().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("udtA"s);
          s.SetSchemaId(cdb_hr::SchemaId{1});
          s.SetOptions(cdb_core::make_unique_with([&](cdb_hr::SchemaOptions& so)
          {
            so.SetDisallowUnschematized(false);
          }));
          s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
          {
            p.SetPath("a"s);
            p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Int8);
              pt.SetStorage(cdb_hr::StorageKind::Fixed);
            }));
          }));
          s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(cdb_core::make_unique_with([&](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Utf8);
              pt.SetStorage(cdb_hr::StorageKind::Variable);
              pt.SetLength(100);
            }));
          }));
        }));
        n.GetSchemas().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("udtB"s);
          s.SetSchemaId(cdb_hr::SchemaId{2});
        }));
        n.GetSchemas().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("udtB"s);
          s.SetSchemaId(cdb_hr::SchemaId{3});
        }));
        n.GetSchemas().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("udtB"s);
          s.SetSchemaId(cdb_hr::SchemaId{4});
        }));
        n.GetSchemas().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Schema& s)
        {
          s.SetName("table"s);
          s.SetSchemaId(cdb_hr::SchemaId{-1});
          s.SetOptions(cdb_core::make_unique_with([&](cdb_hr::SchemaOptions& so)
          {
            so.SetDisallowUnschematized(false);
          }));
          s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
          {
            p.SetPath("u"s);
            p.SetPropertyType(std::make_unique<cdb_hr::UdtPropertyType>("udtA"));
          }));
          s.GetProperties().emplace_back(cdb_core::make_unique_with([&](cdb_hr::Property& p)
          {
            p.SetPath("v"s);
            p.SetPropertyType(std::make_unique<cdb_hr::UdtPropertyType>("udtB", cdb_hr::SchemaId{3}));
          }));
        }));
      });
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      std::wstring_view tag = L"Tag: myNamespace"sv;

      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("u"sv);
      Assert::IsTrue(std::get<0>(found), tag.data());
      const cdb_hr::LayoutColumn& udtACol = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, udtACol.GetStorage(), tag.data());

      // Verify that UDT versioning works through schema references.
      found = layout.TryFind("v"sv);
      Assert::IsTrue(std::get<0>(found), tag.data());
      const cdb_hr::LayoutColumn& udtBCol = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, udtBCol.GetStorage(), tag.data());
      Assert::AreEqual(static_cast<const cdb_hr::LayoutType*>(&cdb_hr::LayoutLiteral::UDT), udtBCol.GetType());
      Assert::AreEqual(cdb_hr::SchemaId{3}, udtBCol.GetTypeArgs().GetSchemaId());

      const cdb_hr::Layout& udtLayout = resolver.Resolve(cdb_hr::SchemaId{1});
      row.Reset();
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      // Verify the udt doesn't yet exist.
      cdb_hr::RowCursor scope = cdb_hr::RowCursor::Create(row).Find(row, udtACol.GetPath());
      auto [r, scope2] = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
      ResultAssert::NotFound(r, tag);
      cdb_hr::RowCursor udtScope1;
      std::tie(r, udtScope1) = cdb_hr::LayoutLiteral::UDT.WriteScope(row, scope, udtACol.GetTypeArgs());
      ResultAssert::IsSuccess(r, tag);
      cdb_hr::RowCursor udtScope2;
      std::tie(r, udtScope2) = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
      ResultAssert::IsSuccess(r, tag);
      Assert::AreSame(udtLayout, udtScope2.GetLayout(), tag.data());
      Assert::AreEqual(udtScope1.GetScopeType(), udtScope2.GetScopeType(), tag.data());
      Assert::AreEqual(udtScope1.m_start, udtScope2.m_start, tag.data());
      Assert::AreEqual(udtScope1.GetImmutable(), udtScope2.GetImmutable(), tag.data());

      ParseSchemaUDTCase<int8_t>(row, udtScope1, "a", RoundTripFixed::Expected<int8_t>{
        cdb_hr::TypeKind::Int8,
        int8_t{0},
        int8_t{42},
        0,
        L"{ 'type': 'int8', 'storage': 'fixed' }"s
      });
      ParseSchemaUDTCase<std::string_view>(row, udtScope1, "b", RoundTripVariable::Expected<std::string_view>{
        cdb_hr::TypeKind::Utf8,
        2,
        L"{ 'type': 'utf8', 'storage': 'variable' }"s,
        "",
        "AB",
        "",
        ""
      });

      cdb_hr::RowCursor roRoot = cdb_hr::RowCursor::Create(row).AsReadOnly().Find(row, udtACol.GetPath());
      ResultAssert::InsufficientPermissions(udtACol.TypeAs<cdb_hr::LayoutUDT>().DeleteScope(row, roRoot));
      std::tie(r, udtScope2) = udtACol.TypeAs<cdb_hr::LayoutUDT>().WriteScope(row, roRoot, udtACol.GetTypeArgs());
      ResultAssert::InsufficientPermissions(r);

      // Overwrite the whole scope.
      scope = std::move(cdb_hr::RowCursor::Create(row).Find(row, udtACol.GetPath()));
      r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, scope, cdb_hr::NullValue{});
      ResultAssert::IsSuccess(r, tag);
      std::tie(r, std::ignore) = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
      ResultAssert::TypeMismatch(r, tag);
      r = cdb_hr::LayoutLiteral::UDT.DeleteScope(row, scope);
      ResultAssert::TypeMismatch(r, tag);

      // Overwrite it again, then delete it.
      scope = std::move(cdb_hr::RowCursor::Create(row).Find(row, udtACol.GetPath()));
      std::tie(r, std::ignore) = cdb_hr::LayoutLiteral::UDT.WriteScope(row, scope, udtACol.GetTypeArgs());
      ResultAssert::IsSuccess(r, tag);
      r = cdb_hr::LayoutLiteral::UDT.DeleteScope(row, scope);
      ResultAssert::IsSuccess(r, tag);
      scope = std::move(cdb_hr::RowCursor::Create(row).Find(row, udtACol.GetPath()));
      std::tie(r, std::ignore) = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
      ResultAssert::NotFound(r, tag);
    }

    struct RoundTripSparseObject final
    {
      template<typename TValue>
      struct Expected final
      {
        cdb_hr::TypeKind Type;
        std::wstring_view Tag;
        TValue Value;
      };

      template<typename TValue>
      struct Closure
      {
        const cdb_hr::LayoutColumn& ObjCol;
        const cdb_hr::LayoutColumn& Col;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& objCol = closure.ObjCol;
          const cdb_hr::LayoutObject& objT = objCol.TypeAs<cdb_hr::LayoutObject>();
          const cdb_hr::LayoutColumn& col = closure.Col;
          Expected<TValue>& expected = closure.Expected;

          Logger::WriteMessage(col.GetType()->GetName().data());
          Assert::AreSame(objCol, *col.GetParent(), expected.Tag.data());

          const TLayout& t = col.GetType()->TypeAs<TLayout>();

          // Attempt to read the object and the nested column.
          cdb_hr::RowCursor field = root.Clone().Find(row, objCol.GetPath());
          auto [r, scope] = objT.ReadScope(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());

          // Write the object and the nested column.
          std::tie(r, scope) = objT.WriteScope(row, field, objCol.GetTypeArgs());
          ResultAssert::IsSuccess(r, expected.Tag.data());

          // Verify the nested field doesn't yet appear within the new scope.
          cdb_hr::RowCursor nestedField = scope.Clone().Find(row, col.GetPath());
          TValue value;
          std::tie(r, value) = t.ReadSparse(row, nestedField);
          ResultAssert::NotFound(r, expected.Tag.data());

          // Write the nested field.
          r = t.WriteSparse(row, nestedField, expected.Value);
          ResultAssert::IsSuccess(r, expected.Tag.data());

          // Read the object and the nested column, validate the nested column has the proper value.
          cdb_hr::RowCursor scope2;
          std::tie(r, scope2) = objT.ReadScope(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          Assert::AreEqual(scope.GetScopeType(), scope2.GetScopeType(), expected.Tag.data());
          Assert::AreEqual(scope.m_start, scope2.m_start, expected.Tag.data());
          Assert::AreEqual(scope.GetImmutable(), scope2.GetImmutable(), expected.Tag.data());

          // Read the nested field
          nestedField = scope2.Clone().Find(row, col.GetPath());
          std::tie(r, value) = t.ReadSparse(row, nestedField);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(expected.Value, value, expected.Tag.data());
          }
          else
          {
            Assert::AreEqual(expected.Value, value, expected.Tag.data());
          }

          cdb_hr::RowCursor roRoot = root.AsReadOnly().Find(row, objCol.GetPath());
          ResultAssert::InsufficientPermissions(objT.DeleteScope(row, roRoot));
          std::tie(r, scope2) = objT.WriteScope(row, roRoot, objCol.GetTypeArgs());
          ResultAssert::InsufficientPermissions(r);

          // Overwrite the whole scope.
          r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
          ResultAssert::IsSuccess(r, expected.Tag.data());
          std::tie(r, std::ignore) = objT.ReadScope(row, field);
          ResultAssert::TypeMismatch(r, expected.Tag.data());
          r = objT.DeleteScope(row, field);
          ResultAssert::TypeMismatch(r, expected.Tag.data());

          // Overwrite it again, then delete it.
          std::tie(r, std::ignore) = objT.WriteScope(row, field, objCol.GetTypeArgs(), cdb_hr::UpdateOptions::Update);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          r = objT.DeleteScope(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          std::tie(r, std::ignore) = objT.ReadScope(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseObject, "jthunter")
    {
      RoundTripSparseObject::Expected<int8_t> expected{cdb_hr::TypeKind::Int8, L"int8"sv, int8_t{42}};
      auto schema = cdb_core::make_unique_with([](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
        s.GetProperties().emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
        {
          p.SetPath("a"s);
          p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::ObjectPropertyType& pt)
          {
            pt.GetProperties().emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& innerP)
            {
              innerP.SetPath("b"s);
              innerP.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& innerPT)
              {
                innerPT.SetType(cdb_hr::TypeKind::Int8);
              }));
            }));
          }));
        }));
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.GetSchemas().emplace_back(std::move(schema));
      });

      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      Assert::AreEqual(size_t{1}, layout.GetColumns().size(), expected.Tag.data());
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& objCol = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, objCol.GetStorage(), expected.Tag.data());

      found = layout.TryFind("a.b"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, objCol.GetStorage(), expected.Tag.data());

      // Try writing a row using the layout.
      row.Reset();
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
      RoundTripSparseObject::Closure<int8_t> closure{objCol, col, expected};
      LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseObject>(
        col.GetType()->GetLayoutCode(),
        row,
        root,
        &closure);
    }

    struct RoundTripSparseObjectMulti final
    {
      template<typename TValue>
      struct ExpectedProperty final
      {
        std::string_view Path;
        TValue Value;
      };

      template<typename ...TArgs>
      struct Expected final
      {
        std::wstring_view Tag;
        std::vector<std::unique_ptr<cdb_hr::Property>> Props;
        std::tuple<ExpectedProperty<TArgs>...> ExpectedProps;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& Col;
        ExpectedProperty<TValue>& Prop;
        std::wstring_view Tag;
      };

      // Class Template Argument Deduction (CTAD) hint
      template<typename TValue> Closure(
        const cdb_hr::LayoutColumn& col, ExpectedProperty<TValue>& prop, std::wstring_view tag) -> Closure<TValue>;

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          ExpectedProperty<TValue>& prop = closure.Prop;
          std::wstring tag = cdb_core::make_string(L"Prop: %S: Tag: %s", prop.Path.data(), closure.Tag.data());

          Logger::WriteMessage(tag.data());

          const TLayout& t = col.TypeAs<TLayout>();

          // Verify the nested field doesn't yet appear within the new scope.
          cdb_hr::RowCursor nestedField = root.Clone().Find(row, col.GetPath());
          auto [r, value] = t.ReadSparse(row, nestedField);
          Assert::IsTrue(r == cdb_hr::Result::NotFound || r == cdb_hr::Result::TypeMismatch, tag.data());

          // Write the nested field.
          r = t.WriteSparse(row, nestedField, prop.Value);
          ResultAssert::IsSuccess(r, tag);

          // Read the nested field
          std::tie(r, value) = t.ReadSparse(row, nestedField);
          ResultAssert::IsSuccess(r, tag);
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(prop.Value, value, tag);
          }
          else
          {
            Assert::AreEqual(prop.Value, value, tag.data());
          }

          // Overwrite the nested field.
          if (t.IsNull())
          {
            r = cdb_hr::LayoutLiteral::Boolean.WriteSparse(row, nestedField, false);
            ResultAssert::IsSuccess(r, tag);
          }
          else
          {
            r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, nestedField, cdb_hr::NullValue{});
            ResultAssert::IsSuccess(r, tag);
          }

          // Verify nested field no longer there.
          std::tie(r, value) = t.ReadSparse(row, nestedField);
          ResultAssert::TypeMismatch(r, tag);
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          ExpectedProperty<nullptr_t>& prop = closure.Prop;
          std::wstring tag = cdb_core::make_string(L"Prop: %S: Tag: %s", prop.Path.data(), closure.Tag.data());

          Logger::WriteMessage(tag.data());

          const cdb_hr::LayoutObject& t = col.TypeAs<cdb_hr::LayoutObject>();

          // Verify the nested field doesn't yet appear within the new scope.
          cdb_hr::RowCursor nestedField = scope.Clone().Find(row, col.GetPath());
          auto [r, scope2] = t.ReadScope(row, nestedField);
          ResultAssert::NotFound(r, tag);

          // Write the nested field.
          std::tie(r, scope2) = t.WriteScope(row, nestedField, col.GetTypeArgs());
          ResultAssert::IsSuccess(r, tag);

          // Read the nested field
          cdb_hr::RowCursor scope3;
          std::tie(r, scope3) = t.ReadScope(row, nestedField);
          ResultAssert::IsSuccess(r, tag);
          Assert::AreEqual(scope2.AsReadOnly().GetScopeType(), scope3.GetScopeType(), tag.data());
          Assert::AreEqual(scope2.AsReadOnly().m_start, scope3.m_start, tag.data());

          // Overwrite the nested field.
          r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, nestedField, cdb_hr::NullValue{});
          ResultAssert::IsSuccess(r, tag);

          // Verify nested field no longer there.
          std::tie(r, scope3) = t.ReadScope(row, nestedField);
          ResultAssert::TypeMismatch(r, tag);
        }
      };
    };

    template<typename TValue>
    static void ParseSchemaSparseObjectMultiValue(
      cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const cdb_hr::Layout& layout,
      RoundTripSparseObjectMulti::ExpectedProperty<TValue>& prop,
      std::wstring_view tag)
    {
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind(prop.Path);
      Assert::IsTrue(std::get<0>(found), tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, col.GetStorage(), tag.data());

      RoundTripSparseObjectMulti::Closure closure{col, prop, tag};
      LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseObjectMulti>(
        col.GetType()->GetLayoutCode(), row, scope, &closure);
    }

    template<typename ...TArgs>
    static void ParseSchemaSparseObjectMultiCase(cdb_hr::RowBuffer& row,
                                                 RoundTripSparseObjectMulti::Expected<TArgs...> expected)
    {
      auto schema = cdb_core::make_unique_with([&expected](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&expected](cdb_hr::Property& p)
        {
          p.SetPath("a"s);
          p.SetPropertyType(cdb_core::make_unique_with([&expected](cdb_hr::ObjectPropertyType& pt)
          {
            for (auto& pp : expected.Props)
            {
              pt.GetProperties().emplace_back(std::move(pp));
            }
          }));
        }));
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.GetSchemas().emplace_back(std::move(schema));
      });
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      Assert::AreEqual(size_t{1}, layout.GetColumns().size(), expected.Tag.data());
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& objCol = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, objCol.GetStorage(), expected.Tag.data());

      // Try writing a row using the layout.
      row.Reset();
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      // Verify the object doesn't exist.
      const cdb_hr::LayoutObject& objT = objCol.TypeAs<cdb_hr::LayoutObject>();
      cdb_hr::RowCursor field = cdb_hr::RowCursor::Create(row).Find(row, objCol.GetPath());
      auto [r, scope] = objT.ReadScope(row, field);
      ResultAssert::NotFound(r, expected.Tag.data());

      // Write the object and the nested column.
      std::tie(r, scope) = objT.WriteScope(row, field, objCol.GetTypeArgs());
      ResultAssert::IsSuccess(r, expected.Tag.data());

      constexpr size_t N = std::tuple_size<std::tuple<TArgs...>>::value;
      std::array<size_t, N> permutation{};
      for (size_t i = 0; i < N; i++) { permutation[i] = i; }

      do
      {
        for (size_t i : permutation)
        {
          if (i == 0)
          {
            LayoutCompilerUnitTests::ParseSchemaSparseObjectMultiValue(row, scope, layout,
              std::get<0>(expected.ExpectedProps), expected.Tag);
          }

          if constexpr (N > 1)
          {
            if (i == 1)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectMultiValue(row, scope, layout,
                std::get<1>(expected.ExpectedProps), expected.Tag);
            }
          }

          if constexpr (N > 2)  // NOLINT(readability-misleading-indentation)
          {
            if (i == 2)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectMultiValue(row, scope, layout,
                std::get<2>(expected.ExpectedProps), expected.Tag);
            }
          }

          if constexpr (N > 3)  // NOLINT(readability-misleading-indentation)
          {
            if (i == 3)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectMultiValue(row, scope, layout,
                std::get<3>(expected.ExpectedProps), expected.Tag);
            }
          }
        }
      } while (std::next_permutation(permutation.begin(), permutation.end()));

      // Write something after the scope.
      std::string otherColumnPath = cdb_core::make_string("not-%s", objCol.GetPath());
      cdb_hr::RowCursor otherColumn = field.Clone().Find(row, otherColumnPath);
      r = cdb_hr::LayoutLiteral::Boolean.WriteSparse(row, otherColumn, true);
      ResultAssert::IsSuccess(r, expected.Tag.data());

      // Overwrite the whole scope.
      r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
      ResultAssert::IsSuccess(r, expected.Tag.data());
      std::tie(r, std::ignore) = objT.ReadScope(row, field);
      ResultAssert::TypeMismatch(r, expected.Tag.data());

      // Read the thing after the scope and verify it is still there.
      otherColumn = field.Clone().Find(row, otherColumnPath);
      bool notScope;
      std::tie(r, notScope) = cdb_hr::LayoutLiteral::Boolean.ReadSparse(row, otherColumn);
      ResultAssert::IsSuccess(r, expected.Tag.data());
      Assert::IsTrue(notScope, expected.Tag.data());
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseObjectMulti, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      ParseSchemaSparseObjectMultiCase(row, RoundTripSparseObjectMulti::Expected<int8_t>
      {
        L"{'path': 'b', 'type': {'type': 'int8'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Int8);
            }));
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectMulti::ExpectedProperty<int8_t>{"a.b"sv, int8_t{42}}
        )
      });

      ParseSchemaSparseObjectMultiCase(row, RoundTripSparseObjectMulti::Expected<int8_t, std::string_view>
      {
        L"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Int8);
            }));
          }));
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("c"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Utf8);
            }));
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectMulti::ExpectedProperty<int8_t>{"a.b"sv, int8_t{42}},
          RoundTripSparseObjectMulti::ExpectedProperty<std::string_view>{"a.c"sv, "abc"sv}
        )
      });

      ParseSchemaSparseObjectMultiCase(row,
        RoundTripSparseObjectMulti::Expected<int8_t, bool, cdb_core::ReadOnlySpan<byte>, cdb_hr::NullValue>
        {
          L"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'bool'}}, {'path': 'd', 'type': {'type': 'binary'}}, {'path': 'e', 'type': {'type': 'null'}}"sv,
          cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
          {
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("b"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Int8);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("c"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Boolean);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("d"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Binary);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("e"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Null);
              }));
            }));
          }),
          std::make_tuple(
            RoundTripSparseObjectMulti::ExpectedProperty<int8_t>{"a.b"sv, int8_t{42}},
            RoundTripSparseObjectMulti::ExpectedProperty<bool>{"a.c"sv, true},
            RoundTripSparseObjectMulti::ExpectedProperty<cdb_core::ReadOnlySpan<byte>>{
              "a.d"sv,
              std::array<byte, 3>{{byte{0x01}, byte{0x02}, byte{0x03}}}
            },
            RoundTripSparseObjectMulti::ExpectedProperty<cdb_hr::NullValue>{"a.e"sv, {}}
          )
        });

      ParseSchemaSparseObjectMultiCase(row, RoundTripSparseObjectMulti::Expected<nullptr_t>
      {
        L"{'path': 'b', 'type': {'type': 'object'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(std::make_unique<cdb_hr::ObjectPropertyType>());
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectMulti::ExpectedProperty<nullptr_t>{"a.b"sv, nullptr}
        )
      });
    }

    struct RoundTripSparseObjectNested final
    {
      template<typename TValue>
      struct ExpectedProperty
      {
        std::string_view Path;
        TValue Value;
      };

      template<typename ...TArgs>
      struct Expected final
      {
        std::wstring_view Tag;
        std::vector<std::unique_ptr<cdb_hr::Property>> Props;
        std::tuple<ExpectedProperty<TArgs>...> ExpectedProps;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& Col;
        ExpectedProperty<TValue>& Prop;
        std::wstring_view Tag;
      };

      // Class Template Argument Deduction (CTAD) hint
      template<typename TValue> Closure(
        const cdb_hr::LayoutColumn& col, ExpectedProperty<TValue>& prop, std::wstring_view tag) -> Closure<TValue>;

      /// <summary>Ensure that a parent scope exists in the row.</summary>
      /// <param name="row">The row to create the desired scope.</param>
      /// <param name="root">The root scope.</param>
      /// <param name="col">The scope to create.</param>
      /// <param name="tag">A string to tag errors with.</param>
      /// <returns>The enclosing scope.</returns>
      static cdb_hr::RowCursor EnsureScope(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, const cdb_hr::LayoutColumn* col, std::wstring_view tag)
      {
        if (col == nullptr)
        {
          return root;
        }

        cdb_hr::RowCursor parentScope = RoundTripSparseObjectNested::EnsureScope(row, root, col->GetParent(), tag);

        const cdb_hr::LayoutObject& pT = col->GetType()->TypeAs<cdb_hr::LayoutObject>();
        cdb_hr::RowCursor field = parentScope.Clone().Find(row, col->GetPath());
        auto [r, scope] = pT.ReadScope(row, field);
        if (r == cdb_hr::Result::NotFound)
        {
          std::tie(r, scope) = pT.WriteScope(row, field, col->GetTypeArgs());
        }

        ResultAssert::IsSuccess(r, tag);
        return scope;
      }

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          ExpectedProperty<TValue>& prop = closure.Prop;
          std::wstring tag = cdb_core::make_string(L"Prop: %S: Tag: %s", prop.Path.data(), closure.Tag.data());
          Logger::WriteMessage(tag.data());

          const TLayout& t = col.GetType()->TypeAs<TLayout>();

          // Ensure scope exists.
          cdb_hr::RowCursor scope = RoundTripSparseObjectNested::EnsureScope(row, root, col.GetParent(), tag);

          // Write the nested field.
          cdb_hr::RowCursor field = scope.Clone().Find(row, col.GetPath());
          cdb_hr::Result r = t.WriteSparse(row, field, prop.Value);
          ResultAssert::IsSuccess(r, tag);

          // Read the nested field
          TValue value;
          std::tie(r, value) = t.ReadSparse(row, field);
          ResultAssert::IsSuccess(r, tag);
          if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
          {
            CollectionAssert::AreEqual(prop.Value, value, tag);
          }
          else
          {
            Assert::AreEqual(prop.Value, value, tag.data());
          }

          // Overwrite the nested field.
          if (t.IsNull())
          {
            r = cdb_hr::LayoutLiteral::Boolean.WriteSparse(row, field, false);
            ResultAssert::IsSuccess(r, tag);
          }
          else
          {
            r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
            ResultAssert::IsSuccess(r, tag);
          }

          // Verify nested field no longer there.
          std::tie(r, value) = t.ReadSparse(row, field);
          ResultAssert::TypeMismatch(r, tag);
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<std::nullptr_t>& closure) override
        {
          const cdb_hr::LayoutColumn& col = closure.Col;
          ExpectedProperty<nullptr_t>& prop = closure.Prop;
          std::wstring tag = cdb_core::make_string(L"Prop: %S: Tag: %s", prop.Path.data(), closure.Tag.data());
          Logger::WriteMessage(tag.data());

          // Ensure scope exists.
          cdb_hr::RowCursor scope = RoundTripSparseObjectNested::EnsureScope(row, root, &col, tag);
          Assert::AreNotEqual(root.m_start, scope.m_start, tag.data());
        }
      };
    };

    template<typename TValue>
    static void ParseSchemaSparseObjectNestedValue(
      cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const cdb_hr::Layout& layout,
      RoundTripSparseObjectNested::ExpectedProperty<TValue>& prop,
      std::wstring_view tag)
    {
      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind(prop.Path);
      Assert::IsTrue(std::get<0>(found), tag.data());
      const cdb_hr::LayoutColumn& col = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, col.GetStorage(), tag.data());

      RoundTripSparseObjectNested::Closure closure{col, prop, tag};
      LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseObjectNested>(
        col.GetType()->GetLayoutCode(), row, scope, &closure);
    }

    template<typename ...TArgs>
    static void ParseSchemaSparseObjectNestedCase(cdb_hr::RowBuffer& row,
                                                  RoundTripSparseObjectNested::Expected<TArgs...> expected)
    {
      auto schema = cdb_core::make_unique_with([&expected](cdb_hr::Schema& s)
      {
        s.SetName("table"s);
        s.SetSchemaId(cdb_hr::SchemaId{-1});
        s.GetProperties().emplace_back(cdb_core::make_unique_with([&expected](cdb_hr::Property& p)
        {
          p.SetPath("a"s);
          p.SetPropertyType(cdb_core::make_unique_with([&expected](cdb_hr::ObjectPropertyType& pt)
          {
            pt.GetProperties().emplace_back(cdb_core::make_unique_with([&expected](cdb_hr::Property& p2)
            {
              p2.SetPath("b"s);
              p2.SetPropertyType(cdb_core::make_unique_with([&expected](cdb_hr::ObjectPropertyType& pt2)
              {
                for (auto& pp : expected.Props)
                {
                  pt2.GetProperties().emplace_back(std::move(pp));
                }
              }));
            }));
          }));
        }));
      });
      auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
      {
        n.GetSchemas().emplace_back(std::move(schema));
      });
      cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

      std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& objCol = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, objCol.GetStorage(), expected.Tag.data());
      found = layout.TryFind("a.b"sv);
      Assert::IsTrue(std::get<0>(found), expected.Tag.data());
      const cdb_hr::LayoutColumn& objCol2 = *std::get<1>(found);
      Assert::AreEqual(cdb_hr::StorageKind::Sparse, objCol2.GetStorage(), expected.Tag.data());

      // Try writing a row using the layout.
      row.Reset();
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);
      cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);

      cdb_hr::HybridRowHeader header = row.GetHeader();
      Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
      Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

      // Write the object.
      const cdb_hr::LayoutObject& objT = objCol.TypeAs<cdb_hr::LayoutObject>();
      cdb_hr::RowCursor field = root.Clone().Find(row, objCol.GetPath());
      auto [r, _] = objT.WriteScope(row, field, objCol.GetTypeArgs());
      ResultAssert::IsSuccess(r, expected.Tag.data());

      constexpr size_t N = std::tuple_size<std::tuple<TArgs...>>::value;
      std::array<size_t, N> permutation{};
      for (size_t i = 0; i < N; i++) { permutation[i] = i; }

      do
      {
        for (size_t i : permutation)
        {
          if (i == 0)
          {
            LayoutCompilerUnitTests::ParseSchemaSparseObjectNestedValue(row, root, layout,
              std::get<0>(expected.ExpectedProps), expected.Tag);
          }

          if constexpr (N > 1)
          {
            if (i == 1)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectNestedValue(row, root, layout,
                std::get<1>(expected.ExpectedProps), expected.Tag);
            }
          }

          if constexpr (N > 2)  // NOLINT(readability-misleading-indentation)
          {
            if (i == 2)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectNestedValue(row, root, layout,
                std::get<2>(expected.ExpectedProps), expected.Tag);
            }
          }

          if constexpr (N > 3)  // NOLINT(readability-misleading-indentation)
          {
            if (i == 3)
            {
              LayoutCompilerUnitTests::ParseSchemaSparseObjectNestedValue(row, root, layout,
                std::get<3>(expected.ExpectedProps), expected.Tag);
            }
          }
        }
      } while (std::next_permutation(permutation.begin(), permutation.end()));
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseObjectNested, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      ParseSchemaSparseObjectNestedCase(row, RoundTripSparseObjectNested::Expected<int8_t>
      {
        L"{'path': 'c', 'type': {'type': 'int8'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("c"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Int8);
            }));
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectNested::ExpectedProperty<int8_t>{"a.b.c"sv, int8_t{42}}
        )
      });

      ParseSchemaSparseObjectNestedCase(row, RoundTripSparseObjectNested::Expected<int8_t, std::string_view>
      {
        L"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Int8);
            }));
          }));
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("c"s);
            p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
            {
              pt.SetType(cdb_hr::TypeKind::Utf8);
            }));
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectNested::ExpectedProperty<int8_t>{"a.b.b"sv, int8_t{42}},
          RoundTripSparseObjectNested::ExpectedProperty<std::string_view>{"a.b.c"sv, "abc"sv}
        )
      });

      ParseSchemaSparseObjectNestedCase(row,
        RoundTripSparseObjectNested::Expected<int8_t, bool, cdb_core::ReadOnlySpan<byte>, cdb_hr::NullValue>
        {
          L"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'bool'}}, {'path': 'd', 'type': {'type': 'binary'}}, {'path': 'e', 'type': {'type': 'null'}}"sv,
          cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
          {
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("b"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Int8);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("c"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Boolean);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("d"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Binary);
              }));
            }));
            v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
            {
              p.SetPath("e"s);
              p.SetPropertyType(cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)
              {
                pt.SetType(cdb_hr::TypeKind::Null);
              }));
            }));
          }),
          std::make_tuple(
            RoundTripSparseObjectNested::ExpectedProperty<int8_t>{"a.b.b"sv, int8_t{42}},
            RoundTripSparseObjectNested::ExpectedProperty<bool>{"a.b.c"sv, true},
            RoundTripSparseObjectNested::ExpectedProperty<cdb_core::ReadOnlySpan<byte>>{
              "a.b.d"sv,
              std::array<byte, 3>{{byte{0x01}, byte{0x02}, byte{0x03}}}
            },
            RoundTripSparseObjectNested::ExpectedProperty<cdb_hr::NullValue>{"a.b.e"sv, {}}
          )
        });

      ParseSchemaSparseObjectNestedCase(row, RoundTripSparseObjectNested::Expected<nullptr_t>
      {
        L"{'path': 'b', 'type': {'type': 'object'}}"sv,
        cdb_core::make_with([](std::vector<std::unique_ptr<cdb_hr::Property>>& v)
        {
          v.emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)
          {
            p.SetPath("b"s);
            p.SetPropertyType(std::make_unique<cdb_hr::ObjectPropertyType>());
          }));
        }),
        std::make_tuple(
          RoundTripSparseObjectNested::ExpectedProperty<nullptr_t>{"a.b.b"sv, nullptr}
        )
      });
    }

    struct RoundTripSparseArray final
    {
      template<typename TValue>
      struct Expected final
      {
        std::wstring_view Tag;
        cdb_hr::TypeKind Type;
        const cdb_hr::LayoutType& LayoutType;
        std::vector<TValue> Value;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& ArrCol;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& arrCol = closure.ArrCol;
          const cdb_hr::LayoutIndexedScope& arrT = arrCol.TypeAs<cdb_hr::LayoutIndexedScope>();
          Expected<TValue>& expected = closure.Expected;
          std::wstring tag = cdb_core::make_string(L"Tag: %s, Array: %S", expected.Tag.data(),
            arrCol.GetType()->GetName().data());

          Logger::WriteMessage(tag.data());

          const TLayout& t = expected.LayoutType.template TypeAs<TLayout>();

          // Verify the array doesn't yet exist.
          cdb_hr::RowCursor field = root.Clone().Find(row, arrCol.GetPath());
          auto [r, scope] = arrT.ReadScope(row, field);
          ResultAssert::NotFound(r, tag);

          // Write the array.
          std::tie(r, scope) = arrT.WriteScope(row, field, arrCol.GetTypeArgs());
          ResultAssert::IsSuccess(r, tag);

          // Verify the nested field doesn't yet appear within the new scope.
          Assert::IsFalse(scope.MoveNext(row), tag.data());
          TValue value;
          std::tie(r, value) = t.ReadSparse(row, scope);
          ResultAssert::NotFound(r, tag);

          // Write the nested fields.
          cdb_hr::RowCursor elm = scope.Clone();
          for (TValue item : expected.Value)
          {
            // Write the ith index.
            r = t.WriteSparse(row, elm, item);
            ResultAssert::IsSuccess(r, tag);

            // Move cursor to the ith+1 index.
            Assert::IsFalse(elm.MoveNext(row), tag.data());
          }

          // Read the array and the nested column, validate the nested column has the proper value.
          cdb_hr::RowCursor scope2;
          std::tie(r, scope2) = arrT.ReadScope(row, field);
          ResultAssert::IsSuccess(r, tag);
          Assert::AreEqual(scope.GetScopeType(), scope2.GetScopeType(), tag.data());
          Assert::AreEqual(scope.m_start, scope2.m_start, tag.data());
          Assert::AreEqual(scope.GetImmutable(), scope2.GetImmutable(), tag.data());

          // Read the nested fields
          elm = scope2.Clone();
          for (TValue item : expected.Value)
          {
            Assert::IsTrue(elm.MoveNext(row));
            std::tie(r, value) = t.ReadSparse(row, elm);
            ResultAssert::IsSuccess(r, tag);
            if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
            {
              CollectionAssert::AreEqual(item, value, tag);
            }
            else
            {
              Assert::AreEqual(item, value, tag.data());
            }
          }

          // Delete an item.
          int indexToDelete = 1;
          elm = scope2.Clone();
          Assert::IsTrue(elm.MoveTo(row, indexToDelete), tag.data());
          r = t.DeleteSparse(row, elm);
          ResultAssert::IsSuccess(r, tag);
          std::vector<TValue> remainingValues = std::vector<TValue>(expected.Value);
          remainingValues.erase(remainingValues.begin() + indexToDelete);
          elm = scope2.Clone();
          for (TValue item : remainingValues)
          {
            Assert::IsTrue(elm.MoveNext(row), tag.data());
            std::tie(r, value) = t.ReadSparse(row, elm);
            ResultAssert::IsSuccess(r, tag);
            if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
            {
              CollectionAssert::AreEqual(item, value, tag);
            }
            else
            {
              Assert::AreEqual(item, value, tag.data());
            }
          }

          elm = scope2.Clone();
          Assert::IsFalse(elm.MoveTo(row, static_cast<uint32_t>(remainingValues.size())), tag.data());

          cdb_hr::RowCursor roRoot = root.AsReadOnly().Find(row, arrCol.GetPath());
          ResultAssert::InsufficientPermissions(arrT.DeleteScope(row, roRoot));
          std::tie(r, scope2) = arrT.WriteScope(row, roRoot, arrCol.GetTypeArgs());
          ResultAssert::InsufficientPermissions(r);

          // Overwrite the whole scope.
          r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
          ResultAssert::IsSuccess(r, tag);
          std::tie(r, std::ignore) = arrT.ReadScope(row, field);
          ResultAssert::TypeMismatch(r, tag);
          r = arrT.DeleteScope(row, field);
          ResultAssert::TypeMismatch(r, expected.Tag.data());

          // Overwrite it again, then delete it.
          std::tie(r, std::ignore) = arrT.WriteScope(row, field, arrCol.GetTypeArgs(), cdb_hr::UpdateOptions::Update);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          r = arrT.DeleteScope(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          std::tie(r, std::ignore) = arrT.ReadScope(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    template<typename TValue>
    void ParseSchemaSparseArrayCase(cdb_hr::RowBuffer& row, RoundTripSparseArray::Expected<TValue> expected)
    {
      for (cdb_hr::LayoutCode arrT : std::array<cdb_hr::LayoutCode, 2>{cdb_hr::LayoutCode::TypedArrayScope, cdb_hr::LayoutCode::ArrayScope})
      {
        auto schema = cdb_core::make_unique_with([arrT, itemType = expected.Type](cdb_hr::Schema& s)
        {
          s.SetName("table"s);
          s.SetSchemaId(cdb_hr::SchemaId{-1});
          s.GetProperties().emplace_back(cdb_core::make_unique_with([arrT, itemType](cdb_hr::Property& p)
          {
            p.SetPath("a"s);
            p.SetPropertyType(cdb_core::make_unique_with([arrT, itemType](cdb_hr::ArrayPropertyType& pt)
            {
              if (arrT == cdb_hr::LayoutCode::TypedArrayScope)
              {
                pt.SetItems(cdb_core::make_unique_with([arrT, itemType](cdb_hr::PrimitivePropertyType& pt2)
                {
                  pt2.SetType(itemType);
                  pt2.SetNullable(false);
                }));
              }
            }));
          }));
        });
        auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
        {
          n.GetSchemas().emplace_back(std::move(schema));
        });

        cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
        const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

        std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
        Assert::IsTrue(std::get<0>(found), expected.Tag.data());
        const cdb_hr::LayoutColumn& arrCol = *std::get<1>(found);
        Assert::AreEqual(cdb_hr::StorageKind::Sparse, arrCol.GetStorage(), expected.Tag.data());

        // Try writing a row using the layout.
        row.Reset();
        row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

        cdb_hr::HybridRowHeader header = row.GetHeader();
        Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
        Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

        cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
        RoundTripSparseArray::Closure<TValue> closure{arrCol, expected};
        LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseArray>(
          expected.LayoutType.GetLayoutCode(),
          row,
          root,
          &closure);
      }
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseArray, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::NullValue>
        {L"array[null]"sv, cdb_hr::TypeKind::Null, cdb_hr::LayoutLiteral::Null, {cdb_hr::NullValue{}, cdb_hr::NullValue{}, cdb_hr::NullValue{}}});

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<bool>
        {L"array[bool]"sv, cdb_hr::TypeKind::Boolean, cdb_hr::LayoutLiteral::Boolean, {true, false, true}});

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<int8_t>
        {L"array[int8]"sv, cdb_hr::TypeKind::Int8, cdb_hr::LayoutLiteral::Int8, {int8_t{42}, int8_t{43}, int8_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<int16_t>
        {L"array[int16]"sv, cdb_hr::TypeKind::Int16, cdb_hr::LayoutLiteral::Int16, {int16_t{42}, int16_t{43}, int16_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<int32_t>
        {L"array[int32]"sv, cdb_hr::TypeKind::Int32, cdb_hr::LayoutLiteral::Int32, {42, 43, 44}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<int64_t>
        {L"array[int64]"sv, cdb_hr::TypeKind::Int64, cdb_hr::LayoutLiteral::Int64, {42i64, 43i64, 44i64}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<uint8_t>
        {L"array[uint8]"sv, cdb_hr::TypeKind::UInt8, cdb_hr::LayoutLiteral::UInt8, {uint8_t{42}, uint8_t{43}, uint8_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<uint16_t>
        {L"array[uint16]"sv, cdb_hr::TypeKind::UInt16, cdb_hr::LayoutLiteral::UInt16, {uint16_t{42}, uint16_t{43}, uint16_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<uint32_t>
        {L"array[uint32]"sv, cdb_hr::TypeKind::UInt32, cdb_hr::LayoutLiteral::UInt32, {42ui32, 43ui32, 44ui32}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<uint64_t>
        {L"array[uint64]"sv, cdb_hr::TypeKind::UInt64, cdb_hr::LayoutLiteral::UInt64, {42ui64, 43ui64, 44ui64}});

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<int64_t>
        {L"array[varint]"sv, cdb_hr::TypeKind::VarInt, cdb_hr::LayoutLiteral::VarInt, {42i64, 43i64, 44i64}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<uint64_t>
        {L"array[varuint]"sv, cdb_hr::TypeKind::VarUInt, cdb_hr::LayoutLiteral::VarUInt, {42ui64, 43ui64, 44ui64}});

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<float32_t>
        {L"array[float32]"sv, cdb_hr::TypeKind::Float32, cdb_hr::LayoutLiteral::Float32, {4.2F, 4.3F, 4.4F}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<float64_t>
        {L"array[float64]"sv, cdb_hr::TypeKind::Float64, cdb_hr::LayoutLiteral::Float64, {4.2, 4.3, 4.4}});
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<float128_t>
      {
        L"array[float128]"sv,
        cdb_hr::TypeKind::Float128,
        cdb_hr::LayoutLiteral::Float128,
        {cdb_hr::Float128{0, 42}, cdb_hr::Float128{0, 43}, cdb_hr::Float128{0, 44}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::Decimal>
      {
        L"array[decimal]"sv,
        cdb_hr::TypeKind::Decimal,
        cdb_hr::LayoutLiteral::Decimal,
        {cdb_hr::Decimal{42}, cdb_hr::Decimal{43}, cdb_hr::Decimal{44}}
      });

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::DateTime>
      {
        L"array[datetime]"sv,
        cdb_hr::TypeKind::DateTime,
        cdb_hr::LayoutLiteral::DateTime,
        {cdb_hr::DateTime{0}, cdb_hr::DateTime{1}, cdb_hr::DateTime{2}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::UnixDateTime>
      {
        L"array[unixdatetime]"sv,
        cdb_hr::TypeKind::UnixDateTime,
        cdb_hr::LayoutLiteral::UnixDateTime,
        {cdb_hr::UnixDateTime{1}, cdb_hr::UnixDateTime{2}, cdb_hr::UnixDateTime{3}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::Guid>
      {
        L"array[guid]"sv,
        cdb_hr::TypeKind::Guid,
        cdb_hr::LayoutLiteral::Guid,
        {
          cdb_hr::Guid{0x8ddde8c5, 0xbba1, 0x43dc, 0xa4, 0xcc, 0x3e, 0x7f, 0xb8, 0x4e, 0x96, 0xc0},
          cdb_hr::Guid{0x20825fb2, 0xc1b0, 0x400f, 0x91, 0x67, 0xce, 0x34, 0x4f, 0xbd, 0xb0, 0x91},
          cdb_hr::Guid{0xbc722d4e, 0x4e31, 0x42e0, 0xac, 0xe7, 0xd, 0xe7, 0x9b, 0xbb, 0xc8, 0x19}
        }
      });
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_hr::MongoDbObjectId>
      {
        L"array[mongodbobjectid]"sv,
        cdb_hr::TypeKind::MongoDbObjectId,
        cdb_hr::LayoutLiteral::MongoDbObjectId,
        {cdb_hr::MongoDbObjectId{0, 1}, cdb_hr::MongoDbObjectId{0, 2}, cdb_hr::MongoDbObjectId{0, 3}}
      });

      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<std::string_view>
      {
        L"array[utf8]"sv,
        cdb_hr::TypeKind::Utf8,
        cdb_hr::LayoutLiteral::Utf8,
        {"abc"sv, "def"sv, "xyz"sv}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseArrayCase(row, RoundTripSparseArray::Expected<cdb_core::ReadOnlySpan<byte>>
      {
        L"array[binary]"sv,
        cdb_hr::TypeKind::Binary,
        cdb_hr::LayoutLiteral::Binary,
        {
          std::array<byte, 2>{{byte{0x01}, byte{0x02}}},
          std::array<byte, 2>{{byte{0x03}, byte{0x04}}},
          std::array<byte, 2>{{byte{0x05}, byte{0x06}}},
        },
      });
    }

    struct RoundTripSparseSet final
    {
      template<typename TValue>
      struct Expected final
      {
        std::wstring_view Tag;
        cdb_hr::TypeKind Type;
        const cdb_hr::LayoutType& LayoutType;
        std::vector<TValue> Value;
      };

      template<typename TValue>
      struct Closure final
      {
        const cdb_hr::LayoutColumn& SetCol;
        Expected<TValue>& Expected;
      };

      template<typename TLayout, typename TValue>
      struct Dispatcher : TestActionDispatcher<Closure<TValue>, TLayout, TValue>
      {
        void Dispatch(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& root, Closure<TValue>& closure) override
        {
          const cdb_hr::LayoutColumn& setCol = closure.SetCol;
          const cdb_hr::LayoutUniqueScope& setT = setCol.TypeAs<cdb_hr::LayoutUniqueScope>();
          Expected<TValue>& expected = closure.Expected;
          std::wstring tag = cdb_core::make_string(L"Tag: %s, Set: %S", expected.Tag.data(),
            setCol.GetType()->GetName().data());

          Logger::WriteMessage(tag.data());
          const TLayout& t = expected.LayoutType.template TypeAs<TLayout>();

          // Verify the Set doesn't yet exist.
          cdb_hr::RowCursor field = root.Clone().Find(row, setCol.GetPath());
          auto [r, scope] = setT.ReadScope(row, field);
          ResultAssert::NotFound(r, tag);

          // Write the Set.
          std::tie(r, scope) = setT.WriteScope(row, field, setCol.GetTypeArgs());
          ResultAssert::IsSuccess(r, tag);

          // Verify the nested field doesn't yet appear within the new scope.
          Assert::IsFalse(scope.MoveNext(row), tag.data());
          TValue value;
          std::tie(r, value) = t.ReadSparse(row, scope);
          ResultAssert::NotFound(r, tag);

          // Write the nested fields.
          for (TValue v1 : expected.Value)
          {
            // Write the ith item into staging storage.
            cdb_hr::RowCursor tempCursor = root.Clone().Find(row, ""sv);
            r = t.WriteSparse(row, tempCursor, v1);
            ResultAssert::IsSuccess(r, tag);

            // Move item into the set.
            r = setT.MoveField(row, scope, tempCursor);
            ResultAssert::IsSuccess(r, tag);
          }

          // Attempts to insert the same items into the set again will fail.
          for (TValue v2 : expected.Value)
          {
            // Write the ith item into staging storage.
            cdb_hr::RowCursor tempCursor = root.Clone().Find(row, ""sv);
            r = t.WriteSparse(row, tempCursor, v2);
            ResultAssert::IsSuccess(r, tag);

            // Move item into the set.
            r = setT.MoveField(row, scope, tempCursor, cdb_hr::UpdateOptions::Insert);
            ResultAssert::Exists(r, tag);
          }

          // Read the Set and the nested column, validate the nested column has the proper value.
          cdb_hr::RowCursor scope2;
          std::tie(r, scope2) = setT.ReadScope(row, field);
          ResultAssert::IsSuccess(r, tag);
          Assert::AreEqual(scope.GetScopeType(), scope2.GetScopeType(), tag.data());
          Assert::AreEqual(scope.m_start, scope2.m_start, tag.data());
          Assert::AreEqual(scope.GetImmutable(), scope2.GetImmutable(), tag.data());

          // Read the nested fields
          std::tie(r, scope) = setT.ReadScope(row, field);
          ResultAssert::IsSuccess(r);
          for (TValue item : expected.Value)
          {
            Assert::IsTrue(scope.MoveNext(row), tag.data());
            std::tie(r, value) = t.ReadSparse(row, scope);
            ResultAssert::IsSuccess(r, tag);
            if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
            {
              CollectionAssert::AreEqual(item, value, tag);
            }
            else
            {
              Assert::AreEqual(item, value, tag.data());
            }
          }

          // Delete all of the items and then insert them again in the opposite order.
          std::tie(r, scope) = setT.ReadScope(row, field);
          ResultAssert::IsSuccess(r);
          for (size_t i = 0; i < expected.Value.size(); i++)
          {
            Assert::IsTrue(scope.MoveNext(row), tag.data());
            r = t.DeleteSparse(row, scope);
            ResultAssert::IsSuccess(r, tag);
          }

          std::tie(r, scope) = setT.ReadScope(row, field);
          ResultAssert::IsSuccess(r);
          for (int i = static_cast<int>(expected.Value.size()) - 1; i >= 0; --i)
          {
            // Write the ith item into staging storage.
            cdb_hr::RowCursor tempCursor = root.Clone().Find(row, ""sv);
            r = t.WriteSparse(row, tempCursor, expected.Value[i]);
            ResultAssert::IsSuccess(r, tag);

            // Move item into the set.
            r = setT.MoveField(row, scope, tempCursor);
            ResultAssert::IsSuccess(r, tag);
          }

          // Verify they still enumerate in sorted order.
          std::tie(r, scope) = setT.ReadScope(row, field);
          ResultAssert::IsSuccess(r);
          for (TValue item : expected.Value)
          {
            Assert::IsTrue(scope.MoveNext(row), tag.data());
            std::tie(r, value) = t.ReadSparse(row, scope);
            ResultAssert::IsSuccess(r, tag);
            if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
            {
              CollectionAssert::AreEqual(item, value, tag);
            }
            else
            {
              Assert::AreEqual(item, value, tag.data());
            }
          }

          // Delete one item.
          if (expected.Value.size() > 1)
          {
            int indexToDelete = 1;
            std::tie(r, scope) = setT.ReadScope(row, field);
            ResultAssert::IsSuccess(r);
            Assert::IsTrue(scope.MoveTo(row, indexToDelete), tag.data());
            r = t.DeleteSparse(row, scope);
            ResultAssert::IsSuccess(r, tag);
            std::vector<TValue> remainingValues(expected.Value);
            remainingValues.erase(remainingValues.begin() + indexToDelete);

            std::tie(r, scope) = setT.ReadScope(row, field);
            ResultAssert::IsSuccess(r);
            for (TValue item : remainingValues)
            {
              Assert::IsTrue(scope.MoveNext(row), tag.data());
              std::tie(r, value) = t.ReadSparse(row, scope);
              ResultAssert::IsSuccess(r, tag);
              if constexpr (std::is_same_v<TValue, cdb_core::ReadOnlySpan<byte>>)
              {
                CollectionAssert::AreEqual(item, value, tag);
              }
              else
              {
                Assert::AreEqual(item, value, tag.data());
              }
            }

            Assert::IsFalse(scope.MoveTo(row, static_cast<uint32_t>(remainingValues.size())), tag.data());
          }

          cdb_hr::RowCursor roRoot = root.AsReadOnly().Find(row, setCol.GetPath());
          ResultAssert::InsufficientPermissions(setT.DeleteScope(row, roRoot));
          std::tie(r, std::ignore) = setT.WriteScope(row, roRoot, setCol.GetTypeArgs());
          ResultAssert::InsufficientPermissions(r);

          // Overwrite the whole scope.
          r = cdb_hr::LayoutLiteral::Null.WriteSparse(row, field, cdb_hr::NullValue{});
          ResultAssert::IsSuccess(r, tag);
          std::tie(r, std::ignore) = setT.ReadScope(row, field);
          ResultAssert::TypeMismatch(r, tag);
          r = setT.DeleteScope(row, field);
          ResultAssert::TypeMismatch(r, expected.Tag.data());

          // Overwrite it again, then delete it.
          std::tie(r, std::ignore) = setT.WriteScope(row, field, setCol.GetTypeArgs(), cdb_hr::UpdateOptions::Update);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          r = setT.DeleteScope(row, field);
          ResultAssert::IsSuccess(r, expected.Tag.data());
          std::tie(r, std::ignore) = setT.ReadScope(row, field);
          ResultAssert::NotFound(r, expected.Tag.data());
        }
      };

      struct ObjectDispatcher final : TestActionObjectDispatcher<Closure<nullptr_t>>
      {
        void DispatchObject(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, Closure<std::nullptr_t>& closure) override
        {
          Assert::Fail(L"not implemented");
        }
      };
    };

    template<typename TValue>
    void ParseSchemaSparseSetCase(cdb_hr::RowBuffer& row, RoundTripSparseSet::Expected<TValue> expected)
    {
      for (cdb_hr::LayoutCode arrT : std::array<cdb_hr::LayoutCode, 1>{cdb_hr::LayoutCode::TypedSetScope /*, LayoutCode::SetScope*/})
      {
        auto schema = cdb_core::make_unique_with([arrT, itemType = expected.Type](cdb_hr::Schema& s)
        {
          s.SetName("table"s);
          s.SetSchemaId(cdb_hr::SchemaId{-1});
          s.GetProperties().emplace_back(cdb_core::make_unique_with([arrT, itemType](cdb_hr::Property& p)
          {
            p.SetPath("a"s);
            p.SetPropertyType(cdb_core::make_unique_with([arrT, itemType](cdb_hr::SetPropertyType& pt)
            {
              if (arrT == cdb_hr::LayoutCode::TypedSetScope)
              {
                pt.SetItems(cdb_core::make_unique_with([arrT, itemType](cdb_hr::PrimitivePropertyType& pt2)
                {
                  pt2.SetType(itemType);
                  pt2.SetNullable(false);
                }));
              }
            }));
          }));
        });
        auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)
        {
          n.GetSchemas().emplace_back(std::move(schema));
        });

        cdb_hr::LayoutResolverNamespace resolver{std::move(ns)};
        const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::SchemaId{-1});

        std::tuple<bool, const cdb_hr::LayoutColumn*> found = layout.TryFind("a"sv);
        Assert::IsTrue(std::get<0>(found), expected.Tag.data());
        const cdb_hr::LayoutColumn& setCol = *std::get<1>(found);
        Assert::AreEqual(cdb_hr::StorageKind::Sparse, setCol.GetStorage(), expected.Tag.data());

        // Try writing a row using the layout.
        row.Reset();
        row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

        cdb_hr::HybridRowHeader header = row.GetHeader();
        Assert::AreEqual(cdb_hr::HybridRowVersion::V1, header.GetVersion());
        Assert::AreEqual(layout.GetSchemaId(), header.GetSchemaId());

        cdb_hr::RowCursor root = cdb_hr::RowCursor::Create(row);
        RoundTripSparseSet::Closure<TValue> closure{setCol, expected};
        LayoutCompilerUnitTests::LayoutCodeSwitch<RoundTripSparseSet>(
          expected.LayoutType.GetLayoutCode(),
          row,
          root,
          &closure);
      }
    }

  TEST_METHOD_WITH_OWNER(ParseSchemaSparseSet, "jthunter")
    {
      cdb_hr::MemorySpanResizer<byte> resizer{InitialRowSize};
      cdb_hr::RowBuffer row{InitialRowSize, &resizer};

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_hr::NullValue>
        {L"set[null]"sv, cdb_hr::TypeKind::Null, cdb_hr::LayoutLiteral::Null, {cdb_hr::NullValue{}}});

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<bool>
        {L"set[bool]"sv, cdb_hr::TypeKind::Boolean, cdb_hr::LayoutLiteral::Boolean, {false, true}});

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<int8_t>
        {L"set[int8]"sv, cdb_hr::TypeKind::Int8, cdb_hr::LayoutLiteral::Int8, {int8_t{42}, int8_t{43}, int8_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<int16_t>
        {L"set[int16]"sv, cdb_hr::TypeKind::Int16, cdb_hr::LayoutLiteral::Int16, {int16_t{42}, int16_t{43}, int16_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<int32_t>
        {L"set[int32]"sv, cdb_hr::TypeKind::Int32, cdb_hr::LayoutLiteral::Int32, {42, 43, 44}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<int64_t>
        {L"set[int64]"sv, cdb_hr::TypeKind::Int64, cdb_hr::LayoutLiteral::Int64, {42i64, 43i64, 44i64}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<uint8_t>
        {L"set[uint8]"sv, cdb_hr::TypeKind::UInt8, cdb_hr::LayoutLiteral::UInt8, {uint8_t{42}, uint8_t{43}, uint8_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<uint16_t>
        {L"set[uint16]"sv, cdb_hr::TypeKind::UInt16, cdb_hr::LayoutLiteral::UInt16, {uint16_t{42}, uint16_t{43}, uint16_t{44}}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<uint32_t>
        {L"set[uint32]"sv, cdb_hr::TypeKind::UInt32, cdb_hr::LayoutLiteral::UInt32, {42ui32, 43ui32, 44ui32}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<uint64_t>
        {L"set[uint64]"sv, cdb_hr::TypeKind::UInt64, cdb_hr::LayoutLiteral::UInt64, {42ui64, 43ui64, 44ui64}});

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<int64_t>
        {L"set[varint]"sv, cdb_hr::TypeKind::VarInt, cdb_hr::LayoutLiteral::VarInt, {42i64, 43i64, 44i64}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<uint64_t>
        {L"set[varuint]"sv, cdb_hr::TypeKind::VarUInt, cdb_hr::LayoutLiteral::VarUInt, {42ui64, 43ui64, 44ui64}});

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<float32_t>
        {L"set[float32]"sv, cdb_hr::TypeKind::Float32, cdb_hr::LayoutLiteral::Float32, {4.2F, 4.3F, 4.4F}});
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<float64_t>
      {
        L"set[float64]"sv,
        cdb_hr::TypeKind::Float64,
        cdb_hr::LayoutLiteral::Float64,
        {
          static_cast<double>(0xAAAAAAAAAAAAAAAA),
          static_cast<double>(0xBBBBBBBBBBBBBBBB),
          static_cast<double>(0xCCCCCCCCCCCCCCCC),
        }
      });
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<float128_t>
      {
        L"set[float128]"sv,
        cdb_hr::TypeKind::Float128,
        cdb_hr::LayoutLiteral::Float128,
        {cdb_hr::Float128{0, 42}, cdb_hr::Float128{0, 43}, cdb_hr::Float128{0, 44}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_hr::Decimal>
      {
        L"set[decimal]"sv,
        cdb_hr::TypeKind::Decimal,
        cdb_hr::LayoutLiteral::Decimal,
        {cdb_hr::Decimal{42}, cdb_hr::Decimal{43}, cdb_hr::Decimal{44}}
      });

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_hr::DateTime>
      {
        L"set[datetime]"sv,
        cdb_hr::TypeKind::DateTime,
        cdb_hr::LayoutLiteral::DateTime,
        {cdb_hr::DateTime{1}, cdb_hr::DateTime{2}, cdb_hr::DateTime{3}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_hr::UnixDateTime>
      {
        L"set[unixdatetime]"sv,
        cdb_hr::TypeKind::UnixDateTime,
        cdb_hr::LayoutLiteral::UnixDateTime,
        {cdb_hr::UnixDateTime{1}, cdb_hr::UnixDateTime{2}, cdb_hr::UnixDateTime{3}}
      });
      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_hr::Guid>
      {
        L"set[guid]"sv,
        cdb_hr::TypeKind::Guid,
        cdb_hr::LayoutLiteral::Guid,
        {
          cdb_hr::Guid{0xaaaaaaaa, 0xaaaa, 0xaaaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa},
          cdb_hr::Guid{0xbbbbbbbb, 0xbbbb, 0xbbbb, 0xbb, 0xbb, 0xbb, 0xbb, 0xbb, 0xbb, 0xbb, 0xbb},
          cdb_hr::Guid{0xcccccccc, 0xcccc, 0xcccc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc}
        }
      });

      LayoutCompilerUnitTests::ParseSchemaSparseSetCase(row, RoundTripSparseSet::Expected<cdb_core::ReadOnlySpan<byte>>
      {
        L"set[binary]"sv,
        cdb_hr::TypeKind::Binary,
        cdb_hr::LayoutLiteral::Binary,
        {
          std::array<byte, 2>{{byte{0x01}, byte{0x02}}},
          std::array<byte, 2>{{byte{0x03}, byte{0x04}}},
          std::array<byte, 2>{{byte{0x05}, byte{0x06}}},
        },
      });
    }
  };
}
