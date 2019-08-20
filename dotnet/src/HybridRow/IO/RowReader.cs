// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable CA1034 // Nested types should not be visible

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.IO
{
    using System;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Schemas;

    /// <summary>A forward-only, streaming, field reader for <see cref="RowBuffer" />.</summary>
    /// <remarks>
    /// A <see cref="RowReader" /> allows the traversal in a streaming, left to right fashion, of
    /// an entire HybridRow. The row's layout provides decoding for any schematized portion of the row.
    /// However, unschematized sparse fields are read directly from the sparse segment with or without
    /// schematization allowing all fields within the row, both known and unknown, to be read.
    /// <para />
    /// Modifying a <see cref="RowBuffer" /> invalidates any reader or child reader associated with it.  In
    /// general <see cref="RowBuffer" />'s should not be mutated while being enumerated.
    /// </remarks>
    public ref struct RowReader
    {
        private readonly int schematizedCount;
        private readonly ReadOnlySpan<LayoutColumn> columns;
        private RowBuffer row;

        // State that can be checkpointed.
        private States state;
        private int columnIndex;
        private RowCursor cursor;

        /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
        /// <param name="row">The row to be read.</param>
        /// <param name="scope">The scope whose fields should be enumerated.</param>
        /// <remarks>
        /// A <see cref="RowReader" /> instance traverses all of the top-level fields of a given
        /// scope.  If the root scope is provided then all top-level fields in the row are enumerated.  Nested
        /// child <see cref="RowReader" /> instances can be access through the <see cref="ReadScope" /> method
        /// to process nested content.
        /// </remarks>
        private RowReader(ref RowBuffer row, in RowCursor scope)
        {
            this.cursor = scope;
            this.row = row;
            this.columns = this.cursor.layout.Columns;
            this.schematizedCount = this.cursor.layout.NumFixed + this.cursor.layout.NumVariable;

            this.state = States.None;
            this.columnIndex = -1;
        }

        /// <summary>Initializes a new instance of the <see cref="RowReader" /> struct.</summary>
        /// <param name="row">The row to be read.</param>
        /// <param name="scope">The scope whose fields should be enumerated.</param>
        /// <remarks>
        /// A <see cref="RowReader" /> instance traverses all of the top-level fields of a given
        /// scope.  If the root scope is provided then all top-level fields in the row are enumerated.  Nested
        /// child <see cref="RowReader" /> instances can be access through the <see cref="ReadScope" /> method
        /// to process nested content.
        /// </remarks>
        public RowReader(ref RowBuffer row)
            : this(ref row, RowCursor.Create(ref row))
        {
        }

        public RowReader(ref RowBuffer row, in Checkpoint checkpoint)
        {
            this.row = row;
            this.columns = checkpoint.Cursor.layout.Columns;
            this.schematizedCount = checkpoint.Cursor.layout.NumFixed + checkpoint.Cursor.layout.NumVariable;

            this.state = checkpoint.State;
            this.cursor = checkpoint.Cursor;
            this.columnIndex = checkpoint.ColumnIndex;
        }

        /// <summary>The current traversal state of the reader.</summary>
        internal enum States : byte
        {
            /// <summary>The reader has not be started yet.</summary>
            None,

            /// <summary>Enumerating schematized fields (fixed and variable) from left to right.</summary>
            Schematized,

            /// <summary>Enumerating top-level fields of the current scope.</summary>
            Sparse,

            /// <summary>The reader has completed the scope.</summary>
            Done,
        }

        /// <summary>The length of row in bytes.</summary>
        public int Length => this.row.Length;

        /// <summary>The storage placement of the field (if positioned on a field, undefined otherwise).</summary>
        public StorageKind Storage
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return this.columns[this.columnIndex].Storage;
                    case States.Sparse:
                        return StorageKind.Sparse;
                    default:
                        return default;
                }
            }
        }

        /// <summary>The type of the field  (if positioned on a field, undefined otherwise).</summary>
        public LayoutType Type
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return this.columns[this.columnIndex].Type;
                    case States.Sparse:
                        return this.cursor.cellType;
                    default:
                        return default;
                }
            }
        }

        /// <summary>The type arguments of the field  (if positioned on a field, undefined otherwise).</summary>
        public TypeArgumentList TypeArgs
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return this.columns[this.columnIndex].TypeArgs;
                    case States.Sparse:
                        return this.cursor.cellTypeArgs;
                    default:
                        return TypeArgumentList.Empty;
                }
            }
        }

        /// <summary>True if field has a value (if positioned on a field, undefined otherwise).</summary>
        /// <remarks>
        /// If the current field is a Nullable scope, this method return true if the value is not
        /// null. If the current field is a nullable Null primitive value, this method return true if the value
        /// is set (even though its values is set to null).
        /// </remarks>
        public bool HasValue
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return true;
                    case States.Sparse:
                        if (this.cursor.cellType is LayoutNullable)
                        {
                            RowCursor nullableScope = this.row.SparseIteratorReadScope(ref this.cursor, immutable: true);
                            return LayoutNullable.HasValue(ref this.row, ref nullableScope) == Result.Success;
                        }

                        return true;
                    default:
                        return false;
                }
            }
        }

        /// <summary>
        /// The path, relative to the scope, of the field (if positioned on a field, undefined
        /// otherwise).
        /// </summary>
        /// <remarks>When enumerating an indexed scope, this value is always null (see <see cref="Index" />).</remarks>
        public UtfAnyString Path
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return this.columns[this.columnIndex].Path;
                    case States.Sparse:
                        if (this.cursor.pathOffset == default)
                        {
                            return default;
                        }

                        Utf8Span span = this.row.ReadSparsePath(ref this.cursor);
                        return Utf8String.CopyFrom(span);
                    default:
                        return default;
                }
            }
        }

        /// <summary>
        /// The path, relative to the scope, of the field (if positioned on a field, undefined
        /// otherwise).
        /// </summary>
        /// <remarks>When enumerating an indexed scope, this value is always null (see <see cref="Index" />).</remarks>
        public Utf8Span PathSpan
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return this.columns[this.columnIndex].Path.Span;
                    case States.Sparse:
                        return this.row.ReadSparsePath(ref this.cursor);
                    default:
                        return default;
                }
            }
        }

        /// <summary>
        /// The 0-based index, relative to the start of the scope, of the field (if positioned on a
        /// field, undefined otherwise).
        /// </summary>
        /// <remarks>When enumerating a non-indexed scope, this value is always 0 (see <see cref="Path" />).</remarks>
        public int Index
        {
            get
            {
                switch (this.state)
                {
                    case States.Schematized:
                        return 0;
                    case States.Sparse:
                        return this.cursor.index;
                    default:
                        return default;
                }
            }
        }

        public Checkpoint SaveCheckpoint()
        {
            return new Checkpoint(this.state, this.columnIndex, this.cursor);
        }

        /// <summary>Advances the reader to the next field.</summary>
        /// <returns>True, if there is another field to be read, false otherwise.</returns>
        public bool Read()
        {
            switch (this.state)
            {
                case States.None:
                {
                    if (this.cursor.scopeType is LayoutUDT)
                    {
                        this.state = States.Schematized;
                        goto case States.Schematized;
                    }

                    this.state = States.Sparse;
                    goto case States.Sparse;
                }

                case States.Schematized:
                {
                    this.columnIndex++;
                    if (this.columnIndex >= this.schematizedCount)
                    {
                        this.state = States.Sparse;
                        goto case States.Sparse;
                    }

                    Contract.Assert(this.cursor.scopeType is LayoutUDT);
                    LayoutColumn col = this.columns[this.columnIndex];
                    if (!this.row.ReadBit(this.cursor.start, col.NullBit))
                    {
                        // Skip schematized values if they aren't present.
                        goto case States.Schematized;
                    }

                    return true;
                }

                case States.Sparse:
                {
                    if (!this.cursor.MoveNext(ref this.row))
                    {
                        this.state = States.Done;
                        goto case States.Done;
                    }

                    return true;
                }

                case States.Done:
                {
                    return false;
                }
            }

            return false;
        }

        /// <summary>Read the current field as a <see cref="bool" />.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadBool(out bool value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutBoolean))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseBool(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a null.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadNull(out NullValue value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutNull))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseNull(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 8-bit, signed integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadInt8(out sbyte value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutInt8))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseInt8(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 16-bit, signed integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadInt16(out short value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutInt16))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseInt16(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 32-bit, signed integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadInt32(out int value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutInt32))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseInt32(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 64-bit, signed integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadInt64(out long value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutInt64))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseInt64(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 8-bit, unsigned integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadUInt8(out byte value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUInt8))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseUInt8(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 16-bit, unsigned integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadUInt16(out ushort value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUInt16))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseUInt16(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 32-bit, unsigned integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadUInt32(out uint value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUInt32))
                    {
                        value = default(int);
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseUInt32(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 64-bit, unsigned integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadUInt64(out ulong value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUInt64))
                    {
                        value = default(long);
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseUInt64(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a variable length, 7-bit encoded, signed integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadVarInt(out long value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutVarInt))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseVarInt(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a variable length, 7-bit encoded, unsigned integer.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadVarUInt(out ulong value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutVarUInt))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseVarUInt(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 32-bit, IEEE-encoded floating point value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadFloat32(out float value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutFloat32))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseFloat32(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 64-bit, IEEE-encoded floating point value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadFloat64(out double value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutFloat64))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseFloat64(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length, 128-bit, IEEE-encoded floating point value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadFloat128(out Float128 value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutFloat128))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseFloat128(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length <see cref="decimal" /> value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadDecimal(out decimal value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutDecimal))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseDecimal(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length <see cref="DateTime" /> value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadDateTime(out DateTime value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutDateTime))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseDateTime(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length <see cref="UnixDateTime" /> value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadUnixDateTime(out UnixDateTime value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUnixDateTime))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseUnixDateTime(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length <see cref="Guid" /> value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadGuid(out Guid value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutGuid))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseGuid(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a fixed length <see cref="MongoDbObjectId" /> value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadMongoDbObjectId(out MongoDbObjectId value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutMongoDbObjectId))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseMongoDbObjectId(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a variable length, UTF8 encoded, string value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadString(out string value)
        {
            Result r = this.ReadString(out Utf8Span span);
            value = (r == Result.Success) ? span.ToString() : default;
            return r;
        }

        /// <summary>Read the current field as a variable length, UTF8 encoded, string value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadString(out Utf8String value)
        {
            Result r = this.ReadString(out Utf8Span span);
            value = (r == Result.Success) ? Utf8String.CopyFrom(span) : default;
            return r;
        }

        /// <summary>Read the current field as a variable length, UTF8 encoded, string value.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadString(out Utf8Span value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutUtf8))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseString(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a variable length, sequence of bytes.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadBinary(out byte[] value)
        {
            Result r = this.ReadBinary(out ReadOnlySpan<byte> span);
            value = (r == Result.Success) ? span.ToArray() : default;
            return r;
        }

        /// <summary>Read the current field as a variable length, sequence of bytes.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        public Result ReadBinary(out ReadOnlySpan<byte> value)
        {
            switch (this.state)
            {
                case States.Schematized:
                    return this.ReadPrimitiveValue(out value);
                case States.Sparse:
                    if (!(this.cursor.cellType is LayoutBinary))
                    {
                        value = default;
                        return Result.TypeMismatch;
                    }

                    value = this.row.ReadSparseBinary(ref this.cursor);
                    return Result.Success;
                default:
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
        /// <remarks>
        /// Child readers can be used to read all sparse scope types including typed and untyped
        /// objects, arrays, tuples, set, and maps.
        /// <para />
        /// Nested child readers are independent of their parent.
        /// </remarks>
        public RowReader ReadScope()
        {
            RowCursor newScope = this.row.SparseIteratorReadScope(ref this.cursor, immutable: true);
            return new RowReader(ref this.row, newScope);
        }

        /// <summary>A function to reader content from a <see cref="RowBuffer" />.</summary>
        /// <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
        /// <param name="reader">A forward-only cursor for writing content.</param>
        /// <param name="context">A context value provided by the caller.</param>
        /// <returns>The result.</returns>
        public delegate Result ReaderFunc<in TContext>(ref RowReader reader, TContext context);

        /// <summary>Read the current field as a nested, structured, sparse scope.</summary>
        /// <remarks>
        /// Child readers can be used to read all sparse scope types including typed and untyped
        /// objects, arrays, tuples, set, and maps.
        /// </remarks>
        public Result ReadScope<TContext>(TContext context, ReaderFunc<TContext> func)
        {
            RowCursor childScope = this.row.SparseIteratorReadScope(ref this.cursor, immutable: true);
            RowReader nestedReader = new RowReader(ref this.row, childScope);

            Result result = func?.Invoke(ref nestedReader, context) ?? Result.Success;
            if (result != Result.Success)
            {
                return result;
            }

            this.cursor.Skip(ref this.row, ref nestedReader.cursor);
            return Result.Success;
        }

        /// <summary>
        /// Advance a reader to the end of a child reader. The child reader is also advanced to the
        /// end of its scope.
        /// </summary>
        /// <remarks>
        /// The reader must not have been advanced since the child reader was created with ReadScope.
        /// This method can be used when the overload of <see cref="ReadScope" /> that takes a
        /// <see cref="ReaderFunc{TContext}" /> is not an option, such as when TContext is a ref struct.
        /// </remarks>
        public Result SkipScope(ref RowReader nestedReader)
        {
            if (nestedReader.cursor.start != this.cursor.valueOffset)
            {
                return Result.Failure;
            }

            this.cursor.Skip(ref this.row, ref nestedReader.cursor);
            return Result.Success;
        }

        /// <summary>Read a generic schematized field value via the scope's layout.</summary>
        /// <typeparam name="TValue">The expected type of the field.</typeparam>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        private Result ReadPrimitiveValue<TValue>(out TValue value)
        {
            LayoutColumn col = this.columns[this.columnIndex];
            LayoutType t = this.columns[this.columnIndex].Type;
            if (!(t is LayoutType<TValue>))
            {
                value = default;
                return Result.TypeMismatch;
            }

            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<LayoutType<TValue>>().ReadFixed(ref this.row, ref this.cursor, col, out value);
                case StorageKind.Variable:
                    return t.TypeAs<LayoutType<TValue>>().ReadVariable(ref this.row, ref this.cursor, col, out value);
                default:
                    Contract.Assert(false);
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read a generic schematized field value via the scope's layout.</summary>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        private Result ReadPrimitiveValue(out Utf8Span value)
        {
            LayoutColumn col = this.columns[this.columnIndex];
            LayoutType t = this.columns[this.columnIndex].Type;
            if (!(t is ILayoutUtf8SpanReadable))
            {
                value = default;
                return Result.TypeMismatch;
            }

            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<ILayoutUtf8SpanReadable>().ReadFixed(ref this.row, ref this.cursor, col, out value);
                case StorageKind.Variable:
                    return t.TypeAs<ILayoutUtf8SpanReadable>().ReadVariable(ref this.row, ref this.cursor, col, out value);
                default:
                    Contract.Assert(false);
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>Read a generic schematized field value via the scope's layout.</summary>
        /// <typeparam name="TElement">The sub-element type of the field.</typeparam>
        /// <param name="value">On success, receives the value, undefined otherwise.</param>
        /// <returns>Success if the read is successful, an error code otherwise.</returns>
        private Result ReadPrimitiveValue<TElement>(out ReadOnlySpan<TElement> value)
        {
            LayoutColumn col = this.columns[this.columnIndex];
            LayoutType t = this.columns[this.columnIndex].Type;
            if (!(t is ILayoutSpanReadable<TElement>))
            {
                value = default;
                return Result.TypeMismatch;
            }

            switch (col?.Storage)
            {
                case StorageKind.Fixed:
                    return t.TypeAs<ILayoutSpanReadable<TElement>>().ReadFixed(ref this.row, ref this.cursor, col, out value);
                case StorageKind.Variable:
                    return t.TypeAs<ILayoutSpanReadable<TElement>>().ReadVariable(ref this.row, ref this.cursor, col, out value);
                default:
                    Contract.Assert(false);
                    value = default;
                    return Result.Failure;
            }
        }

        /// <summary>
        /// An encapsulation of the current state of a <see cref="RowReader" /> that can be used to
        /// recreate the <see cref="RowReader" /> in the same logical position.
        /// </summary>
        public readonly struct Checkpoint
        {
            internal readonly States State;
            internal readonly int ColumnIndex;
            internal readonly RowCursor Cursor;

            internal Checkpoint(States state, int columnIndex, RowCursor cursor)
            {
                this.State = state;
                this.ColumnIndex = columnIndex;
                this.Cursor = cursor;
            }
        }
    }
}
