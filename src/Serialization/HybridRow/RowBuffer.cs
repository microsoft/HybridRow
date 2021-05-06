// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow
{
    using System;
    using System.Buffers;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Runtime.CompilerServices;
    using System.Runtime.InteropServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;
    using Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts;

    public ref struct RowBuffer
    {
        /// <summary>Resizer for growing the memory buffer.</summary>
        private readonly ISpanResizer<byte> resizer;

        /// <summary>A sequence of bytes managed by this <see cref="RowBuffer" />.</summary>
        /// <remarks>
        /// A Hybrid Row begins in the 0-th byte of the <see cref="RowBuffer" />.  Remaining byte
        /// sequence is defined by the Hybrid Row grammar.
        /// </remarks>
        private Span<byte> buffer;

        /// <summary>The resolver for UDTs.</summary>
        private LayoutResolver resolver;

        /// <summary>The length of row in bytes.</summary>
        private int length;

        /// <summary>Initializes a new instance of the <see cref="RowBuffer" /> struct.</summary>
        /// <param name="capacity">Initial buffer capacity.</param>
        /// <param name="resizer">Optional memory resizer.</param>
        public RowBuffer(int capacity, ISpanResizer<byte> resizer = default)
        {
            this.resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            this.buffer = this.resizer.Resize(capacity);
            this.length = 0;
            this.resolver = null;
        }

        /// <summary>Initializes a new instance of the <see cref="RowBuffer" /> struct from an existing buffer.</summary>
        /// <param name="buffer">The buffer.</param>
        /// <param name="version">The version of the Hybrid Row format to used to encoding the buffer.</param>
        /// <param name="resolver">The resolver for UDTs.</param>
        /// <param name="resizer">Optional memory resizer.</param>
        public RowBuffer(Span<byte> buffer, HybridRowVersion version, LayoutResolver resolver, ISpanResizer<byte> resizer = default)
        {
            Contract.Requires(buffer.Length >= HybridRowHeader.Size);
            this.resizer = resizer ?? DefaultSpanResizer<byte>.Default;
            this.length = buffer.Length;
            this.buffer = buffer;
            this.resolver = resolver;

            HybridRowHeader header = this.ReadHeader(0);
            Contract.Invariant(header.Version == version);
            Layout layout = resolver.Resolve(header.SchemaId);
            Contract.Assert(header.SchemaId == layout.SchemaId);
            Contract.Invariant(HybridRowHeader.Size + layout.Size <= this.length);
        }

        /// <summary>The root header for the row.</summary>
        public HybridRowHeader Header => this.ReadHeader(0);

        /// <summary>The length of row in bytes.</summary>
        public int Length => this.length;

        /// <summary>The full encoded content of the row.</summary>
        public byte[] ToArray()
        {
            return this.AsSpan().ToArray();
        }

        /// <summary>The full encoded content of the row.</summary>
        public ReadOnlySpan<byte> AsSpan()
        {
            return this.buffer.Slice(0, this.length);
        }

        /// <summary>The resolver for UDTs.</summary>
        public LayoutResolver Resolver => this.resolver;

        /// <summary>Clears all content from the row. The row is empty after this method.</summary>
        public void Reset()
        {
            this.length = 0;
            this.resolver = null;
        }

        /// <summary>Copies the content of the buffer into the target stream.</summary>
        public void WriteTo(Stream stream)
        {
            stream.Write(this.buffer.Slice(0, this.length));
        }

        /// <summary>
        /// Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
        /// with the associated layout and rowVersion.
        /// </summary>
        /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
        public bool ReadFrom(
            Stream inputStream,
            int bytesCount,
            HybridRowVersion rowVersion,
            LayoutResolver resolver)
        {
            Contract.Requires(inputStream != null);
            Contract.Assert(bytesCount >= HybridRowHeader.Size);

            this.Reset();
            this.resolver = resolver;
            this.Ensure(bytesCount);
            Contract.Assert(this.buffer.Length >= bytesCount);
            this.length = bytesCount;
            Span<byte> active = this.buffer.Slice(0, bytesCount);
            int bytesRead;
            do
            {
                bytesRead = inputStream.Read(active);
                active = active.Slice(bytesRead);
            }
            while (bytesRead != 0);

            if (active.Length != 0)
            {
                return false;
            }

            return this.InitReadFrom(rowVersion);
        }

        /// <summary>
        /// Reads in the contents of the RowBuffer from an existing block of memory and initializes
        /// the row buffer with the associated layout and rowVersion.
        /// </summary>
        /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
        public bool ReadFrom(ReadOnlySpan<byte> input, HybridRowVersion rowVersion, LayoutResolver resolver)
        {
            int bytesCount = input.Length;
            Contract.Assert(bytesCount >= HybridRowHeader.Size);

            this.Reset();
            this.resolver = resolver;
            this.Ensure(bytesCount);
            Contract.Assert(this.buffer.Length >= bytesCount);
            input.CopyTo(this.buffer);
            this.length = bytesCount;
            return this.InitReadFrom(rowVersion);
        }

        /// <summary>Initializes a row to the minimal size for the given layout.</summary>
        /// <param name="version">The version of the Hybrid Row format to use for encoding this row.</param>
        /// <param name="layout">The layout that describes the column layout of the row.</param>
        /// <param name="resolver">The resolver for UDTs.</param>
        /// <remarks>
        /// The row is initialized to default row for the given layout.  All fixed columns have their
        /// default values.  All variable columns are null.  No sparse columns are present. The row is valid.
        /// </remarks>
        public void InitLayout(HybridRowVersion version, Layout layout, LayoutResolver resolver)
        {
            Contract.Requires(layout != null);
            this.resolver = resolver;

            // Ensure sufficient space for fixed schema fields.
            this.Ensure(HybridRowHeader.Size + layout.Size);
            this.length = HybridRowHeader.Size + layout.Size;

            // Clear all presence bits.
            this.buffer.Slice(HybridRowHeader.Size, layout.Size).Fill(0);

            // Set the header.
            this.WriteHeader(0, new HybridRowHeader(version, layout.SchemaId));
        }

        internal void WriteHeader(int offset, HybridRowHeader value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal HybridRowHeader ReadHeader(int offset)
        {
            return MemoryMarshal.Read<HybridRowHeader>(this.buffer.Slice(offset));
        }

        internal void WriteSchemaId(int offset, SchemaId value)
        {
            this.WriteInt32(offset, value.Id);
        }

        internal SchemaId ReadSchemaId(int offset)
        {
            return new SchemaId(this.ReadInt32(offset));
        }

        internal void SetBit(int offset, LayoutBit bit)
        {
            // If the bit to be read is itself undefined, then return true.  This is used to
            // short-circuit the non-nullable vs. nullable field cases.  For nullable fields
            // the bit indicates the presence bit to be read for the field.  For non-nullable
            // fields there is no presence bit, and so "undefined" is passed and true is always 
            // returned indicating the field *is* present (as non-nullable fields are ALWAYS
            // present).
            if (bit.IsInvalid)
            {
                return;
            }

            this.buffer[bit.GetOffset(offset)] |= unchecked((byte)(1 << bit.GetBit()));
        }

        internal void UnsetBit(int offset, LayoutBit bit)
        {
            Contract.Assert(bit != LayoutBit.Invalid);
            this.buffer[bit.GetOffset(offset)] &= unchecked((byte)~(1 << bit.GetBit()));
        }

        internal bool ReadBit(int offset, LayoutBit bit)
        {
            // If the bit to be read is itself undefined, then return true.  This is used to
            // short-circuit the non-nullable vs. nullable field cases.  For nullable fields
            // the bit indicates the presence bit to be read for the field.  For non-nullable
            // fields there is no presence bit, and so "undefined" is passed and true is always 
            // returned indicating the field *is* present (as non-nullable fields are ALWAYS
            // present).
            if (bit.IsInvalid)
            {
                return true;
            }

            return (this.buffer[bit.GetOffset(offset)] & unchecked((byte)(1 << bit.GetBit()))) != 0;
        }

        internal void DeleteVariable(int offset, bool isVarint)
        {
            ulong existingValueBytes = this.Read7BitEncodedUInt(offset, out int spaceAvailable);
            if (!isVarint)
            {
                spaceAvailable += (int)existingValueBytes; // "size" already in spaceAvailable
            }

            this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset));
            this.length -= spaceAvailable;
        }

        internal void WriteInt8(int offset, sbyte value)
        {
            this.buffer[offset] = unchecked((byte)value);
        }

        internal sbyte ReadInt8(int offset)
        {
            return unchecked((sbyte)this.buffer[offset]);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal void WriteUInt8(int offset, byte value)
        {
            this.buffer[offset] = value;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal byte ReadUInt8(int offset)
        {
            return this.buffer[offset];
        }

        internal void WriteInt16(int offset, short value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal short ReadInt16(int offset)
        {
            return MemoryMarshal.Read<short>(this.buffer.Slice(offset));
        }

        internal void WriteUInt16(int offset, ushort value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal ushort ReadUInt16(int offset)
        {
            return MemoryMarshal.Read<ushort>(this.buffer.Slice(offset));
        }

        internal void WriteInt32(int offset, int value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal int ReadInt32(int offset)
        {
            return MemoryMarshal.Read<int>(this.buffer.Slice(offset));
        }

        internal void IncrementUInt32(int offset, uint increment)
        {
            MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] += increment;
        }

        internal void DecrementUInt32(int offset, uint decrement)
        {
            MemoryMarshal.Cast<byte, uint>(this.buffer.Slice(offset))[0] -= decrement;
        }

        internal void WriteUInt32(int offset, uint value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal uint ReadUInt32(int offset)
        {
            return MemoryMarshal.Read<uint>(this.buffer.Slice(offset));
        }

        internal void WriteInt64(int offset, long value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal long ReadInt64(int offset)
        {
            return MemoryMarshal.Read<long>(this.buffer.Slice(offset));
        }

        internal void WriteUInt64(int offset, ulong value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal ulong ReadUInt64(int offset)
        {
            return MemoryMarshal.Read<ulong>(this.buffer.Slice(offset));
        }

        internal int Write7BitEncodedUInt(int offset, ulong value)
        {
            // Write out an unsigned long 7 bits at a time.  The high bit of the byte,
            // when set, indicates there are more bytes.
            int i = 0;
            while (value >= 0x80)
            {
                this.WriteUInt8(offset + i++, unchecked((byte)(value | 0x80)));
                value >>= 7;
            }

            this.WriteUInt8(offset + i++, (byte)value);
            return i;
        }

        internal ulong Read7BitEncodedUInt(int offset, out int lenInBytes)
        {
            // Read out an unsigned long 7 bits at a time.  The high bit of the byte,
            // when set, indicates there are more bytes.
            ulong b = this.buffer[offset];
            if (b < 0x80)
            {
                lenInBytes = 1;
                return b;
            }

            ulong retval = b & 0x7F;
            int shift = 7;
            do
            {
                Contract.Assert(shift < 10 * 7);
                b = this.buffer[++offset];
                retval |= (b & 0x7F) << shift;
                shift += 7;
            }
            while (b >= 0x80);

            lenInBytes = shift / 7;
            return retval;
        }

        internal int Write7BitEncodedInt(int offset, long value)
        {
            return this.Write7BitEncodedUInt(offset, RowBuffer.RotateSignToLsb(value));
        }

        internal long Read7BitEncodedInt(int offset, out int lenInBytes)
        {
            return RowBuffer.RotateSignToMsb(this.Read7BitEncodedUInt(offset, out lenInBytes));
        }

        internal void WriteFloat32(int offset, float value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal float ReadFloat32(int offset)
        {
            return MemoryMarshal.Read<float>(this.buffer.Slice(offset));
        }

        internal void WriteFloat64(int offset, double value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal double ReadFloat64(int offset)
        {
            return MemoryMarshal.Read<double>(this.buffer.Slice(offset));
        }

        internal void WriteFloat128(int offset, Float128 value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal Float128 ReadFloat128(int offset)
        {
            return MemoryMarshal.Read<Float128>(this.buffer.Slice(offset));
        }

        internal void WriteDecimal(int offset, decimal value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal decimal ReadDecimal(int offset)
        {
            return MemoryMarshal.Read<decimal>(this.buffer.Slice(offset));
        }

        internal void WriteDateTime(int offset, DateTime value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal DateTime ReadDateTime(int offset)
        {
            return MemoryMarshal.Read<DateTime>(this.buffer.Slice(offset));
        }

        internal void WriteUnixDateTime(int offset, UnixDateTime value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal UnixDateTime ReadUnixDateTime(int offset)
        {
            return MemoryMarshal.Read<UnixDateTime>(this.buffer.Slice(offset));
        }

        internal void WriteGuid(int offset, Guid value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal Guid ReadGuid(int offset)
        {
            return MemoryMarshal.Read<Guid>(this.buffer.Slice(offset));
        }

        internal void WriteMongoDbObjectId(int offset, MongoDbObjectId value)
        {
            MemoryMarshal.Write(this.buffer.Slice(offset), ref value);
        }

        internal MongoDbObjectId ReadMongoDbObjectId(int offset)
        {
            return MemoryMarshal.Read<MongoDbObjectId>(this.buffer.Slice(offset));
        }

        internal Utf8Span ReadFixedString(int offset, int len)
        {
            return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(offset, len));
        }

        internal void WriteFixedString(int offset, Utf8Span value)
        {
            value.Span.CopyTo(this.buffer.Slice(offset));
        }

        internal ReadOnlySpan<byte> ReadFixedBinary(int offset, int len)
        {
            return this.buffer.Slice(offset, len);
        }

        internal void WriteFixedBinary(int offset, ReadOnlySpan<byte> value, int len)
        {
            value.CopyTo(this.buffer.Slice(offset, len));
            if (value.Length < len)
            {
                this.buffer.Slice(offset + value.Length, len - value.Length).Fill(0);
            }
        }

        internal void WriteFixedBinary(int offset, ReadOnlySequence<byte> value, int len)
        {
            value.CopyTo(this.buffer.Slice(offset, len));
            if (value.Length < len)
            {
                this.buffer.Slice(offset + (int)value.Length, len - (int)value.Length).Fill(0);
            }
        }

        internal Utf8Span ReadVariableString(int offset)
        {
            return this.ReadString(offset, out int _);
        }

        internal void WriteVariableString(int offset, Utf8Span value, bool exists, out int shift)
        {
            int numBytes = value.Length;
            this.EnsureVariable(offset, false, numBytes, exists, out int spaceNeeded, out shift);

            int sizeLenInBytes = this.WriteString(offset, value);
            Contract.Assert(spaceNeeded == numBytes + sizeLenInBytes);
            this.length += shift;
        }

        internal ReadOnlySpan<byte> ReadVariableBinary(int offset)
        {
            return this.ReadBinary(offset, out int _);
        }

        internal void WriteVariableBinary(int offset, ReadOnlySpan<byte> value, bool exists, out int shift)
        {
            int numBytes = value.Length;
            this.EnsureVariable(offset, false, numBytes, exists, out int spaceNeeded, out shift);

            int sizeLenInBytes = this.WriteBinary(offset, value);
            Contract.Assert(spaceNeeded == numBytes + sizeLenInBytes);
            this.length += shift;
        }

        internal void WriteVariableBinary(int offset, ReadOnlySequence<byte> value, bool exists, out int shift)
        {
            int numBytes = (int)value.Length;
            this.EnsureVariable(offset, false, numBytes, exists, out int spaceNeeded, out shift);

            int sizeLenInBytes = this.WriteBinary(offset, value);
            Contract.Assert(spaceNeeded == numBytes + sizeLenInBytes);
            this.length += shift;
        }

        internal long ReadVariableInt(int offset)
        {
            return this.Read7BitEncodedInt(offset, out int _);
        }

        internal void WriteVariableInt(int offset, long value, bool exists, out int shift)
        {
            int numBytes = RowBuffer.Count7BitEncodedInt(value);
            this.EnsureVariable(offset, true, numBytes, exists, out int spaceNeeded, out shift);

            int sizeLenInBytes = this.Write7BitEncodedInt(offset, value);
            Contract.Assert(sizeLenInBytes == numBytes);
            Contract.Assert(spaceNeeded == numBytes);
            this.length += shift;
        }

        internal ulong ReadVariableUInt(int offset)
        {
            return this.Read7BitEncodedUInt(offset, out int _);
        }

        internal void WriteVariableUInt(int offset, ulong value, bool exists, out int shift)
        {
            int numBytes = RowBuffer.Count7BitEncodedUInt(value);
            this.EnsureVariable(offset, true, numBytes, exists, out int spaceNeeded, out shift);

            int sizeLenInBytes = this.Write7BitEncodedUInt(offset, value);
            Contract.Assert(sizeLenInBytes == numBytes);
            Contract.Assert(spaceNeeded == numBytes);
            this.length += shift;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal LayoutType ReadSparseTypeCode(int offset)
        {
            return LayoutType.FromCode((LayoutCode)this.ReadUInt8(offset));
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal void WriteSparseTypeCode(int offset, LayoutCode code)
        {
            this.WriteUInt8(offset, (byte)code);
        }

        internal sbyte ReadSparseInt8(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Int8);
            edit.endOffset = edit.valueOffset + sizeof(sbyte);
            return this.ReadInt8(edit.valueOffset);
        }

        internal void WriteSparseInt8(ref RowCursor edit, sbyte value, UpdateOptions options)
        {
            int numBytes = sizeof(sbyte);
            this.EnsureSparse(
                ref edit,
                LayoutType.Int8,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);

            this.WriteSparseMetadata(ref edit, LayoutType.Int8, TypeArgumentList.Empty, metaBytes);
            this.WriteInt8(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(sbyte));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal short ReadSparseInt16(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Int16);
            edit.endOffset = edit.valueOffset + sizeof(short);
            return this.ReadInt16(edit.valueOffset);
        }

        internal void WriteSparseInt16(ref RowCursor edit, short value, UpdateOptions options)
        {
            int numBytes = sizeof(short);
            this.EnsureSparse(
                ref edit,
                LayoutType.Int16,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Int16, TypeArgumentList.Empty, metaBytes);
            this.WriteInt16(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(short));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal int ReadSparseInt32(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Int32);
            edit.endOffset = edit.valueOffset + sizeof(int);
            return this.ReadInt32(edit.valueOffset);
        }

        internal void WriteSparseInt32(ref RowCursor edit, int value, UpdateOptions options)
        {
            int numBytes = sizeof(int);
            this.EnsureSparse(
                ref edit,
                LayoutType.Int32,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Int32, TypeArgumentList.Empty, metaBytes);
            this.WriteInt32(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(int));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal long ReadSparseInt64(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Int64);
            edit.endOffset = edit.valueOffset + sizeof(long);
            return this.ReadInt64(edit.valueOffset);
        }

        internal void WriteSparseInt64(ref RowCursor edit, long value, UpdateOptions options)
        {
            int numBytes = sizeof(long);
            this.EnsureSparse(
                ref edit,
                LayoutType.Int64,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Int64, TypeArgumentList.Empty, metaBytes);
            this.WriteInt64(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(long));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal byte ReadSparseUInt8(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.UInt8);
            edit.endOffset = edit.valueOffset + sizeof(byte);
            return this.ReadUInt8(edit.valueOffset);
        }

        internal void WriteSparseUInt8(ref RowCursor edit, byte value, UpdateOptions options)
        {
            int numBytes = sizeof(byte);
            this.EnsureSparse(
                ref edit,
                LayoutType.UInt8,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.UInt8, TypeArgumentList.Empty, metaBytes);
            this.WriteUInt8(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(byte));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal ushort ReadSparseUInt16(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.UInt16);
            edit.endOffset = edit.valueOffset + sizeof(ushort);
            return this.ReadUInt16(edit.valueOffset);
        }

        internal void WriteSparseUInt16(ref RowCursor edit, ushort value, UpdateOptions options)
        {
            int numBytes = sizeof(ushort);
            this.EnsureSparse(
                ref edit,
                LayoutType.UInt16,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.UInt16, TypeArgumentList.Empty, metaBytes);
            this.WriteUInt16(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(ushort));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal uint ReadSparseUInt32(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.UInt32);
            edit.endOffset = edit.valueOffset + sizeof(uint);
            return this.ReadUInt32(edit.valueOffset);
        }

        internal void WriteSparseUInt32(ref RowCursor edit, uint value, UpdateOptions options)
        {
            int numBytes = sizeof(uint);
            this.EnsureSparse(
                ref edit,
                LayoutType.UInt32,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.UInt32, TypeArgumentList.Empty, metaBytes);
            this.WriteUInt32(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(uint));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal ulong ReadSparseUInt64(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.UInt64);
            edit.endOffset = edit.valueOffset + sizeof(ulong);
            return this.ReadUInt64(edit.valueOffset);
        }

        internal void WriteSparseUInt64(ref RowCursor edit, ulong value, UpdateOptions options)
        {
            int numBytes = sizeof(ulong);
            this.EnsureSparse(
                ref edit,
                LayoutType.UInt64,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.UInt64, TypeArgumentList.Empty, metaBytes);
            this.WriteUInt64(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(ulong));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal long ReadSparseVarInt(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.VarInt);
            long value = this.Read7BitEncodedInt(edit.valueOffset, out int sizeLenInBytes);
            edit.endOffset = edit.valueOffset + sizeLenInBytes;
            return value;
        }

        internal void WriteSparseVarInt(ref RowCursor edit, long value, UpdateOptions options)
        {
            int numBytes = RowBuffer.Count7BitEncodedInt(value);
            this.EnsureSparse(
                ref edit,
                LayoutType.VarInt,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.VarInt, TypeArgumentList.Empty, metaBytes);
            int sizeLenInBytes = this.Write7BitEncodedInt(edit.valueOffset, value);
            Contract.Assert(sizeLenInBytes == numBytes);
            Contract.Assert(spaceNeeded == metaBytes + sizeLenInBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal ulong ReadSparseVarUInt(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.VarUInt);
            ulong value = this.Read7BitEncodedUInt(edit.valueOffset, out int sizeLenInBytes);
            edit.endOffset = edit.valueOffset + sizeLenInBytes;
            return value;
        }

        internal void WriteSparseVarUInt(ref RowCursor edit, ulong value, UpdateOptions options)
        {
            int numBytes = RowBuffer.Count7BitEncodedUInt(value);
            this.EnsureSparse(
                ref edit,
                LayoutType.VarUInt,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.VarUInt, TypeArgumentList.Empty, metaBytes);
            int sizeLenInBytes = this.Write7BitEncodedUInt(edit.valueOffset, value);
            Contract.Assert(sizeLenInBytes == numBytes);
            Contract.Assert(spaceNeeded == metaBytes + sizeLenInBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal float ReadSparseFloat32(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Float32);
            edit.endOffset = edit.valueOffset + sizeof(float);
            return this.ReadFloat32(edit.valueOffset);
        }

        internal void WriteSparseFloat32(ref RowCursor edit, float value, UpdateOptions options)
        {
            int numBytes = sizeof(float);
            this.EnsureSparse(
                ref edit,
                LayoutType.Float32,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Float32, TypeArgumentList.Empty, metaBytes);
            this.WriteFloat32(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(float));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal double ReadSparseFloat64(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Float64);
            edit.endOffset = edit.valueOffset + sizeof(double);
            return this.ReadFloat64(edit.valueOffset);
        }

        internal void WriteSparseFloat64(ref RowCursor edit, double value, UpdateOptions options)
        {
            int numBytes = sizeof(double);
            this.EnsureSparse(
                ref edit,
                LayoutType.Float64,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Float64, TypeArgumentList.Empty, metaBytes);
            this.WriteFloat64(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(double));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal Float128 ReadSparseFloat128(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Float128);
            edit.endOffset = edit.valueOffset + Float128.Size;
            return this.ReadFloat128(edit.valueOffset);
        }

        internal void WriteSparseFloat128(ref RowCursor edit, Float128 value, UpdateOptions options)
        {
            int numBytes = Float128.Size;
            this.EnsureSparse(
                ref edit,
                LayoutType.Float128,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Float128, TypeArgumentList.Empty, metaBytes);
            this.WriteFloat128(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + Float128.Size);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal decimal ReadSparseDecimal(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Decimal);
            edit.endOffset = edit.valueOffset + sizeof(decimal);
            return this.ReadDecimal(edit.valueOffset);
        }

        internal void WriteSparseDecimal(ref RowCursor edit, decimal value, UpdateOptions options)
        {
            int numBytes = sizeof(decimal);
            this.EnsureSparse(
                ref edit,
                LayoutType.Decimal,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Decimal, TypeArgumentList.Empty, metaBytes);
            this.WriteDecimal(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + sizeof(decimal));
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal DateTime ReadSparseDateTime(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.DateTime);
            edit.endOffset = edit.valueOffset + 8;
            return this.ReadDateTime(edit.valueOffset);
        }

        internal void WriteSparseDateTime(ref RowCursor edit, DateTime value, UpdateOptions options)
        {
            int numBytes = 8;
            this.EnsureSparse(
                ref edit,
                LayoutType.DateTime,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.DateTime, TypeArgumentList.Empty, metaBytes);
            this.WriteDateTime(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + 8);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal UnixDateTime ReadSparseUnixDateTime(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.UnixDateTime);
            edit.endOffset = edit.valueOffset + 8;
            return this.ReadUnixDateTime(edit.valueOffset);
        }

        internal void WriteSparseUnixDateTime(ref RowCursor edit, UnixDateTime value, UpdateOptions options)
        {
            int numBytes = 8;
            this.EnsureSparse(
                ref edit,
                LayoutType.UnixDateTime,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);

            this.WriteSparseMetadata(ref edit, LayoutType.UnixDateTime, TypeArgumentList.Empty, metaBytes);
            this.WriteUnixDateTime(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + 8);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal Guid ReadSparseGuid(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Guid);
            edit.endOffset = edit.valueOffset + 16;
            return this.ReadGuid(edit.valueOffset);
        }

        internal void WriteSparseGuid(ref RowCursor edit, Guid value, UpdateOptions options)
        {
            int numBytes = 16;
            this.EnsureSparse(
                ref edit,
                LayoutType.Guid,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Guid, TypeArgumentList.Empty, metaBytes);
            this.WriteGuid(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + 16);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal MongoDbObjectId ReadSparseMongoDbObjectId(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.MongoDbObjectId);
            edit.endOffset = edit.valueOffset + MongoDbObjectId.Size;
            return this.ReadMongoDbObjectId(edit.valueOffset);
        }

        internal void WriteSparseMongoDbObjectId(ref RowCursor edit, MongoDbObjectId value, UpdateOptions options)
        {
            int numBytes = MongoDbObjectId.Size;
            this.EnsureSparse(
                ref edit,
                LayoutType.MongoDbObjectId,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);

            this.WriteSparseMetadata(ref edit, LayoutType.MongoDbObjectId, TypeArgumentList.Empty, metaBytes);
            this.WriteMongoDbObjectId(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + MongoDbObjectId.Size);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal NullValue ReadSparseNull(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Null);
            edit.endOffset = edit.valueOffset;
            return NullValue.Default;
        }

        internal void WriteSparseNull(ref RowCursor edit, NullValue value, UpdateOptions options)
        {
            int numBytes = 0;
            this.EnsureSparse(
                ref edit,
                LayoutType.Null,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Null, TypeArgumentList.Empty, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal bool ReadSparseBool(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Boolean);
            edit.endOffset = edit.valueOffset;
            return edit.cellType == LayoutType.Boolean;
        }

        internal void WriteSparseBool(ref RowCursor edit, bool value, UpdateOptions options)
        {
            int numBytes = 0;
            this.EnsureSparse(
                ref edit,
                value ? LayoutType.Boolean : LayoutType.BooleanFalse,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, value ? LayoutType.Boolean : LayoutType.BooleanFalse, TypeArgumentList.Empty, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal Utf8Span ReadSparseString(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Utf8);
            Utf8Span span = this.ReadString(edit.valueOffset, out int sizeLenInBytes);
            edit.endOffset = edit.valueOffset + sizeLenInBytes + span.Length;
            return span;
        }

        internal void WriteSparseString(ref RowCursor edit, Utf8Span value, UpdateOptions options)
        {
            int len = value.Length;
            int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
            this.EnsureSparse(
                ref edit,
                LayoutType.Utf8,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Utf8, TypeArgumentList.Empty, metaBytes);
            int sizeLenInBytes = this.WriteString(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + len + sizeLenInBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal ReadOnlySpan<byte> ReadSparseBinary(ref RowCursor edit)
        {
            this.ValidateSparsePrimitiveTypeCode(ref edit, LayoutType.Binary);
            ReadOnlySpan<byte> span = this.ReadBinary(edit.valueOffset, out int sizeLenInBytes);
            edit.endOffset = edit.valueOffset + sizeLenInBytes + span.Length;
            return span;
        }

        internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySpan<byte> value, UpdateOptions options)
        {
            int len = value.Length;
            int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
            this.EnsureSparse(
                ref edit,
                LayoutType.Binary,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Binary, TypeArgumentList.Empty, metaBytes);
            int sizeLenInBytes = this.WriteBinary(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + len + sizeLenInBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal void WriteSparseBinary(ref RowCursor edit, ReadOnlySequence<byte> value, UpdateOptions options)
        {
            int len = (int)value.Length;
            int numBytes = len + RowBuffer.Count7BitEncodedUInt((ulong)len);
            this.EnsureSparse(
                ref edit,
                LayoutType.Binary,
                TypeArgumentList.Empty,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shift);
            this.WriteSparseMetadata(ref edit, LayoutType.Binary, TypeArgumentList.Empty, metaBytes);
            int sizeLenInBytes = this.WriteBinary(edit.valueOffset, value);
            Contract.Assert(spaceNeeded == metaBytes + len + sizeLenInBytes);
            edit.endOffset = edit.metaOffset + spaceNeeded;
            this.length += shift;
        }

        internal void WriteSparseObject(ref RowCursor edit, LayoutScope scopeType, UpdateOptions options, out RowCursor newScope)
        {
            int numBytes = sizeof(LayoutCode); // end scope type code.
            TypeArgumentList typeArgs = TypeArgumentList.Empty;
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, TypeArgumentList.Empty, metaBytes);
            this.WriteSparseTypeCode(edit.valueOffset, LayoutCode.EndScope);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = TypeArgumentList.Empty,
                start = edit.valueOffset,
                valueOffset = edit.valueOffset,
                metaOffset = edit.valueOffset,
                layout = edit.layout,
            };

            this.length += shift;
        }

        internal void WriteSparseArray(ref RowCursor edit, LayoutScope scopeType, UpdateOptions options, out RowCursor newScope)
        {
            int numBytes = sizeof(LayoutCode); // end scope type code.
            TypeArgumentList typeArgs = TypeArgumentList.Empty;
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            this.WriteSparseTypeCode(edit.valueOffset, LayoutCode.EndScope);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset,
                valueOffset = edit.valueOffset,
                metaOffset = edit.valueOffset,
                layout = edit.layout,
            };

            this.length += shift;
        }

        internal void WriteTypedArray(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            out RowCursor newScope)
        {
            int numBytes = sizeof(uint);
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            this.WriteUInt32(edit.valueOffset, 0);
            int valueOffset = edit.valueOffset + sizeof(uint); // Point after the Size
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset, // Point at the Size
                valueOffset = valueOffset,
                metaOffset = valueOffset,
                layout = edit.layout,
            };

            this.length += shift;
        }

        internal void WriteTypedSet(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            out RowCursor newScope)
        {
            int numBytes = sizeof(uint);
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            this.WriteUInt32(edit.valueOffset, 0);
            int valueOffset = edit.valueOffset + sizeof(uint); // Point after the Size
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset, // Point at the Size
                valueOffset = valueOffset,
                metaOffset = valueOffset,
                layout = edit.layout,
            };

            this.length += shift;
        }

        internal void WriteTypedMap(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            out RowCursor newScope)
        {
            int numBytes = sizeof(uint); // Sized scope.
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            this.WriteUInt32(edit.valueOffset, 0);
            int valueOffset = edit.valueOffset + sizeof(uint); // Point after the Size
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset, // Point at the Size
                valueOffset = valueOffset,
                metaOffset = valueOffset,
                layout = edit.layout,
            };

            this.length += shift;
        }

        internal void WriteSparseTuple(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            out RowCursor newScope)
        {
            int numBytes = sizeof(LayoutCode) * (1 + typeArgs.Count); // nulls for each element.
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            int valueOffset = edit.valueOffset;
            for (int i = 0; i < typeArgs.Count; i++)
            {
                this.WriteSparseTypeCode(valueOffset, LayoutCode.Null);
                valueOffset += sizeof(LayoutCode);
            }

            this.WriteSparseTypeCode(valueOffset, LayoutCode.EndScope);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset,
                valueOffset = edit.valueOffset,
                metaOffset = edit.valueOffset,
                layout = edit.layout,
                count = typeArgs.Count,
            };

            this.length += shift;
        }

        internal void WriteTypedTuple(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            out RowCursor newScope)
        {
            int numBytes = this.CountDefaultValue(scopeType, typeArgs);
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            int numWritten = this.WriteDefaultValue(edit.valueOffset, scopeType, typeArgs);
            Contract.Assert(numBytes == numWritten);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset,
                valueOffset = edit.valueOffset,
                metaOffset = edit.valueOffset,
                layout = edit.layout,
                count = typeArgs.Count,
            };

            this.length += shift;
            newScope.MoveNext(ref this);
        }

        internal void WriteNullable(
            ref RowCursor edit,
            LayoutScope scopeType,
            TypeArgumentList typeArgs,
            UpdateOptions options,
            bool hasValue,
            out RowCursor newScope)
        {
            int numBytes = this.CountDefaultValue(scopeType, typeArgs);
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);
            int numWritten = this.WriteDefaultValue(edit.valueOffset, scopeType, typeArgs);
            Contract.Assert(numBytes == numWritten);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            if (hasValue)
            {
                this.WriteInt8(edit.valueOffset, 1);
            }

            int valueOffset = edit.valueOffset + 1;
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset,
                valueOffset = valueOffset,
                metaOffset = valueOffset,
                layout = edit.layout,
                count = 2,
                index = 1,
            };

            this.length += shift;
            newScope.MoveNext(ref this);
        }

        internal void WriteSparseUDT(
            ref RowCursor edit,
            LayoutScope scopeType,
            Layout udt,
            UpdateOptions options,
            out RowCursor newScope)
        {
            TypeArgumentList typeArgs = new TypeArgumentList(udt.SchemaId);
            int numBytes = udt.Size + sizeof(LayoutCode);
            this.EnsureSparse(ref edit, scopeType, typeArgs, numBytes, options, out int metaBytes, out int spaceNeeded, out int shift);
            this.WriteSparseMetadata(ref edit, scopeType, typeArgs, metaBytes);

            // Clear all presence bits.
            this.buffer.Slice(edit.valueOffset, udt.Size).Fill(0);

            // Write scope terminator.
            int valueOffset = edit.valueOffset + udt.Size;
            this.WriteSparseTypeCode(valueOffset, LayoutCode.EndScope);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            newScope = new RowCursor
            {
                scopeType = scopeType,
                scopeTypeArgs = typeArgs,
                start = edit.valueOffset,
                valueOffset = valueOffset,
                metaOffset = valueOffset,
                layout = udt,
            };

            this.length += shift;
        }

        /// <summary>Delete the sparse field at the indicated path.</summary>
        /// <param name="edit">The field to delete.</param>
        internal void DeleteSparse(ref RowCursor edit)
        {
            // If the field doesn't exist, then nothing to do.
            if (!edit.exists)
            {
                return;
            }

            int numBytes = 0;
            this.EnsureSparse(
                ref edit,
                edit.cellType,
                edit.cellTypeArgs,
                numBytes,
                RowOptions.Delete,
                out int _,
                out int _,
                out int shift);
            this.length += shift;
        }

        /// <summary>Rotates the sign bit of a two's complement value to the least significant bit.</summary>
        /// <param name="value">A signed value.</param>
        /// <returns>An unsigned value encoding the same value but with the sign bit in the LSB.</returns>
        /// <remarks>
        /// Moves the signed bit of a two's complement value to the least significant bit (LSB) by:
        /// <list type="number">
        /// <item>
        /// <description>If negative, take the two's complement.</description>
        /// </item> <item>
        /// <description>Left shift the value by 1 bit.</description>
        /// </item> <item>
        /// <description>If negative, set the LSB to 1.</description>
        /// </item>
        /// </list>
        /// </remarks>
        [SuppressMessage("StyleCop.CSharp.DocumentationRules", "SA1629:DocumentationTextMustEndWithAPeriod", Justification = "Colon.")]
        internal static ulong RotateSignToLsb(long value)
        {
            // Rotate sign to LSB
            unchecked
            {
                bool isNegative = value < 0;
                ulong uvalue = (ulong)value;
                uvalue = isNegative ? ((~uvalue + 1ul) << 1) + 1ul : uvalue << 1;
                return uvalue;
            }
        }

        /// <summary>Undoes the rotation introduced by <see cref="RotateSignToLsb" />.</summary>
        /// <param name="uvalue">An unsigned value with the sign bit in the LSB.</param>
        /// <returns>A signed two's complement value encoding the same value.</returns>
        internal static long RotateSignToMsb(ulong uvalue)
        {
            // Rotate sign to MSB
            unchecked
            {
                bool isNegative = uvalue % 2 != 0;
                long value = (long)(isNegative ? (~(uvalue >> 1) + 1ul) | 0x8000000000000000ul : uvalue >> 1);
                return value;
            }
        }

        /// <summary>Compute the byte offset from the beginning of the row for a given variable column's value.</summary>
        /// <param name="layout">The (optional) layout of the current scope.</param>
        /// <param name="scopeOffset">The 0-based offset to the beginning of the scope's value.</param>
        /// <param name="varIndex">The 0-based index of the variable column within the variable segment.</param>
        /// <returns>
        /// The byte offset from the beginning of the row where the variable column's value should be
        /// located.
        /// </returns>
        internal int ComputeVariableValueOffset(Layout layout, int scopeOffset, int varIndex)
        {
            if (layout == null)
            {
                return scopeOffset;
            }

            int index = layout.NumFixed + varIndex;
            Layout.ColumnView columns = layout.Columns;
            Contract.Assert(index <= columns.Length);
            int offset = scopeOffset + layout.Size;
            for (int i = layout.NumFixed; i < index; i++)
            {
                LayoutColumn col = columns[i];
                if (this.ReadBit(scopeOffset, col.NullBit))
                {
                    ulong valueSizeInBytes = this.Read7BitEncodedUInt(offset, out int lengthSizeInBytes);
                    if (col.Type.IsVarint)
                    {
                        offset += lengthSizeInBytes;
                    }
                    else
                    {
                        offset += (int)valueSizeInBytes + lengthSizeInBytes;
                    }
                }
            }

            return offset;
        }

        /// <summary>Move a sparse iterator to the next field within the same sparse scope.</summary>
        /// <param name="edit">The iterator to advance.</param>
        /// <remarks>
        /// <paramref name="edit.Path">
        /// On success, the path of the field at the given offset, otherwise
        /// undefined.
        /// </paramref>
        /// <paramref name="edit.MetaOffset">
        /// If found, the offset to the metadata of the field, otherwise a
        /// location to insert the field.
        /// </paramref>
        /// <paramref name="edit.cellType">
        /// If found, the layout code of the matching field found, otherwise
        /// undefined.
        /// </paramref>
        /// <paramref name="edit.ValueOffset">
        /// If found, the offset to the value of the field, otherwise
        /// undefined.
        /// </paramref>.
        /// </remarks>
        /// <returns>True if there is another field, false if there are no more.</returns>
        internal bool SparseIteratorMoveNext(ref RowCursor edit)
        {
            if (edit.cellType != null)
            {
                // Move to the next element of an indexed scope.
                if (edit.scopeType.IsIndexedScope)
                {
                    edit.index++;
                }

                // Skip forward to the end of the current value.
                if (edit.endOffset != 0)
                {
                    edit.metaOffset = edit.endOffset;
                    edit.endOffset = 0;
                }
                else
                {
                    edit.metaOffset += this.SparseComputeSize(ref edit);
                }
            }

            // Check if reached end of buffer.
            if (edit.metaOffset < this.length)
            {
                // Check if reached end of sized scope.
                if (!edit.scopeType.IsSizedScope || (edit.index != edit.count))
                {
                    // Read the metadata.
                    this.ReadSparseMetadata(ref edit);

                    // Check if reached end of sparse scope.
                    if (!(edit.cellType is LayoutEndScope))
                    {
                        edit.exists = true;
                        return true;
                    }
                }
            }

            edit.cellType = LayoutType.EndScope;
            edit.exists = false;
            edit.valueOffset = edit.metaOffset;
            return false;
        }

        /// <summary>Produce a new scope from the current iterator position.</summary>
        /// <param name="edit">An initialized iterator pointing at a scope.</param>
        /// <param name="immutable">True if the new scope should be marked immutable (read-only).</param>
        /// <returns>A new scope beginning at the current iterator position.</returns>
        internal RowCursor SparseIteratorReadScope(ref RowCursor edit, bool immutable)
        {
            LayoutScope scopeType = edit.cellType as LayoutScope;
            switch (scopeType)
            {
                case LayoutObject _:
                case LayoutArray _:
                {
                    return new RowCursor
                    {
                        scopeType = scopeType,
                        scopeTypeArgs = edit.cellTypeArgs,
                        start = edit.valueOffset,
                        valueOffset = edit.valueOffset,
                        metaOffset = edit.valueOffset,
                        layout = edit.layout,
                        immutable = immutable,
                    };
                }

                case LayoutTypedArray _:
                case LayoutTypedSet _:
                case LayoutTypedMap _:
                {
                    int valueOffset = edit.valueOffset + sizeof(uint); // Point after the Size
                    return new RowCursor
                    {
                        scopeType = scopeType,
                        scopeTypeArgs = edit.cellTypeArgs,
                        start = edit.valueOffset, // Point at the Size
                        valueOffset = valueOffset,
                        metaOffset = valueOffset,
                        layout = edit.layout,
                        immutable = immutable,
                        count = (int)this.ReadUInt32(edit.valueOffset),
                    };
                }

                case LayoutTypedTuple _:
                case LayoutTuple _:
                case LayoutTagged _:
                case LayoutTagged2 _:
                {
                    return new RowCursor
                    {
                        scopeType = scopeType,
                        scopeTypeArgs = edit.cellTypeArgs,
                        start = edit.valueOffset,
                        valueOffset = edit.valueOffset,
                        metaOffset = edit.valueOffset,
                        layout = edit.layout,
                        immutable = immutable,
                        count = edit.cellTypeArgs.Count,
                    };
                }

                case LayoutNullable _:
                {
                    bool hasValue = this.ReadInt8(edit.valueOffset) != 0;
                    if (hasValue)
                    {
                        // Start at the T so it can be read.
                        int valueOffset = edit.valueOffset + 1;
                        return new RowCursor
                        {
                            scopeType = scopeType,
                            scopeTypeArgs = edit.cellTypeArgs,
                            start = edit.valueOffset,
                            valueOffset = valueOffset,
                            metaOffset = valueOffset,
                            layout = edit.layout,
                            immutable = immutable,
                            count = 2,
                            index = 1,
                        };
                    }
                    else
                    {
                        // Start at the end of the scope, instead of at the T, so the T will be skipped.
                        TypeArgument typeArg = edit.cellTypeArgs[0];
                        int valueOffset = edit.valueOffset + 1 + this.CountDefaultValue(typeArg.Type, typeArg.TypeArgs);
                        return new RowCursor
                        {
                            scopeType = scopeType,
                            scopeTypeArgs = edit.cellTypeArgs,
                            start = edit.valueOffset,
                            valueOffset = valueOffset,
                            metaOffset = valueOffset,
                            layout = edit.layout,
                            immutable = immutable,
                            count = 2,
                            index = 2,
                        };
                    }
                }

                case LayoutUDT _:
                {
                    Layout udt = this.resolver.Resolve(edit.cellTypeArgs.SchemaId);
                    int valueOffset = this.ComputeVariableValueOffset(udt, edit.valueOffset, udt.NumVariable);
                    return new RowCursor
                    {
                        scopeType = scopeType,
                        scopeTypeArgs = edit.cellTypeArgs,
                        start = edit.valueOffset,
                        valueOffset = valueOffset,
                        metaOffset = valueOffset,
                        layout = udt,
                        immutable = immutable,
                    };
                }

                default:
                    Contract.Fail("Not a scope type.");
                    return default;
            }
        }

        /// <summary>
        /// Compute the byte offsets from the beginning of the row for a given sparse field insertion
        /// into a set/map.
        /// </summary>
        /// <param name="scope">The sparse scope to insert into.</param>
        /// <param name="srcEdit">The field to move into the set/map.</param>
        /// <returns>The prepared edit context.</returns>
        internal RowCursor PrepareSparseMove(ref RowCursor scope, ref RowCursor srcEdit)
        {
            Contract.Requires(scope.scopeType.IsUniqueScope);

            Contract.Requires(scope.index == 0);
            scope.Clone(out RowCursor dstEdit);

            dstEdit.metaOffset = scope.valueOffset;
            int srcSize = this.SparseComputeSize(ref srcEdit);
            int srcBytes = srcSize - (srcEdit.valueOffset - srcEdit.metaOffset);
            while (dstEdit.index < dstEdit.count)
            {
                this.ReadSparseMetadata(ref dstEdit);
                Contract.Assert(dstEdit.pathOffset == default);

                int elmSize = -1; // defer calculating the full size until needed.
                int cmp;
                if (scope.scopeType is LayoutTypedMap)
                {
                    cmp = this.CompareKeyValueFieldValue(srcEdit, dstEdit);
                }
                else
                {
                    elmSize = this.SparseComputeSize(ref dstEdit);
                    int elmBytes = elmSize - (dstEdit.valueOffset - dstEdit.metaOffset);
                    cmp = this.CompareFieldValue(srcEdit, srcBytes, dstEdit, elmBytes);
                }

                if (cmp <= 0)
                {
                    dstEdit.exists = cmp == 0;
                    return dstEdit;
                }

                elmSize = (elmSize == -1) ? this.SparseComputeSize(ref dstEdit) : elmSize;
                dstEdit.index++;
                dstEdit.metaOffset += elmSize;
            }

            dstEdit.exists = false;
            dstEdit.cellType = LayoutType.EndScope;
            dstEdit.valueOffset = dstEdit.metaOffset;
            return dstEdit;
        }

        internal void TypedCollectionMoveField(ref RowCursor dstEdit, ref RowCursor srcEdit, RowOptions options)
        {
            int encodedSize = this.SparseComputeSize(ref srcEdit);
            int numBytes = encodedSize - (srcEdit.valueOffset - srcEdit.metaOffset);

            // Insert the field metadata into its new location.
            this.EnsureSparse(
                ref dstEdit,
                srcEdit.cellType,
                srcEdit.cellTypeArgs,
                numBytes,
                options,
                out int metaBytes,
                out int spaceNeeded,
                out int shiftInsert);

            this.WriteSparseMetadata(ref dstEdit, srcEdit.cellType, srcEdit.cellTypeArgs, metaBytes);
            Contract.Assert(spaceNeeded == metaBytes + numBytes);
            if (srcEdit.metaOffset >= dstEdit.metaOffset)
            {
                srcEdit.metaOffset += shiftInsert;
                srcEdit.valueOffset += shiftInsert;
            }

            // Copy the value bits from the old location.
            this.buffer.Slice(srcEdit.valueOffset, numBytes).CopyTo(this.buffer.Slice(dstEdit.valueOffset));
            this.length += shiftInsert;

            // Delete the old location.
            this.EnsureSparse(
                ref srcEdit,
                srcEdit.cellType,
                srcEdit.cellTypeArgs,
                numBytes,
                RowOptions.Delete,
                out metaBytes,
                out spaceNeeded,
                out int shiftDelete);

            Contract.Assert(shiftDelete < 0);
            this.length += shiftDelete;
        }

        /// <summary>Rebuild the unique index for a set/map scope.</summary>
        /// <param name="scope">The sparse scope to rebuild an index for.</param>
        /// <returns>Success if the index could be built, an error otherwise.</returns>
        /// <remarks>
        /// The <paramref name="scope" /> MUST be a set or map scope.
        /// <para>
        /// The scope may have been built (e.g. via RowWriter) with relaxed uniqueness constraint checking.
        /// This operation rebuilds an index to support verification of uniqueness constraints during
        /// subsequent partial updates.  If the appropriate uniqueness constraints cannot be established (i.e.
        /// a duplicate exists), this operation fails.  Before continuing, the resulting scope should either:
        /// <list type="number">
        /// <item>
        /// <description>
        /// Be repaired (e.g. by deleting duplicates) and the index rebuild operation should be
        /// run again.
        /// </description>
        /// </item> <item>
        /// <description>Be deleted.  The entire scope should be removed including its items.</description>
        /// </item>
        /// </list> Failure to perform one of these actions will leave the row is potentially in a corrupted
        /// state where partial updates may subsequent fail.
        /// </para>
        /// <para>
        /// The target <paramref name="scope" /> may or may not have already been indexed.  This
        /// operation is idempotent.
        /// </para>
        /// </remarks>
        internal Result TypedCollectionUniqueIndexRebuild(ref RowCursor scope)
        {
            Contract.Requires(scope.scopeType.IsUniqueScope);
            Contract.Requires(scope.index == 0);
            scope.Clone(out RowCursor dstEdit);
            if (dstEdit.count <= 1)
            {
                return Result.Success;
            }

            // Compute Index Elements.
            Span<UniqueIndexItem> uniqueIndex = dstEdit.count < 100 ? stackalloc UniqueIndexItem[dstEdit.count] : new UniqueIndexItem[dstEdit.count];
            dstEdit.metaOffset = scope.valueOffset;
            for (; dstEdit.index < dstEdit.count; dstEdit.index++)
            {
                this.ReadSparseMetadata(ref dstEdit);
                Contract.Assert(dstEdit.pathOffset == default);
                int elmSize = this.SparseComputeSize(ref dstEdit);

                uniqueIndex[dstEdit.index] = new UniqueIndexItem
                {
                    Code = dstEdit.cellType.LayoutCode,
                    MetaOffset = dstEdit.metaOffset,
                    ValueOffset = dstEdit.valueOffset,
                    Size = elmSize,
                };

                dstEdit.metaOffset += elmSize;
            }

            // Create scratch space equal to the sum of the sizes of the scope's values.
            // Implementation Note: theoretically this scratch space could be eliminated by
            // performing the item move operations directly during the Insertion Sort, however,
            // doing so might result in moving the same item multiple times.  Under the assumption
            // that items are relatively large, using scratch space requires each item to be moved
            // AT MOST once.  Given that row buffer memory is likely reused, scratch space is
            // relatively memory efficient.
            int shift = dstEdit.metaOffset - scope.valueOffset;

            // Sort and check for duplicates.
            unsafe
            {
                Span<UniqueIndexItem> p = new Span<UniqueIndexItem>(Unsafe.AsPointer(ref uniqueIndex.GetPinnableReference()), uniqueIndex.Length);
                if (!this.InsertionSort(ref scope, ref dstEdit, p))
                {
                    return Result.Exists;
                }
            }

            // Move elements.
            int metaOffset = scope.valueOffset;
            this.Ensure(this.length + shift);
            this.buffer.Slice(metaOffset, this.length - metaOffset).CopyTo(this.buffer.Slice(metaOffset + shift));
            foreach (UniqueIndexItem x in uniqueIndex)
            {
                this.buffer.Slice(x.MetaOffset + shift, x.Size).CopyTo(this.buffer.Slice(metaOffset));
                metaOffset += x.Size;
            }

            // Delete the scratch space (if necessary - if it doesn't just fall off the end of the row).
            if (metaOffset != this.length)
            {
                this.buffer.Slice(metaOffset + shift, this.length - metaOffset).CopyTo(this.buffer.Slice(metaOffset));
            }

#if DEBUG

            // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
            this.buffer.Slice(this.length, shift).Fill(0xFF);
#endif

            return Result.Success;
        }

        private static int CountSparsePath(ref RowCursor edit)
        {
            if (!edit.writePathToken.IsNull)
            {
                return edit.writePathToken.Varint.Length;
            }

            if (edit.layout.Tokenizer.TryFindToken(edit.writePath, out edit.writePathToken))
            {
                return edit.writePathToken.Varint.Length;
            }

            int numBytes = edit.writePath.ToUtf8String().Length;
            int sizeLenInBytes = RowBuffer.Count7BitEncodedUInt((ulong)(edit.layout.Tokenizer.Count + numBytes));
            return sizeLenInBytes + numBytes;
        }

        /// <summary>
        /// Compute the number of bytes necessary to store the unsigned integer using the varuint
        /// encoding.
        /// </summary>
        /// <param name="value">The value to be encoded.</param>
        /// <returns>The number of bytes needed to store the varuint encoding of <see cref="value" />.</returns>
        internal static int Count7BitEncodedUInt(ulong value)
        {
            // Count the number of bytes needed to write out an int 7 bits at a time.
            int i = 0;
            while (value >= 0x80)
            {
                i++;
                value >>= 7;
            }

            i++;
            return i;
        }

        /// <summary>
        /// Compute the number of bytes necessary to store the signed integer using the varint
        /// encoding.
        /// </summary>
        /// <param name="value">The value to be encoded.</param>
        /// <returns>The number of bytes needed to store the varint encoding of <see cref="value" />.</returns>
        private static int Count7BitEncodedInt(long value)
        {
            return RowBuffer.Count7BitEncodedUInt(RowBuffer.RotateSignToLsb(value));
        }

        /// <summary>
        /// Reads in the contents of the RowBuffer from an input stream and initializes the row buffer
        /// with the associated layout and rowVersion.
        /// </summary>
        /// <returns>true if the serialization succeeded. false if the input stream was corrupted.</returns>
        private bool InitReadFrom(HybridRowVersion rowVersion)
        {
            HybridRowHeader header = this.ReadHeader(0);
            Layout layout = this.resolver.Resolve(header.SchemaId);
            Contract.Assert(header.SchemaId == layout.SchemaId);
            if ((header.Version != rowVersion) || (HybridRowHeader.Size + layout.Size > this.length))
            {
                return false;
            }

            return true;
        }

        /// <summary>Skip over a nested scope.</summary>
        /// <param name="edit">The sparse scope to search.</param>
        /// <returns>The 0-based byte offset immediately following the scope end marker.</returns>
        private int SkipScope(ref RowCursor edit)
        {
            while (this.SparseIteratorMoveNext(ref edit))
            {
            }

            if (!edit.scopeType.IsSizedScope)
            {
                edit.metaOffset += sizeof(LayoutCode); // Move past the end of scope marker.
            }

            return edit.metaOffset;
        }

        /// <summary>Compares the values of two encoded fields using the hybrid row binary collation.</summary>
        /// <param name="left">An edit describing the left field.</param>
        /// <param name="leftLen">The size of the left field's value in bytes.</param>
        /// <param name="right">An edit describing the right field.</param>
        /// <param name="rightLen">The size of the right field's value in bytes.</param>
        /// <returns>
        /// <list type="table">
        /// <item>
        /// <term>-1</term><description>left less than right.</description>
        /// </item> <item>
        /// <term>0</term><description>left and right are equal.</description>
        /// </item> <item>
        /// <term>1</term><description>left is greater than right.</description>
        /// </item>
        /// </list>
        /// </returns>
        [SuppressMessage("Microsoft.StyleCop.CSharp.OrderingRules", "SA1201", Justification = "Logical Grouping.")]
        private int CompareFieldValue(RowCursor left, int leftLen, RowCursor right, int rightLen)
        {
            if (left.cellType.LayoutCode < right.cellType.LayoutCode)
            {
                return -1;
            }

            if (left.cellType == right.cellType)
            {
                if (leftLen < rightLen)
                {
                    return -1;
                }

                if (leftLen == rightLen)
                {
                    return this.buffer.Slice(left.valueOffset, leftLen).SequenceCompareTo(this.buffer.Slice(right.valueOffset, rightLen));
                }
            }

            return 1;
        }

        /// <summary>
        /// Compares the values of two encoded key-value pair fields using the hybrid row binary
        /// collation.
        /// </summary>
        /// <param name="left">An edit describing the left field.</param>
        /// <param name="right">An edit describing the right field.</param>
        /// <returns>
        /// <list type="table">
        /// <item>
        /// <term>-1</term><description>left less than right.</description>
        /// </item> <item>
        /// <term>0</term><description>left and right are equal.</description>
        /// </item> <item>
        /// <term>1</term><description>left is greater than right.</description>
        /// </item>
        /// </list>
        /// </returns>
        private int CompareKeyValueFieldValue(RowCursor left, RowCursor right)
        {
            LayoutTypedTuple leftScopeType = left.cellType as LayoutTypedTuple;
            LayoutTypedTuple rightScopeType = right.cellType as LayoutTypedTuple;
            Contract.Requires(leftScopeType != null);
            Contract.Requires(rightScopeType != null);
            Contract.Requires(left.cellTypeArgs.Count == 2);
            Contract.Requires(left.cellTypeArgs.Equals(right.cellTypeArgs));

            RowCursor leftKey = new RowCursor
            {
                layout = left.layout,
                scopeType = leftScopeType,
                scopeTypeArgs = left.cellTypeArgs,
                start = left.valueOffset,
                metaOffset = left.valueOffset,
                index = 0,
            };

            this.ReadSparseMetadata(ref leftKey);
            Contract.Assert(leftKey.pathOffset == default);
            int leftKeyLen = this.SparseComputeSize(ref leftKey) - (leftKey.valueOffset - leftKey.metaOffset);

            RowCursor rightKey = new RowCursor
            {
                layout = right.layout,
                scopeType = rightScopeType,
                scopeTypeArgs = right.cellTypeArgs,
                start = right.valueOffset,
                metaOffset = right.valueOffset,
                index = 0,
            };

            this.ReadSparseMetadata(ref rightKey);
            Contract.Assert(rightKey.pathOffset == default);
            int rightKeyLen = this.SparseComputeSize(ref rightKey) - (rightKey.valueOffset - rightKey.metaOffset);

            return this.CompareFieldValue(leftKey, leftKeyLen, rightKey, rightKeyLen);
        }

        /// <summary>
        /// Sorts the <paramref name="uniqueIndex" /> array structure using the hybrid row binary
        /// collation.
        /// </summary>
        /// <param name="scope">The scope to be sorted.</param>
        /// <param name="dstEdit">A edit that points at the scope.</param>
        /// <param name="uniqueIndex">
        /// A unique index array structure that identifies the row offsets of each
        /// element in the scope.
        /// </param>
        /// <returns>true if the array was sorted, false if a duplicate was found during sorting.</returns>
        /// <remarks>
        /// Implementation Note:
        /// <para>This method MUST guarantee that if at least one duplicate exists it will be found.</para>
        /// Insertion Sort is used for this purpose as it guarantees that each value is eventually compared
        /// against its previous item in sorted order.  If any two successive items are the same they must be
        /// duplicates.
        /// <para>
        /// Other search algorithms, such as Quick Sort or Merge Sort, may offer fewer comparisons in the
        /// limit but don't necessarily guarantee that duplicates will be discovered.  If an alternative
        /// algorithm is used, then an independent duplicate pass MUST be employed.
        /// </para>
        /// <para>
        /// Under the current operational assumptions, the expected cardinality of sets and maps is
        /// expected to be relatively small.  If this assumption changes, Insertion Sort may no longer be the
        /// best choice.
        /// </para>
        /// </remarks>
        private bool InsertionSort(ref RowCursor scope, ref RowCursor dstEdit, Span<UniqueIndexItem> uniqueIndex)
        {
            RowCursor leftEdit = dstEdit;
            RowCursor rightEdit = dstEdit;

            for (int i = 1; i < uniqueIndex.Length; i++)
            {
                UniqueIndexItem x = uniqueIndex[i];
                leftEdit.cellType = LayoutType.FromCode(x.Code);
                leftEdit.metaOffset = x.MetaOffset;
                leftEdit.valueOffset = x.ValueOffset;
                int leftBytes = x.Size - (x.ValueOffset - x.MetaOffset);

                // Walk backwards searching for the insertion point for the item as position i.
                int j;
                for (j = i - 1; j >= 0; j--)
                {
                    UniqueIndexItem y = uniqueIndex[j];
                    rightEdit.cellType = LayoutType.FromCode(y.Code);
                    rightEdit.metaOffset = y.MetaOffset;
                    rightEdit.valueOffset = y.ValueOffset;

                    int cmp;
                    if (scope.scopeType is LayoutTypedMap)
                    {
                        cmp = this.CompareKeyValueFieldValue(leftEdit, rightEdit);
                    }
                    else
                    {
                        int rightBytes = y.Size - (y.ValueOffset - y.MetaOffset);
                        cmp = this.CompareFieldValue(leftEdit, leftBytes, rightEdit, rightBytes);
                    }

                    // If there are duplicates then fail.
                    if (cmp == 0)
                    {
                        return false;
                    }

                    if (cmp > 0)
                    {
                        break;
                    }

                    // Swap the jth item to the right to make space for the ith item which is smaller.
                    uniqueIndex[j + 1] = uniqueIndex[j];
                }

                // Insert the ith item into the sorted array.
                uniqueIndex[j + 1] = x;
            }

            return true;
        }

        internal int ReadSparsePathLen(Layout layout, int offset, out int pathLenInBytes, out int pathOffset)
        {
            int token = (int)this.Read7BitEncodedUInt(offset, out int sizeLenInBytes);
            if (token < layout.Tokenizer.Count)
            {
                pathLenInBytes = sizeLenInBytes;
                pathOffset = offset;
                return token;
            }

            int numBytes = token - layout.Tokenizer.Count;
            pathLenInBytes = numBytes + sizeLenInBytes;
            pathOffset = offset + sizeLenInBytes;
            return token;
        }

        internal Utf8Span ReadSparsePath(ref RowCursor edit)
        {
            if (edit.layout.Tokenizer.TryFindString((ulong)edit.pathToken, out Utf8String path))
            {
                return path.Span;
            }

            int numBytes = edit.pathToken - edit.layout.Tokenizer.Count;
            return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(edit.pathOffset, numBytes));
        }

        private void WriteSparsePath(ref RowCursor edit, int offset)
        {
            // Some scopes don't encode paths, therefore the cost is always zero.
            if (edit.scopeType.IsIndexedScope)
            {
                edit.pathToken = default;
                edit.pathOffset = default;
                return;
            }

            Contract.Assert(!edit.layout.Tokenizer.TryFindToken(edit.writePath, out StringToken _) || !edit.writePathToken.IsNull);
            if (!edit.writePathToken.IsNull)
            {
                edit.writePathToken.Varint.CopyTo(this.buffer.Slice(offset));
                edit.pathToken = (int)edit.writePathToken.Id;
                edit.pathOffset = offset;
            }
            else
            {
                // TODO: It would be better if we could avoid allocating here when the path is UTF16.
                Utf8Span span = edit.writePath.ToUtf8String();
                edit.pathToken = edit.layout.Tokenizer.Count + span.Length;
                int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)edit.pathToken);
                edit.pathOffset = offset + sizeLenInBytes;
                span.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
            }
        }

        private Utf8Span ReadString(int offset, out int sizeLenInBytes)
        {
            int numBytes = (int)this.Read7BitEncodedUInt(offset, out sizeLenInBytes);
            return Utf8Span.UnsafeFromUtf8BytesNoValidation(this.buffer.Slice(offset + sizeLenInBytes, numBytes));
        }

        private int WriteString(int offset, Utf8Span value)
        {
            int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
            value.Span.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
            return sizeLenInBytes;
        }

        private ReadOnlySpan<byte> ReadBinary(int offset, out int sizeLenInBytes)
        {
            int numBytes = (int)this.Read7BitEncodedUInt(offset, out sizeLenInBytes);
            return this.buffer.Slice(offset + sizeLenInBytes, numBytes);
        }

        private int WriteBinary(int offset, ReadOnlySpan<byte> value)
        {
            int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
            value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
            return sizeLenInBytes;
        }

        private int WriteBinary(int offset, ReadOnlySequence<byte> value)
        {
            int sizeLenInBytes = this.Write7BitEncodedUInt(offset, (ulong)value.Length);
            value.CopyTo(this.buffer.Slice(offset + sizeLenInBytes));
            return sizeLenInBytes;
        }

        private void Ensure(int size)
        {
            if (this.buffer.Length < size)
            {
                this.buffer = this.resizer.Resize(size, this.buffer);
            }
        }

        private void EnsureVariable(int offset, bool isVarint, int numBytes, bool exists, out int spaceNeeded, out int shift)
        {
            int spaceAvailable = 0;
            ulong existingValueBytes = 0;
            if (exists)
            {
                existingValueBytes = this.Read7BitEncodedUInt(offset, out spaceAvailable);
            }

            if (isVarint)
            {
                spaceNeeded = numBytes;
            }
            else
            {
                spaceAvailable += (int)existingValueBytes; // size already in spaceAvailable
                spaceNeeded = numBytes + RowBuffer.Count7BitEncodedUInt((ulong)numBytes);
            }

            shift = spaceNeeded - spaceAvailable;
            if (shift > 0)
            {
                this.Ensure(this.length + shift);
                this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded));
            }
            else if (shift < 0)
            {
                this.buffer.Slice(offset + spaceAvailable, this.length - (offset + spaceAvailable)).CopyTo(this.buffer.Slice(offset + spaceNeeded));
            }
        }

        [Conditional("DEBUG")]
        private void ValidateSparsePrimitiveTypeCode(ref RowCursor edit, LayoutType code)
        {
            Contract.Assert(edit.exists);

            if (edit.scopeType.HasImplicitTypeCode(ref edit))
            {
                if (edit.scopeType is LayoutNullable)
                {
                    Contract.Assert(edit.scopeTypeArgs.Count == 1);
                    Contract.Assert(edit.index == 1);
                    Contract.Assert(edit.scopeTypeArgs[0].Type == code);
                    Contract.Assert(edit.scopeTypeArgs[0].TypeArgs.Count == 0);
                }
                else if (edit.scopeType.IsFixedArity)
                {
                    Contract.Assert(edit.scopeTypeArgs.Count > edit.index);
                    Contract.Assert(edit.scopeTypeArgs[edit.index].Type == code);
                    Contract.Assert(edit.scopeTypeArgs[edit.index].TypeArgs.Count == 0);
                }
                else
                {
                    Contract.Assert(edit.scopeTypeArgs.Count == 1);
                    Contract.Assert(edit.scopeTypeArgs[0].Type == code);
                    Contract.Assert(edit.scopeTypeArgs[0].TypeArgs.Count == 0);
                }
            }
            else
            {
                if (code == LayoutType.Boolean)
                {
                    code = this.ReadSparseTypeCode(edit.metaOffset);
                    Contract.Assert(code == LayoutType.Boolean || code == LayoutType.BooleanFalse);
                }
                else
                {
                    Contract.Assert(this.ReadSparseTypeCode(edit.metaOffset) == code);
                }
            }

            if (edit.scopeType.IsIndexedScope)
            {
                Contract.Assert(edit.pathOffset == default);
                Contract.Assert(edit.pathToken == default);
            }
            else
            {
                int token = this.ReadSparsePathLen(edit.layout, edit.metaOffset + sizeof(LayoutCode), out int _, out int pathOffset);
                Contract.Assert(edit.pathOffset == pathOffset);
                Contract.Assert(edit.pathToken == token);
            }
        }

        private void WriteSparseMetadata(ref RowCursor edit, LayoutType cellType, TypeArgumentList typeArgs, int metaBytes)
        {
            int metaOffset = edit.metaOffset;
            if (!edit.scopeType.HasImplicitTypeCode(ref edit))
            {
                metaOffset += cellType.WriteTypeArgument(ref this, metaOffset, typeArgs);
            }

            this.WriteSparsePath(ref edit, metaOffset);
            edit.valueOffset = edit.metaOffset + metaBytes;
            Contract.Assert(edit.valueOffset == edit.metaOffset + metaBytes);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private void EnsureSparse(
            ref RowCursor edit,
            LayoutType cellType,
            TypeArgumentList typeArgs,
            int numBytes,
            UpdateOptions options,
            out int metaBytes,
            out int spaceNeeded,
            out int shift)
        {
            this.EnsureSparse(ref edit, cellType, typeArgs, numBytes, (RowOptions)options, out metaBytes, out spaceNeeded, out shift);
        }

        /// <summary>Ensure that sufficient space exists in the row buffer to write the current value.</summary>
        /// <param name="edit">
        /// The prepared edit indicating where and in what context the current write will
        /// happen.
        /// </param>
        /// <param name="cellType">The type of the field to be written.</param>
        /// <param name="typeArgs">The type arguments of the field to be written.</param>
        /// <param name="numBytes">The number of bytes needed to encode the value of the field to be written.</param>
        /// <param name="options">The kind of edit to be performed.</param>
        /// <param name="metaBytes">
        /// On success, the number of bytes needed to encode the metadata of the new
        /// field.
        /// </param>
        /// <param name="spaceNeeded">
        /// On success, the number of bytes needed in total to encode the new field
        /// and its metadata.
        /// </param>
        /// <param name="shift">
        /// On success, the number of bytes the length of the row buffer was increased
        /// (which may be negative if the row buffer was shrunk).
        /// </param>
        private void EnsureSparse(
            ref RowCursor edit,
            LayoutType cellType,
            TypeArgumentList typeArgs,
            int numBytes,
            RowOptions options,
            out int metaBytes,
            out int spaceNeeded,
            out int shift)
        {
            int metaOffset = edit.metaOffset;
            int spaceAvailable = 0;

            // Compute the metadata offsets
            if (edit.scopeType.HasImplicitTypeCode(ref edit))
            {
                metaBytes = 0;
            }
            else
            {
                metaBytes = cellType.CountTypeArgument(typeArgs);
            }

            if (!edit.scopeType.IsIndexedScope)
            {
                Contract.Assert(edit.writePath != default(UtfAnyString));
                int pathLenInBytes = RowBuffer.CountSparsePath(ref edit);
                metaBytes += pathLenInBytes;
            }

            if (edit.exists)
            {
                // Compute value offset for existing value to be overwritten.
                spaceAvailable = this.SparseComputeSize(ref edit);
            }

            spaceNeeded = options == RowOptions.Delete ? 0 : metaBytes + numBytes;
            shift = spaceNeeded - spaceAvailable;
            if (shift > 0)
            {
                this.Ensure(this.length + shift);
            }

            this.buffer.Slice(metaOffset + spaceAvailable, this.length - (metaOffset + spaceAvailable))
                .CopyTo(this.buffer.Slice(metaOffset + spaceNeeded));

#if DEBUG
            if (shift < 0)
            {
                // Fill deleted bits (in debug builds) to detect overflow/alignment errors.
                this.buffer.Slice(this.length + shift, -shift).Fill(0xFF);
            }
#endif

            // Update the stored size (fixed arity scopes don't store the size because it is implied by the type args).
            if (edit.scopeType.IsSizedScope && !edit.scopeType.IsFixedArity)
            {
                if ((options == RowOptions.Insert) || (options == RowOptions.InsertAt) || ((options == RowOptions.Upsert) && !edit.exists))
                {
                    // Add one to the current scope count.
                    Contract.Assert(!edit.exists);
                    this.IncrementUInt32(edit.start, 1);
                    edit.count++;
                }
                else if ((options == RowOptions.Delete) && edit.exists)
                {
                    // Subtract one from the current scope count.
                    Contract.Assert(this.ReadUInt32(edit.start) > 0);
                    this.DecrementUInt32(edit.start, 1);
                    edit.count--;
                }
            }

            if (options == RowOptions.Delete)
            {
                edit.cellType = default;
                edit.cellTypeArgs = default;
                edit.exists = false;
            }
            else
            {
                edit.cellType = cellType;
                edit.cellTypeArgs = typeArgs;
                edit.exists = true;
            }
        }

        /// <summary>Read the metadata of an encoded sparse field.</summary>
        /// <param name="edit">The edit structure to fill in.</param>
        /// <remarks>
        /// <paramref name="edit.Path">
        /// On success, the path of the field at the given offset, otherwise
        /// undefined.
        /// </paramref>
        /// <paramref name="edit.MetaOffset">
        /// On success, the offset to the metadata of the field, otherwise a
        /// location to insert the field.
        /// </paramref>
        /// <paramref name="edit.cellType">
        /// On success, the layout code of the existing field, otherwise
        /// undefined.
        /// </paramref>
        /// <paramref name="edit.TypeArgs">
        /// On success, the type args of the existing field, otherwise
        /// undefined.
        /// </paramref>
        /// <paramref name="edit.ValueOffset">
        /// On success, the offset to the value of the field, otherwise
        /// undefined.
        /// </paramref>.
        /// </remarks>
        private void ReadSparseMetadata(ref RowCursor edit)
        {
            if (edit.scopeType.HasImplicitTypeCode(ref edit))
            {
                edit.scopeType.SetImplicitTypeCode(ref edit);
                edit.valueOffset = edit.metaOffset;
            }
            else
            {
                edit.cellType = this.ReadSparseTypeCode(edit.metaOffset);
                edit.valueOffset = edit.metaOffset + sizeof(LayoutCode);
                edit.cellTypeArgs = TypeArgumentList.Empty;
                if (edit.cellType is LayoutEndScope)
                {
                    // Reached end of current scope without finding another field.
                    edit.pathToken = default;
                    edit.pathOffset = default;
                    edit.valueOffset = edit.metaOffset;
                    return;
                }

                edit.cellTypeArgs = edit.cellType.ReadTypeArgumentList(ref this, edit.valueOffset, out int sizeLenInBytes);
                edit.valueOffset += sizeLenInBytes;
            }

            edit.scopeType.ReadSparsePath(ref this, ref edit);
        }

        /// <summary>Compute the size of a sparse field.</summary>
        /// <param name="edit">The edit structure describing the field to measure.</param>
        /// <returns>The length (in bytes) of the encoded field including the metadata and the value.</returns>
        private int SparseComputeSize(ref RowCursor edit)
        {
            if (!(edit.cellType is LayoutScope))
            {
                return this.SparseComputePrimitiveSize(edit.cellType, edit.metaOffset, edit.valueOffset);
            }

            // Compute offset to end of value for current value.
            RowCursor newScope = this.SparseIteratorReadScope(ref edit, immutable: true);
            return this.SkipScope(ref newScope) - edit.metaOffset;
        }

        /// <summary>Compute the size of a sparse (primitive) field.</summary>
        /// <param name="cellType">The type of the current sparse field.</param>
        /// <param name="metaOffset">The 0-based offset from the beginning of the row where the field begins.</param>
        /// <param name="valueOffset">
        /// The 0-based offset from the beginning of the row where the field's value
        /// begins.
        /// </param>
        /// <returns>The length (in bytes) of the encoded field including the metadata and the value.</returns>
        private int SparseComputePrimitiveSize(LayoutType cellType, int metaOffset, int valueOffset)
        {
            int metaBytes = valueOffset - metaOffset;
            LayoutCode code = cellType.LayoutCode;
            switch (code)
            {
                case LayoutCode.Null:
                    Contract.Assert(LayoutType.Null.Size == 0);
                    return metaBytes;

                case LayoutCode.Boolean:
                case LayoutCode.BooleanFalse:
                    Contract.Assert(LayoutType.Boolean.Size == 0);
                    return metaBytes;

                case LayoutCode.Int8:
                    return metaBytes + LayoutType.Int8.Size;

                case LayoutCode.Int16:
                    return metaBytes + LayoutType.Int16.Size;

                case LayoutCode.Int32:
                    return metaBytes + LayoutType.Int32.Size;

                case LayoutCode.Int64:
                    return metaBytes + LayoutType.Int64.Size;

                case LayoutCode.UInt8:
                    return metaBytes + LayoutType.UInt8.Size;

                case LayoutCode.UInt16:
                    return metaBytes + LayoutType.UInt16.Size;

                case LayoutCode.UInt32:
                    return metaBytes + LayoutType.UInt32.Size;

                case LayoutCode.UInt64:
                    return metaBytes + LayoutType.UInt64.Size;

                case LayoutCode.Float32:
                    return metaBytes + LayoutType.Float32.Size;

                case LayoutCode.Float64:
                    return metaBytes + LayoutType.Float64.Size;

                case LayoutCode.Float128:
                    return metaBytes + LayoutType.Float128.Size;

                case LayoutCode.Decimal:
                    return metaBytes + LayoutType.Decimal.Size;

                case LayoutCode.DateTime:
                    return metaBytes + LayoutType.DateTime.Size;

                case LayoutCode.UnixDateTime:
                    return metaBytes + LayoutType.UnixDateTime.Size;

                case LayoutCode.Guid:
                    return metaBytes + LayoutType.Guid.Size;

                case LayoutCode.MongoDbObjectId:
                    return metaBytes + LayoutType.MongoDbObjectId.Size;

                case LayoutCode.Utf8:
                case LayoutCode.Binary:
#pragma warning disable SA1137 // Elements should have the same indentation
                {
                    int numBytes = (int)this.Read7BitEncodedUInt(metaOffset + metaBytes, out int sizeLenInBytes);
                    return metaBytes + sizeLenInBytes + numBytes;
                }

                case LayoutCode.VarInt:
                case LayoutCode.VarUInt:
                {
                    this.Read7BitEncodedUInt(metaOffset + metaBytes, out int sizeLenInBytes);
                    return metaBytes + sizeLenInBytes;
                }
#pragma warning restore SA1137 // Elements should have the same indentation

                default:
                    Contract.Fail($"Not Implemented: {code}");
                    return 0;
            }
        }

        /// <summary>Return the size (in bytes) of the default sparse value for the type.</summary>
        /// <param name="code">The type of the default value.</param>
        /// <param name="typeArgs"></param>
        private int CountDefaultValue(LayoutType code, TypeArgumentList typeArgs)
        {
            switch (code)
            {
                case LayoutNull _:
                case LayoutBoolean _:
                    return 1;

                case LayoutInt8 _:
                    return LayoutType.Int8.Size;

                case LayoutInt16 _:
                    return LayoutType.Int16.Size;

                case LayoutInt32 _:
                    return LayoutType.Int32.Size;

                case LayoutInt64 _:
                    return LayoutType.Int64.Size;

                case LayoutUInt8 _:
                    return LayoutType.UInt8.Size;

                case LayoutUInt16 _:
                    return LayoutType.UInt16.Size;

                case LayoutUInt32 _:
                    return LayoutType.UInt32.Size;

                case LayoutUInt64 _:
                    return LayoutType.UInt64.Size;

                case LayoutFloat32 _:
                    return LayoutType.Float32.Size;

                case LayoutFloat64 _:
                    return LayoutType.Float64.Size;

                case LayoutFloat128 _:
                    return LayoutType.Float128.Size;

                case LayoutDecimal _:
                    return LayoutType.Decimal.Size;

                case LayoutDateTime _:
                    return LayoutType.DateTime.Size;

                case LayoutUnixDateTime _:
                    return LayoutType.UnixDateTime.Size;

                case LayoutGuid _:
                    return LayoutType.Guid.Size;

                case LayoutMongoDbObjectId _:
                    return LayoutType.MongoDbObjectId.Size;

                case LayoutUtf8 _:
                case LayoutBinary _:
                case LayoutVarInt _:
                case LayoutVarUInt _:

                    // Variable length types preceded by their varuint size take 1 byte for a size of 0.
                    return 1;

                case LayoutObject _:
                case LayoutArray _:

                    // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
                    return sizeof(LayoutCode);

                case LayoutTypedArray _:
                case LayoutTypedSet _:
                case LayoutTypedMap _:

                    // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
                    return sizeof(uint);

                case LayoutTuple _:

                    // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
                    return sizeof(LayoutCode) + (sizeof(LayoutCode) * typeArgs.Count);

                case LayoutTypedTuple _:
                case LayoutTagged _:
                case LayoutTagged2 _:

                    // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
                    int sum = 0;
                    foreach (TypeArgument arg in typeArgs)
                    {
                        sum += this.CountDefaultValue(arg.Type, arg.TypeArgs);
                    }

                    return sum;

                case LayoutNullable _:

                    // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
                    return 1 + this.CountDefaultValue(typeArgs[0].Type, typeArgs[0].TypeArgs);

                case LayoutUDT _:
                    Layout udt = this.resolver.Resolve(typeArgs.SchemaId);
                    return udt.Size + sizeof(LayoutCode);

                default:
                    Contract.Fail($"Not Implemented: {code}");
                    return 0;
            }
        }

        private int WriteDefaultValue(int offset, LayoutType code, TypeArgumentList typeArgs)
        {
            switch (code)
            {
                case LayoutNull _:
                    this.WriteSparseTypeCode(offset, code.LayoutCode);
                    return 1;

                case LayoutBoolean _:
                    this.WriteSparseTypeCode(offset, LayoutCode.BooleanFalse);
                    return 1;

                case LayoutInt8 _:
                    this.WriteInt8(offset, 0);
                    return LayoutType.Int8.Size;

                case LayoutInt16 _:
                    this.WriteInt16(offset, 0);
                    return LayoutType.Int16.Size;

                case LayoutInt32 _:
                    this.WriteInt32(offset, 0);
                    return LayoutType.Int32.Size;

                case LayoutInt64 _:
                    this.WriteInt64(offset, 0);
                    return LayoutType.Int64.Size;

                case LayoutUInt8 _:
                    this.WriteUInt8(offset, 0);
                    return LayoutType.UInt8.Size;

                case LayoutUInt16 _:
                    this.WriteUInt16(offset, 0);
                    return LayoutType.UInt16.Size;

                case LayoutUInt32 _:
                    this.WriteUInt32(offset, 0);
                    return LayoutType.UInt32.Size;

                case LayoutUInt64 _:
                    this.WriteUInt64(offset, 0);
                    return LayoutType.UInt64.Size;

                case LayoutFloat32 _:
                    this.WriteFloat32(offset, 0);
                    return LayoutType.Float32.Size;

                case LayoutFloat64 _:
                    this.WriteFloat64(offset, 0);
                    return LayoutType.Float64.Size;

                case LayoutFloat128 _:
                    this.WriteFloat128(offset, default);
                    return LayoutType.Float128.Size;

                case LayoutDecimal _:
                    this.WriteDecimal(offset, 0);
                    return LayoutType.Decimal.Size;

                case LayoutDateTime _:
                    this.WriteDateTime(offset, default);
                    return LayoutType.DateTime.Size;

                case LayoutUnixDateTime _:
                    this.WriteUnixDateTime(offset, default);
                    return LayoutType.UnixDateTime.Size;

                case LayoutGuid _:
                    this.WriteGuid(offset, default);
                    return LayoutType.Guid.Size;

                case LayoutMongoDbObjectId _:
                    this.WriteMongoDbObjectId(offset, default);
                    return LayoutType.MongoDbObjectId.Size;

                case LayoutUtf8 _:
                case LayoutBinary _:
                case LayoutVarInt _:
                case LayoutVarUInt _:

                    // Variable length types preceded by their varuint size take 1 byte for a size of 0.
                    return this.Write7BitEncodedUInt(offset, 0);

                case LayoutObject _:
                case LayoutArray _:

                    // Variable length sparse collection scopes take 1 byte for the end-of-scope terminator.
                    this.WriteSparseTypeCode(offset, LayoutCode.EndScope);
                    return sizeof(LayoutCode);

                case LayoutTypedArray _:
                case LayoutTypedSet _:
                case LayoutTypedMap _:

                    // Variable length typed collection scopes preceded by their scope size take sizeof(uint) for a size of 0.
                    this.WriteUInt32(offset, 0);
                    return sizeof(uint);

                case LayoutTuple _:

                    // Fixed arity sparse collections take 1 byte for end-of-scope plus a null for each element.
                    for (int i = 0; i < typeArgs.Count; i++)
                    {
                        this.WriteSparseTypeCode(offset, LayoutCode.Null);
                    }

                    this.WriteSparseTypeCode(offset, LayoutCode.EndScope);
                    return sizeof(LayoutCode) + (sizeof(LayoutCode) * typeArgs.Count);

                case LayoutTypedTuple _:
                case LayoutTagged _:
                case LayoutTagged2 _:

                    // Fixed arity typed collections take the sum of the default values of each element.  The scope size is implied by the arity.
                    int sum = 0;
                    foreach (TypeArgument arg in typeArgs)
                    {
                        sum += this.WriteDefaultValue(offset + sum, arg.Type, arg.TypeArgs);
                    }

                    return sum;

                case LayoutNullable _:

                    // Nullables take the default values of the value plus null.  The scope size is implied by the arity.
                    this.WriteInt8(offset, 0);
                    return 1 + this.WriteDefaultValue(offset + 1, typeArgs[0].Type, typeArgs[0].TypeArgs);

                case LayoutUDT _:

                    // Clear all presence bits.
                    Layout udt = this.resolver.Resolve(typeArgs.SchemaId);
                    this.buffer.Slice(offset, udt.Size).Fill(0);

                    // Write scope terminator.
                    this.WriteSparseTypeCode(offset + udt.Size, LayoutCode.EndScope);
                    return udt.Size + sizeof(LayoutCode);

                default:
                    Contract.Fail($"Not Implemented: {code}");
                    return 0;
            }
        }

        /// <summary>
        /// <see cref="UniqueIndexItem" /> represents a single item within a set/map scope that needs
        /// to be indexed.
        /// </summary>
        /// <remarks>
        /// This structure is used when rebuilding a set/map index during row streaming via
        /// <see cref="IO.RowWriter" />.
        /// <para />
        /// Each item encodes its offsets and length within the row.
        /// </remarks>
        [DebuggerDisplay("{MetaOffset}/{ValueOffset}")]
        private struct UniqueIndexItem
        {
            /// <summary>The layout code of the value.</summary>
            public LayoutCode Code;

            /// <summary>
            /// If existing, the offset to the metadata of the existing field, otherwise the location to
            /// insert a new field.
            /// </summary>
            public int MetaOffset;

            /// <summary>If existing, the offset to the value of the existing field, otherwise undefined.</summary>
            public int ValueOffset;

            /// <summary>Size of the target element.</summary>
            public int Size;
        }
    }
}
