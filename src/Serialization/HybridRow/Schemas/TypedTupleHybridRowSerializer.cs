// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1034 // Do not nest types.

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Collections.Generic;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public struct TypedTupleHybridRowSerializer<T1, T1Serializer, T2, T2Serializer>
        : IHybridRowSerializer<(T1 Item1, T2 Item2)>
        where T1Serializer : struct, IHybridRowSerializer<T1>
        where T2Serializer : struct, IHybridRowSerializer<T2>
    {
        public IEqualityComparer<(T1 Item1, T2 Item2)> Comparer => TypedTupleComparer.Default;

        public Result Write(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            TypeArgumentList typeArgs,
            (T1 Item1, T2 Item2) value)
        {
            Result r = LayoutType.TypedTuple.WriteScope(ref row, ref scope, typeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = default(T1Serializer).Write(ref row, ref childScope, false, typeArgs[0].TypeArgs, value.Item1);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T2Serializer).Write(ref row, ref childScope, false, typeArgs[1].TypeArgs, value.Item2);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            out (T1 Item1, T2 Item2) value)
        {
            Result r = LayoutType.TypedTuple.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            T1 item1 = default;
            T2 item2 = default;
            if (childScope.MoveNext(ref row))
            {
                r = default(T1Serializer).Read(ref row, ref childScope, isRoot, out item1);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                if (childScope.MoveNext(ref row))
                {
                    r = default(T2Serializer).Read(ref row, ref childScope, isRoot, out item2);
                    if (r != Result.Success)
                    {
                        value = default;
                        return r;
                    }
                }
            }

            value = (item1, item2);
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class TypedTupleComparer : EqualityComparer<(T1 Item1, T2 Item2)>
        {
            public static new readonly TypedTupleComparer Default = new TypedTupleComparer();

            public override bool Equals((T1 Item1, T2 Item2) x, (T1 Item1, T2 Item2) y)
            {
                return default(T1Serializer).Comparer.Equals(x.Item1, y.Item1) &&
                       default(T2Serializer).Comparer.Equals(x.Item2, y.Item2);
            }

            public override int GetHashCode((T1 Item1, T2 Item2) obj)
            {
                HashCode hash = default;
                hash.Add(obj.Item1, default(T1Serializer).Comparer);
                hash.Add(obj.Item2, default(T2Serializer).Comparer);
                return hash.ToHashCode();
            }
        }
    }

    public struct
        TypedTupleHybridRowSerializer<T1, T1Serializer, T2, T2Serializer, T3, T3Serializer>
        : IHybridRowSerializer<(T1 Item1, T2 Item2, T3 Item3)>
        where T1Serializer : struct, IHybridRowSerializer<T1>
        where T2Serializer : struct, IHybridRowSerializer<T2>
        where T3Serializer : struct, IHybridRowSerializer<T3>
    {
        public IEqualityComparer<(T1 Item1, T2 Item2, T3 Item3)> Comparer => TypedTupleComparer.Default;

        public Result Write(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            TypeArgumentList typeArgs,
            (T1 Item1, T2 Item2, T3 Item3) value)
        {
            Result r = LayoutType.TypedTuple.WriteScope(ref row, ref scope, typeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = default(T1Serializer).Write(ref row, ref childScope, false, typeArgs[0].TypeArgs, value.Item1);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T2Serializer).Write(ref row, ref childScope, false, typeArgs[1].TypeArgs, value.Item2);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T3Serializer).Write(ref row, ref childScope, false, typeArgs[2].TypeArgs, value.Item3);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            out (T1 Item1, T2 Item2, T3 Item3) value)
        {
            Result r = LayoutType.TypedTuple.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            T1 item1 = default;
            T2 item2 = default;
            T3 item3 = default;
            if (childScope.MoveNext(ref row))
            {
                r = default(T1Serializer).Read(ref row, ref childScope, isRoot, out item1);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                if (childScope.MoveNext(ref row))
                {
                    r = default(T2Serializer).Read(ref row, ref childScope, isRoot, out item2);
                    if (r != Result.Success)
                    {
                        value = default;
                        return r;
                    }

                    if (childScope.MoveNext(ref row))
                    {
                        r = default(T3Serializer).Read(ref row, ref childScope, isRoot, out item3);
                        if (r != Result.Success)
                        {
                            value = default;
                            return r;
                        }
                    }
                }
            }

            value = (item1, item2, item3);
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class TypedTupleComparer : EqualityComparer<(T1 Item1, T2 Item2, T3 Item3)>
        {
            public static new readonly TypedTupleComparer Default = new TypedTupleComparer();

            public override bool Equals((T1 Item1, T2 Item2, T3 Item3) x, (T1 Item1, T2 Item2, T3 Item3) y)
            {
                return default(T1Serializer).Comparer.Equals(x.Item1, y.Item1) &&
                       default(T2Serializer).Comparer.Equals(x.Item2, y.Item2) &&
                       default(T3Serializer).Comparer.Equals(x.Item3, y.Item3);
            }

            public override int GetHashCode((T1 Item1, T2 Item2, T3 Item3) obj)
            {
                HashCode hash = default;
                hash.Add(obj.Item1, default(T1Serializer).Comparer);
                hash.Add(obj.Item2, default(T2Serializer).Comparer);
                hash.Add(obj.Item3, default(T3Serializer).Comparer);
                return hash.ToHashCode();
            }
        }
    }

    public struct
        TypedTupleHybridRowSerializer<T1, T1Serializer, T2, T2Serializer, T3, T3Serializer, T4, T4Serializer>
        : IHybridRowSerializer<(T1 Item1, T2 Item2, T3 Item3, T4 Item4)>
        where T1Serializer : struct, IHybridRowSerializer<T1>
        where T2Serializer : struct, IHybridRowSerializer<T2>
        where T3Serializer : struct, IHybridRowSerializer<T3>
        where T4Serializer : struct, IHybridRowSerializer<T4>
    {
        public IEqualityComparer<(T1 Item1, T2 Item2, T3 Item3, T4 Item4)> Comparer => TypedTupleComparer.Default;

        public Result Write(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            TypeArgumentList typeArgs,
            (T1 Item1, T2 Item2, T3 Item3, T4 Item4) value)
        {
            Result r = LayoutType.TypedTuple.WriteScope(ref row, ref scope, typeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            r = default(T1Serializer).Write(ref row, ref childScope, false, typeArgs[0].TypeArgs, value.Item1);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T2Serializer).Write(ref row, ref childScope, false, typeArgs[1].TypeArgs, value.Item2);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T3Serializer).Write(ref row, ref childScope, false, typeArgs[2].TypeArgs, value.Item3);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            r = default(T4Serializer).Write(ref row, ref childScope, false, typeArgs[3].TypeArgs, value.Item4);
            if (r != Result.Success)
            {
                return r;
            }

            childScope.MoveNext(ref row);

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(
            ref RowBuffer row,
            ref RowCursor scope,
            bool isRoot,
            out (T1 Item1, T2 Item2, T3 Item3, T4 Item4) value)
        {
            Result r = LayoutType.TypedTuple.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            T1 item1 = default;
            T2 item2 = default;
            T3 item3 = default;
            T4 item4 = default;
            if (childScope.MoveNext(ref row))
            {
                r = default(T1Serializer).Read(ref row, ref childScope, isRoot, out item1);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                if (childScope.MoveNext(ref row))
                {
                    r = default(T2Serializer).Read(ref row, ref childScope, isRoot, out item2);
                    if (r != Result.Success)
                    {
                        value = default;
                        return r;
                    }

                    if (childScope.MoveNext(ref row))
                    {
                        r = default(T3Serializer).Read(ref row, ref childScope, isRoot, out item3);
                        if (r != Result.Success)
                        {
                            value = default;
                            return r;
                        }

                        if (childScope.MoveNext(ref row))
                        {
                            r = default(T4Serializer).Read(ref row, ref childScope, isRoot, out item4);
                            if (r != Result.Success)
                            {
                                value = default;
                                return r;
                            }
                        }
                    }
                }
            }

            value = (item1, item2, item3, item4);
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class TypedTupleComparer : EqualityComparer<(T1 Item1, T2 Item2, T3 Item3, T4 Item4)>
        {
            public static new readonly TypedTupleComparer Default = new TypedTupleComparer();

            public override bool Equals((T1 Item1, T2 Item2, T3 Item3, T4 Item4) x, (T1 Item1, T2 Item2, T3 Item3, T4 Item4) y)
            {
                return default(T1Serializer).Comparer.Equals(x.Item1, y.Item1) &&
                       default(T2Serializer).Comparer.Equals(x.Item2, y.Item2) &&
                       default(T3Serializer).Comparer.Equals(x.Item3, y.Item3) &&
                       default(T4Serializer).Comparer.Equals(x.Item4, y.Item4);
            }

            public override int GetHashCode((T1 Item1, T2 Item2, T3 Item3, T4 Item4) obj)
            {
                HashCode hash = default;
                hash.Add(obj.Item1, default(T1Serializer).Comparer);
                hash.Add(obj.Item2, default(T2Serializer).Comparer);
                hash.Add(obj.Item3, default(T3Serializer).Comparer);
                hash.Add(obj.Item4, default(T4Serializer).Comparer);
                return hash.ToHashCode();
            }
        }
    }
}
