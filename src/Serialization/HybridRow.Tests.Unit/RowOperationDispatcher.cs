// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1402 // FileMayOnlyContainASingleType
#pragma warning disable SA1201 // OrderingRules
#pragma warning disable SA1401 // Public Fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Tests.Unit
{
    using System;
    using System.Collections;
    using System.IO;
    using System.Reflection;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.IO;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;
    using Microsoft.Azure.Cosmos.Serialization.HybridRowGenerator;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    internal ref struct RowOperationDispatcher
    {
        public readonly LayoutResolver Resolver;
        public RowBuffer Row;

        private const int InitialRowSize = 2 * 1024 * 1024;
        private readonly IDispatcher dispatcher;

        private RowOperationDispatcher(IDispatcher dispatcher, Layout layout, LayoutResolver resolver)
        {
            this.dispatcher = dispatcher;
            this.Row = new RowBuffer(RowOperationDispatcher.InitialRowSize);
            this.Resolver = resolver;
            this.Row.InitLayout(HybridRowVersion.V1, layout, this.Resolver);
        }

        private RowOperationDispatcher(IDispatcher dispatcher, LayoutResolver resolver, string expected)
        {
            this.dispatcher = dispatcher;
            this.Row = new RowBuffer(RowOperationDispatcher.InitialRowSize);
            this.Resolver = resolver;
            byte[] bytes = ByteConverter.ToBytes(expected);
            this.Row.ReadFrom(bytes, HybridRowVersion.V1, this.Resolver);
        }

        public static RowOperationDispatcher Create<TDispatcher>(Layout layout, LayoutResolver resolver)
            where TDispatcher : struct, IDispatcher
        {
            return new RowOperationDispatcher(default(TDispatcher), layout, resolver);
        }

        public static RowOperationDispatcher ReadFrom<TDispatcher>(LayoutResolver resolver, string expected)
            where TDispatcher : struct, IDispatcher
        {
            return new RowOperationDispatcher(default(TDispatcher), resolver, expected);
        }

        public string RowToHex()
        {
            using (MemoryStream stm = new MemoryStream())
            {
                this.Row.WriteTo(stm);
                ReadOnlyMemory<byte> bytes = stm.GetBuffer().AsMemory(0, (int)stm.Position);
                return ByteConverter.ToHex(bytes.Span);
            }
        }

        public RowReader GetReader()
        {
            return new RowReader(ref this.Row);
        }

        public void LayoutCodeSwitch(
            string path = null,
            LayoutType type = null,
            TypeArgumentList typeArgs = default,
            object value = null)
        {
            RowCursor root = RowCursor.Create(ref this.Row);
            this.LayoutCodeSwitch(ref root, path, type, typeArgs, value);
        }

        public void LayoutCodeSwitch(
            ref RowCursor scope,
            string path = null,
            LayoutType type = null,
            TypeArgumentList typeArgs = default,
            object value = null)
        {
            LayoutColumn col = null;
            if (type == null)
            {
                Assert.IsNotNull(path);
                Assert.IsTrue(scope.Layout.TryFind(path, out col));
                Assert.IsNotNull(col);
                type = col.Type;
                typeArgs = col.TypeArgs;
            }

            if ((path != null) && (col == null || col.Storage == StorageKind.Sparse))
            {
                scope.Find(ref this.Row, path);
            }

            switch (type.LayoutCode)
            {
                case LayoutCode.Null:
                    this.dispatcher.Dispatch<LayoutNull, NullValue>(ref this, ref scope, col, type, NullValue.Default);
                    break;
                case LayoutCode.Boolean:
                    this.dispatcher.Dispatch<LayoutBoolean, bool>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (bool?)value ?? default);

                    break;
                case LayoutCode.Int8:
                    this.dispatcher.Dispatch<LayoutInt8, sbyte>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (sbyte?)value ?? default);

                    break;
                case LayoutCode.Int16:
                    this.dispatcher.Dispatch<LayoutInt16, short>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (short?)value ?? default);

                    break;
                case LayoutCode.Int32:
                    this.dispatcher.Dispatch<LayoutInt32, int>(ref this, ref scope, col, type, (int?)value ?? default);
                    break;
                case LayoutCode.Int64:
                    this.dispatcher.Dispatch<LayoutInt64, long>(ref this, ref scope, col, type, (long?)value ?? default);
                    break;
                case LayoutCode.UInt8:
                    this.dispatcher.Dispatch<LayoutUInt8, byte>(ref this, ref scope, col, type, (byte?)value ?? default);
                    break;
                case LayoutCode.UInt16:
                    this.dispatcher.Dispatch<LayoutUInt16, ushort>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (ushort?)value ?? default);

                    break;
                case LayoutCode.UInt32:
                    this.dispatcher.Dispatch<LayoutUInt32, uint>(ref this, ref scope, col, type, (uint?)value ?? default);
                    break;
                case LayoutCode.UInt64:
                    this.dispatcher.Dispatch<LayoutUInt64, ulong>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (ulong?)value ?? default);

                    break;
                case LayoutCode.VarInt:
                    this.dispatcher.Dispatch<LayoutVarInt, long>(ref this, ref scope, col, type, (long?)value ?? default);
                    break;
                case LayoutCode.VarUInt:
                    this.dispatcher.Dispatch<LayoutVarUInt, ulong>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (ulong?)value ?? default);

                    break;
                case LayoutCode.Float32:
                    this.dispatcher.Dispatch<LayoutFloat32, float>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (float?)value ?? default);

                    break;
                case LayoutCode.Float64:
                    this.dispatcher.Dispatch<LayoutFloat64, double>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (double?)value ?? default);

                    break;
                case LayoutCode.Float128:
                    this.dispatcher.Dispatch<LayoutFloat128, Float128>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (Float128?)value ?? default);

                    break;
                case LayoutCode.Decimal:
                    this.dispatcher.Dispatch<LayoutDecimal, decimal>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (decimal?)value ?? default);

                    break;
                case LayoutCode.DateTime:
                    this.dispatcher.Dispatch<LayoutDateTime, DateTime>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (DateTime?)value ?? default);

                    break;
                case LayoutCode.UnixDateTime:
                    this.dispatcher.Dispatch<LayoutUnixDateTime, UnixDateTime>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (UnixDateTime?)value ?? default);

                    break;
                case LayoutCode.Guid:
                    this.dispatcher.Dispatch<LayoutGuid, Guid>(ref this, ref scope, col, type, (Guid?)value ?? default);
                    break;
                case LayoutCode.MongoDbObjectId:
                    this.dispatcher.Dispatch<LayoutMongoDbObjectId, MongoDbObjectId>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (MongoDbObjectId?)value ?? default);

                    break;
                case LayoutCode.Utf8:
                    this.dispatcher.Dispatch<LayoutUtf8, string>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (string)value);

                    break;
                case LayoutCode.Binary:
                    this.dispatcher.Dispatch<LayoutBinary, byte[]>(
                        ref this,
                        ref scope,
                        col,
                        type,
                        (byte[])value);

                    break;
                case LayoutCode.ObjectScope:
                case LayoutCode.ImmutableObjectScope:
                    this.dispatcher.DispatchObject(ref this, ref scope);
                    break;
                case LayoutCode.TypedArrayScope:
                case LayoutCode.ImmutableTypedArrayScope:
                    this.dispatcher.DispatchArray(ref this, ref scope, type, typeArgs, value);
                    break;
                case LayoutCode.TypedSetScope:
                case LayoutCode.ImmutableTypedSetScope:
                    this.dispatcher.DispatchSet(ref this, ref scope, type, typeArgs, value);
                    break;
                case LayoutCode.TypedMapScope:
                case LayoutCode.ImmutableTypedMapScope:
                    this.dispatcher.DispatchMap(ref this, ref scope, type, typeArgs, value);
                    break;
                case LayoutCode.TupleScope:
                case LayoutCode.ImmutableTupleScope:
                case LayoutCode.TypedTupleScope:
                case LayoutCode.ImmutableTypedTupleScope:
                case LayoutCode.TaggedScope:
                case LayoutCode.ImmutableTaggedScope:
                case LayoutCode.Tagged2Scope:
                case LayoutCode.ImmutableTagged2Scope:
                    this.dispatcher.DispatchTuple(ref this, ref scope, type, typeArgs, value);
                    break;
                case LayoutCode.NullableScope:
                    this.dispatcher.DispatchNullable(ref this, ref scope, type, typeArgs, value);
                    break;
                case LayoutCode.Schema:
                case LayoutCode.ImmutableSchema:
                    this.dispatcher.DispatchUDT(ref this, ref scope, type, typeArgs, value);
                    break;
                default:
                    Contract.Assert(false, $"Unknown type will be ignored: {type.LayoutCode}");
                    break;
            }
        }
    }

    internal interface IDispatcher
    {
        void Dispatch<TLayout, TValue>(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutColumn col,
            LayoutType t,
            TValue value = default)
            where TLayout : LayoutType<TValue>;

        void DispatchObject(ref RowOperationDispatcher dispatcher, ref RowCursor scope);

        void DispatchArray(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value);

        void DispatchTuple(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value);

        void DispatchNullable(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value);

        void DispatchSet(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value);

        void DispatchMap(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value);

        void DispatchUDT(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType type,
            TypeArgumentList typeArgs,
            object value);
    }

    internal interface IDispatchable
    {
        void Dispatch(ref RowOperationDispatcher dispatcher, ref RowCursor scope);
    }

    internal struct WriteRowDispatcher : IDispatcher
    {
        public void Dispatch<TLayout, TValue>(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor field,
            LayoutColumn col,
            LayoutType t,
            TValue value = default)
            where TLayout : LayoutType<TValue>
        {
            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().WriteFixed(ref dispatcher.Row, ref field, col, value));
                    break;
                case StorageKind.Variable:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().WriteVariable(ref dispatcher.Row, ref field, col, value));
                    break;
                default:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().WriteSparse(ref dispatcher.Row, ref field, value));
                    break;
            }
        }

        public void DispatchObject(ref RowOperationDispatcher dispatcher, ref RowCursor scope)
        {
        }

        public void DispatchArray(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutTypedArray>().WriteScope(ref dispatcher.Row, ref scope, typeArgs, out RowCursor arrayScope));

            IList items = (IList)value;
            foreach (object item in items)
            {
                dispatcher.LayoutCodeSwitch(ref arrayScope, null, typeArgs[0].Type, typeArgs[0].TypeArgs, item);
                arrayScope.MoveNext(ref dispatcher.Row);
            }
        }

        public void DispatchTuple(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count >= 2);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutIndexedScope>().WriteScope(ref dispatcher.Row, ref scope, typeArgs, out RowCursor tupleScope));

            for (int i = 0; i < typeArgs.Count; i++)
            {
                PropertyInfo valueAccessor = value.GetType().GetProperty($"Item{i + 1}");
                dispatcher.LayoutCodeSwitch(
                    ref tupleScope,
                    null,
                    typeArgs[i].Type,
                    typeArgs[i].TypeArgs,
                    valueAccessor.GetValue(value));
                tupleScope.MoveNext(ref dispatcher.Row);
            }
        }

        public void DispatchNullable(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutNullable>()
                    .WriteScope(ref dispatcher.Row, ref scope, typeArgs, value != null, out RowCursor nullableScope));

            if (value != null)
            {
                dispatcher.LayoutCodeSwitch(ref nullableScope, null, typeArgs[0].Type, typeArgs[0].TypeArgs, value);
            }
        }

        public void DispatchSet(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedSet>().WriteScope(ref dispatcher.Row, ref scope, typeArgs, out RowCursor setScope));
            IList items = (IList)value;
            foreach (object item in items)
            {
                string elmPath = Guid.NewGuid().ToString();
                RowCursor.CreateForAppend(ref dispatcher.Row, out RowCursor tempCursor);
                dispatcher.LayoutCodeSwitch(ref tempCursor, elmPath, typeArgs[0].Type, typeArgs[0].TypeArgs, item);

                // Move item into the set.
                ResultAssert.IsSuccess(t.TypeAs<LayoutTypedSet>().MoveField(ref dispatcher.Row, ref setScope, ref tempCursor));
            }
        }

        public void DispatchMap(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 2);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedMap>().WriteScope(ref dispatcher.Row, ref scope, typeArgs, out RowCursor mapScope));
            TypeArgument fieldType = t.TypeAs<LayoutUniqueScope>().FieldType(ref mapScope);
            IList pairs = (IList)value;
            foreach (object pair in pairs)
            {
                string elmPath = Guid.NewGuid().ToString();
                RowCursor.CreateForAppend(ref dispatcher.Row, out RowCursor tempCursor);
                dispatcher.LayoutCodeSwitch(ref tempCursor, elmPath, fieldType.Type, fieldType.TypeArgs, pair);

                // Move item into the map.
                ResultAssert.IsSuccess(t.TypeAs<LayoutTypedMap>().MoveField(ref dispatcher.Row, ref mapScope, ref tempCursor));
            }
        }

        public void DispatchUDT(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            ResultAssert.IsSuccess(t.TypeAs<LayoutUDT>().WriteScope(ref dispatcher.Row, ref scope, typeArgs, out RowCursor udtScope));
            IDispatchable valueDispatcher = value as IDispatchable;
            Assert.IsNotNull(valueDispatcher);
            valueDispatcher.Dispatch(ref dispatcher, ref udtScope);
        }
    }

    internal struct ReadRowDispatcher : IDispatcher
    {
        public void Dispatch<TLayout, TValue>(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor root,
            LayoutColumn col,
            LayoutType t,
            TValue expected = default)
            where TLayout : LayoutType<TValue>
        {
            TValue value;
            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().ReadFixed(ref dispatcher.Row, ref root, col, out value));
                    break;
                case StorageKind.Variable:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().ReadVariable(ref dispatcher.Row, ref root, col, out value));
                    break;
                default:
                    ResultAssert.IsSuccess(t.TypeAs<TLayout>().ReadSparse(ref dispatcher.Row, ref root, out value));
                    break;
            }

            if (typeof(TValue).IsArray)
            {
                CollectionAssert.AreEqual((ICollection)expected, (ICollection)value);
            }
            else
            {
                Assert.AreEqual(expected, value);
            }
        }

        public void DispatchObject(ref RowOperationDispatcher dispatcher, ref RowCursor scope)
        {
        }

        public void DispatchArray(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutTypedArray>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor arrayScope));

            int i = 0;
            IList items = (IList)value;
            while (arrayScope.MoveNext(ref dispatcher.Row))
            {
                dispatcher.LayoutCodeSwitch(
                    ref arrayScope,
                    null,
                    typeArgs[0].Type,
                    typeArgs[0].TypeArgs,
                    items[i++]);
            }
        }

        public void DispatchTuple(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count >= 2);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutIndexedScope>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor tupleScope));

            for (int i = 0; i < typeArgs.Count; i++)
            {
                tupleScope.MoveNext(ref dispatcher.Row);
                PropertyInfo valueAccessor = value.GetType().GetProperty($"Item{i + 1}");
                dispatcher.LayoutCodeSwitch(
                    ref tupleScope,
                    null,
                    typeArgs[i].Type,
                    typeArgs[i].TypeArgs,
                    valueAccessor.GetValue(value));
            }
        }

        public void DispatchNullable(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutNullable>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor nullableScope));

            if (value != null)
            {
                ResultAssert.IsSuccess(LayoutNullable.HasValue(ref dispatcher.Row, ref nullableScope));
                nullableScope.MoveNext(ref dispatcher.Row);
                dispatcher.LayoutCodeSwitch(ref nullableScope, null, typeArgs[0].Type, typeArgs[0].TypeArgs, value);
            }
            else
            {
                ResultAssert.NotFound(LayoutNullable.HasValue(ref dispatcher.Row, ref nullableScope));
            }
        }

        public void DispatchSet(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedSet>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor setScope));
            int i = 0;
            IList items = (IList)value;
            while (setScope.MoveNext(ref dispatcher.Row))
            {
                dispatcher.LayoutCodeSwitch(
                    ref setScope,
                    null,
                    typeArgs[0].Type,
                    typeArgs[0].TypeArgs,
                    items[i++]);
            }
        }

        public void DispatchMap(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 2);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedMap>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor mapScope));
            int i = 0;
            IList items = (IList)value;
            while (mapScope.MoveNext(ref dispatcher.Row))
            {
                dispatcher.LayoutCodeSwitch(
                    ref mapScope,
                    null,
                    LayoutType.TypedTuple,
                    typeArgs,
                    items[i++]);
            }
        }

        public void DispatchUDT(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            ResultAssert.IsSuccess(t.TypeAs<LayoutUDT>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor udtScope));
            IDispatchable valueDispatcher = value as IDispatchable;
            Assert.IsNotNull(valueDispatcher);
            valueDispatcher.Dispatch(ref dispatcher, ref udtScope);
        }
    }

    internal struct DeleteRowDispatcher : IDispatcher
    {
        public void Dispatch<TLayout, TValue>(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor root,
            LayoutColumn col,
            LayoutType t,
            TValue value = default)
            where TLayout : LayoutType<TValue>
        {
            ResultAssert.IsSuccess(t.TypeAs<TLayout>().DeleteSparse(ref dispatcher.Row, ref root));
        }

        public void DispatchObject(ref RowOperationDispatcher dispatcher, ref RowCursor scope)
        {
        }

        public void DispatchArray(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(
                t.TypeAs<LayoutTypedArray>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor arrayScope));

            if (!arrayScope.Immutable)
            {
                IList items = (IList)value;
                foreach (object item in items)
                {
                    Assert.IsTrue(arrayScope.MoveNext(ref dispatcher.Row));
                    dispatcher.LayoutCodeSwitch(ref arrayScope, null, typeArgs[0].Type, typeArgs[0].TypeArgs, item);
                }
            }

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedArray>().DeleteScope(ref dispatcher.Row, ref scope));
        }

        public void DispatchTuple(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count >= 2);
            ResultAssert.IsSuccess(t.TypeAs<LayoutIndexedScope>().DeleteScope(ref dispatcher.Row, ref scope));
        }

        public void DispatchNullable(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);
            ResultAssert.IsSuccess(t.TypeAs<LayoutNullable>().DeleteScope(ref dispatcher.Row, ref scope));
        }

        public void DispatchSet(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 1);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedSet>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor setScope));
            if (!setScope.Immutable)
            {
                IList items = (IList)value;
                foreach (object item in items)
                {
                    Assert.IsTrue(setScope.MoveNext(ref dispatcher.Row));
                    dispatcher.LayoutCodeSwitch(ref setScope, null, typeArgs[0].Type, typeArgs[0].TypeArgs, item);
                }
            }

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedSet>().DeleteScope(ref dispatcher.Row, ref scope));
        }

        public void DispatchMap(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            Contract.Requires(typeArgs.Count == 2);

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedMap>().ReadScope(ref dispatcher.Row, ref scope, out RowCursor mapScope));
            if (!mapScope.Immutable)
            {
                IList items = (IList)value;
                foreach (object item in items)
                {
                    Assert.IsTrue(mapScope.MoveNext(ref dispatcher.Row));
                    dispatcher.LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs, item);
                }
            }

            ResultAssert.IsSuccess(t.TypeAs<LayoutTypedMap>().DeleteScope(ref dispatcher.Row, ref scope));
        }

        public void DispatchUDT(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
            ResultAssert.IsSuccess(t.TypeAs<LayoutUDT>().DeleteScope(ref dispatcher.Row, ref scope));
        }
    }

    internal struct NullRowDispatcher : IDispatcher
    {
        public void Dispatch<TLayout, TValue>(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor root,
            LayoutColumn col,
            LayoutType t,
            TValue expected = default)
            where TLayout : LayoutType<TValue>
        {
            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    ResultAssert.NotFound(t.TypeAs<TLayout>().ReadFixed(ref dispatcher.Row, ref root, col, out TValue _));
                    break;
                case StorageKind.Variable:
                    ResultAssert.NotFound(t.TypeAs<TLayout>().ReadVariable(ref dispatcher.Row, ref root, col, out TValue _));
                    break;
                default:
                    ResultAssert.NotFound(t.TypeAs<TLayout>().ReadSparse(ref dispatcher.Row, ref root, out TValue _));
                    break;
            }
        }

        public void DispatchObject(ref RowOperationDispatcher dispatcher, ref RowCursor scope)
        {
        }

        public void DispatchArray(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }

        public void DispatchTuple(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }

        public void DispatchNullable(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }

        public void DispatchSet(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }

        public void DispatchMap(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }

        public void DispatchUDT(
            ref RowOperationDispatcher dispatcher,
            ref RowCursor scope,
            LayoutType t,
            TypeArgumentList typeArgs,
            object value)
        {
        }
    }
}
