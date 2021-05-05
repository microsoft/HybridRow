// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;
import com.azure.data.cosmos.serialization.hybridrow.RowOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LayoutUniqueScope extends LayoutIndexedScope implements ILayoutType {

    protected LayoutUniqueScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isTypedScope) {
        super(code, immutable, isSizedScope, false, true, isTypedScope);
    }

    @Nonnull
    public abstract TypeArgument fieldType(@Nonnull RowCursor scope);

    /**
     * Search for a matching field within a unique index.
     * <p>
     * The pattern field is deleted whether the find succeeds or fails.
     *
     * @param buffer       The row to search.
     * @param scope        The parent unique index edit to search.
     * @param patternScope The parent edit from which the match pattern is read.
     * @param value        If successful, the updated edit.
     * @return Success a matching field exists in the unique index, NotFound if no match is found, the error code
     * otherwise.
     */
    @Nonnull
    public final Result find(RowBuffer buffer, RowCursor scope, RowCursor patternScope, Out<RowCursor> value) {

        Result result = LayoutType.prepareSparseMove(buffer, scope, this, this.fieldType(scope), patternScope,
            UpdateOptions.UPDATE, value);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.deleteSparse(patternScope);
        return Result.SUCCESS;
    }

    /**
     * Moves an existing sparse field into the unique index.
     * <p>
     * The source field MUST be a field whose type arguments match the element type of the destination unique index.
     * The source field is deleted whether the move succeeds or fails.
     *
     * @param buffer           The row to move within.
     * @param destinationScope The parent unique indexed edit into which the field should be moved.
     * @param sourceEdit       The field to be moved.
     * @param options          The move options.
     * @return Success if the field is permitted within the unique index, the error code otherwise.
     */
    @Nonnull
    public final Result moveField(
        RowBuffer buffer, RowCursor destinationScope, RowCursor sourceEdit, UpdateOptions options) {

        Out<RowCursor> dstEdit = new Out<>();

        Result result = LayoutType.prepareSparseMove(
            buffer, destinationScope, this, this.fieldType(destinationScope), sourceEdit, options, dstEdit);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.typedCollectionMoveField(dstEdit.get(), sourceEdit, RowOptions.from(options.value()));

        // TODO: it would be "better" if the destinationScope were updated to point to the highest item seen. Then we
        // would avoid the maximum reparse

        destinationScope.count(dstEdit.get().count());
        return Result.SUCCESS;
    }

    /**
     * Moves an existing sparse field into the unique index.
     *
     * @param buffer           The row to move within.
     * @param destinationScope The parent unique indexed edit into which the field should be moved.
     * @param sourceEdit       The field to be moved.
     * @return {@link Result#SUCCESS} if the field is moved; an error {@link Result} otherwise.
     * <p>
     * The source field MUST be a field whose type arguments match the element type of the
     * destination unique index.
     * <p>
     * The source field is deleted whether the move succeeds or fails.
     */
    @Nonnull
    public final Result moveField(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor destinationScope,
        @Nonnull final RowCursor sourceEdit) {
        return this.moveField(buffer, destinationScope, sourceEdit, UpdateOptions.UPSERT);
    }

    @Override
    @Nonnull
    public <TContext> Result writeScope(
        @Nonnull final RowBuffer buffer,
        @Nonnull final RowCursor scope,
        @Nonnull final TypeArgumentList typeArgs,
        @Nullable final TContext context,
        @Nullable final WriterFunc<TContext> func,
        @Nonnull final UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");

        final Out<RowCursor> uniqueScope = new Out<>();
        Result result;

        result = this.writeScope(buffer, scope, typeArgs, options, uniqueScope);

        if (result != Result.SUCCESS) {
            return result;
        }

        RowCursor childScope = uniqueScope.get().deferUniqueIndex(true);
        result = func == null ? null : func.invoke(buffer, childScope, context);

        if (result != null && result != Result.SUCCESS) {
            this.deleteScope(buffer, scope);
            return result;
        }

        uniqueScope.get().count(childScope.count());
        result = buffer.typedCollectionUniqueIndexRebuild(uniqueScope.get());

        if (result != Result.SUCCESS) {
            this.deleteScope(buffer, scope);
            return result;
        }

        RowCursors.skip(scope, buffer, childScope);
        return Result.SUCCESS;
    }
}