//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.layouts;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.Result;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Describes the physical byte layout of a hybrid row field of a specific physical type
 * <typeparamref name="T" />.
 *
 *
 * {@link LayoutType{T}} is an immutable, stateless, helper class.  It provides
 * methods for manipulating hybrid row fields of a particular type, and properties that describe the
 * layout of fields of that type.
 * <para />
 * {@link LayoutType{T}} is immutable.
 */
public abstract class LayoutType<T> extends LayoutType {
    private TypeArgument typeArg = new TypeArgument();

    public LayoutType(LayoutCode code, int size) {
        super(code, false, size);
        this.typeArg = new TypeArgument(this);
    }

    public final Result DeleteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                    LayoutColumn col) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        if (col.getNullBit().getIsInvalid()) {
            // Cannot delete a non-nullable fixed column.
            return Result.TypeMismatch;
        }

        b.get().UnsetBit(scope.get().start, col.getNullBit().clone());
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    public final Result DeleteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit) {
        Result result = LayoutType.PrepareSparseDelete(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            return result;
        }

        b.get().DeleteSparse(edit);
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    public final Result DeleteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                       LayoutColumn col) {
        checkArgument(scope.get().scopeType instanceof LayoutUDT);
        if (scope.get().immutable) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.get().ReadBit(scope.get().start, col.getNullBit().clone());
        if (exists) {
            int varOffset = b.get().ComputeVariableValueOffset(scope.get().layout, scope.get().start,
                col.getOffset());
            b.get().DeleteVariable(varOffset, this.getIsVarint());
            b.get().UnsetBit(scope.get().start, col.getNullBit().clone());
        }

        return Result.Success;
    }

    public final Result HasValue(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                 LayoutColumn col) {
        if (!b.get().ReadBit(scope.get().start, col.getNullBit().clone())) {
            return Result.NotFound;
        }

        return Result.Success;
    }

    public abstract Result ReadFixed(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                     LayoutColumn col, Out<T> value);

    public abstract Result ReadSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, Out<T> value);

    public Result ReadVariable(Reference<RowBuffer> b, Reference<RowCursor> scope, LayoutColumn col
        , Out<T> value) {
        value.set(null);
        return Result.Failure;
    }

    public abstract Result WriteFixed(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                      LayoutColumn col, T value);

    public abstract Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, T value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public abstract Result WriteSparse(ref RowBuffer b, ref RowCursor edit, T value, UpdateOptions
    // options = UpdateOptions.Upsert);
    public abstract Result WriteSparse(Reference<RowBuffer> b, Reference<RowCursor> edit, T value, UpdateOptions options);

    public Result WriteVariable(Reference<RowBuffer> b, Reference<RowCursor> scope,
                                LayoutColumn col, T value) {
        return Result.Failure;
    }

    abstract TypeArgument getTypeArg();
}