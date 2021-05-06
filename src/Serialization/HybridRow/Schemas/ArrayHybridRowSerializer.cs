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

    public struct ArrayHybridRowSerializer<T, TSerializer> : IHybridRowSerializer<List<T>>
        where TSerializer : struct, IHybridRowSerializer<T>
    {
        public IEqualityComparer<List<T>> Comparer => ArrayComparer.Default;

        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, List<T> value)
        {
            Result r = LayoutType.Array.WriteScope(ref row, ref scope, default, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            foreach (T item in value)
            {
                r = default(TSerializer).Write(ref row, ref childScope, false, typeArgs[0].TypeArgs, item);
                if (r != Result.Success)
                {
                    return r;
                }

                childScope.MoveNext(ref row);
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out List<T> value)
        {
            Result r = LayoutType.Array.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            List<T> items = new List<T>();
            while (childScope.MoveNext(ref row))
            {
                r = default(TSerializer).Read(ref row, ref childScope, isRoot, out T item);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                items.Add(item);
            }

            value = items;
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class ArrayComparer : EqualityComparer<List<T>>
        {
            public static new readonly ArrayComparer Default = new ArrayComparer();

            public override bool Equals(List<T> x, List<T> y)
            {
                if (object.ReferenceEquals(x, y))
                {
                    return true;
                }
                if (x is null || y is null)
                {
                    return false;
                }
                if (x.Count != y.Count)
                {
                    return false;
                }

                for (int i = 0; i < x.Count; i++)
                {
                    if (!default(TSerializer).Comparer.Equals(x[i], y[i]))
                    {
                        return false;
                    }
                }

                return true;
            }

            public override int GetHashCode(List<T> obj)
            {
                HashCode hash = default;
                IEqualityComparer<T> comparer = default(TSerializer).Comparer;
                foreach (T item in obj)
                {
                    hash.Add(item, comparer);
                }
                return hash.ToHashCode();
            }
        }
    }
}
