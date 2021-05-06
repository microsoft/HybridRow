// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class SchemaValidatorUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void EnumSchemaValidator()
        {
            Namespace MakeNs()
            {
                return new Namespace
                {
                    Enums = new List<EnumSchema>
                    {
                        new EnumSchema
                        {
                            Name = "MyEnum", Type = TypeKind.Int8,
                            Values = new List<EnumValue>
                            {
                                new EnumValue { Name = "MyValue", Value = 42 }
                            }
                        }
                    }
                };
            }

            void AssertSuccess(string label, Action<Namespace> modify)
            {
                Namespace ns = MakeNs();
                modify(ns);
                try
                {
                    SchemaValidator.Validate(ns);
                }
                catch (SchemaException ex)
                {
                    Assert.Fail($"{label} should not have thrown a validation error {ex}.");
                }
            }

            void AssertError(string label, Action<Namespace> modify)
            {
                Namespace ns = MakeNs();
                modify(ns);
                try
                {
                    SchemaValidator.Validate(ns);
                    Assert.Fail($"{label} should have thrown a validation error.");
                }
                catch (SchemaException ex)
                {
                    Assert.IsNotNull(ex);
                }
            }

            void SetValue(EnumSchema es, TypeKind type, long value)
            {
                es.Type = type;
                es.Values[0].Value = value;
            }

            AssertSuccess("Init", ns => { });
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int8, sbyte.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int8, sbyte.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int16, short.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int16, short.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int32, int.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int32, int.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int64, long.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int64, long.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt8, byte.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt8, byte.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt16, ushort.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt16, ushort.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt32, uint.MinValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt32, uint.MaxValue));
            AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt64, (long)ulong.MinValue));
            unchecked
            {
                AssertSuccess("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt64, (long)ulong.MaxValue));
            }

            AssertError("SDL v2", ns => ns.Version = SchemaLanguageVersion.V1);
            AssertError("Duplicate Enum", ns => ns.Enums.Add(new EnumSchema { Name = "MyEnum", Type = TypeKind.Int8 }));
            AssertError("Duplicate Value", ns => ns.Enums[0].Values.Add(new EnumValue { Name = "MyValue" }));

            // Check that only numeric types are validate base types.
            foreach (TypeKind type in Enum.GetValues(typeof(TypeKind)))
            {
                switch (type)
                {
                    case TypeKind.Int8:
                    case TypeKind.Int16:
                    case TypeKind.Int32:
                    case TypeKind.Int64:
                    case TypeKind.UInt8:
                    case TypeKind.UInt16:
                    case TypeKind.UInt32:
                    case TypeKind.UInt64:
                    case TypeKind.VarInt:
                    case TypeKind.VarUInt:
                        AssertSuccess("Valid base type", ns => ns.Enums[0].Type = type);
                        break;
                    default:
                        AssertError("Invalid base type", ns => ns.Enums[0].Type = type);
                        break;
                }
            }

            AssertError("New Value Fit", ns => ns.Enums[0].Values.Add(new EnumValue { Name = "MyValue", Value = 256 }));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int8, sbyte.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int8, sbyte.MaxValue + 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int16, short.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int16, short.MaxValue + 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int32, (long)int.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.Int32, (long)int.MaxValue + 1));

            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt8, byte.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt8, byte.MaxValue + 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt16, ushort.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt16, ushort.MaxValue + 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt32, (long)uint.MinValue - 1));
            AssertError("Value Fit", ns => SetValue(ns.Enums[0], TypeKind.UInt32, (long)uint.MaxValue + 1));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void RowBufferSizeSchemaValidator()
        {
            Namespace MakeNs()
            {
                return new Namespace
                {
                    Schemas = new List<Schema>
                    {
                        new Schema
                        {
                            Name = "MyType",
                            SchemaId = new SchemaId(1),
                            Properties = new List<Property>
                            {
                                new Property
                                {
                                    Path = "Prop",
                                    PropertyType = new PrimitivePropertyType
                                    {
                                        Type = TypeKind.Int32,
                                        Storage = StorageKind.Fixed,
                                        RowBufferSize = true,
                                    }
                                }
                            }
                        }
                    }
                };
            }

            void AssertSuccess(string label, Action<Namespace> modify)
            {
                Namespace ns = MakeNs();
                modify(ns);
                try
                {
                    SchemaValidator.Validate(ns);
                }
                catch (SchemaException ex)
                {
                    Assert.Fail($"{label} should not have thrown a validation error {ex}.");
                }
            }

            void AssertError(string label, Action<Namespace> modify)
            {
                Namespace ns = MakeNs();
                modify(ns);
                try
                {
                    SchemaValidator.Validate(ns);
                    Assert.Fail($"{label} should have thrown a validation error.");
                }
                catch (SchemaException ex)
                {
                    Assert.IsNotNull(ex);
                }
            }

            AssertSuccess("Init", ns => { });

            void Set(Namespace ns, TypeKind type, StorageKind storage)
            {
                Property p = ns.Schemas[0].Properties[0];
                PrimitivePropertyType pp = p.PropertyType as PrimitivePropertyType;
                pp.Type = type;
                pp.Storage = storage;
            }

            for (TypeKind t = TypeKind.Null; t < TypeKind.Object; t++)
            {
                if (t == TypeKind.Int32)
                {
                    continue;
                }

                // ReSharper disable once AccessToModifiedClosure
                AssertError("Wrong type", ns => Set(ns, t, StorageKind.Fixed));
            }

            AssertError("Wrong storage", ns => Set(ns, TypeKind.Int32, StorageKind.Sparse));
            AssertError("Wrong storage", ns => Set(ns, TypeKind.Int32, StorageKind.Variable));
        }
    }
}
