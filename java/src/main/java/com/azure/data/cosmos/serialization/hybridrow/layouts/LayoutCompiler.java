// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.schemas.ArrayPropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.MapPropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.data.cosmos.serialization.hybridrow.schemas.ObjectPropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.PrimitivePropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Property;
import com.azure.data.cosmos.serialization.hybridrow.schemas.PropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.Schema;
import com.azure.data.cosmos.serialization.hybridrow.schemas.ScopePropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.SetPropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.TaggedPropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.TuplePropertyType;
import com.azure.data.cosmos.serialization.hybridrow.schemas.TypeKind;
import com.azure.data.cosmos.serialization.hybridrow.schemas.UdtPropertyType;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Converts a logical schema into a physical layout.
 */
public final class LayoutCompiler {
    /**
     * Compiles a logical schema into a physical layout that can be used to read and write rows.
     *
     * @param ns     The namespace within which <paramref name="schema" /> is defined.
     * @param schema The logical schema to produce a layout for.
     * @return The layout for the schema.
     */
    public static Layout Compile(Namespace ns, Schema schema) {
        checkArgument(ns != null);
        checkArgument(schema != null);
        checkArgument(schema.type() == TypeKind.Schema);
        checkArgument(!tangible.StringHelper.isNullOrWhiteSpace(schema.name()));
        checkArgument(ns.schemas().contains(schema));

        LayoutBuilder builder = new LayoutBuilder(schema.name(), schema.schemaId().clone());
        LayoutCompiler.AddProperties(builder, ns, LayoutCode.SCHEMA, schema.properties());

        return builder.build();
    }

    private static void AddProperties(LayoutBuilder builder, Namespace ns, LayoutCode scope,
                                      ArrayList<Property> properties) {
        for (Property p : properties) {
            TypeArgumentList typeArgs = new TypeArgumentList();
            Out<TypeArgumentList> tempOut_typeArgs =
                new Out<TypeArgumentList>();
            LayoutType type = LayoutCompiler.LogicalToPhysicalType(ns, p.propertyType(), tempOut_typeArgs);
            typeArgs = tempOut_typeArgs.get();
            switch (LayoutCodeTraits.ClearImmutableBit(type.LayoutCode)) {
                case OBJECT_SCOPE: {
                    if (!p.propertyType().nullable()) {
                        throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                    }

                    ObjectPropertyType op = (ObjectPropertyType)p.propertyType();
                    builder.addObjectScope(p.path(), type);
                    LayoutCompiler.AddProperties(builder, ns, type.LayoutCode, op.properties());
                    builder.EndObjectScope();
                    break;
                }

                case ARRAY_SCOPE:
                case TYPED_ARRAY_SCOPE:
                case SET_SCOPE:
                case TYPED_SET_SCOPE:
                case MAP_SCOPE:
                case TYPED_MAP_SCOPE:
                case TUPLE_SCOPE:
                case TYPED_TUPLE_SCOPE:
                case TAGGED_SCOPE:
                case TAGGED2_SCOPE:
                case SCHEMA: {
                    if (!p.propertyType().nullable()) {
                        throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                    }

                    builder.addTypedScope(p.path(), type, typeArgs.clone());
                    break;
                }

                case NULLABLE_SCOPE: {
                    throw new LayoutCompilationException("Nullables cannot be explicitly declared as columns.");
                }

                default: {
                    PropertyType tempVar = p.propertyType();
                    PrimitivePropertyType pp = tempVar instanceof PrimitivePropertyType ?
                        (PrimitivePropertyType)tempVar : null;
                    if (pp != null) {
                        switch (pp.storage()) {
                            case FIXED:
                                if (LayoutCodeTraits.ClearImmutableBit(scope) != LayoutCode.SCHEMA) {
                                    throw new LayoutCompilationException("Cannot have fixed storage within a sparse " +
                                        "scope.");
                                }

                                if (type.getIsNull() && !pp.nullable()) {
                                    throw new LayoutCompilationException("Non-nullable null columns are not supported" +
                                        ".");
                                }

                                builder.addFixedColumn(p.path(), type, pp.nullable(), pp.length());
                                break;
                            case VARIABLE:
                                if (LayoutCodeTraits.ClearImmutableBit(scope) != LayoutCode.SCHEMA) {
                                    throw new LayoutCompilationException("Cannot have variable storage within a " +
                                        "sparse scope.");
                                }

                                if (!pp.nullable()) {
                                    throw new LayoutCompilationException("Non-nullable variable columns are not " +
                                        "supported.");
                                }

                                builder.addVariableColumn(p.path(), type, pp.length());
                                break;
                            case SPARSE:
                                if (!pp.nullable()) {
                                    throw new LayoutCompilationException("Non-nullable sparse columns are not " +
                                        "supported.");
                                }

                                builder.addSparseColumn(p.path(), type);
                                break;
                            default:
                                throw new LayoutCompilationException(String.format("Unknown storage specification: " +
                                    "%1$s", pp.storage()));
                        }
                    } else {
                        throw new LayoutCompilationException(String.format("Unknown property type: %1$s",
                            type.getName()));
                    }

                    break;
                }
            }
        }
    }

    private static LayoutType LogicalToPhysicalType(Namespace ns, PropertyType logicalType,
                                                    Out<TypeArgumentList> typeArgs) {
        typeArgs.setAndGet(TypeArgumentList.EMPTY);
        boolean tempVar =
            (logicalType instanceof ScopePropertyType ? (ScopePropertyType)logicalType : null).immutable();
        boolean immutable =
            (logicalType instanceof ScopePropertyType ? (ScopePropertyType)logicalType : null) == null ? null :
                tempVar != null && tempVar;

        switch (logicalType.type()) {
            case Null:
                return LayoutType.Null;
            case Boolean:
                return LayoutType.Boolean;
            case Int8:
                return LayoutType.Int8;
            case Int16:
                return LayoutType.Int16;
            case Int32:
                return LayoutType.Int32;
            case Int64:
                return LayoutType.Int64;
            case UInt8:
                return LayoutType.UInt8;
            case UInt16:
                return LayoutType.UInt16;
            case UInt32:
                return LayoutType.UInt32;
            case UInt64:
                return LayoutType.UInt64;
            case Float32:
                return LayoutType.Float32;
            case Float64:
                return LayoutType.Float64;
            case Float128:
                return LayoutType.Float128;
            case Decimal:
                return LayoutType.Decimal;
            case DateTime:
                return LayoutType.DateTime;
            case UnixDateTime:
                return LayoutType.UnixDateTime;
            case Guid:
                return LayoutType.Guid;
            case MongoDbObjectId:
                return LayoutType.MongoDbObjectId;
            case Utf8:
                return LayoutType.Utf8;
            case Binary:
                return LayoutType.Binary;
            case VarInt:
                return LayoutType.VarInt;
            case VarUInt:
                return LayoutType.VarUInt;

            case Object:
                return immutable ? LayoutType.ImmutableObject : LayoutType.Object;
            case Array:
                ArrayPropertyType ap = (ArrayPropertyType)logicalType;
                if ((ap.items() != null) && (ap.items().type() != TypeKind.Any)) {
                    TypeArgumentList itemTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_itemTypeArgs = new Out<TypeArgumentList>();
                    LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, ap.items(), tempOut_itemTypeArgs);
                    itemTypeArgs = tempOut_itemTypeArgs.get();
                    if (ap.items().nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                            itemTypeArgs.clone()) });
                        itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    typeArgs.setAndGet(new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                        itemTypeArgs.clone()) }));
                    return immutable ? LayoutType.ImmutableTypedArray : LayoutType.TypedArray;
                }

                return immutable ? LayoutType.ImmutableArray : LayoutType.Array;
            case Set:
                SetPropertyType sp = (SetPropertyType)logicalType;
                if ((sp.items() != null) && (sp.items().type() != TypeKind.Any)) {
                    TypeArgumentList itemTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_itemTypeArgs2 = new Out<TypeArgumentList>();
                    LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, sp.items(),
                        tempOut_itemTypeArgs2);
                    itemTypeArgs = tempOut_itemTypeArgs2.get();
                    if (sp.items().nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                            itemTypeArgs.clone()) });
                        itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    typeArgs.setAndGet(new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                        itemTypeArgs.clone()) }));
                    return immutable ? LayoutType.ImmutableTypedSet : LayoutType.TypedSet;
                }

                // TODO(283638): implement sparse set.
                throw new LayoutCompilationException(String.format("Unknown property type: %1$s",
                    logicalType.type()));

            case Map:
                MapPropertyType mp = (MapPropertyType)logicalType;
                if ((mp.keys() != null) && (mp.keys().type() != TypeKind.Any) && (mp.values() != null) && (mp.values().type() != TypeKind.Any)) {
                    TypeArgumentList keyTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_keyTypeArgs = new Out<TypeArgumentList>();
                    LayoutType keyType = LayoutCompiler.LogicalToPhysicalType(ns, mp.keys(), tempOut_keyTypeArgs);
                    keyTypeArgs = tempOut_keyTypeArgs.get();
                    if (mp.keys().nullable()) {
                        keyTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(keyType,
                            keyTypeArgs.clone()) });
                        keyType = keyType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    TypeArgumentList valueTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_valueTypeArgs = new Out<TypeArgumentList>();
                    LayoutType valueType = LayoutCompiler.LogicalToPhysicalType(ns, mp.values(),
                        tempOut_valueTypeArgs);
                    valueTypeArgs = tempOut_valueTypeArgs.get();
                    if (mp.values().nullable()) {
                        valueTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(valueType,
                            valueTypeArgs.clone()) });
                        valueType = valueType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    typeArgs.setAndGet(new TypeArgumentList(new TypeArgument[]
                        {
                            new TypeArgument(keyType, keyTypeArgs.clone()),
                            new TypeArgument(valueType, valueTypeArgs.clone())
                        }));
                    return immutable ? LayoutType.ImmutableTypedMap : LayoutType.TypedMap;
                }

                // TODO(283638): implement sparse map.
                throw new LayoutCompilationException(String.format("Unknown property type: %1$s",
                    logicalType.type()));

            case Tuple:
                TuplePropertyType tp = (TuplePropertyType)logicalType;
                TypeArgument[] args = new TypeArgument[tp.items().size()];
                for (int i = 0; i < tp.items().size(); i++) {
                    TypeArgumentList itemTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_itemTypeArgs3 = new Out<TypeArgumentList>();
                    LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, tp.items().get(i),
                        tempOut_itemTypeArgs3);
                    itemTypeArgs = tempOut_itemTypeArgs3.get();
                    if (tp.items().get(i).nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                            itemTypeArgs.clone()) });
                        itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    args[i] = new TypeArgument(itemType, itemTypeArgs.clone());
                }

                typeArgs.setAndGet(new TypeArgumentList(args));
                return immutable ? LayoutType.ImmutableTypedTuple : LayoutType.TypedTuple;

            case Tagged:
                TaggedPropertyType tg = (TaggedPropertyType)logicalType;
                if ((tg.items().size() < TaggedPropertyType.MinTaggedArguments) || (tg.items().size() > TaggedPropertyType.MaxTaggedArguments)) {
                    throw new LayoutCompilationException(String.format("Invalid number of arguments in Tagged: %1$s " +
                        "<= %2$s <= %3$s", TaggedPropertyType.MinTaggedArguments, tg.items().size(),
                        TaggedPropertyType.MaxTaggedArguments));
                }

                TypeArgument[] tgArgs = new TypeArgument[tg.items().size() + 1];
                tgArgs[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.EMPTY);
                for (int i = 0; i < tg.items().size(); i++) {
                    TypeArgumentList itemTypeArgs = new TypeArgumentList();
                    Out<TypeArgumentList> tempOut_itemTypeArgs4 = new Out<TypeArgumentList>();
                    LayoutType itemType = LayoutCompiler.LogicalToPhysicalType(ns, tg.items().get(i),
                        tempOut_itemTypeArgs4);
                    itemTypeArgs = tempOut_itemTypeArgs4.get();
                    if (tg.items().get(i).nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument[] { new TypeArgument(itemType,
                            itemTypeArgs.clone()) });
                        itemType = itemType.Immutable ? LayoutType.ImmutableNullable : LayoutType.Nullable;
                    }

                    tgArgs[i + 1] = new TypeArgument(itemType, itemTypeArgs.clone());
                }

                typeArgs.setAndGet(new TypeArgumentList(tgArgs));
                switch (tg.items().size()) {
                    case 1:
                        return immutable ? LayoutType.ImmutableTagged : LayoutType.Tagged;
                    case 2:
                        return immutable ? LayoutType.ImmutableTagged2 : LayoutType.Tagged2;
                    default:
                        throw new LayoutCompilationException("Unexpected tagged arity");
                }

            case Schema:
                UdtPropertyType up = (UdtPropertyType)logicalType;
                Schema udtSchema;
                if (SchemaId.opEquals(up.schemaId().clone(), SchemaId.INVALID)) {
                    udtSchema = tangible.ListHelper.find(ns.schemas(), s = up.name().equals( > s.Name))
                } else {
                    udtSchema = tangible.ListHelper.find(ns.schemas(), s =
                        SchemaId.opEquals( > s.SchemaId, up.schemaId().clone()))
                    if (!udtSchema.name().equals(up.name())) {
                        throw new LayoutCompilationException(String.format("Ambiguous schema reference: '%1$s:%2$s'",
                            up.name(), up.schemaId().clone()));
                    }
                }

                if (udtSchema == null) {
                    throw new LayoutCompilationException(String.format("Cannot resolve schema reference '%1$s:%2$s'",
                        up.name(), up.schemaId().clone()));
                }

                typeArgs.setAndGet(new TypeArgumentList(udtSchema.schemaId().clone()));
                return immutable ? LayoutType.ImmutableUDT : LayoutType.UDT;

            default:
                throw new LayoutCompilationException(String.format("Unknown property type: %1$s",
                    logicalType.type()));
        }
    }
}