// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1034 // Do not nest types.

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public struct NullableHybridRowSerializer<TNullable, T, TSerializer> : IHybridRowSerializer<TNullable>
        where TSerializer : struct, IHybridRowSerializer<T>
    {
        public IEqualityComparer<TNullable> Comparer => NullableComparer.Default;

        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, TNullable value)
        {
            bool hasValue = NullableHybridRowSerializer<TNullable, T, TSerializer>.HasValue(value);
            Result r = LayoutType.Nullable.WriteScope(ref row, ref scope, typeArgs, hasValue, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            if (hasValue)
            {
                r = default(TSerializer).Write(
                    ref row,
                    ref childScope,
                    false,
                    typeArgs[0].TypeArgs,
                    NullableHybridRowSerializer<TNullable, T, TSerializer>.AsValue(value));
                if (r != Result.Success)
                {
                    return r;
                }
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out TNullable value)
        {
            Result r = LayoutType.Nullable.ReadScope(ref row, ref scope, out RowCursor childScope);
            if (r != Result.Success)
            {
                value = default;
                return r;
            }

            if (childScope.MoveNext(ref row))
            {
                r = default(TSerializer).Read(ref row, ref childScope, isRoot, out T item);
                if (r != Result.Success)
                {
                    value = default;
                    return r;
                }

                value = NullableHybridRowSerializer<TNullable, T, TSerializer>.AsNullable(item);
            }
            else
            {
                value = default;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        public sealed class NullableComparer : EqualityComparer<TNullable>
        {
            public static new readonly NullableComparer Default = new NullableComparer();

            public override bool Equals(TNullable x, TNullable y)
            {
                bool xHasValue = NullableHybridRowSerializer<TNullable, T, TSerializer>.HasValue(x);
                if (xHasValue != NullableHybridRowSerializer<TNullable, T, TSerializer>.HasValue(y))
                {
                    return false;
                }
                if (!xHasValue)
                {
                    return true;
                }

                return default(TSerializer).Comparer.Equals(
                    NullableHybridRowSerializer<TNullable, T, TSerializer>.AsValue(x),
                    NullableHybridRowSerializer<TNullable, T, TSerializer>.AsValue(y));
            }

            public override int GetHashCode(TNullable obj)
            {
                HashCode hash = default;
                if (NullableHybridRowSerializer<TNullable, T, TSerializer>.HasValue(obj))
                {
                    hash.Add(NullableHybridRowSerializer<TNullable, T, TSerializer>.AsValue(obj), default(TSerializer).Comparer);
                }

                return hash.ToHashCode();
            }
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static bool HasValue(TNullable value)
        {
            return !((object)value is null);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static TNullable AsNullable(T value)
        {
            return (TNullable)(object)value;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static T AsValue(TNullable value)
        {
            return (T)(object)value;
        }
    }
}
