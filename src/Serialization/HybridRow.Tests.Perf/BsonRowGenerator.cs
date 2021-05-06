// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Perf
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using MongoDB.Bson;
    using MongoDB.Bson.IO;

    internal sealed class BsonRowGenerator : IDisposable
    {
        private readonly MemoryStream stream;
        private readonly BsonWriter writer;
        private readonly Layout layout;
        private readonly LayoutResolver resolver;

        public BsonRowGenerator(int capacity, Layout layout, LayoutResolver resolver)
        {
            this.stream = new MemoryStream(capacity);
            this.writer = new BsonBinaryWriter(this.stream);
            this.layout = layout;
            this.resolver = resolver;
        }

        public int Length => (int)this.stream.Position;

        public byte[] ToArray()
        {
            return this.stream.ToArray();
        }

        public void Reset()
        {
            this.stream.SetLength(0);
            this.stream.Position = 0;
        }

        public void WriteBuffer(Dictionary<Utf8String, object> dict)
        {
            this.writer.WriteStartDocument();
            foreach (LayoutColumn c in this.layout.Columns)
            {
                this.LayoutCodeSwitch(c.Path, c.TypeArg, dict[c.Path]);
            }

            this.writer.WriteEndDocument();
        }

        public void Dispose()
        {
            this.writer.Dispose();
            this.stream.Dispose();
        }

        private void LayoutCodeSwitch(UtfAnyString path, TypeArgument typeArg, object value)
        {
            if (!path.IsNull)
            {
                this.writer.WriteName(path);
            }

            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.Null:
                    this.writer.WriteNull();
                    return;

                case LayoutCode.Boolean:
                    this.writer.WriteBoolean(value == null ? default(bool) : (bool)value);
                    return;

                case LayoutCode.Int8:
                    this.writer.WriteInt32(value == null ? default(sbyte) : (sbyte)value);
                    return;

                case LayoutCode.Int16:
                    this.writer.WriteInt32(value == null ? default(short) : (short)value);
                    return;

                case LayoutCode.Int32:
                    this.writer.WriteInt32(value == null ? default(int) : (int)value);
                    return;

                case LayoutCode.Int64:
                    this.writer.WriteInt64(value == null ? default(long) : (long)value);
                    return;

                case LayoutCode.UInt8:
                    this.writer.WriteInt32(value == null ? default(byte) : (byte)value);
                    return;

                case LayoutCode.UInt16:
                    this.writer.WriteInt32(value == null ? default(ushort) : (ushort)value);
                    return;

                case LayoutCode.UInt32:
                    this.writer.WriteInt32(value == null ? default(int) : unchecked((int)(uint)value));
                    return;

                case LayoutCode.UInt64:
                    this.writer.WriteInt64(value == null ? default(long) : unchecked((long)(ulong)value));
                    return;

                case LayoutCode.VarInt:
                    this.writer.WriteInt64(value == null ? default(long) : (long)value);
                    return;

                case LayoutCode.VarUInt:
                    this.writer.WriteInt64(value == null ? default(long) : unchecked((long)(ulong)value));
                    return;

                case LayoutCode.Float32:
                    this.writer.WriteDouble(value == null ? default(float) : (float)value);
                    return;

                case LayoutCode.Float64:
                    this.writer.WriteDouble(value == null ? default(double) : (double)value);
                    return;

                case LayoutCode.Float128:
                    Decimal128 d128 = default(Decimal128);
                    if (value != null)
                    {
                        Float128 f128 = (Float128)value;
                        d128 = unchecked(Decimal128.FromIEEEBits((ulong)f128.High, (ulong)f128.Low));
                    }

                    this.writer.WriteDecimal128(d128);
                    return;

                case LayoutCode.Decimal:
                    this.writer.WriteDecimal128(value == null ? default(Decimal128) : new Decimal128((decimal)value));
                    return;

                case LayoutCode.DateTime:
                    this.writer.WriteDateTime(value == null ? default(long) : ((DateTime)value).Ticks);
                    return;

                case LayoutCode.UnixDateTime:
                    this.writer.WriteDateTime(value == null ? default(long) : ((UnixDateTime)value).Milliseconds);
                    return;

                case LayoutCode.Guid:
                    this.writer.WriteString(value == null ? string.Empty : ((Guid)value).ToString());
                    return;

                case LayoutCode.MongoDbObjectId:
                    this.writer.WriteObjectId(value == null ? default(ObjectId) : new ObjectId(((MongoDbObjectId)value).ToByteArray()));
                    return;

                case LayoutCode.Utf8:
                    this.writer.WriteString(value == null ? string.Empty : ((Utf8String)value).ToString());
                    return;

                case LayoutCode.Binary:
                    this.writer.WriteBytes(value == null ? default(byte[]) : (byte[])value);
                    return;

                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                    this.DispatchObject(typeArg, value);
                    return;

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                    this.DispatchArray(typeArg, value);
                    return;

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                    this.DispatchSet(typeArg, value);
                    return;

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                    this.DispatchMap(typeArg, value);
                    return;

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                    this.DispatchTuple(typeArg, value);
                    return;

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                    this.DispatchNullable(typeArg, value);
                    return;

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                    this.DispatchUDT(typeArg, value);
                    return;

                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {typeArg}");
                    return;
            }
        }

        private void DispatchObject(TypeArgument typeArg, object value)
        {
            this.writer.WriteStartDocument();

            // TODO: support properties in an object scope.
            this.writer.WriteEndDocument();
        }

        private void DispatchArray(TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            this.writer.WriteStartArray();
            foreach (object item in (List<object>)value)
            {
                this.LayoutCodeSwitch(null, typeArg.TypeArgs[0], item);
            }

            this.writer.WriteEndArray();
        }

        private void DispatchTuple(TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count >= 2);
            List<object> items = (List<object>)value;
            Contract.Assert(items.Count == typeArg.TypeArgs.Count);

            this.writer.WriteStartArray();
            for (int i = 0; i < items.Count; i++)
            {
                object item = items[i];
                this.LayoutCodeSwitch(null, typeArg.TypeArgs[i], item);
            }

            this.writer.WriteEndArray();
        }

        private void DispatchNullable(TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            if (value != null)
            {
                this.LayoutCodeSwitch(null, typeArg.TypeArgs[0], value);
            }
            else
            {
                this.writer.WriteNull();
            }
        }

        private void DispatchSet(TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 1);

            this.writer.WriteStartArray();
            foreach (object item in (List<object>)value)
            {
                this.LayoutCodeSwitch(null, typeArg.TypeArgs[0], item);
            }

            this.writer.WriteEndArray();
        }

        private void DispatchMap(TypeArgument typeArg, object value)
        {
            Contract.Requires(typeArg.TypeArgs.Count == 2);

            this.writer.WriteStartArray();
            foreach (object item in (List<object>)value)
            {
                this.DispatchTuple(typeArg, item);
            }

            this.writer.WriteEndArray();
        }

        private void DispatchUDT(TypeArgument typeArg, object value)
        {
            this.writer.WriteStartDocument();

            Dictionary<Utf8String, object> dict = (Dictionary<Utf8String, object>)value;
            Layout udt = this.resolver.Resolve(typeArg.TypeArgs.SchemaId);
            foreach (LayoutColumn c in udt.Columns)
            {
                this.LayoutCodeSwitch(c.Path, c.TypeArg, dict[c.Path]);
            }

            this.writer.WriteEndDocument();
        }
    }
}
