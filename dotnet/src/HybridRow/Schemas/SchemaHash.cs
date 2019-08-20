﻿// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Internal;

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
            hash = MurmurHash3.Hash128(schema.SchemaId, hash);
            hash = MurmurHash3.Hash128(schema.Type, hash);
            hash = SchemaHash.ComputeHash(ns, schema.Options, hash);
            if (schema.PartitionKeys != null)
            {
                foreach (PartitionKey p in schema.PartitionKeys)
                {
                    hash = SchemaHash.ComputeHash(ns, p, hash);
                }
            }

            if (schema.PrimarySortKeys != null)
            {
                foreach (PrimarySortKey p in schema.PrimarySortKeys)
                {
                    hash = SchemaHash.ComputeHash(ns, p, hash);
                }
            }

            if (schema.StaticKeys != null)
            {
                foreach (StaticKey p in schema.StaticKeys)
                {
                    hash = SchemaHash.ComputeHash(ns, p, hash);
                }
            }

            if (schema.Properties != null)
            {
                foreach (Property p in schema.Properties)
                {
                    hash = SchemaHash.ComputeHash(ns, p, hash);
                }
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, SchemaOptions options, (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            hash = MurmurHash3.Hash128(options?.DisallowUnschematized ?? false, hash);
            hash = MurmurHash3.Hash128(options?.EnablePropertyLevelTimestamp ?? false, hash);
            if (options?.DisableSystemPrefix ?? false)
            {
                hash = MurmurHash3.Hash128(true, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, Property p, (ulong low, ulong high) seed = default)
        {
            Contract.Requires(p != null);
            (ulong low, ulong high) hash = seed;
            hash = MurmurHash3.Hash128(p.Path, hash);
            hash = SchemaHash.ComputeHash(ns, p.PropertyType, hash);
            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, PropertyType p, (ulong low, ulong high) seed = default)
        {
            Contract.Requires(p != null);
            (ulong low, ulong high) hash = seed;
            hash = MurmurHash3.Hash128(p.Type, hash);
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
                    break;
                case ScopePropertyType pp:
                    hash = MurmurHash3.Hash128(pp.Immutable, hash);
                    switch (p)
                    {
                        case ArrayPropertyType spp:
                            if (spp.Items != null)
                            {
                                hash = SchemaHash.ComputeHash(ns, spp.Items, hash);
                            }

                            break;
                        case ObjectPropertyType spp:
                            if (spp.Properties != null)
                            {
                                foreach (Property opp in spp.Properties)
                                {
                                    hash = SchemaHash.ComputeHash(ns, opp, hash);
                                }
                            }

                            break;
                        case MapPropertyType spp:
                            if (spp.Keys != null)
                            {
                                hash = SchemaHash.ComputeHash(ns, spp.Keys, hash);
                            }

                            if (spp.Values != null)
                            {
                                hash = SchemaHash.ComputeHash(ns, spp.Values, hash);
                            }

                            break;
                        case SetPropertyType spp:
                            if (spp.Items != null)
                            {
                                hash = SchemaHash.ComputeHash(ns, spp.Items, hash);
                            }

                            break;
                        case TaggedPropertyType spp:
                            if (spp.Items != null)
                            {
                                foreach (PropertyType pt in spp.Items)
                                {
                                    hash = SchemaHash.ComputeHash(ns, pt, hash);
                                }
                            }

                            break;
                        case TuplePropertyType spp:
                            if (spp.Items != null)
                            {
                                foreach (PropertyType pt in spp.Items)
                                {
                                    hash = SchemaHash.ComputeHash(ns, pt, hash);
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
                                    throw new Exception($"Ambiguous schema reference: '{spp.Name}:{spp.SchemaId}'");
                                }
                            }

                            if (udtSchema == null)
                            {
                                throw new Exception($"Cannot resolve schema reference '{spp.Name}:{spp.SchemaId}'");
                            }

                            hash = SchemaHash.ComputeHash(ns, udtSchema, hash);
                            break;
                    }

                    break;
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, PartitionKey key, (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, PrimarySortKey key, (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
                hash = MurmurHash3.Hash128(key.Direction, hash);
            }

            return hash;
        }

        private static (ulong low, ulong high) ComputeHash(Namespace ns, StaticKey key, (ulong low, ulong high) seed = default)
        {
            (ulong low, ulong high) hash = seed;
            if (key != null)
            {
                hash = MurmurHash3.Hash128(key.Path, hash);
            }

            return hash;
        }
    }
}
