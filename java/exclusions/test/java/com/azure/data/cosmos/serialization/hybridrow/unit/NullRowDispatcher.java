// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct NullRowDispatcher : IDispatcher
public final class NullRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                Reference<RowBuffer> tempReference_Row =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadFixed(tempReference_Row, root, col, out _));
                dispatcher.get().argValue.Row = tempReference_Row.get();
                break;
            case Variable:
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadVariable(tempReference_Row2, root, col, out _));
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                break;
            default:
                Reference<RowBuffer> tempReference_Row3 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'Out' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadSparse(tempReference_Row3, root, out _));
                dispatcher.get().argValue.Row = tempReference_Row3.get();
                break;
        }
    }

    public void DispatchArray(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchMap(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }

    public void DispatchNullable(Reference<RowOperationDispatcher> dispatcher,
                                 Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
    }

    public void DispatchObject(Reference<RowOperationDispatcher> dispatcher,
                               Reference<RowCursor> scope) {
    }

    public void DispatchSet(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
    }

    public void DispatchTuple(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchUDT(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }
}