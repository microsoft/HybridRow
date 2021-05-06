// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using System;
    using System.Buffers;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    public ref struct RowWriter
    {
        private RowBuffer row;
        private RowCursor cursor;

        /// <summary>Initializes a new instance of the <see cref="RowWriter" /> struct.</summary>
        /// <param name="row">The row to be read.</param>
        /// <param name="scope">The scope into which items should be written.</param>
        /// <remarks>
        /// A <see cref="RowWriter" /> instance writes the fields of a given scope from left to right
        /// in a forward only manner. If the root scope is provided then all top-level fields in the row can be
        /// written.
        /// </remarks>
        private RowWriter(ref RowBuffer row, ref RowCursor scope)
        {
            this.row = row;
            this.cursor = scope;
        }

        /// <summary>A function to write content into a <see cref="RowBuffer" />.</summary>
        /// <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
        /// <param name="writer">A forward-only cursor for writing content.</param>
        /// <param name="typeArg">The type of the current scope.</param>
        /// <param name="context">A context value provided by the caller.</param>
        /// <returns>The result.</returns>
        public delegate Result WriterFunc<in TContext>(ref RowWriter writer, TypeArgument typeArg, TContext context);

        private delegate void AccessMethod<in TValue>(ref RowWriter writer, TValue value);

        private delegate void AccessReadOnlySpanMethod<T>(ref RowWriter writer, ReadOnlySpan<T> value);

        private delegate void AccessUtf8SpanMethod(ref RowWriter writer, Utf8Span value);

        /// <summary>The resolver for UDTs.</summary>
        public LayoutResolver Resolver => this.row.Resolver;

        /// <summary>The length of row in bytes.</summary>
        public int Length => this.row.Length;

        /// <summary>The active layout of the current writer scope.</summary>
        public Layout Layout => this.cursor.layout;

        /// <summary>Write an entire row in a streaming left-to-right way.</summary>
        /// <typeparam name="TContext">The type of the context value to pass to <paramref name="func" />.</typeparam>
        /// <param name="row">The row to write.</param>
        /// <param name="context">A context value to pass to <paramref name="func" />.</param>
        /// <param name="func">A function to write the entire row.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public static Result WriteBuffer<TContext>(ref RowBuffer row, TContext context, WriterFunc<TContext> func)
        {
            RowCursor scope = RowCursor.Create(ref row);
            RowWriter writer = new RowWriter(ref row, ref scope);
            TypeArgument typeArg = new TypeArgument(LayoutType.UDT, new TypeArgumentList(scope.layout.SchemaId));
            Result result = func(ref writer, typeArg, context);
            row = writer.row;
            return result;
        }

        /// <summary>Write a field as a <see cref="bool" />.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteBool(UtfAnyString path, bool value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Boolean,
                (ref RowWriter w, bool v) => w.row.WriteSparseBool(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a <see cref="t:null"/>.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteNull(UtfAnyString path)
        {
            return this.WritePrimitive(
                path,
                NullValue.Default,
                LayoutType.Null,
                (ref RowWriter w, NullValue v) => w.row.WriteSparseNull(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 8-bit, signed integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteInt8(UtfAnyString path, sbyte value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Int8,
                (ref RowWriter w, sbyte v) => w.row.WriteSparseInt8(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 16-bit, signed integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteInt16(UtfAnyString path, short value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Int16,
                (ref RowWriter w, short v) => w.row.WriteSparseInt16(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 32-bit, signed integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteInt32(UtfAnyString path, int value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Int32,
                (ref RowWriter w, int v) => w.row.WriteSparseInt32(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 64-bit, signed integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteInt64(UtfAnyString path, long value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Int64,
                (ref RowWriter w, long v) => w.row.WriteSparseInt64(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 8-bit, unsigned integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteUInt8(UtfAnyString path, byte value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.UInt8,
                (ref RowWriter w, byte v) => w.row.WriteSparseUInt8(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 16-bit, unsigned integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteUInt16(UtfAnyString path, ushort value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.UInt16,
                (ref RowWriter w, ushort v) => w.row.WriteSparseUInt16(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 32-bit, unsigned integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteUInt32(UtfAnyString path, uint value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.UInt32,
                (ref RowWriter w, uint v) => w.row.WriteSparseUInt32(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 64-bit, unsigned integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteUInt64(UtfAnyString path, ulong value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.UInt64,
                (ref RowWriter w, ulong v) => w.row.WriteSparseUInt64(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, 7-bit encoded, signed integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteVarInt(UtfAnyString path, long value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.VarInt,
                (ref RowWriter w, long v) => w.row.WriteSparseVarInt(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, 7-bit encoded, unsigned integer.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteVarUInt(UtfAnyString path, ulong value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.VarUInt,
                (ref RowWriter w, ulong v) => w.row.WriteSparseVarUInt(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 32-bit, IEEE-encoded floating point value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteFloat32(UtfAnyString path, float value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Float32,
                (ref RowWriter w, float v) => w.row.WriteSparseFloat32(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 64-bit, IEEE-encoded floating point value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteFloat64(UtfAnyString path, double value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Float64,
                (ref RowWriter w, double v) => w.row.WriteSparseFloat64(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length, 128-bit, IEEE-encoded floating point value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteFloat128(UtfAnyString path, Float128 value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Float128,
                (ref RowWriter w, Float128 v) => w.row.WriteSparseFloat128(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length <see cref="decimal" /> value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteDecimal(UtfAnyString path, decimal value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Decimal,
                (ref RowWriter w, decimal v) => w.row.WriteSparseDecimal(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length <see cref="DateTime" /> value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteDateTime(UtfAnyString path, DateTime value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.DateTime,
                (ref RowWriter w, DateTime v) => w.row.WriteSparseDateTime(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length <see cref="UnixDateTime" /> value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteUnixDateTime(UtfAnyString path, UnixDateTime value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.UnixDateTime,
                (ref RowWriter w, UnixDateTime v) => w.row.WriteSparseUnixDateTime(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length <see cref="Guid" /> value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteGuid(UtfAnyString path, Guid value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Guid,
                (ref RowWriter w, Guid v) => w.row.WriteSparseGuid(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a fixed length <see cref="MongoDbObjectId" /> value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteMongoDbObjectId(UtfAnyString path, MongoDbObjectId value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.MongoDbObjectId,
                (ref RowWriter w, MongoDbObjectId v) => w.row.WriteSparseMongoDbObjectId(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, UTF8 encoded, string value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteString(UtfAnyString path, string value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Utf8,
                (ref RowWriter w, string v) => w.row.WriteSparseString(ref w.cursor, Utf8Span.TranscodeUtf16(v), UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, UTF8 encoded, string value.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteString(UtfAnyString path, Utf8Span value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Utf8,
                (ref RowWriter w, Utf8Span v) => w.row.WriteSparseString(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, sequence of bytes.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteBinary(UtfAnyString path, byte[] value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Binary,
                (ref RowWriter w, byte[] v) => w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, sequence of bytes.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteBinary(UtfAnyString path, ReadOnlySpan<byte> value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Binary,
                (ref RowWriter w, ReadOnlySpan<byte> v) => w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        }

        /// <summary>Write a field as a variable length, sequence of bytes.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        public Result WriteBinary(UtfAnyString path, ReadOnlySequence<byte> value)
        {
            return this.WritePrimitive(
                path,
                value,
                LayoutType.Binary,
                (ref RowWriter w, ReadOnlySequence<byte> v) => w.row.WriteSparseBinary(ref w.cursor, v, UpdateOptions.Upsert));
        }

        public Result WriteScope<T, TSerializer>(UtfAnyString path, TypeArgument typeArg, T value)
            where TSerializer : struct, IHybridRowSerializer<T>
        {
            Result result = this.PrepareSparseWrite(path, typeArg);
            if (result != Result.Success)
            {
                return result;
            }

            result = default(TSerializer).Write(ref this.row, ref this.cursor, isRoot: false, typeArg.TypeArgs, value);
            if (result != Result.Success)
            {
                return result;
            }

            this.cursor.MoveNext(ref this.row);
            return Result.Success;
        }

        public Result WriteScope<TContext>(UtfAnyString path, TypeArgument typeArg, TContext context, WriterFunc<TContext> func)
        {
            LayoutType type = typeArg.Type;
            Result result = this.PrepareSparseWrite(path, typeArg);
            if (result != Result.Success)
            {
                return result;
            }

            RowCursor nestedScope;
            switch (type)
            {
                case LayoutObject scopeType:
                    this.row.WriteSparseObject(ref this.cursor, scopeType, UpdateOptions.Upsert, out nestedScope);
                    break;
                case LayoutArray scopeType:
                    this.row.WriteSparseArray(ref this.cursor, scopeType, UpdateOptions.Upsert, out nestedScope);
                    break;
                case LayoutTypedArray scopeType:
                    this.row.WriteTypedArray(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutTuple scopeType:
                    this.row.WriteSparseTuple(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutTypedTuple scopeType:
                    this.row.WriteTypedTuple(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutTagged scopeType:
                    this.row.WriteTypedTuple(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutTagged2 scopeType:
                    this.row.WriteTypedTuple(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutNullable scopeType:
                    this.row.WriteNullable(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        func != null,
                        out nestedScope);

                    break;
                case LayoutUDT scopeType:
                    Layout udt = this.row.Resolver.Resolve(typeArg.TypeArgs.SchemaId);
                    this.row.WriteSparseUDT(ref this.cursor, scopeType, udt, UpdateOptions.Upsert, out nestedScope);
                    break;

                case LayoutTypedSet scopeType:
                    this.row.WriteTypedSet(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;
                case LayoutTypedMap scopeType:
                    this.row.WriteTypedMap(
                        ref this.cursor,
                        scopeType,
                        typeArg.TypeArgs,
                        UpdateOptions.Upsert,
                        out nestedScope);

                    break;

                default:
                    return Result.Failure;
            }

            RowWriter nestedWriter = new RowWriter(ref this.row, ref nestedScope);
            result = func?.Invoke(ref nestedWriter, typeArg, context) ?? Result.Success;
            this.row = nestedWriter.row;
            nestedScope.count = nestedWriter.cursor.count;

            if (result != Result.Success)
            {
                // TODO: what about unique violations here?
                return result;
            }

            if (type is LayoutUniqueScope)
            {
                result = this.row.TypedCollectionUniqueIndexRebuild(ref nestedScope);
                if (result != Result.Success)
                {
                    // TODO: If the index rebuild fails then the row is corrupted.  Should we automatically clean up here?
                    return result;
                }
            }

            this.cursor.MoveNext(ref this.row, ref nestedWriter.cursor);
            return Result.Success;
        }

        /// <summary>Helper for writing a primitive value.</summary>
        /// <typeparam name="TValue">The type of the primitive value.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <param name="type">The layout type.</param>
        /// <param name="sparse">The <see cref="RowBuffer" /> access method for <paramref name="type" />.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WritePrimitive<TValue>(UtfAnyString path, TValue value, LayoutType<TValue> type, AccessMethod<TValue> sparse)
        {
            Result result = Result.NotFound;
            if (this.cursor.scopeType is LayoutUDT)
            {
                result = this.WriteSchematizedValue(path, value);
            }

            if (result == Result.NotFound)
            {
                // Write sparse value.

                result = this.PrepareSparseWrite(path, type.TypeArg);
                if (result != Result.Success)
                {
                    return result;
                }

                sparse(ref this, value);
                this.cursor.MoveNext(ref this.row);
            }

            return result;
        }

        /// <summary>Helper for writing a primitive value.</summary>
        /// <typeparam name="TLayoutType">The type of layout type.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <param name="type">The layout type.</param>
        /// <param name="sparse">The <see cref="RowBuffer" /> access method for <paramref name="type" />.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WritePrimitive<TLayoutType>(
            UtfAnyString path,
            Utf8Span value,
            TLayoutType type,
            AccessUtf8SpanMethod sparse)
            where TLayoutType : LayoutType<string>, ILayoutUtf8SpanWritable
        {
            Result result = Result.NotFound;
            if (this.cursor.scopeType is LayoutUDT)
            {
                result = this.WriteSchematizedValue(path, value);
            }

            if (result == Result.NotFound)
            {
                // Write sparse value.
                result = this.PrepareSparseWrite(path, type.TypeArg);
                if (result != Result.Success)
                {
                    return result;
                }

                sparse(ref this, value);
                this.cursor.MoveNext(ref this.row);
            }

            return result;
        }

        /// <summary>Helper for writing a primitive value.</summary>
        /// <typeparam name="TLayoutType">The type of layout type.</typeparam>
        /// <typeparam name="TElement">The sub-element type of the field.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <param name="type">The layout type.</param>
        /// <param name="sparse">The <see cref="RowBuffer" /> access method for <paramref name="type" />.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WritePrimitive<TLayoutType, TElement>(
            UtfAnyString path,
            ReadOnlySpan<TElement> value,
            TLayoutType type,
            AccessReadOnlySpanMethod<TElement> sparse)
            where TLayoutType : LayoutType<TElement[]>, ILayoutSpanWritable<TElement>
        {
            Result result = Result.NotFound;
            if (this.cursor.scopeType is LayoutUDT)
            {
                result = this.WriteSchematizedValue(path, value);
            }

            if (result == Result.NotFound)
            {
                // Write sparse value.
                result = this.PrepareSparseWrite(path, type.TypeArg);
                if (result != Result.Success)
                {
                    return result;
                }

                sparse(ref this, value);
                this.cursor.MoveNext(ref this.row);
            }

            return result;
        }

        /// <summary>Helper for writing a primitive value.</summary>
        /// <typeparam name="TLayoutType">The type of layout type.</typeparam>
        /// <typeparam name="TElement">The sub-element type of the field.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <param name="type">The layout type.</param>
        /// <param name="sparse">The <see cref="RowBuffer" /> access method for <paramref name="type" />.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WritePrimitive<TLayoutType, TElement>(
            UtfAnyString path,
            ReadOnlySequence<TElement> value,
            TLayoutType type,
            AccessMethod<ReadOnlySequence<TElement>> sparse)
            where TLayoutType : LayoutType<TElement[]>, ILayoutSequenceWritable<TElement>
        {
            Result result = Result.NotFound;
            if (this.cursor.scopeType is LayoutUDT)
            {
                result = this.WriteSchematizedValue(path, value);
            }

            if (result == Result.NotFound)
            {
                // Write sparse value.
                result = this.PrepareSparseWrite(path, type.TypeArg);
                if (result != Result.Success)
                {
                    return result;
                }

                sparse(ref this, value);
                this.cursor.MoveNext(ref this.row);
            }

            return result;
        }

        /// <summary>Helper for preparing the write of a sparse field.</summary>
        /// <param name="path">The path identifying the field to write.</param>
        /// <param name="typeArg">The (optional) type constraints.</param>
        /// <returns>Success if the write is permitted, the error code otherwise.</returns>
        private Result PrepareSparseWrite(UtfAnyString path, TypeArgument typeArg)
        {
            if (this.cursor.scopeType.IsFixedArity && !(this.cursor.scopeType is LayoutNullable))
            {
                if ((this.cursor.index < this.cursor.scopeTypeArgs.Count) && !typeArg.Equals(this.cursor.scopeTypeArgs[this.cursor.index]))
                {
                    return Result.TypeConstraint;
                }
            }
            else if (this.cursor.scopeType is LayoutTypedMap)
            {
                if (!typeArg.Equals(this.cursor.scopeType.TypeAs<LayoutUniqueScope>().FieldType(ref this.cursor)))
                {
                    return Result.TypeConstraint;
                }
            }
            else if (this.cursor.scopeType.IsTypedScope && !typeArg.Equals(this.cursor.scopeTypeArgs[0]))
            {
                return Result.TypeConstraint;
            }

            this.cursor.writePath = path;
            return Result.Success;
        }

        /// <summary>Write a generic schematized field value via the scope's layout.</summary>
        /// <typeparam name="TValue">The expected type of the field.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WriteSchematizedValue<TValue>(UtfAnyString path, TValue value)
        {
            if (!this.cursor.layout.TryFind(path, out LayoutColumn col))
            {
                return Result.NotFound;
            }

            if (!(col.Type is LayoutType<TValue> t))
            {
                return Result.NotFound;
            }

            switch (col.Storage)
            {
                case StorageKind.Fixed:
                    return t.WriteFixed(ref this.row, ref this.cursor, col, value);

                case StorageKind.Variable:
                    return t.WriteVariable(ref this.row, ref this.cursor, col, value);

                default:
                    return Result.NotFound;
            }
        }

        /// <summary>Write a generic schematized field value via the scope's layout.</summary>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WriteSchematizedValue(UtfAnyString path, Utf8Span value)
        {
            if (!this.cursor.layout.TryFind(path, out LayoutColumn col))
            {
                return Result.NotFound;
            }

            LayoutType t = col.Type;
            if (!(t is ILayoutUtf8SpanWritable))
            {
                return Result.NotFound;
            }

            switch (col.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<ILayoutUtf8SpanWritable>().WriteFixed(ref this.row, ref this.cursor, col, value);
                case StorageKind.Variable:
                    return t.TypeAs<ILayoutUtf8SpanWritable>().WriteVariable(ref this.row, ref this.cursor, col, value);
                default:
                    return Result.NotFound;
            }
        }

        /// <summary>Write a generic schematized field value via the scope's layout.</summary>
        /// <typeparam name="TElement">The sub-element type of the field.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WriteSchematizedValue<TElement>(UtfAnyString path, ReadOnlySpan<TElement> value)
        {
            if (!this.cursor.layout.TryFind(path, out LayoutColumn col))
            {
                return Result.NotFound;
            }

            LayoutType t = col.Type;
            if (!(t is ILayoutSpanWritable<TElement>))
            {
                return Result.NotFound;
            }

            switch (col.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<ILayoutSpanWritable<TElement>>().WriteFixed(ref this.row, ref this.cursor, col, value);
                case StorageKind.Variable:
                    return t.TypeAs<ILayoutSpanWritable<TElement>>().WriteVariable(ref this.row, ref this.cursor, col, value);
                default:
                    return Result.NotFound;
            }
        }

        /// <summary>Write a generic schematized field value via the scope's layout.</summary>
        /// <typeparam name="TElement">The sub-element type of the field.</typeparam>
        /// <param name="path">The scope-relative path of the field to write.</param>
        /// <param name="value">The value to write.</param>
        /// <returns>Success if the write is successful, an error code otherwise.</returns>
        private Result WriteSchematizedValue<TElement>(UtfAnyString path, ReadOnlySequence<TElement> value)
        {
            if (!this.cursor.layout.TryFind(path, out LayoutColumn col))
            {
                return Result.NotFound;
            }

            LayoutType t = col.Type;
            if (!(t is ILayoutSequenceWritable<TElement>))
            {
                return Result.NotFound;
            }

            switch (col.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<ILayoutSequenceWritable<TElement>>().WriteFixed(ref this.row, ref this.cursor, col, value);
                case StorageKind.Variable:
                    return t.TypeAs<ILayoutSequenceWritable<TElement>>().WriteVariable(ref this.row, ref this.cursor, col, value);
                default:
                    return Result.NotFound;
            }
        }
    }
}
