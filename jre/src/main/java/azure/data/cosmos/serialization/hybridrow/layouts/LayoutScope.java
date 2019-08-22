//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

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

    protected LayoutScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isIndexedScope,
                          boolean isFixedArity, boolean isUniqueScope, boolean isTypedScope) {
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

    public final Result DeleteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit) {
        Result result = LayoutType.PrepareSparseDelete(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.DeleteSparse(edit);
        return Result.Success;
    }

    /**
     * Returns true if writing an item in the specified typed scope would elide the type code
     * because it is implied by the type arguments.
     *
     * @param edit
     * @return True if the type code is implied (not written), false otherwise.
     */
    public boolean HasImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        return false;
    }

    public final Result ReadScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit,
                                  tangible.OutObject<RowCursor> value) {
        Result result = LayoutType.PrepareSparseRead(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            value.argValue = null;
            return result;
        }

        value.argValue = b.argValue.SparseIteratorReadScope(edit,
            this.Immutable || edit.argValue.immutable || edit.argValue.scopeType.IsUniqueScope).clone();
        return Result.Success;
    }

    public void ReadSparsePath(tangible.RefObject<RowBuffer> row, tangible.RefObject<RowCursor> edit) {
        int pathLenInBytes;
        tangible.OutObject<Integer> tempOut_pathLenInBytes = new tangible.OutObject<Integer>();
        tangible.OutObject<Integer> tempOut_pathOffset = new tangible.OutObject<Integer>();
        edit.argValue.pathToken = row.argValue.ReadSparsePathLen(edit.argValue.layout, edit.argValue.valueOffset, tempOut_pathLenInBytes, tempOut_pathOffset);
        edit.argValue.argValue.pathOffset = tempOut_pathOffset.argValue;
        pathLenInBytes = tempOut_pathLenInBytes.argValue;
        edit.argValue.valueOffset += pathLenInBytes;
    }

    public void SetImplicitTypeCode(tangible.RefObject<RowCursor> edit) {
        Contract.Fail("No implicit type codes.");
    }

    public final abstract Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                            TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public abstract Result WriteScope(ref RowBuffer b, ref RowCursor scope, TypeArgumentList
    // typeArgs, out RowCursor value, UpdateOptions options = UpdateOptions.Upsert);
    public abstract Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                      TypeArgumentList typeArgs, tangible.OutObject<RowCursor> value,
                                      UpdateOptions options);

    public <TContext> Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return WriteScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public virtual Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    public <TContext> Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func,
                                        UpdateOptions options) {
        RowCursor childScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.WriteScope(b, scope, typeArgs.clone(), out childScope, options);
        if (r != Result.Success) {
            return r;
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempRef_childScope.argValue;
        if (r != Result.Success) {
            this.DeleteScope(b, scope);
            return r;
        }

        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope2 =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
        Microsoft.Azure.Cosmos.Serialization.HybridRow.RowCursorExtensions.Skip(scope.argValue.clone(), b,
            tempRef_childScope2);
        childScope = tempRef_childScope2.argValue;
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
        Result invoke(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, TContext context);
    }
}