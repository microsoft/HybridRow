// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

#pragma warning disable SA1402 // FileMayOnlyContainASingleType
#pragma warning disable SA1201 // Elements should appear in the correct order
#pragma warning disable SA1401 // Fields should be private
#pragma warning disable CA1040 // Avoid empty interfaces
#pragma warning disable CA1051 // Do not declare visible instance fields

namespace Microsoft.Azure.Cosmos.Serialization.HybridRow.Layouts
{
    using System;
    using System.Buffers;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.Runtime.CompilerServices;
    using Microsoft.Azure.Cosmos.Core;
    using Microsoft.Azure.Cosmos.Core.Utf8;

    /// <summary>The abstract base class for typed hybrid row field descriptors.</summary>
    /// <remarks><see cref="LayoutType" /> is immutable.</remarks>
    [DebuggerDisplay("{" + nameof(LayoutType.Name) + "}")]
    public abstract class LayoutType : ILayoutType
    {
        /// <summary>The number of bits in a single byte on the current architecture.</summary>
        internal const int BitsPerByte = 8;

        private static readonly LayoutType[] CodeIndex = new LayoutType[(int)LayoutCode.EndScope + 1];

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutInt8 Int8 = new LayoutInt8();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutInt16 Int16 = new LayoutInt16();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutInt32 Int32 = new LayoutInt32();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutInt64 Int64 = new LayoutInt64();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUInt8 UInt8 = new LayoutUInt8();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUInt16 UInt16 = new LayoutUInt16();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUInt32 UInt32 = new LayoutUInt32();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUInt64 UInt64 = new LayoutUInt64();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutVarInt VarInt = new LayoutVarInt();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutVarUInt VarUInt = new LayoutVarUInt();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutFloat32 Float32 = new LayoutFloat32();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutFloat64 Float64 = new LayoutFloat64();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutFloat128 Float128 = new LayoutFloat128();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutDecimal Decimal = new LayoutDecimal();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutNull Null = new LayoutNull();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutBoolean Boolean = new LayoutBoolean(true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutBoolean BooleanFalse = new LayoutBoolean(false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutDateTime DateTime = new LayoutDateTime();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUnixDateTime UnixDateTime = new LayoutUnixDateTime();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutGuid Guid = new LayoutGuid();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutMongoDbObjectId MongoDbObjectId = new LayoutMongoDbObjectId();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUtf8 Utf8 = new LayoutUtf8();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutBinary Binary = new LayoutBinary();

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutObject Object = new LayoutObject(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutObject ImmutableObject = new LayoutObject(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutArray Array = new LayoutArray(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutArray ImmutableArray = new LayoutArray(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedArray TypedArray = new LayoutTypedArray(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedArray ImmutableTypedArray = new LayoutTypedArray(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedSet TypedSet = new LayoutTypedSet(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedSet ImmutableTypedSet = new LayoutTypedSet(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedMap TypedMap = new LayoutTypedMap(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedMap ImmutableTypedMap = new LayoutTypedMap(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTuple Tuple = new LayoutTuple(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTuple ImmutableTuple = new LayoutTuple(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedTuple TypedTuple = new LayoutTypedTuple(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTypedTuple ImmutableTypedTuple = new LayoutTypedTuple(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTagged Tagged = new LayoutTagged(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTagged ImmutableTagged = new LayoutTagged(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTagged2 Tagged2 = new LayoutTagged2(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutTagged2 ImmutableTagged2 = new LayoutTagged2(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutNullable Nullable = new LayoutNullable(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutNullable ImmutableNullable = new LayoutNullable(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUDT UDT = new LayoutUDT(immutable: false);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        public static readonly LayoutUDT ImmutableUDT = new LayoutUDT(immutable: true);

        [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Type is immutable.")]
        internal static readonly LayoutEndScope EndScope = new LayoutEndScope();

        /// <summary>The physical layout code used to represent the type within the serialization.</summary>
        public readonly LayoutCode LayoutCode;

        /// <summary>Initializes a new instance of the <see cref="LayoutType" /> class.</summary>
        internal LayoutType(LayoutCode code, bool immutable, int size)
        {
            this.LayoutCode = code;
            this.Immutable = immutable;
            this.Size = size;
            LayoutType.CodeIndex[(int)code] = this;
        }

        /// <summary>Human readable name of the type.</summary>
        public abstract string Name { get; }

        /// <summary>True if this type is always fixed length.</summary>
        public abstract bool IsFixed { get; }

        /// <summary>True if this type can be used in the variable-length segment.</summary>
        public bool AllowVariable => !this.IsFixed;

        /// <summary>If true, this edit's nested fields cannot be updated individually.</summary>
        /// <remarks>The entire edit can still be replaced.</remarks>
        public readonly bool Immutable;

        /// <summary>If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.</summary>
        public readonly int Size;

        /// <summary>True if this type is a boolean.</summary>
        public virtual bool IsBool => false;

        /// <summary>True if this type is a literal null.</summary>
        public virtual bool IsNull => false;

        /// <summary>True if this type is a variable-length encoded integer type (either signed or unsigned).</summary>
        public virtual bool IsVarint => false;

        /// <summary>The physical layout type of the field cast to the specified type.</summary>
        [DebuggerHidden]
        public T TypeAs<T>()
            where T : ILayoutType
        {
            return (T)(ILayoutType)this;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal static LayoutType FromCode(LayoutCode code)
        {
            LayoutType type = LayoutType.CodeIndex[(int)code];
#if DEBUG
            if (type == null)
            {
                Contract.Fail($"Not Implemented: {code}");
            }
#endif

            return type;
        }

        /// <summary>Helper for preparing the delete of a sparse field.</summary>
        /// <param name="b">The row to delete from.</param>
        /// <param name="edit">The parent edit containing the field to delete.</param>
        /// <param name="code">The expected type of the field.</param>
        /// <returns>Success if the delete is permitted, the error code otherwise.</returns>
        internal static Result PrepareSparseDelete(ref RowBuffer b, ref RowCursor edit, LayoutCode code)
        {
            if (edit.scopeType.IsFixedArity)
            {
                return Result.TypeConstraint;
            }

            if (edit.immutable)
            {
                return Result.InsufficientPermissions;
            }

            if (edit.exists && LayoutCodeTraits.Canonicalize(edit.cellType.LayoutCode) != code)
            {
                return Result.TypeMismatch;
            }

            return Result.Success;
        }

        /// <summary>Helper for preparing the write of a sparse field.</summary>
        /// <param name="b">The row to write to.</param>
        /// <param name="edit">The cursor for the field to write.</param>
        /// <param name="typeArg">The (optional) type constraints.</param>
        /// <param name="options">The write options.</param>
        /// <returns>Success if the write is permitted, the error code otherwise.</returns>
        internal static Result PrepareSparseWrite(ref RowBuffer b, ref RowCursor edit, TypeArgument typeArg, UpdateOptions options)
        {
            if (edit.immutable || (edit.scopeType.IsUniqueScope && !edit.deferUniqueIndex))
            {
                return Result.InsufficientPermissions;
            }

            if (edit.scopeType.IsFixedArity && !(edit.scopeType is LayoutNullable))
            {
                if ((edit.index < edit.scopeTypeArgs.Count) && !typeArg.Equals(edit.scopeTypeArgs[edit.index]))
                {
                    return Result.TypeConstraint;
                }
            }
            else if (edit.scopeType is LayoutTypedMap)
            {
                if (!((typeArg.Type is LayoutTypedTuple) && typeArg.TypeArgs.Equals(edit.scopeTypeArgs)))
                {
                    return Result.TypeConstraint;
                }
            }
            else if (edit.scopeType.IsTypedScope && !typeArg.Equals(edit.scopeTypeArgs[0]))
            {
                return Result.TypeConstraint;
            }

            if ((options == UpdateOptions.InsertAt) && edit.scopeType.IsFixedArity)
            {
                return Result.TypeConstraint;
            }

            if ((options == UpdateOptions.InsertAt) && !edit.scopeType.IsFixedArity)
            {
                edit.exists = false; // InsertAt never overwrites an existing item.
            }

            if ((options == UpdateOptions.Update) && (!edit.exists))
            {
                return Result.NotFound;
            }

            if ((options == UpdateOptions.Insert) && edit.exists)
            {
                return Result.Exists;
            }

            return Result.Success;
        }

        /// <summary>Helper for preparing the read of a sparse field.</summary>
        /// <param name="b">The row to read from.</param>
        /// <param name="edit">The parent edit containing the field to read.</param>
        /// <param name="code">The expected type of the field.</param>
        /// <returns>Success if the read is permitted, the error code otherwise.</returns>
        internal static Result PrepareSparseRead(ref RowBuffer b, ref RowCursor edit, LayoutCode code)
        {
            if (!edit.exists)
            {
                return Result.NotFound;
            }

            if (LayoutCodeTraits.Canonicalize(edit.cellType.LayoutCode) != code)
            {
                return Result.TypeMismatch;
            }

            return Result.Success;
        }

        /// <summary>Helper for preparing the move of a sparse field into an existing restricted edit.</summary>
        /// <param name="b">The row to read from.</param>
        /// <param name="destinationScope">The parent set edit into which the field should be moved.</param>
        /// <param name="destinationCode">The expected type of the edit moving within.</param>
        /// <param name="elementType">The expected type of the elements within the edit.</param>
        /// <param name="srcEdit">The field to be moved.</param>
        /// <param name="options">The move options.</param>
        /// <param name="dstEdit">If successful, a prepared insertion cursor for the destination.</param>
        /// <returns>Success if the move is permitted, the error code otherwise.</returns>
        /// <remarks>The source field is delete if the move prepare fails with a destination error.</remarks>
        internal static Result PrepareSparseMove(
            ref RowBuffer b,
            ref RowCursor destinationScope,
            LayoutScope destinationCode,
            TypeArgument elementType,
            ref RowCursor srcEdit,
            UpdateOptions options,
            out RowCursor dstEdit)
        {
            Contract.Requires(destinationScope.scopeType == destinationCode);
            Contract.Requires(destinationScope.index == 0, "Can only insert into a edit at the root");

            // Prepare the delete of the source.
            Result result = LayoutType.PrepareSparseDelete(ref b, ref srcEdit, elementType.Type.LayoutCode);
            if (result != Result.Success)
            {
                dstEdit = default;
                return result;
            }

            if (!srcEdit.exists)
            {
                dstEdit = default;
                return Result.NotFound;
            }

            if (destinationScope.immutable)
            {
                b.DeleteSparse(ref srcEdit);
                dstEdit = default;
                return Result.InsufficientPermissions;
            }

            if (!srcEdit.cellTypeArgs.Equals(elementType.TypeArgs))
            {
                b.DeleteSparse(ref srcEdit);
                dstEdit = default;
                return Result.TypeConstraint;
            }

            if (options == UpdateOptions.InsertAt)
            {
                b.DeleteSparse(ref srcEdit);
                dstEdit = default;
                return Result.TypeConstraint;
            }

            // Prepare the insertion at the destination.
            dstEdit = b.PrepareSparseMove(ref destinationScope, ref srcEdit);
            if ((options == UpdateOptions.Update) && (!dstEdit.exists))
            {
                b.DeleteSparse(ref srcEdit);
                dstEdit = default;
                return Result.NotFound;
            }

            if ((options == UpdateOptions.Insert) && dstEdit.exists)
            {
                b.DeleteSparse(ref srcEdit);
                dstEdit = default;
                return Result.Exists;
            }

            return Result.Success;
        }

        internal virtual int CountTypeArgument(TypeArgumentList value)
        {
            return sizeof(LayoutCode);
        }

        internal virtual int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            return sizeof(LayoutCode);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        internal static TypeArgument ReadTypeArgument(ref RowBuffer row, int offset, out int lenInBytes)
        {
            LayoutType itemCode = row.ReadSparseTypeCode(offset);
            TypeArgumentList itemTypeArgs = itemCode.ReadTypeArgumentList(ref row, offset + sizeof(LayoutCode), out int argsLenInBytes);
            lenInBytes = sizeof(LayoutCode) + argsLenInBytes;
            return new TypeArgument(itemCode, itemTypeArgs);
        }

        internal virtual TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            lenInBytes = 0;
            return TypeArgumentList.Empty;
        }
    }

    /// <summary>Marker interface for layout types.</summary>
    public interface ILayoutType
    {
    }

    /// <summary>
    /// Describes the physical byte layout of a hybrid row field of a specific physical type
    /// <typeparamref name="T" />.
    /// </summary>
    /// <remarks>
    /// <see cref="LayoutType{T}" /> is an immutable, stateless, helper class.  It provides
    /// methods for manipulating hybrid row fields of a particular type, and properties that describe the
    /// layout of fields of that type.
    /// <para />
    /// <see cref="LayoutType{T}" /> is immutable.
    /// </remarks>
    public abstract class LayoutType<T> : LayoutType
    {
        private readonly TypeArgument typeArg;

        internal LayoutType(LayoutCode code, int size)
            : base(code, false, size)
        {
            this.typeArg = new TypeArgument(this);
        }

        internal TypeArgument TypeArg
        {
            [MethodImpl(MethodImplOptions.AggressiveInlining)]
            get => this.typeArg;
        }

        public Result HasValue(ref RowBuffer b, ref RowCursor scope, LayoutColumn col)
        {
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                return Result.NotFound;
            }

            return Result.Success;
        }

        public abstract Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, T value);

        public abstract Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out T value);

        public Result DeleteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            if (col.NullBit.IsInvalid)
            {
                // Cannot delete a non-nullable fixed column.
                return Result.TypeMismatch;
            }

            b.UnsetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        /// <summary>Delete an existing value.</summary>
        /// <remarks>
        /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
        /// a decrease in required space.  If no value exists this operation is a no-op.
        /// </remarks>
        public Result DeleteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            if (exists)
            {
                int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
                b.DeleteVariable(varOffset, this.IsVarint);
                b.UnsetBit(scope.start, col.NullBit);
            }

            return Result.Success;
        }

        public virtual Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, T value)
        {
            return Result.Failure;
        }

        public virtual Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out T value)
        {
            value = default;
            return Result.Failure;
        }

        /// <summary>Delete an existing value.</summary>
        /// <remarks>
        /// If a value exists, then it is removed.  The remainder of the row is resized to accomodate
        /// a decrease in required space.  If no value exists this operation is a no-op.
        /// </remarks>
        public Result DeleteSparse(ref RowBuffer b, ref RowCursor edit)
        {
            Result result = LayoutType.PrepareSparseDelete(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                return result;
            }

            b.DeleteSparse(ref edit);
            return Result.Success;
        }

        public abstract Result WriteSparse(ref RowBuffer b, ref RowCursor edit, T value, UpdateOptions options = UpdateOptions.Upsert);

        public abstract Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out T value);
    }

    /// <summary>
    /// An optional interface that indicates a <see cref="LayoutType{T}" /> can also write using a
    /// <see cref="Utf8Span" />.
    /// </summary>
    public interface ILayoutUtf8SpanWritable : ILayoutType
    {
        Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Utf8Span value);

        Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Utf8Span value);

        Result WriteSparse(ref RowBuffer b, ref RowCursor edit, Utf8Span value, UpdateOptions options = UpdateOptions.Upsert);
    }

    /// <summary>
    /// An optional interface that indicates a <see cref="LayoutType{T}" /> can also write using a
    /// <see cref="ReadOnlySpan{T}" />.
    /// </summary>
    /// <typeparam name="TElement">The sub-element type to be written.</typeparam>
    public interface ILayoutSpanWritable<TElement> : ILayoutType
    {
        Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySpan<TElement> value);

        Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySpan<TElement> value);

        Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySpan<TElement> value, UpdateOptions options = UpdateOptions.Upsert);
    }

    /// <summary>
    /// An optional interface that indicates a <see cref="LayoutType{T}" /> can also write using a
    /// <see cref="ReadOnlySequence{T}" />.
    /// </summary>
    /// <typeparam name="TElement">The sub-element type to be written.</typeparam>
    public interface ILayoutSequenceWritable<TElement> : ILayoutType
    {
        Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySequence<TElement> value);

        Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySequence<TElement> value);

        Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ReadOnlySequence<TElement> value, UpdateOptions options = UpdateOptions.Upsert);
    }

    /// <summary>
    /// An optional interface that indicates a <see cref="LayoutType{T}" /> can also read using a
    /// <see cref="Utf8Span" />.
    /// </summary>
    public interface ILayoutUtf8SpanReadable : ILayoutType
    {
        Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Utf8Span value);

        Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Utf8Span value);

        Result ReadSparse(ref RowBuffer b, ref RowCursor scope, out Utf8Span value);
    }

    /// <summary>
    /// An optional interface that indicates a <see cref="LayoutType{T}" /> can also read using a
    /// <see cref="ReadOnlySpan{T}" />.
    /// </summary>
    /// <typeparam name="TElement">The sub-element type to be written.</typeparam>
    public interface ILayoutSpanReadable<TElement> : ILayoutType
    {
        Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ReadOnlySpan<TElement> value);

        Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ReadOnlySpan<TElement> value);

        Result ReadSparse(ref RowBuffer b, ref RowCursor scope, out ReadOnlySpan<TElement> value);
    }

    public sealed class LayoutInt8 : LayoutType<sbyte>
    {
        internal LayoutInt8()
            : base(LayoutCode.Int8, size: sizeof(sbyte))
        {
        }

        public override string Name => "int8";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, sbyte value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteInt8(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out sbyte value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadInt8(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, sbyte value, UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseInt8(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out sbyte value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseInt8(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutInt16 : LayoutType<short>
    {
        internal LayoutInt16()
            : base(LayoutCode.Int16, size: sizeof(short))
        {
        }

        public override string Name => "int16";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, short value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteInt16(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out short value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadInt16(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            short value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseInt16(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out short value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseInt16(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutInt32 : LayoutType<int>
    {
        internal LayoutInt32()
            : base(LayoutCode.Int32, size: sizeof(int))
        {
        }

        public override string Name => "int32";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, int value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteInt32(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out int value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadInt32(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            int value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseInt32(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out int value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseInt32(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutInt64 : LayoutType<long>
    {
        internal LayoutInt64()
            : base(LayoutCode.Int64, size: sizeof(long))
        {
        }

        public override string Name => "int64";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, long value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteInt64(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out long value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadInt64(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            long value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseInt64(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out long value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseInt64(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUInt8 : LayoutType<byte>
    {
        internal LayoutUInt8()
            : base(LayoutCode.UInt8, size: sizeof(byte))
        {
        }

        public override string Name => "uint8";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteUInt8(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out byte value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadUInt8(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            byte value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseUInt8(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out byte value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseUInt8(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUInt16 : LayoutType<ushort>
    {
        internal LayoutUInt16()
            : base(LayoutCode.UInt16, size: sizeof(ushort))
        {
        }

        public override string Name => "uint16";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ushort value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteUInt16(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ushort value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadUInt16(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            ushort value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseUInt16(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ushort value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseUInt16(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUInt32 : LayoutType<uint>
    {
        internal LayoutUInt32()
            : base(LayoutCode.UInt32, size: sizeof(uint))
        {
        }

        public override string Name => "uint32";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, uint value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteUInt32(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out uint value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadUInt32(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            uint value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseUInt32(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out uint value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseUInt32(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUInt64 : LayoutType<ulong>
    {
        internal LayoutUInt64()
            : base(LayoutCode.UInt64, size: sizeof(ulong))
        {
        }

        public override string Name => "uint64";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteUInt64(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ulong value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadUInt64(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            ulong value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseUInt64(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ulong value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseUInt64(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutFloat32 : LayoutType<float>
    {
        internal LayoutFloat32()
            : base(LayoutCode.Float32, size: sizeof(float))
        {
        }

        public override string Name => "float32";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, float value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFloat32(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out float value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadFloat32(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            float value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseFloat32(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out float value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseFloat32(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutFloat64 : LayoutType<double>
    {
        internal LayoutFloat64()
            : base(LayoutCode.Float64, size: sizeof(double))
        {
        }

        public override string Name => "float64";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, double value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFloat64(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out double value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadFloat64(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            double value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseFloat64(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out double value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseFloat64(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutFloat128 : LayoutType<Float128>
    {
        internal LayoutFloat128()
            : base(LayoutCode.Float128, size: HybridRow.Float128.Size)
        {
        }

        public override string Name => "float128";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Float128 value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFloat128(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Float128 value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadFloat128(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            Float128 value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseFloat128(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out Float128 value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseFloat128(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutDecimal : LayoutType<decimal>
    {
        internal LayoutDecimal()
            : base(LayoutCode.Decimal, size: sizeof(decimal))
        {
        }

        public override string Name => "decimal";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, decimal value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteDecimal(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out decimal value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadDecimal(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            decimal value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseDecimal(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out decimal value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseDecimal(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutBoolean : LayoutType<bool>
    {
        internal LayoutBoolean(bool value)
            : base(value ? LayoutCode.Boolean : LayoutCode.BooleanFalse, size: 0)
        {
        }

        public override string Name => "bool";

        public override bool IsFixed => true;

        public override bool IsBool => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, bool value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            if (value)
            {
                b.SetBit(scope.start, col.BoolBit);
            }
            else
            {
                b.UnsetBit(scope.start, col.BoolBit);
            }

            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out bool value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadBit(scope.start, col.BoolBit);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            bool value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseBool(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out bool value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseBool(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutNull : LayoutType<NullValue>
    {
        internal LayoutNull()
            : base(LayoutCode.Null, size: 0)
        {
        }

        public override string Name => "null";

        public override bool IsFixed => true;

        public override bool IsNull => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, NullValue value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out NullValue value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            value = NullValue.Default;
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                return Result.NotFound;
            }

            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            NullValue value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseNull(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out NullValue value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseNull(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutDateTime : LayoutType<DateTime>
    {
        internal LayoutDateTime()
            : base(LayoutCode.DateTime, size: 8)
        {
        }

        public override string Name => "datetime";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, DateTime value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteDateTime(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out DateTime value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadDateTime(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            DateTime value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseDateTime(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out DateTime value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseDateTime(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUnixDateTime : LayoutType<UnixDateTime>
    {
        internal LayoutUnixDateTime()
            : base(LayoutCode.UnixDateTime, size: Microsoft.Azure.Cosmos.Serialization.HybridRow.UnixDateTime.Size)
        {
        }

        public override string Name => "unixdatetime";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, UnixDateTime value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteUnixDateTime(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out UnixDateTime value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadUnixDateTime(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            UnixDateTime value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseUnixDateTime(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out UnixDateTime value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseUnixDateTime(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutGuid : LayoutType<Guid>
    {
        internal LayoutGuid()
            : base(LayoutCode.Guid, size: 16)
        {
        }

        public override string Name => "guid";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Guid value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteGuid(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Guid value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadGuid(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            Guid value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseGuid(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out Guid value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseGuid(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutMongoDbObjectId : LayoutType<MongoDbObjectId>
    {
        internal LayoutMongoDbObjectId()
            : base(LayoutCode.MongoDbObjectId, size: Microsoft.Azure.Cosmos.Serialization.HybridRow.MongoDbObjectId.Size)
        {
        }

        // ReSharper disable once StringLiteralTypo
        public override string Name => "mongodbobjectid";

        public override bool IsFixed => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, MongoDbObjectId value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteMongoDbObjectId(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out MongoDbObjectId value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadMongoDbObjectId(scope.start + col.Offset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            MongoDbObjectId value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseMongoDbObjectId(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out MongoDbObjectId value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseMongoDbObjectId(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutUtf8 : LayoutType<string>, ILayoutUtf8SpanWritable, ILayoutUtf8SpanReadable
    {
        internal LayoutUtf8()
            : base(LayoutCode.Utf8, size: 0)
        {
        }

        public override string Name => "utf8";

        public override bool IsFixed => false;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, string value)
        {
            Contract.Requires(value != null);
            return this.WriteFixed(ref b, ref scope, col, Utf8Span.TranscodeUtf16(value));
        }

        public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Utf8Span value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            Contract.Requires(col.Size >= 0);
            Contract.Requires(value.Length == col.Size);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFixedString(scope.start + col.Offset, value);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out string value)
        {
            Result r = this.ReadFixed(ref b, ref scope, col, out Utf8Span span);
            value = (r == Result.Success) ? span.ToString() : default;
            return r;
        }

        public Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Utf8Span value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            Contract.Requires(col.Size >= 0);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            value = b.ReadFixedString(scope.start + col.Offset, col.Size);
            return Result.Success;
        }

        public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, string value)
        {
            Contract.Requires(value != null);
            return this.WriteVariable(ref b, ref scope, col, Utf8Span.TranscodeUtf16(value));
        }

        public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, Utf8Span value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            int length = value.Length;
            if ((col.Size > 0) && (length > col.Size))
            {
                return Result.TooBig;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            b.WriteVariableString(varOffset, value, exists, out int shift);
            b.SetBit(scope.start, col.NullBit);
            scope.metaOffset += shift;
            scope.valueOffset += shift;
            return Result.Success;
        }

        public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out string value)
        {
            Result r = this.ReadVariable(ref b, ref scope, col, out Utf8Span span);
            value = (r == Result.Success) ? span.ToString() : default;
            return r;
        }

        public Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out Utf8Span value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            value = b.ReadVariableString(varOffset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            string value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Contract.Requires(value != null);
            return this.WriteSparse(ref b, ref edit, Utf8Span.TranscodeUtf16(value), options);
        }

        public Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            Utf8Span value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseString(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out string value)
        {
            Result r = this.ReadSparse(ref b, ref edit, out Utf8Span span);
            value = (r == Result.Success) ? span.ToString() : default;
            return r;
        }

        public Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out Utf8Span value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseString(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutBinary : LayoutType<byte[]>, ILayoutSpanWritable<byte>, ILayoutSpanReadable<byte>, ILayoutSequenceWritable<byte>
    {
        internal LayoutBinary()
            : base(LayoutCode.Binary, size: 0)
        {
        }

        public override string Name => "binary";

        public override bool IsFixed => false;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte[] value)
        {
            Contract.Requires(value != null);
            return this.WriteFixed(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        }

        public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySpan<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            Contract.Requires(col.Size >= 0);
            Contract.Requires(value.Length == col.Size);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFixedBinary(scope.start + col.Offset, value, col.Size);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySequence<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            Contract.Requires(col.Size >= 0);
            Contract.Requires(value.Length == col.Size);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            b.WriteFixedBinary(scope.start + col.Offset, value, col.Size);
            b.SetBit(scope.start, col.NullBit);
            return Result.Success;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out byte[] value)
        {
            Result r = this.ReadFixed(ref b, ref scope, col, out ReadOnlySpan<byte> span);
            value = (r == Result.Success) ? span.ToArray() : default;
            return r;
        }

        public Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ReadOnlySpan<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            Contract.Requires(col.Size >= 0);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default(byte[]);
                return Result.NotFound;
            }

            value = b.ReadFixedBinary(scope.start + col.Offset, col.Size);
            return Result.Success;
        }

        public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, byte[] value)
        {
            Contract.Requires(value != null);
            return this.WriteVariable(ref b, ref scope, col, new ReadOnlySpan<byte>(value));
        }

        public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySpan<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            int length = value.Length;
            if ((col.Size > 0) && (length > col.Size))
            {
                return Result.TooBig;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            b.WriteVariableBinary(varOffset, value, exists, out int shift);
            b.SetBit(scope.start, col.NullBit);
            scope.metaOffset += shift;
            scope.valueOffset += shift;
            return Result.Success;
        }

        public Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ReadOnlySequence<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            int length = (int)value.Length;
            if ((col.Size > 0) && (length > col.Size))
            {
                return Result.TooBig;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            b.WriteVariableBinary(varOffset, value, exists, out int shift);
            b.SetBit(scope.start, col.NullBit);
            scope.metaOffset += shift;
            scope.valueOffset += shift;
            return Result.Success;
        }

        public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out byte[] value)
        {
            Result r = this.ReadVariable(ref b, ref scope, col, out ReadOnlySpan<byte> span);
            value = (r == Result.Success) ? span.ToArray() : default;
            return r;
        }

        public Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ReadOnlySpan<byte> value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default(byte[]);
                return Result.NotFound;
            }

            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            value = b.ReadVariableBinary(varOffset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            byte[] value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Contract.Requires(value != null);
            return this.WriteSparse(ref b, ref edit, new ReadOnlySpan<byte>(value), options);
        }

        public Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            ReadOnlySpan<byte> value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseBinary(ref edit, value, options);
            return Result.Success;
        }

        public Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            ReadOnlySequence<byte> value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseBinary(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out byte[] value)
        {
            Result r = this.ReadSparse(ref b, ref edit, out ReadOnlySpan<byte> span);
            value = (r == Result.Success) ? span.ToArray() : default;
            return r;
        }

        public Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ReadOnlySpan<byte> value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default(byte[]);
                return result;
            }

            value = b.ReadSparseBinary(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutVarInt : LayoutType<long>
    {
        internal LayoutVarInt()
            : base(LayoutCode.VarInt, size: 0)
        {
        }

        public override string Name => "varint";

        public override bool IsFixed => false;

        public override bool IsVarint => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, long value)
        {
            Contract.Fail("Not Implemented");
            return Result.Failure;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out long value)
        {
            Contract.Fail("Not Implemented");
            value = default;
            return Result.Failure;
        }

        public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, long value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            b.WriteVariableInt(varOffset, value, exists, out int shift);
            b.SetBit(scope.start, col.NullBit);
            scope.metaOffset += shift;
            scope.valueOffset += shift;
            return Result.Success;
        }

        public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out long value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            value = b.ReadVariableInt(varOffset);
            return Result.Success;
        }

        public override Result WriteSparse(
            ref RowBuffer b,
            ref RowCursor edit,
            long value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseVarInt(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out long value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseVarInt(ref edit);
            return Result.Success;
        }
    }

    public sealed class LayoutVarUInt : LayoutType<ulong>
    {
        internal LayoutVarUInt()
            : base(LayoutCode.VarUInt, size: 0)
        {
        }

        public override string Name => "varuint";

        public override bool IsFixed => false;

        public override bool IsVarint => true;

        public override Result WriteFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong value)
        {
            Contract.Fail("Not Implemented");
            return Result.Failure;
        }

        public override Result ReadFixed(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ulong value)
        {
            Contract.Fail("Not Implemented");
            value = default(long);
            return Result.Failure;
        }

        public override Result WriteVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, ulong value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (scope.immutable)
            {
                return Result.InsufficientPermissions;
            }

            bool exists = b.ReadBit(scope.start, col.NullBit);
            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            b.WriteVariableUInt(varOffset, value, exists, out int shift);
            b.SetBit(scope.start, col.NullBit);
            scope.metaOffset += shift;
            scope.valueOffset += shift;
            return Result.Success;
        }

        public override Result ReadVariable(ref RowBuffer b, ref RowCursor scope, LayoutColumn col, out ulong value)
        {
            Contract.Requires(scope.scopeType is LayoutUDT);
            if (!b.ReadBit(scope.start, col.NullBit))
            {
                value = default;
                return Result.NotFound;
            }

            int varOffset = b.ComputeVariableValueOffset(scope.layout, scope.start, col.Offset);
            value = b.ReadVariableUInt(varOffset);
            return Result.Success;
        }

        public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, ulong value, UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                return result;
            }

            b.WriteSparseVarUInt(ref edit, value, options);
            return Result.Success;
        }

        public override Result ReadSparse(ref RowBuffer b, ref RowCursor edit, out ulong value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.ReadSparseVarUInt(ref edit);
            return Result.Success;
        }
    }

    public abstract class LayoutScope : LayoutType
    {
        /// <summary>A function to write content into a <see cref="RowBuffer" />.</summary>
        /// <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
        /// <param name="b">The row to write to.</param>
        /// <param name="scope">The type of the scope to write into.</param>
        /// <param name="context">A context value provided by the caller.</param>
        /// <returns>The result.</returns>
        public delegate Result WriterFunc<in TContext>(ref RowBuffer b, ref RowCursor scope, TContext context);

        protected LayoutScope(
            LayoutCode code,
            bool immutable,
            bool isSizedScope,
            bool isIndexedScope,
            bool isFixedArity,
            bool isUniqueScope,
            bool isTypedScope)
            : base(code, immutable, size: 0)
        {
            this.IsSizedScope = isSizedScope;
            this.IsIndexedScope = isIndexedScope;
            this.IsFixedArity = isFixedArity;
            this.IsUniqueScope = isUniqueScope;
            this.IsTypedScope = isTypedScope;
        }

        public sealed override bool IsFixed => false;

        public Result ReadScope(ref RowBuffer b, ref RowCursor edit, out RowCursor value)
        {
            Result result = LayoutType.PrepareSparseRead(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            value = b.SparseIteratorReadScope(ref edit, this.Immutable || edit.immutable || edit.scopeType.IsUniqueScope);
            return Result.Success;
        }

        public abstract Result WriteScope(
            ref RowBuffer b,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert);

        public virtual Result WriteScope<TContext>(
            ref RowBuffer b,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            TContext context,
            WriterFunc<TContext> func,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result r = this.WriteScope(ref b, ref scope, typeArgs, out RowCursor childScope, options);
            if (r != Result.Success)
            {
                return r;
            }

            r = func?.Invoke(ref b, ref childScope, context) ?? Result.Success;
            if (r != Result.Success)
            {
                this.DeleteScope(ref b, ref scope);
                return r;
            }
           
            scope.Skip(ref b, ref childScope);
            return Result.Success;
        }

        public Result DeleteScope(ref RowBuffer b, ref RowCursor edit)
        {
            Result result = LayoutType.PrepareSparseDelete(ref b, ref edit, this.LayoutCode);
            if (result != Result.Success)
            {
                return result;
            }

            b.DeleteSparse(ref edit);
            return Result.Success;
        }

        /// <summary>Returns true if the scope's elements cannot be updated directly.</summary>
        internal readonly bool IsUniqueScope;

        /// <summary>Returns true if this is an indexed scope.</summary>
        internal readonly bool IsIndexedScope;

        /// <summary>Returns true if this is a sized scope.</summary>
        internal readonly bool IsSizedScope;

        /// <summary>Returns true if this is a fixed arity scope.</summary>
        internal readonly bool IsFixedArity;

        /// <summary>Returns true if this is a typed scope.</summary>
        internal readonly bool IsTypedScope;

        /// <summary>
        /// Returns true if writing an item in the specified typed scope would elide the type code
        /// because it is implied by the type arguments.
        /// </summary>
        /// <param name="edit"></param>
        /// <returns>True if the type code is implied (not written), false otherwise.</returns>
        internal virtual bool HasImplicitTypeCode(ref RowCursor edit)
        {
            return false;
        }

        internal virtual void SetImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Fail("No implicit type codes.");
        }

        internal virtual void ReadSparsePath(ref RowBuffer row, ref RowCursor edit)
        {
            edit.pathToken = row.ReadSparsePathLen(edit.layout, edit.valueOffset, out int pathLenInBytes, out edit.pathOffset);
            edit.valueOffset += pathLenInBytes;
        }
    }

    public sealed class LayoutEndScope : LayoutScope
    {
        public LayoutEndScope()
            : base(LayoutCode.EndScope, false, isSizedScope: false, isIndexedScope: false, isFixedArity: false, isUniqueScope: false, isTypedScope: false)
        {
        }

        public override string Name => "end";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Contract.Fail("Cannot write an EndScope directly");
            value = default;
            return Result.Failure;
        }
    }

    public abstract class LayoutPropertyScope : LayoutScope
    {
        protected LayoutPropertyScope(LayoutCode code, bool immutable)
            : base(code, immutable, isSizedScope: false, isIndexedScope: false, isFixedArity: false, isUniqueScope: false, isTypedScope: false)
        {
        }
    }

    public sealed class LayoutObject : LayoutPropertyScope
    {
        internal LayoutObject(bool immutable)
            : base(immutable ? LayoutCode.ImmutableObjectScope : LayoutCode.ObjectScope, immutable)
        {
            this.TypeArg = new TypeArgument(this);
        }

        public override string Name => this.Immutable ? "im_object" : "object";

        internal TypeArgument TypeArg { get; }

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteSparseObject(ref edit, this, options, out value);
            return Result.Success;
        }
    }

    public sealed class LayoutUDT : LayoutPropertyScope
    {
        internal LayoutUDT(bool immutable)
            : base(immutable ? LayoutCode.ImmutableSchema : LayoutCode.Schema, immutable)
        {
        }

        public override string Name => this.Immutable ? "im_udt" : "udt";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Layout udt = b.Resolver.Resolve(typeArgs.SchemaId);
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteSparseUDT(ref edit, this, udt, options, out value);
            return Result.Success;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            return sizeof(LayoutCode) + SchemaId.Size;
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            row.WriteSchemaId(offset + sizeof(LayoutCode), value.SchemaId);
            return sizeof(LayoutCode) + SchemaId.Size;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            SchemaId schemaId = row.ReadSchemaId(offset);
            lenInBytes = SchemaId.Size;
            return new TypeArgumentList(schemaId);
        }
    }

    public abstract class LayoutIndexedScope : LayoutScope
    {
        protected LayoutIndexedScope(LayoutCode code, bool immutable, bool isSizedScope, bool isFixedArity, bool isUniqueScope, bool isTypedScope)
            : base(code, immutable, isSizedScope, isIndexedScope: true, isFixedArity: isFixedArity, isUniqueScope: isUniqueScope, isTypedScope: isTypedScope)
        {
        }

        internal override void ReadSparsePath(ref RowBuffer row, ref RowCursor edit)
        {
            edit.pathToken = default;
            edit.pathOffset = default;
        }
    }

    public sealed class LayoutArray : LayoutIndexedScope
    {
        internal LayoutArray(bool immutable)
            : base(immutable ? LayoutCode.ImmutableArrayScope : LayoutCode.ArrayScope, immutable, isSizedScope: false, isFixedArity: false, isUniqueScope: false, isTypedScope: false)
        {
            this.TypeArg = new TypeArgument(this);
        }

        public override string Name => this.Immutable ? "im_array" : "array";

        internal TypeArgument TypeArg { get; }

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, this.TypeArg, options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteSparseArray(ref edit, this, options, out value);
            return Result.Success;
        }
    }

    public sealed class LayoutTypedArray : LayoutIndexedScope
    {
        internal LayoutTypedArray(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTypedArrayScope : LayoutCode.TypedArrayScope, immutable, isSizedScope: true, isFixedArity: false, isUniqueScope: false, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_array_t" : "array_t";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedArray(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count == 1);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[0].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeTypeArgs[0].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[0].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            return sizeof(LayoutCode) + value[0].Type.CountTypeArgument(value[0].TypeArgs);
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += value[0].Type.WriteTypeArgument(ref row, offset + lenInBytes, value[0].TypeArgs);
            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            return new TypeArgumentList(new[] { LayoutType.ReadTypeArgument(ref row, offset, out lenInBytes) });
        }
    }

    public sealed class LayoutTuple : LayoutIndexedScope
    {
        internal LayoutTuple(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTupleScope : LayoutCode.TupleScope, immutable, isSizedScope: false, isFixedArity: true, isUniqueScope: false, isTypedScope: false)
        {
        }

        public override string Name => this.Immutable ? "im_tuple" : "tuple";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteSparseTuple(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.CountTypeArgument(arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.WriteTypeArgument(ref row, offset + lenInBytes, arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            int numTypeArgs = (int)row.Read7BitEncodedUInt(offset, out lenInBytes);
            TypeArgument[] retval = new TypeArgument[numTypeArgs];
            for (int i = 0; i < numTypeArgs; i++)
            {
                retval[i] = LayoutType.ReadTypeArgument(ref row, offset + lenInBytes, out int itemLenInBytes);
                lenInBytes += itemLenInBytes;
            }

            return new TypeArgumentList(retval);
        }
    }

    public sealed class LayoutTypedTuple : LayoutIndexedScope
    {
        internal LayoutTypedTuple(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTypedTupleScope : LayoutCode.TypedTupleScope, immutable, isSizedScope: true, isFixedArity: true, isUniqueScope: false, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_tuple_t" : "tuple_t";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedTuple(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count > edit.index);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[edit.index].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeTypeArgs[edit.index].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[edit.index].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += RowBuffer.Count7BitEncodedUInt((ulong)value.Count);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.CountTypeArgument(arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += row.Write7BitEncodedUInt(offset + lenInBytes, (ulong)value.Count);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.WriteTypeArgument(ref row, offset + lenInBytes, arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            int numTypeArgs = (int)row.Read7BitEncodedUInt(offset, out lenInBytes);
            TypeArgument[] retval = new TypeArgument[numTypeArgs];
            for (int i = 0; i < numTypeArgs; i++)
            {
                retval[i] = LayoutType.ReadTypeArgument(ref row, offset + lenInBytes, out int itemLenInBytes);
                lenInBytes += itemLenInBytes;
            }

            return new TypeArgumentList(retval);
        }
    }

    public sealed class LayoutTagged : LayoutIndexedScope
    {
        internal LayoutTagged(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTaggedScope : LayoutCode.TaggedScope, immutable, isSizedScope: true, isFixedArity: true, isUniqueScope: false, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_tagged_t" : "tagged_t";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedTuple(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count > edit.index);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[edit.index].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeTypeArgs[edit.index].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[edit.index].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 2);
            return sizeof(LayoutCode) + value[1].Type.CountTypeArgument(value[1].TypeArgs);
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 2);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += value[1].Type.WriteTypeArgument(ref row, offset + lenInBytes, value[1].TypeArgs);
            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            TypeArgument[] retval = new TypeArgument[2];
            retval[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.Empty);
            retval[1] = LayoutType.ReadTypeArgument(ref row, offset, out lenInBytes);
            return new TypeArgumentList(retval);
        }
    }

    public sealed class LayoutTagged2 : LayoutIndexedScope
    {
        internal LayoutTagged2(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTagged2Scope : LayoutCode.Tagged2Scope, immutable, isSizedScope: true, isFixedArity: true, isUniqueScope: false, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_tagged2_t" : "tagged2_t";

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedTuple(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count > edit.index);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[edit.index].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeTypeArgs[edit.index].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[edit.index].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 3);
            int lenInBytes = sizeof(LayoutCode);
            for (int i = 1; i < value.Count; i++)
            {
                TypeArgument arg = value[i];
                lenInBytes += arg.Type.CountTypeArgument(arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 3);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            for (int i = 1; i < value.Count; i++)
            {
                TypeArgument arg = value[i];
                lenInBytes += arg.Type.WriteTypeArgument(ref row, offset + lenInBytes, arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            lenInBytes = 0;
            TypeArgument[] retval = new TypeArgument[3];
            retval[0] = new TypeArgument(LayoutType.UInt8, TypeArgumentList.Empty);
            for (int i = 1; i < 3; i++)
            {
                retval[i] = LayoutType.ReadTypeArgument(ref row, offset + lenInBytes, out int itemLenInBytes);
                lenInBytes += itemLenInBytes;
            }

            return new TypeArgumentList(retval);
        }
    }

    public sealed class LayoutNullable : LayoutIndexedScope
    {
        internal LayoutNullable(bool immutable)
            : base(immutable ? LayoutCode.ImmutableNullableScope : LayoutCode.NullableScope, immutable, isSizedScope: true, isFixedArity: true, isUniqueScope: false, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_nullable" : "nullable";

        public Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            bool hasValue,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteNullable(ref edit, this, typeArgs, options, hasValue, out value);
            return Result.Success;
        }

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            return this.WriteScope(ref b, ref edit, typeArgs, hasValue: true, out value, options);
        }

        public static Result HasValue(ref RowBuffer b, ref RowCursor scope)
        {
            Contract.Requires(scope.scopeType is LayoutNullable);
            Contract.Assert(scope.index == 1 || scope.index == 2, "Nullable scopes always point at the value");
            Contract.Assert(scope.scopeTypeArgs.Count == 1);
            bool hasValue = b.ReadInt8(scope.start) != 0;
            return hasValue ? Result.Success : Result.NotFound;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count == 1);
            Contract.Assert(edit.index == 1);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[0].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index == 1);
            edit.cellType = edit.scopeTypeArgs[0].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[0].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            return sizeof(LayoutCode) + value[0].Type.CountTypeArgument(value[0].TypeArgs);
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += value[0].Type.WriteTypeArgument(ref row, offset + lenInBytes, value[0].TypeArgs);
            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            return new TypeArgumentList(new[] { LayoutType.ReadTypeArgument(ref row, offset, out lenInBytes) });
        }
    }

    public abstract class LayoutUniqueScope : LayoutIndexedScope
    {
        protected LayoutUniqueScope(LayoutCode code, bool immutable, bool isSizedScope, bool isTypedScope)
            : base(code, immutable, isSizedScope, isFixedArity: false, isUniqueScope: true, isTypedScope: isTypedScope)
        {
        }

        public abstract TypeArgument FieldType(ref RowCursor scope);

        public override Result WriteScope<TContext>(
            ref RowBuffer b,
            ref RowCursor scope,
            TypeArgumentList typeArgs,
            TContext context,
            WriterFunc<TContext> func,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result r = this.WriteScope(ref b, ref scope, typeArgs, out RowCursor uniqueScope, options);
            if (r != Result.Success)
            {
                return r;
            }

            uniqueScope.Clone(out RowCursor childScope);
            childScope.deferUniqueIndex = true;
            r = func?.Invoke(ref b, ref childScope, context) ?? Result.Success;
            if (r != Result.Success)
            {
                this.DeleteScope(ref b, ref scope);
                return r;
            }

            uniqueScope.count = childScope.count;
            r = b.TypedCollectionUniqueIndexRebuild(ref uniqueScope);
            if (r != Result.Success)
            {
                this.DeleteScope(ref b, ref scope);
                return r;
            }

            scope.Skip(ref b, ref childScope);
            return Result.Success;
        }

        /// <summary>Moves an existing sparse field into the unique index.</summary>
        /// <param name="b">The row to move within.</param>
        /// <param name="destinationScope">The parent unique indexed edit into which the field should be moved.</param>
        /// <param name="sourceEdit">The field to be moved.</param>
        /// <param name="options">The move options.</param>
        /// <returns>Success if the field is permitted within the unique index, the error code otherwise.</returns>
        /// <remarks>
        /// The source field MUST be a field whose type arguments match the element type of the
        /// destination unique index.
        /// <para />
        /// The source field is delete whether the move succeeds or fails.
        /// </remarks>
        public Result MoveField(
            ref RowBuffer b,
            ref RowCursor destinationScope,
            ref RowCursor sourceEdit,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseMove(
                ref b,
                ref destinationScope,
                this,
                this.FieldType(ref destinationScope),
                ref sourceEdit,
                options,
                out RowCursor dstEdit);

            if (result != Result.Success)
            {
                return result;
            }

            // Perform the move.
            b.TypedCollectionMoveField(ref dstEdit, ref sourceEdit, (RowOptions)options);

            // TODO: it would be "better" if the destinationScope were updated to point to the 
            // highest item seen.  Then we would avoid the maximum reparse.
            destinationScope.count = dstEdit.count;
            return Result.Success;
        }

        /// <summary>Search for a matching field within a unique index.</summary>
        /// <param name="b">The row to search.</param>
        /// <param name="scope">The parent unique index edit to search.</param>
        /// <param name="patternScope">The parent edit from which the match pattern is read.</param>
        /// <param name="value">If successful, the updated edit.</param>
        /// <returns>
        /// Success a matching field exists in the unique index, NotFound if no match is found, the
        /// error code otherwise.
        /// </returns>
        /// <remarks>The pattern field is delete whether the find succeeds or fails.</remarks>
        public Result Find(ref RowBuffer b, ref RowCursor scope, ref RowCursor patternScope, out RowCursor value)
        {
            Result result = LayoutType.PrepareSparseMove(
                ref b,
                ref scope,
                this,
                this.FieldType(ref scope),
                ref patternScope,
                UpdateOptions.Update,
                out value);

            if (result != Result.Success)
            {
                return result;
            }

            // Check if the search found the result.
            b.DeleteSparse(ref patternScope);

            return Result.Success;
        }
    }

    public sealed class LayoutTypedSet : LayoutUniqueScope
    {
        internal LayoutTypedSet(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTypedSetScope : LayoutCode.TypedSetScope, immutable, isSizedScope: true, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_set_t" : "set_t";

        public override TypeArgument FieldType(ref RowCursor scope)
        {
            return scope.scopeTypeArgs[0];
        }

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedSet(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            Contract.Assert(edit.index >= 0);
            Contract.Assert(edit.scopeTypeArgs.Count == 1);
            return !LayoutCodeTraits.AlwaysRequiresTypeCode(edit.scopeTypeArgs[0].Type.LayoutCode);
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeTypeArgs[0].Type;
            edit.cellTypeArgs = edit.scopeTypeArgs[0].TypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            return sizeof(LayoutCode) + value[0].Type.CountTypeArgument(value[0].TypeArgs);
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 1);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            lenInBytes += value[0].Type.WriteTypeArgument(ref row, offset + lenInBytes, value[0].TypeArgs);
            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            return new TypeArgumentList(new[] { LayoutType.ReadTypeArgument(ref row, offset, out lenInBytes) });
        }
    }

    public sealed class LayoutTypedMap : LayoutUniqueScope
    {
        internal LayoutTypedMap(bool immutable)
            : base(immutable ? LayoutCode.ImmutableTypedMapScope : LayoutCode.TypedMapScope, immutable, isSizedScope: true, isTypedScope: true)
        {
        }

        public override string Name => this.Immutable ? "im_map_t" : "map_t";

        public override TypeArgument FieldType(ref RowCursor scope)
        {
            return new TypeArgument(
                scope.scopeType.Immutable ? LayoutType.ImmutableTypedTuple : LayoutType.TypedTuple,
                scope.scopeTypeArgs);
        }

        public override Result WriteScope(
            ref RowBuffer b,
            ref RowCursor edit,
            TypeArgumentList typeArgs,
            out RowCursor value,
            UpdateOptions options = UpdateOptions.Upsert)
        {
            Result result = LayoutType.PrepareSparseWrite(ref b, ref edit, new TypeArgument(this, typeArgs), options);
            if (result != Result.Success)
            {
                value = default;
                return result;
            }

            b.WriteTypedMap(ref edit, this, typeArgs, options, out value);
            return Result.Success;
        }

        internal override bool HasImplicitTypeCode(ref RowCursor edit)
        {
            return true;
        }

        internal override void SetImplicitTypeCode(ref RowCursor edit)
        {
            edit.cellType = edit.scopeType.Immutable ? LayoutType.ImmutableTypedTuple : LayoutType.TypedTuple;
            edit.cellTypeArgs = edit.scopeTypeArgs;
        }

        internal override int CountTypeArgument(TypeArgumentList value)
        {
            Contract.Assert(value.Count == 2);
            int lenInBytes = sizeof(LayoutCode);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.CountTypeArgument(arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override int WriteTypeArgument(ref RowBuffer row, int offset, TypeArgumentList value)
        {
            Contract.Assert(value.Count == 2);
            row.WriteSparseTypeCode(offset, this.LayoutCode);
            int lenInBytes = sizeof(LayoutCode);
            foreach (TypeArgument arg in value)
            {
                lenInBytes += arg.Type.WriteTypeArgument(ref row, offset + lenInBytes, arg.TypeArgs);
            }

            return lenInBytes;
        }

        internal override TypeArgumentList ReadTypeArgumentList(ref RowBuffer row, int offset, out int lenInBytes)
        {
            lenInBytes = 0;
            TypeArgument[] retval = new TypeArgument[2];
            for (int i = 0; i < 2; i++)
            {
                retval[i] = LayoutType.ReadTypeArgument(ref row, offset + lenInBytes, out int itemLenInBytes);
                lenInBytes += itemLenInBytes;
            }

            return new TypeArgumentList(retval);
        }
    }
}
