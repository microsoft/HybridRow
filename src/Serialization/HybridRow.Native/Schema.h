// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma once
#include <string>
#include <vector>
#include <memory>
#include <optional>

#include "SchemaId.h"
#include "SchemaLanguageVersion.h"
#include "SchemaOptions.h"
#include "TypeKind.h"
#include "Property.h"
#include "PartitionKey.h"
#include "PrimarySortKey.h"
#include "StaticKey.h"

namespace cdb_hr
{
  class Layout;
  class LayoutCompiler;
  class Namespace;

  /// <summary>A schema describes either table or UDT metadata.</summary>
  /// <remarks>
  /// The schema of a table or UDT describes the structure of row (i.e. which columns and the
  /// types of those columns).  A table schema represents the description of the contents of a collection
  /// level row directly.  UDTs described nested structured objects that may appear either within a table
  /// column or within another UDT (i.e. nested UDTs).
  /// </remarks>
  class Schema final
  {
  public:
    Schema() noexcept :
      m_version{SchemaLanguageVersion::Unspecified},
      m_comment{},
      m_name{},
      m_schemaId{SchemaId::Invalid()},
      m_baseName{},
      m_baseSchemaId{SchemaId::Invalid()},
      m_options{},
      m_type{TypeKind::Schema},
      m_partitionKeys{},
      m_primaryKeys{},
      m_staticKeys{},
      m_properties{} {}

    ~Schema() noexcept = default;
    Schema(const Schema& other) = delete;
    Schema(Schema&& other) = delete;
    Schema& operator=(const Schema& other) = delete;
    Schema& operator=(Schema&& other) = delete;

    /// <summary>The version of the HybridRow Schema Definition Language used to encode this schema.</summary>
    [[nodiscard]] SchemaLanguageVersion GetVersion() const noexcept { return m_version; }
    void SetVersion(SchemaLanguageVersion value) noexcept { m_version = value; }

    /// <summary>An (optional) comment describing the purpose of this schema.</summary>
    /// <remarks>Comments are for documentary purpose only and do not affect the schema at runtime.</remarks>
    [[nodiscard]] std::string_view GetComment() const noexcept { return m_comment; }
    void SetComment(std::string_view value) noexcept { m_comment = value; }

    /// <summary>The name of the schema.</summary>
    /// <remarks>
    /// The name of a schema MUST be unique within its namespace.
    /// <para />
    /// Names must begin with an alpha-numeric character and can only contain alpha-numeric characters and
    /// underscores.
    /// </remarks>
    [[nodiscard]] std::string_view GetName() const noexcept { return m_name; }
    void SetName(std::string_view value) noexcept { m_name = value; }

    /// <summary>The unique identifier for a schema.</summary>
    /// <remarks>Identifiers must be unique within the scope of the database in which they are used.</remarks>
    [[nodiscard]] SchemaId GetSchemaId() const noexcept { return m_schemaId; }
    void SetSchemaId(SchemaId value) noexcept { m_schemaId = value; }

    /// <summary>The name of the schema this schema derives from.</summary>
    [[nodiscard]] std::string_view GetBaseName() const noexcept { return m_baseName; }
    void SetBaseName(std::string_view value) noexcept { m_baseName = value; }

    /// <summary>The unique identifier of the schema this schema derives from.</summary>
    [[nodiscard]] SchemaId GetBaseSchemaId() const noexcept { return m_baseSchemaId; }
    void SetBaseSchemaId(SchemaId value) noexcept { m_baseSchemaId = value; }

    /// <summary>Schema-wide operations.</summary>
    [[nodiscard]] std::optional<std::reference_wrapper<const SchemaOptions>> GetOptions() const noexcept
    {
      return m_options ? std::optional<std::reference_wrapper<const SchemaOptions>>{*m_options} : std::nullopt;
    }

    void SetOptions(std::unique_ptr<SchemaOptions> value) noexcept { m_options = std::move(value); }

    /// <summary>The type of this schema.  This value MUST be <see cref="TypeKind::Schema" />.</summary>
    [[nodiscard]] TypeKind GetType() const noexcept { return m_type; }
    void SetType(TypeKind value) noexcept { m_type = value; }

    /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
    /// <remarks>This field is never null.</remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<Property>>& GetProperties() const noexcept { return m_properties; }
    std::vector<std::unique_ptr<Property>>& GetProperties() noexcept { return m_properties; }
    void SetProperties(std::vector<std::unique_ptr<Property>> value) noexcept { m_properties = std::move(value); }

    /// <summary>An (optional) list of zero or more logical paths that form the partition key.</summary>
    /// <remarks>All paths referenced MUST map to a property within the schema.
    /// <para />
    /// This field is never null.</remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<PartitionKey>>& GetPartitionKeys() const noexcept
    {
      return m_partitionKeys;
    }

    std::vector<std::unique_ptr<PartitionKey>>& GetPartitionKeys() noexcept { return m_partitionKeys; }

    void SetPartitionKeys(std::vector<std::unique_ptr<PartitionKey>> value) noexcept
    {
      m_partitionKeys = std::move(value);
    }

    /// <summary>An (optional) list of zero or more logical paths that form the primary sort key.</summary>
    /// <remarks>All paths referenced MUST map to a property within the schema.
    /// <para />
    /// This field is never null.</remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<PrimarySortKey>>& GetPrimaryKeys() const noexcept
    {
      return m_primaryKeys;
    }

    std::vector<std::unique_ptr<PrimarySortKey>>& GetPrimaryKeys() noexcept { return m_primaryKeys; }

    void SetPrimaryKeys(std::vector<std::unique_ptr<PrimarySortKey>> value) noexcept
    {
      m_primaryKeys = std::move(value);
    }

    /// <summary>An (optional) list of zero or more logical paths that hold data shared by all documents with same partition key.</summary>
    /// <remarks>All paths referenced MUST map to a property within the schema.
    /// <para />
    /// This field is never null.</remarks>
    [[nodiscard]] const std::vector<std::unique_ptr<StaticKey>>& GetStaticKeys() const noexcept { return m_staticKeys; }
    std::vector<std::unique_ptr<StaticKey>>& GetStaticKeys() noexcept { return m_staticKeys; }
    void SetStaticKeys(std::vector<std::unique_ptr<StaticKey>> value) noexcept { m_staticKeys = std::move(value); }

    /// <summary>
    /// Compiles this logical schema into a physical layout that can be used to read and write
    /// rows.
    /// </summary>
    /// <param name="ns">The namespace within which this schema is defined.</param>
    /// <returns>The layout for the schema.</returns>
    [[nodiscard]] std::unique_ptr<Layout> Compile(const Namespace& ns) const noexcept(false);
  private:
    friend class LayoutCompiler;

    /// <summary>
    /// Returns the effective SDL language version of the current schema in the context of the given <see cref="Namespace"/>.
    /// </summary>
    /// <param name="ns">The namespace used to resolve the schema.</param>
    /// <returns>The effective SDL language version.</returns>
    [[nodiscard]] SchemaLanguageVersion GetEffectiveSdlVersion(const Namespace& ns) const noexcept;

    SchemaLanguageVersion m_version;
    tla::string m_comment;
    tla::string m_name;
    SchemaId m_schemaId;
    tla::string m_baseName;
    SchemaId m_baseSchemaId;
    std::unique_ptr<SchemaOptions> m_options;
    TypeKind m_type;

    /// <summary>An (optional) list of zero or more logical paths that form the partition key.</summary>
    std::vector<std::unique_ptr<PartitionKey>> m_partitionKeys;

    /// <summary>An (optional) list of zero or more logical paths that form the primary sort key.</summary>
    std::vector<std::unique_ptr<PrimarySortKey>> m_primaryKeys;

    /// <summary>An (optional) list of zero or more logical paths that hold data shared by all documents that have the same partition key.</summary>
    std::vector<std::unique_ptr<StaticKey>> m_staticKeys;

    /// <summary>A list of zero or more property definitions that define the columns within the schema.</summary>
    std::vector<std::unique_ptr<Property>> m_properties;
  };
}
