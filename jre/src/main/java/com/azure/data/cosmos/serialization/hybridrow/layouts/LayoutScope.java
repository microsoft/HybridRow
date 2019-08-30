//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LayoutScope extends LayoutType {

    private boolean isFixedArity;
    private boolean isIndexedScope;
    private boolean isSizedScope;
    private boolean isTypedScope;
    private boolean isUniqueScope;

    protected LayoutScope(
        LayoutCode code, boolean immutable, boolean isSizedScope, boolean isIndexedScope, boolean isFixedArity,
        boolean isUniqueScope, boolean isTypedScope
    ) {
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

    public final Result DeleteScope(Reference<RowBuffer> b, Reference<RowCursor> edit) {

        Result result = LayoutType.prepareSparseDelete(b, edit, this.LayoutCode);

        if (result != Result.Success) {
            return result;
        }

        b.get().deleteSparse(edit);
        return Result.Success;
    }

    /**
     * {@code true} if writing an item in the specified typed scope would elide the type code because it is implied by the
     * type arguments
     *
     * @param edit a non-null {@link RowCursor} specifying a typed scope
     * @return {@code true} if the type code is implied (not written); {@code false} otherwise.
     */
    public boolean hasImplicitTypeCode(@Nonnull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        return false;
    }

    public final Result ReadScope(Reference<RowBuffer> b, Reference<RowCursor> edit, Out<RowCursor> value) {

        Result result = LayoutType.prepareSparseRead(b, edit, this.LayoutCode);

        if (result != Result.Success) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(b.get().sparseIteratorReadScope(edit,
            this.Immutable || edit.get().immutable() || edit.get().scopeType().isUniqueScope()).clone());

        return Result.Success;
    }

    public void ReadSparsePath(Reference<RowBuffer> row, Reference<RowCursor> edit) {
        int pathLenInBytes;
        Out<Integer> tempOut_pathLenInBytes = new Out<Integer>();
        Out<Integer> tempOut_pathOffset = new Out<Integer>();
        edit.get().pathToken = row.get().ReadSparsePathLen(edit.get().layout(), edit.get().valueOffset(), tempOut_pathLenInBytes, tempOut_pathOffset);
        edit.get().pathOffset = tempOut_pathOffset.get();
        pathLenInBytes = tempOut_pathLenInBytes.get();
        edit.get().valueOffset(edit.get().valueOffset() + pathLenInBytes);
    }

    public void SetImplicitTypeCode(Reference<RowCursor> edit) {
        throw new NotImplementedException();
    }

    public abstract Result WriteScope(
        Reference<RowBuffer> b, Reference<RowCursor> scope, TypeArgumentList typeArgs,
        Out<RowCursor> value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public abstract Result WriteScope(ref RowBuffer b, ref RowCursor scope, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert);
    public abstract Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                      TypeArgumentList typeArgs, Out<RowCursor> value,
                                      UpdateOptions options);

    public <TContext> Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return WriteScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public virtual Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    public <TContext> Result WriteScope(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func,
                                        UpdateOptions options) {
        RowCursor childScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.WriteScope(b, scope, typeArgs.clone(), out childScope, options);
        if (r != Result.Success) {
            return r;
        }

        Reference<RowCursor> tempReference_childScope =
            new Reference<RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempReference_childScope.get();
        if (r != Result.Success) {
            this.DeleteScope(b, scope);
            return r;
        }

        Reference<RowCursor> tempReference_childScope2 =
            new Reference<RowCursor>(childScope);
        RowCursors.skip(scope.get().clone(), b,
            tempReference_childScope2);
        childScope = tempReference_childScope2.get();
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
        Result invoke(Reference<RowBuffer> b, Reference<RowCursor> scope, TContext context);
    }
}