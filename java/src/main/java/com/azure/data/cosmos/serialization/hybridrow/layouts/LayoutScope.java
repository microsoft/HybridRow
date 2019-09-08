// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LayoutScope extends LayoutType {

    private final boolean isFixedArity;
    private final boolean isIndexedScope;
    private final boolean isSizedScope;
    private final boolean isTypedScope;
    private final boolean isUniqueScope;

    protected LayoutScope(
        @Nonnull final LayoutCode code,
        final boolean immutable,
        final boolean isSizedScope,
        final boolean isIndexedScope,
        final boolean isFixedArity,
        final boolean isUniqueScope,
        final boolean isTypedScope) {

        super(code, immutable, 0);
        this.isSizedScope = isSizedScope;
        this.isIndexedScope = isIndexedScope;
        this.isFixedArity = isFixedArity;
        this.isUniqueScope = isUniqueScope;
        this.isTypedScope = isTypedScope;
    }

    /**
     * Returns {@code false} to indicate that a {@link LayoutScope} is a variable length, not fixed length layout type
     *
     * @return {@code false}
     */
    @Override
    public boolean isFixed() {
        return false;
    }

    /**
     * Returns true if this is a fixed arity scope.
     */
    public boolean isFixedArity() {
        return this.isFixedArity;
    }

    /**
     * Returns true if this is an indexed scope.
     */
    public boolean isIndexedScope() {
        return this.isIndexedScope;
    }

    /**
     * Returns true if this is a sized scope.
     */
    public boolean isSizedScope() {
        return this.isSizedScope;
    }

    /**
     * Returns true if this is a typed scope.
     */
    public boolean isTypedScope() {
        return this.isTypedScope;
    }

    /**
     * Returns true if the scope's elements cannot be updated directly.
     */
    public boolean isUniqueScope() {
        return this.isUniqueScope;
    }

    @Nonnull
    public final Result deleteScope(@Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");

        Result result = LayoutType.prepareSparseDelete(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.deleteSparse(edit);
        return Result.SUCCESS;
    }

    /**
     * {@code true} if writing an item in the specified typed scope would elide the type code because it is implied by
     * the type arguments
     *
     * @param edit a non-null {@link RowCursor} specifying a typed scope
     * @return {@code true} if the type code is implied (not written); {@code false} otherwise.
     */
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        return false;
    }

    @Nonnull
    public final Result readScope(
        @Nonnull final RowBuffer buffer, @Nonnull final RowCursor edit, @Nonnull final Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        boolean immutable = this.isImmutable() || edit.immutable() || edit.scopeType().isUniqueScope();
        value.set(buffer.sparseIteratorReadScope(edit, immutable));
        return Result.SUCCESS;
    }

    public void readSparsePath(@Nonnull final RowBuffer row, @Nonnull final RowCursor edit) {
        Out<Integer> pathLenInBytes = new Out<>();
        Out<Integer> pathOffset = new Out<>();
        edit.pathToken(row.readSparsePathLen(edit.layout(), edit.valueOffset(), pathLenInBytes, pathOffset));
        edit.pathOffset(pathOffset.get());
        edit.valueOffset(edit.valueOffset() + pathLenInBytes.get());
    }

    public void setImplicitTypeCode(final RowCursor edit) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public abstract Result writeScope(
        RowBuffer buffer,
        RowCursor scope,
        TypeArgumentList typeArgs, Out<RowCursor> value);

    @Nonnull
    public abstract Result writeScope(
        RowBuffer buffer,
        RowCursor scope,
        TypeArgumentList typeArgs,
        UpdateOptions options, Out<RowCursor> value);

    @Nonnull
    public <TContext> Result writeScope(
        RowBuffer buffer,
        RowCursor scope,
        TypeArgumentList typeArgs,
        TContext context, WriterFunc<TContext> func) {
        return this.writeScope(buffer, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }

    @Nonnull
    public <TContext> Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nullable TContext context,
        @Nullable WriterFunc<TContext> func,
        @Nonnull UpdateOptions options) {

        final Out<RowCursor> out = new Out<>();
        Result result = this.writeScope(buffer, scope, typeArgs, options, out);

        if (result != Result.SUCCESS) {
            return result;
        }

        final RowCursor childScope = out.get();

        if (func != null) {
            result = func.invoke(buffer, childScope, context);
            if (result != Result.SUCCESS) {
                this.deleteScope(buffer, scope);
                return result;
            }
        }

        RowCursors.skip(scope, buffer, childScope);
        return Result.SUCCESS;
    }

    /**
     * A functional interfaced that can be used to write content to a {@link RowBuffer}
     *
     * @param <TContext> The type of the context value passed by the caller
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        /**
         * Writes content to a {@link RowBuffer}
         *
         * @param buffer  The row to write to
         * @param scope   The type of the scope to write into
         * @param context A context value provided by the caller
         * @return The result
         */
        @Nonnull
        Result invoke(@Nonnull final RowBuffer buffer, @Nonnull final RowCursor scope, @Nullable TContext context);
    }
}