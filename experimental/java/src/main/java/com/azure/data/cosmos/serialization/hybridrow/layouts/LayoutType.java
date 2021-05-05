// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Json;
import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes the physical byte layout of a hybrid row field of a specific physical type {@code T}.
 * <p>
 * {@link LayoutType} provides methods for manipulating hybrid row fields of a particular type, and properties that
 * describe the layout of fields of that type.
 */
public abstract class LayoutType /*implements ILayoutType*/ {

    private static final LayoutType[] codeIndex = new LayoutType[LayoutCode.END_SCOPE.value() + 1];

    @JsonProperty
    private final boolean immutable;

    @JsonProperty
    private final LayoutCode layoutCode;

    @JsonProperty
    private final int size;

    @JsonProperty
    private final TypeArgument typeArg;

    /**
     * Initializes a new instance of the {@link LayoutType} class.
     *
     * @param code      the {@linkplain LayoutCode} layout code of the instance.
     * @param immutable {@code true} if edits to fields with this layout type are prohibited.
     * @param size      size of fields with this layout type in bytes.
     */
    protected LayoutType(@Nonnull final LayoutCode code, final boolean immutable, final int size) {

        checkNotNull(code, "expected non-null code");

        this.immutable = immutable;
        this.layoutCode = code;
        this.size = size;
        this.typeArg = new TypeArgument(this);

        codeIndex[code.value()] = this;
    }

    /**
     * Initializes a new instance of the {@link LayoutType} class.
     *
     * @param code the {@linkplain LayoutCode} layout code of the instance.
     * @param size size of fields with this layout type in bytes.
     */
    protected LayoutType(LayoutCode code, int size) {
        this(code, false, size);
    }

    /**
     * {@code true} if this type is a boolean.
     *
     * @return {@code true} if this type is a boolean.
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * {@code true} if this type is always fixed length.
     *
     * @return {@code true} if this type is always fixed length.
     */
    public abstract boolean isFixed();

    /**
     * {@code true} if this {@link LayoutType}'s nested fields cannot be updated individually.
     * <p>
     * Instances of this {@link LayoutType} can still be replaced in their entirety.
     *
     * @return {@code true} if this {@link LayoutType}'s nested fields cannot be updated individually.
     */
    public boolean isImmutable() {
        return this.immutable;
    }

    /**
     * {@code true} if this type is a literal null.
     *
     * @return {@code true} if this type is a literal null.
     */
    public boolean isNull() {
        return false;
    }

    /**
     * {@code true} if this type is a variable-length encoded integer type (either signed or unsigned).
     *
     * @return {@code true} if this type is a variable-length encoded integer type (either signed or unsigned).
     */
    public boolean isVarint() {
        return false;
    }

    /**
     * {@code true} if this type can be used in the variable-length segment.
     *
     * @return {@code true} if this type can be used in the variable-length segment.
     */
    public final boolean allowVariable() {
        return !this.isFixed();
    }

    public int countTypeArgument(@Nonnull TypeArgumentList value) {
        return LayoutCode.BYTES;
    }

    @Nonnull
    public static LayoutType fromLayoutCode(LayoutCode code) {
        LayoutType type = LayoutType.codeIndex[code.value()];
        checkArgument(type != null, "unimplemented code: %s", code);
        return type;
    }

    /**
     * The physical layout code used to represent the type within the serialization.
     *
     * @return the physical layout code used to represent the type within the serialization.
     */
    @Nonnull
    public LayoutCode layoutCode() {
        return this.layoutCode;
    }

    /**
     * Human readable name of the type.
     *
     * @return human readable name of the type.
     */
    @Nonnull
    public abstract String name();

    /**
     * Helper for preparing the delete of a sparse field.
     *
     * @param buffer The row to delete from.
     * @param edit   The parent edit containing the field to delete.
     * @param code   The expected type of the field.
     * @return Success if the delete is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseDelete(RowBuffer buffer, RowCursor edit, LayoutCode code) {

        if (edit.scopeType().isFixedArity()) {
            return Result.TYPE_CONSTRAINT;
        }

        if (edit.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (edit.exists() && LayoutCodeTraits.canonicalize(edit.cellType().layoutCode()) != code) {
            return Result.TYPE_MISMATCH;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the move of a sparse field into an existing restricted edit.
     *
     * @param buffer           The row to read from.
     * @param destinationScope The parent set edit into which the field should be moved.
     * @param destinationCode  The expected type of the edit moving within.
     * @param elementType      The expected type of the elements within the edit.
     * @param srcEdit          The field to be moved.
     * @param options          The move options.
     * @param dstEdit          If successful, a prepared insertion cursor for the destination.
     * @return Success if the move is permitted, the error code otherwise.
     * The source field is delete if the move prepare fails with a destination error.
     */
    @Nonnull
    public static Result prepareSparseMove(
        RowBuffer buffer,
        RowCursor destinationScope,
        LayoutTypeScope destinationCode,
        TypeArgument elementType,
        RowCursor srcEdit,
        UpdateOptions options,
        Out<RowCursor> dstEdit) {

        checkArgument(destinationScope.scopeType() == destinationCode);
        checkArgument(destinationScope.index() == 0, "Can only insert into a edit at the root");

        // Prepare the delete of the source
        Result result = LayoutType.prepareSparseDelete(buffer, srcEdit, elementType.type().layoutCode());

        if (result != Result.SUCCESS) {
            dstEdit.set(null);
            return result;
        }

        if (!srcEdit.exists()) {
            dstEdit.set(null);
            return Result.NOT_FOUND;
        }

        if (destinationScope.immutable()) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (!srcEdit.cellTypeArgs().equals(elementType.typeArgs())) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TYPE_CONSTRAINT;
        }

        if (options == UpdateOptions.INSERT_AT) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.TYPE_CONSTRAINT;
        }

        // Prepare the insertion at the destination.
        dstEdit.set(buffer.prepareSparseMove(destinationScope, srcEdit));

        if ((options == UpdateOptions.UPDATE) && (!dstEdit.get().exists())) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.NOT_FOUND;
        }

        if ((options == UpdateOptions.INSERT) && dstEdit.get().exists()) {
            buffer.deleteSparse(srcEdit);
            dstEdit.set(null);
            return Result.EXISTS;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the read of a sparse field.
     *
     * @param buffer The row to read from.
     * @param edit   The parent edit containing the field to read.
     * @param code   The expected type of the field.
     * @return Success if the read is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseRead(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit, @Nonnull LayoutCode code) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(code, "expected non-null code");

        if (!edit.exists()) {
            return Result.NOT_FOUND;
        }

        if (LayoutCodeTraits.canonicalize(edit.cellType().layoutCode()) != code) {
            return Result.TYPE_MISMATCH;
        }

        return Result.SUCCESS;
    }

    /**
     * Helper for preparing the write of a sparse field.
     *
     * @param buffer  The row to write to.
     * @param edit    The cursor for the field to write.
     * @param typeArg The (optional) type constraints.
     * @param options The write options.
     * @return Success if the write is permitted, the error code otherwise.
     */
    @Nonnull
    public static Result prepareSparseWrite(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor edit,
        @Nonnull final TypeArgument typeArg,
        @Nonnull final UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArg, "expected non-null typeArg");
        checkNotNull(options, "expected non-null options");

        if (edit.immutable() || (edit.scopeType().isUniqueScope() && !edit.deferUniqueIndex())) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (edit.scopeType().isFixedArity() && !(edit.scopeType() instanceof LayoutNullable)) {
            if ((edit.index() < edit.scopeTypeArgs().count()) && !typeArg.equals(edit.scopeTypeArgs().get(edit.index()))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (edit.scopeType() instanceof LayoutTypedMap) {
            if (!((typeArg.type() instanceof LayoutTypedTuple) && typeArg.typeArgs().equals(edit.scopeTypeArgs()))) {
                return Result.TYPE_CONSTRAINT;
            }
        } else if (edit.scopeType().isTypedScope() && !typeArg.equals(edit.scopeTypeArgs().get(0))) {
            return Result.TYPE_CONSTRAINT;
        }

        if ((options == UpdateOptions.INSERT_AT) && edit.scopeType().isFixedArity()) {
            return Result.TYPE_CONSTRAINT;
        }

        if ((options == UpdateOptions.INSERT_AT) && !edit.scopeType().isFixedArity()) {
            edit.exists(false); // InsertAt never overwrites an existing item.
        }

        if ((options == UpdateOptions.UPDATE) && (!edit.exists())) {
            return Result.NOT_FOUND;
        }

        if ((options == UpdateOptions.INSERT) && edit.exists()) {
            return Result.EXISTS;
        }

        return Result.SUCCESS;
    }

    @Nonnull
    public static TypeArgument readTypeArgument(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        LayoutType type = buffer.readSparseTypeCode(offset);
        TypeArgumentList typeArgs = type.readTypeArgumentList(buffer, offset + LayoutCode.BYTES, lengthInBytes);
        lengthInBytes.set(LayoutCode.BYTES + lengthInBytes.get());

        return new TypeArgument(type, typeArgs);
    }

    @Nonnull
    public TypeArgumentList readTypeArgumentList(
        @Nonnull final RowBuffer buffer, final int offset, @Nonnull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        lengthInBytes.set(0);
        return TypeArgumentList.EMPTY;
    }

    /**
     * If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.
     *
     * @return If fixed, the fixed size of the type's serialization in bytes, otherwise undefined.
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns a string representation of the {@linkplain LayoutType layout type}.
     *
     * @return a string representation of the {@linkplain LayoutType layout type}.
     */
    @Override
    public String toString() {
        return Json.toString(this);
    }

    public TypeArgument typeArg() {
        return this.typeArg;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     *
     * @param <T> a type that implements {@link ILayoutType}.
     * @return the physical layout type of the field cast to the specified type.
     */
    @SuppressWarnings("unchecked")
    public final <T extends ILayoutType> T typeAs() {
        return (T) this;
    }

    public int writeTypeArgument(@Nonnull final RowBuffer buffer, int offset, @Nonnull final TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        return LayoutCode.BYTES;
    }
}
