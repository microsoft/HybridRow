// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.HashCode128;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.azure.data.cosmos.serialization.hybridrow.internal.Murmur3Hash;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.lenientFormat;

public final class SchemaHash {

	/**
	 * Computes the logical hash for a logical schema.
	 *
	 * @param namespace The namespace within which <paramref name="schema" /> is defined.
	 * @param schema    The logical schema to compute the hash of.
	 * @param seed      The seed to initialized the hash function.
	 * @return The logical 128-bit hash as a two-tuple (low, high).
	 */
	public static HashCode128 ComputeHash(Namespace namespace, Schema schema, HashCode128 seed) {
		HashCode128 hash = seed;

		hash = Murmur3Hash.Hash128(schema.schemaId().value(), hash);
		hash = Murmur3Hash.Hash128(schema.type().value(), hash);
		hash = SchemaHash.ComputeHash(namespace, schema.options(), hash);

		if (schema.partitionKeys() != null) {
			for (PartitionKey partitionKey : schema.partitionKeys()) {
				hash = SchemaHash.ComputeHash(namespace, partitionKey, hash);
			}
		}

		if (schema.primarySortKeys() != null) {
			for (PrimarySortKey p : schema.primarySortKeys()) {
				hash = SchemaHash.ComputeHash(namespace, p, hash);
			}
		}

		if (schema.staticKeys() != null) {
			for (StaticKey p : schema.staticKeys()) {
				hash = SchemaHash.ComputeHash(namespace, p, hash);
			}
		}

		if (schema.properties() != null) {
			for (Property p : schema.properties()) {
				hash = SchemaHash.ComputeHash(namespace, p, hash);
			}
		}

		return hash;
	}

	private static HashCode128 ComputeHash(Namespace namespace, SchemaOptions options, HashCode128 seed) {

		HashCode128 hash = seed;

		hash = Murmur3Hash.Hash128(options != null && options.disallowUnschematized(), hash);
		hash = Murmur3Hash.Hash128(options != null && options.enablePropertyLevelTimestamp(), hash);
		hash = Murmur3Hash.Hash128(options != null && options.disableSystemPrefix(), hash);

		return hash;
	}

	private static HashCode128 ComputeHash(Namespace ns, Property p, HashCode128 seed) {

		HashCode128 hash = seed;

		hash = Murmur3Hash.Hash128(p.path(), hash);
		hash = SchemaHash.ComputeHash(ns, p.propertyType(), hash);

		return hash;
	}

	private static HashCode128 ComputeHash(Namespace namespace, PropertyType p, HashCode128 seed) {

		HashCode128 hash = seed;

		hash = Murmur3Hash.Hash128(p.type(), hash);
		hash = Murmur3Hash.Hash128(p.nullable(), hash);

		if (p.apiType() != null) {
			hash = Murmur3Hash.Hash128(p.apiType(), hash);
		}

		if (p instanceof PrimitivePropertyType) {
			PrimitivePropertyType pp = (PrimitivePropertyType) p;
			hash = Murmur3Hash.Hash128(pp.storage(), hash);
			hash = Murmur3Hash.Hash128(pp.length(), hash);
			return hash;
		}

		checkState(p instanceof ScopePropertyType);
		ScopePropertyType pp = (ScopePropertyType) p;
		hash = Murmur3Hash.Hash128(pp.immutable(), hash);

		if (p instanceof ArrayPropertyType) {
			ArrayPropertyType spp = (ArrayPropertyType) p;
			if (spp.items() != null) {
				hash = SchemaHash.ComputeHash(namespace, spp.items(), hash);
			}
			return hash;
		}

		if (p instanceof ObjectPropertyType) {
			ObjectPropertyType spp = (ObjectPropertyType) p;
			if (spp.properties() != null) {
				for (Property opp : spp.properties()) {
					hash = SchemaHash.ComputeHash(namespace, opp, hash);
				}
			}
			return hash;
		}

		if (p instanceof MapPropertyType) {

			MapPropertyType spp = (MapPropertyType) p;

			if (spp.keys() != null) {
				hash = SchemaHash.ComputeHash(namespace, spp.keys(), hash);
			}

			if (spp.values() != null) {
				hash = SchemaHash.ComputeHash(namespace, spp.values(), hash);
			}

			return hash;
		}

		if (p instanceof SetPropertyType) {

			SetPropertyType spp = (SetPropertyType) p;

			if (spp.items() != null) {
				hash = SchemaHash.ComputeHash(namespace, spp.items(), hash);
			}

			return hash;
		}

		if (p instanceof TaggedPropertyType) {

			TaggedPropertyType spp = (TaggedPropertyType) p;

			if (spp.items() != null) {
				for (PropertyType pt : spp.items()) {
					hash = SchemaHash.ComputeHash(namespace, pt, hash);
				}
			}

			return hash;
		}

		if (p instanceof TuplePropertyType) {

			TuplePropertyType spp = (TuplePropertyType) p;

			if (spp.items() != null) {
				for (PropertyType pt : spp.items()) {
					hash = SchemaHash.ComputeHash(namespace, pt, hash);
				}
			}

			return hash;
		}

		if (p instanceof UdtPropertyType) {

			UdtPropertyType spp = (UdtPropertyType) p;
			Schema udtSchema;

			if (spp.schemaId() == SchemaId.INVALID) {
				udtSchema = namespace.schemas().Find(s = > s.name() == spp.name());
			} else {
				udtSchema = namespace.schemas().Find(s = > s.schemaId() == spp.schemaId());
				checkState(udtSchema.name().equals(spp.name()), "Ambiguous schema reference: '%s:%s'", spp.name(), spp.schemaId());
			}

			checkState(udtSchema != null, "Cannot resolve schema reference '{0}:{1}'", spp.name(), spp.schemaId());
			return SchemaHash.ComputeHash(namespace, udtSchema, hash);
		}

		throw new IllegalStateException(lenientFormat("unrecognized property type: %s", p.getClass()));
	}

	// TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	private static(ulong low, ulong high) ComputeHash(Namespace ns, PartitionKey key, (ulong low, ulong high) seed
    //	= default)
    //		{
    //			(ulong low, ulong high) hash = seed;
    //			if (key != null)
    //			{
    //				hash = Murmur3Hash.Hash128(key.Path, hash);
    //			}
    //
    //			return hash;
    //		}

    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	private static(ulong low, ulong high) ComputeHash(Namespace ns, PrimarySortKey key, (ulong low, ulong high) seed = default)
    //		{
    //			(ulong low, ulong high) hash = seed;
    //			if (key != null)
    //			{
    //				hash = Murmur3Hash.Hash128(key.Path, hash);
    //				hash = Murmur3Hash.Hash128(key.Direction, hash);
    //			}
    //
    //			return hash;
    //		}

    // TODO: C# TO JAVA CONVERTER: Methods returning tuples are not converted by C# to Java Converter:
    //	private static(ulong low, ulong high) ComputeHash(Namespace ns, StaticKey key, (ulong low, ulong high) seed = default)
    //		{
    //			(ulong low, ulong high) hash = seed;
    //			if (key != null)
    //			{
    //				hash = Murmur3Hash.Hash128(key.Path, hash);
    //			}
    //
    //			return hash;
    //		}
}