//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package azure.data.cosmos.serialization.hybridrow.unit;

import azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatcher {

    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                                               tangible.RefObject<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor scope,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>;
    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(tangible.RefObject<RowOperationDispatcher> dispatcher,
                                                               tangible.RefObject<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t, TValue value);

    void DispatchArray(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchMap(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchNullable(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                          LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchObject(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope);

    void DispatchSet(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchTuple(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchUDT(tangible.RefObject<RowOperationDispatcher> dispatcher, tangible.RefObject<RowCursor> scope,
                     LayoutType type, TypeArgumentList typeArgs, Object value);
}