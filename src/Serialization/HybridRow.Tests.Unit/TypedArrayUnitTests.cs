// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

// ReSharper disable StringLiteralTypo
// ReSharper disable IdentifierTypo

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.TypedArray;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public sealed class TypedArrayUnitTests
    {
        private const int InitialRowSize = 2 * 1024 * 1024;
        private Layout layout;

        [TestInitialize]
        public void ParseNamespaceExample()
        {
            this.layout = TypedArrayHrSchema.LayoutResolver.Resolve((SchemaId)TaggedHybridRowSerializer.SchemaId);
        }

        [TestMethod]
        [Owner("jthunter")]
        public void CreateTags()
        {
            RowBuffer row = new RowBuffer(TypedArrayUnitTests.InitialRowSize);
            row.InitLayout(HybridRowVersion.V1, this.layout, TypedArrayHrSchema.LayoutResolver);

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
                Priority = new List<(string, long)>
                {
                    ("80's", 100L),
                    ("classics", 100L),
                    ("pop", 50L),
                },
            };

            ResultAssert.IsSuccess(
                default(TaggedHybridRowSerializer).Write(
                    ref row,
                    ref RowCursor.Create(ref row, out RowCursor _),
                    true,
                    default,
                    t1));
            ResultAssert.IsSuccess(
                default(TaggedHybridRowSerializer).Read(
                    ref row,
                    ref RowCursor.Create(ref row, out RowCursor _),
                    true,
                    out Tagged t2));
            Assert.IsTrue(TaggedComparer.Default.Equals(t1, t2));
        }

        private sealed class TaggedComparer : EqualityComparer<Tagged>
        {
            public static new readonly TaggedComparer Default = new TaggedComparer();

            public override bool Equals(Tagged x, Tagged y)
            {
                if (object.ReferenceEquals(x, y))
                {
                    return true;
                }
                if (object.ReferenceEquals(x, null))
                {
                    return false;
                }
                if (object.ReferenceEquals(y, null))
                {
                    return false;
                }
                if (x.GetType() != y.GetType())
                {
                    return false;
                }
                return string.Equals(x.Title, y.Title) &&
                       (object.ReferenceEquals(x.Tags, y.Tags) ||
                        ((x.Tags != null) && (y.Tags != null) && x.Tags.SequenceEqual(y.Tags))) &&
                       (object.ReferenceEquals(x.Options, y.Options) ||
                        ((x.Options != null) && (y.Options != null) && x.Options.SequenceEqual(y.Options))) &&
                       (object.ReferenceEquals(x.Ratings, y.Ratings) ||
                        ((x.Ratings != null) && (y.Ratings != null) && TaggedComparer.NestedSequenceEquals(x.Ratings, y.Ratings))) &&
                       (object.ReferenceEquals(x.Similars, y.Similars) ||
                        ((x.Similars != null) &&
                         (y.Similars != null) &&
                         x.Similars.SequenceEqual(y.Similars, SimilarMatchComparer.Default))) &&
                       (object.ReferenceEquals(x.Priority, y.Priority) ||
                        ((x.Priority != null) && (y.Priority != null) && x.Priority.SequenceEqual(y.Priority)));
            }

            public override int GetHashCode(Tagged obj)
            {
                unchecked
                {
                    int hashCode = obj.Title?.GetHashCode() ?? 0;
                    hashCode = (hashCode * 397) ^ (obj.Tags?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (obj.Options?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (obj.Ratings?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (obj.Similars?.GetHashCode() ?? 0);
                    hashCode = (hashCode * 397) ^ (obj.Priority?.GetHashCode() ?? 0);
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
        }

        private sealed class SimilarMatchComparer : EqualityComparer<SimilarMatch>
        {
            public static new readonly SimilarMatchComparer Default = new SimilarMatchComparer();

            public override bool Equals(SimilarMatch x, SimilarMatch y)
            {
                if (object.ReferenceEquals(x, y))
                {
                    return true;
                }
                if (object.ReferenceEquals(x, null))
                {
                    return false;
                }
                if (object.ReferenceEquals(y, null))
                {
                    return false;
                }
                if (x.GetType() != y.GetType())
                {
                    return false;
                }
                return x.Thumbprint == y.Thumbprint &&
                       EqualityComparer<double>.Default.Equals(x.Score, y.Score);
            }

            public override int GetHashCode(SimilarMatch obj)
            {
                return HashCode.Combine(obj.Thumbprint, obj.Score);
            }
        }
    }
}
