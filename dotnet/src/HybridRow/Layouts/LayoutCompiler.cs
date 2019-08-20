// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    /// <summary>Converts a logical schema into a physical layout.</summary>
    internal sealed class LayoutCompiler
    {
        /// <summary>Compiles a logical schema into a physical layout that can be used to read and write rows.</summary>
        /// <param name="ns">The namespace within which <paramref name="schema" /> is defined.</param>
        /// <param name="schema">The logical schema to produce a layout for.</param>
        /// <returns>The layout for the schema.</returns>
        public static Layout Compile(Namespace ns, Schema schema)
        {
            Contract.Requires(ns != null);
            Contract.Requires(schema != null);
            Contract.Requires(schema.Type == TypeKind.Schema);
            Contract.Requires(!string.IsNullOrWhiteSpace(schema.Name));
            Contract.Requires(ns.Schemas.Contains(schema));

            LayoutBuilder builder = new LayoutBuilder(schema.Name, schema.SchemaId);
            LayoutCompiler.AddProperties(builder, ns, LayoutCode.Schema, schema.Properties);

            return builder.Build();
        }

        private static void AddProperties(LayoutBuilder builder, Namespace ns, LayoutCode scope, List<Property> properties)
        {
            foreach (Property p in properties)
            {
                TypeArgumentList typeArgs;
                LayoutType type = LayoutCompiler.LogicalToPhysicalType(ns, p.PropertyType, out typeArgs);
                switch (LayoutCodeTraits.ClearImmutableBit(type.LayoutCode))
                {
                    case LayoutCode.ObjectScope:
                    {
                        if (!p.PropertyType.Nullable)
                        {
                            throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                        }

                        ObjectPropertyType op = (ObjectPropertyType)p.PropertyType;
                        builder.AddObjectScope(p.Path, type);
                        LayoutCompiler.AddProperties(builder, ns, type.LayoutCode, op.Properties);
                        builder.EndObjectScope();
                        break;
                    }

                    case LayoutCode.ArrayScope:
                    case LayoutCode.TypedArrayScope:
                    case LayoutCode.SetScope:
                    case LayoutCode.TypedSetScope:
                    case LayoutCode.MapScope:
                    case LayoutCode.TypedMapScope:
                    case LayoutCode.TupleScope:
                    case LayoutCode.TypedTupleScope:
                    case LayoutCode.TaggedScope:
                    case LayoutCode.Tagged2Scope:
                    case LayoutCode.Schema:
                    {
                        if (!p.PropertyType.Nullable)
                        {
                            throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                        }

                        builder.AddTypedScope(p.Path, type, typeArgs);
                        break;
                    }

                    case LayoutCode.NullableScope:
                    {
                        throw new LayoutCompilationException("Nullables cannot be explicitly declared as columns.");
                    }

                    default:
                    {
                        PrimitivePropertyType pp = p.PropertyType as PrimitivePropertyType;
                        if (pp != null)
                        {
                            switch (pp.Storage)
                            {
                                case StorageKind.Fixed:
                                    if (LayoutCodeTraits.ClearImmutableBit(scope) != LayoutCode.Schema)
                                    {
                                        throw new LayoutCompilationException("Cannot have fixed storage within a sparse scope.");
                                    }

                                    if (type.IsNull && !pp.Nullable)
                                    {
                                        throw new LayoutCompilationException("Non-nullable null columns are not supported.");
                                    }

                                    builder.AddFixedColumn(p.Path, type, pp.Nullable, pp.Length);
                                    break;
                                case StorageKind.Variable:
                                    if (LayoutCodeTraits.ClearImmutableBit(scope) != LayoutCode.Schema)
                                    {
                                        throw new LayoutCompilationException("Cannot have variable storage within a sparse scope.");
                                    }

                                    if (!pp.Nullable)
                                    {
                                        throw new LayoutCompilationException("Non-nullable variable columns are not supported.");
                                    }

                                    builder.AddVariableColumn(p.Path, type, pp.Length);
                                    break;
                                case StorageKind.Sparse:
                                    if (!pp.Nullable)
                                    {
                                        throw new LayoutCompilationException("Non-nullable sparse columns are not supported.");
                                    }

                                    builder.AddSparseColumn(p.Path, type);
                                    break;
                                default:
                                    throw new LayoutCompilationException($"Unknown storage specification: {pp.Storage}");
                            }
                        }
                        else
                        {
                            throw new LayoutCompilationException($"Unknown property type: {type.Name}");
                        }

                        break;
                    }
                }
            }
        }

        private static LayoutType LogicalToPhysicalType(Namespace ns, PropertyType logicalType, out TypeArgumentList typeArgs)
        {
            typeArgs = TypeArgumentList.Empty;
            bool immutable = (logicalType as ScopePropertyType)?.Immutable ?? false;

            switch (logicalType.Type)
            {
                case TypeKind.Null:
                    return LayoutType.Null;
                case TypeKind.Boolean:
                    return LayoutType.Boolean;
                case TypeKind.Int8:
                    return LayoutType.Int8;
                case TypeKind.Int16:
                    return LayoutType.Int16;
                case TypeKind.Int32:
                    return LayoutType.Int32;
                case TypeKind.Int64:
                    return LayoutType.Int64;
                case TypeKind.UInt8:
                    return LayoutType.UInt8;
                case TypeKind.UInt16:
                    return LayoutType.UInt16;
                case TypeKind.UInt32:
                    return LayoutType.UInt32;
                case TypeKind.UInt64:
                    return LayoutType.UInt64;
                case TypeKind.Float32:
                    return LayoutType.Float32;
                case TypeKind.Float64:
                    return LayoutType.Float64;
                case TypeKind.Float128:
                    return LayoutType.Float128;
                case TypeKind.Decimal:
                    return LayoutType.Decimal;
                case TypeKind.DateTime:
                    return LayoutType.DateTime;
                case TypeKind.UnixDateTime:
                    return LayoutType.UnixDateTime;
                case TypeKind.Guid:
                    return LayoutType.Guid;
                case TypeKind.MongoDbObjectId:
                    return LayoutType.MongoDbObjectId;
                case TypeKind.Utf8:
                    return LayoutType.Utf8;
                case TypeKind.Binary:
                    return LayoutType.Binary;
                case TypeKind.VarInt:
                    return LayoutType.VarInt;
                case TypeKind.VarUInt:
                    return LayoutType.VarUInt;

                case TypeKind.Object:
                    return immutable ? LayoutType.ImmutableObject : LayoutType.Object;
                case TypeKind.Array:
                    ArrayPropertyType ap = (ArrayPropertyType)logicalType;
                    if ((ap.Items != null) && (ap.Items.Type != TypeKind.Any))
                    {
                        TypeArgumentList itemTypeArgs;
                        LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, ap.Items, out itemTypeArgs);
                        if (ap.Items.Nullable)
                        {
                            itemTypeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                            itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        typeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                        return immutable ? LayoutType.ImmutableTypedArray : LayoutType.TypedArray;
                    }

                    return immutable ? LayoutType.ImmutableArray : LayoutType.Array;
                case TypeKind.Set:
                    SetPropertyType sp = (SetPropertyType)logicalType;
                    if ((sp.Items != null) && (sp.Items.Type != TypeKind.Any))
                    {
                        TypeArgumentList itemTypeArgs;
                        LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, sp.Items, out itemTypeArgs);
                        if (sp.Items.Nullable)
                        {
                            itemTypeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                            itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        typeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                        return immutable ? LayoutType.ImmutableTypedSet : LayoutType.TypedSet;
                    }

                    // TODO(283638): implement sparse set.
                    throw new LayoutCompilationException($"Unknown property type: {logicalType.Type}");

                case TypeKind.Map:
                    MapPropertyType mp = (MapPropertyType)logicalType;
                    if ((mp.Keys != null) && (mp.Keys.Type != TypeKind.Any) && (mp.Values != null) && (mp.Values.Type != TypeKind.Any))
                    {
                        TypeArgumentList keyTypeArgs;
                        LayoutType keyType = LayoutCompiler.LogicalToPhysicalType(ns, mp.Keys, out keyTypeArgs);
                        if (mp.Keys.Nullable)
                        {
                            keyTypeArgs = new TypeArgumentList(new[] { new TypeArgument(keyType, keyTypeArgs) });
                            keyType = keyType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        TypeArgumentList valueTypeArgs;
                        LayoutType valueType = LayoutCompiler.LogicalToPhysicalType(ns, mp.Values, out valueTypeArgs);
                        if (mp.Values.Nullable)
                        {
                            valueTypeArgs = new TypeArgumentList(new[] { new TypeArgument(valueType, valueTypeArgs) });
                            valueType = valueType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        typeArgs = new TypeArgumentList(new[] { new TypeArgument(keyType, keyTypeArgs), new TypeArgument(valueType, valueTypeArgs) });
                        return immutable ? LayoutType.ImmutableTypedMap : LayoutType.TypedMap;
                    }

                    // TODO(283638): implement sparse map.
                    throw new LayoutCompilationException($"Unknown property type: {logicalType.Type}");

                case TypeKind.Tuple:
                    TuplePropertyType tp = (TuplePropertyType)logicalType;
                    TypeArgument[] args = new TypeArgument[tp.Items.Count];
                    for (int i = 0; i < tp.Items.Count; i++)
                    {
                        TypeArgumentList itemTypeArgs;
                        LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, tp.Items[i], out itemTypeArgs);
                        if (tp.Items[i].Nullable)
                        {
                            itemTypeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                            itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        args[i] = new TypeArgument(itemType, itemTypeArgs);
                    }

                    typeArgs = new TypeArgumentList(args);
                    return immutable ? LayoutType.ImmutableTypedTuple : LayoutType.TypedTuple;

                case TypeKind.Tagged:
                    TaggedPropertyType tg = (TaggedPropertyType)logicalType;
                    if ((tg.Items.Count < TaggedPropertyType.MinTaggedArguments) || (tg.Items.Count > TaggedPropertyType.MaxTaggedArguments))
                    {
                        throw new LayoutCompilationException(
                            $"Invalid number of arguments in Tagged: {TaggedPropertyType.MinTaggedArguments} <= {tg.Items.Count} <= {TaggedPropertyType.MaxTaggedArguments}");
                    }

                    TypeArgument[] tgArgs = new TypeArgument[tg.Items.Count + 1];
                    tgArgs[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.Empty);
                    for (int i = 0; i < tg.Items.Count; i++)
                    {
                        TypeArgumentList itemTypeArgs;
                        LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, tg.Items[i], out itemTypeArgs);
                        if (tg.Items[i].Nullable)
                        {
                            itemTypeArgs = new TypeArgumentList(new[] { new TypeArgument(itemType, itemTypeArgs) });
                            itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                        }

                        tgArgs[i + 1] = new TypeArgument(itemType, itemTypeArgs);
                    }

                    typeArgs = new TypeArgumentList(tgArgs);
                    switch (tg.Items.Count)
                    {
                        case 1:
                            return immutable ? LayoutType.ImmutableTagged : LayoutType.Tagged;
                        case 2:
                            return immutable ? LayoutType.ImmutableTagged2 : LayoutType.Tagged2;
                        default:
                            throw new LayoutCompilationException("Unexpected tagged arity");
                    }

                case TypeKind.Schema:
                    UdtPropertyType up = (UdtPropertyType)logicalType;
                    Schema udtSchema;
                    if (up.SchemaId == SchemaId.Invalid)
                    {
                        udtSchema = ns.Schemas.Find(s => s.Name == up.Name);
                    }
                    else
                    {
                        udtSchema = ns.Schemas.Find(s => s.SchemaId == up.SchemaId);
                        if (udtSchema.Name != up.Name)
                        {
                            throw new LayoutCompilationException($"Ambiguous schema reference: '{up.Name}:{up.SchemaId}'");
                        }
                    }

                    if (udtSchema == null)
                    {
                        throw new LayoutCompilationException($"Cannot resolve schema reference '{up.Name}:{up.SchemaId}'");
                    }

                    typeArgs = new TypeArgumentList(udtSchema.SchemaId);
                    return immutable ? LayoutType.ImmutableUDT : LayoutType.UDT;

                default:
                    throw new LayoutCompilationException($"Unknown property type: {logicalType.Type}");
            }
        }
    }
}
