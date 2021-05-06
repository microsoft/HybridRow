// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Internal;

    // ReSharper disable UnusedParameter.Local
    public static class SchemaHash
    {
        /// <summary>Computes the logical hash for a logical schema.</summary>
        /// <param name="ns">The namespace within which <paramref name="schema" /> is defined.</param>
        /// <param name="schema">The logical schema to compute the hash of.</param>
        /// <param name="seed">The seed to initialized the hash function.</param>
        /// <returns>The logical 128-bit hash as a two-tuple (low, high).</returns>
        public static (ulong low, ulong high) ComputeHash(Namespace ns, Schema schema, (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            SchemaLanguageVersion v = schema.GetEffectiveSdlVersion(ns);
            hash = MurmurHash3.Hash128(schema.SchemaId, hash);
            hash = SchemaHash.ComputeHash(v, ns, schema.Type, hash);
            hash = MurmurHash3.Hash128(v, hash);
            hash = SchemaHash.ComputeHash(v, schema.Options, hash);

            hash = MurmurHash3.Hash128(schema.PartitionKeys.Count, hash);
            foreach (PartitionKey p in schema.PartitionKeys)
            {
                hash = SchemaHash.ComputeHash(v, ns, p, hash);
            }

            hash = MurmurHash3.Hash128(schema.PrimaryKeys.Count, hash);
            foreach (PrimarySortKey p in schema.PrimaryKeys)
            {
                hash = SchemaHash.ComputeHash(v, ns, p, hash);
            }

            hash = MurmurHash3.Hash128(schema.StaticKeys.Count, hash);
            foreach (StaticKey p in schema.StaticKeys)
            {
                hash = SchemaHash.ComputeHash(v, ns, p, hash);
            }

            hash = MurmurHash3.Hash128(schema.Properties.Count, hash);
            foreach (Property p in schema.Properties)
            {
                hash = SchemaHash.ComputeHash(v, ns, p, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            SchemaOptions options,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;

            hash = MurmurHash3.Hash128(options?.DisallowUnschematized ?? false, hash);
            hash = MurmurHash3.Hash128(options?.EnablePropertyLevelTimestamp ?? false, hash);
            hash = MurmurHash3.Hash128(options?.DisableSystemPrefix ?? false, hash);

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            Property p,
            (ulong low, ulong high) seed = default)
        {
            Contract.Requires(p != null);
            (ulong low, ulong high) hash = seed;
            hash = MurmurHash3.Hash128(p.Path, hash);
            hash = SchemaHash.ComputeHash(v, ns, p.PropertyType, hash);
            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            PropertyType p,
            (ulong low, ulong high) seed = default)
        {
            Contract.Requires(p != null);
            (ulong low, ulong high) hash = seed;
            hash = SchemaHash.ComputeHash(v, ns, p.Type, hash);
            hash = MurmurHash3.Hash128(p.Nullable, hash);
            if (p.ApiType != null)
            {
                hash = MurmurHash3.Hash128(p.ApiType, hash);
            }

            switch (p)
            {
                case PrimitivePropertyType pp:
                    hash = MurmurHash3.Hash128(pp.Storage, hash);
                    hash = MurmurHash3.Hash128(pp.Length, hash);
                    if (pp.Type == TypeKind.Enum)
                    {
                        if (v < SchemaLanguageVersion.V2)
                        {
                            throw new SchemaException($"Enums require SDL v2 or higher: {v}.");
                        }

                        EnumSchema enumSchema = ns.Enums.Find(es => es.Name == pp.Enum);
                        if (enumSchema == null)
                        {
                            throw new SchemaException($"Cannot resolve enum schema reference '{pp.Enum}'");
                        }

                        hash = SchemaHash.ComputeHash(v, ns, enumSchema, hash);
                    }
                    break;
                case ScopePropertyType pp:
                    hash = MurmurHash3.Hash128(pp.Immutable, hash);
                    switch (p)
                    {
                        case ArrayPropertyType spp:
                            if (spp.Items != null)
                            {
                                hash = SchemaHash.ComputeHash(v, ns, spp.Items, hash);
                            }

                            break;
                        case ObjectPropertyType spp:
                            if (spp.Properties != null)
                            {
                                foreach (Property opp in spp.Properties)
                                {
                                    hash = SchemaHash.ComputeHash(v, ns, opp, hash);
                                }
                            }

                            break;
                        case MapPropertyType spp:
                            if (spp.Keys != null)
                            {
                                hash = SchemaHash.ComputeHash(v, ns, spp.Keys, hash);
                            }

                            if (spp.Values != null)
                            {
                                hash = SchemaHash.ComputeHash(v, ns, spp.Values, hash);
                            }

                            break;
                        case SetPropertyType spp:
                            if (spp.Items != null)
                            {
                                hash = SchemaHash.ComputeHash(v, ns, spp.Items, hash);
                            }

                            break;
                        case TaggedPropertyType spp:
                            if (spp.Items != null)
                            {
                                foreach (PropertyType pt in spp.Items)
                                {
                                    hash = SchemaHash.ComputeHash(v, ns, pt, hash);
                                }
                            }

                            break;
                        case TuplePropertyType spp:
                            if (spp.Items != null)
                            {
                                foreach (PropertyType pt in spp.Items)
                                {
                                    hash = SchemaHash.ComputeHash(v, ns, pt, hash);
                                }
                            }

                            break;
                        case UdtPropertyType spp:
                            Schema udtSchema;
                            if (spp.SchemaId == SchemaId.Invalid)
                            {
                                udtSchema = ns.Schemas.Find(s => s.Name == spp.Name);
                            }
                            else
                            {
                                udtSchema = ns.Schemas.Find(s => s.SchemaId == spp.SchemaId);
                                if (udtSchema.Name != spp.Name)
                                {
                                    throw new SchemaException($"Ambiguous schema reference: '{spp.Name}:{spp.SchemaId}'");
                                }
                            }

                            if (udtSchema == null)
                            {
                                throw new SchemaException($"Cannot resolve schema reference '{spp.Name}:{spp.SchemaId}'");
                            }

                            hash = SchemaHash.ComputeHash(ns, udtSchema, hash);
                            break;
                    }

                    break;
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            PartitionKey key,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            PrimarySortKey key,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
                hash = SchemaHash.ComputeHash(v, ns, key.Direction, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            StaticKey key,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            EnumSchema es,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            hash = SchemaHash.ComputeHash(v, ns, es.Type, hash);

            hash = MurmurHash3.Hash128(es.Values.Count, hash);
            foreach (EnumValue ev in es.Values)
            {
                hash = SchemaHash.ComputeHash(v, ns, ev, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            EnumValue ev,
            (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            hash = MurmurHash3.Hash128(ev.Value, hash);
            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            TypeKind type,
            (ulong low, ulong high) seed = default)
        {
            return MurmurHash3.Hash128((int)type, seed);
        }

        private static (ulong low, ulong high) ComputeHash(
            SchemaLanguageVersion v,
            Namespace ns,
            SortDirection direction,
            (ulong low, ulong high) seed = default)
        {
            return MurmurHash3.Hash128((int)direction, seed);
        }
    }
}
