// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;

public interface IDispatcher {

    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher,
                                                               Reference<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t);

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor scope,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>;
    <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher,
                                                               Reference<RowCursor> scope, LayoutColumn col,
                                                               LayoutType t, TValue value);

    void DispatchArray(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchMap(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchNullable(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                          LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchObject(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope);

    void DispatchSet(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                     LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchTuple(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                       LayoutType t, TypeArgumentList typeArgs, Object value);

    void DispatchUDT(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope,
                     LayoutType type, TypeArgumentList typeArgs, Object value);
}