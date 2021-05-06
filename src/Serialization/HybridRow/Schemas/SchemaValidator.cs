// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1316 // Tuple element names should use correct casing
#pragma warning disable SA1414 // Tuple types in signatures should have element names

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Core;

    public static class SchemaValidator
    {
        public static void Validate(Namespace ns)
        {
            Dictionary<string, int> nameVersioningCheck = new Dictionary<string, int>(ns.Schemas.Count);
            Dictionary<(string, SchemaId), Schema> nameDupCheck = new Dictionary<(string, SchemaId), Schema>(ns.Schemas.Count);
            Dictionary<SchemaId, Schema> idDupCheck = new Dictionary<SchemaId, Schema>(ns.Schemas.Count);
            Dictionary<string, EnumSchema> enumDupCheck = new Dictionary<string, EnumSchema>(ns.Enums.Count);
            foreach (Schema s in ns.Schemas)
            {
                ValidateAssert.IsValidSchemaId(s.SchemaId, "Schema id");
                ValidateAssert.IsValidIdentifier(s.Name, "Schema name");
                ValidateAssert.DuplicateCheck(s.SchemaId, s, idDupCheck, "Schema id", "Namespace");
                ValidateAssert.DuplicateCheck((s.Name, s.SchemaId), s, nameDupCheck, "Schema reference", "Namespace");

                // Count the versions of each schema by name.
                nameVersioningCheck.TryGetValue(s.Name, out int count);
                nameVersioningCheck[s.Name] = count + 1;
            }

            // Enable id-less Schema references for all types with a unique version in the namespace.
            foreach (Schema s in ns.Schemas)
            {
                if (nameVersioningCheck[s.Name] == 1)
                {
                    ValidateAssert.DuplicateCheck((s.Name, SchemaId.Invalid), s, nameDupCheck, "Schema reference", "Namespace");
                }
            }

            foreach (EnumSchema es in ns.Enums)
            {
                ValidateAssert.IsValidIdentifier(es.Name, "EnumSchema name");
                ValidateAssert.DuplicateCheck(es.Name, es, enumDupCheck, "EnumSchema reference", "Namespace");
            }

            SchemaValidator.Visit(ns, nameDupCheck, idDupCheck, enumDupCheck);
        }

        /// <summary>Visit an entire namespace and validate its constraints.</summary>
        /// <param name="ns">The <see cref="Namespace" /> to validate.</param>
        /// <param name="schemas">A map from schema names within the namespace to their schemas.</param>
        /// <param name="ids">A map from schema ids within the namespace to their schemas.</param>
        /// <param name="enums">A map from enum schema names within the namespace to their enum schemas.</param>
        private static void Visit(
            Namespace ns,
            Dictionary<(string, SchemaId), Schema> schemas,
            Dictionary<SchemaId, Schema> ids,
            Dictionary<string, EnumSchema> enums)
        {
            foreach (Schema s in ns.Schemas)
            {
                SchemaValidator.Visit(s.GetEffectiveSdlVersion(ns), s, schemas, ids, enums);
            }

            foreach (EnumSchema es in ns.Enums)
            {
                SchemaValidator.Visit(ns.GetEffectiveSdlVersion(), es);
            }
        }

        /// <summary>Visit a single schema and validate its constraints.</summary>
        /// <param name="v">The version of the SDL in effect.</param>
        /// <param name="es">The <see cref="EnumSchema" /> to validate.</param>
        private static void Visit(SchemaLanguageVersion v, EnumSchema es)
        {
            ValidateAssert.IsTrue(v >= SchemaLanguageVersion.V2, $"Enums require SDL v2 or higher : {v}");

            Dictionary<string, EnumValue> nameDupCheck = new Dictionary<string, EnumValue>(es.Values.Count);
            foreach (EnumValue value in es.Values)
            {
                ValidateAssert.IsValidIdentifier(value.Name, "EnumSchema name");
                ValidateAssert.DuplicateCheck(value.Name, value, nameDupCheck, "EnumValue", "EnumSchema");

                switch (es.Type)
                {
                    case TypeKind.Int8:
                        ValidateAssert.IsValidEnumSize(sbyte.MinValue, sbyte.MaxValue, value, es);
                        break;
                    case TypeKind.Int16:
                        ValidateAssert.IsValidEnumSize(short.MinValue, short.MaxValue, value, es);
                        break;
                    case TypeKind.Int32:
                        ValidateAssert.IsValidEnumSize(int.MinValue, int.MaxValue, value, es);
                        break;
                    case TypeKind.VarInt:
                    case TypeKind.Int64:
                        ValidateAssert.IsValidEnumSize(long.MinValue, long.MaxValue, value, es);
                        break;
                    case TypeKind.UInt8:
                        ValidateAssert.IsValidEnumUSize(byte.MinValue, byte.MaxValue, value, es);
                        break;
                    case TypeKind.UInt16:
                        ValidateAssert.IsValidEnumUSize(ushort.MinValue, ushort.MaxValue, value, es);
                        break;
                    case TypeKind.UInt32:
                        ValidateAssert.IsValidEnumUSize(uint.MinValue, uint.MaxValue, value, es);
                        break;
                    case TypeKind.VarUInt:
                    case TypeKind.UInt64:
                        ValidateAssert.IsValidEnumUSize(ulong.MinValue, ulong.MaxValue, value, es);
                        break;
                    default:
                        throw new SchemaException($"{es.Type} is not a valid enumeration type.");
                }
            }
        }

        /// <summary>Visit a single schema and validate its constraints.</summary>
        /// <param name="v">The version of the SDL in effect.</param>
        /// <param name="s">The <see cref="Schema" /> to validate.</param>
        /// <param name="schemas">A map from schema names within the namespace to their schemas.</param>
        /// <param name="ids">A map from schema ids within the namespace to their schemas.</param>
        /// <param name="enums">A map from enum schema names within the namespace to their enum schemas.</param>
        private static void Visit(
            SchemaLanguageVersion v,
            Schema s,
            Dictionary<(string, SchemaId), Schema> schemas,
            Dictionary<SchemaId, Schema> ids,
            Dictionary<string, EnumSchema> enums)
        {
            ValidateAssert.AreEqual(s.Type, TypeKind.Schema, $"The type of a schema MUST be {TypeKind.Schema}: {s.Type}");
            Dictionary<string, Property> pathDupCheck = new Dictionary<string, Property>(s.Properties.Count);
            foreach (Property p in s.Properties)
            {
                ValidateAssert.DuplicateCheck(p.Path, p, pathDupCheck, "Property path", "Schema");
            }

            foreach (PartitionKey pk in s.PartitionKeys)
            {
                ValidateAssert.Exists(pk.Path, pathDupCheck, "Partition key column", "Schema");
            }

            foreach (PrimarySortKey ps in s.PrimaryKeys)
            {
                ValidateAssert.Exists(ps.Path, pathDupCheck, "Primary sort key column", "Schema");
            }

            foreach (StaticKey sk in s.StaticKeys)
            {
                ValidateAssert.Exists(sk.Path, pathDupCheck, "Static key column", "Schema");
            }

            foreach (Property p in s.Properties)
            {
                SchemaValidator.Visit(p, s, schemas, ids, enums);
            }

            List<SchemaId> bases = new List<SchemaId>();
            Schema cs = s;
            while (cs.BaseName != null)
            {
                ValidateAssert.IsTrue(v >= SchemaLanguageVersion.V2, $"Inheritance requires SDL v2 or higher : {v}");
                Schema bs = ValidateAssert.Exists((cs.BaseName, cs.BaseSchemaId), schemas, "Schema reference", "Namespace");
                if (cs.BaseSchemaId != SchemaId.Invalid)
                {
                    Schema baseSchema = ValidateAssert.Exists(cs.BaseSchemaId, ids, "Schema id", "Namespace");
                    ValidateAssert.AreEqual(
                        cs.BaseName,
                        baseSchema.Name,
                        $"Schema name '{cs.BaseName}' does not match the name of schema with id '{cs.BaseSchemaId}': {baseSchema.Name}");
                }

                ValidateAssert.NotExists(bs.SchemaId, bases, $"Inheritance cycle with '{s.Name}': {s.SchemaId}", "Namespace");
                bases.Add(bs.SchemaId);
                cs = bs;
            }
        }

        private static void Visit(
            Property p,
            Schema s,
            Dictionary<(string, SchemaId), Schema> schemas,
            Dictionary<SchemaId, Schema> ids,
            Dictionary<string, EnumSchema> enums)
        {
            ValidateAssert.IsValidIdentifier(p.Path, "Property path");
            SchemaValidator.Visit(p.PropertyType, null, schemas, ids, enums);
        }

        private static void Visit(
            PropertyType p,
            PropertyType parent,
            Dictionary<(string, SchemaId), Schema> schemas,
            Dictionary<SchemaId, Schema> ids,
            Dictionary<string, EnumSchema> enums)
        {
            switch (p)
            {
                case PrimitivePropertyType pp:
                    ValidateAssert.IsTrue(pp.Length >= 0, "Length MUST be positive");
                    if (parent != null)
                    {
                        ValidateAssert.AreEqual(pp.Storage, StorageKind.Sparse, $"Nested fields MUST have storage {StorageKind.Sparse}");
                    }
                    if (pp.Type == TypeKind.Enum)
                    {
                        ValidateAssert.Exists(pp.Enum, enums, "Enum reference", "Namespace");
                    }
                    if (pp.RowBufferSize)
                    {
                        ValidateAssert.AreEqual(pp.Storage, StorageKind.Fixed, $"RowBufferSize fields MUST have storage {StorageKind.Fixed}");
                        ValidateAssert.AreEqual(pp.Type, TypeKind.Int32, "RowBufferSize fields MUST be type int32");
                    }

                    break;
                case ArrayPropertyType ap:
                    if (ap.Items != null)
                    {
                        SchemaValidator.Visit(ap.Items, p, schemas, ids, enums);
                    }

                    break;
                case MapPropertyType mp:
                    SchemaValidator.Visit(mp.Keys, p, schemas, ids, enums);
                    SchemaValidator.Visit(mp.Values, p, schemas, ids, enums);
                    break;
                case SetPropertyType sp:
                    SchemaValidator.Visit(sp.Items, p, schemas, ids, enums);
                    break;
                case TaggedPropertyType gp:
                    foreach (PropertyType item in gp.Items)
                    {
                        SchemaValidator.Visit(item, p, schemas, ids, enums);
                    }

                    break;
                case TuplePropertyType tp:
                    foreach (PropertyType item in tp.Items)
                    {
                        SchemaValidator.Visit(item, p, schemas, ids, enums);
                    }

                    break;
                case ObjectPropertyType op:
                    Dictionary<string, Property> pathDupCheck = new Dictionary<string, Property>(op.Properties.Count);
                    foreach (Property nested in op.Properties)
                    {
                        ValidateAssert.DuplicateCheck(nested.Path, nested, pathDupCheck, "Property path", "Object");
                        SchemaValidator.Visit(nested.PropertyType, p, schemas, ids, enums);
                    }

                    break;
                case UdtPropertyType up:
                    ValidateAssert.Exists((up.Name, up.SchemaId), schemas, "Schema reference", "Namespace");
                    if (up.SchemaId != SchemaId.Invalid)
                    {
                        Schema s = ValidateAssert.Exists(up.SchemaId, ids, "Schema id", "Namespace");
                        ValidateAssert.AreEqual(
                            up.Name,
                            s.Name,
                            $"Schema name '{up.Name}' does not match the name of schema with id '{up.SchemaId}': {s.Name}");
                    }

                    break;
                default:
                    Contract.Fail("Unknown property type");
                    break;
            }
        }

        private static class ValidateAssert
        {
            /// <summary>Validate two values are equal.</summary>
            /// <typeparam name="T">Type of the values to compare.</typeparam>
            /// <param name="left">The left value to compare.</param>
            /// <param name="right">The right value to compare.</param>
            /// <param name="message">Diagnostic message if the comparison fails.</param>
            public static void AreEqual<T>(T left, T right, string message)
            {
                if (!left.Equals(right))
                {
                    throw new SchemaException(message);
                }
            }

            /// <summary>Validate a predicate is true.</summary>
            /// <param name="predicate">The predicate to check.</param>
            /// <param name="message">Diagnostic message if the comparison fails.</param>
            public static void IsTrue(bool predicate, string message)
            {
                if (!predicate)
                {
                    throw new SchemaException(message);
                }
            }

            /// <summary>
            /// Validate <paramref name="identifier" /> contains only characters valid in a schema
            /// identifier.
            /// </summary>
            /// <param name="identifier">The identifier to check.</param>
            /// <param name="label">Diagnostic label describing <paramref name="identifier" />.</param>
            public static void IsValidIdentifier(string identifier, string label)
            {
                if (string.IsNullOrWhiteSpace(identifier))
                {
                    throw new SchemaException($"{label} must be a valid identifier: {identifier}");
                }
            }

            /// <summary>Validate <paramref name="id" /> is a valid <see cref="SchemaId" />.</summary>
            /// <param name="id">The id to check.</param>
            /// <param name="label">Diagnostic label describing <paramref name="id" />.</param>
            public static void IsValidSchemaId(SchemaId id, string label)
            {
                if (id == SchemaId.Invalid)
                {
                    throw new SchemaException($"{label} cannot be 0");
                }
            }

            /// <summary>Validate <paramref name="key" /> does not already appear within the given scope.</summary>
            /// <typeparam name="TKey">The type of the keys within the scope.</typeparam>
            /// <typeparam name="TValue">The type of the values within the scope.</typeparam>
            /// <param name="key">The key to check.</param>
            /// <param name="value">The value to add to the scope if there is no duplicate.</param>
            /// <param name="scope">The set of existing values within the scope.</param>
            /// <param name="label">Diagnostic label describing <paramref name="key" />.</param>
            /// <param name="scopeLabel">Diagnostic label describing <paramref name="scope" />.</param>
            public static void DuplicateCheck<TKey, TValue>(TKey key, TValue value, Dictionary<TKey, TValue> scope, string label, string scopeLabel)
            {
                if (scope.ContainsKey(key))
                {
                    throw new SchemaException($"{label} must be unique within a {scopeLabel}: {key}");
                }

                scope.Add(key, value);
            }

            /// <summary>Validate <paramref name="key" /> does appear within the given scope.</summary>
            /// <typeparam name="TKey">The type of the keys within the scope.</typeparam>
            /// <typeparam name="TValue">The type of the values within the scope.</typeparam>
            /// <param name="key">The key to check.</param>
            /// <param name="scope">The set of existing values within the scope.</param>
            /// <param name="label">Diagnostic label describing <paramref name="key" />.</param>
            /// <param name="scopeLabel">Diagnostic label describing <paramref name="scope" />.</param>
            public static TValue Exists<TKey, TValue>(TKey key, Dictionary<TKey, TValue> scope, string label, string scopeLabel)
            {
                if (!scope.TryGetValue(key, out TValue value))
                {
                    throw new SchemaException($"{label} must exist within a {scopeLabel}: {key}");
                }

                return value;
            }

            /// <summary>Validate <paramref name="key" /> does appear within the given scope.</summary>
            /// <typeparam name="TKey">The type of the keys within the scope.</typeparam>
            /// <param name="key">The key to check.</param>
            /// <param name="scope">The set of existing values within the scope.</param>
            /// <param name="label">Diagnostic label describing <paramref name="key" />.</param>
            /// <param name="scopeLabel">Diagnostic label describing <paramref name="scope" />.</param>
            public static void NotExists<TKey>(TKey key, List<TKey> scope, string label, string scopeLabel)
            {
                if (scope.Contains(key))
                {
                    throw new SchemaException($"{label} must not exist within a {scopeLabel}: {key}");
                }
            }

            /// <summary>Validate if <paramref name="value" /> fits within the base type of <paramref name="es" />.</summary>
            /// <param name="minValue">The min value of the allowable range.</param>
            /// <param name="maxValue">The max value of the allowable range.</param>
            /// <param name="value">The value to check.</param>
            /// <param name="es">The enum being checked.</param>
            public static void IsValidEnumSize(long minValue, long maxValue, EnumValue value, EnumSchema es)
            {
                if ((value.Value < minValue) || (value.Value > maxValue))
                {
                    throw new SchemaException($"{value.Name} ({value.Value}) cannot fit in {es.Name} of type {es.Type}.");
                }
            }

            /// <summary>Validate if <paramref name="value" /> fits within the base type of <paramref name="es" />.</summary>
            /// <param name="minValue">The min value of the allowable range.</param>
            /// <param name="maxValue">The max value of the allowable range.</param>
            /// <param name="value">The value to check.</param>
            /// <param name="es">The enum being checked.</param>
            public static void IsValidEnumUSize(ulong minValue, ulong maxValue, EnumValue value, EnumSchema es)
            {
                ulong uvalue;
                unchecked
                {
                    uvalue = (ulong)value.Value;
                }
                if ((uvalue < minValue) || (uvalue > maxValue))
                {
                    throw new SchemaException($"{value.Name} ({value.Value}) cannot fit in {es.Name} of type {es.Type}.");
                }
            }
        }
    }
}
