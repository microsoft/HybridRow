// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Utf8String;
import com.azure.data.cosmos.serialization.hybridrow.schemas.StorageKind;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;

public final class LayoutColumn {

    private final LayoutBit booleanBit;
    private final Utf8String fullPath;
    private final LayoutBit nullBit;
    private final LayoutColumn parent;
    private final Utf8String path;
    private final int size;
    private final StorageKind storage;
    private final LayoutType type;
    private final TypeArgument typeArg;
    private final TypeArgumentList typeArgs;

    private int index;
    private int offset;

    /**
     * Initializes a new instance of the {@link LayoutColumn} class
     *
     * @param path       The path to the field relative to parent scope.
     * @param type       Type of the field.
     * @param typeArgs   For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     * @param storage    Storage encoding of the field.
     * @param parent     The layout of the parent scope, if a nested column.
     * @param index      zero-based column index.
     * @param offset     zero-based Offset from beginning of serialization.
     * @param nullBit    zero-based index into the bit mask for the null bit.
     * @param booleanBit For bool fields, zero-based index into the bit mask for the boolean value.
     * @param length     For variable length types the length, otherwise {@code 0}.
     */
    public LayoutColumn(
        @Nonnull final String path, @Nonnull final LayoutType type, @Nonnull final TypeArgumentList typeArgs,
        @Nonnull final StorageKind storage, final LayoutColumn parent, int index, int offset,
        @Nonnull final LayoutBit nullBit, @Nonnull final LayoutBit booleanBit, int length) {

        checkNotNull(path);
        checkNotNull(type);
        checkNotNull(typeArgs);
        checkNotNull(storage);
        checkNotNull(nullBit);
        checkNotNull(booleanBit);

        this.path = Utf8String.transcodeUtf16(path);
        this.fullPath = Utf8String.transcodeUtf16(fullPath(parent, path));
        this.type = type;
        this.typeArgs = typeArgs;
        this.typeArg = new TypeArgument(type, typeArgs);
        this.storage = storage;
        this.parent = parent;
        this.index = index;
        this.offset = offset;
        this.nullBit = nullBit;
        this.booleanBit = booleanBit;
        this.size = this.typeArg().type().isFixed() ? type.size() : length;
    }

    /**
     * For boolean fields, the zero-based index into the bit mask for the boolean value.
     *
     * @return for boolean fields, the zero-based index into the bit mask for the boolean value.
     */
    public @Nonnull LayoutBit booleanBit() {
        return this.booleanBit;
    }

    /**
     * Full logical path of the field within the row.
     * <p>
     * Paths are expressed in dotted notation: e.g. a relative {@link #path()} of 'b.c' within the scope 'a' yields a
     * full path of 'a.b.c'.
     *
     * @return Full logical path of the field within the row.
     */
    public @Nonnull Utf8String fullPath() {
        return this.fullPath;
    }

    /**
     * Zero-based index of the column within the structure.
     * <p>
     * This value also indicates which presence bit controls this column.
     *
     * @return Zero-based index of the column within the structure.
     */
    public int index() {
        return this.index;
    }

    /**
     * For nullable fields, the zero-based index into the bit mask for the null bit.
     *
     * @return For nullable fields, the zero-based index into the bit mask for the null bit.
     */
    public @Nonnull LayoutBit nullBit() {
        return this.nullBit;
    }

    /**
     * If {@link #storage()} equals {@link StorageKind#FIXED} then the byte offset to the field location.
     * <p>
     * If {@link #storage()} equals {@link StorageKind#VARIABLE} then the zero-based index of the field from the
     * beginning of the variable length segment.
     * <p>
     * For all other values of {@link #storage()}, {@code offset} is ignored.
     *
     * @return If {@link #storage()} equals {@link StorageKind#FIXED} then the byte offset to the field location.
     */
    public int offset() {
        return this.offset;
    }

    /**
     * Layout of the parent scope, if a nested column, otherwise {@code null}.
     *
     * @return Layout of the parent scope, if a nested column, otherwise {@code null}.
     */
    public LayoutColumn parent() {
        return this.parent;
    }

    /**
     * The relative path of the field within its parent scope.
     * <p>
     * Paths are expressed in dotted notation: e.g. a relative {@link #path} of 'b.c' within the scope 'a' yields a
     * {@link #fullPath} of 'a.b.c'.
     *
     * @return the relative path of the field within its parent scope.
     */
    public @Nonnull Utf8String path() {
        return this.path;
    }

    /**
     * If {@link LayoutType#isBoolean()} then the zero-based extra index within the boolean byte holding the value of
     * this type, otherwise must be 0.
     *
     * @return If {@link LayoutType#isBoolean()} then the zero-based extra index within the boolean byte holding the
     * value of this type, otherwise must be 0.
     */
    public int size() {
        return this.size;
    }

    /**
     * The storage kind of the field.
     *
     * @return the storage kind of the field.
     */
    public @Nonnull StorageKind storage() {
        return this.storage;
    }

    /**
     * The physical layout type of the field.
     *
     * @return the physical layout type of the field.
     */
    public @Nonnull LayoutType type() {
        return this.type;
    }

    /**
     * The full logical type.
     *
     * @return the full logical type.
     */
    public @Nonnull TypeArgument typeArg() {
        return this.typeArg;
    }

    /**
     * For types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     *
     * @return for types with generic parameters (e.g. {@link LayoutTuple}, the type parameters.
     */
    public @Nonnull TypeArgumentList typeArgs() {
        return this.typeArgs;
    }

    /**
     * The physical layout type of the field cast to the specified type.
     *
     * @param <T> a type that implements {@code ILayoutType}.
     *
     * @return The physical layout type of the field cast to the specified type.
     */
    @SuppressWarnings("unchecked")
    public @Nonnull <T extends ILayoutType> T typeAs() {
        return (T) this.type().typeAs();
    }

    LayoutColumn index(int value) {
        this.index = value;
        return this;
    }

    LayoutColumn offset(int value) {
        this.offset = value;
        return this;
    }

    /**
     * Computes the full logical path to the column.
     *
     * @param parent The layout of the parent scope, if a nested column, otherwise null.
     * @param path   The path to the field relative to parent scope.
     * @return The full logical path.
     */
    private static @Nonnull String fullPath(final LayoutColumn parent, @Nonnull final String path) {

        if (parent != null) {
            switch (LayoutCodeTraits.clearImmutableBit(parent.type().layoutCode())) {
                case OBJECT_SCOPE:
                case SCHEMA:
                    return parent.fullPath().toString() + "." + path;
                case ARRAY_SCOPE:
                case TYPED_ARRAY_SCOPE:
                case TYPED_SET_SCOPE:
                case TYPED_MAP_SCOPE:
                    return parent.fullPath().toString() + "[]" + path;
                default:
                    final String message = lenientFormat("Parent scope type not supported: %s", parent.type().layoutCode());
                    throw new IllegalStateException(message);
            }
        }

        return path;
    }
}