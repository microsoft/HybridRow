//------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------

package com.azure.data.cosmos.serialization.hybridrow.unit;

import com.azure.data.cosmos.core.Out;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.core.Reference;
import com.azure.data.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.data.cosmos.serialization.hybridrow.RowCursor;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutIndexedScope;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutType;
import com.azure.data.cosmos.serialization.hybridrow.layouts.LayoutTypedSet;
import com.azure.data.cosmos.serialization.hybridrow.layouts.TypeArgumentList;

import java.util.List;

//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class may differ
// from the original:
//ORIGINAL LINE: internal struct DeleteRowDispatcher : IDispatcher
public final class DeleteRowDispatcher implements IDispatcher {

    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t) {
        Dispatch(dispatcher, root, col, t, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public void Dispatch<TLayout, TValue>(ref RowOperationDispatcher dispatcher, ref RowCursor root,
    // LayoutColumn col, LayoutType t, TValue value = default) where TLayout : LayoutType<TValue>
    public <TLayout extends LayoutType<TValue>, TValue> void Dispatch(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> root, LayoutColumn col, LayoutType t, TValue value) {
        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<TLayout>TypeAs().DeleteSparse(tempReference_Row, root));
        dispatcher.get().argValue.Row = tempReference_Row.get();
    }

    public void DispatchArray(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor arrayScope;
        Out<RowCursor> tempOut_arrayScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().ReadScope(tempReference_Row, scope, tempOut_arrayScope));
        arrayScope = tempOut_arrayScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();

        if (!arrayScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                assert arrayScope.MoveNext(tempReference_Row2);
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref arrayScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        Reference<RowBuffer> tempReference_Row3 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedArray>TypeAs().DeleteScope(tempReference_Row3, scope));
        dispatcher.get().argValue.Row = tempReference_Row3.get();
    }

    public void DispatchMap(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.getCount() == 2);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor mapScope;
        Out<RowCursor> tempOut_mapScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().ReadScope(tempReference_Row, scope, tempOut_mapScope));
        mapScope = tempOut_mapScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        if (!mapScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                assert mapScope.MoveNext(tempReference_Row2);
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref mapScope, null, LayoutType.TypedTuple, typeArgs.clone(), item);
            }
        }

        Reference<RowBuffer> tempReference_Row3 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedMap>TypeAs().DeleteScope(tempReference_Row3, scope));
        dispatcher.get().argValue.Row = tempReference_Row3.get();
    }

    public void DispatchNullable(Reference<RowOperationDispatcher> dispatcher,
                                 Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                                 Object value) {
        checkArgument(typeArgs.getCount() == 1);
        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutNullable>TypeAs().DeleteScope(tempReference_Row, scope));
        dispatcher.get().argValue.Row = tempReference_Row.get();
    }

    public void DispatchObject(Reference<RowOperationDispatcher> dispatcher,
                               Reference<RowCursor> scope) {
    }

    public void DispatchSet(Reference<RowOperationDispatcher> dispatcher,
                            Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                            Object value) {
        checkArgument(typeArgs.getCount() == 1);

        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        RowCursor setScope;
        Out<RowCursor> tempOut_setScope =
            new Out<RowCursor>();
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().ReadScope(tempReference_Row, scope, tempOut_setScope));
        setScope = tempOut_setScope.get();
        dispatcher.get().argValue.Row = tempReference_Row.get();
        if (!setScope.Immutable) {
            List items = (List)value;
            for (Object item : items) {
                Reference<RowBuffer> tempReference_Row2 =
                    new Reference<RowBuffer>(dispatcher.get().Row);
                assert setScope.MoveNext(tempReference_Row2);
                dispatcher.get().argValue.Row = tempReference_Row2.get();
                // TODO: C# TO JAVA CONVERTER: The following method call contained an unresolved 'ref' keyword -
                // these cannot be converted using the 'Ref' helper class unless the method is within the code
                // being modified:
                dispatcher.get().LayoutCodeSwitch(ref setScope, null, typeArgs.get(0).getType(),
                    typeArgs.get(0).getTypeArgs().clone(), item);
            }
        }

        Reference<RowBuffer> tempReference_Row3 =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutTypedSet>TypeAs().DeleteScope(tempReference_Row3, scope));
        dispatcher.get().argValue.Row = tempReference_Row3.get();
    }

    public void DispatchTuple(Reference<RowOperationDispatcher> dispatcher,
                              Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs,
                              Object value) {
        checkArgument(typeArgs.getCount() >= 2);
        Reference<RowBuffer> tempReference_Row =
            new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutIndexedScope>TypeAs().DeleteScope(tempReference_Row, scope));
        dispatcher.get().argValue.Row = tempReference_Row.get();
    }

    public void DispatchUDT(Reference<RowOperationDispatcher> dispatcher, Reference<RowCursor> scope, LayoutType t, TypeArgumentList typeArgs, Object value) {
        Reference<RowBuffer> tempReference_Row = new Reference<RowBuffer>(dispatcher.get().Row);
        ResultAssert.IsSuccess(t.<LayoutUDT>TypeAs().DeleteScope(tempReference_Row, scope));
        dispatcher.get().argValue.Row = tempReference_Row.get();
    }
}