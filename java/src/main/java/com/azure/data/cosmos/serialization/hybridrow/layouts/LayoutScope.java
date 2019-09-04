// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LayoutScope extends LayoutType {

    private final boolean isFixedArity;
    private final boolean isIndexedScope;
    private final boolean isSizedScope;
    private final boolean isTypedScope;
    private final boolean isUniqueScope;

    protected LayoutScope(
        @Nonnull final LayoutCode code, final boolean immutable, final boolean isSizedScope,
        final boolean isIndexedScope, final boolean isFixedArity, final boolean isUniqueScope, boolean isTypedScope) {

        super(code, immutable, 0);
        this.isSizedScope = isSizedScope;
        this.isIndexedScope = isIndexedScope;
        this.isFixedArity = isFixedArity;
        this.isUniqueScope = isUniqueScope;
        this.isTypedScope = isTypedScope;
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

    public final Result deleteScope(@Nonnull final RowBuffer b, @Nonnull final RowCursor edit) {

        checkNotNull(b);
        checkNotNull(edit);

        Result result = LayoutType.prepareSparseDelete(b, edit, this.layoutCode());

        if (result != Result.Success) {
            return result;
        }

        b.DeleteSparse(edit);
        return Result.Success;
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
        @Nonnull final RowBuffer b, @Nonnull final RowCursor edit, @Nonnull final Out<RowCursor> value) {

        checkNotNull(b);
        checkNotNull(edit);
        checkNotNull(value);

        Result result = LayoutType.prepareSparseRead(b, edit, this.layoutCode());

        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        boolean immutable = this.isImmutable() || edit.immutable() || edit.scopeType().isUniqueScope();
        value.set(b.sparseIteratorReadScope(edit, immutable));
        return Result.Success;
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

    public abstract Result writeScope(
        RowBuffer b,
        RowCursor scope,
        TypeArgumentList typeArgs, Out<RowCursor> value);

    public abstract Result writeScope(
        RowBuffer b,
        RowCursor scope,
        TypeArgumentList typeArgs,
        UpdateOptions options, Out<RowCursor> value);

    public <TContext> Result writeScope(
        RowBuffer b,
        RowCursor scope,
        TypeArgumentList typeArgs,
        TContext context, WriterFunc<TContext> func) {
        return this.writeScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public virtual Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    public <TContext> Result writeScope(
        RowBuffer b,
        RowCursor scope,
        TypeArgumentList typeArgs,
        TContext context, WriterFunc<TContext> func, UpdateOptions options) {

        final Out<RowCursor> out = new Out<>();
        Result result = this.writeScope(b, scope, typeArgs, options, out);

        if (result != Result.Success) {
            return result;
        }

        final RowCursor childScope = out.get();

        if (func != null) {
            result = func.invoke(b, childScope, context);
            if (result != Result.Success) {
                this.deleteScope(b, scope);
                return result;
            }
        }

        RowCursors.skip(scope, b, childScope);
        return Result.Success;
    }

    /**
     * A function to write content into a {@link RowBuffer}.
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param b       The row to write to.
     * @param scope   The type of the scope to write into.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        @Nonnull Result invoke(RowBuffer b, RowCursor scope, TContext context);
    }
}