// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"
#include "CppUnitTestFramework.inl"
#include "ResultAssert.h"

// ReSharper disable CppClangTidyCppcoreguidelinesProTypeStaticCastDowncast
namespace cdb_hr_test
{
  using namespace std::literals;
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;

  TEST_CLASS(SystemSchemaUnitTests)
  {
  public:

  TEST_METHOD_WITH_OWNER(LoadGeneratedHrSchema, "jthunter")
    {
      const cdb_hr::Layout& layout = cdb_hr::SchemasHrSchema::GetLayoutResolver().Resolve(cdb_hr::SchemaOptionsHybridRowSerializer::Id);
      Assert::AreEqual(cdb_hr::SchemaOptionsHybridRowSerializer::Id, layout.GetSchemaId());
    }

  TEST_METHOD_WITH_OWNER(LoadTest, "jthunter")
    {
      const cdb_hr::LayoutResolver& resolver = cdb_hr::SchemasHrSchema::GetLayoutResolver();

      std::array systemSchemas{
        cdb_hr::SystemSchemaLiteral::EmptySchemaId,
        cdb_hr::SegmentHybridRowSerializer::Id,
        cdb_hr::RecordHybridRowSerializer::Id,
        cdb_hr::NamespaceHybridRowSerializer::Id,
        cdb_hr::SchemaHybridRowSerializer::Id,
        cdb_hr::SchemaOptionsHybridRowSerializer::Id,
        cdb_hr::PartitionKeyHybridRowSerializer::Id,
        cdb_hr::PrimarySortKeyHybridRowSerializer::Id,
        cdb_hr::StaticKeyHybridRowSerializer::Id,
        cdb_hr::PropertyHybridRowSerializer::Id,
        cdb_hr::PropertyTypeHybridRowSerializer::Id,
        cdb_hr::PrimitivePropertyTypeHybridRowSerializer::Id,
        cdb_hr::ScopePropertyTypeHybridRowSerializer::Id,
        cdb_hr::ArrayPropertyTypeHybridRowSerializer::Id,
        cdb_hr::ObjectPropertyTypeHybridRowSerializer::Id,
        cdb_hr::SetPropertyTypeHybridRowSerializer::Id,
        cdb_hr::MapPropertyTypeHybridRowSerializer::Id,
        cdb_hr::TuplePropertyTypeHybridRowSerializer::Id,
        cdb_hr::TaggedPropertyTypeHybridRowSerializer::Id,
      };

      // Make sure all system schemas are loadable.
      for (cdb_hr::SchemaId id : systemSchemas)
      {
        const cdb_hr::Layout& l = resolver.Resolve(id);
        Assert::AreEqual(id, l.GetSchemaId());
      }

      // Make sure all system schema ids are unique.
      for (cdb_hr::SchemaId id : systemSchemas)
      {
        int count = 0;
        for (cdb_hr::SchemaId other : systemSchemas)
        {
          if (other == id)
          {
            count++;
          }
        }

        Assert::AreEqual(1, count);
      }
    }

    static void AssertEqual(const cdb_hr::PartitionKey& i1, const cdb_hr::PartitionKey& i2)
    {
      Assert::AreEqual(i1.GetPath(), i2.GetPath());
    }

    static void AssertEqual(const cdb_hr::PrimarySortKey& i1, const cdb_hr::PrimarySortKey& i2)
    {
      Assert::AreEqual(i1.GetPath(), i2.GetPath());
      Assert::AreEqual(i1.GetDirection(), i2.GetDirection());
    }

    static void AssertEqual(const cdb_hr::StaticKey& i1, const cdb_hr::StaticKey& i2)
    {
      Assert::AreEqual(i1.GetPath(), i2.GetPath());
    }

    static void AssertEqual(const cdb_hr::Property& i1, const cdb_hr::Property& i2)
    {
      Assert::AreEqual(i1.GetComment(), i2.GetComment());
      Assert::AreEqual(i1.GetPath(), i2.GetPath());
      Assert::AreEqual(i1.GetApiName(), i2.GetApiName());
      Assert::AreEqual(i1.GetAllowEmpty(), i2.GetAllowEmpty());
      if (i1.GetPropertyType().has_value())
      {
        Assert::IsTrue(i2.GetPropertyType().has_value());
        AssertEqual(i1.GetPropertyType().value(), i2.GetPropertyType().value());
      }
      else
      {
        Assert::IsFalse(i2.GetPropertyType().has_value());
      }
    }

    static void AssertEqual(const cdb_hr::PropertyType& i1, const cdb_hr::PropertyType& i2)
    {
      Assert::AreEqual(i1.GetApiType(), i2.GetApiType());
      Assert::AreEqual(i1.GetType(), i2.GetType());
      Assert::AreEqual(i1.GetNullable(), i2.GetNullable());

      Assert::AreEqual(i1.GetRuntimeSchemaId(), i2.GetRuntimeSchemaId());
      switch (i1.GetRuntimeSchemaId().Id())
      {
      case cdb_hr::PrimitivePropertyTypeHybridRowSerializer::Id.Id():
      {
        AssertEqual(static_cast<const cdb_hr::PrimitivePropertyType&>(i1), static_cast<const cdb_hr::PrimitivePropertyType&>(i2));
        return;
      }

      case cdb_hr::ArrayPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::ObjectPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::UdtPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::SetPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::MapPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::TuplePropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::TaggedPropertyTypeHybridRowSerializer::Id.Id():
      case cdb_hr::ScopePropertyTypeHybridRowSerializer::Id.Id():
      {
        AssertEqual(static_cast<const cdb_hr::ScopePropertyType&>(i1), static_cast<const cdb_hr::ScopePropertyType&>(i2));
        return;
      }

      default:
        Assert::Fail(L"Type is abstract.");
      }
    }

    static void AssertEqual(const cdb_hr::PrimitivePropertyType& i1, const cdb_hr::PrimitivePropertyType& i2)
    {
      Assert::AreEqual(i1.GetLength(), i2.GetLength());
      Assert::AreEqual(i1.GetStorage(), i2.GetStorage());
    }

    static void AssertEqual(const cdb_hr::ScopePropertyType& i1, const cdb_hr::ScopePropertyType& i2)
    {
      Assert::AreEqual(i1.GetImmutable(), i2.GetImmutable());

      switch (i1.GetRuntimeSchemaId().Id())
      {
      case cdb_hr::ArrayPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::ArrayPropertyType& p = static_cast<const cdb_hr::ArrayPropertyType&>(i1);
        const cdb_hr::ArrayPropertyType& q = static_cast<const cdb_hr::ArrayPropertyType&>(i2);
        Assert::AreEqual(p.GetItems().has_value(), q.GetItems().has_value());
        AssertEqual(p.GetItems().value(), q.GetItems().value());
        break;
      }
      case cdb_hr::ObjectPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::ObjectPropertyType& p = static_cast<const cdb_hr::ObjectPropertyType&>(i1);
        const cdb_hr::ObjectPropertyType& q = static_cast<const cdb_hr::ObjectPropertyType&>(i2);
        Assert::AreEqual(p.GetProperties().size(), q.GetProperties().size());
        for (size_t i = 0; i < p.GetProperties().size(); i++)
        {
          AssertEqual(*p.GetProperties()[i], *q.GetProperties()[i]);
        }

        break;
      }
      case cdb_hr::SetPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::SetPropertyType& p = static_cast<const cdb_hr::SetPropertyType&>(i1);
        const cdb_hr::SetPropertyType& q = static_cast<const cdb_hr::SetPropertyType&>(i2);
        Assert::AreEqual(p.GetItems().has_value(), q.GetItems().has_value());
        AssertEqual(p.GetItems().value(), q.GetItems().value());
        break;
      }
      case cdb_hr::MapPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::MapPropertyType& p = static_cast<const cdb_hr::MapPropertyType&>(i1);
        const cdb_hr::MapPropertyType& q = static_cast<const cdb_hr::MapPropertyType&>(i2);
        Assert::AreEqual(p.GetKeys().has_value(), q.GetKeys().has_value());
        Assert::AreEqual(p.GetValues().has_value(), q.GetValues().has_value());
        AssertEqual(p.GetKeys().value(), q.GetKeys().value());
        AssertEqual(p.GetValues().value(), q.GetValues().value());
        break;
      }
      case cdb_hr::TuplePropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::TuplePropertyType& p = static_cast<const cdb_hr::TuplePropertyType&>(i1);
        const cdb_hr::TuplePropertyType& q = static_cast<const cdb_hr::TuplePropertyType&>(i2);
        Assert::AreEqual(p.GetItems().size(), q.GetItems().size());
        for (size_t i = 0; i < p.GetItems().size(); i++)
        {
          AssertEqual(*p.GetItems()[i], *q.GetItems()[i]);
        }

        break;
      }
      case cdb_hr::TaggedPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::TaggedPropertyType& p = static_cast<const cdb_hr::TaggedPropertyType&>(i1);
        const cdb_hr::TaggedPropertyType& q = static_cast<const cdb_hr::TaggedPropertyType&>(i2);
        Assert::AreEqual(p.GetItems().size(), q.GetItems().size());
        for (size_t i = 0; i < p.GetItems().size(); i++)
        {
          AssertEqual(*p.GetItems()[i], *q.GetItems()[i]);
        }

        break;
      }
      case cdb_hr::UdtPropertyTypeHybridRowSerializer::Id.Id():
      {
        const cdb_hr::UdtPropertyType& p = static_cast<const cdb_hr::UdtPropertyType&>(i1);
        const cdb_hr::UdtPropertyType& q = static_cast<const cdb_hr::UdtPropertyType&>(i2);
        Assert::AreEqual(p.GetName(), q.GetName());
        Assert::AreEqual(p.GetSchemaId(), q.GetSchemaId());
        break;
      }
      default:
        Assert::Fail(L"Type is abstract.");
      }
    }

    static void AssertEqual(const cdb_hr::EnumSchema& s1, const cdb_hr::EnumSchema& s2)
    {
      Assert::AreEqual(s1.GetType(), s2.GetType());
      Assert::AreEqual(s1.GetApiType(), s2.GetApiType());
      Assert::AreEqual(s1.GetName(), s2.GetName());
      Assert::AreEqual(s1.GetComment(), s2.GetComment());

      Assert::AreEqual(s1.GetValues().size(), s2.GetValues().size());
      for (size_t i = 0; i < s1.GetValues().size(); i++)
      {
        AssertEqual(*s1.GetValues()[i], *s2.GetValues()[i]);
      }
    }

    static void AssertEqual(const cdb_hr::EnumValue& s1, const cdb_hr::EnumValue& s2)
    {
      Assert::AreEqual(s1.GetValue(), s2.GetValue());
      Assert::AreEqual(s1.GetName(), s2.GetName());
      Assert::AreEqual(s1.GetComment(), s2.GetComment());
    }

    static void AssertEqual(const cdb_hr::SchemaOptions& s1, const cdb_hr::SchemaOptions& s2)
    {
      Assert::AreEqual(s1.GetDisallowUnschematized(), s2.GetDisallowUnschematized());
      Assert::AreEqual(s1.GetEnablePropertyLevelTimestamp(), s2.GetEnablePropertyLevelTimestamp());
      Assert::AreEqual(s1.GetDisableSystemPrefix(), s2.GetDisableSystemPrefix());
      Assert::AreEqual(s1.GetAbstract(), s2.GetAbstract());
    }

    static void AssertEqual(const cdb_hr::Schema& s1, const cdb_hr::Schema& s2)
    {
      Assert::AreEqual(s1.GetVersion(), s2.GetVersion());
      Assert::AreEqual(s1.GetType(), s2.GetType());
      Assert::AreEqual(s1.GetSchemaId(), s2.GetSchemaId());
      Assert::AreEqual(s1.GetName(), s2.GetName());
      Assert::AreEqual(s1.GetComment(), s2.GetComment());

      if (!s1.GetOptions().has_value())
      {
        Assert::IsFalse(s2.GetOptions().has_value());
      }
      else
      {
        Assert::IsTrue(s2.GetOptions().has_value());
        AssertEqual(s1.GetOptions().value(), s2.GetOptions().value());
      }

      Assert::AreEqual(s1.GetPartitionKeys().size(), s2.GetPartitionKeys().size());
      for (size_t i = 0; i < s1.GetPartitionKeys().size(); i++)
      {
        AssertEqual(*s1.GetPartitionKeys()[i], *s2.GetPartitionKeys()[i]);
      }

      Assert::AreEqual(s1.GetPrimaryKeys().size(), s2.GetPrimaryKeys().size());
      for (size_t i = 0; i < s1.GetPrimaryKeys().size(); i++)
      {
        AssertEqual(*s1.GetPrimaryKeys()[i], *s2.GetPrimaryKeys()[i]);
      }

      Assert::AreEqual(s1.GetStaticKeys().size(), s2.GetStaticKeys().size());
      for (size_t i = 0; i < s1.GetStaticKeys().size(); i++)
      {
        AssertEqual(*s1.GetStaticKeys()[i], *s2.GetStaticKeys()[i]);
      }

      Assert::AreEqual(s1.GetProperties().size(), s2.GetProperties().size());
      for (size_t i = 0; i < s1.GetProperties().size(); i++)
      {
        AssertEqual(*s1.GetProperties()[i], *s2.GetProperties()[i]);
      }
    }

    static void SerializerRoundtripNamespace(const cdb_hr::Namespace& ns1)
    {
      const cdb_hr::LayoutResolver& resolver = cdb_hr::SystemSchemaLiteral::GetLayoutResolver();
      const cdb_hr::Layout& layout = resolver.Resolve(cdb_hr::NamespaceHybridRowSerializer::Id);

      cdb_hr::MemorySpanResizer<byte> resizer{0};
      cdb_hr::RowBuffer row{0, &resizer};
      row.InitLayout(cdb_hr::HybridRowVersion::V1, layout, &resolver);

      // Write the whole namespace to a row.
      cdb_hr::Result r = ns1.Write(row);
      ResultAssert::IsSuccess(r);

      // Read the namespace back.
      std::unique_ptr<cdb_hr::Namespace> pns2;
      std::tie(r, pns2) = cdb_hr::Namespace::Read(row);
      ResultAssert::IsSuccess(r);
      const cdb_hr::Namespace& ns2 = *pns2;

      // Compare the materialized row with the original in-memory object model.
      Assert::AreEqual(ns1.GetVersion(), ns2.GetVersion());
      Assert::AreEqual(ns1.GetName(), ns2.GetName());
      Assert::AreEqual(ns1.GetComment(), ns2.GetComment());
      Assert::AreEqual(ns1.GetSchemas().size(), ns2.GetSchemas().size());
      for (size_t i = 0; i < ns1.GetSchemas().size(); i++)
      {
        const cdb_hr::Schema& s1 = *ns1.GetSchemas()[i];
        const cdb_hr::Schema& s2 = *ns2.GetSchemas()[i];
        AssertEqual(s1, s2);
      }
      Assert::AreEqual(ns1.GetEnums().size(), ns2.GetEnums().size());
      for (size_t i = 0; i < ns1.GetEnums().size(); i++)
      {
        const cdb_hr::EnumSchema& s1 = *ns1.GetEnums()[i];
        const cdb_hr::EnumSchema& s2 = *ns2.GetEnums()[i];
        AssertEqual(s1, s2);
      }
    }

  TEST_METHOD_WITH_OWNER(SerializeSystemNamespaceTest, "jthunter")
    {
      const cdb_hr::Namespace& ns1 = cdb_hr::SystemSchemaLiteral::GetNamespace();
      SystemSchemaUnitTests::SerializerRoundtripNamespace(ns1);
    }
  };
}
