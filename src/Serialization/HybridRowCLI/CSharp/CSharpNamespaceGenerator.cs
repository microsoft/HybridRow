// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1402 // File may only contain a single type
#pragma warning disable SA1009 // Closing parenthesis should be followed by a space.
#pragma warning disable VSTHRD200 // Use "Async" suffix for async methods
#pragma warning disable AsyncMethodsMustEndInAsync // Async method must end in Async

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowCLI.CSharp
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
    internal class CSharpNamespaceGenerator
    {
        private readonly Namespace ns;
        private readonly LayoutResolver resolver;

        /// <summary>
        /// Mapping from types to their descendants.  The inner map includes all descendants within
        /// the closure of a type and is marked 'true' if it is a direct descendent (child).
        /// </summary>
        private readonly Dictionary<Schema, Dictionary<Schema, bool>> hierarchy;

        public CSharpNamespaceGenerator(Namespace ns)
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

        public async ValueTask GenerateNamespace(List<string> excludes, Emit emit, bool includeDataContracts, bool includeEmbedSchema)
        {
            if (this.ns.Comment != null)
            {
                await emit.Comment(this.ns.Comment);
            }

            await emit.Whitespace();
            await emit.Comment("ReSharper disable CheckNamespace");
            await emit.Comment("ReSharper disable InconsistentNaming");
            await emit.Comment("ReSharper disable RedundantEmptySwitchSection");
            await emit.Comment("ReSharper disable JoinDeclarationAndInitializer");
            await emit.Comment("ReSharper disable TooWideLocalVariableScope");
            await emit.Comment("ReSharper disable ArrangeStaticMemberQualifier");
            await emit.Comment("ReSharper disable RedundantJumpStatement");
            await emit.Comment("ReSharper disable RedundantUsingDirective");
            await using Emit.Scope s1 = await emit.Namespace(this.ns.Name);
            await emit.Using("System");
            await emit.Using("System.Collections.Generic");
            await emit.Using("System.Runtime.CompilerServices");
            await emit.Using("Microsoft.Azure.Cosmos.Core");
            await emit.Using("Microsoft.Azure.Cosmos.Core.Utf8");
            await emit.Using("Microsoft.Azure.Cosmos.Serialization.HybridRow");
            await emit.Using("Microsoft.Azure.Cosmos.Serialization.HybridRow.IO");
            await emit.Using("Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts");
            await emit.Using("Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO");
            await emit.Using("Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas");

            if (includeEmbedSchema)
            {
                await this.GenerateHrSchema(emit);
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
                    await this.GenerateDataContracts(emit, s);
                }
            }

            foreach (Schema s in this.ns.Schemas)
            {
                if (excludes.Contains(s.Name))
                {
                    continue;
                }

                Console.WriteLine($"Compiling Schema: {s.Name}");
                await this.GenerateSchema(emit, s);
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, Namespace item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Block($"Name = \"{item.Name}\",\n");
            }
            await source.Block($"Version = SchemaLanguageVersion.{item.Version},\n");
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Block($"Comment = \"{item.Comment}\",\n");
            }
            if (!string.IsNullOrEmpty(item.CppNamespace))
            {
                await source.Block($"CppNamespace = \"{item.CppNamespace}\",\n");
            }
            if (item.Enums.Count > 0)
            {
                await using (await source.Block("Enums = new List<EnumSchema>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (EnumSchema item2 in item.Enums)
                    {
                        await source.Whitespace("//////////////////////////////////////////////////////////////////////////////");
                        await using Emit.Scope s1 = await source.Block(
                            @"
                            new EnumSchema
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }

            if (item.Schemas.Count > 0)
            {
                await using (await source.Block("Schemas = new List<Schema>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (Schema item2 in item.Schemas)
                    {
                        await source.Whitespace("//////////////////////////////////////////////////////////////////////////////");
                        await using Emit.Scope s1 = await source.Block(
                            @"
                            new Schema
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, Schema item)
        {
            if (item.Version != SchemaLanguageVersion.Unspecified)
            {
                await source.Block($"Version = SchemaLanguageVersion::{item.Version},\n");
            }
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Block($"Name = \"{item.Name}\",\n");
            }
            if (item.Type != TypeKind.Schema)
            {
                await source.Block($"Type = TypeKind.{item.Type},\n");
            }
            await source.Block($"SchemaId = new SchemaId({item.SchemaId}),\n");
            if (!string.IsNullOrEmpty(item.BaseName))
            {
                await source.Block($"BaseName = \"{item.BaseName}\",\n");
                await source.Block($"BaseSchemaId = new SchemaId({item.BaseSchemaId}),\n");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Block($"Comment = \"{item.Comment}\",\n");
            }
            if (item.Options != null)
            {
                await using Emit.Scope scope2 = await source.Block("Options = new SchemaOptions\n{", "},");
                await source.Whitespace();
                await GenerateHrSchemaConstant(source, item.Options);
            }

            if (item.PartitionKeys.Count > 0)
            {
                await using (await source.Block("PartitionKeys = new List<PartitionKey>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (PartitionKey item2 in item.PartitionKeys)
                    {
                        await using Emit.Scope scope2 = await source.Block(
                            @"
                            new PartitionKey
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }

            if (item.PrimaryKeys.Count > 0)
            {
                await using (await source.Block("PrimaryKeys = new List<PrimarySortKey>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (PrimarySortKey item2 in item.PrimaryKeys)
                    {
                        await using Emit.Scope scope2 = await source.Block(
                            @"
                            new PrimarySortKey
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }

            if (item.StaticKeys.Count > 0)
            {
                await using (await source.Block("StaticKeys = new List<StaticKey>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (StaticKey item2 in item.StaticKeys)
                    {
                        await using Emit.Scope scope2 = await source.Block(
                            @"
                            new StaticKey
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }

            if (item.Properties.Count > 0)
            {
                await using (await source.Block("Properties = new List<Property>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (Property item2 in item.Properties)
                    {
                        await using Emit.Scope scope2 = await source.Block(
                            @"
                            new Property
                            {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, SchemaOptions item)
        {
            if (item.DisallowUnschematized)
            {
                await source.Block("DisallowUnschematized = true,\n");
            }
            if (item.EnablePropertyLevelTimestamp)
            {
                await source.Block("EnablePropertyLevelTimestamp = true,\n");
            }
            if (item.DisableSystemPrefix)
            {
                await source.Block("DisableSystemPrefix = true,\n");
            }
            if (item.Abstract)
            {
                await source.Block("Abstract = true,\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, PartitionKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Block($"Path = \"{item.Path}\",\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, PrimarySortKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Block($"Path = \"{item.Path}\",\n");
            }
            if (item.Direction != SortDirection.Ascending)
            {
                await source.Block($"Direction = SortDirection.{item.Direction},\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, StaticKey item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Block($"Path = \"{item.Path}\",\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, Property item)
        {
            if (!string.IsNullOrEmpty(item.Path))
            {
                await source.Block($"Path = \"{item.Path}\",\n");
            }
            if (item.AllowEmpty != AllowEmptyKind.None)
            {
                await source.Block($"AllowEmpty = AllowEmptyKind.{item.AllowEmpty},\n");
            }
            if (item.PropertyType != null)
            {
                await using Emit.Scope scope3 = await source.Block(@"PropertyType = ", "");
                await GenerateHrSchemaConstant(source, scope3, item.PropertyType);
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Block($"Comment = \"{item.Comment}\",\n");
            }
            if (!string.IsNullOrEmpty(item.ApiName))
            {
                await source.Block($"ApiName = \"{item.ApiName}\",\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, Emit.Scope scope, PropertyType item)
        {
            switch (item)
            {
                case PrimitivePropertyType p:
                    await GenerateHrSchemaConstant(source, scope, p);
                    return;
                case UdtPropertyType p:
                {
                    await using Emit.Scope s2 = await source.Block("new UdtPropertyType\n{", "},", scope);
                    await source.Whitespace();
                    await source.Block($"Name = \"{p.Name}\",\n");
                    await source.Block($"SchemaId = new SchemaId({p.SchemaId}),\n");
                    if (!p.Nullable)
                    {
                        await source.Block("Nullable = false,\n");
                    }
                    if (p.Immutable)
                    {
                        await source.Block("Immutable = true,\n");
                    }

                    return;
                }
                case ArrayPropertyType p:
                {
                    await using Emit.Scope s2 = await source.Block("new ArrayPropertyType\n{", "},", scope);
                    await source.Whitespace();
                    await using (Emit.Scope s3 = await source.Block(@"Items = ", ""))
                    {
                        await GenerateHrSchemaConstant(source, s3, p.Items);
                    }
                    if (!p.Nullable)
                    {
                        await source.Block("Nullable = false,\n");
                    }
                    if (p.Immutable)
                    {
                        await source.Block("Immutable = true,\n");
                    }
                    return;
                }
                case MapPropertyType p:
                {
                    await using Emit.Scope s2 = await source.Block("new MapPropertyType\n{", "},", scope);
                    await source.Whitespace();
                    await using (Emit.Scope s3 = await source.Block(@"Keys = ", ""))
                    {
                        await GenerateHrSchemaConstant(source, s3, p.Keys);
                    }
                    await using (Emit.Scope s4 = await source.Block(@"Values = ", ""))
                    {
                        await GenerateHrSchemaConstant(source, s4, p.Values);
                    }
                    if (!p.Nullable)
                    {
                        await source.Block("Nullable = false,\n");
                    }
                    if (p.Immutable)
                    {
                        await source.Block("Immutable = true,\n");
                    }
                    return;
                }
                case TuplePropertyType p:
                {
                    await using Emit.Scope s2 = await source.Block("new TuplePropertyType\n{", "},", scope);
                    await source.Whitespace();
                    await using (await source.Block("Items = new List<PropertyType>\n{", "},"))
                    {
                        await source.Whitespace();
                        foreach (PropertyType item2 in p.Items)
                        {
                            await CSharpNamespaceGenerator.GenerateHrSchemaConstant(source, default, item2);
                        }
                    }
                    if (!p.Nullable)
                    {
                        await source.Block("Nullable = false,\n");
                    }
                    if (p.Immutable)
                    {
                        await source.Block("Immutable = true,\n");
                    }
                    return;
                }
                default:
                    Contract.Fail($"Not Yet Implemented: {item.GetType().Name}");
                    return;
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, Emit.Scope scope, PrimitivePropertyType item)
        {
            await using Emit.Scope scope2 = await source.Block("new PrimitivePropertyType\n{", "},", scope);
            await source.Whitespace();
            await source.Block($"Type = TypeKind.{item.Type},\n");
            if (item.Length != 0)
            {
                await source.Block($"Length = {item.Length},\n");
            }
            if (item.Storage != StorageKind.Sparse)
            {
                await source.Block($"Storage = StorageKind.{item.Storage},\n");
            }
            if (!string.IsNullOrEmpty(item.Enum))
            {
                await source.Block($"Enum = \"{item.Enum}\",\n");
            }
            if (item.RowBufferSize)
            {
                await source.Block("RowBufferSize = true,\n");
            }
            if (!item.Nullable)
            {
                await source.Block($"Nullable = {item.Nullable.ToString().ToLowerInvariant()},\n");
            }
            if (!string.IsNullOrEmpty(item.ApiType))
            {
                await source.Block($"ApiType = \"{item.ApiType}\",\n");
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, EnumSchema item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Block($"Name = \"{item.Name}\",\n");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Block($"Comment = \"{item.Comment}\",\n");
            }
            await source.Block($"Type = TypeKind.{item.Type},\n");
            if (!string.IsNullOrEmpty(item.ApiType))
            {
                await source.Block($"ApiType = \"{item.ApiType}\",\n");
            }
            if (item.Values.Count > 0)
            {
                await using (await source.Block("Values = new List<EnumValue>\n{", "},"))
                {
                    await source.Whitespace();
                    foreach (EnumValue item2 in item.Values)
                    {
                        await using Emit.Scope s1 = await source.Block(
                            @"
                        new EnumValue
                        {",
                            "},");
                        await source.Whitespace();
                        await GenerateHrSchemaConstant(source, item2);
                    }
                }
            }
        }

        private static async ValueTask GenerateHrSchemaConstant(Emit source, EnumValue item)
        {
            if (!string.IsNullOrEmpty(item.Name))
            {
                await source.Block($"Name = \"{item.Name}\",\n");
            }
            if (!string.IsNullOrEmpty(item.Comment))
            {
                await source.Block($"Comment = \"{item.Comment}\",\n");
            }
            await source.Block($"Value = {item.Value},\n");
        }

        private async ValueTask GenerateHrSchema(Emit emit)
        {
            string name = $"{this.ns.Name.IdentifierOnly()}HrSchema";
            await emit.Whitespace();
            await using Emit.Scope s1 = await emit.Class(Keywords.Internal | Keywords.Static, name);

            await emit.Variable(
                Keywords.Public | Keywords.Static | Keywords.ReadOnly,
                "Namespace",
                "Namespace",
                $"{name}.CreateSchema()");
            await emit.Variable(
                Keywords.Public | Keywords.Static | Keywords.ReadOnly,
                "LayoutResolver",
                "LayoutResolver",
                $"{name}.LoadSchema()");

            await using (await emit.Method(Keywords.Private | Keywords.Static, "Namespace", "CreateSchema"))
            {
                await using (await emit.Block(
                    @"
                    return new Namespace
                    {",
                    "};"))
                {
                    await emit.Whitespace();
                    await GenerateHrSchemaConstant(emit, this.ns);
                }
            }

            await using (await emit.Method(Keywords.Private | Keywords.Static, "LayoutResolver", "LoadSchema"))
            {
                await emit.Statement($"return new LayoutResolverNamespace({name}.Namespace)");
            }
        }

        private async ValueTask GenerateDataContracts(Emit emit, Schema s)
        {
            await emit.Whitespace();
            if (s.Comment != null)
            {
                await emit.DocComment(s.Comment);
            }

            // If the type has a dotted name then use only the final identifier.
            string name = s.Name.IdentifierOnly();
            Keywords flags = Keywords.Public;
            flags |= (s.BaseName == null) ? Keywords.Sealed : 0;
            await using Emit.Scope s1 = await emit.Class(flags, name, s.BaseName);
            foreach (Property p in s.Properties)
            {
                await emit.AutoProperty(Keywords.Public, p.UnderlyingType(this.ns), p.AsPascal());
            }
        }

        private async ValueTask GenerateSchema(Emit emit, Schema s)
        {
            await emit.Whitespace();
            if (s.Comment != null)
            {
                await emit.DocComment(s.Comment);
            }

            // If the type has a dotted name then use only the final identifier.
            string name = s.Name.IdentifierOnly();
            string typename = $"{name}HybridRowSerializer";
            await using Emit.Scope s1 = await emit.Struct(Keywords.Public | Keywords.ReadOnly, typename, $"IHybridRowSerializer<{name}>");
            await emit.Variable(Keywords.Public | Keywords.Const, "int", "SchemaId", s.SchemaId.ToString());
            await emit.Variable(
                Keywords.Public | Keywords.Const,
                "int",
                "Size",
                this.resolver.Resolve(s.SchemaId).Size.ToString());
            await emit.PropertyExpr(Keywords.Public, $"IEqualityComparer<{name}>", "Comparer", $"{name}Comparer.Default");

            // Emit the names.
            foreach (Property p in s.Properties)
            {
                await emit.Variable(
                    Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                    "Utf8String",
                    $"{p.AsPascal()}Name",
                    $"Utf8String.TranscodeUtf16(\"{p.Path}\")");
            }
            if (s.BaseName != null)
            {
                await emit.Variable(
                    Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                    "Utf8String",
                    "__BaseName",
                    "Utf8String.TranscodeUtf16(\"__base\")");
            }

            // Emit the columns.
            if (s.Properties.Count > 0)
            {
                await emit.Whitespace();
            }
            foreach (Property p in s.Properties)
            {
                await emit.Variable(
                    Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                    "LayoutColumn",
                    $"{p.AsPascal()}Column");
            }
            if (s.BaseName != null)
            {
                await emit.Variable(
                    Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                    "LayoutColumn",
                    "__BaseColumn");
            }

            // Emit the tokens.
            if (s.HasSparse())
            {
                await emit.Whitespace();
                foreach (Property p in s.Properties)
                {
                    switch (p.PropertyType)
                    {
                        case PrimitivePropertyType pp when pp.Storage != StorageKind.Sparse:
                            break; // Don't need tokens for non-sparse primitives.
                        default:
                            await emit.Variable(
                                Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                                "StringToken",
                                $"{p.AsPascal()}Token");
                            break;
                    }
                }
                if (s.BaseName != null)
                {
                    await emit.Variable(
                        Keywords.Private | Keywords.Static | Keywords.ReadOnly,
                        "StringToken",
                        "__BaseToken");
                }
            }

            // Emit constructor.
            await using (Emit.Scope unused = await emit.Method(Keywords.Static, null, typename))
            {
                string hrSchemaName = $"{this.ns.Name.IdentifierOnly()}HrSchema";
                await emit.Variable("Layout", "layout", $"{hrSchemaName}.LayoutResolver.Resolve(new SchemaId(SchemaId))");
                await emit.Whitespace();

                if ((s.Properties.Count > 0) || (s.BaseName != null))
                {
                    await emit.Variable("bool", "found");
                }
                foreach (Property p in s.Properties)
                {
                    string cname = p.AsPascal();
                    await emit.Statement($"found = layout.TryFind({cname}Name, out {cname}Column)");
                    await emit.Statement("Contract.Invariant(found)");
                }
                if (s.BaseName != null)
                {
                    await emit.Statement("found = layout.TryFind(__BaseName, out __BaseColumn)");
                    await emit.Statement("Contract.Invariant(found)");
                }

                int i = 0;
                foreach (Property p in s.Properties)
                {
                    switch (p.PropertyType)
                    {
                        case PrimitivePropertyType pp when pp.Storage != StorageKind.Sparse:
                            break; // Don't need ids for non-sparse primitives.
                        default:
                            if (i++ == 0)
                            {
                                await emit.Whitespace();
                            }

                            string cname = p.AsPascal();
                            await emit.Statement($"found = layout.Tokenizer.TryFindToken({cname}Column.Path, out {cname}Token)");
                            await emit.Statement("Contract.Invariant(found)");
                            break;
                    }
                }
                if (s.BaseName != null)
                {
                    if (i == 0)
                    {
                        await emit.Whitespace();
                    }

                    await emit.Statement("found = layout.Tokenizer.TryFindToken(__BaseColumn.Path, out __BaseToken)");
                    await emit.Statement("Contract.Invariant(found)");
                }
            }

            await this.GenerateWriteRoot(emit, name, s);
            await this.GenerateWriteProperties(emit, name, s);
            await this.GenerateReadRoot(emit, name, s);
            await this.GenerateReadProperties(emit, name, s);
            await this.GenerateComparer(emit, name, s);
        }

        private async Task GenerateWriteRoot(Emit emit, string name, Schema s)
        {
            this.hierarchy.TryGetValue(s, out Dictionary<Schema, bool> descendants);
            await using (Emit.Scope unused = await emit.Method(
                Keywords.Public,
                "Result",
                "Write",
                $"ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, {name} value"))
            {
                if (descendants != null)
                {
                    await using Emit.Scope unused2 = await emit.Control("switch (value)");
                    foreach (Schema child in from d in descendants where d.Value select d.Key)
                    {
                        await emit.Block(
                            $@"
                                case {child.Name} p:
                                    return default({child.Name}HybridRowSerializer)
                                        .Write(ref row, ref scope, isRoot, typeArgs, p);
                            ");
                    }
                    await emit.Block(
                        @"
                            default:
                                break;
                            ");
                }

                if (descendants != null)
                {
                    await emit.Whitespace();
                }
                if (s.Options?.Abstract ?? false)
                {
                    await emit.Block(
                        @"
                            Contract.Fail(""Type is abstract."");
                            return Result.Failure;
                        ");
                }
                else
                {
                    await emit.Block(
                        @"
                    if (isRoot)
                    {
                        return Write(ref row, ref scope, value);
                    }

                    Result r = LayoutType.UDT.WriteScope(ref row, ref scope, new SchemaId(SchemaId), out RowCursor childScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    r = Write(ref row, ref childScope, value);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    scope.Skip(ref row, ref childScope);
                    return Result.Success;
                    ");
                }
            }

            if (descendants != null)
            {
                await using Emit.Scope unused1 = await emit.Method(
                    Keywords.Public | Keywords.Static,
                    "Result",
                    "WriteBase",
                    $"ref RowBuffer row, ref RowCursor scope, {name} value");
                await emit.Block(
                    @"
                    Result r = LayoutType.UDT.WriteScope(ref row, ref scope, new SchemaId(SchemaId), out RowCursor childScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    r = Write(ref row, ref childScope, value);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    scope.Skip(ref row, ref childScope);
                    return Result.Success;
                    ");
            }
        }

        private async Task GenerateWriteProperties(Emit emit, string name, Schema s)
        {
            await using Emit.Scope unused = await emit.Method(
                Keywords.Private | Keywords.Static,
                "Result",
                "Write",
                $"ref RowBuffer row, ref RowCursor scope, {name} value");

            await emit.Variable("Result", "r");

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
                    await emit.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await emit.Control($"if ({NullCheck(p)})");
                await emit.Statement($"r = {p.Instance(this.ns)}.WriteFixed(ref row, ref scope, {cname}Column, {p.Cast(this.ns)}value.{cname})");
                await emit.Block(
                    @"
                        if (r != Result.Success)
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
                    await emit.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await emit.Control($"if ({NullCheck(p)})");
                await emit.Statement($"r = {p.Instance(this.ns)}.WriteVariable(ref row, ref scope, {cname}Column, {p.Cast(this.ns)}value.{cname})");
                await emit.Block(
                    @"
                        if (r != Result.Success)
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
                    await emit.Whitespace();
                }
                string cname = p.AsPascal();
                await using Emit.Scope unused1 = await emit.Control($"if ({NullCheck(p)})");
                await emit.Statement($"scope.Find(ref row, {cname}Column.Path)");
                switch (p.PropertyType)
                {
                    case PrimitivePropertyType _:
                        await emit.Statement($@"r = {p.Instance(this.ns)}.WriteSparse(ref row, ref scope, {p.Cast(this.ns)}value.{cname})");
                        break;
                    case ArrayPropertyType apt:
                    {
                        string arrayType =
                            (apt.Items is UdtPropertyType upt && this.hierarchy.ContainsKey(upt.Resolve(this.ns)))
                                ? "Array"
                                : "TypedArray";
                        await emit.Statement(
                            $@"r = default({arrayType}HybridRowSerializer<{apt.Items.UnderlyingType(this.ns)}, {apt.Items.UnderlyingTypeSerializer(this.ns)}>).Write(
                                                ref row,
                                                ref scope,
                                                false,
                                                {p.AsPascal()}Column.TypeArgs,
                                                {p.Cast(this.ns)}value.{p.AsPascal()})");
                        break;
                    }
                    case MapPropertyType apt:
                    {
                        bool hasInheritance =
                            (apt.Keys is UdtPropertyType upt1 && this.hierarchy.ContainsKey(upt1.Resolve(this.ns))) ||
                            (apt.Keys is UdtPropertyType upt2 && this.hierarchy.ContainsKey(upt2.Resolve(this.ns)));
                        Contract.Invariant(!hasInheritance, "Inheritance is not supported in maps");
                        await emit.Statement(
                            $@"r = default(TypedMapHybridRowSerializer<
                                                {apt.Keys.UnderlyingType(this.ns)}, {apt.Keys.UnderlyingTypeSerializer(this.ns)}, 
                                                {apt.Values.UnderlyingType(this.ns)}, {apt.Values.UnderlyingTypeSerializer(this.ns)}
                                                >).Write(
                                                ref row,
                                                ref scope,
                                                false,
                                                {p.AsPascal()}Column.TypeArgs,
                                                {p.Cast(this.ns)}value.{p.AsPascal()})");
                        break;
                    }
                    case TuplePropertyType tpt:
                    {
                        await emit.Statement(
                            $@"r = default({tpt.UnderlyingTypeSerializer(this.ns)}).Write(
                                                ref row,
                                                ref scope,
                                                false,
                                                {p.AsPascal()}Column.TypeArgs,
                                                {p.Cast(this.ns)}value.{p.AsPascal()})");
                        break;
                    }
                    case UdtPropertyType _:
                        await emit.Statement(
                            $@"r = default({p.UnderlyingTypeSerializer(this.ns)})
                            .Write(ref row, ref scope, false, {p.AsPascal()}Column.TypeArgs, {p.Cast(this.ns)}value.{p.AsPascal()})");
                        break;
                    default:
                        throw new NotImplementedException();
                }
                await emit.Block(
                    @"
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    ");
            }
            if (s.BaseName != null)
            {
                if (i++ != 0)
                {
                    await emit.Whitespace();
                }
                await using Emit.Scope unused1 = await emit.Braces();
                await emit.Block(
                    $@"
                        scope.Find(ref row, __BaseColumn.Path);
                        r = {s.BaseName}HybridRowSerializer.WriteBase(ref row, ref scope, value);
                        if (r != Result.Success)
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
                    await emit.Whitespace();
                }
                string cname = p.AsPascal();
                await emit.Comment("Emit RowBufferSize field with actual size of RowBuffer.");
                await emit.Statement($"r = {p.Instance(this.ns)}.WriteFixed(ref row, ref scope, {cname}Column, row.Length)");
                await emit.Block(
                    @"
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    ");
            }

            await emit.Whitespace();
            await emit.Statement("return Result.Success");

            static string NullCheck(Property p)
            {
                string cname = p.AsPascal();
                if ((p.AllowEmpty & AllowEmptyKind.EmptyAsNull) == 0)
                {
                    return $"value.{cname} != default";
                }
                return p.PropertyType.Type switch
                {
                    TypeKind.Utf8 => $"!string.IsNullOrEmpty(value.{cname})",
                    TypeKind.Binary => $"(value.{cname} != null) && (value.{cname}.Length > 0)",
                    TypeKind.Array => $"(value.{cname} != null) && (value.{cname}.Count > 0)",
                    TypeKind.Set => $"(value.{cname} != null) && (value.{cname}.Count > 0)",
                    TypeKind.Map => $"(value.{cname} != null) && (value.{cname}.Count > 0)",
                    TypeKind.Tuple => $"(value.{cname} != null) && (value.{cname}.Count > 0)",
                    TypeKind.Tagged => $"(value.{cname} != null) && (value.{cname}.Count > 0)",
                    _ => $"value.{cname} != default",
                };
            }
        }

        private async Task GenerateReadRoot(Emit emit, string name, Schema s)
        {
            this.hierarchy.TryGetValue(s, out Dictionary<Schema, bool> descendants);
            await using (Emit.Scope unused = await emit.Method(
                Keywords.Public,
                "Result",
                "Read",
                $"ref RowBuffer row, ref RowCursor scope, bool isRoot, out {name} value"))
            {
                if (descendants != null)
                {
                    await emit.Block(
                        @"
                            if (!(scope.TypeArg.Type is LayoutUDT))
                            {
                                value = default;
                                return Result.TypeMismatch;
                            }

                        ");

                    int i = 0;
                    await using Emit.Scope unused2 = await emit.Control("switch (scope.TypeArg.TypeArgs.SchemaId.Id)");
                    foreach (Schema child in from d in descendants where d.Value select d.Key)
                    {
                        if (i++ != 0)
                        {
                            await emit.Whitespace();
                        }

                        if (this.hierarchy.TryGetValue(child, out Dictionary<Schema, bool> childMap))
                        {
                            foreach (Schema grandchild in childMap.Keys)
                            {
                                await emit.Block(
                                    $@"
                                        case {grandchild.Name}HybridRowSerializer.SchemaId:
                                    ");
                            }
                        }

                        await emit.Block(
                            $@"
                                case {child.Name}HybridRowSerializer.SchemaId:
                            ");
                        await using Emit.Scope unused3 = await emit.Braces();
                        await emit.Block(
                            $@"
                                Result r = default({child.Name}HybridRowSerializer)
                                    .Read(ref row, ref scope, false, out {child.Name} fieldValue);
                                value = fieldValue;
                                return r;
                            ");
                    }
                    await emit.Whitespace();
                    await emit.Block(
                        @"
                            default:
                                break;
                            ");
                }

                if (descendants != null)
                {
                    await emit.Whitespace();
                }
                if (s.Options?.Abstract ?? false)
                {
                    await emit.Block(
                        @"
                            Contract.Fail(""Type is abstract."");
                            value = default;
                            return Result.Failure;
                        ");
                }
                else
                {
                    await emit.Block(
                        $@"
                        if (isRoot)
                        {{
                            value = new {name}();
                            return Read(ref row, ref scope, ref value);
                        }}

                        Result r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor childScope);
                        if (r != Result.Success)
                        {{
                            value = default;
                            return r;
                        }}

                        value = new {name}();
                        r = Read(ref row, ref childScope, ref value);
                        if (r != Result.Success)
                        {{
                            value = default;
                            return r;
                        }}

                        scope.Skip(ref row, ref childScope);
                        return Result.Success;
                        ");
                }
            }

            if (descendants != null)
            {
                await using Emit.Scope unused1 = await emit.Method(
                    Keywords.Public | Keywords.Static,
                    "Result",
                    "ReadBase",
                    $"ref RowBuffer row, ref RowCursor scope, ref {name} value");
                await emit.Block(
                    @"
                    Result r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor childScope);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    r = Read(ref row, ref childScope, ref value);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    scope.Skip(ref row, ref childScope);
                    return Result.Success;
                    ");
            }
        }

        private async Task GenerateReadProperties(Emit emit, string name, Schema s)
        {
            await using Emit.Scope unused = await emit.Method(
                Keywords.Private | Keywords.Static,
                "Result",
                "Read",
                $"ref RowBuffer row, ref RowCursor scope, ref {name} value");

            await emit.Variable("Result", "r");

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
                    await emit.Whitespace();
                }
                await using Emit.Scope unused1 = await emit.Braces();
                await emit.Statement(
                    $"r = {p.Instance(this.ns)}.ReadFixed(ref row, ref scope, {cname}Column, out {p.UnderlyingType(this.ns, true)} fieldValue)");
                await emit.Block(
                    $@"
                        switch (r)
                        {{
                            case Result.NotFound:
                                break;
                            case Result.Success:
                                value.{cname} = {p.RCast(this.ns)}fieldValue;
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
                    await emit.Whitespace();
                }
                await using Emit.Scope unused1 = await emit.Braces();
                await emit.Statement(
                    $"r = {p.Instance(this.ns)}.ReadVariable(ref row, ref scope, {cname}Column, out {p.UnderlyingType(this.ns, true)} fieldValue)");
                await emit.Block(
                    $@"
                        switch (r)
                        {{
                            case Result.NotFound:
                                break;
                            case Result.Success:
                                value.{cname} = {p.RCast(this.ns)}fieldValue;
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
                    await emit.Whitespace();
                }
                await using Emit.Scope unused1 = await emit.Control("while (scope.MoveNext(ref row))");

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
                        await emit.Whitespace();
                    }

                    await using Emit.Scope unused2 = await emit.Control($"if (scope.Token == {cname}Token.Id)");
                    switch (p.PropertyType)
                    {
                        case ArrayPropertyType ap:
                        {
                            string arrayType =
                                (ap.Items is UdtPropertyType upt && this.hierarchy.ContainsKey(upt.Resolve(this.ns)))
                                    ? "Array"
                                    : "TypedArray";
                            await emit.Statement(
                                $@"r = default({arrayType}HybridRowSerializer<{ap.Items.UnderlyingType(this.ns)}, {ap.Items.UnderlyingTypeSerializer(this.ns)}>)
                                           .Read(ref row, ref scope, false, out {p.UnderlyingType(this.ns)} fieldValue)");
                            break;
                        }
                        case MapPropertyType mp:
                        {
                            bool hasInheritance =
                                (mp.Keys is UdtPropertyType upt1 && this.hierarchy.ContainsKey(upt1.Resolve(this.ns))) ||
                                (mp.Keys is UdtPropertyType upt2 && this.hierarchy.ContainsKey(upt2.Resolve(this.ns)));
                            Contract.Invariant(!hasInheritance, "Inheritance is not supported in maps");
                            await emit.Statement(
                                $@"r = default(TypedMapHybridRowSerializer<
                                            {mp.Keys.UnderlyingType(this.ns)}, {mp.Keys.UnderlyingTypeSerializer(this.ns)},
                                            {mp.Values.UnderlyingType(this.ns)}, {mp.Values.UnderlyingTypeSerializer(this.ns)}
                                            >).Read(ref row, ref scope, false, out {p.UnderlyingType(this.ns)} fieldValue)");
                            break;
                        }
                        case TuplePropertyType tpt:
                        {
                            string args = string.Join(
                                ", ",
                                tpt.Items.Select(x => $"{x.UnderlyingType(this.ns)}, {x.UnderlyingTypeSerializer(this.ns)}"));
                            await emit.Statement(
                                $@"r = default(TypedTupleHybridRowSerializer<{args}>)
                                           .Read(ref row, ref scope, false, out {p.UnderlyingType(this.ns)} fieldValue)");
                            break;
                        }
                        case UdtPropertyType upt:
                            await emit.Statement(
                                $@"r = default({upt.UnderlyingTypeSerializer(this.ns)})
                                           .Read(ref row, ref scope, false, out {p.UnderlyingType(this.ns)} fieldValue)");
                            break;
                        default:
                            await emit.Statement(
                                $@"r = {p.Instance(this.ns)}.ReadSparse(ref row, ref scope, out {p.UnderlyingType(this.ns)} fieldValue)");
                            break;
                    }
                    await emit.Block(
                        $@"
                            if (r != Result.Success)
                            {{
                                return r;
                            }}

                            value.{cname} = {p.RCast(this.ns)}fieldValue;
                            continue;
                        ");
                }
                if (s.BaseName != null)
                {
                    if (i++ != 0)
                    {
                        await emit.Whitespace();
                    }
                    await using Emit.Scope unused2 = await emit.Control("if (scope.Token == __BaseToken.Id)");
                    await emit.Block(
                        $@"
                            {s.BaseName} baseValue = value;
                            r = {s.BaseName}HybridRowSerializer.ReadBase(ref row, ref scope, ref baseValue);
                            if (r != Result.Success)
                            {{
                                return r;
                            }}

                            Contract.Assert(baseValue == value);
                            continue;
                        ");
                }
            }

            if (i != 0)
            {
                await emit.Whitespace();
            }
            await emit.Statement("return Result.Success");
        }

        private async Task GenerateComparer(Emit emit, string name, Schema s)
        {
            this.hierarchy.TryGetValue(s, out Dictionary<Schema, bool> descendants);
            await emit.Whitespace();
            string typename = $"{name}Comparer";
            await using Emit.Scope s1 = await emit.Class(Keywords.Public | Keywords.Sealed, typename, $"EqualityComparer<{name}>");
            await emit.Variable(Keywords.Public | Keywords.Static | Keywords.New | Keywords.ReadOnly, typename, "Default", $"new {typename}()");

            await using (Emit.Scope unused = await emit.Method(
                Keywords.Public | Keywords.Override,
                "bool",
                "Equals",
                $"{name} x, {name} y"))
            {
                await emit.Block(
                    @"
                    HybridRowSerializer.EqualityReferenceResult refCheck = HybridRowSerializer.EqualityReferenceCheck(x, y);
                    if (refCheck != HybridRowSerializer.EqualityReferenceResult.Unknown)
                    {
                        return refCheck == HybridRowSerializer.EqualityReferenceResult.Equal;
                    }
                    ");

                if (descendants != null)
                {
                    await using (Emit.Scope unused2 = await emit.Control("switch (x)"))
                    {
                        foreach (Schema child in from d in descendants where d.Value select d.Key)
                        {
                            await emit.Block(
                                $@"
                                case {child.Name} p:
                                    return default({child.Name}HybridRowSerializer)
                                        .Comparer.Equals(p, ({child.Name})y);
                            ");
                        }
                        await emit.Block(
                            @"
                            default:
                                break;
                            ");
                    }

                    if (s.Options?.Abstract ?? false)
                    {
                        await emit.Block(
                            @"
                                Contract.Fail(""Type is abstract."");
                                return false;
                            ");
                    }
                    else
                    {
                        await emit.Block(@"return EqualsBase(x, y);");
                    }
                }
                else
                {
                    await this.GenerateComparerEquals(emit, s);
                }
            }

            if (descendants != null)
            {
                await emit.Whitespace();
                await emit.Block("[MethodImpl(MethodImplOptions.AggressiveInlining)]");
                await using Emit.Scope unused = await emit.Method(
                    Keywords.Internal | Keywords.Static,
                    "bool",
                    "EqualsBase",
                    $"{name} x, {name} y");
                await this.GenerateComparerEquals(emit, s);
            }

            // public override int GetHashCode(Address obj)
            await using (Emit.Scope unused = await emit.Method(
                Keywords.Public | Keywords.Override,
                "int",
                "GetHashCode",
                $"{name} obj"))
            {
                if (descendants != null)
                {
                    await using (Emit.Scope unused2 = await emit.Control("switch (obj)"))
                    {
                        foreach (Schema child in from d in descendants where d.Value select d.Key)
                        {
                            await emit.Block(
                                $@"
                                case {child.Name} p:
                                    return default({child.Name}HybridRowSerializer)
                                        .Comparer.GetHashCode(p);
                            ");
                        }
                        await emit.Block(
                            @"
                            default:
                                break;
                            ");
                    }

                    if (s.Options?.Abstract ?? false)
                    {
                        await emit.Block(
                            @"
                                Contract.Fail(""Type is abstract."");
                                return 0;
                            ");
                    }
                    else
                    {
                        await emit.Block(@"return GetHashCodeBase(x, y);");
                    }
                }
                else
                {
                    await this.GenerateComparerGetHashCode(emit, s);
                }
            }

            if (descendants != null)
            {
                await emit.Whitespace();
                await emit.Block("[MethodImpl(MethodImplOptions.AggressiveInlining)]");
                await using Emit.Scope unused = await emit.Method(
                    Keywords.Internal | Keywords.Static,
                    "int",
                    "GetHashCodeBase",
                    $"{name} obj");
                await this.GenerateComparerGetHashCode(emit, s);
            }
        }

        private async Task GenerateComparerEquals(Emit emit, Schema s)
        {
            await emit.Whitespace();
            if (s.Properties.Count == 0)
            {
                if (s.BaseName != null)
                {
                    await emit.Block(
                        $@"
                                return {s.BaseName}HybridRowSerializer
                                    .{s.BaseName}Comparer.EqualsBase(x, y);
                            ");
                }
                else
                {
                    await emit.Statement("return true");
                }
            }
            else if ((s.Properties.Count == 1) && (s.BaseName == null))
            {
                Property p = s.Properties[0];
                await emit.Statement(
                    $"return default({p.UnderlyingTypeSerializer(this.ns)}).Comparer.Equals({p.Cast(this.ns)}x.{p.AsPascal()}, {p.Cast(this.ns)}y.{p.AsPascal()})");
            }
            else
            {
                await using Emit.Scope s2 = await emit.Block("return\n", ");");
                int i = 0;
                if (s.BaseName != null)
                {
                    i++;
                    await emit.Block(
                        $@"{s.BaseName}HybridRowSerializer.{s.BaseName}Comparer.EqualsBase(x, y");
                }
                foreach (Property p in s.Properties)
                {
                    if (i++ != 0)
                    {
                        await emit.Block(") && \n", s2);
                    }
                    await emit.Block(
                        $"default({p.UnderlyingTypeSerializer(this.ns)}).Comparer.Equals({p.Cast(this.ns)}x.{p.AsPascal()}, {p.Cast(this.ns)}y.{p.AsPascal()}");
                }
            }
        }

        private async Task GenerateComparerGetHashCode(Emit emit, Schema s)
        {
            if (s.Properties.Count == 0)
            {
                if (s.BaseName != null)
                {
                    await emit.Block(
                        $@"
                                return {s.BaseName}HybridRowSerializer
                                    .{s.BaseName}Comparer.EqualsBase(x, y);
                            ");
                }
                else
                {
                    await emit.Statement("return 0");
                }
            }
            else if ((s.Properties.Count == 1) && (s.BaseName == null))
            {
                Property p = s.Properties[0];
                await emit.Statement(
                    $"return default({p.UnderlyingTypeSerializer(this.ns)}).Comparer.GetHashCode({p.Cast(this.ns)}obj.{p.AsPascal()})");
            }
            else if (s.Properties.Count < 8)
            {
                await using Emit.Scope s2 = await emit.Block("return HashCode.Combine(\n", "));");
                int i = 0;
                if (s.BaseName != null)
                {
                    i++;
                    await emit.Block(
                        $@"{s.BaseName}HybridRowSerializer.{s.BaseName}Comparer.GetHashCodeBase(obj");
                }
                foreach (Property p in s.Properties)
                {
                    if (i++ != 0)
                    {
                        await emit.Block("),\n", s2);
                    }
                    await emit.Block(
                        $"default({p.UnderlyingTypeSerializer(this.ns)}).Comparer.GetHashCode({p.Cast(this.ns)}obj.{p.AsPascal()}");
                }
            }
            else
            {
                await emit.Statement("HashCode hash = default");
                foreach (Property p in s.Properties)
                {
                    if (s.BaseName != null)
                    {
                        await emit.Statement(
                            $"hash.Add({s.BaseName}HybridRowSerializer.{s.BaseName}Comparer.GetHashCodeBase(obj))");
                    }
                    await emit.Statement(
                        $"hash.Add({p.Cast(this.ns)}obj.{p.AsPascal()}, default({p.UnderlyingTypeSerializer(this.ns)}).Comparer)");
                }
                await emit.Statement("return hash.ToHashCode()");
            }
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
            Sealed = 0x40,
            New = 0x80,
            Override = 0x100,
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
                this.step = 4;
                this.indent = 0;
            }

            public async ValueTask<Scope> Struct(Keywords keywords, string identifier, string baseIdentifier = null)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                if (baseIdentifier is null)
                {
                    await this.writer.WriteLineAsync($"struct {identifier}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"struct {identifier} : {baseIdentifier}");
                }
                return await this.Braces();
            }

            public async ValueTask<Scope> Class(Keywords keywords, string identifier, string baseIdentifier = null)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                if (baseIdentifier is null)
                {
                    await this.writer.WriteLineAsync($"class {identifier}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"class {identifier} : {baseIdentifier}");
                }
                return await this.Braces();
            }

            public async ValueTask FileHeader()
            {
                await this.WriteLine("// ------------------------------------------------------------");
                await this.WriteLine("//  Copyright (c) Microsoft Corporation.  All rights reserved.");
                await this.WriteLine("// ------------------------------------------------------------");
                await this.Whitespace();
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
                return this.WriteLine("using {0};", identifier);
            }

            public async ValueTask Pragma(string warning, string comment = null)
            {
                await this.Indent();

                if (comment == null)
                {
                    await this.writer.WriteLineAsync($"#pragma warning disable {warning}");
                }
                else
                {
                    await this.writer.WriteLineAsync($"#pragma warning disable {warning} // {comment}");
                }
            }

            public ValueTask Variable(string typename, string identifier, string expr = null)
            {
                return this.Variable(CSharpNamespaceGenerator.Keywords.None, typename, identifier, expr);
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
                    await this.writer.WriteAsync($"{typename} {identifier} = ");
                    await this.Expr(expr);
                    await this.writer.WriteLineAsync(";");
                }
            }

            public async ValueTask PropertyExpr(Keywords keywords, string typename, string identifier, string expr)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                await this.writer.WriteAsync($"{typename} {identifier} => ");
                await this.Expr(expr);
                await this.writer.WriteLineAsync(";");
            }

            public async ValueTask AutoProperty(Keywords keywords, string typename, string identifier)
            {
                await this.Indent();
                await this.Modifiers(keywords);
                await this.writer.WriteLineAsync($"{typename} {identifier} {{ get; set; }}");
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

            public async ValueTask<Scope> Block(string startBlock, string endBlock, Scope outer = null)
            {
                bool isNested = outer != null;
                int blockStep = this.step;
                if (isNested)
                {
                    // If this is a nested block that beings with a newline, then don't
                    // align it with the parent (the newline indicates an explicit desire
                    // for the nested block to have its own indent).  Otherwise a nested
                    // block begins on the parent's first line and should adopt its indent.
                    string firstLine = startBlock.Replace("\r\n", "\n").Split("\n").FirstOrDefault();
                    if (firstLine != null && !string.IsNullOrWhiteSpace(firstLine))
                    {
                        blockStep = outer.Indent;
                        this.indent -= blockStep;
                        outer.Indent = 0;
                    }
                }
                await this.Block(startBlock, isNested);

                this.indent += blockStep;
                if (isNested)
                {
                    outer.HasNest = true;
                }
                return new Scope(this, blockStep, endBlock, isNested);
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

                if (parameters == null)
                {
                    await this.writer.WriteLineAsync($"{identifier}()");
                }
                else
                {
                    await this.writer.WriteLineAsync($"{identifier}({parameters})");
                }
                return await this.Braces();
            }

            public ValueTask DisposeAsync()
            {
                return this.writer.DisposeAsync();
            }

            public async ValueTask<Scope> Braces()
            {
                await this.WriteLine("{");
                this.indent += this.step;
                return new Scope(this, this.step, "}");
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
                if ((keywords & Keywords.Public) != 0)
                {
                    await this.writer.WriteAsync("public ");
                }
                if ((keywords & Keywords.Private) != 0)
                {
                    await this.writer.WriteAsync("private ");
                }
                if ((keywords & Keywords.Internal) != 0)
                {
                    await this.writer.WriteAsync("internal ");
                }
                if ((keywords & Keywords.Static) != 0)
                {
                    await this.writer.WriteAsync("static ");
                }
                if ((keywords & Keywords.Override) != 0)
                {
                    await this.writer.WriteAsync("override ");
                }
                if ((keywords & Keywords.New) != 0)
                {
                    await this.writer.WriteAsync("new ");
                }
                if ((keywords & Keywords.ReadOnly) != 0)
                {
                    await this.writer.WriteAsync("readonly ");
                }
                if ((keywords & Keywords.Const) != 0)
                {
                    await this.writer.WriteAsync("const ");
                }
                if ((keywords & Keywords.Sealed) != 0)
                {
                    await this.writer.WriteAsync("sealed ");
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
                private readonly bool isNested;

                public Scope(Emit parent, int indent, string closer, bool isNested = false)
                {
                    this.parent = parent;
                    this.Indent = indent;
                    this.closer = closer;
                    this.isNested = isNested;
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
                    if (this.isNested)
                    {
                        await this.parent.writer.WriteAsync(this.closer);
                    }
                    else
                    {
                        await this.parent.writer.WriteLineAsync(this.closer);
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
                    $"LayoutType.{Enum.GetName(typeof(TypeKind), pp.ResolveEnum(ns))}",
                PrimitivePropertyType pp => $"LayoutType.{Enum.GetName(typeof(TypeKind), pp.Type)}",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string Cast(this Property p, Namespace ns)
        {
            // Don't use nullable types for top-level nullable fields (instead use default-as-null semantics).
            if (p.PropertyType.Nullable && p.Path == null)
            {
                return p.PropertyType switch
                {
                    PrimitivePropertyType { Type: TypeKind.Utf8 } => "",
                    PrimitivePropertyType { Type: TypeKind.Binary } => "",
                    PrimitivePropertyType pp => $"({pp.UnderlyingType(ns, true)})",
                    _ => "",
                };
            }

            return p.PropertyType switch
            {
                PrimitivePropertyType pp when !string.IsNullOrEmpty(pp.ApiType) => $"({pp.UnderlyingType(ns, true)})",
                PrimitivePropertyType { Type: TypeKind.Enum } pp => $"({pp.UnderlyingType(ns, true)})",
                _ => "",
            };
        }

        public static string RCast(this Property p, Namespace ns)
        {
            return p.PropertyType switch
            {
                PrimitivePropertyType pp when !string.IsNullOrEmpty(pp.ApiType) => $"({pp.ApiType})",
                PrimitivePropertyType { Type: TypeKind.Enum } pp => $"({pp.Enum})",
                _ => "",
            };
        }

        public static TypeKind ResolveEnum(this PrimitivePropertyType ep, Namespace ns)
        {
            EnumSchema enumSchema = ns.Enums.Find(es => es.Name == ep.Enum);
            Contract.Invariant(enumSchema != null);
            return enumSchema.Type;
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
                ArrayPropertyType ap => (ap.Items is null) ? "List<object>" : $"List<{ap.Items.UnderlyingType(ns)}>",
                MapPropertyType mp when !((mp.Keys is null) || (mp.Values is null)) =>
                    $"Dictionary<{mp.Keys.UnderlyingType(ns)}, {mp.Values.UnderlyingType(ns)}>",
                TuplePropertyType ap when !(ap.Items is null) =>
                    $"({string.Join(", ", ap.Items.Select(x => x.UnderlyingType(ns)))})",
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
                TypeKind.Binary => "byte[]",
                TypeKind.Boolean => nullable ? "bool?" : "bool",
                TypeKind.DateTime => nullable ? "DateTime?" : "DateTime",
                TypeKind.Decimal => nullable ? "Decimal?" : "Decimal",
                TypeKind.Float128 => nullable ? "Float128?" : "Float128",
                TypeKind.Float32 => nullable ? "float?" : "float",
                TypeKind.Float64 => nullable ? "double?" : "double",
                TypeKind.Guid => nullable ? "Guid?" : "Guid",
                TypeKind.Int16 => nullable ? "short?" : "short",
                TypeKind.Int32 => nullable ? "int?" : "int",
                TypeKind.Int64 => nullable ? "long?" : "long",
                TypeKind.Int8 => nullable ? "sbyte?" : "sbyte",
                TypeKind.MongoDbObjectId => nullable ? "MongoDbObjectId?" : "MongoDbObjectId",
                TypeKind.UInt16 => nullable ? "ushort?" : "ushort",
                TypeKind.UInt32 => nullable ? "uint?" : "uint",
                TypeKind.UInt64 => nullable ? "ulong?" : "ulong",
                TypeKind.UInt8 => nullable ? "byte?" : "byte",
                TypeKind.UnixDateTime => nullable ? "UnixDateTime?" : "UnixDateTime",
                TypeKind.Utf8 => "string",
                TypeKind.VarInt => nullable ? "long?" : "long",
                TypeKind.VarUInt => nullable ? "ulong?" : "ulong",
                _ => NotImplemented(),
            };

            static string NotImplemented()
            {
                Contract.Fail("This should never happen");
                return "";
            }
        }

        public static string UnderlyingTypeSerializer(this Property p, Namespace ns, bool stripNullable = false)
        {
            // Don't use nullable types for top-level nullable fields (instead use default-as-null semantics).
            return p.PropertyType.UnderlyingTypeSerializer(ns, stripNullable || p.Path != null);
        }

        public static string UnderlyingTypeSerializer(this PropertyType p, Namespace ns, bool stripNullable = false)
        {
            bool nullable = !stripNullable && p.Nullable;
            return p switch
            {
                PrimitivePropertyType pp when pp.Type == TypeKind.Enum =>
                    ns.Enums.Find(es => es.Name == (p as PrimitivePropertyType).Enum).Type.UnderlyingTypeSerializer(nullable),
                PrimitivePropertyType pp => pp.Type.UnderlyingTypeSerializer(nullable),
                ArrayPropertyType arr when !(arr.Items is null) =>
                    $"TypedArrayHybridRowSerializer<{arr.Items.UnderlyingType(ns)}, {arr.Items.UnderlyingTypeSerializer(ns)}>",
                MapPropertyType mp when !((mp.Keys is null) || (mp.Values is null)) =>
                    $"TypedMapHybridRowSerializer<{mp.Keys.UnderlyingType(ns)}, {mp.Keys.UnderlyingTypeSerializer(ns)}, " +
                    $"{mp.Values.UnderlyingType(ns)}, {mp.Values.UnderlyingTypeSerializer(ns)}>",
                UdtPropertyType up => $"{up.Name.IdentifierOnly()}HybridRowSerializer",
                TuplePropertyType tpt =>
                    "TypedTupleHybridRowSerializer<" +
                    string.Join(", ", tpt.Items.Select(x => $"{x.UnderlyingType(ns)}, {x.UnderlyingTypeSerializer(ns)}")) +
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
                return $"NullableHybridRowSerializer<{type.UnderlyingType(true)}, " +
                       $"{type.UnderlyingType(false)}, " +
                       $"{type.UnderlyingTypeSerializer(false)}>";
            }

            return $"{type}HybridRowSerializer";
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
