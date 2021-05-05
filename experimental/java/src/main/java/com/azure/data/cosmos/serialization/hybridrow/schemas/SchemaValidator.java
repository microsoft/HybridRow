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

    public static void validate(@NonNull final Namespace namespace) {

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

            nameVersioningCheck.compute(schema.name(), (name, count) -> count == null ? 1 : count + 1);
        }

        // Enable id-less Schema references for all types with a unique version in the namespace

        for (Schema schema : namespace.schemas()) {
            if (nameVersioningCheck.get(schema.name()) == 1) {
                Assert.duplicateCheck(
                    SchemaIdentification.of(schema.name(), SchemaId.NONE), schema, nameDupCheck,
                    "Schema reference", "Namespace"
                );
            }
        }

        SchemaValidator.visit(namespace, nameDupCheck, idDupCheck);
    }

    /**
     * Visit an entire namespace and validate its constraints.
     *
     * @param namespace The {@link Namespace} to validate.
     * @param schemas   A map from schema names within the namespace to their schemas.
     * @param ids       A map from schema ids within the namespace to their schemas.
     */
    private static void visit(
        Namespace namespace, Map<SchemaIdentification, Schema> schemas, Map<SchemaId, Schema> ids) {
        for (Schema schema : namespace.schemas()) {
            SchemaValidator.visit(schema, schemas, ids);
        }
    }

    /**
     * Visit a single schema and validate its constraints.
     *
     * @param schema  The {@link Schema} to validate.
     * @param schemas A map from schema names within the namespace to their schemas.
     * @param ids     A map from schema ids within the namespace to their schemas.
     */
    private static void visit(Schema schema, Map<SchemaIdentification, Schema> schemas, Map<SchemaId, Schema> ids) {

        Assert.areEqual(
            schema.type(), TypeKind.SCHEMA, lenientFormat("The type of a schema MUST be %s: %s", TypeKind.SCHEMA,
                schema.type())
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
        SchemaValidator.visit(p.type(), null, schemas, ids);
    }

    private static void visit(
        PropertyType p,
        PropertyType parent,
        Map<SchemaIdentification, Schema> schemas,
        Map<SchemaId, Schema> ids) {

        if (p instanceof PrimitivePropertyType) {
            PrimitivePropertyType pp = (PrimitivePropertyType) p;
            Assert.isTrue(pp.length() >= 0, "Length MUST be positive");
            if (parent != null) {
                Assert.areEqual(pp.storage(), StorageKind.SPARSE, "Nested fields MUST have storage kind SPARSE");
            }
            return;
        }
        if (p instanceof ArrayPropertyType) {
            ArrayPropertyType ap = (ArrayPropertyType) p;
            if (ap.items() != null) {
                SchemaValidator.visit(ap.items(), p, schemas, ids);
            }
            return;
        }
        if (p instanceof MapPropertyType) {
            MapPropertyType mp = (MapPropertyType) p;
            SchemaValidator.visit(mp.keys(), p, schemas, ids);
            SchemaValidator.visit(mp.values(), p, schemas, ids);
            return;
        }
        if (p instanceof SetPropertyType) {
            SetPropertyType sp = (SetPropertyType) p;
            SchemaValidator.visit(sp.items(), p, schemas, ids);
            return;
        }
        if (p instanceof TaggedPropertyType) {
            TaggedPropertyType gp = (TaggedPropertyType) p;
            for (PropertyType item : gp.items()) {
                SchemaValidator.visit(item, p, schemas, ids);
            }
            return;
        }
        if (p instanceof TuplePropertyType) {
            TuplePropertyType tp = (TuplePropertyType) p;
            for (PropertyType item : tp.items()) {
                SchemaValidator.visit(item, p, schemas, ids);
            }
            return;
        }
        if (p instanceof ObjectPropertyType) {
            ObjectPropertyType op = (ObjectPropertyType) p;
            Map<String, Property> pathDupCheck = new HashMap<>(op.properties().size());
            for (Property nested : op.properties()) {
                Assert.duplicateCheck(nested.path(), nested, pathDupCheck, "Property path", "Object");
                SchemaValidator.visit(nested.type(), p, schemas, ids);
            }
            return;
        }
        if (p instanceof UdtPropertyType) {
            UdtPropertyType up = (UdtPropertyType) p;
            Assert.exists(SchemaIdentification.of(up.name(), up.schemaId()), schemas, "Schema reference", "Namespace");
            if (up.schemaId() != SchemaId.INVALID) {
                Schema s = Assert.exists(up.schemaId(), ids, "Schema id", "Namespace");
                Assert.areEqual(up.name(), s.name(), lenientFormat("Schema name '%s' does not match the name of " +
                    "schema with id '%s': %s", up.name(), up.schemaId(), s.name()));
            }
            return;
        }
        throw new IllegalStateException(lenientFormat("Unknown property type: %s", p.getClass()));
    }

    private static class Assert {

        /**
         * Validate two values are equal.
         *
         * @param <T>     Type of the values to compare.
         * @param left    The left value to compare.
         * @param right   The right value to compare.
         * @param message Diagnostic message if the comparison fails.
         */
        static <T> void areEqual(T left, T right, String message) {
            if (!left.equals(right)) {
                throw new SchemaException(message);
            }
        }

        /**
         * Validate {@code key} does not already appear within the given scope.
         *
         * @param <TKey>     The type of the keys within the scope.
         * @param <TValue>   The type of the values within the scope.
         * @param key        The key to check.
         * @param value      The value to add to the scope if there is no duplicate.
         * @param scope      The set of existing values within the scope.
         * @param label      Diagnostic label describing {@code key}.
         * @param scopeLabel Diagnostic label describing {@code scope}.
         */
        static <TKey, TValue> void duplicateCheck(
            TKey key, TValue value, Map<TKey, TValue> scope, String label, String scopeLabel) {
            if (scope.containsKey(key)) {
                throw new SchemaException(lenientFormat("%s must be unique within a %s: %s", label, scopeLabel, key));
            }
            scope.put(key, value);
        }

        /**
         * Validate {@code key} does appear within the given scope.
         *
         * @param <TKey>     The type of the keys within the scope.
         * @param <TValue>   The type of the values within the scope.
         * @param key        The key to check.
         * @param scope      The set of existing values within the scope.
         * @param label      Diagnostic label describing {@code key}.
         * @param scopeLabel Diagnostic label describing {@code scope}.
         */
        static <TKey, TValue> TValue exists(TKey key, Map<TKey, TValue> scope, String label, String scopeLabel) {
            TValue value = scope.get(key);
            if (value == null) {
                throw new SchemaException(lenientFormat("%s must exist within a %s: %s", label, scopeLabel, key));
            }
            return value;
        }

        /**
         * Validate a predicate is true.
         *
         * @param predicate The predicate to check.
         * @param message   Diagnostic message if the comparison fails.
         */
        static void isTrue(boolean predicate, String message) {
            if (!predicate) {
                throw new SchemaException(message);
            }
        }

        /**
         * Validate {@code identifier} contains only characters valid in a schema identifier.
         *
         * @param identifier The identifier to check.
         * @param label      Diagnostic label describing {@code identifier}.
         */
        static void isValidIdentifier(String identifier, String label) {
            if (Strings.isNullOrEmpty(identifier)) {
                throw new SchemaException(lenientFormat("%s must be a valid identifier: %s", label, identifier));
            }
        }

        /**
         * Validate a {@link SchemaId}.
         *
         * @param id    The id to check.
         * @param label Diagnostic label describing {@code id}.
         */
        static void isValidSchemaId(SchemaId id, String label) {
            if (id == SchemaId.INVALID) {
                throw new SchemaException(lenientFormat("%s cannot be 0", label));
            }
        }
    }

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

        public static SchemaIdentification of(@NonNull String name, @NonNull SchemaId id) {
            return new SchemaIdentification(name, id);
        }

        @Override
        public String toString() {
            return Json.toString(this);
        }
    }
}