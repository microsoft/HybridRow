//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct NullRowDispatcher : IDispatcher
public final class NullRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue expected = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> root, LayoutColumn col, LayoutType t, TValue expected) {
        switch (col == null ? null : col.getStorage()) {
            case Fixed:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadFixed(tempRef_Row, root, col, out _));
                dispatcher.argValue.argValue.Row = tempRef_Row.argValue;
                break;
            case Variable:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row2 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadVariable(tempRef_Row2, root, col, out _));
                dispatcher.argValue.argValue.Row = tempRef_Row2.argValue;
                break;
            default:
                tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer> tempRef_Row3 =
                    new tangible.RefObject<azure.data.cosmos.serialization.hybridrow.RowBuffer>(dispatcher.argValue.Row);
                TValue _;
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                ResultAssert.NotFound(t.<TLayout>TypeAs().ReadSparse(tempRef_Row3, root, out _));
                dispatcher.argValue.argValue.Row = tempRef_Row3.argValue;
                break;
        }
    }

    public void DispatchArray(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchMap(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }

    public void DispatchNullable(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                 tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
    }

    public void DispatchObject(tangible.RefObject<RowOperationDispatcher> dispatcher,
                               tangible.RefObject<RowCursor> scope) {
    }

    public void DispatchSet(tangible.RefObject<RowOperationDispatcher> dispatcher,
                            tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
    }

    public void DispatchTuple(tangible.RefObject<RowOperationDispatcher> dispatcher,
                              tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
    }

    public void DispatchUDT(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
    }
}