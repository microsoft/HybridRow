// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public class HybridRowGeneratorConfig
    {
        /// <summary>The number of attempts to allocate a value given an exclusion list before aborting.</summary>
        private const int ConflictRetryAttemptsDefault = 100;

        /// <summary>
        /// The rate at which the cardinality of substructures decays as a function of depth.  This
        /// ensures that randomly generated values don't become infinitely large.
        /// </summary>
        private const double DepthDecayFactorDefault = -1.0D;

        /// <summary>
        /// The length (in chars) of identifiers including namespace names, schema names, property
        /// names, etc.
        /// </summary>
        private static readonly IntDistribution IdentifierLengthDefault = new IntDistribution(1, 20);

        /// <summary>
        /// The distribution of unicode characters used in constructing identifiers including
        /// namespace names, schema names, property names, etc.
        /// </summary>
        private static readonly CharDistribution IdentifierCharactersDefault = new CharDistribution('a', 'z');

        /// <summary>The length (in chars) of annotation comments within the schema.</summary>
        private static readonly IntDistribution CommentLengthDefault = new IntDistribution(0, 50);

        /// <summary>The length (in chars) of string values.</summary>
        private static readonly IntDistribution StringValueLengthDefault = new IntDistribution(0, 100);

        /// <summary>The length (in bytes) of binary values.</summary>
        private static readonly IntDistribution BinaryValueLengthDefault = new IntDistribution(0, 100);

        /// <summary>The length (in number of elements) of collection scopes.</summary>
        private static readonly IntDistribution CollectionValueLengthDefault = new IntDistribution(0, 10);

        /// <summary>The distribution of unicode characters used in constructing Unicode field values.</summary>
        private static readonly CharDistribution UnicodeCharactersDefault = new CharDistribution('\u0001', char.MaxValue);

        /// <summary>The space of SchemaId values assigned to schemas (table or UDT) within a single namespace.</summary>
        private static readonly IntDistribution SchemaIdsDefault = new IntDistribution(int.MinValue, int.MaxValue);

        /// <summary>The number of properties (i.e. columns, fields) to appear in a table or UDT definition.</summary>
        private static readonly IntDistribution NumTablePropertiesDefault = new IntDistribution(1, 10);

        /// <summary>The number of items to appear in a tuple field.</summary>
        private static readonly IntDistribution NumTupleItemsDefault = new IntDistribution(2, 5);

        /// <summary>The number of items to appear in a tagged field.</summary>
        private static readonly IntDistribution NumTaggedItemsDefault = new IntDistribution(1, 2);

        /// <summary>The length (in units, e.g. chars, bytes, etc.) of variable length primitive field values.</summary>
        private static readonly IntDistribution PrimitiveFieldValueLengthDefault = new IntDistribution(1, 1024);

        /// <summary>The distribution of types for fields.</summary>
        private static readonly IntDistribution FieldTypeDefault = new IntDistribution((int)TypeKind.Null, (int)TypeKind.Schema);

        /// <summary>The distribution of storage for fields.</summary>
        private static readonly IntDistribution FieldStorageDefault = new IntDistribution((int)StorageKind.Sparse, (int)StorageKind.Variable);

        /// <summary>The distribution of initial sizes for RowBuffers.</summary>
        private static readonly IntDistribution RowBufferInitialCapacityDefault = new IntDistribution(0, 2 * 1024 * 1024);

        public IntDistribution IdentifierLength { get; set; } = HybridRowGeneratorConfig.IdentifierLengthDefault;

        public CharDistribution IdentifierCharacters { get; set; } = HybridRowGeneratorConfig.IdentifierCharactersDefault;

        public IntDistribution CommentLength { get; set; } = HybridRowGeneratorConfig.CommentLengthDefault;

        public IntDistribution StringValueLength { get; set; } = HybridRowGeneratorConfig.StringValueLengthDefault;

        public IntDistribution BinaryValueLength { get; set; } = HybridRowGeneratorConfig.BinaryValueLengthDefault;

        public IntDistribution CollectionValueLength { get; set; } = HybridRowGeneratorConfig.CollectionValueLengthDefault;

        public CharDistribution UnicodeCharacters { get; set; } = HybridRowGeneratorConfig.UnicodeCharactersDefault;

        public IntDistribution SchemaIds { get; set; } = HybridRowGeneratorConfig.SchemaIdsDefault;

        public IntDistribution NumTableProperties { get; set; } = HybridRowGeneratorConfig.NumTablePropertiesDefault;

        public IntDistribution NumTupleItems { get; set; } = HybridRowGeneratorConfig.NumTupleItemsDefault;

        public IntDistribution NumTaggedItems { get; set; } = HybridRowGeneratorConfig.NumTaggedItemsDefault;

        public IntDistribution PrimitiveFieldValueLength { get; set; } = HybridRowGeneratorConfig.PrimitiveFieldValueLengthDefault;

        public IntDistribution FieldType { get; set; } = HybridRowGeneratorConfig.FieldTypeDefault;

        public IntDistribution FieldStorage { get; set; } = HybridRowGeneratorConfig.FieldStorageDefault;

        public IntDistribution RowBufferInitialCapacity { get; set; } = HybridRowGeneratorConfig.RowBufferInitialCapacityDefault;

        public int ConflictRetryAttempts { get; set; } = HybridRowGeneratorConfig.ConflictRetryAttemptsDefault;

        public double DepthDecayFactor { get; set; } = HybridRowGeneratorConfig.DepthDecayFactorDefault;
    }
}
