//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.lenientFormat;

// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1402 // FileMayOnlyContainASingleType
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1201 // Elements should appear in the correct order
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable SA1401 // Fields should be private
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1040 // Avoid empty interfaces
// TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
///#pragma warning disable CA1051 // Do not declare visible instance fields


/**
 * The abstract base class for typed hybrid row field descriptors.
 * <see cref="LayoutType" /> is immutable.
 */
// TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [DebuggerDisplay("{" + nameof(LayoutType.Name) + "}")] public abstract class LayoutType : ILayoutType
public abstract class LayoutType implements ILayoutType {
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutArray Array = new LayoutArray(immutable:
    // false);
    public static final LayoutArray Array = new LayoutArray(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutBinary Binary = new LayoutBinary();
    public static final LayoutBinary Binary = new LayoutBinary();
    /**
     * The number of bits in a single byte on the current architecture.
     */
    public static final int BitsPerByte = 8;
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutBoolean Boolean = new LayoutBoolean(true);
    public static final LayoutBoolean Boolean = new LayoutBoolean(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutBoolean BooleanFalse = new LayoutBoolean
    // (false);
    public static final LayoutBoolean BooleanFalse = new LayoutBoolean(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutDateTime DateTime = new LayoutDateTime();
    public static final LayoutDateTime DateTime = new LayoutDateTime();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutDecimal Decimal = new LayoutDecimal();
    public static final LayoutDecimal Decimal = new LayoutDecimal();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] internal static readonly LayoutEndScope EndScope = new LayoutEndScope();
    public static final LayoutEndScope EndScope = new LayoutEndScope();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutFloat128 Float128 = new LayoutFloat128();
    public static final LayoutFloat128 Float128 = new LayoutFloat128();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutFloat32 Float32 = new LayoutFloat32();
    public static final LayoutFloat32 Float32 = new LayoutFloat32();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutFloat64 Float64 = new LayoutFloat64();
    public static final LayoutFloat64 Float64 = new LayoutFloat64();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutArray ImmutableArray = new LayoutArray
    // (immutable: true);
    public static final LayoutArray ImmutableArray = new LayoutArray(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutNullable ImmutableNullable = new
    // LayoutNullable(immutable: true);
    public static final LayoutNullable ImmutableNullable = new LayoutNullable(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutObject ImmutableObject = new LayoutObject
    // (immutable: true);
    public static final LayoutObject ImmutableObject = new LayoutObject(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTagged ImmutableTagged = new LayoutTagged
    // (immutable: true);
    public static final LayoutTagged ImmutableTagged = new LayoutTagged(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTagged2 ImmutableTagged2 = new
    // LayoutTagged2(immutable: true);
    public static final LayoutTagged2 ImmutableTagged2 = new LayoutTagged2(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTuple ImmutableTuple = new LayoutTuple
    // (immutable: true);
    public static final LayoutTuple ImmutableTuple = new LayoutTuple(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedArray ImmutableTypedArray = new
    // LayoutTypedArray(immutable: true);
    public static final LayoutTypedArray ImmutableTypedArray = new LayoutTypedArray(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedMap ImmutableTypedMap = new
    // LayoutTypedMap(immutable: true);
    public static final LayoutTypedMap ImmutableTypedMap = new LayoutTypedMap(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedSet ImmutableTypedSet = new
    // LayoutTypedSet(immutable: true);
    public static final LayoutTypedSet ImmutableTypedSet = new LayoutTypedSet(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedTuple ImmutableTypedTuple = new
    // LayoutTypedTuple(immutable: true);
    public static final LayoutTypedTuple ImmutableTypedTuple = new LayoutTypedTuple(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUDT ImmutableUDT = new LayoutUDT
    // (immutable: true);
    public static final LayoutUDT ImmutableUDT = new LayoutUDT(true);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutInt16 Int16 = new LayoutInt16();
    public static final LayoutInt16 Int16 = new LayoutInt16();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutInt32 Int32 = new LayoutInt32();
    public static final LayoutInt32 Int32 = new LayoutInt32();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutInt64 Int64 = new LayoutInt64();
    public static final LayoutInt64 Int64 = new LayoutInt64();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutInt8 Int8 = new LayoutInt8();
    public static final LayoutInt8 Int8 = new LayoutInt8();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutMongoDbObjectId MongoDbObjectId = new
    // LayoutMongoDbObjectId();
    public static final LayoutMongoDbObjectId MongoDbObjectId = new LayoutMongoDbObjectId();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutNull Null = new LayoutNull();
    public static final LayoutNull Null = new LayoutNull();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutNullable Nullable = new LayoutNullable
    // (immutable: false);
    public static final LayoutNullable Nullable = new LayoutNullable(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutObject Object = new LayoutObject
    // (immutable: false);
    public static final LayoutObject Object = new LayoutObject(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTagged Tagged = new LayoutTagged
    // (immutable: false);
    public static final LayoutTagged Tagged = new LayoutTagged(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTagged2 Tagged2 = new LayoutTagged2
    // (immutable: false);
    public static final LayoutTagged2 Tagged2 = new LayoutTagged2(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTuple Tuple = new LayoutTuple(immutable:
    // false);
    public static final LayoutTuple Tuple = new LayoutTuple(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedArray TypedArray = new
    // LayoutTypedArray(immutable: false);
    public static final LayoutTypedArray TypedArray = new LayoutTypedArray(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedMap TypedMap = new LayoutTypedMap
    // (immutable: false);
    public static final LayoutTypedMap TypedMap = new LayoutTypedMap(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedSet TypedSet = new LayoutTypedSet
    // (immutable: false);
    public static final LayoutTypedSet TypedSet = new LayoutTypedSet(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutTypedTuple TypedTuple = new
    // LayoutTypedTuple(immutable: false);
    public static final LayoutTypedTuple TypedTuple = new LayoutTypedTuple(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUDT UDT = new LayoutUDT(immutable: false);
    public static final LayoutUDT UDT = new LayoutUDT(false);
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt16 UInt16 = new LayoutUInt16();
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt16 UInt16 = new LayoutUInt16();
    public static final LayoutUInt16 UInt16 = new LayoutUInt16();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt32 UInt32 = new LayoutUInt32();
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt32 UInt32 = new LayoutUInt32();
    public static final LayoutUInt32 UInt32 = new LayoutUInt32();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt64 UInt64 = new LayoutUInt64();
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt64 UInt64 = new LayoutUInt64();
    public static final LayoutUInt64 UInt64 = new LayoutUInt64();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUInt8 UInt8 = new LayoutUInt8();
    public static final LayoutUInt8 UInt8 = new LayoutUInt8();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutGuid Guid = new LayoutGuid();
    public static final LayoutGuid Guid = new LayoutGuid();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUnixDateTime UnixDateTime = new
    // LayoutUnixDateTime();
    public static final LayoutUnixDateTime UnixDateTime = new LayoutUnixDateTime();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutUtf8 Utf8 = new LayoutUtf8();
    public static final LayoutUtf8 Utf8 = new LayoutUtf8();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutVarInt VarInt = new LayoutVarInt();
    public static final LayoutVarInt VarInt = new LayoutVarInt();
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes",
    // Justification = "Type is immutable.")] public static readonly LayoutVarUInt VarUInt = new LayoutVarUInt();
    public static final LayoutVarUInt VarUInt = new LayoutVarUInt();
    private static final LayoutType[] CodeIndex = new LayoutType[com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.EndScope.getValue() + 1];
    /**
     * If true, this edit's nested fields cannot be updated individually.
     * The entire edit can still be replaced.
     */
    public boolean Immutable;
    /**
     * The physical layout code used to represent the type within the serialization.
     */
    public LayoutCode LayoutCode = com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.values()[0];
    /**
     * If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.
     */
    public int Size;

    /**
     * Initializes a new instance of the <see cref="LayoutType" /> class.
     */
    public LayoutType(LayoutCode code, boolean immutable, int size) {
        this.LayoutCode = code;
        this.Immutable = immutable;
        this.Size = size;
        LayoutType.CodeIndex[code.getValue()] = this;
    }

    /**
     * True if this type can be used in the variable-length segment.
     */
    public final boolean getAllowVariable() {
        return !this.getIsFixed();
    }

    /**
     * True if this type is a boolean.
     */
    public boolean getIsBool() {
        return false;
    }

    /**
     * True if this type is always fixed length.
     */
    public abstract boolean getIsFixed();

    /**
     * True if this type is a literal null.
     */
    public boolean getIsNull() {
        return false;
    }

    /**
     * True if this type is a variable-length encoded integer type (either signed or unsigned).
     */
    public boolean getIsVarint() {
        return false;
    }

    /**
     * Human readable name of the type.
     */
    public abstract String getName();

    public int CountTypeArgument(TypeArgumentList value) {
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal static LayoutType FromCode
    // (LayoutCode code)
    public static LayoutType FromCode(LayoutCode code) {
        LayoutType type = LayoutType.CodeIndex[code.getValue()];
        // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
        //#if DEBUG
        if (type == null) {
            throw new IllegalStateException(lenientFormat("Not Implemented: %s", code));
        }
        //#endif

        return type;
    }

    /**
     * Helper for preparing the delete of a sparse field.
     *
     * @param b    The row to delete from.
     * @param edit The parent edit containing the field to delete.
     * @param code The expected type of the field.
     * @return Success if the delete is permitted, the error code otherwise.
     */
    public static Result PrepareSparseDelete(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                                             LayoutCode code) {
        if (edit.get().scopeType.IsFixedArity) {
            return Result.TypeConstraint;
        }

        if (edit.get().immutable) {
            return Result.InsufficientPermissions;
        }

        if (edit.get().exists && LayoutCodeTraits.Canonicalize(edit.get().cellType.LayoutCode) != code) {
            return Result.TypeMismatch;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the move of a sparse field into an existing restricted edit.
     *
     * @param b                The row to read from.
     * @param destinationScope The parent set edit into which the field should be moved.
     * @param destinationCode  The expected type of the edit moving within.
     * @param elementType      The expected type of the elements within the edit.
     * @param srcEdit          The field to be moved.
     * @param options          The move options.
     * @param dstEdit          If successful, a prepared insertion cursor for the destination.
     * @return Success if the move is permitted, the error code otherwise.
     * The source field is delete if the move prepare fails with a destination error.
     */
    public static Result PrepareSparseMove(RefObject<RowBuffer> b,
                                           RefObject<RowCursor> destinationScope,
                                           LayoutScope destinationCode, TypeArgument elementType,
                                           RefObject<RowCursor> srcEdit, UpdateOptions options,
                                           OutObject<RowCursor> dstEdit) {
        checkArgument(destinationScope.get().scopeType == destinationCode);
        checkArgument(destinationScope.get().index == 0, "Can only insert into a edit at the root");

        // Prepare the delete of the source.
        Result result = LayoutType.PrepareSparseDelete(b, srcEdit, elementType.getType().LayoutCode);
        if (result != Result.Success) {
            dstEdit.set(null);
            return result;
        }

        if (!srcEdit.get().exists) {
            dstEdit.set(null);
            return Result.NotFound;
        }

        if (destinationScope.get().immutable) {
            b.get().DeleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.InsufficientPermissions;
        }

        if (!srcEdit.get().cellTypeArgs.equals(elementType.getTypeArgs().clone())) {
            b.get().DeleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TypeConstraint;
        }

        if (options == UpdateOptions.InsertAt) {
            b.get().DeleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TypeConstraint;
        }

        // Prepare the insertion at the destination.
        dstEdit.set(b.get().PrepareSparseMove(destinationScope, srcEdit).clone());
        if ((options == UpdateOptions.Update) && (!dstEdit.get().exists)) {
            b.get().DeleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.NotFound;
        }

        if ((options == UpdateOptions.Insert) && dstEdit.get().exists) {
            b.get().DeleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.Exists;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the read of a sparse field.
     *
     * @param b    The row to read from.
     * @param edit The parent edit containing the field to read.
     * @param code The expected type of the field.
     * @return Success if the read is permitted, the error code otherwise.
     */
    public static Result PrepareSparseRead(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                                           LayoutCode code) {
        if (!edit.get().exists) {
            return Result.NotFound;
        }

        if (LayoutCodeTraits.Canonicalize(edit.get().cellType.LayoutCode) != code) {
            return Result.TypeMismatch;
        }

        return Result.Success;
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param b       The row to write to.
     * @param edit    The cursor for the field to write.
     * @param typeArg The (optional) type constraints.
     * @param options The write options.
     * @return Success if the write is permitted, the error code otherwise.
     */
    public static Result PrepareSparseWrite(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                                            TypeArgument typeArg, UpdateOptions options) {
        if (edit.get().immutable || (edit.get().scopeType.IsUniqueScope && !edit.get().deferUniqueIndex)) {
            return Result.InsufficientPermissions;
        }

        if (edit.get().scopeType.IsFixedArity && !(edit.get().scopeType instanceof LayoutNullable)) {
            if ((edit.get().index < edit.get().scopeTypeArgs.getCount()) && !typeArg.equals(edit.get().scopeTypeArgs.get(edit.get().index).clone())) {
                return Result.TypeConstraint;
            }
        } else if (edit.get().scopeType instanceof LayoutTypedMap) {
            if (!((typeArg.getType() instanceof LayoutTypedTuple) && typeArg.getTypeArgs().equals(edit.get().scopeTypeArgs.clone()))) {
                return Result.TypeConstraint;
            }
        } else if (edit.get().scopeType.IsTypedScope && !typeArg.equals(edit.get().scopeTypeArgs.get(0).clone())) {
            return Result.TypeConstraint;
        }

        if ((options == UpdateOptions.InsertAt) && edit.get().scopeType.IsFixedArity) {
            return Result.TypeConstraint;
        }

        if ((options == UpdateOptions.InsertAt) && !edit.get().scopeType.IsFixedArity) {
            edit.get().exists = false; // InsertAt never overwrites an existing item.
        }

        if ((options == UpdateOptions.Update) && (!edit.get().exists)) {
            return Result.NotFound;
        }

        if ((options == UpdateOptions.Insert) && edit.get().exists) {
            return Result.Exists;
        }

        return Result.Success;
    }

    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [MethodImpl(MethodImplOptions.AggressiveInlining)] internal static TypeArgument ReadTypeArgument(ref RowBuffer row, int offset, out int lenInBytes)
    public static TypeArgument ReadTypeArgument(RefObject<RowBuffer> row, int offset, OutObject<Integer> lenInBytes) {
        LayoutType itemCode = row.get().ReadSparseTypeCode(offset);
        int argsLenInBytes;
        OutObject<Integer> tempOut_argsLenInBytes = new OutObject<Integer>();
        TypeArgumentList itemTypeArgs = itemCode.ReadTypeArgumentList(row, offset + (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE), tempOut_argsLenInBytes).clone();
        argsLenInBytes = tempOut_argsLenInBytes.get();
        lenInBytes.set((com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE) + argsLenInBytes);
        return new TypeArgument(itemCode, itemTypeArgs.clone());
    }

    public TypeArgumentList ReadTypeArgumentList(RefObject<RowBuffer> row, int offset, OutObject<Integer> lenInBytes) {
        lenInBytes.set(0);
        return TypeArgumentList.Empty;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     */
    // TODO: C# TO JAVA CONVERTER: Java annotations will not correspond to .NET attributes:
    //ORIGINAL LINE: [DebuggerHidden] public T TypeAs<T>() where T : ILayoutType
    public final <T extends ILayoutType> T TypeAs() {
        return (T)this;
    }

    public int WriteTypeArgument(RefObject<RowBuffer> row, int offset, TypeArgumentList value) {
        row.get().WriteSparseTypeCode(offset, this.LayoutCode);
        return (com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutCode.SIZE / Byte.SIZE);
    }
}