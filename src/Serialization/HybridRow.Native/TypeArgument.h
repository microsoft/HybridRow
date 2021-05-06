// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include "TypeArgumentList.h"

namespace cdb_hr
{
  class LayoutType;
  struct ILayoutType;

  struct TypeArgument final
  {
    constexpr TypeArgument() noexcept : m_type{nullptr}, m_typeArgs{} {}
    ~TypeArgument() noexcept = default;
    TypeArgument(const TypeArgument& other) noexcept = default;
    TypeArgument(TypeArgument&& other) noexcept = default;
    TypeArgument& operator=(const TypeArgument& other) noexcept = default;
    TypeArgument& operator=(TypeArgument&& other) noexcept = default;

    /// <summary>Initializes a new instance of the <see cref="TypeArgument" /> struct.</summary>
    /// <param name="type">The type of the constraint.</param>
    constexpr TypeArgument(const LayoutType* type) noexcept;

    /// <summary>Initializes a new instance of the <see cref="TypeArgument" /> struct.</summary>
    /// <param name="type">The type of the constraint.</param>
    /// <param name="typeArgs">For generic types the type parameters.</param>
    TypeArgument(const LayoutType* type, TypeArgumentList typeArgs) noexcept;

    /// <summary>The physical layout type.</summary>
    [[nodiscard]]
    const LayoutType* GetType() const noexcept;

    /// <summary>If the type argument is itself generic, then its type arguments.</summary>
    [[nodiscard]]
    const TypeArgumentList& GetTypeArgs() const noexcept;

    friend bool operator==(const TypeArgument& lhs, const TypeArgument& rhs);
    friend bool operator!=(const TypeArgument& lhs, const TypeArgument& rhs);

    /// <summary>The physical layout type of the field cast to the specified type.</summary>
    template<typename T, typename = std::enable_if_t<std::is_base_of_v<ILayoutType, T>>>
    const T& TypeAs() const;

    [[nodiscard]]
    tla::string ToString() const noexcept;

    [[nodiscard]]
    size_t GetHashCode() const noexcept;

  private:
    const LayoutType* m_type;
    TypeArgumentList m_typeArgs;
  };

  constexpr TypeArgument::TypeArgument(const LayoutType* type) noexcept: m_type{type}, m_typeArgs{}
  {
    cdb_core::Contract::Requires(type != nullptr);
  }
}
