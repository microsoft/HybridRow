// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#include "pch.h"
#include "CppUnitTest.h"

namespace cdb_core_test
{
  using Assert = Microsoft::VisualStudio::CppUnitTestFramework::Assert;
  using Logger = Microsoft::VisualStudio::CppUnitTestFramework::Logger;

  template<class T>
  void TestRange(T m, int start, int length)
  {
    int i = 0;
    for (auto b : m.AsSpan())
    {
      Assert::AreEqual(static_cast<byte>(i + start), b);
      i++;
    }
    Assert::AreEqual(length, i);
    Assert::AreEqual(static_cast<uint32_t>(length), m.Length());
    Assert::AreEqual(length == 0, m.IsEmpty());
  }

  TEST_CLASS(MemoryUnitTests)
  {
  public:

    TEST_METHOD(SliceTest)
    {
      Logger::WriteMessage("Memory<byte>:");
      cdb_core::Memory<byte> m{new byte[10], 10};
      for (int i = 0; i < 10; i++)
      {
        m.AsSpan()[i] = static_cast<byte>(i);
      }
      TestRange(m, 0, 10);

      {
        const cdb_core::ReadOnlyMemory<byte> m1 = static_cast<const cdb_core::Memory<byte>&>(m).Slice(5);
        TestRange(m1, 5, 5);
      }

      {
        const auto m2 = m.Slice(5, 2);
        TestRange(m2, 5, 2);
      }

      {
        const auto m3 = m.Slice(10);
        TestRange(m3, 0, 0);
      }

      {
        const auto m4 = m.Slice(0, 0);
        TestRange(m4, 10, 0);
      }

      {
        cdb_core::Memory<byte> m5{new byte[5], 5};
        m.Slice(5, 5).AsSpan().CopyTo(m5.AsSpan());
        TestRange(m5, 5, 5);
      }
    }

    TEST_METHOD(CopyTest)
    {
      Logger::WriteMessage("Memory<byte>:");
      cdb_core::Memory<byte> m{new byte[10], 10};
      for (int i = 0; i < 10; i++)
      {
        m.AsSpan()[i] = static_cast<byte>(i);
      }
      TestRange(m, 0, 10);

      {
        cdb_core::Memory<byte> m2{new byte[2], 2};
        m.Slice(5, 2).AsSpan().CopyTo(m2.AsSpan());
        TestRange(m2, 5, 2);
      }

      {
        cdb_core::Memory<byte> m3{new byte[0], 0};
        m.Slice(10).AsSpan().CopyTo(m3.AsSpan());
        TestRange(m3, 0, 0);
      }

      {
        cdb_core::Memory<byte> m3{nullptr, 0};
        m.Slice(10).AsSpan().CopyTo(m3.AsSpan());
        TestRange(m3, 0, 0);
      }

      {
        cdb_core::Memory<byte> m5{new byte[5], 5};
        m.Slice(5, 5).AsSpan().CopyTo(m5.AsSpan());
        TestRange(m5, 5, 5);
      }

      {
        cdb_core::Memory<byte> m6{m.AsSpan()};
        TestRange(m6, 0, 10);
      }

      {
        std::vector<byte> v{};
        v.reserve(m.Length());
        for (auto b : m.AsSpan()) { v.emplace_back(b); }
        cdb_core::Memory<byte> m7{v};
        TestRange(m7, 0, 10);
      }
    }

    TEST_METHOD(CastTest)
    {
      const int num = 3;
      cdb_core::Memory<int32_t> m{new int32_t[num], static_cast<uint32_t>(num)};
      for (int i = 0; i < num; i++)
      {
        m.AsSpan()[i] = i + 1;
      }

      cdb_core::Span<byte> mb = cdb_core::MemoryMarshal::Cast<int32_t, byte>(m.AsSpan());
      byte expectedBytes[] = {
        static_cast<byte>(1),
        static_cast<byte>(0),
        static_cast<byte>(0),
        static_cast<byte>(0),
        static_cast<byte>(2),
        static_cast<byte>(0),
        static_cast<byte>(0),
        static_cast<byte>(0),
        static_cast<byte>(3),
        static_cast<byte>(0),
        static_cast<byte>(0),
        static_cast<byte>(0),
      };
      for (size_t i = 0; i < std::size(expectedBytes); i++)
      {
        Assert::AreEqual(expectedBytes[i], mb[static_cast<uint32_t>(i)], cdb_core::make_string<std::wstring>(L"i = %llu", i).c_str());
      }
      Assert::AreEqual(std::size(expectedBytes), static_cast<size_t>(mb.Length()));

      cdb_core::Span<int64_t> m64 = cdb_core::MemoryMarshal::Cast<int32_t, int64_t>(m.AsSpan());
      int64_t expected64[] = {static_cast<int64_t>(0x200000001)};
      for (size_t i = 0; i < std::size(expected64); i++)
      {
        Assert::AreEqual(expected64[i], m64[static_cast<uint32_t>(i)], cdb_core::make_string<std::wstring>(L"i = %llu", i).c_str());
      }
      Assert::AreEqual(std::size(expected64), static_cast<size_t>(m64.Length()));
    }

    template<typename T>
    void MemoryEqualityTest(T a, T b, T c, T d)
    {
      // ReSharper disable once CppIdenticalOperandsInBinaryExpression
      Assert::IsTrue(a == a);
      Assert::AreNotEqual(a, b);
      Assert::IsFalse(a == b);
      Assert::IsTrue(a != b);
      Assert::AreEqual(a, c);
      Assert::IsTrue(a == c);
      Assert::IsFalse(a != c);
      Assert::AreNotEqual(a, d);
      Assert::IsFalse(a == d);
      Assert::IsTrue(a != d);
    }

    TEST_METHOD(EqualityTest)
    {
      cdb_core::Memory<std::byte> a = cdb_core::Memory<std::byte>{5};
      cdb_core::Memory<std::byte> b = cdb_core::Memory<std::byte>{5};
      cdb_core::Memory<std::byte> c = a.Slice(0);
      cdb_core::Memory<std::byte> d = a.Slice(1);

      MemoryEqualityTest(a, b, c, d);
      MemoryEqualityTest(a.AsSpan(), b.AsSpan(), c.AsSpan(), d.AsSpan());
      MemoryEqualityTest(
        cdb_core::ReadOnlySpan<byte>(a.AsSpan()),
        cdb_core::ReadOnlySpan<byte>(b.AsSpan()),
        cdb_core::ReadOnlySpan<byte>(c.AsSpan()),
        cdb_core::ReadOnlySpan<byte>(d.AsSpan()));
    }
  };
}

namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<> inline std::wstring ToString<std::byte>(const std::byte& q)
  {
    RETURN_WIDE_STRING(static_cast<int>(q));
  }

  template<> inline std::wstring ToString<std::byte>(const std::byte* q)
  {
    RETURN_WIDE_STRING(q);
  }
}

namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<>
  std::wstring ToString<cdb_core::Memory<std::byte>>(
    const cdb_core::Memory<std::byte>& q)
  {
    return cdb_core::make_string(L"{p:%p l:%u}", &q.AsSpan()[0], q.Length());
  }
}

namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<>
  std::wstring ToString<cdb_core::Span<std::byte>>(
    const cdb_core::Span<std::byte>& q)
  {
    return cdb_core::make_string(L"{p:%p l:%u}", &q[0], q.Length());
  }
}

namespace Microsoft::VisualStudio::CppUnitTestFramework
{
  template<>
  std::wstring ToString<cdb_core::ReadOnlySpan<std::byte>>(
    const cdb_core::ReadOnlySpan<std::byte>& q)
  {
    return cdb_core::make_string(L"{p:%p l:%u}", &q[0], q.Length());
  }
}
