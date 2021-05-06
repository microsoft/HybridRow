// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once

// ReSharper disable four CommentTypo
/*

  This xxHash64 implementation is based on the Netcore xxHash32 C# port which was in turn
  based on the xxHash32 code published by Yann Collet:
  
  https://raw.githubusercontent.com/Cyan4973/xxHash/5c174cfa4e45a42f94082dc0d4539b39696afea1/xxhash.c

  xxHash - Fast Hash algorithm
  Copyright (C) 2012-2016, Yann Collet

  BSD 2-Clause License (http://www.opensource.org/licenses/bsd-license.php)

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above
  copyright notice, this list of conditions and the following disclaimer
  in the documentation and/or other materials provided with the
  distribution.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

  You can contact the author at :
  - xxHash homepage: http://www.xxhash.com
  - xxHash source repository : https://github.com/Cyan4973/xxHash

*/

#include <cstdint>

namespace cdb_core
{
  /// <summary>Utility functions for combining hash codes..</summary>
  struct HashCode final
  {
    template<typename T1, typename H1 = std::hash<T1>>
    constexpr static size_t Combine(const T1& value1)
    {
      // Provide a way of diffusing bits from something with a limited
      // input hash space. For example, many enums only have a few
      // possible hashes, only using the bottom few bits of the code. Some
      // collections are built on the assumption that hashes are spread
      // over a larger space, so diffusing the bits may help the
      // collection work more efficiently.

      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));

      uint64_t hash = MixEmptyState();
      hash += 8;

      hash = QueueRound(hash, hc1);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<typename T1, typename T2, typename H1 = std::hash<T1>, typename H2 = std::hash<T2>>
    constexpr static size_t Combine(const T1& value1, const T2& value2)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));

      uint64_t hash = MixEmptyState();
      hash += 16;

      hash = QueueRound(hash, hc1);
      hash = QueueRound(hash, hc2);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));

      uint64_t hash = MixEmptyState();
      hash += 24;

      hash = QueueRound(hash, hc1);
      hash = QueueRound(hash, hc2);
      hash = QueueRound(hash, hc3);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename T4,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>,
      typename H4 = std::hash<T4>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3, const T4& value4)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));
      uint64_t hc4 = static_cast<uint64_t>(H4{}.operator()(value4));

      uint64_t v1;
      uint64_t v2;
      uint64_t v3;
      uint64_t v4;
      Initialize(v1, v2, v3, v4);

      v1 = Round(v1, hc1);
      v2 = Round(v2, hc2);
      v3 = Round(v3, hc3);
      v4 = Round(v4, hc4);

      uint64_t hash = MixState(v1, v2, v3, v4);
      hash += 32;

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename T4,
      typename T5,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>,
      typename H4 = std::hash<T4>,
      typename H5 = std::hash<T5>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3, const T4& value4,
                                    const T5& value5)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));
      uint64_t hc4 = static_cast<uint64_t>(H4{}.operator()(value4));
      uint64_t hc5 = static_cast<uint64_t>(H5{}.operator()(value5));

      uint64_t v1;
      uint64_t v2;
      uint64_t v3;
      uint64_t v4;
      Initialize(v1, v2, v3, v4);

      v1 = Round(v1, hc1);
      v2 = Round(v2, hc2);
      v3 = Round(v3, hc3);
      v4 = Round(v4, hc4);

      uint64_t hash = MixState(v1, v2, v3, v4);
      hash += 40;

      hash = QueueRound(hash, hc5);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename T4,
      typename T5,
      typename T6,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>,
      typename H4 = std::hash<T4>,
      typename H5 = std::hash<T5>,
      typename H6 = std::hash<T6>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3, const T4& value4,
                                    const T5& value5, const T6& value6)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));
      uint64_t hc4 = static_cast<uint64_t>(H4{}.operator()(value4));
      uint64_t hc5 = static_cast<uint64_t>(H5{}.operator()(value5));
      uint64_t hc6 = static_cast<uint64_t>(H6{}.operator()(value6));

      uint64_t v1;
      uint64_t v2;
      uint64_t v3;
      uint64_t v4;
      Initialize(v1, v2, v3, v4);

      v1 = Round(v1, hc1);
      v2 = Round(v2, hc2);
      v3 = Round(v3, hc3);
      v4 = Round(v4, hc4);

      uint64_t hash = MixState(v1, v2, v3, v4);
      hash += 48;

      hash = QueueRound(hash, hc5);
      hash = QueueRound(hash, hc6);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename T4,
      typename T5,
      typename T6,
      typename T7,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>,
      typename H4 = std::hash<T4>,
      typename H5 = std::hash<T5>,
      typename H6 = std::hash<T6>,
      typename H7 = std::hash<T7>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3, const T4& value4,
                                    const T5& value5, const T6& value6, const T7& value7)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));
      uint64_t hc4 = static_cast<uint64_t>(H4{}.operator()(value4));
      uint64_t hc5 = static_cast<uint64_t>(H5{}.operator()(value5));
      uint64_t hc6 = static_cast<uint64_t>(H6{}.operator()(value6));
      uint64_t hc7 = static_cast<uint64_t>(H7{}.operator()(value7));

      uint64_t v1;
      uint64_t v2;
      uint64_t v3;
      uint64_t v4;
      Initialize(v1, v2, v3, v4);

      v1 = Round(v1, hc1);
      v2 = Round(v2, hc2);
      v3 = Round(v3, hc3);
      v4 = Round(v4, hc4);

      uint64_t hash = MixState(v1, v2, v3, v4);
      hash += 56;

      hash = QueueRound(hash, hc5);
      hash = QueueRound(hash, hc6);
      hash = QueueRound(hash, hc7);

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<
      typename T1,
      typename T2,
      typename T3,
      typename T4,
      typename T5,
      typename T6,
      typename T7,
      typename T8,
      typename H1 = std::hash<T1>,
      typename H2 = std::hash<T2>,
      typename H3 = std::hash<T3>,
      typename H4 = std::hash<T4>,
      typename H5 = std::hash<T5>,
      typename H6 = std::hash<T6>,
      typename H7 = std::hash<T7>,
      typename H8 = std::hash<T8>>
    constexpr static size_t Combine(const T1& value1, const T2& value2, const T3& value3, const T4& value4,
                                    const T5& value5, const T6& value6, const T7& value7, const T8& value8)
    {
      uint64_t hc1 = static_cast<uint64_t>(H1{}.operator()(value1));
      uint64_t hc2 = static_cast<uint64_t>(H2{}.operator()(value2));
      uint64_t hc3 = static_cast<uint64_t>(H3{}.operator()(value3));
      uint64_t hc4 = static_cast<uint64_t>(H4{}.operator()(value4));
      uint64_t hc5 = static_cast<uint64_t>(H5{}.operator()(value5));
      uint64_t hc6 = static_cast<uint64_t>(H6{}.operator()(value6));
      uint64_t hc7 = static_cast<uint64_t>(H7{}.operator()(value7));
      uint64_t hc8 = static_cast<uint64_t>(H8{}.operator()(value8));

      uint64_t v1;
      uint64_t v2;
      uint64_t v3;
      uint64_t v4;
      Initialize(v1, v2, v3, v4);

      v1 = Round(v1, hc1);
      v2 = Round(v2, hc2);
      v3 = Round(v3, hc3);
      v4 = Round(v4, hc4);

      v1 = Round(v1, hc5);
      v2 = Round(v2, hc6);
      v3 = Round(v3, hc7);
      v4 = Round(v4, hc8);

      uint64_t hash = MixState(v1, v2, v3, v4);
      hash += 64;

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

    template<typename T1, typename H1 = std::hash<T1>>
    constexpr void Add(const T1& value) noexcept
    {
      AddHash(static_cast<uint64_t>(H1{}.operator()(value)));
    }

    constexpr void AddHash(uint64_t value) noexcept
    {
      // The original xxHash works as follows:
      // 0. Initialize immediately. We can't do this in a struct (no
      //    default ctor).
      // 1. Accumulate blocks of length 32 (4 uints) into 4 accumulators.
      // 2. Accumulate remaining blocks of length 4 (1 uint64_t) into the
      //    hash.
      // 3. Accumulate remaining blocks of length 1 into the hash.

      // There is no need for #3 as this type only accepts int64s. _queue1,
      // _queue2 and _queue3 are basically a buffer so that when
      // ToHashCode is called we can execute #2 correctly.

      // We need to initialize the xxHash64 state (_v1 to _v4) lazily (see
      // #0) and the last place that can be done if you look at the
      // original code is just before the first block of 32 bytes is mixed
      // in. The xxHash64 state is never used for streams containing fewer
      // than 64 bytes.

      // To see what's really going on here, have a look at the Combine
      // methods.

      // Storing the value of _length locally shaves of quite a few bytes
      // in the resulting machine code.
      uint64_t previousLength = m_length++;
      uint64_t position = previousLength % 4;

      // Switch can't be inlined.

      if (position == 0)
      {
        m_queue1 = value;
      }
      else if (position == 1)
      {
        m_queue2 = value;
      }
      else if (position == 2)
      {
        m_queue3 = value;
      }
      else // position == 3
      {
        if (previousLength == 3)
        {
          Initialize(m_v1, m_v2, m_v3, m_v4);
        }

        m_v1 = Round(m_v1, m_queue1);
        m_v2 = Round(m_v2, m_queue2);
        m_v3 = Round(m_v3, m_queue3);
        m_v4 = Round(m_v4, value);
      }
    }

    [[nodiscard]] constexpr size_t ToHashCode() const noexcept
    {
      // Storing the value of _length locally shaves of quite a few bytes
      // in the resulting machine code.
      uint64_t length = m_length;

      // position refers to the *next* queue position in this method, so
      // position == 1 means that _queue1 is populated; _queue2 would have
      // been populated on the next call to Add.
      uint64_t position = length % 4;

      // If the length is less than 4, _v1 to _v4 don't contain anything
      // yet. xxHash64 treats this differently.

      uint64_t hash = length < 4 ? MixEmptyState() : MixState(m_v1, m_v2, m_v3, m_v4);

      // _length is incremented once per Add(uint64_t) and is therefore 8
      // times too small (xxHash length is in bytes, not int64s).

      hash += length * 8;

      // Mix what remains in the queue

      // Switch can't be inlined right now, so use as few branches as
      // possible by manually excluding impossible scenarios (position > 1
      // is always false if position is not > 0).
      if (position > 0)
      {
        hash = QueueRound(hash, m_queue1);
        if (position > 1)
        {
          hash = QueueRound(hash, m_queue2);
          if (position > 2)
          {
            hash = QueueRound(hash, m_queue3);
          }
        }
      }

      hash = MixFinal(hash);
      return static_cast<size_t>(hash);
    }

  private:

    constexpr static void Initialize(uint64_t& v1, uint64_t& v2, uint64_t& v3, uint64_t& v4)
    {
      v1 = s_seed + Prime1 + Prime2;
      v2 = s_seed + Prime2;
      v3 = s_seed;
      v4 = s_seed - Prime1;
    }

    constexpr static uint64_t RotateLeft(uint64_t x, int r)
    {
      return ((x << r) | (x >> (64 - r)));
    }

    constexpr static uint64_t Round(uint64_t hash, uint64_t input)
    {
      return RotateLeft(hash + input * Prime2, 31) * Prime1;
    }

    constexpr static uint64_t QueueRound(uint64_t hash, uint64_t queuedValue)
    {
      return RotateLeft(hash ^ Round(0, queuedValue), 27) * Prime1 + Prime4;
    }

    constexpr static uint64_t MixState(uint64_t v1, uint64_t v2, uint64_t v3, uint64_t v4)
    {
      return RotateLeft(v1, 1) + RotateLeft(v2, 7) + RotateLeft(v3, 12) + RotateLeft(v4, 18);
    }

    constexpr static uint64_t MixEmptyState()
    {
      return s_seed + Prime5;
    }

    constexpr static uint64_t MixFinal(uint64_t hash)
    {
      hash ^= hash >> 33;
      hash *= Prime2;
      hash ^= hash >> 29;
      hash *= Prime3;
      hash ^= hash >> 32;
      return hash;
    }

  private:
    static constexpr uint64_t s_seed = 0x9FB21C651E98DF25ULL;

    static constexpr uint64_t Prime1 = 11400714785074694791ULL;
    static constexpr uint64_t Prime2 = 14029467366897019727ULL;
    static constexpr uint64_t Prime3 = 1609587929392839161ULL;
    static constexpr uint64_t Prime4 = 9650029242287828579ULL;
    static constexpr uint64_t Prime5 = 2870177450012600261ULL;

    uint64_t m_v1{0}, m_v2{0}, m_v3{0}, m_v4{0};
    uint64_t m_queue1{0}, m_queue2{0}, m_queue3{0};
    uint64_t m_length{0};
  };
}
