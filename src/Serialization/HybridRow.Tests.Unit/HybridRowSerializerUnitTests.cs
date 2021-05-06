// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public sealed class HybridRowSerializerUnitTests
    {
        [TestMethod]
        [Owner("jthunter")]
        public void TypedArraySerializerTest()
        {
            TypeArgumentList typeArgs1 = new TypeArgumentList(new[] { new TypeArgument(LayoutType.Utf8) });
            HybridRowSerializerUnitTests.TestSerializer<List<string>, TypedArrayHybridRowSerializer<string, Utf8HybridRowSerializer>>(
                typeArgs1,
                new List<string> { "abc", "xyz" },
                new List<string> { "abc", "ghk" },
                new List<string> { "abc" });

            TypeArgumentList typeArgs2 = new TypeArgumentList(new[] { new TypeArgument(LayoutType.Int32) });
            HybridRowSerializerUnitTests.TestSerializer<List<int>, TypedArrayHybridRowSerializer<int, Int32HybridRowSerializer>>(
                typeArgs2,
                new List<int> { 123, 456 },
                new List<int> { 123, 789 },
                new List<int> { 4733584 });
        }

        [TestMethod]
        [Owner("jthunter")]
        public void ArraySerializerTest()
        {
            TypeArgumentList typeArgs1 = new TypeArgumentList(new[] { new TypeArgument(LayoutType.Utf8) });
            HybridRowSerializerUnitTests.TestSerializer<List<string>, ArrayHybridRowSerializer<string, Utf8HybridRowSerializer>>(
                typeArgs1,
                new List<string> { "abc", "xyz" },
                new List<string> { "abc", "ghk" },
                new List<string> { "abc" });

            TypeArgumentList typeArgs2 = new TypeArgumentList(new[] { new TypeArgument(LayoutType.Int32) });
            HybridRowSerializerUnitTests.TestSerializer<List<int>, ArrayHybridRowSerializer<int, Int32HybridRowSerializer>>(
                typeArgs2,
                new List<int> { 123, 456 },
                new List<int> { 123, 789 },
                new List<int> { 4733584 });
        }

        [TestMethod]
        [Owner("jthunter")]
        public void TypedTupleSerializerTest()
        {
            TypeArgumentList typeArgs1 = new TypeArgumentList(
                new[]
                {
                    new TypeArgument(LayoutType.Utf8),
                    new TypeArgument(LayoutType.Int32),
                });
            HybridRowSerializerUnitTests
                .TestSerializer<(string, int), TypedTupleHybridRowSerializer<string, Utf8HybridRowSerializer, int, Int32HybridRowSerializer>
                >(typeArgs1, ("abc", 123), ("xyz", 123), ("abc", 456));

            TypeArgumentList typeArgs2 = new TypeArgumentList(
                new[]
                {
                    new TypeArgument(LayoutType.Int64),
                    new TypeArgument(LayoutType.Int32),
                    new TypeArgument(LayoutType.DateTime),
                });
            HybridRowSerializerUnitTests.TestSerializer<(long, int, DateTime), TypedTupleHybridRowSerializer<
                    long, Int64HybridRowSerializer,
                    int, Int32HybridRowSerializer,
                    DateTime, DateTimeHybridRowSerializer>
            >(typeArgs2, (789, 123, new DateTime(456)), (654, 123, new DateTime(456)), (789, 123, new DateTime(789)));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void NullableSerializerTest()
        {
            TypeArgumentList typeArgs1 = new TypeArgumentList(new[] { new TypeArgument(LayoutType.Int32) });
            HybridRowSerializerUnitTests.TestSerializer<int?, NullableHybridRowSerializer<int?, int, Int32HybridRowSerializer>>(
                typeArgs1,
                123,
                456,
                null);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void MapSerializerTest()
        {
            TypeArgumentList typeArgs1 = new TypeArgumentList(
                new[]
                {
                    new TypeArgument(LayoutType.Utf8),
                    new TypeArgument(LayoutType.Int32),
                });

            HybridRowSerializerUnitTests.TestSerializer<Dictionary<string, int>,
                TypedMapHybridRowSerializer<string, Utf8HybridRowSerializer, int, Int32HybridRowSerializer>>(
                typeArgs1,
                new Dictionary<string, int> { { "abc", 123 } },
                new Dictionary<string, int> { { "xyz", 123 } },
                new Dictionary<string, int> { { "abc", 456 } });
        }

        private static void TestSerializer<T, TS>(TypeArgumentList typeArgs, T t1, T t2, T t3)
            where TS : IHybridRowSerializer<T>
        {
            LayoutResolver resolver = SchemasHrSchema.LayoutResolver;
            Layout layout = resolver.Resolve(SystemSchema.EmptySchemaId);

            MemorySpanResizer<byte> resizer = new MemorySpanResizer<byte>();
            RowBuffer row = new RowBuffer(0, resizer);
            row.InitLayout(HybridRowVersion.V1, layout, resolver);

            RowCursor root = RowCursor.Create(ref row);
            ResultAssert.IsSuccess(default(TS).Write(ref row, ref root.Clone(out RowCursor _).Find(ref row, "a"), false, typeArgs, t1));
            Result r2 = default(TS).Read(ref row, ref root.Clone(out RowCursor _).Find(ref row, "a"), false, out T v1);
            ResultAssert.IsSuccess(r2);

            Assert.IsTrue(default(TS).Comparer.Equals(t1, v1));
            Assert.AreEqual(default(TS).Comparer.GetHashCode(t1), default(TS).Comparer.GetHashCode(v1));
            Assert.AreNotEqual(default(TS).Comparer.Equals(t1), 0);

            Assert.IsFalse(default(TS).Comparer.Equals(t1, t2));
            Assert.AreNotEqual(default(TS).Comparer.GetHashCode(t1), default(TS).Comparer.GetHashCode(t2));

            Assert.IsFalse(default(TS).Comparer.Equals(t1, t3));
            Assert.AreNotEqual(default(TS).Comparer.GetHashCode(t1), default(TS).Comparer.GetHashCode(t3));
        }
    }
}
