// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

// ------------------------------------------------------------
// This file was generated by:
//   Microsoft.Azure.Cosmos.Serialization.HybridRowCLI: 1.0.0.0
//
// This file should not be modified directly.
// ------------------------------------------------------------

#pragma warning disable NamespaceMatchesFolderStructure // Namespace Declarations must match folder structure.
#pragma warning disable CA1707 // Identifiers should not contain underscores.
#pragma warning disable CA1034 // Do not nest types.
#pragma warning disable CA2104 // Do not declare readonly mutable reference types.
#pragma warning disable SA1129 // Do not use default value type constructor.
#pragma warning disable SA1309 // Field should not begin with an underscore.
#pragma warning disable SA1310 // Field names should not contain underscore.
#pragma warning disable SA1402 // File may only contain a single type.
#pragma warning disable SA1414 // Tuple types in signatures should have element names.
#pragma warning disable SA1514 // Element documentation header should be preceded by blank line.
#pragma warning disable SA1516 // Elements should be separated by blank line.
#pragma warning disable SA1649 // File name should match first type name.

// ReSharper disable CheckNamespace
// ReSharper disable InconsistentNaming
// ReSharper disable RedundantEmptySwitchSection
// ReSharper disable JoinDeclarationAndInitializer
// ReSharper disable TooWideLocalVariableScope
// ReSharper disable ArrangeStaticMemberQualifier
// ReSharper disable RedundantJumpStatement
// ReSharper disable RedundantUsingDirective
namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.TypedArray
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.RecordIO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    internal static class TypedArrayHrSchema
    {
        public static readonly Namespace Namespace = TypedArrayHrSchema.CreateSchema();
        public static readonly LayoutResolver LayoutResolver = TypedArrayHrSchema.LoadSchema();

        private static Namespace CreateSchema()
        {
            return new Namespace
            {
                Name = "Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit.TypedArray",
                Version = SchemaLanguageVersion.V2,
                CppNamespace = "cdb_hr_test::typed_array",
                Schemas = new List<Schema>
                {
                    //////////////////////////////////////////////////////////////////////////////
                    new Schema
                    {
                        Name = "Tagged",
                        SchemaId = new SchemaId(1),
                        Properties = new List<Property>
                        {
                            new Property
                            {
                                Path = "title",
                                PropertyType = new PrimitivePropertyType
                                {
                                    Type = TypeKind.Utf8,
                                    Storage = StorageKind.Variable,
                                },
                            },
                            new Property
                            {
                                Path = "tags",
                                PropertyType = new ArrayPropertyType
                                {
                                    Items = new PrimitivePropertyType
                                    {
                                        Type = TypeKind.Utf8,
                                        Nullable = false,
                                    },
                                },
                            },
                            new Property
                            {
                                Path = "options",
                                PropertyType = new ArrayPropertyType
                                {
                                    Items = new PrimitivePropertyType
                                    {
                                        Type = TypeKind.Int32,
                                    },
                                },
                            },
                            new Property
                            {
                                Path = "ratings",
                                PropertyType = new ArrayPropertyType
                                {
                                    Items = new ArrayPropertyType
                                    {
                                        Items = new PrimitivePropertyType
                                        {
                                            Type = TypeKind.Float64,
                                            Nullable = false,
                                        },
                                        Nullable = false,
                                    },
                                },
                            },
                            new Property
                            {
                                Path = "similars",
                                PropertyType = new ArrayPropertyType
                                {
                                    Items = new UdtPropertyType
                                    {
                                        Name = "SimilarMatch",
                                        SchemaId = new SchemaId(0),
                                        Nullable = false,
                                    },
                                },
                            },
                            new Property
                            {
                                Path = "priority",
                                PropertyType = new ArrayPropertyType
                                {
                                    Items = new TuplePropertyType
                                    {
                                        Items = new List<PropertyType>
                                        {
                                            new PrimitivePropertyType
                                            {
                                                Type = TypeKind.Utf8,
                                                Nullable = false,
                                            },
                                            new PrimitivePropertyType
                                            {
                                                Type = TypeKind.Int64,
                                                Nullable = false,
                                            },
                                        },
                                        Nullable = false,
                                    },
                                },
                            },
                        },
                    },
                    //////////////////////////////////////////////////////////////////////////////
                    new Schema
                    {
                        Name = "SimilarMatch",
                        SchemaId = new SchemaId(2),
                        Properties = new List<Property>
                        {
                            new Property
                            {
                                Path = "thumbprint",
                                PropertyType = new PrimitivePropertyType
                                {
                                    Type = TypeKind.Utf8,
                                    Length = 18,
                                    Storage = StorageKind.Fixed,
                                },
                            },
                            new Property
                            {
                                Path = "score",
                                PropertyType = new PrimitivePropertyType
                                {
                                    Type = TypeKind.Float64,
                                    Storage = StorageKind.Fixed,
                                },
                            },
                        },
                    },
                },
            };
        }

        private static LayoutResolver LoadSchema()
        {
            return new LayoutResolverNamespace(TypedArrayHrSchema.Namespace);
        }
    }

    public sealed class Tagged
    {
        public string Title { get; set; }
        public List<string> Tags { get; set; }
        public List<int?> Options { get; set; }
        public List<List<double>> Ratings { get; set; }
        public List<SimilarMatch> Similars { get; set; }
        public List<(string, long)> Priority { get; set; }
    }

    public sealed class SimilarMatch
    {
        public string Thumbprint { get; set; }
        public double Score { get; set; }
    }

    public readonly struct TaggedHybridRowSerializer : IHybridRowSerializer<Tagged>
    {
        public const int SchemaId = 1;
        public const int Size = 1;
        public IEqualityComparer<Tagged> Comparer => TaggedComparer.Default;
        private static readonly Utf8String TitleName = Utf8String.TranscodeUtf16("title");
        private static readonly Utf8String TagsName = Utf8String.TranscodeUtf16("tags");
        private static readonly Utf8String OptionsName = Utf8String.TranscodeUtf16("options");
        private static readonly Utf8String RatingsName = Utf8String.TranscodeUtf16("ratings");
        private static readonly Utf8String SimilarsName = Utf8String.TranscodeUtf16("similars");
        private static readonly Utf8String PriorityName = Utf8String.TranscodeUtf16("priority");

        private static readonly LayoutColumn TitleColumn;
        private static readonly LayoutColumn TagsColumn;
        private static readonly LayoutColumn OptionsColumn;
        private static readonly LayoutColumn RatingsColumn;
        private static readonly LayoutColumn SimilarsColumn;
        private static readonly LayoutColumn PriorityColumn;

        private static readonly StringToken TagsToken;
        private static readonly StringToken OptionsToken;
        private static readonly StringToken RatingsToken;
        private static readonly StringToken SimilarsToken;
        private static readonly StringToken PriorityToken;

        static TaggedHybridRowSerializer()
        {
            Layout layout = TypedArrayHrSchema.LayoutResolver.Resolve(new SchemaId(SchemaId));

            bool found;
            found = layout.TryFind(TitleName, out TitleColumn);
            Contract.Invariant(found);
            found = layout.TryFind(TagsName, out TagsColumn);
            Contract.Invariant(found);
            found = layout.TryFind(OptionsName, out OptionsColumn);
            Contract.Invariant(found);
            found = layout.TryFind(RatingsName, out RatingsColumn);
            Contract.Invariant(found);
            found = layout.TryFind(SimilarsName, out SimilarsColumn);
            Contract.Invariant(found);
            found = layout.TryFind(PriorityName, out PriorityColumn);
            Contract.Invariant(found);

            found = layout.Tokenizer.TryFindToken(TagsColumn.Path, out TagsToken);
            Contract.Invariant(found);
            found = layout.Tokenizer.TryFindToken(OptionsColumn.Path, out OptionsToken);
            Contract.Invariant(found);
            found = layout.Tokenizer.TryFindToken(RatingsColumn.Path, out RatingsToken);
            Contract.Invariant(found);
            found = layout.Tokenizer.TryFindToken(SimilarsColumn.Path, out SimilarsToken);
            Contract.Invariant(found);
            found = layout.Tokenizer.TryFindToken(PriorityColumn.Path, out PriorityToken);
            Contract.Invariant(found);
        }

        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, Tagged value)
        {
            if (isRoot)
            {
                return Write(ref row, ref scope, value);
            }

            Result r = LayoutType.UDT.WriteScope(ref row, ref scope, new SchemaId(SchemaId), out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = Write(ref row, ref childScope, value);
            if (r != Result.Success)
            {
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result Write(ref RowBuffer row, ref RowCursor scope, Tagged value)
        {
            Result r;
            if (value.Title != default)
            {
                r = LayoutType.Utf8.WriteVariable(ref row, ref scope, TitleColumn, value.Title);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Tags != default)
            {
                scope.Find(ref row, TagsColumn.Path);
                r = default(TypedArrayHybridRowSerializer<string, Utf8HybridRowSerializer>).Write(
                    ref row,
                    ref scope,
                    false,
                    TagsColumn.TypeArgs,
                    value.Tags);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Options != default)
            {
                scope.Find(ref row, OptionsColumn.Path);
                r = default(TypedArrayHybridRowSerializer<int?, NullableHybridRowSerializer<int?, int, Int32HybridRowSerializer>>).Write(
                    ref row,
                    ref scope,
                    false,
                    OptionsColumn.TypeArgs,
                    value.Options);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Ratings != default)
            {
                scope.Find(ref row, RatingsColumn.Path);
                r = default(TypedArrayHybridRowSerializer<List<double>, TypedArrayHybridRowSerializer<double, Float64HybridRowSerializer>>).Write(
                    ref row,
                    ref scope,
                    false,
                    RatingsColumn.TypeArgs,
                    value.Ratings);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Similars != default)
            {
                scope.Find(ref row, SimilarsColumn.Path);
                r = default(TypedArrayHybridRowSerializer<SimilarMatch, SimilarMatchHybridRowSerializer>).Write(
                    ref row,
                    ref scope,
                    false,
                    SimilarsColumn.TypeArgs,
                    value.Similars);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Priority != default)
            {
                scope.Find(ref row, PriorityColumn.Path);
                r = default(TypedArrayHybridRowSerializer<(string, long), TypedTupleHybridRowSerializer<string, Utf8HybridRowSerializer, long, Int64HybridRowSerializer>>).Write(
                    ref row,
                    ref scope,
                    false,
                    PriorityColumn.TypeArgs,
                    value.Priority);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out Tagged value)
        {
            if (isRoot)
            {
                value = new Tagged();
                return Read(ref row, ref scope, ref value);
            }

            Result r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            value = new Tagged();
            r = Read(ref row, ref childScope, ref value);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result Read(ref RowBuffer row, ref RowCursor scope, ref Tagged value)
        {
            Result r;
            {
                r = LayoutType.Utf8.ReadVariable(ref row, ref scope, TitleColumn, out string fieldValue);
                switch (r)
                {
                    case Result.NotFound:
                        break;
                    case Result.Success:
                        value.Title = fieldValue;
                        break;
                    default:
                        return r;
                }
            }

            while (scope.MoveNext(ref row))
            {
                if (scope.Token == TagsToken.Id)
                {
                    r = default(TypedArrayHybridRowSerializer<string, Utf8HybridRowSerializer>)
                        .Read(ref row, ref scope, false, out List<string> fieldValue);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    value.Tags = fieldValue;
                    continue;
                }

                if (scope.Token == OptionsToken.Id)
                {
                    r = default(TypedArrayHybridRowSerializer<int?, NullableHybridRowSerializer<int?, int, Int32HybridRowSerializer>>)
                        .Read(ref row, ref scope, false, out List<int?> fieldValue);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    value.Options = fieldValue;
                    continue;
                }

                if (scope.Token == RatingsToken.Id)
                {
                    r = default(TypedArrayHybridRowSerializer<List<double>, TypedArrayHybridRowSerializer<double, Float64HybridRowSerializer>>)
                        .Read(ref row, ref scope, false, out List<List<double>> fieldValue);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    value.Ratings = fieldValue;
                    continue;
                }

                if (scope.Token == SimilarsToken.Id)
                {
                    r = default(TypedArrayHybridRowSerializer<SimilarMatch, SimilarMatchHybridRowSerializer>)
                        .Read(ref row, ref scope, false, out List<SimilarMatch> fieldValue);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    value.Similars = fieldValue;
                    continue;
                }

                if (scope.Token == PriorityToken.Id)
                {
                    r = default(TypedArrayHybridRowSerializer<(string, long), TypedTupleHybridRowSerializer<string, Utf8HybridRowSerializer, long, Int64HybridRowSerializer>>)
                        .Read(ref row, ref scope, false, out List<(string, long)> fieldValue);
                    if (r != Result.Success)
                    {
                        return r;
                    }

                    value.Priority = fieldValue;
                    continue;
                }
            }

            return Result.Success;
        }

        public sealed class TaggedComparer : EqualityComparer<Tagged>
        {
            public static new readonly TaggedComparer Default = new TaggedComparer();

            public override bool Equals(Tagged x, Tagged y)
            {
                HybridRowSerializer.EqualityReferenceResult refCheck = HybridRowSerializer.EqualityReferenceCheck(x, y);
                if (refCheck != HybridRowSerializer.EqualityReferenceResult.Unknown)
                {
                    return refCheck == HybridRowSerializer.EqualityReferenceResult.Equal;
                }

                return
                    default(Utf8HybridRowSerializer).Comparer.Equals(x.Title, y.Title) && 
                    default(TypedArrayHybridRowSerializer<string, Utf8HybridRowSerializer>).Comparer.Equals(x.Tags, y.Tags) && 
                    default(TypedArrayHybridRowSerializer<int?, NullableHybridRowSerializer<int?, int, Int32HybridRowSerializer>>).Comparer.Equals(x.Options, y.Options) && 
                    default(TypedArrayHybridRowSerializer<List<double>, TypedArrayHybridRowSerializer<double, Float64HybridRowSerializer>>).Comparer.Equals(x.Ratings, y.Ratings) && 
                    default(TypedArrayHybridRowSerializer<SimilarMatch, SimilarMatchHybridRowSerializer>).Comparer.Equals(x.Similars, y.Similars) && 
                    default(TypedArrayHybridRowSerializer<(string, long), TypedTupleHybridRowSerializer<string, Utf8HybridRowSerializer, long, Int64HybridRowSerializer>>).Comparer.Equals(x.Priority, y.Priority);
            }

            public override int GetHashCode(Tagged obj)
            {
                return HashCode.Combine(
                    default(Utf8HybridRowSerializer).Comparer.GetHashCode(obj.Title),
                    default(TypedArrayHybridRowSerializer<string, Utf8HybridRowSerializer>).Comparer.GetHashCode(obj.Tags),
                    default(TypedArrayHybridRowSerializer<int?, NullableHybridRowSerializer<int?, int, Int32HybridRowSerializer>>).Comparer.GetHashCode(obj.Options),
                    default(TypedArrayHybridRowSerializer<List<double>, TypedArrayHybridRowSerializer<double, Float64HybridRowSerializer>>).Comparer.GetHashCode(obj.Ratings),
                    default(TypedArrayHybridRowSerializer<SimilarMatch, SimilarMatchHybridRowSerializer>).Comparer.GetHashCode(obj.Similars),
                    default(TypedArrayHybridRowSerializer<(string, long), TypedTupleHybridRowSerializer<string, Utf8HybridRowSerializer, long, Int64HybridRowSerializer>>).Comparer.GetHashCode(obj.Priority));
            }
        }
    }

    public readonly struct SimilarMatchHybridRowSerializer : IHybridRowSerializer<SimilarMatch>
    {
        public const int SchemaId = 2;
        public const int Size = 27;
        public IEqualityComparer<SimilarMatch> Comparer => SimilarMatchComparer.Default;
        private static readonly Utf8String ThumbprintName = Utf8String.TranscodeUtf16("thumbprint");
        private static readonly Utf8String ScoreName = Utf8String.TranscodeUtf16("score");

        private static readonly LayoutColumn ThumbprintColumn;
        private static readonly LayoutColumn ScoreColumn;

        static SimilarMatchHybridRowSerializer()
        {
            Layout layout = TypedArrayHrSchema.LayoutResolver.Resolve(new SchemaId(SchemaId));

            bool found;
            found = layout.TryFind(ThumbprintName, out ThumbprintColumn);
            Contract.Invariant(found);
            found = layout.TryFind(ScoreName, out ScoreColumn);
            Contract.Invariant(found);
        }

        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, SimilarMatch value)
        {
            if (isRoot)
            {
                return Write(ref row, ref scope, value);
            }

            Result r = LayoutType.UDT.WriteScope(ref row, ref scope, new SchemaId(SchemaId), out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = Write(ref row, ref childScope, value);
            if (r != Result.Success)
            {
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result Write(ref RowBuffer row, ref RowCursor scope, SimilarMatch value)
        {
            Result r;
            if (value.Thumbprint != default)
            {
                r = LayoutType.Utf8.WriteFixed(ref row, ref scope, ThumbprintColumn, value.Thumbprint);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            if (value.Score != default)
            {
                r = LayoutType.Float64.WriteFixed(ref row, ref scope, ScoreColumn, value.Score);
                if (r != Result.Success)
                {
                    return r;
                }
            }

            return Result.Success;
        }

        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out SimilarMatch value)
        {
            if (isRoot)
            {
                value = new SimilarMatch();
                return Read(ref row, ref scope, ref value);
            }

            Result r = LayoutType.UDT.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            value = new SimilarMatch();
            r = Read(ref row, ref childScope, ref value);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result Read(ref RowBuffer row, ref RowCursor scope, ref SimilarMatch value)
        {
            Result r;
            {
                r = LayoutType.Utf8.ReadFixed(ref row, ref scope, ThumbprintColumn, out string fieldValue);
                switch (r)
                {
                    case Result.NotFound:
                        break;
                    case Result.Success:
                        value.Thumbprint = fieldValue;
                        break;
                    default:
                        return r;
                }
            }

            {
                r = LayoutType.Float64.ReadFixed(ref row, ref scope, ScoreColumn, out double fieldValue);
                switch (r)
                {
                    case Result.NotFound:
                        break;
                    case Result.Success:
                        value.Score = fieldValue;
                        break;
                    default:
                        return r;
                }
            }

            return Result.Success;
        }

        public sealed class SimilarMatchComparer : EqualityComparer<SimilarMatch>
        {
            public static new readonly SimilarMatchComparer Default = new SimilarMatchComparer();

            public override bool Equals(SimilarMatch x, SimilarMatch y)
            {
                HybridRowSerializer.EqualityReferenceResult refCheck = HybridRowSerializer.EqualityReferenceCheck(x, y);
                if (refCheck != HybridRowSerializer.EqualityReferenceResult.Unknown)
                {
                    return refCheck == HybridRowSerializer.EqualityReferenceResult.Equal;
                }

                return
                    default(Utf8HybridRowSerializer).Comparer.Equals(x.Thumbprint, y.Thumbprint) && 
                    default(Float64HybridRowSerializer).Comparer.Equals(x.Score, y.Score);
            }

            public override int GetHashCode(SimilarMatch obj)
            {
                return HashCode.Combine(
                    default(Utf8HybridRowSerializer).Comparer.GetHashCode(obj.Thumbprint),
                    default(Float64HybridRowSerializer).Comparer.GetHashCode(obj.Score));
            }
        }
    }
}
