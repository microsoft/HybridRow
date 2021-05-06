// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public class SchemaGenerator
    {
        private readonly RandomGenerator rand;
        private readonly HybridRowGeneratorConfig config;
        private readonly HybridRowValueGenerator generator;

        public SchemaGenerator(RandomGenerator rand, HybridRowGeneratorConfig config, HybridRowValueGenerator generator)
        {
            this.rand = rand;
            this.config = config;
            this.generator = generator;
        }

        public Namespace InitializeRandomNamespace()
        {
            Namespace ns = new Namespace()
            {
                Name = this.generator.GenerateIdentifier(),
            };

            return ns;
        }

        public Schema InitializeRandomSchema(Namespace ns, int depth)
        {
            string name = HybridRowValueGenerator.GenerateExclusive(
                this.generator.GenerateIdentifier,
                from s1 in ns.Schemas select s1.Name,
                this.config.ConflictRetryAttempts);

            SchemaId sid = HybridRowValueGenerator.GenerateExclusive(
                this.generator.GenerateSchemaId,
                from s1 in ns.Schemas select s1.SchemaId,
                this.config.ConflictRetryAttempts);

            // Allocate and insert the schema *before* recursing to allocate properties.  This ensures that nested structure
            // doesn't conflict with name or id constraints.
            Schema s = new Schema()
            {
                Name = name,
                SchemaId = sid,
                Type = TypeKind.Schema,
                Comment = this.generator.GenerateComment(),
                Options = this.InitializeRandomSchemaOptions(),
            };

            ns.Schemas.Add(s);

            // Recurse and allocate its properties.
            s.Properties = this.InitializeRandomProperties(ns, TypeKind.Schema, depth);

            return s;
        }

        private List<Property> InitializeRandomProperties(Namespace ns, TypeKind scope, int depth)
        {
            int length = this.config.NumTableProperties.Next(this.rand);

            // Introduce some decay rate for the number of properties in nested contexts
            // to limit the depth of schemas.
            if (depth > 0)
            {
                double scaled = Math.Floor(length * Math.Exp(depth * this.config.DepthDecayFactor));
                Contract.Assert(scaled <= length);
                length = (int)scaled;
            }

            List<Property> properties = new List<Property>(length);
            for (int i = 0; i < length; i++)
            {
                PropertyType propType = this.InitializeRandomPropertyType(ns, scope, depth);
                string path = HybridRowValueGenerator.GenerateExclusive(
                    this.generator.GenerateIdentifier,
                    from s1 in properties select s1.Path,
                    this.config.ConflictRetryAttempts);
                Property prop = new Property()
                {
                    Comment = this.generator.GenerateComment(),
                    Path = path,
                    PropertyType = propType,
                };

                properties.Add(prop);
            }

            return properties;
        }

        private PropertyType InitializeRandomPropertyType(Namespace ns, TypeKind scope, int depth)
        {
            TypeKind type = this.generator.GenerateTypeKind();
            PropertyType propType;
            switch (type)
            {
                case TypeKind.Object:
                    propType = new ObjectPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),

                        // TODO: add properties to object scopes.
                        // Properties = this.InitializeRandomProperties(ns, type, depth + 1),
                    };

                    break;
                case TypeKind.Array:
                    propType = new ArrayPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Items = this.InitializeRandomPropertyType(ns, type, depth + 1),
                    };

                    break;
                case TypeKind.Set:
                    propType = new SetPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Items = this.InitializeRandomPropertyType(ns, type, depth + 1),
                    };

                    break;
                case TypeKind.Map:
                    propType = new MapPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Keys = this.InitializeRandomPropertyType(ns, type, depth + 1),
                        Values = this.InitializeRandomPropertyType(ns, type, depth + 1),
                    };

                    break;
                case TypeKind.Tuple:
                    int numItems = this.config.NumTupleItems.Next(this.rand);
                    List<PropertyType> itemTypes = new List<PropertyType>(numItems);
                    for (int i = 0; i < numItems; i++)
                    {
                        itemTypes.Add(this.InitializeRandomPropertyType(ns, type, depth + 1));
                    }

                    propType = new TuplePropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Items = itemTypes,
                    };

                    break;
                case TypeKind.Tagged:
                    int numTagged = this.config.NumTaggedItems.Next(this.rand);
                    List<PropertyType> taggedItemTypes = new List<PropertyType>(numTagged);
                    for (int i = 0; i < numTagged; i++)
                    {
                        taggedItemTypes.Add(this.InitializeRandomPropertyType(ns, type, depth + 1));
                    }

                    propType = new TaggedPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Items = taggedItemTypes,
                    };

                    break;
                case TypeKind.Schema:
                    Schema udt = this.InitializeRandomSchema(ns, depth + 1);
                    propType = new UdtPropertyType()
                    {
                        Immutable = this.generator.GenerateBool(),
                        Name = udt.Name,
                    };

                    break;
                default:
                    StorageKind storage = (scope == TypeKind.Schema) ? this.generator.GenerateStorageKind() : StorageKind.Sparse;
                    switch (storage)
                    {
                        case StorageKind.Sparse:
                            // All types are supported in Sparse.
                            break;

                        case StorageKind.Fixed:
                            switch (type)
                            {
                                case TypeKind.Null:
                                case TypeKind.Boolean:
                                case TypeKind.Int8:
                                case TypeKind.Int16:
                                case TypeKind.Int32:
                                case TypeKind.Int64:
                                case TypeKind.UInt8:
                                case TypeKind.UInt16:
                                case TypeKind.UInt32:
                                case TypeKind.UInt64:
                                case TypeKind.Float32:
                                case TypeKind.Float64:
                                case TypeKind.Float128:
                                case TypeKind.Decimal:
                                case TypeKind.DateTime:
                                case TypeKind.UnixDateTime:
                                case TypeKind.Guid:
                                case TypeKind.MongoDbObjectId:
                                case TypeKind.Utf8:
                                case TypeKind.Binary:
                                    // Only these types are supported with fixed storage today.
                                    break;
                                default:
                                    storage = StorageKind.Sparse;
                                    break;
                            }

                            break;
                        case StorageKind.Variable:
                            switch (type)
                            {
                                case TypeKind.Binary:
                                case TypeKind.Utf8:
                                case TypeKind.VarInt:
                                case TypeKind.VarUInt:
                                    // Only these types are supported with variable storage today.
                                    break;

                                default:
                                    storage = StorageKind.Sparse;
                                    break;
                            }

                            break;
                    }

                    propType = new PrimitivePropertyType()
                    {
                        Length = storage == StorageKind.Sparse ? 0 : this.config.PrimitiveFieldValueLength.Next(this.rand),
                        Storage = storage,
                    };

                    break;
            }

            propType.ApiType = this.generator.GenerateIdentifier();
            propType.Type = type;
            switch (scope)
            {
                case TypeKind.Array:
                case TypeKind.Map:
                case TypeKind.Set:
                case TypeKind.Tuple:
                case TypeKind.Tagged:
                    propType.Nullable = this.generator.GenerateBool();
                    break;
                default:
                    propType.Nullable = true;
                    break;
            }

            return propType;
        }

        private SchemaOptions InitializeRandomSchemaOptions()
        {
            SchemaOptions o = new SchemaOptions()
            {
                DisallowUnschematized = this.generator.GenerateBool(),
            };

            return o;
        }
    }
}
