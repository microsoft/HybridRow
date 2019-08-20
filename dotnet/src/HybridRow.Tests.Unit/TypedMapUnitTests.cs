// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(TypedMapUnitTests.SchemaFile, "TestData")]
    public sealed class TypedMapUnitTests
    {
        private const string SchemaFile = @"TestData\MovieSchema.json";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace counterSchema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(TypedMapUnitTests.SchemaFile);
            this.counterSchema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.counterSchema);
            this.layout = this.resolver.Resolve(this.counterSchema.Schemas.Find(x => x.Name == "Movie").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateMovies()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            // ReSharper disable StringLiteralTypo
            Movie t1 = new Movie
            {
                Cast = new Dictionary<string, string> { { "Mark", "Luke" }, { "Harrison", "Han" }, { "Carrie", "Leia" } },
                Stats = new Dictionary<Guid, double>
                {
                    { Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"), 11000000.00 },
                    { Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"), 1554475.00 },
                },
                Related = new Dictionary<string, Dictionary<long, string>>
                {
                    { "Mark", new Dictionary<long, string> { { 103359, "Joker" }, { 131646, "Merlin" } } },
                    { "Harrison", new Dictionary<long, string> { { 0082971, "Indy" }, { 83658, "Deckard" } } },
                },
                Revenue = new Dictionary<DateTime, Earnings>
                {
                    { DateTime.Parse("05/25/1977"), new Earnings { Domestic = 307263857M, Worldwide = 100000M } },
                    { DateTime.Parse("08/13/1982"), new Earnings { Domestic = 15476285M, Worldwide = 200000M } },
                    { DateTime.Parse("01/31/1997"), new Earnings { Domestic = 138257865M, Worldwide = 300000M } },
                },
            };

            // ReSharper restore StringLiteralTypo
            this.WriteMovie(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);
            Movie t2 = this.ReadMovie(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.AreEqual(t1, t2);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void PreventUpdatesInNonUpdatableScope()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RowCursor root = RowCursor.Create(ref row);

            // Write a map and then try to write directly into it.
            Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
            root.Clone(out RowCursor mapScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref mapScope, c.TypeArgs, out mapScope));
            ResultAssert.InsufficientPermissions(
                TypedMapUnitTests.WriteKeyValue(ref row, ref mapScope, c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
            root.Clone(out RowCursor tempCursor).Find(ref row, "cast.0");
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor));
            root.Clone(out tempCursor).Find(ref row, "cast.0");
            ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(ref row, ref tempCursor, c.TypeArgs, out KeyValuePair<string, string> _));

            // Write a map of maps, successfully insert an empty map into it, and then try to write directly to the inner map.
            Assert.IsTrue(this.layout.TryFind("related", out c));
            root.Clone(out mapScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref mapScope, c.TypeArgs, out mapScope));
            LayoutIndexedScope tupleLayout = c.TypeAs<LayoutUniqueScope>().FieldType(ref mapScope).TypeAs<LayoutIndexedScope>();
            root.Clone(out tempCursor).Find(ref row, "related.0");
            ResultAssert.IsSuccess(tupleLayout.WriteScope(ref row, ref tempCursor, c.TypeArgs, out RowCursor tupleScope));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tupleScope, "Mark"));
            Assert.IsTrue(tupleScope.MoveNext(ref row));
            TypeArgument valueType = c.TypeArgs[1];
            LayoutUniqueScope valueLayout = valueType.Type.TypeAs<LayoutUniqueScope>();
            ResultAssert.IsSuccess(valueLayout.WriteScope(ref row, ref tupleScope, valueType.TypeArgs, out RowCursor innerScope));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor));
            Assert.IsTrue(mapScope.MoveNext(ref row));
            ResultAssert.IsSuccess(tupleLayout.ReadScope(ref row, ref mapScope, out tupleScope));
            Assert.IsTrue(tupleScope.MoveNext(ref row));

            // Skip key.
            Assert.IsTrue(tupleScope.MoveNext(ref row));
            ResultAssert.IsSuccess(valueLayout.ReadScope(ref row, ref tupleScope, out innerScope));
            TypeArgument itemType = valueType.TypeArgs[0];
            Assert.IsFalse(innerScope.MoveNext(ref row));
            ResultAssert.InsufficientPermissions(itemType.Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref innerScope, 1));
            ResultAssert.InsufficientPermissions(itemType.Type.TypeAs<LayoutInt64>().DeleteSparse(ref row, ref innerScope));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void PreventUniquenessViolations()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RowCursor root = RowCursor.Create(ref row);

            Movie t1 = new Movie
            {
                Cast = new Dictionary<string, string> { { "Mark", "Luke" } },
                Stats = new Dictionary<Guid, double>
                {
                    { Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"), 11000000.00 },
                },
                Related = new Dictionary<string, Dictionary<long, string>>
                {
                    { "Mark", new Dictionary<long, string> { { 103359, "Joker" } } },
                },
                Revenue = new Dictionary<DateTime, Earnings>
                {
                    { DateTime.Parse("05/25/1977"), new Earnings { Domestic = 307263857M, Worldwide = 100000M } },
                },
            };

            RowCursor rc1 = RowCursor.Create(ref row);
            this.WriteMovie(ref row, ref rc1, t1);

            // Attempt to insert duplicate items in existing sets.
            Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
            root.Clone(out RowCursor mapScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref mapScope, out mapScope));
            root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, KeyValuePair.Create("Mark", "Luke")));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Insert));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(ref row, ref tempCursor, c.TypeArgs, out KeyValuePair<string, string> _));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(
                TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, KeyValuePair.Create("Mark", "Joker")));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Insert));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(ref row, ref tempCursor, c.TypeArgs, out KeyValuePair<string, string> _));

            Assert.IsTrue(this.layout.TryFind("stats", out c));
            root.Clone(out mapScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref mapScope, out mapScope));
            KeyValuePair<Guid, double> pair = KeyValuePair.Create(Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"), 11000000.00);
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Insert));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.NotFound(TypedMapUnitTests.ReadKeyValue(ref row, ref tempCursor, c.TypeArgs, out pair));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void FindInMap()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RowCursor root = RowCursor.Create(ref row);

            Movie t1 = new Movie
            {
                Cast = new Dictionary<string, string> { { "Mark", "Luke" }, { "Harrison", "Han" }, { "Carrie", "Leia" } },
            };
            RowCursor rc1 = RowCursor.Create(ref row);
            this.WriteMovie(ref row, ref rc1, t1);

            // Attempt to find each item in turn.
            Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
            root.Clone(out RowCursor mapScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref mapScope, out mapScope));
            foreach (string key in t1.Cast.Keys)
            {
                KeyValuePair<string, string> pair = new KeyValuePair<string, string>(key, "map lookup matches only on key");
                root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref mapScope, ref tempCursor, out RowCursor findScope));
                ResultAssert.IsSuccess(
                    TypedMapUnitTests.ReadKeyValue(ref row, ref findScope, c.TypeArgs, out KeyValuePair<string, string> foundPair));
                Assert.AreEqual(key, foundPair.Key, $"Failed to find t1.Cast[{key}]");
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void UpdateInMap()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RowCursor root = RowCursor.Create(ref row);

            List<string> expected = new List<string> { "Mark", "Harrison", "Carrie", };

            foreach (IEnumerable<string> permutation in expected.Permute())
            {
                Movie t1 = new Movie
                {
                    Cast = new Dictionary<string, string> { { "Mark", "Luke" }, { "Harrison", "Han" }, { "Carrie", "Leia" } },
                };
                this.WriteMovie(ref row, ref root.Clone(out RowCursor _), t1);

                // Attempt to find each item in turn and then delete it.
                Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
                root.Clone(out RowCursor mapScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref mapScope, out mapScope));
                foreach (string key in permutation)
                {
                    // Verify it is already there.
                    KeyValuePair<string, string> pair = new KeyValuePair<string, string>(key, "map lookup matches only on key");
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref mapScope, ref tempCursor, out RowCursor findScope));

                    // Insert it again with update.
                    KeyValuePair<string, string> updatePair = new KeyValuePair<string, string>(key, "update value");
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, updatePair));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Update));

                    // Verify that the value was updated.
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref mapScope, ref tempCursor, out findScope));
                    ResultAssert.IsSuccess(
                        TypedMapUnitTests.ReadKeyValue(ref row, ref findScope, c.TypeArgs, out KeyValuePair<string, string> foundPair));
                    Assert.AreEqual(key, foundPair.Key);
                    Assert.AreEqual(updatePair.Value, foundPair.Value);

                    // Insert it again with upsert.
                    updatePair = new KeyValuePair<string, string>(key, "upsert value");
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, updatePair));

                    // ReSharper disable once RedundantArgumentDefaultValue
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Upsert));

                    // Verify that the value was upserted.
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref mapScope, ref tempCursor, out findScope));
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(ref row, ref findScope, c.TypeArgs, out foundPair));
                    Assert.AreEqual(key, foundPair.Key);
                    Assert.AreEqual(updatePair.Value, foundPair.Value);

                    // Insert it again with insert (fail: exists).
                    updatePair = new KeyValuePair<string, string>(key, "insert value");
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, updatePair));
                    ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.Insert));

                    // Insert it again with insert at (fail: disallowed).
                    updatePair = new KeyValuePair<string, string>(key, "insertAt value");
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, updatePair));
                    ResultAssert.TypeConstraint(
                        c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref mapScope, ref tempCursor, UpdateOptions.InsertAt));
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void FindAndDelete()
        {
            RowBuffer row = new RowBuffer(TypedMapUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
            RowCursor root = RowCursor.Create(ref row);

            List<string> expected = new List<string> { "Mark", "Harrison", "Carrie", };

            foreach (IEnumerable<string> permutation in expected.Permute())
            {
                Movie t1 = new Movie
                {
                    Cast = new Dictionary<string, string> { { "Mark", "Luke" }, { "Harrison", "Han" }, { "Carrie", "Leia" } },
                };
                this.WriteMovie(ref row, ref root.Clone(out RowCursor _), t1);

                // Attempt to find each item in turn and then delete it.
                Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
                root.Clone(out RowCursor mapScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref mapScope, out mapScope));
                foreach (string key in permutation)
                {
                    KeyValuePair<string, string> pair = new KeyValuePair<string, string>(key, "map lookup matches only on key");
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, pair));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref mapScope, ref tempCursor, out RowCursor findScope));
                    TypeArgument tupleType = c.TypeAs<LayoutUniqueScope>().FieldType(ref mapScope);
                    ResultAssert.IsSuccess(tupleType.TypeAs<LayoutIndexedScope>().DeleteScope(ref row, ref findScope));
                }
            }
        }

        private static Result WriteKeyValue<TKey, TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            KeyValuePair<TKey, TValue> pair)
        {
            LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
            Result r = tupleLayout.WriteScope(ref row, ref scope, typeArgs, out RowCursor tupleScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = typeArgs[0].Type.TypeAs<LayoutType<TKey>>().WriteSparse(ref row, ref tupleScope, pair.Key);
            if (r != Result.Success)
            {
                return r;
            }

            tupleScope.MoveNext(ref row);
            r = typeArgs[1].Type.TypeAs<LayoutType<TValue>>().WriteSparse(ref row, ref tupleScope, pair.Value);
            if (r != Result.Success)
            {
                return r;
            }

            return Result.Success;
        }

        private static Result ReadKeyValue<TKey, TValue>(
            ref RowBuffer row,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            out KeyValuePair<TKey, TValue> pair)
        {
            pair = default;
            LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
            Result r = tupleLayout.ReadScope(ref row, ref scope, out RowCursor tupleScope);
            if (r != Result.Success)
            {
                return r;
            }

            tupleScope.MoveNext(ref row);
            r = typeArgs[0].Type.TypeAs<LayoutType<TKey>>().ReadSparse(ref row, ref tupleScope, out TKey key);
            if (r != Result.Success)
            {
                return r;
            }

            tupleScope.MoveNext(ref row);
            r = typeArgs[1].Type.TypeAs<LayoutType<TValue>>().ReadSparse(ref row, ref tupleScope, out TValue value);
            if (r != Result.Success)
            {
                return r;
            }

            pair = new KeyValuePair<TKey, TValue>(key, value);
            return Result.Success;
        }

        private void WriteMovie(ref RowBuffer row, ref RowCursor root, Movie value)
        {
            LayoutColumn c;

            if (value.Cast != null)
            {
                Assert.IsTrue(this.layout.TryFind("cast", out c));
                root.Clone(out RowCursor castScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref castScope, c.TypeArgs, out castScope));
                foreach (KeyValuePair<string, string> item in value.Cast)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref castScope, ref tempCursor));
                }
            }

            if (value.Stats != null)
            {
                Assert.IsTrue(this.layout.TryFind("stats", out c));
                root.Clone(out RowCursor statsScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref statsScope, c.TypeArgs, out statsScope));
                foreach (KeyValuePair<Guid, double> item in value.Stats)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor, c.TypeArgs, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref statsScope, ref tempCursor));
                }
            }

            if (value.Related != null)
            {
                Assert.IsTrue(this.layout.TryFind("related", out c));
                root.Clone(out RowCursor relatedScoped).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref relatedScoped, c.TypeArgs, out relatedScoped));
                foreach (KeyValuePair<string, Dictionary<long, string>> item in value.Related)
                {
                    Assert.IsTrue(item.Value != null);

                    LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                    root.Clone(out RowCursor tempCursor1).Find(ref row, "related.0");
                    ResultAssert.IsSuccess(tupleLayout.WriteScope(ref row, ref tempCursor1, c.TypeArgs, out RowCursor tupleScope));
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tupleScope, item.Key));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    TypeArgument valueType = c.TypeArgs[1];
                    LayoutUniqueScope valueLayout = valueType.Type.TypeAs<LayoutUniqueScope>();
                    ResultAssert.IsSuccess(valueLayout.WriteScope(ref row, ref tupleScope, valueType.TypeArgs, out RowCursor innerScope));
                    foreach (KeyValuePair<long, string> innerItem in item.Value)
                    {
                        root.Clone(out RowCursor tempCursor2).Find(ref row, "related.0.0");
                        ResultAssert.IsSuccess(TypedMapUnitTests.WriteKeyValue(ref row, ref tempCursor2, valueType.TypeArgs, innerItem));
                        ResultAssert.IsSuccess(valueLayout.MoveField(ref row, ref innerScope, ref tempCursor2));
                    }

                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref relatedScoped, ref tempCursor1));
                }
            }

            if (value.Revenue != null)
            {
                Assert.IsTrue(this.layout.TryFind("revenue", out c));
                root.Clone(out RowCursor revenueScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref revenueScope, c.TypeArgs, out revenueScope));
                foreach (KeyValuePair<DateTime, Earnings> item in value.Revenue)
                {
                    Assert.IsTrue(item.Value != null);

                    LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                    root.Clone(out RowCursor tempCursor1).Find(ref row, "revenue.0");
                    ResultAssert.IsSuccess(tupleLayout.WriteScope(ref row, ref tempCursor1, c.TypeArgs, out RowCursor tupleScope));
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutDateTime>().WriteSparse(ref row, ref tupleScope, item.Key));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    TypeArgument valueType = c.TypeArgs[1];
                    LayoutUDT valueLayout = valueType.Type.TypeAs<LayoutUDT>();
                    ResultAssert.IsSuccess(valueLayout.WriteScope(ref row, ref tupleScope, valueType.TypeArgs, out RowCursor itemScope));
                    TypedMapUnitTests.WriteEarnings(ref row, ref itemScope, valueType.TypeArgs, item.Value);

                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref revenueScope, ref tempCursor1));
                }
            }
        }

        private Movie ReadMovie(ref RowBuffer row, ref RowCursor root)
        {
            Movie value = new Movie();

            Assert.IsTrue(this.layout.TryFind("cast", out LayoutColumn c));
            root.Clone(out RowCursor castScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref castScope, out castScope) == Result.Success)
            {
                value.Cast = new Dictionary<string, string>();
                while (castScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(ref row, ref castScope, c.TypeArgs, out KeyValuePair<string, string> item));
                    value.Cast.Add(item.Key, item.Value);
                }
            }

            Assert.IsTrue(this.layout.TryFind("stats", out c));
            root.Clone(out RowCursor statsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref statsScope, out statsScope) == Result.Success)
            {
                value.Stats = new Dictionary<Guid, double>();
                while (statsScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(TypedMapUnitTests.ReadKeyValue(ref row, ref statsScope, c.TypeArgs, out KeyValuePair<Guid, double> item));
                    value.Stats.Add(item.Key, item.Value);
                }
            }

            Assert.IsTrue(this.layout.TryFind("related", out c));
            root.Clone(out RowCursor relatedScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref relatedScope, out relatedScope) == Result.Success)
            {
                value.Related = new Dictionary<string, Dictionary<long, string>>();
                TypeArgument keyType = c.TypeArgs[0];
                TypeArgument valueType = c.TypeArgs[1];
                LayoutUtf8 keyLayout = keyType.Type.TypeAs<LayoutUtf8>();
                LayoutUniqueScope valueLayout = valueType.Type.TypeAs<LayoutUniqueScope>();
                while (relatedScope.MoveNext(ref row))
                {
                    LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                    ResultAssert.IsSuccess(tupleLayout.ReadScope(ref row, ref relatedScope, out RowCursor tupleScope));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(keyLayout.ReadSparse(ref row, ref tupleScope, out string itemKey));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(valueLayout.ReadScope(ref row, ref tupleScope, out RowCursor itemValueScope));
                    Dictionary<long, string> itemValue = new Dictionary<long, string>();
                    while (itemValueScope.MoveNext(ref row))
                    {
                        ResultAssert.IsSuccess(
                            TypedMapUnitTests.ReadKeyValue(
                                ref row,
                                ref itemValueScope,
                                valueType.TypeArgs,
                                out KeyValuePair<long, string> innerItem));
                        itemValue.Add(innerItem.Key, innerItem.Value);
                    }

                    value.Related.Add(itemKey, itemValue);
                }
            }

            Assert.IsTrue(this.layout.TryFind("revenue", out c));
            root.Clone(out RowCursor revenueScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref revenueScope, out revenueScope) == Result.Success)
            {
                value.Revenue = new Dictionary<DateTime, Earnings>();
                TypeArgument keyType = c.TypeArgs[0];
                TypeArgument valueType = c.TypeArgs[1];
                LayoutDateTime keyLayout = keyType.Type.TypeAs<LayoutDateTime>();
                LayoutUDT valueLayout = valueType.Type.TypeAs<LayoutUDT>();
                while (revenueScope.MoveNext(ref row))
                {
                    LayoutIndexedScope tupleLayout = LayoutType.TypedTuple;
                    ResultAssert.IsSuccess(tupleLayout.ReadScope(ref row, ref revenueScope, out RowCursor tupleScope));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(keyLayout.ReadSparse(ref row, ref tupleScope, out DateTime itemKey));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(valueLayout.ReadScope(ref row, ref tupleScope, out RowCursor itemValueScope));
                    Earnings itemValue = TypedMapUnitTests.ReadEarnings(ref row, ref itemValueScope);

                    value.Revenue.Add(itemKey, itemValue);
                }
            }

            return value;
        }

        private static void WriteEarnings(ref RowBuffer row, ref RowCursor udtScope, TypeArgumentList typeArgs, Earnings m)
        {
            Layout udt = row.Resolver.Resolve(typeArgs.SchemaId);
            Assert.IsTrue(udt.TryFind("domestic", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutDecimal>().WriteFixed(ref row, ref udtScope, c, m.Domestic));
            Assert.IsTrue(udt.TryFind("worldwide", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutDecimal>().WriteFixed(ref row, ref udtScope, c, m.Worldwide));
        }

        private static Earnings ReadEarnings(ref RowBuffer row, ref RowCursor udtScope)
        {
            Layout udt = udtScope.Layout;
            Earnings m = new Earnings();
            Assert.IsTrue(udt.TryFind("domestic", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutDecimal>().ReadFixed(ref row, ref udtScope, c, out m.Domestic));
            Assert.IsTrue(udt.TryFind("worldwide", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutDecimal>().ReadFixed(ref row, ref udtScope, c, out m.Worldwide));
            return m;
        }

        private static class KeyValuePair
        {
            public static KeyValuePair<TKey, TValue> Create<TKey, TValue>(TKey key, TValue value)
            {
                return new KeyValuePair<TKey, TValue>(key, value);
            }
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class Movie
        {
            public Dictionary<string, string> Cast;
            public Dictionary<Guid, double> Stats;
            public Dictionary<string, Dictionary<long, string>> Related;
            public Dictionary<DateTime, Earnings> Revenue;

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

                return obj is Movie movie && this.Equals(movie);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    int hashCode = 0;
                    hashCode = (hashCode * 397) ^ (this.Cast?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Stats?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Related?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Revenue?.GetHashCode() ?? 0);
                    return hashCode;
                }
            }

            private static bool NestedMapEquals<TKey1, TKey2, TValue>(
                Dictionary<TKey1, Dictionary<TKey2, TValue>> left,
                Dictionary<TKey1, Dictionary<TKey2, TValue>> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                foreach (KeyValuePair<TKey1, Dictionary<TKey2, TValue>> item in left)
                {
                    if (!right.TryGetValue(item.Key, out Dictionary<TKey2, TValue> value))
                    {
                        return false;
                    }

                    if (!Movie.MapEquals(item.Value, value))
                    {
                        return false;
                    }
                }

                return true;
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

            private bool Equals(Movie other)
            {
                return (object.ReferenceEquals(this.Cast, other.Cast) ||
                        ((this.Cast != null) && (other.Cast != null) && Movie.MapEquals(this.Cast, other.Cast))) &&
                       (object.ReferenceEquals(this.Stats, other.Stats) ||
                        ((this.Stats != null) && (other.Stats != null) && Movie.MapEquals(this.Stats, other.Stats))) &&
                       (object.ReferenceEquals(this.Related, other.Related) ||
                        ((this.Related != null) && (other.Related != null) && Movie.NestedMapEquals(this.Related, other.Related))) &&
                       (object.ReferenceEquals(this.Revenue, other.Revenue) ||
                        ((this.Revenue != null) && (other.Revenue != null) && Movie.MapEquals(this.Revenue, other.Revenue)));
            }
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class Earnings
        {
            public decimal Domestic;
            public decimal Worldwide;

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

                return obj is Earnings earnings && this.Equals(earnings);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    return (this.Domestic.GetHashCode() * 397) ^ this.Worldwide.GetHashCode();
                }
            }

            private bool Equals(Earnings other)
            {
                return this.Domestic == other.Domestic && this.Worldwide == other.Worldwide;
            }
        }
    }
}
