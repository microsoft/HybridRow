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
import com.google.common.base.Strings;
import org.checkerframework.checker.index.qual.NonNegative;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;

/**
 * Converts a logical schema into a physical layout.
 */
public final class LayoutCompiler {
    /**
     * Compiles a logical schema into a physical layout that can be used to read and write rows
     *
     * @param namespace     The namespace within which {@code schema} is defined
     * @param schema The logical schema to produce a layout for
     * @return The layout for the schema
     */
    @Nonnull
    public static Layout compile(@Nonnull final Namespace namespace, @Nonnull final Schema schema) {

        checkNotNull(namespace, "expected non-null namespace");
        checkNotNull(schema, "expected non-null schema");
        checkArgument(schema.type() == TypeKind.SCHEMA);
        checkArgument(!Strings.isNullOrEmpty(schema.name()));
        checkArgument(namespace.schemas().contains(schema));

        LayoutBuilder builder = new LayoutBuilder(schema.name(), schema.schemaId());
        LayoutCompiler.addProperties(builder, namespace, LayoutCode.SCHEMA, schema.properties());

        return builder.build();
    }

    private static void addProperties(
        @Nonnull final LayoutBuilder builder,
        @Nonnull final Namespace namespace,
        @Nonnull LayoutCode layoutCode,
        @Nonnull List<Property> properties) {

        final Out<TypeArgumentList> typeArgs = new Out<>();

        for (Property p : properties) {

            LayoutType type = LayoutCompiler.logicalToPhysicalType(namespace, p.type(), typeArgs);

            switch (LayoutCodeTraits.clearImmutableBit(type.layoutCode())) {

                case OBJECT_SCOPE: {
                    if (!p.type().nullable()) {
                        throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                    }
                    ObjectPropertyType op = (ObjectPropertyType)p.type();
                    builder.addObjectScope(p.path(), type);
                    LayoutCompiler.addProperties(builder, namespace, type.layoutCode(), op.properties());
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
                    if (!p.type().nullable()) {
                        throw new LayoutCompilationException("Non-nullable sparse column are not supported.");
                    }
                    builder.addTypedScope(p.path(), type, typeArgs.get());
                    break;
                }

                case NULLABLE_SCOPE: {
                    throw new LayoutCompilationException("Nullables cannot be explicitly declared as columns.");
                }

                default: {

                    if (p.type() instanceof PrimitivePropertyType) {

                        PrimitivePropertyType pp = (PrimitivePropertyType) p.type();

                        switch (pp.storage()) {

                            case FIXED:
                                if (LayoutCodeTraits.clearImmutableBit(layoutCode) != LayoutCode.SCHEMA) {
                                    throw new LayoutCompilationException(
                                        "Cannot have fixed storage within a sparse layoutCode.");
                                }
                                if (type.isNull() && !pp.nullable()) {
                                    throw new LayoutCompilationException(
                                        "Non-nullable null columns are not supported.");
                                }
                                builder.addFixedColumn(p.path(), type, pp.nullable(), pp.length());
                                break;

                            case VARIABLE:
                                if (LayoutCodeTraits.clearImmutableBit(layoutCode) != LayoutCode.SCHEMA) {
                                    throw new LayoutCompilationException(
                                        "Cannot have variable storage within a sparse layoutCode.");
                                }
                                if (!pp.nullable()) {
                                    throw new LayoutCompilationException(
                                        "Non-nullable variable columns are not supported.");
                                }
                                builder.addVariableColumn(p.path(), type, pp.length());
                                break;

                            case SPARSE:
                                if (!pp.nullable()) {
                                    throw new LayoutCompilationException(
                                        "Non-nullable sparse columns are not supported.");
                                }
                                builder.addSparseColumn(p.path(), type);
                                break;

                            default:
                                throw new LayoutCompilationException(
                                    lenientFormat("Unknown storage specification: %s", pp.storage()));
                        }
                    } else {
                        throw new LayoutCompilationException(
                            lenientFormat("Unknown property type: %s", type.name()));
                    }

                    break;
                }
            }
        }
    }

    private static LayoutType logicalToPhysicalType(
        Namespace namespace, PropertyType logicalType, Out<TypeArgumentList> typeArgs) {

        typeArgs.set(TypeArgumentList.EMPTY);
        boolean immutable = logicalType instanceof ScopePropertyType && ((ScopePropertyType) logicalType).immutable();

        switch (logicalType.type()) {

            case NULL:
                return LayoutTypes.NULL;

            case BOOLEAN:
                return LayoutTypes.BOOLEAN;

            case INT_8:
                return LayoutTypes.INT_8;

            case INT_16:
                return LayoutTypes.INT_16;

            case INT_32:
                return LayoutTypes.INT_32;

            case INT_64:
                return LayoutTypes.INT_64;

            case UINT_8:
                return LayoutTypes.UINT_8;

            case UINT_16:
                return LayoutTypes.UINT_16;

            case UINT_32:
                return LayoutTypes.UINT_32;

            case UINT_64:
                return LayoutTypes.UINT_64;

            case FLOAT_32:
                return LayoutTypes.FLOAT_32;

            case FLOAT_64:
                return LayoutTypes.FLOAT_64;

            case FLOAT_128:
                return LayoutTypes.FLOAT_128;

            case DECIMAL:
                return LayoutTypes.DECIMAL;

            case DATE_TIME:
                return LayoutTypes.DATE_TIME;

            case UNIX_DATE_TIME:
                return LayoutTypes.UNIX_DATE_TIME;

            case GUID:
                return LayoutTypes.GUID;

            case MONGODB_OBJECT_ID:
                throw new UnsupportedOperationException();
                // return LayoutTypes.MONGO_DB_OBJECT_ID;

            case UTF_8:
                return LayoutTypes.UTF_8;

            case BINARY:
                return LayoutTypes.BINARY;

            case VAR_INT:
                return LayoutTypes.VAR_INT;

            case VAR_UINT:
                return LayoutTypes.VAR_UINT;

            case OBJECT:
                return immutable ? LayoutTypes.IMMUTABLE_OBJECT : LayoutTypes.OBJECT;

            case ARRAY: {

                assert logicalType instanceof ArrayPropertyType;
                ArrayPropertyType ap = (ArrayPropertyType) logicalType;

                if (ap.items() != null && (ap.items().type() != TypeKind.ANY)) {

                    final Out<TypeArgumentList> out = new Out<>();

                    LayoutType itemType = LayoutCompiler.logicalToPhysicalType(namespace, ap.items(), out);
                    TypeArgumentList itemTypeArgs = out.get();

                    if (ap.items().nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs));
                        itemType = itemType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    typeArgs.set(new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs)));
                    return immutable ? LayoutTypes.IMMUTABLE_TYPED_ARRAY : LayoutTypes.TYPED_ARRAY;
                }

                return immutable ? LayoutTypes.IMMUTABLE_ARRAY : LayoutTypes.ARRAY;
            }
            case SET: {

                assert logicalType instanceof SetPropertyType;
                SetPropertyType sp = (SetPropertyType) logicalType;

                if ((sp.items() != null) && (sp.items().type() != TypeKind.ANY)) {

                    final Out<TypeArgumentList> out = new Out<>();

                    LayoutType itemType = LayoutCompiler.logicalToPhysicalType(namespace, sp.items(), out);
                    TypeArgumentList itemTypeArgs = out.get();

                    if (sp.items().nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs));
                        itemType = itemType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    typeArgs.set(new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs)));
                    return immutable ? LayoutTypes.IMMUTABLE_TYPED_SET : LayoutTypes.TYPED_SET;
                }

                // TODO(283638): implement sparse set

                throw new LayoutCompilationException(lenientFormat(
                    "Unknown property type: %s",
                    logicalType.type()
                ));
            }
            case MAP: {

                assert logicalType instanceof MapPropertyType;
                MapPropertyType mp = (MapPropertyType) logicalType;

                if (mp.keys() != null && (mp.keys().type() != TypeKind.ANY) && (mp.values() != null) && (mp.values().type() != TypeKind.ANY)) {

                    final Out<TypeArgumentList> out = new Out<>();

                    LayoutType keyType = LayoutCompiler.logicalToPhysicalType(namespace, mp.keys(), out);
                    TypeArgumentList keyTypeArgs = out.get();

                    if (mp.keys().nullable()) {
                        keyTypeArgs = new TypeArgumentList(new TypeArgument(keyType, keyTypeArgs));
                        keyType = keyType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    LayoutType valueType = LayoutCompiler.logicalToPhysicalType(namespace, mp.values(), out);
                    TypeArgumentList valueTypeArgs = out.get();

                    if (mp.values().nullable()) {
                        valueTypeArgs = new TypeArgumentList(new TypeArgument(valueType, valueTypeArgs));
                        valueType = valueType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    typeArgs.set(new TypeArgumentList(
                        new TypeArgument(keyType, keyTypeArgs),
                        new TypeArgument(valueType, valueTypeArgs)
                    ));

                    return immutable ? LayoutTypes.IMMUTABLE_TYPED_MAP : LayoutTypes.TYPED_MAP;
                }

                // TODO(283638): implement sparse map

                throw new LayoutCompilationException(lenientFormat(
                    "Unknown property type: %s", logicalType.type())
                );
            }
            case TUPLE: {

                assert logicalType instanceof TuplePropertyType;
                final TuplePropertyType tp = (TuplePropertyType) logicalType;

                final TypeArgument[] args = new TypeArgument[tp.items().size()];
                final Out<TypeArgumentList> out = new Out<>();

                for (int i = 0; i < tp.items().size(); i++) {

                    LayoutType itemType = LayoutCompiler.logicalToPhysicalType(namespace, tp.items().get(i), out);
                    TypeArgumentList itemTypeArgs = out.get();

                    if (tp.items().get(i).nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs));
                        itemType = itemType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    args[i] = new TypeArgument(itemType, itemTypeArgs);
                }

                typeArgs.setAndGet(new TypeArgumentList(args));
                return immutable ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE;
            }
            case TAGGED: {

                assert logicalType instanceof TaggedPropertyType;
                TaggedPropertyType tg = (TaggedPropertyType) logicalType;

                if (tg.items().size() < TaggedPropertyType.MIN_TAGGED_ARGUMENTS || (tg.items().size() > TaggedPropertyType.MAX_TAGGED_ARGUMENTS)) {
                    throw new LayoutCompilationException(lenientFormat(
                        "Invalid number of arguments in Tagged: %s <= %s <= %s",
                        TaggedPropertyType.MIN_TAGGED_ARGUMENTS,
                        tg.items().size(),
                        TaggedPropertyType.MAX_TAGGED_ARGUMENTS
                    ));
                }

                final Out<TypeArgumentList> out = new Out<>();
                final TypeArgument[] tgArgs = new TypeArgument[tg.items().size() + 1];

                tgArgs[0] = new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY);

                for (int i = 0; i < tg.items().size(); i++) {

                    LayoutType itemType = LayoutCompiler.logicalToPhysicalType(namespace, tg.items().get(i), out);
                    TypeArgumentList itemTypeArgs = out.get();

                    if (tg.items().get(i).nullable()) {
                        itemTypeArgs = new TypeArgumentList(new TypeArgument(itemType, itemTypeArgs));
                        itemType = itemType.isImmutable() ? LayoutTypes.IMMUTABLE_NULLABLE : LayoutTypes.NULLABLE;
                    }

                    tgArgs[i + 1] = new TypeArgument(itemType, itemTypeArgs);
                }

                typeArgs.set(new TypeArgumentList(tgArgs));

                switch (tg.items().size()) {
                    case 1:
                        return immutable ? LayoutTypes.IMMUTABLE_TAGGED : LayoutTypes.TAGGED;
                    case 2:
                        return immutable ? LayoutTypes.IMMUTABLE_TAGGED_2 : LayoutTypes.TAGGED_2;
                    default:
                        throw new LayoutCompilationException("Unexpected tagged arity");
                }
            }
            case SCHEMA: {

                assert logicalType instanceof UdtPropertyType;
                UdtPropertyType up = (UdtPropertyType) logicalType;

                final Optional<Schema> udtSchema;

                if (up.schemaId() == SchemaId.NONE) {
                    udtSchema = namespace.schemas().stream()
                        .filter(schema -> up.name().equals(schema.name()))
                        .findFirst();
                } else {
                    udtSchema = namespace.schemas().stream()
                        .filter(schema -> up.schemaId().equals(schema.schemaId()))
                        .findFirst();
                    if (udtSchema.isPresent() && !up.name().equals(udtSchema.get().name())) {
                        throw new LayoutCompilationException(lenientFormat(
                            "ambiguous schema reference: '%s:%s'", up.name(), up.schemaId()
                        ));
                    }
                }

                if (!udtSchema.isPresent()) {
                    throw new LayoutCompilationException(lenientFormat(
                        "cannot resolve schema reference '%s:%s'", up.name(), up.schemaId()
                    ));
                }

                typeArgs.set(new TypeArgumentList(udtSchema.get().schemaId()));
                return immutable ? LayoutTypes.IMMUTABLE_UDT : LayoutTypes.UDT;
            }
            default:
                throw new LayoutCompilationException(Strings.lenientFormat(
                    "Unknown property type: %s", logicalType.type()
                ));
        }
    }
}
