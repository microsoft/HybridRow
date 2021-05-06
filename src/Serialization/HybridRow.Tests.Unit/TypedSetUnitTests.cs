// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    [DeploymentItem(TypedSetUnitTests.SchemaFile, "TestData")]
    public sealed class TypedSetUnitTests
    {
        private const string SchemaFile = @"TestData\TodoSchema.hrschema";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace counterSchema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            this.counterSchema = SchemaUtil.LoadFromHrSchema(TypedSetUnitTests.SchemaFile);
            this.resolver = new LayoutResolverNamespace(this.counterSchema);
            this.layout = this.resolver.Resolve(this.counterSchema.Schemas.Find(x => x.Name == "Todo").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateTodos()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            Todo t1 = new Todo
            {
                Attendees = new List<string> { "jason", "janice", "joshua" },
                Projects = new List<Guid>
                {
                    Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"),
                    Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"),
                    Guid.Parse("{B7BC39C2-1A2D-4EAF-8F33-ED976872D876}"),
                    Guid.Parse("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}"),
                },
                Checkboxes = new List<bool> { true, false },
                Prices = new List<List<float>>
                {
                    new List<float> { 1.2F, 3.0F },
                    new List<float> { 4.1F, 5.7F },
                    new List<float> { 7.3F, 8.12F, 9.14F },
                },
                Nested = new List<List<List<int>>>
                {
                    new List<List<int>> { new List<int> { 1, 2 } },
                    new List<List<int>> { new List<int> { 3, 4 } },
                    new List<List<int>> { new List<int> { 5, 6 } },
                },
                Shopping = new List<ShoppingItem>
                {
                    new ShoppingItem { Label = "milk", Count = 1 },
                    new ShoppingItem { Label = "broccoli", Count = 2 },
                    new ShoppingItem { Label = "steak", Count = 6 },
                },
                Work = new List<Tuple<bool, ulong>>
                {
                    Tuple.Create(false, 10000UL),
                    Tuple.Create(true, 49053UL),
                    Tuple.Create(false, 53111UL),
                },
            };

            this.WriteTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);
            Todo t2 = this.ReadTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.AreEqual(t1, t2);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void PreventUpdatesInNonUpdatableScope()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            // Write a set and then try to write directly into it.
            Assert.IsTrue(this.layout.TryFind("attendees", out LayoutColumn c));
            RowCursor.Create(ref row, out RowCursor setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref setScope, c.TypeArgs, out setScope));
            ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref setScope, "foo"));
            RowCursor.Create(ref row, out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tempCursor, "foo"));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor));
            ResultAssert.InsufficientPermissions(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref setScope, "foo"));
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().DeleteSparse(ref row, ref setScope));

            // Write a set of sets, successfully insert an empty set into it, and then try to write directly to the inner set.
            Assert.IsTrue(this.layout.TryFind("prices", out c));
            RowCursor.Create(ref row, out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref setScope, c.TypeArgs, out setScope));
            TypeArgument innerType = c.TypeArgs[0];
            TypeArgument itemType = innerType.TypeArgs[0];
            LayoutUniqueScope innerLayout = innerType.Type.TypeAs<LayoutUniqueScope>();
            RowCursor.Create(ref row, out RowCursor tempCursor1).Find(ref row, "prices.0");
            ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor1, innerType.TypeArgs, out RowCursor innerScope));
            RowCursor.Create(ref row, out RowCursor tempCursor2).Find(ref row, "prices.0.0");
            ResultAssert.IsSuccess(itemType.Type.TypeAs<LayoutFloat32>().WriteSparse(ref row, ref tempCursor2, 1.0F));
            ResultAssert.IsSuccess(innerLayout.MoveField(ref row, ref innerScope, ref tempCursor2));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor1));
            Assert.IsTrue(setScope.MoveNext(ref row));
            ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref setScope, out innerScope));
            ResultAssert.InsufficientPermissions(itemType.Type.TypeAs<LayoutFloat32>().WriteSparse(ref row, ref innerScope, 1.0F));
            ResultAssert.InsufficientPermissions(itemType.Type.TypeAs<LayoutFloat32>().DeleteSparse(ref row, ref innerScope));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void PreventUniquenessViolations()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            Todo t1 = new Todo
            {
                Attendees = new List<string> { "jason" },
                Projects = new List<Guid>
                {
                    Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"),
                },
                Prices = new List<List<float>>
                {
                    new List<float> { 1.2F, 3.0F },
                },
                Shopping = new List<ShoppingItem>
                {
                    new ShoppingItem { Label = "milk", Count = 1 },
                },
                Work = new List<Tuple<bool, ulong>>
                {
                    Tuple.Create(false, 10000UL),
                },
            };

            this.WriteTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);

            // Attempt to insert duplicate items in existing sets.
            RowCursor root = RowCursor.Create(ref row);
            Assert.IsTrue(this.layout.TryFind("attendees", out LayoutColumn c));
            root.Clone(out RowCursor setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tempCursor, t1.Attendees[0]));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Insert));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tempCursor, t1.Attendees[0]));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref tempCursor, out RowCursor _));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.NotFound(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref tempCursor, out string _));

            Assert.IsTrue(this.layout.TryFind("projects", out c));
            root.Clone(out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, t1.Projects[0]));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Insert));

            // Attempt to move a duplicate set into a set of sets.
            Assert.IsTrue(this.layout.TryFind("prices", out c));
            root.Clone(out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            TypeArgument innerType = c.TypeArgs[0];
            LayoutUniqueScope innerLayout = innerType.Type.TypeAs<LayoutUniqueScope>();
            root.Clone(out RowCursor tempCursor1).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor1, innerType.TypeArgs, out RowCursor innerScope));
            foreach (float innerItem in t1.Prices[0])
            {
                LayoutFloat32 itemLayout = innerType.TypeArgs[0].Type.TypeAs<LayoutFloat32>();
                root.Clone(out RowCursor tempCursor2).Find(ref row, "prices.0.0");
                ResultAssert.IsSuccess(itemLayout.WriteSparse(ref row, ref tempCursor2, innerItem));
                ResultAssert.IsSuccess(innerLayout.MoveField(ref row, ref innerScope, ref tempCursor2));
            }

            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor1, UpdateOptions.Insert));

            // Attempt to move a duplicate UDT into a set of UDT.
            Assert.IsTrue(this.layout.TryFind("shopping", out c));
            root.Clone(out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            LayoutUDT udtLayout = c.TypeArgs[0].Type.TypeAs<LayoutUDT>();
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(udtLayout.WriteScope(ref row, ref tempCursor, c.TypeArgs[0].TypeArgs, out RowCursor udtScope));
            TypedSetUnitTests.WriteShoppingItem(ref row, ref udtScope, c.TypeArgs[0].TypeArgs, t1.Shopping[0]);
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Insert));

            // Attempt to move a duplicate tuple into a set of tuple.
            Assert.IsTrue(this.layout.TryFind("work", out c));
            root.Clone(out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            innerType = c.TypeArgs[0];
            LayoutIndexedScope tupleLayout = innerType.Type.TypeAs<LayoutIndexedScope>();
            root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
            ResultAssert.IsSuccess(tupleLayout.WriteScope(ref row, ref tempCursor, innerType.TypeArgs, out RowCursor tupleScope));
            ResultAssert.IsSuccess(innerType.TypeArgs[0].Type.TypeAs<LayoutBoolean>().WriteSparse(ref row, ref tupleScope, t1.Work[0].Item1));
            Assert.IsTrue(tupleScope.MoveNext(ref row));
            ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().WriteSparse(ref row, ref tupleScope, t1.Work[0].Item2));
            ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Insert));
        }

        [TestMethod]
        [Owner("jthunter")]
        public void FindInSet()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            Todo t1 = new Todo
            {
                Attendees = new List<string> { "jason", "janice", "joshua" },
                Prices = new List<List<float>>
                {
                    new List<float> { 1.2F, 3.0F },
                    new List<float> { 4.1F, 5.7F },
                    new List<float> { 7.3F, 8.12F, 9.14F },
                },
            };

            this.WriteTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);

            // Attempt to find each item in turn.
            RowCursor root = RowCursor.Create(ref row);
            Assert.IsTrue(this.layout.TryFind("attendees", out LayoutColumn c));
            root.Clone(out RowCursor setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            for (int i = 0; i < t1.Attendees.Count; i++)
            {
                root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tempCursor, t1.Attendees[i]));
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref tempCursor, out RowCursor findScope));
                Assert.AreEqual(i, findScope.Index, $"Failed to find t1.Attendees[{i}]");
            }

            Assert.IsTrue(this.layout.TryFind("prices", out c));
            root.Clone(out setScope).Find(ref row, c.Path);
            ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
            TypeArgument innerType = c.TypeArgs[0];
            TypeArgument itemType = innerType.TypeArgs[0];
            LayoutUniqueScope innerLayout = innerType.Type.TypeAs<LayoutUniqueScope>();
            for (int i = 0; i < t1.Prices.Count; i++)
            {
                root.Clone(out RowCursor tempCursor1).Find(ref row, Utf8String.Empty);
                ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor1, innerType.TypeArgs, out RowCursor innerScope));
                for (int j = 0; j < t1.Prices[i].Count; j++)
                {
                    root.Clone(out RowCursor tempCursor2).Find(ref row, "prices.0.0");
                    ResultAssert.IsSuccess(itemType.Type.TypeAs<LayoutFloat32>().WriteSparse(ref row, ref tempCursor2, t1.Prices[i][j]));
                    ResultAssert.IsSuccess(innerLayout.MoveField(ref row, ref innerScope, ref tempCursor2));
                }

                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref tempCursor1, out RowCursor findScope));
                Assert.AreEqual(i, findScope.Index, $"Failed to find t1.Prices[{i}]");
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void UpdateInSet()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            List<Guid> expected = new List<Guid>
            {
                Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"),
                Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"),
                Guid.Parse("{B7BC39C2-1A2D-4EAF-8F33-ED976872D876}"),
                Guid.Parse("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}"),
            };

            foreach (IEnumerable<Guid> permutation in expected.Permute())
            {
                Todo t1 = new Todo
                {
                    Projects = new List<Guid>(permutation),
                };

                this.WriteTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);

                // Attempt to find each item in turn and then delete it.
                RowCursor root = RowCursor.Create(ref row);
                Assert.IsTrue(this.layout.TryFind("projects", out LayoutColumn c));
                root.Clone(out RowCursor setScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
                foreach (Guid elm in t1.Projects)
                {
                    // Verify it is already there.
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, elm));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref tempCursor, value: out RowCursor _));

                    // Insert it again with update.
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, elm));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Update));

                    // Insert it again with upsert.
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, elm));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor));

                    // Insert it again with insert (fail: exists).
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, elm));
                    ResultAssert.Exists(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.Insert));

                    // Insert it again with insert at (fail: disallowed).
                    root.Clone(out tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, elm));
                    ResultAssert.TypeConstraint(
                        c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref setScope, ref tempCursor, UpdateOptions.InsertAt));
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void FindAndDelete()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            List<Guid> expected = new List<Guid>
            {
                Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"),
                Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"),
                Guid.Parse("{B7BC39C2-1A2D-4EAF-8F33-ED976872D876}"),
                Guid.Parse("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}"),
            };

            foreach (IEnumerable<Guid> permutation in expected.Permute())
            {
                Todo t1 = new Todo
                {
                    Projects = new List<Guid>(permutation),
                };

                this.WriteTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);

                // Attempt to update each item in turn and then update it with itself.
                RowCursor root = RowCursor.Create(ref row);
                Assert.IsTrue(this.layout.TryFind("projects", out LayoutColumn c));
                root.Clone(out RowCursor setScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref setScope, out setScope));
                foreach (Guid p in t1.Projects)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, p));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().Find(ref row, ref setScope, ref tempCursor, out RowCursor findScope));
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().DeleteSparse(ref row, ref findScope));
                }
            }
        }

        [TestMethod]
        [Owner("jthunter")]
        public void RowWriterTest()
        {
            RowBuffer row = new RowBuffer(TypedSetUnitTests.InitialRowSize);

            List<Guid> expected = new List<Guid>
            {
                Guid.Parse("{4674962B-CE11-4916-81C5-0421EE36F168}"),
                Guid.Parse("{7499C40E-7077-45C1-AE5F-3E384966B3B9}"),
                Guid.Parse("{B7BC39C2-1A2D-4EAF-8F33-ED976872D876}"),
                Guid.Parse("{DEA71ABE-3041-4CAF-BBD9-1A46D10832A0}"),
            };

            foreach (IEnumerable<Guid> permutation in expected.Permute())
            {
                Todo t1 = new Todo
                {
                    Projects = new List<Guid>(permutation),
                };

                row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);
                ResultAssert.IsSuccess(RowWriter.WriteBuffer(ref row, t1, TypedSetUnitTests.SerializeTodo));

                // Update the existing Set by updating each item with itself.  This ensures that the RowWriter has
                // maintained the unique index correctly.
                Assert.IsTrue(this.layout.TryFind("projects", out LayoutColumn c));
                RowCursor.Create(ref row, out RowCursor root);
                root.Clone(out RowCursor projScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref projScope, out projScope));
                foreach (Guid item in t1.Projects)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref projScope, ref tempCursor));
                }

                Todo t2 = this.ReadTodo(ref row, ref RowCursor.Create(ref row, out RowCursor _));
                Assert.AreEqual(t1, t2);
            }
        }

        private static Result SerializeTodo(ref RowWriter writer, TypeArgument typeArg, Todo value)
        {
            if (value.Projects != null)
            {
                Assert.IsTrue(writer.Layout.TryFind("projects", out LayoutColumn c));
                Result r = writer.WriteScope(
                    "projects",
                    c.TypeArg,
                    value.Projects,
                    (ref RowWriter writer2, TypeArgument typeArg2, List<Guid> value2) =>
                    {
                        foreach (Guid item in value2)
                        {
                            ResultAssert.IsSuccess(writer2.WriteGuid(null, item));
                        }

                        return Result.Success;
                    });
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        private static void WriteShoppingItem(ref RowBuffer row, ref RowCursor matchScope, TypeArgumentList typeArgs, ShoppingItem m)
        {
            Layout matchLayout = row.Resolver.Resolve(typeArgs.SchemaId);
            Assert.IsTrue(matchLayout.TryFind("label", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref matchScope, c, m.Label));
            Assert.IsTrue(matchLayout.TryFind("count", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUInt8>().WriteFixed(ref row, ref matchScope, c, m.Count));
        }

        private static ShoppingItem ReadShoppingItem(ref RowBuffer row, ref RowCursor matchScope)
        {
            Layout matchLayout = matchScope.Layout;
            ShoppingItem m = new ShoppingItem();
            Assert.IsTrue(matchLayout.TryFind("label", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref matchScope, c, out m.Label));
            Assert.IsTrue(matchLayout.TryFind("count", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUInt8>().ReadFixed(ref row, ref matchScope, c, out m.Count));
            return m;
        }

        private void WriteTodo(ref RowBuffer row, ref RowCursor root, Todo value)
        {
            LayoutColumn c;

            if (value.Attendees != null)
            {
                Assert.IsTrue(this.layout.TryFind("attendees", out c));
                root.Clone(out RowCursor attendScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref attendScope, c.TypeArgs, out attendScope));
                foreach (string item in value.Attendees)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tempCursor, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref attendScope, ref tempCursor));
                }
            }

            if (value.Projects != null)
            {
                Assert.IsTrue(this.layout.TryFind("projects", out c));
                root.Clone(out RowCursor projScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref projScope, c.TypeArgs, out projScope));
                foreach (Guid item in value.Projects)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().WriteSparse(ref row, ref tempCursor, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref projScope, ref tempCursor));
                }
            }

            if (value.Checkboxes != null)
            {
                Assert.IsTrue(this.layout.TryFind("checkboxes", out c));
                root.Clone(out RowCursor checkboxScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref checkboxScope, c.TypeArgs, out checkboxScope));
                foreach (bool item in value.Checkboxes)
                {
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutBoolean>().WriteSparse(ref row, ref tempCursor, item));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref checkboxScope, ref tempCursor));
                }
            }

            if (value.Prices != null)
            {
                Assert.IsTrue(this.layout.TryFind("prices", out c));
                root.Clone(out RowCursor pricesScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref pricesScope, c.TypeArgs, out pricesScope));
                foreach (List<float> item in value.Prices)
                {
                    Assert.IsTrue(item != null);
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutUniqueScope innerLayout = innerType.Type.TypeAs<LayoutUniqueScope>();
                    root.Clone(out RowCursor tempCursor1).Find(ref row, "prices.0");
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor1, innerType.TypeArgs, out RowCursor innerScope));
                    foreach (float innerItem in item)
                    {
                        LayoutFloat32 itemLayout = innerType.TypeArgs[0].Type.TypeAs<LayoutFloat32>();
                        root.Clone(out RowCursor tempCursor2).Find(ref row, "prices.0.0");
                        ResultAssert.IsSuccess(itemLayout.WriteSparse(ref row, ref tempCursor2, innerItem));
                        ResultAssert.IsSuccess(innerLayout.MoveField(ref row, ref innerScope, ref tempCursor2));
                    }

                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref pricesScope, ref tempCursor1));
                }
            }

            if (value.Nested != null)
            {
                Assert.IsTrue(this.layout.TryFind("nested", out c));
                root.Clone(out RowCursor nestedScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref nestedScope, c.TypeArgs, out nestedScope));
                foreach (List<List<int>> item in value.Nested)
                {
                    Assert.IsTrue(item != null);
                    TypeArgument in2Type = c.TypeArgs[0];
                    LayoutUniqueScope in2Layout = in2Type.Type.TypeAs<LayoutUniqueScope>();
                    root.Clone(out RowCursor tempCursor1).Find(ref row, "prices.0");
                    ResultAssert.IsSuccess(in2Layout.WriteScope(ref row, ref tempCursor1, in2Type.TypeArgs, out RowCursor in2Scope));
                    foreach (List<int> item2 in item)
                    {
                        Assert.IsTrue(item2 != null);
                        TypeArgument in3Type = in2Type.TypeArgs[0];
                        LayoutUniqueScope in3Layout = in3Type.Type.TypeAs<LayoutUniqueScope>();
                        root.Clone(out RowCursor tempCursor2).Find(ref row, "prices.0.0");
                        ResultAssert.IsSuccess(in3Layout.WriteScope(ref row, ref tempCursor2, in3Type.TypeArgs, out RowCursor in3Scope));
                        foreach (int innerItem in item2)
                        {
                            LayoutInt32 itemLayout = in3Type.TypeArgs[0].Type.TypeAs<LayoutInt32>();
                            root.Clone(out RowCursor tempCursor3).Find(ref row, "prices.0.0.0");
                            ResultAssert.IsSuccess(itemLayout.WriteSparse(ref row, ref tempCursor3, innerItem));
                            ResultAssert.IsSuccess(in3Layout.MoveField(ref row, ref in3Scope, ref tempCursor3));
                        }

                        ResultAssert.IsSuccess(in2Layout.MoveField(ref row, ref in2Scope, ref tempCursor2));
                    }

                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref nestedScope, ref tempCursor1));
                }
            }

            if (value.Shopping != null)
            {
                Assert.IsTrue(this.layout.TryFind("shopping", out c));
                root.Clone(out RowCursor shoppingScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref shoppingScope, c.TypeArgs, out shoppingScope));
                foreach (ShoppingItem item in value.Shopping)
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutUDT innerLayout = innerType.Type.TypeAs<LayoutUDT>();
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor, innerType.TypeArgs, out RowCursor itemScope));
                    TypedSetUnitTests.WriteShoppingItem(ref row, ref itemScope, innerType.TypeArgs, item);
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref shoppingScope, ref tempCursor));
                }
            }

            if (value.Work != null)
            {
                Assert.IsTrue(this.layout.TryFind("work", out c));
                root.Clone(out RowCursor workScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().WriteScope(ref row, ref workScope, c.TypeArgs, out workScope));
                foreach (Tuple<bool, ulong> item in value.Work)
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutIndexedScope innerLayout = innerType.Type.TypeAs<LayoutIndexedScope>();
                    root.Clone(out RowCursor tempCursor).Find(ref row, Utf8String.Empty);
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref tempCursor, innerType.TypeArgs, out RowCursor tupleScope));
                    ResultAssert.IsSuccess(innerType.TypeArgs[0].Type.TypeAs<LayoutBoolean>().WriteSparse(ref row, ref tupleScope, item.Item1));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().WriteSparse(ref row, ref tupleScope, item.Item2));
                    ResultAssert.IsSuccess(c.TypeAs<LayoutUniqueScope>().MoveField(ref row, ref workScope, ref tempCursor));
                }
            }
        }

        private Todo ReadTodo(ref RowBuffer row, ref RowCursor root)
        {
            Todo value = new Todo();

            Assert.IsTrue(this.layout.TryFind("attendees", out LayoutColumn c));
            root.Clone(out RowCursor tagsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref tagsScope, out tagsScope) == Result.Success)
            {
                value.Attendees = new List<string>();
                while (tagsScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref tagsScope, out string item));
                    value.Attendees.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("projects", out c));
            root.Clone(out RowCursor projScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref projScope, out projScope) == Result.Success)
            {
                value.Projects = new List<Guid>();
                while (projScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutGuid>().ReadSparse(ref row, ref projScope, out Guid item));
                    value.Projects.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("checkboxes", out c));
            root.Clone(out RowCursor checkboxScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref checkboxScope, out checkboxScope) == Result.Success)
            {
                value.Checkboxes = new List<bool>();
                while (checkboxScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutBoolean>().ReadSparse(ref row, ref checkboxScope, out bool item));
                    value.Checkboxes.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("prices", out c));
            root.Clone(out RowCursor pricesScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref pricesScope, out pricesScope) == Result.Success)
            {
                value.Prices = new List<List<float>>();
                TypeArgument innerType = c.TypeArgs[0];
                LayoutUniqueScope innerLayout = innerType.Type.TypeAs<LayoutUniqueScope>();
                while (pricesScope.MoveNext(ref row))
                {
                    List<float> item = new List<float>();
                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref pricesScope, out RowCursor innerScope));
                    while (innerScope.MoveNext(ref row))
                    {
                        LayoutFloat32 itemLayout = innerType.TypeArgs[0].Type.TypeAs<LayoutFloat32>();
                        ResultAssert.IsSuccess(itemLayout.ReadSparse(ref row, ref innerScope, out float innerItem));
                        item.Add(innerItem);
                    }

                    value.Prices.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("nested", out c));
            root.Clone(out RowCursor nestedScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref nestedScope, out nestedScope) == Result.Success)
            {
                value.Nested = new List<List<List<int>>>();
                TypeArgument in2Type = c.TypeArgs[0];
                LayoutUniqueScope in2Layout = in2Type.Type.TypeAs<LayoutUniqueScope>();
                while (nestedScope.MoveNext(ref row))
                {
                    List<List<int>> item = new List<List<int>>();
                    ResultAssert.IsSuccess(in2Layout.ReadScope(ref row, ref nestedScope, out RowCursor in2Scope));
                    while (in2Scope.MoveNext(ref row))
                    {
                        TypeArgument in3Type = in2Type.TypeArgs[0];
                        LayoutUniqueScope in3Layout = in3Type.Type.TypeAs<LayoutUniqueScope>();
                        List<int> item2 = new List<int>();
                        ResultAssert.IsSuccess(in3Layout.ReadScope(ref row, ref in2Scope, out RowCursor in3Scope));
                        while (in3Scope.MoveNext(ref row))
                        {
                            LayoutInt32 itemLayout = in3Type.TypeArgs[0].Type.TypeAs<LayoutInt32>();
                            ResultAssert.IsSuccess(itemLayout.ReadSparse(ref row, ref in3Scope, out int innerItem));
                            item2.Add(innerItem);
                        }

                        item.Add(item2);
                    }

                    value.Nested.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("shopping", out c));
            root.Clone(out RowCursor shoppingScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref shoppingScope, out shoppingScope) == Result.Success)
            {
                value.Shopping = new List<ShoppingItem>();
                while (shoppingScope.MoveNext(ref row))
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutUDT innerLayout = innerType.Type.TypeAs<LayoutUDT>();
                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref shoppingScope, out RowCursor matchScope));
                    ShoppingItem item = TypedSetUnitTests.ReadShoppingItem(ref row, ref matchScope);
                    value.Shopping.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("work", out c));
            root.Clone(out RowCursor workScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutUniqueScope>().ReadScope(ref row, ref workScope, out workScope) == Result.Success)
            {
                value.Work = new List<Tuple<bool, ulong>>();
                while (workScope.MoveNext(ref row))
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutIndexedScope innerLayout = innerType.Type.TypeAs<LayoutIndexedScope>();

                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref workScope, out RowCursor tupleScope));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(innerType.TypeArgs[0].Type.TypeAs<LayoutBoolean>().ReadSparse(ref row, ref tupleScope, out bool item1));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutVarUInt>().ReadSparse(ref row, ref tupleScope, out ulong item2));
                    value.Work.Add(Tuple.Create(item1, item2));
                }
            }

            return value;
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class Todo
        {
            public List<string> Attendees;
            public List<Guid> Projects;
            public List<bool> Checkboxes;
            public List<List<float>> Prices;
            public List<List<List<int>>> Nested;
            public List<ShoppingItem> Shopping;
            public List<Tuple<bool, ulong>> Work;

            public override bool Equals(object obj)
            {
                return obj is Todo todo && this.Equals(todo);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    int hashCode = 0;
                    hashCode = (hashCode * 397) ^ (this.Attendees?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Projects?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Checkboxes?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Prices?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Nested?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Shopping?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Work?.GetHashCode() ?? 0);
                    return hashCode;
                }
            }

            private static bool NestedNestedSetEquals<T>(List<List<List<T>>> left, List<List<List<T>>> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                for (int i = 0; i < left.Count; i++)
                {
                    if (!Todo.NestedSetEquals(left[i], right[i]))
                    {
                        return false;
                    }
                }

                return true;
            }

            private static bool NestedSetEquals<T>(List<List<T>> left, List<List<T>> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                for (int i = 0; i < left.Count; i++)
                {
                    if (!Todo.SetEquals(left[i], right[i]))
                    {
                        return false;
                    }
                }

                return true;
            }

            private static bool SetEquals<T>(List<T> left, List<T> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                foreach (T item in left)
                {
                    if (!right.Contains(item))
                    {
                        return false;
                    }
                }

                return true;
            }

            private bool Equals(Todo other)
            {
                return (object.ReferenceEquals(this.Attendees, other.Attendees) ||
                        ((this.Attendees != null) && (other.Attendees != null) && Todo.SetEquals(this.Attendees, other.Attendees))) &&
                       (object.ReferenceEquals(this.Projects, other.Projects) ||
                        ((this.Projects != null) && (other.Projects != null) && Todo.SetEquals(this.Projects, other.Projects))) &&
                       (object.ReferenceEquals(this.Checkboxes, other.Checkboxes) ||
                        ((this.Checkboxes != null) && (other.Checkboxes != null) && Todo.SetEquals(this.Checkboxes, other.Checkboxes))) &&
                       (object.ReferenceEquals(this.Prices, other.Prices) ||
                        ((this.Prices != null) && (other.Prices != null) && Todo.NestedSetEquals(this.Prices, other.Prices))) &&
                       (object.ReferenceEquals(this.Nested, other.Nested) ||
                        ((this.Nested != null) && (other.Nested != null) && Todo.NestedNestedSetEquals(this.Nested, other.Nested))) &&
                       (object.ReferenceEquals(this.Shopping, other.Shopping) ||
                        ((this.Shopping != null) && (other.Shopping != null) && Todo.SetEquals(this.Shopping, other.Shopping))) &&
                       (object.ReferenceEquals(this.Work, other.Work) ||
                        ((this.Work != null) && (other.Work != null) && Todo.SetEquals(this.Work, other.Work)));
            }
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class ShoppingItem
        {
            public string Label;
            public byte Count;

            public override bool Equals(object obj)
            {
                return obj is ShoppingItem shoppingItem && this.Equals(shoppingItem);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    return (this.Label.GetHashCode() * 397) ^ this.Count.GetHashCode();
                }
            }

            private bool Equals(ShoppingItem other)
            {
                return this.Label == other.Label && this.Count == other.Count;
            }
        }
    }
}
