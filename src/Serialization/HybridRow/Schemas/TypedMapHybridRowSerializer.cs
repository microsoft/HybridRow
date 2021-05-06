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

    public struct TypedMapHybridRowSerializer<TKey, TKeySerializer, TValue, TValueSerializer> : IHybridRowSerializer<Dictionary<TKey, TValue>>
        where TKeySerializer : struct, IHybridRowSerializer<TKey>
        where TValueSerializer : struct, IHybridRowSerializer<TValue>
    {
        public IEqualityComparer<Dictionary<TKey, TValue>> Comparer => TypedMapComparer.Default;

        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, Dictionary<TKey, TValue> value)
        {
            Result r = LayoutType.TypedMap.WriteScope(ref row, ref scope, typeArgs, out RowCursor uniqueScope);
            if (r != Result.Success)
            {
                return r;
            }

            uniqueScope.Clone(out RowCursor childScope);
            childScope.deferUniqueIndex = true;

            foreach (KeyValuePair<TKey, TValue> item in value)
            {
                r = default(TypedTupleHybridRowSerializer<TKey, TKeySerializer, TValue, TValueSerializer>)
                    .Write(ref row, ref childScope, false, typeArgs, (item.Key, item.Value));
                if (r != Result.Success)
                {
                    return r;
                }

                childScope.MoveNext(ref row);
            }

            uniqueScope.count = childScope.count;
            r = row.TypedCollectionUniqueIndexRebuild(ref uniqueScope);
            if (r != Result.Success)
            {
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out Dictionary<TKey, TValue> value)
        {
            Result r = LayoutType.TypedMap.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            Dictionary<TKey, TValue> items = new Dictionary<TKey, TValue>(default(TKeySerializer).Comparer);
            while (childScope.MoveNext(ref row))
            {
                r = default(TypedTupleHybridRowSerializer<TKey, TKeySerializer, TValue, TValueSerializer>)
                    .Read(ref row, ref childScope, isRoot, out (TKey Key, TValue Value) item);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                items.Add(item.Key, item.Value);
            }

            value = items;
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class TypedMapComparer : EqualityComparer<Dictionary<TKey, TValue>>
        {
            public static new readonly TypedMapComparer Default = new TypedMapComparer();

            public override bool Equals(Dictionary<TKey, TValue> x, Dictionary<TKey, TValue> y)
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

                foreach (KeyValuePair<TKey, TValue> p in x)
                {
                    if (!y.TryGetValue(p.Key, out TValue value))
                    {
                        return false;
                    }

                    if (!default(TValueSerializer).Comparer.Equals(p.Value, value))
                    {
                        return false;
                    }
                }

                return true;
            }

            public override int GetHashCode(Dictionary<TKey, TValue> obj)
            {
                HashCode hash = default;
                IEqualityComparer<TKey> keyComparer = default(TKeySerializer).Comparer;
                IEqualityComparer<TValue> valueComparer = default(TValueSerializer).Comparer;
                foreach (KeyValuePair<TKey, TValue> p in obj)
                {
                    hash.Add(p.Key, keyComparer);
                    hash.Add(p.Value, valueComparer);
                }
                return hash.ToHashCode();
            }
        }
    }
}
