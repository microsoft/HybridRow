//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;
import azure.data.cosmos.serialization.hybridrow.RowOptions;

public abstract class LayoutUniqueScope extends LayoutIndexedScope {
    protected LayoutUniqueScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isTypedScope) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(code, immutable, isSizedScope, isFixedArity: false, isUniqueScope: true, isTypedScope:
        // isTypedScope);
        super(code, immutable, isSizedScope, isFixedArity:false, isUniqueScope:true, isTypedScope:isTypedScope)
    }

    public abstract TypeArgument FieldType(tangible.RefObject<RowCursor> scope);

    /**
     * Search for a matching field within a unique index.
     *
     * @param b            The row to search.
     * @param scope        The parent unique index edit to search.
     * @param patternScope The parent edit from which the match pattern is read.
     * @param value        If successful, the updated edit.
     * @return Success a matching field exists in the unique index, NotFound if no match is found, the
     * error code otherwise.
     * <p>
     * The pattern field is delete whether the find succeeds or fails.
     */
    public final Result Find(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                             tangible.RefObject<RowCursor> patternScope, tangible.OutObject<RowCursor> value) {
        Result result = LayoutType.PrepareSparseMove(b, scope, this, this.FieldType(scope).clone(), patternScope, UpdateOptions.Update, value.clone());

        if (result != Result.Success) {
            return result;
        }

        // Check if the search found the result.
        b.argValue.DeleteSparse(patternScope);

        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result MoveField(ref RowBuffer b, ref RowCursor destinationScope, ref RowCursor
    // sourceEdit, UpdateOptions options = UpdateOptions.Upsert)
    public final Result MoveField(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> destinationScope,
                                  tangible.RefObject<RowCursor> sourceEdit, UpdateOptions options) {
        RowCursor dstEdit;
        tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempOut_dstEdit =
            new tangible.OutObject<azure.data.cosmos.serialization.hybridrow.RowCursor>();
        Result result = LayoutType.PrepareSparseMove(b, destinationScope, this,
            this.FieldType(destinationScope).clone(), sourceEdit, options, tempOut_dstEdit);
        dstEdit = tempOut_dstEdit.argValue;

        if (result != Result.Success) {
            return result;
        }

        // Perform the move.
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_dstEdit =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(dstEdit);
        b.argValue.TypedCollectionMoveField(tempRef_dstEdit, sourceEdit, RowOptions.forValue(options));
        dstEdit = tempRef_dstEdit.argValue;

        // TODO: it would be "better" if the destinationScope were updated to point to the
        // highest item seen.  Then we would avoid the maximum reparse.
        destinationScope.argValue.count = dstEdit.count;
        return Result.Success;
    }

    /**
     * Moves an existing sparse field into the unique index.
     *
     * @param b                The row to move within.
     * @param destinationScope The parent unique indexed edit into which the field should be moved.
     * @param sourceEdit       The field to be moved.
     * @param options          The move options.
     * @return Success if the field is permitted within the unique index, the error code otherwise.
     * <p>
     * The source field MUST be a field whose type arguments match the element type of the
     * destination unique index.
     * <para />
     * The source field is delete whether the move succeeds or fails.
     */

    public final Result MoveField(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> destinationScope,
                                  tangible.RefObject<RowCursor> sourceEdit) {
        return MoveField(b, destinationScope, sourceEdit, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    @Override
    public <TContext> Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func,
                                        UpdateOptions options) {
        RowCursor uniqueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        Result r = this.WriteScope(b, scope, typeArgs.clone(), out uniqueScope, options);
        if (r != Result.Success) {
            return r;
        }

        RowCursor childScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        uniqueScope.Clone(out childScope);
        childScope.deferUniqueIndex = true;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_childScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempRef_childScope.argValue;
        if (r != Result.Success) {
            this.DeleteScope(b, scope);
            return r;
        }

        uniqueScope.count = childScope.count;
        tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor> tempRef_uniqueScope =
            new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowCursor>(uniqueScope);
        r = b.argValue.TypedCollectionUniqueIndexRebuild(tempRef_uniqueScope);
        uniqueScope = tempRef_uniqueScope.argValue;
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

    @Override
    public <TContext> Result WriteScope(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return WriteScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }
}