// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1822 // Mark members as static
#pragma warning disable CS1998 // Async method lacks 'await' operators and will run synchronously
#pragma warning disable CA1823 // Avoid unused private fields

#pragma warning disable SA1402 // File may only contain a single type
#pragma warning disable SA1009 // Closing parenthesis should be followed by a space.
#pragma warning disable SA1515 // Single-line comment should be preceded by blank line
#pragma warning disable SA1118 // The parameter spans multiple lines
#pragma warning disable VSTHRD200 // Use "Async" suffix for async methods
#pragma warning disable AsyncMethodsMustEndInAsync // Async method must end in Async

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI.Cpp
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Reflection;
    using System.Text;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    // ReSharper disable ArrangeStaticMemberQualifier
    internal class CppNamespaceGenerator
    {
        private readonly Namespace ns;
        private readonly LayoutResolver resolver;

        /// <summary>
        /// Mapping from types to their descendants.  The inner map includes all descendants within
        /// the closure of a type and is marked 'true' if it is a direct descendent (child).
        /// </summary>
        private readonly Dictionary<Schema, Dictionary<Schema, bool>> hierarchy;

        public CppNamespaceGenerator(Namespace ns)
        {
            this.ns = ns;
            this.resolver = new LayoutResolverNamespace(ns);
            this.hierarchy = new Dictionary<Schema, Dictionary<Schema, bool>>();

            // Build the type hierarchy in two steps:
            // 1.  Add all first level dependencies from parent to children.
            // 2.  Add all descendant dependencies from parent to all descendants.
            foreach (Schema s in ns.Schemas)
            {
                if (s.BaseName == null)
                {
                    continue;
                }

                Schema parent = s.ResolveBase(ns);
                Contract.Invariant(parent != null);
                if (!this.hierarchy.TryGetValue(parent!, out Dictionary<Schema, bool> parentMap))
                {
                    parentMap = new Dictionary<Schema, bool>();
                    this.hierarchy[parent] = parentMap;
                }
                parentMap.Add(s, true);
            }

            foreach (Dictionary<Schema, bool> parentMap in this.hierarchy.Values)
            {
                IEnumerable<Schema> children = from t in parentMap where t.Value select t.Key;
                foreach (Schema child in children.ToList())
                {
                    AddDescendants(parentMap, child);
                }
            }

            void AddDescendants(Dictionary<Schema, bool> parentMap, Schema child)
            {
                if (!this.hierarchy.TryGetValue(child, out Dictionary<Schema, bool> childMap))
                {
                    return;
                }

                IEnumerable<Schema> grandchildren = from t in childMap where t.Value select t.Key;
                foreach (Schema gc in grandchildren)
                {
                    if (parentMap.ContainsKey(gc))
                    {
                        continue;
                    }
                    parentMap[gc] = false;
                    AddDescendants(parentMap, gc);
                }
            }
        }

        public async ValueTask GenerateNamespace(
            List<string> excludes,
            Emit header,
            Emit source,
            bool includeDataContracts,
            bool includeEmbedSchema)
        {
            if (this.ns.Comment != null)
            {
                await header.Comment(this.ns.Comment);
            }

            string cppNamespace = this.ns.CppNamespace ?? this.ns.Name.Replace(".", "::");
            await using Emit.Scope s1 = await header.Namespace(cppNamespace);

            // Emit forward references for Data Contracts.
            foreach (Schema s in this.ns.Schemas)
            {
                if (excludes.Contains(s.Name))
                {
                    continue;
                }

                await header.Statement($"class {s.Name}");
            }

            await source.Comment("ReSharper disable CppClangTidyCppcoreguidelinesProTypeStaticCastDowncast");
            await source.Comment("ReSharper disable CppClangTidyPerformanceMoveConstArg");
            await source.Comment("ReSharper disable CppRedundantControlFlowJump");
            await source.Comment("ReSharper disable CppClangTidyClangDiagnosticExitTimeDestructors");
            await using Emit.Scope s2 = await source.Namespace(cppNamespace);
            await source.Using("std::literals");
            await source.Whitespace();

            if (includeEmbedSchema)
            {
                await this.GenerateHrSchema(header, source);
            }

            if (includeDataContracts)
            {
                foreach (Schema s in this.ns.Schemas)
                {
                    if (excludes.Contains(s.Name))
                    {
                        continue;
                    }

                    Console.WriteLine($"Generating Data Contracts: {s.Name}");
                    await this.GenerateDataContracts(header, s);
                }
            }

            foreach (Schema s in this.ns.Schemas)
            {
                if (excludes.Contains(s.Name))
                {
                    continue;
                }

                Console.WriteLine($"Compiling Schema: {s.Name}");
                await this.GenerateSchema(header, source, s);
            }
        }

        private async ValueTask GenerateHrSchema(Emit header, Emit source)
        {
            string name = $"{this.ns.Name.IdentifierOnly()}HrSchema";
            await header.Whitespace();
            await using (await header.Struct(Keywords.Final, $"{name}"))
            {
                await header.Statement("static const cdb_hr::Namespace& GetNamespace() noexcept");
                await header.Statement("static const cdb_hr::LayoutResolver& GetLayoutResolver() noexcept");
                await header.Whitespace();
                await header.Accessor(Keywords.Private);
                await header.Statement("class Literal");
            }

            await using (await source.Class(Keywords.Final, $"{name}::Literal"))
            {
                await source.Statement($"friend struct {name}");
                await source.Whitespace();
                await using (await source.Method(Keywords.Static | Keywords.Noexcept, "const cdb_hr::Namespace&", "GetNamespace"))
                {
                    await source.Statement("return *s_namespace");
                }

                await using (await source.Method(Keywords.Static, "std::unique_ptr<cdb_hr::LayoutResolver>", "LoadSchema"))
                {
                    await using (await source.Block("auto ns = cdb_core::make_unique_with([&](cdb_hr::Namespace& n)\n{", "});"))
                    {
                        await source.Whitespace();
                        await this.GenerateHrSchemaConstant(source, this.ns);
                    }

                    await source.Whitespace();
                    await source.Statement("s_namespace = ns.get()");
                    await source.Statement("return std::make_unique<cdb_hr::LayoutResolverNamespace>(std::move(ns))");
                }

                await source.Whitespace();
                await source.Variable(
                    Keywords.Inline | Keywords.Static,
                    "cdb_hr::Namespace*",
                    "s_namespace",
                    "nullptr");
                await source.Variable(
                    Keywords.Inline | Keywords.Static,
                    "std::unique_ptr<cdb_hr::LayoutResolver>",
                    "s_layoutResolver",
                    "LoadSchema()");
            }

            await using (await source.Method(Keywords.Noexcept, "const cdb_hr::Namespace&", $"{name}::GetNamespace"))
            {
                await source.Statement("return *Literal::s_namespace");
            }

            await using (await source.Method(Keywords.Noexcept, "const cdb_hr::LayoutResolver&", $"{name}::GetLayoutResolver"))
            {
                await source.Statement("return *Literal::s_layoutResolver");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, Namespace item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Statement(@$"n.SetName(""{item.Name}"")");
            }
            await source.Statement(@$"n.SetVersion(cdb_hr::SchemaLanguageVersion::{item.Version})");
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Statement(@$"n.SetComment(""{item.Comment}"")");
            }
            if (!string.IsNullOrEmpty(item.CppNamespace))
            {
                await source.Statement(@$"n.SetCppNamespace(""{item.CppNamespace}"")");
            }
            foreach (EnumSchema item2 in item.Enums)
            {
                await source.Whitespace("//////////////////////////////////////////////////////////////////////////////");
                await using Emit.Scope s2 = await source.Block(
                    "n.GetEnums().emplace_back(cdb_core::make_unique_with([](cdb_hr::EnumSchema& es)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }
            foreach (Schema item2 in item.Schemas)
            {
                await source.Whitespace("//////////////////////////////////////////////////////////////////////////////");
                await using Emit.Scope s2 = await source.Block(
                    "n.GetSchemas().emplace_back(cdb_core::make_unique_with([](cdb_hr::Schema& s)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, Schema item)
        {
            if (item.Version != SchemaLanguageVersion.Unspecified)
            {
                await source.Statement(@$"s.SetVersion(cdb_hr::SchemaLanguageVersion::{item.Version})");
            }
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Statement(@$"s.SetName(""{item.Name}"")");
            }
            if (item.Type != TypeKind.Schema)
            {
                await source.Statement(@$"s.SetType(cdb_hr::TypeKind::{item.Type})");
            }
            await source.Statement(@$"s.SetSchemaId(cdb_hr::SchemaId{{{item.SchemaId}}})");
            if (!string.IsNullOrEmpty(item.BaseName))
            {
                await source.Statement(@$"s.SetBaseName(""{item.BaseName}"")");
                await source.Statement(@$"s.SetBaseSchemaId(cdb_hr::SchemaId{{{item.BaseSchemaId}}})");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Statement(@$"s.SetComment(""{item.Comment}"")");
            }
            if (item.Options != null)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "s.SetOptions(cdb_core::make_unique_with([](cdb_hr::SchemaOptions& so)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item.Options);
            }

            foreach (PartitionKey item2 in item.PartitionKeys)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "s.GetPartitionKeys().emplace_back(cdb_core::make_unique_with([](cdb_hr::PartitionKey& pk)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }

            foreach (PrimarySortKey item2 in item.PrimaryKeys)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "s.GetPrimaryKeys().emplace_back(cdb_core::make_unique_with([](cdb_hr::PrimarySortKey& psk)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }

            foreach (StaticKey item2 in item.StaticKeys)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "s.GetStaticKeys().emplace_back(cdb_core::make_unique_with([](cdb_hr::StaticKey& sk)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }

            foreach (Property item2 in item.Properties)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "s.GetProperties().emplace_back(cdb_core::make_unique_with([](cdb_hr::Property& p)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, SchemaOptions item)
        {
            if (item.DisallowUnschematized)
            {
                await source.Statement("so.SetDisallowUnschematized(true)");
            }
            if (item.EnablePropertyLevelTimestamp)
            {
                await source.Statement("so.SetEnablePropertyLevelTimestamp(true)");
            }
            if (item.DisableSystemPrefix)
            {
                await source.Statement("so.SetDisableSystemPrefix(true)");
            }
            if (item.Abstract)
            {
                await source.Statement("so.SetAbstract(true)");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, PartitionKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Statement(@$"pk.SetPath(""{item.Path}"")");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, PrimarySortKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Statement(@$"psk.SetPath(""{item.Path}"")");
            }
            if (item.Direction != SortDirection.Ascending)
            {
                await source.Statement(@$"psk.SetDirection(SortDirection::{item.Direction})");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, StaticKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Statement(@$"sk.SetPath(""{item.Path}"")");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, Property item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Statement(@$"p.SetPath(""{item.Path}"")");
            }
            if (item.AllowEmpty != AllowEmptyKind.None)
            {
                await source.Statement(@$"p.SetAllowEmpty(AllowEmptyKind::{item.AllowEmpty})");
            }
            if (item.PropertyType != null)
            {
                await using Emit.Scope scope3 = await source.Block(@"p.SetPropertyType(", ");");
                await this.GenerateHrSchemaConstant(source, scope3, item.PropertyType);
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Statement(@$"p.SetComment(""{item.Comment}"")");
            }
            if (!string.IsNullOrEmpty(item.ApiName))
            {
                await source.Statement(@$"p.SetApiName(""{item.ApiName}"")");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, Emit.Scope scope, PropertyType item)
        {
            switch (item)
            {
                case PrimitivePropertyType p:
                    await this.GenerateHrSchemaConstant(source, scope, p);
                    return;
                case UdtPropertyType p:
                {
                    string nullableClause = p.Nullable ? "" : ", false";
                    nullableClause = p.Immutable ? $"{p.Nullable}, true" : nullableClause;
                    await source.Block(
                        $"\nstd::make_unique<cdb_hr::UdtPropertyType>(\"{p.Name}\", cdb_hr::SchemaId{{{p.SchemaId}}}{nullableClause})",
                        scope);
                    return;
                }
                case ArrayPropertyType p:
                {
                    string nullableClause = p.Nullable ? "" : ", false";
                    nullableClause = p.Immutable ? $"{p.Nullable}, true" : nullableClause;
                    await using Emit.Scope s2 = await source.Block("\nstd::make_unique<cdb_hr::ArrayPropertyType>(", $"{nullableClause})", scope);
                    await this.GenerateHrSchemaConstant(source, s2, p.Items);
                    return;
                }
                case TuplePropertyType p:
                {
                    string nullableClause = p.Nullable ? "" : ", false";
                    nullableClause = p.Immutable ? $"{p.Nullable}, true" : nullableClause;
                    await using Emit.Scope s2 = await source.Block("\nstd::make_unique<cdb_hr::TuplePropertyType>(", $"){nullableClause})", scope);
                    await using (Emit.Scope s3 = await source.Block("\ncdb_hr::IHybridRowSerializer::make_unique_vector<cdb_hr::PropertyType>(", "", s2))
                    {
                        int i = 0;
                        foreach (PropertyType item2 in p.Items)
                        {
                            await source.Whitespace();
                            await source.Block("");
                            await using Emit.Scope s4 = await source.Block("", (++i == p.Items.Count) ? "" : ",", s3);
                            await this.GenerateHrSchemaConstant(source, s4, item2);
                        }
                    }
                    await source.Whitespace();
                    await source.Block("");
                    return;
                }
                default:
                    Contract.Fail($"Not Yet Implemented: {item.GetType().Name}");
                    return;
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, Emit.Scope scope, PrimitivePropertyType item)
        {
            await using Emit.Scope scope2 = await source.Block(
                "cdb_core::make_unique_with([](cdb_hr::PrimitivePropertyType& pt)\n{",
                "})",
                scope, true);
            await source.Whitespace();
            await source.Statement($"pt.SetType(cdb_hr::TypeKind::{item.Type})");
            if (item.Length != 0)
            {
                await source.Statement($"pt.SetLength({item.Length})");
            }
            if (item.Storage != StorageKind.Sparse)
            {
                await source.Statement($"pt.SetStorage(cdb_hr::StorageKind::{item.Storage})");
            }
            if (!string.IsNullOrEmpty(item.Enum))
            {
                await source.Statement(@$"pt.SetEnum(""{item.Enum}"")");
            }
            if (item.RowBufferSize)
            {
                await source.Statement(@"pt.SetRowBufferSize(true)");
            }
            if (!string.IsNullOrEmpty(item.ApiType))
            {
                await source.Statement(@$"pt.SetApiType(""{item.ApiType}"")");
            }
            if (!item.Nullable)
            {
                await source.Statement(@$"pt.SetNullable({item.Nullable.ToString().ToLowerInvariant()})");
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, EnumSchema item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Statement(@$"es.SetName(""{item.Name}"")");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Statement(@$"es.SetComment(""{item.Comment}"")");
            }
            await source.Statement(@$"es.SetType(cdb_hr::TypeKind::{item.Type})");
            if (!string.IsNullOrEmpty(item.ApiType))
            {
                await source.Statement(@$"es.SetApiType(""{item.ApiType}"")");
            }
            foreach (EnumValue item2 in item.Values)
            {
                await using Emit.Scope scope2 = await source.Block(
                    "es.GetValues().emplace_back(cdb_core::make_unique_with([](cdb_hr::EnumValue& ev)\n{",
                    "}));");
                await source.Whitespace();
                await this.GenerateHrSchemaConstant(source, item2);
            }
        }

        private async ValueTask GenerateHrSchemaConstant(Emit source, EnumValue item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Statement(@$"ev.SetName(""{item.Name}"")");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Statement(@$"ev.SetComment(""{item.Comment}"")");
            }
            await source.Statement(@$"ev.SetValue({item.Value})");
        }

        private async ValueTask GenerateDataContracts(Emit header, Schema s)
        {
            await header.Whitespace();
            if (s.Comment != null)
            {
                await header.DocComment(s.Comment);
            }

            // If the type has a dotted name then use only the final identifier.
            string name = s.Name.IdentifierOnly();
            Keywords flags = Keywords.Public;
            flags |= (s.BaseName == null) ? Keywords.Final : 0;
            await using Emit.Scope s1 = await header.Class(flags, name, s.BaseName);
            await header.Accessor(Keywords.Public);
            foreach (Property p in s.Properties)
            {
                await header.AutoProperty(p.ConstRefType(this.ns), p.OwningType(this.ns), p.AsPascal(), p.AsField());
            }
            await header.Accessor(Keywords.Private);
            foreach (Property p in s.Properties)
            {
                await header.Variable(Keywords.None, p.OwningType(this.ns), p.AsField(), "");
            }
        }

        private async ValueTask GenerateSchema(Emit header, Emit source, Schema s)
        {
            // If the type has a dotted name then use only the final identifier.
            string name = s.Name.IdentifierOnly();
            string typename = $"{name}HybridRowSerializer";

            ///////////////////////////////////////////////////////////////
            // Generate serializer declaration.
            ///////////////////////////////////////////////////////////////
            await header.Whitespace();
            if (s.Comment != null)
            {
                await header.DocComment(s.Comment);
            }
            await using (await header.Struct(Keywords.Final, typename))
            {
                await header.Using("value_type", name);
                await header.Using("owning_type", "std::unique_ptr<value_type>");
                await header.Variable(Keywords.Constexpr | Keywords.Static, "cdb_hr::SchemaId", "Id", s.SchemaId.ToString());
                await header.Variable(Keywords.Constexpr | Keywords.Static, "uint32_t", "Size", 
                    this.resolver.Resolve(s.SchemaId).Size.ToString());
                await header.Whitespace();

                ///////////////////////////////////////////////////////////////
                // Generate serializer definition.
                ///////////////////////////////////////////////////////////////

                await source.Whitespace();
                await using (await source.Class(Keywords.Final, typename + "::Literal"))
                {
                    await source.Statement($"friend struct {typename}");
                    await source.Whitespace();

                    // Emit the names.
                    foreach (Property p in s.Properties)
                    {
                        await source.Variable(
                            Keywords.Constexpr | Keywords.Static,
                            "std::string_view",
                            $"{p.AsPascal()}Name",
                            $"\"{p.Path}\"sv");
                    }
                    if (s.BaseName != null)
                    {
                        await source.Variable(
                            Keywords.Constexpr | Keywords.Static,
                            "std::string_view",
                            "__BaseName",
                            "\"__base\"sv");
                    }

                    // Emit the columns.
                    if (s.Properties.Count > 0)
                    {
                        await source.Whitespace();
                        await source.Variable(
                            Keywords.Inline | Keywords.Static,
                            "const cdb_hr::Layout&",
                            "Layout",
                            $"{this.ns.Name.IdentifierOnly()}HrSchema::GetLayoutResolver().Resolve(Id)");
                        await source.Whitespace();
                    }
                    foreach (Property p in s.Properties)
                    {
                        await source.Variable(
                            Keywords.Inline | Keywords.Static | Keywords.Const,
                            "const cdb_hr::LayoutColumn&",
                            $"{p.AsPascal()}Column",
                            $"cdb_hr::IHybridRowSerializer::InitLayoutColumn(Layout, {p.AsPascal()}Name)");
                    }
                    if (s.BaseName != null)
                    {
                        await source.Variable(
                            Keywords.Inline | Keywords.Static | Keywords.Const,
                            "const cdb_hr::LayoutColumn&",
                            "__BaseColumn",
                            "cdb_hr::IHybridRowSerializer::InitLayoutColumn(Layout, __BaseName)");
                    }

                    // Emit the tokens.
                    if (s.HasSparse())
                    {
                        await source.Whitespace();
                        foreach (Property p in s.Properties)
                        {
                            switch (p.PropertyType)
                            {
                                case PrimitivePropertyType pp when pp.Storage != StorageKind.Sparse:
                                    break; // Don't need tokens for non-sparse primitives.
                                default:
                                    await source.Variable(
                                        Keywords.Inline | Keywords.Static,
                                        "const cdb_hr::StringTokenizer::StringToken&",
                                        $"{p.AsPascal()}Token",
                                        $@"
                                cdb_hr::IHybridRowSerializer::InitStringToken(Layout, {p.AsPascal()}Column.GetPath())");
                                    break;
                            }
                        }
                        if (s.BaseName != null)
                        {
                            await source.Variable(
                                Keywords.Inline | Keywords.Static,
                                "const cdb_hr::StringTokenizer::StringToken&",
                                "__BaseToken",
                                @"
                                cdb_hr::IHybridRowSerializer::InitStringToken(Layout, __BaseColumn.GetPath())");
                        }
                    }

                    // Emit the property method declarations.
                    if (s.Properties.Count > 0)
                    {
                        await source.Whitespace();
                    }
                    await source.Statement(
                        $"static cdb_hr::Result Write(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const {name}& value) noexcept");
                    await source.Statement($"static cdb_hr::Result Read(const cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, {name}& value)");
                }

                await this.GenerateWriteRoot(header, source, name, typename, s);
                await this.GenerateWriteProperties(source, name, typename, s);
                await this.GenerateReadRoot(header, source, name, typename, s);
                await this.GenerateReadProperties(source, name, typename, s);

                await header.Whitespace();
                await header.Accessor(Keywords.Private);
                await header.Statement("class Literal");
            }

            await header.Whitespace();
            await header.Statement($"static_assert(cdb_hr::is_hybridrow_serializer_v<{name}, {typename}>)");
        }

        private async Task GenerateWriteRoot(Emit header, Emit source, string name, string typename, Schema s)
        {
            // Emit declaration.
            await header.Statement(
                $@"static cdb_hr::Result Write(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, bool isRoot, const cdb_hr::TypeArgumentList& typeArgs, 
                const {name}& value) noexcept");

            this.hierarchy.TryGetValue(s, out Dictionary<Schema, bool> descendants);
            await using (Emit.Scope unused = await source.Method(
                Keywords.Noexcept,
                "cdb_hr::Result",
                $"{typename}::Write",
                $@"cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, bool isRoot, 
                   const cdb_hr::TypeArgumentList& typeArgs, const {name}& value"))
            {
                if (descendants != null)
                {
                    int i = 0;
                    await using Emit.Scope unused2 = await source.Switch("value.GetRuntimeSchemaId().Id()");
                    foreach (Schema child in from d in descendants where d.Value select d.Key)
                    {
                        if (i++ != 0)
                        {
                            await source.Whitespace();
                        }

                        if (this.hierarchy.TryGetValue(child, out Dictionary<Schema, bool> childMap))
                        {
                            foreach (Schema grandchild in childMap.Keys)
                            {
                                await source.Block(
                                    $@"
                                    case {grandchild.Name}HybridRowSerializer::Id.Id():
                                    ");
                            }
                        }

                        await source.Block(
                            $@"
                            case {child.Name}HybridRowSerializer::Id.Id():
                            ");
                        await using Emit.Scope unused3 = await source.Braces();
                        await source.Block(
                            $@"
                            return {child.Name}HybridRowSerializer::Write(row, scope, isRoot, typeArgs,
                              static_cast<const {child.Name}&>(value));
                            ");
                    }
                    await source.Whitespace();
                    await source.Block(
                        @"
                          default:
                            break;
                            ");
                }

                if (descendants != null)
                {
                    await source.Whitespace();
                }
                if (s.Options?.Abstract ?? false)
                {
                    await source.Block(
                        @"
                            cdb_core::Contract::Fail(""Type is abstract."");
                        ");
                }
                else
                {
                    await source.Block(
                        $@"
                    if (isRoot)
                    {{
                      return {typename}::Literal::Write(row, scope, value);
                    }}

                    auto [r, childScope] = cdb_hr::LayoutLiteral::UDT.WriteScope(row, scope, Id);
                    if (r != cdb_hr::Result::Success)
                    {{
                      return r;
                    }}

                    r = {typename}::Literal::Write(row, childScope, value);
                    if (r != cdb_hr::Result::Success)
                    {{
                      return r;
                    }}

                    scope.Skip(row, childScope);
                    return cdb_hr::Result::Success;
                    ");
                }
            }

            if (descendants != null)
            {
                // Emit declaration.
                await header.Statement(
                    $"static cdb_hr::Result WriteBase(cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const {name}& value) noexcept");

                await using Emit.Scope unused1 = await source.Method(
                    Keywords.Noexcept,
                    "cdb_hr::Result",
                    $"{typename}::WriteBase",
                    $"cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const {name}& value");
                await source.Block(
                    $@"
                    auto [r, childScope] = cdb_hr::LayoutLiteral::UDT.WriteScope(row, scope, Id);
                    if (r != cdb_hr::Result::Success)
                    {{
                      return r;
                    }}

                    r = {typename}::Literal::Write(row, childScope, value);
                    if (r != Result::Success)
                    {{
                      return r;
                    }}

                    scope.Skip(row, childScope);
                    return cdb_hr::Result::Success;
                    ");
            }
        }

        private async Task GenerateWriteProperties(Emit source, string name, string typename, Schema s)
        {
            await using Emit.Scope unused = await source.Method(
                Keywords.Noexcept,
                "cdb_hr::Result",
                $"{typename}::Literal::Write",
                $"cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, const {name}& value");

            // Handle fixed properties first.
            int i = 0;
            Property rowBufferSizeProp = null;
            foreach (Property p in s.Properties)
            {
                if (!(p.PropertyType is PrimitivePropertyType pp) || (pp.Storage != StorageKind.Fixed))
                {
                    continue;
                }

                // Defer RowBufferSize properties till the end.
                if (pp.RowBufferSize)
                {
                    rowBufferSizeProp = p;
                    continue;
                }

                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await source.Control($"if ({NullCheck(p)})");
                await source.Statement(
                    $@"cdb_hr::Result r = {p.Instance(this.ns)}.WriteFixed(
                                            row, scope, {cname}Column, {p.Cast(this.ns, cname)})");
                await source.Block(
                    @"
                        if (r != cdb_hr::Result::Success)
                        {
                          return r;
                        }
                    ");
            }

            // Handle variable properties.
            foreach (Property p in s.Properties)
            {
                if (!(p.PropertyType is PrimitivePropertyType pp) || (pp.Storage != StorageKind.Variable))
                {
                    continue;
                }

                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await source.Control($"if ({NullCheck(p)})");
                await source.Statement(
                    $@"cdb_hr::Result r = {p.Instance(this.ns)}.WriteVariable(
                                            row, scope, {cname}Column, {p.Cast(this.ns, cname)})");
                await source.Block(
                    @"
                        if (r != cdb_hr::Result::Success)
                        {
                          return r;
                        }
                    ");
            }

            // Handle sparse properties.
            foreach (Property p in s.Properties)
            {
                if ((p.PropertyType is PrimitivePropertyType pp1) && (pp1.Storage != StorageKind.Sparse))
                {
                    continue;
                }

                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await source.Control($"if ({NullCheck(p)})");
                await source.Statement($"scope.Find(row, {cname}Column.GetPath())");
                switch (p.PropertyType)
                {
                    case PrimitivePropertyType _:
                        await source.Statement(
                            $@"cdb_hr::Result r = {p.Instance(this.ns)}.WriteSparse(
                                                    row, scope, {p.Cast(this.ns, cname)})");
                        break;
                    case ArrayPropertyType apt:
                    {
                        string arrayType =
                            (apt.Items is UdtPropertyType upt && this.hierarchy.ContainsKey(upt.Resolve(this.ns)))
                                ? "Array"
                                : "TypedArray";
                        await source.Statement(
                            $@"cdb_hr::Result r = cdb_hr::{arrayType}HybridRowSerializer<{apt.Items.UnderlyingType(this.ns)}, {apt.Items.UnderlyingTypeSerializer(this.ns)}>::Write(
                                                row,
                                                scope,
                                                false,
                                                {p.AsPascal()}Column.GetTypeArgs(),
                                                {p.Cast(this.ns, cname)})");
                        break;
                    }
                    case TuplePropertyType tpt:
                    {
                        await source.Statement(
                            $@"cdb_hr::Result r = cdb_hr::{tpt.UnderlyingTypeSerializer(this.ns)}::Write(
                                                row,
                                                scope,
                                                false,
                                                {p.AsPascal()}Column.GetTypeArgs(),
                                                {p.Cast(this.ns, cname)})");
                        break;
                    }
                    case UdtPropertyType _:
                        await source.Statement(
                            $@"cdb_hr::Result r = {p.UnderlyingTypeSerializer(this.ns)}::Write(
                                 row, scope, false, {p.AsPascal()}Column.GetTypeArgs(), {p.Cast(this.ns, cname)})");
                        break;
                    default:
                        throw new NotImplementedException();
                }
                await source.Block(
                    @"
                        if (r != cdb_hr::Result::Success)
                        {
                          return r;
                        }
                    ");
            }
            if (s.BaseName != null)
            {
                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                await using Emit.Scope unused1 = await source.Braces();
                await source.Block(
                    $@"
                        scope.Find(row, __BaseColumn.GetPath());
                        cdb_hr::Result r = {s.BaseName}HybridRowSerializer::WriteBase(row, scope, value);
                        if (r != cdb_hr::Result::Success)
                        {{
                          return r;
                        }}
                    ");
            }

            // Emit the RowBufferSize property last (if it exists).
            if (rowBufferSizeProp != null)
            {
                Property p = rowBufferSizeProp;
                if (i != 0)
                {
                    await source.Whitespace();
                }
                string cname = p.AsPascal();
                await source.Comment("Emit RowBufferSize field with actual size of RowBuffer.");
                await source.Statement(
                    $@"cdb_hr::Result r = {p.Instance(this.ns)}.WriteFixed(
                                            row, scope, {cname}Column, static_cast<int32_t>(row.GetLength()))");
                await source.Block(
                    @"
                        if (r != cdb_hr::Result::Success)
                        {
                          return r;
                        }
                    ");
            }

            await source.Whitespace();
            await source.Statement("return cdb_hr::Result::Success");

            static string NullCheck(Property p)
            {
                string cname = p.AsPascal();
                if ((p.AllowEmpty & AllowEmptyKind.EmptyAsNull) == 0)
                {
                    return $"!cdb_hr::IHybridRowSerializer::is_default(value.Get{cname}())";
                }
                return $"!cdb_hr::IHybridRowSerializer::is_default_or_empty(value.Get{cname}())";
            }
        }

        private async Task GenerateReadRoot(Emit header, Emit source, string name, string typename, Schema s)
        {
            // Emit declaration.
            await header.Statement(
                $@"static std::tuple<cdb_hr::Result, std::unique_ptr<{name}>> 
                Read(const cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, bool isRoot)");

            // Emit definition.
            this.hierarchy.TryGetValue(s, out Dictionary<Schema, bool> descendants);
            await using (Emit.Scope unused = await source.Method(
                Keywords.None,
                $"std::tuple<cdb_hr::Result, std::unique_ptr<{name}>>",
                $"{typename}::Read",
                @"const cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, bool isRoot"))
            {
                if (descendants != null)
                {
                    await source.Block(
                        $@"
                            if (!(scope.GetTypeArg().GetType()->IsUDT()))
                            {{
                              return {{cdb_hr::Result::TypeMismatch, std::unique_ptr<{name}>{{}}}};
                            }}

                        ");

                    int i = 0;
                    await using Emit.Scope unused2 = await source.Switch("scope.GetTypeArg().GetTypeArgs().GetSchemaId().Id()");
                    foreach (Schema child in from d in descendants where d.Value select d.Key)
                    {
                        if (i++ != 0)
                        {
                            await source.Whitespace();
                        }

                        if (this.hierarchy.TryGetValue(child, out Dictionary<Schema, bool> childMap))
                        {
                            foreach (Schema grandchild in childMap.Keys)
                            {
                                await source.Block(
                                    $@"
                                    case {grandchild.Name}HybridRowSerializer::Id.Id():
                                    ");
                            }
                        }

                        await source.Block(
                            $@"
                            case {child.Name}HybridRowSerializer::Id.Id():
                            ");
                        await using Emit.Scope unused3 = await source.Braces();
                        await source.Block(
                            $@"
                            auto [r, fieldValue] = {child.Name}HybridRowSerializer::Read(row, scope, false);
                            return {{r, std::move(fieldValue)}};
                            ");
                    }
                    await source.Whitespace();
                    await source.Block(
                        @"
                          default:
                            break;
                            ");
                }

                if (descendants != null)
                {
                    await source.Whitespace();
                }
                if (s.Options?.Abstract ?? false)
                {
                    await source.Block(
                        @"
                          cdb_core::Contract::Fail(""Type is abstract."");
                        ");
                }
                else
                {
                    await source.Block(
                        $@"
                        if (isRoot)
                        {{
                          std::unique_ptr<{name}> value = std::make_unique<{name}>();
                          cdb_hr::Result r = {typename}::Literal::Read(row, scope, *value);
                          return {{r, std::move(value)}};
                        }}

                        auto [r, childScope] = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
                        if (r != cdb_hr::Result::Success)
                        {{
                          return {{r, std::unique_ptr<{name}>{{}}}};
                        }}

                        std::unique_ptr<{name}> value = std::make_unique<{name}>();
                        r = {typename}::Literal::Read(row, childScope, *value);
                        if (r != cdb_hr::Result::Success)
                        {{
                          return {{r, std::unique_ptr<{name}>{{}}}};
                        }}

                        scope.Skip(row, childScope);
                        return {{cdb_hr::Result::Success, std::move(value)}};
                        ");
                }
            }

            if (descendants != null)
            {
                // Emit declaration.
                await header.Statement($"static cdb_hr::Result ReadBase(const RowBuffer& row, RowCursor& scope, {name}& value)");

                await using Emit.Scope unused1 = await source.Method(
                    Keywords.None,
                    "cdb_hr::Result",
                    $"{typename}::ReadBase",
                    $"const cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, {name}& value");
                await source.Block(
                    @$"
                    auto [r, childScope] = cdb_hr::LayoutLiteral::UDT.ReadScope(row, scope);
                    if (r != cdb_hr::Result::Success)
                    {{
                      return r;
                    }}

                    r = {typename}::Literal::Read(row, childScope, value);
                    if (r != cdb_hr::Result::Success)
                    {{
                      return r;
                    }}

                    scope.Skip(row, childScope);
                    return cdb_hr::Result::Success;
                    ");
            }
        }

        private async Task GenerateReadProperties(Emit source, string name, string typename, Schema s)
        {
            await using Emit.Scope unused = await source.Method(
                Keywords.None,
                "cdb_hr::Result",
                $"{typename}::Literal::Read",
                $"const cdb_hr::RowBuffer& row, cdb_hr::RowCursor& scope, {name}& value");

            // Handle fixed properties first.
            int i = 0;
            foreach (Property p in s.Properties)
            {
                if (!(p.PropertyType is PrimitivePropertyType pp) || (pp.Storage != StorageKind.Fixed))
                {
                    continue;
                }

                string cname = p.AsPascal();
                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                await using Emit.Scope unused1 = await source.Braces();
                await source.Statement(
                    $"auto [r, fieldValue] = {p.Instance(this.ns)}.ReadFixed(row, scope, {cname}Column)");
                await source.Block(
                    $@"
                        switch (r)
                        {{
                        case cdb_hr::Result::NotFound:
                          break;
                        case cdb_hr::Result::Success:
                          value.Set{cname}({p.Hoist(p.RCast(this.ns, "fieldValue"))});
                          break;
                        default:
                          return r;
                        }}
                    ");
            }

            // Handle variable properties.
            foreach (Property p in s.Properties)
            {
                if (!(p.PropertyType is PrimitivePropertyType pp) || (pp.Storage != StorageKind.Variable))
                {
                    continue;
                }

                string cname = p.AsPascal();
                if (i++ != 0)
                {
                    await source.Whitespace();
                }
                await using Emit.Scope unused1 = await source.Braces();
                await source.Statement(
                    $"auto [r, fieldValue] = {p.Instance(this.ns)}.ReadVariable(row, scope, {cname}Column)");
                await source.Block(
                    $@"
                        switch (r)
                        {{
                        case cdb_hr::Result::NotFound:
                          break;
                        case cdb_hr::Result::Success:
                          value.Set{cname}({p.Hoist(p.RCast(this.ns, "fieldValue"))});
                          break;
                        default:
                          return r;
                        }}
                    ");
            }

            if (s.HasSparse())
            {
                if (i != 0)
                {
                    i = 0;
                    await source.Whitespace();
                }
                await using Emit.Scope unused1 = await source.Control("while (scope.MoveNext(row))");

                // Handle sparse properties.
                foreach (Property p in s.Properties)
                {
                    if ((p.PropertyType is PrimitivePropertyType pp) && (pp.Storage != StorageKind.Sparse))
                    {
                        continue;
                    }

                    string cname = p.AsPascal();
                    if (i++ != 0)
                    {
                        await source.Whitespace();
                    }

                    await using Emit.Scope unused2 = await source.Control($"if (scope.GetToken() == {cname}Token.GetId())");
                    switch (p.PropertyType)
                    {
                        case ArrayPropertyType ap:
                        {
                            string arrayType =
                                (ap.Items is UdtPropertyType upt && this.hierarchy.ContainsKey(upt.Resolve(this.ns)))
                                    ? "Array"
                                    : "TypedArray";
                            await source.Statement(
                                $@"auto [r, fieldValue] = cdb_hr::{arrayType}HybridRowSerializer<{ap.Items.UnderlyingType(this.ns)}, {ap.Items.UnderlyingTypeSerializer(this.ns)}>::Read(
                                    row, scope, false)");
                            break;
                        }
                        case TuplePropertyType tpt:
                        {
                            string args = string.Join(", ", tpt.Items.Select(x => x.UnderlyingTypeSerializer(this.ns)));
                            await source.Statement(
                                $@"auto [r, fieldValue] = cdb_hr::TypedTupleHybridRowSerializer<{args}HybridRowSerializer>::Read(
                                    row, scope, false)");
                            break;
                        }
                        case UdtPropertyType upt:
                            await source.Statement(
                                $@"auto [r, fieldValue] = {upt.UnderlyingTypeSerializer(this.ns)}::Read(row, scope, false)");
                            break;
                        default:
                            await source.Statement(
                                $@"auto [r, fieldValue] = {p.Instance(this.ns)}.ReadSparse(row, scope)");
                            break;
                    }
                    await source.Block(
                        $@"
                          if (r != cdb_hr::Result::Success)
                          {{
                            return r;
                          }}

                          value.Set{cname}({p.RCast(this.ns, "std::move(fieldValue)")});
                          continue;
                        ");
                }
                if (s.BaseName != null)
                {
                    if (i++ != 0)
                    {
                        await source.Whitespace();
                    }
                    await using Emit.Scope unused2 = await source.Control("if (scope.GetToken() == __BaseToken.GetId())");
                    await source.Block(
                        $@"
                            cdb_hr::Result r = {s.BaseName}HybridRowSerializer::ReadBase(row, scope, value);
                            if (r != cdb_hr::Result::Success)
                            {{
                              return r;
                            }}
                            continue;
                        ");
                }
            }

            if (i != 0)
            {
                await source.Whitespace();
            }
            await source.Statement("return cdb_hr::Result::Success");
        }

        [Flags]
        internal enum Keywords
        {
            None = 0,
            Public = 0x1,
            Private = 0x2,
            Internal = 0x4,
            Static = 0x8,
            ReadOnly = 0x10,
            Const = 0x20,
            Final = 0x40,
            Constexpr = 0x80,
            Noexcept = 0x100,
            Inline = 0x200,
        }

        internal sealed class Emit : IAsyncDisposable
        {
            private readonly TextWriter writer;

            /// <summary>Number of spaces indent is increased for each scope.</summary>
            private readonly int step;

            /// <summary>Current indent.</summary>
            private int indent;

            public Emit(Stream stm)
            {
                this.writer = new StreamWriter(stm, new UTF8Encoding(false, true));
                this.step = 2;
                this.indent = 0;
            }

            public async ValueTask<Scope> Struct(Keywords keywords, string identifier, string baseIdentifier = null)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                string final = ((keywords & Keywords.Final) != 0) ? " final" : "";
                if (baseIdentifier is null)
                {
                    await this.writer.WriteLineAsync($"struct {identifier}{final}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"struct {identifier}{final} : {baseIdentifier}");
                }
                return await this.Braces(terminated: true);
            }

            public async ValueTask<Scope> Class(Keywords keywords, string identifier, string baseIdentifier = null)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                string final = ((keywords & Keywords.Final) != 0) ? " final" : "";
                if (baseIdentifier is null)
                {
                    await this.writer.WriteLineAsync($"class {identifier}{final}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"class {identifier}{final} : {baseIdentifier}");
                }
                return await this.Braces(terminated: true);
            }

            public async ValueTask FileHeader()
            {
                await this.WriteLine("// ------------------------------------------------------------");
                await this.WriteLine("//  Copyright (c) Microsoft Corporation.  All rights reserved.");
                await this.WriteLine("// ------------------------------------------------------------");
                await this.Whitespace();
            }

            public async ValueTask GeneratedComment()
            {
                await this.WriteLine("// ------------------------------------------------------------");
                await this.WriteLine("// This file was generated by:");
                AssemblyName asm = Assembly.GetEntryAssembly().GetName();
                await this.WriteLine($"//   {asm.Name}: {asm.Version}");
                await this.WriteLine("//");
                await this.WriteLine("// This file should not be modified directly.");
                await this.WriteLine("// ------------------------------------------------------------");
            }

            public async ValueTask Whitespace(string comment = null)
            {
                if (comment == null)
                {
                    await this.WriteLine();
                    return;
                }

                foreach (string line in comment.Split('\n'))
                {
                    await this.WriteLine(line);
                }
            }

            public async ValueTask Comment(string comment = null)
            {
                if (comment == null)
                {
                    await this.WriteLine();
                    return;
                }

                foreach (string line in comment.Split('\n'))
                {
                    await this.WriteLine("// {0}", line);
                }
            }

            public async ValueTask DocComment(string comment)
            {
                await this.WriteLine("/// <summary>");
                foreach (string line in comment.Split('\n'))
                {
                    await this.WriteLine("/// {0}", line);
                }
                await this.WriteLine("/// </summary>");
            }

            public async ValueTask<Scope> Namespace(string identifier)
            {
                await this.WriteLine("namespace {0}", identifier);
                return await this.Braces();
            }

            public ValueTask Using(string identifier)
            {
                return this.WriteLine("using namespace {0};", identifier);
            }

            public ValueTask Using(string identifier, string expr)
            {
                return this.WriteLine("using {0} = {1};", identifier, expr);
            }

            public async ValueTask Pragma(string warning, string comment = null)
            {
                await this.Indent();

                if (warning == "once")
                {
                    await this.writer.WriteLineAsync("#pragma once");
                }
                else if (comment == null)
                {
                    await this.writer.WriteLineAsync($"#pragma warning disable {warning}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"#pragma warning disable {warning} // {comment}");
                }
            }

            public async ValueTask Include(string path)
            {
                await this.Indent();
                await this.writer.WriteLineAsync($"#include \"{path}\"");
            }

            public async ValueTask Accessor(Keywords keywords)
            {
                this.indent -= this.step;
                try
                {
                    await this.Indent();
                    switch (keywords)
                    {
                        case Keywords.Public:
                            await this.writer.WriteLineAsync("public:");
                            break;
                        case Keywords.Private:
                            await this.writer.WriteLineAsync("private:");
                            break;
                        default:
                            Contract.Fail($"Invalid accessor: {keywords}");
                            break;
                    }
                }
                finally
                {
                    this.indent += this.step;
                }
            }

            public ValueTask Variable(string typename, string identifier, string expr = null)
            {
                return this.Variable(CppNamespaceGenerator.Keywords.None, typename, identifier, expr);
            }

            public async ValueTask Variable(Keywords keywords, string typename, string identifier, string expr = null)
            {
                await this.Indent();
                await this.Modifiers(keywords);

                if (expr == null)
                {
                    await this.writer.WriteLineAsync($"{typename} {identifier};");
                }
                else
                {
                    if (expr.Contains('\n'))
                    {
                        await this.writer.WriteAsync($"{typename} {identifier}{{");
                        await this.Expr(expr);
                        await this.Whitespace();
                        await this.Indent();
                        await this.writer.WriteLineAsync("};");
                    }
                    else
                    {
                        await this.writer.WriteAsync($"{typename} {identifier}{{");
                        await this.Expr(expr);
                        await this.writer.WriteLineAsync("};");
                    }
                }
            }

            public async ValueTask AutoProperty(string constRefType, string owningType, string identifier, string field)
            {
                await this.Indent();
                // ReSharper disable once StringLiteralTypo
                await this.writer.WriteLineAsync($"[[nodiscard]] {constRefType} Get{identifier}() const noexcept {{ return {field}; }}");
                await this.Indent();
                await this.writer.WriteLineAsync($"void Set{identifier}({owningType} value) noexcept {{ {field} = std::move(value);}}");
            }

            public async ValueTask Statement(string expr)
            {
                await this.Indent();
                await this.Expr(expr);
                await this.writer.WriteLineAsync(";");
            }

            public async ValueTask<Scope> Control(string expr)
            {
                await this.Indent();
                await this.Expr(expr);
                await this.writer.WriteLineAsync();
                return await this.Braces();
            }

            public async ValueTask<Scope> Switch(string expr)
            {
                await this.Indent();
                await this.Expr($"switch ({expr})");
                await this.writer.WriteLineAsync();
                await this.WriteLine("{");
                return new Scope(this, 0, "}");
            }

            public ValueTask<Scope> Block(string startBlock, string endBlock)
            {
                return this.Block(startBlock, endBlock, null);
            }

            public async ValueTask<Scope> Block(string startBlock, string endBlock, Scope outer, bool adopted = false)
            {
                bool isNested = outer != null;
                int blockStep = this.step;
                if (isNested && adopted)
                {
                    blockStep = outer.Indent;
                    this.indent -= blockStep;
                    outer.Indent = 0;
                }
                await this.Block(startBlock, isNested);

                this.indent += blockStep;
                if (isNested)
                {
                    outer.HasNest = true;
                }
                return new Scope(this, blockStep, endBlock, outer, adopted);
            }

            public async ValueTask Block(string block, Scope outer)
            {
                await this.Block(block, outer != null);
                if (outer != null)
                {
                    outer.HasNest = true;
                }
            }

            public ValueTask Block(string block)
            {
                return this.Block(block, false);
            }

            public async ValueTask<Scope> Method(Keywords keywords, string typename, string identifier, string parameters = null)
            {
                await this.Whitespace();
                await this.Indent();
                await this.Modifiers(keywords);

                if (typename != null)
                {
                    await this.writer.WriteAsync($"{typename} ");
                }

                string constMod = ((keywords & Keywords.Const) != 0) ? " const" : "";
                string noexceptMod = ((keywords & Keywords.Noexcept) != 0) ? " noexcept" : "";
                if (parameters == null)
                {
                    await this.writer.WriteLineAsync($"{identifier}(){constMod}{noexceptMod}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"{identifier}({parameters}){constMod}{noexceptMod}");
                }
                return await this.Braces();
            }

            public ValueTask DisposeAsync()
            {
                return this.writer.DisposeAsync();
            }

            public async ValueTask<Scope> Braces(bool terminated = false)
            {
                await this.WriteLine("{");
                this.indent += this.step;
                return new Scope(this, this.step, terminated ? "};" : "}");
            }

            private async ValueTask Block(string block, bool isNested)
            {
                block = block.Replace("\r\n", "\n");
                int i = 0;
                int trim = 0;
                foreach (string line in block.Split("\n"))
                {
                    // Skip leading empty lines (unless nested).
                    if (string.IsNullOrWhiteSpace(line) && i == 0 && !isNested)
                    {
                        continue;
                    }

                    string trimmed;
                    if (i++ == 0)
                    {
                        trimmed = line.TrimStart();
                        trim = line.Length - trimmed.Length;
                    }
                    else
                    {
                        await this.writer.WriteLineAsync();
                        trimmed = (line.Length >= trim) ? line[trim..] : line;
                    }
                    if (!string.IsNullOrWhiteSpace(trimmed))
                    {
                        if (!isNested || i > 1)
                        {
                            await this.Indent();
                        }
                        await this.writer.WriteAsync(trimmed);
                    }
                }
                if (i == 0)
                {
                    await this.Indent();
                }
            }

            private async ValueTask Expr(string expr)
            {
                expr = expr.Replace("\r\n", "\n");
                int i = 0;
                this.indent += this.step;
                foreach (string line in expr.Split("\n"))
                {
                    if (i++ != 0)
                    {
                        await this.writer.WriteLineAsync();
                        await this.Indent();
                    }
                    await this.writer.WriteAsync(line.Trim());
                }
                this.indent -= this.step;
            }

            private ValueTask Indent()
            {
                return new ValueTask(this.writer.WriteAsync(new string(' ', this.indent)));
            }

            private async ValueTask Modifiers(Keywords keywords)
            {
                if ((keywords & Keywords.Inline) != 0)
                {
                    await this.writer.WriteAsync("inline ");
                }
                if ((keywords & Keywords.Constexpr) != 0)
                {
                    await this.writer.WriteAsync("constexpr ");
                }
                if ((keywords & Keywords.Static) != 0)
                {
                    await this.writer.WriteAsync("static ");
                }
            }

            private async ValueTask WriteLine(string format, params object[] args)
            {
                await this.Indent();
                await this.writer.WriteLineAsync(string.Format(format, args));
            }

            private async ValueTask WriteLine(string format)
            {
                await this.Indent();
                await this.writer.WriteLineAsync(format);
            }

            private ValueTask WriteLine()
            {
                return new ValueTask(this.writer.WriteLineAsync());
            }

            public sealed class Scope : IAsyncDisposable
            {
                private readonly Emit parent;
                private readonly string closer;
                private readonly Scope outer;
                private readonly bool adopted;

                public Scope(Emit parent, int indent, string closer, Scope outer = null, bool adopted = false)
                {
                    this.parent = parent;
                    this.Indent = indent;
                    this.closer = closer;
                    this.outer = outer;
                    this.adopted = adopted;
                }

                public bool HasNest { get; set; }

                public int Indent { get; set; }

                public async ValueTask DisposeAsync()
                {
                    this.parent.indent -= this.Indent;
                    if (!this.HasNest)
                    {
                        await this.parent.Indent();
                    }
                    if (this.outer != null)
                    {
                        await this.parent.writer.WriteAsync(this.closer);
                    }
                    else
                    {
                        await this.parent.writer.WriteLineAsync(this.closer);
                    }
                    if ((this.outer != null) && this.adopted)
                    {
                        this.outer.Indent = this.Indent;
                        this.parent.indent += this.Indent;
                    }
                }
            }
        }
    }

    internal static class CodeGenExtensions
    {
        public static string AsPascal(this Property p)
        {
            if (!string.IsNullOrEmpty(p.ApiName))
            {
                return p.ApiName;
            }

            string identifier = p.Path;
            return identifier[..1].ToUpperInvariant() + identifier[1..];
        }

        public static string AsField(this Property p)
        {
            string identifier = p.AsPascal();
            return "m_" + identifier[..1].ToLowerInvariant() + identifier[1..];
        }

        public static string IdentifierOnly(this string fullyQualifiedIdentifier)
        {
            // If the type has a dotted name then use only the final identifier.
            string identifier = fullyQualifiedIdentifier;
            int index = identifier.LastIndexOf('.');
            if (index != -1)
            {
                identifier = identifier[(index + 1)..];
            }
            return identifier;
        }

        public static string Instance(this Property p, Namespace ns)
        {
            return p.PropertyType switch
            {
                PrimitivePropertyType pp when pp.Type == TypeKind.Enum =>
                    $"cdb_hr::LayoutLiteral::{Enum.GetName(typeof(TypeKind), pp.ResolveEnum(ns))}",
                PrimitivePropertyType pp => $"cdb_hr::LayoutLiteral::{Enum.GetName(typeof(TypeKind), pp.Type)}",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string Cast(this Property p, Namespace ns, string cname)
        {
            return p.PropertyType switch
            {
                PrimitivePropertyType pp when !string.IsNullOrEmpty(pp.ApiType) =>
                    $"static_cast<{p.UnderlyingType(ns)}>(cdb_hr::IHybridRowSerializer::get(value.Get{cname}()))",
                PrimitivePropertyType { Type: TypeKind.Enum } =>
                    $"static_cast<{p.UnderlyingType(ns)}>(cdb_hr::IHybridRowSerializer::get(value.Get{cname}()))",
                _ => $"cdb_hr::IHybridRowSerializer::get(value.Get{cname}())",
            };
        }

        public static string RCast(this Property p, Namespace ns, string fieldValue)
        {
            return p.PropertyType switch
            {
                PrimitivePropertyType pp when !string.IsNullOrEmpty(pp.ApiType) => $"static_cast<{pp.ApiType}>({fieldValue})",
                PrimitivePropertyType { Type: TypeKind.Enum } pp => $"static_cast<{pp.Enum}>({fieldValue})",
                _ => fieldValue,
            };
        }

        public static string Hoist(this Property p, string fieldValue)
        {
            return p.PropertyType switch
            {
                PrimitivePropertyType pp when !string.IsNullOrEmpty(pp.ApiType) => fieldValue,
                PrimitivePropertyType { Type: TypeKind.Utf8 } pp => $"std::string({fieldValue})",
                PrimitivePropertyType { Type: TypeKind.Binary} pp => $"cdb_core::Memory({fieldValue})",
                _ => fieldValue,
            };
        }

        public static TypeKind ResolveEnum(this PrimitivePropertyType ep, Namespace ns)
        {
            EnumSchema enumSchema = ns.Enums.Find(es => es.Name == ep.Enum);
            Contract.Invariant(enumSchema != null);
            return enumSchema.Type;
        }

        public static string OwningType(this Property p, Namespace ns, bool stripNullable = false)
        {
            // Don't use nullable types for top-level nullable fields (instead use default-as-null semantics).
            return p.PropertyType.OwningType(ns, stripNullable || p.Path != null);
        }

        public static string OwningType(this PropertyType p, Namespace ns, bool stripNullable = false)
        {
            return p switch
            {
                PrimitivePropertyType pp => pp.UnderlyingType(ns, stripNullable),
                ArrayPropertyType ap when !(ap.Items is null) => $"std::vector<{ap.Items.OwningType(ns)}>",
                TuplePropertyType ap when !(ap.Items is null) => 
                    $"std::tuple<{string.Join(", ", ap.Items.Select(x => x.OwningType(ns)))}>",
                UdtPropertyType up => $"std::unique_ptr<{up.UnderlyingType(ns, stripNullable)}>",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string ConstRefType(this Property p, Namespace ns, bool stripNullable = false)
        {
            // Don't use nullable types for top-level nullable fields (instead use default-as-null semantics).
            return p.PropertyType.ConstRefType(ns, stripNullable || p.Path != null);
        }

        public static string ConstRefType(this PropertyType p, Namespace ns, bool stripNullable = false)
        {
            return p switch
            {
                PrimitivePropertyType pp when p.Type == TypeKind.Utf8 => $"const {pp.UnderlyingType(ns, stripNullable)}&",
                PrimitivePropertyType pp => pp.UnderlyingType(ns, stripNullable),
                ArrayPropertyType ap when !(ap.Items is null) => $"const std::vector<{ap.Items.OwningType(ns)}>&",
                TuplePropertyType ap when !(ap.Items is null) => 
                    $"const std::tuple<{string.Join(", ", ap.Items.Select(x => x.OwningType(ns)))}>&",
                UdtPropertyType up => $"const std::unique_ptr<{up.UnderlyingType(ns, stripNullable)}>&",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string UnderlyingType(this Property p, Namespace ns, bool stripNullable = false)
        {
            // Don't use nullable types for top-level nullable fields (instead use default-as-null semantics).
            return p.PropertyType.UnderlyingType(ns, stripNullable || p.Path != null);
        }

        public static string UnderlyingType(this PropertyType p, Namespace ns, bool stripNullable = false)
        {
            bool nullable = !stripNullable && p.Nullable;
            return p switch
            {
                PrimitivePropertyType pp when pp.Type == TypeKind.Enum =>
                    ns.Enums.Find(es => es.Name == (p as PrimitivePropertyType).Enum).Type.UnderlyingType(nullable),
                PrimitivePropertyType pp => pp.Type.UnderlyingType(nullable),
                ArrayPropertyType ap when !(ap.Items is null) => $"std::vector<{ap.Items.UnderlyingType(ns)}>",
                TuplePropertyType ap when !(ap.Items is null) => 
                    $"std::tuple<{string.Join(", ", ap.Items.Select(x => x.UnderlyingType(ns)))}>",
                UdtPropertyType up => up.Name.IdentifierOnly(),
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string UnderlyingType(this TypeKind type, bool nullable)
        {
            return type switch
            {
                _ when nullable => $"std::optional<{type.UnderlyingType(false)}>",
                TypeKind.Binary => "cdb_core::Memory<byte>",
                TypeKind.Boolean => "bool",
                TypeKind.DateTime => "cdb_hr::DateTime",
                TypeKind.Decimal => "cdb_hr::Decimal",
                TypeKind.Float128 => "cdb_hr::Float128",
                TypeKind.Float32 => "float32_t",
                TypeKind.Float64 => "float64_t",
                TypeKind.Guid => "cdb_hr::Guid",
                TypeKind.Int16 => "int16_t",
                TypeKind.Int32 => "int32_t",
                TypeKind.Int64 => "int64_t",
                TypeKind.Int8 => "int8_t",
                TypeKind.MongoDbObjectId => "cdb_hr::MongoDbObjectId",
                TypeKind.UInt16 => "uint16_t",
                TypeKind.UInt32 => "uint32_t",
                TypeKind.UInt64 => "uint64_t",
                TypeKind.UInt8 => "uint8_t",
                TypeKind.UnixDateTime => "cdb_hr::UnixDateTime",
                TypeKind.Utf8 => "std::string",
                TypeKind.VarInt => "int64_t",
                TypeKind.VarUInt => "uint64_t",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string UnderlyingTypeSerializer(this Property p, Namespace ns)
        {
            return p.PropertyType.UnderlyingTypeSerializer(ns);
        }

        public static string UnderlyingTypeSerializer(this PropertyType p, Namespace ns)
        {
            return p switch
            {
                PrimitivePropertyType pp when pp.Type == TypeKind.Enum =>
                    ns.Enums.Find(es => es.Name == (p as PrimitivePropertyType).Enum).Type.UnderlyingTypeSerializer(p.Nullable),
                PrimitivePropertyType pp => pp.Type.UnderlyingTypeSerializer(p.Nullable),
                ArrayPropertyType arr when !(arr.Items is null) =>
                    $"cdb_hr::TypedArrayHybridRowSerializer<{arr.Items.UnderlyingType(ns)}, {arr.Items.UnderlyingTypeSerializer(ns)}>",
                UdtPropertyType up => $"{up.Name.IdentifierOnly()}HybridRowSerializer",
                TuplePropertyType tpt => 
                    "cdb_hr::TypedTupleHybridRowSerializer<" +
                    string.Join(", ", tpt.Items.Select(x => x.UnderlyingTypeSerializer(ns))) + 
                    ">",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string UnderlyingTypeSerializer(this TypeKind type, bool nullable)
        {
            if (nullable)
            {
                return $"cdb_hr::NullableHybridRowSerializer<{type.UnderlyingType(true)}, " +
                       $"{type.UnderlyingType(false)}, " +
                       $"{type.UnderlyingTypeSerializer(false)}>";
            }

            return $"cdb_hr::{type}HybridRowSerializer";
        }
        
        public static bool HasSparse(this Schema s)
        {
            return (
                       from p in s.Properties
                       where !(p.PropertyType is PrimitivePropertyType pp) || (pp.Storage == StorageKind.Sparse)
                       select p).Any() ||
                   s.BaseName != null;
        }

        public static Schema Resolve(this UdtPropertyType upt, Namespace ns)
        {
            return CodeGenExtensions.Resolve(upt.Name, upt.SchemaId, ns);
        }

        public static Schema ResolveBase(this Schema s, Namespace ns)
        {
            return CodeGenExtensions.Resolve(s.BaseName, s.BaseSchemaId, ns);
        }

        private static Schema Resolve(string name, SchemaId id, Namespace ns)
        {
            Schema s;
            if (id == SchemaId.Invalid)
            {
                s = ns.Schemas.Find(q => q.Name == name);
            }
            else
            {
                s = ns.Schemas.Find(q => q.SchemaId == id);
                if (s.Name != name)
                {
                    throw new Exception($"Ambiguous schema reference: '{name}:{id}'");
                }
            }
            return s;
        }
    }
}
