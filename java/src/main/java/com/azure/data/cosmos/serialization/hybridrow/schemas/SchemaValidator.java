// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.schemas;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.serialization.hybridrow.SchemaId;
import com.google.common.base.Strings;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;

public final class SchemaValidator {

    private static class SchemaIdentification implements Comparable<SchemaIdentification> {

        private final SchemaId id;
        private final String name;

        private SchemaIdentification(@Nonnull final String name, @Nonnull final SchemaId id) {
            checkNotNull(name, "expected non-null name");
            checkNotNull(id, "expected non-null id");
            this.name = name;
            this.id = id;
        }

        @Override
        public int compareTo(@Nonnull SchemaIdentification other) {
            checkNotNull(other, "expected non-null other");
            int result = Integer.compare(this.id.value(), other.id.value());
            return result == 0 ? this.name().compareTo(other.name()) : result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (null == other || this.getClass() != other.getClass()) {
                return false;
            }
            SchemaIdentification that = (SchemaIdentification) other;
            return this.id.equals(that.id) && this.name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.id, this.name);
        }

        public SchemaId id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        @Override
        public String toString() {
            return Json.toString(this);
        }

        public static SchemaIdentification of(@NonNull String name, @NonNull SchemaId id) {
            return new SchemaIdentification(name, id);
        }
    }

    public static void Validate(@NonNull final Namespace namespace) {

        checkNotNull(namespace, "expected non-null namespace");

        final int initialCapacity = namespace.schemas().size();

        final Map<SchemaIdentification, Schema> nameDupCheck = new HashMap<>(initialCapacity);
        final Map<String, Integer> nameVersioningCheck = new HashMap<>(initialCapacity);
        final Map<SchemaId, Schema> idDupCheck = new HashMap<>(initialCapacity);

        for (Schema schema : namespace.schemas()) {

            SchemaIdentification identification = SchemaIdentification.of(schema.name(), schema.schemaId());

            Assert.isValidIdentifier(identification.name(), "Schema name");
            Assert.isValidSchemaId(identification.id(), "Schema id");
            Assert.duplicateCheck(identification.id(), schema, idDupCheck, "Schema id", "Namespace");
            Assert.duplicateCheck(identification, schema, nameDupCheck, "Schema reference", "Namespace");

            // Count the versions of each schema by name.
            nameVersioningCheck.TryGetValue(schema.name(), out int count);
            nameVersioningCheck.put(schema.name(), count + 1);
        }

        // Enable id-less Schema references for all types with a unique version in the namespace

        for (Schema schema : namespace.schemas()) {
            if (nameVersioningCheck.get(schema.name()) == 1) {
                Assert.duplicateCheck(SchemaIdentification.of(schema.name(), SchemaId.INVALID), schema, nameDupCheck, "Schema reference", "Namespace");
            }
        }

        SchemaValidator.visit(namespace, nameDupCheck, idDupCheck);
    }

    /// <summary>Visit an entire namespace and validate its constraints.</summary>
    /// <param name="ns">The <see cref="Namespace" /> to validate.</param>
    /// <param name="schemas">A map from schema names within the namespace to their schemas.</param>
    /// <param name="ids">A map from schema ids within the namespace to their schemas.</param>
    private static void visit(Namespace ns, Map<SchemaIdentification, Schema> schemas, Map<SchemaId, Schema> ids) {
        for (Schema schema : ns.schemas()) {
            SchemaValidator.visit(schema, schemas, ids);
        }
    }

    /// <summary>Visit a single schema and validate its constraints.</summary>
    /// <param name="schema">The <see cref="Schema" /> to validate.</param>
    /// <param name="schemas">A map from schema names within the namespace to their schemas.</param>
    /// <param name="ids">A map from schema ids within the namespace to their schemas.</param>
    private static void visit(Schema schema, Map<SchemaIdentification, Schema> schemas, Map<SchemaId, Schema> ids) {

        Assert.areEqual(
            schema.type(), TypeKind.SCHEMA, lenientFormat("The type of a schema MUST be %s: %s", TypeKind.SCHEMA, schema.type())
        );

        HashMap<String, Property> pathDupCheck = new HashMap<>(schema.properties().size());

        for (Property p : schema.properties()) {
            Assert.duplicateCheck(p.path(), p, pathDupCheck, "Property path", "Schema");
        }

        for (PartitionKey pk : schema.partitionKeys()) {
            Assert.exists(pk.path(), pathDupCheck, "Partition key column", "Schema");
        }

        for (PrimarySortKey ps : schema.primarySortKeys()) {
            Assert.exists(ps.path(), pathDupCheck, "Primary sort key column", "Schema");
        }

        for (StaticKey sk : schema.staticKeys()) {
            Assert.exists(sk.path(), pathDupCheck, "Static key column", "Schema");
        }

        for (Property p : schema.properties()) {
            SchemaValidator.visit(p, schema, schemas, ids);
        }
    }

    private static void visit(
        Property p, Schema s, Map<SchemaIdentification, Schema> schemas, Map<SchemaId, Schema> ids) {

        Assert.isValidIdentifier(p.path(), "Property path");
        SchemaValidator.visit(p.propertyType(), null, schemas, ids);
    }

    private static void visit(
        PropertyType p,
        PropertyType parent,
        Map<SchemaIdentification, Schema> schemas,
        Map<SchemaId, Schema> ids)
    {
        switch (p)
        {
            case PrimitivePropertyType pp:
            Assert.isTrue(pp.Length >= 0, "Length MUST be positive");
                if (parent != null)
                {
                    Assert.areEqual(pp.Storage, StorageKind.Sparse, $"Nested fields MUST have storage {StorageKind.Sparse}");
                }

                break;
            case ArrayPropertyType ap:
            if (ap.Items != null)
            {
                SchemaValidator.visit(ap.Items, p, schemas, ids);
            }

                break;
            case MapPropertyType mp:
            SchemaValidator.visit(mp.Keys, p, schemas, ids);
                SchemaValidator.visit(mp.Values, p, schemas, ids);
                break;
            case SetPropertyType sp:
            SchemaValidator.visit(sp.Items, p, schemas, ids);
                break;
            case TaggedPropertyType gp:
            for (PropertyType item : gp.Items)
            {
                SchemaValidator.visit(item, p, schemas, ids);
            }

            break;
            case TuplePropertyType tp:
            for (PropertyType item : tp.Items)
            {
                SchemaValidator.visit(item, p, schemas, ids);
            }

            break;
            case ObjectPropertyType op:
            Map<String, Property> pathDupCheck = new HashMap<>(op.Properties.Count);
                for (Property nested : op.Properties)
            {
                Assert.duplicateCheck(nested.path(), nested, pathDupCheck, "Property path", "Object");
                SchemaValidator.visit(nested.propertyType(), p, schemas, ids);
            }

            break;
            case UdtPropertyType up:
            Assert.exists((up.Name, up.SchemaId), schemas, "Schema reference", "Namespace");
                if (up.SchemaId != SchemaId.Invalid)
                {
                    Schema s = Assert.exists(up.SchemaId, ids, "Schema id", "Namespace");
                    Assert.areEqual(
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

    private static class Assert {

        /// <summary>Validate <paramref name="key" /> does not already appear within the given scope.</summary>
        /// <typeparam name="TKey">The type of the keys within the scope.</typeparam>
        /// <typeparam name="TValue">The type of the values within the scope.</typeparam>
        /// <param name="key">The key to check.</param>
        /// <param name="value">The value to add to the scope if there is no duplicate.</param>
        /// <param name="scope">The set of existing values within the scope.</param>
        /// <param name="label">Diagnostic label describing <paramref name="key" />.</param>
        /// <param name="scopeLabel">Diagnostic label describing <paramref name="scope" />.</param>
        static <TKey, TValue> void duplicateCheck(
            TKey key, TValue value, Map<TKey, TValue> scope, String label, String scopeLabel) {
            if (scope.containsKey(key)) {
                throw new SchemaException(lenientFormat("%s must be unique within a %s: %s", label, scopeLabel, key));
            }
            scope.put(key, value);
        }

        /// <summary>Validate <paramref name="key" /> does appear within the given scope.</summary>
        /// <typeparam name="TKey">The type of the keys within the scope.</typeparam>
        /// <typeparam name="TValue">The type of the values within the scope.</typeparam>
        /// <param name="key">The key to check.</param>
        /// <param name="scope">The set of existing values within the scope.</param>
        /// <param name="label">Diagnostic label describing <paramref name="key" />.</param>
        /// <param name="scopeLabel">Diagnostic label describing <paramref name="scope" />.</param>
        static <TKey, TValue> TValue exists(TKey key, Map<TKey, TValue> scope, String label, String scopeLabel) {
            TValue value = scope.get(key);
            if (value == null) {
                throw new SchemaException(lenientFormat("%s must exist within a %s: %s", label, scopeLabel, key));
            }
            return value;
        }

        /// <summary>Validate two values are equal.</summary>
        /// <typeparam name="T">Type of the values to compare.</typeparam>
        /// <param name="left">The left value to compare.</param>
        /// <param name="right">The right value to compare.</param>
        /// <param name="message">Diagnostic message if the comparison fails.</param>
        static <T> void areEqual(T left, T right, String message) {
            if (!left.equals(right)) {
                throw new SchemaException(message);
            }
        }

        /// <summary>Validate a predicate is true.</summary>
        /// <param name="predicate">The predicate to check.</param>
        /// <param name="message">Diagnostic message if the comparison fails.</param>
        static void isTrue(boolean predicate, String message) {
            if (!predicate) {
                throw new SchemaException(message);
            }
        }

        /// <summary>
        /// Validate <paramref name="identifier" /> contains only characters valid in a schema
        /// identifier.
        /// </summary>
        /// <param name="identifier">The identifier to check.</param>
        /// <param name="label">Diagnostic label describing <paramref name="identifier" />.</param>
        static void isValidIdentifier(String identifier, String label) {
            if (Strings.isNullOrEmpty(identifier)) {
                throw new SchemaException(lenientFormat("%s must be a valid identifier: %s", label, identifier));
            }
        }

        /// <summary>Validate <paramref name="id" /> is a valid <see cref="SchemaId" />.</summary>
        /// <param name="id">The id to check.</param>
        /// <param name="label">Diagnostic label describing <paramref name="id" />.</param>
        static void isValidSchemaId(SchemaId id, String label) {
            if (id == SchemaId.INVALID) {
                throw new SchemaException(lenientFormat("%s cannot be 0", label));
            }
        }
    }
}