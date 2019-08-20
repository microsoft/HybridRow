// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

// ReSharper disable StringLiteralTypo
namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Linq;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(NullableUnitTests.SchemaFile, "TestData")]
    public sealed class NullableUnitTests
    {
        private const string SchemaFile = @"TestData\NullableSchema.json";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace schema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(NullableUnitTests.SchemaFile);
            this.schema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.schema);
            this.layout = this.resolver.Resolve(this.schema.Schemas.Find(x => x.Name == "Nullables").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateNullables()
        {
            RowBuffer row = new RowBuffer(NullableUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            Nullables t1 = new Nullables
            {
                NullBool = new List<bool?> { true, false, null },
                NullArray = new List<float?> { 1.2F, null, 3.0F },
                NullSet = new List<string> { null, "abc", "def" },
                NullTuple = new List<(int?, long?)>
                {
                    (1, 2), (null, 3), (4, null),
                    (null, null),
                },
                NullMap = new Dictionary<Guid, byte?>
                {
                    { Guid.Parse("{00000000-0000-0000-0000-000000000000}"), 1 },
                    { Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"), 20 },
                    { Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), null },
                },
            };

            this.WriteNullables(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);
            Nullables t2 = this.ReadNullables(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.AreEqual(t1, t2);
        }

        private static Result WriteNullable<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            TValue? item,
            out RowCursor nullableScope)
            where TValue : struct
        {
            return NullableUnitTests.WriteNullableImpl(ref row, ref scope, itemType, item.HasValue, item ?? default, out nullableScope);
        }

        private static Result WriteNullable<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            TValue item,
            out RowCursor nullableScope)
            where TValue : class
        {
            return NullableUnitTests.WriteNullableImpl(ref row, ref scope, itemType, item != null, item, out nullableScope);
        }

        private static Result WriteNullableImpl<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            bool hasValue,
            TValue item,
            out RowCursor nullableScope)
        {
            Result r = itemType.TypeAs<LayoutNullable>()
                .WriteScope(ref row, ref scope, itemType.TypeArgs, hasValue, out nullableScope);

            if (r != Result.Success)
            {
                return r;
            }

            if (hasValue)
            {
                r = itemType.TypeArgs[0].Type.TypeAs<LayoutType<TValue>>().WriteSparse(ref row, ref nullableScope, item);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        private static Result ReadNullable<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            out TValue? item,
            out RowCursor nullableScope)
            where TValue : struct
        {
            Result r = NullableUnitTests.ReadNullableImpl(ref row, ref scope, itemType, out TValue value, out nullableScope);
            if ((r != Result.Success) && (r != Result.NotFound))
            {
                item = null;
                return r;
            }

            item = (r == Result.NotFound) ? (TValue?)null : value;
            return Result.Success;
        }

        private static Result ReadNullable<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            out TValue item,
            out RowCursor nullableScope)
            where TValue : class
        {
            Result r = NullableUnitTests.ReadNullableImpl(ref row, ref scope, itemType, out item, out nullableScope);
            return (r == Result.NotFound) ? Result.Success : r;
        }

        private static Result ReadNullableImpl<TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgument itemType,
            out TValue item,
            out RowCursor nullableScope)
        {
            Result r = itemType.Type.TypeAs<LayoutNullable>().ReadScope(ref row, ref scope, out nullableScope);
            if (r != Result.Success)
            {
                item = default;
                return r;
            }

            if (nullableScope.MoveNext(ref row))
            {
                ResultAssert.IsSuccess(LayoutNullable.HasValue(ref row, ref nullableScope));
                return itemType.TypeArgs[0].Type.TypeAs<LayoutType<TValue>>().ReadSparse(ref row, ref nullableScope, out item);
            }

            ResultAssert.NotFound(LayoutNullable.HasValue(ref row, ref nullableScope));
            item = default;
            return Result.NotFound;
        }

        private void WriteNullables(ref RowBuffer row, ref RowCursor root, Nullables value)
        {
            LayoutColumn c;

            if (value.NullBool != null)
            {
                Assert.IsTrue(this.layout.TryFind("nullbool", out c));
                root.Clone(out RowCursor outerScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref outerScope, c.TypeArgs, out outerScope));
                foreach (bool? item in value.NullBool)
                {
                    ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(ref row, ref outerScope, c.TypeArgs[0], item, out RowCursor innerScope));
                    Assert.IsFalse(outerScope.MoveNext(ref row, ref innerScope));
                }
            }

            if (value.NullArray != null)
            {
                Assert.IsTrue(this.layout.TryFind("nullarray", out c));
                root.Clone(out RowCursor outerScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref outerScope, c.TypeArgs, out outerScope));
                foreach (float? item in value.NullArray)
                {
                    ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(ref row, ref outerScope, c.TypeArgs[0], item, out RowCursor innerScope));
                    Assert.IsFalse(outerScope.MoveNext(ref row, ref innerScope));
                }
            }

            if (value.NullSet != null)
            {
                Assert.IsTrue(this.layout.TryFind("nullset", out c));
                root.Clone(out RowCursor outerScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedSet>().WriteScope(ref row, ref outerScope, c.TypeArgs, out outerScope));
                foreach (string item in value.NullSet)
                {
                    RowCursor.CreateForAppend(ref row, out RowCursor temp).Find(ref row, string.Empty);
                    ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(ref row, ref temp, c.TypeArgs[0], item, out RowCursor _));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutTypedSet>().MoveField(ref row, ref outerScope, ref temp));
                }
            }

            if (value.NullTuple != null)
            {
                Assert.IsTrue(this.layout.TryFind("nulltuple", out c));
                root.Clone(out RowCursor outerScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref outerScope, c.TypeArgs, out outerScope));
                foreach ((int? item1, long? item2) in value.NullTuple)
                {
                    TypeArgument tupleType = c.TypeArgs[0];
                    ResultAssert.IsSuccess(
                        tupleType.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref outerScope, tupleType.TypeArgs, out RowCursor tupleScope));

                    ResultAssert.IsSuccess(
                        NullableUnitTests.WriteNullable(ref row, ref tupleScope, tupleType.TypeArgs[0], item1, out RowCursor nullableScope));

                    Assert.IsTrue(tupleScope.MoveNext(ref row, ref nullableScope));
                    ResultAssert.IsSuccess(NullableUnitTests.WriteNullable(ref row, ref tupleScope, tupleType.TypeArgs[1], item2, out nullableScope));
                    Assert.IsFalse(tupleScope.MoveNext(ref row, ref nullableScope));

                    Assert.IsFalse(outerScope.MoveNext(ref row, ref tupleScope));
                }
            }

            if (value.NullMap != null)
            {
                Assert.IsTrue(this.layout.TryFind("nullmap", out c));
                root.Clone(out RowCursor outerScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref outerScope, c.TypeArgs, out outerScope));
                foreach ((Guid key, byte? itemValue) in value.NullMap)
                {
                    TypeArgument tupleType = c.TypeAs<LayoutUniqueScope>().FieldType(ref outerScope);
                    RowCursor.CreateForAppend(ref row, out RowCursor temp).Find(ref row, string.Empty);
                    ResultAssert.IsSuccess(
                        tupleType.TypeAs<LayoutIndexedScope>().WriteScope(ref row, ref temp, tupleType.TypeArgs, out RowCursor tupleScope));

                    Guid? itemKey = key.Equals(Guid.Empty) ? (Guid?)null : key;
                    ResultAssert.IsSuccess(
                        NullableUnitTests.WriteNullable(ref row, ref tupleScope, tupleType.TypeArgs[0], itemKey, out RowCursor nullableScope));

                    Assert.IsTrue(tupleScope.MoveNext(ref row, ref nullableScope));
                    ResultAssert.IsSuccess(
                        NullableUnitTests.WriteNullable(ref row, ref tupleScope, tupleType.TypeArgs[1], itemValue, out nullableScope));

                    Assert.IsFalse(tupleScope.MoveNext(ref row, ref nullableScope));

                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref outerScope, ref temp));
                }
            }
        }

        private Nullables ReadNullables(ref RowBuffer row, ref RowCursor root)
        {
            Nullables value = new Nullables();

            Assert.IsTrue(this.layout.TryFind("nullbool", out LayoutColumn c));
            root.Clone(out RowCursor scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref scope, out scope) == Result.Success)
            {
                value.NullBool = new List<bool?>();
                RowCursor nullableScope = default;
                while (scope.MoveNext(ref row, ref nullableScope))
                {
                    ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(ref row, ref scope, c.TypeArgs[0], out bool? item, out nullableScope));
                    value.NullBool.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("nullarray", out c));
            root.Clone(out scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref scope, out scope) == Result.Success)
            {
                value.NullArray = new List<float?>();
                RowCursor nullableScope = default;
                while (scope.MoveNext(ref row, ref nullableScope))
                {
                    ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(ref row, ref scope, c.TypeArgs[0], out float? item, out nullableScope));
                    value.NullArray.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("nullset", out c));
            root.Clone(out scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedSet>().ReadScope(ref row, ref scope, out scope) == Result.Success)
            {
                value.NullSet = new List<string>();
                RowCursor nullableScope = default;
                while (scope.MoveNext(ref row, ref nullableScope))
                {
                    ResultAssert.IsSuccess(NullableUnitTests.ReadNullable(ref row, ref scope, c.TypeArgs[0], out string item, out nullableScope));
                    value.NullSet.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("nulltuple", out c));
            root.Clone(out scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref scope, out scope) == Result.Success)
            {
                value.NullTuple = new List<(int?, long?)>();
                RowCursor tupleScope = default;
                TypeArgument tupleType = c.TypeArgs[0];
                while (scope.MoveNext(ref row, ref tupleScope))
                {
                    ResultAssert.IsSuccess(tupleType.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref scope, out tupleScope));

                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(
                        NullableUnitTests.ReadNullable(ref row, ref tupleScope, tupleType.TypeArgs[0], out int? item1, out RowCursor nullableScope));
                    Assert.IsTrue(tupleScope.MoveNext(ref row, ref nullableScope));
                    ResultAssert.IsSuccess(
                        NullableUnitTests.ReadNullable(ref row, ref tupleScope, tupleType.TypeArgs[1], out long? item2, out nullableScope));

                    Assert.IsFalse(tupleScope.MoveNext(ref row, ref nullableScope));
                    value.NullTuple.Add((item1, item2));
                }
            }

            Assert.IsTrue(this.layout.TryFind("nullmap", out c));
            root.Clone(out scope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref scope, out scope) == Result.Success)
            {
                value.NullMap = new Dictionary<Guid, byte?>();
                RowCursor tupleScope = default;
                TypeArgument tupleType = c.TypeAs<LayoutUniqueScope>().FieldType(ref scope);
                while (scope.MoveNext(ref row, ref tupleScope))
                {
                    ResultAssert.IsSuccess(tupleType.TypeAs<LayoutIndexedScope>().ReadScope(ref row, ref scope, out tupleScope));

                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(
                        NullableUnitTests.ReadNullable(
                            ref row,
                            ref tupleScope,
                            tupleType.TypeArgs[0],
                            out Guid? itemKey,
                            out RowCursor nullableScope));

                    Assert.IsTrue(tupleScope.MoveNext(ref row, ref nullableScope));
                    ResultAssert.IsSuccess(
                        NullableUnitTests.ReadNullable(ref row, ref tupleScope, tupleType.TypeArgs[1], out byte? itemValue, out nullableScope));

                    Assert.IsFalse(tupleScope.MoveNext(ref row, ref nullableScope));
                    value.NullMap.Add(itemKey ?? Guid.Empty, itemValue);
                }
            }

            return value;
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class Nullables
        {
            public List<bool?> NullBool;
            public List<string> NullSet;
            public List<float?> NullArray;
            public List<(int?, long?)> NullTuple;
            public Dictionary<Guid, byte?> NullMap;

            public override bool Equals(object obj)
            {
                if (object.ReferenceEquals(null, obj))
                {
                    return false;
                }

                if (object.ReferenceEquals(this, obj))
                {
                    return true;
                }

                return obj is Nullables nullables && this.Equals(nullables);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    int hashCode = 0;
                    hashCode = (hashCode * 397) ^ (this.NullBool?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.NullSet?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.NullArray?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.NullTuple?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.NullMap?.GetHashCode() ?? 0);
                    return hashCode;
                }
            }

            private static bool MapEquals<TKey, TValue>(Dictionary<TKey, TValue> left, Dictionary<TKey, TValue> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                foreach (KeyValuePair<TKey, TValue> item in left)
                {
                    if (!right.TryGetValue(item.Key, out TValue value))
                    {
                        return false;
                    }

                    if (!item.Value.Equals(value))
                    {
                        return false;
                    }
                }

                return true;
            }

            private bool Equals(Nullables other)
            {
                return (object.ReferenceEquals(this.NullBool, other.NullBool) ||
                        ((this.NullBool != null) && (other.NullBool != null) && this.NullBool.SequenceEqual(other.NullBool))) &&
                       (object.ReferenceEquals(this.NullSet, other.NullSet) ||
                        ((this.NullSet != null) && (other.NullSet != null) && this.NullSet.SequenceEqual(other.NullSet))) &&
                       (object.ReferenceEquals(this.NullArray, other.NullArray) ||
                        ((this.NullArray != null) && (other.NullArray != null) && this.NullArray.SequenceEqual(other.NullArray))) &&
                       (object.ReferenceEquals(this.NullTuple, other.NullTuple) ||
                        ((this.NullTuple != null) && (other.NullTuple != null) && this.NullTuple.SequenceEqual(other.NullTuple))) &&
                       (object.ReferenceEquals(this.NullMap, other.NullMap) ||
                        ((this.NullMap != null) && (other.NullMap != null) && Nullables.MapEquals(this.NullMap, other.NullMap)));
            }
        }
    }
}
