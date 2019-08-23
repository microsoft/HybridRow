//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct NullRowDispatcher : IDispatcher
public final class NullRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                RefObject<RowBuffer> tempRef_Row =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadFixed(tempRef_Row, root, col, out _));
                dispatcher.get().argValue.Row = tempRef_Row.get();
                break;
            case Variable:
                RefObject<RowBuffer> tempRef_Row2 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadVariable(tempRef_Row2, root, col, out _));
                dispatcher.get().argValue.Row = tempRef_Row2.get();
                break;
            default:
                RefObject<RowBuffer> tempRef_Row3 =
                    new RefObject<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadSparse(tempRef_Row3, root, out _));
                dispatcher.get().argValue.Row = tempRef_Row3.get();
                break;
        }
    }

    public void DispatchArray(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchMap(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }

    public void DispatchNullable(RefObject<RowOperationDispatcher> dispatcher,
                                 RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
    }

    public void DispatchObject(RefObject<RowOperationDispatcher> dispatcher,
                               RefObject<RowCursor> scope) {
    }

    public void DispatchSet(RefObject<RowOperationDispatcher> dispatcher,
                            RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
    }

    public void DispatchTuple(RefObject<RowOperationDispatcher> dispatcher,
                              RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchUDT(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }
}