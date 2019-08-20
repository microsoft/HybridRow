// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public ref struct StreamingRowGenerator
    {
        private RowBuffer row;

        public StreamingRowGenerator(int capacity, Layout layout, LayoutResolver resolver, ISpanResizer<byte> resizer = default)
        {
            this.row = new RowBuffer(capacity, resizer);
            this.row.InitLayout(HybridRowVersion.V1, layout, resolver);
        }

        public int Length => this.row.Length;

        public byte[] ToArray() => this.row.ToArray();

        public void WriteTo(Stream stream)
        {
            this.row.WriteTo(stream);
        }

        public bool ReadFrom(Stream stream, int length)
        {
            return this.row.ReadFrom(stream, length, HybridRowVersion.V1, this.row.Resolver);
        }

        public void Reset()
        {
            Layout layout = this.row.Resolver.Resolve(this.row.Header.SchemaId);
            this.row.InitLayout(HybridRowVersion.V1, layout, this.row.Resolver);
        }

        public RowReader GetReader()
        {
            return new RowReader(ref this.row);
        }

        public Result WriteBuffer(Dictionary<Utf8String, object> value)
        {
            return RowWriter.WriteBuffer(
                ref this.row,
                value,
                (ref RowWriter writer, TypeArgument typeArg, Dictionary<Utf8String, object> dict) =>
                {
                    Layout layout = writer.Resolver.Resolve(typeArg.TypeArgs.SchemaId);
                    foreach (LayoutColumn c in layout.Columns)
                    {
                        Result result = StreamingRowGenerator.LayoutCodeSwitch(ref writer, c.Path, c.TypeArg, dict[c.Path]);
                        if (result != Result.Success)
                        {
                            return result;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result LayoutCodeSwitch(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.Null:
                    return writer.WriteNull(path);

                case LayoutCode.Boolean:
                    return writer.WriteBool(path, value == null ? default : (bool)value);

                case LayoutCode.Int8:
                    return writer.WriteInt8(path, value == null ? default : (sbyte)value);

                case LayoutCode.Int16:
                    return writer.WriteInt16(path, value == null ? default : (short)value);

                case LayoutCode.Int32:
                    return writer.WriteInt32(path, value == null ? default : (int)value);

                case LayoutCode.Int64:
                    return writer.WriteInt64(path, value == null ? default : (long)value);

                case LayoutCode.UInt8:
                    return writer.WriteUInt8(path, value == null ? default : (byte)value);

                case LayoutCode.UInt16:
                    return writer.WriteUInt16(path, value == null ? default : (ushort)value);

                case LayoutCode.UInt32:
                    return writer.WriteUInt32(path, value == null ? default : (uint)value);

                case LayoutCode.UInt64:
                    return writer.WriteUInt64(path, value == null ? default : (ulong)value);

                case LayoutCode.VarInt:
                    return writer.WriteVarInt(path, value == null ? default : (long)value);

                case LayoutCode.VarUInt:
                    return writer.WriteVarUInt(path, value == null ? default : (ulong)value);

                case LayoutCode.Float32:
                    return writer.WriteFloat32(path, value == null ? default : (float)value);

                case LayoutCode.Float64:
                    return writer.WriteFloat64(path, value == null ? default : (double)value);

                case LayoutCode.Float128:
                    return writer.WriteFloat128(path, value == null ? default : (Float128)value);

                case LayoutCode.Decimal:
                    return writer.WriteDecimal(path, value == null ? default : (decimal)value);

                case LayoutCode.DateTime:
                    return writer.WriteDateTime(path, value == null ? default : (DateTime)value);

                case LayoutCode.UnixDateTime:
                    return writer.WriteUnixDateTime(path, value == null ? default : (UnixDateTime)value);

                case LayoutCode.Guid:
                    return writer.WriteGuid(path, value == null ? default : (Guid)value);

                case LayoutCode.MongoDbObjectId:
                    return writer.WriteMongoDbObjectId(path, value == null ? default : (MongoDbObjectId)value);

                case LayoutCode.Utf8:
                    return writer.WriteString(path, value == null ? default : (Utf8String)value);

                case LayoutCode.Binary:
                    return writer.WriteBinary(path, value == null ? default : (byte[])value);

                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                    return StreamingRowGenerator.DispatchObject(ref writer, path, typeArg, value);

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                    return StreamingRowGenerator.DispatchArray(ref writer, path, typeArg, value);

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                    return StreamingRowGenerator.DispatchSet(ref writer, path, typeArg, value);

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                    return StreamingRowGenerator.DispatchMap(ref writer, path, typeArg, value);

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                    return StreamingRowGenerator.DispatchTuple(ref writer, path, typeArg, value);

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                    return StreamingRowGenerator.DispatchNullable(ref writer, path, typeArg, value);

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                    return StreamingRowGenerator.DispatchUDT(ref writer, path, typeArg, value);

                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {typeArg}");
                    return Result.Failure;
            }
        }

        private static Result DispatchObject(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            return writer.WriteScope(
                path,
                typeArg,
                (Dictionary<Utf8String, object>)value,
                (ref RowWriter writer2, TypeArgument typeArg2, Dictionary<Utf8String, object> value2) =>
                {
                    // TODO: support properties in an object scope.
                    return Result.Success;
                });
        }

        private static Result DispatchArray(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            return writer.WriteScope(
                path,
                typeArg,
                (List<object>)value,
                (ref RowWriter writer2, TypeArgument typeArg2, List<object> items2) =>
                {
                    foreach (object item in items2)
                    {
                        Result r = StreamingRowGenerator.LayoutCodeSwitch(ref writer2, null, typeArg2.TypeArgs[0], item);
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result DispatchTuple(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count >= 2);
            List<object> items = (List<object>)value;
            Contract.Assert(items.Count == typeArg.TypeArgs.Count);

            return writer.WriteScope(
                path,
                typeArg,
                items,
                (ref RowWriter writer2, TypeArgument typeArg2, List<object> items2) =>
                {
                    for (int i = 0; i < items2.Count; i++)
                    {
                        object item = items2[i];
                        Result r = StreamingRowGenerator.LayoutCodeSwitch(ref writer2, null, typeArg2.TypeArgs[i], item);
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result DispatchNullable(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            RowWriter.WriterFunc<object> f0 = null;
            if (value != null)
            {
                f0 = (ref RowWriter writer2, TypeArgument typeArg2, object value2) =>
                    StreamingRowGenerator.LayoutCodeSwitch(ref writer2, null, typeArg2.TypeArgs[0], value2);
            }

            return writer.WriteScope(path, typeArg, value, f0);
        }

        private static Result DispatchSet(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            return writer.WriteScope(
                path,
                typeArg,
                (List<object>)value,
                (ref RowWriter writer2, TypeArgument typeArg2, List<object> items2) =>
                {
                    foreach (object item in items2)
                    {
                        Result r = StreamingRowGenerator.LayoutCodeSwitch(ref writer2, null, typeArg2.TypeArgs[0], item);
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result DispatchMap(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 2);

            return writer.WriteScope(
                path,
                typeArg,
                (List<object>)value,
                (ref RowWriter writer2, TypeArgument typeArg2, List<object> items2) =>
                {
                    TypeArgument fieldType = new TypeArgument(
                        typeArg2.Type.Immutable ? LayoutType.ImmutableTypedTuple : LayoutType.TypedTuple,
                        typeArg2.TypeArgs);

                    foreach (object item in items2)
                    {
                        Result r = StreamingRowGenerator.DispatchTuple(ref writer2, null, fieldType, item);
                        if (r != Result.Success)
                        {
                            return r;
                        }
                    }

                    return Result.Success;
                });
        }

        private static Result DispatchUDT(ref RowWriter writer, Utf8String path, TypeArgument typeArg, object value)
        {
            return writer.WriteScope(
                path,
                typeArg,
                (Dictionary<Utf8String, object>)value,
                (ref RowWriter writer2, TypeArgument typeArg2, Dictionary<Utf8String, object> dict) =>
                {
                    Layout udt = writer2.Resolver.Resolve(typeArg2.TypeArgs.SchemaId);
                    foreach (LayoutColumn c in udt.Columns)
                    {
                        Result result = StreamingRowGenerator.LayoutCodeSwitch(ref writer2, c.Path, c.TypeArg, dict[c.Path]);
                        if (result != Result.Success)
                        {
                            return result;
                        }
                    }

                    return Result.Success;
                });
        }
    }
}
