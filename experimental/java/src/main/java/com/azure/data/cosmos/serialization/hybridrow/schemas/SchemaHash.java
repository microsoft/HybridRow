// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.HashCode128;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.internal.Murmur3Hash;

import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

public final class SchemaHash {

    /**
     * Computes the logical hash for a logical schema.
     *
     * @param namespace The namespace within which {@code schema} is defined.
     * @param schema    The logical schema to compute the hash of.
     * @param seed      The seed to initialized the hash function.
     * @return The logical 128-bit hash as a two-tuple (low, high).
     */
    public static HashCode128 computeHash(Namespace namespace, Schema schema, HashCode128 seed) {
        HashCode128 hash = seed;

        hash = Murmur3Hash.Hash128(schema.schemaId().value(), hash);
        hash = Murmur3Hash.Hash128(schema.type().value(), hash);
        hash = SchemaHash.computeHash(namespace, schema.options(), hash);

        if (schema.partitionKeys() != null) {
            for (PartitionKey partitionKey : schema.partitionKeys()) {
                hash = SchemaHash.computeHash(namespace, partitionKey, hash);
            }
        }

        if (schema.primarySortKeys() != null) {
            for (PrimarySortKey p : schema.primarySortKeys()) {
                hash = SchemaHash.computeHash(namespace, p, hash);
            }
        }

        if (schema.staticKeys() != null) {
            for (StaticKey p : schema.staticKeys()) {
                hash = SchemaHash.computeHash(namespace, p, hash);
            }
        }

        if (schema.properties() != null) {
            for (Property p : schema.properties()) {
                hash = SchemaHash.computeHash(namespace, p, hash);
            }
        }

        return hash;
    }

    private static HashCode128 computeHash(Namespace namespace, SchemaOptions options, HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.Hash128(options != null && options.disallowUnschematized(), hash);
        hash = Murmur3Hash.Hash128(options != null && options.enablePropertyLevelTimestamp(), hash);
        hash = Murmur3Hash.Hash128(options != null && options.disableSystemPrefix(), hash);

        return hash;
    }

    private static HashCode128 computeHash(Namespace ns, Property p, HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.Hash128(p.path(), hash);
        hash = SchemaHash.computeHash(ns, p.type(), hash);

        return hash;
    }

    private static HashCode128 computeHash(Namespace namespace, PropertyType p, HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.Hash128(p.type().value(), hash);
        hash = Murmur3Hash.Hash128(p.nullable(), hash);

        if (p.apiType() != null) {
            hash = Murmur3Hash.Hash128(p.apiType(), hash);
        }

        if (p instanceof PrimitivePropertyType) {

            PrimitivePropertyType pp = (PrimitivePropertyType) p;

            hash = Murmur3Hash.Hash128(pp.storage().value(), hash);
            hash = Murmur3Hash.Hash128(pp.length(), hash);

            return hash;
        }

        checkState(p instanceof ScopePropertyType);
        ScopePropertyType pp = (ScopePropertyType) p;
        hash = Murmur3Hash.Hash128(pp.immutable(), hash);

        if (p instanceof ArrayPropertyType) {
            ArrayPropertyType spp = (ArrayPropertyType) p;
            if (spp.items() != null) {
                hash = SchemaHash.computeHash(namespace, spp.items(), hash);
            }
            return hash;
        }

        if (p instanceof ObjectPropertyType) {
            ObjectPropertyType spp = (ObjectPropertyType) p;
            if (spp.properties() != null) {
                for (Property opp : spp.properties()) {
                    hash = SchemaHash.computeHash(namespace, opp, hash);
                }
            }
            return hash;
        }

        if (p instanceof MapPropertyType) {

            MapPropertyType spp = (MapPropertyType) p;

            if (spp.keys() != null) {
                hash = SchemaHash.computeHash(namespace, spp.keys(), hash);
            }

            if (spp.values() != null) {
                hash = SchemaHash.computeHash(namespace, spp.values(), hash);
            }

            return hash;
        }

        if (p instanceof SetPropertyType) {

            SetPropertyType spp = (SetPropertyType) p;

            if (spp.items() != null) {
                hash = SchemaHash.computeHash(namespace, spp.items(), hash);
            }

            return hash;
        }

        if (p instanceof TaggedPropertyType) {

            TaggedPropertyType spp = (TaggedPropertyType) p;

            if (spp.items() != null) {
                for (PropertyType pt : spp.items()) {
                    hash = SchemaHash.computeHash(namespace, pt, hash);
                }
            }

            return hash;
        }

        if (p instanceof TuplePropertyType) {

            TuplePropertyType spp = (TuplePropertyType) p;

            if (spp.items() != null) {
                for (PropertyType pt : spp.items()) {
                    hash = SchemaHash.computeHash(namespace, pt, hash);
                }
            }

            return hash;
        }

        if (p instanceof UdtPropertyType) {

            Stream<Schema> schemaStream = namespace.schemas().stream();
            UdtPropertyType spp = (UdtPropertyType) p;
            Optional<Schema> udtSchema;

            if (spp.schemaId() == SchemaId.INVALID) {
                udtSchema = schemaStream.filter(schema -> schema.name().equals(spp.name())).findFirst();
            } else {
                udtSchema = schemaStream.filter(schema -> schema.schemaId().equals(spp.schemaId())).findFirst();
                udtSchema.ifPresent(schema -> checkState(schema.name().equals(spp.name()),
                    "Ambiguous schema reference: '%s:%s'", spp.name(), spp.schemaId()));
            }

            checkState(udtSchema.isPresent(), "Cannot resolve schema reference '{0}:{1}'", spp.name(), spp.schemaId());
            return SchemaHash.computeHash(namespace, udtSchema.get(), hash);
        }

        throw new IllegalStateException(lenientFormat("unrecognized property type: %s", p.getClass()));
    }

    private static HashCode128 computeHash(Namespace namespace, PartitionKey key, HashCode128 seed) {
        return key == null ? seed : Murmur3Hash.Hash128(key.path(), seed);
    }

    private static HashCode128 computeHash(Namespace namespace, PrimarySortKey key, HashCode128 seed) {
        HashCode128 hash = seed;
        if (key != null) {
            hash = Murmur3Hash.Hash128(key.path(), hash);
            hash = Murmur3Hash.Hash128(key.direction().value(), hash);
        }
        return hash;
    }

    private static HashCode128 computeHash(Namespace ns, StaticKey key, HashCode128 seed) {
        return key == null ? seed : Murmur3Hash.Hash128(key.path(), seed);
    }
}