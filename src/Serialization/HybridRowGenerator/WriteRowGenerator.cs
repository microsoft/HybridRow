// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1401", Justification = "Test types.")]
    public ref struct WriteRowGenerator
    {
        private RowBuffer row;

        public WriteRowGenerator(int capacity, Layout layout, LayoutResolver resolver)
        {
            this.row = new RowBuffer(capacity);
            this.row.InitLayout(HybridRowVersion.V1, layout, resolver);
        }

        public int Length => this.row.Length;

        public void Reset()
        {
            Layout layout = this.row.Resolver.Resolve(this.row.Header.SchemaId);
            this.row.InitLayout(HybridRowVersion.V1, layout, this.row.Resolver);
        }

        public RowReader GetReader()
        {
            return new RowReader(ref this.row);
        }

        public Result DispatchLayout(Layout layout, Dictionary<Utf8String, object> dict)
        {
            RowCursor scope = RowCursor.Create(ref this.row);
            return WriteRowGenerator.DispatchLayout(ref this.row, ref scope, layout, dict);
        }

        private static Result LayoutCodeSwitch(
            ref RowBuffer row,
            ref RowCursor scope,
            LayoutColumn col = default,
            TypeArgument typeArg = default,
            object value = null)
        {
            if (col != null)
            {
                typeArg = col.TypeArg;
            }

            // ReSharper disable MergeConditionalExpression
            // ReSharper disable SimplifyConditionalTernaryExpression
            // ReSharper disable RedundantTypeSpecificationInDefaultExpression
#pragma warning disable IDE0034 // Simplify 'default' expression
            switch (typeArg.Type.LayoutCode)
            {
                case LayoutCode.Null:
                    return WriteRowGenerator.Dispatch<LayoutNull, NullValue>(ref row, ref scope, col, typeArg.Type, NullValue.Default);

                case LayoutCode.Boolean:
                    return WriteRowGenerator.Dispatch<LayoutBoolean, bool>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(bool) : (bool)value);

                case LayoutCode.Int8:
                    return WriteRowGenerator.Dispatch<LayoutInt8, sbyte>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(sbyte) : (sbyte)value);

                case LayoutCode.Int16:
                    return WriteRowGenerator.Dispatch<LayoutInt16, short>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(short) : (short)value);

                case LayoutCode.Int32:
                    return WriteRowGenerator.Dispatch<LayoutInt32, int>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(int) : (int)value);

                case LayoutCode.Int64:
                    return WriteRowGenerator.Dispatch<LayoutInt64, long>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(long) : (long)value);

                case LayoutCode.UInt8:
                    return WriteRowGenerator.Dispatch<LayoutUInt8, byte>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(byte) : (byte)value);

                case LayoutCode.UInt16:
                    return WriteRowGenerator.Dispatch<LayoutUInt16, ushort>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(ushort) : (ushort)value);

                case LayoutCode.UInt32:
                    return WriteRowGenerator.Dispatch<LayoutUInt32, uint>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(uint) : (uint)value);

                case LayoutCode.UInt64:
                    return WriteRowGenerator.Dispatch<LayoutUInt64, ulong>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(ulong) : (ulong)value);

                case LayoutCode.VarInt:
                    return WriteRowGenerator.Dispatch<LayoutVarInt, long>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(long) : (long)value);

                case LayoutCode.VarUInt:
                    return WriteRowGenerator.Dispatch<LayoutVarUInt, ulong>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(ulong) : (ulong)value);

                case LayoutCode.Float32:
                    return WriteRowGenerator.Dispatch<LayoutFloat32, float>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(float) : (float)value);

                case LayoutCode.Float64:
                    return WriteRowGenerator.Dispatch<LayoutFloat64, double>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(double) : (double)value);

                case LayoutCode.Float128:
                    return WriteRowGenerator.Dispatch<LayoutFloat128, Float128>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(Float128) : (Float128)value);

                case LayoutCode.Decimal:
                    return WriteRowGenerator.Dispatch<LayoutDecimal, decimal>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(decimal) : (decimal)value);

                case LayoutCode.DateTime:
                    return WriteRowGenerator.Dispatch<LayoutDateTime, DateTime>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(DateTime) : (DateTime)value);

                case LayoutCode.UnixDateTime:
                    return WriteRowGenerator.Dispatch<LayoutUnixDateTime, UnixDateTime>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(UnixDateTime) : (UnixDateTime)value);

                case LayoutCode.Guid:
                    return WriteRowGenerator.Dispatch<LayoutGuid, Guid>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(Guid) : (Guid)value);

                case LayoutCode.MongoDbObjectId:
                    return WriteRowGenerator.Dispatch<LayoutMongoDbObjectId, MongoDbObjectId>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(MongoDbObjectId) : (MongoDbObjectId)value);

                case LayoutCode.Utf8:
                    return WriteRowGenerator.Dispatch(ref row, ref scope, col, (Utf8String)value);

                case LayoutCode.Binary:
                    return WriteRowGenerator.Dispatch<LayoutBinary, byte[]>(
                        ref row,
                        ref scope,
                        col,
                        typeArg.Type,
                        value == null ? default(byte[]) : (byte[])value);

                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                    return WriteRowGenerator.DispatchObject(ref row, ref scope, typeArg, value);

                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                    return WriteRowGenerator.DispatchArray(ref row, ref scope, typeArg, value);

                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                    return WriteRowGenerator.DispatchSet(ref row, ref scope, typeArg, value);

                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                    return WriteRowGenerator.DispatchMap(ref row, ref scope, typeArg, value);

                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                    return WriteRowGenerator.DispatchTuple(ref row, ref scope, typeArg, value);

                case LayoutCode.NullableScope:
                case LayoutCode.ImmutableNullableScope:
                    return WriteRowGenerator.DispatchNullable(ref row, ref scope, typeArg, value);

                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                    return WriteRowGenerator.DispatchUDT(ref row, ref scope, typeArg, value);

                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {typeArg.Type.LayoutCode}");
                    return Result.Failure;
            }

            // ReSharper restore SimplifyConditionalTernaryExpression
            // ReSharper restore MergeConditionalExpression
            // ReSharper restore RedundantTypeSpecificationInDefaultExpression
#pragma warning restore IDE0034 // Simplify 'default' expression
        }

        private static Result Dispatch<TLayout, TValue>(ref RowBuffer row, ref RowCursor root, LayoutColumn col, LayoutType t, TValue value)
            where TLayout : LayoutType<TValue>
        {
            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<TLayout>().WriteFixed(ref row, ref root, col, value);

                case StorageKind.Variable:
                    return t.TypeAs<TLayout>().WriteVariable(ref row, ref root, col, value);

                default:
                    return t.TypeAs<TLayout>().WriteSparse(ref row, ref root, value);
            }
        }

        private static Result Dispatch(ref RowBuffer row, ref RowCursor root, LayoutColumn col, Utf8String value)
        {
            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    return LayoutType.Utf8.WriteFixed(ref row, ref root, col, value);

                case StorageKind.Variable:
                    return LayoutType.Utf8.WriteVariable(ref row, ref root, col, value);

                default:
                    return LayoutType.Utf8.WriteSparse(ref row, ref root, value);
            }
        }

        private static Result DispatchObject(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Result r = t.TypeAs<LayoutObject>().WriteScope(ref row, ref scope, t.TypeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            // TODO: support properties in an object scope.
            Dictionary<Utf8String, object> dict = (Dictionary<Utf8String, object>)value;
            Contract.Assert(dict.Count == 0);
            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result DispatchArray(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            Result r = t.TypeAs<LayoutTypedArray>().WriteScope(ref row, ref scope, t.TypeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            List<object> items = (List<object>)value;
            foreach (object item in items)
            {
                r = WriteRowGenerator.LayoutCodeSwitch(ref row, ref childScope, null, t.TypeArgs[0], item);
                if (r != Result.Success)
                {
                    return r;
                }

                childScope.MoveNext(ref row);
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result DispatchTuple(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Contract.Assert(t.TypeArgs.Count >= 2);

            return t.TypeAs<LayoutIndexedScope>()
                .WriteScope(
                    ref row,
                    ref scope,
                    t.TypeArgs,
                    (List<object>)value,
                    (ref RowBuffer row2, ref RowCursor childScope, List<object> items) =>
                    {
                        Contract.Assert(items.Count == childScope.ScopeTypeArgs.Count);
                        for (int i = 0; i < items.Count; i++)
                        {
                            Result r = WriteRowGenerator.LayoutCodeSwitch(ref row2, ref childScope, null, childScope.ScopeTypeArgs[i], items[i]);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            childScope.MoveNext(ref row2);
                        }

                        return Result.Success;
                    });
        }

        private static Result DispatchNullable(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            bool hasValue = value != null;
            Result r = t.TypeAs<LayoutNullable>().WriteScope(ref row, ref scope, t.TypeArgs, hasValue, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            if (hasValue)
            {
                r = WriteRowGenerator.LayoutCodeSwitch(ref row, ref childScope, null, t.TypeArgs[0], value);
                if (r != Result.Success)
                {
                    return r;
                }

                childScope.MoveNext(ref row);
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result DispatchSet(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Contract.Assert(t.TypeArgs.Count == 1);

            return t.TypeAs<LayoutTypedSet>()
                .WriteScope(
                    ref row,
                    ref scope,
                    t.TypeArgs,
                    (List<object>)value,
                    (ref RowBuffer row2, ref RowCursor childScope, List<object> items) =>
                    {
                        foreach (object item in items)
                        {
                            Result r = WriteRowGenerator.LayoutCodeSwitch(ref row2, ref childScope, null, childScope.ScopeTypeArgs[0], item);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            childScope.MoveNext(ref row2);
                        }

                        return Result.Success;
                    });
        }

        private static Result DispatchMap(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Contract.Assert(t.TypeArgs.Count == 2);
            return t.TypeAs<LayoutUniqueScope>()
                .WriteScope(
                    ref row,
                    ref scope,
                    t.TypeArgs,
                    (List<object>)value,
                    (ref RowBuffer row2, ref RowCursor childScope, List<object> pairs) =>
                    {
                        TypeArgument fieldType = childScope.ScopeType.TypeAs<LayoutUniqueScope>().FieldType(ref childScope);
                        foreach (object elm in pairs)
                        {
                            Result r = WriteRowGenerator.LayoutCodeSwitch(ref row2, ref childScope, null, fieldType, elm);
                            if (r != Result.Success)
                            {
                                return r;
                            }

                            childScope.MoveNext(ref row2);
                        }

                        return Result.Success;
                    });
        }

        private static Result DispatchUDT(ref RowBuffer row, ref RowCursor scope, TypeArgument t, object value)
        {
            Result r = t.TypeAs<LayoutUDT>().WriteScope(ref row, ref scope, t.TypeArgs, out RowCursor childScope);
            if (r != Result.Success)
            {
                return r;
            }

            Dictionary<Utf8String, object> dict = (Dictionary<Utf8String, object>)value;
            Layout layout = row.Resolver.Resolve(t.TypeArgs.SchemaId);
            r = WriteRowGenerator.DispatchLayout(ref row, ref childScope, layout, dict);
            if (r != Result.Success)
            {
                return r;
            }

            scope.Skip(ref row, ref childScope);
            return Result.Success;
        }

        private static Result DispatchLayout(ref RowBuffer row, ref RowCursor scope, Layout layout, Dictionary<Utf8String, object> dict)
        {
            foreach (LayoutColumn c in layout.Columns)
            {
                if (c.Storage != StorageKind.Sparse)
                {
                    Result r = WriteRowGenerator.LayoutCodeSwitch(ref row, ref scope, c, value: dict[c.Path]);
                    if (r != Result.Success)
                    {
                        return r;
                    }
                }
                else
                {
                    scope.Find(ref row, c.Path);
                    Result r = WriteRowGenerator.LayoutCodeSwitch(ref row, ref scope, null, c.TypeArg, dict[c.Path]);
                    if (r != Result.Success)
                    {
                        return r;
                    }
                }
            }

            return Result.Success;
        }
    }
}
