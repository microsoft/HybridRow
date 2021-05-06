// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1649 // File name should match first type name
#pragma warning disable CA1034 // Do not nest types.

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public struct Int8HybridRowSerializer : IHybridRowSerializer<sbyte>
    {
        public IEqualityComparer<sbyte> Comparer => EqualityComparer<sbyte>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, sbyte value)
        {
            return LayoutType.Int8.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out sbyte value)
        {
            return LayoutType.Int8.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Int16HybridRowSerializer : IHybridRowSerializer<short>
    {
        public IEqualityComparer<short> Comparer => EqualityComparer<short>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, short value)
        {
            return LayoutType.Int16.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out short value)
        {
            return LayoutType.Int16.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Int32HybridRowSerializer : IHybridRowSerializer<int>
    {
        public IEqualityComparer<int> Comparer => EqualityComparer<int>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, int value)
        {
            return LayoutType.Int32.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out int value)
        {
            return LayoutType.Int32.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Int64HybridRowSerializer : IHybridRowSerializer<long>
    {
        public IEqualityComparer<long> Comparer => EqualityComparer<long>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, long value)
        {
            return LayoutType.Int64.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out long value)
        {
            return LayoutType.Int64.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct UInt8HybridRowSerializer : IHybridRowSerializer<byte>
    {
        public IEqualityComparer<byte> Comparer => EqualityComparer<byte>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, byte value)
        {
            return LayoutType.UInt8.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out byte value)
        {
            return LayoutType.UInt8.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct UInt16HybridRowSerializer : IHybridRowSerializer<ushort>
    {
        public IEqualityComparer<ushort> Comparer => EqualityComparer<ushort>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, ushort value)
        {
            return LayoutType.UInt16.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out ushort value)
        {
            return LayoutType.UInt16.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct UInt32HybridRowSerializer : IHybridRowSerializer<uint>
    {
        public IEqualityComparer<uint> Comparer => EqualityComparer<uint>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, uint value)
        {
            return LayoutType.UInt32.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out uint value)
        {
            return LayoutType.UInt32.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct UInt64HybridRowSerializer : IHybridRowSerializer<ulong>
    {
        public IEqualityComparer<ulong> Comparer => EqualityComparer<ulong>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, ulong value)
        {
            return LayoutType.UInt64.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out ulong value)
        {
            return LayoutType.UInt64.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Float32HybridRowSerializer : IHybridRowSerializer<float>
    {
        public IEqualityComparer<float> Comparer => EqualityComparer<float>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, float value)
        {
            return LayoutType.Float32.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out float value)
        {
            return LayoutType.Float32.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Float64HybridRowSerializer : IHybridRowSerializer<double>
    {
        public IEqualityComparer<double> Comparer => EqualityComparer<double>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, double value)
        {
            return LayoutType.Float64.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out double value)
        {
            return LayoutType.Float64.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Float128HybridRowSerializer : IHybridRowSerializer<Float128>
    {
        public IEqualityComparer<Float128> Comparer => EqualityComparer<Float128>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, Float128 value)
        {
            return LayoutType.Float128.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out Float128 value)
        {
            return LayoutType.Float128.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct DecimalHybridRowSerializer : IHybridRowSerializer<decimal>
    {
        public IEqualityComparer<decimal> Comparer => EqualityComparer<decimal>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, decimal value)
        {
            return LayoutType.Decimal.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out decimal value)
        {
            return LayoutType.Decimal.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct BooleanHybridRowSerializer : IHybridRowSerializer<bool>
    {
        public IEqualityComparer<bool> Comparer => EqualityComparer<bool>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, bool value)
        {
            return LayoutType.Boolean.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out bool value)
        {
            return LayoutType.Boolean.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct NullHybridRowSerializer : IHybridRowSerializer<NullValue>
    {
        public IEqualityComparer<NullValue> Comparer => EqualityComparer<NullValue>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, NullValue value)
        {
            return LayoutType.Null.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out NullValue value)
        {
            return LayoutType.Null.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct DateTimeHybridRowSerializer : IHybridRowSerializer<DateTime>
    {
        public IEqualityComparer<DateTime> Comparer => EqualityComparer<DateTime>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, DateTime value)
        {
            return LayoutType.DateTime.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out DateTime value)
        {
            return LayoutType.DateTime.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct UnixDateTimeHybridRowSerializer : IHybridRowSerializer<UnixDateTime>
    {
        public IEqualityComparer<UnixDateTime> Comparer => EqualityComparer<UnixDateTime>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, UnixDateTime value)
        {
            return LayoutType.UnixDateTime.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out UnixDateTime value)
        {
            return LayoutType.UnixDateTime.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct GuidHybridRowSerializer : IHybridRowSerializer<Guid>
    {
        public IEqualityComparer<Guid> Comparer => EqualityComparer<Guid>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, Guid value)
        {
            return LayoutType.Guid.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out Guid value)
        {
            return LayoutType.Guid.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct MongoDbObjectIdHybridRowSerializer : IHybridRowSerializer<MongoDbObjectId>
    {
        public IEqualityComparer<MongoDbObjectId> Comparer => EqualityComparer<MongoDbObjectId>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, MongoDbObjectId value)
        {
            return LayoutType.MongoDbObjectId.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out MongoDbObjectId value)
        {
            return LayoutType.MongoDbObjectId.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct Utf8HybridRowSerializer : IHybridRowSerializer<string>
    {
        public IEqualityComparer<string> Comparer => EqualityComparer<string>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, string value)
        {
            return LayoutType.Utf8.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out string value)
        {
            return LayoutType.Utf8.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct BinaryHybridRowSerializer : IHybridRowSerializer<byte[]>
    {
        public IEqualityComparer<byte[]> Comparer => BinaryComparer.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, byte[] value)
        {
            return LayoutType.Binary.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out byte[] value)
        {
            return LayoutType.Binary.ReadSparse(ref row, ref scope, out value);
        }

        public sealed class BinaryComparer : EqualityComparer<byte[]>
        {
            public static new readonly BinaryComparer Default = new BinaryComparer();

            public override bool Equals(byte[] x, byte[] y)
            {
                return x.AsSpan().SequenceEqual(y.AsSpan());
            }

            public override int GetHashCode(byte[] obj)
            {
                HashCode hash = default;

                // Add bulk in 8-byte words.
                ReadOnlySpan<ulong> span = MemoryMarshal.Cast<byte, ulong>(obj.AsSpan());
                foreach (ulong i in span)
                {
                    hash.Add(i);
                }

                // Add any residual as separate bytes.
                ReadOnlySpan<byte> residual = obj.AsSpan().Slice(span.Length * sizeof(ulong));
                foreach (byte i in residual)
                {
                    hash.Add(i);
                }

                return hash.ToHashCode();
            }
        }
    }

    public struct VarIntHybridRowSerializer : IHybridRowSerializer<long>
    {
        public IEqualityComparer<long> Comparer => EqualityComparer<long>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, long value)
        {
            return LayoutType.VarInt.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out long value)
        {
            return LayoutType.VarInt.ReadSparse(ref row, ref scope, out value);
        }
    }

    public struct VarUIntHybridRowSerializer : IHybridRowSerializer<ulong>
    {
        public IEqualityComparer<ulong> Comparer => EqualityComparer<ulong>.Default;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Write(ref RowBuffer row, ref RowCursor scope, bool isRoot, TypeArgumentList typeArgs, ulong value)
        {
            return LayoutType.VarUInt.WriteSparse(ref row, ref scope, value);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Result Read(ref RowBuffer row, ref RowCursor scope, bool isRoot, out ulong value)
        {
            return LayoutType.VarUInt.ReadSparse(ref row, ref scope, out value);
        }
    }
}
