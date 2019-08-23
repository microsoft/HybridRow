//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.RefObject;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatcher {

    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher,
                                                               RefObject<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor scope,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>;
    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(RefObject<RowOperationDispatcher> dispatcher,
                                                               RefObject<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t, TValue value);

    void DispatchArray(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchMap(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchNullable(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                          LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchObject(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope);

    void DispatchSet(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchTuple(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchUDT(RefObject<RowOperationDispatcher> dispatcher, RefObject<RowCursor> scope,
                     LayoutType type, TypeArgumentList typeArgs, Object value);
}