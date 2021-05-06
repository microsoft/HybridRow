// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <memory>
#include <string>
#include "Result.h"
#include "EnumSchema.h"
#include "RowBuffer.h"
#include "SchemaLanguageVersion.h"
#include "Schema.h"

namespace cdb_hr
{
  class Namespace final
  {
  public :
    /// <summary>Initializes a new instance of the <see cref="Namespace" /> class.</summary>
    Namespace() noexcept : m_version{SchemaLanguageVersion::V2}, m_name{}, m_comment{}, m_enums{}, m_schemas{} { }
    ~Namespace() noexcept = default;
    Namespace(const Namespace& other) noexcept = delete;
    Namespace(Namespace&& other) noexcept = delete;
    Namespace& operator=(const Namespace& other) noexcept = delete;
    Namespace& operator=(Namespace&& other) noexcept = delete;

    /// <summary>The version of the HybridRow Schema Definition Language used to encode this namespace.</summary>
    [[nodiscard]] SchemaLanguageVersion GetVersion() const noexcept { return m_version; }
    void SetVersion(SchemaLanguageVersion value) noexcept { m_version = value; }

    /// <summary>The fully qualified identifier of the namespace.</summary>
    [[nodiscard]] std::string_view GetName() const noexcept { return m_name; }
    void SetName(std::string_view value) noexcept { m_name = value; }

    /// <summary>An (optional) comment describing the purpose of this namespace.</summary>
    /// <remarks>Comments are for documentary purpose only and do not affect the namespace at runtime.</remarks>
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view comment) noexcept { m_comment = comment; }

    /// <summary>An (optional) namespace to use when performing C++ codegen.</summary>
    [[nodiscard]] std::string_view GetCppNamespace() const noexcept { return m_cppNamespace; }
    void SetCppNamespace(std::string_view cppNamespace) noexcept { m_cppNamespace = cppNamespace; }

    /// <summary>The set of enums defined in <see cref="Namespace" />.</summary>
    [[nodiscard]] const std::vector<std::unique_ptr<EnumSchema>>& GetEnums() const noexcept { return m_enums; }
    std::vector<std::unique_ptr<EnumSchema>>& GetEnums() noexcept { return m_enums; }
    void SetEnums(std::vector<std::unique_ptr<EnumSchema>> value) noexcept { m_enums = std::move(value); }

    /// <summary>The set of schemas that make up the <see cref="Namespace" />.</summary>
    /// <remarks>
    /// Namespaces may consist of zero or more table schemas along with zero or more UDT schemas.
    /// Table schemas can only reference UDT schemas defined in the same namespace.  UDT schemas can
    /// contain nested UDTs whose schemas are defined within the same namespace.
    /// </remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<Schema>>& GetSchemas() const noexcept { return m_schemas; }
    std::vector<std::unique_ptr<Schema>>& GetSchemas() noexcept { return m_schemas; }
    void SetSchemas(std::vector<std::unique_ptr<Schema>> value) noexcept { m_schemas = std::move(value); }

    /// <summary>Read Namespace as a row.</summary>
    /// <param name="row">The row to read from.</param>
    /// <returns>Success if the read is successful, an error code otherwise.</returns>
    static std::tuple<Result, std::unique_ptr<Namespace>> Read(const RowBuffer& row) noexcept;

    /// <summary>Write Namespace as a row.</summary>
    /// <param name="row">The row to write into.</param>
    /// <returns>Success if the write is successful, an error code otherwise.</returns>
    Result Write(RowBuffer& row) const noexcept;

  private:
    friend class Schema;

    /// <summary>
    /// Returns the effective SDL language version.
    /// </summary>
    /// <returns>The effective SDL language version.</returns>
    [[nodiscard]] SchemaLanguageVersion GetEffectiveSdlVersion() const noexcept;

    SchemaLanguageVersion m_version;
    tla::string m_name;
    tla::string m_comment;
    tla::string m_cppNamespace;

    /// <summary>The set of enums for the <see cref="Namespace" />.</summary>
    std::vector<std::unique_ptr<EnumSchema>> m_enums;

    /// <summary>The set of schemas that make up the <see cref="Namespace" />.</summary>
    std::vector<std::unique_ptr<Schema>> m_schemas;
  };
}
