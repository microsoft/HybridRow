// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.serialization.hybridrow.SchemaId;

import java.util.HashMap;

public final class SchemaValidator {
HashMap<SchemaId, Schema> ids
HashMap<SchemaId, Schema> ids>schemas,
    HashMap<SchemaId, Schema> ids)
HashMap<SchemaId, Schema> ids

        {
        for (Schema s : ns.getSchemas()) {
            SchemaValidator.Visit(s, schemas, ids);
        }
    }>schemas,

    {
        ValidateAssert.AreEqual(s.getType(), TypeKind.Schema, String.format("The type of a schema MUST be %1$s: %2$s"
            , TypeKind.Schema, s.getType()));
        HashMap<String, Property> pathDupCheck = new HashMap<String, Property>(s.getProperties().size());
        for (Property p : s.getProperties()) {
            ValidateAssert.DuplicateCheck(p.path(), p, pathDupCheck, "Property path", "Schema");
        }

        for (PartitionKey pk : s.getPartitionKeys()) {
            ValidateAssert.Exists(pk.path(), pathDupCheck, "Partition key column", "Schema");
        }

        for (PrimarySortKey ps : s.getPrimarySortKeys()) {
            ValidateAssert.Exists(ps.path(), pathDupCheck, "Primary sort key column", "Schema");
        }

        for (StaticKey sk : s.getStaticKeys()) {
            ValidateAssert.Exists(sk.path(), pathDupCheck, "Static key column", "Schema");
        }

        for (Property p : s.getProperties()) {
            SchemaValidator.Visit(p, s, schemas, ids);
        }
    })

    {
        ValidateAssert.IsValidIdentifier(p.getPath(), "Property path");
        SchemaValidator.Visit(p.getPropertyType(), null, schemas, ids);
    }

        {
        switch (p) {
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case PrimitivePropertyType pp:
            case PrimitivePropertyType
                pp:
                ValidateAssert.IsTrue(pp.Length >= 0, "Length MUST be positive");
                if (parent != null) {
                    ValidateAssert.AreEqual(pp.Storage, StorageKind.Sparse, String.format("Nested fields MUST have " +
                        "storage %1$s", StorageKind.Sparse));
                }

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case ArrayPropertyType ap:
            case ArrayPropertyType
                ap:
                if (ap.Items != null) {
                    SchemaValidator.Visit(ap.Items, p, schemas, ids);
                }

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case MapPropertyType mp:
            case MapPropertyType
                mp:
                SchemaValidator.Visit(mp.keySet(), p, schemas, ids);
                SchemaValidator.Visit(mp.Values, p, schemas, ids);
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case SetPropertyType sp:
            case SetPropertyType
                sp:
                SchemaValidator.Visit(sp.Items, p, schemas, ids);
                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case TaggedPropertyType gp:
            case TaggedPropertyType
                gp:
                for (PropertyType item : gp.Items) {
                    SchemaValidator.Visit(item, p, schemas, ids);
                }

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case TuplePropertyType tp:
            case TuplePropertyType
                tp:
                for (PropertyType item : tp.Items) {
                    SchemaValidator.Visit(item, p, schemas, ids);
                }

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case ObjectPropertyType op:
            case ObjectPropertyType
                op:
                HashMap<String, Property> pathDupCheck = new HashMap<String, Property>(op.Properties.Count);
                for (Property nested : op.Properties) {
                    ValidateAssert.DuplicateCheck(nested.path(), nested, pathDupCheck, "Property path", "Object");
                    SchemaValidator.Visit(nested.propertyType(), p, schemas, ids);
                }

                break;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to C# pattern variables in 'case' statements:
            //ORIGINAL LINE: case UdtPropertyType up:
            case UdtPropertyType
                up:
                ValidateAssert.Exists((up.Name, up.SchemaId), schemas, "Schema reference", "Namespace")
                if (SchemaId.opNotEquals(up.SchemaId,
                    SchemaId.INVALID)) {
                    Schema s = ValidateAssert.Exists(up.SchemaId, ids, "Schema id", "Namespace");
                    ValidateAssert.AreEqual(up.Name, s.name(), String.format("Schema name '%1$s' does not match " +
                        "the name of schema with id '%2$s': %3$s", up.Name, up.SchemaId, s.name()));
                }

                break;
            default:
                Contract.Fail("Unknown property type");
                break;
        }
    }>schemas,

    public static void Validate(Namespace ns) {
        HashMap<String, Integer> nameVersioningCheck = new HashMap<String, Integer>(ns.schemas().size());
        HashMap< (String, SchemaId),
        Schema > nameDupCheck = new HashMap<(String, SchemaId), Schema > (ns.schemas().size());
        HashMap<SchemaId, Schema> idDupCheck = new HashMap<SchemaId, Schema>(ns.schemas().size());
        for (Schema s : ns.schemas()) {
            ValidateAssert.IsValidSchemaId(s.schemaId().clone(), "Schema id");
            ValidateAssert.IsValidIdentifier(s.name(), "Schema name");
            ValidateAssert.DuplicateCheck(s.schemaId().clone(), s, idDupCheck, "Schema id", "Namespace");
            ValidateAssert.DuplicateCheck((s.name(), s.schemaId().clone()), s, nameDupCheck, "Schema reference"
                , "Namespace")

            // Count the versions of each schema by name.
            int count;
            count = nameVersioningCheck.get(s.name());
            nameVersioningCheck.put(s.name(), count + 1);
        }

        // Enable id-less Schema references for all types with a unique version in the namespace.
        for (Schema s : ns.schemas()) {
            if (nameVersioningCheck.get(s.name()).equals(1)) {
                ValidateAssert.DuplicateCheck((s.name(), SchemaId.INVALID), s, nameDupCheck, "Schema reference",
                    "Namespace")
            }
        }

        SchemaValidator.Visit(ns, nameDupCheck, idDupCheck);
    })

/**
     * Visit an entire namespace and validate its constraints.
     *
     * @param ns      The {@link Namespace} to validate.
     * @param schemas A map from schema names within the namespace to their schemas.
     * @param ids     A map from schema ids within the namespace to their schemas.
     */
    private static void Visit(Namespace ns, HashMap<(String, SchemaId),Schema

    /**
     * Visit a single schema and validate its constraints.
     *
     * @param s       The {@link Schema} to validate.
     * @param schemas A map from schema names within the namespace to their schemas.
     * @param ids     A map from schema ids within the namespace to their schemas.
     */
    private static void Visit(Schema s, HashMap<(String, SchemaId),Schema>schemas,

private static void Visit(Property p, Schema s, HashMap<(String, SchemaId),Schema)

private static void Visit(PropertyType p, PropertyType parent, HashMap<(String, SchemaId),Schema

    private static class ValidateAssert {
        /**
         * Validate two values are equal.
         * <typeparam name="T">Type of the values to compare.</typeparam>
         *
         * @param left    The left value to compare.
         * @param right   The right value to compare.
         * @param message Diagnostic message if the comparison fails.
         */
        public static <T> void AreEqual(T left, T right, String message) {
            if (!left.equals(right)) {
                throw new SchemaException(message);
            }
        }

        /**
         * Validate <paramref name="key" /> does not already appear within the given scope.
         * <typeparam name="TKey">The type of the keys within the scope.</typeparam>
         * <typeparam name="TValue">The type of the values within the scope.</typeparam>
         *
         * @param key        The key to check.
         * @param value      The value to add to the scope if there is no duplicate.
         * @param scope      The set of existing values within the scope.
         * @param label      Diagnostic label describing <paramref name="key" />.
         * @param scopeLabel Diagnostic label describing <paramref name="scope" />.
         */
        public static <TKey, TValue> void DuplicateCheck(TKey key, TValue value, HashMap<TKey, TValue> scope, String label, String scopeLabel) {
            if (scope.containsKey(key)) {
                throw new SchemaException(String.format("%1$s must be unique within a %2$s: %3$s", label, scopeLabel, key));
            }

            scope.put(key, value);
        }

        /**
         * Validate <paramref name="key" /> does appear within the given scope.
         * <typeparam name="TKey">The type of the keys within the scope.</typeparam>
         * <typeparam name="TValue">The type of the values within the scope.</typeparam>
         *
         * @param key        The key to check.
         * @param scope      The set of existing values within the scope.
         * @param label      Diagnostic label describing <paramref name="key" />.
         * @param scopeLabel Diagnostic label describing <paramref name="scope" />.
         */
        public static <TKey, TValue> TValue Exists(TKey key, HashMap<TKey, TValue> scope, String label, String scopeLabel) {
            TValue value;
            if (!(scope.containsKey(key) && (value = scope.get(key)) == value)) {
                throw new SchemaException(String.format("%1$s must exist within a %2$s: %3$s", label, scopeLabel, key));
            }

            return value;
        }

        /**
         * Validate a predicate is true.
         *
         * @param predicate The predicate to check.
         * @param message   Diagnostic message if the comparison fails.
         */
        public static void IsTrue(boolean predicate, String message) {
            if (!predicate) {
                throw new SchemaException(message);
            }
        }

        /**
         * Validate <paramref name="identifier" /> contains only characters valid in a schema
         * identifier.
         *
         * @param identifier The identifier to check.
         * @param label      Diagnostic label describing <paramref name="identifier" />.
         */
        public static void IsValidIdentifier(String identifier, String label) {
            if (tangible.StringHelper.isNullOrWhiteSpace(identifier)) {
                throw new SchemaException(String.format("%1$s must be a valid identifier: %2$s", label, identifier));
            }
        }

        /**
         * Validate <paramref name="id" /> is a valid {@link SchemaId}.
         *
         * @param id    The id to check.
         * @param label Diagnostic label describing <paramref name="id" />.
         */
        public static void IsValidSchemaId(SchemaId id, String label) {
            if (SchemaId.opEquals(id.clone(), SchemaId.INVALID)) {
                throw new SchemaException(String.format("%1$s cannot be 0", label));
            }
        }
    }
}