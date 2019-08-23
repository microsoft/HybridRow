//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowCursorExtensions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class LayoutScope extends LayoutType {
    /**
     * Returns true if this is a fixed arity scope.
     */
    public boolean IsFixedArity;
    /**
     * Returns true if this is an indexed scope.
     */
    public boolean IsIndexedScope;
    /**
     * Returns true if this is a sized scope.
     */
    public boolean IsSizedScope;
    /**
     * Returns true if this is a typed scope.
     */
    public boolean IsTypedScope;
    /**
     * Returns true if the scope's elements cannot be updated directly.
     */
    public boolean IsUniqueScope;

    protected LayoutScope(
        LayoutCode code, boolean immutable, boolean isSizedScope, boolean isIndexedScope, boolean isFixedArity,
        boolean isUniqueScope, boolean isTypedScope
    ) {
        super(code, immutable, 0);
        this.IsSizedScope = isSizedScope;
        this.IsIndexedScope = isIndexedScope;
        this.IsFixedArity = isFixedArity;
        this.IsUniqueScope = isUniqueScope;
        this.IsTypedScope = isTypedScope;
    }

    @Override
    public final boolean getIsFixed() {
        return false;
    }

    public final Result DeleteScope(RefObject<RowBuffer> b, RefObject<RowCursor> edit) {
        Result result = LayoutType.PrepareSparseDelete(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            return result;
        }

        b.get().DeleteSparse(edit);
        return Result.Success;
    }

    /**
     * Returns true if writing an item in the specified typed scope would elide the type code
     * because it is implied by the type arguments.
     *
     * @param edit
     * @return True if the type code is implied (not written), false otherwise.
     */
    public boolean HasImplicitTypeCode(RefObject<RowCursor> edit) {
        return false;
    }

    public final Result ReadScope(RefObject<RowBuffer> b, RefObject<RowCursor> edit,
                                  OutObject<RowCursor> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.set(null);
            return result;
        }

        value.set(b.get().SparseIteratorReadScope(edit,
            this.Immutable || edit.get().immutable || edit.get().scopeType.IsUniqueScope).clone());
        return Result.Success;
    }

    public void ReadSparsePath(RefObject<RowBuffer> row, RefObject<RowCursor> edit) {
        int pathLenInBytes;
        OutObject<Integer> tempOut_pathLenInBytes = new OutObject<Integer>();
        OutObject<Integer> tempOut_pathOffset = new OutObject<Integer>();
        edit.get().pathToken = row.get().ReadSparsePathLen(edit.get().layout, edit.get().valueOffset, tempOut_pathLenInBytes, tempOut_pathOffset);
        edit.get().argValue.pathOffset = tempOut_pathOffset.get();
        pathLenInBytes = tempOut_pathLenInBytes.get();
        edit.get().valueOffset += pathLenInBytes;
    }

    public void SetImplicitTypeCode(RefObject<RowCursor> edit) {
        throw new NotImplementedException();
    }

    public abstract Result WriteScope(
        RefObject<RowBuffer> b, RefObject<RowCursor> scope, TypeArgumentList typeArgs,
        OutObject<RowCursor> value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public abstract Result WriteScope(ref RowBuffer b, ref RowCursor scope, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert);
    public abstract Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                      TypeArgumentList typeArgs, OutObject<RowCursor> value,
                                      UpdateOptions options);

    public <TContext> Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return WriteScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public virtual Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    public <TContext> Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func,
                                        UpdateOptions options) {
        RowCursor childScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.WriteScope(b, scope, typeArgs.clone(), out childScope, options);
        if (r != Result.Success) {
            return r;
        }

        RefObject<RowCursor> tempRef_childScope =
            new RefObject<RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempRef_childScope.get();
        if (r != Result.Success) {
            this.DeleteScope(b, scope);
            return r;
        }

        RefObject<RowCursor> tempRef_childScope2 =
            new RefObject<RowCursor>(childScope);
        RowCursorExtensions.Skip(scope.get().clone(), b,
            tempRef_childScope2);
        childScope = tempRef_childScope2.get();
        return Result.Success;
    }

    /**
     * A function to write content into a <see cref="RowBuffer" />.
     * <typeparam name="TContext">The type of the context value passed by the caller.</typeparam>
     *
     * @param b       The row to write to.
     * @param scope   The type of the scope to write into.
     * @param context A context value provided by the caller.
     * @return The result.
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        Result invoke(RefObject<RowBuffer> b, RefObject<RowCursor> scope, TContext context);
    }
}