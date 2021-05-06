// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include "SchemaId.h"

namespace cdb_hr
{
  struct TypeArgument;
  class LayoutType;

  struct TypeArgumentList final
  {
    struct Enumerator;

  public:
    constexpr TypeArgumentList() noexcept : m_args{}, m_schemaId{} {}
    ~TypeArgumentList() noexcept = default;
    TypeArgumentList(const TypeArgumentList& other) noexcept = default;
    TypeArgumentList(TypeArgumentList&& other) noexcept = default;
    TypeArgumentList& operator=(const TypeArgumentList& other) noexcept = default;
    TypeArgumentList& operator=(TypeArgumentList&& other) noexcept = default;
    TypeArgumentList(const tla::vector<TypeArgument>& args) noexcept;
    TypeArgumentList(tla::vector<TypeArgument>&& args) noexcept;
    TypeArgumentList(const TypeArgument& arg1) noexcept;

    /// <summary>Empty value.</summary>
    static TypeArgumentList Empty;

    /// <summary>Initializes a new instance of the <see cref="TypeArgumentList" /> struct.</summary>
    /// <param name="schemaId">For UDT fields, the schema id of the nested layout.</param>
    TypeArgumentList(SchemaId schemaId) noexcept;

    [[nodiscard]]
    size_t GetCount() const noexcept;

    /// <summary>For UDT fields, the schema id of the nested layout.</summary>
    [[nodiscard]]
    SchemaId GetSchemaId() const noexcept;

    const TypeArgument& operator[](size_t i) const noexcept;
    friend bool operator==(const TypeArgumentList& left, const TypeArgumentList& right) noexcept;
    friend bool operator!=(const TypeArgumentList& left, const TypeArgumentList& right) noexcept;

    /// <summary>Gets beginning enumerator for this span.</summary>
    [[nodiscard]]
    Enumerator begin() const;

    /// <summary>Gets the end enumerator for this span.</summary>
    [[nodiscard]]
    Enumerator end() const;

    [[nodiscard]]
    tla::string ToString() const noexcept;

    [[nodiscard]]
    size_t GetHashCode() const noexcept;

    /// <summary>Enumerates the elements of a <see cref="TypeArgumentList" />.</summary>
    struct Enumerator final
    {
      Enumerator() noexcept = delete;
      ~Enumerator() noexcept = default;
      Enumerator(const Enumerator& other) noexcept = default;
      Enumerator(Enumerator&& other) noexcept = default;
      Enumerator& operator=(const Enumerator& other) noexcept = default;
      Enumerator& operator=(Enumerator&& other) noexcept = default;

      friend bool operator==(const Enumerator& lhs, const Enumerator& rhs);
      friend bool operator!=(const Enumerator& lhs, const Enumerator& rhs);

      /// <summary>Advances the enumerator to the next element of the span.</summary>
      Enumerator& operator++() noexcept;
      Enumerator operator++(int) noexcept;

      /// <summary>Gets the element at the current position of the enumerator.</summary>
      [[nodiscard]]
      const TypeArgument& operator*() const noexcept;

    private:
      friend struct TypeArgumentList;

      /// <summary>Initializes a new instance of the <see cref="Enumerator" /> struct.</summary>
      /// <param name="list">The list to enumerate.</param>
      /// <param name="index">Index within the list to start.</param>
      Enumerator(const tla::vector<TypeArgument>* list, size_t index) noexcept;

      /// <summary>The list being enumerated.</summary>
      const tla::vector<TypeArgument>* m_list;

      /// <summary>The next index to yield.</summary>
      size_t m_index;
    };

  private:
    std::shared_ptr<tla::vector<TypeArgument>> m_args;

    /// <summary>For UDT fields, the schema id of the nested layout.</summary>
    SchemaId m_schemaId;
  };
}
