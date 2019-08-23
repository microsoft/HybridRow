//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.OutObject;
import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowOptions;
import com.azure.data.cosmos.serialization.hybridrow.RowCursorExtensions;

public abstract class LayoutUniqueScope extends LayoutIndexedScope {
    protected LayoutUniqueScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isTypedScope) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(code, immutable, isSizedScope, isFixedArity: false, isUniqueScope: true, isTypedScope:
        // isTypedScope);
        super(code, immutable, isSizedScope, false, true, isTypedScope);
    }

    public abstract TypeArgument FieldType(RefObject<RowCursor> scope);

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
    public final Result Find(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                             RefObject<RowCursor> patternScope, OutObject<RowCursor> value) {
        Result result = LayoutType.PrepareSparseMove(b, scope, this, this.FieldType(scope).clone(), patternScope, UpdateOptions.Update, value.clone());

        if (result != Result.Success) {
            return result;
        }

        // Check if the search found the result.
        b.get().DeleteSparse(patternScope);

        return Result.Success;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public Result MoveField(ref RowBuffer b, ref RowCursor destinationScope, ref RowCursor
    // sourceEdit, UpdateOptions options = UpdateOptions.Upsert)
    public final Result MoveField(RefObject<RowBuffer> b, RefObject<RowCursor> destinationScope,
                                  RefObject<RowCursor> sourceEdit, UpdateOptions options) {
        RowCursor dstEdit;
        OutObject<RowCursor> tempOut_dstEdit =
            new OutObject<RowCursor>();
        Result result = LayoutType.PrepareSparseMove(b, destinationScope, this,
            this.FieldType(destinationScope).clone(), sourceEdit, options, tempOut_dstEdit);
        dstEdit = tempOut_dstEdit.get();

        if (result != Result.Success) {
            return result;
        }

        // Perform the move.
        RefObject<RowCursor> tempRef_dstEdit =
            new RefObject<RowCursor>(dstEdit);
        b.get().TypedCollectionMoveField(tempRef_dstEdit, sourceEdit, RowOptions.forValue(options));
        dstEdit = tempRef_dstEdit.get();

        // TODO: it would be "better" if the destinationScope were updated to point to the
        // highest item seen.  Then we would avoid the maximum reparse.
        destinationScope.get().count = dstEdit.count;
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

    public final Result MoveField(RefObject<RowBuffer> b, RefObject<RowCursor> destinationScope,
                                  RefObject<RowCursor> sourceEdit) {
        return MoveField(b, destinationScope, sourceEdit, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    @Override
    public <TContext> Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
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
        RefObject<RowCursor> tempRef_childScope =
            new RefObject<RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempRef_childScope.get();
        if (r != Result.Success) {
            this.DeleteScope(b, scope);
            return r;
        }

        uniqueScope.count = childScope.count;
        RefObject<RowCursor> tempRef_uniqueScope =
            new RefObject<RowCursor>(uniqueScope);
        r = b.get().TypedCollectionUniqueIndexRebuild(tempRef_uniqueScope);
        uniqueScope = tempRef_uniqueScope.get();
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

    @Override
    public <TContext> Result WriteScope(RefObject<RowBuffer> b, RefObject<RowCursor> scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return WriteScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }
}