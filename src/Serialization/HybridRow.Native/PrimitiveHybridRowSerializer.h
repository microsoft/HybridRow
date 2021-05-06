// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

#include "LayoutType.h"
#include "RowCursor.h"

namespace cdb_hr
{
  template<typename T>
  struct EqualityComparer final
  {
    constexpr bool operator()(const T& x, const T& y) const { return x == y; }
    constexpr std::size_t operator()(const T& s) const noexcept { return std::hash<T>{}.operator()(s); }
  };

  struct Int8HybridRowSerializer final
  {
    using value_type = int8_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const_reference value) noexcept
    {
      return LayoutLiteral::Int8.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, int8_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Int8.ReadSparse(row, scope);
    }
  };

  struct Int16HybridRowSerializer final
  {
    using value_type = int16_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const int16_t& value) noexcept
    {
      return LayoutLiteral::Int16.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, int16_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Int16.ReadSparse(row, scope);
    }
  };

  struct Int32HybridRowSerializer final
  {
    using value_type = int32_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const int32_t& value) noexcept
    {
      return LayoutLiteral::Int32.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, int32_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Int32.ReadSparse(row, scope);
    }
  };

  struct Int64HybridRowSerializer final
  {
    using value_type = int64_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const int64_t& value) noexcept
    {
      return LayoutLiteral::Int64.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, int64_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Int64.ReadSparse(row, scope);
    }
  };

  struct UInt8HybridRowSerializer final
  {
    using value_type = uint8_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const uint8_t& value) noexcept
    {
      return LayoutLiteral::UInt8.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, uint8_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::UInt8.ReadSparse(row, scope);
    }
  };

  struct UInt16HybridRowSerializer final
  {
    using value_type = uint16_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const uint16_t& value) noexcept
    {
      return LayoutLiteral::UInt16.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, uint16_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::UInt16.ReadSparse(row, scope);
    }
  };

  struct UInt32HybridRowSerializer final
  {
    using value_type = uint32_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const uint32_t& value) noexcept
    {
      return LayoutLiteral::UInt32.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, uint32_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::UInt32.ReadSparse(row, scope);
    }
  };

  struct UInt64HybridRowSerializer final
  {
    using value_type = uint64_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const uint64_t& value) noexcept
    {
      return LayoutLiteral::UInt64.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, uint64_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::UInt64.ReadSparse(row, scope);
    }
  };

  struct Float32HybridRowSerializer final
  {
    using value_type = float32_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const float32_t& value) noexcept
    {
      return LayoutLiteral::Float32.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, float32_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Float32.ReadSparse(row, scope);
    }
  };

  struct Float64HybridRowSerializer final
  {
    using value_type = float64_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const float64_t& value) noexcept
    {
      return LayoutLiteral::Float64.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, float64_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Float64.ReadSparse(row, scope);
    }
  };

  struct Float128HybridRowSerializer final
  {
    using value_type = float128_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const float128_t& value) noexcept
    {
      return LayoutLiteral::Float128.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, float128_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Float128.ReadSparse(row, scope);
    }
  };

  struct DecimalHybridRowSerializer final
  {
    using value_type = Decimal;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const Decimal& value) noexcept
    {
      return LayoutLiteral::Decimal.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, Decimal> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Decimal.ReadSparse(row, scope);
    }
  };

  struct BooleanHybridRowSerializer final
  {
    using value_type = bool;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const bool& value) noexcept
    {
      return LayoutLiteral::Boolean.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, bool> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Boolean.ReadSparse(row, scope);
    }
  };

  struct NullHybridRowSerializer final
  {
    using value_type = NullValue;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const NullValue& value) noexcept
    {
      return LayoutLiteral::Null.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, NullValue> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Null.ReadSparse(row, scope);
    }
  };

  struct DateTimeHybridRowSerializer final
  {
    using value_type = DateTime;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const DateTime& value) noexcept
    {
      return LayoutLiteral::DateTime.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, DateTime> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::DateTime.ReadSparse(row, scope);
    }
  };

  struct UnixDateTimeHybridRowSerializer final
  {
    using value_type = UnixDateTime;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const UnixDateTime& value) noexcept
    {
      return LayoutLiteral::UnixDateTime.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, UnixDateTime> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::UnixDateTime.ReadSparse(row, scope);
    }
  };

  struct GuidHybridRowSerializer final
  {
    using value_type = Guid;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const Guid& value) noexcept
    {
      return LayoutLiteral::Guid.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, Guid> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::Guid.ReadSparse(row, scope);
    }
  };

  struct MongoDbObjectIdHybridRowSerializer final
  {
    using value_type = MongoDbObjectId;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const MongoDbObjectId& value) noexcept
    {
      return LayoutLiteral::MongoDbObjectId.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, MongoDbObjectId> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::MongoDbObjectId.ReadSparse(row, scope);
    }
  };

  struct Utf8HybridRowSerializer final
  {
    using value_type = std::string;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const value_type& value) noexcept
    {
      return LayoutLiteral::Utf8.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, value_type> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      auto [r, sv] = LayoutLiteral::Utf8.ReadSparse(row, scope);
      if (r != Result::Success)
      {
        return {r, {}};
      }
      return {r, std::string(sv)};
    }
  };

  struct BinaryHybridRowSerializer final
  {
    struct BinaryComparer;
    using value_type = cdb_core::Memory<byte>;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = BinaryComparer;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const cdb_core::ReadOnlySpan<byte>& value) noexcept
    {
      return LayoutLiteral::Binary.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, cdb_core::Memory<byte>> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      auto [r, sp] =  LayoutLiteral::Binary.ReadSparse(row, scope);
      if (r != Result::Success)
      {
        return {r, {}};
      }
      return {r, cdb_core::Memory<byte>(sp)};
    }

    struct BinaryComparer final
    {
      constexpr bool operator()(const_reference x, const_reference y) const
      {
        return x.AsSpan().SequenceEqual(y.AsSpan());
      }

      std::size_t operator()(const_reference s) const noexcept
      {
        cdb_core::HashCode hash{};

        // Add bulk in 8-byte words.
        cdb_core::ReadOnlySpan<uint64_t> span = cdb_core::MemoryMarshal::Cast<byte, uint64_t>(s.AsSpan());
        for (auto i : span)
        {
            hash.AddHash(i);
        }

        // Add any residual as separate bytes.
        cdb_core::ReadOnlySpan<byte> residual = s.AsSpan().Slice(span.Length() * sizeof(uint64_t));
        for (auto i : residual)
        {
            hash.AddHash(static_cast<uint64_t>(i));
        }

        return hash.ToHashCode();
      }
    };

  };

  struct VarIntHybridRowSerializer final
  {
    using value_type = int64_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const int64_t& value) noexcept
    {
      return LayoutLiteral::VarInt.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, int64_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::VarInt.ReadSparse(row, scope);
    }
  };

  struct VarUIntHybridRowSerializer final
  {
    using value_type = uint64_t;
    using owning_type = value_type;
    using const_reference = const value_type&;
    using comparer_type = EqualityComparer<value_type>;

    static Result Write(RowBuffer& row, RowCursor& scope, bool isRoot, const TypeArgumentList& typeArgs,
                        const uint64_t& value) noexcept
    {
      return LayoutLiteral::VarUInt.WriteSparse(row, scope, value);
    }

    static std::tuple<Result, uint64_t> Read(const RowBuffer& row, RowCursor& scope, bool isRoot)
    {
      return LayoutLiteral::VarUInt.ReadSparse(row, scope);
    }
  };
}
