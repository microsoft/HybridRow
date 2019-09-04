// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.RowOptions;
import com.azure.data.cosmos.serialization.hybridrow.RowCursors;

public abstract class LayoutUniqueScope extends LayoutIndexedScope {
    protected LayoutUniqueScope(LayoutCode code, boolean immutable, boolean isSizedScope, boolean isTypedScope) {
        // TODO: C# TO JAVA CONVERTER: C# to Java Converter could not resolve the named parameters in the
        // following line:
        //ORIGINAL LINE: base(code, immutable, isSizedScope, isFixedArity: false, isUniqueScope: true, isTypedScope:
        // isTypedScope);
        super(code, immutable, isSizedScope, false, true, isTypedScope);
    }

    public abstract TypeArgument FieldType(Reference<RowCursor> scope);

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
    public final Result Find(Reference<RowBuffer> b, Reference<RowCursor> scope,
                             Reference<RowCursor> patternScope, Out<RowCursor> value) {
        Result result = LayoutType.prepareSparseMove(b, scope, this, this.FieldType(scope).clone(), patternScope, UpdateOptions.Update, value.clone());

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
    public final Result MoveField(Reference<RowBuffer> b, Reference<RowCursor> destinationScope,
                                  Reference<RowCursor> sourceEdit, UpdateOptions options) {
        RowCursor dstEdit;
        Out<RowCursor> tempOut_dstEdit =
            new Out<RowCursor>();
        Result result = LayoutType.prepareSparseMove(b, destinationScope, this,
            this.FieldType(destinationScope).clone(), sourceEdit, options, tempOut_dstEdit);
        dstEdit = tempOut_dstEdit.get();

        if (result != Result.Success) {
            return result;
        }

        // Perform the move.
        Reference<RowCursor> tempReference_dstEdit =
            new Reference<RowCursor>(dstEdit);
        b.get().TypedCollectionMoveField(tempReference_dstEdit, sourceEdit, RowOptions.from(options));
        dstEdit = tempReference_dstEdit.get();

        // TODO: it would be "better" if the destinationScope were updated to point to the
        // highest item seen.  Then we would avoid the maximum reparse.
        destinationScope.get().count(dstEdit.count());
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

    public final Result MoveField(Reference<RowBuffer> b, Reference<RowCursor> destinationScope,
                                  Reference<RowCursor> sourceEdit) {
        return MoveField(b, destinationScope, sourceEdit, UpdateOptions.Upsert);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteScope<TContext>(ref RowBuffer b, ref RowCursor scope,
    // TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func, UpdateOptions options = UpdateOptions
    // .Upsert)
    @Override
    public <TContext> Result writeScope(RowBuffer b, RowCursor scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func,
                                        UpdateOptions options) {
        RowCursor uniqueScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        Result r = this.WriteScope(b, scope, typeArgs.clone(), out uniqueScope, options);
        if (r != Result.Success) {
            return r;
        }

        RowCursor childScope;
        // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'Out' helper class unless the method is within the code being modified:
        uniqueScope.Clone(out childScope);
        childScope.deferUniqueIndex = true;
        Reference<RowCursor> tempReference_childScope =
            new Reference<RowCursor>(childScope);
        // TODO: C# TO JAVA CONVERTER: The following line could not be converted:
        r = func == null ? null : func.Invoke(ref b, ref childScope, context) ??Result.Success;
        childScope = tempReference_childScope.get();
        if (r != Result.Success) {
            this.deleteScope(b, scope);
            return r;
        }

        uniqueScope.count(childScope.count());
        Reference<RowCursor> tempReference_uniqueScope =
            new Reference<RowCursor>(uniqueScope);
        r = b.get().TypedCollectionUniqueIndexRebuild(tempReference_uniqueScope);
        uniqueScope = tempReference_uniqueScope.get();
        if (r != Result.Success) {
            this.deleteScope(b, scope);
            return r;
        }

        Reference<RowCursor> tempReference_childScope2 =
            new Reference<RowCursor>(childScope);
        RowCursors.skip(scope.get().clone(), b,
            tempReference_childScope2);
        childScope = tempReference_childScope2.get();
        return Result.Success;
    }

    @Override
    public <TContext> Result writeScope(RowBuffer b, RowCursor scope,
                                        TypeArgumentList typeArgs, TContext context, WriterFunc<TContext> func) {
        return writeScope(b, scope, typeArgs, context, func, UpdateOptions.Upsert);
    }
}