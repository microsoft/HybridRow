//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.layouts;

import azure.data.cosmos.serialization.hybridrow.Result;
import azure.data.cosmos.serialization.hybridrow.RowBuffer;
import azure.data.cosmos.serialization.hybridrow.RowCursor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Describes the physical byte layout of a hybrid row field of a specific physical type
 * <typeparamref name="T" />.
 *
 *
 * <see cref="LayoutType{T}" /> is an immutable, stateless, helper class.  It provides
 * methods for manipulating hybrid row fields of a particular type, and properties that describe the
 * layout of fields of that type.
 * <para />
 * <see cref="LayoutType{T}" /> is immutable.
 */
public abstract class LayoutType<T> extends LayoutType {
    private TypeArgument typeArg = new TypeArgument();

    public LayoutType(LayoutCode code, int size) {
        super(code, false, size);
        this.typeArg = new TypeArgument(this);
    }

    public final Result DeleteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                    LayoutColumn col) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        if (col.getNullBit().getIsInvalid()) {
            // Cannot delete a non-nullable fixed column.
            return Result.TypeMismatch;
        }

        b.argValue.UnsetBit(scope.argValue.start, col.getNullBit().clone());
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    public final Result DeleteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit) {
        Result result = LayoutType.PrepareSparseDelete(b, edit, this.LayoutCode);
        if (result != Result.Success) {
            return result;
        }

        b.argValue.DeleteSparse(edit);
        return Result.Success;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accomodate
     * a decrease in required space.  If no value exists this operation is a no-op.
     */
    public final Result DeleteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                       LayoutColumn col) {
        checkArgument(scope.argValue.scopeType instanceof LayoutUDT);
        if (scope.argValue.immutable) {
            return Result.InsufficientPermissions;
        }

        boolean exists = b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone());
        if (exists) {
            int varOffset = b.argValue.ComputeVariableValueOffset(scope.argValue.layout, scope.argValue.start,
                col.getOffset());
            b.argValue.DeleteVariable(varOffset, this.getIsVarint());
            b.argValue.UnsetBit(scope.argValue.start, col.getNullBit().clone());
        }

        return Result.Success;
    }

    public final Result HasValue(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                 LayoutColumn col) {
        if (!b.argValue.ReadBit(scope.argValue.start, col.getNullBit().clone())) {
            return Result.NotFound;
        }

        return Result.Success;
    }

    public abstract Result ReadFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                     LayoutColumn col, tangible.OutObject<T> value);

    public abstract Result ReadSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, tangible.OutObject<T> value);

    public Result ReadVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope, LayoutColumn col
        , tangible.OutObject<T> value) {
        value.argValue = null;
        return Result.Failure;
    }

    public abstract Result WriteFixed(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                      LayoutColumn col, T value);

    public abstract Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, T value);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public abstract Result WriteSparse(ref RowBuffer b, ref RowCursor edit, T value, UpdateOptions
    // options = UpdateOptions.Upsert);
    public abstract Result WriteSparse(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> edit, T value, UpdateOptions options);

    public Result WriteVariable(tangible.RefObject<RowBuffer> b, tangible.RefObject<RowCursor> scope,
                                LayoutColumn col, T value) {
        return Result.Failure;
    }

    abstract TypeArgument getTypeArg();
}