// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

// ReSharper disable CommentTypo
// ReSharper disable StringLiteralTypo
#pragma warning disable SA1201 // Elements should appear in the correct order
#pragma warning disable SA1401 // Fields should be private
#pragma warning disable IDE0008 // Use explicit type

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Text;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [SuppressMessage("Naming", "DontUseVarForVariableTypes", Justification = "The types here are anonymous.")]
    public class LayoutCompilerUnitTests
    {
        private const int InitialRowSize = 2 * 1024 * 1024;

        [TestMethod]
        [Owner("jthunter")]
        public void PackNullAndBoolBits()
        {
            // Test that null bits and bool bits are packed tightly in the layout.
            Schema s = new Schema { Name = "TestSchema", SchemaId = new SchemaId(1), Type = TypeKind.Schema };
            for (int i = 0; i < 32; i++)
            {
                s.Properties.Add(
                    new Property
                    {
                        Path = i.ToString(),
                        PropertyType = new PrimitivePropertyType { Type = TypeKind.Boolean, Storage = StorageKind.Fixed },
                    });

                Layout layout = s.Compile(new Namespace { Schemas = new List<Schema> { s } });
                Assert.IsTrue(layout.Size == LayoutBit.DivCeiling((i + 1) * 2, LayoutType.BitsPerByte), "Size: {0}, i: {1}", layout.Size, i);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaFixed()
        {
            // Test all fixed column types.
            RoundTripFixed.Expected[] expectedSchemas = new[]
            {
                new RoundTripFixed.Expected { TypeName = "null", Default = default(NullValue), Value = NullValue.Default },
                new RoundTripFixed.Expected { TypeName = "bool", Default = default(bool), Value = false },

                new RoundTripFixed.Expected { TypeName = "int8", Default = default(sbyte), Value = (sbyte)42 },
                new RoundTripFixed.Expected { TypeName = "int16", Default = default(short), Value = (short)42 },
                new RoundTripFixed.Expected { TypeName = "int32", Default = default(int), Value = 42 },
                new RoundTripFixed.Expected { TypeName = "int64", Default = default(long), Value = 42L },
                new RoundTripFixed.Expected { TypeName = "uint8", Default = default(byte), Value = (byte)42 },
                new RoundTripFixed.Expected { TypeName = "uint16", Default = default(ushort), Value = (ushort)42 },
                new RoundTripFixed.Expected { TypeName = "uint32", Default = default(uint), Value = 42U },
                new RoundTripFixed.Expected { TypeName = "uint64", Default = default(ulong), Value = 42UL },

                new RoundTripFixed.Expected { TypeName = "float32", Default = default(float), Value = 4.2F },
                new RoundTripFixed.Expected { TypeName = "float64", Default = default(double), Value = 4.2 },
                new RoundTripFixed.Expected { TypeName = "float128", Default = default(Float128), Value = new Float128(0, 42) },
                new RoundTripFixed.Expected { TypeName = "decimal", Default = default(decimal), Value = 4.2M },

                new RoundTripFixed.Expected { TypeName = "datetime", Default = default(DateTime), Value = DateTime.UtcNow },
                new RoundTripFixed.Expected { TypeName = "unixdatetime", Default = default(UnixDateTime), Value = new UnixDateTime(42) },

                new RoundTripFixed.Expected { TypeName = "guid", Default = default(Guid), Value = Guid.NewGuid() },
                new RoundTripFixed.Expected { TypeName = "mongodbobjectid", Default = default(MongoDbObjectId), Value = new MongoDbObjectId(0, 42) },

                new RoundTripFixed.Expected { TypeName = "utf8", Default = "\0\0", Value = "AB", Length = 2 },
                new RoundTripFixed.Expected
                {
                    TypeName = "binary",
                    Default = new byte[] { 0x00, 0x00 },
                    Value = new byte[] { 0x01, 0x02 },
                    Length = 2,
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (string nullable in new[] { "true", "false" })
            {
                foreach (RoundTripFixed.Expected exp in expectedSchemas)
                {
                    RoundTripFixed.Expected expected = exp;
                    string typeSchema = $@"{{'type': '{expected.TypeName}', 'storage': 'fixed', 'length': {expected.Length}, 'nullable': {nullable}}}";
                    expected.Json = typeSchema;
                    string propSchema = $@"{{'path': 'a', 'type': {typeSchema}}}";
                    string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{propSchema}] }}";
                    try
                    {
                        Schema s = Schema.Parse(tableSchema);
                        LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                        Layout layout = resolver.Resolve(new SchemaId(-1));
                        Assert.AreEqual(1, layout.Columns.Length, "Json: {0}", expected.Json);
                        Assert.AreEqual(s.Name, layout.Name, "Json: {0}", expected.Json);
                        Assert.IsTrue(layout.ToString().Length > 0, "Json: {0}", expected.Json);
                        bool found = layout.TryFind("a", out LayoutColumn col);
                        Assert.IsTrue(found, "Json: {0}", expected.Json);
                        Assert.AreEqual(StorageKind.Fixed, col.Storage, "Json: {0}", expected.Json);
                        Assert.AreEqual(expected.Length == 0, col.Type.IsFixed, "Json: {0}", expected.Json);

                        // Try writing a row using the layout.
                        row.Reset();
                        row.InitLayout(HybridRowVersion.V1, layout, resolver);

                        HybridRowHeader header = row.Header;
                        Assert.AreEqual(HybridRowVersion.V1, header.Version);
                        Assert.AreEqual(layout.SchemaId, header.SchemaId);

                        RowCursor root = RowCursor.Create(ref row);
                        this.LayoutCodeSwitch<RoundTripFixed, RoundTripFixed.Closure>(
                            col.Type.LayoutCode,
                            ref row,
                            ref root,
                            new RoundTripFixed.Closure { Col = col, Expected = expected });
                    }
                    catch (LayoutCompilationException)
                    {
                        Assert.AreEqual(expected.TypeName, "null");
                        Assert.AreEqual("false", nullable);
                    }
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaVariable()
        {
            // Helper functions to create sample arrays.
            string MakeS(int size)
            {
                StringBuilder ret = new StringBuilder(size);
                for (int i = 0; i < size; i++)
                {
                    ret.Append(unchecked((char)('a' + (i % 26)))); // allow wrapping (this also allows \0 chars)
                }

                return ret.ToString();
            }

            byte[] MakeB(int size)
            {
                byte[] ret = new byte[size];
                for (int i = 0; i < size; i++)
                {
                    ret[i] = unchecked((byte)(i + 1)); // allow wrapping
                }

                return ret;
            }

            // Test all variable column types.
            RoundTripVariable.Expected[] expectedSchemas = new[]
            {
                new RoundTripVariable.Expected
                {
                    Json = @"{'type': 'utf8', 'storage': 'variable', 'length': 100}",
                    Short = MakeS(2),
                    Value = MakeS(20),
                    Long = MakeS(100),
                    TooBig = MakeS(200),
                },
                new RoundTripVariable.Expected
                {
                    Json = @"{'type': 'binary', 'storage': 'variable', 'length': 100}",
                    Short = MakeB(2),
                    Value = MakeB(20),
                    Long = MakeB(100),
                    TooBig = MakeB(200),
                },
                new RoundTripVariable.Expected { Json = @"{'type': 'varint', 'storage': 'variable'}", Short = 1L, Value = 255L, Long = long.MaxValue },
                new RoundTripVariable.Expected { Json = @"{'type': 'varuint', 'storage': 'variable'}", Short = 1UL, Value = 255UL, Long = ulong.MaxValue },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripVariable.Expected expected in expectedSchemas)
            {
                string propSchema = $@"{{'path': 'a', 'type': {expected.Json}}}, 
                                       {{'path': 'b', 'type': {expected.Json}}}, 
                                       {{'path': 'c', 'type': {expected.Json}}}";

                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{propSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                Layout layout = resolver.Resolve(new SchemaId(-1));
                bool found = layout.TryFind("a", out LayoutColumn col);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.IsTrue(col.Type.AllowVariable);
                Assert.AreEqual(StorageKind.Variable, col.Storage, "Json: {0}", expected.Json);

                // Try writing a row using the layout.
                row.Reset();
                row.InitLayout(HybridRowVersion.V1, layout, resolver);

                HybridRowHeader header = row.Header;
                Assert.AreEqual(HybridRowVersion.V1, header.Version);
                Assert.AreEqual(layout.SchemaId, header.SchemaId);

                RowCursor root = RowCursor.Create(ref row);
                this.LayoutCodeSwitch<VariableInterleaving, RoundTripVariable.Closure>(
                    col.Type.LayoutCode, ref row, ref root, new RoundTripVariable.Closure { Layout = layout, Col = col, Expected = expected });
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void SparseOrdering()
        {
            // Test various orderings of multiple sparse column types.
            RoundTripSparseOrdering.Expected[][] expectedOrders = new[]
            {
                new[]
                {
                    new RoundTripSparseOrdering.Expected { Path = "a", Type = LayoutType.Utf8, Value = "aa" },
                    new RoundTripSparseOrdering.Expected { Path = "b", Type = LayoutType.Utf8, Value = "bb" },
                },
                new[]
                {
                    new RoundTripSparseOrdering.Expected { Path = "a", Type = LayoutType.VarInt, Value = 42L },
                    new RoundTripSparseOrdering.Expected { Path = "b", Type = LayoutType.Int64, Value = 43L },
                },
                new[]
                {
                    new RoundTripSparseOrdering.Expected { Path = "a", Type = LayoutType.VarInt, Value = 42L },
                    new RoundTripSparseOrdering.Expected { Path = "b", Type = LayoutType.Utf8, Value = "aa" },
                    new RoundTripSparseOrdering.Expected { Path = "c", Type = LayoutType.Null, Value = NullValue.Default },
                    new RoundTripSparseOrdering.Expected { Path = "d", Type = LayoutType.Boolean, Value = true },
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseOrdering.Expected[] expectedSet in expectedOrders)
            {
                foreach (IEnumerable<RoundTripSparseOrdering.Expected> permutation in expectedSet.Permute())
                {
                    string json = string.Join(", ", from p in permutation select p.Path + ": " + p.Type.Name);
                    Console.WriteLine("{0}", json);

                    row.Reset();
                    row.InitLayout(HybridRowVersion.V1, Layout.Empty, SystemSchema.LayoutResolver);
                    foreach (RoundTripSparseOrdering.Expected field in permutation)
                    {
                        RowCursor root = RowCursor.Create(ref row);
                        this.LayoutCodeSwitch<RoundTripSparseOrdering, RoundTripSparseOrdering.Closure>(
                            field.Type.LayoutCode, ref row, ref root, new RoundTripSparseOrdering.Closure { Expected = field, Json = json });
                    }
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaSparseSimple()
        {
            // Test all sparse column types.
            RoundTripSparseSimple.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'null', 'storage': 'sparse'}", Value = NullValue.Default },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'bool', 'storage': 'sparse'}", Value = true },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'bool', 'storage': 'sparse'}", Value = false },

                new RoundTripSparseSimple.Expected { Json = @"{'type': 'int8', 'storage': 'sparse'}", Value = (sbyte)42 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'int16', 'storage': 'sparse'}", Value = (short)42 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'int32', 'storage': 'sparse'}", Value = 42 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'int64', 'storage': 'sparse'}", Value = 42L },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'uint8', 'storage': 'sparse'}", Value = (byte)42 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'uint16', 'storage': 'sparse'}", Value = (ushort)42 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'uint32', 'storage': 'sparse'}", Value = 42U },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'uint64', 'storage': 'sparse'}", Value = 42UL },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'varint', 'storage': 'sparse'}", Value = 42L },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'varuint', 'storage': 'sparse'}", Value = 42UL },

                new RoundTripSparseSimple.Expected { Json = @"{'type': 'float32', 'storage': 'sparse'}", Value = 4.2F },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'float64', 'storage': 'sparse'}", Value = 4.2 },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'float128', 'storage': 'sparse'}", Value = new Float128(0, 42) },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'decimal', 'storage': 'sparse'}", Value = 4.2M },

                new RoundTripSparseSimple.Expected { Json = @"{'type': 'datetime', 'storage': 'sparse'}", Value = DateTime.UtcNow },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'unixdatetime', 'storage': 'sparse'}", Value = new UnixDateTime(42) },

                new RoundTripSparseSimple.Expected { Json = @"{'type': 'guid', 'storage': 'sparse'}", Value = Guid.NewGuid() },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'mongodbobjectid', 'storage': 'sparse'}", Value = new MongoDbObjectId(0, 42) },

                new RoundTripSparseSimple.Expected { Json = @"{'type': 'utf8', 'storage': 'sparse'}", Value = "AB" },
                new RoundTripSparseSimple.Expected { Json = @"{'type': 'binary', 'storage': 'sparse'}", Value = new byte[] { 0x01, 0x02 } },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseSimple.Expected expected in expectedSchemas)
            {
                string propSchema = $@"{{'path': 'a', 'type': {expected.Json}}}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{propSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                Layout layout = resolver.Resolve(new SchemaId(-1));
                Assert.AreEqual(1, layout.Columns.Length, "Json: {0}", expected.Json);
                bool found = layout.TryFind("a", out LayoutColumn col);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, col.Storage, "Json: {0}", expected.Json);

                // Try writing a row using the layout.
                row.Reset();
                row.InitLayout(HybridRowVersion.V1, layout, resolver);

                HybridRowHeader header = row.Header;
                Assert.AreEqual(HybridRowVersion.V1, header.Version);
                Assert.AreEqual(layout.SchemaId, header.SchemaId);

                RowCursor root = RowCursor.Create(ref row);
                this.LayoutCodeSwitch<RoundTripSparseSimple, RoundTripSparseSimple.Closure>(
                    col.Type.LayoutCode, ref row, ref root, new RoundTripSparseSimple.Closure { Col = col, Expected = expected });
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaUDT()
        {
            string namespaceJson = @"
                {'name': 'myNamespace', 
                 'schemas': [
                    {'name': 'udtA', 'id': 1, 'type': 'schema', 'options': { 'disallowUnschematized': false }, 
                     'properties': [ 
                        { 'path': 'a', 'type': { 'type': 'int8', 'storage': 'fixed' }}, 
                        { 'path': 'b', 'type': { 'type': 'utf8', 'storage': 'variable', 'length': 100 }} 
                     ]
                    },
                    {'name': 'udtB', 'id': 2, 'type': 'schema'},
                    {'name': 'udtB', 'id': 3, 'type': 'schema'},
                    {'name': 'udtB', 'id': 4, 'type': 'schema'},
                    {'name': 'table', 'id': -1, 'type': 'schema', 
                     'properties': [
                        { 'path': 'u', 'type': { 'type': 'schema', 'name': 'udtA' }}, 
                        { 'path': 'v', 'type': { 'type': 'schema', 'name': 'udtB', 'id': 3 }}, 
                     ] 
                    }
                 ]
                }";

            Namespace n1 = Namespace.Parse(namespaceJson);

            string tag = $"Json: {namespaceJson}";

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            Schema s = n1.Schemas.Find(x => x.Name == "table");
            Assert.IsNotNull(s);
            Assert.AreEqual("table", s.Name);
            Layout layout = s.Compile(n1);
            bool found = layout.TryFind("u", out LayoutColumn udtACol);
            Assert.IsTrue(found, tag);
            Assert.AreEqual(StorageKind.Sparse, udtACol.Storage, tag);

            Schema udtASchema = n1.Schemas.Find(x => x.SchemaId == udtACol.TypeArgs.SchemaId);
            Assert.IsNotNull(udtASchema);
            Assert.AreEqual("udtA", udtASchema.Name);

            // Verify that UDT versioning works through schema references.
            found = layout.TryFind("v", out LayoutColumn udtBCol);
            Assert.IsTrue(found, tag);
            Assert.AreEqual(StorageKind.Sparse, udtBCol.Storage, tag);
            Schema udtBSchema = n1.Schemas.Find(x => x.SchemaId == udtBCol.TypeArgs.SchemaId);
            Assert.IsNotNull(udtBSchema);
            Assert.AreEqual("udtB", udtBSchema.Name);
            Assert.AreEqual(new SchemaId(3), udtBSchema.SchemaId);

            LayoutResolver resolver = new LayoutResolverNamespace(n1);
            Layout udtLayout = resolver.Resolve(udtASchema.SchemaId);
            row.Reset();
            row.InitLayout(HybridRowVersion.V1, layout, resolver);

            HybridRowHeader header = row.Header;
            Assert.AreEqual(HybridRowVersion.V1, header.Version);
            Assert.AreEqual(layout.SchemaId, header.SchemaId);

            // Verify the udt doesn't yet exist.
            RowCursor.Create(ref row, out RowCursor scope).Find(ref row, udtACol.Path);
            Result r = LayoutType.UDT.ReadScope(ref row, ref scope, out _);
            ResultAssert.NotFound(r, tag);
            r = LayoutType.UDT.WriteScope(ref row, ref scope, udtACol.TypeArgs, out RowCursor udtScope1);
            ResultAssert.IsSuccess(r, tag);
            r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor udtScope2);
            ResultAssert.IsSuccess(r, tag);
            Assert.AreSame(udtLayout, udtScope2.Layout, tag);
            Assert.AreEqual(udtScope1.ScopeType, udtScope2.ScopeType, tag);
            Assert.AreEqual(udtScope1.start, udtScope2.start, tag); 
            Assert.AreEqual(udtScope1.Immutable, udtScope2.Immutable, tag);

            var expectedSchemas = new[]
            {
                new
                {
                    Storage = StorageKind.Fixed,
                    Path = "a",
                    FixedExpected = new RoundTripFixed.Expected { Json = @"{ 'type': 'int8', 'storage': 'fixed' }", Value = (sbyte)42 },
                    VariableExpected = default(RoundTripVariable.Expected),
                },
                new
                {
                    Storage = StorageKind.Variable,
                    Path = "b",
                    FixedExpected = default(RoundTripFixed.Expected),
                    VariableExpected = new RoundTripVariable.Expected { Json = @"{ 'type': 'utf8', 'storage': 'variable' }", Value = "AB" },
                },
            };

            foreach (var expected in expectedSchemas)
            {
                found = udtLayout.TryFind(expected.Path, out LayoutColumn col);
                Assert.IsTrue(found, "Path: {0}", expected.Path);
                StorageKind storage = expected.Storage;
                switch (storage)
                {
                    case StorageKind.Fixed:
                        this.LayoutCodeSwitch<RoundTripFixed, RoundTripFixed.Closure>(
                            col.Type.LayoutCode, ref row, ref udtScope1, new RoundTripFixed.Closure
                            {
                                Col = col,
                                Expected = expected.FixedExpected,
                            });
                        break;
                    case StorageKind.Variable:
                        this.LayoutCodeSwitch<RoundTripVariable, RoundTripVariable.Closure>(
                            col.Type.LayoutCode, ref row, ref udtScope1, new RoundTripVariable.Closure
                            {
                                Col = col, Layout = layout,
                                Expected = expected.VariableExpected,
                            });
                        break;
                }
            }

            RowCursor.Create(ref row).AsReadOnly(out RowCursor roRoot).Find(ref row, udtACol.Path);
            ResultAssert.InsufficientPermissions(udtACol.TypeAs<LayoutUDT>().DeleteScope(ref row, ref roRoot));
            ResultAssert.InsufficientPermissions(udtACol.TypeAs<LayoutUDT>().WriteScope(ref row, ref roRoot, udtACol.TypeArgs, out udtScope2));

            // Overwrite the whole scope.
            RowCursor.Create(ref row, out scope).Find(ref row, udtACol.Path);
            r = LayoutType.Null.WriteSparse(ref row, ref scope, NullValue.Default);
            ResultAssert.IsSuccess(r, tag);
            r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor _);
            ResultAssert.TypeMismatch(r, tag);
            r = LayoutType.UDT.DeleteScope(ref row, ref scope);
            ResultAssert.TypeMismatch(r, tag);

            // Overwrite it again, then delete it.
            RowCursor.Create(ref row, out scope).Find(ref row, udtACol.Path);
            r = LayoutType.UDT.WriteScope(ref row, ref scope, udtACol.TypeArgs, out RowCursor _);
            ResultAssert.IsSuccess(r, tag);
            r = LayoutType.UDT.DeleteScope(ref row, ref scope);
            ResultAssert.IsSuccess(r, tag);
            RowCursor.Create(ref row, out scope).Find(ref row, udtACol.Path);
            r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor _);
            ResultAssert.NotFound(r, tag);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaSparseObject()
        {
            // Test all fixed column types.
            RoundTripSparseObject.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseObject.Expected { Json = @"{'path': 'b', 'type': {'type': 'int8'}}", Value = (sbyte)42 },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseObject.Expected expected in expectedSchemas)
            {
                string objectColumnSchema = $"{{'path': 'a', 'type': {{'type': 'object', 'properties': [{expected.Json}] }} }}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{objectColumnSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                Layout layout = resolver.Resolve(new SchemaId(-1));
                Assert.AreEqual(1, layout.Columns.Length, "Json: {0}", expected.Json);
                bool found = layout.TryFind("a", out LayoutColumn objCol);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, objCol.Storage, "Json: {0}", expected.Json);
                found = layout.TryFind("a.b", out LayoutColumn col);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, col.Storage, "Json: {0}", expected.Json);

                // Try writing a row using the layout.
                row.Reset();
                row.InitLayout(HybridRowVersion.V1, layout, resolver);

                HybridRowHeader header = row.Header;
                Assert.AreEqual(HybridRowVersion.V1, header.Version);
                Assert.AreEqual(layout.SchemaId, header.SchemaId);

                RowCursor root = RowCursor.Create(ref row);
                this.LayoutCodeSwitch<RoundTripSparseObject, RoundTripSparseObject.Closure>(
                    col.Type.LayoutCode, ref row, ref root, new RoundTripSparseObject.Closure
                    {
                        ObjCol = objCol,
                        Col = col,
                        Expected = expected,
                    });
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaSparseObjectMulti()
        {
            // Test sparse object columns with various kinds of sparse column fields.
            RoundTripSparseObjectMulti.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseObjectMulti.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'int8'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectMulti.Property { Path = "a.b", Value = (sbyte)42 },
                    },
                },
                new RoundTripSparseObjectMulti.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectMulti.Property { Path = "a.b", Value = (sbyte)42 },
                        new RoundTripSparseObjectMulti.Property { Path = "a.c", Value = "abc" },
                    },
                },
                new RoundTripSparseObjectMulti.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'int8'}}, 
                             {'path': 'c', 'type': {'type': 'bool'}},
                             {'path': 'd', 'type': {'type': 'binary'}},
                             {'path': 'e', 'type': {'type': 'null'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectMulti.Property { Path = "a.b", Value = (sbyte)42 },
                        new RoundTripSparseObjectMulti.Property { Path = "a.c", Value = true },
                        new RoundTripSparseObjectMulti.Property { Path = "a.d", Value = new byte[] { 0x01, 0x02, 0x03 } },
                        new RoundTripSparseObjectMulti.Property { Path = "a.e", Value = NullValue.Default },
                    },
                },
                new RoundTripSparseObjectMulti.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'object'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectMulti.Property { Path = "a.b" },
                    },
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseObjectMulti.Expected expected in expectedSchemas)
            {
                string objectColumnSchema = $"{{'path': 'a', 'type': {{'type': 'object', 'properties': [{expected.Json}] }} }}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{objectColumnSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                Layout layout = resolver.Resolve(new SchemaId(-1));
                Assert.AreEqual(1, layout.Columns.Length, "Json: {0}", expected.Json);
                bool found = layout.TryFind("a", out LayoutColumn objCol);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, objCol.Storage, "Json: {0}", expected.Json);

                // Try writing a row using the layout.
                row.Reset();
                row.InitLayout(HybridRowVersion.V1, layout, resolver);

                HybridRowHeader header = row.Header;
                Assert.AreEqual(HybridRowVersion.V1, header.Version);
                Assert.AreEqual(layout.SchemaId, header.SchemaId);

                // Verify the object doesn't exist.
                LayoutObject objT = objCol.Type as LayoutObject;
                Assert.IsNotNull(objT);
                RowCursor.Create(ref row, out RowCursor field).Find(ref row, objCol.Path);
                Result r = objT.ReadScope(ref row, ref field, out RowCursor scope);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);

                // Write the object and the nested column.
                r = objT.WriteScope(ref row, ref field, objCol.TypeArgs, out scope);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

                foreach (IEnumerable<RoundTripSparseObjectMulti.Property> permutation in expected.Props.Permute())
                {
                    foreach (RoundTripSparseObjectMulti.Property prop in permutation)
                    {
                        found = layout.TryFind(prop.Path, out LayoutColumn col);
                        Assert.IsTrue(found, "Json: {0}", expected.Json);
                        Assert.AreEqual(StorageKind.Sparse, col.Storage, "Json: {0}", expected.Json);

                        this.LayoutCodeSwitch<RoundTripSparseObjectMulti, RoundTripSparseObjectMulti.Closure>(
                            col.Type.LayoutCode, ref row, ref scope, new RoundTripSparseObjectMulti.Closure
                            {
                                Col = col,
                                Prop = prop,
                                Expected = expected,
                            });
                    }
                }

                // Write something after the scope.
                UtfAnyString otherColumnPath = "not-" + objCol.Path;
                field.Clone(out RowCursor otherColumn).Find(ref row, otherColumnPath);
                r = LayoutType.Boolean.WriteSparse(ref row, ref otherColumn, true);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

                // Overwrite the whole scope.
                r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = objT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

                // Read the thing after the scope and verify it is still there.
                field.Clone(out otherColumn).Find(ref row, otherColumnPath);
                r = LayoutType.Boolean.ReadSparse(ref row, ref otherColumn, out bool notScope);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                Assert.IsTrue(notScope);
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaSparseObjectNested()
        {
            // Test nested sparse object columns with various kinds of sparse column fields.
            RoundTripSparseObjectNested.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseObjectNested.Expected
                {
                    Json = @"{'path': 'c', 'type': {'type': 'int8'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectNested.Property { Path = "a.b.c", Value = (sbyte)42 },
                    },
                },
                new RoundTripSparseObjectNested.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'int8'}}, {'path': 'c', 'type': {'type': 'utf8'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectNested.Property { Path = "a.b.b", Value = (sbyte)42 },
                        new RoundTripSparseObjectNested.Property { Path = "a.b.c", Value = "abc" },
                    },
                },
                new RoundTripSparseObjectNested.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'int8'}}, 
                             {'path': 'c', 'type': {'type': 'bool'}},
                             {'path': 'd', 'type': {'type': 'binary'}},
                             {'path': 'e', 'type': {'type': 'null'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectNested.Property { Path = "a.b.b", Value = (sbyte)42 },
                        new RoundTripSparseObjectNested.Property { Path = "a.b.c", Value = true },
                        new RoundTripSparseObjectNested.Property { Path = "a.b.d", Value = new byte[] { 0x01, 0x02, 0x03 } },
                        new RoundTripSparseObjectNested.Property { Path = "a.b.e", Value = NullValue.Default },
                    },
                },
                new RoundTripSparseObjectNested.Expected
                {
                    Json = @"{'path': 'b', 'type': {'type': 'object'}}",
                    Props = new[]
                    {
                        new RoundTripSparseObjectNested.Property { Path = "a.b.b" },
                    },
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseObjectNested.Expected expected in expectedSchemas)
            {
                string nestedColumnSchema = $"{{'path': 'b', 'type': {{'type': 'object', 'properties': [{expected.Json}] }} }}";
                string objectColumnSchema = $"{{'path': 'a', 'type': {{'type': 'object', 'properties': [{nestedColumnSchema}] }} }}";
                string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{objectColumnSchema}] }}";
                Schema s = Schema.Parse(tableSchema);
                LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                Layout layout = resolver.Resolve(new SchemaId(-1));
                bool found = layout.TryFind("a", out LayoutColumn objCol);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, objCol.Storage, "Json: {0}", expected.Json);
                found = layout.TryFind("a.b", out LayoutColumn objCol2);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.AreEqual(StorageKind.Sparse, objCol2.Storage, "Json: {0}", expected.Json);

                // Try writing a row using the layout.
                row.Reset();
                row.InitLayout(HybridRowVersion.V1, layout, resolver);
                RowCursor root = RowCursor.Create(ref row);

                HybridRowHeader header = row.Header;
                Assert.AreEqual(HybridRowVersion.V1, header.Version);
                Assert.AreEqual(layout.SchemaId, header.SchemaId);

                // Write the object.
                LayoutObject objT = objCol.Type as LayoutObject;
                Assert.IsNotNull(objT);
                root.Clone(out RowCursor field).Find(ref row, objCol.Path);
                Result r = objT.WriteScope(ref row, ref field, objCol.TypeArgs, out RowCursor _);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

                foreach (IEnumerable<RoundTripSparseObjectNested.Property> permutation in expected.Props.Permute())
                {
                    foreach (RoundTripSparseObjectNested.Property prop in permutation)
                    {
                        found = layout.TryFind(prop.Path, out LayoutColumn col);
                        Assert.IsTrue(found, "Json: {0}", expected.Json);
                        Assert.AreEqual(StorageKind.Sparse, col.Storage, "Json: {0}", expected.Json);

                        this.LayoutCodeSwitch<RoundTripSparseObjectNested, RoundTripSparseObjectNested.Closure>(
                            col.Type.LayoutCode, ref row, ref root, new RoundTripSparseObjectNested.Closure
                            {
                                Col = col,
                                Prop = prop,
                                Expected = expected,
                            });
                    }
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ParseSchemaSparseArray()
        {
            // Test all fixed column types.
            RoundTripSparseArray.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[null]",
                    Type = LayoutType.Null,
                    Value = new List<object> { NullValue.Default, NullValue.Default, NullValue.Default },
                },
                new RoundTripSparseArray.Expected { Json = @"array[bool]", Type = LayoutType.Boolean, Value = new List<object> { true, false, true } },

                new RoundTripSparseArray.Expected { Json = @"array[int8]", Type = LayoutType.Int8, Value = new List<object> { (sbyte)42, (sbyte)43, (sbyte)44 } },
                new RoundTripSparseArray.Expected { Json = @"array[int16]", Type = LayoutType.Int16, Value = new List<object> { (short)42, (short)43, (short)44 } },
                new RoundTripSparseArray.Expected { Json = @"array[int32]", Type = LayoutType.Int32, Value = new List<object> { 42, 43, 44 } },
                new RoundTripSparseArray.Expected { Json = @"array[int64]", Type = LayoutType.Int64, Value = new List<object> { 42L, 43L, 44L } },
                new RoundTripSparseArray.Expected { Json = @"array[uint8]", Type = LayoutType.UInt8, Value = new List<object> { (byte)42, (byte)43, (byte)44 } },
                new RoundTripSparseArray.Expected { Json = @"array[uint16]", Type = LayoutType.UInt16, Value = new List<object> { (ushort)42, (ushort)43, (ushort)44 } },
                new RoundTripSparseArray.Expected { Json = @"array[uint32]", Type = LayoutType.UInt32, Value = new List<object> { 42u, 43u, 44u } },
                new RoundTripSparseArray.Expected { Json = @"array[uint64]", Type = LayoutType.UInt64, Value = new List<object> { 42UL, 43UL, 44UL } },

                new RoundTripSparseArray.Expected { Json = @"array[varint]", Type = LayoutType.VarInt, Value = new List<object> { 42L, 43L, 44L } },
                new RoundTripSparseArray.Expected { Json = @"array[varuint]", Type = LayoutType.VarUInt, Value = new List<object> { 42UL, 43UL, 44UL } },

                new RoundTripSparseArray.Expected { Json = @"array[float32]", Type = LayoutType.Float32, Value = new List<object> { 4.2F, 4.3F, 4.4F } },
                new RoundTripSparseArray.Expected { Json = @"array[float64]", Type = LayoutType.Float64, Value = new List<object> { 4.2, 4.3, 4.4 } },
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[float128]",
                    Type = LayoutType.Float128,
                    Value = new List<object> { new Float128(0, 42), new Float128(0, 43), new Float128(0, 44) },
                },
                new RoundTripSparseArray.Expected { Json = @"array[decimal]", Type = LayoutType.Decimal, Value = new List<object> { 4.2M, 4.3M, 4.4M } },

                new RoundTripSparseArray.Expected
                {
                    Json = @"array[datetime]",
                    Type = LayoutType.DateTime,
                    Value = new List<object> { DateTime.UtcNow, DateTime.UtcNow.AddTicks(1), DateTime.UtcNow.AddTicks(2) },
                },
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[unixdatetime]",
                    Type = LayoutType.UnixDateTime,
                    Value = new List<object> { new UnixDateTime(1), new UnixDateTime(2), new UnixDateTime(3) },
                },
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[guid]",
                    Type = LayoutType.Guid,
                    Value = new List<object> { Guid.NewGuid(), Guid.NewGuid(), Guid.NewGuid() },
                },
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[mongodbobjectid]",
                    Type = LayoutType.MongoDbObjectId,
                    Value = new List<object> { new MongoDbObjectId(0, 1), new MongoDbObjectId(0, 2), new MongoDbObjectId(0, 3) },
                },

                new RoundTripSparseArray.Expected { Json = @"array[utf8]", Type = LayoutType.Utf8, Value = new List<object> { "abc", "def", "xyz" } },
                new RoundTripSparseArray.Expected
                {
                    Json = @"array[binary]",
                    Type = LayoutType.Binary,
                    Value = new List<object> { new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04 }, new byte[] { 0x05, 0x06 } },
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseArray.Expected expected in expectedSchemas)
            {
                foreach (Type arrT in new[] { typeof(LayoutTypedArray), typeof(LayoutArray) })
                {
                    string arrayColumnSchema = @"{'path': 'a', 'type': {'type': 'array', 'items': {'type': 'any'}} }";
                    if (arrT == typeof(LayoutTypedArray))
                    {
                        arrayColumnSchema = $@"{{'path': 'a', 'type': {{'type': 'array',
                                                                        'items': {{'type': '{expected.Type.Name}', 'nullable': false }}}} }}";
                    }

                    string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{arrayColumnSchema}] }}";
                    Schema s = Schema.Parse(tableSchema);
                    LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                    Layout layout = resolver.Resolve(new SchemaId(-1));
                    bool found = layout.TryFind("a", out LayoutColumn arrCol);
                    Assert.IsTrue(found, "Json: {0}", expected.Json);
                    Assert.AreEqual(StorageKind.Sparse, arrCol.Storage, "Json: {0}", expected.Json);

                    // Try writing a row using the layout.
                    row.Reset();
                    row.InitLayout(HybridRowVersion.V1, layout, resolver);

                    HybridRowHeader header = row.Header;
                    Assert.AreEqual(HybridRowVersion.V1, header.Version);
                    Assert.AreEqual(layout.SchemaId, header.SchemaId);

                    RowCursor root = RowCursor.Create(ref row);
                    this.LayoutCodeSwitch<RoundTripSparseArray, RoundTripSparseArray.Closure>(
                        expected.Type.LayoutCode, ref row, ref root, new RoundTripSparseArray.Closure
                        {
                            ArrCol = arrCol,
                            Expected = expected,
                        });
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1139", Justification = "Need to control the binary ordering.")]
        public void ParseSchemaSparseSet()
        {
            // Test all fixed column types.
            RoundTripSparseSet.Expected[] expectedSchemas = new[]
            {
                new RoundTripSparseSet.Expected { Json = @"set[null]", Type = LayoutType.Null, Value = new List<object> { NullValue.Default } },
                new RoundTripSparseSet.Expected { Json = @"set[bool]", Type = LayoutType.Boolean, Value = new List<object> { false, true } },

                new RoundTripSparseSet.Expected { Json = @"set[int8]", Type = LayoutType.Int8, Value = new List<object> { (sbyte)42, (sbyte)43, (sbyte)44 } },

                new RoundTripSparseSet.Expected { Json = @"set[int16]", Type = LayoutType.Int16, Value = new List<object> { (short)42, (short)43, (short)44 } },
                new RoundTripSparseSet.Expected { Json = @"set[int32]", Type = LayoutType.Int32, Value = new List<object> { 42, 43, 44 } },
                new RoundTripSparseSet.Expected { Json = @"set[int64]", Type = LayoutType.Int64, Value = new List<object> { 42L, 43L, 44L } },
                new RoundTripSparseSet.Expected { Json = @"set[uint8]", Type = LayoutType.UInt8, Value = new List<object> { (byte)42, (byte)43, (byte)44 } },
                new RoundTripSparseSet.Expected { Json = @"set[uint16]", Type = LayoutType.UInt16, Value = new List<object> { (ushort)42, (ushort)43, (ushort)44 } },
                new RoundTripSparseSet.Expected { Json = @"set[uint32]", Type = LayoutType.UInt32, Value = new List<object> { 42u, 43u, 44u } },
                new RoundTripSparseSet.Expected { Json = @"set[uint64]", Type = LayoutType.UInt64, Value = new List<object> { 42UL, 43UL, 44UL } },

                new RoundTripSparseSet.Expected { Json = @"set[varint]", Type = LayoutType.VarInt, Value = new List<object> { 42L, 43L, 44L } },
                new RoundTripSparseSet.Expected { Json = @"set[varuint]", Type = LayoutType.VarUInt, Value = new List<object> { 42UL, 43UL, 44UL } },

                new RoundTripSparseSet.Expected { Json = @"set[float32]", Type = LayoutType.Float32, Value = new List<object> { 4.2F, 4.3F, 4.4F } },
                new RoundTripSparseSet.Expected
                {
                    Json = @"set[float64]",
                    Type = LayoutType.Float64,
                    Value = new List<object>
                    {
                        (double)0xAAAAAAAAAAAAAAAA,
                        (double)0xBBBBBBBBBBBBBBBB,
                        (double)0xCCCCCCCCCCCCCCCC,
                    },
                },
                new RoundTripSparseSet.Expected { Json = @"set[decimal]", Type = LayoutType.Decimal, Value = new List<object> { 4.2M, 4.3M, 4.4M } },

                new RoundTripSparseSet.Expected
                {
                    Json = @"set[datetime]",
                    Type = LayoutType.DateTime,
                    Value = new List<object>
                    {
                        new DateTime(1, DateTimeKind.Unspecified),
                        new DateTime(2, DateTimeKind.Unspecified),
                        new DateTime(3, DateTimeKind.Unspecified),
                    },
                },
                new RoundTripSparseSet.Expected
                {
                    Json = @"set[guid]",
                    Type = LayoutType.Guid,
                    Value = new List<object>
                    {
                        Guid.Parse("AAAAAAAA-AAAA-AAAA-AAAA-AAAAAAAAAAAA"),
                        Guid.Parse("BBBBBBBB-BBBB-BBBB-BBBB-BBBBBBBBBBBB"),
                        Guid.Parse("CCCCCCCC-CCCC-CCCC-CCCC-CCCCCCCCCCCC"),
                    },
                },

                new RoundTripSparseSet.Expected { Json = @"set[utf8]", Type = LayoutType.Utf8, Value = new List<object> { "abc", "def", "xyz" } },
                new RoundTripSparseSet.Expected
                {
                    Json = @"set[binary]",
                    Type = LayoutType.Binary,
                    Value = new List<object> { new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04 }, new byte[] { 0x05, 0x06 } },
                },
            };

            RowBuffer row = new RowBuffer(LayoutCompilerUnitTests.InitialRowSize);
            foreach (RoundTripSparseSet.Expected expected in expectedSchemas)
            {
                foreach (Type setT in new[] { typeof(LayoutTypedSet) })
                {
                    string setColumnSchema = @"{'path': 'a', 'type': {'type': 'set', 'items': {'type': 'any'}} }";
                    if (setT == typeof(LayoutTypedSet))
                    {
                        setColumnSchema = $@"{{'path': 'a', 'type': {{'type': 'set', 
                                                                      'items': {{'type': '{expected.Type.Name}', 'nullable': false }}}} }}";
                    }

                    string tableSchema = $"{{'name': 'table', 'id': -1, 'type': 'schema', 'properties': [{setColumnSchema}] }}";
                    Schema s = Schema.Parse(tableSchema);
                    LayoutResolverNamespace resolver = new LayoutResolverNamespace(new Namespace { Schemas = new List<Schema> { s } });
                    Layout layout = resolver.Resolve(new SchemaId(-1));
                    bool found = layout.TryFind("a", out LayoutColumn setCol);
                    Assert.IsTrue(found, "Json: {0}", expected.Json);
                    Assert.AreEqual(StorageKind.Sparse, setCol.Storage, "Json: {0}", expected.Json);

                    // Try writing a row using the layout.
                    row.Reset();
                    row.InitLayout(HybridRowVersion.V1, layout, resolver);

                    HybridRowHeader header = row.Header;
                    Assert.AreEqual(HybridRowVersion.V1, header.Version);
                    Assert.AreEqual(layout.SchemaId, header.SchemaId);

                    RowCursor root = RowCursor.Create(ref row);
                    this.LayoutCodeSwitch<RoundTripSparseSet, RoundTripSparseSet.Closure>(
                        expected.Type.LayoutCode, ref row, ref root, new RoundTripSparseSet.Closure
                        {
                            SetCol = setCol,
                            Expected = expected,
                        });
                }
            }
        }

        /// <summary>Ensure that a parent scope exists in the row.</summary>
        /// <param name="row">The row to create the desired scope.</param>
        /// <param name="root">The root scope.</param>
        /// <param name="col">The scope to create.</param>
        /// <param name="tag">A string to tag errors with.</param>
        /// <returns>The enclosing scope.</returns>
        private static RowCursor EnsureScope(ref RowBuffer row, ref RowCursor root, LayoutColumn col, string tag)
        {
            if (col == null)
            {
                return root;
            }

            RowCursor parentScope = LayoutCompilerUnitTests.EnsureScope(ref row, ref root, col.Parent, tag);

            LayoutObject pT = col.Type as LayoutObject;
            Assert.IsNotNull(pT);
            parentScope.Clone(out RowCursor field).Find(ref row, col.Path);
            Result r = pT.ReadScope(ref row, ref field, out RowCursor scope);
            if (r == Result.NotFound)
            {
                r = pT.WriteScope(ref row, ref field, col.TypeArgs, out scope);
            }

            ResultAssert.IsSuccess(r, tag);
            return scope;
        }

        private void LayoutCodeSwitch<TDispatcher, TClosure>(LayoutCode code, ref RowBuffer row, ref RowCursor scope, TClosure closure)
            where TDispatcher : TestActionDispatcher<TClosure>, new()
        {
            TDispatcher dispatcher = new TDispatcher();
            switch (code)
            {
                case LayoutCode.Null:
                    dispatcher.Dispatch<LayoutNull, NullValue>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Boolean:
                    dispatcher.Dispatch<LayoutBoolean, bool>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Int8:
                    dispatcher.Dispatch<LayoutInt8, sbyte>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Int16:
                    dispatcher.Dispatch<LayoutInt16, short>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Int32:
                    dispatcher.Dispatch<LayoutInt32, int>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Int64:
                    dispatcher.Dispatch<LayoutInt64, long>(ref row, ref scope, closure);
                    break;
                case LayoutCode.UInt8:
                    dispatcher.Dispatch<LayoutUInt8, byte>(ref row, ref scope, closure);
                    break;
                case LayoutCode.UInt16:
                    dispatcher.Dispatch<LayoutUInt16, ushort>(ref row, ref scope, closure);
                    break;
                case LayoutCode.UInt32:
                    dispatcher.Dispatch<LayoutUInt32, uint>(ref row, ref scope, closure);
                    break;
                case LayoutCode.UInt64:
                    dispatcher.Dispatch<LayoutUInt64, ulong>(ref row, ref scope, closure);
                    break;
                case LayoutCode.VarInt:
                    dispatcher.Dispatch<LayoutVarInt, long>(ref row, ref scope, closure);
                    break;
                case LayoutCode.VarUInt:
                    dispatcher.Dispatch<LayoutVarUInt, ulong>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Float32:
                    dispatcher.Dispatch<LayoutFloat32, float>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Float64:
                    dispatcher.Dispatch<LayoutFloat64, double>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Float128:
                    dispatcher.Dispatch<LayoutFloat128, Float128>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Decimal:
                    dispatcher.Dispatch<LayoutDecimal, decimal>(ref row, ref scope, closure);
                    break;
                case LayoutCode.DateTime:
                    dispatcher.Dispatch<LayoutDateTime, DateTime>(ref row, ref scope, closure);
                    break;
                case LayoutCode.UnixDateTime:
                    dispatcher.Dispatch<LayoutUnixDateTime, UnixDateTime>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Guid:
                    dispatcher.Dispatch<LayoutGuid, Guid>(ref row, ref scope, closure);
                    break;
                case LayoutCode.MongoDbObjectId:
                    dispatcher.Dispatch<LayoutMongoDbObjectId, MongoDbObjectId>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Utf8:
                    dispatcher.Dispatch<LayoutUtf8, string>(ref row, ref scope, closure);
                    break;
                case LayoutCode.Binary:
                    dispatcher.Dispatch<LayoutBinary, byte[]>(ref row, ref scope, closure);
                    break;
                case LayoutCode.ObjectScope:
                    dispatcher.DispatchObject(ref row, ref scope, closure);
                    break;
                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {code}");
                    break;
            }
        }

        private sealed class RoundTripFixed : TestActionDispatcher<RoundTripFixed.Closure>
        {
            public struct Expected
            {
                public string TypeName;
                public string Json;
                public object Value;
                public object Default;
                public int Length;
            }

            public struct Closure
            {
                public LayoutColumn Col;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Expected expected = closure.Expected;
                Result r;
                TValue value;

                Console.WriteLine("{0}", expected.Json);
                TLayout t = (TLayout)col.Type;
                if (col.NullBit != LayoutBit.Invalid)
                {
                    r = t.ReadFixed(ref row, ref root, col, out value);
                    ResultAssert.NotFound(r, "Json: {0}", expected.Json);
                }
                else
                {
                    r = t.ReadFixed(ref row, ref root, col, out value);
                    ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                    if (expected.Default is Array defaultArray)
                    {
                        CollectionAssert.AreEqual(defaultArray, (ICollection)value, "Json: {0}", expected.Json);
                    }
                    else
                    {
                        Assert.AreEqual(expected.Default, value, "Json: {0}", expected.Json);
                    }
                }

                r = t.WriteFixed(ref row, ref root, col, (TValue)expected.Value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = t.ReadFixed(ref row, ref root, col, out value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                if (expected.Value is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, "Json: {0}", expected.Json);
                }
                else
                {
                    Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
                }

                root.AsReadOnly(out RowCursor roRoot);
                ResultAssert.InsufficientPermissions(t.WriteFixed(ref row, ref roRoot, col, (TValue)expected.Value));
                ResultAssert.InsufficientPermissions(t.DeleteFixed(ref row, ref roRoot, col));

                if (col.NullBit != LayoutBit.Invalid)
                {
                    ResultAssert.IsSuccess(t.DeleteFixed(ref row, ref root, col));
                }
                else
                {
                    ResultAssert.TypeMismatch(t.DeleteFixed(ref row, ref root, col));
                }
            }
        }

        private class RoundTripVariable : TestActionDispatcher<RoundTripVariable.Closure>
        {
            public struct Expected
            {
                public string Json;
                public object Short;
                public object Value;
                public object Long;
                public object TooBig;
            }

            public struct Closure
            {
                public LayoutColumn Col;
                public Layout Layout;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Expected expected = closure.Expected;

                Console.WriteLine("{0}", expected.Json);

                this.RoundTrip<TLayout, TValue>(ref row, ref root, col, expected.Value, expected);
            }

            protected void RoundTrip<TLayout, TValue>(
                ref RowBuffer row,
                ref RowCursor root,
                LayoutColumn col,
                object exValue,
                Expected expected)
                where TLayout : LayoutType<TValue>
            {
                TLayout t = (TLayout)col.Type;
                Result r = t.WriteVariable(ref row, ref root, col, (TValue)exValue);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                this.Compare<TLayout, TValue>(ref row, ref root, col, exValue, expected);

                root.AsReadOnly(out RowCursor roRoot);
                ResultAssert.InsufficientPermissions(t.WriteVariable(ref row, ref roRoot, col, (TValue)expected.Value));
            }

            protected void Compare<TLayout, TValue>(
                ref RowBuffer row,
                ref RowCursor root,
                LayoutColumn col,
                object exValue,
                Expected expected)
                where TLayout : LayoutType<TValue>
            {
                TLayout t = (TLayout)col.Type;
                Result r = t.ReadVariable(ref row, ref root, col, out TValue value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                if (exValue is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, "Json: {0}", expected.Json);
                }
                else
                {
                    Assert.AreEqual(exValue, value, "Json: {0}", expected.Json);
                }
            }
        }

        private sealed class VariableInterleaving : RoundTripVariable
        {
            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                Layout layout = closure.Layout;
                Expected expected = closure.Expected;

                Console.WriteLine("{0}", expected.Json);

                LayoutColumn a = this.Verify<TLayout, TValue>(ref row, ref root, layout, "a", expected);
                LayoutColumn b = this.Verify<TLayout, TValue>(ref row, ref root, layout, "b", expected);
                LayoutColumn c = this.Verify<TLayout, TValue>(ref row, ref root, layout, "c", expected);

                this.RoundTrip<TLayout, TValue>(ref row, ref root, b, expected.Value, expected);
                this.RoundTrip<TLayout, TValue>(ref row, ref root, a, expected.Value, expected);
                this.RoundTrip<TLayout, TValue>(ref row, ref root, c, expected.Value, expected);

                // Make the var column shorter.
                int rowSizeBeforeShrink = row.Length;
                this.RoundTrip<TLayout, TValue>(ref row, ref root, a, expected.Short, expected);
                this.Compare<TLayout, TValue>(ref row, ref root, c, expected.Value, expected);
                int rowSizeAfterShrink = row.Length;
                Assert.IsTrue(rowSizeAfterShrink < rowSizeBeforeShrink, "Json: {0}", expected.Json);

                // Make the var column longer.
                this.RoundTrip<TLayout, TValue>(ref row, ref root, a, expected.Long, expected);
                this.Compare<TLayout, TValue>(ref row, ref root, c, expected.Value, expected);
                int rowSizeAfterGrow = row.Length;
                Assert.IsTrue(rowSizeAfterGrow > rowSizeAfterShrink, "Json: {0}", expected.Json);
                Assert.IsTrue(rowSizeAfterGrow > rowSizeBeforeShrink, "Json: {0}", expected.Json);

                // Check for size overflow errors.
                if (a.Size > 0)
                {
                    this.TooBig<TLayout, TValue>(ref row, ref root, a, expected);
                }

                // Delete the var column.
                this.Delete<TLayout, TValue>(ref row, ref root, b, expected);
                this.Delete<TLayout, TValue>(ref row, ref root, c, expected);
                this.Delete<TLayout, TValue>(ref row, ref root, a, expected);
            }

            private LayoutColumn Verify<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Layout layout, string path, Expected expected)
                where TLayout : LayoutType<TValue>
            {
                bool found = layout.TryFind(path, out LayoutColumn col);
                Assert.IsTrue(found, "Json: {0}", expected.Json);
                Assert.IsTrue(col.Type.AllowVariable);
                TLayout t = (TLayout)col.Type;
                Result r = t.ReadVariable(ref row, ref root, col, out TValue _);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
                return col;
            }

            private void TooBig<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, LayoutColumn col, Expected expected)
                where TLayout : LayoutType<TValue>
            {
                TLayout t = (TLayout)col.Type;
                Result r = t.WriteVariable(ref row, ref root, col, (TValue)expected.TooBig);
                Assert.AreEqual(Result.TooBig, r, "Json: {0}", expected.Json);
            }

            private void Delete<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, LayoutColumn col, Expected expected)
                where TLayout : LayoutType<TValue>
            {
                TLayout t = (TLayout)col.Type;
                root.AsReadOnly(out RowCursor roRoot);
                ResultAssert.InsufficientPermissions(t.DeleteVariable(ref row, ref roRoot, col));
                Result r = t.DeleteVariable(ref row, ref root, col);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = t.ReadVariable(ref row, ref root, col, out TValue _);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
            }
        }

        private sealed class RoundTripSparseOrdering : TestActionDispatcher<RoundTripSparseOrdering.Closure>
        {
            public struct Expected
            {
                public string Path;
                public LayoutType Type;
                public object Value;
            }

            public struct Closure
            {
                public string Json;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutType type = closure.Expected.Type;
                string path = closure.Expected.Path;
                object exValue = closure.Expected.Value;
                string json = closure.Json;

                TLayout t = (TLayout)type;
                TValue value = (TValue)exValue;
                root.Clone(out RowCursor field).Find(ref row, path);
                Result r = t.WriteSparse(ref row, ref field, value);
                ResultAssert.IsSuccess(r, "Json: {0}", json);
                r = t.ReadSparse(ref row, ref field, out value);
                ResultAssert.IsSuccess(r, "Json: {0}", json);
                if (exValue is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, "Json: {0}", json);
                }
                else
                {
                    Assert.AreEqual(exValue, value, "Json: {0}", json);
                }

                if (t is LayoutNull)
                {
                    r = LayoutType.Boolean.WriteSparse(ref row, ref field, false);
                    ResultAssert.IsSuccess(r, "Json: {0}", json);
                    r = t.ReadSparse(ref row, ref field, out value);
                    ResultAssert.TypeMismatch(r, "Json: {0}", json);
                }
                else
                {
                    r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                    ResultAssert.IsSuccess(r, "Json: {0}", json);
                    r = t.ReadSparse(ref row, ref field, out value);
                    ResultAssert.TypeMismatch(r, "Json: {0}", json);
                }
            }
        }

        private sealed class RoundTripSparseSimple : TestActionDispatcher<RoundTripSparseSimple.Closure>
        {
            public struct Expected
            {
                public string Json;
                public object Value;
            }

            public struct Closure
            {
                public LayoutColumn Col;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Expected expected = closure.Expected;

                Console.WriteLine("{0}", col.Type.Name);
                TLayout t = (TLayout)col.Type;
                root.Clone(out RowCursor field).Find(ref row, col.Path);
                Result r = t.ReadSparse(ref row, ref field, out TValue value);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
                r = t.WriteSparse(ref row, ref field, (TValue)expected.Value, UpdateOptions.Update);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
                r = t.WriteSparse(ref row, ref field, (TValue)expected.Value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = t.WriteSparse(ref row, ref field, (TValue)expected.Value, UpdateOptions.Insert);
                ResultAssert.Exists(r, "Json: {0}", expected.Json);
                r = t.ReadSparse(ref row, ref field, out value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                if (expected.Value is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, "Json: {0}", expected.Json);
                }
                else
                {
                    Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
                }

                root.AsReadOnly(out RowCursor roRoot).Find(ref row, col.Path);
                ResultAssert.InsufficientPermissions(t.DeleteSparse(ref row, ref roRoot));
                ResultAssert.InsufficientPermissions(t.WriteSparse(ref row, ref roRoot, (TValue)expected.Value, UpdateOptions.Update));

                if (t is LayoutNull)
                {
                    r = LayoutType.Boolean.WriteSparse(ref row, ref field, false);
                    ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                    r = t.ReadSparse(ref row, ref field, out value);
                    ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
                }
                else
                {
                    r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                    ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                    r = t.ReadSparse(ref row, ref field, out value);
                    ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
                }

                r = t.DeleteSparse(ref row, ref field);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

                // Overwrite it again, then delete it.
                r = t.WriteSparse(ref row, ref field, (TValue)expected.Value, UpdateOptions.Update);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = t.DeleteSparse(ref row, ref field);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = t.ReadSparse(ref row, ref field, out value);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
            }
        }

        private sealed class RoundTripSparseObject : TestActionDispatcher<RoundTripSparseObject.Closure>
        {
            public struct Expected
            {
                public string Json;
                public object Value;
            }

            public struct Closure
            {
                public LayoutColumn ObjCol;
                public LayoutColumn Col;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn objCol = closure.ObjCol;
                LayoutObject objT = objCol.Type as LayoutObject;
                LayoutColumn col = closure.Col;
                Expected expected = closure.Expected;

                Console.WriteLine("{0}", col.Type.Name);
                Assert.IsNotNull(objT, "Json: {0}", expected.Json);
                Assert.AreEqual(objCol, col.Parent, "Json: {0}", expected.Json);

                TLayout t = (TLayout)col.Type;

                // Attempt to read the object and the nested column.
                root.Clone(out RowCursor field).Find(ref row, objCol.Path);
                Result r = objT.ReadScope(ref row, ref field, out RowCursor scope);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);

                // Write the object and the nested column.
                r = objT.WriteScope(ref row, ref field, objCol.TypeArgs, out scope);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

                // Verify the nested field doesn't yet appear within the new scope.
                scope.Clone(out RowCursor nestedField).Find(ref row, col.Path);
                r = t.ReadSparse(ref row, ref nestedField, out TValue value);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);

                // Write the nested field.
                r = t.WriteSparse(ref row, ref nestedField, (TValue)expected.Value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);

                // Read the object and the nested column, validate the nested column has the proper value.
                r = objT.ReadScope(ref row, ref field, out RowCursor scope2);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                Assert.AreEqual(scope.ScopeType, scope2.ScopeType, "Json: {0}", expected.Json);
                Assert.AreEqual(scope.start, scope2.start, "Json: {0}", expected.Json);
                Assert.AreEqual(scope.Immutable, scope2.Immutable, "Json: {0}", expected.Json);

                // Read the nested field
                scope2.Clone(out nestedField).Find(ref row, col.Path);
                r = t.ReadSparse(ref row, ref nestedField, out value);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                if (expected.Value is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, "Json: {0}", expected.Json);
                }
                else
                {
                    Assert.AreEqual(expected.Value, value, "Json: {0}", expected.Json);
                }

                root.AsReadOnly(out RowCursor roRoot).Find(ref row, objCol.Path);
                ResultAssert.InsufficientPermissions(objT.DeleteScope(ref row, ref roRoot));
                ResultAssert.InsufficientPermissions(objT.WriteScope(ref row, ref roRoot, objCol.TypeArgs, out scope2));

                // Overwrite the whole scope.
                r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = objT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);
                r = objT.DeleteScope(ref row, ref field);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

                // Overwrite it again, then delete it.
                r = objT.WriteScope(ref row, ref field, objCol.TypeArgs, out RowCursor _, UpdateOptions.Update);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = objT.DeleteScope(ref row, ref field);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = objT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
            }
        }

        private sealed class RoundTripSparseObjectMulti : TestActionDispatcher<RoundTripSparseObjectMulti.Closure>
        {
            public struct Expected
            {
                public string Json;
                public Property[] Props;
            }

            public struct Property
            {
                public string Path;
                public object Value;
            }

            public struct Closure
            {
                public LayoutColumn Col;
                public Property Prop;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor scope, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Property prop = closure.Prop;
                Expected expected = closure.Expected;
                string tag = string.Format("Prop: {1}: Json: {0}", expected.Json, prop.Path);

                Console.WriteLine(tag);

                TLayout t = (TLayout)col.Type;

                // Verify the nested field doesn't yet appear within the new scope.
                scope.Clone(out RowCursor nestedField).Find(ref row, col.Path);
                Result r = t.ReadSparse(ref row, ref nestedField, out TValue value);
                Assert.IsTrue(r == Result.NotFound || r == Result.TypeMismatch, tag);

                // Write the nested field.
                r = t.WriteSparse(ref row, ref nestedField, (TValue)prop.Value);
                ResultAssert.IsSuccess(r, tag);

                // Read the nested field
                r = t.ReadSparse(ref row, ref nestedField, out value);
                ResultAssert.IsSuccess(r, tag);
                if (prop.Value is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, tag);
                }
                else
                {
                    Assert.AreEqual(prop.Value, value, tag);
                }

                // Overwrite the nested field.
                if (t is LayoutNull)
                {
                    r = LayoutType.Boolean.WriteSparse(ref row, ref nestedField, false);
                    ResultAssert.IsSuccess(r, tag);
                }
                else
                {
                    r = LayoutType.Null.WriteSparse(ref row, ref nestedField, NullValue.Default);
                    ResultAssert.IsSuccess(r, tag);
                }

                // Verify nested field no longer there.
                r = t.ReadSparse(ref row, ref nestedField, out value);
                ResultAssert.TypeMismatch(r, tag);
            }

            public override void DispatchObject(ref RowBuffer row, ref RowCursor scope, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Property prop = closure.Prop;
                Expected expected = closure.Expected;
                string tag = string.Format("Prop: {1}: Json: {0}", expected.Json, prop.Path);

                Console.WriteLine(tag);

                LayoutObject t = (LayoutObject)col.Type;

                // Verify the nested field doesn't yet appear within the new scope.
                scope.Clone(out RowCursor nestedField).Find(ref row, col.Path);
                Result r = t.ReadScope(ref row, ref nestedField, out RowCursor scope2);
                ResultAssert.NotFound(r, tag);

                // Write the nested field.
                r = t.WriteScope(ref row, ref nestedField, col.TypeArgs, out scope2);
                ResultAssert.IsSuccess(r, tag);

                // Read the nested field
                r = t.ReadScope(ref row, ref nestedField, out RowCursor scope3);
                ResultAssert.IsSuccess(r, tag);
                Assert.AreEqual(scope2.AsReadOnly(out RowCursor _).ScopeType, scope3.ScopeType, tag);
                Assert.AreEqual(scope2.AsReadOnly(out RowCursor _).start, scope3.start, tag);

                // Overwrite the nested field.
                r = LayoutType.Null.WriteSparse(ref row, ref nestedField, NullValue.Default);
                ResultAssert.IsSuccess(r, tag);

                // Verify nested field no longer there.
                r = t.ReadScope(ref row, ref nestedField, out scope3);
                ResultAssert.TypeMismatch(r, tag);
            }
        }

        private sealed class RoundTripSparseObjectNested : TestActionDispatcher<RoundTripSparseObjectNested.Closure>
        {
            public struct Expected
            {
                public string Json;
                public Property[] Props;
            }

            public struct Property
            {
                public string Path;
                public object Value;
            }

            public struct Closure
            {
                public LayoutColumn Col;
                public Property Prop;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Property prop = closure.Prop;
                Expected expected = closure.Expected;
                string tag = string.Format("Prop: {1}: Json: {0}", expected.Json, prop.Path);

                Console.WriteLine(tag);

                TLayout t = (TLayout)col.Type;

                // Ensure scope exists.
                RowCursor scope = LayoutCompilerUnitTests.EnsureScope(ref row, ref root, col.Parent, tag);

                // Write the nested field.
                scope.Clone(out RowCursor field).Find(ref row, col.Path);
                Result r = t.WriteSparse(ref row, ref field, (TValue)prop.Value);
                ResultAssert.IsSuccess(r, tag);

                // Read the nested field
                r = t.ReadSparse(ref row, ref field, out TValue value);
                ResultAssert.IsSuccess(r, tag);
                if (prop.Value is Array array)
                {
                    CollectionAssert.AreEqual(array, (ICollection)value, tag);
                }
                else
                {
                    Assert.AreEqual(prop.Value, value, tag);
                }

                // Overwrite the nested field.
                if (t is LayoutNull)
                {
                    r = LayoutType.Boolean.WriteSparse(ref row, ref field, false);
                    ResultAssert.IsSuccess(r, tag);
                }
                else
                {
                    r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                    ResultAssert.IsSuccess(r, tag);
                }

                // Verify nested field no longer there.
                r = t.ReadSparse(ref row, ref field, out value);
                ResultAssert.TypeMismatch(r, tag);
            }

            public override void DispatchObject(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn col = closure.Col;
                Property prop = closure.Prop;
                Expected expected = closure.Expected;
                string tag = string.Format("Prop: {1}: Json: {0}", expected.Json, prop.Path);

                Console.WriteLine(tag);

                // Ensure scope exists.
                RowCursor scope = LayoutCompilerUnitTests.EnsureScope(ref row, ref root, col, tag);
                Assert.AreNotEqual(root, scope);
            }
        }

        private sealed class RoundTripSparseArray : TestActionDispatcher<RoundTripSparseArray.Closure>
        {
            public struct Expected
            {
                public string Json;
                public LayoutType Type;
                public List<object> Value;
            }

            public struct Closure
            {
                public LayoutColumn ArrCol;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn arrCol = closure.ArrCol;
                LayoutIndexedScope arrT = arrCol.Type as LayoutIndexedScope;
                Expected expected = closure.Expected;
                string tag = $"Json: {expected.Json}, Array: {arrCol.Type.Name}";

                Console.WriteLine(tag);
                Assert.IsNotNull(arrT, tag);

                TLayout t = (TLayout)expected.Type;

                // Verify the array doesn't yet exist.
                root.Clone(out RowCursor field).Find(ref row, arrCol.Path);
                Result r = arrT.ReadScope(ref row, ref field, out RowCursor scope);
                ResultAssert.NotFound(r, tag);

                // Write the array.
                r = arrT.WriteScope(ref row, ref field, arrCol.TypeArgs, out scope);
                ResultAssert.IsSuccess(r, tag);

                // Verify the nested field doesn't yet appear within the new scope.
                Assert.IsFalse(scope.MoveNext(ref row));
                r = t.ReadSparse(ref row, ref scope, out TValue value);
                ResultAssert.NotFound(r, tag);

                // Write the nested fields.
                scope.Clone(out RowCursor elm);
                foreach (object item in expected.Value)
                {
                    // Write the ith index.
                    r = t.WriteSparse(ref row, ref elm, (TValue)item);
                    ResultAssert.IsSuccess(r, tag);

                    // Move cursor to the ith+1 index.
                    Assert.IsFalse(elm.MoveNext(ref row));
                }

                // Read the array and the nested column, validate the nested column has the proper value.
                r = arrT.ReadScope(ref row, ref field, out RowCursor scope2);
                ResultAssert.IsSuccess(r, tag);
                Assert.AreEqual(scope.ScopeType, scope2.ScopeType, tag);
                Assert.AreEqual(scope.start, scope2.start, tag);
                Assert.AreEqual(scope.Immutable, scope2.Immutable, tag);

                // Read the nested fields
                scope2.Clone(out elm);
                foreach (object item in expected.Value)
                {
                    Assert.IsTrue(elm.MoveNext(ref row));
                    r = t.ReadSparse(ref row, ref elm, out value);
                    ResultAssert.IsSuccess(r, tag);
                    if (item is Array array)
                    {
                        CollectionAssert.AreEqual(array, (ICollection)value, tag);
                    }
                    else
                    {
                        Assert.AreEqual((TValue)item, value, tag);
                    }
                }

                // Delete an item.
                int indexToDelete = 1;
                Assert.IsTrue(scope2.Clone(out elm).MoveTo(ref row, indexToDelete));
                r = t.DeleteSparse(ref row, ref elm);
                ResultAssert.IsSuccess(r, tag);
                List<object> remainingValues = new List<object>(expected.Value);
                remainingValues.RemoveAt(indexToDelete);
                scope2.Clone(out elm);
                foreach (object item in remainingValues)
                {
                    Assert.IsTrue(elm.MoveNext(ref row));
                    r = t.ReadSparse(ref row, ref elm, out value);
                    ResultAssert.IsSuccess(r, tag);
                    if (item is Array array)
                    {
                        CollectionAssert.AreEqual(array, (ICollection)value, tag);
                    }
                    else
                    {
                        Assert.AreEqual(item, value, tag);
                    }
                }

                Assert.IsFalse(scope2.Clone(out elm).MoveTo(ref row, remainingValues.Count));

                root.AsReadOnly(out RowCursor roRoot).Find(ref row, arrCol.Path);
                ResultAssert.InsufficientPermissions(arrT.DeleteScope(ref row, ref roRoot));
                ResultAssert.InsufficientPermissions(arrT.WriteScope(ref row, ref roRoot, arrCol.TypeArgs, out scope2));

                // Overwrite the whole scope.
                r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                ResultAssert.IsSuccess(r, tag);
                r = arrT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.TypeMismatch(r, tag);
                r = arrT.DeleteScope(ref row, ref field);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

                // Overwrite it again, then delete it.
                r = arrT.WriteScope(ref row, ref field, arrCol.TypeArgs, out RowCursor _, UpdateOptions.Update);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = arrT.DeleteScope(ref row, ref field);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = arrT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
            }
        }

        private sealed class RoundTripSparseSet : TestActionDispatcher<RoundTripSparseSet.Closure>
        {
            public struct Expected
            {
                public string Json;
                public LayoutType Type;
                public List<object> Value;
            }

            public struct Closure
            {
                public LayoutColumn SetCol;
                public Expected Expected;
            }

            public override void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, Closure closure)
            {
                LayoutColumn setCol = closure.SetCol;
                LayoutUniqueScope setT = setCol.Type as LayoutUniqueScope;
                Expected expected = closure.Expected;
                string tag = $"Json: {expected.Json}, Set: {setCol.Type.Name}";

                Console.WriteLine(tag);
                Assert.IsNotNull(setT, tag);

                TLayout t = (TLayout)expected.Type;

                // Verify the Set doesn't yet exist.
                root.Clone(out RowCursor field).Find(ref row, setCol.Path);
                Result r = setT.ReadScope(ref row, ref field, out RowCursor scope);
                ResultAssert.NotFound(r, tag);

                // Write the Set.
                r = setT.WriteScope(ref row, ref field, setCol.TypeArgs, out scope);
                ResultAssert.IsSuccess(r, tag);

                // Verify the nested field doesn't yet appear within the new scope.
                Assert.IsFalse(scope.MoveNext(ref row));
                r = t.ReadSparse(ref row, ref scope, out TValue value);
                ResultAssert.NotFound(r, tag);

                // Write the nested fields.
                foreach (object v1 in expected.Value)
                {
                    // Write the ith item into staging storage.
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    r = t.WriteSparse(ref row, ref tempCursor, (TValue)v1);
                    ResultAssert.IsSuccess(r, tag);

                    // Move item into the set.
                    r = setT.MoveField(ref row, ref scope, ref tempCursor);
                    ResultAssert.IsSuccess(r, tag);
                }

                // Attempts to insert the same items into the set again will fail.
                foreach (object v2 in expected.Value)
                {
                    // Write the ith item into staging storage.
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    r = t.WriteSparse(ref row, ref tempCursor, (TValue)v2);
                    ResultAssert.IsSuccess(r, tag);

                    // Move item into the set.
                    r = setT.MoveField(ref row, ref scope, ref tempCursor, UpdateOptions.Insert);
                    ResultAssert.Exists(r, tag);
                }

                // Read the Set and the nested column, validate the nested column has the proper value.
                r = setT.ReadScope(ref row, ref field, out RowCursor scope2);
                ResultAssert.IsSuccess(r, tag);
                Assert.AreEqual(scope.ScopeType, scope2.ScopeType, tag);
                Assert.AreEqual(scope.start, scope2.start, tag);
                Assert.AreEqual(scope.Immutable, scope2.Immutable, tag);

                // Read the nested fields
                ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                foreach (object item in expected.Value)
                {
                    Assert.IsTrue(scope.MoveNext(ref row));
                    r = t.ReadSparse(ref row, ref scope, out value);
                    ResultAssert.IsSuccess(r, tag);
                    if (item is Array array)
                    {
                        CollectionAssert.AreEqual(array, (ICollection)value, tag);
                    }
                    else
                    {
                        Assert.AreEqual(item, value, tag);
                    }
                }

                // Delete all of the items and then insert them again in the opposite order.
                ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                for (int i = 0; i < expected.Value.Count; i++)
                {
                    Assert.IsTrue(scope.MoveNext(ref row));
                    r = t.DeleteSparse(ref row, ref scope);
                    ResultAssert.IsSuccess(r, tag);
                }

                ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                for (int i = expected.Value.Count - 1; i >= 0; i--)
                {
                    // Write the ith item into staging storage.
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    r = t.WriteSparse(ref row, ref tempCursor, (TValue)expected.Value[i]);
                    ResultAssert.IsSuccess(r, tag);

                    // Move item into the set.
                    r = setT.MoveField(ref row, ref scope, ref tempCursor);
                    ResultAssert.IsSuccess(r, tag);
                }

                // Verify they still enumerate in sorted order.
                ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                foreach (object item in expected.Value)
                {
                    Assert.IsTrue(scope.MoveNext(ref row));
                    r = t.ReadSparse(ref row, ref scope, out value);
                    ResultAssert.IsSuccess(r, tag);
                    if (item is Array array)
                    {
                        CollectionAssert.AreEqual(array, (ICollection)value, tag);
                    }
                    else
                    {
                        Assert.AreEqual(item, value, tag);
                    }
                }

                // Delete one item.
                if (expected.Value.Count > 1)
                {
                    int indexToDelete = 1;
                    ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                    Assert.IsTrue(scope.MoveTo(ref row, indexToDelete));
                    r = t.DeleteSparse(ref row, ref scope);
                    ResultAssert.IsSuccess(r, tag);
                    List<object> remainingValues = new List<object>(expected.Value);
                    remainingValues.RemoveAt(indexToDelete);

                    ResultAssert.IsSuccess(setT.ReadScope(ref row, ref field, out scope));
                    foreach (object item in remainingValues)
                    {
                        Assert.IsTrue(scope.MoveNext(ref row));
                        r = t.ReadSparse(ref row, ref scope, out value);
                        ResultAssert.IsSuccess(r, tag);
                        if (item is Array array)
                        {
                            CollectionAssert.AreEqual(array, (ICollection)value, tag);
                        }
                        else
                        {
                            Assert.AreEqual(item, value, tag);
                        }
                    }

                    Assert.IsFalse(scope.MoveTo(ref row, remainingValues.Count));
                }

                root.AsReadOnly(out RowCursor roRoot).Find(ref row, setCol.Path);
                ResultAssert.InsufficientPermissions(setT.DeleteScope(ref row, ref roRoot));
                ResultAssert.InsufficientPermissions(setT.WriteScope(ref row, ref roRoot, setCol.TypeArgs, out _));

                // Overwrite the whole scope.
                r = LayoutType.Null.WriteSparse(ref row, ref field, NullValue.Default);
                ResultAssert.IsSuccess(r, tag);
                r = setT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.TypeMismatch(r, tag);
                r = setT.DeleteScope(ref row, ref field);
                ResultAssert.TypeMismatch(r, "Json: {0}", expected.Json);

                // Overwrite it again, then delete it.
                r = setT.WriteScope(ref row, ref field, setCol.TypeArgs, out RowCursor _, UpdateOptions.Update);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = setT.DeleteScope(ref row, ref field);
                ResultAssert.IsSuccess(r, "Json: {0}", expected.Json);
                r = setT.ReadScope(ref row, ref field, out RowCursor _);
                ResultAssert.NotFound(r, "Json: {0}", expected.Json);
            }
        }

        private abstract class TestActionDispatcher<TClosure>
        {
            public abstract void Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor scope, TClosure closure)
                where TLayout : LayoutType<TValue>;

            public virtual void DispatchObject(ref RowBuffer row, ref RowCursor scope, TClosure closure)
            {
                Assert.Fail("not implemented");
            }
        }
    }
}
