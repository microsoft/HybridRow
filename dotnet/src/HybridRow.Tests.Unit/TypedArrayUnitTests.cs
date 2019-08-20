// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

// ReSharper disable StringLiteralTypo
// ReSharper disable IdentifierTypo
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
    [DeploymentItem(TypedArrayUnitTests.SchemaFile, "TestData")]
    public sealed class TypedArrayUnitTests
    {
        private const string SchemaFile = @"TestData\TagSchema.json";
        private const int InitialRowSize = 2 * 1024 * 1024;

        private Namespace counterSchema;
        private LayoutResolver resolver;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            string json = File.ReadAllText(TypedArrayUnitTests.SchemaFile);
            this.counterSchema = Namespace.Parse(json);
            this.resolver = new LayoutResolverNamespace(this.counterSchema);
            this.layout = this.resolver.Resolve(this.counterSchema.Schemas.Find(x => x.Name == "Tagged").SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateTags()
        {
            RowBuffer row = new RowBuffer(TypedArrayUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, this.resolver);

            Tagged t1 = new Tagged
            {
                Title = "Thriller",
                Tags = new List<string> { "classic", "Post-disco", "funk" },
                Options = new List<int?> { 8, null, 9 },
                Ratings = new List<List<double>>
                {
                    new List<double> { 1.2, 3.0 },
                    new List<double> { 4.1, 5.7 },
                    new List<double> { 7.3, 8.12, 9.14 },
                },
                Similars = new List<SimilarMatch>
                {
                    new SimilarMatch { Thumbprint = "TRABACN128F425B784", Score = 0.87173699999999998 },
                    new SimilarMatch { Thumbprint = "TRJYGLF12903CB4952", Score = 0.75105200000000005 },
                    new SimilarMatch { Thumbprint = "TRWJMMB128F429D550", Score = 0.50866100000000003 },
                },
                Priority = new List<Tuple<string, long>>
                {
                    Tuple.Create("80's", 100L),
                    Tuple.Create("classics", 100L),
                    Tuple.Create("pop", 50L),
                },
            };

            this.WriteTagged(ref row, ref RowCursor.Create(ref row, out RowCursor _), t1);
            Tagged t2 = this.ReadTagged(ref row, ref RowCursor.Create(ref row, out RowCursor _));
            Assert.AreEqual(t1, t2);
        }

        private void WriteTagged(ref RowBuffer row, ref RowCursor root, Tagged value)
        {
            Assert.IsTrue(this.layout.TryFind("title", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteVariable(ref row, ref root, c, value.Title));

            if (value.Tags != null)
            {
                Assert.IsTrue(this.layout.TryFind("tags", out c));
                root.Clone(out RowCursor tagsScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref tagsScope, c.TypeArgs, out tagsScope));
                foreach (string item in value.Tags)
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tagsScope, item));
                    Assert.IsFalse(tagsScope.MoveNext(ref row));
                }
            }

            if (value.Options != null)
            {
                Assert.IsTrue(this.layout.TryFind("options", out c));
                root.Clone(out RowCursor optionsScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref optionsScope, c.TypeArgs, out optionsScope));
                foreach (int? item in value.Options)
                {
                    TypeArgument itemType = c.TypeArgs[0];
                    ResultAssert.IsSuccess(
                        itemType.Type.TypeAs<LayoutNullable>()
                            .WriteScope(ref row, ref optionsScope, itemType.TypeArgs, item.HasValue, out RowCursor nullableScope));

                    if (item.HasValue)
                    {
                        ResultAssert.IsSuccess(
                            itemType.TypeArgs[0].Type.TypeAs<LayoutInt32>().WriteSparse(ref row, ref nullableScope, item.Value));
                    }

                    Assert.IsFalse(optionsScope.MoveNext(ref row, ref nullableScope));
                }
            }

            if (value.Ratings != null)
            {
                Assert.IsTrue(this.layout.TryFind("ratings", out c));
                root.Clone(out RowCursor ratingsScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref ratingsScope, c.TypeArgs, out ratingsScope));
                foreach (List<double> item in value.Ratings)
                {
                    Assert.IsTrue(item != null);
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutTypedArray innerLayout = innerType.Type.TypeAs<LayoutTypedArray>();
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref ratingsScope, innerType.TypeArgs, out RowCursor innerScope));
                    foreach (double innerItem in item)
                    {
                        LayoutFloat64 itemLayout = innerType.TypeArgs[0].Type.TypeAs<LayoutFloat64>();
                        ResultAssert.IsSuccess(itemLayout.WriteSparse(ref row, ref innerScope, innerItem));
                        Assert.IsFalse(innerScope.MoveNext(ref row));
                    }

                    Assert.IsFalse(ratingsScope.MoveNext(ref row, ref innerScope));
                }
            }

            if (value.Similars != null)
            {
                Assert.IsTrue(this.layout.TryFind("similars", out c));
                root.Clone(out RowCursor similarsScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref similarsScope, c.TypeArgs, out similarsScope));
                foreach (SimilarMatch item in value.Similars)
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutUDT innerLayout = innerType.Type.TypeAs<LayoutUDT>();
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref similarsScope, innerType.TypeArgs, out RowCursor matchScope));
                    TypedArrayUnitTests.WriteSimilarMatch(ref row, ref matchScope, innerType.TypeArgs, item);

                    Assert.IsFalse(similarsScope.MoveNext(ref row, ref matchScope));
                }
            }

            if (value.Priority != null)
            {
                Assert.IsTrue(this.layout.TryFind("priority", out c));
                root.Clone(out RowCursor priorityScope).Find(ref row, c.Path);
                ResultAssert.IsSuccess(c.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref priorityScope, c.TypeArgs, out priorityScope));
                foreach (Tuple<string, long> item in value.Priority)
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutIndexedScope innerLayout = innerType.Type.TypeAs<LayoutIndexedScope>();
                    ResultAssert.IsSuccess(innerLayout.WriteScope(ref row, ref priorityScope, innerType.TypeArgs, out RowCursor tupleScope));
                    ResultAssert.IsSuccess(innerType.TypeArgs[0].Type.TypeAs<LayoutUtf8>().WriteSparse(ref row, ref tupleScope, item.Item1));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(innerType.TypeArgs[1].Type.TypeAs<LayoutInt64>().WriteSparse(ref row, ref tupleScope, item.Item2));

                    Assert.IsFalse(priorityScope.MoveNext(ref row, ref tupleScope));
                }
            }
        }

        private Tagged ReadTagged(ref RowBuffer row, ref RowCursor root)
        {
            Tagged value = new Tagged();
            Assert.IsTrue(this.layout.TryFind("title", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadVariable(ref row, ref root, c, out value.Title));

            Assert.IsTrue(this.layout.TryFind("tags", out c));
            root.Clone(out RowCursor tagsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref tagsScope, out tagsScope) == Result.Success)
            {
                value.Tags = new List<string>();
                while (tagsScope.MoveNext(ref row))
                {
                    ResultAssert.IsSuccess(c.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref tagsScope, out string item));
                    value.Tags.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("options", out c));
            root.Clone(out RowCursor optionsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref optionsScope, out optionsScope) == Result.Success)
            {
                value.Options = new List<int?>();
                while (optionsScope.MoveNext(ref row))
                {
                    TypeArgument itemType = c.TypeArgs[0];
                    ResultAssert.IsSuccess(
                        itemType.Type.TypeAs<LayoutNullable>()
                            .ReadScope(ref row, ref optionsScope, out RowCursor nullableScope));

                    if (nullableScope.MoveNext(ref row))
                    {
                        ResultAssert.IsSuccess(LayoutNullable.HasValue(ref row, ref nullableScope));

                        ResultAssert.IsSuccess(
                            itemType.TypeArgs[0].Type.TypeAs<LayoutInt32>().ReadSparse(ref row, ref nullableScope, out int itemValue));

                        value.Options.Add(itemValue);
                    }
                    else
                    {
                        ResultAssert.NotFound(LayoutNullable.HasValue(ref row, ref nullableScope));

                        value.Options.Add(null);
                    }
                }
            }

            Assert.IsTrue(this.layout.TryFind("ratings", out c));
            root.Clone(out RowCursor ratingsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref ratingsScope, out ratingsScope) == Result.Success)
            {
                value.Ratings = new List<List<double>>();
                TypeArgument innerType = c.TypeArgs[0];
                LayoutTypedArray innerLayout = innerType.Type.TypeAs<LayoutTypedArray>();
                RowCursor innerScope = default;
                while (ratingsScope.MoveNext(ref row, ref innerScope))
                {
                    List<double> item = new List<double>();
                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref ratingsScope, out innerScope));
                    while (innerScope.MoveNext(ref row))
                    {
                        LayoutFloat64 itemLayout = innerType.TypeArgs[0].Type.TypeAs<LayoutFloat64>();
                        ResultAssert.IsSuccess(itemLayout.ReadSparse(ref row, ref innerScope, out double innerItem));
                        item.Add(innerItem);
                    }

                    value.Ratings.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("similars", out c));
            root.Clone(out RowCursor similarsScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref similarsScope, out similarsScope) == Result.Success)
            {
                value.Similars = new List<SimilarMatch>();
                while (similarsScope.MoveNext(ref row))
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutUDT innerLayout = innerType.Type.TypeAs<LayoutUDT>();
                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref similarsScope, out RowCursor matchScope));
                    SimilarMatch item = TypedArrayUnitTests.ReadSimilarMatch(ref row, ref matchScope);
                    value.Similars.Add(item);
                }
            }

            Assert.IsTrue(this.layout.TryFind("priority", out c));
            root.Clone(out RowCursor priorityScope).Find(ref row, c.Path);
            if (c.TypeAs<LayoutTypedArray>().ReadScope(ref row, ref priorityScope, out priorityScope) == Result.Success)
            {
                value.Priority = new List<Tuple<string, long>>();
                RowCursor tupleScope = default;
                while (priorityScope.MoveNext(ref row, ref tupleScope))
                {
                    TypeArgument innerType = c.TypeArgs[0];
                    LayoutIndexedScope innerLayout = innerType.Type.TypeAs<LayoutIndexedScope>();

                    ResultAssert.IsSuccess(innerLayout.ReadScope(ref row, ref priorityScope, out tupleScope));
                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(
                        innerType.TypeArgs[0].Type.TypeAs<LayoutUtf8>().ReadSparse(ref row, ref tupleScope, out string item1));

                    Assert.IsTrue(tupleScope.MoveNext(ref row));
                    ResultAssert.IsSuccess(
                        innerType.TypeArgs[1].Type.TypeAs<LayoutInt64>().ReadSparse(ref row, ref tupleScope, out long item2));

                    value.Priority.Add(Tuple.Create(item1, item2));
                }
            }

            return value;
        }

        private static void WriteSimilarMatch(ref RowBuffer row, ref RowCursor matchScope, TypeArgumentList typeArgs, SimilarMatch m)
        {
            Layout matchLayout = row.Resolver.Resolve(typeArgs.SchemaId);
            Assert.IsTrue(matchLayout.TryFind("thumbprint", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().WriteFixed(ref row, ref matchScope, c, m.Thumbprint));
            Assert.IsTrue(matchLayout.TryFind("score", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutFloat64>().WriteFixed(ref row, ref matchScope, c, m.Score));
        }

        private static SimilarMatch ReadSimilarMatch(ref RowBuffer row, ref RowCursor matchScope)
        {
            Layout matchLayout = matchScope.Layout;
            SimilarMatch m = new SimilarMatch();
            Assert.IsTrue(matchLayout.TryFind("thumbprint", out LayoutColumn c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutUtf8>().ReadFixed(ref row, ref matchScope, c, out m.Thumbprint));
            Assert.IsTrue(matchLayout.TryFind("score", out c));
            ResultAssert.IsSuccess(c.TypeAs<LayoutFloat64>().ReadFixed(ref row, ref matchScope, c, out m.Score));
            return m;
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class Tagged
        {
            public string Title;
            public List<string> Tags;
            public List<int?> Options;
            public List<List<double>> Ratings;
            public List<SimilarMatch> Similars;
            public List<Tuple<string, long>> Priority;

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

                return obj is Tagged tagged && this.Equals(tagged);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    int hashCode = this.Title?.GetHashCode() ?? 0;
                    hashCode = (hashCode * 397) ^ (this.Tags?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Options?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Ratings?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Similars?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (this.Priority?.GetHashCode() ?? 0);
                    return hashCode;
                }
            }

            private static bool NestedSequenceEquals<T>(List<List<T>> left, List<List<T>> right)
            {
                if (left.Count != right.Count)
                {
                    return false;
                }

                for (int i = 0; i < left.Count; i++)
                {
                    if (!left[i].SequenceEqual(right[i]))
                    {
                        return false;
                    }
                }

                return true;
            }

            private bool Equals(Tagged other)
            {
                return string.Equals(this.Title, other.Title) &&
                       (object.ReferenceEquals(this.Tags, other.Tags) ||
                        ((this.Tags != null) && (other.Tags != null) && this.Tags.SequenceEqual(other.Tags))) &&
                       (object.ReferenceEquals(this.Options, other.Options) ||
                        ((this.Options != null) && (other.Options != null) && this.Options.SequenceEqual(other.Options))) &&
                       (object.ReferenceEquals(this.Ratings, other.Ratings) ||
                        ((this.Ratings != null) && (other.Ratings != null) && Tagged.NestedSequenceEquals(this.Ratings, other.Ratings))) &&
                       (object.ReferenceEquals(this.Similars, other.Similars) ||
                        ((this.Similars != null) && (other.Similars != null) && this.Similars.SequenceEqual(other.Similars))) &&
                       (object.ReferenceEquals(this.Priority, other.Priority) ||
                        ((this.Priority != null) && (other.Priority != null) && this.Priority.SequenceEqual(other.Priority)));
            }
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
        private sealed class SimilarMatch
        {
            public string Thumbprint;
            public double Score;

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

                return obj is SimilarMatch match && this.Equals(match);
            }

            public override int GetHashCode()
            {
                unchecked
                {
                    return (this.Thumbprint.GetHashCode() * 397) ^ this.Score.GetHashCode();
                }
            }

            private bool Equals(SimilarMatch other)
            {
                // ReSharper disable once CompareOfFloatsByEqualityOperator
                return this.Thumbprint == other.Thumbprint && this.Score == other.Score;
            }
        }
    }
}
